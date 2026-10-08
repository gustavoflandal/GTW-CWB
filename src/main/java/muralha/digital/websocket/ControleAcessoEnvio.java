package muralha.digital.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import muralha.digital.blitz.Blitzes;
import muralha.digital.util.Constantes;

@ServerEndpoint("/ClientesWebSocket")
public class ControleAcessoEnvio 
{
	
	  private static final Logger logger = Logger.getLogger(ControleAcessoEnvio.class);
	
	  Session 	sessaoLocal;
	  
	  @OnOpen
	  public void onOpen(Session session) 
	  {	  
		  logger.info("onOpen:: Cliente connectado Id: " + session.getId() + ". Esperando mensagem de validacao!") ;
		  sessaoLocal = session;
	  }

	  @OnClose
	  public void onClose() throws IOException
	  {
		  logger.info("onClose:: Conexao WebSocket encerrada com o cliente " + sessaoLocal.getId());

		  ClienteSessoes.RemoveClienteBySessionId(sessaoLocal.getId());

		  // SE NÃO HOUVER MAIS CLIENTES BLITZ DIGITAL → PARA O TIMER
		  if (ClienteSessoes.ObterQtdeSessoesPorTipo(
				  Constantes.ClientesWS.CLIENTE_SOCK_BLITZ_DIGITAL.getValor()
			  ) == 0)
		  {
			  Blitzes.pararTimer();
		  }

		  sessaoLocal.close();
	  }

	  @OnMessage
	  public void onMessage(String message, Session session) throws IOException, InterruptedException 
	  {
		
	    String[] quebra = message.split("_");
	    	    
	    if(!ValidaCliente(quebra)) return;
	    
	    AdicionaClienteTempoReal(session, quebra);
	    
	    return;
	  }

	  ///////////////////////////////////////////////////////////////////////////////////////////////
	  ////////////////////////////////////////////////////////////////////////////////////////////////
	  
	  //Protocolo com mais de tres informações
	  public boolean ValidaCliente(String[] cli)
	  {
		  if(cli.length < 3)
			  return false;
		  else
			  return true;		  
	  }
	  
	  public void AdicionaClienteTempoReal(Session session, String[] mensagemCompleta)
	  {
		  try
		  {
			  	String cliente			= "";
		    	String tipoContexto 	= "";
		    	String infoAdicional 	= "";
		    	String idUsuario		= "";
		    	String[] infos 			= mensagemCompleta[2].split("-");   
		    			    	
		    	/////////////////DOCUMENTACAO///////////////////////////////////
		    	//Pega o tipo e o ID do Usuario
		    	//Neste caso, o cliente receberá alertas de acordo com sua hierarquia
		    	//Ex:
		    	// ALERTA-NOTIFICACAO-USUARIOID-999 
		    	// ==> cliente ==> ALERTA-NOTIFICACAO
		    	// ==> tipoContexto ==> USUARIOID
		    	// ==> idContexto ==> 999
		    	////////////////////////////////////////////////////////////////
		    	
		    	cliente = infos[0] + "-" + infos[1];
		    	
		    	if ( infos.length > 2) {
		    		tipoContexto = infos[2];
		    		infoAdicional = infos[3];
		    		idUsuario = (infos.length > 4 ? infos[5] : "");
		    	}
		    	
		    	AdicionaNovoCliente(session, cliente, tipoContexto, infoAdicional, idUsuario);
	    	
		  }	
		  catch (Exception e) 
		  {
		    	logger.error("AdicionaClienteTempoReal(): Erro. " + e.getMessage(), e);
		  }			    	
	  }
	  
	  public void AdicionaNovoCliente(	Session session, 
			  							String cliente, 
			  							String tipoContexto, 
			  							String infoAdicional,
			  							String idUsuario)
	  {		  
		  
		  logger.info("cliente: " + cliente + ", tipoContexto: " + tipoContexto + ", infoAdicional: " + infoAdicional + ", idUsuario: " + idUsuario);
		  
		  //Adiciona novo cliente na lista de envio em tempo real		  
		  ClienteSessoes.AdicionaNovoClienteWeb(session, cliente, infoAdicional, idUsuario);

		  if (cliente.equals(Constantes.ClientesWS.CLIENTE_SOCK_BLITZ_DIGITAL.getValor()))
		  {
				int qtd = ClienteSessoes.ObterQtdeSessoesPorTipo(
					Constantes.ClientesWS.CLIENTE_SOCK_BLITZ_DIGITAL.getValor()
				);

				// SE FOR O PRIMEIRO CLIENTE → INICIA TIMER
				if (qtd == 1)
				{
					logger.info("Primeiro cliente BLITZ-DIGITAL conectado → iniciando timer");
					Blitzes.iniciarTimer();
				}
		  }
	  }
}
			

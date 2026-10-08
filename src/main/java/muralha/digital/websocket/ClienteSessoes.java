package muralha.digital.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.websocket.Session;

import org.apache.log4j.Logger;

import com.consilux.model.Usuario;

import muralha.digital.alerta.Alertas;
import muralha.digital.temporeal.BlitzEletronica;
import muralha.digital.temporeal.VeiculoTempoReal;
import muralha.digital.util.Constantes;

public class ClienteSessoes 
{
	private static final Logger logger = Logger.getLogger(ClienteSessoes.class);
	
	private boolean 					continua = true;
	private Thread						tRemoveClientes;
	private static final List<Cliente> 	clientesWebSock = Collections.synchronizedList(new ArrayList<Cliente>());
	
	
	public synchronized void iniciaListaClientesWebSockets()
	{
		logger.info("Iniciando Gerenciador de cliente webSockets!!");
		
			//Iniciando thread para busca de Alertas 
			new Alertas(true);
			
			//Iniciando thread para busca de Veiculos em Tempo Real
			new VeiculoTempoReal(true);
			
			//Iniciando thread para busca de Veiculos para Blitz Eletronica
			new BlitzEletronica(true);
			
			RemoveClientesDesconectados();
	}
	
	public void PararEnvioAosClientes()
	{
		logger.info("Parando Servidor de WebSockets");
		
		this.continua = false;
		Alertas.continua = false;
	}
	
	public static synchronized int ObterQtdeSessoes()
	{		
		return clientesWebSock.size();
	}	
	
	public static synchronized Cliente ObterClienteById(int id)
	{
		return clientesWebSock.get(id);
	}
	
	public static synchronized void RemoverCliente(Cliente cli)
	{
		logger.info("Removendo cliente Socket::" + cli.getTpCliente() + " Id Sessao: " + cli.getSessao().getId());
		clientesWebSock.remove(cli);
	}
	
	public static synchronized void RemoveClienteBySessionId(String idSession)
	{
		for (int i = 0; i < clientesWebSock.size(); i++) 
		{
			Cliente temp = null;
			temp = clientesWebSock.get(i);
			
			if( temp.getSessao().getId().equals(idSession))
			{
				logger.info("RemoveClienteBySessionId:: Cliente removido da lista de sessoes. IdSessao: " + temp.getSessao().getId());
				clientesWebSock.remove(temp);
			}			
		}
	}
	
	public static synchronized void AdicionaNovoClienteWeb(Session sess, String tipoCli, String infoAdicional, String idUsuario)
	{
		if(sess != null)
		{
			Cliente cli = new Cliente();
			cli.setTpCliente(tipoCli);
			cli.setConectado(true);
			cli.setDataConexao(new Date());
			cli.setSessao(sess);
			
			if(tipoCli.equals(Constantes.ClientesWS.CLIENTE_SOCK_ALERTA_NOTIFICACAO.getValor())) 
				cli.setUsuario(AdicionaInfoUsuario(infoAdicional));
			
			if(tipoCli.equals(Constantes.ClientesWS.CLIENTE_SOCK_VEICULO_TEMPO_REAL.getValor()))
			{
				cli.setUsuario(AdicionaInfoUsuario(idUsuario));
				cli.setEquipamentosTempoReal(new VeiculoTempoReal().AdicionaEquipamentosTempoReal(infoAdicional));
			}
			
			if(tipoCli.equals(Constantes.ClientesWS.CLIENTE_SOCK_BLITZ_ELETRONICA.getValor()))
			{
				cli.setUsuario(AdicionaInfoUsuario(idUsuario));
				cli.setEquipamentosTempoReal(new VeiculoTempoReal().AdicionaEquipamentosTempoReal(infoAdicional));
			}

			if(tipoCli.equals(Constantes.ClientesWS.CLIENTE_SOCK_BLITZ_DIGITAL.getValor()))
			{
				cli.setUsuario(AdicionaInfoUsuario(infoAdicional));
			}
			
			clientesWebSock.add(cli);
			logger.info("Qtde sessoes abertas::" + ObterQtdeSessoes());
			
			logger.info("Novo cliente conectado. id: " + sess.getId() + " TipoCliente: " + tipoCli + " DataConexao: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " QtdeClientes: " + clientesWebSock.size());
		}
	}	
	
	private static Usuario AdicionaInfoUsuario(String infoAdicional)
	{
		Usuario retUsuario = null;
		
		try{
			 logger.info("XXXXXXXXXXXXXX:::" + infoAdicional);
			 retUsuario = Usuario.buscaUsuarioPorIdUsuario(Integer.parseInt(infoAdicional));
		
		}
		catch (Exception e) {
			logger.error("AdicionaInfoUsuario(): Erro:: " + e.getMessage(), e);
		}
		
		return retUsuario;
	}
		
	
	public static void EnviaTextoClientes(String tipo, String texto) 
	{
		logger.info("Qtde sessoes::" + ObterQtdeSessoes());
		for (int i = 0; i < ObterQtdeSessoes(); i++) 
		{	
			
			//Obtendo cliente para envio
			Cliente tmpCliente 	= ObterClienteById(i);
			
			if (tmpCliente.getUsuario() != null)
				logger.info("Sessao::" + tmpCliente.getTpCliente() + " ID " + tmpCliente.getSessao().getId() + " ==> Usuario: " + tmpCliente.getUsuario().getUsuario());
			
			if(tmpCliente.getTpCliente().equals(tipo))
			{						    			    			
    			try 
    			{
    				logger.debug("Enviando ao cliente: " + tmpCliente.getTpCliente()+ " Id da Sessao: " + tmpCliente.getSessao().getId() + (tmpCliente.getUsuario() != null ? " ==> Usuario: " + tmpCliente.getUsuario().getUsuario() : ""));
    				
    				if (tmpCliente.getSessao().isOpen()) 
    				{
    					tmpCliente.getSessao().getBasicRemote().sendText(texto);
    				} else {
    					logger.error("Nao foi possivel enviar, pois a conexao esta fechada. Cliente: " + tmpCliente.getTpCliente() + " / Id da Sessao: " + tmpCliente.getSessao().getId());
    				}
					
				} 
    			catch (IOException e) 
    			{
    				logger.error("Erro ao enviar dados ao cliente", e);
    				try 
    				{
    					if (tmpCliente.getSessao().isOpen())
    					{
    						logger.error("FECHANDO CONEXAO COM CLIENTE " + tmpCliente.getTpCliente().trim());
    						tmpCliente.getSessao().close();
    					}
					} 
    				catch (IOException e1) {
						logger.error("XXXXXX:: Falha GERAL:: EnviaTextoClientes()");
					}
				}
			}
		}    		
	}
	
	public static Boolean IsClienteConectado(String tipo) 
	{
		for (int i = 0; i < ObterQtdeSessoes(); i++) 
		{				
			Cliente tmpCliente 	= ObterClienteById(i);
			if(tmpCliente.getTpCliente().equals(tipo))
			{						    			    			
				if (tmpCliente.getSessao().isOpen())  return true;
			}
		}
		
		return false;
	}
	
	
	public void RemoveClientesDesconectados()
	{			
	  tRemoveClientes = new Thread() 
	  {
		    public void run() 
		    {
                while (continua) 
                {
                	try 
                	{		                           		
                		Thread.sleep(2000);                		
                		//logger.info("RemoveClientesDesconectados:: Sessoes WebSock abertas:: " + ClienteSessoes.ObterQtdeSessoes()) ;
                		SessoesParaRemocao();                		                		
                	}
					catch (Exception e) 
		        	{
						logger.error("Falha na thread RemoveClientesDesconectados() websocket(browser)." + e.getMessage(), e);
					}
				}
		    }

		};
		tRemoveClientes.start();
	}	
	
	public static synchronized void SessoesParaRemocao()
	{
		//Verificando se os clientes estão  conectados
		for (int i = 0; i < clientesWebSock.size(); i++) 
		{
			Cliente temp = null;
			temp = clientesWebSock.get(i);
			
			if( ! temp.getSessao().isOpen())
			{
				logger.info("SessoesParaRemocao():: Cliente removido da lista de sessoes por nao estar mais conectado. IdSessao: " + temp.getSessao().getId());
				clientesWebSock.remove(temp);
			}			
		}
	}	
	 	
	public static synchronized int ObterQtdeSessoesPorTipo(String tipo)
	{
		int qtd = 0;

		for (Cliente cli : clientesWebSock)
		{
			if (cli.getTpCliente().equals(tipo) && cli.getSessao().isOpen())
			{
				qtd++;
			}
		}

		return qtd;
	}

}

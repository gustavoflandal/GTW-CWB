package muralha.digital.temporeal;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital._ini.Inicializacao;
import muralha.digital.dispositivo.DispositivoEquipamento;
import muralha.digital.dispositivo.DispositivosEquipamentos;
import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.veiculo.VeiculosBlitz;
import muralha.digital.websocket.ClienteSessoes;

@WebServlet("/MuralhaDigital/BlitzEletronica/VeiculoIrregular")
public class BlitzEletronica extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BlitzEletronica.class); 		

	private Thread 			tBlitzEletronica;
	private Boolean 		iniciaThread = false;
	public static boolean 	continua = true;
	
	public BlitzEletronica(){}
	
	public BlitzEletronica(boolean iniTh) 
	{
		if(Inicializacao.BlitzEletronica.equals("1"))
		{
			this.iniciaThread = iniTh;
			if(iniciaThread)
				BuscaNovosVeiculosIrregulares();
		}
		else
			logger.info("--- Envio de Veiculos Irregulares para Blitz Eletronica não habilitado para Muralha Digital");
	}
	

	public void BuscaNovosVeiculosIrregulares()
	{		
		tBlitzEletronica = new Thread() 
		{
		    public void run() 
		    {
                while (continua) 
                {
                	try 
                	{		                           		
                		Thread.sleep(100);
                		
                		//logger.info("Thread BuscaNovosVeiculos:: Sessoes WebSock abertas:: " + ClienteSessoes.ObterQtdeSessoes()) ;
                		
                		StopWatch swCompleto = new StopWatch();
                		StopWatch sw = new StopWatch();
                		
                		swCompleto.start();
                		                		
                		sw.start();
                		VeiculosBlitz veics = VeiculosBlitz.ObterVeiculosBlitzEletronica();
                		sw.stop();
                		sw.getTime(TimeUnit.MILLISECONDS);
                		
                		if (sw.getTime(TimeUnit.MILLISECONDS) > 1000)
                			logger.debug(String.format("Blitz Eletrônica -- ObterVeiculosBlitzEletronica:: registros %d | tempo: %d ms / %d s", veics.getListaVeiculos().size(), sw.getTime(TimeUnit.MILLISECONDS), sw.getTime(TimeUnit.SECONDS)));
                		
                		if (veics.getListaVeiculos().size() > 0)
                		{      
                			logger.info("Thread tBlitzEletronica:: Itens encontrados, qtde: " + veics.getListaVeiculos().size());
                			
                			sw.reset();
                			sw.start();
                			String xmlSend = RespostaRequisicaoXML.MontarVeicBlitzXML(veics);
                			sw.stop();
                			if (sw.getTime(TimeUnit.MILLISECONDS) > 1000)
                    			logger.debug(String.format("Blitz Eletrônica -- MontarVeicBlitzXML:: registros %d | tempo: %d ms / %d s", veics.getListaVeiculos().size(), sw.getTime(TimeUnit.MILLISECONDS), sw.getTime(TimeUnit.SECONDS)));
                			
                			if( ! xmlSend.equals(""))
                			{
                				sw.reset();
                    			sw.start();
                				ClienteSessoes.EnviaTextoClientes("BLITZ-ELETRONICA", xmlSend);
                				sw.stop();
                				if (sw.getTime(TimeUnit.MILLISECONDS) > 1000)
                					logger.debug(String.format("Blitz Eletrônica -- EnviaTextoClientes:: registros %d | tempo: %d ms / %d s", veics.getListaVeiculos().size(), sw.getTime(TimeUnit.MILLISECONDS), sw.getTime(TimeUnit.SECONDS)));
                			}
                		}
                		
                		swCompleto.stop();
                		if (swCompleto.getTime(TimeUnit.MILLISECONDS) > 1000)
                			logger.debug(String.format("Blitz Eletrônica -- BuscaNovosVeiculosIrregulares:: registros %d | tempo: %d ms / %d s", veics.getListaVeiculos().size(), swCompleto.getTime(TimeUnit.MILLISECONDS), swCompleto.getTime(TimeUnit.SECONDS)));
                	}
					catch (Exception e) 
		        	{
						logger.error("Falha na thread de buscar de novos veiculos irregulares e envio aos clientes websocket(browser)." + e.getMessage(), e);
					}
				}
		    }
		};

		tBlitzEletronica.start();
	}	
	
	public DispositivosEquipamentos AdicionaEquipamentosTempoReal(String infoAdicional) 
	{
		DispositivosEquipamentos retEqps = new DispositivosEquipamentos();
		
		try{
			
			//Obtem a lista de Equipamentos para enviar imagens
			//em tempo real ao cliente
			String[] eqptosTxt = infoAdicional.split(";");
			
			logger.info("WebSocket Veiculos Irregulares Tempo real:: Equipamentos: " + infoAdicional);
			
			for (int i = 0; i < eqptosTxt.length; i++) 
			{
				DispositivoEquipamento eqpto;
				eqpto = DispositivosEquipamentos.ObterDispositivoById(Integer.parseInt(eqptosTxt[i]));
				retEqps.getListaDispositivos().add(eqpto);
			}
						
			//Grava os equipamentos para enviar em tempo real
			for (int i = 0; i < retEqps.getListaDispositivos().size(); i++) {
				AtualizarIniEquipamentoTempoReal(retEqps.getListaDispositivos().get(i));
			}						
		}
		catch (Exception e) {
			logger.error("AdicionaInfoEquipamentosTempoReal(): Erro:: " + e.getMessage(), e);
		}		
		return retEqps;
	}
	
	private void AtualizarIniEquipamentoTempoReal(DispositivoEquipamento eqpto) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
		
			sbSQL.append(" UPDATE muralha.config_envio_tempo_real_equipamento ");
			sbSQL.append(" SET  ");
			sbSQL.append(" data_exibicao_inicial_usuarios = getdate(), ");
			sbSQL.append(" data_exibicao_atualizacao = getdate(), ");
			sbSQL.append(" enviar = 1 ");
			sbSQL.append(" WHERE id_local = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, eqpto.getIdDispositivo());
			
			ps.executeUpdate();
				
		}
		catch(Exception e) {
			logger.error("Erro ao executar AtualizarIniEquipamentoTempoReal()" + ":: " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
	}
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			String sAcao 			= request.getParameter("acao");		
			String sListaEqptos 	= request.getParameter("ListaEqptosTReal");
			
			if( sAcao.equals("AddEqtosTempoReal") ) 
				AdicionaEquipamentosTempoReal(sListaEqptos);
			else if( sAcao.equals("AtualizaEqptosTempoReal") ) 
				AtualizarEquipamentosTempoReal(sListaEqptos);
			else
				logger.warn("doGet() sem String de AÇÃO esperada!!");
		}
		catch(Exception err)
		{
			logger.error("Erro ao obter veiculos irregulares em tempo real.", err);
			return;
		}
		
	}

	public DispositivosEquipamentos AtualizarEquipamentosTempoReal(String infoAdicional) 
	{
		DispositivosEquipamentos retEqps = new DispositivosEquipamentos();
		
		try{
			
			//Obtem a lista de Equipamentos para enviar imagens
			//em tempo real ao cliente
			String[] eqptosTxt = infoAdicional.split(";");
			
			logger.info("XXX - ATUALIZANDO - Tempo Real:: Equipamentos: " + infoAdicional);
			
			for (int i = 0; i < eqptosTxt.length; i++) 
			{
				DispositivoEquipamento eqpto;
				eqpto = DispositivosEquipamentos.ObterDispositivoById(Integer.parseInt(eqptosTxt[i]));
				retEqps.getListaDispositivos().add(eqpto);
			}
						
			//Atualiza data dos equipamentos para enviar em tempo real
			for (int i = 0; i < retEqps.getListaDispositivos().size(); i++) {
				AtualizarEquipamentoTempoReal(retEqps.getListaDispositivos().get(i));
			}						
		}
		catch (Exception e) {
			logger.error("AtualizarEquipamentosTempoReal(): Erro:: " + e.getMessage(), e);
		}		
		return retEqps;
	}

	
	private void AtualizarEquipamentoTempoReal(DispositivoEquipamento eqpto) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		
		//logger.info("XXX - ATUALIZANDO - Tempo Real:: Equipamentos: " + eqpto.getIdDispositivo());
		
		try {
		
			sbSQL.append(" UPDATE muralha.config_envio_tempo_real_equipamento ");
			sbSQL.append(" SET  ");
			sbSQL.append(" data_exibicao_atualizacao = getdate() ");
			sbSQL.append(" WHERE id_local = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, eqpto.getIdDispositivo());
			
			ps.executeUpdate();
				
		}
		catch(Exception e) {
			logger.error("Erro ao executar AtualizarEquipamentoTempoReal()" + ":: " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
	}
}


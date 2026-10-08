package muralha.digital.temporeal;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital._ini.Inicializacao;
import muralha.digital.dispositivo.DispositivoEquipamento;
import muralha.digital.dispositivo.DispositivosEquipamentos;
import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.veiculo.Veiculos;
import muralha.digital.websocket.ClienteSessoes;

@WebServlet("/MuralhaDigital/VeiculoTempoReal")
public class VeiculoTempoReal 
								extends javax.servlet.http.HttpServlet 
								implements javax.servlet.Servlet 
{	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(VeiculoTempoReal.class); 		

	private Thread 			tVeiculosTempoReal;
	private Boolean 		iniciaThread = false;
	public static boolean 	continua = true;	
	
	
	public VeiculoTempoReal()
	{}
	
	public VeiculoTempoReal(boolean iniTh) 
	{		
		if(Inicializacao.VeiculosTempoRealWebSocket.equals("1"))
		{		
			this.iniciaThread = iniTh;			
			if(iniciaThread)
				BuscaNovosVeiculos();
		}
		else
			logger.info("--- Envio de Veiculos Tempo Real não habilitados para Muralha Digital");
	}
	

	public void BuscaNovosVeiculos()
	{		
		tVeiculosTempoReal = new Thread() 
		{
		    public void run() 
		    {
                while (continua) 
                {
                	try 
                	{		                           		
                		Thread.sleep(1000);
                		
                		//logger.info("Thread BuscaNovosVeiculos:: Sessoes WebSock abertas:: " + ClienteSessoes.ObterQtdeSessoes()) ;
                		
                		Veiculos veics = Veiculos.ObterVeiculosTempoReal();
                		
                		if (veics.getListaVeiculos().size() > 0)
                		{      
                			logger.info("Thread tVeiculosTempoReal:: Itens encontrados, qtde: " + veics.getListaVeiculos().size());
                				
                			String xmlSend = RespostaRequisicaoXML.MontarVeicXML(veics);
                			
                			if( ! xmlSend.equals(""))
                				ClienteSessoes.EnviaTextoClientes("VEICULO-TEMPOREAL", xmlSend);							
                		}
                	}
					catch (Exception e) 
		        	{
						logger.error("Falha na thread de buscar de novos Veiculos e envio aos clientes websocket(browser)." + e.getMessage(), e);
					}
				}
		    }
		};

		tVeiculosTempoReal.start();
	}	
	
	public DispositivosEquipamentos AdicionaEquipamentosTempoReal(String infoAdicional) 
	{
		DispositivosEquipamentos retEqps = new DispositivosEquipamentos();
		
		try{
			
			//Obtem a lista de Equipamentos para enviar imagens
			//em tempo real ao cliente
			String[] eqptosTxt = infoAdicional.split(";");
			
			logger.info("WebSocket Veiculos Tempo real:: Equipamentos: " + infoAdicional);
			
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
			
			if( sAcao.equals("HistoricoVeiculos") ) 
				EnviarVeiculosHistorico(response);
			
			
			else if( sAcao.equals("AddEqtosTempoReal") ) 
				AdicionaEquipamentosTempoReal(sListaEqptos);
			
			
			else if( sAcao.equals("AtualizaEqptosTempoReal") ) 
				AtualizarEquipamentosTempoReal(sListaEqptos);

			
			else
				logger.warn("doGet() sem String de AÇÃO esperada!!");

			
			
		}
		catch(Exception err) {
			logger.error("Erro ao obter histórico de veiculos de tempo real.", err);
			return;
		}
		
	}  	
	
	private void EnviarVeiculosHistorico(HttpServletResponse response) throws IOException
	{
		Veiculos veics = Veiculos.ObterVeiculosHistorico();
		String xmlEnviar = RespostaRequisicaoXML.MontarVeicXML(veics);
		
		response.setHeader("Content-Type", "text/xml");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(xmlEnviar);
		response.getWriter().flush();
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
	
	
	public static boolean AlterarPlacaVeic(UUID idVeiculo, String placa, Integer idUsuario, String dadosOriginais) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		boolean ret = false;
		
		logger.info("XXX - ATUALIZANDO - Placa de Veiculo: " + placa + " - " + idUsuario.toString());
		
		try {
		
			sbSQL.append(" UPDATE muralha.veiculo_tempo_real ");
			sbSQL.append(" SET  ");
			sbSQL.append(" placa = ?, id_usuario_alt = ?, data_alt = getdate(), dados_alt_orig = ? ");
			sbSQL.append(" WHERE id = ? ");
						
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, placa);
			ps.setInt(2, idUsuario);
			ps.setString(3, dadosOriginais);
			ps.setString(4, idVeiculo.toString());
			
			ps.executeUpdate();
			
			ret = true;
				
		}
		catch(Exception e) {
			logger.error("Erro ao executar AlterarPlacaVeic()" + ":: " + e.getMessage(), e);
			ret = false;
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
		
		return ret;
	}
}


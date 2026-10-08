package muralha.digital.acessos;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.cidade.Cidades;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/ConfiguracaoInatividade")
public class ConfiguracaoInatividadeServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ConfiguracaoInatividadeServlet.class);
    private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

    public ConfiguracaoInatividadeServlet() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try
    	{
       		String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}  
	    	if(strAcao.equals("buscarTempoInatividade"))	    	
	    		buscarTempoInatividade();
 
    		if(strAcao.equals("buscarConfigsInatividade"))	    	
	    		buscarConfigsInatividadeXML(response);
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de Alerta: " + e.getMessage(), e);
	    }       
        
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	
		final Acesso acesso = new Acesso(request, response, true);
		if (!acesso.verificaAcesso()) {
			new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
    	
       	try
       	{
       		
       		String acao = request.getParameter("acao");
       		
	       	if(acao.equals("atualizarTempoInatividade"))
	       	{
	       		
	       		Integer tempoInatividade = Integer.parseInt(request.getParameter("tempo"));
	       		Integer idUsuario = acesso.getUsuario().getId();
	       		Boolean loginIndefinido = request.getParameter("loginIndefinido").equals("true") ? true : false;
	       		
	       		atualizarTempoInatividade(tempoInatividade, idUsuario, loginIndefinido);
	       		return;
	       	}	       	      	
       	} 
       	catch (ConexaoException e) 
       	{
       		logger.error("Erro ao processar requisicao. " + e.getMessage(), e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	       	
       		
    }
    
	public static Integer buscarTempoInatividade() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		Integer tempoInatividade = 0;

		sbSQL.append("select * from muralha.config_chave_valor where chave = 'tempo_inatividade'");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {		
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				tempoInatividade = rs.getInt("valor");
			}
		}				
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		
		return tempoInatividade;
	}
	
	public static void atualizarTempoInatividade(Integer tempoInatividade, Integer idUsuario, Boolean tempoIndefinido) 
	        throws ConexaoException, SQLException {

	    StringBuilder sbSQL = new StringBuilder();

	    Integer tempoInatividadeAnterior = buscarTempoInatividade();	    
	    
	    if(tempoIndefinido == true) {
	    	sbSQL.append("update muralha.config_chave_valor set valor = 1 where chave = 'login_nunca_bloqueia'");
	    }else {
		    sbSQL.append("update muralha.config_chave_valor set valor = ? where chave = 'login_tempo_inatividade'");  	
	    }	    

	    Connection conn = null;
	    PreparedStatement ps = null;

	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());

	        if(tempoIndefinido == false) {
	        	 ps.setInt(1, tempoInatividade);
	        }
	       
	        ps.executeUpdate();
	        
	        if(tempoIndefinido == false) {
	        	
	        	String eventoHistorico = "login_tempo_inatividade = " + tempoInatividadeAnterior + " -> " + tempoInatividade;
	        	
	        	gerarHistorico("login_tempo_inatividade", eventoHistorico, idUsuario);
	        }else {
	        	
	        	String eventoHistorico = tempoIndefinido == false ? "login_nunca_bloqueia 1 -> 0 " : "login_nunca_bloqueia 0 -> c1";
	        	
	        	gerarHistorico("login_nunca_bloqueia", eventoHistorico, idUsuario);
	        }
	    }
	    finally {
	        if (ps != null) ps.close();
	        if (conn != null) conn.close();
	    }
	}
	
	public static void gerarHistorico(String chave, String msg, Integer idUsuario) throws SQLException, ConexaoException {
		
		StringBuilder sbSQL = new StringBuilder();
	    Timestamp agora = Timestamp.from(Instant.now());
			    
	    sbSQL.append("insert into muralha.config_chave_valor_hist (id_usuario, data_atualizacao, evento) values (?,?,?)");
	    
	    Connection conn = null;
	    PreparedStatement ps = null;

	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());

	        ps.setInt(1, idUsuario);
	        ps.setTimestamp(2, agora);
	        ps.setString(3, msg);

	        ps.executeUpdate();
	    }
	    finally {
	        if (ps != null) ps.close();
	        if (conn != null) conn.close();
	    }
	}
	
	public static ConfiguracaoInatividadeResult buscarConfigsInatividade() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		ConfiguracaoInatividadeResult configs = new ConfiguracaoInatividadeResult();

		sbSQL.append("select * from muralha.config_chave_valor where chave in ('login_tempo_inatividade', 'login_nunca_bloqueia'); ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {		
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				
				String chave = rs.getString("chave");
				Integer valor = rs.getInt("valor");
				
				ConfiguracaoInatividade resultConfig = new ConfiguracaoInatividade();
				resultConfig.setChave(chave);
				resultConfig.setValor(valor);
				
				if(chave.equals("login_nunca_bloqueia")) {
					configs.setLoginNuncaBloqueia(resultConfig);
				}else {
					configs.setLoginTempoInatividade(resultConfig);
				}				
			}
		}				
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		
		return configs;
	}
	
	public void buscarConfigsInatividadeXML(HttpServletResponse response) throws ConexaoException, SQLException, JAXBException, IOException {
		
		StringBuilder sbSQL = new StringBuilder();
		ConfiguracaoInatividadeResult configs = new ConfiguracaoInatividadeResult();

		sbSQL.append("select * from muralha.config_chave_valor where chave in ('login_tempo_inatividade', 'login_nunca_bloqueia'); ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {		
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				
				String chave = rs.getString("chave");
				Integer valor = rs.getInt("valor");
				
				ConfiguracaoInatividade resultConfig = new ConfiguracaoInatividade();
				resultConfig.setChave(chave);
				resultConfig.setValor(valor);
				
				if(chave.equals("login_nunca_bloqueia")) {
					configs.setLoginNuncaBloqueia(resultConfig);
				}else {
					configs.setLoginTempoInatividade(resultConfig);
				}				
			}
		}				
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		
		EnviarRespostaXML(response, configs);
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, ConfiguracaoInatividadeResult result) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(ConfiguracaoInatividadeResult.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(result, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			result = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta das configurações de tempo de inatividade!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

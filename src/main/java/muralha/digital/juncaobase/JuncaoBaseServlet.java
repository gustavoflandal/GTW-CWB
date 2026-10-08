/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Guilherme Morgado Bassan
  Data: 07/01/2022

*********************************************************************************/
package muralha.digital.juncaobase;

//import java.util.List;

import java.sql.ResultSet;
import java.io.IOException;
import java.sql.Connection;
//import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javax.xml.bind.Marshaller;
//import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.infra.exception.ConexaoException;

//import muralha.digital.juncaobase.JuncaoBaseCSV;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/JuncaoBase")
public class JuncaoBaseServlet 
		extends javax.servlet.http.HttpServlet 
		implements javax.servlet.Servlet 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(JuncaoBaseServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		if ( ! new Acesso(request, response, true).verificaAcesso(false)) { 
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); 
			return; 
		}
		
		try {
			executaQuery(response);
		}
		catch(Exception err) {
			logger.error("doGet:: Erro ao importar CSV.", err);
			return;
		}
	}
	
	private void executaQuery(HttpServletResponse response) throws ConexaoException, SQLException, JAXBException, IOException
	{
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("EXEC muralha.spu_ImportarBase");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
		}
		catch (Exception e) {
			String msg = "Erro ao executar procedure muralha.spu_ImportarBase ";
			logger.error(msg);
			throw new SQLException(msg,e);
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				throw new ConexaoException("Erro de SQL: ", e);
			}
		}
		respostaImportarCSV(response);
	}
	
	private void respostaImportarCSV(HttpServletResponse response) throws JAXBException, IOException, ConexaoException, SQLException
	{
		try
		{
			/*
			//Formando dados para envio
			JAXBContext juncao_context = JAXBContext.newInstance(JuncaoBaseCSV.class);
			Marshaller marsHall = juncao_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			*/
			
			String msg = "Procedure chamado com sucesso.";
			respostaXML.EnviarRespostaRequisicaoXML(response, true, msg);
		}
		catch(Exception e)
		{
			logger.error("respostaImportarCSV:: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da chamada de procedure!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
}

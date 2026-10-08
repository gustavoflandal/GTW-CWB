package com.abertura.sistemas;

/**********************************************************************************

Projeto: Muralha Digital / GTW / JARI

Empresa: Consilux Tecnologia

Autor: Luiz Fernando Amaral
Data: 27/09/2021

*********************************************************************************/

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

@WebServlet("/Abertura/SistemasConsilux")
public class SistemasServlet  extends javax.servlet.http.HttpServlet 
							 implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SistemasServlet.class); 
	
	boolean temp;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		logger.info("doGet():: SistemasServlet");
		
		
		//Validando acesso do usuário
		//if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			
			ObterListaSistemas(response);
			 //MapaCalorServlet.obterPontosMapaCalor(response);
			
		}
		catch(Exception err) {
			logger.error("Erro ao obter Lista de Sistemas Consilux", err);
			return;
		}
		
	}  	
	
	public void ObterListaSistemas(HttpServletResponse response) 
	{
		
		logger.info("ObterListaSistemas()");
		
		List<SistemaConsiluxItem> 	listaSistemas = new ArrayList<SistemaConsiluxItem>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		try 
		{
			
			sbSQL.append(" SELECT ");
			sbSQL.append(" id, descricao, descricao_detalhes, mostrar_painel, url_externa ");
			sbSQL.append(" FROM sistemas_consilux ");
			sbSQL.append(" WHERE  ");
			sbSQL.append(" mostrar_painel = 1 ");
			
			conn = Conexao.getConexao();;
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				SistemaConsiluxItem st = new SistemaConsiluxItem();
				
				st.setIdSistema(rs.getInt("id"));
				st.setDescricao(rs.getString("descricao"));
				st.setDescricaoDetalhada(rs.getString("descricao_detalhes"));
				st.setMostrarPainel(rs.getBoolean("mostrar_painel"));
				
				if(rs.getString("url_externa") != null){
					st.setUrlExterna(rs.getString("url_externa"));
				}
				else{
					st.setUrlExterna("");
				}				
				
				listaSistemas.add(st); 
			}
			
			SistemasConsilux sistemas = new SistemasConsilux();
			sistemas.setListaSistemas(listaSistemas);
			
			EnviaRespostaSistemasXML(response, sistemas);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter Lista de Sistemas Consilux: " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
		}		
	}
	
	private void EnviaRespostaSistemasXML( HttpServletResponse response, SistemasConsilux sistemas) throws JAXBException, IOException
	{
		
		logger.info("Consulta de Sistemas:: Enviado XML de resposta");
		//Formando dados para envio
		JAXBContext sistemas_context = JAXBContext.newInstance(SistemasConsilux.class);
		Marshaller marsHall = sistemas_context.createMarshaller();
		marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		marsHall.marshal(sistemas, sw);
		String xml = sw.toString();
		sw.close();
		
		response.setHeader("Content-Type", "text/xml");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(xml);
		response.getWriter().flush();
	}
}



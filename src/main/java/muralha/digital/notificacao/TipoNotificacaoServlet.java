/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 06/10/2021

*********************************************************************************/

package muralha.digital.notificacao;

import java.io.IOException;
import java.io.StringWriter;
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

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/TipoNotificacao")
public class TipoNotificacaoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(TipoNotificacaoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
    	//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
       	try
    	{
	    	ObterTiposNotificacao(request, response);
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de Tipos de Notificação: " + e.getMessage(), e);
	    }
    }

	
	protected void ObterTiposNotificacao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			//Cria objeto de retorno
			TiposNotificacao tipos = new TiposNotificacao();
			tipos.setListaTiposNotificacao(new ArrayList<TipoNotificacao>());
				
			//Faz a consulta já existente no banco de dados
			List<TipoNotificacao> listaTipos = TiposNotificacao.ObterListaTiposNotificacao();
			tipos.setListaTiposNotificacao(listaTipos);
			
			EnviarRespostaXML(response, tipos);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter tipos de notificação: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar tipos de notificação!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, TiposNotificacao tiposNotificacao) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(TiposNotificacao.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(tiposNotificacao, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			tiposNotificacao = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de tipos de notificação!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

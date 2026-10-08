/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.relatorios;

import java.io.IOException;
import java.io.StringWriter;

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

@WebServlet("/MuralhaDigital/Dashboard")
public class DashboardServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DashboardServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
    	//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
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
	    	
	    	
	    	if(strAcao.equals("obterTotalizadorCategoria"))
	    		ObterTotalizadorCategoria(request, response);
	    	else if(strAcao.equals("obterCalendarioIntensidade"))
	    		ObterCalendarioIntensidade(request, response);
	    	
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados do dashboard!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterTotalizadorCategoria(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				TotalizadorCategoria totalizadorCategoria = Dashboards.ObterTotalizadorCategoria(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaTotalCategoriaXML(response, totalizadorCategoria);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o dashboard!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	

	private void ObterCalendarioIntensidade(HttpServletRequest request, HttpServletResponse response) throws ServletException 
	{
		try
		{
			DashboardValidacao dashboardValidacao = DashboardValidacao.ValidarFiltros(request, response);
			
			if (dashboardValidacao.isFiltroValido())
			{
				CalendariosIntensidades calendariosIntensidade = CalendariosIntensidades.ObterCalendarioIntensidade(dashboardValidacao.getDataIni(), dashboardValidacao.getDataFim(), dashboardValidacao.getIdLocal(), dashboardValidacao.getTipoRelatorio(), dashboardValidacao.getMunicipio(), dashboardValidacao.getRegiao());
				EnviarRespostaCalendarioIntensidadeXML(response, calendariosIntensidade);
			}
			else
			{
				respostaXML.EnviarRespostaRequisicaoXML(response, dashboardValidacao.isFiltroValido(), dashboardValidacao.getMensagem());
			}
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar dados para o dashboard!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void EnviarRespostaTotalCategoriaXML(HttpServletResponse response, TotalizadorCategoria totalizadorCategoria) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(TotalizadorCategoria.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(totalizadorCategoria, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			totalizadorCategoria = null;
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao retornar os dados para o dashboard!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	

	private void EnviarRespostaCalendarioIntensidadeXML(HttpServletResponse response, CalendariosIntensidades calendariosIntensidades) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(CalendariosIntensidades.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(calendariosIntensidades, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			calendariosIntensidades = null;
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao retornar os dados para o dashboard!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

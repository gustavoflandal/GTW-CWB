/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Douglas Alisson Tubiana
  Data: 15/01/2022

*********************************************************************************/

package muralha.digital.anomalia;

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

@WebServlet("/MuralhaDigital/Anomalia")
public class AnomaliaServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{
	//
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AnomaliaServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			
			obterAlertaAnomalia(response);
			
		}
		catch(Exception e) 
		{
			String msg = "Ocorreu um erro ao consultar dados para o alerta de anomalias!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	} 
	
	private void obterAlertaAnomalia(HttpServletResponse response)
	{
		try 
		{
			// Obter dados
			Anomalias anomalias = Anomalias.ObterDadosAlertaAnomalias();
			enviaRespostaAlertaAnomaliaXML(response, anomalias);
		} 
		catch (Exception e) 
		{
			String msg = "Ocorreu um erro ao obterAlertaAnomalia()";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void enviaRespostaAlertaAnomaliaXML( HttpServletResponse response, Anomalias anomalias) throws JAXBException, IOException
	{
		//Formando dados para envio
		JAXBContext anomalias_context = JAXBContext.newInstance(Anomalias.class);
		Marshaller marsHall = anomalias_context.createMarshaller();
		marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		marsHall.marshal(anomalias, sw);
		String xml = sw.toString();
		sw.close();
		
		response.setHeader("Content-Type", "text/xml");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(xml);
		response.getWriter().flush();
		
		anomalias = null;
	}
}

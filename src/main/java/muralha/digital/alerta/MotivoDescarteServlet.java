/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.alerta;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;


@WebServlet("/MuralhaDigital/Alerta/MotivoDescarte")
public class MotivoDescarteServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MotivoDescarteServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
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
	    	
	    	if(strAcao.equals("obterListaMotivosDescarte"))
	    		ObterListaMotivosDescarte(request, response);
	    		
	    	if(strAcao.equals("obterMotivoDescartePorId"))	    	
	    		ObterMotivoDescartePorId(request, response);
	    		
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doPost() de requisição de Motivo de Descarte: " + e.getMessage(), e);
	    }
	}
	
	private void ObterMotivoDescartePorId(HttpServletRequest request, HttpServletResponse response) throws JAXBException, IOException
	{
		try 
		{
			String strIdMotivoDescarte = request.getParameter("idMotivoDescarte");
			
			if (strIdMotivoDescarte == null || strIdMotivoDescarte.equals("") || strIdMotivoDescarte.equals("0")) 
	    	{
	    		String msg = "Identificador do Motivo de Descarte não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			UUID idMotivoDescarte = null;
			
			try
	    	{
    			idMotivoDescarte = UUID.fromString(strIdMotivoDescarte);
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
			
			//Cria objeto de retorno
			MotivoDescarte motivoDescarte = MotivosDescarte.ObterMotivoDescartePorId(idMotivoDescarte);

			EnviarItemRespostaXML(response, motivoDescarte);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter motivos de descarte: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os motivos de descarte!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaMotivosDescarte(HttpServletRequest request, HttpServletResponse response) throws JAXBException, IOException
	{
		try 
		{
			//Cria objeto de retorno
			MotivosDescarte motivosDescarte = new MotivosDescarte();
			motivosDescarte.setListaMotivosDescarte(new ArrayList<MotivoDescarte>());
				
			//Faz a consulta já existente no banco de dados
			List<MotivoDescarte> listaMotivoDescarte = MotivosDescarte.ObterListaMotivosDescarte();
			motivosDescarte.setListaMotivosDescarte(listaMotivoDescarte);

			EnviarListaRespostaXML(response, motivosDescarte);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter motivos de descarte: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os motivos de descarte!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarListaRespostaXML(HttpServletResponse response, MotivosDescarte motivosDescarte) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(MotivosDescarte.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(motivosDescarte, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(motivosDescarte.getListaMotivosDescarte().size()) );
			motivosDescarte = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de motivos de descarte de alertas!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private void EnviarItemRespostaXML(HttpServletResponse response, MotivoDescarte motivoDescarte) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(MotivoDescarte.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(motivoDescarte, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			motivoDescarte = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarItemRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de motivo de descarte!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

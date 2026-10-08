/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.ocorrencia;

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


@WebServlet("/MuralhaDigital/Ocorrencia/Status")
public class StatusOcorrenciaServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StatusOcorrenciaServlet.class);
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
	    	
	    	if(strAcao.equals("obterListaStatusFinalizacao"))
	    		ObterStatusOcorrenciaFinalizacao(request, response);
	    		
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de status de irregularidades: " + e.getMessage(), e);
	    }
	}
	
	private void ObterStatusOcorrenciaFinalizacao(HttpServletRequest request, HttpServletResponse response) throws JAXBException, IOException
	{
		try 
		{
			String strTipoOcorrencia = request.getParameter("tipoOcorrencia");
			
			if (strTipoOcorrencia == null || strTipoOcorrencia.equals("") || strTipoOcorrencia.equals("0")) 
	    	{
	    		String msg = "Tipo da irregularidade não informado!";
	    		logger.error(msg);
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			UUID idTipoOcorrencia = null;
			
    		if (strTipoOcorrencia != null && !strTipoOcorrencia.trim().equals(""))
    		{
    			idTipoOcorrencia = UUID.fromString(strTipoOcorrencia.trim());
    		}
			
			//Cria objeto de retorno
			StatusOcorrencias statusOcorrencias = new StatusOcorrencias();
			statusOcorrencias.setListaStatusOcorrencias(new ArrayList<StatusOcorrencia>());
				
			List<StatusOcorrencia> listaStatusOcorrencia = new ArrayList<StatusOcorrencia>();
			listaStatusOcorrencia = StatusOcorrencias.ObterListaStatusOcorrenciaFinalizacao(idTipoOcorrencia);
			
			statusOcorrencias.setListaStatusOcorrencias(listaStatusOcorrencia);

			EnviarRespostaXML(response, statusOcorrencias);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter status de irregularidades: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar status de irregularidades!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, StatusOcorrencias statusOcorrencias) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(StatusOcorrencias.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(statusOcorrencias, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(statusOcorrencias.getListaStatusOcorrencias().size()) );
			statusOcorrencias = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de status de irregularidades!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

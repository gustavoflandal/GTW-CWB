/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.consulta;

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


@WebServlet("/MuralhaDigital/AlertaOcorrencia/Status")
public class StatusAlertaOcorrenciaServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StatusAlertaOcorrenciaServlet.class);
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
	    	
	    	else if(strAcao.equals("obterListaStatus"))
	    		ListaStatusAlertaOcorrencia(request, response);
	    	else if(strAcao.equals("obterListaStatusVinculado"))
	    		ObterListaStatusAlertaVinculado(response);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter status de alertas/irregularidades: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar status de alertas/irregularidades!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ListaStatusAlertaOcorrencia(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strTipoRegistro = request.getParameter("tipoRegistro");
			
			if (strTipoRegistro == null || strTipoRegistro.equals("") || strTipoRegistro.equals("0")) 
	    	{
	    		String msg = "Tipo não informado!";
	    		logger.error(msg);
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			UUID idTipoRegistro = null;
			TipoRegistro.Tipo tipoRegistro = null;
			
    		if (strTipoRegistro != null && !strTipoRegistro.trim().equals(""))
    		{
    			idTipoRegistro = UUID.fromString(strTipoRegistro.trim());
				tipoRegistro = TipoRegistro.Tipo.GetValue(idTipoRegistro);
    		}
    		else if (tipoRegistro == null)
    		{
    			tipoRegistro = TipoRegistro.Tipo.ALERTA;
    		}
						
    		ObterListaStatus(tipoRegistro, response);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro preparar parâmetros para consulta!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaStatus(TipoRegistro.Tipo tipoRegistro, HttpServletResponse response)
	{
		try
		{
			//Cria objeto de retorno
			StatusAlertasOcorrencias statusAlertasOcorrencias = new StatusAlertasOcorrencias();
			statusAlertasOcorrencias.setListaStatusAlertasOcorrencias(new ArrayList<StatusAlertaOcorrencia>());
				
			List<StatusAlertaOcorrencia> listaStatusAlertaOcorrencia = new ArrayList<StatusAlertaOcorrencia>();
			
			//Faz a consulta já existente no banco de dados
			if (tipoRegistro == TipoRegistro.Tipo.ALERTA)
			{
				listaStatusAlertaOcorrencia = StatusAlertasOcorrencias.ObterListaStatusAlerta();
			}
			else if (tipoRegistro == TipoRegistro.Tipo.IRREGULARIDADES)
			{
				listaStatusAlertaOcorrencia = StatusAlertasOcorrencias.ObterListaStatusOcorrencia();
			}
			
			statusAlertasOcorrencias.setListaStatusAlertasOcorrencias(listaStatusAlertaOcorrencia);

			EnviarRespostaXML(response, statusAlertasOcorrencias);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar os status de alertas/irregularidades!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void ObterListaStatusAlertaVinculado(HttpServletResponse response)
	{
		try
		{
			//Cria objeto de retorno
			StatusAlertasOcorrencias statusAlertasOcorrencias = new StatusAlertasOcorrencias();
			statusAlertasOcorrencias.setListaStatusAlertasOcorrencias(new ArrayList<StatusAlertaOcorrencia>());
				
			List<StatusAlertaOcorrencia> listaStatusAlertaOcorrencia = new ArrayList<StatusAlertaOcorrencia>();
			
			//Faz a consulta já existente no banco de dados
			listaStatusAlertaOcorrencia = StatusAlertasOcorrencias.ObterListaStatusAlertaVinculado();
			statusAlertasOcorrencias.setListaStatusAlertasOcorrencias(listaStatusAlertaOcorrencia);

			EnviarRespostaXML(response, statusAlertasOcorrencias);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar os status de alertas vinculados!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, StatusAlertasOcorrencias statusAlertasOcorrencias) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(StatusAlertasOcorrencias.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(statusAlertasOcorrencias, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(statusAlertasOcorrencias.getListaStatusAlertasOcorrencias().size()) );
			statusAlertasOcorrencias = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de status de alertas/irregularidades!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

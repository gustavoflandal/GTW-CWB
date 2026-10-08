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


@WebServlet("/MuralhaDigital/AlertaOcorrencia/Tipo")
public class TipoAlertaOcorrenciaServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TipoAlertaOcorrenciaServlet.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			//Cria objeto de retorno
			TiposAlertaOcorrencias tiposAlertasOcorrencias = new TiposAlertaOcorrencias();
			tiposAlertasOcorrencias.setListaTiposAlertasOcorrencias(new ArrayList<TipoAlertaOcorrencia>());
				
			//Faz a consulta já existente no banco de dados
			List<TipoAlertaOcorrencia> listaTipoAlertaOcorrencia = TiposAlertaOcorrencias.ObterListaTiposAlertasOcorrencias();
			tiposAlertasOcorrencias.setListaTiposAlertasOcorrencias(listaTipoAlertaOcorrencia);

			EnviarRespostaXML(response, tiposAlertasOcorrencias);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter tipos de alertas/irregularidades: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar tipos de alertas/irregularidades!";
			logger.error(msg);	
			EnviarMensagemXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, TiposAlertaOcorrencias tiposAlertasOcorrencias) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(TiposAlertaOcorrencias.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(tiposAlertasOcorrencias, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(tiposAlertasOcorrencias.getListaTiposAlertasOcorrencias().size()) );
			tiposAlertasOcorrencias = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de tipos de alertas/irregularidades!";
			logger.error(msg);
			EnviarMensagemXML(response, false, msg);
			return;
		}	
	}
	
	private void EnviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) 
	{

		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);
		
    	//Formando dados para envio
		JAXBContext evidencia_context;
		try
		{
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
		
		}
		catch (Exception e)
		{
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
}

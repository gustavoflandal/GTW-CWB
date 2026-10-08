package muralha.digital.boletim;

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


@WebServlet("/MuralhaDigital/Boletim/Tipo")
public class BoletimTipoServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoletimTipoServlet.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			//Cria objeto de retorno
			BoletimTipos tiposBoletim = new BoletimTipos();
			tiposBoletim.setListaTiposOcorrencia(new ArrayList<OcorrenciaTipo>());
				
			//Faz a consulta já existente no banco de dados
			List<OcorrenciaTipo> listaTipoBoletim = BoletimTipos.ObterListaTiposOcorrencia();
			tiposBoletim.setListaTiposOcorrencia(listaTipoBoletim);

			EnviarRespostaXML(response, tiposBoletim);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter tipos de boletim: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar tipos de boletim!";
			logger.error(msg);	
			EnviarMensagemXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, BoletimTipos tiposBoletim) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(BoletimTipos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(tiposBoletim, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(tiposBoletim.getListaTiposOcorrencia().size()) );
			tiposBoletim = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de tipos de boletim!";
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
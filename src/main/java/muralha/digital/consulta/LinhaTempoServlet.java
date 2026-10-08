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


@WebServlet("/MuralhaDigital/LinhaTempo")
public class LinhaTempoServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LinhaTempoServlet.class);
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
	    	
	    	else if(strAcao.equals("obterLinhaTempo"))
	    		ObterListaLinhaTempo(request, response);
	    	
		}
		catch(Exception e) {
			logger.error("Erro ao obter linha do tempo: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar a linha do tempo!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterListaLinhaTempo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strIdAlerta = request.getParameter("idAlerta");
			String strIdVeiculo = request.getParameter("idVeiculo");
			
			boolean possuiIdAlerta = ( (strIdAlerta != null && !strIdAlerta.trim().equals("") && !strIdAlerta.trim().equals("0")) ? true : false);
			boolean possuiIdVeiculo = ( (strIdVeiculo != null && !strIdVeiculo.trim().equals("") && !strIdVeiculo.trim().equals("0")) ? true : false);
			
			if ( possuiIdAlerta && possuiIdVeiculo )
	    	{
	    		String msg = "Favor informar um alerta ou um veículo para obter a linha do tempo!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			UUID idAlerta = null, idVeiculo = null;
			
			if (possuiIdAlerta)
				idAlerta = UUID.fromString(strIdAlerta);
			
			if (possuiIdVeiculo)
				idVeiculo = UUID.fromString(strIdVeiculo);
						
    		ObterLinhaTempo(idAlerta, idVeiculo, response);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro preparar parâmetros para consulta!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterLinhaTempo(UUID idAlerta, UUID idVeiculo, HttpServletResponse response)
	{
		try
		{
			//Cria objeto de retorno
			LinhasTempo linhasTempo = new LinhasTempo();
			linhasTempo.setListaLinhasTempo(new ArrayList<LinhaTempo>());
				
			List<LinhaTempo> listaLinhaTempo = LinhasTempo.ObterListaLinhaTempo(idAlerta, idVeiculo);
			
			linhasTempo.setListaLinhasTempo(listaLinhaTempo);

			EnviarRespostaXML(response, linhasTempo);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar os status de alertas/irregularidades!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, LinhasTempo linhasTempo) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(LinhasTempo.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(linhasTempo, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(linhasTempo.getListaLinhasTempo().size()) );
			linhasTempo = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de linha do tempo!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

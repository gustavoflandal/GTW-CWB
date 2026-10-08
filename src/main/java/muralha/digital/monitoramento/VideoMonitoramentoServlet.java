/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 11/11/2021

*********************************************************************************/

package muralha.digital.monitoramento;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

import muralha.digital._ini.Inicializacao;
import muralha.digital.util.RespostaRequisicaoXML;


@WebServlet("/MuralhaDigital/VideoMonitoramento")
public class VideoMonitoramentoServlet extends HttpServlet
{
	
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VideoMonitoramentoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	private static int TEMPO_MAXIMO_CONSULTA_EM_MINUTOS = Inicializacao.TempoMaxConsultaVideosMonEmMinutos;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErroMuralha("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
      	try
    	{
       		String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.warn(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	else if(strAcao.equals("consultaVideosMonitoramento"))
	    		ObterListaVideosMonitoramento(request, response);
	    	
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar videos de monitoramento!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	

	private void ObterListaVideosMonitoramento(HttpServletRequest request, HttpServletResponse response)
	{
		try 
		{
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strEquipamento = request.getParameter("equipamento");

    		if (strDataIni != null && strDataIni.trim().equals(""))
	    	{
	    		String msg = "Data início informada inválida!";
				logger.warn(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
	    	}
	    	
    		if (strDataFim != null && strDataFim.trim().equals(""))
	    	{
	    		String msg = "Data fim informada inválida!";
				logger.warn(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
	    	}

	    	if (strEquipamento == null || strEquipamento.equals("") || strEquipamento.equals("0")) 
	    	{
	    		String msg = "Equipamento informado inválido!";
	    		logger.warn(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Integer idLocal = null;
			Date dataIni = null, dataFim = null;
	    	
	    	try
	    	{
    			dataIni = sdf.parse(strDataIni + ":00");
    			dataFim = sdf.parse(strDataFim + ":59");
    			idLocal = Integer.parseInt(strEquipamento);
    			
    			if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
    	    	{
    	    		String msg = "A data de início deve ser menor ou igual a data fim!";
    				logger.warn(msg);
    	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    	    		return;
    	    	}
    			
    		    long diffDatasEmMilli = Math.abs(dataFim.getTime() - dataIni.getTime());
    		    long diffMinutos = TimeUnit.MINUTES.convert(diffDatasEmMilli, TimeUnit.MILLISECONDS);
    		    
    		    if (diffMinutos > TEMPO_MAXIMO_CONSULTA_EM_MINUTOS)
    		    {
    		    	String msg = String.format("A consulta não deve exceder %d minutos!", TEMPO_MAXIMO_CONSULTA_EM_MINUTOS);
    				logger.warn(msg);
    	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    	    		return;
    		    }
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
			
			VideosMonitoramento videosMonitoramento = new VideosMonitoramento();
			videosMonitoramento.setListaVideosMonitoramento(VideosMonitoramento.ObterListaVideosExibicao(dataIni, dataFim, idLocal));
			videosMonitoramento.setQuantidadeCameras(videosMonitoramento.getListaVideosMonitoramento().size());
			
			if (videosMonitoramento.getListaVideosMonitoramento().size() == 0)
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "Não foram encontrados videos para os parâmetros informados!");
			else
				EnviarRespostaXML(response, videosMonitoramento);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter equipamentos: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os equipamentos!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, VideosMonitoramento videosMonitoramento) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(VideosMonitoramento.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(videosMonitoramento, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(videosMonitoramento.getListaVideosMonitoramento().size()) );
			videosMonitoramento = null;
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de videos de monitoramento!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

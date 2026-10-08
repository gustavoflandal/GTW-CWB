/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.monitoramento;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.Servlet;
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

@WebServlet("/MuralhaDigital/ConfigMonAoVivo")
public class MonitoramentoAoVivoServlet extends HttpServlet implements Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MonitoramentoAoVivoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		Integer idUsuario = null;
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		else
		{
			idUsuario = acessoUsuario.getUsuario().getId();
		}
		
		try 
		{
			String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao.equals("")) 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	if(strAcao.equals("salvar"))
	    		Salvar(request, response, idUsuario);
	    	else if(strAcao.equals("atualizarGrupoExibicao"))
	    		AtualizarGrupoEmExibicao(request, response);
			
			return;
		}
		catch(Exception e)
		{
			logger.error("Erro ao salvar configurações: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao salvar as configurações!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao.equals("")) 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	if(strAcao.equals("obterConfigVigente"))
	    		ObterConfigVigente(request, response);
	    	else if(strAcao.equals("obterCamerasMonAoVivo"))
	    		ObterCamerasMonitoramentoAoVivo(request, response);
	    	else if(strAcao.equals("obterCamerasPorIdLocal"))
	    		ObterCamerasPorIdLocal(request, response);

			return;
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter configuração de monitoramento ao vivo: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar a configuração de monitoramento ao vivo!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	protected void ObterConfigVigente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			MonitoramentoAoVivo configVigente = MonitoramentosAoVivo.ObterConfigVigente();
			EnviarConfigMonitoramentoXML(response, configVigente);
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao ObterConfigVigente(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar a configuração vigente de monitoramento ao vivo!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	protected void ObterCamerasMonitoramentoAoVivo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String msg = null;
		Integer idGrupoExibicao = null;
		
		try
		{
			String strGrupoExibicao = request.getParameter("idGrupoExibicao");
			
			if (strGrupoExibicao == null || strGrupoExibicao.equals("") || strGrupoExibicao.equals("0")) 
	    	{
				msg = "Grupo de exibição das câmeras não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			idGrupoExibicao = Integer.parseInt(strGrupoExibicao);
			
			CamerasMonitoramentoAoVivo camerasMonAoVivo = CamerasMonitoramentoAoVivo.ObterCamerasMonitoramentoAoVivo(Inicializacao.listaEqptoCameras, idGrupoExibicao); 

			EnviarCamerasMonAoVivoXML(response, camerasMonAoVivo);
			
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao consultar as câmeras para monitoramento ao vivo!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}

	protected void ObterCamerasPorIdLocal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String msg = null;
		Integer idLocal = null;
		
		try
		{
			String strIdLocal = request.getParameter("idLocal");
			
			if (strIdLocal == null || strIdLocal.equals("") || strIdLocal.equals("0")) 
	    	{
				msg = "IdLocal das câmeras não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			idLocal = Integer.parseInt(strIdLocal);
			
			CamerasMonitoramentoAoVivo camerasMonAoVivo = CamerasMonitoramentoAoVivo.ObterCamerasPorIdLocal(Inicializacao.listaEqptoCameras, idLocal); 

			EnviarCamerasMonAoVivoXML(response, camerasMonAoVivo);
			
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao consultar as câmeras para video ao vivo!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private void Salvar(HttpServletRequest request, HttpServletResponse response, Integer idUsuario)
	{
		String msg = null;
		boolean sucesso = true;
		Integer segundos = null;
		
		try
		{
			String strSegundos = request.getParameter("segundos");
			
			if (strSegundos == null || strSegundos.equals("") || strSegundos.equals("0")) 
	    	{
				sucesso = false;
	    		msg = "Tempo de rodízio entre câmeras não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			segundos = Integer.parseInt(strSegundos);
			
			sucesso = MonitoramentosAoVivo.SalvarConfiguracoes(segundos, idUsuario);
			
			if (sucesso)
				msg = "Configurações salvas com sucesso!";
			else
				msg = "Falha ao salvar as configurações!";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg);
		}
		catch(Exception e)
		{
			logger.error("Erro ao inserir configurações de monitoramento ao vivo: " + e.getMessage(), e);
			msg = "Ocorreu um erro ao salvar as configurações!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
		
	}
	
	private void AtualizarGrupoEmExibicao(HttpServletRequest request, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		Integer idGrupoExibicao = null;
		
		try
		{
			String strGrupoExibicao = request.getParameter("idGrupoExibicao");
			
			if (strGrupoExibicao == null || strGrupoExibicao.equals("") || strGrupoExibicao.equals("0")) 
	    	{
				sucesso = false;
	    		msg = "Grupo de exibição das câmeras não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			idGrupoExibicao = Integer.parseInt(strGrupoExibicao);
			
			int maiorGrupo = CamerasMonitoramentoAoVivo.ObterMaiorGrupoExibicao(Inicializacao.listaEqptoCameras);
			
			idGrupoExibicao = (idGrupoExibicao > maiorGrupo ? 1 : idGrupoExibicao);
			
			Inicializacao.grupoCamerasEmExibicao = idGrupoExibicao;
			MonitoramentoAoVivo configVigente = MonitoramentosAoVivo.ObterConfigVigente();
			
			AtualizarListaCamerasMonitoramentoAoVivo(response);
			
			msg = "Grupo de câmeras em exibição atualizado com sucesso! Grupo em exibição: " + idGrupoExibicao;			
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg, idGrupoExibicao, configVigente.getSegundos());
		}
		catch(Exception e)
		{
			logger.error("Erro ao atualizar grupo de câmeras em exibição: " + e.getMessage(), e);
			msg = "falha ao atualizar grupo de câmeras em exibição! Grupo em exibição: " + Inicializacao.grupoCamerasEmExibicao;
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
		
	}
	
	private void AtualizarListaCamerasMonitoramentoAoVivo(HttpServletResponse response)
	{
		try
		{
			Inicializacao.listaEqptoCameras.removeAll(Inicializacao.listaEqptoCameras);
			Inicializacao.listaEqptoCameras = CamerasMonitoramentoAoVivo.ObterCamerasMonitoramentoAoVivoBD();
		}
		catch(Exception e)
		{
			String msg = "Erro ao atualizar câmeras disponíveis para monitoramento ao vivo";
			logger.error(msg + ": " +  e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void EnviarConfigMonitoramentoXML(HttpServletResponse response, MonitoramentoAoVivo configMonitoramento) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(MonitoramentoAoVivo.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(configMonitoramento, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			configMonitoramento = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarConfigMonitoramentoXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta do configurações de monitoramento ao vivo!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private void EnviarCamerasMonAoVivoXML(HttpServletResponse response, CamerasMonitoramentoAoVivo camerasMonAoVivo) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(CamerasMonitoramentoAoVivo.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(camerasMonAoVivo, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			camerasMonAoVivo = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarCamerasMonAoVivoXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de câmeras para monitoramento ao vivo!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

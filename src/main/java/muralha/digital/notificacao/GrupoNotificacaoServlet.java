/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 06/10/2021

*********************************************************************************/

package muralha.digital.notificacao;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import muralha.digital.consulta.TipoRegistro;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/GrupoNotificacao")
public class GrupoNotificacaoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GrupoNotificacaoServlet.class);
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
	    	
	    	if(strAcao.equals("obterGruposEmail"))
	    	{
	    		UUID idTipoNotificacao = TipoNotificacao.Tipo.EMAIL.GetID();
	    		ObterGrupos(idTipoNotificacao, request, response);
	    	}
	    		
	    	if(strAcao.equals("obterGruposSMS"))
	    	{
	    		UUID idTipoNotificacao = TipoNotificacao.Tipo.SMS.GetID();
	    		ObterGrupos(idTipoNotificacao, request, response);
	    	}
    		
	    	if(strAcao.equals("obterGruposPopup"))
	    	{
	    		UUID idTipoNotificacao = TipoNotificacao.Tipo.POPUP.GetID();
	    		ObterGrupos(idTipoNotificacao, request, response);
	    	}
	    	
	    	if(strAcao.equals("obterGruposEmailPorIdOcorrencia"))
	    	{
	    		UUID idTipoNotificacao = TipoNotificacao.Tipo.EMAIL.GetID();
	    		ObterGruposPorOcorrenciaTipoNotificacao(idTipoNotificacao, request, response);
	    	}
	    		
	    	if(strAcao.equals("obterGruposSmsPorIdOcorrencia"))
	    	{
	    		UUID idTipoNotificacao = TipoNotificacao.Tipo.SMS.GetID();
	    		ObterGruposPorOcorrenciaTipoNotificacao(idTipoNotificacao, request, response);
	    	}
    		
	    	if(strAcao.equals("obterGruposPopupPorIdOcorrencia"))
	    	{
	    		UUID idTipoNotificacao = TipoNotificacao.Tipo.POPUP.GetID();
	    		ObterGruposPorOcorrenciaTipoNotificacao(idTipoNotificacao, request, response);
	    	}
	    	
	    	if(strAcao.equals("obterGruposPorIdGrupoETipoNotificacao"))
	    	{
	    		ConsultarConfiguracaoPorGrupoETipoAlertaOcorrencia(request, response);
	    	}
	    	if(strAcao.equals("buscarGruposView"))
	    	{
	    		ObterGruposView(request, response);
	    	}
	    	if(strAcao.equals("buscarGruposSelecionados"))
	    	{
	    		BuscarGruposSelecionados(request, response);
	    	}
			if(strAcao.equals("obterStatusAgenteGuarnicao"))
			{
				ObterStatusAgenteGuarnicao(request, response);
			}
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de Alerta: " + e.getMessage(), e);
	    }
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
			
			if(strAcao.equals("configAgenteGuarnicao"))
			{
				ConfigAgenteGuarnicao(request, response);
			}
		}
		catch(Exception e)
		{
			logger.error("Erro no processo doPost() de requisição de GrupoNotificacao: " + e.getMessage(), e);
		}
	}

	
	protected void ObterGrupos(UUID idTipoNotificacao, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strTipoRegistro = request.getParameter("tipoRegistro");
			String strTipoAlertaOcorrencia = request.getParameter("tipoAlertaOcorrencia");
			
			
	    	if (strTipoRegistro == null || strTipoRegistro.equals("") || strTipoRegistro.equals("0")) 
	    	{
	    		String msg = "Tipo do Registro não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	if (strTipoAlertaOcorrencia == null || strTipoAlertaOcorrencia.equals("") || strTipoAlertaOcorrencia.equals("0")) 
	    	{
	    		String msg = "Tipo Alerta/Irregularidade não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	UUID idTipoRegistro = null, idTipoAlertaOcorrencia = null;
	    	
	    	try
	    	{
    			idTipoRegistro = UUID.fromString(strTipoRegistro.trim());
				idTipoAlertaOcorrencia = UUID.fromString(strTipoAlertaOcorrencia);
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
	    	
			//Cria objeto de retorno
			GruposNotificacao grupos = new GruposNotificacao();
			grupos.setListaGruposNotificacao(new ArrayList<GrupoNotificacao>());
				
			//Faz a consulta já existente no banco de dados
			List<GrupoNotificacao> listaGrupoNotificacao = GruposNotificacao.ObterListaGruposNotificacao(idTipoRegistro, idTipoAlertaOcorrencia, idTipoNotificacao);
			grupos.setListaGruposNotificacao(listaGrupoNotificacao);
			
			EnviarRespostaXML(response, grupos);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter grupos de notificação: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar grupos de notificação!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	protected void ObterGruposPorOcorrenciaTipoNotificacao(UUID idTipoNotificacao, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strIdOcorrencia = request.getParameter("idOcorrencia");
			
	    	if (strIdOcorrencia == null || strIdOcorrencia.equals("") || strIdOcorrencia.equals("0")) 
	    	{
	    		String msg = "Identificador da Irregularidade não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	UUID idOcorrencia = null, idTipoRegistro = null;
	    	
	    	try
	    	{
    			idOcorrencia = UUID.fromString(strIdOcorrencia.trim());
    			idTipoRegistro = TipoRegistro.Tipo.IRREGULARIDADES.GetID();
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
	    	
			//Cria objeto de retorno
			GruposNotificacao grupos = new GruposNotificacao();
			grupos.setListaGruposNotificacao(new ArrayList<GrupoNotificacao>());
				
			//Faz a consulta já existente no banco de dados
			List<GrupoNotificacao> listaGrupoNotificacao = GruposNotificacao.ObterListaGruposPorOcorrenciaTipoNotificacao(idOcorrencia, idTipoNotificacao, idTipoRegistro);
			grupos.setListaGruposNotificacao(listaGrupoNotificacao);
			
			EnviarRespostaXML(response, grupos);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter grupos de notificação por irregularidade e tipo de notificação: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar grupos de notificação por irregularidade e tipo de notificação!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	protected void ObterGruposView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    	
			//Cria objeto de retorno
			ViewGruposAlertas grupos = new ViewGruposAlertas();
			grupos.setListaViewGrupoAlertas(new ArrayList<ViewGrupoAlertas>());
				
			//Faz a consulta já existente no banco de dados
			List<ViewGrupoAlertas> lista = ViewGruposAlertas.consultarView();
			
			grupos.setListaViewGrupoAlertas(lista);
			
			EnviarRespostaXMLView(response, grupos);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter grupos de notificação na view" + e.getMessage(), e);
			String msg = "Não foi possível buscar os grupos para notificação!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	protected void ConsultarConfiguracaoPorGrupoETipoAlertaOcorrencia(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    							
			UUID idTipoAlertaOcorrencia = UUID.fromString(request.getParameter("idTipoAlertaOcorrencia"));
			
			//Cria objeto de retorno
			GruposNotificacao grupos = new GruposNotificacao();
			grupos.setListaGruposNotificacao(new ArrayList<GrupoNotificacao>());
				
			//Faz a consulta já existente no banco de dados
			List<GrupoNotificacao> listaGrupoNotificacao = GruposNotificacao.ConsultarConfiguracaoPorTipoAlertaOcorrencia(idTipoAlertaOcorrencia);
			grupos.setListaGruposNotificacao(listaGrupoNotificacao);
			
			EnviarRespostaXML(response, grupos);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter grupos de notificação por idGrupo e tipo de notificação: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar grupos de notificação por idGrupo e tipo de notificação!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
		
	protected Boolean InserirConfigsPorLista(List<GrupoNotificacao> grupos, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    				
			Boolean result = GruposNotificacao.InserirConfigsPorLista(grupos);
			return result;		
		}
		catch(Exception e)
		{
			logger.error("Erro ao inserir as configs de grupos por lista: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao atualizar as configs!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return false;
		}
	}
	
	protected void BuscarGruposSelecionados(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    
			//Cria objeto de retorno
			GruposNotificacao grupos = new GruposNotificacao();
			grupos.setListaGruposNotificacao(new ArrayList<GrupoNotificacao>());
							
			List<GrupoNotificacao> result = GruposNotificacao.BuscarGruposSelecionados();
			grupos.setListaGruposNotificacao(result);
			
			EnviarRespostaXML(response, grupos);	
		}
		catch(Exception e)
		{
			logger.error("Erro ao inserir as configs de grupos por lista: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao atualizar as configs!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, GruposNotificacao gruposNotificacao) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(GruposNotificacao.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(gruposNotificacao, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			gruposNotificacao = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de grupos de notificação!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private void EnviarRespostaXMLView(HttpServletResponse response, ViewGruposAlertas resultView) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(ViewGruposAlertas.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(resultView, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			resultView = null;			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de grupos da view!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}

	protected void ConfigAgenteGuarnicao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strStatus = request.getParameter("status");
			boolean status = "true".equals(strStatus);
			
			Boolean resultado = GruposNotificacao.ConfigAgenteGuarnicao(status);
			
			if (resultado) {
				respostaXML.EnviarRespostaRequisicaoXML(response, true, "Configuração atualizada com sucesso");
			} else {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao atualizar configuração");
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao configurar agente de guarnição: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao atualizar a configuração!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void ObterStatusAgenteGuarnicao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			Integer status = GruposNotificacao.ObterStatusAgenteGuarnicao();
			
			String xmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><config>";
			xmlResponse += "<status_agente_guarnicao>" + status + "</status_agente_guarnicao>";
			xmlResponse += "</config>";
			
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xmlResponse);
			response.getWriter().flush();
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter status do agente de guarnição: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao obter a configuração!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
}

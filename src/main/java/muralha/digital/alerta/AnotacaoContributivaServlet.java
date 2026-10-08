/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 06/10/2021

*********************************************************************************/

package muralha.digital.alerta;

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

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/Alerta/AnotacaoContributiva")
public class AnotacaoContributivaServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AnotacaoContributivaServlet.class);
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
	    		    	
	    	if(strAcao.equals("obterAnotacaoContributivaPorIdAlerta"))	    	
	    		ObterAnotacaoContributivaPorIdAlerta(request, response);
	    		
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de anotação contributiva: " + e.getMessage(), e);
	    }
    }

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
		
		
		String strIdAlerta = request.getParameter("idAlerta");
		String strAnotacaoAlertaCad = request.getParameter("anotacaoContributiva");
		String msg = null;
    	
		try
    	{
	    	if (strAnotacaoAlertaCad == null || strAnotacaoAlertaCad == "") 
	    	{
	    		msg = "Anotação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
	    	if (strIdAlerta == null || strIdAlerta.equals("") || strIdAlerta.equals("0")) 
	    	{
	    		msg = "Identificador do Alerta não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
			UUID idAlerta = null;

			try
	    	{
				idAlerta = UUID.fromString(strIdAlerta.trim());
			}
	    	catch (Exception e)
	    	{
				msg = "Erro ao preparar dados para consulta!";
				logger.error(msg + ": " + e.getMessage(), e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
		
	    	InserirAnotacaoContributiva(idAlerta, strAnotacaoAlertaCad, idUsuario, response);
	    		
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doPost() de requisição de anotação contributiva: " + e.getMessage(), e);
	    }
    }
    
	private void InserirAnotacaoContributiva(UUID idAlerta, String strAnotacaoAlertaCad, Integer idUsuario, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		
		try
		{
			sucesso = AnotacoesContributivas.InserirAnotacaoContributiva(idAlerta, strAnotacaoAlertaCad, idUsuario);
			
			if (sucesso)
				msg = "Anotação contributiva adicionada com sucesso!";
			else
				msg = "Falha ao adicionar a anotação contributiva!";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg);
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao adicionar a anotação contributiva!";
			logger.error(msg + ": "  + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
		
	}
	
	protected void ObterAnotacaoContributivaPorIdAlerta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strIdAlerta = request.getParameter("idAlerta");
			
	    	if (strIdAlerta == null || strIdAlerta.equals("") || strIdAlerta.equals("0")) 
	    	{
	    		String msg = "Identificador do Alerta não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
			UUID idAlerta = null;

			try
	    	{
    			idAlerta = UUID.fromString(strIdAlerta.trim());
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
	    	
			//Cria objeto de retorno
			AnotacoesContributivas anotacoes = new AnotacoesContributivas();
			anotacoes.setAnotacoesContributivas(new ArrayList<AnotacaoContributiva>());
				
			//Faz a consulta já existente no banco de dados
			List<AnotacaoContributiva> listaAnotacao = AnotacoesContributivas.obterAnotacoesContributivasPorIdAlerta(idAlerta);
			anotacoes.setAnotacoesContributivas(listaAnotacao);
			
			EnviarRespostaXML(response, anotacoes);

			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter dados do alerta: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar o alerta!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, AnotacoesContributivas anotacoes) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(AnotacoesContributivas.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(anotacoes, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			anotacoes = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de anotações contributivas!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

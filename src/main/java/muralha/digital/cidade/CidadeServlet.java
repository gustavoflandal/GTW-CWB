package muralha.digital.cidade;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/CidadesMinasGerais")
public class CidadeServlet extends HttpServlet implements Servlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CidadeServlet.class);
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
	    	
	    	if(strAcao.equals("buscarCidades"))
	    		ObterCidades(request, response);	    		
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() de requisição de obterTiposSolicitante: " + e.getMessage(), e);
	    }
	}
	
	public void ObterCidades(HttpServletRequest request, HttpServletResponse response) throws JAXBException, IOException, ConexaoException, SQLException {
		
		//Cria objeto de retorno
		Cidades listaCidades = new Cidades();
		listaCidades.setListaCidades(new ArrayList<Cidade>());
			
		List<Cidade> resultListaCidades = new ArrayList<Cidade>();
		resultListaCidades = Cidades.ObterListaCidades();			
		
		listaCidades.setListaCidades(resultListaCidades);

		EnviarRespostaXML(response, listaCidades);
	}

	@SuppressWarnings("unused")
	private void EnviarRespostaXML(HttpServletResponse response, Cidades listaCidades) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(Cidades.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(listaCidades, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			listaCidades = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de cidades de Minas Gerais!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}

package muralha.digital.perguntasRespostas;

import java.io.BufferedReader;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/AlertaQuestionario")
public class AlertaQuestionarioServlet extends HttpServlet implements Servlet {
    private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AlertaQuestionarioServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		
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
	    	
	    	if(strAcao.equals("obterQuestionarios")) {
	    		ObterQuestionariosAlerta(request, response, strAcao);
	    	}
	    	
	    	if(strAcao.equals("verificarQuestionarioObrigatorio")) {
	    		VerificarQuestionarioObrigatorio(request, response, strAcao);
	    	}
	    	
	    	if(strAcao.equals("obterRespostas")) {
	    		ObterRespostasQuestionario(request, response, strAcao);
	    	}
    	}
    	
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doGet() da AlertaQuestionarioServlet: " + e.getMessage(), e);
	    }
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Integer idUsuario = null;
	    final Acesso acessoUsuario = new Acesso(request, response, true);
	    if (!acessoUsuario.verificaAcesso()) {
	        new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
	        return;
	    } else {
	        idUsuario = acessoUsuario.getUsuario().getId();
	    }

	    try {
	        String msg = null;
	        String strAcao = request.getParameter("acao");

	        if (strAcao == null || strAcao.isEmpty()) {
	            msg = "Ação não informada!";
	            logger.error(msg);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        if (strAcao.equals("registrarAcessoUsuario")) {
	        	RegistrarAcessoUsuario(request, response, strAcao, idUsuario);
	        }
	        
	        if(strAcao.equals("registrarRespostasUsuario")) {
	        	RegistrarRespostasUsuario(request, response, strAcao, idUsuario);
	        };
	        
	    } catch (Exception e) {
	        logger.error("Erro no processo doPost() da OcorrenciaLigacaoServlet: " + e.getMessage(), e);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("Erro interno no servidor.");
	    }
	}
	
	public void ObterQuestionariosAlerta (HttpServletRequest request, HttpServletResponse response, String strAcao) throws JAXBException, IOException, ConexaoException, SQLException {
		
		//Cria objeto de retorno
		AlertasQuestionarios listaQuestionarios = new AlertasQuestionarios();
		listaQuestionarios.setListaAlertaQuestionario(new ArrayList<AlertaQuestionario>());
			
		List<AlertaQuestionario> resultListaQuestionarios = new ArrayList<AlertaQuestionario>();
		resultListaQuestionarios = AlertasQuestionarios.ObterQuestionariosAlerta();
		
		listaQuestionarios.setListaAlertaQuestionario(resultListaQuestionarios);

		EnviarRespostaXML(response, listaQuestionarios, strAcao);
	}
	
	public void RegistrarAcessoUsuario (HttpServletRequest request, HttpServletResponse response, String strAcao, int idUsuario) throws JAXBException, IOException, ConexaoException, SQLException {
		
		String idAlerta = request.getParameter("idAlerta");
		AlertasQuestionarios.RegistrarAcessoUsuario(idAlerta, idUsuario);
	}
	
	public static void VerificarQuestionarioObrigatorio (HttpServletRequest request, HttpServletResponse response, String strAcao) throws JAXBException, IOException, ConexaoException, SQLException {
		
		Boolean result = AlertasQuestionarios.VerificarQuestionarioObrigatorio();
		
		EnviarBooleanXML(response, result, strAcao);
	}
	
	public void RegistrarRespostasUsuario (HttpServletRequest request, HttpServletResponse response, String strAcao, int idUsuario) throws JAXBException, IOException, ConexaoException, SQLException {
		
	    // 1. Lê o corpo JSON da requisição
	    StringBuilder sb = new StringBuilder();
	    String line;
	    try (BufferedReader reader = request.getReader()) {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	    }
	    String requestBody = sb.toString();

	    // 2. Converte JSON em lista de objetos
	    Gson gson = new Gson();
	    List<AlertaQuestionarioResposta> respostas = gson.fromJson(
	        requestBody,
	        new TypeToken<List<AlertaQuestionarioResposta>>(){}.getType()
	    );
	    
	    //Realiza a persistência no banco
	    AlertasQuestionarios.RegistrarRespostasUsuario(respostas, idUsuario);
	}
	
	public void ObterRespostasQuestionario (HttpServletRequest request, HttpServletResponse response, String strAcao) throws JAXBException, IOException, ConexaoException, SQLException {
		
		String idAlerta = request.getParameter("idAlerta");
		
		//Cria objeto de retorno
		AlertasQuestionarios listaQuestionarios = new AlertasQuestionarios();
		listaQuestionarios.setListaRespostasQuestionario(new ArrayList<RespostaQuestionarioDTO>());
			
		List<RespostaQuestionarioDTO> resultRespostasQuestionario = new ArrayList<RespostaQuestionarioDTO>();
		resultRespostasQuestionario = AlertasQuestionarios.ObterRespostasQuestionario(idAlerta);
		
		listaQuestionarios.setListaRespostasQuestionario(resultRespostasQuestionario);

		EnviarRespostaXML(response, listaQuestionarios, strAcao);
	}
	
	private <T> void EnviarRespostaXML(HttpServletResponse response, T objetoResposta, String strAcao) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(objetoResposta.getClass());
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(objetoResposta, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			objetoResposta = null;			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da ação "+ strAcao;
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private static void EnviarBooleanXML(HttpServletResponse response, Boolean valor, String strAcao) throws IOException {
	    try {
	        String xml = "<result>" + valor + "</result>";
	        response.setContentType("text/xml");
	        response.setCharacterEncoding("UTF-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().write(xml);
	        response.getWriter().flush();
	    } catch (Exception e) {
	        logger.error("Erro ao enviar booleano em " + strAcao, e);
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar ação " + strAcao);
	    }
	}
}

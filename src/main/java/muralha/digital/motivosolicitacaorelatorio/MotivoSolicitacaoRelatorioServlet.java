package muralha.digital.motivosolicitacaorelatorio;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

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

@WebServlet("/MuralhaDigital/MotivoSolicitacaoRelatorio")
public class MotivoSolicitacaoRelatorioServlet extends HttpServlet implements Servlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MotivoSolicitacaoRelatorioServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
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

	        if (strAcao.equals("registrarMotivoSolicitacaoRelatorio")) {
	        	RegistrarMotivoSolicitacao(request, response, strAcao, idUsuario);
	        }
	        
	    } catch (Exception e) {
	        logger.error("Erro no processo doPost() da OcorrenciaLigacaoServlet: " + e.getMessage(), e);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("Erro interno no servidor.");
	    }
	}
	
	public static void RegistrarMotivoSolicitacao (HttpServletRequest request, HttpServletResponse response, String strAcao, int idUsuario) throws JAXBException, IOException, ConexaoException, SQLException {
		
		String motivo = request.getParameter("motivo");
		String placa = request.getParameter("placa");
		String tipoSolicitacao = request.getParameter("tipoSolicitacao");
		
		Boolean result = MotivosSolicitacoesRelatorios.RegistrarMotivoSolicitacao(idUsuario, motivo, placa, tipoSolicitacao);
		
		EnviarBooleanXML(response, result, strAcao);
	}
	
	@SuppressWarnings("unused")
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

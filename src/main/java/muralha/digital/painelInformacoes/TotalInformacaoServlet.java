package muralha.digital.painelInformacoes;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
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

@WebServlet("/MuralhaDigital/PainelInformacao/TotalInformacoes")
public class TotalInformacaoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TotalInformacaoServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    final Acesso acesso = new Acesso(request, response, true);
	    if (!acesso.verificaAcesso()) {
	        new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
	        return;
	    }

	    try {
	        String acao = request.getParameter("acao");
	        String periodo = request.getParameter("periodo");
	        if (acao == null || periodo == null) {
	            enviarMensagemXML(response, false, "Parâmetros inválidos.");
	            return;
	        }
		       TotalInformacaoResponse pResponse;
			if (acao.equals("ObterTotalInformacoes")) {
		        pResponse = TotalInformacoes.ObterTotalInformacao(periodo);
				enviarRespostaXML(response, pResponse);
				return;
			}

	    } catch (Exception e) {
	        logger.error("Erro ao obter Total de Informações: " + e.getMessage(), e);
	        enviarMensagemXML(response, false, "Ocorreu um erro ao consultar informações do Painel de Informações!");
	    }
	}

	private void enviarRespostaXML(HttpServletResponse response, Object objeto) 
	        throws JAXBException, IOException {
	    try {
	        if (objeto == null) {
	            enviarMensagemXML(response, false, "Objeto nulo para serialização!");
	            return;
	        }

	        JAXBContext context = JAXBContext.newInstance(objeto.getClass());
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // <<< UTF-8 garantido

	        // Tenta setar sucesso se existir o método
	        try {
	            objeto.getClass().getMethod("setSucesso", boolean.class).invoke(objeto, true);
	        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
	            // Ignora se não existir o método setSucesso
	        }

	        response.setContentType("application/xml; charset=UTF-8"); // <<< Content-Type UTF-8
	        response.setCharacterEncoding("UTF-8");
	        response.setStatus(HttpServletResponse.SC_OK);

	        marshaller.marshal(objeto, response.getWriter()); // escreve direto no writer

	    } catch (Exception e) {
	        logger.error("Erro ao enviarRespostaXML(): " + e.getMessage(), e);
	        String msg = "Ocorreu um erro ao consultar informações do Painel de Informações!";
	        enviarMensagemXML(response, false, msg);
	    }
	}
	
	private void enviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) {
		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);

		try {
			JAXBContext context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marsHall.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error("Erro gravíssimo ao preparar resposta da requisição: " + e.getMessage(), e);
		}
	}
}

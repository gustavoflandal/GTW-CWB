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

@WebServlet("/MuralhaDigital/Boletim/Cidade")
public class BoletimCidadeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoletimCidadeServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Validando acesso do usuário
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			// Cria objeto de retorno
			BoletimCidades listaCidades = new BoletimCidades();
			listaCidades.setListaCidades(new ArrayList<Cidade>());

			// Faz a consulta no banco de dados
			List<Cidade> cidades = BoletimCidades.obterListaCidades();
			listaCidades.setListaCidades(cidades);

			enviarRespostaXML(response, listaCidades);

		} catch (Exception e) {
			logger.error("Erro ao obter cidades: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar cidades!";
			logger.error(msg);
			enviarMensagemXML(response, false, msg);
			return;
		}
	}

	private void enviarRespostaXML(HttpServletResponse response, BoletimCidades listaCidades)
			throws JAXBException, IOException {
		JAXBContext context;
		try {
			// Formando dados para envio
			context = JAXBContext.newInstance(BoletimCidades.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(listaCidades, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("enviarRespostaXML():: Registros enviados: " + listaCidades.getListaCidades().size());

		} catch (Exception e) {
			logger.error("Erro ao enviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de cidades!";
			logger.error(msg);
			enviarMensagemXML(response, false, msg);
			return;
		}
	}

	private void enviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) {
		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);

		// Formando dados para envio
		JAXBContext evidencia_context;
		try {
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marshaller = evidencia_context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

		} catch (Exception e) {
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
}
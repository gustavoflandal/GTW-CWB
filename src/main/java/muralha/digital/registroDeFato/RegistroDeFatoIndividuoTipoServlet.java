package muralha.digital.registroDeFato;

import java.io.IOException;
import java.io.StringWriter;
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

@WebServlet("/MuralhaDigital/RegistroDeFato/IndividuoTipo")
public class RegistroDeFatoIndividuoTipoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RegistroDeFatoIndividuoTipoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Validando acesso do usuário
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso()) {
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			String msg = null;
			String strAcao = request.getParameter("acao");

			if (strAcao == null || strAcao.equals("")) {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			if (strAcao.equals("obterLista")) {
				BuscarTiposIndividuo(response);
			}									
			else {
				msg = "Ação não reconhecida!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar Registros De Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void BuscarTiposIndividuo(HttpServletResponse response) throws JAXBException, IOException {
		try {
			
			RegistroDeFatoIndividuoTipos result = RegistroDeFatoIndividuoTipos.BuscarTiposIndividuo();
			EnviarRespostaXML(response, result);			
			
		}catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de tipos de indivíduos!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, RegistroDeFatoIndividuoTipos registrosTiposIndividuos) throws JAXBException, IOException {
		JAXBContext context;
		try {
			// Formando dados para envio
			context = JAXBContext.newInstance(RegistroDeFatoIndividuoTipos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marsHall.marshal(registrosTiposIndividuos, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("EnviarRespostaXML():: Registros enviados: "
					+ Integer.toString(registrosTiposIndividuos.getListaTiposIndividuos().size()));
			registrosTiposIndividuos = null;

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de tipos de indivíduos!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
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

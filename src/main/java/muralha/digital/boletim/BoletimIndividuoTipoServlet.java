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

@WebServlet("/MuralhaDigital/Boletim/TipoIndividuo")
public class BoletimIndividuoTipoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoletimIndividuoTipoServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			BoletimIndividuosTipos tipos = new BoletimIndividuosTipos();
			tipos.setListaTiposIndividuo(new ArrayList<BoletimIndividuoTipo>());

			List<BoletimIndividuoTipo> lista = BoletimIndividuosTipos.ObterListaTiposIndividuo();
			tipos.setListaTiposIndividuo(lista);

			enviarRespostaXML(response, tipos);

		} catch (Exception e) {
			logger.error("Erro ao obter tipos de indivíduo: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar tipos de indivíduo!";
			enviarMensagemXML(response, false, msg);
		}
	}

	private void enviarRespostaXML(HttpServletResponse response, BoletimIndividuosTipos tipos)
			throws JAXBException, IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(BoletimIndividuosTipos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marsHall.marshal(tipos, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("enviarRespostaXML():: Registros enviados: " + tipos.getListaTiposIndividuo().size());

		} catch (Exception e) {
			logger.error("Erro ao enviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Erro ao retornar o resultado da consulta de tipos de indivíduo!";
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

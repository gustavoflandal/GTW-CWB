package muralha.digital.registroDeFato;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/RegistroDeFato/Historico")
public class RegistroDeFatoHistoricoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RegistroDeFatoHistoricoServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 1. Bloco de verificação de acesso (permanece o mesmo)
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			String strAcao = request.getParameter("acao");

			// 2. Validação da ação (permanece a mesma)
			if (strAcao == null || strAcao.trim().isEmpty()) {
				String msg = "Ação não informada!";
				logger.error(msg);
				EnviarMensagemXML(response, false, msg);
				return;
			}

			switch (strAcao) {
			case "obterHistoricoPorId":
				obterHistoricoPorIdRegistro(request, response);
				break;

			default:
				String msg = "Ação '" + strAcao + "' é inválida ou não foi encontrada!";
				logger.warn(msg);
				EnviarMensagemXML(response, false, msg);
				break;
			}
		} catch (NumberFormatException e) {
			logger.error("Erro ao converter parâmetro de ID: " + e.getMessage(), e);
			String msg = "O parâmetro de ID fornecido é inválido!";
			logger.error(msg);
			EnviarMensagemXML(response, false, msg);
			return;
		} catch (Exception e) {
			logger.error("Erro inesperado no doGet: " + e.getMessage(), e);
			String msg = "Ocorreu um erro inesperado ao processar a solicitação!";
			logger.error(msg);
			EnviarMensagemXML(response, false, msg);
			return;
		}
	}
	
	/**
	 * Processa a requisição para obter um histórico específico pelo registro de Fato ID.
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws Exception Lança exceções que serão capturadas pelo método doGet.
	 */
	private void obterHistoricoPorIdRegistro(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
	    // 1. Obter e validar o parâmetro
	    String idRegistroStr = request.getParameter("idRegistro");
	    
	    if (idRegistroStr == null || idRegistroStr.trim().isEmpty()) {
	        String msg = "O parâmetro 'idRegistro' não foi informado.";
	        logger.warn(msg);
	        EnviarMensagemXML(response, false, msg);
	        return;
	    }

	    long idRegistro = Long.parseLong(idRegistroStr);

	    List<RegistroDeFatoHistoricoDTO> listaDeHistoricos = RegistroDeFatoHistoricos.ListarHistoricosPorRegistro(idRegistro);

	    RegistroDeFatoHistoricoResponse resposta = new RegistroDeFatoHistoricoResponse(listaDeHistoricos);

	    //Enviar a resposta XML
	    logger.info("Enviando " + listaDeHistoricos.size() + " registros de histórico para o Registro de Fato ID: " + idRegistro);
	    EnviarRespostaXML(response, resposta);
	}

	public <T> void EnviarRespostaXML(HttpServletResponse response, T objetoOriginal) {
		try {
			JAXBContext context = JAXBContext.newInstance(objetoOriginal.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// 1. Crie uma fábrica de documentos DOM
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			// 2. Marshall (converta) o objeto Java para um Documento DOM em memória
			marshaller.marshal(objetoOriginal, document);

			// 3. Obtenha o elemento raiz do documento
			Element rootElement = document.getDocumentElement();

			// 4. Verifique se o nó "sucesso" já existe
			NodeList sucessoNodes = rootElement.getElementsByTagName("sucesso");

			if (sucessoNodes.getLength() == 0) {
				// 5. Se não existir, crie o elemento e adicione-o ao nó raiz
				Element sucessoElement = document.createElement("sucesso");
				sucessoElement.appendChild(document.createTextNode("true"));
				// Adiciona como o primeiro filho para consistência
				rootElement.insertBefore(sucessoElement, rootElement.getFirstChild());
			}

			// 6. Transforme o Documento DOM (agora modificado) de volta para uma String XML
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			// Configurações para uma saída formatada e bonita
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(new DOMSource(document), new StreamResult(sw));
			String xml = sw.toString();

			// 7. Envie a resposta
			response.setHeader("Content-Type", "text/xml; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("EnviarRespostaXML_SemWrapper():: Resposta XML enviada para o tipo: "
					+ objetoOriginal.getClass().getSimpleName());

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML_SemWrapper() para o objeto "
					+ objetoOriginal.getClass().getSimpleName() + ": " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar Registro de Fato Historico!";
			EnviarMensagemXML(response, false, msg);
			return;
		}
	}

	private void EnviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) {
		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);

		JAXBContext evidencia_context;
		try {
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marsHall = evidencia_context.createMarshaller();
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
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}
}
package muralha.digital.registroDeFato;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/RegistroDeFato/Doducmento")
public class RegistroDeFatoDocumentoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RegistroDeFatoDocumentoServlet.class);
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
				enviarMensagemXML(response, false, msg);
				return;
			}

			if (strAcao.equals("obterPorId")) {
				ObterPorId(request, response);
			} else if (strAcao.equals("downloadDocumento")) {
				DownloadDocumento(request, response);
			} else {
				msg = "Ação não reconhecida!";
				logger.error(msg);
				enviarMensagemXML(response, false, msg);
				return;
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar boletins!";
			logger.error(msg, e);
			enviarMensagemXML(response, false, msg);
			return;
		}
	}

	protected void ObterPorId(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String strId = request.getParameter("id");
			Integer id;

			if (strId == null || strId.trim().equals("") || strId.trim().equals("0")) {
				String msg = "Identificador do documento não informado!";
				logger.error(msg);
				enviarMensagemXML(response, false, msg);
				return;
			}

			try {
				id = Integer.parseInt(strId);
			} catch (NumberFormatException e) {
				String msg = "ID inválido para documento.";
				logger.error(msg, e);
				enviarMensagemXML(response, false, msg);
				return;
			}

			RegistroDeFatoDocumento documento = RegistroDeFatoDocumentos.ObterPorId(id);
			if (documento == null) {
				String msg = "Documento não encontrado!";
				logger.warn(msg);
				enviarMensagemXML(response, false, msg);
				return;
			}

			EnviarRespostaXML(response, documento);

		} catch (Exception e) {
			String msg = "Erro ao obter documento!";
			logger.error(msg, e);
			enviarMensagemXML(response, false, msg);
		}
	}

	private void DownloadDocumento(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String strId = request.getParameter("id");
			Integer id;

			if (strId == null || strId.trim().isEmpty() || strId.equals("0")) {
				String msg = "ID do documento não informado!";
				logger.error(msg);
				enviarMensagemXML(response, false, msg);
				return;
			}

			try {
				id = Integer.parseInt(strId);
			} catch (NumberFormatException e) {
				String msg = "ID inválido para download de documento.";
				logger.error(msg, e);
				enviarMensagemXML(response, false, msg);
				return;
			}

			// Buscar o documento
			RegistroDeFatoDocumento documento = RegistroDeFatoDocumentos.ObterPorId(id);
			if (documento == null) {
				String msg = "Documento não encontrado!";
				logger.warn(msg);
				enviarMensagemXML(response, false, msg);
				return;
			}

			// Caminho do arquivo
			String caminhoArquivo = documento.getDirArquivo();
			File arquivo = new File(caminhoArquivo);
			if (!arquivo.exists()) {
				String msg = "Arquivo não encontrado no servidor!";
				logger.warn(msg + " Caminho: " + caminhoArquivo);
				enviarMensagemXML(response, false, msg);
				return;
			}

			// Definir tipo de conteúdo para download
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");
			response.setContentLength((int) arquivo.length());

			// Escrever o conteúdo no response
			try (FileInputStream fis = new FileInputStream(arquivo)) {
				byte[] buffer = new byte[4096];
				int bytesLidos;
				while ((bytesLidos = fis.read(buffer)) != -1) {
					response.getOutputStream().write(buffer, 0, bytesLidos);
				}
				response.getOutputStream().flush();
			}

			logger.info("Download realizado com sucesso. Arquivo: " + arquivo.getAbsolutePath());

		} catch (Exception e) {
			String msg = "Erro ao realizar download do documento!";
			logger.error(msg, e);
			enviarMensagemXML(response, false, msg);
		}
	}

	private void EnviarRespostaXML(HttpServletResponse response, RegistroDeFatoDocumento documento)
			throws JAXBException, IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(RegistroDeFatoDocumento.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(documento, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("Documento retornado com sucesso: ID = " + documento.getId());
			documento = null;

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado de buscar por ID documento!";
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
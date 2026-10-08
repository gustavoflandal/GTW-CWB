package muralha.digital.painelInformacoes;

import java.io.IOException;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import java.text.ParseException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@WebServlet("/MuralhaDigital/PainelInformacao/Camera")
public class CameraServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CameraServlet.class);
	
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

	        switch (acao) {
	            case "ObterCamerasPorDispositivoId":
	                processarObterCameras(request, response);
	                break;

	            case "buscarLeituraPlacas":
	                processarBuscarLeituraPlacas(request, response);
	                break;

	            case "ObterStatusCamera":
	                processarObterStatusCamera(request, response);
	                break;

	            case "ObterRecursoServidor":
	                processarObterRecursoServidor(response);
	                break;

	            default:
	                enviarMensagemXML(response, false, "Ação não reconhecida.");
	        }
	    } catch (Exception e) {
	        logger.error("Erro ao processar requisição: " + e.getMessage(), e);
	        enviarMensagemXML(response, false, "Ocorreu um erro ao consultar informações do Painel de Informações!");
	    }
	}

	
	private void processarObterCameras(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    int dispositivoId = Integer.parseInt(request.getParameter("dispositivoId"));
	    CameraResponse pResponse = Cameras.ObterCamerasPorDispositivoId(dispositivoId);
	    enviarRespostaXML(response, pResponse);
	}

	private void processarBuscarLeituraPlacas(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String dataInicio = request.getParameter("dataInicio");
	    String dataFim = request.getParameter("dataFim");
	    String camerasParam = request.getParameter("cameras");

	    if (dataInicio == null || dataFim == null || camerasParam == null || camerasParam.isEmpty()) {
	        enviarMensagemXML(response, false, "Parâmetros inválidos: datas ou câmeras não informadas.");
	        return;
	    }

	    // CSV → List<Integer>
	    List<Integer> idsCameras = new ArrayList<>();
	    for (String s : camerasParam.split(",")) {
	        try {
	            idsCameras.add(Integer.parseInt(s.trim()));
	        } catch (NumberFormatException e) {
	            logger.warn("ID de câmera inválido: " + s);
	        }
	    }
	    if (idsCameras.isEmpty()) {
	        enviarMensagemXML(response, false, "Nenhuma câmera válida informada.");
	        return;
	    }

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date inicioDate = sdf.parse(dataInicio);
	    Date fimDate = sdf.parse(dataFim);

	    LeituraPlacasResponse percentuais = Cameras.ObterPercentualPlacas(idsCameras, inicioDate, fimDate);
	    enviarRespostaXML(response, percentuais);
	}

	private void processarObterStatusCamera(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String camerasParam = request.getParameter("cameras");
	    if (camerasParam == null || camerasParam.isEmpty()) {
	        enviarMensagemXML(response, false, "Nenhuma câmera informada.");
	        return;
	    }

	    List<Integer> idsCameras = new ArrayList<>();
	    for (String s : camerasParam.split(",")) {
	        try {
	            idsCameras.add(Integer.parseInt(s.trim()));
	        } catch (NumberFormatException e) {
	            logger.warn("ID de câmera inválido: " + s);
	        }
	    }
	    if (idsCameras.isEmpty()) {
	        enviarMensagemXML(response, false, "Nenhuma câmera válida informada.");
	        return;
	    }

	    List<DispositivoSimples> retorno = Cameras.ObterListaDispositivosContagensMisto(idsCameras);
	    DispositivoSimplesResponse responseWrapper = new DispositivoSimplesResponse(retorno);
	    enviarRespostaXML(response, responseWrapper);
	}

	private void processarObterRecursoServidor(HttpServletResponse response) throws Exception {
	    RecursoServidorResponse recurso = Cameras.ObterRecursoServidor();
	    enviarRespostaXML(response, recurso);
	}

	private void enviarRespostaXML(HttpServletResponse response, Object objeto) throws JAXBException, IOException {
		try {
			if (objeto == null) {
				enviarMensagemXML(response, false, "Objeto nulo para serialização!");
				return;
			}

			JAXBContext context = JAXBContext.newInstance(objeto.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// seta sucesso se existir
			try {
				objeto.getClass().getMethod("setSucesso", boolean.class).invoke(objeto, true);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
				// ignora
			}

			StringWriter sw = new StringWriter();
			marshaller.marshal(objeto, sw);
			String xml = sw.toString();
			sw.close();

			// 🔹 Content-Type + charset
			response.setContentType("text/xml; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);

			// 🔹 Usa apenas getWriter()
			response.getWriter().write(xml);
			response.getWriter().flush();

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

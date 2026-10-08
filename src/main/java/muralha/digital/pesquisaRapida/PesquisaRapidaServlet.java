package muralha.digital.pesquisaRapida;

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

@WebServlet("/MuralhaDigital/PesquisaRapida")
public class PesquisaRapidaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PesquisaRapidaServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    final Acesso acesso = new Acesso(request, response, true);
	    if (!acesso.verificaAcesso()) {
	        new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
	        return;
	    }

	    try {
	        String valor = request.getParameter("valor");
	        String idTipoConsultaStr = request.getParameter("idTipoConsulta");

	        if (valor == null || idTipoConsultaStr == null) {
	            enviarMensagemXML(response, false, "Parâmetros inválidos.");
	            return;
	        }

	        int idTipoConsulta = Integer.parseInt(idTipoConsultaStr);
	        TipoConsulta tipoConsulta = TipoConsulta.fromCodigo(idTipoConsulta);

	        PesquisaRapidaRequest pRequest = new PesquisaRapidaRequest();
	        pRequest.setValor(valor);
	        pRequest.setTipoConsulta(tipoConsulta);

	        PesquisaRapidaResponse pResponse;

	        switch (tipoConsulta) {
	            case CPF:
	            case NOME:
	            case VEICULO:
	                pResponse = PesquisasRapidas.ObterPesquisaRapidaPorNomeCPFVei(pRequest.getTipoConsulta(),pRequest.getValor());
	                break;
	            default:
	                enviarMensagemXML(response, false, "Tipo de consulta não suportado.");
	                return;
	        }

	        enviarRespostaXML(response, pResponse);

	    } catch (Exception e) {
	        logger.error("Erro ao obter Pesquisa Rápida: " + e.getMessage(), e);
	        enviarMensagemXML(response, false, "Ocorreu um erro ao consultar informações da Pesquisa Rápida!");
	    }
	}



	private void enviarRespostaXML(HttpServletResponse response, PesquisaRapidaResponse tipos)
			throws JAXBException, IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(PesquisaRapidaResponse.class);
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

		} catch (Exception e) {
			logger.error("Erro ao enviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar informações da Pesquisa Rápida!";
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

package muralha.digital.pesquisaRapida;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

@WebServlet("/MuralhaDigital/PesquisaRapida/Autocomplete")
public class PesquisaRapidaAutocompleteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PesquisaRapidaAutocompleteServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String tipo = request.getParameter("tipo");
		String termo = request.getParameter("termo");

		if (tipo == null || termo == null || termo.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			AutocompleteResponse resp = new AutocompleteResponse(false, Collections.emptyList());
			enviarRespostaXML(response, resp);
			return;
		}

		try {
			List<String> suggestions;
			switch (tipo) {
			case "nome":
				suggestions = PesquisasRapidas.obterSugestoesPorNome(termo);
				break;
			default:
				suggestions = Collections.emptyList();
				break;
			}

			AutocompleteResponse resp = new AutocompleteResponse(true, suggestions);
			enviarRespostaXML(response, resp);

		} catch (Exception e) {
			logger.error("Erro no autocomplete para o termo: " + termo, e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			AutocompleteResponse resp = new AutocompleteResponse(false, Collections.emptyList());
			enviarRespostaXML(response, resp);
		}
	}

	private void enviarRespostaXML(HttpServletResponse response, AutocompleteResponse dados) {
		try {
			JAXBContext context = JAXBContext.newInstance(AutocompleteResponse.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(dados, sw);
			String xmlString = sw.toString();

			response.setContentType("application/xml; charset=UTF-8");
			response.getWriter().write(xmlString);

		} catch (Exception e) {
			logger.error("Erro crítico ao enviar resposta XML do autocomplete", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}

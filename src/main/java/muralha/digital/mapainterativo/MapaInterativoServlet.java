package muralha.digital.mapainterativo;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;
import com.google.gson.Gson;

@WebServlet("/MuralhaDigital/MapaInterativo")
public class MapaInterativoServlet extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MapaInterativoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	private static final Gson gson = new Gson();

	boolean temp;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Validando acesso do usuário
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			String msg = null;
			String strAcao = request.getParameter("acao");

			if (strAcao == null || strAcao == "") {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			if (strAcao.equals("obterAtendimentos"))
				listarAtendimentosPorTipoEvento(request, response);
			else if (strAcao.equals("obterGuarnicaoLocalizacao"))
				listarGuarnicaoLocalizacao(request, response);

		} catch (Exception err) {
			logger.error("Erro ao obter dados de dispositivos/equipamentos.", err);
			return;
		}

	}
	
	private void listarAtendimentosPorTipoEvento(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    String tipoEventoStr = request.getParameter("idTipoEvento");
	    
	    String tipoEvento = tipoEventoStr != null ? tipoEventoStr : "1";

	    try {
	        int idTipoEvento = Integer.parseInt(tipoEvento);

	        List<Atendimento> atendimentos = Atendimento.ObterAtendimentosPorTipoEvento(idTipoEvento);

	        String json = gson.toJson(atendimentos);
	        response.getWriter().write(json);

	    } catch (NumberFormatException e) {
	        logger.error("Formato inválido para 'idTipoEvento'", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Parâmetro 'idTipoEvento' inválido.")));
	    } catch (Exception e) {
	        logger.error("Erro ao listar atendimentos por tipo de evento", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao buscar atendimentos.")));
	    }
	}
	
	private void listarGuarnicaoLocalizacao(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    String idGuarnicaoStr = request.getParameter("idGuarnicao");

	    try {
	        Integer idGuarnicao = null;

	        if (idGuarnicaoStr != null && !idGuarnicaoStr.isEmpty()) {
	            idGuarnicao = Integer.parseInt(idGuarnicaoStr);
	        }
	        else {
	        	idGuarnicao = 0;
	        }

	        List<GuarnicaoLocalizacao> integrantes = GuarnicaoLocalizacao.ObterIntegrantesGuarnicao(idGuarnicao);

	        String json = gson.toJson(integrantes);
	        response.getWriter().write(json);

	    } catch (NumberFormatException e) {
	        logger.error("Formato inválido para 'idGuarnicao'", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Parâmetro 'idGuarnicao' inválido.")));
	    } catch (Exception e) {
	        logger.error("Erro ao listar integrantes da guarnição", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao buscar integrantes da guarnição.")));
	    }
	}



    private static class ApiResponse {
        boolean success;
        String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

}

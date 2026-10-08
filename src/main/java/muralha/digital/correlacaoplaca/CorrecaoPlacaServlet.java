package muralha.digital.correlacaoplaca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@WebServlet("/MuralhaDigital/CorrecaoPlaca")
public class CorrecaoPlacaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CorrecaoPlacaServlet.class);
	private static final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			 String msg = null;
	            String strAcao = request.getParameter("acao");


	            if(strAcao.equals("listarVeiculos")) {
	                listarVeiculos(request, response); 
	            
	            //else if(strAcao.equals("listarIntegrantes"))
	            	//listarIntegrantes(request, response);
	            }
			
			 else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
			}
		} catch (Exception e) {
			logger.error("Erro no doGet da GuarnicaoServlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String acao = request.getParameter("acao");

		try {
			if ("atualizarPlacas".equalsIgnoreCase(acao)) {
				atualizarPlacas(request, response);
			}
			
			 else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST inválida ou não informada.");
			}
		} catch (Exception e) {
			logger.error("Erro no doPost da GuarnicaoServlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
		}
	}

	private void listarVeiculos(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();

	    try {
	        String dataInicioParam = request.getParameter("dataInicio");
	        String dataFimParam    = request.getParameter("dataFim");
	        String limiteParam     = request.getParameter("limite");
	        String idLocalParam    = request.getParameter("idLocal");

	        Timestamp dataInicio = null;
	        Timestamp dataFim    = null;
	        Integer limite       = null;
	        Integer idLocal      = null;

	        try {
	            if (dataInicioParam != null && !dataInicioParam.isEmpty()) {
	                dataInicio = Timestamp.valueOf(dataInicioParam); 
	            }
	            if (dataFimParam != null && !dataFimParam.isEmpty()) {
	                dataFim = Timestamp.valueOf(dataFimParam);
	            }
	            if (limiteParam != null && !limiteParam.isEmpty()) {
	                limite = Integer.parseInt(limiteParam);
	            }
	            if (idLocalParam != null && !idLocalParam.isEmpty()) {
	                idLocal = Integer.parseInt(idLocalParam);
	            }
	        } catch (IllegalArgumentException e) {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            out.print("{\"erro\":\"Parâmetros inválidos.\"}");
	            return;
	        }

	        List<VeiculoRegistro> listaVeiculos = VeiculoRegistro.ObterVeiculos(
	            dataInicio, dataFim, limite, idLocal
	        );

	        Gson gson = new Gson();
	        String json = gson.toJson(listaVeiculos);

	        out.print(json);
	        out.flush();

	    } catch (ConexaoException | SQLException e) {
	        e.printStackTrace();
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("{\"erro\":\"Erro ao listar veículos.\"}");
	    } finally {
	        out.close();
	    }
	}

	private void atualizarPlacas(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {

	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();

	    try {
	        StringBuilder sb = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }

	        Gson gson = new Gson();
	        JsonObject jsonBody = gson.fromJson(sb.toString(), JsonObject.class);

	        List<Map<String, String>> loteCompleto = new ArrayList<>();

	        if (jsonBody.has("loteCompleto") && jsonBody.get("loteCompleto").isJsonArray()) {
	            JsonArray array = jsonBody.getAsJsonArray("loteCompleto");

	            for (JsonElement element : array) {
	                JsonObject obj = element.getAsJsonObject();
	                Map<String, String> map = new HashMap<>();

	                map.put("placaAtual", getSafeString(obj, "placaAtual"));
	                map.put("novaPlaca", getSafeString(obj, "novaPlaca"));
	                map.put("idVeiculo", getSafeString(obj, "idVeiculo"));
	                map.put("idUsuario", getSafeString(obj, "idUsuario"));

	                loteCompleto.add(map);
	            }
	        }

	        VeiculoRegistro.ProcessarCorrecaoPlacas(loteCompleto);

	        out.print("{\"status\":\"ok\",\"mensagem\":\"Processamento concluído com sucesso.\"}");
	        out.flush();

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("{\"status\":\"erro\",\"mensagem\":\"Erro ao atualizar placas.\"}");
	    } finally {
	        out.close();
	    }
	}

	private String getSafeString(JsonObject obj, String key) {
	    if (obj.has(key) && !obj.get(key).isJsonNull()) {
	        return obj.get(key).getAsString();
	    }
	    return ""; 
	}




}

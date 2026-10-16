package muralha.digital.correlacaoplaca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.consilux.infra.SessaoConstantes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.ocr.CorrecaoPlacaService;
import muralha.digital.ocr.JdbcRepositorioOcr;
import muralha.digital.ocr.LeituraPlaca;
import muralha.digital.ocr.ValidacaoCorrecao;
import muralha.digital.util.Resultado;

@WebServlet("/MuralhaDigital/CorrecaoPlaca")
public class CorrecaoPlacaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CorrecaoPlacaServlet.class);
	private static final Gson gson = new Gson();
	private static final int MAX_LOTE = 500;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Usuario usuario = (Usuario) request.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
		if (usuario == null) { response.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
		if (!new Acesso(request, response, true).verificaAcesso(false)) { return; }

		try {
			String acao = request.getParameter("acao");
			if ("listarVeiculos".equals(acao)) {
				listarVeiculos(request, response);
			} else if ("motivos".equals(acao)) {
				motivos(response);
			} else if ("placas".equals(acao)) {
				placas(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
			}
		} catch (Exception e) {
			logger.error("Erro no doGet de CorrecaoPlacaServlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Usuario usuario = (Usuario) request.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
		if (usuario == null) { response.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
		if (!new Acesso(request, response, true).verificaAcesso(false)) { return; }

		try {
			if ("atualizarPlacas".equalsIgnoreCase(request.getParameter("acao"))) {
				atualizarPlacas(usuario, request, response);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST inválida ou não informada.");
			}
		} catch (Exception e) {
			logger.error("Erro no doPost de CorrecaoPlacaServlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
		}
	}

	private void listarVeiculos(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    response.setContentType("application/json; charset=UTF-8");
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
	                limite = Math.min(Integer.parseInt(limiteParam), MAX_LOTE);
	            }
	            if (idLocalParam != null && !idLocalParam.isEmpty()) {
	                idLocal = Integer.parseInt(idLocalParam);
	            }
	        } catch (IllegalArgumentException e) {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            out.print("{\"erro\":\"Parâmetros inválidos.\"}");
	            return;
	        }

	        List<VeiculoRegistro> listaVeiculos = VeiculoRegistro.ObterVeiculos(dataInicio, dataFim, limite, idLocal);
	        out.print(gson.toJson(listaVeiculos));
	        out.flush();

	    } catch (ConexaoException | SQLException e) {
	        logger.error("Erro ao listar veículos para correção de placas", e);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("{\"erro\":\"Erro ao listar veículos.\"}");
	    } finally {
	        out.close();
	    }
	}

	/**
	 * Grava o lote: placas alteradas viram correção com o motivo e a justificativa informados
	 * (TR 5.7.5.2); placas mantidas viram conferência com o motivo de conferência cadastrado.
	 * O usuário é o da sessão (o id enviado pelo navegador é ignorado). Tudo ou nada.
	 */
	private void atualizarPlacas(Usuario usuario, HttpServletRequest request, HttpServletResponse response)
	        throws IOException {

	    response.setContentType("application/json; charset=UTF-8");
	    StringBuilder sb = new StringBuilder();
	    try (BufferedReader reader = request.getReader()) {
	        String line;
	        while ((line = reader.readLine()) != null) sb.append(line);
	    }

	    JsonObject corpo;
	    try {
	        corpo = gson.fromJson(sb.toString(), JsonObject.class);
	    } catch (RuntimeException e) {
	        response.getWriter().print(gson.toJson(new Resultado(false, "Requisição inválida.")));
	        return;
	    }
	    if (corpo == null || !corpo.has("loteCompleto") || !corpo.get("loteCompleto").isJsonArray()) {
	        response.getWriter().print(gson.toJson(new Resultado(false, "Lote vazio.")));
	        return;
	    }
	    JsonArray lote = corpo.getAsJsonArray("loteCompleto");
	    if (lote.size() > MAX_LOTE) {
	        response.getWriter().print(gson.toJson(new Resultado(false, "Lote acima de " + MAX_LOTE + " veículos.")));
	        return;
	    }
	    Integer idMotivo = null;
	    if (corpo.has("idMotivo") && !corpo.get("idMotivo").isJsonNull()) {
	        try { idMotivo = corpo.get("idMotivo").getAsInt(); } catch (RuntimeException ignorada) { /* inválido: validado abaixo */ }
	    }
	    String justificativa = texto(corpo, "justificativa");

	    int corrigidas = 0, conferidas = 0;
	    StringBuilder auditoria = new StringBuilder();
	    try (Connection conn = Conexao.getConexao()) {
	        List<CorrecaoPlacaService.Motivo> motivos = CorrecaoPlacaService.motivos(conn);
	        Integer idConfirmacao = null;
	        for (CorrecaoPlacaService.Motivo m : motivos) if (m.confirmacao) idConfirmacao = m.id;

	        conn.setAutoCommit(false);
	        try {
	            for (JsonElement el : lote) {
	                JsonObject obj = el.getAsJsonObject();
	                String idVeiculo = texto(obj, "idVeiculo");
	                String placaAtual = texto(obj, "placaAtual");
	                String novaPlaca = texto(obj, "novaPlaca");
	                if (idVeiculo == null || novaPlaca == null) continue;
	                boolean mudou = !novaPlaca.trim().equalsIgnoreCase(placaAtual == null ? "" : placaAtual.trim());
	                if (!mudou && idConfirmacao == null) continue;
	                CorrecaoPlacaService.Resultado r = CorrecaoPlacaService.corrigir(conn,
	                    new CorrecaoPlacaService.Pedido(idVeiculo, novaPlaca, null, null,
	                        mudou ? idMotivo : idConfirmacao, mudou ? justificativa : null,
	                        CorrecaoPlacaService.OrigemIntervencao.LOTE),
	                    usuario.getId(), request.getRemoteAddr());
	                if (r.confirmacao) conferidas++; else corrigidas++;
	                if (!r.confirmacao) auditoria.append(idVeiculo).append(": ").append(r.descricao).append(' ');
	            }
	            conn.commit();
	        } catch (IllegalArgumentException | SQLException e) {
	            conn.rollback();
	            throw e;
	        } finally {
	            conn.setAutoCommit(true);
	        }
	    } catch (IllegalArgumentException e) {
	        response.getWriter().print(gson.toJson(new Resultado(false, e.getMessage())));
	        return;
	    } catch (Exception e) {
	        logger.error("Erro ao gravar o lote de correção de placas", e);
	        response.getWriter().print(gson.toJson(new Resultado(false, "Erro ao gravar o lote.")));
	        return;
	    }
	    String desc = corrigidas + " placa(s) corrigida(s) e " + conferidas + " conferida(s). " + auditoria;
	    AuditoriaService.registrar(request, "Correcao de placas por lote", "gravar-lote", null,
	        desc.length() > 500 ? desc.substring(0, 500) : desc);
	    response.getWriter().print(gson.toJson(new Resultado(true, corrigidas + " placa(s) corrigida(s) e " + conferidas + " conferida(s).")));
	}

	/** Motivos de correção ativos, para o pedido de justificativa (TR 5.7.5.2). */
	private void motivos(HttpServletResponse response) throws Exception {
		JsonArray a = new JsonArray();
		try (Connection conn = Conexao.getConexao()) {
			for (CorrecaoPlacaService.Motivo m : CorrecaoPlacaService.motivos(conn)) {
				JsonObject o = new JsonObject();
				o.addProperty("id", m.id);
				o.addProperty("descricao", m.descricao);
				o.addProperty("exige_texto", m.exigeTexto);
				o.addProperty("confirmacao", m.confirmacao);
				a.add(o);
			}
		}
		JsonObject r = new JsonObject();
		r.add("motivos", a);
		r.addProperty("justificativa_min", ValidacaoCorrecao.JUSTIFICATIVA_MIN);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(gson.toJson(r));
	}

	/** Todas as placas lidas na passagem (TR 5.7.1.4), com score e origem. */
	private void placas(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id;
		try {
			id = java.util.UUID.fromString(request.getParameter("idVeiculo").trim()).toString();
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Passagem inválida.");
			return;
		}
		JsonArray a = new JsonArray();
		try (Connection conn = Conexao.getConexao()) {
			if (CorrecaoPlacaService.estruturaInstalada(conn)) {
				for (LeituraPlaca l : JdbcRepositorioOcr.leituras(conn, id)) {
					JsonObject o = new JsonObject();
					o.addProperty("ordem", l.getOrdem());
					o.addProperty("tipo", l.getTipo().name());
					o.addProperty("placa", l.getPlaca());
					o.addProperty("score", l.getScore());
					o.addProperty("origem", l.getOrigem().name());
					a.add(o);
				}
			}
		}
		JsonObject r = new JsonObject();
		r.add("leituras", a);
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().print(gson.toJson(r));
	}

	private static String texto(JsonObject obj, String chave) {
	    if (obj.has(chave) && !obj.get(chave).isJsonNull()) {
	        String v = obj.get(chave).getAsString();
	        return v.trim().isEmpty() ? null : v;
	    }
	    return null;
	}
}

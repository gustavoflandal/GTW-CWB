package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.Usuario;

/**
 * Relatório de alterações de configuração de equipamento (menu 201).
 *
 * Lista as versões do local (dbo.local, uma linha por sequencia_local) e da
 * configuração vinculada (dbo.configuracao_equipamento) atualizadas no período.
 * O banco não guarda o valor anterior de cada parâmetro; o histórico completo
 * é o escopo do TR 5.4.7.6 (Fase 4 da correção da auditoria de 2026-10-16).
 */
public class RelatorioAlteracaoConfigEquip extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioAlteracaoConfigEquip.class);

	static final String JSP_RESULTADO = "/relatorio/res_alteracoes_configequip.jsp";
	static final int MAX_DIAS = 366;
	static final int MAX_LINHAS = 5000;

	private static final Pattern ID_LOCAL = Pattern.compile("[0-9]{1,8}");

	static final String SQL =
		"SELECT TOP " + MAX_LINHAS + " l.id_local, l.sequencia_local, l.nome, l.data_atualizacao, " +
		"       e.id_configuracao_equipamento, e.serie_equipamento, e.ativo, e.em_operacao, " +
		"       e.data_inicio, e.data_fim, e.data_modificacao, u.usuario AS usuario_modificacao " +
		"  FROM dbo.local l " +
		"  JOIN dbo.configuracao_equipamento e ON e.id_configuracao_equipamento = l.id_configuracao_equipamento " +
		"  LEFT JOIN dbo.sis_usuario u ON u.id_usuario = e.id_usuario " +
		" WHERE e.id_grupo_equipamento = ? " +
		"   AND (? = 0 OR l.id_local = ?) " +
		"   AND ((l.data_atualizacao >= ? AND l.data_atualizacao < ?) " +
		"     OR (e.data_modificacao >= ? AND e.data_modificacao < ?)) " +
		" ORDER BY l.id_local, l.sequencia_local";

	/**
	 * Converte o período da tela (dd/MM/yyyy, data final inclusiva) em
	 * {início, fim exclusivo}. Devolve null se inválido, invertido ou maior que MAX_DIAS.
	 */
	static Date[] periodo(String dataIni, String dataFim) {
		if (dataIni == null || dataFim == null) return null;
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		fmt.setLenient(false);
		try {
			Date ini = fmt.parse(dataIni.trim());
			Date fim = fmt.parse(dataFim.trim());
			if (fim.before(ini)) return null;
			Calendar c = Calendar.getInstance();
			c.setTime(fim);
			c.add(Calendar.DAY_OF_MONTH, 1);
			Date fimExclusivo = c.getTime();
			long dias = (fimExclusivo.getTime() - ini.getTime()) / 86_400_000L;
			if (dias > MAX_DIAS) return null;
			return new Date[] { ini, fimExclusivo };
		} catch (ParseException e) {
			return null;
		}
	}

	/** id_local da tela: vazio ou "0" = todos; devolve null se inválido. */
	static Integer idLocal(String valor) {
		if (valor == null || valor.trim().isEmpty()) return 0;
		String v = valor.trim();
		return ID_LOCAL.matcher(v).matches() ? Integer.valueOf(v) : null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Usuario usuario = (Usuario) request.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
		if (usuario == null) { response.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
		if (!new Acesso(request, response).verificaAcesso(false)) { return; }

		Integer idLocal = idLocal(request.getParameter("id_local"));
		if (idLocal == null) {
			new Mensagem(response).showErro("Identificador de local enviado inválido!");
			return;
		}
		Date[] periodo = periodo(request.getParameter("dataini"), request.getParameter("datafim"));
		if (periodo == null) {
			new Mensagem(response).showErro("Período inválido: informe datas dd/mm/aaaa, a inicial antes da final, em até " + MAX_DIAS + " dias.");
			return;
		}

		List<Map<String, Object>> linhas = new ArrayList<>();
		try (Connection conn = Conexao.getConexao();
			 PreparedStatement ps = conn.prepareStatement(SQL)) {
			Integer grupo = usuario.getIdGrupoEquipamentoConfig();
			ps.setInt(1, grupo != null ? grupo : 0);
			ps.setInt(2, idLocal);
			ps.setInt(3, idLocal);
			Timestamp ini = new Timestamp(periodo[0].getTime());
			Timestamp fim = new Timestamp(periodo[1].getTime());
			ps.setTimestamp(4, ini);
			ps.setTimestamp(5, fim);
			ps.setTimestamp(6, ini);
			ps.setTimestamp(7, fim);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> l = new LinkedHashMap<>();
					l.put("id_local", rs.getInt("id_local"));
					l.put("sequencia_local", rs.getInt("sequencia_local"));
					l.put("nome", rs.getString("nome") != null ? rs.getString("nome").trim() : "");
					l.put("data_atualizacao", rs.getTimestamp("data_atualizacao"));
					l.put("id_configuracao_equipamento", rs.getInt("id_configuracao_equipamento"));
					l.put("serie_equipamento", rs.getObject("serie_equipamento"));
					l.put("ativo", rs.getBoolean("ativo"));
					l.put("em_operacao", rs.getBoolean("em_operacao"));
					l.put("data_inicio", rs.getTimestamp("data_inicio"));
					l.put("data_fim", rs.getTimestamp("data_fim"));
					l.put("data_modificacao", rs.getTimestamp("data_modificacao"));
					l.put("usuario_modificacao", rs.getString("usuario_modificacao"));
					linhas.add(l);
				}
			}
		} catch (Exception e) {
			logger.error("Erro no RelatorioAlteracaoConfigEquip", e);
			new Mensagem(response).showErro("Erro ao gerar o relatório.");
			return;
		}

		request.setAttribute("linhas", linhas);
		request.setAttribute("limite_atingido", linhas.size() >= MAX_LINHAS);
		request.setAttribute("periodo_ini", request.getParameter("dataini"));
		request.setAttribute("periodo_fim", request.getParameter("datafim"));
		request.getRequestDispatcher(JSP_RESULTADO).forward(request, response);
	}
}

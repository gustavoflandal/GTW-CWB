package com.consilux.servlet.relatorio;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.model.Mensagem;

/**
 * Relatório de alterações de configuração de equipamento (menu 201).
 * Desativado em 2026-10-16: o menu não tinha nenhum grupo com direito e o histórico de
 * parâmetros (valor anterior e novo) passou a ser registrado em muralha.parametro_historico
 * e consultado na tela de logs técnicos da Muralha Digital (Plano 26).
 */
public class RelatorioAlteracaoConfigEquip extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new Mensagem(response).showErro(
			"Relatório desativado. Consulte o histórico de parâmetros na tela Logs Técnicos da Muralha Digital.",
			"/login/gtw_principal.jsp");
	}
}

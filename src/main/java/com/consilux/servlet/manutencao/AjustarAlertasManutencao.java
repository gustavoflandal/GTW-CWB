package com.consilux.servlet.manutencao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.exception.ModelException;
import com.consilux.model.manutencao.AlertaManutencao;

/**
 * Servlet implementation class AdicionarAlertaManutencao
 */
public class AjustarAlertasManutencao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AjustarAlertasManutencao.class);  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AjustarAlertasManutencao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("incomplete-switch")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}
		String sAcao = request.getParameter("acao");
		String sInformacaoAdicional = request.getParameter("info_adic");
		
		if (sAcao == null || !Pattern.matches("^[1]$", sAcao)) {
			throw new ServletException("Identificador da ação enviado invalido!");
		}
		else if (sInformacaoAdicional == null || !ExpValida.TEXTO.validar(sInformacaoAdicional, 100)) {
			throw new ServletException("Informação adicional enviada invalida!");
		}
		
		AlertaManutencao.Acao acao = AlertaManutencao.Acao.values()[Integer.parseInt(sAcao)];

		try {
			switch (acao) {
				case DESATIVAR_INEXISTENTES:
					desativarInexistentes(sInformacaoAdicional);
				break;
			}
		}
		catch (Exception e) {
			logger.error("Não foi possível ajustar os alertas.",e);
			throw new ServletException("Não foi possível ajustar os alertas!");
		}
	}

	private void desativarInexistentes(String sInformacaoAdicional) throws ServletException, ConexaoException, SQLException, ModelException {
		Boolean bOk;
		
		bOk = AlertaManutencao.desativarAlertasInexistentes(sInformacaoAdicional);
		if (!bOk) {
			throw new ModelException("Não foi encontrado nenhum alerta.");
		}
	}

}

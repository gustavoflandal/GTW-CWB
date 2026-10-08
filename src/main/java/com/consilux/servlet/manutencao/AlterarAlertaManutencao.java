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
public class AlterarAlertaManutencao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AlterarAlertaManutencao.class);  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlterarAlertaManutencao() {
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
		String sSerieEquipamento = request.getParameter("serie_equipamento");
		String sPista = request.getParameter("pista");
		String sAcao = request.getParameter("acao");
		
		if (sSerieEquipamento == null || !ExpValida.NATURAL.validar(sSerieEquipamento)) {
			new Mensagem(response).showErro("Identificador do equipamento enviado invalido!");
			return;
		}
		else if (sPista == null || !ExpValida.NATURAL_COM_ZERO.validar(sPista)) {
			new Mensagem(response).showErro("Pista enviada invalida!");
			return;
		}
		else if (sAcao == null || !Pattern.matches("^[0]$", sAcao)) {
			throw new ServletException("Identificador da ação enviado invalido!");
		}
		
		AlertaManutencao.Acao acao = AlertaManutencao.Acao.values()[Integer.parseInt(sAcao)];

		try {
			switch (acao) {
				case DESATIVAR:
					Integer serieEquipamento = Integer.valueOf(sSerieEquipamento);
					Integer pista = Integer.valueOf(sPista);
					desativarAlerta(serieEquipamento, pista);
				break;
			}
		}
		catch (Exception e) {
			new Mensagem(response).showErro("Não foi possível alterar o alerta: "+e.getMessage());
			logger.error("Não foi possível alterar o alerta.",e);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		String sSerieEquipamento = request.getParameter("serie_equipamento");
		String sPista = request.getParameter("pista");
		String sInformacaoAdicional = request.getParameter("info_adic");
		String sCausaFalsoPositivo = request.getParameter("causa_falso_positivo");
		String sCausaExternaEnergia = request.getParameter("causa_ext_energia");
		String sCausaExternaPavimento = request.getParameter("causa_ext_pavimento");
		String sCausaExternaVandalismo = request.getParameter("causa_ext_vandalismo");
		
		if (sSerieEquipamento == null || !ExpValida.NATURAL.validar(sSerieEquipamento)) {
			new Mensagem(response).showErro("Identificador do equipamento enviado invalido!");
			return;
		}
		else if (sPista == null || !ExpValida.NATURAL_COM_ZERO.validar(sPista)) {
			new Mensagem(response).showErro("Pista enviada invalida!");
			return;
		}
		else if (sInformacaoAdicional == null || !ExpValida.TEXTO.validar(sInformacaoAdicional, 100)) {
			new Mensagem(response).showErro("Informação adicional enviada invalida!");
			return;
		}

		Integer serieEquipamento;
		Integer pista;
		Boolean causaExternaEnergia;
		Boolean causaExternaPavimento;
		Boolean causaExternaVandalismo;
		Boolean causaFalsoPositivo;
		
		serieEquipamento = Integer.valueOf(sSerieEquipamento);
		pista = Integer.valueOf(sPista);
		causaExternaEnergia = sCausaExternaEnergia != null && sCausaExternaEnergia.equals("1");
		causaExternaPavimento = sCausaExternaPavimento != null && sCausaExternaPavimento.equals("1");
		causaExternaVandalismo = sCausaExternaVandalismo != null && sCausaExternaVandalismo.equals("1");
		causaFalsoPositivo = sCausaFalsoPositivo != null && sCausaFalsoPositivo.equals("1");
		
		Boolean bOk;
		
		try {
			bOk = AlertaManutencao.alterarAlerta(serieEquipamento, pista, sInformacaoAdicional, causaExternaEnergia, causaExternaPavimento, causaExternaVandalismo, causaFalsoPositivo);
			if (!bOk) {
				throw new ModelException("Não foi encontrado nenhum alerta para a faixa enviada.");
			}
		} 
		catch (Exception e) {
			new Mensagem(response).showErro("Não foi possível alterar o alerta: "+e.getMessage());
			logger.error("Não foi possível alterar o alerta: "+e.getMessage());
			return;
		}
		
		new Mensagem(response).showSucesso("Alerta alterado com sucesso!","/manutencao/alertas_manutencao.jsp");
	}
	
	private void desativarAlerta(Integer serieEquipamento, Integer pista) throws ServletException, ConexaoException, SQLException, ModelException {
		Boolean bOk;
		
		bOk = AlertaManutencao.desativarAlerta(serieEquipamento, pista);
		if (!bOk) {
			throw new ModelException("Não foi encontrado nenhum alerta para a faixa enviada.");
		}
	}

}

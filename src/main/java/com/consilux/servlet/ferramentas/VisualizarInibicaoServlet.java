package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.ClasseVeiculo;
import com.consilux.model.Enquadramento;
import com.consilux.model.Inibicao;
import com.consilux.model.LocalVigente;
import com.consilux.model.Mensagem;
import com.consilux.model.beans.InibicaoBean;

/**
 * Servlet implementation class VisualizarInibicaoServlet
 */
public class VisualizarInibicaoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(VisualizarInibicaoServlet.class);
	private DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
	private DateFormat formatoHora = new SimpleDateFormat("HH:mm");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VisualizarInibicaoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		String sIdInibicaoInfracao = request.getParameter("id_inibicao_infracao");
		
		if (sIdInibicaoInfracao == null || !ExpValida.NATURAL.validar(sIdInibicaoInfracao)) {
			new Mensagem(response).showErro("Identificador de inibição enviado inválido!");
			return;
		}
		
		Inibicao inibicao;
		try {
			inibicao = Inibicao.buscaInibicaoPorId(Integer.valueOf(sIdInibicaoInfracao));
		}
		catch (Exception e) {
			new Mensagem(response).showErro("Não foi possível encontrar a inibição no BD: "+e.getMessage());
			logger.error("Não foi possível encontrar a inibição no BD.", e);
			return;
		}
		
		InibicaoBean bean = inibicao.getBean();
		
		String sClasseVeiculo = null;
		String sLocal = null;
		Enquadramento enquadramento = null;
		
		try {
			ClasseVeiculo cv;
			LocalVigente lv;
			if (bean.getIdClasse() != null) {
				cv = ClasseVeiculo.buscaClasseVeiculo(bean.getIdClasse());
				sClasseVeiculo = cv.getDescricao();
			}
			if (bean.getSerieEquipamento() != null) { 
				lv = LocalVigente.buscaLocalVigentePorSerieEquipamento(bean.getSerieEquipamento());
				sLocal = lv.getNome();
			}
			enquadramento = Enquadramento.buscaEnquadramentosPorId(bean.getIdEnquadramento());
		}
		catch (Exception e) {
			new Mensagem(response).showErro("Não foi possível buscar os dados no BD: "+e.getMessage());
			logger.error("Não foi possível buscar os dados no BD.", e);
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/inibicao/ver_inibicao.jsp");
	    request.setAttribute("nome_inibicao", bean.getDescricao());
	    request.setAttribute("cod_classe", bean.getIdClasse());
	    request.setAttribute("classe_veiculo", sClasseVeiculo);
	    request.setAttribute("serie_equipamento", bean.getSerieEquipamento());
	    request.setAttribute("sLocal", sLocal);
	    request.setAttribute("pista", bean.getPista());
	    request.setAttribute("data_ini", formatoData.format(bean.getDataInicio()));
	    request.setAttribute("hora_ini", formatoHora.format(bean.getHorarioInicio()));
	    request.setAttribute("data_fim", formatoData.format(bean.getDataFim()));
	    request.setAttribute("hora_fim", formatoHora.format(bean.getHorarioFim()));
	    request.setAttribute("enquadramento", enquadramento);
		rd.forward(request, response);
		
	}

}

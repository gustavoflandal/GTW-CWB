package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.ClasseVeiculo;
import com.consilux.model.Enquadramento;
import com.consilux.model.Inibicao;
import com.consilux.model.LocalVigente;
import com.consilux.model.Mensagem;
import com.consilux.model.beans.InibicaoBean;
import com.consilux.model.exception.ModelException;

/**
 * Servlet implementation class CadastrarInibicaoServlet
 */
public class CadastrarInibicaoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CadastrarInibicaoServlet.class);
	private DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
	private DateFormat formatoHora = new SimpleDateFormat("HH:mm");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CadastrarInibicaoServlet() {
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

	    List<Enquadramento> enquadramentos = null;
		List<ClasseVeiculo> classeVeiculo = null;
	    List<LocalVigente> equipamentos = null;
		try {
			enquadramentos = Enquadramento.buscaTodosEnquadramentos();
			classeVeiculo = ClasseVeiculo.buscaTodasClassesVeiculo();
			
		    Map<String,Object> mFiltro = new HashMap<String,Object>();
		    equipamentos = LocalVigente.buscaLocalVigentePor(mFiltro,2);
		    
		} 
		catch (Exception e) {
			logger.error("Erro ao buscar os dados básicos para o cadastro.", e);
			new Mensagem(response).showErro("Erro ao buscar os cadados básico para o cadastro: "+e.getMessage());
			return;
		}
		
	    
	    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/inibicao/cad_inibicao.jsp");
		request.setAttribute("enquadramentos", enquadramentos);
		request.setAttribute("classeVeiculo", classeVeiculo);
		request.setAttribute("equipamentos", equipamentos);
		rd.forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!
		
		String sNomeInibicao = request.getParameter("nome_inibicao");
		String sCodClasse = request.getParameter("classe_veiculo");
		String sSerieEquipamento = request.getParameter("serie_equipamento");
		sSerieEquipamento = sSerieEquipamento != null && sSerieEquipamento.length() > 0 ? sSerieEquipamento : "0";
		String sPista = request.getParameter("pista");
		sPista = sPista != null && sPista.length() > 0 ? sPista : "0";
		String sDataImagensIni = request.getParameter("data_ini");
		String sHoraImagensIni = request.getParameter("hora_ini");
		String sDataImagensFim = request.getParameter("data_fim");
		String sHoraImagensFim = request.getParameter("hora_fim");
		String[] aEnquadramentosSelecionados = request.getParameterValues("enquadramentos_selecionados");
		String sConfirmado = request.getParameter("confirmado");

		if (sNomeInibicao == null || !ExpValida.TEXTO.validar(sNomeInibicao,200)) {
			new Mensagem(response).showErro("Nome da inibição enviado inválido!");
			return;
		}
		if (sCodClasse != null && !ExpValida.TEXTO.validar(sCodClasse,1)) {
			new Mensagem(response).showErro("Classe de veículo enviada inválida!");
			return;
		}
		if (sSerieEquipamento != null && !ExpValida.NATURAL_COM_ZERO.validar(sSerieEquipamento)) {
			new Mensagem(response).showErro("Código de série do equipamento enviado inválido!");
			return;
		}
		if (sPista != null && !ExpValida.NATURAL_COM_ZERO.validar(sPista,1)) {
			new Mensagem(response).showErro("Código da pista enviada inválido!");
			return;
		}
		if (sDataImagensIni == null || !ExpValida.DATA.validar(sDataImagensIni)) {
			new Mensagem(response).showErro("Data inicial das imagens enviada inválida!");
			return;
		}
		if (sHoraImagensIni == null || !ExpValida.HORA_MINUTO.validar(sHoraImagensIni)) {
			new Mensagem(response).showErro("Hora inicial das imagens enviada inválida!");
			return;
		}
		if (sDataImagensFim == null || !ExpValida.DATA.validar(sDataImagensFim)) {
			new Mensagem(response).showErro("Data final das imagens enviada inválida!");
			return;
		}
		if (sHoraImagensFim == null || !ExpValida.HORA_MINUTO.validar(sHoraImagensFim)) {
			new Mensagem(response).showErro("Hora final das imagens enviada inválida!");
			return;
		}
		if (aEnquadramentosSelecionados == null || aEnquadramentosSelecionados.length == 0) {
			new Mensagem(response).showErro("Enquadramentos enviados inválido!");
			return;
		}
		
		Integer serieEquipamento = Integer.valueOf(sSerieEquipamento) > 0 ? Integer.valueOf(sSerieEquipamento) : null;
		String codClasse = sCodClasse.length() > 0 ? sCodClasse : null;
		Integer pista = Integer.valueOf(sPista) > 0 ? Integer.valueOf(sPista) : null;
		
		Date dataInicio = null;
		Date dataFim = null;
		Date horarioInicio = null;
		Date horarioFim = null;
		
		try {
			dataInicio = formatoData.parse(sDataImagensIni);
			dataFim = formatoData.parse(sDataImagensFim);		
			horarioInicio = formatoHora.parse(sHoraImagensIni);		
			horarioFim = formatoHora.parse(sHoraImagensFim);		
		}
		catch (Exception e) {
			new Mensagem(response).showErro("Erro ao realizar parser das datas/horas: "+e.getMessage());
			logger.error("Erro ao realizar parser das datas/horas.", e);
			return;
		}
		
		if (dataFim.after(DateUtils.addDays(dataInicio, 15))) {
			new Mensagem(response).showErro("Periodo entre as datas não pode ser maior que 15 dias.");
			logger.error("Periodo entre as datas não pode ser maior que 15 dias.");
			return;
		}
				
		List<Enquadramento> enquadramentosSelecionados = new ArrayList<Enquadramento>();
		
		for (String sIdEnquadramento : aEnquadramentosSelecionados) {
			Integer idEnquadramento = Integer.valueOf(sIdEnquadramento);
			Enquadramento enq;
			try {
				enq = Enquadramento.buscaEnquadramentosPorId(idEnquadramento);
				enquadramentosSelecionados.add(enq);
			}
			catch (Exception e) {
				new Mensagem(response).showErro("Erro ao buscar os enquadramentos no BD: "+e.getMessage());
				logger.error("Erro ao buscar os enquadramentos no BD.", e);
				return;
			}
		}

		InibicaoBean bean = new InibicaoBean();
		bean.setDescricao(sNomeInibicao);
		bean.setPista(pista);
		bean.setSerieEquipamento(serieEquipamento);
		bean.setIdClasse(codClasse);
		bean.setDataInicio(dataInicio);
		bean.setDataFim(dataFim);
		bean.setHorarioInicio(horarioInicio);
		bean.setHorarioFim(horarioFim);
		bean.setIdUsuario(acesso.getUsuario().getId());
		bean.setDataCriacao(new Date());
		
		if (sConfirmado == null) {
			try {
				visualizar(request, response, bean, enquadramentosSelecionados);
			}
			catch (Exception e) {
				new Mensagem(response).showErro("Erro ao visualizar os dados: "+e.getMessage());
				logger.error("Erro ao visualizar os dados.", e);
			}
		}
		else
			try {
				cadastrar(request, response, bean, enquadramentosSelecionados);
				new Mensagem(response).showSucesso("Inibição cadastrada com sucesso!");
			}
			catch (Exception e) {
				new Mensagem(response).showErro("Erro ao cadastrar os dados: "+e.getMessage());
				logger.error("Erro ao cadastrar os dados.", e);
			}
	}
	
	private void visualizar(HttpServletRequest request, HttpServletResponse response, InibicaoBean bean, List<Enquadramento> enquadramentosSelecionados) throws ServletException, IOException, ConexaoException, SQLException {

		String sClasseVeiculo = null;
		String sLocal = null;
		
		if (bean.getIdClasse() != null) {
			ClasseVeiculo cv = ClasseVeiculo.buscaClasseVeiculo(bean.getIdClasse());
			sClasseVeiculo = cv.getDescricao();
		}
		if (bean.getSerieEquipamento() != null) {
			LocalVigente lv = LocalVigente.buscaLocalVigentePorSerieEquipamento(bean.getSerieEquipamento());
			sLocal = lv.getNome();
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
	    request.setAttribute("enquadramentosSelecionados", enquadramentosSelecionados);
		rd.forward(request, response);	
	}
	
	private void cadastrar(HttpServletRequest request, HttpServletResponse response, InibicaoBean bean, List<Enquadramento> enquadramentosSelecionados) throws ServletException, IOException, ConexaoException, SQLException, ModelException {
		for (Enquadramento enquadramento : enquadramentosSelecionados) {
			bean.setIdEnquadramento(enquadramento.getIdEnquadramento());
			Inibicao.incluirInibicao(bean);
		}
	}

}

package com.consilux.ui.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Local;
import com.consilux.model.Usuario;
import com.consilux.model.relatorio.AnaliseAutuacoes;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.AnaliseAutuacoesService;
import com.consilux.ui.client.beans.AnaliseAutuacoesGwtBean;
import com.consilux.ui.client.beans.GwtBean;

public class AnaliseAutuacoesServiceImpl extends
		GwtBaseServlet implements AnaliseAutuacoesService {
	
	private static final long serialVersionUID = 855720708704140034L;

	private static final String JASPER_FILE = "/WEB-INF/relatorio/AnaliseAutuacoes.jasper";
	
	@Override
	public List<AnaliseAutuacoesGwtBean> getStats(GwtBean params) throws Exception {
		verificarSessaoLogada();
		List<AnaliseAutuacoesGwtBean> lRet = new ArrayList<AnaliseAutuacoesGwtBean>(0);
		
		try {
			lRet = AnaliseAutuacoes.geraAnaliseAutuacoes(params.getProperties());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lRet;
	}

	@Override
	public List<GwtBean> getAuditores() throws Exception {
		verificarSessaoLogada();
		Configuracao conf = ConfiguracaoProvider.getInstance();
		int idGrupoAuditores = conf.getIdGrupoAuditores();
		List<GwtBean> l = new ArrayList<GwtBean>();
		try {
			for (Usuario usuario : Usuario.buscaUsuarioPorGrupo(idGrupoAuditores)) {
				l.add(new GwtBean(usuario.getId(),usuario.getNome()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public List<GwtBean> getDigitadores() throws Exception {
		verificarSessaoLogada();
		Configuracao conf = ConfiguracaoProvider.getInstance();
		int idGrupoDigitadores = conf.getIdGrupoDigitadores();
		List<GwtBean> l = new ArrayList<GwtBean>();
		try {
			for (Usuario usuario : Usuario.buscaUsuarioPorGrupo(idGrupoDigitadores)) {
				l.add(new GwtBean(usuario.getId(),usuario.getNome()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public List<GwtBean> getLocais() throws Exception {
		verificarSessaoLogada();
		List<GwtBean> l = new ArrayList<GwtBean>();
		try {
			for (Local local : Local.listarLocaisVigentes()) {
				l.add(new GwtBean(local.getIdLocal(), local.getNome()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		String sDigitadorNome = req.getParameter("digitador_id"),
			   sDigitadorId = req.getParameter("digitador_id-hidden"),
			   sAuditorNome = req.getParameter("auditor_id"),
			   sAuditorId = req.getParameter("auditor_id-hidden"),
			   sDataInicial = req.getParameter("data_inicial"),
			   sDataFinal = req.getParameter("data_final"),
			   sLocalNome = req.getParameter("local_id"),
			   sLocalId = req.getParameter("local_id-hidden"),
			   sPista = req.getParameter("pista");
		
		Integer idLocal = sLocalId == null || "".equals(sLocalId) ? 0 : Integer.parseInt(sLocalId);
		Integer pista = sPista == null || "".equals(sPista) ? 0 : Integer.parseInt(sPista);
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		
		Timestamp data_inicial = null, 
			 data_final = null;

		int idGrupoDigitadores = 0;
		
		try {
			if (sDataInicial != null) {
				data_inicial = new Timestamp(fmt.parse(sDataInicial).getTime());
			}
			if (sDataFinal != null) {
				data_final = new Timestamp(fmt.parse(sDataFinal).getTime()+86399999);
			}
			Configuracao conf = ConfiguracaoProvider.getInstance();
			idGrupoDigitadores = conf.getIdGrupoDigitadores();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(JASPER_FILE));
		
		Integer idDigitador = 0;
		if (sDigitadorNome != null && !"".equals(sDigitadorNome)) {
			idDigitador = Integer.parseInt(sDigitadorId);
		}
		
		Integer idAuditor = 0;
		if (sDigitadorNome != null && !"".equals(sAuditorNome)) {
			idAuditor = Integer.parseInt(sAuditorId);
		}
		
		if (sLocalNome != null && !"".equals(sLocalNome)) {
			sLocalNome = "Todos";
		}
		
		relatorio.adicParametro("DIGITADOR", idDigitador == 0 ? "Todos" : sDigitadorNome);
		relatorio.adicParametro("ID_DIGITADOR", idDigitador);
		relatorio.adicParametro("AUDITOR", idAuditor == 0 ? "Todos" : sAuditorNome);
		relatorio.adicParametro("ID_AUDITOR", idAuditor);
		relatorio.adicParametro("DATA_INICIAL", data_inicial);
		relatorio.adicParametro("DATA_FINAL", data_final);
		relatorio.adicParametro("ID_LOCAL", idLocal);
		relatorio.adicParametro("LOCAL", sLocalNome);
		relatorio.adicParametro("ID_GRUPO_DIGITADOR", idGrupoDigitadores);
		relatorio.adicParametro("PISTA", pista);
		
		try {
			relatorio.preencheRelatorio();
			resp.setContentType("application/pdf");
			resp.addHeader("Content-Disposition","inline; filename=\"AnaliseAutuacoes.pdf\"");
			OutputStream out = resp.getOutputStream();
		    relatorio.exportReportToPdfStream(out);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}

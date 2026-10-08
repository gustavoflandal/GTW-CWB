package com.consilux.ui.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Evento;
import com.consilux.model.exception.ModelException;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.ui.client.EventosPesquisaService;
import com.consilux.ui.client.beans.CategoriaGwtBean;
import com.consilux.ui.client.beans.EventoGwtBean;
import com.consilux.ui.client.beans.EventoPesquisaParamGwtBean;
import com.consilux.ui.client.beans.EventoPesquisaResultadoContainerGwtBean;
import com.consilux.ui.client.beans.GwtBean;
import com.consilux.ui.client.beans.NivelGwtBean;
import com.consilux.ui.client.beans.PrioridadeGwtBean;
import com.consilux.ui.client.beans.ProprietarioGwtBean;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class EventosPesquisaServiceImpl extends GwtBaseServlet implements
		EventosPesquisaService {

	private static Logger logger = Logger.getLogger(EventosPesquisaServiceImpl.class);  
	private static final long serialVersionUID = -6915517623390464130L;

	public List<EventoGwtBean> getEventos() throws Exception {
		
		verificarSessaoLogada();
		
		List<EventoGwtBean> eventos = new ArrayList<EventoGwtBean>();
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				eventos.addAll(Evento.listarEventosCAV());
			} else {
				eventos.addAll(Evento.listarEventos());
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de tipos de eventos (eventos).", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar a lista de eventos",e);
		}
		return eventos;
	}

	
	public List<NivelGwtBean> getNiveis() throws Exception {

		verificarSessaoLogada();
		
		List<NivelGwtBean> niveis = new ArrayList<NivelGwtBean>();
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				niveis.addAll(Evento.listarNiveisCAV());
			} else {
				niveis.addAll(Evento.listarNiveis());
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de níveis (eventos).", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar a lista de niveis.",e);
		}
		return niveis;
	}

	public List<PrioridadeGwtBean> getPrioridades() throws Exception {

		verificarSessaoLogada();
		
		List<PrioridadeGwtBean> prioridades = new ArrayList<PrioridadeGwtBean>();
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				prioridades = Evento.listarPrioridadesCAV();
			} else {
				prioridades = Evento.listarPrioridades();
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de prioridades (eventos).", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar a lista de prioridades",e);
		}
		return prioridades;
		
	}

	public List<CategoriaGwtBean> getCategorias() throws Exception {

		verificarSessaoLogada();
		
		List<CategoriaGwtBean> categorias = new ArrayList<CategoriaGwtBean>();
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				categorias = Evento.listarCategoriasCAV();
			} else {
				categorias = Evento.listarCategorias();
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de categorias (eventos).", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar a lista de categorias",e);
		}
		return categorias;
		
	}
	
	public List<ProprietarioGwtBean> getProprietarios() throws Exception {

		verificarSessaoLogada();

		List<ProprietarioGwtBean> proprietarios = new ArrayList<ProprietarioGwtBean>();
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				proprietarios = Evento.listarProprietariosCAV();
			} else {
				proprietarios = Evento.listarProprietarios();
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de proprietarios (eventos).", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar a lista de proprietarios",e);
		}

		return proprietarios;
		
	}
	
	@Override
	public List<GwtBean> getUsuarios() throws Exception {

		verificarSessaoLogada();
		
		List<GwtBean> usuarios = new ArrayList<GwtBean>();
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				usuarios = Evento.listarUsuariosCAV();
			} else {
				usuarios = Evento.listarUsuarios();
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de usuarios (eventos).", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar a lista de usuários",e);
		}
		return usuarios;
	}
	
	public EventoPesquisaResultadoContainerGwtBean getResultadoPesquisa(EventoPesquisaParamGwtBean params, PagingLoadConfig config) throws Exception {
		
		verificarSessaoLogada();
		
		EventoPesquisaResultadoContainerGwtBean resultados = null;
		try {
			
			if (pertenceGrupoAcessoCAV(getIdUsuario())) {
				resultados = Evento.pesquisarEventosCAV(params.getProperties(), config.getLimit(), config.getOffset(), config.getSortField(), config.getSortDir().name());
			} else {
				resultados = Evento.pesquisarEventos(params.getProperties(), config.getLimit(), config.getOffset(), config.getSortField(), config.getSortDir().name());
			}
			
		} catch (Exception e) {
			logger.error("Erro ao realizar pesquisa de eventos.", e);
			throw new Exception("Ocorreu um problema ao tentar recuperar o resultado da pesquisa",e);
		}
		return resultados;
	}
	
	private List<String> toList(String strings){
		
		List<String> list = new ArrayList<String>();
		
		if (strings != null && strings.length() > 0)
		{
			for (String string : strings.split(",")) {
				if (!"".equals(string.trim())){
					list.add(string);
				}
			}
		}
		return list;
	}
	
	
	protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Verifica segurança
		Acesso acesso = new Acesso(req, resp, false); 
		if (!acesso.verificaAcesso(true))
		{
			// throw new ServletException("Usuário não atenticado!");
			return;
		}

		logger.debug("Exportando CSV de eventos.");
		
		try {
			// Recupera os parâmetros
			HttpSession session = req.getSession();
			
			if (session != null){
				
				Map<String, Object> params;
				
				String mensagem = req.getParameter("mensagem");
				List<String> proprietarios = toList(req.getParameter("proprietarios")); 
				List<String> prioridades = toList(req.getParameter("prioridades"));
				List<String> eventos = toList(req.getParameter("eventos"));
				List<String> niveis = toList(req.getParameter("niveis"));
				List<String> categorias = toList(req.getParameter("categorias")); 
				List<String> usuarios = toList(req.getParameter("usuarios"));
				String sHoraInicial = req.getParameter("hora_inicial");
				String sHoraFinal = req.getParameter("hora_final");
				String sDataInicial = req.getParameter("data_inicial");
				String sDataFinal = req.getParameter("data_final");
				
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date dataInicial = null;
				Date dataFinal = null;

				params = new HashMap<String, Object>();
				
				// Precisa ter a data inicial
				if (sDataInicial == null || !ExpValida.DATA.validar(sDataInicial)) {
					throw new ServletException("Erro: parâmetro data_inicial incorreto");
				}
				
				// Precisa ter a data inicial
				if (sDataInicial == null || !ExpValida.DATA.validar(sDataInicial)) {
					throw new ServletException("Erro: parâmetro data_final incorreto");
				}
				
				// Se tiver hora,
				if (sHoraInicial != null && ExpValida.HORA_MINUTO.validar(sHoraInicial)) {
					sDataInicial += " " + sHoraInicial + ":00";
				} else {
					sDataInicial += " 00:00:00";					
				}

				if (sHoraFinal != null && ExpValida.HORA_MINUTO.validar(sHoraFinal)) {
					sDataFinal += " " + sHoraFinal + ":59";
				} else {
					sDataFinal += " 23:59:59";
				}
				
				// Parse
				dataInicial = df.parse(sDataInicial);
				dataFinal = df.parse(sDataFinal);
				
				// Coloca os parâmetros
				params.put("data_inicial", dataInicial);
				params.put("data_final", dataFinal);
				
				if (proprietarios.size() > 0) {
					params.put("proprietarios", proprietarios);
				}
				if (prioridades.size() > 0) {
					params.put("prioridades", prioridades);
				}
				if (niveis.size() > 0) {
					params.put("niveis", niveis);
				}
				if (eventos.size() > 0) {
					params.put("eventos", eventos);
				}
				if (categorias.size() > 0) {
					params.put("categorias", categorias);
				}
				if (usuarios.size() > 0){
					params.put("usuarios", usuarios);
				}
				if (mensagem != null && mensagem.length() > 0 ) {
					params.put("mensagem", "%" + mensagem + "%");
				}

				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				PrintWriter out = null;
				
				try {
					// Ajusta a resposta do servlet. 
					resp.setContentType("application/save");
					resp.setCharacterEncoding("ISO-8859-1");
					resp.addHeader("Content-disposition","attachment; filename=" + getCsvFileName() + ";");
					
					out = resp.getWriter();
					out.write("Data/Hora;Proprietário;Usuário;Evento;Mensagem;Prioridade;Categoria;Nível\r\n");
					
					// Pede os dados do banco
					conn = Conexao.getConexao();
					
					if (pertenceGrupoAcessoCAV(acesso.getUsuario().getId())) {
						rs = Evento.getCSVCAV(params, conn, ps);
					} else {
						rs = Evento.getCSV(params, conn, ps);
					}
					
					while (rs.next()) {
						out.write(rs.getString(1));
						out.write("\r\n");
					}
					
				} catch (Exception e) {
					if ( out != null)
					{
						out.write(Funcoes.concatStringArray(Funcoes.repeteString("\"CSV CORROMPIDO\"", 8), ";") + "\r\n");
					}
					logger.error("Erro ao exportar CSV de eventos.", e);
				} finally {
					try {
						if (rs != null)
							rs.close();
						if (ps != null)
							ps.close();
						if (conn != null)
							conn.close();
					} catch (SQLException e) {
						logger.error("Erro ao fechar conexão com o banco de dados (exportação de CSV de eventos)", e);
					}
				}
			} else {
				getServletContext().getRequestDispatcher("/login/login.jsp?p=/eventospesquisa/eventos_pesquisa.jsp").forward(req, resp);
			}
		} catch (ParseException e) {
			logger.error("Erro ao exportar CSV de eventos.", e);
		} catch (Exception e) {
			logger.error("Erro ao exportar CSV de eventos.", e);
		}
	}
	
	private String getCsvFileName() {
		
		StringBuilder filename = new StringBuilder();
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
		
		filename.append("log_eventos-");
		Calendar cal = Calendar.getInstance();
		df = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
		filename.append(df.format(cal.getTime()));
		filename.append(".csv");
		
		return filename.toString();
	}

	
	
	/**
	 * Autor: Thiago Surgik 04/04/2016
	 * Verifica se usuário pertence ao grupo de consulta de CAV.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static boolean pertenceGrupoAcessoCAV(Integer idUsuario) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = false;
		String grupoCAV = "Consulta Eventos CAV";

		sbSQL.append(" SELECT 1 AS existe ");
		sbSQL.append(" FROM   sis_usuario_grupo sug ");
		sbSQL.append(" WHERE  sug.id_usuario = ? ");
		sbSQL.append(" 		  AND sug.id_grupo = ( ");
		sbSQL.append(" 		  			SELECT sg.id_grupo ");
		sbSQL.append(" 		  			FROM   sis_grupo sg ");
		sbSQL.append(" 		  			WHERE  sg.descricao = '" + grupoCAV + "' ");
		sbSQL.append(" 		  ) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);

			rs = ps.executeQuery();
			
			if (rs.next()) {
				if(rs.getInt("existe") > 0){
					retorno = true;
				}				
			}
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao verficar grupo de acesso do CAV.");
			throw new ModelException(sbErro.toString(), ex);
		} finally {
			if (conn != null)
				conn.close();
		}
		return retorno;
	}
	
}

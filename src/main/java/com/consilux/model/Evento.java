package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.lib.Tuple;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.BaseGwtBean;
import com.consilux.ui.client.beans.CategoriaGwtBean;
import com.consilux.ui.client.beans.EventoGwtBean;
import com.consilux.ui.client.beans.EventoPesquisaResultadoContainerGwtBean;
import com.consilux.ui.client.beans.EventoPesquisaResultadoGwtBean;
import com.consilux.ui.client.beans.GwtBean;
import com.consilux.ui.client.beans.NivelGwtBean;
import com.consilux.ui.client.beans.PrioridadeGwtBean;
import com.consilux.ui.client.beans.ProprietarioGwtBean;
import com.consilux.ui.server.EventosPesquisaServiceImpl;

public class Evento {
	
	public final static String __LIKE  = " LIKE ";
	public final static String __OR	   = " OR ";
	public final static String __AND   = " AND ";
	public final static String __EQUAL = " = ";
	public final static String __WHERE = " WHERE ";
	public final static String __EMPTY = "";
	
	public static final String PARAM_MENSAGEM = "mensagem";
	public static final String PARAM_PROPRIETARIOS = "proprietarios";
	public static final String PARAM_PRIORIDADES = "prioridades";
	public static final String PARAM_EVENTOS = "eventos";
	public static final String PARAM_NIVEIS = "niveis";
	public static final String PARAM_CATEGORIAS = "categorias";
	public static final String PARAM_USUARIOS = "usuarios";
	
	public static final String PARAM_DATA_INICIAL = "data_inicial";
	public static final String PARAM_DATA_FINAL = "data_final";
	public static final String PARAM_HORA_INICIAL = "hora_inicial";
	public static final String PARAM_HORA_FINAL = "hora_final";	
	
	private static Logger logger = Logger.getLogger(EventosPesquisaServiceImpl.class);
	
	public static String join(Collection<String> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
	
	public static String join(String[] s, String delimiter){
		List<String> collection = new ArrayList<String>();
		for (String string : s) {
			collection.add(string);
		}
		return join(collection, delimiter);
	}
	
	public static String sqlValue(Object value){
		if (value instanceof Integer) {
			return sqlValue((Integer) value);
		}
		if (value instanceof String) {
			return sqlValue((String) value);
		}
		return null;
	}
	
	public static String sqlValue(String value){
		StringBuilder sb = new StringBuilder();
		return sb.append("'").append(value).append("'").toString();
	}
	
	public static String sqlValue(String value, boolean likeComparator){
		StringBuilder sb = new StringBuilder();
		return sb.append("'%").append(value).append("%'").toString();
	}
	
	public static String sqlValue(Integer value){
		StringBuilder sb = new StringBuilder();
		return sb.append(value).toString();
	}
	
	public static Integer countQuery(String whereClause) throws Exception{
		StringBuilder query = new StringBuilder();
		Integer totalRows = 0;
		query.append("SELECT COUNT(*) FROM eventos_csx_pesquisa ");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			query.append(!"".equals(whereClause) ? __WHERE : ' ');
			query.append(whereClause);
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			rs.next();
			totalRows = rs.getInt(1);
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError("Memória insuficiente: "+e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		return totalRows;
	}
	
	public static ResultSet getCSV(Map<String,Object> mFiltros, Connection conn, PreparedStatement ps) throws Exception {
		
		logger.info("Consulta Eventos - Executando método getCSV()");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append(" '\"' + CONVERT(VARCHAR, COALESCE(data_hora,''), 20) + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(proprietario,'') + '\";' + ");
		sbSQL.append(" '\"' + RTRIM(COALESCE(usuario,'')) + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(evento,'') + '\";' + ");
		sbSQL.append(" '\"' + REPLACE(REPLACE(COALESCE(mensagem,''), '\"', ''''), CHAR(10), '') + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(prioridade,'') + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(categoria,'') + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(nivel,'') + '\"' AS csvLine ");
		sbSQL.append(" FROM eventos_csx_pesquisa ");
		
		
		if (!mFiltros.isEmpty()){
			
			String whereClause = handleParameters2(mFiltros);
			
			if (whereClause.length() > 0)
			{
				sbSQL.append(" WHERE ");
				sbSQL.append(whereClause);
			}
		}
		
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Injeta os parâmetros (ou seja: substituir as interrogações).
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			// Executa a querye devolve o resultset. 
			rs = ps.executeQuery();
			return rs;
			
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError("Memória insuficiente: " + e.getMessage());
		} 
	}
	
	public static ResultSet getCSVCAV(Map<String,Object> mFiltros, Connection conn, PreparedStatement ps) throws Exception {
		
		logger.info("Consulta Eventos - Executando método getCSVCAV()");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append(" '\"' + CONVERT(VARCHAR, COALESCE(data_hora,''), 20) + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(proprietario,'') + '\";' + ");
		sbSQL.append(" '\"' + RTRIM(COALESCE(usuario,'')) + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(evento,'') + '\";' + ");
		sbSQL.append(" '\"' + REPLACE(REPLACE(COALESCE(mensagem,''), '\"', ''''), CHAR(10), '') + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(prioridade,'') + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(categoria,'') + '\";' + ");
		sbSQL.append(" '\"' + COALESCE(nivel,'') + '\"' AS csvLine ");
		sbSQL.append(" FROM eventos_csx_pesquisa_CAV ");
		
		
		if (!mFiltros.isEmpty()){
			
			String whereClause = handleParameters2(mFiltros);
			
			if (whereClause.length() > 0)
			{
				sbSQL.append(" WHERE ");
				sbSQL.append(whereClause);
			}
		}
		
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Injeta os parâmetros (ou seja: substituir as interrogações).
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			// Executa a querye devolve o resultset. 
			rs = ps.executeQuery();
			return rs;
			
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError("Memória insuficiente: " + e.getMessage());
		} 
	}
	
	public static EventoPesquisaResultadoContainerGwtBean pesquisarEventos(Map<String, Object> params, 
																		   int limit, 
																		   int offset,
																		   String sortField,
																		   String sortDirection)
				throws Exception {
		
		logger.info("Consulta Eventos - Executando método pesquisarEventos()");
		
		List<EventoPesquisaResultadoGwtBean> lRet = new ArrayList<EventoPesquisaResultadoGwtBean>();
		Integer totalRows = 0;
		
		StringBuilder query = new StringBuilder();

		List<String> whereClause = new ArrayList<String>();
		String limitClause = new String();
		try {
			if (!params.isEmpty()){

				whereClause = handleParameters(params);
				query.append("SELECT * FROM (\n");
				query.append(   "\tSELECT data_hora, proprietario, evento, mensagem, prioridade, nivel, categoria , usuario \n");
				query.append(       "\t, ROW_NUMBER() OVER(ORDER BY data_hora DESC) as rowNumber\n");
				query.append(   "\tFROM eventos_csx_pesquisa\n");
				query.append(whereClause.isEmpty() ? __EMPTY : "\t" + __WHERE + join(whereClause,__AND)+"\n");
				query.append(") as eventos\n");
				totalRows = countQuery(join(whereClause,__AND));
				limitClause = "rowNumber BETWEEN "+(offset + 1) + __AND + (Math.min(offset+limit,totalRows));
				query.append(__WHERE +limitClause);
			}
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			
			EventoPesquisaResultadoGwtBean evento;
			while (rs.next()){
				evento = new EventoPesquisaResultadoGwtBean();
				evento.setDataHora(new Date(rs.getTimestamp("data_hora").getTime()));
				evento.setCategoria(rs.getString("categoria"));
				evento.setEvento(rs.getString("evento"));
				evento.setPrioridade(rs.getString("prioridade"));
				evento.setProprietario(rs.getString("proprietario"));
				evento.setMensagem(rs.getString("mensagem"));
				evento.setNivel(rs.getString("nivel"));
				evento.setUsuario(rs.getString("usuario"));
				lRet.add(evento);
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError("Memória insuficiente: "+e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		EventoPesquisaResultadoContainerGwtBean container = new EventoPesquisaResultadoContainerGwtBean();
		container.setBeans(lRet);
		container.setTotalRows(totalRows);
		return container;
	}
	
	
	public static EventoPesquisaResultadoContainerGwtBean pesquisarEventosCAV(
			Map<String, Object> params, int limit, int offset,
			String sortField, String sortDirection) throws Exception {
		
		logger.info("Consulta Eventos - Executando método pesquisarEventosCAV()");

		List<EventoPesquisaResultadoGwtBean> lRet = new ArrayList<EventoPesquisaResultadoGwtBean>();
		Integer totalRows = 0;

		StringBuilder query = new StringBuilder();

		List<String> whereClause = new ArrayList<String>();
		String limitClause = new String();
		try {
			if (!params.isEmpty()) {

				whereClause = handleParameters(params);
				query.append("SELECT * FROM (\n");
				query.append(   "\tSELECT data_hora, proprietario, evento, mensagem, prioridade, nivel, categoria , usuario \n");
				query.append(       "\t, ROW_NUMBER() OVER(ORDER BY data_hora DESC) as rowNumber\n");
				query.append(   "\tFROM eventos_csx_pesquisa_CAV\n");
				query.append(whereClause.isEmpty() ? __EMPTY : "\t" + __WHERE + join(whereClause,__AND)+"\n");
				query.append(") as eventos\n");
				totalRows = countQuery(join(whereClause,__AND));
				limitClause = "rowNumber BETWEEN "+(offset + 1) + __AND + (Math.min(offset+limit,totalRows));
				query.append(__WHERE +limitClause);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();

			EventoPesquisaResultadoGwtBean evento;
			while (rs.next()) {
				evento = new EventoPesquisaResultadoGwtBean();
				evento.setDataHora(new Date(rs.getTimestamp("data_hora").getTime()));
				evento.setCategoria(rs.getString("categoria"));
				evento.setEvento(rs.getString("evento"));
				evento.setPrioridade(rs.getString("prioridade"));
				evento.setProprietario(rs.getString("proprietario"));
				evento.setMensagem(rs.getString("mensagem"));
				evento.setNivel(rs.getString("nivel"));
				evento.setUsuario(rs.getString("usuario"));
				lRet.add(evento);
			}
			
			rs.close();
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		} catch (OutOfMemoryError e) {
			throw new OutOfMemoryError("Memória insuficiente: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		EventoPesquisaResultadoContainerGwtBean container = new EventoPesquisaResultadoContainerGwtBean();
		container.setBeans(lRet);
		container.setTotalRows(totalRows);
		
		return container;
	}
	
	private static String handleParameters2(Map<String,Object> mFiltros) throws ModelException
	{
		Map<String, String> mRegras = new HashMap<String, String>();
		
		if (mFiltros.containsKey(PARAM_DATA_INICIAL) && mFiltros.get(PARAM_DATA_INICIAL) != null)
		{
			mRegras.put(PARAM_DATA_INICIAL, " data_hora >= ? ");
		}
		if (mFiltros.containsKey(PARAM_DATA_FINAL) && mFiltros.get(PARAM_DATA_FINAL) != null)
		{
			mRegras.put(PARAM_DATA_FINAL, " data_hora <= ? ");
		}
		if (mFiltros.containsKey(PARAM_MENSAGEM) && mFiltros.get(PARAM_MENSAGEM) != null){
			mRegras.put(PARAM_MENSAGEM, " mensagem LIKE ? ");
		}
		
		if (mFiltros.containsKey(PARAM_PROPRIETARIOS) && mFiltros.get(PARAM_PROPRIETARIOS) != null) {
			List<?> pProprietarios = (List<?>) mFiltros.get(PARAM_PROPRIETARIOS);
			Tuple<String,String>  regraMultipĺa = Funcoes.preparaRegraMultipla(PARAM_PROPRIETARIOS, "proprietario", pProprietarios.size());
			mRegras.put(regraMultipĺa.getKey(), regraMultipĺa.getValue());	
		}
		
		if (mFiltros.containsKey(PARAM_NIVEIS) && mFiltros.get(PARAM_NIVEIS) != null) {
			List<?> pNiveis = (List<?>) mFiltros.get(PARAM_NIVEIS);
			Tuple<String,String>  regraMultipĺa = Funcoes.preparaRegraMultipla(PARAM_NIVEIS, "nivel", pNiveis.size());
			mRegras.put(regraMultipĺa.getKey(), regraMultipĺa.getValue());	
		}
		
		if (mFiltros.containsKey(PARAM_EVENTOS) && mFiltros.get(PARAM_EVENTOS) != null){
			List<?> pEventos = (List<?>) mFiltros.get(PARAM_EVENTOS);
			Tuple<String,String>  regraMultipĺa = Funcoes.preparaRegraMultipla(PARAM_EVENTOS, "evento", pEventos.size());
			mRegras.put(regraMultipĺa.getKey(), regraMultipĺa.getValue());		
		}
		
		if (mFiltros.containsKey(PARAM_PRIORIDADES) && mFiltros.get(PARAM_PRIORIDADES) != null) {
			List<?> pPrioridades = (List<?>) mFiltros.get(PARAM_PRIORIDADES);
			Tuple<String,String>  regraMultipĺa = Funcoes.preparaRegraMultipla(PARAM_PRIORIDADES, "prioridade", pPrioridades.size());
			mRegras.put(regraMultipĺa.getKey(), regraMultipĺa.getValue());			
		}
		
		if (mFiltros.containsKey(PARAM_CATEGORIAS) && mFiltros.get(PARAM_CATEGORIAS) != null) {
			List<?> pCategorias = (List<?>) mFiltros.get(PARAM_CATEGORIAS);
			Tuple<String,String>  regraMultipĺa = Funcoes.preparaRegraMultipla(PARAM_CATEGORIAS, "categoria", pCategorias.size());
			mRegras.put(regraMultipĺa.getKey(), regraMultipĺa.getValue());
		}
		
		if (mFiltros.containsKey(PARAM_USUARIOS) && mFiltros.get(PARAM_USUARIOS) != null) {
			List<?> pUsuarios = (List<?>) mFiltros.get(PARAM_USUARIOS);
			Tuple<String,String>  regraMultipĺa = Funcoes.preparaRegraMultipla(PARAM_USUARIOS, "usuario", pUsuarios.size());
			mRegras.put(regraMultipĺa.getKey(), regraMultipĺa.getValue());
		}
		
		return Funcoes.preparaCondicoesFiltro(mFiltros, mRegras); 
	}
	
	private static List<String> handleParameters(Map<String,Object> params){
		List<String> whereClause = new ArrayList<String>();
		
		if (params.containsKey("data_inicial") && params.get("data_inicial") != null){


			Date dIni = (Date)params.get("data_inicial");
			Date dFim = (Date)params.get("data_final");
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if (params.containsKey("hora_inicial") && params.get("hora_inicial") != null){
				Date hIni = (Date)params.get("hora_inicial");
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTime(dIni);
				c2.setTime(hIni);
				
				c1.set(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
				c1.set(Calendar.MINUTE, c2.get(Calendar.MINUTE));
				c1.set(Calendar.SECOND, c2.get(Calendar.SECOND));
				c1.set(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));
				
				dIni.setTime(c1.getTimeInMillis());
			}
			
			if (params.containsKey("hora_final") && params.get("hora_final") != null){
				Date hFim = (Date)params.get("hora_final");
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTime(dFim);
				c2.setTime(hFim);
				
				c1.set(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
				c1.set(Calendar.MINUTE, c2.get(Calendar.MINUTE));
				c1.set(Calendar.SECOND, c2.get(Calendar.SECOND));
				c1.set(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));
				
				dFim.setTime(c1.getTimeInMillis());
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(dFim.getTime());
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MILLISECOND, 999);
				dFim.setTime(calendar.getTimeInMillis());
			}
			
			String sqlDate1 = formatter.format(dIni);
			String sqlDate2 = formatter.format(dFim);
			
			whereClause.add("data_hora BETWEEN '"+sqlDate1+"' AND '"+sqlDate2+"'");
		}
		if (params.containsKey("proprietarios") && params.get("proprietarios") != null){
			whereClause.add(handleParameter((List<?>)params.get("proprietarios"), "proprietario", "descricao"));
		}
		if (params.containsKey("niveis") && params.get("niveis") != null){
			whereClause.add(handleParameter((List<?>)params.get("niveis"), "id_nivel", "id"));
		}
		if (params.containsKey("eventos") && params.get("eventos") != null){
			whereClause.add(handleParameter((List<?>)params.get("eventos"), "id_evento", "id"));
		}
		if (params.containsKey("prioridades") && params.get("prioridades") != null){
			whereClause.add(handleParameter((List<?>)params.get("prioridades"), "id_prioridade", "id"));
		}
		
		if (params.containsKey("categorias") && params.get("categorias") != null){
			whereClause.add(handleParameter((List<?>)params.get("categorias"), "id_categoria", "id"));
		}
		
		if (params.containsKey("usuarios") && params.get("usuarios") != null) {
			whereClause.add(handleParameter((List<?>)params.get("usuarios"), "usuario", "descricao"));
		}
		
		if (params.containsKey("mensagem") && params.get("mensagem") != null){
			whereClause.add(handleParameter(params.get("mensagem"), "mensagem", __LIKE));
		}
		
		return whereClause;
	}
	
	
	private static String handleParameter(List<?> parameters, String fieldName, String propertyName) {
		
		if (parameters.size() == 1) {
			String clause = "";
			for (Object parameter : parameters) {
				if (parameter instanceof BaseGwtBean){
					clause = handleParameter((Object) ((BaseGwtBean<?>)parameter).get(propertyName), fieldName, __EQUAL);
				} else if (parameter instanceof String){
					clause = handleParameter(parameter, fieldName, __EQUAL);
				}
			}
			return "("+clause+")";
			
		} else {
			List<String> list = new ArrayList<String>();
			for (Object parameter : parameters) {
				if (parameter instanceof BaseGwtBean){
					list.add(sqlValue((Object) ((BaseGwtBean<?>)parameter).get(propertyName) ));
				} else if (parameter instanceof String){
					list.add(sqlValue((String)parameter));
				}
			}
			return "(" + fieldName +" IN ("+join(list, ",") +"))";
		}
	}
	
	private static String handleParameter(Object value, String fieldName, String comparisionOperator){
		if (__LIKE.equals(comparisionOperator)){
			return (new StringBuilder())
						.append(fieldName)
						.append(comparisionOperator)
						.append(sqlValue((String)value,true)) //Retorna %value% para comparacoes com like
						.toString();
		}
		return (new StringBuilder())
					.append(fieldName)
					.append(comparisionOperator)
					.append(sqlValue(value))
					.toString();
	}
	
	
	public static List<ProprietarioGwtBean> listarProprietarios() throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarProprietarios()");
		
		List<ProprietarioGwtBean> lRet = new ArrayList<ProprietarioGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("select proprietario from eventos_csx_desc_proprietario order by proprietario");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new ProprietarioGwtBean(""+rs.getString("proprietario") ));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	public static List<ProprietarioGwtBean> listarProprietariosCAV() throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarProprietariosCAV()");
		
		List<ProprietarioGwtBean> lRet = new ArrayList<ProprietarioGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT proprietario FROM dbo.fcn_getEventosCsxDescProprietarioCAV() ORDER BY proprietario");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new ProprietarioGwtBean(""+rs.getString("proprietario") ));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}

	
	public static List<PrioridadeGwtBean> listarPrioridades() 
				throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarPrioridades()");
		
		List<PrioridadeGwtBean> lRet = new ArrayList<PrioridadeGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_prioridade,prioridade FROM eventos_csx_desc_prioridade ORDER BY [prioridade]");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new PrioridadeGwtBean(rs.getInt(1),rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	public static List<PrioridadeGwtBean> listarPrioridadesCAV() 
			throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarPrioridadesCAV()");
		
		List<PrioridadeGwtBean> lRet = new ArrayList<PrioridadeGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_prioridade,prioridade FROM dbo.fcn_getEventosCsxDescPrioridadeCAV() ORDER BY prioridade");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new PrioridadeGwtBean(rs.getInt(1),rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	
	public static List<NivelGwtBean> listarNiveis() 
				throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarNiveis()");
		
		List<NivelGwtBean> lRet = new ArrayList<NivelGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_nivel,nivel FROM eventos_csx_desc_nivel ORDER BY [nivel]");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new NivelGwtBean(rs.getInt(1),rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	public static List<NivelGwtBean> listarNiveisCAV() 
			throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarNiveisCAV()");
		
		List<NivelGwtBean> lRet = new ArrayList<NivelGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_nivel,nivel FROM dbo.fcn_getEventosCsxDescNivelCAV() ORDER BY nivel");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new NivelGwtBean(rs.getInt(1),rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	
	
	public static List<EventoGwtBean> listarEventos() 
				throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarEventos()");
		
		List<EventoGwtBean> lRet = new ArrayList<EventoGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_evento, evento FROM eventos_csx_desc_evento ORDER BY [evento]");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new EventoGwtBean(rs.getInt(1),rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	public static List<EventoGwtBean> listarEventosCAV() throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarEventosCAV()");
		
		List<EventoGwtBean> lRet = new ArrayList<EventoGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_evento, evento FROM dbo.fcn_getEventosCsxDescEventoCAV() ORDER BY evento");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new EventoGwtBean(rs.getInt(1),rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	
	public static List<CategoriaGwtBean> listarCategorias() 
			throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarCategorias()");
		
		List<CategoriaGwtBean> lRet = new ArrayList<CategoriaGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT * FROM eventos_csx_desc_categoria ORDER BY [categoria]");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(query.toString());
		rs = ps.executeQuery();
		while (rs.next()){
			lRet.add(new CategoriaGwtBean(rs.getInt("id_categoria"),rs.getString("categoria")));
		}
		rs.close();
		} catch (SQLException e) {
		throw new ConexaoException("ERRO de SQL",e);
		} finally {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		}
		
		return lRet;
	}
	
	public static List<CategoriaGwtBean> listarCategoriasCAV() throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarCategoriasCAV()");
		
		List<CategoriaGwtBean> lRet = new ArrayList<CategoriaGwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT * FROM dbo.fcn_getEventosCsxDescCategoriaCAV() ORDER BY categoria");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(query.toString());
		rs = ps.executeQuery();
		while (rs.next()){
			lRet.add(new CategoriaGwtBean(rs.getInt("id_categoria"),rs.getString("categoria")));
		}
		rs.close();
		} catch (SQLException e) {
		throw new ConexaoException("ERRO de SQL",e);
		} finally {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		}
		
		return lRet;
	}
	

	public static List<GwtBean> listarUsuarios() throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarUsuarios()");
		
		List<GwtBean> lRet = new ArrayList<GwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_usuario,usuario FROM sis_usuario_eventos ORDER BY [usuario]");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new GwtBean(rs.getInt("id_usuario"),rs.getString("usuario")));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	public static List<GwtBean> listarUsuariosCAV() throws ConexaoException {
		
		logger.info("Consulta Eventos - Executando método listarUsuariosCAV()");
		
		List<GwtBean> lRet = new ArrayList<GwtBean>();
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT id_usuario,usuario FROM sis_usuario_eventos_CAV ORDER BY usuario");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(query.toString());
			rs = ps.executeQuery();
			while (rs.next()){
				lRet.add(new GwtBean(rs.getInt("id_usuario"),rs.getString("usuario")));
			}
			rs.close();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}
	
	
	public static boolean incluirEventoCSX( EventoCSX eventoCSX )
	throws ConexaoException, SQLException {
		
		Connection conn = null;
		
		try {
			
			conn = Conexao.getConexao();
			return incluirEventoCSX(conn, eventoCSX);
			
		} finally {
			if (conn != null)
				conn.close();
		}
		
	}

	
	public static boolean incluirEventoCSX(Connection conn, EventoCSX eventoCSX)
	throws SQLException {

		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("{? = call spu_insere_evento_csx(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
				
		CallableStatement cs = conn.prepareCall(sbSQL.toString());

		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setString(2, eventoCSX.getProprietario() );
		cs.setTimestamp(3, new Timestamp( eventoCSX.getData_hora().getTime() ) );
		cs.setInt(4, eventoCSX.getId_categoria());
		cs.setInt(5, eventoCSX.getId_evento());
		cs.setNull(6, Types.VARCHAR);  // campo legado, não é utilizado na sp
		cs.setString(7, eventoCSX.getMensagem());
		cs.setInt(8, eventoCSX.getPrioridade());
		cs.setInt(9, eventoCSX.getNivel());
		cs.setString(10, eventoCSX.getUsuario());
		
		cs.execute();
		
		bRet = cs.getInt(1) > 0;

		return bRet;
	}

	
	
}

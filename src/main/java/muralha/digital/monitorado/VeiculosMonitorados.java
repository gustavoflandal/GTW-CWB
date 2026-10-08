package muralha.digital.monitorado;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import muralha.digital.consulta.StatusAlertaOcorrencia.StatusAlerta;
import muralha.digital.util.Paginacao;

@XmlRootElement		(name="VeiculosMonitorados") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class VeiculosMonitorados
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(VeiculosMonitorados.class);
	
	@XmlElementWrapper	(name = "ListaVeiculosMonitorados")
	@XmlElement			(name = "VeiculoMonitorado")	
	private List<VeiculoMonitorado> listaVeiculosMonitorados;
	
	@XmlElementWrapper	(name = "VeiculosMonitoradosAtivos")
	@XmlElement			(name = "VeicMonitorado")	
	private List<VeicMonitorado> veiculosMonitoradosAtivos;	
	
	private Paginacao paginacao;

	public List<VeiculoMonitorado> getListaVeiculosMonitorados() {
		return listaVeiculosMonitorados;
	}
	public void setListaVeiculosMonitorados(List<VeiculoMonitorado> listaVeiculosMonitorados) {
		this.listaVeiculosMonitorados = listaVeiculosMonitorados;
	}
	
	public List<VeicMonitorado> getListaVeiculosMonitoradosAtivos() {
		return veiculosMonitoradosAtivos;
	}
	public void setListaVeiculosMonitoradosAtivos(List<VeicMonitorado> veiculosMonitoradosAtivos) {
		this.veiculosMonitoradosAtivos = veiculosMonitoradosAtivos;
	}
	
	public Paginacao getPaginacao() {
		return paginacao;
	}
	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}

	public VeiculosMonitorados()
	{
		super();
	}
	
	public static VeiculosMonitorados ObterListaVeiculosMonitorados(UUID idTipoAlertaOcorrencia, Date dataIni, Date dataFim, String placa,
		boolean buscaApenasCadAtivo, boolean buscarApenasPlacaComCoringa, boolean supervisionado,boolean privado, Integer idUsuario,  Paginacao paginacao) throws ConexaoException, SQLException 
	{
		VeiculosMonitorados retorno = new VeiculosMonitorados();
		List<VeiculoMonitorado> listaRet = new ArrayList<VeiculoMonitorado>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
			sbSQL.append(" SELECT cvm.id, ");
			sbSQL.append(" 		  cvm.nome, ");
			sbSQL.append(" 		  cvm.placa, ");
			sbSQL.append(" 		  tao.id AS id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  RTRIM(cvm.descricao) AS descricao, ");
			sbSQL.append(" 		  cvm.privado, ");
			sbSQL.append(" 		  cvm.data_inicio, ");
			sbSQL.append(" 		  cvm.data_fim, ");
			sbSQL.append(" 		  cvm.data_cadastro, ");
			sbSQL.append(" 		  su.id_usuario, ");
			sbSQL.append(" 		  RTRIM(su.usuario) AS usuario, ");
			sbSQL.append(" 		  RTRIM(su.nome) AS nome_usuario, ");
			sbSQL.append(" 		  su_exc.id_usuario AS id_usuario_exclusao, ");
			sbSQL.append(" 		  RTRIM(su_exc.usuario) AS usuario_exclusao, ");
			sbSQL.append(" 		  RTRIM(su_exc.nome) AS nome_usuario_exclusao, ");
			sbSQL.append(" 		  cvm.data_exclusao, ");
			sbSQL.append(" 		  RTRIM(cvm.motivo_exclusao) AS motivo_exclusao, ");
			sbSQL.append(" 		  su_ina.id_usuario AS id_usuario_inativacao, ");
			sbSQL.append(" 		  RTRIM(su_ina.usuario) AS usuario_inativacao, ");
			sbSQL.append(" 		  RTRIM(su_ina.nome) AS nome_usuario_inativacao, ");
			sbSQL.append(" 		  cvm.data_inativacao, ");
			sbSQL.append(" 		  CASE WHEN (cvm.data_fim IS NOT NULL AND CAST(cvm.data_fim AS DATE) < CAST(GETDATE() AS DATE)) OR cvm.data_inativacao IS NOT NULL THEN 0 ELSE 1 END AS permite_edicao, ");
			sbSQL.append(" 		  CASE WHEN a.id_cad_veiculo_monitorado IS NOT NULL THEN 1 ELSE 0 END AS possui_alerta, ");
			sbSQL.append(" 		  cvm.supervisionado, ");
			sbSQL.append(paginacao.QueryTotalRegistros());
			sbSQL.append(" FROM   muralha.cad_veiculo_monitorado cvm ");
			sbSQL.append(" 		  INNER JOIN muralha.tipo_alerta_ocorrencia tao ");
			sbSQL.append(" 		  	   ON  tao.id = cvm.id_tipo_alerta_ocorrencia ");
			sbSQL.append(" 		  LEFT JOIN sis_usuario su ");
			sbSQL.append(" 		  	   ON  su.id_usuario = cvm.id_usuario ");
			sbSQL.append(" 		  LEFT JOIN sis_usuario su_exc ");
			sbSQL.append(" 		  	   ON  su_exc.id_usuario = cvm.id_usuario_exclusao ");
			sbSQL.append(" 		  LEFT JOIN sis_usuario su_ina ");
			sbSQL.append(" 		  	   ON  su_ina.id_usuario = cvm.id_usuario_inativacao ");
			sbSQL.append(" 		  LEFT JOIN (SELECT id_cad_veiculo_monitorado, COUNT(*) AS qtde FROM muralha.alerta GROUP BY id_cad_veiculo_monitorado) AS a ");
			sbSQL.append(" 		  	   ON  a.id_cad_veiculo_monitorado = cvm.id ");
			sbSQL.append(" WHERE  cvm.data_exclusao IS NULL ");
			
			int paramIndex = 1;
			Map<Integer, Object> mapaParametros = new LinkedHashMap<Integer, Object>();
			
			if (idTipoAlertaOcorrencia != null)
			{
				sbSQL.append("	AND cvm.id_tipo_alerta_ocorrencia = ? ");
				mapaParametros.put(paramIndex++, idTipoAlertaOcorrencia);
			}
			if (dataIni != null)
			{
				sbSQL.append("	AND cvm.data_inicio >= ? ");
				mapaParametros.put(paramIndex++, new java.sql.Date(dataIni.getTime()));
			}
			if (dataIni != null && dataFim != null)
			{
				sbSQL.append("	AND (cvm.data_fim <= ? OR cvm.data_fim IS NULL) ");
				mapaParametros.put(paramIndex++, new java.sql.Date(dataFim.getTime()));
			}
			if (placa != null)
			{
				// separa por vírgula e normaliza
				String[] arr = placa.split(",");
				List<String> placasList = new ArrayList<>();
				for (String p : arr) {
					if (p != null && !p.trim().isEmpty()) {
						placasList.add(p.trim().toUpperCase());
					}
				}

				if (!placasList.isEmpty()) {
					if (placasList.size() == 1) {
						// único elemento: usa o comportamento anterior (LIKE '%p%' se curto, senão LIKE 'p')
						String p = placasList.get(0);
						String paramValue;
						if (p.contains("*")) {
							// permite o uso de '*' como coringa vindo do front (converte para SQL '%')
							paramValue = p.replace('*', '%');
						} else if (p.length() < 7) {
							paramValue = "%" + p + "%";
						} else {
							paramValue = p;
						}
						sbSQL.append(" AND UPPER(cvm.placa) LIKE ? ");
						mapaParametros.put(paramIndex++, paramValue);

					} else {
						// várias placas: monta (cvm.placa LIKE ? OR cvm.placa LIKE ? OR ...)
						sbSQL.append(" AND (");
						for (int i = 0; i < placasList.size(); i++) {
							if (i > 0) sbSQL.append(" OR ");
							sbSQL.append(" UPPER(cvm.placa) LIKE ? ");
							String p = placasList.get(i);
							String paramValue;
							if (p.contains("*")) {
								paramValue = p.replace('*', '%');
							} else if (p.length() < 7) {
								paramValue = "%" + p + "%";
							} else {
								paramValue = p;
							}
							mapaParametros.put(paramIndex++, paramValue);
						}
						sbSQL.append(") ");
					}
				}
			}
			if (buscaApenasCadAtivo)
			{
				sbSQL.append("	AND ( ");
				sbSQL.append("			(cvm.data_fim IS NULL OR cvm.data_fim >= CAST(GETDATE() AS DATE)) ");
				sbSQL.append("			AND cvm.data_exclusao IS NULL ");
				sbSQL.append("			AND cvm.data_inativacao IS NULL ");
				sbSQL.append("	    ) ");
			}
			if (buscarApenasPlacaComCoringa)
			{
				sbSQL.append("	AND CHARINDEX('*', cvm.placa) > 0 ");
			}
			
			if (supervisionado) {
				sbSQL.append(" AND cvm.supervisionado = ? ");
				mapaParametros.put(paramIndex++, true);
			}
			
			if (privado) {
				// Buscar apenas os privados do próprio usuário
				sbSQL.append(" AND cvm.privado = 1 AND cvm.id_usuario = ? ");
				mapaParametros.put(paramIndex++, idUsuario);
			} else {
				// Buscar todos os públicos ou privados do próprio usuário
				sbSQL.append(" AND (cvm.privado = 0 OR (cvm.privado = 1 AND cvm.id_usuario = ?)) ");
				mapaParametros.put(paramIndex++, idUsuario);
			}
			
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  cvm.data_inicio DESC, ");
			sbSQL.append(" 		  cvm.data_cadastro DESC ");
			sbSQL.append(paginacao.QueryPaginacao());
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Iterando e ajustando os valores dos parametros para os wheres (as interrogações):
			Object valorParam = null;
			for (Entry<Integer, Object> paramQuery : mapaParametros.entrySet()) {
				valorParam = paramQuery.getValue();
				
				if (valorParam == null)
					continue;				
				else if (valorParam instanceof UUID)
					ps.setString(paramQuery.getKey(), valorParam.toString());
				else if (valorParam instanceof String)
					ps.setString(paramQuery.getKey(), (String) valorParam);
				else if (valorParam instanceof Integer)
					ps.setInt(paramQuery.getKey(), ((Integer)valorParam).intValue());
				else if (valorParam instanceof Boolean)
					ps.setBoolean(paramQuery.getKey(), ((Boolean)valorParam).booleanValue());				
				else if (valorParam instanceof java.sql.Date)
					ps.setDate(paramQuery.getKey(), (java.sql.Date)valorParam);
				else if (valorParam instanceof Timestamp)
					ps.setTimestamp(paramQuery.getKey(), (Timestamp)valorParam);
				else {
					erro = true;
					msgErro = "Ocorreu um erro ao preparar os filtros para a pesquisa!";
					break;
				}
			}
			
			if (!erro) {
				rs = ps.executeQuery();
				
				int registros = 0;
				
				while (rs.next()) 
				{
					if (registros == 0)
						registros = rs.getInt("total_registros");
					
					VeiculoMonitorado item = new VeiculoMonitorado();
	
					item.setId(UUID.fromString(rs.getString("id")));
					item.setNome(rs.getString("nome"));
					item.setPlaca(rs.getString("placa"));
					item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
					item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
					item.setDescricao(rs.getString("descricao"));
					item.setDataInicio(rs.getDate("data_inicio"));
					item.setDataFim(rs.getDate("data_fim"));
					item.setDataCadastro(rs.getTimestamp("data_cadastro"));
					item.setIdUsuario(rs.getInt("id_usuario"));
					item.setUsuario(rs.getString("usuario"));
					item.setNomeUsuario(rs.getString("nome_usuario"));
					item.setIdUsuarioExclusao(rs.getInt("id_usuario_exclusao"));
					item.setUsuarioExclusao(rs.getString("usuario_exclusao"));
					item.setNomeUsuarioExclusao(rs.getString("nome_usuario_exclusao"));
					item.setDataExclusao(rs.getTimestamp("data_exclusao"));
					item.setIdUsuarioInativacao(rs.getInt("id_usuario_inativacao"));
					item.setUsuarioInativacao(rs.getString("usuario_inativacao"));
					item.setNomeUsuarioInativacao(rs.getString("nome_usuario_inativacao"));
					item.setDataInativacao(rs.getTimestamp("data_inativacao"));
					item.setEditavel(rs.getBoolean("permite_edicao"));
					item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
					item.setSupervisionado(rs.getBoolean("supervisionado"));
					
					item.setDataInicioFormatada(item.getDataInicioFormatada());
					item.setDataFimFormatada(item.getDataFimFormatada());
					item.setDataCadastroFormatada(item.getDataCadastroFormatada());
					item.setHoraCadastroFormatada(item.getHoraCadastroFormatada());
					item.setDataExclusaoFormatada(item.getDataExclusaoFormatada());
					item.setHoraExclusaoFormatada(item.getHoraExclusaoFormatada());
					item.setDataInativacaoFormatada(item.getDataInativacaoFormatada());
					item.setHoraInativacaoFormatada(item.getHoraInativacaoFormatada());
					
					listaRet.add(item);
				}
				
				paginacao.TotalRegistros(registros);
				retorno.setPaginacao(paginacao);
				retorno.setListaVeiculosMonitorados(listaRet);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter veículos monitorados!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar os veículos monitorados no banco de dados!");
			}
		}
		return retorno;
	}
	
	public static VeiculosMonitorados ObterListaVeiculosMonitoradosPorListaPlaca(List<String> placas, Integer idUsuario) throws ConexaoException, SQLException 
	{
		VeiculosMonitorados retorno = new VeiculosMonitorados();
		List<VeiculoMonitorado> listaRet = new ArrayList<VeiculoMonitorado>();
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";

		try {
			sbSQL.append(" SELECT cvm.id, ");
			sbSQL.append("        cvm.nome, ");
			sbSQL.append("        cvm.placa, ");
			sbSQL.append("        tao.id AS id_tipo_alerta_ocorrencia, ");
			sbSQL.append("        RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, ");
			sbSQL.append("        RTRIM(cvm.descricao) AS descricao, ");
			sbSQL.append("        cvm.privado, ");
			sbSQL.append("        cvm.data_inicio, ");
			sbSQL.append("        cvm.data_fim, ");
			sbSQL.append("        cvm.data_cadastro, ");
			sbSQL.append("        su.id_usuario, ");
			sbSQL.append("        RTRIM(su.usuario) AS usuario, ");
			sbSQL.append("        RTRIM(su.nome) AS nome_usuario, ");
			sbSQL.append("        su_exc.id_usuario AS id_usuario_exclusao, ");
			sbSQL.append("        RTRIM(su_exc.usuario) AS usuario_exclusao, ");
			sbSQL.append("        RTRIM(su_exc.nome) AS nome_usuario_exclusao, ");
			sbSQL.append("        cvm.data_exclusao, ");
			sbSQL.append("        RTRIM(cvm.motivo_exclusao) AS motivo_exclusao, ");
			sbSQL.append("        su_ina.id_usuario AS id_usuario_inativacao, ");
			sbSQL.append("        RTRIM(su_ina.usuario) AS usuario_inativacao, ");
			sbSQL.append("        RTRIM(su_ina.nome) AS nome_usuario_inativacao, ");
			sbSQL.append("        cvm.data_inativacao, ");
			sbSQL.append("        CASE WHEN (cvm.data_fim IS NOT NULL AND CAST(cvm.data_fim AS DATE) < CAST(GETDATE() AS DATE)) OR cvm.data_inativacao IS NOT NULL THEN 0 ELSE 1 END AS permite_edicao, ");
			sbSQL.append("        CASE WHEN a.id_cad_veiculo_monitorado IS NOT NULL THEN 1 ELSE 0 END AS possui_alerta, ");
			sbSQL.append("        cvm.supervisionado ");
			sbSQL.append(" FROM   muralha.cad_veiculo_monitorado cvm ");
			sbSQL.append("        INNER JOIN muralha.tipo_alerta_ocorrencia tao ");
			sbSQL.append("             ON  tao.id = cvm.id_tipo_alerta_ocorrencia ");
			sbSQL.append("        LEFT JOIN sis_usuario su ");
			sbSQL.append("             ON  su.id_usuario = cvm.id_usuario ");
			sbSQL.append("        LEFT JOIN sis_usuario su_exc ");
			sbSQL.append("             ON  su_exc.id_usuario = cvm.id_usuario_exclusao ");
			sbSQL.append("        LEFT JOIN sis_usuario su_ina ");
			sbSQL.append("             ON  su_ina.id_usuario = cvm.id_usuario_inativacao ");
			sbSQL.append("        LEFT JOIN (SELECT id_cad_veiculo_monitorado, COUNT(*) AS qtde FROM muralha.alerta GROUP BY id_cad_veiculo_monitorado) AS a ");
			sbSQL.append("             ON  a.id_cad_veiculo_monitorado = cvm.id ");

			// Monta a cláusula WHERE e o filtro por placas
			sbSQL.append(" WHERE  cvm.data_exclusao IS NULL ");

			if (placas != null && !placas.isEmpty()) {
				// Cria os placeholders para o IN (?, ?, ...)
				StringBuilder inClause = new StringBuilder();
				for (int i = 0; i < placas.size(); i++) {
					inClause.append("?");
					if (i < placas.size() - 1) {
						inClause.append(",");
					}
				}
				sbSQL.append(" AND UPPER(cvm.placa) IN (").append(inClause).append(") ");
			}

			sbSQL.append(" ORDER BY ");
			sbSQL.append("        cvm.data_inicio DESC, ");
			sbSQL.append("        cvm.data_cadastro DESC ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Seta os parâmetros para a cláusula IN
			int paramIndex = 1;
			if (placas != null && !placas.isEmpty()) {
				for (String placa : placas) {
					ps.setString(paramIndex++, placa.toUpperCase().trim());
				}
			}

			rs = ps.executeQuery();

			while (rs.next()) 
			{
				VeiculoMonitorado item = new VeiculoMonitorado();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setNome(rs.getString("nome"));
				item.setPlaca(rs.getString("placa"));
				item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				item.setDescricao(rs.getString("descricao"));
				item.setDataInicio(rs.getDate("data_inicio"));
				item.setDataFim(rs.getDate("data_fim"));
				item.setDataCadastro(rs.getTimestamp("data_cadastro"));
				item.setIdUsuario(rs.getInt("id_usuario"));
				item.setUsuario(rs.getString("usuario"));
				item.setNomeUsuario(rs.getString("nome_usuario"));
				item.setIdUsuarioExclusao(rs.getInt("id_usuario_exclusao"));
				item.setUsuarioExclusao(rs.getString("usuario_exclusao"));
				item.setNomeUsuarioExclusao(rs.getString("nome_usuario_exclusao"));
				item.setDataExclusao(rs.getTimestamp("data_exclusao"));
				item.setIdUsuarioInativacao(rs.getInt("id_usuario_inativacao"));
				item.setUsuarioInativacao(rs.getString("usuario_inativacao"));
				item.setNomeUsuarioInativacao(rs.getString("nome_usuario_inativacao"));
				item.setDataInativacao(rs.getTimestamp("data_inativacao"));
				item.setEditavel(rs.getBoolean("permite_edicao"));
				item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
				item.setSupervisionado(rs.getBoolean("supervisionado"));

				item.setDataInicioFormatada(item.getDataInicioFormatada());
				item.setDataFimFormatada(item.getDataFimFormatada());
				item.setDataCadastroFormatada(item.getDataCadastroFormatada());
				item.setHoraCadastroFormatada(item.getHoraCadastroFormatada());
				item.setDataExclusaoFormatada(item.getDataExclusaoFormatada());
				item.setHoraExclusaoFormatada(item.getHoraExclusaoFormatada());
				item.setDataInativacaoFormatada(item.getDataInativacaoFormatada());
				item.setHoraInativacaoFormatada(item.getHoraInativacaoFormatada());

				listaRet.add(item);
			}
			retorno.setListaVeiculosMonitorados(listaRet);
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter veículos monitorados!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {

			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}

			if (erro) {
				throw new SQLException("Erro ao consultar os veículos monitorados no banco de dados!");
			}
		}
		return retorno;
	}
	
	public static VeiculoMonitorado ObterVeiculoMonitoradoPorId(UUID id) throws ConexaoException, SQLException 
	{
		VeiculoMonitorado monitorado = new VeiculoMonitorado();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
			sbSQL.append(" SELECT cvm.id, ");
			sbSQL.append(" 		  cvm.placa, ");
			sbSQL.append(" 		  cvm.nome, ");
			sbSQL.append(" 		  tao.id AS id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  RTRIM(cvm.descricao) AS descricao, ");
			sbSQL.append(" 		  cvm.data_inicio, ");
			sbSQL.append(" 		  cvm.data_fim, ");
			sbSQL.append(" 		  cvm.data_cadastro, ");
			sbSQL.append(" 		  cvm.privado, ");
			sbSQL.append(" 		  cvm.supervisionado, ");
			sbSQL.append(" 		  cvm.erros_permitidos_placa, ");
			sbSQL.append("		  cvm.erros_permitido_ini, ");
			sbSQL.append("		  cvm.erros_permitido_fim, ");
			sbSQL.append(" 		  su.id_usuario, ");
			sbSQL.append(" 		  RTRIM(su.usuario) AS usuario, ");
			sbSQL.append(" 		  RTRIM(su.nome) AS nome_usuario, ");
			sbSQL.append(" 		  cvm.id_usuario_responsavel, ");
			sbSQL.append(" 		  CASE WHEN (cvm.data_fim IS NOT NULL AND cvm.data_fim < CAST(GETDATE() AS DATE)) OR cvm.data_inativacao IS NOT NULL THEN 0 ELSE 1 END AS ativo, ");
			sbSQL.append(" 		  su_ina.id_usuario AS id_usuario_inativacao, ");
			sbSQL.append(" 		  RTRIM(su_ina.usuario) AS usuario_inativacao, ");
			sbSQL.append(" 		  RTRIM(su_ina.nome) AS nome_usuario_inativacao, ");
			sbSQL.append("        cvm.monitorar_somente_este, ");
			sbSQL.append(" 		  cvm.data_inativacao, ");
			sbSQL.append("        cvm.id_classe, ");
			sbSQL.append("        cvm.id_cor, ");
			sbSQL.append("        cvm.id_marca, ");
			sbSQL.append("        cvm.id_modelo, ");
			sbSQL.append("        cvm.texto_adesivo ");
			sbSQL.append(" FROM   muralha.cad_veiculo_monitorado cvm ");
			sbSQL.append(" 		  INNER JOIN muralha.tipo_alerta_ocorrencia tao ");
			sbSQL.append(" 		  	   ON  tao.id = cvm.id_tipo_alerta_ocorrencia ");
			sbSQL.append(" 		  LEFT JOIN sis_usuario su ");
			sbSQL.append(" 		  	   ON  su.id_usuario = cvm.id_usuario ");
			sbSQL.append(" 		  LEFT JOIN sis_usuario su_ina ");
			sbSQL.append(" 		  	   ON  su_ina.id_usuario = cvm.id_usuario_inativacao ");
			sbSQL.append(" WHERE  cvm.id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, id.toString());
			
			rs = ps.executeQuery();
				
			if (rs.next()) 
			{
				monitorado.setId(UUID.fromString(rs.getString("id")));
				monitorado.setPlaca(rs.getString("placa"));
				monitorado.setNome(rs.getString("nome"));
				monitorado.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				monitorado.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				monitorado.setDescricao(rs.getString("descricao"));
				monitorado.setDataInicio(rs.getDate("data_inicio"));
				monitorado.setDataFim(rs.getDate("data_fim"));
				monitorado.setDataCadastro(rs.getTimestamp("data_cadastro"));
				monitorado.setIdUsuario(rs.getInt("id_usuario"));
				monitorado.setUsuario(rs.getString("usuario"));
				monitorado.setNomeUsuario(rs.getString("nome_usuario"));
				monitorado.setAtivo(rs.getBoolean("ativo"));
				monitorado.setIdUsuarioInativacao(rs.getInt("id_usuario_inativacao"));
				monitorado.setUsuarioInativacao(rs.getString("usuario_inativacao"));
				monitorado.setNomeUsuarioInativacao(rs.getString("nome_usuario_inativacao"));
				monitorado.setDataInativacao(rs.getTimestamp("data_inativacao"));
				
				monitorado.setPrivado(rs.getBoolean("privado"));
				monitorado.setSupervisionado(rs.getBoolean("supervisionado"));

				
				Integer monitorarSomenteEste = (Integer) rs.getObject("monitorar_somente_este");
				monitorado.setMonitorarSomenteEste(monitorarSomenteEste);
				
				Integer errosPermitidosPlaca = (Integer) rs.getObject("erros_permitidos_placa");
				monitorado.setErrosPermitidosPlaca(errosPermitidosPlaca);

				/*
					Nessa parte abaixo é pego os valores de erros_permitido_ini e erros_permitido_fim,
					após isso é passado para o set de cada um, atualizando os valores.
				*/
				String errosPermitidosIni = (String) rs.getString("erros_permitido_ini");
				monitorado.setErrosPermitidosIni(errosPermitidosIni);

				String errosPermitidosFim = (String) rs.getString("erros_permitido_fim");
				monitorado.setErrosPermitidosFim(errosPermitidosFim);
				
				monitorado.setDataInicioFormatada(monitorado.getDataInicioFormatada());
				monitorado.setDataFimFormatada(monitorado.getDataFimFormatada());
				monitorado.setDataCadastroFormatada(monitorado.getDataCadastroFormatada());
				monitorado.setHoraCadastroFormatada(monitorado.getHoraCadastroFormatada());
				monitorado.setDataInativacaoFormatada(monitorado.getDataInativacaoFormatada());
				monitorado.setHoraInativacaoFormatada(monitorado.getHoraInativacaoFormatada());
				monitorado.setId_usuario_responsavel(rs.getInt("id_usuario_responsavel"));
				
				// Dados do veículo
				monitorado.setIdClasse(rs.getString("id_classe"));

				Integer idCor = (Integer) rs.getObject("id_cor");
				monitorado.setIdCor(idCor);

				Integer idMarca = (Integer) rs.getObject("id_marca");
				monitorado.setIdMarca(idMarca);

				Integer idModelo = (Integer) rs.getObject("id_modelo");
				monitorado.setIdModelo(idModelo);

				monitorado.setTextoAdesivo(rs.getString("texto_adesivo"));
				
				List<Integer> grupos = new ArrayList<>();

				String sqlGrupos = "SELECT id_grupo FROM muralha.cad_veiculo_monitorado_grupo WHERE id_cad_veiculo_monitorado = ?";
				PreparedStatement psGrupos = conn.prepareStatement(sqlGrupos);
				psGrupos.setString(1, id.toString());
				ResultSet rsGrupos = psGrupos.executeQuery();

				while (rsGrupos.next()) {
					grupos.add(rsGrupos.getInt("id_grupo"));
				}

				monitorado.setGrupos(grupos);

				rsGrupos.close();
				psGrupos.close();
				
				// Consulta equipamentos
				String sqlEquip = "SELECT id, id_local, data_cadastro, id_usuario " +
										"FROM muralha.cad_veiculo_monitorado_equipamento " +
										"WHERE id_cad_veiculo_monitorado = ?";
				PreparedStatement psEquip = conn.prepareStatement(sqlEquip);
				psEquip.setString(1, id.toString());
				ResultSet rsEquip = psEquip.executeQuery();

				List<VeiculoMonitoradoEquipamentoEntidade> equipamentos = new ArrayList<>();
				while (rsEquip.next()) {
					VeiculoMonitoradoEquipamentoEntidade eq = new VeiculoMonitoradoEquipamentoEntidade();
					eq.setId(UUID.fromString(rsEquip.getString("id")));
					eq.setIdCadVeiculoMonitorado(id);
					eq.setIdLocal(rsEquip.getInt("id_local"));
					eq.setDataCadastro(rsEquip.getTimestamp("data_cadastro"));
					eq.setIdUsuario(rsEquip.getInt("id_usuario"));
					equipamentos.add(eq);
				}
				monitorado.setEquipamentosEntidade(equipamentos);
				rsEquip.close();
				psEquip.close();
				
				// Consulta horários permitidos
				String sqlHorarios = "SELECT id, dia_semana, hora_inicio, hora_fim, data_cadastro, id_usuario " +
											"FROM muralha.cad_veiculo_monitorado_periodo " +
											"WHERE id_cad_veiculo_monitorado = ? " +
											"ORDER BY dia_semana ASC, hora_inicio ASC";
				PreparedStatement psHorarios = conn.prepareStatement(sqlHorarios);
				psHorarios.setString(1, id.toString());
				ResultSet rsHorarios = psHorarios.executeQuery();

				List<VeiculoMonitoradoPeriodoEntidade> horarios = new ArrayList<>();
				while (rsHorarios.next()) {
					VeiculoMonitoradoPeriodoEntidade hp = new VeiculoMonitoradoPeriodoEntidade();
					hp.setId(UUID.fromString(rsHorarios.getString("id")));
					hp.setIdCadVeiculoMonitorado(id);
					hp.setDiaSemana(rsHorarios.getInt("dia_semana"));
					Time horaIni = rsHorarios.getTime("hora_inicio");
					Time horaFim = rsHorarios.getTime("hora_fim");
					hp.setHoraInicio(horaIni != null ? horaIni.toLocalTime() : null);
					hp.setHoraFim(horaFim != null ? horaFim.toLocalTime() : null);
					hp.setDataCadastro(rsHorarios.getTimestamp("data_cadastro"));
					hp.setIdUsuario(rsHorarios.getInt("id_usuario"));
					horarios.add(hp);
				}
				monitorado.setHorariosEntidade(horarios);
				rsHorarios.close();
				psHorarios.close();
				
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar o veículo monitorado no banco de dados!");
			}
		}
		return monitorado;
	}
	
	public static VeiculoMonitorado ObterVeiculoMonitoradoPorIdAlerta(UUID idAlerta) throws ConexaoException, SQLException 
	{
		VeiculoMonitorado monitorado = new VeiculoMonitorado();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
			sbSQL.append(" SELECT cvm.id, "											);
			sbSQL.append(" 		  cvm.placa, "										);
			sbSQL.append(" 		  cvm.nome, "										);
			sbSQL.append(" 		  tao.id AS id_tipo_alerta_ocorrencia, "			);
			sbSQL.append(" 		  RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, "		);
			sbSQL.append(" 		  RTRIM(cvm.descricao) AS descricao, "				);
			sbSQL.append(" 		  cvm.data_inicio, "								);
			sbSQL.append(" 		  cvm.data_fim, "									);
			sbSQL.append(" 		  cvm.data_cadastro, "								);
			sbSQL.append(" 		  su.id_usuario, "									);
			sbSQL.append(" 		  RTRIM(su.usuario) AS usuario, "					);
			sbSQL.append(" 		  RTRIM(su.nome) AS nome_usuario "					);
			sbSQL.append(" FROM   muralha.cad_veiculo_monitorado cvm "				);
			sbSQL.append(" 		  INNER JOIN muralha.tipo_alerta_ocorrencia tao "	);
			sbSQL.append(" 		  	   ON  tao.id = cvm.id_tipo_alerta_ocorrencia "	);
			sbSQL.append(" 		  INNER JOIN muralha.alerta a "						);
			sbSQL.append(" 		  	   ON  a.id_cad_veiculo_monitorado = cvm.id "	);
			sbSQL.append(" 		  LEFT JOIN sis_usuario su "						);
			sbSQL.append(" 		  	   ON  su.id_usuario = cvm.id_usuario "			);
			sbSQL.append(" WHERE  a.id = ? "										);

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());
			
			rs = ps.executeQuery();
				
			if (rs.next()) 
			{
				monitorado.setId(						UUID.fromString(rs.getString("id"))							);
				monitorado.setPlaca(					rs.getString("placa")										);
				monitorado.setNome(						rs.getString("nome")										);
				monitorado.setIdTipoAlertaOcorrencia(	UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia"))	);
				monitorado.setTipoAlertaOcorrencia(		rs.getString("tipo_alerta_ocorrencia")						);
				monitorado.setDescricao(				rs.getString("descricao")									);
				monitorado.setDataInicio(				rs.getDate("data_inicio")									);
				monitorado.setDataFim(					rs.getDate("data_fim")										);
				monitorado.setDataCadastro(				rs.getTimestamp("data_cadastro")							);
				monitorado.setIdUsuario(				rs.getInt("id_usuario")										);
				monitorado.setUsuario(					rs.getString("usuario")										);
				monitorado.setNomeUsuario(				rs.getString("nome_usuario")								);
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
			if (erro) {
				throw new SQLException("Erro ao consultar o veículo monitorado no banco de dados!");
			}
		}
		return monitorado;
	}
	
	public static List<String> obterPlacasComAlerta() throws ConexaoException, SQLException {
		List<String> placas = new ArrayList<>();
		
		String sql = " SELECT placa " +
			"FROM muralha.registro_fato_veiculo " +
			"GROUP BY placa " +
			"HAVING COUNT(id_registro_fato) >= 1";

		try (Connection conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String placa = rs.getString("placa");
				if (placa != null && !placa.trim().isEmpty()) {
					placas.add(placa.trim());
				}
			}

		} catch (Exception e) {
			Logger.getLogger("PlacaMonitoramento").error("Erro ao consultar placas repetidas: " + e.getMessage(), e);
			throw new SQLException("Erro ao consultar placas repetidas no banco de dados!");
		}
		return placas;
	}

	public static VeiculosMonitorados ObterCadastrosAtivos() throws ConexaoException, SQLException 
	{
		VeiculosMonitorados retorno = new VeiculosMonitorados();
		List<VeicMonitorado> listaRet = new ArrayList<VeicMonitorado>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			sbSQL.append(" SELECT cvm.id, ");
			sbSQL.append(" 		  cvm.placa, ");
			sbSQL.append(" 		  cvm.id_tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  tao.tipo AS tipo_alerta_ocorrencia, ");
			sbSQL.append(" 		  cvm.data_inicio, ");
			sbSQL.append(" 		  cvm.data_fim ");
			sbSQL.append(" FROM   muralha.cad_veiculo_monitorado cvm ");
			sbSQL.append(" 		  JOIN muralha.tipo_alerta_ocorrencia tao ");
			sbSQL.append(" 		  	   ON  tao.id = cvM.id_tipo_alerta_ocorrencia ");
			sbSQL.append(" WHERE  ( ");
			sbSQL.append(" 		  		(cvm.data_fim IS NULL OR cvm.data_fim >= CAST(GETDATE() AS DATE)) ");
			sbSQL.append(" 		  		AND cvm.data_exclusao IS NULL ");
			sbSQL.append(" 		  		AND cvm.data_inativacao IS NULL ");
			sbSQL.append(" 		  ) ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  cvm.data_cadastro DESC ");
			

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				VeicMonitorado item = new VeicMonitorado();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setPlaca(rs.getString("placa"));
				item.setIdTipoIrregularidade(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoIrregularidade(rs.getString("tipo_alerta_ocorrencia"));
				item.setDtDataInicio(rs.getDate("data_inicio"));
				item.setDtDataFim(rs.getDate("data_fim"));
				
				item.setDataInicio(item.getDataInicio());
				item.setDataFim(item.getDataFim());
				
				listaRet.add(item);
			}
			
			retorno.setListaVeiculosMonitoradosAtivos(listaRet);
		}
		catch(Exception e)
		{
			String msgErro = "Erro ao obter veículos monitorados ativos!";
			logger.error(msgErro + ": " + e.getMessage(), e);
			throw new SQLException(msgErro);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		return retorno;
	}
	
	public static boolean PossuiCadastroAtivo(UUID idTipoAlertaOcorrencia) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT 1 "																);
		sbSQL.append(" FROM   muralha.cad_veiculo_monitorado "									);
		sbSQL.append(" WHERE  id_tipo_alerta_ocorrencia = ? "									);
		sbSQL.append(" 		  AND (data_fim IS NULL OR data_fim >= CAST(GETDATE() AS DATE)) "	);
		sbSQL.append(" 		  AND data_exclusao IS NULL "										);
		sbSQL.append(" 		  AND data_inativacao IS NULL "										);
		sbSQL.append(" 		  AND placa IS NULL "												);


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoAlertaOcorrencia.toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = true; }
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (PossuiCadastroAtivoPorTipo):: ", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return retorno;
	}
	
	public static boolean PossuiCadastroAtivo(UUID idTipoAlertaOcorrencia, String placa) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT 1 "																);
		sbSQL.append(" FROM   muralha.cad_veiculo_monitorado "									);
		sbSQL.append(" WHERE  id_tipo_alerta_ocorrencia = ? "									);
		sbSQL.append(" 		  AND placa = ? "													);
		sbSQL.append(" 		  AND (data_fim IS NULL OR data_fim >= CAST(GETDATE() AS DATE)) "	);
		sbSQL.append(" 		  AND data_exclusao IS NULL "										);
		sbSQL.append(" 		  AND data_inativacao IS NULL "										);


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoAlertaOcorrencia.toString());
			ps.setString(2, placa);
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = true; }
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (PossuiCadastroAtivoPorTipo):: ", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return retorno;
	}
	
	public static boolean PossuiAlertaPendente(UUID id) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT TOP 1 1 AS possui_alerta_pendente "	);
		sbSQL.append(" FROM   muralha.alerta a "					);
		sbSQL.append(" WHERE  a.id_cad_veiculo_monitorado = ? "		);
		sbSQL.append(" 		  AND a.id_status_alerta = ? "			);


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, id.toString());
			ps.setString(2, StatusAlerta.PENDENTE.GetID().toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = true; }
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (PossuiAlertaPendente):: ", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return retorno;
	}
	
	public static boolean CadastroAtivo(UUID idCadMonitorado) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT 1 ");
		sbSQL.append(" FROM   muralha.cad_veiculo_monitorado ");
		sbSQL.append(" WHERE  id = ? ");
		sbSQL.append(" 		  AND (data_fim IS NULL OR data_fim >= CAST(GETDATE() AS DATE)) ");
		sbSQL.append(" 		  AND data_exclusao IS NULL ");
		sbSQL.append(" 		  AND data_inativacao IS NULL ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idCadMonitorado.toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = true; }
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (CadastroAtivo):: ", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return retorno;
	}
	
	public static boolean InserirVeiculoMonitorado(VeiculoMonitorado veiculoMonitorado) throws ConexaoException, SQLException {
		String sql = "INSERT INTO muralha.cad_veiculo_monitorado " +
		        "(placa, id_tipo_alerta_ocorrencia, descricao, data_inicio, data_fim, id_usuario, nome, privado, supervisionado, id_registro_fato, id_usuario_responsavel, monitorar_somente_este, erros_permitidos_placa, erros_permitido_ini, erros_permitido_fim, " +
		        "id_classe, id_cor, id_marca, id_modelo, texto_adesivo) " +
		        "OUTPUT INSERTED.id " +
		        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		String msgErro = "";
		UUID idMonitorado = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);

			// Param 1 - Placa
			if (veiculoMonitorado.getPlaca() != null && !veiculoMonitorado.getPlaca().isEmpty())
				ps.setString(1, veiculoMonitorado.getPlaca());
			else
				ps.setNull(1, java.sql.Types.VARCHAR);

			// Param 2 - ID tipo alerta
			ps.setString(2, veiculoMonitorado.getIdTipoAlertaOcorrencia().toString());

			// Param 3 - Descrição
			if (veiculoMonitorado.getDescricao() != null && !veiculoMonitorado.getDescricao().isEmpty())
				ps.setString(3, veiculoMonitorado.getDescricao());
			else
				ps.setNull(3, java.sql.Types.VARCHAR);

			// Param 4 - Data início
			ps.setDate(4, new java.sql.Date(veiculoMonitorado.getDataInicio().getTime()));

			// Param 5 - Data fim
			if (veiculoMonitorado.getDataFim() != null)
				ps.setTimestamp(5, new java.sql.Timestamp(veiculoMonitorado.getDataFim().getTime()));
			else
				ps.setNull(5, java.sql.Types.TIMESTAMP);

			// Param 6 - ID usuário
			ps.setInt(6, veiculoMonitorado.getIdUsuario());

			// Param 7 - Nome
			if (veiculoMonitorado.getNome() != null)
				ps.setString(7, veiculoMonitorado.getNome());
			else
				ps.setNull(7, java.sql.Types.VARCHAR);

			// Param 8 - Privado
			ps.setBoolean(8, veiculoMonitorado.isPrivado());

			// Param 9 - Supervisionado
			ps.setBoolean(9, veiculoMonitorado.isSupervisionado());

			// Param 10 - IdRegistroFato
			if (veiculoMonitorado.getIdRegistroFato() != null)
				ps.setLong(10, veiculoMonitorado.getIdRegistroFato());
			else
				ps.setNull(10, java.sql.Types.BIGINT);

			// Param 11 - Id_usuario_responsavel
			if (veiculoMonitorado.getId_usuario_responsavel() != null && veiculoMonitorado.getId_usuario_responsavel() != 0)
				ps.setInt(11, veiculoMonitorado.getId_usuario_responsavel());
			else
				ps.setNull(11, java.sql.Types.INTEGER);

			// Param 12 - Monitorar Somente Este
			if (veiculoMonitorado.getMonitorarSomenteEste() != null)
				ps.setInt(12, veiculoMonitorado.getMonitorarSomenteEste());
			else
				ps.setNull(12, java.sql.Types.INTEGER);

			// Param 13 - Erros permitidos na placa
			if (veiculoMonitorado.getErrosPermitidosPlaca() != null)
				ps.setInt(13, veiculoMonitorado.getErrosPermitidosPlaca());
			else
				ps.setNull(13, java.sql.Types.INTEGER);

			// Param 14 - Erros Permitidos Inicialização
			if (veiculoMonitorado.getErrosPermitidosIni() != null
					&& !veiculoMonitorado.getErrosPermitidosIni().isEmpty()
					&& !"null".equalsIgnoreCase(veiculoMonitorado.getErrosPermitidosIni())) {
				ps.setString(14, veiculoMonitorado.getErrosPermitidosIni());
			}
			else {
				ps.setNull(14, java.sql.Types.VARCHAR);
			}

			// Param 15 - Erros Permitidos Finalização
			if (veiculoMonitorado.getErrosPermitidosFim() != null
					&& !veiculoMonitorado.getErrosPermitidosFim().isEmpty()
					&& !"null".equalsIgnoreCase(veiculoMonitorado.getErrosPermitidosFim())) {
				ps.setString(15, veiculoMonitorado.getErrosPermitidosFim());
			}
			else {
				ps.setNull(15, java.sql.Types.VARCHAR);
			}
			
			// Param 16 - Classe
			if (veiculoMonitorado.getIdClasse() != null
			        && !"0".equals(veiculoMonitorado.getIdClasse())) {

			    ps.setString(16, veiculoMonitorado.getIdClasse());

			} else {

			    ps.setNull(16, java.sql.Types.CHAR);
			}

			// Param 17 - Cor
			if (veiculoMonitorado.getIdCor() != null
			        && veiculoMonitorado.getIdCor() != 0) {

			    ps.setInt(17, veiculoMonitorado.getIdCor());

			} else {

			    ps.setNull(17, java.sql.Types.INTEGER);

			}


			// Param 18 - Marca
			if (veiculoMonitorado.getIdMarca() != null
			        && veiculoMonitorado.getIdMarca() != 0) {

			    ps.setInt(18, veiculoMonitorado.getIdMarca());

			} else {

			    ps.setNull(18, java.sql.Types.INTEGER);

			}


			// Param 19 - Modelo
			if (veiculoMonitorado.getIdModelo() != null
			        && veiculoMonitorado.getIdModelo() != 0) {

			    ps.setInt(19, veiculoMonitorado.getIdModelo());

			} else {

			    ps.setNull(19, java.sql.Types.INTEGER);

			}


			// Param 20 - Texto Adesivo
			if (veiculoMonitorado.getTextoAdesivo() != null
			        && !veiculoMonitorado.getTextoAdesivo().isEmpty()) {

			    ps.setString(20, veiculoMonitorado.getTextoAdesivo());

			} else {

			    ps.setNull(20, java.sql.Types.VARCHAR);

			}

			// Executa o INSERT e obtém o ID gerado
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				idMonitorado = UUID.fromString(rs.getString(1));
				veiculoMonitorado.setId(idMonitorado);
				retorno = true;
			}

			// Insere os grupos relacionados
			if (retorno && idMonitorado != null) {
			Timestamp dataCadastro = new Timestamp(System.currentTimeMillis());
				if (veiculoMonitorado.getGruposPopup() != null) {
					for (String idGrupo : veiculoMonitorado.getGruposPopup()) {
						InserirGrupoDoMonitorado(conn, idMonitorado, Integer.parseInt(idGrupo), veiculoMonitorado.getIdUsuario(),dataCadastro);
					}
				}
				
				// Equipamentos
				if (veiculoMonitorado.getEquipamentosLocais() != null) {
					for (Integer idLocal : veiculoMonitorado.getEquipamentosLocais()) {
						InserirEquipamentoMonitorado(conn, idMonitorado, idLocal, veiculoMonitorado.getIdUsuario(), dataCadastro);
					}
				}

				// Horários Permitidos
				if (veiculoMonitorado.getHorariosPermitidos() != null) {
					for (HorarioPermitido h : veiculoMonitorado.getHorariosPermitidos()) {
						InserirHorarioPermitido(conn, idMonitorado, h, veiculoMonitorado.getIdUsuario(), dataCadastro);
					}
				}
			}

		} catch (Exception e) {
			msgErro = "Erro ao cadastrar veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
			}
		}

		return retorno;
	}

	private static void InserirGrupoDoMonitorado(Connection conn, UUID idMonitorado, int idGrupo, int idUsuario, Timestamp dataCadastro) throws SQLException {
		String sql = "INSERT INTO muralha.cad_veiculo_monitorado_grupo " +
						"(id_cad_veiculo_monitorado, id_grupo, id_usuario, data_cadastro) " +
						"VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());
			ps.setInt(2, idGrupo);
			ps.setInt(3, idUsuario);
			ps.setTimestamp(4, dataCadastro);
			ps.executeUpdate();
		}
	}
	
	private static void InserirEquipamentoMonitorado(Connection conn, UUID idMonitorado, int idLocal, int idUsuario, Timestamp dataCadastro) throws SQLException {
		String sql = "INSERT INTO muralha.cad_veiculo_monitorado_equipamento " +
						"(id, id_cad_veiculo_monitorado, id_local, data_cadastro, id_usuario) " +
						"VALUES (NEWID(), ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());
			ps.setInt(2, idLocal);
			ps.setTimestamp(3, dataCadastro);
			ps.setInt(4, idUsuario);
			ps.executeUpdate();
		}
	}

	private static void InserirHorarioPermitido(Connection conn, UUID idMonitorado, HorarioPermitido h, int idUsuario, Timestamp dataCadastro) throws SQLException {
		String sql = "INSERT INTO muralha.cad_veiculo_monitorado_periodo " +
						"(id, id_cad_veiculo_monitorado, dia_semana, hora_inicio, hora_fim, data_cadastro, id_usuario) " +
						"VALUES (NEWID(), ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());
			ps.setInt(2, Integer.parseInt(h.getDia()));
			ps.setString(3, h.getHoraInicio());
			ps.setString(4, h.getHoraFim());
			ps.setTimestamp(5, dataCadastro);
			ps.setInt(6, idUsuario);
			ps.executeUpdate();
		}
	}
	
	public static boolean AtualizarVeiculoMonitorado(VeiculoMonitorado veiculoMonitorado) 
			throws ConexaoException, SQLException {
		
		String sqlUpdate = "UPDATE muralha.cad_veiculo_monitorado SET data_fim = ?, descricao = ?, data_atualizacao = ?, " +
		        "id_usuario_atualizacao = ?, nome = ?, data_inativacao = ?, id_usuario_inativacao = ?, privado = ?, " +
		        "supervisionado = ?, id_usuario_responsavel = ?, monitorar_somente_este = ?, erros_permitidos_placa = ?, " +
		        "erros_permitido_ini = ?, erros_permitido_fim = ?, id_classe = ?, id_cor = ?, id_marca = ?, " +
		        "id_modelo = ?, texto_adesivo = ? WHERE id = ?";
		
		String sqlDeleteGrupos = "DELETE FROM muralha.cad_veiculo_monitorado_grupo WHERE id_cad_veiculo_monitorado = ?";
		String sqlDeleteEquipamentos = "DELETE FROM muralha.cad_veiculo_monitorado_equipamento WHERE id_cad_veiculo_monitorado = ?";
		String sqlDeletePeriodos = "DELETE FROM muralha.cad_veiculo_monitorado_periodo WHERE id_cad_veiculo_monitorado = ?";
		
		Connection conn = null;
		PreparedStatement psUpdate = null;
		PreparedStatement psDeleteGrupos = null;
		boolean retorno = false;
		
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			
			// 1. Gerar JSON completo com os dados do monitorado atualizado e gravar no histórico
			String jsonCompleto = montarDadosMonitoradoJson(conn, veiculoMonitorado.getId());
			inserirHistoricoCompleto(conn, veiculoMonitorado.getId(), veiculoMonitorado.getIdUsuario(), jsonCompleto);
			
			// 2. Apagar oos dados das tabelas secundarias
			psDeleteGrupos = conn.prepareStatement(sqlDeleteGrupos);
			psDeleteGrupos.setString(1, veiculoMonitorado.getId().toString());
			psDeleteGrupos.executeUpdate();
			
			PreparedStatement psDeleteEquipamentos = conn.prepareStatement(sqlDeleteEquipamentos);
			psDeleteEquipamentos.setString(1, veiculoMonitorado.getId().toString());
			psDeleteEquipamentos.executeUpdate();
			
			PreparedStatement psDeletePeriodos = conn.prepareStatement(sqlDeletePeriodos);
			psDeletePeriodos.setString(1, veiculoMonitorado.getId().toString());
			psDeletePeriodos.executeUpdate();
			
			// 3. Inserir novos grupos (email + sms) na tabela principal
			Timestamp dataCadastro = new Timestamp(System.currentTimeMillis());
			
			if (veiculoMonitorado.getGruposPopup() != null) {
				for (String idGrupo : veiculoMonitorado.getGruposPopup()) {
					InserirGrupoDoMonitorado(conn, veiculoMonitorado.getId(), Integer.parseInt(idGrupo), veiculoMonitorado.getIdUsuario(), dataCadastro);
				}
			}
			
			// Equipamentos
			if (veiculoMonitorado.getEquipamentosLocais() != null) {
				for (Integer idLocal : veiculoMonitorado.getEquipamentosLocais()) {
					InserirEquipamentoMonitorado(conn, veiculoMonitorado.getId(), idLocal, veiculoMonitorado.getIdUsuario(), dataCadastro);
				}
			}

	        // Horários Permitidos
			if (veiculoMonitorado.getHorariosPermitidos() != null) {
				for (HorarioPermitido h : veiculoMonitorado.getHorariosPermitidos()) {
						InserirHorarioPermitido(conn, veiculoMonitorado.getId(), h, veiculoMonitorado.getIdUsuario(), dataCadastro);
				}
			}
			
			// 4. Atualizar o veículo monitorado (dados principais)
			psUpdate = conn.prepareStatement(sqlUpdate);
			
			if (veiculoMonitorado.getDataFim() != null)
				psUpdate.setTimestamp(1, new java.sql.Timestamp(veiculoMonitorado.getDataFim().getTime()));
			else
				psUpdate.setNull(1, java.sql.Types.TIMESTAMP);
			
			psUpdate.setString(2, veiculoMonitorado.getDescricao());
			psUpdate.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
			psUpdate.setInt(4, veiculoMonitorado.getIdUsuario());
			psUpdate.setString(5, veiculoMonitorado.getNome());
			
			if (veiculoMonitorado.getDataInativacao() != null)
				psUpdate.setTimestamp(6, new java.sql.Timestamp(veiculoMonitorado.getDataInativacao().getTime()));
			else
				psUpdate.setNull(6, java.sql.Types.TIMESTAMP);
			
			if (veiculoMonitorado.getIdUsuarioInativacao() != null && veiculoMonitorado.getIdUsuarioInativacao() != 0)
				psUpdate.setInt(7, veiculoMonitorado.getIdUsuarioInativacao());
			else
				psUpdate.setNull(7, java.sql.Types.INTEGER);
			
			psUpdate.setBoolean(8, veiculoMonitorado.isPrivado());
			psUpdate.setBoolean(9, veiculoMonitorado.isSupervisionado());	        
			
			if (veiculoMonitorado.getId_usuario_responsavel() != null && veiculoMonitorado.getId_usuario_responsavel() != 0)
				psUpdate.setInt(10, veiculoMonitorado.getId_usuario_responsavel());
			else
				psUpdate.setNull(10, java.sql.Types.INTEGER);

			if (veiculoMonitorado.getMonitorarSomenteEste() != null)
				psUpdate.setInt(11, veiculoMonitorado.getMonitorarSomenteEste());
			else
				psUpdate.setNull(11, java.sql.Types.INTEGER);

			// Param 12 - Erros Permitidos Placa
			if (veiculoMonitorado.getErrosPermitidosPlaca() != null)
				psUpdate.setInt(12, veiculoMonitorado.getErrosPermitidosPlaca());
			else
				psUpdate.setNull(12, java.sql.Types.INTEGER);

			// Param 13 - Erros Permitidos Inicialização
			if(veiculoMonitorado.getErrosPermitidosIni() != null)
				psUpdate.setString(13, veiculoMonitorado.getErrosPermitidosIni());
			else
				psUpdate.setNull(13, java.sql.Types.VARCHAR);

			// Param 14 - Erros Permitidos Finalização
			if(veiculoMonitorado.getErrosPermitidosFim() != null)
				psUpdate.setString(14, veiculoMonitorado.getErrosPermitidosFim());
			else
				psUpdate.setNull(14, java.sql.Types.VARCHAR);
			
			// Param 15 - Classe
			if (veiculoMonitorado.getIdClasse() != null
			        && !"0".equals(veiculoMonitorado.getIdClasse())) {
			    psUpdate.setString(15, veiculoMonitorado.getIdClasse());
			} else {
			    psUpdate.setNull(15, java.sql.Types.CHAR);
			}

			// Param 16 - Cor
			if (veiculoMonitorado.getIdCor() != null
			        && veiculoMonitorado.getIdCor() != 0) {
			    psUpdate.setInt(16, veiculoMonitorado.getIdCor());
			} else {
			    psUpdate.setNull(16, java.sql.Types.INTEGER);
			}

			// Param 17 - Marca
			if (veiculoMonitorado.getIdMarca() != null
			        && veiculoMonitorado.getIdMarca() != 0) {
			    psUpdate.setInt(17, veiculoMonitorado.getIdMarca());
			} else {
			    psUpdate.setNull(17, java.sql.Types.INTEGER);
			}

			// Param 18 - Modelo
			if (veiculoMonitorado.getIdModelo() != null
			        && veiculoMonitorado.getIdModelo() != 0) {
			    psUpdate.setInt(18, veiculoMonitorado.getIdModelo());
			} else {
			    psUpdate.setNull(18, java.sql.Types.INTEGER);
			}

			// Param 19 - Texto Adesivo
			if (veiculoMonitorado.getTextoAdesivo() != null
			        && !veiculoMonitorado.getTextoAdesivo().trim().isEmpty()) {
			    psUpdate.setString(19, veiculoMonitorado.getTextoAdesivo().trim());
			} else {
			    psUpdate.setNull(19, java.sql.Types.VARCHAR);
			}

			// Param 20 - ID do veículo monitorado
			psUpdate.setString(20, veiculoMonitorado.getId().toString());
			
			retorno = (psUpdate.executeUpdate() == 1);
			conn.commit();
		} catch (Exception e) {
			if (conn != null) conn.rollback();
			logger.error("Erro ao atualizar veículo monitorado: " + e.getMessage(), e);
			retorno = false;
		} finally {
			try {
				if (psDeleteGrupos != null) psDeleteGrupos.close();
				if (psUpdate != null) psUpdate.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
			}
		}
		return retorno;
	}
	
	
	
	public static String montarDadosMonitoradoJson(Connection conn, UUID idMonitorado) throws SQLException, IOException {
		VeiculoMonitoradoCompletoDTO dto = new VeiculoMonitoradoCompletoDTO();

		dto.setVeiculo(buscarVeiculo(conn, idMonitorado));
		dto.setGrupos(buscarGrupos(conn, idMonitorado));
		dto.setEquipamentos(buscarEquipamentos(conn, idMonitorado));
		dto.setPeriodos(buscarPeriodos(conn, idMonitorado));

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // opcional: força formatação como string legível
		return mapper.writeValueAsString(dto);
	}
	
	private static void inserirHistoricoCompleto(Connection conn, UUID idMonitorado, int idUsuarioResponsavel, String dadosJson) throws SQLException {
		String sql = "INSERT INTO muralha.cad_veiculo_monitorado_historico " +
						"(id, id_cad_veiculo_monitorado, id_usuario_responsavel, data_acao, dados_json) " +
						"VALUES (?, ?, ?, GETDATE(), ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, UUID.randomUUID().toString());
			ps.setString(2, idMonitorado.toString());
			ps.setInt(3, idUsuarioResponsavel);
			ps.setString(4, dadosJson);
			ps.executeUpdate();
		}
	}
	
	private static VeiculoMonitoradoEntidade buscarVeiculo(Connection conn, UUID idMonitorado) throws SQLException {
		String sql = "SELECT * FROM muralha.cad_veiculo_monitorado WHERE id = ?";
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());
			
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					VeiculoMonitoradoEntidade veiculo = new VeiculoMonitoradoEntidade();
					veiculo.setId(UUID.fromString(rs.getString("id")));
					veiculo.setPlaca(rs.getString("placa"));
					veiculo.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
					veiculo.setDescricao(rs.getString("descricao"));
					veiculo.setDataInicio(rs.getDate("data_inicio"));
					veiculo.setDataFim(rs.getDate("data_fim"));
					veiculo.setDataCadastro(rs.getTimestamp("data_cadastro"));
					veiculo.setIdUsuario(rs.getInt("id_usuario"));
					veiculo.setDataExclusao(rs.getTimestamp("data_exclusao"));
					veiculo.setIdUsuarioExclusao((Integer) rs.getObject("id_usuario_exclusao"));
					veiculo.setMotivoExclusao(rs.getString("motivo_exclusao"));
					veiculo.setIdUsuarioAtualizacao((Integer) rs.getObject("id_usuario_atualizacao"));
					veiculo.setDataAtualizacao(rs.getTimestamp("data_atualizacao"));
					veiculo.setNome(rs.getString("nome"));
					veiculo.setDataInativacao(rs.getTimestamp("data_inativacao"));
					veiculo.setIdUsuarioInativacao((Integer) rs.getObject("id_usuario_inativacao"));
					veiculo.setPrivado(rs.getBoolean("privado"));
					veiculo.setSupervisionado(rs.getBoolean("supervisionado"));
					veiculo.setErrosPermitidosPlaca((Integer) rs.getObject("erros_permitidos_placa"));
					veiculo.setErrosPermitidosIni(rs.getString("erros_permitido_ini"));
					veiculo.setErrosPermitidosFim(rs.getString("erros_permitido_fim"));
					return veiculo;
				}
			}
		}
		return null;
	}
	
	private static List<VeiculoMonitoradoGrupoEntidade> buscarGrupos(Connection conn, UUID idMonitorado) throws SQLException {
		List<VeiculoMonitoradoGrupoEntidade> grupos = new ArrayList<>();

		String sql = "SELECT * FROM muralha.cad_veiculo_monitorado_grupo WHERE id_cad_veiculo_monitorado = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					VeiculoMonitoradoGrupoEntidade grupo = new VeiculoMonitoradoGrupoEntidade();
					grupo.setId(UUID.fromString(rs.getString("id")));
					grupo.setIdCadVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
					grupo.setIdGrupo(rs.getInt("id_grupo"));
					grupo.setDataCadastro(rs.getTimestamp("data_cadastro"));
					grupo.setIdUsuario(rs.getInt("id_usuario"));
					grupos.add(grupo);
				}
			}
		}

		return grupos; // lista vazia se não encontrar nada
	}
	
	private static List<VeiculoMonitoradoEquipamentoEntidade> buscarEquipamentos(Connection conn, UUID idMonitorado) throws SQLException {
		List<VeiculoMonitoradoEquipamentoEntidade> equipamentos = new ArrayList<>();

		String sql = "SELECT * FROM muralha.cad_veiculo_monitorado_equipamento WHERE id_cad_veiculo_monitorado = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					VeiculoMonitoradoEquipamentoEntidade equipamento = new VeiculoMonitoradoEquipamentoEntidade();
					equipamento.setId(UUID.fromString(rs.getString("id")));
					equipamento.setIdCadVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
					equipamento.setIdLocal(rs.getInt("id_local"));
					equipamento.setDataCadastro(rs.getTimestamp("data_cadastro"));
					equipamento.setIdUsuario(rs.getInt("id_usuario"));
					equipamentos.add(equipamento);
				}
			}
		}

		return equipamentos; // lista vazia se não encontrar nada
	}
	
	private static List<VeiculoMonitoradoPeriodoEntidade> buscarPeriodos(Connection conn, UUID idMonitorado) throws SQLException {
		List<VeiculoMonitoradoPeriodoEntidade> periodos = new ArrayList<>();

		String sql = "SELECT * FROM muralha.cad_veiculo_monitorado_periodo WHERE id_cad_veiculo_monitorado = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, idMonitorado.toString());

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					VeiculoMonitoradoPeriodoEntidade periodo = new VeiculoMonitoradoPeriodoEntidade();
					periodo.setId(UUID.fromString(rs.getString("id")));
					periodo.setIdCadVeiculoMonitorado(UUID.fromString(rs.getString("id_cad_veiculo_monitorado")));
					periodo.setDiaSemana(rs.getInt("dia_semana"));

					// Como o tipo SQL TIME, usamos getTime e convertemos para java.time.LocalTime
					Time horaInicio = rs.getTime("hora_inicio");
					if (horaInicio != null) {
						periodo.setHoraInicio(horaInicio.toLocalTime());
					}

					Time horaFim = rs.getTime("hora_fim");
					if (horaFim != null) {
						periodo.setHoraFim(horaFim.toLocalTime());
					}

					periodo.setDataCadastro(rs.getTimestamp("data_cadastro"));
					periodo.setIdUsuario(rs.getInt("id_usuario"));

					periodos.add(periodo);
				}
			}
		}
		return periodos; // lista vazia se não encontrar nada
	}
	
	public static boolean EncerrarVeiculoMonitorado(VeiculoMonitorado veiculoMonitorado) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		String msgErro = "";
		
		try {
			sbSQL.append(" UPDATE muralha.cad_veiculo_monitorado SET data_fim = ?, data_atualizacao = ?, id_usuario_atualizacao = ?, data_inativacao = ?, id_usuario_inativacao = ? WHERE id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			if (veiculoMonitorado.getDataFim() != null)
				ps.setTimestamp(1, new java.sql.Timestamp(veiculoMonitorado.getDataFim().getTime()));
			else
				ps.setNull(1, java.sql.Types.TIMESTAMP);
			
			ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ps.setInt(3, veiculoMonitorado.getIdUsuario());
			
			if (veiculoMonitorado.getDataInativacao() != null)
				ps.setTimestamp(4, new java.sql.Timestamp(veiculoMonitorado.getDataInativacao().getTime()));
			else
				ps.setNull(4, java.sql.Types.TIMESTAMP);
			
			if (veiculoMonitorado.getIdUsuarioInativacao() != null && veiculoMonitorado.getIdUsuarioInativacao() != 0)
				ps.setInt(5, veiculoMonitorado.getIdUsuarioInativacao());
			else
				ps.setInt(5, java.sql.Types.INTEGER);
			
			
			ps.setString(6, veiculoMonitorado.getId().toString());
			
			retorno = (ps.executeUpdate() == 1);
				
		}
		catch(Exception e) {
			msgErro = "Erro ao encerrar veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		return retorno;
	}
	
	public static boolean VerificaAcessoPorCadVeiMoniUsuarioId(UUID idCadVeiculoMonitorado, Integer idUsuario) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean retorno = false;

		try {
			sbSQL.append("SELECT privado, id_usuario ");
			sbSQL.append("FROM muralha.cad_veiculo_monitorado ");
			sbSQL.append("WHERE id = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, idCadVeiculoMonitorado.toString());

			rs = ps.executeQuery();
			if (rs.next()) {
				boolean privado = rs.getBoolean("privado");
				int usuarioVeiculo = rs.getInt("id_usuario");

				retorno = !privado || (usuarioVeiculo == idUsuario);
			}
		} catch (Exception e) {
			String msgErro = "Erro ao verificar acesso ao veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return retorno;
	}
	
	public static VeiculosMonitorados ObterListaVeiculosMonitoradosPorIds(List<UUID> ids, Integer idUsuario, Paginacao paginacao) throws ConexaoException, SQLException {
		VeiculosMonitorados retorno = new VeiculosMonitorados();
		List<VeiculoMonitorado> listaRet = new ArrayList<>();
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;

		try {
			sbSQL.append("SELECT cvm.id, ");
			sbSQL.append("       cvm.nome, ");
			sbSQL.append("       cvm.placa, ");
			sbSQL.append("       tao.id AS id_tipo_alerta_ocorrencia, ");
			sbSQL.append("       RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, ");
			sbSQL.append("       RTRIM(cvm.descricao) AS descricao, ");
			sbSQL.append("       cvm.privado, ");
			sbSQL.append("       cvm.data_inicio, ");
			sbSQL.append("       cvm.data_fim, ");
			sbSQL.append("       cvm.data_cadastro, ");
			sbSQL.append("       su.id_usuario, ");
			sbSQL.append("       RTRIM(su.usuario) AS usuario, ");
			sbSQL.append("       RTRIM(su.nome) AS nome_usuario, ");
			sbSQL.append("       su_exc.id_usuario AS id_usuario_exclusao, ");
			sbSQL.append("       RTRIM(su_exc.usuario) AS usuario_exclusao, ");
			sbSQL.append("       RTRIM(su_exc.nome) AS nome_usuario_exclusao, ");
			sbSQL.append("       cvm.data_exclusao, ");
			sbSQL.append("       RTRIM(cvm.motivo_exclusao) AS motivo_exclusao, ");
			sbSQL.append("       su_ina.id_usuario AS id_usuario_inativacao, ");
			sbSQL.append("       RTRIM(su_ina.usuario) AS usuario_inativacao, ");
			sbSQL.append("       RTRIM(su_ina.nome) AS nome_usuario_inativacao, ");
			sbSQL.append("       cvm.data_inativacao, ");
			sbSQL.append("       CASE WHEN (cvm.data_fim IS NOT NULL AND CAST(cvm.data_fim AS DATE) < CAST(GETDATE() AS DATE)) OR cvm.data_inativacao IS NOT NULL THEN 0 ELSE 1 END AS permite_edicao, ");
			sbSQL.append("       CASE WHEN a.id_cad_veiculo_monitorado IS NOT NULL THEN 1 ELSE 0 END AS possui_alerta, ");
			sbSQL.append("       cvm.supervisionado, ");
			sbSQL.append(paginacao.QueryTotalRegistros());

			sbSQL.append(" FROM muralha.cad_veiculo_monitorado cvm ");
			sbSQL.append(" INNER JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = cvm.id_tipo_alerta_ocorrencia ");
			sbSQL.append(" LEFT JOIN sis_usuario su ON su.id_usuario = cvm.id_usuario ");
			sbSQL.append(" LEFT JOIN sis_usuario su_exc ON su_exc.id_usuario = cvm.id_usuario_exclusao ");
			sbSQL.append(" LEFT JOIN sis_usuario su_ina ON su_ina.id_usuario = cvm.id_usuario_inativacao ");
			sbSQL.append(" LEFT JOIN (SELECT id_cad_veiculo_monitorado FROM muralha.alerta GROUP BY id_cad_veiculo_monitorado) AS a ON a.id_cad_veiculo_monitorado = cvm.id ");
			sbSQL.append(" WHERE cvm.data_exclusao IS NULL ");

			if (ids != null && !ids.isEmpty()) {
				String inClause = ids.stream().map(x -> "?").collect(Collectors.joining(","));
				sbSQL.append(" AND cvm.id IN (" + inClause + ") ");
			}

			sbSQL.append(" AND (cvm.privado = 0 OR (cvm.privado = 1 AND cvm.id_usuario = ?)) ");

			sbSQL.append(" ORDER BY cvm.data_inicio DESC, cvm.data_cadastro DESC ");
			sbSQL.append(paginacao.QueryPaginacao());

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			int paramIndex = 1;
			if (ids != null && !ids.isEmpty()) {
				for (UUID id : ids) {
					ps.setString(paramIndex++, id.toString());
				}
			}

			ps.setInt(paramIndex++, idUsuario);

			rs = ps.executeQuery();
			int registros = 0;

			while (rs.next()) {
				if (registros == 0)
					registros = rs.getInt("total_registros");

				VeiculoMonitorado item = new VeiculoMonitorado();
				item.setId(UUID.fromString(rs.getString("id")));
				item.setNome(rs.getString("nome"));
				item.setPlaca(rs.getString("placa"));
				item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				item.setDescricao(rs.getString("descricao"));
				item.setDataInicio(rs.getDate("data_inicio"));
				item.setDataFim(rs.getDate("data_fim"));
				item.setDataCadastro(rs.getTimestamp("data_cadastro"));
				item.setIdUsuario(rs.getInt("id_usuario"));
				item.setUsuario(rs.getString("usuario"));
				item.setNomeUsuario(rs.getString("nome_usuario"));
				item.setIdUsuarioExclusao(rs.getInt("id_usuario_exclusao"));
				item.setUsuarioExclusao(rs.getString("usuario_exclusao"));
				item.setNomeUsuarioExclusao(rs.getString("nome_usuario_exclusao"));
				item.setDataExclusao(rs.getTimestamp("data_exclusao"));
				item.setIdUsuarioInativacao(rs.getInt("id_usuario_inativacao"));
				item.setUsuarioInativacao(rs.getString("usuario_inativacao"));
				item.setNomeUsuarioInativacao(rs.getString("nome_usuario_inativacao"));
				item.setDataInativacao(rs.getTimestamp("data_inativacao"));
				item.setEditavel(rs.getBoolean("permite_edicao"));
				item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
				item.setSupervisionado(rs.getBoolean("supervisionado"));

				listaRet.add(item);
			}

			paginacao.TotalRegistros(registros);
			retorno.setPaginacao(paginacao);
			retorno.setListaVeiculosMonitorados(listaRet);
		}
		catch (Exception e) {
			logger.error("Erro ao obter veículos monitorados por IDs: " + e.getMessage(), e);
			throw new SQLException("Erro ao consultar os veículos monitorados por IDs!", e);
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão: " + e.getMessage(), e);
			}
		}
		return retorno;
	}
	
	public static void desvincularRegistro(String placa) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			sbSQL.append("UPDATE muralha.cad_veiculo_monitorado ");
			sbSQL.append("SET id_registro_fato = NULL ");
			sbSQL.append("WHERE placa = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, placa);

			ps.executeUpdate();
		} catch (Exception e) {
			String msgErro = "Erro ao desvincular veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
			}
		}
	}

	
	public static boolean existePorPlacaERegistro(String placa, Long idRegistro) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean retorno = false;

		try {
			sbSQL.append("SELECT COUNT(*) AS total ");
			sbSQL.append("FROM muralha.cad_veiculo_monitorado ");
			sbSQL.append("WHERE placa = ? AND id_registro_fato = ?");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, placa);
			ps.setLong(2, idRegistro);

			rs = ps.executeQuery();
			if (rs.next()) {
				retorno = rs.getInt("total") > 0;
			}
		} catch (Exception e) {
			String msgErro = "Erro ao verificar se veículo monitorado existe para o registro!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return retorno;
	}

	public static UUID InserirVeiculoMonitoradoRetornaId(VeiculoMonitorado veiculoMonitorado, Integer idUsuarioLogado)
			throws ConexaoException, SQLException {

		String sql = "INSERT INTO muralha.cad_veiculo_monitorado " +
				"(placa, id_tipo_alerta_ocorrencia, descricao, data_inicio, data_fim, id_usuario, nome, privado, supervisionado, id_registro_fato, erros_permitidos_placa, erros_permitido_ini, erros_permitido_fim) " +
				"OUTPUT INSERTED.id " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement ps = null;
		UUID idMonitorado = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);

			// Param 1 - Placa
			if (veiculoMonitorado.getPlaca() != null && !veiculoMonitorado.getPlaca().isEmpty())
				ps.setString(1, veiculoMonitorado.getPlaca());
			else
				ps.setNull(1, java.sql.Types.VARCHAR);

			// Param 2 - ID tipo alerta
			ps.setString(2, veiculoMonitorado.getIdTipoAlertaOcorrencia().toString());

			// Param 3 - Descrição
			if (veiculoMonitorado.getDescricao() != null && !veiculoMonitorado.getDescricao().isEmpty())
				ps.setString(3, veiculoMonitorado.getDescricao());
			else
				ps.setNull(3, java.sql.Types.VARCHAR);

			// Param 4 - Data início
			ps.setDate(4, new java.sql.Date(veiculoMonitorado.getDataInicio().getTime()));

			// Param 5 - Data fim
			if (veiculoMonitorado.getDataFim() != null)
				ps.setTimestamp(5, new java.sql.Timestamp(veiculoMonitorado.getDataFim().getTime()));
			else
				ps.setNull(5, java.sql.Types.TIMESTAMP);

			// Param 6 - ID usuário
			if(veiculoMonitorado.getIdUsuario() != null) {
				ps.setInt(6, veiculoMonitorado.getIdUsuario());
			}else {
				ps.setInt(6, idUsuarioLogado);
			}
			
			// Param 7 - Nome
			if (veiculoMonitorado.getNome() != null)
				ps.setString(7, veiculoMonitorado.getNome());
			else
				ps.setNull(7, java.sql.Types.VARCHAR);

			// Param 8 - Privado
			ps.setBoolean(8, veiculoMonitorado.isPrivado());

			// Param 9 - Supervisionado
			ps.setBoolean(9, veiculoMonitorado.isSupervisionado());

			// Param 10 - IdRegistroFato
			if (veiculoMonitorado.getIdRegistroFato() != null)
				ps.setLong(10, veiculoMonitorado.getIdRegistroFato());
			else
				ps.setNull(10, java.sql.Types.BIGINT);

			// Param 11 - Erros permitidos na placa
			if (veiculoMonitorado.getErrosPermitidosPlaca() != null)
				ps.setInt(11, veiculoMonitorado.getErrosPermitidosPlaca());
			else
				ps.setNull(11, java.sql.Types.INTEGER);

			// Param 12 - Erros Permitidos Início
			if(veiculoMonitorado.getErrosPermitidosIni() != null)
				ps.setString(12, veiculoMonitorado.getErrosPermitidosIni());
			else
				ps.setNull(12, java.sql.Types.VARCHAR);
			
			// Param 13 - Erros Permitidos Fim
			if(veiculoMonitorado.getErrosPermitidosFim() != null)
				ps.setString(13, veiculoMonitorado.getErrosPermitidosFim());
			else
				ps.setNull(13, java.sql.Types.VARCHAR);
			
			// Executa o INSERT e obtém o ID gerado
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				idMonitorado = UUID.fromString(rs.getString(1));
				veiculoMonitorado.setId(idMonitorado);
			}

			// Insere os grupos relacionados
			if (idMonitorado != null) {
				Timestamp dataCadastro = new Timestamp(System.currentTimeMillis());

				if (veiculoMonitorado.getGruposPopup() != null) {
					for (String idGrupo : veiculoMonitorado.getGruposPopup()) {
						InserirGrupoDoMonitorado(conn, idMonitorado, Integer.parseInt(idGrupo), veiculoMonitorado.getIdUsuario(), dataCadastro);
					}
				}

				if (veiculoMonitorado.getEquipamentosLocais() != null) {
					for (Integer idLocal : veiculoMonitorado.getEquipamentosLocais()) {
						InserirEquipamentoMonitorado(conn, idMonitorado, idLocal, veiculoMonitorado.getIdUsuario(), dataCadastro);
					}
				}

				if (veiculoMonitorado.getHorariosPermitidos() != null) {
					for (HorarioPermitido h : veiculoMonitorado.getHorariosPermitidos()) {
						InserirHorarioPermitido(conn, idMonitorado, h, veiculoMonitorado.getIdUsuario(), dataCadastro);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Erro ao cadastrar veículo monitorado: " + e.getMessage(), e);
		} finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
			}
		}
		return idMonitorado;
	}
	
	public static UUID inserirCadastroVeiculoMonitoradoGrupoRetornoId(VeiculoMonitoradoGrupoEntidade vinculo) throws ConexaoException, SQLException {
		String sql = "INSERT INTO muralha.cad_veiculo_monitorado_grupo " +
						"(id_cad_veiculo_monitorado, id_grupo, id_usuario, data_cadastro) " +
						"OUTPUT INSERTED.id " +
						"VALUES (?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UUID idGerado = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);

			ps.setObject(1, vinculo.getIdCadVeiculoMonitorado()); // UUID direto
			ps.setInt(2, vinculo.getIdGrupo());
			ps.setInt(3, vinculo.getIdUsuario());
			ps.setTimestamp(4, new java.sql.Timestamp(vinculo.getDataCadastro().getTime()));

			rs = ps.executeQuery();
			if (rs.next()) {
				idGerado = UUID.fromString(rs.getString(1));
			}
		} catch (Exception e) {
			String msgErro = "Erro ao inserir grupo do veículo monitorado!";
			logger.error(msgErro + ": " + e.getMessage(), e);
			throw e; // opcional: relançar para ser tratado acima
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados! " + e.getMessage(), e);
			}
		}

		return idGerado;
	}

	public static List<UUID> ObterIdsVeiculosMonitorarSomenteEste(Integer idUsuario) throws ConexaoException, SQLException 
	{
		List<UUID> ids = new ArrayList<>();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			sbSQL.append(" SELECT id ");
			sbSQL.append(" FROM muralha.cad_veiculo_monitorado ");
			sbSQL.append(" WHERE id_usuario = ? ");
			sbSQL.append(" AND monitorar_somente_este = 1 ");
			sbSQL.append(" AND data_exclusao IS NULL ");
			sbSQL.append(" AND (data_fim IS NULL OR data_fim >= CAST(GETDATE() AS DATE)) ");
			sbSQL.append(" AND data_inativacao IS NULL ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idUsuario);
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				ids.add(UUID.fromString(rs.getString("id")));
			}
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter IDs para monitorar somente este: " + e.getMessage(), e);
			throw new SQLException("Erro ao consultar IDs para monitorar somente este!");
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}
			catch(Exception e) {
				logger.error("Erro ao fechar conexão: " + e.getMessage(), e);
			}
		}
		return ids;
	}
	
	public static List<VeiculoMonitorado> buscarPorIdRegistroFato(Long idRegistroFato)
	        throws SQLException, ConexaoException {
	    
	    List<VeiculoMonitorado> lista = new ArrayList<>();

	    String sql = "SELECT id, nome, placa, id_tipo_alerta_ocorrencia, descricao, data_inicio, data_fim, "
	               + "data_cadastro, id_usuario, data_exclusao, id_usuario_exclusao, motivo_exclusao, "
	               + "data_inativacao, id_usuario_inativacao, privado, supervisionado, id_registro_fato, "
	               + "id_usuario_atualizacao, data_atualizacao, monitorar_somente_este, erros_permitidos_placa, "
	               + "erros_permitido_ini, erros_permitido_fim "
	               + "FROM muralha.cad_veiculo_monitorado WHERE id_registro_fato = ? "
	               + "and data_fim is null";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, idRegistroFato);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                VeiculoMonitorado v = new VeiculoMonitorado();

	                String idStr = rs.getString("id");
	                if (idStr != null && !idStr.isEmpty()) {
	                    v.setId(UUID.fromString(idStr));
	                }

	                String idTipoStr = rs.getString("id_tipo_alerta_ocorrencia");
	                if (idTipoStr != null && !idTipoStr.isEmpty()) {
	                    v.setIdTipoAlertaOcorrencia(UUID.fromString(idTipoStr));
	                }

	                v.setNome(rs.getString("nome"));
	                v.setPlaca(rs.getString("placa"));
	                v.setDescricao(rs.getString("descricao"));
	                v.setDataInicio(rs.getDate("data_inicio"));
	                v.setDataFim(rs.getDate("data_fim"));
	                v.setDataCadastro(rs.getTimestamp("data_cadastro"));
	                v.setIdUsuario(rs.getInt("id_usuario"));
	                v.setDataExclusao(rs.getTimestamp("data_exclusao"));
	                v.setIdUsuarioExclusao(rs.getInt("id_usuario_exclusao"));
	                v.setMotivoExclusao(rs.getString("motivo_exclusao"));
	                v.setDataInativacao(rs.getTimestamp("data_inativacao"));
	                v.setIdUsuarioInativacao(rs.getInt("id_usuario_inativacao"));
	                v.setPrivado(rs.getBoolean("privado"));
	                v.setSupervisionado(rs.getBoolean("supervisionado"));
	                v.setIdRegistroFato(rs.getLong("id_registro_fato"));
	                v.setIdUsuarioAtualizacao(rs.getInt("id_usuario_atualizacao"));
	                v.setDataAtualizacao(rs.getTimestamp("data_atualizacao"));
	                v.setMonitorarSomenteEste(rs.getInt("monitorar_somente_este"));
	                v.setErrosPermitidosPlaca(rs.getInt("erros_permitidos_placa"));
	                lista.add(v);
	            }
	        }
	    } catch (SQLException | ConexaoException e) {
	        logger.error("Erro ao buscar veículos por idRegistroFato: ", e);
	        throw e;
	    }

	    return lista;
	}
	
	public static void encerrarMonitoramentoPorPlacas(List<String> placas) throws SQLException, ConexaoException {
	    if (placas == null || placas.isEmpty()) {
	        return;
	    }

	    StringBuilder sql = new StringBuilder("UPDATE muralha.cad_veiculo_monitorado SET data_fim = CURRENT_TIMESTAMP WHERE placa IN (");
	    String placeholders = placas.stream().map(p -> "?").collect(Collectors.joining(", "));
	    sql.append(placeholders).append(")");

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

	        for (int i = 0; i < placas.size(); i++) {
	            ps.setString(i + 1, placas.get(i));
	        }

	        int atualizados = ps.executeUpdate();
	        logger.info("Monitoramento encerrado para " + atualizados + " veículo(s).");

	    } catch (SQLException | ConexaoException e) {
	        logger.error("Erro ao encerrar monitoramento por placas: ", e);
	        throw e;
	    }
	}
}

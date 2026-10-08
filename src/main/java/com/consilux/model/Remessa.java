/**
 * 
 */
package com.consilux.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.AcessoFTP.eTipoArquivo;
import com.consilux.model.MovimentoArquivo.TipoArquivoML;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 * 
 */
public class Remessa {
	protected final static Logger logger = Logger.getLogger(Remessa.class);
	private Integer idRemessa;
	private Integer codigoExterno;
	private Date data;
	private Date dataConfirmacao;
	private Integer totalInfracao;
	private Integer autoInicial;
	private Integer autoFinal;
	private Integer idEnquadramento;
	private Integer idProcesso;
	private String tipo;
	private Date dataExportacao;
	private Date dataValidacao;
	private Date dataInicio;
	private Date dataFinal;
	private Integer id_usuario;
	private Boolean integridade = null;
	private Boolean reprovado = false;
	private String integridadeDescricao;
	private String integridadeDescricaoExt;
	private Integer totalInfracoesValidaveis;
	private Integer revisao;
	private Integer tamanhoAmostra;
	private Integer ac, re;
	private boolean dadosObtidos = false;
	private Long idMovimentoArquivo;
	private Integer idMovimento;
	private Date dataMovimento;
	private Integer idUsuarioJanela;
	private String usuarioJanela;
	private String nomeUsuarioJanela;
	private Long qtdeInfracoesJanela;
	private boolean amostra = false;
	private Date dataAtualizacao;
	
	private static SimpleDateFormat 
	formato_data = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")),
	formato_data_simples = new SimpleDateFormat("yyyyMMdd");

	private static AcessoStorageInterface acesso = AcessoStorageProvider.ObterInterface();

	private Integer errosProcessamento = null;

	private static Map<Integer, String> 
	desc_tipos_apait = EnquadramentoRegraInfracao.ObterDescTiposAPAIT();

	private Integer total_real_amostra = null, validaveis_real_amostra = null;

	public Remessa(Integer idRemessa, Integer codigoExterno, Date data,
			Date dataConfirmacao, Integer totalInfracao, Integer autoInicial,
			Integer autoFinal, Integer idEnquadramento, Integer idProcesso,
			String tipo, Integer revisao, Date dataExportacao,
			Date dataValidacao, Integer id_usuario, Date dataInicio,
			Date dataFinal, Long id_movimento_arquivo) {
		super();
		this.idRemessa = idRemessa;
		this.codigoExterno = codigoExterno;
		this.data = data;
		this.dataConfirmacao = dataConfirmacao;
		this.totalInfracao = totalInfracao;
		this.autoInicial = autoInicial;
		this.autoFinal = autoFinal;
		this.idEnquadramento = idEnquadramento;
		this.idProcesso = idProcesso;
		this.tipo = tipo;
		this.revisao = revisao;
		this.dataExportacao = dataExportacao;
		this.dataValidacao = dataValidacao;
		this.setDataInicio(dataInicio);
		this.setDataFinal(dataFinal);
		this.id_usuario = id_usuario;
		this.idMovimentoArquivo = id_movimento_arquivo;
	}
	
	public Remessa(Integer idRemessa, Integer codigoExterno, Date data,
			Date dataConfirmacao, Integer totalInfracao, Integer autoInicial,
			Integer autoFinal, Integer idEnquadramento, Integer idProcesso,
			String tipo, Integer revisao, Date dataExportacao,
			Date dataValidacao, Integer id_usuario, Date dataInicio,
			Date dataFinal, Long id_movimento_arquivo, String nomeUsuarioJanela, Long qtdeInfracoesJanela,
			boolean possuiAmostra, Integer totalRealAmostra, Integer validaveisRealAmostra) {
		super();
		this.idRemessa = idRemessa;
		this.codigoExterno = codigoExterno;
		this.data = data;
		this.dataConfirmacao = dataConfirmacao;
		this.totalInfracao = totalInfracao;
		this.autoInicial = autoInicial;
		this.autoFinal = autoFinal;
		this.idEnquadramento = idEnquadramento;
		this.idProcesso = idProcesso;
		this.tipo = tipo;
		this.revisao = revisao;
		this.dataExportacao = dataExportacao;
		this.dataValidacao = dataValidacao;
		this.setDataInicio(dataInicio);
		this.setDataFinal(dataFinal);
		this.id_usuario = id_usuario;
		this.idMovimentoArquivo = id_movimento_arquivo;
		this.nomeUsuarioJanela = nomeUsuarioJanela;
		this.qtdeInfracoesJanela = qtdeInfracoesJanela;
		this.amostra = possuiAmostra;
		this.total_real_amostra = totalRealAmostra;
		this.validaveis_real_amostra = validaveisRealAmostra;
	}
	
	public Remessa(Integer idRemessa, Date dataInicio, Date dataFinal,
				   Long idMovimentoArquivo, Integer idMovimento, Date dataMovimento,
				   Integer idUsuarioJanela, String usuarioJanela, String nomeUsuarioJanela, Long qtdeInfracoesJanela) {
		super();
		this.idRemessa = idRemessa;
		this.dataInicio = dataInicio;
		this.dataFinal = dataFinal;
		this.idMovimentoArquivo = idMovimentoArquivo;
		this.idMovimento = idMovimento;
		this.dataMovimento = dataMovimento;
		this.idUsuarioJanela = idUsuarioJanela;
		this.usuarioJanela = usuarioJanela;
		this.nomeUsuarioJanela = nomeUsuarioJanela;
		this.qtdeInfracoesJanela = qtdeInfracoesJanela;
	}
	
	public static Remessa buscarRemessaPorId(Integer idRemessa)
			throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();
		Remessa rRet = null;

		sbSQL.append("SELECT ");
		sbSQL.append("	id_remessa,");
		sbSQL.append("	codigo_externo,");
		sbSQL.append("	data,");
		sbSQL.append("	data_confirmacao,");
		sbSQL.append("	total_infracao,");
		sbSQL.append("	auto_inicial,");
		sbSQL.append("	auto_final,");
		sbSQL.append("  id_enquadramento,");
		sbSQL.append("	id_processo,");
		sbSQL.append("	RTRIM(tipo) AS tipo, ");
		sbSQL.append("  revisao, ");
		sbSQL.append("  data_exportacao, ");
		sbSQL.append("  data_validacao, ");
		sbSQL.append("  data_inicial, ");
		sbSQL.append("  data_final, ");
		sbSQL.append("  id_usuario, ");
		sbSQL.append("  id_movimento_arquivo ");
		sbSQL.append("FROM");
		sbSQL.append("	remessa WITH (NOLOCK)");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_remessa = ? ");
//		sbSQL.append(" ORDER BY codigo_externo "); (ORDER BY completamente desnecessario...)

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);

			rs = ps.executeQuery();
			if (rs.next()) {

				String tmpString = rs.getString("tipo");
				if (rs.wasNull()) {
					tmpString = null;
				} else {
					tmpString = tmpString.trim();
				}

				rRet = new Remessa(rs.getInt("id_remessa"),
						rs.getInt("codigo_externo"), rs.getTimestamp("data"),
						rs.getTimestamp("data_confirmacao"),
						rs.getInt("total_infracao"), rs.getInt("auto_inicial"),
						rs.getInt("auto_final"), rs.getInt("id_enquadramento"),
						rs.getInt("id_processo"), tmpString,
						rs.getInt("revisao"), rs.getDate("data_exportacao"),
						rs.getDate("data_validacao"), rs.getInt("id_usuario"),
						rs.getDate("data_inicial"), rs.getDate("data_final"),
						rs.getLong("id_movimento_arquivo"));
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return rRet;
	}
	
	public static HashMap<Integer, Remessa> buscarRemessa(boolean validavel)
			throws ConexaoException, SQLException {
		return buscarRemessa(validavel, 0, 0, 0, 0); //reprovado=0 pra trazer apenas lotes BONS para Validação
	}
	
	private static Date obterDataAtualizacao() {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Date data_atualizacao = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT MIN(data_atualizacao) FROM movimentos_pendentes (NOLOCK)");
			rs = ps.executeQuery();
			if (rs.next())
				data_atualizacao = rs.getTimestamp(1);
		}
		catch(Exception e) {
			logger.error("ao obterDataAtualizacao : " + e.getMessage(), e);
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
				logger.error("ao obterDataAtualizacao");
			}
		}
		
		return data_atualizacao;
	}
	
public static Integer obterToleranciaObterRemessas() {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Integer toleranciaObterRemessas = 60000;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT TOP(1) CAST(valor AS INT) tolerancia FROM chave_valor (NOLOCK) WHERE chave = 'tolerancia_busca_remessa'");
			rs = ps.executeQuery();
			if (rs.next())
				toleranciaObterRemessas = rs.getInt(1);
		}
		catch(Exception e) {
			logger.error("ao obterToleranciaObterRemessas");
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
				logger.error("ao obterToleranciaObterRemessas");
			}
		}
		
		return toleranciaObterRemessas;
	}

	public static HashMap<Integer, Remessa> buscarRemessa(boolean validavel, int quantidade, int reprovado, int filtroMovLote, int mostrarTodos)
			throws ConexaoException, SQLException {

		logger.info("Iniciando busca de Remessas");
		Date dt1 = Calendar.getInstance().getTime(), dt2 = null, data_atualizacao = null;
		
		HashMap<Integer, Remessa> lRet = new LinkedHashMap<Integer, Remessa>();
		StringBuilder sbSQL = new StringBuilder();
		
		data_atualizacao = obterDataAtualizacao();
		Integer tolerancia = obterToleranciaObterRemessas();
		boolean atualizado = true;
		
		// Se a data de atualizacao for nula ou maior do que 1 minuto (tempo de intervalo de atualizacao)
		if (data_atualizacao == null || (Calendar.getInstance().getTime().getTime() - data_atualizacao.getTime()) > tolerancia) {
			if (data_atualizacao != null)
				logger.warn("TABELA movimentos_pendentes DESATUALIZADA em " + (Calendar.getInstance().getTime().getTime() - data_atualizacao.getTime()) + " ms (Tolerancia = " + tolerancia + ")");
			else 
				logger.warn("TABELA movimentos_pendentes DESATUALIZADA (Tolerancia = " + tolerancia + ")");
			atualizado = false;
			data_atualizacao = Calendar.getInstance().getTime();
		}
		else {
			logger.info("TABELA movimentos_pendentes ATUALIZADA em " + (Calendar.getInstance().getTime().getTime() - data_atualizacao.getTime()) + " ms (Tolerancia = " + tolerancia + ")");
		}
		
		if (mostrarTodos == 0) {
			quantidade = 20;
		}
		
		if (quantidade > 0)
			sbSQL.append("SELECT TOP(" + quantidade + ") ");
		else
			sbSQL.append("SELECT ");
		
		sbSQL.append("        resultado.id_remessa ");
		sbSQL.append("    	 ,resultado.codigo_externo ");
		sbSQL.append("    	 ,resultado.data ");
		sbSQL.append("    	 ,resultado.total_infracao ");
		sbSQL.append("    	 ,resultado.auto_inicial ");
		sbSQL.append("    	 ,resultado.auto_final ");
		sbSQL.append("    	 ,resultado.id_enquadramento ");
		sbSQL.append("    	 ,resultado.id_processo ");
		sbSQL.append("    	 ,resultado.tipo ");
		sbSQL.append("    	 ,resultado.revisao ");
		sbSQL.append("    	 ,resultado.reprovado ");
		sbSQL.append("    	 ,resultado.data_exportacao ");
		sbSQL.append("    	 ,resultado.data_confirmacao ");
		sbSQL.append("    	 ,resultado.id_usuario ");
		sbSQL.append("    	 ,resultado.data_validacao ");
		sbSQL.append("    	 ,resultado.data_inicial ");
		sbSQL.append("    	 ,resultado.data_final ");
		sbSQL.append("    	 ,resultado.id_movimento_arquivo ");
		sbSQL.append("    	 ,resultado.amostra ");
		sbSQL.append("    	 ,resultado.infracoes_validaveis ");
		sbSQL.append("    	 ,resultado.qtde ");
		sbSQL.append("    	 ,resultado.nome_usuario_janela ");
		sbSQL.append("    	 ,resultado.total_real_amostra ");
		sbSQL.append("    	 ,resultado.validaveis_real_amostra ");
		
		if (atualizado)
			sbSQL.append("FROM movimentos_pendentes AS resultado ");
		else 
			sbSQL.append("FROM v_movimentos_pendentes AS resultado ");
		
		sbSQL.append(" WHERE 1 = 1 ");
		
		//Thiago Surgik 15/05/2015
		//mostrarTodos = 0 - Apenas lotes disponíveis para validação (sem lotes já validados OU reprovados)
		//mostrarTodos = 1 - Todos os lotes
		if(mostrarTodos == 0){
			sbSQL.append(" AND resultado.reprovado = 0 ");  
			sbSQL.append(" AND  NOT ( (resultado.infracoes_validaveis = 0) OR (resultado.amostra = 1 AND resultado.validaveis_real_amostra = 0)) ");
		}
				
		//Thiago Surgik 14/04/2015
		//filtroMovLote = 0 - TODOS OS LOTES
		//filtroMovLote = 1 - LOTES DISPONÍVEIS PARA LIBERAÇÃO
		//filtroMovLote = 2 - LOTES DISPONÍVEIS PARA ENVIO AO APAIT
		//filtroMovLote = 3 - PROCESSANDO
		if(filtroMovLote == 1){
			sbSQL.append(" AND resultado.reprovado = 0 ");  
			sbSQL.append(" AND resultado.total_real_amostra > 0 AND resultado.validaveis_real_amostra = 0 AND resultado.infracoes_validaveis > 0 ");  
		
		}else if(filtroMovLote == 2){
			sbSQL.append(" AND resultado.reprovado = 0 ");  
			sbSQL.append(" AND resultado.infracoes_validaveis = 0 ");
		
		}else if(filtroMovLote == 3){
			sbSQL.append(" AND resultado.reprovado = 0 ");  
			sbSQL.append(" AND resultado.nome_usuario_janela IS NOT NULL AND resultado.qtde = 1 ");
		}
		
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  resultado.id_remessa ");
		sbSQL.append(" 		 ,resultado.codigo_externo ");
		sbSQL.append(" 		 ,resultado.data ");
		sbSQL.append(" 		 ,resultado.total_infracao ");
		sbSQL.append(" 		 ,resultado.auto_inicial ");
		sbSQL.append(" 		 ,resultado.auto_final ");
		sbSQL.append(" 		 ,resultado.id_enquadramento ");
		sbSQL.append(" 		 ,resultado.id_processo ");
		sbSQL.append(" 		 ,resultado.tipo ");
		sbSQL.append(" 		 ,resultado.revisao ");
		sbSQL.append(" 		 ,resultado.reprovado ");
		sbSQL.append(" 		 ,resultado.data_exportacao ");
		sbSQL.append(" 		 ,resultado.data_confirmacao ");
		sbSQL.append(" 		 ,resultado.id_usuario ");
		sbSQL.append(" 		 ,resultado.data_validacao ");
		sbSQL.append(" 		 ,resultado.data_inicial ");
		sbSQL.append(" 		 ,resultado.data_final ");
		sbSQL.append(" 		 ,resultado.id_movimento_arquivo ");
		sbSQL.append(" 		 ,resultado.amostra ");
		sbSQL.append(" 		 ,resultado.infracoes_validaveis ");
		sbSQL.append(" 		 ,resultado.qtde ");
		sbSQL.append(" 		 ,resultado.nome_usuario_janela ");
		sbSQL.append(" 		 ,resultado.total_real_amostra ");
		sbSQL.append(" 		 ,resultado.validaveis_real_amostra ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  resultado.data_inicial "); 
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				Remessa rem = new Remessa(rs.getInt("id_remessa"),
										  rs.getInt("codigo_externo"),
										  rs.getTimestamp("data"),
										  rs.getTimestamp("data_confirmacao"),
										  rs.getInt("total_infracao"),
										  rs.getInt("auto_inicial"),
										  rs.getInt("auto_final"),
										  rs.getInt("id_enquadramento"),
										  rs.getInt("id_processo"),
										  rs.getString("tipo").trim(),
										  rs.getInt("revisao"),
										  rs.getDate("data_exportacao"),
										  rs.getDate("data_validacao"),
										  rs.getInt("id_usuario"),
										  rs.getDate("data_inicial"),
										  rs.getDate("data_final"),
										  rs.getLong("id_movimento_arquivo"),
										  rs.getString("nome_usuario_janela"),
										  rs.getLong("qtde"),
										  rs.getLong("amostra") == 1 ? true : false,
										  rs.getInt("total_real_amostra"),
										  rs.getInt("validaveis_real_amostra"));
				
				rem.setTotalInfracoesValidaveis(rs.getInt("infracoes_validaveis"));
				
				rem.setDataAtualizacao(data_atualizacao);
				
				lRet.put(rem.getIdRemessa(), rem);
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		
		dt2 = Calendar.getInstance().getTime();
		logger.info("Termino da busca de Remessas em " + (dt2.getTime() - dt1.getTime()) + " ms");
		
		return lRet;
	}
	
	private void setTotalInfracoesValidaveis(int int1) {
		this.totalInfracoesValidaveis = int1;
	}

	public static List<Remessa> buscarRemessaPor(Map<String, Object> mFiltros)
			throws ConexaoException, SQLException {

		List<Remessa> lRet = new ArrayList<Remessa>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("	r.id_remessa,");
		sbSQL.append("	codigo_externo,");
		sbSQL.append("	data,");
		sbSQL.append("	data_confirmacao,");
		sbSQL.append("	total_infracao,");
		sbSQL.append("	auto_inicial,");
		sbSQL.append("	auto_final,");
		sbSQL.append("  id_enquadramento,");
		sbSQL.append("	id_processo, ");
		sbSQL.append("	RTRIM(tipo) AS tipo, ");
		sbSQL.append("  revisao, ");
		sbSQL.append("  data_exportacao, ");
		sbSQL.append("  data_validacao, ");
		sbSQL.append("  id_usuario, ");
		sbSQL.append("  data_inicial, ");
		sbSQL.append("  data_final, ");
		sbSQL.append("  id_movimento_arquivo ");
		sbSQL.append("FROM");
		sbSQL.append("	remessa r WITH (NOLOCK)");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("data_ini", "data >= ?");
		mRegras.put("data_fim", "data <= ?");
		mRegras.put("data_ini_infracao", "r.data_final >= ?");
		mRegras.put("data_fim_infracao", "r.data_inicial <= ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY codigo_externo ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();

			while (rs.next()) {

				String tmpString = rs.getString("tipo");
				if (rs.wasNull()) {
					tmpString = null;
				} else {
					tmpString = tmpString.trim();
				}

				lRet.add(new Remessa(rs.getInt("id_remessa"), rs
						.getInt("codigo_externo"), rs.getTimestamp("data"), rs
						.getTimestamp("data_confirmacao"), rs
						.getInt("total_infracao"), rs.getInt("auto_inicial"),
						rs.getInt("auto_final"), rs.getInt("id_enquadramento"),
						rs.getInt("id_processo"), tmpString, rs
								.getInt("revisao"), rs
								.getDate("data_exportacao"), rs
								.getDate("data_validacao"), rs
								.getInt("id_usuario"), rs
								.getDate("data_inicial"), rs
								.getDate("data_final"), rs
								.getLong("id_movimento_arquivo")));
			}
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		} finally {
			if (conn != null)
				conn.close();
		}
		return lRet;
	}

	/**
	 * Verifica se a remessa já foi (parcial ou completamente) validada.
	 * 
	 * @return Verdadeiro, se a remessa já foi validada, ou falso.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public Boolean isValidada() throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT TOP 1 i.id_infracao FROM infracao i (NOLOCK) ");
		sbSQL.append("  JOIN infracao_remessa ir ON i.id_infracao = ir.id_infracao ");
		sbSQL.append("  WHERE i.id_processo = ? ");// +
													// Processamento.EtapaProcesso.REMESSA_VALIDADA);
		sbSQL.append("  AND   ir.id_remessa = ? ");// + idRemessa);

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros:
			ps.setInt(1, Processamento.EtapaProcesso.REMESSA_VALIDADA.getId());
			ps.setInt(2, idRemessa);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				bRet = true;
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return bRet;
	}

	/**
	 * Verifica se é a ultima remessa deste tipo.
	 * 
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public Boolean isUltima() throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT TOP 1 id_remessa FROM remessa ");
		sbSQL.append("WHERE tipo=? ORDER BY codigo_externo desc");

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros:
			ps.setString(1, this.tipo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				bRet = (rs.getInt("id_remessa") == this.idRemessa);
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return bRet;
	}

	/**
	 * Remove uma remessa.
	 * 
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public void excluirRemessa() throws ConexaoException, SQLException {

		Connection conn = Conexao.getConexao();

		try {
			CallableStatement cs = conn
					.prepareCall("{call spu_excluir_remessa(?)}");
			cs.setInt(1, idRemessa);

			cs.execute();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	public void incrementarRevisaoRemessa() throws ConexaoException,
			SQLException {

		Connection conn = Conexao.getConexao();

		try {
			incrementarRevisaoRemessa(conn);
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	public void incrementarRevisaoRemessa(Connection conn) throws SQLException {
		CallableStatement cs = conn
				.prepareCall("{call spu_IncrementaRevisaoRemessa(?)}");
		cs.setInt(1, idRemessa);
		cs.execute();
	}

	public Integer getIdRemessa() {
		return idRemessa;
	}

	public Integer getIdRemessaValida() {
		if (integridade == false && reprovado == false)
			return 0;
		return idRemessa;
	}

	public Integer getCodigoExterno() {
		return codigoExterno;
	}

	public Date getData() {
		return data;
	}

	public Date getDataConfirmacao() {
		return dataConfirmacao;
	}

	public Integer getTotalInfracao() {
		return totalInfracao;
	}

	public Integer getAutoInicial() {
		return autoInicial;
	}

	public Integer getAutoFinal() {
		return autoFinal;
	}

	public Integer getIdProcesso() {
		return idProcesso;
	}

	public String getTipo() {
		return tipo;
	}

	public Date getDataExportacao() {
		return dataExportacao;
	}

	public void setDataExportacao(Date dataExportacao) {
		this.dataExportacao = dataExportacao;
	}
	

	public Integer getIdMovimento() {
		return idMovimento;
	}

	public void setIdMovimento(Integer idMovimento) {
		this.idMovimento = idMovimento;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public Integer getIdUsuarioJanela() {
		return idUsuarioJanela;
	}

	public void setIdUsuarioJanela(Integer idUsuarioJanela) {
		this.idUsuarioJanela = idUsuarioJanela;
	}

	public String getUsuarioJanela() {
		return usuarioJanela;
	}

	public void setUsuarioJanela(String usuarioJanela) {
		this.usuarioJanela = usuarioJanela;
	}

	public String getNomeUsuarioJanela() {
		return nomeUsuarioJanela;
	}

	public void setNomeUsuarioJanela(String nomeUsuarioJanela) {
		this.nomeUsuarioJanela = nomeUsuarioJanela;
	}

	public Long getQtdeInfracoesJanela() {
		return qtdeInfracoesJanela;
	}

	public void setQtdeInfracoesJanela(Long qtdeInfracoesJanela) {
		this.qtdeInfracoesJanela = qtdeInfracoesJanela;
	}

	public boolean isAmostra() {
		return amostra;
	}

	public void setAmostra(boolean amostra) {
		this.amostra = amostra;
	}

	public Integer getRevisaoBanco() {
		Integer revisao = 0;
		 
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT COALESCE(ma.revisao, r.revisao) AS revisao FROM remessa r (NOLOCK) ");
		sbSQL.append("LEFT JOIN movimento_arquivo ma (NOLOCK) ON r.id_movimento_arquivo = ma.id_movimento_arquivo ");
		sbSQL.append("WHERE r.id_remessa = ? ");
		
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				revisao = rs.getInt("revisao");
		} catch(Exception e) {
			logger.error("Não foi possível obter revisão do lote", e);
		} finally {
			try {
				if(conn != null)
				conn.close();
			} catch (Exception e) {}
		}
	
		return revisao;
	}

	public Integer getRevisao() {
		return revisao;
	}

	public Date getDataValidacao() {
		return dataValidacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public boolean isRemessaValidada() {
		return (dataValidacao != null);
	}

	public Integer getIdUsuario() {
		return id_usuario;
	}

	public void reposicionarRemessa() throws ConexaoException, SQLException {
		int idProcesso_dest = Processamento.EtapaProcesso.VALIDACAO.getId();
		this.reposicionarRemessa(idProcesso_dest);
	}

	public void reposicionarRemessa(Integer idProcesso_dest)
			throws ConexaoException, SQLException {

		Connection conn = Conexao.getConexao();

		try {
			logger.info("Reposicionando remessa " + idRemessa + " : "
					+ idProcesso + " -> " + idProcesso_dest);
			CallableStatement cs = conn
					.prepareCall("{call spu_ReposicionaInfracoesRemessa(?, ?, ?)}");
			cs.setInt(1, idProcesso);
			cs.setInt(2, idProcesso_dest);
			cs.setInt(3, idRemessa);
			cs.execute();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	public void reposicionarRemessa(Integer idProcesso_orig,
			Integer idProcesso_dest) throws ConexaoException, SQLException {

		Connection conn = Conexao.getConexao();

		try {
			logger.info("Reposicionando remessa " + idRemessa + " : "
					+ idProcesso_orig + " -> " + idProcesso_dest);
			CallableStatement cs = conn
					.prepareCall("{call spu_ReposicionaInfracoesRemessa(?, ?, ?)}");
			cs.setInt(1, idProcesso_orig);
			cs.setInt(2, idProcesso_dest);
			cs.setInt(3, idRemessa);
			cs.execute();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private Integer getTotalInfracoesValidaveis_banco() {
		Connection conn = null;
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT COUNT(i.id_infracao) FROM infracao i (NOLOCK) ");
		sbSQL.append("JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao "); 
		sbSQL.append("LEFT JOIN infracao_processo ip_v (NOLOCK) "); 
		sbSQL.append("				ON	ir.id_infracao = ip_v.id_infracao "); 
		sbSQL.append("				AND ip_v.id_processo = 3 ");
		sbSQL.append("				AND ip_v.status_processo = 0  ");
		sbSQL.append("WHERE i.id_processo = 3 AND ir.id_remessa = ? "); 
		sbSQL.append("AND ip_v.id_infracao_processo IS NULL "); 
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				this.totalInfracoesValidaveis = rs.getInt(1);
		} catch (Exception e) {
			logger.error("Erro ao obter Infracoes Validaveis", e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao obter Infracoes Validaveis", e);
			}
		}
		return this.totalInfracoesValidaveis;
	}

	public Integer getTotalInfracoesValidaveis() {
		if (totalInfracoesValidaveis == null)
			getTotalInfracoesValidaveis_banco();
		return totalInfracoesValidaveis;
	}

	public Integer getTotalInfracoesValidadas() {
		return totalInfracao - getTotalInfracoesValidaveis();
	}

	public String getEstadoLoteAlt() {
		Integer totalInfracaoValidadas = getTotalInfracoesValidadas();

		if (totalInfracaoValidadas == 0)
			return "Em Espera";

		Integer totalInfracoesValidaveis = getTotalInfracoesValidaveis();
		if (totalInfracoesValidaveis == 0)
			return "100% Auditado";

		Integer amostra = getTamanhoAmostraReal();
		Integer amostra_validavel = getTamanhoAmostraValidavel();

		amostra = amostra >= 0 ? amostra : 0;
		amostra_validavel = amostra_validavel >= 0 ? amostra_validavel : 0;

		if (amostra == 0 || amostra_validavel > 0)
			return "Falta Imagens a Auditar";

		if (amostra_validavel == 0)
			return "Amostra OK";

		return "Estado Inválido de Lote";
	}
	
	public String getEstadoLote() {

		if (this.getIntegridade()) {
			return this.getEstadoLoteAlt();
		} else {
			return this.getIntegridadeDescricao();
		}
		
	}
	
	public String getEstadoLoteBD() {

		if (this.getIntegridadeBD()) {
			return this.getEstadoLoteAlt();
		} else {
			return this.getIntegridadeDescricao();
		}
		
	}

	public String getDescricao() {
		String inicial;
		if (this.idEnquadramento == 99999)
			inicial = "X";
		else 
			inicial = dataValidacao != null ? "L" : "R";
		
		return String.format("%2s %07d %2s %10s", dataValidacao != null ? inicial + "V"
				:  inicial + "M", codigoExterno, tipo,
				formato_data.format(data));
	}

	public String getDescricaoApait() {
		String inicial;
		if (this.idEnquadramento == 99999)
			inicial = "X";
		else 
			inicial = dataValidacao != null ? "L" : "R";
		
		return String.format("%2s %07d %2s %10s", dataValidacao != null ? inicial + "V"
				: inicial + "M", codigoExterno, desc_tipos_apait.get(idEnquadramento),
				formato_data.format(dataInicio));
	}
	
	public String getDescricaoApaitBloqueado() {
		String inicial;
		if (this.idEnquadramento == 99999)
			inicial = "X";
		else 
			inicial = dataValidacao != null ? "L" : "R";
		
		return String.format("%2s %07d %2s %10s %2s", dataValidacao != null ? inicial + "V"
				: inicial + "M", codigoExterno, desc_tipos_apait.get(idEnquadramento),
				formato_data.format(dataInicio), nomeUsuarioJanela != null ? "auditor [" + nomeUsuarioJanela.trim() + "]" : "");
	}
	
	public String getDescricaoApaitAlt() {
		String inicial;
		if (this.idEnquadramento == 99999)
			inicial = "X";
		else 
			inicial = dataValidacao != null ? "L" : "R";
		
		return String.format("%2s %07d %2s", dataValidacao != null ? inicial + "V"
				: inicial + "M", codigoExterno, desc_tipos_apait.get(idEnquadramento));
	}

	public Boolean getIntegridade() {
		while (integridade == null) {
			String integridade_pad = "LOTE INVÁLIDO";
			
			String inicial;
			if (this.idEnquadramento == 99999)
				inicial = "XM";
			else 
				inicial = "RM";

			String nomeArquivoLote = String.format("%2s%2s%07d%8s%4s", inicial,
					tipo, codigoExterno,
					formato_data_simples.format(data), ".TXT");
			
			logger.info("Verificando integridade do lote " + nomeArquivoLote + " (método getIntegridade())");
	        
			String s_ArquivoLote = null;
			try {
				s_ArquivoLote = acesso.LerArquivoMovimentoLote(nomeArquivoLote);
			} catch (IOException e) {
				logger.error("Erro ao obter ML", e);
			}
			if (s_ArquivoLote == null) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				integridadeDescricaoExt = "Arquivo de Movimento de Lote não encontrado!";
				continue;
			}

			List<String> arquivos_tarja = null, arquivos_imagem = null;
			try {
				arquivos_tarja = acesso.ListarArquivos(eTipoArquivo.TEXTO,
						nomeArquivoLote);
				arquivos_imagem = acesso.ListarArquivos(eTipoArquivo.IMAGEM,
						nomeArquivoLote);
			} catch (IOException e) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				integridadeDescricaoExt = "Não foi possível obter arquivos te texto/imagem do Servidor FTP";
				logger.error(integridadeDescricaoExt, e);
			}

			int iNumeroRegistrosLido = 0;
			BufferedReader br = null;
			try {
				String sCurrentLine, sNomeArquivoTXT, sNomeArquivoImagem;
				int iNumeroRegistros = 0, iSequencia = 0, iSequenciaAnterior = -1;
				br = new BufferedReader(new StringReader(s_ArquivoLote));
				while (integridade == null
						&& (sCurrentLine = br.readLine()) != null) {
					if (sCurrentLine.charAt(0) == '1') {
						iNumeroRegistros = Integer.parseInt(sCurrentLine
								.substring(18, 22));
					} else {
						iNumeroRegistrosLido++;
						
						// Verifica sequencia dos registros no Lote
						iSequencia = Integer.parseInt(sCurrentLine.substring(
								18, 22));
						if (iSequenciaAnterior > 0
								&& iSequencia != (iSequenciaAnterior + 1)) {
							integridade = false;
							integridadeDescricao = integridade_pad;
							integridadeDescricaoExt = "Sequencia de Registros inválida para o Lote";
							continue;
						}
						iSequenciaAnterior = iSequencia;
						
						// Verificar se as imagens/texto existem no STORAGE
						for (int sequenciaImagemLocal = 0;; sequenciaImagemLocal++) {
							sNomeArquivoTXT = String.format(
									"%2s%2s%07d%8s%04d%1d%4s", "TX",
									tipo,
									codigoExterno, formato_data_simples.format(data),
									iSequencia, sequenciaImagemLocal, ".TXT");
							sNomeArquivoImagem = String.format(
									"%2s%2s%07d%8s%04d%1d%4s", "IM",
									tipo,
									codigoExterno, formato_data_simples.format(data),
									iSequencia, sequenciaImagemLocal, ".JPG");
							boolean b1 = arquivos_tarja
									.contains(sNomeArquivoTXT);
							boolean b2 = arquivos_imagem
									.contains(sNomeArquivoImagem);

							if ((!b1 && !b2) && sequenciaImagemLocal > 0)
								break;
							if (!(b1 && b2)) {
								integridade = false;
								integridadeDescricao = integridade_pad;
								integridadeDescricaoExt = "Arquivo(s) faltando no Lote";
								break;
							}
						}
					}
				}

				// Número de registros do cabeçalho
				if (integridade == null
						&& iNumeroRegistros != iNumeroRegistrosLido) {
					integridade = false;
					integridadeDescricao = integridade_pad;
					integridadeDescricaoExt = "Número de Registros difere do Cabeçalho";
				}

			} catch (IOException e) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				integridadeDescricaoExt = "Não foi possível ler do arquivo de Lote";
				logger.error("Não foi possível ler arquivo de Lote "
						+ nomeArquivoLote, e);
			} catch (NumberFormatException e) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				if(iNumeroRegistrosLido > 0) {
					integridadeDescricaoExt = "Erro de parse na linha Nº" + (iNumeroRegistrosLido + 1) + " do detalhe do lote";
				} else {
					integridadeDescricaoExt = "Erro no cabeçalho do arquivo de Lote";
				}
				logger.error("Não foi possível ler arquivo de Lote "
						+ nomeArquivoLote, e);
			}
			finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException e) {
					logger.error("Não foi possível fechar arquivo de Lote "
							+ nomeArquivoLote, e);
				}
			}

			try {
				if (integridade == null
						&& (this.getTamanhoAmostraReal() > 0)
						&& (this.getErrosProcessamento() >= this.getRe())
						&& (this.getTotalInfracoesValidadas() <= this.getTamanhoAmostra())) {
					integridade = false;
					integridadeDescricao = "LOTE REPROVADO";
					reprovado = true;
				}
			} catch (Exception e) {
				logger.debug(
						"Erro ao obter Erros de Processamento ou Total Infracoes Validadas",
						e);
			}

			if (integridade == null) {
				integridade = true;
				integridadeDescricao = "LOTE VÁLIDO";
			} else {
				new AvisoMovimentoReprovado(idRemessa, nomeArquivoLote,
						integridadeDescricao, integridadeDescricaoExt,
						reprovado.booleanValue()).run();
			}
			
			logger.info(nomeArquivoLote + " => " + integridadeDescricao);
			
			if(integridadeDescricaoExt != null && integridadeDescricaoExt.length() > 0)
			logger.info(nomeArquivoLote + " => " + integridadeDescricaoExt);
		}
		
		return integridade;
	}
	
	public Boolean getIntegridadeBD() {
		
		List<Integer> sequenciaLote = new ArrayList<Integer>();
		MovimentoArquivo movimentoArquivo = null;
		
		Date dt1 = null, dt2 = null;
		
		while (integridade == null) {
			
			String inicial;
			if (this.idEnquadramento == 99999)
				inicial = "XM";
			else 
				inicial = "RM";
			
			String integridade_pad = "LOTE INVÁLIDO";
			String nomeArquivoLote = String.format("%2s%2s%07d%8s%4s", inicial, tipo, codigoExterno, formato_data_simples.format(data), ".TXT");
	        String s_ArquivoLote = null;
	        
	        logger.info("Verificando integridade do lote " + nomeArquivoLote + " (método getIntegridadeBD())");
	        
			try {
				dt1 = Calendar.getInstance().getTime();
				movimentoArquivo = MovimentoArquivo.getDadosMovimentoArquivo(codigoExterno, tipo);
				dt2 = Calendar.getInstance().getTime();
				logger.info("Executou o método getDadosMovimentoArquivo em " + (dt2.getTime() - dt1.getTime()) + " ms");
				
				dt1 = Calendar.getInstance().getTime();
				sequenciaLote = MovimentoArquivo.getSequenciaMovimentoArquivo(codigoExterno, tipo);
				dt2 = Calendar.getInstance().getTime();
				logger.info("Executou o método getSequenciaMovimentoArquivo em " + (dt2.getTime() - dt1.getTime()) + " ms");
				
				if (!sequenciaLote.isEmpty() && movimentoArquivo != null) {
					s_ArquivoLote = movimentoArquivo.getNomeArquivo();
				}
				
			} catch (Exception e) {
				logger.error("Erro ao obter ML", e);
			}
			
			if (s_ArquivoLote == null) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				integridadeDescricaoExt = "Arquivo de Movimento de Lote não encontrado!";
				continue;
			}

			List<String> arquivos_tarja = null, arquivos_imagem = null;
			
			try {
				dt1 = Calendar.getInstance().getTime();
				arquivos_tarja = MovimentoArquivo.ListarArquivosML(TipoArquivoML.TARJA, codigoExterno, tipo);
				dt2 = Calendar.getInstance().getTime();
				logger.info("Executou o método ListarArquivosML - TARJA em " + (dt2.getTime() - dt1.getTime()) + " ms");
				
				dt1 = Calendar.getInstance().getTime();
				arquivos_imagem = MovimentoArquivo.ListarArquivosML(TipoArquivoML.IMAGEM, codigoExterno, tipo);
				dt2 = Calendar.getInstance().getTime();
				logger.info("Executou o método ListarArquivosML - IMAGEM em " + (dt2.getTime() - dt1.getTime()) + " ms");
				
			} catch (Exception e) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				integridadeDescricaoExt = "Não foi possível obter arquivos te texto/imagem do Banco de Dados.";
				logger.error(integridadeDescricaoExt, e);
			}

			int iNumeroRegistrosLido = 0;
			
			try {
				String sNomeArquivoTXT, sNomeArquivoImagem;
				int iSequencia = 0, iSequenciaAnterior = -1;
				int iNumeroRegistros = movimentoArquivo.getNumeroRegistros();
				
				dt1 = Calendar.getInstance().getTime();
				for (int seq = 0; seq < sequenciaLote.size(); seq++) {
					iNumeroRegistrosLido++;
					
					// Verifica sequencia dos registros no Lote
					iSequencia = sequenciaLote.get(seq);
					if (iSequenciaAnterior > 0 && iSequencia != (iSequenciaAnterior + 1)) {
						integridade = false;
						integridadeDescricao = integridade_pad;
						integridadeDescricaoExt = "Sequencia de Registros inválida para o Lote";
						System.out.println("Sequencia: " + Integer.toString(iSequencia) + " / Sequencia Anterior: " + Integer.toString(iSequenciaAnterior));
						break;
					}
					
					iSequenciaAnterior = iSequencia;
					
					// Verificar se as imagens/texto existem no Banco de Dados
					for (int sequenciaImagemLocal = 0;; sequenciaImagemLocal++) {
						sNomeArquivoTXT = String.format("%2s%2s%07d%8s%04d%1d%4s", "TX", tipo, codigoExterno, formato_data_simples.format(data), iSequencia, sequenciaImagemLocal, ".TXT");
						sNomeArquivoImagem = String.format("%2s%2s%07d%8s%04d%1d%4s", "IM", tipo, codigoExterno, formato_data_simples.format(data), iSequencia, sequenciaImagemLocal, ".JPG");
						boolean b1 = arquivos_tarja.contains(sNomeArquivoTXT);
						boolean b2 = arquivos_imagem.contains(sNomeArquivoImagem);

						if ((!b1 && !b2) && sequenciaImagemLocal > 0) {
							break;
						}
						
						if (!(b1 && b2)) {
							integridade = false;
							integridadeDescricao = integridade_pad;
							integridadeDescricaoExt = "Arquivo(s) faltando no Lote";
							break;
						}
					}
				}

				dt2 = Calendar.getInstance().getTime();
				logger.info("Executou o passo Verifica sequencia dos registros no Lote em " + (dt2.getTime() - dt1.getTime()) + " ms");
				
				// Número de registros do cabeçalho
				if (integridade == null && iNumeroRegistros != iNumeroRegistrosLido) {
					integridade = false;
					integridadeDescricao = integridade_pad;
					integridadeDescricaoExt = "Número de Registros difere do Cabeçalho";
				}


			} catch (NumberFormatException e) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				if(iNumeroRegistrosLido > 0) {
					integridadeDescricaoExt = "Erro de parse na linha Nº" + (iNumeroRegistrosLido + 1) + " do detalhe do lote";
				} else {
					integridadeDescricaoExt = "Erro no cabeçalho do arquivo de Lote";
				}
				
				logger.error("Não foi possível ler arquivo de Lote " + nomeArquivoLote, e);
			} catch (Exception e) {
				integridade = false;
				integridadeDescricao = integridade_pad;
				integridadeDescricaoExt = "Não foi possível ler do arquivo de Lote";
				logger.error("Não foi possível ler arquivo de Lote " + nomeArquivoLote, e);
			}
			
			try {
				if (integridade == null
						&& (this.getTamanhoAmostraReal() > 0)
						&& (this.getErrosProcessamento() >= this.getRe())
						&& (this.getTotalInfracoesValidadas() <= this.getTamanhoAmostra())) {
					integridade = false;
					integridadeDescricao = "LOTE REPROVADO";
					reprovado = true;
				}
				
			} catch (Exception e) {
				logger.debug("Erro ao obter Erros de Processamento ou Total Infracoes Validadas", e);
			}

			if (integridade == null) {
				integridade = true;
				integridadeDescricao = "LOTE VÁLIDO";
			} else {
				new AvisoMovimentoReprovado(idRemessa, nomeArquivoLote, integridadeDescricao, integridadeDescricaoExt, reprovado.booleanValue()).run();
			}
			
			logger.info(nomeArquivoLote + " => " + integridadeDescricao);
			
			if(integridadeDescricaoExt != null && integridadeDescricaoExt.length() > 0) {
				logger.info(nomeArquivoLote + " => " + integridadeDescricaoExt);
			}
		}
		
		return integridade;
	}

	public boolean getReprovado() {
		return reprovado;
	}

	public String getIntegridadeDescricao() {
		return integridadeDescricao;
	}

	public String getIntegridadeDescricaoExt() {
		return integridadeDescricaoExt;
	}

	public boolean possuiAmostra() {
		Connection conn = null;
		boolean possui_amostra = false;
		try {
			conn = Conexao.getConexao();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT CASE WHEN EXISTS (SELECT 1 FROM remessa_amostragem (NOLOCK) WHERE id_remessa = ?) THEN 1 ELSE 0 END AS possui_amostra");
			ps.setInt(1, idRemessa);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				possui_amostra = rs.getBoolean(1);
			}
		} catch (Exception e) {
			integridade = false;
			integridadeDescricao = "Não foi possível obter dados de Amostra";
			logger.error(integridadeDescricao, e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar conexão!", e);
			}
		}
		return possui_amostra;
	}
	
	public static boolean possuiAmostra(Integer idRemessa) {
		Connection conn = null;
		boolean possui_amostra = false;
		try {
			conn = Conexao.getConexao();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT CASE WHEN EXISTS (SELECT 1 FROM remessa_amostragem (NOLOCK) WHERE id_remessa = ?) THEN 1 ELSE 0 END AS possui_amostra");
			ps.setInt(1, idRemessa);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				possui_amostra = rs.getBoolean(1);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar conexão!", e);
			}
		}
		return possui_amostra;
	}
	
	private void obterDadosAmostraReal() {
		Connection conn = null;
		try {
			conn = Conexao.getConexao();

			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("SELECT ");
			sbSQL.append(" id_remessa, COUNT(DISTINCT ra.id_infracao) AS infracoes, SUM(CASE WHEN ip.id_infracao IS NULL THEN 1 ELSE 0 END) AS validaveis ");
			sbSQL.append(" FROM remessa_amostragem ra (NOLOCK) ");
			sbSQL.append(" LEFT JOIN infracao_processo ip (NOLOCK) ON ra.id_infracao = ip.id_infracao AND ip.id_processo = 3 AND ip.status_processo = 0 ");
			sbSQL.append(" WHERE id_remessa = ? ");
			sbSQL.append(" GROUP BY id_remessa ");

			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				total_real_amostra = rs.getInt("infracoes");
				validaveis_real_amostra = rs.getInt("validaveis");
			} else {
				total_real_amostra = -1;
				validaveis_real_amostra = -1;
			}
		} catch (Exception e) {
			integridade = false;
			integridadeDescricao = "Não foi possível obter dados de Amostra";
			logger.error(integridadeDescricao, e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar conexão!", e);
			}
		}
	}

	private void obterDadosAmostra() {
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance()
				.getConfiguracaoChaveValor();
		String sNivel = ccv.get("nivel_inspecao");
		String sNQA = ccv.get("nqa");
		Connection conn = null;
		try {
			float fNQA = Float.parseFloat(sNQA);
			String sSQL = "SELECT * FROM fcn_getAmostra(?, ?, ?)";
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sSQL);
			ps.setString(1, sNivel);
			ps.setFloat(2, fNQA);
			ps.setInt(3, totalInfracao);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tamanhoAmostra = rs.getInt("tamanho_amostra");
				ac = rs.getInt("Ac");
				re = rs.getInt("Re");
				dadosObtidos = true;
			}
		} catch (Exception e) {
			integridade = false;
			integridadeDescricao = "Não foi possível obter dados de Amostra";
			logger.error(integridadeDescricao, e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar conexão!", e);
			}
		}
	}

	public Integer getTamanhoAmostra() {
		if (!dadosObtidos)
			obterDadosAmostra();
		return tamanhoAmostra;
	}

	public Integer getTamanhoAmostraReal() {
		if (total_real_amostra == null)
			obterDadosAmostraReal();
		return total_real_amostra;
	}

	public Integer getTamanhoAmostraValidavel() {
		if (validaveis_real_amostra == null)
			obterDadosAmostraReal();
		return validaveis_real_amostra;
	}

	public Integer getRe() {
		if (!dadosObtidos)
			obterDadosAmostra();
		return re;
	}

	public Integer getAc() {
		if (!dadosObtidos)
			obterDadosAmostra();
		return ac;
	}
	
	public Integer getErrosProcessamentoRelatorio()
	{
		Integer ret = 0;
		String sbSQL = "SELECT COUNT(*) FROM dbo.fcn_getRelatorioValidacao(?)";

		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL);
			ps.setInt(1, idRemessa);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getInt(1);
			} else {
				ret = 0;
			}
		} catch (Exception e) {
			integridade = false;
			integridadeDescricao = "Não foi possível obter erros da Remessa";
			logger.error(integridadeDescricao, e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Erro ao fechar conexão!", e);
			}
		}
		
		return ret;
	}

	public Integer getErrosProcessamento() {
		if (errosProcessamento == null) {
			String sbSQL = "SELECT dbo.fcn_ObterErrosRemessa(?)";

			Connection conn = null;
			try {
				conn = Conexao.getConexao();
				PreparedStatement ps = conn.prepareStatement(sbSQL);
				ps.setInt(1, idRemessa);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					errosProcessamento = rs.getInt(1);
				} else {
					errosProcessamento = 0;
				}
			} catch (Exception e) {
				integridade = false;
				integridadeDescricao = "Não foi possível obter erros da Remessa";
				logger.error(integridadeDescricao, e);
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					logger.error("Erro ao fechar conexão!", e);
				}
			}
		}
		return errosProcessamento;
	}

	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}
	
	public static Map.Entry<Integer, Integer> ObterTotalDisponivelEm(HashMap<Integer, Remessa> remessas_ht) 
	{
		Map<Integer,Integer> ret = new HashMap<Integer, Integer>();
		Integer validaveis = 0;
		Integer total = 0;
		
		for(Remessa rem_at : remessas_ht.values()) {
			validaveis += rem_at.getTotalInfracoesValidaveis();
			total += rem_at.getTotalInfracao();
		}
		
		if(ret.size() == 0)
			ret.put(validaveis, total);
		
		return ret.entrySet().iterator().next();
	}
	
	public static Map.Entry<Integer, Integer> ObterTotalDisponivel()
	{
		Map<Integer,Integer> ret = new HashMap<Integer, Integer>();
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT SUM(CASE WHEN i.id_processo = 3 THEN 1 ELSE 0 END) AS validaveis, ");
		sbSQL.append("COUNT(i.id_infracao) AS total  ");
		sbSQL.append("FROM infracao i (NOLOCK) ");
		sbSQL.append("JOIN infracao_remessa ir (NOLOCK) "); 
		sbSQL.append("ON i.id_infracao = ir.id_infracao ");
		sbSQL.append("JOIN remessa r (NOLOCK) ");
		sbSQL.append("ON ir.id_remessa = r.id_remessa "); 
		sbSQL.append("WHERE r.data_confirmacao IS NULL ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			if(rs.next()) {
				ret.put(rs.getInt("validaveis"), rs.getInt("total"));
			}
		} catch(Exception e) {
			logger.error("ObterTotalDisponivel" , e);
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
				if(conn != null)
					conn.close();
			} catch(Exception e) {}
		}
		
		if(ret.size() == 0)
			ret.put(0, 0);
		
		return ret.entrySet().iterator().next();
	}

	public static void ObterAmostraRemessa(Integer id_remessa)
			throws ConexaoException, SQLException {
		Connection conn = Conexao.getConexao();
		CallableStatement cs = null;
		try {
			cs = conn.prepareCall("{call spu_ObterAmostraRemessa(?)}");
			cs.setInt(1, id_remessa);
			cs.execute();
		} finally {
			if (cs != null)
				cs.close();
			if (conn != null)
				conn.close();
		}
	}

	public void AtualizarDataConfirmacao() throws ConexaoException,
			SQLException {
		Connection conn = Conexao.getConexao();
		PreparedStatement cs = null;
		try {
			cs = conn
					.prepareStatement("UPDATE remessa WITH(ROWLOCK) SET data_confirmacao = GETDATE() WHERE id_remessa = ?");

			cs.setInt(1, this.getIdRemessa());
			cs.executeUpdate();
		} finally {
			if (cs != null)
				cs.close();
			if (conn != null)
				conn.close();
		}
	}

	public Long getIdMovimentoArquivo() {
		return idMovimentoArquivo;
	}

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	public String getDataMinInfracaoStr() throws ConexaoException, SQLException {
//		Date data_inf = getDataMinInfracao(idRemessa);
		Date data_inf = dataInicio;
		
		Integer dias_at = daysBetween(data_inf, Calendar.getInstance().getTime());

		String data_str;
		if (dias_at < 14)
			data_str = String.format("%10s (%03d dias)",
					formato_data.format(data_inf), dias_at);
		else if (dias_at < 19)
			data_str = String.format(
					"%10s (<font color=\"orange\">%03d</font> dias)",
					formato_data.format(data_inf), dias_at);
		else
			data_str = String.format(
					"%10s (<font color=\"red\">%03d</font> dias)",
					formato_data.format(data_inf), dias_at);

		return data_str;
	}
	
	public String getUltimoUsuarioValidacao() {
		String ret = "N/D";
		
		Usuario usuario = getUltimoUsuarioProcesso(EtapaProcesso.VALIDACAO.getId());
		if(usuario != null) {
			ret = String.format("%06d - %s", usuario.getCodigoAgente(), usuario.getNome());
		}
		
		return ret;
	}
	
	public String getPrimeiroUsuarioValidacao() {
		String ret = "N/D";
		
		Usuario usuario = getPrimeiroUsuarioProcesso(EtapaProcesso.VALIDACAO.getId());
		if(usuario != null) {
			ret = String.format("%06d - %s", usuario.getCodigoAgente(), usuario.getNome());
		}
		
		return ret;
	}
	
	public Usuario getUltimoUsuarioProcesso(int id_processo) {
		
		Connection conn = null;
		
		Integer id_usuario = 0;
		Usuario usuario = null;
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append(" SELECT ip.id_usuario ");
		sbSQL.append(" FROM   infracao_processo ip (NOLOCK) "); 
		sbSQL.append(" 		  INNER JOIN ( ");
		sbSQL.append("		  	   			SELECT MAX(ip.id_infracao_processo) AS id_infracao_processo ");
		sbSQL.append("						FROM   infracao_remessa ir (NOLOCK) ");
		sbSQL.append("							   INNER JOIN infracao_processo ip (NOLOCK) ");
		sbSQL.append("							   		ON  ip.id_infracao = ir.id_infracao ");
		sbSQL.append("							   			AND ip.id_processo = ? ");
		sbSQL.append("							   			AND ip.status_processo = 0 ");
		sbSQL.append("						WHERE  ir.id_remessa = ? ");
		sbSQL.append(" 		  ) max_ip ");
		sbSQL.append(" 		  	   ON  max_ip.id_infracao_processo = ip.id_infracao_processo ");
		
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_processo);
			ps.setInt(2, idRemessa);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				id_usuario = rs.getInt(1);
		} catch(Exception e) {
			logger.error("Erro ao obter ultimo usuario do processo", e);
		} finally {
			try { if(conn!=null) conn.close(); } catch(Exception ex) {}
		}
		
		try {
			if (id_usuario > 0)
				usuario = Usuario.buscaUsuarioPorIdUsuario(id_usuario);
		} catch(Exception e) {}
		
		return usuario;
	}
	

	public Usuario getPrimeiroUsuarioProcesso(int id_processo) {
		
		Connection conn = null;
		
		Integer id_usuario = 0;
		Usuario usuario = null;
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append(" SELECT ip.id_usuario ");
		sbSQL.append(" FROM   infracao_processo ip (NOLOCK) "); 
		sbSQL.append(" 		  INNER JOIN ( ");
		sbSQL.append("		  	   			SELECT MIN(ip.id_infracao_processo) AS id_infracao_processo ");
		sbSQL.append("						FROM   infracao_remessa ir (NOLOCK) ");
		sbSQL.append("							   INNER JOIN infracao_processo ip (NOLOCK) ");
		sbSQL.append("							   		ON  ip.id_infracao = ir.id_infracao ");
		sbSQL.append("							   			AND ip.id_processo = ? ");
		sbSQL.append("							   			AND ip.status_processo = 0 ");
		sbSQL.append("						WHERE  ir.id_remessa = ? ");
		sbSQL.append(" 		  ) min_ip ");
		sbSQL.append(" 		  	   ON  min_ip.id_infracao_processo = ip.id_infracao_processo ");
		
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_processo);
			ps.setInt(2, idRemessa);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				id_usuario = rs.getInt(1);
		} catch(Exception e) {
			logger.error("Erro ao obter ultimo usuario do processo", e);
		} finally {
			try { if(conn!=null) conn.close(); } catch(Exception ex) {}
		}
		
		try {
			if (id_usuario > 0)
				usuario = Usuario.buscaUsuarioPorIdUsuario(id_usuario);
		} catch(Exception e) {}
		
		return usuario;
	}
	
	public Date getDataMinInfracao() throws ConexaoException, SQLException {
		return this.getDataMinInfracao(this.idRemessa);
	}

	/*-----------------------------------------------------------------------------------------------------------------
	Método: getDataMinInfracao   
	Descrição: Obter a data minima da infração dentro da remssa - Processo São Paulo
	Criador: Luiz Fernando Martins do Amaral
	Data: 19/05/2014
	-------------------------------------------------------------------------------------------------------------------
	Alteração		 Data			Descrição
	-------------------------------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------------------------*/
	public Date getDataMinInfracao(Integer id_remessa) throws ConexaoException,
			SQLException {

		Date dataResult = null;

		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append(" select " + " MIN(INf.data) as Min_DataInfracao "
				+ " from remessa rem (NOLOCK) "
				+ " inner join infracao_remessa infRem (NOLOCK) "
				+ " on rem.id_remessa = infRem.id_remessa "
				+ " inner join infracao Inf (NOLOCK) "
				+ " on Inf.id_infracao = InfRem.id_infracao "
				+ " where rem.id_remessa = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_remessa);

			rs = ps.executeQuery();
			if (rs.next()) {
				dataResult = rs.getDate(1);
			} else {
				dataResult = null;
			}

			return dataResult;
		} finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Autor: Luiz Amaral 27/10/2014
	 * Realiza a validação da remessa para não gerar sem INFRAÇÕES
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static boolean validarRemessaZerada(Integer idEnquadramento, 
									           Date dtInicio, 
									           Date dtFim) throws ModelException, ConexaoException, SQLException
	{
		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = true;
		StringBuilder erro = new StringBuilder("Erro na geração da Remessa: <br> <br> Não há infrações para gerar remessa.");

		sbSQL.append("	select count(*) as qtde ");
		sbSQL.append("	from infracao i ");
		sbSQL.append("	where  ");
		sbSQL.append("		id_enquadramento = ? ");
		sbSQL.append("		and cast(data as date) between ? and ? ");
		sbSQL.append("		and i.id_processo = 4 ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt (1, idEnquadramento);
			ps.setDate(2, new java.sql.Date(dtInicio.getTime()));
			ps.setDate(3, new java.sql.Date(dtFim.getTime()));

			rs = ps.executeQuery();
			
			if(rs.next()){
				if(rs.getInt("qtde") == 0){
					retorno = false;
					throw new ModelException(erro.toString());
				}				
			}

			
			return retorno;
			
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao gerar Remessa.");
			throw new ModelException(sbErro.toString(), ex);
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * Autor: Thiago Surgik 02/12/2014
	 * Verifica se existe janela de validação para a remessa.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static boolean verificarJanelaValidacao(Integer idRemessa) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = false;

		sbSQL.append(" SELECT count(ij.id_infracao) as qtde ");
		sbSQL.append(" FROM   movimento_arquivo ma (NOLOCK) ");
		sbSQL.append("		  INNER JOIN remessa r (NOLOCK) ");
		sbSQL.append("		  	   ON  r.id_movimento_arquivo = ma.id_movimento_arquivo ");
		sbSQL.append("		  INNER JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append("		  	   ON  ir.id_remessa = r.id_remessa ");
		sbSQL.append("		  INNER JOIN infracao_janela ij (NOLOCK) ");
		sbSQL.append("		  	   ON  ij.id_infracao = ir.id_infracao ");
		sbSQL.append("		  INNER JOIN sis_usuario su (NOLOCK) ");
		sbSQL.append("		  	   ON  ij.id_usuario = su.id_usuario ");
		sbSQL.append(" WHERE  ij.id_processo = ? ");
		sbSQL.append(" 		  AND r.id_remessa = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, Processamento.EtapaProcesso.VALIDACAO.getId());
			ps.setInt(2, idRemessa);

			rs = ps.executeQuery();
			
			if (rs.next()) {
				if(rs.getInt("qtde") > 0){
					retorno = true;
				}				
			}
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao verficar janela de validação.");
			throw new ModelException(sbErro.toString(), ex);
		} finally {
			if (conn != null)
				conn.close();
		}
		return retorno;
	}
	
	/**
	 * Autor: Thiago Surgik 02/12/2014
	 * Busca os dados da janela de validação para a remessa informada.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static Remessa buscarJanelaValidacao(Integer idRemessa) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		Remessa rRet = null;

		sbSQL.append(" SELECT ma.id_movimento_arquivo ");
		sbSQL.append("		 ,ma.id_movimento ");
		sbSQL.append("		 ,ma.data_movimento ");
		sbSQL.append("		 ,r.id_remessa ");
		sbSQL.append("		 ,r.data_inicial ");
		sbSQL.append("		 ,r.data_final ");
		sbSQL.append("		 ,su.id_usuario as id_usuario_janela ");
		sbSQL.append("		 ,su.usuario as usuario_janela ");
		sbSQL.append("		 ,su.nome as nome_usuario_janela ");
		sbSQL.append("		 ,count(ij.id_infracao) as qtde ");
		sbSQL.append(" FROM   movimento_arquivo ma (NOLOCK) ");
		sbSQL.append("		  INNER JOIN remessa r (NOLOCK) ");
		sbSQL.append("		  	   ON  r.id_movimento_arquivo = ma.id_movimento_arquivo ");
		sbSQL.append("		  INNER JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append("		  	   ON  ir.id_remessa = r.id_remessa ");
		sbSQL.append("		  INNER JOIN infracao_janela ij (NOLOCK) ");
		sbSQL.append("		  	   ON  ij.id_infracao = ir.id_infracao ");
		sbSQL.append("		  INNER JOIN sis_usuario su (NOLOCK) ");
		sbSQL.append("		  	   ON  ij.id_usuario = su.id_usuario ");
		sbSQL.append(" WHERE  ij.id_processo = ? ");
		sbSQL.append(" 		  AND r.id_remessa = ? ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append("		  ma.id_movimento_arquivo ");
		sbSQL.append("		 ,ma.id_movimento ");
		sbSQL.append("		 ,ma.data_movimento ");
		sbSQL.append("		 ,r.id_remessa ");
		sbSQL.append("		 ,r.data_inicial ");
		sbSQL.append("		 ,r.data_final ");
		sbSQL.append("		 ,su.id_usuario ");
		sbSQL.append("		 ,su.usuario ");
		sbSQL.append("		 ,su.nome ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, Processamento.EtapaProcesso.VALIDACAO.getId());
			ps.setInt(2, idRemessa);

			rs = ps.executeQuery();
			
			if (rs.next()) {
				rRet = new Remessa(rs.getInt("id_remessa"),
								   rs.getDate("data_inicial"),
								   rs.getDate("data_final"),
								   rs.getLong("id_movimento_arquivo"),
								   rs.getInt("id_movimento"),
								   rs.getDate("data_movimento"),
								   rs.getInt("id_usuario_janela"),
								   rs.getString("usuario_janela"),
								   rs.getString("nome_usuario_janela"),
								   rs.getLong("qtde"));
			}
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao buscar janela de validação.");
			throw new ModelException(sbErro.toString(), ex);
		} finally {
			if (conn != null)
				conn.close();
		}
		return rRet;
	}

	/**
	 * Autor: Thiago Surgik 10/12/2014
	 * Limpa a janela de validação para a remessa informada.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */	
	public static boolean limparAmostraValidacao(Integer idRemessa) throws ConexaoException, SQLException, ModelException {

		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = false;
		
		sbSQL.append(" {call spu_limpa_amostra_validacao(?)} ");
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(sbSQL.toString());
			
			cs.setInt(1, idRemessa);

			int cont = cs.executeUpdate();
			
			if (cont > 0) {
				retorno = true;
			}else {
				retorno = false;
			}
			
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao limpar amostra de validação.");
			throw new ModelException(sbErro.toString(), ex);
		
		} finally {
			if (conn != null)
				conn.close();
		}
		return retorno;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
}

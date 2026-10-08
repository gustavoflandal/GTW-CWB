/**
 * 
 */
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * @author thiago.surgik
 * @since 03/11/2015
 */
public class RemessaAutomatico {
	protected final static Logger logger = Logger.getLogger(RemessaAutomatico.class);
	private Integer idRemessaAutomatico;
	private Date dataSolicitacao;
	private Date dataRemessa;
	private Date dataInicio;
	private Date dataFinal;
	private Integer infracoesPorLote;
	private Integer totalInfracao;
	private Integer qtdeAproximadaLotes;
	private Integer idUsuario;
	private String usuario;
	private String nomeUsuario;
	private Integer flagGeracao;
	private Date dataGeracao;
	private Integer flagExportacao;
	private Date dataExportacao;
	

	public RemessaAutomatico(Integer idRemessaAutomatico, Date dataSolicitacao, Date dataRemessa, Date dataInicio, Date dataFinal,
			Integer infracoesPorLote, Integer totalInfracao, Integer qtdeAproximadaLotes, Integer idUsuario, String usuario, String nomeUsuario,
			Integer flagGeracao, Date dataGeracao, Integer flagExportacao, Date dataExportacao) {
		super();
		this.idRemessaAutomatico = idRemessaAutomatico;
		this.dataSolicitacao = dataSolicitacao;
		this.dataRemessa = dataRemessa;
		this.dataInicio = dataInicio;
		this.dataFinal = dataFinal;
		this.infracoesPorLote = infracoesPorLote;
		this.totalInfracao = totalInfracao;
		this.qtdeAproximadaLotes = qtdeAproximadaLotes;
		this.idUsuario = idUsuario;
		this.usuario = usuario;
		this.nomeUsuario = nomeUsuario;
		this.flagGeracao = flagGeracao;
		this.dataGeracao = dataGeracao;
		this.flagExportacao = flagGeracao;
		this.dataExportacao = dataExportacao;
	}
	
	public static RemessaAutomatico buscarRemessaAutomaticoPorId(Integer idRemessaAutomatico) throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();
		RemessaAutomatico rRet = null;

		sbSQL.append(" SELECT gra.id_remessa_automatico ");
		sbSQL.append("		 ,gra.data_solicitacao ");
		sbSQL.append("		 ,gra.data_remessa ");
		sbSQL.append("		 ,gra.data_inicial ");
		sbSQL.append("		 ,gra.data_final ");
		sbSQL.append("		 ,gra.infracoes_por_lote ");
		sbSQL.append("		 ,gra.total_infracoes ");
		sbSQL.append("		 ,gra.qtde_aprox_lotes ");
		sbSQL.append("		 ,gra.id_usuario ");
		sbSQL.append("		 ,RTRIM(LTRIM(su.usuario)) AS usuario ");
		sbSQL.append("		 ,RTRIM(LTRIM(su.nome)) AS nome_usuario ");
		sbSQL.append("		 ,gra.flag_geracao ");
		sbSQL.append("		 ,gra.data_geracao ");
		sbSQL.append("		 ,gra.flag_exportacao ");
		sbSQL.append("		 ,gra.data_exportacao ");
		sbSQL.append(" FROM   gera_remessa_automatico gra (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN sis_usuario su (NOLOCK) ");
		sbSQL.append(" 			   ON  su.id_usuario = gra.id_usuario ");
		sbSQL.append(" WHERE  gra.id_remessa_automatico = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  gra.data_solicitacao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessaAutomatico);

			rs = ps.executeQuery();
			if (rs.next()) {
				rRet = new RemessaAutomatico(
								rs.getInt("id_remessa_automatico"),
								rs.getTimestamp("data_solicitacao"),
								rs.getTimestamp("data_remessa"),
								rs.getTimestamp("data_inicial"),
								rs.getTimestamp("data_final"),
								rs.getInt("infracoes_por_lote"),
								rs.getInt("total_infracoes"),
								rs.getInt("qtde_aprox_lotes"),
								rs.getInt("id_usuario"),
								rs.getString("usuario"),
								rs.getString("nome_usuario"),
								rs.getInt("flag_geracao"),
								rs.getDate("data_geracao"),
								rs.getInt("flag_exportacao"),
								rs.getDate("data_exportacao"));
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return rRet;
	}
	
	public static List<RemessaAutomatico> buscarRemessaAutomaticoPor(Map<String, Object> mFiltros) throws ConexaoException, SQLException {

		List<RemessaAutomatico> lRet = new ArrayList<RemessaAutomatico>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT gra.id_remessa_automatico ");
		sbSQL.append("		 ,gra.data_solicitacao ");
		sbSQL.append("		 ,gra.data_remessa ");
		sbSQL.append("		 ,gra.data_inicial ");
		sbSQL.append("		 ,gra.data_final ");
		sbSQL.append("		 ,gra.infracoes_por_lote ");
		sbSQL.append("		 ,gra.total_infracoes ");
		sbSQL.append("		 ,gra.qtde_aprox_lotes ");
		sbSQL.append("		 ,gra.id_usuario ");
		sbSQL.append("		 ,RTRIM(LTRIM(su.usuario)) AS usuario ");
		sbSQL.append("		 ,RTRIM(LTRIM(su.nome)) AS nome_usuario ");
		sbSQL.append("		 ,gra.flag_geracao ");
		sbSQL.append("		 ,gra.data_geracao ");
		sbSQL.append("		 ,gra.flag_exportacao ");
		sbSQL.append("		 ,gra.data_exportacao ");
		sbSQL.append(" FROM   gera_remessa_automatico gra (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN sis_usuario su (NOLOCK) ");
		sbSQL.append(" 			   ON  su.id_usuario = gra.id_usuario ");
		sbSQL.append(" WHERE  ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("data_ini", "gra.data_solicitacao >= ?");
		mRegras.put("data_fim", "gra.data_solicitacao <= ?");
		mRegras.put("data_ini_infracao", "gra.data_inicial >= ?");
		mRegras.put("data_fim_infracao", "gra.data_final <= ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  gra.data_solicitacao ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new RemessaAutomatico(
						rs.getInt("id_remessa_automatico"),
						rs.getTimestamp("data_solicitacao"),
						rs.getTimestamp("data_remessa"),
						rs.getTimestamp("data_inicial"),
						rs.getTimestamp("data_final"),
						rs.getInt("infracoes_por_lote"),
						rs.getInt("total_infracoes"),
						rs.getInt("qtde_aprox_lotes"),
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome_usuario"),
						rs.getInt("flag_geracao"),
						rs.getDate("data_geracao"),
						rs.getInt("flag_exportacao"),
						rs.getDate("data_exportacao")));
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
	 * Autor: Thiago Surgik - 03/11/2015
	 * Realiza a validação para não permitir gravar agendamento automatico sem infrações no período.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static boolean validarRemessaZerada(Date dtInicio, 
									           Date dtFim) throws ModelException, ConexaoException, SQLException
	{
		StringBuilder sbSQL = new StringBuilder();
		boolean retorno = true;
		StringBuilder erro = new StringBuilder("Erro na geração do agendamento: <br> <br> Não há infrações para gerar remessa.");

		sbSQL.append(" SELECT COUNT(*) AS qtde ");
		sbSQL.append(" FROM   infracao i ");
		sbSQL.append(" WHERE  CAST(data AS DATE) BETWEEN ? AND ? ");
		sbSQL.append("		  AND i.id_processo = 4 ");

		if (dtInicio != null && dtFim != null) {
		
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
	
			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setDate(1, new java.sql.Date(dtInicio.getTime()));
				ps.setDate(2, new java.sql.Date(dtFim.getTime()));
	
				rs = ps.executeQuery();
				
				if(rs.next()){
					if(rs.getInt("qtde") == 0){
						retorno = false;
						throw new ModelException(erro.toString());
					}				
				}
				
			} catch (SQLException ex) {
				StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao gerar Remessa.");
				throw new ModelException(sbErro.toString(), ex);
			}
			finally {
				if (conn != null)
					conn.close();
			}
		}
		
		return retorno;
	}
	

	/**
	 * Autor: Thiago Surgik - 03/11/2015
	 * Realiza a validação para não permitir gravar agendamento automatico com mesmas datas.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static boolean validarRemessaAutomatico(Date dtInicio, Date dtFim) throws ModelException, ConexaoException, SQLException
	{
		StringBuilder sbSQL = new StringBuilder();
		
		boolean retorno = true;
		Integer pendente = 0;

		sbSQL.append(" SELECT TOP 1 gra.data_inicial ");
		sbSQL.append(" 		 ,gra.data_final ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), gra.data_inicial, 103) AS data_inicial_format ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), gra.data_final, 103) AS data_final_format ");
		sbSQL.append(" FROM   gera_remessa_automatico gra (NOLOCK) ");
		sbSQL.append(" WHERE  gra.flag_geracao = ? ");
		sbSQL.append("		  AND ( ");
		sbSQL.append("		   		( ");
		sbSQL.append("		   			(? BETWEEN CAST(gra.data_inicial AS DATE) AND CAST(gra.data_final AS DATE)) ");
		sbSQL.append("		   			OR ");
		sbSQL.append("		   			(? BETWEEN CAST(gra.data_inicial AS DATE) AND CAST(gra.data_final AS DATE)) ");
		sbSQL.append("		   		) ");
		sbSQL.append("		   		OR ");
		sbSQL.append("		   		( ");
		sbSQL.append("		   			(CAST(gra.data_inicial AS DATE) BETWEEN ? AND ?) ");
		sbSQL.append("		   			OR ");
		sbSQL.append("		   			(CAST(gra.data_final AS DATE) BETWEEN ? AND ?) ");
		sbSQL.append("		   		) ");
		sbSQL.append("		      ) ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  gra.data_inicial ");
		sbSQL.append(" 		 ,gra.data_final ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), gra.data_inicial, 103) ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), gra.data_final, 103) ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  gra.data_inicial ");

		if (dtInicio != null && dtFim != null) {
		
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
	
			try {
				conn = Conexao.getConexao();
				ps = conn.prepareStatement(sbSQL.toString());
				ps.setInt(1, pendente);
				ps.setDate(2, new java.sql.Date(dtInicio.getTime()));
				ps.setDate(3, new java.sql.Date(dtFim.getTime()));
				ps.setDate(4, new java.sql.Date(dtInicio.getTime()));
				ps.setDate(5, new java.sql.Date(dtFim.getTime()));
				ps.setDate(6, new java.sql.Date(dtInicio.getTime()));
				ps.setDate(7, new java.sql.Date(dtFim.getTime()));
	
				rs = ps.executeQuery();
				
				if(rs.next()){
					retorno = false;
					StringBuilder erro = new StringBuilder("Já existe um agendamento com período coincidente pendente de processamento: <br> <br> Data inicial: " + rs.getString("data_inicial_format") + " <br> Data final: " + rs.getString("data_final_format"));
					throw new ModelException(erro.toString());
				}
				
			} catch (SQLException ex) {
				StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao gerar Agendamento.");
				throw new ModelException(sbErro.toString(), ex);
			}
			finally {
				if (conn != null)
					conn.close();
			}
		}
		
		return retorno;
	}

	public Integer getIdRemessaAutomatico() {
		return idRemessaAutomatico;
	}
	public void setIdRemessaAutomatico(Integer idRemessaAutomatico) {
		this.idRemessaAutomatico = idRemessaAutomatico;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}
	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataRemessa() {
		return dataRemessa;
	}
	public void setDataRemessa(Date dataRemessa) {
		this.dataRemessa = dataRemessa;
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

	public Integer getInfracoesPorLote() {
		return infracoesPorLote;
	}
	public void setInfracoesPorLote(Integer infracoesPorLote) {
		this.infracoesPorLote = infracoesPorLote;
	}

	public Integer getTotalInfracao() {
		return totalInfracao;
	}
	public void setTotalInfracao(Integer totalInfracao) {
		this.totalInfracao = totalInfracao;
	}

	public Integer getQtdeAproximadaLotes() {
		return qtdeAproximadaLotes;
	}
	public void setQtdeAproximadaLotes(Integer qtdeAproximadaLotes) {
		this.qtdeAproximadaLotes = qtdeAproximadaLotes;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public Integer getFlagGeracao() {
		return flagGeracao;
	}
	public void setFlagGeracao(Integer flagGeracao) {
		this.flagGeracao = flagGeracao;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}
	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Integer getFlagExportacao() {
		return flagExportacao;
	}
	public void setFlagExportacao(Integer flagExportacao) {
		this.flagExportacao = flagExportacao;
	}

	public Date getDataExportacao() {
		return dataExportacao;
	}
	public void setDataExportacao(Date dataExportacao) {
		this.dataExportacao = dataExportacao;
	}
}

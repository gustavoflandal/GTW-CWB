/**
 * 
 */
package com.consilux.model.medicao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class ProcessoMedicao {
	public enum EstagioProcesso {
		INICIO(1),
		EXPORTADO_IMAGENS_COMPROVACAO(2),
		PROTOCOLADO_IMAGENS_COMPROVACAO(3),
		RETORNO_IMAGENS_COMPROVACAO(4),
		EXPORTADO_COMPLEMENTO_IMAGENS_COMPROVACAO(5),
		PROTOCOLADO_COMPLEMENTO_IMAGENS_COMPROVACAO(6),
		RETORNO_PLANILHA_QUANTITATIVOS(7);

		private Integer id;
		EstagioProcesso(Integer id) {
			this.id = id;
		}
		public Integer getId() {
			return id;
		}
		public static EstagioProcesso valueOfId(Integer id) {
			for (EstagioProcesso estagio: values()) {
				if (estagio.getId() == id.intValue())
					return estagio;
			}
			return null;
		}
	}
	
	
	private Integer id = null;
	private Integer mes = null;
	private Integer ano = null;
	private Date dataCriacao = null;
	private Date periodoIni = null;
	private Date periodoFim = null;
	private Date dataExportacao = null;
	private Date dataProtocoloExportacao = null;
	private Date dataRetornoExportacao = null;
	private Date dataComplemento = null;
	private Date dataProtocoloComplemento = null;
	private Date dataPlanilhaQuantitativos = null;
	private Integer idUsuarioResponsavel = null;

	/**
	 * @param id
	 * @param mes
	 * @param ano
	 * @param dataCriacao
	 * @param periodoIni
	 * @param periodoFim
	 * @param dataExportacao
	 * @param dataProtocoloExportacao
	 * @param dataRetornoExportacao
	 * @param dataComplemento
	 * @param dataProtocoloComplemento
	 * @param dataPlanilhaQuantitativos
	 * @param idUsuarioResponsavel
	 */
	public ProcessoMedicao(Integer id, Integer mes, Integer ano,
			Date dataCriacao, Date periodoIni, Date periodoFim,
			Date dataExportacao, Date dataProtocoloExportacao,
			Date dataRetornoExportacao, Date dataComplemento,
			Date dataProtocoloComplemento, Date dataPlanilhaQuantitativos,
			Integer idUsuarioResponsavel) {
		super();
		this.id = id;
		this.mes = mes;
		this.ano = ano;
		this.dataCriacao = dataCriacao;
		this.periodoIni = periodoIni;
		this.periodoFim = periodoFim;
		this.dataExportacao = dataExportacao;
		this.dataProtocoloExportacao = dataProtocoloExportacao;
		this.dataRetornoExportacao = dataRetornoExportacao;
		this.dataComplemento = dataComplemento;
		this.dataProtocoloComplemento = dataProtocoloComplemento;
		this.dataPlanilhaQuantitativos = dataPlanilhaQuantitativos;
		this.idUsuarioResponsavel = idUsuarioResponsavel;
	}
	public static ProcessoMedicao buscarProcessoMedicaoPorId(Integer id) throws ConexaoException, SQLException, ModelException {
		List<ProcessoMedicao> lRet = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id_processo_medicao", id);
		
		lRet = buscarProcessoMedicaoPor(map);
		
		return lRet != null && lRet.size() == 1 ? lRet.get(0) : null;
		
	}
	public static ProcessoMedicao buscarProcessoMedicaoPorMesAno(Integer mes, Integer ano) throws ConexaoException, SQLException, ModelException {
		List<ProcessoMedicao> lRet = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mes", mes);
		map.put("ano", ano);
		
		lRet = buscarProcessoMedicaoPor(map);
		
		if (lRet != null && lRet.size() > 1)
			new ModelException("Mais de um processo no mesmo mês!");
		
		return lRet != null && lRet.size() == 1 ? lRet.get(0) : null;
		
	}
	public static List<ProcessoMedicao> buscarProcessoMedicaoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		
		List<ProcessoMedicao> lRet = new ArrayList<ProcessoMedicao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	id_processo_medicao,");
		sbSQL.append("	mes,");
		sbSQL.append("	ano,");
		sbSQL.append("	data_criacao,");
		sbSQL.append("	periodo_ini,");
		sbSQL.append("	periodo_fim,");
		sbSQL.append("	data_exportacao,");
		sbSQL.append("	data_protocolo_exportacao,");
		sbSQL.append("	data_retorno_exportacao,");
		sbSQL.append("	data_complemento,");
		sbSQL.append("	data_protocolo_complemento,");
		sbSQL.append("	data_planilha_quantitativos,");
		sbSQL.append("	id_usuario_responsavel ");
		sbSQL.append("FROM");
		sbSQL.append("	processo_medicao pm WITH (NOLOCK)");
		sbSQL.append("	WHERE ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			Map<String, String> mRegras = new HashMap<String, String>();
			mRegras.put("data_ini", "periodo_fim >= ?");
			mRegras.put("data_fim", "periodo_ini <= ?");
			
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
		
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new ProcessoMedicao (
						rs.getInt("id_processo_medicao"),
						rs.getInt("mes"),
						rs.getInt("ano"),
						rs.getTimestamp("data_criacao"),
						rs.getTimestamp("periodo_ini"),
						rs.getTimestamp("periodo_fim"),
						rs.getTimestamp("data_exportacao"),
						rs.getTimestamp("data_protocolo_exportacao"),
						rs.getTimestamp("data_retorno_exportacao"),
						rs.getTimestamp("data_complemento"),
						rs.getTimestamp("data_protocolo_complemento"),
						rs.getTimestamp("data_planilha_quantitativos"),
						rs.getInt("id_usuario_responsavel")
					)
				);
			}
		}				
		finally {
			if (conn != null)
				conn.close();							
		}
		return lRet;
	}
	
	public static ProcessoMedicao criarProcessoMedicao(Integer mes, Integer ano) throws ConexaoException, SQLException, ModelException {

		Connection conn = Conexao.getConexao();
		ProcessoMedicao pmRet = null;
		
		try {
			CallableStatement cs = conn.prepareCall(
				"{? = call spu_criar_processo_medicao(?, ?)}");
		
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, mes);	
			cs.setInt(3, ano);
			
			cs.execute();
			pmRet = buscarProcessoMedicaoPorId(cs.getInt(1));
			
			cs.close();
			
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return pmRet;
	}

	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the mes
	 */
	public Integer getMes() {
		return mes;
	}

	/**
	 * @return the ano
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * @return the dataCriacao
	 */
	public Date getDataCriacao() {
		return dataCriacao;
	}

	/**
	 * @return the periodoIni
	 */
	public Date getPeriodoIni() {
		return periodoIni;
	}

	/**
	 * @return the periodoFim
	 */
	public Date getPeriodoFim() {
		return periodoFim;
	}

	/**
	 * @return the dataExportacao
	 */
	public Date getDataExportacao() {
		return dataExportacao;
	}

	/**
	 * @return the dataProtocoloExportacao
	 */
	public Date getDataProtocoloExportacao() {
		return dataProtocoloExportacao;
	}

	/**
	 * @return the dataComplemento
	 */
	public Date getDataComplemento() {
		return dataComplemento;
	}

	/**
	 * @return the dataProtocoloComplemento
	 */
	public Date getDataProtocoloComplemento() {
		return dataProtocoloComplemento;
	}

	public EstagioProcesso getEstagioProcesso() {
		EstagioProcesso estagio = EstagioProcesso.INICIO;
		estagio = dataExportacao != null ? EstagioProcesso.EXPORTADO_IMAGENS_COMPROVACAO : estagio;
		estagio = dataProtocoloExportacao != null ? EstagioProcesso.PROTOCOLADO_IMAGENS_COMPROVACAO : estagio;
		estagio = dataRetornoExportacao != null ? EstagioProcesso.RETORNO_IMAGENS_COMPROVACAO : estagio;
		estagio = dataComplemento != null ? EstagioProcesso.EXPORTADO_COMPLEMENTO_IMAGENS_COMPROVACAO : estagio;
		estagio = dataProtocoloComplemento != null ? EstagioProcesso.PROTOCOLADO_COMPLEMENTO_IMAGENS_COMPROVACAO : estagio;
		estagio = dataPlanilhaQuantitativos != null ? EstagioProcesso.RETORNO_PLANILHA_QUANTITATIVOS : estagio;
		return estagio;
	}

	/**
	 * @return the idUsuarioResponsavel
	 */
	public Integer getIdUsuarioResponsavel() {
		return idUsuarioResponsavel;
	}

	/**
	 * @return the dataRetornoExportacao
	 */
	public Date getDataRetornoExportacao() {
		return dataRetornoExportacao;
	}
	/**
	 * @return the dataPlanilhaQuantitativos
	 */
	public Date getDataPlanilhaQuantitativos() {
		return dataPlanilhaQuantitativos;
	}
	public Boolean adicPeriodo(Date dataInicio, Date dataFim) throws ConexaoException, SQLException {

		Connection conn = Conexao.getConexao();
		Boolean bRet = false;
		
		try {
			CallableStatement cs = conn.prepareCall(
				"{call spu_adic_periodo_medicao(?, ?, ?, ?)}");
		
			cs.setInt(1, this.id);
			cs.setTimestamp(2, new Timestamp(dataInicio.getTime()));	
			cs.setTimestamp(3, new Timestamp(dataFim.getTime()));
			if (getEstagioProcesso() == EstagioProcesso.RETORNO_IMAGENS_COMPROVACAO) //complementar = 1
				cs.setBoolean(4, true);
			else
				cs.setBoolean(4, false);
			
			cs.execute();
			
			bRet = true;
			
			cs.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}

	public void ajustaDataExportacao() throws ConexaoException, SQLException {
		String sSQL = "UPDATE processo_medicao SET data_exportacao = getDate() "+
					  "WHERE id_processo_medicao = ? AND CAST(periodo_fim AS DATE) = (" +
					  "		SELECT CAST(MAX(v.data) AS DATE) FROM veiculo v" +
					  "		JOIN processo_medicao_veiculo pmv ON v.id_veiculo = pmv.id_veiculo" +
					  "		WHERE pmv.id_processo_medicao = processo_medicao.id_processo_medicao" +
					  ")";
		
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			ps.setInt(1, this.id);

			ps.executeUpdate();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		
	}

	public void ajustaResponsavel(Integer idUsuario) throws ConexaoException, SQLException {
		String sSQL = "UPDATE processo_medicao SET id_usuario_responsavel = ?" +
		"		WHERE id_processo_medicao = ?";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			ps.setInt(1, idUsuario);
			ps.setInt(2, this.id);

			ps.executeUpdate();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		
	}
	public static Boolean atualizaEtapas(Integer idProcessoMedicao) throws ConexaoException, SQLException {

		Connection conn = Conexao.getConexao();
		Boolean bRet = false;
		
		try {
			CallableStatement cs = conn.prepareCall(
				"{call spu_atualiza_processo_medicao(?)}");
		
			cs.setInt(1, idProcessoMedicao);
			
			cs.execute();
			
			bRet = true;
			
			cs.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}
}

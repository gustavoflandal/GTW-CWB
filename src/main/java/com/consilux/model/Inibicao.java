/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 27/09/2011

  Descricao: Classe para busca de inibições no BD.

  Historico:

    $Log$

 *********************************************************************************/
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.InibicaoBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe para busca de inibições no BD.
 * @author Fernando Oliveira da Silva / Fernando de Souza - Consilux Tecnologia
 * @version $Revision$ $Date$ $Author$
 */
public class Inibicao {
	
	private Integer idInibicaoInfracao;
	private String descricao;
	private Integer pista;
	private Integer serieEquipamento;
	private Integer idEnquadramento;
	private String idClasse;
	private Date dataInicio;
	private Date dataFim;
	private Date horarioInicio;
	private Date horarioFim;
	private Integer idUsuario;
	private Date dataCriacao;
	private Integer idUsuarioCancelado;
	private Date dataCancelado;

	/**
	 * @param idInibicaoInfracao
	 * @param descricao
	 * @param pista
	 * @param serieEquipamento
	 * @param idEnquadramento
	 * @param idClasse
	 * @param dataInicio
	 * @param dataFim
	 * @param horarioInicio
	 * @param horarioFim
	 * @param idUsuario
	 * @param dataCriacao
	 * @param idUsuarioCancelado
	 * @param dataCancelado
	 */
	public Inibicao(Integer idInibicaoInfracao, String descricao,
			Integer pista, Integer serieEquipamento, Integer idEnquadramento,
			String idClasse, Date dataInicio, Date dataFim, Date horarioInicio,
			Date horarioFim, Integer idUsuario, Date dataCriacao,
			Integer idUsuarioCancelado, Date dataCancelado) {
		super();
		this.idInibicaoInfracao = idInibicaoInfracao;
		this.descricao = descricao;
		this.pista = pista;
		this.serieEquipamento = serieEquipamento;
		this.idEnquadramento = idEnquadramento;
		this.idClasse = idClasse;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.horarioInicio = horarioInicio;
		this.horarioFim = horarioFim;
		this.idUsuario = idUsuario;
		this.dataCriacao = dataCriacao;
		this.idUsuarioCancelado = idUsuarioCancelado;
		this.dataCancelado = dataCancelado;
	}

	/**
	 * Insere um novo registro de inibicao.
	 * @param conn Uma conexão (aberta) com o banco.
	 * @param inibicaoBean Um bean que possui os atributos do inibição.
	 * @return Objeto Inibicao materializado.
	 * @throws SQLException 
	 * @throws ModelException
	 * @throws ConexaoException 
	 */
	public static Inibicao incluirInibicao(InibicaoBean inibicaoBean) throws SQLException, ModelException, ConexaoException {
		
		Integer id = null;
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_criar_inibicao_infracao(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, inibicaoBean.getDescricao());
			if (inibicaoBean.getPista() != null)
				cs.setInt(3, inibicaoBean.getPista());
			else
				cs.setNull(3, Types.INTEGER);
			if (inibicaoBean.getSerieEquipamento() != null)
				cs.setInt(4, inibicaoBean.getSerieEquipamento());
			else
				cs.setNull(4, Types.INTEGER);
			cs.setInt(5, inibicaoBean.getIdEnquadramento());
			cs.setString(6, inibicaoBean.getIdClasse());
			cs.setDate(7, new java.sql.Date(inibicaoBean.getDataInicio().getTime()));
			cs.setDate(8, new java.sql.Date(inibicaoBean.getDataFim().getTime()));
			cs.setTime(9, new java.sql.Time(inibicaoBean.getHorarioInicio().getTime()));
			cs.setTime(10, new java.sql.Time(inibicaoBean.getHorarioFim().getTime()));
			cs.setInt(11, inibicaoBean.getIdUsuario());

			cs.execute();
			id = cs.getInt(1);

		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return id != null ? buscaInibicaoPorId(id) : null;
	}

	/**
	 * Remove um registro de inibicao.
	 * @throws ConexaoException
	 * @throws ModelException 
	 * @throws SQLException 
	 * @throws SQLException
	 */
	public Boolean desativarInibicao(Integer idUsuario) throws ConexaoException, ModelException, SQLException {

		Boolean bRet = false;

		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{call spu_desativar_inibicao_infracao(?, ?)}"
			);
			cs.setInt(1, this.idInibicaoInfracao);
			cs.setInt(2, idUsuario);

			cs.execute();
			bRet = true;
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return bRet;
	}
	
	public static Inibicao buscaInibicaoPorId(Integer idInibicaoInfracao) throws ConexaoException, SQLException, ModelException {
		Map<String,Object> mFiltro = new HashMap<String, Object>();
		mFiltro.put("id_inibicao_infracao", idInibicaoInfracao);
		
		List<Inibicao> l = buscaInibicaoPor(mFiltro);
		
		if (l.size() > 0)
			return l.get(0);
		else
			return null;
	}

	/**
	 * Busca inibição no BD.
	 * @param mFiltros Filtros para a busca.
	 * @return Lista de objetos Inibicao
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static List<Inibicao> buscaInibicaoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<Inibicao> lRet = new ArrayList<Inibicao>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("	id_inibicao_infracao,");
		sbSQL.append("	descricao,");
		sbSQL.append("	pista,");
		sbSQL.append("	serie_equipamento,");
		sbSQL.append("	id_enquadramento,");
		sbSQL.append("	id_classe,");
		sbSQL.append("	data_inicio,");
		sbSQL.append("	data_fim,");
		sbSQL.append("	horario_inicio,");
		sbSQL.append("	horario_fim,");
		sbSQL.append("	id_usuario,");
		sbSQL.append("	data_criacao,");
		sbSQL.append("	id_usuario_cancelado,");
		sbSQL.append("	data_cancelado ");		
		sbSQL.append("FROM");
		sbSQL.append("	cad_inibicao_infracao WITH (NOLOCK) ");
		
		if (mFiltros != null && mFiltros.size() > 0)
			sbSQL.append("	WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("somente_ativos", "(data_cancelado IS NULL AND data_fim > CAST(getDate() AS DATE))");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY id_inibicao_infracao");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
	
			rs = ps.executeQuery();
			while (rs.next()) {
				
				lRet.add(new Inibicao(
						rs.getInt("id_inibicao_infracao"),
						rs.getString("descricao"),
						rs.getString("pista") != null ? rs.getInt("pista") : null,
						rs.getString("serie_equipamento") != null ? rs.getInt("serie_equipamento") : null,
						rs.getInt("id_enquadramento"),
						rs.getString("id_classe"),
						rs.getDate("data_inicio"),
						rs.getDate("data_fim"),
						rs.getTime("horario_inicio"),
						rs.getTime("horario_fim"),
						rs.getInt("id_usuario"),
						rs.getTimestamp("data_criacao"),
						rs.getInt("id_usuario_cancelado"),
						rs.getTimestamp("data_cancelado")
					));
			}
		}				
		finally {
			if (conn != null)
				conn.close();
		}
		
		return lRet;
	}

	public InibicaoBean getBean() {
		InibicaoBean ret = new InibicaoBean();
		ret.setIdInibicaoInfracao(idInibicaoInfracao);
		ret.setDescricao(descricao);
		ret.setPista(pista);
		ret.setSerieEquipamento(serieEquipamento);
		ret.setIdEnquadramento(idEnquadramento);
		ret.setIdClasse(idClasse);
		ret.setDataInicio(dataInicio);
		ret.setDataFim(dataFim);
		ret.setHorarioInicio(horarioInicio);
		ret.setHorarioFim(horarioFim);
		ret.setIdUsuario(idUsuario);
		ret.setDataCriacao(dataCriacao);
		ret.setIdUsuarioCancelado(idUsuarioCancelado);
		ret.setDataCancelado(dataCancelado);
		return ret;
	}
	
	public Boolean getAtivo() {
		Date agora = DateUtils.truncate(new Date(), Calendar.DATE);
		
		return agora.compareTo(dataFim) <= 0 && dataCancelado == null;
	}
}

/**********************************************************************************


  Projeto: GTW
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 25/01/2010

  Descricao: Classe de negócio para cadastro de isentos.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.model;

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
 * Classe de negócio para cadastro de isentos.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class Isento {
	private String placa = null;
	private Integer idEnquadramento = null;
	private Date dataInicio = null;
	private Date horarioInicio = null;
	private Date dataFim = null;
	private Date horarioFim = null;
	private String motivo = null;
	private Integer idArquivo = null;
	private Date dataEntradaCadastro = null;
	private Date dataUltimoCadastro = null;
	private Integer idInconsistenciaIsencao = null;

	/**
	 * @param placa
	 * @param idEnquadramento
	 * @param dataInicio
	 * @param horarioInicio
	 * @param dataFim
	 * @param horarioFim
	 * @param motivo
	 * @param idArquivo
	 * @param dataEntradaCadastro
	 * @param dataUltimoCadastro
	 * @param idInconsistenciaIsencao
	 */
	public Isento(String placa, Integer idEnquadramento, Date dataInicio,
			Date horarioInicio, Date dataFim, Date horarioFim, String motivo,
			Integer idArquivo, Date dataEntradaCadastro,
			Date dataUltimoCadastro, Integer idInconsistenciaIsencao) {
		super();
		this.placa = placa;
		this.idEnquadramento = idEnquadramento;
		this.dataInicio = dataInicio;
		this.horarioInicio = horarioInicio;
		this.dataFim = dataFim;
		this.horarioFim = horarioFim;
		this.motivo = motivo;
		this.idArquivo = idArquivo;
		this.dataEntradaCadastro = dataEntradaCadastro;
		this.dataUltimoCadastro = dataUltimoCadastro;
		this.idInconsistenciaIsencao = idInconsistenciaIsencao;
	}
	/**
	 * Lista os cadastros de isentos do banco de dados conforme os critérios passados.
	 * @param mFiltros Filtros para a busca, regras implementadas: placa.
	 * @return Lista de Cadastros encontrados.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static List<Isento> buscaIsentoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<Isento> lRet = new ArrayList<Isento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT top 100"); // proteção contra muitos registros
		sbSQL.append("	placa,");
		sbSQL.append("	i.id_enquadramento,");
		sbSQL.append("	data_inicio,");
		sbSQL.append("	horario_inicio,");
		sbSQL.append("	data_fim,");
		sbSQL.append("	horario_fim,");
		sbSQL.append("	m.descricao AS motivo,");
		sbSQL.append("	e.id_inconsistencia_isencao,");
		sbSQL.append("	MAX(id_arquivo) as id_arquivo,");
		sbSQL.append("	MIN(data_atualizacao) as data_entrada_cadastro,");
		sbSQL.append("	MAX(data_atualizacao) as data_ultimo_cadastro ");
		sbSQL.append("FROM");
		sbSQL.append("	cad_isento_pesquisa i ");
		sbSQL.append("LEFT JOIN cad_modalidade_isento m ON i.modalidade = m.id_modalidade ");
		sbSQL.append("LEFT JOIN enquadramento e ON e.id_enquadramento = i.id_enquadramento ");
		sbSQL.append("WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("placa", "placa LIKE CAST(? AS CHAR(7))");
		mRegras.put("data", "CAST(? AS DATE) BETWEEN data_inicio AND data_fim");
		mRegras.put("area", "(area = 0 OR area = ?)");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" GROUP BY");
			sbSQL.append("	placa,");
			sbSQL.append("	i.id_enquadramento,");
			sbSQL.append("	data_inicio,");
			sbSQL.append("	horario_inicio,");
			sbSQL.append("	data_fim,");
			sbSQL.append("	horario_fim,");
			sbSQL.append("	m.descricao,");
			sbSQL.append("	e.id_inconsistencia_isencao");
			sbSQL.append(" ORDER BY placa, MIN(data_atualizacao) desc");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			//Ajustando os valores dos parâmetros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Isento(
						rs.getString("placa"),
						rs.getInt("id_enquadramento"),
						rs.getDate("data_inicio"),
						rs.getTime("horario_inicio"),
						rs.getDate("data_fim"),
						rs.getTime("horario_fim"),
						rs.getString("motivo"),
						rs.getInt("id_arquivo"),
						rs.getDate("data_entrada_cadastro"),
						rs.getDate("data_ultimo_cadastro"),
						rs.getInt("id_inconsistencia_isencao")
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
	/**
	 * Lista os cadastros de isentos do banco de dados conforme os critérios passados, utilizando a placa, enquadramento e a database para agilizar.
	 * @param mFiltros Filtros para a busca, regras implementadas: placa.
	 * @return Lista de Cadastros encontrados.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static List<Isento> buscaRapidaIsentoPor(String placa, Integer idEnquadramento, Date dataBase, Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<Isento> lRet = new ArrayList<Isento>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT "); // proteção contra muitos registros
		sbSQL.append("	placa,");
		sbSQL.append("	i.id_enquadramento,");
		sbSQL.append("	data_inicio,");
		sbSQL.append("	horario_inicio,");
		sbSQL.append("	data_fim,");
		sbSQL.append("	horario_fim,");
		sbSQL.append("	m.descricao AS motivo,");
		sbSQL.append("	id_arquivo,");
		sbSQL.append("	data_atualizacao,");
		sbSQL.append("	e.id_inconsistencia_isencao ");
		sbSQL.append("FROM");
		sbSQL.append("	fcn_pesquisaIsento(CAST(? AS CHAR(7)), ?, ?) i ");
		sbSQL.append("LEFT JOIN cad_modalidade_isento m ON i.modalidade = m.id_modalidade ");
		sbSQL.append("LEFT JOIN enquadramento e ON e.id_enquadramento = i.id_enquadramento ");
		if(mFiltros.size() > 0)
		sbSQL.append("WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("data_validade", "CAST(? AS DATE) BETWEEN data_inicio AND data_fim");
		mRegras.put("area", "(area = 0 OR area = ?)");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, placa);
			ps.setInt(2, idEnquadramento);
			if (dataBase != null)
				ps.setTimestamp(3, new Timestamp(dataBase.getTime()));
			else
				ps.setTimestamp(3, new Timestamp(new Date().getTime()));
			
			//Ajustando os valores dos parâmetros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 4, mFiltros.values());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Isento(
						rs.getString("placa"),
						rs.getInt("id_enquadramento"),
						rs.getDate("data_inicio"),
						rs.getTime("horario_inicio"),
						rs.getDate("data_fim"),
						rs.getTime("horario_fim"),
						rs.getString("motivo"),
						rs.getInt("id_arquivo"),
						rs.getDate("data_atualizacao"),
						null,
						rs.getInt("id_inconsistencia_isencao")
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

	public Boolean getVigente() throws ConexaoException, SQLException {

		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	max(id_arquivo)");
		sbSQL.append("FROM");
		sbSQL.append("	cad_isento WITH (NOLOCK)");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_enquadramento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idEnquadramento);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				bRet = rs.getInt(1) == idArquivo;
			}
			rs.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}
	
	/**
	 * Retorna o valor do campo 'placa' atual.
	 * @return the placa
	 */
	public String getPlaca() {
		return this.placa;
	}

	/**
	 * Retorna o valor do campo 'idEnquadramento' atual.
	 * @return the idEnquadramento
	 */
	public Integer getIdEnquadramento() {
		return this.idEnquadramento;
	}

	/**
	 * Retorna o valor do campo 'dataInicio' atual.
	 * @return the dataInicio
	 */
	public Date getDataInicio() {
		return this.dataInicio;
	}

	/**
	 * Retorna o valor do campo 'dataFim' atual.
	 * @return the dataFim
	 */
	public Date getDataFim() {
		return this.dataFim;
	}

	/**
	 * Retorna o valor do campo 'motivo' atual.
	 * @return the motivo
	 */
	public String getMotivo() {
		return this.motivo;
	}
	/**
	 * Retorna o valor do campo 'horarioInicio' atual.
	 * @return the horarioInicio
	 */
	public Date getHorarioInicio() {
		return this.horarioInicio;
	}
	/**
	 * Retorna o valor do campo 'horarioFim' atual.
	 * @return the horarioFim
	 */
	public Date getHorarioFim() {
		return this.horarioFim;
	}
	/**
	 * @return the idArquivo
	 */
	public Integer getIdArquivo() {
		return idArquivo;
	}
	/**
	 * @return the dataEntradaCadatro
	 */
	public Date getDataEntradaCadastro() {
		return dataEntradaCadastro;
	}
	/**
	 * @return the dataUltimoCadastro
	 */
	public Date getDataUltimoCadastro() {
		return dataUltimoCadastro;
	}
	/**
	 * @return the idInconsistenciaIsencao
	 */
	public Integer getIdInconsistenciaIsencao() {
		return idInconsistenciaIsencao;
	}
	
}

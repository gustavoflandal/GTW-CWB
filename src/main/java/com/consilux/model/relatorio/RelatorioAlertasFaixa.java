/**********************************************************************************


  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 05/05/2011

  Descricao: Classe para buscar infrações em processos disponíveis para auditoria;


 *********************************************************************************/
package com.consilux.model.relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe para buscar infrações em processos disponíveis para auditoria
 * @author Fernando Oliveira da Silva
 */
public class RelatorioAlertasFaixa {
	
	
	private int serieEquipamento;
	private String nomePista;
	private int idPista;
	private String informacaoAdicional;
	private Boolean energia;
	private Boolean pavimento;
	private Boolean vandalismo;
	private Boolean falsoPositivo;


	/**
	 * @param serieEquipamento
	 * @param nomePista
	 * @param idPista
	 * @param informacaoAdicional
	 * @param energia
	 * @param pavimento
	 * @param vandalismo
	 */
	public RelatorioAlertasFaixa(int serieEquipamento, String nomePista,
			int idPista, String informacaoAdicional, Boolean energia,
			Boolean pavimento, Boolean vandalismo, Boolean falsoPositivo) {
		super();
		this.serieEquipamento = serieEquipamento;
		this.nomePista = nomePista;
		this.idPista = idPista;
		this.informacaoAdicional = informacaoAdicional;
		this.energia = energia;
		this.pavimento = pavimento;
		this.vandalismo = vandalismo;
		this.falsoPositivo = falsoPositivo;
	}

	/**
	 * Busca a quantidade de infrações em cada processo.
	 * @return Relatório de processamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<RelatorioAlertasFaixa> buscaRelatorio() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		List<RelatorioAlertasFaixa> lret = new ArrayList<RelatorioAlertasFaixa>();

		sbSQL.append("SELECT ");
		sbSQL.append("	lv.serie_equipamento,");
		sbSQL.append("	pca.id_pista,");
		sbSQL.append("	COALESCE(cp.nome_pista, lv.nome) AS nome,");
		sbSQL.append("	pca.informacao_adicional,");
		sbSQL.append("	pca.motivo_ext_energia,");
		sbSQL.append("	pca.motivo_ext_pavimento,");
		sbSQL.append("	pca.motivo_ext_vandalismo,");
		sbSQL.append("	pca.motivo_falso_positivo");
		sbSQL.append(" FROM local_vigente lv");
		sbSQL.append(" JOIN painel_contrato_alerta pca ON pca.serie_equipamento = lv.serie_equipamento");
		sbSQL.append(" LEFT JOIN configuracao_equipamento_pista cp ON cp.id_configuracao_equipamento = lv.id_configuracao_equipamento AND cp.id_pista = pca.id_pista");
		sbSQL.append(" WHERE ativo = 1");
		sbSQL.append(" ORDER BY");
		sbSQL.append("	lv.serie_equipamento,");
		sbSQL.append("	pca.id_pista");
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		try {
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) {
					lret.add(new RelatorioAlertasFaixa(
								rs.getInt("serie_equipamento"),
								rs.getString("nome"),
								rs.getInt("id_pista"),
								rs.getString("informacao_adicional"),
								rs.getBoolean("motivo_ext_energia"),
								rs.getBoolean("motivo_ext_pavimento"),
								rs.getBoolean("motivo_ext_vandalismo"),
								rs.getBoolean("motivo_falso_positivo")
							));
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return lret;
	}

	/**
	 * @return the serieEquipamento
	 */
	public int getSerieEquipamento() {
		return serieEquipamento;
	}

	/**
	 * @return the nomePista
	 */
	public String getNomePista() {
		return nomePista;
	}

	/**
	 * @return the idPista
	 */
	public int getIdPista() {
		return idPista;
	}

	/**
	 * @return the informacaoAdicional
	 */
	public String getInformacaoAdicional() {
		return informacaoAdicional;
	}

	/**
	 * @return the energia
	 */
	public Boolean getEnergia() {
		return energia;
	}

	/**
	 * @return the pavimento
	 */
	public Boolean getPavimento() {
		return pavimento;
	}

	/**
	 * @return the vandalismo
	 */
	public Boolean getVandalismo() {
		return vandalismo;
	}

	/**
	 * @return the falsoPositivo
	 */
	public Boolean getFalsoPositivo() {
		return falsoPositivo;
	}
	
}
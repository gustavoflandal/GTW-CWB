/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 30/06/2016

*********************************************************************************/

package com.consilux.model.medicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados para os relatórios de eficiência do equipamento.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 30/06/2016
 */
public class DadosEficienciaEquipamento {

	/**
	 * Classe de negócio para busca de dados para os relatórios de eficiência do equipamento - Relatório de Equipamento/Faixa x Inconsistências.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 30/06/2016
	 */
	public ArrayList<ItemEficienciaEquipamento> equipamentoxInconsistencia(Date dataInicio, Date dataFim,
								Integer codPista, Integer codPistaProdam, String enquadramento) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();
		String strSQLWhere = " WHERE ";

		sbSQL.append(" DECLARE @data_inicio DATE = ? ");
		sbSQL.append(" DECLARE @data_fim DATE = ? ");
		
		if (codPista != null) {
			sbSQL.append(" DECLARE @cod_pista INT = ? ");
		}
		if (codPistaProdam != null) {
			sbSQL.append(" DECLARE @cod_pista_prodam INT = ? ");
		}
		if (enquadramento != null) {
			sbSQL.append(" DECLARE @enquadramento VARCHAR(40) = ? ");
		}

		sbSQL.append(" SELECT CONVERT(VARCHAR(10), datas.Data, 103) AS Data ");
		sbSQL.append("       ,cem.cod_pista ");
		sbSQL.append("       ,cem.descricao ");
		sbSQL.append("       ,COALESCE(equip_prodam.cod_pista_prodam, cem.cod_pista_prodam) AS cod_pista_prodam ");
		sbSQL.append("       ,cem.cod_pista_alternativo AS faixa ");
		sbSQL.append("       ,CONVERT(VARCHAR(10), cem.data_inicio, 103) AS data_inicio_operacao ");
		sbSQL.append("       ,LTRIM(RTRIM(ea.descricao_apait)) AS enquadramento_habilitado ");
		sbSQL.append("       ,CASE WHEN inc.id_inconsistencia IS NULL ");
		sbSQL.append("       	   THEN 'N/D' ");
		sbSQL.append("       	   ELSE LTRIM(RTRIM(RIGHT('00' + CAST(inc.id_inconsistencia AS VARCHAR(2)), 2))) + ' - ' + LTRIM(RTRIM(inc.descricao)) END AS inconsistencia_CAI_motivo ");
		sbSQL.append("       ,COUNT(i.id_infracao) AS inconsistencia_CAI_qtde ");
		sbSQL.append("       ,CASE WHEN cem.serie_equipamento < 9907000 THEN 1 ELSE 0 END equipamento_estatico ");

		sbSQL.append(" FROM   configuracao_equipamento_medicao cem (NOLOCK) ");
		sbSQL.append("		  LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append("			   ON  i.id_local = cem.cod_pista ");
		sbSQL.append("				   AND i.pista = cem.cod_pista_alternativo ");
		sbSQL.append("		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append("			   ON  ir.id_infracao = i.id_infracao ");
		sbSQL.append("		  LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append("			   ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append("		  LEFT JOIN movimento_importacao mi (NOLOCK) ");
//		sbSQL.append("			   ON  mi.id_movimento = r.codigo_externo ");
		sbSQL.append("  			   ON r.id_movimento_arquivo = mi.id_movimento_arquivo ");
		sbSQL.append("				   AND mi.sequencia = ir.sequencia ");
//		sbSQL.append("				   AND mi.id_enquadramento = i.id_enquadramento ");
		sbSQL.append("		  LEFT JOIN inconsistencia inc (NOLOCK) ");
		sbSQL.append("			   ON  inc.id_inconsistencia = mi.id_inconsistencia ");
		sbSQL.append("		  RIGHT JOIN dbo.fcn_ObterDatasPeriodo(@data_inicio, @data_fim) datas ");
		sbSQL.append("			   ON  datas.Data = CAST(i.data AS DATE) ");
		sbSQL.append("		  LEFT JOIN ( ");
		sbSQL.append("		  				SELECT aux.id_local ");
		sbSQL.append("		  					  ,aux.pista ");
		sbSQL.append("		  					  ,aux.cod_pista_prodam ");
		sbSQL.append("		  					  ,CAST(aux.data_hora AS DATE) AS data ");
		sbSQL.append("		  				FROM   movimento_importacao aux (NOLOCK) ");
		sbSQL.append("		  				GROUP BY ");
		sbSQL.append("		  					   aux.id_local ");
		sbSQL.append("		  					  ,aux.pista ");
		sbSQL.append("		  					  ,aux.cod_pista_prodam ");
		sbSQL.append("		  					  ,CAST(aux.data_hora AS DATE) ");
		sbSQL.append("		  			) equip_prodam ");
		sbSQL.append("		  	   ON  equip_prodam.id_local = cem.cod_pista ");
		sbSQL.append("		  		   AND equip_prodam.pista = cem.cod_pista_alternativo ");
		sbSQL.append("		  		   AND equip_prodam.data = datas.Data ");
		sbSQL.append("		  RIGHT JOIN enquadramento_ativo ea (NOLOCK) ");
		sbSQL.append("			   ON  datas.Data >= ea.data_inicio ");
		sbSQL.append("				   AND datas.Data < COALESCE(ea.data_fim, CAST(GETDATE() AS DATE)) ");
		sbSQL.append("				   AND ea.cod_pista = cem.cod_pista ");
		sbSQL.append("				   AND ea.cod_pista_alternativo = cem.cod_pista_alternativo ");
		sbSQL.append("				   AND ea.id_enquadramento = mi.id_enquadramento ");
		
		sbSQL.append(strSQLWhere + " cem.cod_pista IS NOT NULL ");
		strSQLWhere = " AND ";
		
		if (codPista != null) {
			sbSQL.append(strSQLWhere + " cem.cod_pista = @cod_pista ");
			strSQLWhere = " AND ";
		}
		
		if (codPistaProdam != null) {
			sbSQL.append(strSQLWhere + " equip_prodam.cod_pista_prodam = @cod_pista_prodam ");
			strSQLWhere = " AND ";
		}
		
		if (enquadramento != null) {
			sbSQL.append(strSQLWhere + " LTRIM(RTRIM(ea.descricao_apait)) = @enquadramento ");
			strSQLWhere = " AND ";
		}
						
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  datas.Data ");
		sbSQL.append(" 		 ,cem.cod_pista "); 
		sbSQL.append(" 		 ,cem.descricao ");
		sbSQL.append("       ,COALESCE(equip_prodam.cod_pista_prodam, cem.cod_pista_prodam) ");
		sbSQL.append(" 		 ,cem.cod_pista_alternativo ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), cem.data_inicio, 103) ");
		sbSQL.append(" 		 ,LTRIM(RTRIM(ea.descricao_apait)) ");
		sbSQL.append(" 		 ,inc.id_inconsistencia ");
		sbSQL.append(" 		 ,inc.descricao ");
		sbSQL.append("       ,CASE WHEN cem.serie_equipamento < 9907000 THEN 1 ELSE 0 END ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  datas.Data ");
		sbSQL.append(" 		 ,cem.cod_pista ");
		sbSQL.append(" 		 ,cem.cod_pista_alternativo ");
		sbSQL.append(" 		 ,inc.id_inconsistencia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemEficienciaEquipamento> listaItensRelatorio =  new ArrayList<ItemEficienciaEquipamento>();
		ItemEficienciaEquipamento itemRelatorio;
		Integer param = 1;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(param, new java.sql.Date(dataInicio.getTime()));
			param ++;
			ps.setDate(param, new java.sql.Date(dataFim.getTime()));
			param ++;
			
			if (codPista != null) {
				ps.setInt(param, codPista);
				param ++;
			}
			if (codPistaProdam != null) {
				ps.setInt(param, codPistaProdam);
				param ++;
			}
			if (enquadramento != null) {
				ps.setString(param, enquadramento);
				param ++;
			}
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				itemRelatorio = new ItemEficienciaEquipamento(
													rs.getString("Data"),
													rs.getInt("cod_pista"),
													rs.getString("descricao"),
													rs.getInt("cod_pista_prodam"),
													rs.getInt("faixa"),
													rs.getString("data_inicio_operacao"),
													rs.getString("enquadramento_habilitado"),
													rs.getString("inconsistencia_CAI_motivo"),
													rs.getInt("inconsistencia_CAI_qtde"),
													rs.getInt("equipamento_estatico")
													);
				
				listaItensRelatorio.add(itemRelatorio);
			}
			
			return listaItensRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	

	/**
	 * Classe de negócio para busca de dados para os relatórios de eficiência do equipamento - Relatório de Enquadramento Habilitado x Enquadramento Efetivo.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 30/06/2016
	 */
	public ArrayList<ItemEficienciaEquipamento> enquadramentoHabilitadoEfetivo(Date dataInicio, Date dataFim,
							Integer codPista, Integer codPistaProdam, String enquadramento) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		String strSQLWhere = " WHERE ";

		sbSQL.append(" DECLARE @data_inicio DATE = ? ");
		sbSQL.append(" DECLARE @data_fim DATE = ? ");
		
		if (codPista != null) {
			sbSQL.append(" DECLARE @cod_pista INT = ? ");
		}
		if (codPistaProdam != null) {
			sbSQL.append(" DECLARE @cod_pista_prodam INT = ? ");
		}
		if (enquadramento != null) {
			sbSQL.append(" DECLARE @enquadramento VARCHAR(40) = ? ");
		}

		sbSQL.append(" SELECT CONVERT(VARCHAR(10), datas.Data, 103) AS Data ");
		sbSQL.append("       ,ea.cod_pista ");
		sbSQL.append("       ,COALESCE(equip_prodam.cod_pista_prodam, cem.cod_pista_prodam) AS cod_pista_prodam ");
		sbSQL.append("       ,ea.cod_pista_alternativo AS faixa ");
		sbSQL.append("       ,cem.descricao ");
		sbSQL.append("       ,CONVERT(VARCHAR(10), cem.data_inicio, 103) AS data_inicio_operacao ");
		sbSQL.append("       ,LTRIM(RTRIM(ea.descricao_apait)) AS enquadramento_habilitado ");
		sbSQL.append("       ,CASE WHEN ea.ativo = 0 OR manut.id_manutencao IS NOT NULL THEN 'NÃO' ELSE 'SIM' END AS enquadramento_efetivo ");
		sbSQL.append("       ,COUNT(i.id_infracao) AS total_imagens_captadas ");
		sbSQL.append("       ,SUM(CASE WHEN mi.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS total_consistente_CAI ");
		sbSQL.append("       ,CASE WHEN cem.serie_equipamento < 9907000 THEN 1 ELSE 0 END equipamento_estatico ");

		sbSQL.append(" FROM   dbo.fcn_ObterDatasPeriodo(@data_inicio, @data_fim) datas ");
		sbSQL.append("		  LEFT JOIN enquadramento_ativo ea (NOLOCK) ");
		sbSQL.append("			   ON  datas.Data >= ea.data_inicio ");
		sbSQL.append("				   AND datas.Data < COALESCE(ea.data_fim, CAST(GETDATE() AS DATE)) ");
		sbSQL.append("		  LEFT JOIN configuracao_equipamento_medicao cem (NOLOCK) ");
		sbSQL.append("			   ON  cem.id_local = ea.id_local ");
		sbSQL.append("				   AND cem.cod_pista = ea.cod_pista ");
		sbSQL.append("				   AND cem.cod_pista_prodam = ea.cod_pista_prodam ");
		sbSQL.append("				   AND cem.cod_pista_alternativo = ea.cod_pista_alternativo ");
		sbSQL.append("		  LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append("			   ON  i.id_local = cem.cod_pista ");
		sbSQL.append("				   AND i.pista = cem.cod_pista_alternativo ");
		sbSQL.append("				   AND i.id_enquadramento = ea.id_enquadramento ");
		sbSQL.append("				   AND CAST(i.data AS DATE) = datas.Data ");
		sbSQL.append("		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append("			   ON  ir.id_infracao = i.id_infracao ");
		sbSQL.append("		  LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append("			   ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append("		  LEFT JOIN movimento_importacao mi (NOLOCK) ");
//		sbSQL.append("			   ON  mi.id_movimento = r.codigo_externo ");
		sbSQL.append("  			   ON r.id_movimento_arquivo = mi.id_movimento_arquivo ");
		sbSQL.append("				   AND mi.sequencia = ir.sequencia ");
//		sbSQL.append("				   AND mi.id_enquadramento = i.id_enquadramento ");
		sbSQL.append("		  LEFT JOIN ( ");
		sbSQL.append("		  				SELECT aux.id_local ");
		sbSQL.append("		  					  ,aux.pista ");
		sbSQL.append("		  					  ,aux.cod_pista_prodam ");
		sbSQL.append("		  					  ,CAST(aux.data_hora AS DATE) AS data ");
		sbSQL.append("		  				FROM   movimento_importacao aux (NOLOCK) ");
		sbSQL.append("		  				GROUP BY ");
		sbSQL.append("		  					   aux.id_local ");
		sbSQL.append("		  					  ,aux.pista ");
		sbSQL.append("		  					  ,aux.cod_pista_prodam ");
		sbSQL.append("		  					  ,CAST(aux.data_hora AS DATE) ");
		sbSQL.append("		  			) equip_prodam ");
		sbSQL.append("		  	   ON  equip_prodam.id_local = cem.cod_pista ");
		sbSQL.append("		  		   AND equip_prodam.pista = cem.cod_pista_alternativo ");
		sbSQL.append("		  		   AND equip_prodam.data = datas.Data ");
		sbSQL.append("		  LEFT JOIN inconsistencia inc (NOLOCK) ");
		sbSQL.append("			   ON  inc.id_inconsistencia = mi.id_inconsistencia ");
		sbSQL.append("		  LEFT JOIN manutencao_cav manut (NOLOCK) ");
		sbSQL.append("		  	   ON  manut.cod_pista = ea.cod_pista ");
		sbSQL.append("				   AND manut.cod_pista_alternativo = ea.cod_pista_alternativo ");
		sbSQL.append("				   AND ( ");
		sbSQL.append("				   			(manut.id_enquadramento = ea.id_enquadramento AND manut.tipo_grupo_autuador = ea.descricao_apait) ");
		sbSQL.append("				   			OR ");
		sbSQL.append("				   			(manut.id_enquadramento IS NULL AND manut.tipo_grupo_autuador = ea.descricao_apait) ");
		sbSQL.append("				   	   ) ");
		sbSQL.append("				   AND datas.Data BETWEEN manut.data_inicio AND manut.data_fim ");
		
		sbSQL.append(strSQLWhere + " cem.cod_pista IS NOT NULL ");
		strSQLWhere = " AND ";
						
		if (codPista != null) {
			sbSQL.append(strSQLWhere + " cem.cod_pista = @cod_pista ");
			strSQLWhere = " AND ";
		}
		
		if (codPistaProdam != null) {
			sbSQL.append(strSQLWhere + " equip_prodam.cod_pista_prodam = @cod_pista_prodam ");
			strSQLWhere = " AND ";
		}
		
		if (enquadramento != null) {
			sbSQL.append(strSQLWhere + " LTRIM(RTRIM(ea.descricao_apait)) = @enquadramento ");
			strSQLWhere = " AND ";
		}
		
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  datas.Data ");
		sbSQL.append(" 		 ,ea.cod_pista "); 
		sbSQL.append(" 		 ,cem.descricao ");
		sbSQL.append("       ,COALESCE(equip_prodam.cod_pista_prodam, cem.cod_pista_prodam) ");
		sbSQL.append(" 		 ,ea.cod_pista_alternativo ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), cem.data_inicio, 103) ");
		sbSQL.append(" 		 ,LTRIM(RTRIM(ea.descricao_apait)) ");
		sbSQL.append(" 		 ,CASE WHEN ea.ativo = 0 OR manut.id_manutencao IS NOT NULL THEN 'NÃO' ELSE 'SIM' END ");
		sbSQL.append("       ,CASE WHEN cem.serie_equipamento < 9907000 THEN 1 ELSE 0 END ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  datas.Data ");
		sbSQL.append(" 		 ,ea.cod_pista "); 
		sbSQL.append("       ,COALESCE(equip_prodam.cod_pista_prodam, cem.cod_pista_prodam) ");
		sbSQL.append(" 		 ,ea.cod_pista_alternativo ");
		sbSQL.append(" 		 ,LTRIM(RTRIM(ea.descricao_apait)) ");
		sbSQL.append(" 		 ,CASE WHEN ea.ativo = 0 OR manut.id_manutencao IS NOT NULL THEN 'NÃO' ELSE 'SIM' END ");
		
		sbSQL.append(" OPTION (MAXRECURSION 0) ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemEficienciaEquipamento> listaItensRelatorio =  new ArrayList<ItemEficienciaEquipamento>();
		ItemEficienciaEquipamento itemRelatorio;
		Integer param = 1;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(param, new java.sql.Date(dataInicio.getTime()));
			param ++;
			ps.setDate(param, new java.sql.Date(dataFim.getTime()));
			param ++;
			
			if (codPista != null) {
				ps.setInt(param, codPista);
				param ++;
			}
			if (codPistaProdam != null) {
				ps.setInt(param, codPistaProdam);
				param ++;
			}
			if (enquadramento != null) {
				ps.setString(param, enquadramento);
				param ++;
			}
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				itemRelatorio = new ItemEficienciaEquipamento(
											rs.getString("Data"),
											rs.getInt("cod_pista"),
											rs.getString("descricao"),
											rs.getInt("cod_pista_prodam"),
											rs.getInt("faixa"),
											rs.getString("data_inicio_operacao"),
											rs.getString("enquadramento_habilitado"),
											rs.getString("enquadramento_efetivo"),
											rs.getInt("total_imagens_captadas"),
											rs.getInt("total_consistente_CAI"),
											rs.getInt("equipamento_estatico")
											);
				
				listaItensRelatorio.add(itemRelatorio);
			}
			
			return listaItensRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}	


	/**
	 * Classe de negócio para busca de dados para os relatórios de eficiência do equipamento - Relatório de Justificativa de Falhas.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 30/07/2016
	 */
	public ArrayList<ItemEficienciaEquipamento> justificativaFalhas(Date dataInicio, Date dataFim,
							Integer codPista, Integer codPistaProdam, String enquadramento) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		String strSQLWhere = " WHERE ";

		sbSQL.append(" DECLARE @data_inicio DATE = ? ");
		sbSQL.append(" DECLARE @data_fim DATE = ? ");
		
		if (codPista != null) {
			sbSQL.append(" DECLARE @cod_pista INT = ? ");
		}
		if (codPistaProdam != null) {
			sbSQL.append(" DECLARE @cod_pista_prodam INT = ? ");
		}
		if (enquadramento != null) {
			sbSQL.append(" DECLARE @enquadramento VARCHAR(40) = ? ");
		}

		sbSQL.append(" SELECT CONVERT(VARCHAR(10), datas.Data, 103) AS Data ");
		sbSQL.append("       ,ea.cod_pista ");
		sbSQL.append("       ,cem.descricao ");
		sbSQL.append("       ,COALESCE(manut.cod_pista_prodam, cem.cod_pista_prodam) AS cod_pista_prodam ");
		sbSQL.append("       ,ea.cod_pista_alternativo AS faixa ");
		sbSQL.append("       ,CONVERT(VARCHAR(10), cem.data_inicio, 103) AS data_inicio_operacao ");
		sbSQL.append("       ,LTRIM(RTRIM(ea.descricao_apait)) AS enquadramento_habilitado ");
		sbSQL.append("       ,CASE WHEN ea.ativo = 0 OR manut.id_manutencao IS NOT NULL THEN 'NÃO' ELSE 'SIM' END AS enquadramento_efetivo ");
		sbSQL.append("       ,CONVERT(VARCHAR(10), manut.data_inicio, 103) AS data_inicio_manut ");
		sbSQL.append("       ,manut.descricao AS descricao_manut ");
		sbSQL.append("       ,CONVERT(VARCHAR(10), manut.data_fim, 103) AS data_fim_manut ");
		sbSQL.append("       ,manut.estado AS estado_manut ");
		sbSQL.append("       ,CASE WHEN cem.serie_equipamento < 9907000 THEN 1 ELSE 0 END equipamento_estatico ");
		
		sbSQL.append(" FROM   dbo.fcn_ObterDatasPeriodo(@data_inicio, @data_fim) datas ");
		sbSQL.append("		  LEFT JOIN enquadramento_ativo ea (NOLOCK) ");
		sbSQL.append("		  	   ON  datas.Data >= ea.data_inicio ");
		sbSQL.append(" 		   		   AND datas.Data < COALESCE(ea.data_fim, CAST(GETDATE() AS DATE)) ");
		sbSQL.append("		  LEFT JOIN configuracao_equipamento_medicao cem (NOLOCK) ");
		sbSQL.append("		  	   ON  cem.id_local = ea.id_local ");
		sbSQL.append("				   AND cem.cod_pista = ea.cod_pista ");
		sbSQL.append("				   AND cem.cod_pista_prodam = ea.cod_pista_prodam ");
		sbSQL.append("				   AND cem.cod_pista_alternativo = ea.cod_pista_alternativo ");
		sbSQL.append("		  LEFT JOIN manutencao_cav manut (NOLOCK) ");
		sbSQL.append("		  	   ON  manut.cod_pista = ea.cod_pista ");
		sbSQL.append("				   AND manut.cod_pista_alternativo = ea.cod_pista_alternativo ");
		sbSQL.append("				   AND ( ");
		sbSQL.append("				   			(manut.id_enquadramento = ea.id_enquadramento AND manut.tipo_grupo_autuador = ea.descricao_apait) ");
		sbSQL.append("				   			OR ");
		sbSQL.append("				   			(manut.id_enquadramento IS NULL AND manut.tipo_grupo_autuador = ea.descricao_apait) ");
		sbSQL.append("				   	   ) ");
		sbSQL.append("				   AND datas.Data BETWEEN manut.data_inicio AND manut.data_fim ");
		
		sbSQL.append(strSQLWhere + " cem.cod_pista IS NOT NULL ");
		strSQLWhere = " AND ";
		
		if (codPista != null) {
			sbSQL.append(strSQLWhere + " cem.cod_pista = @cod_pista ");
			strSQLWhere = " AND ";
		}
		
		if (codPistaProdam != null) {
			sbSQL.append(strSQLWhere + " (cem.cod_pista_prodam = @cod_pista_prodam OR cem.cod_pista_prodam = 0) ");
			strSQLWhere = " AND ";
		}
		
		if (enquadramento != null) {
			sbSQL.append(strSQLWhere + " LTRIM(RTRIM(ea.descricao_apait)) = @enquadramento ");
			strSQLWhere = " AND ";
		}
		
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  datas.Data ");
		sbSQL.append(" 		 ,ea.cod_pista "); 
		sbSQL.append(" 		 ,cem.descricao ");
		sbSQL.append("       ,COALESCE(manut.cod_pista_prodam, cem.cod_pista_prodam) ");
		sbSQL.append(" 		 ,ea.cod_pista_alternativo ");
		sbSQL.append(" 		 ,CONVERT(VARCHAR(10), cem.data_inicio, 103) ");
		sbSQL.append("       ,LTRIM(RTRIM(ea.descricao_apait)) ");
		sbSQL.append("       ,CASE WHEN ea.ativo = 0 OR manut.id_manutencao IS NOT NULL THEN 'NÃO' ELSE 'SIM' END ");
		sbSQL.append(" 		 ,ea.ativo ");
		sbSQL.append("       ,manut.data_inicio ");
		sbSQL.append("       ,manut.descricao ");
		sbSQL.append("       ,manut.data_fim ");
		sbSQL.append("       ,manut.estado ");
		sbSQL.append("       ,CASE WHEN cem.serie_equipamento < 9907000 THEN 1 ELSE 0 END ");
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  datas.Data ");
		sbSQL.append(" 		 ,ea.cod_pista "); 
		sbSQL.append("       ,COALESCE(manut.cod_pista_prodam, cem.cod_pista_prodam) ");
		sbSQL.append(" 		 ,ea.cod_pista_alternativo ");
		sbSQL.append(" 		 ,ea.ativo ");
		
		sbSQL.append(" OPTION (MAXRECURSION 0) ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemEficienciaEquipamento> listaItensRelatorio =  new ArrayList<ItemEficienciaEquipamento>();
		ItemEficienciaEquipamento itemRelatorio;
		Integer param = 1;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(param, new java.sql.Date(dataInicio.getTime()));
			param ++;
			ps.setDate(param, new java.sql.Date(dataFim.getTime()));
			param ++;
			
			if (codPista != null) {
				ps.setInt(param, codPista);
				param ++;
			}
			if (codPistaProdam != null) {
				ps.setInt(param, codPistaProdam);
				param ++;
			}
			if (enquadramento != null) {
				ps.setString(param, enquadramento);
				param ++;
			}
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				itemRelatorio = new ItemEficienciaEquipamento(
											rs.getString("Data"),
											rs.getInt("cod_pista"),
											rs.getString("descricao"),
											rs.getInt("cod_pista_prodam"),
											rs.getInt("faixa"),
											rs.getString("data_inicio_operacao"),
											rs.getString("enquadramento_habilitado"),
											rs.getString("enquadramento_efetivo"),
											rs.getString("data_inicio_manut"),
											rs.getString("descricao_manut"),
											rs.getString("data_fim_manut"),
											rs.getString("estado_manut"),
											rs.getInt("equipamento_estatico")
											);
				
				listaItensRelatorio.add(itemRelatorio);
			}
			
			return listaItensRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	
	/**
	 * Enquadramentos Ativos.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 07/07/2016
	 */
	public static ArrayList<ItemEficienciaEquipamento> enquadramentosAtivos() throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT LTRIM(RTRIM(ea.descricao_apait)) AS enquadramento ");
		sbSQL.append(" FROM   enquadramento_ativo ea (NOLOCK) ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append(" 		  LTRIM(RTRIM(ea.descricao_apait)) ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  LTRIM(RTRIM(ea.descricao_apait)) ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemEficienciaEquipamento> listaEnquadramentosAtivos =  new ArrayList<ItemEficienciaEquipamento>();
		ItemEficienciaEquipamento itemEnquadramentoAtivo;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			
			while (rs.next()){
				
				itemEnquadramentoAtivo = new ItemEficienciaEquipamento(rs.getString("enquadramento"));
				
				listaEnquadramentosAtivos.add(itemEnquadramentoAtivo);
			}
			
			return listaEnquadramentosAtivos;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}

}

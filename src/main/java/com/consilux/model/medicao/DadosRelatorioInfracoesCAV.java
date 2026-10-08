/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 15/08/2016

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
 * Classe de negócio para busca de dados para o relatório de Infrações CAV
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 15/08/2016
 */
public class DadosRelatorioInfracoesCAV {

	/**
	 * Classe de negócio para busca de dados para o relatório de Infrações CAV.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 15/08/2016
	 */
	public ArrayList<ItemRelatorioInfracoesCAV> relInfracoesCAV(Date dataInicio, Date dataFim, String strMotivoInconsistenciaArray, String strCodProdamArray,
																String strCodPistaProdamArray, String strEnquadramentoArray, String strCodOperadorArray,
																String strCodAuditorArray, Boolean filtroInfracao, Boolean filtroAuditoria, Boolean agruparDia,
																Boolean agruparMes, Boolean agruparNenhum, Boolean agruparLocal, Boolean agruparEquipamento,
																Boolean agruparEnquadramento) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();
		String strWhere = " WHERE ";
		String strAux = ",";
		
		String strData = null, strMesAno = null, strDescLocal = null, strDescEquipamento = null;
		Integer intIdEnquadramento = null;
		
		sbSQL.append(" DECLARE @data_inicio DATE = ? ");
		sbSQL.append(" DECLARE @data_fim DATE = ? ");

		
		sbSQL.append(" SELECT sub1.id_inconsistencia ");
		sbSQL.append(" 		 ,RIGHT('00' + CAST(sub1.id_inconsistencia AS VARCHAR), 2) + ' - ' + sub1.desc_inconsistencia AS [motivo_inconsistência] ");
		sbSQL.append(" 		 ,sub1.imagens_consistentes_cai AS [imagens_consistentes_CAI] ");
		sbSQL.append(" 		 ,sub1.imagens_inconsistentes_cai AS [imagens_inconsistentes_CAI] ");
		sbSQL.append(" 		 ,CASE WHEN sub1.total_infracao = 0 THEN 0 ELSE (CAST(sub1.imagens_consistentes_cai AS FLOAT)/CAST(sub1.total_infracao AS FLOAT)) END AS [porcentagem_consist_inconsist] ");
		sbSQL.append(" 		 ,sub1.imagens_validas_cav AS [imagens_validas_CAV] ");
		sbSQL.append(" 		 ,sub1.imagens_invalidas_cav AS [imagens_invalidas_CAV] ");
		sbSQL.append(" 		 ,CASE WHEN sub1.total_infracao = 0 THEN 0 ELSE (CAST(sub1.imagens_validas_cav AS FLOAT)/CAST(sub1.total_infracao AS FLOAT)) END AS [porcentagem_validas_invalidas] ");
		sbSQL.append(" 		 ,sub1.imagens_amostra AS [qtde_amostragem] ");
		sbSQL.append(" 		 ,CASE WHEN sub1.total_infracao = 0 THEN 0 ELSE (CAST(sub1.imagens_amostra AS FLOAT)/CAST(sub1.total_infracao AS FLOAT)) END AS [porcentagem_amostragem] ");
		sbSQL.append(" 		 ,sub1.imagens_fora_amostra AS [qtde_100_porcento] ");
		sbSQL.append(" 		 ,CASE WHEN sub1.total_infracao = 0 THEN 0 ELSE (CAST(sub1.imagens_fora_amostra AS FLOAT)/CAST(sub1.total_infracao AS FLOAT)) END AS [porcentagem_100_porcento] ");
		
		if (agruparDia) {
			agruparMes = false;
			sbSQL.append("        " + strAux + "CONVERT(VARCHAR(10), sub1.dia, 103) AS data ");
		}
		
		if (agruparMes) {
			agruparDia = false;
			sbSQL.append("        " + strAux + "sub1.mes ");
			sbSQL.append("        " + strAux + "sub1.ano ");
			sbSQL.append("        " + strAux + "sub1.mes_ano ");
		}
		
		if (agruparLocal) {
			sbSQL.append("        " + strAux + "sub1.id_local ");
			sbSQL.append("        " + strAux + "CAST(sub1.id_local AS VARCHAR(4)) + ' - ' + sub1.desc_local AS [desc_local] ");
		}
		
		if (agruparEquipamento) {
			sbSQL.append("        " + strAux + "sub1.cod_pista_prodam ");
			sbSQL.append("        " + strAux + "sub1.faixa ");
			sbSQL.append("        " + strAux + "CAST(sub1.cod_pista_prodam AS VARCHAR(4)) + ' - ' + CAST(sub1.faixa AS VARCHAR(2)) AS [equipamento] ");
		}
		
		if (agruparEnquadramento) {
			sbSQL.append("       " + strAux + "sub1.id_enquadramento ");
		}
		
		sbSQL.append(" FROM ( ");
		sbSQL.append(" 			SELECT inc.id_inconsistencia ");
		sbSQL.append("       		  ,inc.descricao AS desc_inconsistencia ");
		sbSQL.append("       		  ,SUM(CASE WHEN mi.id_inconsistencia IS NOT NULL THEN CASE WHEN mi.id_inconsistencia = 0 THEN 1 ELSE 0 END ELSE 0 END) AS imagens_consistentes_cai ");
		sbSQL.append("       		  ,SUM(CASE WHEN mi.id_inconsistencia IS NOT NULL THEN CASE WHEN mi.id_inconsistencia = 0 THEN 0 ELSE 1 END ELSE 0 END) AS imagens_inconsistentes_cai ");
		sbSQL.append("       		  ,SUM(CASE WHEN i.id_inconsistencia IS NOT NULL THEN CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END ELSE 0 END) AS imagens_validas_cav ");
		sbSQL.append("       		  ,SUM(CASE WHEN i.id_inconsistencia IS NOT NULL THEN CASE WHEN i.id_inconsistencia = 0 THEN 0 ELSE 1 END ELSE 0 END) AS imagens_invalidas_cav  ");
		sbSQL.append("       		  ,SUM(CASE WHEN sub1.id_remessa IS NULL THEN 0 ELSE 1 END) AS imagens_amostra ");
		sbSQL.append("       		  ,SUM(CASE WHEN sub1.id_remessa IS NULL THEN 1 ELSE 0 END) AS imagens_fora_amostra ");
		sbSQL.append("       		  ,COUNT(i.id_infracao) AS total_infracao ");
		
		if (agruparDia) {
			agruparMes = false;
			if (filtroInfracao) {
				filtroAuditoria = false;
				sbSQL.append("       		 " + strAux + "CAST(i.data AS DATE) AS dia ");
			}
			if (filtroAuditoria) {
				filtroInfracao = false;
				sbSQL.append("       		 " + strAux + "CAST(ip_v.data AS DATE) AS dia ");
			}
		}
		
		if (agruparMes) {
			agruparDia = false;
			if (filtroInfracao) {
				filtroAuditoria = false;
				sbSQL.append("       		 " + strAux + "DATEPART(MONTH, i.data) AS mes ");
				sbSQL.append("       		 " + strAux + "DATEPART(YEAR, i.data) AS ano ");
				sbSQL.append("       		 " + strAux + "RIGHT('00' + CAST(DATEPART(MONTH, i.data) AS VARCHAR(2)), 2) + '/' + RIGHT('0000' + CAST(DATEPART(YEAR, i.data) AS VARCHAR(4)), 4) AS mes_ano ");
			}
			if (filtroAuditoria) {
				filtroInfracao = false;
				sbSQL.append("       		 " + strAux + "DATEPART(MONTH, ip_v.data) AS mes ");
				sbSQL.append("       		 " + strAux + "DATEPART(YEAR, ip_v.data) AS ano ");
				sbSQL.append("       		 " + strAux + "RIGHT('00' + CAST(DATEPART(MONTH, ip_v.data) AS VARCHAR(2)), 2) + '/' + RIGHT('0000' + CAST(DATEPART(YEAR, ip_v.data) AS VARCHAR(4)), 4) AS mes_ano ");
			}
		}
		
		if (agruparLocal) {
			sbSQL.append("       		 " + strAux + "mi.id_local ");
			sbSQL.append("       		 " + strAux + "LTRIM(RTRIM(mi.descricao_local)) AS desc_local ");
		}
		
		if (agruparEquipamento) {
			sbSQL.append("       		 " + strAux + "mi.cod_pista_prodam ");
			sbSQL.append("       		 " + strAux + "mi.pista AS faixa ");
		}
		
		if (agruparEnquadramento) {
			sbSQL.append("       		 " + strAux + "i.id_enquadramento ");
		}
		

		sbSQL.append(" 			FROM   inconsistencia inc (NOLOCK) ");
		sbSQL.append(" 				   LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append(" 				   		ON  i.id_inconsistencia = inc.id_inconsistencia ");
		sbSQL.append(" 		  		   			AND i.id_processo = 25 ");
		if (filtroInfracao) {
			filtroAuditoria = false;
			sbSQL.append(" 		  		   			AND CAST(i.data AS DATE) BETWEEN @data_inicio AND @data_fim ");
		}
		
		sbSQL.append(" 				   LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append(" 				   		ON  ir.id_infracao = i.id_infracao ");
		sbSQL.append(" 				   LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append(" 				   		ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append(" 				   LEFT JOIN ( ");
		sbSQL.append(" 		  						SELECT id_remessa ");
		sbSQL.append(" 		  						FROM   remessa_amostragem (NOLOCK) ");
		sbSQL.append(" 		  						GROUP BY ");
		sbSQL.append(" 		  					   		   id_remessa ");
		sbSQL.append(" 				   ) AS sub1 ");
		sbSQL.append(" 				   		ON  ir.id_remessa = sub1.id_remessa ");
		sbSQL.append(" 				   LEFT JOIN movimento_importacao mi (NOLOCK) ");
		sbSQL.append(" 				   		ON  mi.id_movimento_arquivo = r.id_movimento_arquivo ");
		sbSQL.append(" 		  		   			AND mi.sequencia = ir.sequencia ");
		sbSQL.append(" 				   LEFT JOIN ( ");
		sbSQL.append(" 		  						SELECT MAX(id_infracao_processo) AS id_infracao_processo, ");
		sbSQL.append(" 		  					   		   id_infracao, ");
		sbSQL.append(" 		  					   		   id_processo ");
		sbSQL.append(" 		  						FROM   infracao_processo ip (NOLOCK) ");
		sbSQL.append(" 		  						WHERE  ip.id_processo = 3 ");
		if (filtroAuditoria) {
			filtroInfracao = false;
			sbSQL.append(" 		  					   		   AND CAST(ip.data AS DATE) BETWEEN @data_inicio AND @data_fim ");
		}
		sbSQL.append(" 		  						GROUP BY ");
		sbSQL.append(" 		  					   		   id_infracao, ");
		sbSQL.append(" 		  					   		   id_processo ");
		sbSQL.append(" 				   ) AS ultima_validacao ");
		sbSQL.append(" 				   		ON  ultima_validacao.id_infracao = ir.id_infracao ");
		sbSQL.append(" 		  	   	   			AND ultima_validacao.id_processo = 3 ");
		
		sbSQL.append(" 				   LEFT JOIN infracao_processo ip_v (NOLOCK) ");
		sbSQL.append(" 				   		ON  ip_v.id_infracao_processo = ultima_validacao.id_infracao_processo ");
		sbSQL.append(" 				   LEFT JOIN sis_usuario auditor (NOLOCK) ");
		sbSQL.append(" 				   		ON  auditor.id_usuario = ip_v.id_usuario ");
		sbSQL.append(" 				   LEFT JOIN sis_usuario_importacao operador (NOLOCK) ");
		sbSQL.append(" 				   		ON  operador.id_usuario = mi.cod_operador ");
		
		if (agruparDia || agruparMes || agruparLocal || agruparEquipamento || agruparEnquadramento) {
			sbSQL.append(" 			" + strWhere + " i.id_inconsistencia IS NOT NULL ");
			strWhere = " AND ";
		}
		
		if (filtroAuditoria) {
			sbSQL.append(" 			" + strWhere + " ip_v.id_infracao_processo IS NOT NULL ");
			strWhere = " AND ";
		}
		
		
		if (strMotivoInconsistenciaArray != null) {
			sbSQL.append(" 			" + strWhere + " inc.id_inconsistencia IN (" + strMotivoInconsistenciaArray.trim() + ") ");
			strWhere = " AND ";
		}
		
		if (strCodProdamArray != null) {
			sbSQL.append(" 			" + strWhere + " mi.id_local IN (" + strCodProdamArray.trim() + ") ");
			strWhere = " AND ";
		}
		
		if (strCodPistaProdamArray != null) {
			sbSQL.append(" 			" + strWhere + " mi.cod_pista_prodam IN (" + strCodPistaProdamArray.trim() + ") ");
			strWhere = " AND ";
		}
		
		if (strEnquadramentoArray != null) {
			sbSQL.append(" 			" + strWhere + " i.id_enquadramento IN (" + strEnquadramentoArray.trim() + ") ");
			strWhere = " AND ";
		}
		
		if (strCodOperadorArray != null) {
			sbSQL.append(" 			" + strWhere + " operador.id_usuario IN (" + strCodOperadorArray.trim() + ") ");
			strWhere = " AND ";
		}
		
		if (strCodAuditorArray != null) {
			sbSQL.append(" 			" + strWhere + " auditor.id_usuario IN (" + strCodAuditorArray.trim() + ") ");
			strWhere = " AND ";
		}
		
		sbSQL.append(" 			GROUP BY ");
		sbSQL.append(" 		  		   inc.id_inconsistencia ");
		sbSQL.append("       		  ,inc.descricao ");
		
		if (agruparDia) {
			agruparMes = false;
			if (filtroInfracao) {
				filtroAuditoria = false;
				sbSQL.append("       		 " + strAux + "CAST(i.data AS DATE) ");
			}
			if (filtroAuditoria) {
				filtroInfracao = false;
				sbSQL.append("       		 " + strAux + "CAST(ip_v.data AS DATE) ");
			}
		}
		
		if (agruparMes) {
			agruparDia = false;
			if (filtroInfracao) {
				filtroAuditoria = false;
				sbSQL.append("       		 " + strAux + "DATEPART(MONTH, i.data) ");
				sbSQL.append("       		 " + strAux + "DATEPART(YEAR, i.data) ");
			}
			if (filtroAuditoria) {
				filtroInfracao = false;
				sbSQL.append("       		 " + strAux + "DATEPART(MONTH, ip_v.data) ");
				sbSQL.append("       		 " + strAux + "DATEPART(YEAR, ip_v.data) ");
			}
		}
		
		if (agruparLocal) {
			sbSQL.append("       		 " + strAux + "mi.id_local ");
			sbSQL.append("       		 " + strAux + "LTRIM(RTRIM(mi.descricao_local)) ");
		}
		
		if (agruparEquipamento) {
			sbSQL.append("       		 " + strAux + "mi.cod_pista_prodam ");
			sbSQL.append("       		 " + strAux + "mi.pista ");
		}
		
		if (agruparEnquadramento) {
			sbSQL.append("       		 " + strAux + "i.id_enquadramento ");
		}
		
		sbSQL.append(" ) AS sub1 ");
		
		sbSQL.append(" ORDER BY ");
		
		strAux = "";
		if (agruparDia) {
			agruparMes = false;
			sbSQL.append("       " + strAux + "sub1.dia ");
			strAux = ",";
		}
		
		if (agruparMes) {
			agruparDia = false;
			sbSQL.append("       " + strAux + "sub1.ano ");
			strAux = ",";
			sbSQL.append("       " + strAux + "sub1.mes ");
		}
		
		if (agruparLocal) {
			sbSQL.append("       " + strAux + "sub1.id_local ");
			strAux = ",";
		}
		
		if (agruparEquipamento) {
			sbSQL.append("       " + strAux + "sub1.cod_pista_prodam ");
			strAux = ",";
			sbSQL.append("       " + strAux + "sub1.faixa ");
		}
		
		if (agruparEnquadramento) {
			sbSQL.append("       " + strAux + "sub1.id_enquadramento ");
			strAux = ",";
		}
		
		sbSQL.append("       " + strAux + "sub1.id_inconsistencia ");
		strAux = ",";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelatorioInfracoesCAV> listaItensRelatorio =  new ArrayList<ItemRelatorioInfracoesCAV>();
		ItemRelatorioInfracoesCAV itemRelatorio;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(1, new java.sql.Date(dataInicio.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
						
			rs = ps.executeQuery();
			while (rs.next()){
				
				if (agruparDia) {
					agruparMes = false;
					strData = rs.getString("data");
				}
				
				if (agruparMes) {
					agruparDia = false;
					strMesAno = rs.getString("mes_ano");
				}
				
				if (agruparLocal) {
					strDescLocal = rs.getString("desc_local");
				}
				
				if (agruparEquipamento) {
					strDescEquipamento = rs.getString("equipamento");
				}
				
				if (agruparEnquadramento) {
					intIdEnquadramento = rs.getInt("id_enquadramento");
				}
				
				itemRelatorio = new ItemRelatorioInfracoesCAV(
												rs.getInt("id_inconsistencia"),
												rs.getString("motivo_inconsistência"),
												rs.getInt("imagens_consistentes_CAI"),
												rs.getInt("imagens_inconsistentes_CAI"),
												rs.getDouble("porcentagem_consist_inconsist"),
												rs.getInt("imagens_validas_CAV"),
												rs.getInt("imagens_invalidas_CAV"),
												rs.getDouble("porcentagem_validas_invalidas"),
												rs.getInt("qtde_amostragem"),
												rs.getDouble("porcentagem_amostragem"),
												rs.getInt("qtde_100_porcento"),
												rs.getDouble("porcentagem_100_porcento"),
												strData,
												strMesAno,
												strDescLocal,
												strDescEquipamento,
												intIdEnquadramento
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
}

/**
 * 
 */
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * @author thiago.surgik
 *
 */
public class IndicadoresImportacao {


	public Integer diasComImportacaoAtrasada(int mes, int ano) throws ConexaoException, SQLException, ModelException {
		StringBuilder sbSQL = new StringBuilder();
		
		
		sbSQL.append(" SELECT id_local "); 
		sbSQL.append("       ,cod_pista_prodam ");
		sbSQL.append("       ,serie_equipamento ");
		sbSQL.append("       ,faixa ");
		sbSQL.append("  	 ,codigo_pista ");
		sbSQL.append("       ,local AS desc_local ");
		sbSQL.append("       ,data_publicacao ");
		sbSQL.append("       ,flag_funcionamento ");
		sbSQL.append("       ,mes ");
		sbSQL.append("       ,ano ");
		sbSQL.append("       ,data_inicio_dados ");
		sbSQL.append("       ,data_fim_dados ");
		sbSQL.append("       ,DATEPART(DAY, data_inicio_dados) AS dia_inicio_dados ");
		sbSQL.append("       ,DATEPART(DAY, data_fim_dados) AS dia_fim_dados ");
		sbSQL.append("       ,DATEPART(DAY, DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(CAST(ano AS VARCHAR(4)) + '-' + RIGHT('00' + CAST(mes AS VARCHAR(2)), 2) + '-01' AS DATE)))) AS ultimo_dia_mes ");
		sbSQL.append("       ,DATEPART(DAY, GETDATE()) AS dia_atual ");
		sbSQL.append("       ,CASE WHEN DATEPART(MONTH, GETDATE()) = mes " );
		sbSQL.append("       	   THEN 1 ");
		sbSQL.append("       	   ELSE 0 END AS consulta_mes_atual ");
		sbSQL.append("       ,[1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31] ");
		sbSQL.append(" FROM   dbo.indicadores_importacao ");
		
		sbSQL.append(" WHERE  mes = ? ");
		sbSQL.append("        AND ano = ? ");
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append("        codigo_pista ");
		sbSQL.append("       ,faixa ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer qtdeDiasAtraso = 0;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, mes);
			ps.setLong(2, ano);
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				for(int i=1; i<32; i++){

					if((rs.getLong(String.valueOf(i)) == 0 && rs.getInt("flag_funcionamento") == 0) || (rs.getLong(String.valueOf(i)) == 0 && rs.getInt("flag_funcionamento") > i)){
//						if(rs.getInt("consulta_mes_atual") > 0){
							if( (Integer.parseInt(String.valueOf(i)) >= rs.getInt("dia_inicio_dados")) && Integer.parseInt(String.valueOf(i)) <= rs.getInt("dia_fim_dados") ) {
								qtdeDiasAtraso++;
							}
//						}else if(Integer.parseInt(String.valueOf(i)) < rs.getInt("ultimo_dia_mes")) {
//							qtdeDiasAtraso++;
//						}
					}
				}
				
			}
			
			return qtdeDiasAtraso;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
		
	}

}

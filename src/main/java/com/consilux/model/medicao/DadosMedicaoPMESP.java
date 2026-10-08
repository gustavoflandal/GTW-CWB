/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 15/09/2016

*********************************************************************************/

package com.consilux.model.medicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados para o relatório de medição da PMESP.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 15/09/2016
 */
public class DadosMedicaoPMESP {

	/**
	 * Classe de negócio para busca de dados para o relatório de medição da PMESP - Relatório de Envio de Placas PMESP.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 15/09/2016
	 */
	public ArrayList<ItemMedicaoPMESP> relatorioEnvioPlacasPMESP(Long mes, Long ano) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT relPM.item, ");
		sbSQL.append(" 		  relPM.serie_equipamento, ");
		sbSQL.append(" 		  relPM.cod_pista, ");
		sbSQL.append(" 		  relPM.cod_pista_prodam, ");
		sbSQL.append(" 		  relPM.faixa, ");
		sbSQL.append(" 		  relPM.nome_pista, ");
		sbSQL.append(" 		  relPM.[1],relPM.[2],relPM.[3],relPM.[4],relPM.[5],relPM.[6],relPM.[7],relPM.[8],relPM.[9],relPM.[10], ");
		sbSQL.append(" 		  relPM.[11],relPM.[12],relPM.[13],relPM.[14],relPM.[15],relPM.[16],relPM.[17],relPM.[18],relPM.[19],relPM.[20], ");
		sbSQL.append(" 		  relPM.[21],relPM.[22],relPM.[23],relPM.[24],relPM.[25],relPM.[26],relPM.[27],relPM.[28],relPM.[29],relPM.[30],relPM.[31], ");
		sbSQL.append(" 		  relPM.dias_ok, ");
		sbSQL.append(" 		  relPM.atraso_ok, ");
		sbSQL.append(" 		  relPM.atraso_nok, ");
		sbSQL.append(" 		  relPM.movimentos, ");
		sbSQL.append(" 		  relPM.porc_atraso_ok, ");
		sbSQL.append(" 		  relPM.porc_atraso_nok ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorioMedicaoPMESPEnvioPlacas(@Data_Ini, @Data_Fim) relPM ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  relPM.item ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemMedicaoPMESP> listItensMedicao =  new ArrayList<ItemMedicaoPMESP>();
		ItemMedicaoPMESP itemMedicaoPMESP;
		String[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1,ano);
			ps.setLong(2,mes);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				
				celulas = new String[31];
				
				int valor = 0;
				
				for(int i=1; i<32; i++){
					valor = 0;
					valor = (int) rs.getInt(String.valueOf(i));
					celulas[i-1] = String.valueOf(valor) + "%";
				}
				
				itemMedicaoPMESP = new ItemMedicaoPMESP( 
										rs.getInt("item"),
										rs.getLong("serie_equipamento"),
										rs.getInt("cod_pista"),
										rs.getInt("cod_pista_prodam"),
										rs.getString("faixa"),
										rs.getString("nome_pista"),
										celulas,
										rs.getInt("dias_ok"),
										rs.getInt("atraso_ok"),
										rs.getInt("atraso_nok"),
										rs.getInt("movimentos"),
										rs.getInt("porc_atraso_ok"),
										rs.getInt("porc_atraso_nok"));
				
				listItensMedicao.add(itemMedicaoPMESP);
			}
			
			return listItensMedicao;
				
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}	
}

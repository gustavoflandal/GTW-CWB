/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 28/09/2016

*********************************************************************************/

package com.consilux.model.relatorio.rj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados para o relatório de ocorrências de manutenção
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 28/08/2019
 */
public class DadosRelOcorrenciaManut {
	
	/**
	 * Função para busca de dados para o relatório de acompanhamento de ocorrências.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 28/08/2019
	 */
	public ArrayList<ItemRelOcorrenciaManut> relatorioAcompanhamentoOcorrenciaManut(Integer intMes, Integer intAno, Integer intIdLocal) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" EXEC dbo.spu_getRelatorioOcorrenciaManut @Data_Ini, @Data_Fim, @Id_Local ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelOcorrenciaManut> listDadosRelatorio =  new ArrayList<ItemRelOcorrenciaManut>();
		ItemRelOcorrenciaManut itens;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				itens = new ItemRelOcorrenciaManut(rs.getInt("id_manutencao"),
											  	   rs.getTimestamp("data_ocorrencia"),
											  	   rs.getTimestamp("data_cadastro"),
											  	   rs.getTimestamp("data_inicio"),
											  	   rs.getTimestamp("data_termino"),
											  	   rs.getString("descricao"),
											  	   rs.getString("comentario")
											);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
}

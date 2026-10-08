/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 11/08/2016

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
 * Classe de negócio para busca de dados para o relatório de Produtividade de Auditoria
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 11/08/2016
 */
public class DadosProdutividadeAuditoria {

	/**
	 * Classe de negócio para busca de dados para o relatório de Produtividade Auditoria.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/08/2016
	 */
	public ArrayList<ItemProdutividadeAuditoria> produtividadeAuditoria(Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @dataInicio DATE = ? ");
		sbSQL.append(" DECLARE @dataFim DATE = ? ");
		
		sbSQL.append(" SELECT prod.data ");
		sbSQL.append("       ,prod.[login] AS [login] ");
		sbSQL.append("       ,prod.auditor ");
		sbSQL.append("       ,prod.horario_inicio ");
		sbSQL.append("       ,prod.[0] AS [valor0] ");
		sbSQL.append("       ,prod.[1] AS [valor1] ");
		sbSQL.append("       ,prod.[2] AS [valor2] ");
		sbSQL.append("       ,prod.[3] AS [valor3] ");
		sbSQL.append("       ,prod.[4] AS [valor4] ");
		sbSQL.append("       ,prod.[5] AS [valor5] ");
		sbSQL.append("       ,prod.[6] AS [valor6] ");
		sbSQL.append("       ,prod.[7] AS [valor7] ");
		sbSQL.append("       ,prod.[8] AS [valor8] ");
		sbSQL.append("       ,prod.[9] AS [valor9] ");
		sbSQL.append("       ,prod.[10] AS [valor10] ");
		sbSQL.append("       ,prod.[11] AS [valor11] ");
		sbSQL.append("       ,prod.[12] AS [valor12] ");
		sbSQL.append("       ,prod.[13] AS [valor13] ");
		sbSQL.append("       ,prod.[14] AS [valor14] ");
		sbSQL.append("       ,prod.[15] AS [valor15] ");
		sbSQL.append("       ,prod.[16] AS [valor16] ");
		sbSQL.append("       ,prod.[17] AS [valor17] ");
		sbSQL.append("       ,prod.[18] AS [valor18] ");
		sbSQL.append("       ,prod.[19] AS [valor19] ");
		sbSQL.append("       ,prod.[20] AS [valor20] ");
		sbSQL.append("       ,prod.[21] AS [valor21] ");
		sbSQL.append("       ,prod.[22] AS [valor22] ");
		sbSQL.append("       ,prod.[23] AS [valor23] ");
		sbSQL.append("       ,prod.total ");

		sbSQL.append(" FROM   dbo.fcn_getRelatorioProdutividadeAuditoria(@dataInicio, @dataFim) prod ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  prod.ordem ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemProdutividadeAuditoria> listaItensRelatorio =  new ArrayList<ItemProdutividadeAuditoria>();
		ItemProdutividadeAuditoria itemRelatorio;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(1, new java.sql.Date(dataInicio.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				itemRelatorio = new ItemProdutividadeAuditoria(rs.getString("data"),
												rs.getString("login"),
												rs.getString("auditor"),
												rs.getString("horario_inicio"),
												rs.getInt("valor0"),
												rs.getInt("valor1"),
												rs.getInt("valor2"),
												rs.getInt("valor3"),
												rs.getInt("valor4"),
												rs.getInt("valor5"),
												rs.getInt("valor6"),
												rs.getInt("valor7"),
												rs.getInt("valor8"),
												rs.getInt("valor9"),
												rs.getInt("valor10"),
												rs.getInt("valor11"),
												rs.getInt("valor12"),
												rs.getInt("valor13"),
												rs.getInt("valor14"),
												rs.getInt("valor15"),
												rs.getInt("valor16"),
												rs.getInt("valor17"),
												rs.getInt("valor18"),
												rs.getInt("valor19"),
												rs.getInt("valor20"),
												rs.getInt("valor21"),
												rs.getInt("valor22"),
												rs.getInt("valor23"),
												rs.getInt("total")
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

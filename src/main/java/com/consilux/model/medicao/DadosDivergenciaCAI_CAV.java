/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 21/08/2014

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
 * Classe de negócio para busca de dados para orelatório de Divergência CAI-CAV.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 10/08/2016
 */
public class DadosDivergenciaCAI_CAV {

	/**
	 * Classe de negócio para busca de dados para o relatório de Divergência CAI-CAV.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 10/08/2016
	 */
	public ArrayList<ItemDivergenciaCAI_CAV> divergenciaCAICAV(Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @dataInicio DATE = ? ");
		sbSQL.append(" DECLARE @dataFim DATE = ? ");
		
		sbSQL.append(" SELECT erros.cod_pista ");
		sbSQL.append("       ,erros.grupo_auditor ");
		sbSQL.append("       ,erros.codigo_externo ");
		sbSQL.append("       ,erros.sequencia ");
		sbSQL.append("       ,erros.data_infracao ");
		sbSQL.append("       ,erros.nome_imagem ");
		sbSQL.append("       ,erros.status_CAI ");
		sbSQL.append("       ,erros.status_CAV ");
		sbSQL.append("       ,erros.revisao ");
		sbSQL.append("       ,erros.tipo_erro ");
		sbSQL.append("       ,erros.motivo_CAI ");
		sbSQL.append("       ,erros.motivo_CAV ");
		sbSQL.append("       ,erros.placa_CAI ");
		sbSQL.append("       ,erros.placa_CAV ");
		sbSQL.append("       ,erros.marca_CAI ");
		sbSQL.append("       ,erros.marca_CAV ");
		sbSQL.append("       ,erros.digitador ");
		sbSQL.append("       ,erros.auditor ");

		sbSQL.append(" FROM   dbo.fcn_getRelatorioErrosValidacaoCAV(@dataInicio, @dataFim) erros ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  erros.grupo_auditor ");
		sbSQL.append(" 		 ,erros.codigo_externo ");
		sbSQL.append(" 		 ,erros.sequencia ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemDivergenciaCAI_CAV> listaItensRelatorio =  new ArrayList<ItemDivergenciaCAI_CAV>();
		ItemDivergenciaCAI_CAV itemRelatorio;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(1, new java.sql.Date(dataInicio.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			rs = ps.executeQuery();
			while (rs.next()){		
				
				itemRelatorio = new ItemDivergenciaCAI_CAV(rs.getInt("cod_pista"),
												rs.getString("grupo_auditor"),
												rs.getInt("codigo_externo"),
												rs.getInt("sequencia"),
												rs.getString("data_infracao"),
												rs.getString("nome_imagem"),
												rs.getString("status_CAI"),
												rs.getString("status_CAV"),
												rs.getInt("revisao"),
												rs.getString("tipo_erro"),
												rs.getString("motivo_CAI"),
												rs.getString("motivo_CAV"),
												rs.getString("placa_CAI"),
												rs.getString("placa_CAV"),
												rs.getInt("marca_CAI"),
												rs.getInt("marca_CAV"),
												rs.getString("digitador"),
												rs.getString("auditor")
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

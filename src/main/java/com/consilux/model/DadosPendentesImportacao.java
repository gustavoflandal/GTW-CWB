/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Edson Jan Ferreira Lopes
  Data: 14/04/2010

  Descricao: Classe Retorna um relatório para a quantidade de infrações em processamento em processos que não sejam finais (remessas, Remessa de Inconsistencia).


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe para buscar infrações em processos não finais
 * @author Edson Jan F Lopes- Consilux Tecnologia
 */
public class DadosPendentesImportacao {

	private Date data;
	private int totalVeiculos;
	private int totalVeiculosComImagem;

	public DadosPendentesImportacao(
			Date data,
			int totalVeiculos,
			int totalVeiculosComImagem){

		this.data = data;
		this.totalVeiculos = totalVeiculos;
		this.totalVeiculosComImagem = totalVeiculosComImagem;

	}

	/**
	 * Busca Dados Pendentes na Importacao no BD
	 * @return Lista de objetos DadosPendentesImportacao
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<DadosPendentesImportacao> buscaDadosPendentesImportacao() throws ConexaoException, SQLException  {

		List<DadosPendentesImportacao> lRet = new ArrayList<DadosPendentesImportacao>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT "); 
		sbSQL.append("  CAST(data as date) as data ");
		sbSQL.append("  ,COUNT(*) as total_veiculos ");
		sbSQL.append("  ,SUM(CASE WHEN ii.id_veiculo_unic IS NOT NULL THEN 1 ELSE 0 END) as total_veiculos_com_imagem ");
		sbSQL.append("FROM ");
		sbSQL.append("  veiculo_importacao vi WITH(NOLOCK) ");
		sbSQL.append("  left join infracao_importacao ii WITH(NOLOCK) ");
		sbSQL.append("    ON ii.id_veiculo_unic = vi.id_veiculo_unic ");
		sbSQL.append("WHERE vi.importar = 1 ");
		sbSQL.append("GROUP BY ");
		sbSQL.append("  CAST(data as date) ");			
		sbSQL.append("ORDER BY 1 "); 

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new DadosPendentesImportacao(
						rs.getDate("data"), 
						rs.getInt("total_veiculos"), 
						rs.getInt("total_veiculos_com_imagem")
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
	 * Retorna o valor do campo 'data' atual.
	 * @return the data
	 */
	public Date getData() {
		return this.data;
	}

	public int getTotalVeiculos() {
		return totalVeiculos;
	}

	public int getTotalVeiculosComImagem() {
		return totalVeiculosComImagem;
	}

}

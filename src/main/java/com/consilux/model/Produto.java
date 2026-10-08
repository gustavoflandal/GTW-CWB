/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 22/09/2015

  Descrição: Classe de negócio que busca informações básicas de um produto.

*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio que busca informações básicas de um produto.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 22/09/2015
 */
public class Produto {
	private Integer idProduto;
	private String descricao;
	private String modelo;
	private String sigla;
	
	public Produto(Integer idProduto, String descricao, String modelo, String sigla) {
		super();
		this.idProduto = idProduto;
		this.descricao = descricao;
		this.modelo = modelo;
		this.sigla = sigla;
	}
	
	/**
	 * Busca produtos no BD.
	 * @return Lista de objetos Produto
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Produto> buscarTodosProdutos() throws ConexaoException, SQLException {
		
		List<Produto> lRet = new ArrayList<Produto>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT p.id_produto ");
		sbSQL.append("		 ,LTRIM(RTRIM(p.descricao)) AS descricao ");
		sbSQL.append("		 ,LTRIM(RTRIM(p.modelo)) AS modelo ");
		sbSQL.append("		 ,LTRIM(RTRIM(p.sigla)) AS sigla ");
		sbSQL.append(" FROM   produto p (NOLOCK) ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  p.descricao ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new Produto(rs.getInt("id_produto"),
									 rs.getString("descricao"),
									 rs.getString("modelo"),
									 rs.getString("sigla"))
						);
			}
			
		} catch (SQLException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		
		return lRet;
	}


	
	public Integer getIdProduto() {
		return idProduto;
	}
	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
}

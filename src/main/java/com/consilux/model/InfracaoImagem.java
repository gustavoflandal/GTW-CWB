/**********************************************************************************


  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/07/2010

  Descricao: Classe de negócio para controle das imagens escolhidas

  Histórico:

    $Log$


*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;


/**
 * Classe de negócio para controle das imagens escolhidas.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision$ $Date$ $Author: fos $
 */
public class InfracaoImagem {
	private Integer idInfracao = null;
	private Integer idImagemObj = null;
	private Integer idImagemPan1 = null;
	private Integer idImagemPan2 = null;
	
	/**
	 * Constrói o objeto InfracaoImagem com os seus respectivos atributos.
	 * @param request Referência a requisição HTTP
	 * @param response Referência a resposta HTTP
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	
	/**
	 * Constrói o objeto Acesso com os seus respectivos atributos.
	 * @param idInfracao
	 * @param idImagemObj
	 * @param idImagemPan1
	 * @param idImagemPan2
	 */
	public InfracaoImagem(Integer idInfracao, Integer idImagemObj,
			Integer idImagemPan1, Integer idImagemPan2) {
		super();
		this.idInfracao = idInfracao;
		this.idImagemObj = idImagemObj;
		this.idImagemPan1 = idImagemPan1;
		this.idImagemPan2 = idImagemPan2;
	}

	public static InfracaoImagem buscarInfracaoImagemPorIdInfracao(Integer idInfracao) throws ConexaoException, SQLException, ModelException {
		List<InfracaoImagem> lret = null;
		
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("id_infracao", idInfracao);
		lret = buscarInfracaoImagemPor(mFiltros);
		
		return lret != null && lret.size() > 0 ? lret.get(0) : null; 
	}
	public static InfracaoImagem buscarInfracaoImagemPorIdImagemObj(Integer IdImagemObj) throws ConexaoException, SQLException, ModelException {
		List<InfracaoImagem> lret = null;
		
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("id_infracao_obj", IdImagemObj);
		lret = buscarInfracaoImagemPor(mFiltros);
		
		return lret != null && lret.size() > 0 ? lret.get(0) : null; 
	}
	public static List<InfracaoImagem> buscarInfracaoImagemPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<InfracaoImagem> lRet = new ArrayList<InfracaoImagem>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT"); 
		sbSQL.append("	i.id_infracao,");
		sbSQL.append("	ii.id_imagem_obj,");
		sbSQL.append("	coalesce(ii.id_imagem_pan, ipan.id_imagem) as id_imagem_pan,"); 
		sbSQL.append("	coalesce(ii.id_imagem_pan2, ipan2.id_imagem) as id_imagem_pan2 ");
		sbSQL.append("FROM");
		sbSQL.append("	infracao i WITH (NOLOCK)"); 
		sbSQL.append("	JOIN infracao_imagem ii WITH (NOLOCK) ON ii.id_infracao = i.id_infracao");
		sbSQL.append("	JOIN enquadramento e ON e.id_enquadramento = i.id_enquadramento");
		sbSQL.append("	LEFT JOIN tipo_imagem ti ON ti.id_tipo_imagem = e.id_tipo_imagem_pan");
		sbSQL.append("	LEFT JOIN imagem_info ipan ON ipan.id_tipo_imagem = ti.id_tipo_imagem AND ipan.id_imagem in (SELECT id_imagem FROM veiculo_imagem WHERE id_veiculo = i.id_veiculo)");
		sbSQL.append("	LEFT JOIN tipo_imagem ti2 ON ti2.id_tipo_imagem = e.id_tipo_imagem_pan2");
		sbSQL.append("	LEFT JOIN imagem_info ipan2 ON ipan2.id_tipo_imagem = ti2.id_tipo_imagem AND ipan2.id_imagem in (SELECT id_imagem FROM veiculo_imagem WHERE id_veiculo = i.id_veiculo)");
		sbSQL.append(" WHERE ");
		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("id_infracao", "i.id_infracao = ?");
		mRegras.put("id_imagem", "i.id_imagem = ?");
		mRegras.put("id_imagem_obj", "ii.id_imagem_obj = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
	
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new InfracaoImagem (
						rs.getInt("id_infracao"),
						rs.getInt("id_imagem_obj"),
						rs.getInt("id_imagem_pan"),
						rs.getInt("id_imagem_pan2")
					));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return lRet;
	}

	public Integer getIdInfracao() {
		return idInfracao;
	}

	public Integer getIdImagemObj() {
		return idImagemObj;
	}

	public Integer getIdImagemPan1() {
		return idImagemPan1;
	}

	public Integer getIdImagemPan2() {
		return idImagemPan2;
	}
	
}

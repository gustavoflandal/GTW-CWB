/**
 * 
 */
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
 * @author fos
 *
 */
public class ExportacaoImagemImagem {
	private Integer idExportacaoImagem;
	private Integer idImagem;

	/**
	 * @param idExportacaoImagem
	 * @param idImagem
	 */
	public ExportacaoImagemImagem(Integer idExportacaoImagem, Integer idImagem) {
		super();
		this.idExportacaoImagem = idExportacaoImagem;
		this.idImagem = idImagem;
	}

	public static List<ExportacaoImagemImagem> buscarExportacaoImagemImagemPorIdExportacaoImagem(Integer idExportacaoImagem) throws ConexaoException, SQLException, ModelException {
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("id_exportacao_imagem", idExportacaoImagem);
		return buscaExportacaoImagemImagemPor(mFiltros);
	}

	/**
	 * Busca imagens a serem exportadas no BD.
	 * @return Lista de objetos ExportacaoImagemImagem
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws ModelException 
	 */
	public static List<ExportacaoImagemImagem> buscaExportacaoImagemImagemPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<ExportacaoImagemImagem> lRet = new ArrayList<ExportacaoImagemImagem>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ei.id_exportacao_imagem,");
		sbSQL.append("		 ei.id_imagem ");
		sbSQL.append("	FROM");
		sbSQL.append("		exportacao_imagem_imagem ei WITH (NOLOCK) ");
		sbSQL.append("	LEFT JOIN veiculo_imagem vi ON vi.id_imagem = ei.id_imagem");
		sbSQL.append("	LEFT JOIN infracao i ON i.id_veiculo = vi.id_veiculo");
		sbSQL.append("	LEFT JOIN infracao_remessa ir ON ir.id_infracao = i.id_infracao");
		sbSQL.append("	LEFT JOIN remessa r ON r.id_remessa = ir.id_remessa");
		sbSQL.append("	WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY r.tipo, ir.serie, ir.auto");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
	
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new ExportacaoImagemImagem(
						rs.getInt("id_exportacao_imagem"),
						rs.getInt("id_imagem")
					));
			}
		}				
		finally {
			if (conn != null)
				conn.close();
		}
		
		return lRet;
	}


	/**
	 * @return the idExportacaoImagem
	 */
	public Integer getIdExportacaoImagem() {
		return idExportacaoImagem;
	}


	/**
	 * @return the idImagem
	 */
	public Integer getIdImagem() {
		return idImagem;
	}

}

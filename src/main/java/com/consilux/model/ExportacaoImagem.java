/**
 * 
 */
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
public class ExportacaoImagem {
	private Integer idExportacaoImagem;
	private Date dataCriacao;
	private Integer totalImagens;
	private Date dataExportacao;
	/**
	 * @param idExportacaoImagem
	 * @param dataCriacao
	 * @param totalImagens
	 * @param dataExportacao
	 */
	public ExportacaoImagem(Integer idExportacaoImagem, Date dataCriacao,
			Integer totalImagens, Date dataExportacao) {
		super();
		this.idExportacaoImagem = idExportacaoImagem;
		this.dataCriacao = dataCriacao;
		this.totalImagens = totalImagens;
		this.dataExportacao = dataExportacao;
	}
	
	public static ExportacaoImagem buscaExportacaoImagemPorId(Integer idExportacaoImagem) throws ConexaoException, SQLException, ModelException {
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		
		mFiltros.put("id_exportacao_imagem", idExportacaoImagem);
		List<ExportacaoImagem> lret = buscaExportacaoImagemPor(mFiltros);
		
		return lret != null && lret.size() > 0 ? lret.get(0) : null;   
	}
	
	public static List<ExportacaoImagem> buscaExportacaoImagemPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<ExportacaoImagem> lRet = new ArrayList<ExportacaoImagem>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_exportacao_imagem,");
		sbSQL.append("		 data_criacao, ");
		sbSQL.append("		 total_imagens, ");
		sbSQL.append("		 data_exportacao ");
		
		sbSQL.append("	FROM");
		sbSQL.append("		exportacao_imagem WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("id_remessa", "id_exportacao_imagem IN (" +
					"	SELECT id_exportacao_imagem FROM exportacao_imagem_imagem eii" +
					"	JOIN veiculo_imagem vi ON vi.id_imagem = eii.id_imagem" +
					"	JOIN infracao inf ON inf.id_veiculo = vi.id_veiculo" +
					"	JOIN infracao_remessa ir ON ir.id_infracao = inf.id_infracao" +
					"	WHERE ir.id_remessa = ?)");
		mRegras.put("nao_exportada", "data_exportacao IS NULL");
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
				lRet.add(new ExportacaoImagem(
						rs.getInt("id_exportacao_imagem"),
						rs.getTimestamp("data_criacao"),
						rs.getInt("total_imagens"),
						rs.getTimestamp("data_exportacao")
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
	 * @return the dataCriacao
	 */
	public Date getDataCriacao() {
		return dataCriacao;
	}
	/**
	 * @return the totalImagens
	 */
	public Integer getTotalImagens() {
		return totalImagens;
	}
	/**
	 * @return the dataExportacao
	 */
	public Date getDataExportacao() {
		return dataExportacao;
	}
	
	public static ExportacaoImagem criaExportacaoImagemParaRemessa(Integer idRemessa) throws ConexaoException, SQLException, ModelException {
		
		Integer id = null;
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_cria_exportacao_imagem(?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(2, idRemessa);

			cs.execute();
			id = cs.getInt(1);

		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return id != null ? ExportacaoImagem.buscaExportacaoImagemPorId(id) : null;
	}
	public Boolean confirmaExportacao() throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("UPDATE exportacao_imagem SET ");
		sbSQL.append("	data_exportacao = GETDATE()");
		sbSQL.append(" WHERE ");
		sbSQL.append("	id_exportacao_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, this.idExportacaoImagem);
			
			bRet = ps.executeUpdate() > 0;
			
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}
}

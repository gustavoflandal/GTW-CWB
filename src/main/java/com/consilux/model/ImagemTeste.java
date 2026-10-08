/**********************************************************************************


  Projeto: GTW
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 20/08/2009

  Descricao: Classe de controle para ImagensTeste

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.model;

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
 * Classe de controle para ImagensTeste
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class ImagemTeste {
	/**
	 * 16384 em binário = 100000000000000
	 * 					  +-------------- Imagem TESTE
	 */
	public static final Integer MASCARA_IMAGEM_TESTE = 16384;
	/**
	 * 256 em binário = 100000000
	 * 				    +------- Manutenção
	 */
	public static final Integer MASCARA_MANUTENCAO = 256;
	private Integer idVeiculo;
	private Date data;
	private Integer idLocal;
	private Integer pista;
	private boolean visualizado = false; 
	private Date dataVisualizado = null;
	String visualizadoPor = null;


	public String getVisualizadoPor() {
		return visualizadoPor;
	}

	public Date getDataVisualizado() {
		return dataVisualizado;
	}

	public ImagemTeste(Integer idVeiculo, Date data, Integer idLocal,
			Integer pista, boolean visualizado, Date dataVisualizado, String visualizadoPor ) {
		super();
		this.idVeiculo = idVeiculo;
		this.data = data;
		this.idLocal = idLocal;
		this.pista = pista;
		this.visualizado = visualizado;
		this.dataVisualizado = dataVisualizado; 
		this.visualizadoPor = visualizadoPor;
		
	}

	public boolean isVisualizado() {
		return visualizado;
	}

	public static List<ImagemTeste> buscarImagemTestePor(
			Map<String,Object> mFiltros , 
			Integer visualizadosPeloIdUsuario, Integer visualizadosPeloIdGrupo ) throws ConexaoException, ModelException, SQLException {
		List<ImagemTeste> lRet = new ArrayList<ImagemTeste>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT ");
		sbSQL.append(" v.id_veiculo, ");
		sbSQL.append(" v.data, ");
		sbSQL.append(" v.id_local, ");
		sbSQL.append(" v.pista, ");
		sbSQL.append(" ivv.data_hora as data_visualizado, ");
		sbSQL.append(" su.nome as nome_usuario ");
		sbSQL.append(" FROM 	veiculo v ");
		sbSQL.append("		JOIN local_vigente lv on lv.id_local = v.id_local AND lv.desativado = 0" );
		sbSQL.append("		LEFT JOIN (" );
		sbSQL.append("			SELECT id_veiculo, vv.id_usuario, MIN(data_hora) as data_hora ");
		sbSQL.append("			FROM veiculo_visualizados vv");
		sbSQL.append("			LEFT JOIN sis_usuario_grupo sug on sug.id_usuario = vv.id_usuario");

		if (visualizadosPeloIdUsuario != null){
			sbSQL.append(" 		WHERE vv.id_usuario = " + visualizadosPeloIdUsuario.toString());
		}
		else if (visualizadosPeloIdGrupo != null){
			sbSQL.append(" 		WHERE sug.id_grupo = " + visualizadosPeloIdGrupo.toString());
		}
		
		sbSQL.append("			GROUP BY id_veiculo, vv.id_usuario");
		sbSQL.append("		) as ivv on ivv.id_veiculo = v.id_veiculo");
		sbSQL.append("		LEFT JOIN sis_usuario su on su.id_usuario = ivv.id_usuario" );
		sbSQL.append(" WHERE ((flag & "+MASCARA_IMAGEM_TESTE+") > 0) AND (NOT (flag & "+MASCARA_MANUTENCAO+") > 0)");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("id_pista", "pista = ?");
		mRegras.put("id_local", "v.id_local = ?");
		mRegras.put("data_ini", "v.data >= ?");
		mRegras.put("data_fim", "v.data <= ?");
		
		if (mFiltros.size() > 0)
			sbSQL.append(" AND ");
			
		sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
		
		sbSQL.append(" ORDER BY v.id_local, v.pista, v.data");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
		
			rs = ps.executeQuery();

		
			String usuario;
			boolean visualizado;
			
			while (rs.next()) {
				
				usuario = rs.getString("nome_usuario");
				visualizado = !rs.wasNull();
				
				lRet.add(new ImagemTeste (
						rs.getInt("id_veiculo"),
						rs.getTimestamp("data"),
						rs.getInt("id_local"),
						rs.getInt("pista"),
						visualizado,
						rs.getTimestamp("data_visualizado"),
						usuario == null ? "" : usuario.trim()
					)
				);
			}
		}
		finally {
			conn.close();							
		}
		return lRet;
	}

	/**
	 * Retorna o valor do campo 'idVeiculo' atual.
	 * @return the idVeiculo
	 */
	public Integer getIdVeiculo() {
		return this.idVeiculo;
	}

	/**
	 * Retorna o valor do campo 'data' atual.
	 * @return the data
	 */
	public Date getData() {
		return this.data;
	}

	/**
	 * Retorna o valor do campo 'idLocal' atual.
	 * @return the idLocal
	 */
	public Integer getIdLocal() {
		return this.idLocal;
	}

	/**
	 * Retorna o valor do campo 'pista' atual.
	 * @return the pista
	 */
	public Integer getPista() {
		return this.pista;
	}
	
}

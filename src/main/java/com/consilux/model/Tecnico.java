package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.ui.client.beans.TecnicoGwtBean;

public class Tecnico {

	private int idUsuario;
	private String usuario;
	private String nome;
	
	public Tecnico(int idUsuario, String usuario, String nome) {
		this.idUsuario = idUsuario;
		this.usuario = usuario;
		this.nome = nome;
	}
	
	public Tecnico() {
		this.idUsuario = 0;
		this.usuario = "";
		this.nome = "";
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	
	/**
	 * Converte este técnico para um bean.
	 * @return Um bean de técnico.
	 */
	public TecnicoGwtBean toTecnicoBean () {
		
		TecnicoGwtBean bean = new TecnicoGwtBean();
		
		bean.setIdUsuario(this.idUsuario);
		bean.setNome(this.nome);
		bean.setUsuario(this.usuario);
		
		return bean;
	}
		
	
	/**
	 * Busca uma lista de técnicos que estejam cadastrados
	 * e ativos.
	 * @return Lista com Objetos Usuário materializados.
	 * @throws ConexaoException
	 */
	public static List<Tecnico> buscarTecnicosAtivos() throws ConexaoException {
		
		List<Tecnico> lRet = new ArrayList<Tecnico>();
		Tecnico tecnico = null;
		
		StringBuilder sbSQL = new StringBuilder();
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("   SELECT ");
		sbSQL.append("		usu.id_usuario,");
		sbSQL.append("		usu.usuario,");
		sbSQL.append("		usu.nome");
		sbSQL.append("	FROM");
		sbSQL.append("      sis_usuario as usu WITH (NOLOCK)");
		sbSQL.append("      INNER JOIN sis_usuario_grupo as grp");
		sbSQL.append("      ON usu.id_usuario = grp.id_usuario");		
		sbSQL.append("	WHERE ");
		sbSQL.append("		grp.id_grupo = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, ConfiguracaoProvider.getInstance().getIdGrupoTecnico());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				tecnico =  new Tecnico(
						rs.getInt("id_usuario"),
						rs.getString("usuario"),
						rs.getString("nome"));
				lRet.add(tecnico);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
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
	
}

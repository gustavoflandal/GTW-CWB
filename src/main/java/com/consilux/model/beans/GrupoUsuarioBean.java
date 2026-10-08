package com.consilux.model.beans;

/**
 * Bean que representa a associação entre um usuário e um grupo.
 * @author raoni
 */

public class GrupoUsuarioBean {

	private int idUsuario;
	private int idGrupo;
	
	public GrupoUsuarioBean() {
		this.idUsuario = 0;
		this.idGrupo = 0;
	}
	
	public GrupoUsuarioBean(int idUsuario, int idGrupo) {
		this.idUsuario = idUsuario;
		this.idGrupo = idGrupo;
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public int getIdGrupo() {
		return idGrupo;
	}
	
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	
}

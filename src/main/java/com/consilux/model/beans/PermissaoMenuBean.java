package com.consilux.model.beans;

import java.io.Serializable;

import com.consilux.ui.client.beans.PermissaoGwtBean;

public class PermissaoMenuBean  implements Serializable {

	private static final long serialVersionUID = 8623152824091276431L;
	
	private int idPermissao;
	private int idMenu;
	private Integer idUsuario;
	private Integer idGrupo;
	
	public PermissaoMenuBean() {
		this.idPermissao = 0;
		this.idMenu = 0;
		this.idUsuario = null;
		this.idGrupo = null;
	}
	
	public PermissaoMenuBean(int idPermissao, int idMenu, Integer idUsuario, Integer idGrupo) {
		this.idPermissao = idPermissao;
		this.idMenu = idMenu;
		this.idUsuario = idUsuario;
		this.idGrupo = idGrupo;
	}

	public PermissaoMenuBean(PermissaoGwtBean gwtBean) {
		this.idPermissao = gwtBean.getIdPermissao();
		this.idMenu = gwtBean.getMenu() != null	? gwtBean.getMenu().getIdMenu() : 0;
		this.idUsuario = gwtBean.getIdUsuario() != 0 ? gwtBean.getIdUsuario() : null; 
		this.idGrupo = gwtBean.getIdGrupo() != 0 ? gwtBean.getIdGrupo() : null;		
	}
	
	public int getIdPermissao() {
		return idPermissao;
	}
	
	public void setIdPermissao(int idPermissao) {
		this.idPermissao = idPermissao;
	}
	
	public int getIdMenu() {
		return idMenu;
	}
	
	public void setIdMenu(Integer idMenu) {
		this.idMenu = idMenu;
	}
	
	public Integer getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public Integer getIdGrupo() {
		return idGrupo;
	}
	
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

}

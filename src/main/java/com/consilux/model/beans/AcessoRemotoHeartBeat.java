package com.consilux.model.beans;

import java.util.Date;

import com.consilux.model.LocalVigente;
import com.consilux.model.Usuario;

public class AcessoRemotoHeartBeat {

	private Usuario usuario;
	private LocalVigente local;
	private long ultimoPooling;
	
	
	public AcessoRemotoHeartBeat(Usuario usuario, LocalVigente local) {
		super();
		this.usuario = usuario;
		this.local = local;
		this.ultimoPooling = new Date().getTime();
	}


	public synchronized long getUltimoPooling() {
		return ultimoPooling;
	}


	public synchronized void atualizaUltimoPooling() {
		this.ultimoPooling = new Date().getTime();
	}


	public int getIdUsuario() {
		return usuario.getId();
	}

	public String getLoginUsuario() {
		return usuario.getUsuario();
	}

	public int getSerieEquipamento() {
		return local.getSerieEquipamento();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getSerieEquipamento();
		result = prime * result + getIdUsuario();
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AcessoRemotoHeartBeat other = (AcessoRemotoHeartBeat) obj;
		if (getSerieEquipamento() != other.getSerieEquipamento())
			return false;
		if (getIdUsuario() != other.getIdUsuario())
			return false;
		return true;
	} 
	
	

	
	

}

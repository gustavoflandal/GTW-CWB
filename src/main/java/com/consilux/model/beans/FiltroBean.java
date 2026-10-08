package com.consilux.model.beans;

import java.util.Date;

public class FiltroBean {
	
	private Integer idFiltro;

	//query manual
	private String sqlCriterio;


	//Nome do Filtro
	private String nomeFiltro;
	
	//id_inconsistencia utilizado para filtrar as infrações que passam pelo filtro
	private Integer idFiltroInconsistencias;
	
	//id_enquadramento utilizado para filtrar as infrações que passam pelo filtro
	private Integer idFiltroEnquadramento;
	
	//id_enquadramento utilizado para filtrar as infrações que passam pelo filtro
	private Integer idFiltroProcesso;
	
	//id_local utilizado para filtrar as infrações que passam pelo filtro
	private Integer idLocal;
	
	//id_pista utilizado para filtrar as infrações que passam pelo filtro
	private Integer idPista;
	
	//id_classe utilizado para filtrar as infrações que passam pelo filtro
	private String idclasse = "";
	
	//Date que determina a data inicial das infrações que devem ser filtradas
	private Date dtIni;
	
	//Date que determina a data final das infrações que passam pelo filtro
	private Date dtFim;
	
	//Data quando o filtro será desativado
	private Date dtValidade;
	
	//nova id_inconsistencia da infração 
	private Integer setIdInconsistencia;

	private Boolean espera;
	
	private Integer idUsuario;

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNomeFiltro() {
		return nomeFiltro;
	}

	public void setNomeFiltro(String nomeFiltro) {
		this.nomeFiltro = nomeFiltro;
	}

	public Integer getIdFiltroInconsistencias() {
		return idFiltroInconsistencias;
	}

	public void setIdFiltroInconsistencias(Integer idFiltroInconsistencias) {
		this.idFiltroInconsistencias = idFiltroInconsistencias;
	}

	public Integer getIdFiltroEnquadramento() {
		return idFiltroEnquadramento;
	}

	public Integer getIdFiltroProcesso() {
		return idFiltroProcesso;
	}

	public void setIdFiltroEnquadramento(Integer idFiltroEnquadramento) {
		this.idFiltroEnquadramento = idFiltroEnquadramento;
	}

	public void setIdFiltroProcesso(Integer idFiltroProcesso) {
		this.idFiltroProcesso = idFiltroProcesso;
	}

	public Integer getIdLocal() {
		return idLocal;
	}
	
	/**
	 * @return the idPista
	 */
	public Integer getIdPista() {
		return idPista;
	}

	/**
	 * @param idPista the idPista to set
	 */
	public void setIdPista(Integer idPista) {
		this.idPista = idPista;
	}

	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public String getIdclasse() {
		return idclasse;
	}

	public void setIdclasse(String idclasse) {
		this.idclasse = idclasse;
	}

	public Date getDtIni() {
		return dtIni;
	}

	public void setDtIni(Date dtIni) {
		this.dtIni = dtIni;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Date getDtValidade() {
		return dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}

	public Integer getSetIdInconsistencia() {
		return setIdInconsistencia;
	}


	public Boolean getEspera() {
		return espera;
	}

	public void setSetIdInconsistencia(Integer setIdInconsistencia) {
		this.setIdInconsistencia = setIdInconsistencia;
	}

	public void setSetEspera(Boolean espera) {
		this.espera = espera;
	}
	
	
	public String getSqlCriterio() {
		return sqlCriterio;
	}

	public void setSqlCriterio(String sqlCriterio) {
		this.sqlCriterio = sqlCriterio;
	}
	
	public Integer getIdFiltro() {
		return idFiltro;
	}

	public void setIdFiltro(Integer idFiltro) {
		this.idFiltro = idFiltro;
	}
	

}

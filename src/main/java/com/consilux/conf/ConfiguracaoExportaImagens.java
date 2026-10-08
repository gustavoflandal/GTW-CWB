package com.consilux.conf;

import java.io.Serializable;

public class ConfiguracaoExportaImagens implements Serializable {

	private static final long serialVersionUID = -6137489995409096563L;

	private Boolean ativo;
	private String diretorio;
	private String cmdLogon;
	private String cmdLogoff;
	private String horarioJob;
	private Integer diasRetroativoMinimo;
	private Integer diasRetroativoMaximo;
	
	public ConfiguracaoExportaImagens() {
		// Construtor padrão sem parâmetros, padrão JavaBeans		
	}

	/**
	 * @param ativo
	 * @param diretorio
	 * @param cmdLogon
	 * @param cmdLogoff
	 * @param horarioJob
	 * @param diasRetroativoMinimo
	 * @param diasRetroativoMaximo
	 */
	public ConfiguracaoExportaImagens(Boolean ativo, String diretorio,
			String cmdLogon, String cmdLogoff, String horarioJob,
			Integer diasRetroativoMinimo, Integer diasRetroativoMaximo) {
		super();
		this.ativo = ativo;
		this.diretorio = diretorio;
		this.cmdLogon = cmdLogon;
		this.cmdLogoff = cmdLogoff;
		this.horarioJob = horarioJob;
		this.diasRetroativoMinimo = diasRetroativoMinimo;
		this.diasRetroativoMaximo = diasRetroativoMaximo;
	}


	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the ativo
	 */
	public Boolean isAtivo() {
		return ativo;
	}

	/**
	 * @return the diretorio
	 */
	public String getDiretorio() {
		return diretorio;
	}

	/**
	 * @return the cmdLogon
	 */
	public String getCmdLogon() {
		return cmdLogon;
	}

	/**
	 * @return the cmdLogoff
	 */
	public String getCmdLogoff() {
		return cmdLogoff;
	}

	/**
	 * @return the horarioJob
	 */
	public String getHorarioJob() {
		return horarioJob;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @param diretorio the diretorio to set
	 */
	public void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}

	/**
	 * @param cmdLogon the cmdLogon to set
	 */
	public void setCmdLogon(String cmdLogon) {
		this.cmdLogon = cmdLogon;
	}

	/**
	 * @param cmdLogoff the cmdLogoff to set
	 */
	public void setCmdLogoff(String cmdLogoff) {
		this.cmdLogoff = cmdLogoff;
	}

	/**
	 * @param horarioJob the horarioJob to set
	 */
	public void setHorarioJob(String horarioJob) {
		this.horarioJob = horarioJob;
	}

	/**
	 * @return the diasRetroativoMinimo
	 */
	public Integer getDiasRetroativoMinimo() {
		return diasRetroativoMinimo;
	}

	/**
	 * @param diasRetroativoMinimo the diasRetroativoMinimo to set
	 */
	public void setDiasRetroativoMinimo(Integer diasRetroativoMinimo) {
		this.diasRetroativoMinimo = diasRetroativoMinimo;
	}

	/**
	 * @return the diasRetroativoMaximo
	 */
	public Integer getDiasRetroativoMaximo() {
		return diasRetroativoMaximo;
	}

	/**
	 * @param diasRetroativoMaximo the diasRetroativoMaximo to set
	 */
	public void setDiasRetroativoMaximo(Integer diasRetroativoMaximo) {
		this.diasRetroativoMaximo = diasRetroativoMaximo;
	}

	/**
	 * @return the ativo
	 */
	public Boolean getAtivo() {
		return ativo;
	}

}

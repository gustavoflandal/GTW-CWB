package com.consilux.conf;

import java.io.Serializable;

public class ConfiguracaoExportaTrafego implements Serializable {

	private static final long serialVersionUID = -6137489995409096563L;

	private Boolean ativo;
	private String diretorio;
	private String cmdLogon;
	private String cmdLogoff;
	private String codigoEmpresa;
	private String codigoTipoEquipamento;
	private String tipoEquipamento;
	private String horarioJob;
	private Integer diasRetroativoMaximo;
	
	public ConfiguracaoExportaTrafego() {
		// Construtor padrão sem parâmetros, padrão JavaBeans		
	}

	/**
	 * @param ativo
	 * @param diretorio
	 * @param cmdLogon
	 * @param cmdLogoff
	 * @param codigoEmpresa
	 * @param codigoTipoEquipamento
	 * @param tipoEquipamento
	 * @param horarioJob
	 * @param diasRetroativoMaximo
	 */
	public ConfiguracaoExportaTrafego(Boolean ativo, String diretorio,
			String cmdLogon, String cmdLogoff, String codigoEmpresa,
			String codigoTipoEquipamento, String tipoEquipamento,
			String horarioJob, Integer diasRetroativoMaximo) {
		super();
		this.ativo = ativo;
		this.diretorio = diretorio;
		this.cmdLogon = cmdLogon;
		this.cmdLogoff = cmdLogoff;
		this.codigoEmpresa = codigoEmpresa;
		this.codigoTipoEquipamento = codigoTipoEquipamento;
		this.tipoEquipamento = tipoEquipamento;
		this.horarioJob = horarioJob;
		this.diasRetroativoMaximo = diasRetroativoMaximo;
	}

	/**
	 * @return the ativo
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the diretorio
	 */
	public String getDiretorio() {
		return diretorio;
	}

	/**
	 * @param diretorio the diretorio to set
	 */
	public void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}

	/**
	 * @return the cmdLogon
	 */
	public String getCmdLogon() {
		return cmdLogon;
	}

	/**
	 * @param cmdLogon the cmdLogon to set
	 */
	public void setCmdLogon(String cmdLogon) {
		this.cmdLogon = cmdLogon;
	}

	/**
	 * @return the cmdLogoff
	 */
	public String getCmdLogoff() {
		return cmdLogoff;
	}

	/**
	 * @param cmdLogoff the cmdLogoff to set
	 */
	public void setCmdLogoff(String cmdLogoff) {
		this.cmdLogoff = cmdLogoff;
	}

	/**
	 * @return the codigoEmpresa
	 */
	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	/**
	 * @param codigoEmpresa the codigoEmpresa to set
	 */
	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	/**
	 * @return the codigoTipoEquipamento
	 */
	public String getCodigoTipoEquipamento() {
		return codigoTipoEquipamento;
	}

	/**
	 * @param codigoTipoEquipamento the codigoTipoEquipamento to set
	 */
	public void setCodigoTipoEquipamento(String codigoTipoEquipamento) {
		this.codigoTipoEquipamento = codigoTipoEquipamento;
	}

	/**
	 * @return the tipoEquipamento
	 */
	public String getTipoEquipamento() {
		return tipoEquipamento;
	}

	/**
	 * @param tipoEquipamento the tipoEquipamento to set
	 */
	public void setTipoEquipamento(String tipoEquipamento) {
		this.tipoEquipamento = tipoEquipamento;
	}

	/**
	 * @return the horarioJob
	 */
	public String getHorarioJob() {
		return horarioJob;
	}

	/**
	 * @param horarioJob the horarioJob to set
	 */
	public void setHorarioJob(String horarioJob) {
		this.horarioJob = horarioJob;
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


}

package com.consilux.model.relatorio.rj;

import java.util.Date;


public class ItemRelatorioFuncionamento {

	private long numeroEquipamento;
	private String codigoEquipamentoDER;
	private Date dataInicioOperacao;
	Integer[] celulasHorasFuncionamento = new Integer[31];
	private double aproveitamento;
	private String[] celulasColunasRelatorio;
	private Integer qtdeColunas;
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 15/07/2018
	 * Objetivo: Construtor para o relatório de funcionamento
	 */
	public ItemRelatorioFuncionamento(long numeroEquipamento,
									  String codigoEquipamentoDER,
									  Date dataInicioOperacao,
									  String[] celulasColunasRelatorio,
									  Integer[] celulasHorasFuncionamento,
									  double aproveitamento,
									  Integer qtdeColunas){

		super();
		this.numeroEquipamento = numeroEquipamento;
		this.codigoEquipamentoDER = codigoEquipamentoDER;
		this.dataInicioOperacao = dataInicioOperacao;
		this.celulasColunasRelatorio = celulasColunasRelatorio;
		this.celulasHorasFuncionamento = celulasHorasFuncionamento;
		this.aproveitamento = aproveitamento;
		this.qtdeColunas = qtdeColunas;
	}
	
	
	public long getNumeroEquipamento() {
		return numeroEquipamento;
	}
	public void setNumeroEquipamento(long numeroEquipamento) {
		this.numeroEquipamento = numeroEquipamento;
	}
	
	
	public String getCodigoEquipamentoDER() {
		return codigoEquipamentoDER;
	}
	public void setCodigoEquipamentoDER(String codigoEquipamentoDER) {
		this.codigoEquipamentoDER = codigoEquipamentoDER;
	}
	
	
	public Integer[] getCelulasHorasFuncionamento() {
		return celulasHorasFuncionamento;
	}
	public void setCelulasHorasFuncionamento(Integer[] celulasHorasFuncionamento) {
		this.celulasHorasFuncionamento = celulasHorasFuncionamento;
	}
	
	
	public String[] getCelulasColunasRelatorio() {
		return celulasColunasRelatorio;
	}
	public void setCelulasColunasRelatorio(String[] celulasColunasRelatorio) {
		this.celulasColunasRelatorio = celulasColunasRelatorio;
	}


	public double getAproveitamento() {
		return aproveitamento;
	}
	public void setAproveitamento(double aproveitamento) {
		this.aproveitamento = aproveitamento;
	}
	
	
	public Date getDataInicioOperacao() {
		return dataInicioOperacao;
	}
	public void setDataInicioOperacao(Date dataInicioOperacao) {
		this.dataInicioOperacao = dataInicioOperacao;
	}


	public Integer getQtdeColunas() {
		return qtdeColunas;
	}
	public void setQtdeColunas(Integer qtdeColunas) {
		this.qtdeColunas = qtdeColunas;
	}
	
}

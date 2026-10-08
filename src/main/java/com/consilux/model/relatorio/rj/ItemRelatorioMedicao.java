package com.consilux.model.relatorio.rj;

import java.util.Date;
import java.util.List;

public class ItemRelatorioMedicao
{

	private long numeroEquipamento;
	private String enderecoEquipamento;
	private long codigoCET;
	private short faixa;
	private short idOcorrencia;
	private String ocorrencia;
	private Date dataPublicacao;
	private List<String> dias;
	private short diasSemFuncionamento;
	private String justificativa;
	private short regras;
	private String[] celulasColunasRelatorio;
	private Integer[] celulasValores;
	private Integer qtdeColunas;
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 27/01/2021
	 * Objetivo: Construtor para a Planilha de Acompanhamento
	 */
	public ItemRelatorioMedicao(long numeroEquipamento,
								String enderecoEquipamento,
								long codigoCET,
								short faixa,
								short idOcorrencia,
								String ocorrencia,
								Date dataPublicacao,
								String[] celulasColunasRelatorio,
								Integer[] celulasValores,
								Integer qtdeColunas,
								short diasSemFuncionamento,
								String justificativa,
								short regras){

		super();
		this.numeroEquipamento = numeroEquipamento;
		this.enderecoEquipamento = enderecoEquipamento;
		this.codigoCET = codigoCET;
		this.faixa = faixa;
		this.idOcorrencia = idOcorrencia;
		this.ocorrencia = ocorrencia;
		this.dataPublicacao = dataPublicacao;
		this.celulasColunasRelatorio = celulasColunasRelatorio;
		this.celulasValores = celulasValores;
		this.qtdeColunas = qtdeColunas;
		this.diasSemFuncionamento = diasSemFuncionamento;
		this.justificativa = justificativa;
		this.regras = regras;
	}
	
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 27/01/2021
	 * Objetivo: Construtor para o Relatório de Acompanhamento de Infrações e Fluxo Veicular
	 */
	public ItemRelatorioMedicao(long numeroEquipamento,
								String enderecoEquipamento,
								short faixa,
								short idOcorrencia,
								String ocorrencia,
								Date dataPublicacao,
								String[] celulasColunasRelatorio,
								Integer[] celulasValores,
								Integer qtdeColunas){

		super();
		this.numeroEquipamento = numeroEquipamento;
		this.enderecoEquipamento = enderecoEquipamento;
		this.faixa = faixa;
		this.idOcorrencia = idOcorrencia;
		this.ocorrencia = ocorrencia;
		this.dataPublicacao = dataPublicacao;
		this.celulasColunasRelatorio = celulasColunasRelatorio;
		this.celulasValores = celulasValores;
		this.qtdeColunas = qtdeColunas;
	}
	
	
	public long getNumeroEquipamento() {
		return numeroEquipamento;
	}
	public void setNumeroEquipamento(long numeroEquipamento) {
		this.numeroEquipamento = numeroEquipamento;
	}
	
	
	public String getEnderecoEquipamento() {
		return enderecoEquipamento;
	}
	public void setEnderecoEquipamento(String enderecoEquipamento) {
		this.enderecoEquipamento = enderecoEquipamento;
	}
	
	
	public long getCodigoCET() {
		return codigoCET;
	}
	public void setCodigoCET(long codigoCET) {
		this.codigoCET = codigoCET;
	}
	
	
	public short getFaixa() {
		return faixa;
	}
	public void setFaixa(short faixa) {
		this.faixa = faixa;
	}
	
	
	public short getIdOcorrencia() {
		return idOcorrencia;
	}
	public void setIdOcorrencia(short idOcorrencia) {
		this.idOcorrencia = idOcorrencia;
	}
	
	
	public String getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
	
	
	public Date getDataPublicacao() {
		return dataPublicacao;
	}
	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}
	
	
	public List<String> getDias() {
		return dias;
	}
	public void setDias(List<String> dias) {
		this.dias = dias;
	}
	
	
	public short getDiasSemFuncionamento() {
		return diasSemFuncionamento;
	}
	public void setDiasSemFuncionamento(short diasSemFuncionamento) {
		this.diasSemFuncionamento = diasSemFuncionamento;
	}
	
	
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	
	public short getRegras() {
		return regras;
	}
	public void setRegras(short regras) {
		this.regras = regras;
	}
	
	
	public String[] getCelulasColunasRelatorio() {
		return celulasColunasRelatorio;
	}
	public void setCelulasColunasRelatorio(String[] celulasColunasRelatorio) {
		this.celulasColunasRelatorio = celulasColunasRelatorio;
	}
	
	
	public Integer[] getCelulasValores() {
		return celulasValores;
	}
	public void setCelulasValores(Integer[] celulasValores) {
		this.celulasValores = celulasValores;
	}


	public Integer getQtdeColunas() {
		return qtdeColunas;
	}
	public void setQtdeColunas(Integer qtdeColunas) {
		this.qtdeColunas = qtdeColunas;
	}
}

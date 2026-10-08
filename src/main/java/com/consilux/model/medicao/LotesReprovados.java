/**********************************************************************************
  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 22/04/2015
*********************************************************************************/

package com.consilux.model.medicao;

import java.io.Serializable;

/**
 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Lotes Reprovados
 * @author Luiz Fernando Amaral - Consilux Tecnologia
 * Data: 22/04/2015
 */

public  class LotesReprovados implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	private Integer movimentoLote;
	private String tipoApait;
	private String dtMovimento;
	private Integer ultRevisao;
	private String primeiraRevisao;
	private String ultimaRevisao;


	public LotesReprovados(Integer movimentoLote, String tipoApait,
						   String dtMovimento, Integer ultRevisao,
						   String primeiraRevisao, String ultimaRevisao){
		super();
		this.movimentoLote = movimentoLote;
		this.tipoApait = tipoApait;
		this.dtMovimento = dtMovimento;
		this.ultRevisao = ultRevisao;
		this.primeiraRevisao = primeiraRevisao;
		this.ultimaRevisao = ultimaRevisao;	
	}


	public Integer getMovimentoLote() {
		return movimentoLote;
	}


	public void setMovimentoLote(Integer movimentoLote) {
		this.movimentoLote = movimentoLote;
	}


	public String getTipoApait() {
		return tipoApait;
	}


	public void setTipoApait(String tipoApait) {
		this.tipoApait = tipoApait;
	}


	public String getDtMovimento() {
		return dtMovimento;
	}


	public void setDtMovimento(String dtMovimento) {
		this.dtMovimento = dtMovimento;
	}


	public Integer getUltRevisao() {
		return ultRevisao;
	}


	public void setUltRevisao(Integer ultRevisao) {
		this.ultRevisao = ultRevisao;
	}


	public String getPrimeiraRevisao() {
		return primeiraRevisao;
	}


	public void setPrimeiraRevisao(String primeiraRevisao) {
		this.primeiraRevisao = primeiraRevisao;
	}


	public String getUltimaRevisao() {
		return ultimaRevisao;
	}


	public void setUltimaRevisao(String ultimaRevisao) {
		this.ultimaRevisao = ultimaRevisao;
	}
	
}

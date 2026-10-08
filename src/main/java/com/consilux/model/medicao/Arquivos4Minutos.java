/**********************************************************************************
  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 05/09/2014
*********************************************************************************/

package com.consilux.model.medicao;

import java.io.Serializable;

/**
 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de 4 minutos
 * @author Luiz Fernando Amaral - Consilux Tecnologia
 * Data: 05/09/2014
 */

public  class Arquivos4Minutos implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Integer idLocal;
	Integer codPistaProdam;
	Integer faixa;
	String faixaTarja;
	String local;
	String[] celulas = new String[31];
	String dtPulicacao;
	Long serie;

	public Arquivos4Minutos(Integer idLocal, Integer codPistaProdam, String local, String[] cel, String dtPublicacao, String fxTarja, Long serie){
		super();
		this.idLocal = idLocal; 
		this.codPistaProdam = codPistaProdam;
		this.local = local;
		this.celulas = cel;
		this.dtPulicacao = dtPublicacao;
		this.faixaTarja = fxTarja;
		this.serie = serie;
	}
	
	public Arquivos4Minutos(Integer idLocal, String local, String[] cel, String dtPublicacao, String fxTarja, Long serie){
		super();
		this.idLocal = idLocal; 
		this.local = local;
		this.celulas = cel;
		this.dtPulicacao = dtPublicacao;
		this.faixaTarja = fxTarja;
		this.serie = serie;
	}

	public Integer getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public Integer getCodPistaProdam() {
		return codPistaProdam;
	}

	public void setCodPistaProdam(Integer codPistaProdam) {
		this.codPistaProdam = codPistaProdam;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String[] getCelulas() {
		return celulas;
	}

	public void setCelulas(String[] celulas) {
		this.celulas = celulas;
	}

	public String getDtPublicacao() {
		return dtPulicacao;
	}

	public void setDtPublicacao(String dtPublicacao) {
		this.dtPulicacao = dtPublicacao;
	}
	
	public Integer getFaixa() {
		return faixa;
	}

	public void setFaixa(Integer faixa) {
		this.faixa = faixa;
	}
	
	public String getFaixaTarja() {
		return faixaTarja;
	}

	public void setFaixaTarja(String faixaTarja) {
		this.faixaTarja = faixaTarja;
	}

	public String getDtPulicacao() {
		return dtPulicacao;
	}

	public void setDtPulicacao(String dtPulicacao) {
		this.dtPulicacao = dtPulicacao;
	}

	public Long getSerie() {
		return serie;
	}

	public void setSerie(Long serie) {
		this.serie = serie;
	}
}

/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 30/09/2016

*********************************************************************************/
package com.consilux.model.medicao;

import java.io.Serializable;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * DatA: 30/09/2016
 */
public  class ItemMedicaoPMESP implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Integer item;
	Long serieEquipamento;
	Integer codLocalProdam;
	Integer codEquipamentoProdam;
	Integer faixa;
	String faixaTarja;
	String descricaoLocal;
	String[] celulas = new String[31];
	String[] celulaEstatico = new String[31];
	Integer diasOK;
	Integer qtdePlacasAte4s;
	Integer qtdePlacasAcima4s;
	Integer totalPlacas;
	Integer porcentagemPlacasAte4s;
	Integer porcentagemPlacasAcima4s;
	
	public ItemMedicaoPMESP(){
	};
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * DatA: 15/09/2016
	 * Objetivo: Construtor para o relatório de Envio de Placas PMESP
	 */
	
//	rs.getInt("item"),
//	rs.getLong("serie_equipamento"),
//	rs.getInt("cod_pista"),
//	rs.getInt("cod_pista_prodam"),
//	rs.getString("faixa"),
//	rs.getString("nome_pista"),
//	celulas,
//	rs.getInt("dias_ok"),
//	rs.getInt("atraso_ok"),
//	rs.getInt("atraso_nok"),
//	rs.getInt("movimentos"),
//	rs.getInt("porc_atraso_ok"),
//	rs.getInt("porc_atraso_nok"));
	public ItemMedicaoPMESP(Integer item,
							Long serieEquipamento,
							Integer codLocalProdam,
							Integer codEquipamentoProdam, 
				   		   	String faixaTarja, 
				   		   	String descricaoLocal,
				   		   	String[] celulas,
				   		   	Integer diasOK,
				   		   	Integer qtdePlacasAte4s,
							Integer qtdePlacasAcima4s,
							Integer totalPlacas,
							Integer porcentagemPlacasAte4s,
							Integer porcentagemPlacasAcima4s){

		super();
		this.item= item;
		this.serieEquipamento = serieEquipamento;
		this.codLocalProdam = codLocalProdam;
		this.codEquipamentoProdam = codEquipamentoProdam;
		this.faixaTarja = faixaTarja;
		this.descricaoLocal = descricaoLocal;
		this.celulas = celulas;
		this.diasOK = diasOK;
		this.qtdePlacasAte4s = qtdePlacasAte4s;
		this.qtdePlacasAcima4s = qtdePlacasAcima4s;
		this.totalPlacas = totalPlacas;
		this.porcentagemPlacasAte4s = porcentagemPlacasAte4s;
		this.porcentagemPlacasAcima4s = porcentagemPlacasAcima4s;
	}

	
	public Integer getItem() {
		return item;
	}
	public void setItem(Integer item) {
		this.item = item;
	}

	
	public Long getSerieEquipamento() {
		return serieEquipamento;
	}
	public void setSerieEquipamento(Long serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	
	public Integer getCodLocalProdam() {
		return codLocalProdam;
	}
	public void setCodLocalProdam(Integer codLocalProdam) {
		this.codLocalProdam = codLocalProdam;
	}

	
	public Integer getCodEquipamentoProdam() {
		return codEquipamentoProdam;
	}
	public void setCodEquipamentoProdam(Integer codEquipamentoProdam) {
		this.codEquipamentoProdam = codEquipamentoProdam;
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

	
	public String getDescricaoLocal() {
		return descricaoLocal;
	}
	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}


	public String[] getCelulas() {
		return celulas;
	}
	public void setCelulas(String[] celulas) {
		this.celulas = celulas;
	}


	public String[] getCelulaEstatico() {
		return celulaEstatico;
	}
	public void setCelulaEstatico(String[] celulaEstatico) {
		this.celulaEstatico = celulaEstatico;
	}

	
	public Integer getDiasOK() {
		return diasOK;
	}
	public void setDiasOK(Integer diasOK) {
		this.diasOK = diasOK;
	}

	
	public Integer getQtdePlacasAte4s() {
		return qtdePlacasAte4s;
	}
	public void setQtdePlacasAte4s(Integer qtdePlacasAte4s) {
		this.qtdePlacasAte4s = qtdePlacasAte4s;
	}

	
	public Integer getQtdePlacasAcima4s() {
		return qtdePlacasAcima4s;
	}
	public void setQtdePlacasAcima4s(Integer qtdePlacasAcima4s) {
		this.qtdePlacasAcima4s = qtdePlacasAcima4s;
	}

	
	public Integer getTotalPlacas() {
		return totalPlacas;
	}
	public void setTotalPlacas(Integer totalPlacas) {
		this.totalPlacas = totalPlacas;
	}

	
	public Integer getPorcentagemPlacasAte4s() {
		return porcentagemPlacasAte4s;
	}
	public void setPorcentagemPlacasAte4s(Integer porcentagemPlacasAte4s) {
		this.porcentagemPlacasAte4s = porcentagemPlacasAte4s;
	}

	
	public Integer getPorcentagemPlacasAcima4s() {
		return porcentagemPlacasAcima4s;
	}
	public void setPorcentagemPlacasAcima4s(Integer porcentagemPlacasAcima4s) {
		this.porcentagemPlacasAcima4s = porcentagemPlacasAcima4s;
	}
}

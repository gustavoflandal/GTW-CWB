/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 21/08/2014

*********************************************************************************/
package com.consilux.model.medicao;

import java.io.Serializable;

/**
 *
 * @author Luiz Amaral - Consilux Tecnologia
 * DatA: 21/08/2014
 */
public  class ItemMedicao implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Integer codProdamFaixa;
	Integer faixaEquipamento;
	String faixaEquipamentoTarja;
	Long serieEquipamento;
	Integer codProdamLocal;
	String descLocal;
	String descEquadramentos;
	String dtPulicacao;
	Long[] celulas = new Long[31];
	Long codLocal;
	String[] celulaEstatico = new String[31];
	Integer flagFuncionamento;

	public ItemMedicao(Integer cdProdamFx, 
					   String fxEquipTarja, Long serieEquip, 
					   Integer cdProdamLocal, String dsLocal,
					   String dsEnquad, String DtPublic,
					   Long[] cel, Long cdLocal, String dtPublicacao){
		
		super();
		this.codProdamFaixa = cdProdamFx;
		this.faixaEquipamentoTarja = fxEquipTarja;
		this.serieEquipamento = serieEquip;
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.dtPulicacao = DtPublic;
		this.celulas = cel;
		this.codLocal = cdLocal;
		this.dtPulicacao = dtPublicacao;
	}
	
	public ItemMedicao(){
	};
	
	/**
	 * @author Luiz Amaral - Consilux Tecnologia
	 * DatA: 09/08/2014
	 * Objetivo: Construtor para o relatório de Funcionamento FIXO/BARREIRA
	 */
	public ItemMedicao(Integer cdProdamFx, 
			   		   String fxEquipTarja, 
			   		   Long serieEquip, 
			   		   Integer cdProdamLocal, 
			   		   String dsLocal,
			   		   String dtPublicacao,
			   		   Long[] cel){

		super();
		this.codProdamFaixa = cdProdamFx;
		this.faixaEquipamentoTarja = fxEquipTarja;
		this.serieEquipamento = serieEquip;
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.dtPulicacao = dtPublicacao;
		this.celulas = cel;
	}
	
	/**
	 * @author Luiz Amaral - Consilux Tecnologia
	 * DatA: 09/08/2014
	 * Objetivo: Construtor para o relatório de Funcionamento ESTATICO
	 */
	public ItemMedicao(Integer cdProdamLocal, 
			   		   String dsLocal,
			   		   String dtPublic,
			   		   String[] cel,
			   		   Integer cdProdamFx,
			   		   String fxEquipTarja){

		super();
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.celulaEstatico = cel;
		this.dtPulicacao = dtPublic;
		this.codProdamFaixa = cdProdamFx;
		this.faixaEquipamentoTarja = fxEquipTarja;
	}

	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * DatA: 15/09/2014
	 * Objetivo: Construtor para o relatório de Total de infrações Estático
	 */
	public ItemMedicao(Integer cdProdamFx, 
			   		   String fxEquipTarja, 
			   		   Long serieEquip, 
			   		   Integer cdProdamLocal, 
			   		   String dsLocal,
			   		   String dtPublicacao,
			   		   Integer flagFunc,
			   		   Long[] cel){

		super();
		this.codProdamFaixa = cdProdamFx;
		this.faixaEquipamentoTarja = fxEquipTarja;
		this.serieEquipamento = serieEquip;
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.dtPulicacao = dtPublicacao;
		this.flagFuncionamento = flagFunc;
		this.celulas = cel;
	}
	
	/**
	 * @author Luiz Amaral - Consilux Tecnologia
	 * DatA: 09/08/2014
	 * Objetivo: Construtor para o relatório de Total de infrações Fixo
	 */
	public ItemMedicao(String fxEquipTarja,
					   Integer cdProdamLocal, 
			   		   String dsLocal,
			   		   String dtPublicacao,
			   		   Integer flagFunc,
			   		   Long[] cel){

		super();
		this.faixaEquipamentoTarja = fxEquipTarja;
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.dtPulicacao = dtPublicacao;
		this.flagFuncionamento = flagFunc;
		this.celulas = cel;
		
}
	
	/**
	 * @author Luiz Amaral - Consilux Tecnologia
	 * DatA: 15/09/2014
	 * Objetivo: Construtor para o relatório de Fluxo na VIA FIxo/Barreira
	 */
	public ItemMedicao(Integer cdProdamFx, 
			   		   String fxEquipTarja, 
			   		   Long serieEquip, 
			   		   Integer cdProdamLocal, 
			   		   String dsLocal,
			   		   String dtPublicacao,
			   		   Long[] cel,
			   		   Integer flagFunc){

		super();
		this.codProdamFaixa = cdProdamFx;
		this.faixaEquipamentoTarja = fxEquipTarja;
		this.serieEquipamento = serieEquip;
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.dtPulicacao = dtPublicacao;
		this.celulas = cel;
		this.flagFuncionamento = flagFunc;
	}
	
	/**
	 * @author Luiz Amaral - Consilux Tecnologia
	 * DatA: 15/10/2014
	 * Objetivo: Construtor para o relatório de Fluxo na VIA Estatico
	 */
	public ItemMedicao(String fxEquipTarja, 
			   		   Integer cdProdamLocal, 
			   		   String dsLocal,
			   		   String dtPublicacao,
			   		   Long[] cel,
			   		   Integer flagFunc){

		super();
		this.faixaEquipamentoTarja = fxEquipTarja;
		this.codProdamLocal = cdProdamLocal;
		this.descLocal = dsLocal;
		this.dtPulicacao = dtPublicacao;
		this.celulas = cel;
		this.flagFuncionamento = flagFunc;
	}
	
	public Integer getCodProdamFaixa() {
		return codProdamFaixa;
	}

	public void setCodProdamFaixa(Integer codProdamFaixa) {
		this.codProdamFaixa = codProdamFaixa;
	}

	public Integer getFaixaEquipamento() {
		return faixaEquipamento;
	}

	public void setFaixaEquipamento(Integer faixaEquipamento) {
		this.faixaEquipamento = faixaEquipamento;
	}
	
	public String getFaixaEquipamentoTarja() {
		return faixaEquipamentoTarja;
	}

	public void setFaixaEquipamentoTarja(String faixaEquipamentoTarja) {
		this.faixaEquipamentoTarja = faixaEquipamentoTarja;
	}

	public Long getSerieEquipamento() {
		return serieEquipamento;
	}

	public void setSerieEquipamento(Long serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public Integer getCodProdamLocal() {
		return codProdamLocal;
	}

	public void setCodProdamLocal(Integer codProdamLocal) {
		this.codProdamLocal = codProdamLocal;
	}

	public String getDescLocal() {
		return descLocal;
	}

	public void setDescLocal(String descLocal) {
		this.descLocal = descLocal;
	}

	public String getDescEquadramentos() {
		return descEquadramentos;
	}

	public void setDescEquadramentos(String descEquadramentos) {
		this.descEquadramentos = descEquadramentos;
	}

	public String getDtPulicacao() {
		return dtPulicacao;
	}

	public void setDtPulicacao(String dtPulicacao) {
		this.dtPulicacao = dtPulicacao;
	}

	public Long[] getCelulas() {
		return celulas;
	}

	public void setCelulas(Long[] celulas) {
		this.celulas = celulas;
	}
	
	public Long getCodLocal() {
		return codLocal;
	}

	public void setCodLocal(Long codLocal) {
		this.codLocal = codLocal;
	}
	

	public String[] getCelulaEstatico() {
		return celulaEstatico;
	}

	public void setCelulaEstatico(String[] celulaEstatico) {
		this.celulaEstatico = celulaEstatico;
	}
	
	public Integer getFlagFuncionamento() {
		return flagFuncionamento;
	}

	public void setFlagFuncionamento(Integer flagFuncionamento) {
		this.flagFuncionamento = flagFuncionamento;
	}
}

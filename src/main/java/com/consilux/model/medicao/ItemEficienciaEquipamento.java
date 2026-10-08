/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 30/06/2016

*********************************************************************************/
package com.consilux.model.medicao;

import java.io.Serializable;

/**
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 30/06/2016
 */
public  class ItemEficienciaEquipamento implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	String dataInicio;
	String dataFim;
	String dia;
	Integer codProdamLocal;
	Integer codProdamFaixa;
	Integer faixa;
	String descricaoLocal;
	String dataInicioOperacao;
	String descInconsistenciaCAI;
	Integer qtdeInconsistenciaCAI;
	String enquadramentoHabilitado;
	String enquadramentoEfetivo;
	String enquadramentoAtivo;
	Integer equipamentoEstatico;
	Integer totalImagensCaptadas;
	Integer totalConsistentesCAI;
	String dataInicioManut;
	String descricaoManut;
	String dataFimManut;
	String estadoManut;

	public ItemEficienciaEquipamento(){
	};
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 30/06/2016
	 * @return Construtor para o relatório de Equipamento/Faixa x Inconsistências.
	 */
	public ItemEficienciaEquipamento(String dia,
									 Integer codProdamLocal,
									 String descricaoLocal,
									 Integer codProdamFaixa,
									 Integer faixa,
									 String dataInicioOperacao,
									 String enquadramentoHabilitado,
									 String descInconsistenciaCAI,
									 Integer qtdeInconsistenciaCAI,
									 Integer equipamentoEstatico) {

		super();
		this.dia = dia;
		this.codProdamLocal = codProdamLocal;
		this.descricaoLocal = descricaoLocal;
		this.codProdamFaixa = codProdamFaixa;
		this.faixa = faixa;
		this.dataInicioOperacao = dataInicioOperacao;
		this.enquadramentoHabilitado = enquadramentoHabilitado;
		this.descInconsistenciaCAI = descInconsistenciaCAI;
		this.qtdeInconsistenciaCAI = qtdeInconsistenciaCAI;
		this.equipamentoEstatico = equipamentoEstatico;
	}
	

	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 30/06/2016
	 * @return Construtor para o relatório de Enquadramento Habilitado x Enquadramento Efetivo.
	 */
	public ItemEficienciaEquipamento(String dia,
									 Integer codProdamLocal,
									 String descricaoLocal,
									 Integer codProdamFaixa,
									 Integer faixa,
									 String dataInicioOperacao,
									 String enquadramentoHabilitado,
									 String enquadramentoEfetivo,
									 Integer totalImagensCaptadas,
									 Integer totalConsistentesCAI,
									 Integer equipamentoEstatico) {
		super();
		this.dia = dia;
		this.codProdamLocal = codProdamLocal;
		this.descricaoLocal = descricaoLocal;
		this.codProdamFaixa = codProdamFaixa;
		this.faixa = faixa;
		this.dataInicioOperacao = dataInicioOperacao;
		this.enquadramentoHabilitado = enquadramentoHabilitado;
		this.enquadramentoEfetivo = enquadramentoEfetivo;
		this.totalImagensCaptadas = totalImagensCaptadas;
		this.totalConsistentesCAI = totalConsistentesCAI;
		this.equipamentoEstatico = equipamentoEstatico;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/07/2016
	 * @return Construtor para o relatório de Justificativa de Falhas.
	 */
	public ItemEficienciaEquipamento(String dia,
									 Integer codProdamLocal,
									 String descricaoLocal,
									 Integer codProdamFaixa,
									 Integer faixa,
									 String dataInicioOperacao,
									 String enquadramentoHabilitado,
									 String enquadramentoEfetivo,
									 String dataInicioManut,
									 String descricaoManut,
									 String dataFimManut,
									 String estadoManut,
									 Integer equipamentoEstatico) {
		super();
		this.dia = dia;
		this.codProdamLocal = codProdamLocal;
		this.codProdamFaixa = codProdamFaixa;
		this.faixa = faixa;
		this.descricaoLocal = descricaoLocal;
		this.dataInicioOperacao = dataInicioOperacao;
		this.enquadramentoHabilitado = enquadramentoHabilitado;
		this.enquadramentoEfetivo = enquadramentoEfetivo;
		this.dataInicioManut = dataInicioManut;
		this.descricaoManut = descricaoManut;
		this.dataFimManut = dataFimManut;
		this.estadoManut = estadoManut;
		this.equipamentoEstatico = equipamentoEstatico;
	}

	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 30/06/2016
	 * @return Construtor para a lista de enquadramentos ativos.
	 */
	public ItemEficienciaEquipamento(String enquadramentoAtivo) {
		super();
		this.enquadramentoAtivo = enquadramentoAtivo;
	}
	
	
	
	public String getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}


	public String getDataFim() {
		return dataFim;
	}
	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	

	public Integer getCodProdamLocal() {
		return codProdamLocal;
	}
	public void setCodProdamLocal(Integer codProdamLocal) {
		this.codProdamLocal = codProdamLocal;
	}

	
	public Integer getCodProdamFaixa() {
		return codProdamFaixa;
	}
	public void setCodProdamFaixa(Integer codProdamFaixa) {
		this.codProdamFaixa = codProdamFaixa;
	}

	
	public Integer getFaixa() {
		return faixa;
	}
	public void setFaixa(Integer faixa) {
		this.faixa = faixa;
	}


	public String getDescricaoLocal() {
		return descricaoLocal;
	}
	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}

	
	public String getDataInicioOperacao() {
		return dataInicioOperacao;
	}
	public void setDataInicioOperacao(String dataInicioOperacao) {
		this.dataInicioOperacao = dataInicioOperacao;
	}

	
	public String getDescInconsistenciaCAI() {
		return descInconsistenciaCAI;
	}
	public void setDescInconsistenciaCAI(String descInconsistenciaCAI) {
		this.descInconsistenciaCAI = descInconsistenciaCAI;
	}

	
	public Integer getQtdeInconsistenciaCAI() {
		return qtdeInconsistenciaCAI;
	}
	public void setQtdeInconsistenciaCAI(Integer qtdeInconsistenciaCAI) {
		this.qtdeInconsistenciaCAI = qtdeInconsistenciaCAI;
	}

	
	public String getEnquadramentoHabilitado() {
		return enquadramentoHabilitado;
	}
	public void setEnquadramentoHabilitado(String enquadramentoHabilitado) {
		this.enquadramentoHabilitado = enquadramentoHabilitado;
	}


	public String getEnquadramentoEfetivo() {
		return enquadramentoEfetivo;
	}
	public void setEnquadramentoEfetivo(String enquadramentoEfetivo) {
		this.enquadramentoEfetivo = enquadramentoEfetivo;
	}
	
	
	public String getEnquadramentoAtivo() {
		return enquadramentoAtivo;
	}
	public void setEnquadramentoAtivo(String enquadramentoAtivo) {
		this.enquadramentoAtivo = enquadramentoAtivo;
	}

	
	public Integer getEquipamentoEstatico() {
		return equipamentoEstatico;
	}
	public void setEquipamentoEstatico(Integer equipamentoEstatico) {
		this.equipamentoEstatico = equipamentoEstatico;
	}
	
	
	public Integer getTotalImagensCaptadas() {
		return totalImagensCaptadas;
	}
	public void setTotalImagensCaptadas(Integer totalImagensCaptadas) {
		this.totalImagensCaptadas = totalImagensCaptadas;
	}
	

	public Integer getTotalConsistentesCAI() {
		return totalConsistentesCAI;
	}
	public void setTotalConsistentesCAI(Integer totalConsistentesCAI) {
		this.totalConsistentesCAI = totalConsistentesCAI;
	}


	public String getDataInicioManut() {
		return dataInicioManut;
	}
	public void setDataInicioManut(String dataInicioManut) {
		this.dataInicioManut = dataInicioManut;
	}


	public String getDescricaoManut() {
		return descricaoManut;
	}
	public void setDescricaoManut(String descricaoManut) {
		this.descricaoManut = descricaoManut;
	}


	public String getDataFimManut() {
		return dataFimManut;
	}
	public void setDataFimManut(String dataFimManut) {
		this.dataFimManut = dataFimManut;
	}


	public String getEstadoManut() {
		return estadoManut;
	}
	public void setEstadoManut(String estadoManut) {
		this.estadoManut = estadoManut;
	}
}

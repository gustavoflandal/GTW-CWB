/**********************************************************************************

Projeto: GTW
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Thiago Surgik
Data: 02/10/2014

 *********************************************************************************/
package com.consilux.model;

import java.io.Serializable;

/**
 * @author Thiago Surgik - Consilux Tecnologia 
 * @since 02/10/2014
 * 
 */
public class ItemIndicadoresImportacao implements Serializable {

	private static final long serialVersionUID = 2035188461287315850L;
	Integer idLocal;
	Long serieEquipamento;
	Integer faixaEquipamento;
	Integer codigoPista;
	String descLocal;
	String descEquadramentos;
	String dataPublicacao;
	Long[] dias = new Long[31];
	Integer flagFuncionamento;

	public ItemIndicadoresImportacao(Integer idLocal, Long serieEquipamento,
			Integer faixaEquipamento, Integer codigoPista, String descLocal,
			String dataPublicacao, Integer flagFuncionamento, Long[] dias) {

		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.faixaEquipamento = faixaEquipamento;
		this.codigoPista = codigoPista;
		this.descLocal = descLocal;
		this.dataPublicacao = dataPublicacao;
		this.flagFuncionamento = flagFuncionamento;
		this.dias = dias;
	}

	public Integer getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public Long getSerieEquipamento() {
		return serieEquipamento;
	}

	public void setSerieEquipamento(Long serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public Integer getFaixaEquipamento() {
		return faixaEquipamento;
	}

	public void setFaixaEquipamento(Integer faixaEquipamento) {
		this.faixaEquipamento = faixaEquipamento;
	}

	public Integer getCodigoPista() {
		return codigoPista;
	}

	public void setCodigoPista(Integer codigoPista) {
		this.codigoPista = codigoPista;
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

	public String getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(String dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public Long[] getDias() {
		return dias;
	}

	public void setDias(Long[] dias) {
		this.dias = dias;
	}

	public Integer getFlagFuncionamento() {
		return flagFuncionamento;
	}

	public void setFlagFuncionamento(Integer flagFuncionamento) {
		this.flagFuncionamento = flagFuncionamento;
	}

}

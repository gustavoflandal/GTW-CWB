/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 21/08/2014

*********************************************************************************/
package com.consilux.model.relatorio.rj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 28/08/2019
 */
public  class ItemRelOcorrenciaManut implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	
	Integer idManutencao;
	String codigoEquipamentoDER;
	Date dataOcorrencia;
	Date dataCadastro;
	Date dataInicio;
	Date dataTermino;
	String ocorrencia;
	String descricaoDetalhada;
	

	public ItemRelOcorrenciaManut(){
	};
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 28/08/2019
	 * Objetivo: Construtor para o relatório de ocorrências de manutenção.
	 */
	public ItemRelOcorrenciaManut(Integer idManutencao,
								  Date dataOcorrencia,
								  Date dataCadastro,
								  Date dataInicio,
								  Date dataTermino,
								  String ocorrencia,
								  String descricaoDetalhada){

		super();
		this.idManutencao = idManutencao;
		this.dataOcorrencia = dataOcorrencia;
		this.dataCadastro = dataCadastro;
		this.dataInicio = dataInicio;
		this.dataTermino = dataTermino;
		this.ocorrencia = ocorrencia;
		this.descricaoDetalhada = descricaoDetalhada;
	}


	public Integer getIdManutencao() {
		return idManutencao;
	}
	public void setIdManutencao(Integer idManutencao) {
		this.idManutencao = idManutencao;
	}


	public String getCodigoEquipamentoDER() {
		return codigoEquipamentoDER;
	}
	public void setCodigoEquipamentoDER(String codigoEquipamentoDER) {
		this.codigoEquipamentoDER = codigoEquipamentoDER;
	}


	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}
	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}


	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}


	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}


	public Date getDataTermino() {
		return dataTermino;
	}
	public void setDataTermino(Date dataTermino) {
		this.dataTermino = dataTermino;
	}


	public String getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}


	public String getDescricaoDetalhada() {
		return descricaoDetalhada;
	}
	public void setDescricaoDetalhada(String descricaoDetalhada) {
		this.descricaoDetalhada = descricaoDetalhada;
	}
	
}

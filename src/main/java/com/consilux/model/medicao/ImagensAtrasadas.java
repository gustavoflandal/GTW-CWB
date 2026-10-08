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
 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Atraso de Imagens
 * @author Luiz Fernando Amaral - Consilux Tecnologia
 * Data: 05/09/2014
 */

public  class ImagensAtrasadas implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Integer codigo_externo;
	String tipo;
	String dtRemessa;
	String dtInfracao;
	Integer diferencaDias;
	Integer descontoDias;
	Long qtdeInfracoesAtraso;

	public ImagensAtrasadas(Integer codigo_externo,
						    String tipo,
						    String dtRemessa,
						    String dtInfracao,
						    Integer diferencaDias,
						    Integer descontoDias,
						    Long qtdeInfracoesAtraso){
		
		super();
		this.codigo_externo = codigo_externo; 
		this.tipo = tipo;
		this.dtRemessa = dtRemessa;
		this.dtInfracao = dtInfracao;
		this.diferencaDias = diferencaDias;
		this.descontoDias = descontoDias;
		this.qtdeInfracoesAtraso = qtdeInfracoesAtraso;
	}

	public Integer getCodigo_externo() {
		return codigo_externo;
	}

	public void setCodigo_externo(Integer codigo_externo) {
		this.codigo_externo = codigo_externo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDtRemessa() {
		return dtRemessa;
	}

	public void setDtRemessa(String dtRemessa) {
		this.dtRemessa = dtRemessa;
	}

	public String getDtInfracao() {
		return dtInfracao;
	}

	public void setDtInfracao(String dtInfracao) {
		this.dtInfracao = dtInfracao;
	}

	public Integer getDiferencaDias() {
		return diferencaDias;
	}

	public void setDiferencaDias(Integer diferencaDias) {
		this.diferencaDias = diferencaDias;
	}

	public Integer getDescontoDias() {
		return descontoDias;
	}

	public void setDescontoDias(Integer descontoDias) {
		this.descontoDias = descontoDias;
	}

	public Long getQtdeInfracoesAtraso() {
		return qtdeInfracoesAtraso;
	}

	public void setQtdeInfracoesAtraso(Long qtdeInfracoesAtraso) {
		this.qtdeInfracoesAtraso = qtdeInfracoesAtraso;
	}
}

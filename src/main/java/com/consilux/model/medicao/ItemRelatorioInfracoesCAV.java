/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 15/08/2016

*********************************************************************************/
package com.consilux.model.medicao;
import java.io.Serializable;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 15/08/2016
 */
public  class ItemRelatorioInfracoesCAV implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;

	Integer idInconsistencia;
	String motivoInconsistencia;
	Integer qtdeImagensConsistentesCAI;
	Integer qtdeImagensInconsistentesCAI;
	Double porcentagemConsistInconsist;
	Integer qtdeImagensValidasCAV;
	Integer qtdeImagensInvalidasCAV;
	Double porcentagemValidasInvalidas;
	Integer qtdeAmostragem;
	Double porcentagemAmostragem;
	Integer qtde100Porcento;
	Double porcentagem100Porcento;
	
	String data;
	String mesAno;
	String descLocal;
	String descEquipamento;
	Integer idEnquadramento;

	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/08/2016
	 * @return Construtor para o relatório de Produtividade Auditoria
	 */
	public ItemRelatorioInfracoesCAV(Integer idInconsistencia, String motivoInconsistencia,
									 Integer qtdeImagensConsistentesCAI, Integer qtdeImagensInconsistentesCAI, Double porcentagemConsistInconsist,
									 Integer qtdeImagensValidasCAV, Integer qtdeImagensInvalidasCAV, Double porcentagemValidasInvalidas,
									 Integer qtdeAmostragem, Double porcentagemAmostragem,
									 Integer qtde100Porcento, Double porcentagem100Porcento,
									 String data, String mesAno,
									 String descLocal, String descEquipamento, Integer idEnquadramento) {

		super();
		this.idInconsistencia = idInconsistencia;
		this.motivoInconsistencia = motivoInconsistencia;
		this.qtdeImagensConsistentesCAI = qtdeImagensConsistentesCAI;
		this.qtdeImagensInconsistentesCAI = qtdeImagensInconsistentesCAI;
		this.porcentagemConsistInconsist = porcentagemConsistInconsist;
		this.qtdeImagensValidasCAV = qtdeImagensValidasCAV;
		this.qtdeImagensInvalidasCAV = qtdeImagensInvalidasCAV;
		this.porcentagemValidasInvalidas = porcentagemValidasInvalidas;
		this.qtdeAmostragem = qtdeAmostragem;
		this.porcentagemAmostragem = porcentagemAmostragem;
		this.qtde100Porcento = qtde100Porcento;
		this.porcentagem100Porcento = porcentagem100Porcento;
		
		this.data = data;
		this.mesAno = mesAno;
		this.descLocal = descLocal;
		this.descEquipamento = descEquipamento;
		this.idEnquadramento = idEnquadramento;
	}

	
	
	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}
	public void setIdInconsistencia(Integer idInconsistencia) {
		this.idInconsistencia = idInconsistencia;
	}

	
	public String getMotivoInconsistencia() {
		return motivoInconsistencia;
	}
	public void setMotivoInconsistencia(String motivoInconsistencia) {
		this.motivoInconsistencia = motivoInconsistencia;
	}


	public Integer getQtdeImagensConsistentesCAI() {
		return qtdeImagensConsistentesCAI;
	}
	public void setQtdeImagensConsistentesCAI(Integer qtdeImagensConsistentesCAI) {
		this.qtdeImagensConsistentesCAI = qtdeImagensConsistentesCAI;
	}

	
	public Integer getQtdeImagensInconsistentesCAI() {
		return qtdeImagensInconsistentesCAI;
	}
	public void setQtdeImagensInconsistentesCAI(Integer qtdeImagensInconsistentesCAI) {
		this.qtdeImagensInconsistentesCAI = qtdeImagensInconsistentesCAI;
	}

	
	public Double getPorcentagemConsistInconsist() {
		return porcentagemConsistInconsist;
	}
	public void setPorcentagemConsistInconsist(Double porcentagemConsistInconsist) {
		this.porcentagemConsistInconsist = porcentagemConsistInconsist;
	}

	
	public Integer getQtdeImagensValidasCAV() {
		return qtdeImagensValidasCAV;
	}
	public void setQtdeImagensValidasCAV(Integer qtdeImagensValidasCAV) {
		this.qtdeImagensValidasCAV = qtdeImagensValidasCAV;
	}

	
	public Integer getQtdeImagensInvalidasCAV() {
		return qtdeImagensInvalidasCAV;
	}
	public void setQtdeImagensInvalidasCAV(Integer qtdeImagensInvalidasCAV) {
		this.qtdeImagensInvalidasCAV = qtdeImagensInvalidasCAV;
	}

	
	public Double getPorcentagemValidasInvalidas() {
		return porcentagemValidasInvalidas;
	}
	public void setPorcentagemValidasInvalidas(Double porcentagemValidasInvalidas) {
		this.porcentagemValidasInvalidas = porcentagemValidasInvalidas;
	}


	public Integer getQtdeAmostragem() {
		return qtdeAmostragem;
	}
	public void setQtdeAmostragem(Integer qtdeAmostragem) {
		this.qtdeAmostragem = qtdeAmostragem;
	}

	
	public Double getPorcentagemAmostragem() {
		return porcentagemAmostragem;
	}
	public void setPorcentagemAmostragem(Double porcentagemAmostragem) {
		this.porcentagemAmostragem = porcentagemAmostragem;
	}

	
	public Integer getQtde100Porcento() {
		return qtde100Porcento;
	}
	public void setQtde100Porcento(Integer qtde100Porcento) {
		this.qtde100Porcento = qtde100Porcento;
	}

	
	public Double getPorcentagem100Porcento() {
		return porcentagem100Porcento;
	}
	public void setPorcentagem100Porcento(Double porcentagem100Porcento) {
		this.porcentagem100Porcento = porcentagem100Porcento;
	}

	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	
	public String getMesAno() {
		return mesAno;
	}
	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}

	
	public String getDescLocal() {
		return descLocal;
	}
	public void setDescLocal(String descLocal) {
		this.descLocal = descLocal;
	}

	
	public String getDescEquipamento() {
		return descEquipamento;
	}
	public void setDescEquipamento(String descEquipamento) {
		this.descEquipamento = descEquipamento;
	}


	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}
	public void setIdEnquadramento(Integer idEnquadramento) {
		this.idEnquadramento = idEnquadramento;
	}
}

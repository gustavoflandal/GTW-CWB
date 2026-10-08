/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 10/08/2016

*********************************************************************************/
package com.consilux.model.medicao;
import java.io.Serializable;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * DatA: 10/08/2016
 */
public  class ItemDivergenciaCAI_CAV implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Integer codProdamLocal;
	String grupoAuditor;
	Integer codigoExterno;
	Integer sequencia;
	String dataInfracao;
	String nomeImagem;
	String statusCAI;
	String statusCAV;
	Integer revisao;
	String tipoErro;
	String motivoCAI;
	String motivoCAV;
	String placaCAI;
	String placaCAV;
	Integer marcaCAI;
	Integer marcaCAV;
	String digitador;
	String auditor;
	
	
	public ItemDivergenciaCAI_CAV(Integer codProdamLocal, String grupoAuditor, Integer codigoExterno, Integer sequencia,
			String dataInfracao, String nomeImagem, String statusCAI, String statusCAV,
			Integer revisao, String tipoErro, String motivoCAI, String motivoCAV,
			String placaCAI, String placaCAV, Integer marcaCAI, Integer marcaCAV,
			String digitador, String auditor) {

		super();
		this.codProdamLocal = codProdamLocal;
		this.grupoAuditor = grupoAuditor;
		this.codigoExterno = codigoExterno;
		this.sequencia = sequencia;
		this.dataInfracao = dataInfracao;
		this.nomeImagem = nomeImagem;
		this.statusCAI = statusCAI;
		this.statusCAV = statusCAV;
		this.revisao = revisao;
		this.tipoErro = tipoErro;
		this.motivoCAI = motivoCAI;
		this.motivoCAV = motivoCAV;
		this.placaCAI = placaCAI;
		this.placaCAV = placaCAV;
		this.marcaCAI = marcaCAI;
		this.marcaCAV = marcaCAV;
		this.digitador = digitador;
		this.auditor = auditor;
	}

	public Integer getCodProdamLocal() {
		return codProdamLocal;
	}
	public void setCodProdamLocal(Integer codProdamLocal) {
		this.codProdamLocal = codProdamLocal;
	}

	public String getGrupoAuditor() {
		return grupoAuditor;
	}
	public void setGrupoAuditor(String grupoAuditor) {
		this.grupoAuditor = grupoAuditor;
	}

	public Integer getCodigoExterno() {
		return codigoExterno;
	}
	public void setCodigoExterno(Integer codigoExterno) {
		this.codigoExterno = codigoExterno;
	}

	public Integer getSequencia() {
		return sequencia;
	}
	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}
	
	public String getDataInfracao() {
		return dataInfracao;
	}
	public void setDataInfracao(String dataInfracao) {
		this.dataInfracao = dataInfracao;
	}
	
	public String getNomeImagem() {
		return nomeImagem;
	}
	public void setNomeImagem(String nomeImagem) {
		this.nomeImagem = nomeImagem;
	}
	
	public String getStatusCAI() {
		return statusCAI;
	}
	public void setStatusCAI(String statusCAI) {
		this.statusCAI = statusCAI;
	}
	
	public String getStatusCAV() {
		return statusCAV;
	}
	public void setStatusCAV(String statusCAV) {
		this.statusCAV = statusCAV;
	}

	public Integer getRevisao() {
		return revisao;
	}
	public void setRevisao(Integer revisao) {
		this.revisao = revisao;
	}

	public String getTipoErro() {
		return tipoErro;
	}
	public void setTipoErro(String tipoErro) {
		this.tipoErro = tipoErro;
	}

	public String getMotivoCAI() {
		return motivoCAI;
	}
	public void setMotivoCAI(String motivoCAI) {
		this.motivoCAI = motivoCAI;
	}

	public String getMotivoCAV() {
		return motivoCAV;
	}
	public void setMotivoCAV(String motivoCAV) {
		this.motivoCAV = motivoCAV;
	}
	
	public String getPlacaCAI() {
		return placaCAI;
	}
	public void setPlacaCAI(String placaCAI) {
		this.placaCAI = placaCAI;
	}
	
	public String getPlacaCAV() {
		return placaCAV;
	}
	public void setPlacaCAV(String placaCAV) {
		this.placaCAV = placaCAV;
	}
	
	public Integer getMarcaCAI() {
		return marcaCAI;
	}
	public void setMarcaCAI(Integer marcaCAI) {
		this.marcaCAI = marcaCAI;
	}
	
	public Integer getMarcaCAV() {
		return marcaCAV;
	}
	public void setMarcaCAV(Integer marcaCAV) {
		this.marcaCAV = marcaCAV;
	}
	
	public String getDigitador() {
		return digitador;
	}
	public void setDigitador(String digitador) {
		this.digitador = digitador;
	}
	
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
}

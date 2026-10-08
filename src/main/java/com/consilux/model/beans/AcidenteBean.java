/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 13/03/2009

  Descricao: Bean de informações do formulário de cadastro de acidente.

  Historico:

    $Log: AcidenteBean.java,v $
    Revision 1.1  2009/03/18 17:18:54  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model.beans;

/**
 * Bean de informações do formulário de cadastro de acidente.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1.2.2 $ $Date: 2009/06/21 12:56:43 $ $Author: charles.maske $
 */
public class AcidenteBean {
	private Integer id = 0;
	private Integer idLogradouro = 0;
	private Integer tipoGravidade = 0;
	private Integer tipoIluminacao = 0;
	private Integer tipoArea = 0;
	private Integer tipoEquipamentoSeguranca = 0;
	private Integer sexo = 0;
	private Integer faixaEtaria = 0;
	private Integer tipoVeiculo = 0;
	private Integer tipoAcidente = 0;
	private Integer envolvidos = 0;
	private Integer situacaoCondutor = 0;
	private Integer estadoVeiculo = 0;
	private Integer estadoPneus = 0;
	private Integer danosCausados = 0;
	private Integer origemVeiculo = 0;
	private Integer sinalizacaoSemaforica = 0;
	private Integer tipoPista = 0;
	private Integer outrasSinalizacoes = 0;
	private Integer condicoesPista = 0;
	private Integer condicoesTempo = 0;
	private Integer caracteristicasVia = 0;	
	private String descricaoGeral = "";
	private String endereco = "";
	private Double latitude;
	private Double longitude;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getIdLogradouro() {
		return idLogradouro;
	}
	public void setIdLogradouro(Integer idLogradouro) {
		this.idLogradouro = idLogradouro;
	}
	public Integer getTipoGravidade() {
		return tipoGravidade;
	}
	public void setTipoGravidade(Integer tipoGravidade) {
		this.tipoGravidade = tipoGravidade;
	}
	public Integer getTipoIluminacao() {
		return tipoIluminacao;
	}
	public void setTipoIluminacao(Integer tipoIluminacao) {
		this.tipoIluminacao = tipoIluminacao;
	}
	public Integer getTipoArea() {
		return tipoArea;
	}
	public void setTipoArea(Integer tipoArea) {
		this.tipoArea = tipoArea;
	}
	public Integer getTipoEquipamentoSeguranca() {
		return tipoEquipamentoSeguranca;
	}
	public void setTipoEquipamentoSeguranca(Integer tipoEquipamentoSeguranca) {
		this.tipoEquipamentoSeguranca = tipoEquipamentoSeguranca;
	}
	public Integer getSexo() {
		return sexo;
	}
	public void setSexo(Integer sexo) {
		this.sexo = sexo;
	}
	public Integer getFaixaEtaria() {
		return faixaEtaria;
	}
	public void setFaixaEtaria(Integer faixaEtaria) {
		this.faixaEtaria = faixaEtaria;
	}
	public Integer getTipoVeiculo() {
		return tipoVeiculo;
	}
	public void setTipoVeiculo(Integer tipoVeiculo) {
		this.tipoVeiculo = tipoVeiculo;
	}
	public Integer getTipoAcidente() {
		return tipoAcidente;
	}
	public void setTipoAcidente(Integer tipoAcidente) {
		this.tipoAcidente = tipoAcidente;
	}
	public Integer getEnvolvidos() {
		return envolvidos;
	}
	public void setEnvolvidos(Integer envolvidos) {
		this.envolvidos = envolvidos;
	}
	public Integer getSituacaoCondutor() {
		return situacaoCondutor;
	}
	public void setSituacaoCondutor(Integer situacaoCondutor) {
		this.situacaoCondutor = situacaoCondutor;
	}
	public Integer getEstadoVeiculo() {
		return estadoVeiculo;
	}
	public void setEstadoVeiculo(Integer estadoVeiculo) {
		this.estadoVeiculo = estadoVeiculo;
	}
	public Integer getEstadoPneus() {
		return estadoPneus;
	}
	public void setEstadoPneus(Integer estadoPneus) {
		this.estadoPneus = estadoPneus;
	}
	public Integer getDanosCausados() {
		return danosCausados;
	}
	public void setDanosCausados(Integer danosCausados) {
		this.danosCausados = danosCausados;
	}
	public Integer getOrigemVeiculo() {
		return origemVeiculo;
	}
	public void setOrigemVeiculo(Integer origemVeiculo) {
		this.origemVeiculo = origemVeiculo;
	}
	public Integer getSinalizacaoSemaforica() {
		return sinalizacaoSemaforica;
	}
	public void setSinalizacaoSemaforica(Integer sinalizacaoSemaforica) {
		this.sinalizacaoSemaforica = sinalizacaoSemaforica;
	}
	public Integer getTipoPista() {
		return tipoPista;
	}
	public void setTipoPista(Integer tipoPista) {
		this.tipoPista = tipoPista;
	}
	public Integer getOutrasSinalizacoes() {
		return outrasSinalizacoes;
	}
	public void setOutrasSinalizacoes(Integer outrasSinalizacoes) {
		this.outrasSinalizacoes = outrasSinalizacoes;
	}
	public Integer getCondicoesPista() {
		return condicoesPista;
	}
	public void setCondicoesPista(Integer condicoesPista) {
		this.condicoesPista = condicoesPista;
	}
	public Integer getCondicoesTempo() {
		return condicoesTempo;
	}
	public void setCondicoesTempo(Integer condicoesTempo) {
		this.condicoesTempo = condicoesTempo;
	}
	public Integer getCaracteristicasVia() {
		return caracteristicasVia;
	}
	public void setCaracteristicasVia(Integer caracteristicasVia) {
		this.caracteristicasVia = caracteristicasVia;
	}
	public String getDescricaoGeral() {
		return descricaoGeral;
	}
	public void setDescricaoGeral(String descricaoGeral) {
		this.descricaoGeral = descricaoGeral;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}

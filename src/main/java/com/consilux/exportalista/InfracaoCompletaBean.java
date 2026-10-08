package com.consilux.exportalista;
import java.io.Serializable;
import java.util.Date;

public class InfracaoCompletaBean extends ExportaLista implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdImagemLocal(Integer idImagemLocal) {
		this.idImagemLocal = idImagemLocal;
	}

	public void setAuto(Integer auto) {
		this.auto = auto;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public void setTipoRemessa(String tipoRemessa) {
		this.tipoRemessa = tipoRemessa;
	}

	public void setIdEnquadramento(Integer idEnquadramento) {
		this.idEnquadramento = idEnquadramento;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public void setNomeLocal(String nomeLocal) {
		this.nomeLocal = nomeLocal;
	}

	public void setDataVeiculo(Date dataVeiculo) {
		this.dataVeiculo = dataVeiculo;
	}

	private Integer id;
	private Integer idImagemLocal;
	private Integer auto;
	private String serie;
	private String tipoRemessa;
	private Integer idEnquadramento;
	private String placa;
	private String marca;
	private String nomeLocal;
	private Integer pista;
	private Date dataVeiculo;
	private Integer velocidade;
	private Integer velocidadePontoA;
	private Integer velocidadePontoB;
	private Integer velocidadeMedia;
	
	public Integer getVelocidadePontoA() {
		return velocidadePontoA;
	}

	public void setVelocidadePontoA(Integer velocidadePontoA) {
		this.velocidadePontoA = velocidadePontoA;
	}

	public Integer getVelocidadePontoB() {
		return velocidadePontoB;
	}

	public void setVelocidadePontoB(Integer velocidadePontoB) {
		this.velocidadePontoB = velocidadePontoB;
	}

	public Integer getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public void setVelocidadeMedia(Integer velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}

	private String diaSemana;
	private String classe;

	public String getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Integer getPista() {
		return pista;
	}

	public void setPista(Integer faixa) {
		this.pista = faixa;
	}

	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Retorna o valor de idImagemLocal atual.
	 */
	public Integer getIdImagemLocal() {
		return idImagemLocal;
	}

	/**
	 * @return Retorna o valor de placa atual.
	 */
	public String getPlaca() {
		return placa;
	}

	/**
	 * @return Retorna o valor de marca atual.
	 */
	public String getMarca() {
		return marca;
	}

	/**
	 * @return Retorna o valor de nomeLocal atual.
	 */
	public String getNomeLocal() {
		return nomeLocal;
	}

	/**
	 * @return Retorna o valor de dataVeiculo atual.
	 */
	public Date getDataVeiculo() {
		return dataVeiculo;
	}

	/**
	 * @return Retorna o valor de idEnquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}

	public Integer getAuto() {
		return auto;
	}
	
	public String getSerie() {
		return serie;
	}

	public String getTipoRemessa() {
		return tipoRemessa;
	}

	public Integer getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(Integer velocidade) {
		this.velocidade = velocidade;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}
	

}

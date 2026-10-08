package com.consilux.model.beans;

import java.io.Serializable;
import java.util.Date;


/**
 * Bean que representa um veículo monitorado. 
 * @author raoni
 */
public class VeiculoMonitoradoBean implements Serializable {

	private static final long serialVersionUID = -6732284261280038670L;

	private String placaIrregular;
	private String descricao;
	private String situacao;
	private Integer idSituacao;
	private String marca;
	private String cor;
	private Date dataCadastro;
	private Date dataExclusao;
	private Boolean falsoPositivo;
	private String usuario;
	private int id;
	private Date dataHora;
	private int idLocal;
	private String nomeLocal;
	private Integer pista;
	private String placa;
	private float velocidade;
	private String sentido;
	private int idImagem;
	private Integer serieEquipamento;
	private Integer idImagemLocal;
	private Integer idEmail;
	private String emailDestino;
	
	public String getPlacaIrregular() {
		return placaIrregular;
	}
	
	public void setPlacaIrregular(String placaIrregular) {
		this.placaIrregular = placaIrregular;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getSituacao() {
		return situacao;
	}
	
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	public Integer getIdSituacao() {
		return idSituacao;
	}
	
	public void setIdSituacao(Integer idSituacao) {
		this.idSituacao = idSituacao;
	}
	
	public String getMarca() {
		return marca;
	}
	
	public void setMarca(String marca) {
		this.marca = marca;
	}
	
	public String getCor() {
		return cor;
	}
	
	public void setCor(String cor) {
		this.cor = cor;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	public Date getDataExclusao() {
		return dataExclusao;
	}
	
	public void setDataExclusao(Date dataExclusao) {
		this.dataExclusao = dataExclusao;
	}
	
	public Boolean getFalsoPositivo() {
		return falsoPositivo;
	}
	
	public void setFalsoPositivo(Boolean falsoPositivo) {
		this.falsoPositivo = falsoPositivo;
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public String getNomeLocal() {
		return nomeLocal;
	}

	public void setNomeLocal(String nomeLocal) {
		this.nomeLocal = nomeLocal;
	}

	public Integer getPista() {
		return pista;
	}

	public void setPista(Integer pista) {
		this.pista = pista;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public float getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(float velocidade) {
		this.velocidade = velocidade;
	}

	public int getIdImagem() {
		return idImagem;
	}

	public void setIdImagem(int idImagem) {
		this.idImagem = idImagem;
	}

	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}

	public void setSerieEquipamento(Integer serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public Integer getIdImagemLocal() {
		return idImagemLocal;
	}

	public void setIdImagemLocal(Integer idImagemLocal) {
		this.idImagemLocal = idImagemLocal;
	}

	public Integer getIdEmail() {
		return idEmail;
	}

	public void setIdEmail(Integer idEmail) {
		this.idEmail = idEmail;
	}

	public String getSentido() {
		return sentido;
	}
	
	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	
	/**
	 * Recupera o email de destino do veículo mmonitorado.
	 * @return
	 */
	public String getEmailDestino() {
		return emailDestino;
	}

	public void setEmailDestino(String emailDestino) {
		this.emailDestino = emailDestino;
	}
	
}

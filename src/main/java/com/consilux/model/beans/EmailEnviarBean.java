package com.consilux.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;


/**
 * Bean que representa um email a ser enviado.
 * @author raoni
 */
public class EmailEnviarBean implements Serializable {

	private static final long serialVersionUID = 7177390371033274694L;
	
	private int idEmail;
	private Date dataCriacao;
	private Date dataEnvio;
	private InternetAddress remetente;
	private String assunto;
	private String corpo;
	
	private List<InternetAddress> destinatarios = new ArrayList<InternetAddress>();
	private List<AnexoEmailBean> anexos = new ArrayList<AnexoEmailBean>();
	
	public EmailEnviarBean() {
		// Construtor sem parâmetros, padrão Javabeans para
		// atender o Serializable.
		this.idEmail = 0;
		this.dataEnvio = null;
		this.dataCriacao = new Date();
		this.corpo = "";
	}
	
	public EmailEnviarBean(Date dataCriacao, InternetAddress remetente, String assunto,
			String corpo, List<InternetAddress> destinatarios,
			List<AnexoEmailBean> anexos) {
		
		this();
		this.dataCriacao = dataCriacao;
		this.remetente = remetente;
		this.assunto = assunto;
		this.corpo = corpo;
		
		if (destinatarios != null)
			this.destinatarios.addAll(destinatarios);
		
		if (anexos != null)
			this.anexos.addAll(anexos);
	}

	public int getIdEmail() {
		return idEmail;
	}

	public void setIdEmail(int idEmail) {
		this.idEmail = idEmail;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}
	
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	public InternetAddress getRemetente() {
		return remetente;
	}
	
	public void setRemetente(InternetAddress remetente) {
		this.remetente = remetente;
	}
	
	public String getAssunto() {
		return assunto;
	}
	
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	
	public String getCorpo() {
		return corpo;
	}
	
	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}
	
	public List<InternetAddress> getDestinatarios() {
		return destinatarios;
	}
	
	public void setDestinatarios(List<InternetAddress> destinatarios) {
		this.destinatarios = destinatarios;
	}
	
	public List<AnexoEmailBean> getAnexos() {
		return anexos;
	}
	
	public void setAnexos(List<AnexoEmailBean> anexos) {
		this.anexos = anexos;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}	
	
	public boolean isEnviado() {
		return dataEnvio != null;
	}
	
}

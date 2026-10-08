package com.consilux.model.beans;

import java.io.Serializable;

/**
 * Bean que representa um anexo de um email.
 * @author raoni
 */
public class AnexoEmailBean implements Serializable {

	private static final long serialVersionUID = -63354031642894731L;

	private int idEmail;
	private int idAnexo;
	private String contentType;
	private String nomeArquivo;
	private byte[] bytesArquivo;

	public AnexoEmailBean() {
		// Construtor sem parâmetros, pádrão Javabeans para
		// atender o Serializable.
		this.idEmail = 0;
		this.idAnexo = 0;		
	}

	public AnexoEmailBean(String contentType, String nomeArquivo,
			byte[] bytesArquivo) {

		this();
		this.contentType = contentType;
		this.nomeArquivo = nomeArquivo;
		this.bytesArquivo = bytesArquivo;
	}

	public int getIdEmail() {
		return idEmail;
	}

	public void setIdEmail(int idEmail) {
		this.idEmail = idEmail;
	}

	public int getIdAnexo() {
		return idAnexo;
	}

	public void setIdAnexo(int idAnexo) {
		this.idAnexo = idAnexo;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public byte[] getBytesArquivo() {
		return bytesArquivo;
	}

	public void setBytesArquivo(byte[] bytesArquivo) {
		this.bytesArquivo = bytesArquivo;
	}

}

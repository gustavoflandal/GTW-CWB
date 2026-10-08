package com.consilux.model.beans;

import java.util.Date;

public class ExportaComprovacaoImagemBean {

//	serie_equipamento,
//	cod_pista,
//	cod_pista_prodam,
//	data,
//	metrologica,
//	qualificador,
//	id_imagem_obj,
//	img_obj,	   (BLOB)
//	id_imagem_pan, (Pode vir nulo)
//	img_pan        (BLOB) (Pode vir nulo)	
	
	private int serieEquipamento;
	private int codigoPista;
	private int codigoPistaProdam;
	private Date dataImagem;
	private boolean metrologica;
	private String qualificador;
	
	private int idImagemObj;
	private byte[] blobImagemObj;
	private Integer idImagemPan;
	private byte[] blobImagemPan;
	
	public ExportaComprovacaoImagemBean(int serieEquipamento,  int codigoPista,
			int codigoPistaProdam, Date dataImagem, boolean metrologica,
			String qualificador,  int idImagemObj, byte[] blobImagemObj,
			Integer idImagemPan, byte[] blobImagemPan) {
		
		super();
		
		this.serieEquipamento = serieEquipamento;
		this.codigoPista = codigoPista;
		this.codigoPistaProdam = codigoPistaProdam;
		this.dataImagem = dataImagem;
		this.metrologica = metrologica;
		this.qualificador = qualificador;
		this.idImagemObj = idImagemObj;
		this.blobImagemObj = blobImagemObj;
		this.idImagemPan = idImagemPan;
		this.blobImagemPan = blobImagemPan;
	}

	public int getSerieEquipamento() {
		return serieEquipamento;
	}
	
	
	public int getCodigoPista() {
		return codigoPista;
	}

	public int getCodigoPistaProdam() {
		return codigoPistaProdam;
	}
	
	public Date getDataImagem() {
		return dataImagem;
	}

	public boolean isMetrologica() {
		return metrologica;
	}

	public String getQualificador() {
		return qualificador;
	}

	public int getIdImagemObj() {
		return idImagemObj;
	}

	public byte[] getBlobImagemObj() {
		return blobImagemObj;
	}

	public Integer getIdImagemPan() {
		return idImagemPan;
	}

	public byte[] getBlobImagemPan() {
		return blobImagemPan;
	}
}

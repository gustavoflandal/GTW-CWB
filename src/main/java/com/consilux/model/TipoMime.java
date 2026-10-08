package com.consilux.model;

/**
 * Enumerador padrão que contém alguns dos tipos Mime
 * mais utilizados. 
 * @author raoni
 */
public enum TipoMime {

	JPG("image/jpg", ".jpg"),
	PNG("image/png", ".png"),
	GIF("image/gif", ".gif"),
	PDF("application/pdf", ".pdf"),
	XLS("application/vnd.ms-excel", ".xls"),
	HTML("text/html", ".html"),
	ZIP("application/zip", ".zip"),
	ISO("application/x-iso9660-image", ".iso"),
	BIN("application/octet-stream", ".bin"),
	XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",".xlsx"),
	CSV("text/csv", ".csv");
	
	private String tipo;
	private String extensao; 
	
	public String getTipo() {
		return this.tipo;
	}

	public String getExtensao() {
		return this.extensao;
	}
	
	private TipoMime(String tipo, String extensao) {
		this.tipo = tipo;
		this.extensao = extensao;
	}
	
}

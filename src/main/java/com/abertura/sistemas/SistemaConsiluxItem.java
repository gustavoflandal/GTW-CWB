package com.abertura.sistemas;

import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.log4j.Logger;

@XmlRootElement(name = "SistemaItem")
@XmlAccessorType (XmlAccessType.FIELD)
public class SistemaConsiluxItem  extends HttpServlet 
{
	@XmlTransient
    private static final long 		serialVersionUID 	= 1L;

    @XmlTransient
	private static final Logger logger = Logger.getLogger(SistemaConsiluxItem.class);
    
    private int 		idSistema;
	private String		descricao;
    private String		descricaoDetalhada;
    private Boolean		mostrarPainel;
    private String 		urlExterna;
    
    public SistemaConsiluxItem() {}
        
	public SistemaConsiluxItem(int id, String desc, String descDetail, Boolean mostrar)
	{
		idSistema 				= id;
		descricao 				= desc;
		descricaoDetalhada 		= descDetail;
		mostrarPainel 			= mostrar;
	}
	
    public int getIdSistema() {
		return idSistema;
	}

	public void setIdSistema(int idSistema) {
		this.idSistema = idSistema;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricaoDetalhada() {
		return descricaoDetalhada;
	}

	public void setDescricaoDetalhada(String descricaoDetalhada) {
		this.descricaoDetalhada = descricaoDetalhada;
	}

	public Boolean getMostrarPainel() {
		return mostrarPainel;
	}

	public void setMostrarPainel(Boolean mostrarPainel) {
		this.mostrarPainel = mostrarPainel;
	}

	public String getUrlExterna() {
		return urlExterna;
	}

	public void setUrlExterna(String urlExterna) {
		this.urlExterna = urlExterna;
	}

}
 
package com.abertura.sistemas;

import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


@XmlRootElement		(name="SistemasConsilux") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class SistemasConsilux extends HttpServlet 
{
	
	@XmlTransient
	private static final long serialVersionUID = 1L;
	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(SistemasConsilux.class);
	
	@XmlElementWrapper	(name = "ListaSistemas")
	@XmlElement			(name = "item")	
	private List<SistemaConsiluxItem>  	listaSistemas;	
	
	
	public List<SistemaConsiluxItem> getListaSistemas() {
		return listaSistemas;
	}


	public void setListaSistemas(List<SistemaConsiluxItem> listaSistemas) {
		this.listaSistemas = listaSistemas;
	}

	public SistemasConsilux()
	{
		super();
	}
}


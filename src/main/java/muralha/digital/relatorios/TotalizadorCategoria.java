package muralha.digital.relatorios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "TotalizadorCategoria")
@XmlAccessorType (XmlAccessType.FIELD)
public class TotalizadorCategoria
{
    @XmlTransient
	private static final Logger logger = Logger.getLogger(TotalizadorCategoria.class);
	
	public TotalizadorCategoria() {}
	
	private String categoria;

	private Integer total;
	
	
	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
	
	public Integer getTotal() { return total; }
	public void setTotal(Integer total) { this.total = total; }
}

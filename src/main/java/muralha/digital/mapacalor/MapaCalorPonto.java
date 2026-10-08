package muralha.digital.mapacalor;

import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "PontoCalor")
@XmlAccessorType (XmlAccessType.FIELD)
public class MapaCalorPonto extends HttpServlet 
{

	@XmlTransient
    private static final long 		serialVersionUID 	= 1L;
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(MapaCalorPonto.class);
	
	private int 		idLocal;
	private String		descLocal;
	private double 		latitude;
	private double 		longitude;
	private int			qtde;
	
	public MapaCalorPonto() {}
	
	
	public MapaCalorPonto(int id, String desc, double lat, double longit, int qtde)
	{
		this.idLocal 		= id;
		this.descLocal 		= desc;
		this.latitude 		= lat;
		this.longitude 		= longit;
		this.qtde 			= qtde;
	}
	
	public int getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}
	public String getDescLocal() {
		return descLocal;
	}
	public void setDescLocal(String descLocal) {
		this.descLocal = descLocal;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getQtde() {
		return qtde;
	}
	public void setQtde(int qtde) {
		this.qtde = qtde;
	}
	
}

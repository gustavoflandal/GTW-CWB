package muralha.digital.anomalia;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Anomalia {
	
	private int 	id;
	private String	tipo;
	private int		possuiAnomalia;
	private String	descAnomalia;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getPossuiAnomalia() {
		return possuiAnomalia;
	}
	public void setPossuiAnomalia(int possuiAnomalia) {
		this.possuiAnomalia = possuiAnomalia;
	}
	public String getDescAnomalia() {
		return descAnomalia;
	}
	public void setDescAnomalia(String descAnomalia) {
		this.descAnomalia = descAnomalia;
	}
}

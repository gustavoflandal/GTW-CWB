package muralha.digital.relatorios;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class CalendarioIntensidade
{
	private int semana;

	private String domingo;
	private String segunda;
	private String terca;
	private String quarta;
	private String quinta;
	private String sexta;
	private String sabado;
	
	
	public int getSemana() {
		return semana;
	}
	public void setSemana(int semana) {
		this.semana = semana;
	}
	public String getSegunda() {
		return segunda;
	}
	public void setSegunda(String segunda) {
		this.segunda = segunda;
	}
	public String getTerca() {
		return terca;
	}
	public void setTerca(String terca) {
		this.terca = terca;
	}
	public String getQuarta() {
		return quarta;
	}
	public void setQuarta(String quarta) {
		this.quarta = quarta;
	}
	public String getQuinta() {
		return quinta;
	}
	public void setQuinta(String quinta) {
		this.quinta = quinta;
	}
	public String getSexta() {
		return sexta;
	}
	public void setSexta(String sexta) {
		this.sexta = sexta;
	}
	public String getSabado() {
		return sabado;
	}
	public void setSabado(String sabado) {
		this.sabado = sabado;
	}
	public String getDomingo() {
		return domingo;
	}
	public void setDomingo(String domingo) {
		this.domingo = domingo;
	}
	
	
	
	
}

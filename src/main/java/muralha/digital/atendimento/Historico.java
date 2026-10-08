package muralha.digital.atendimento;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Historico {
	
	private int id;
	private String tipoHistorico;
	private String evento;
	private	Date data;	
	private String usuario;
	
	public String           dataFormatada = "";
	
	public String getTipoHistorico() {
		return tipoHistorico;
	}
	public void setTipoHistorico(String tipoHistorico) {
		this.tipoHistorico = tipoHistorico;
	}
	public String getEvento() {
		return evento;
	}
	public void setEvento(String evento) {
		this.evento = evento;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public String getDataFormatada(){
		 if (data == null)
	        return "";return new SimpleDateFormat("dd/MM/yyyy HH:mm:mm").format(data);	
    }
	public void setDataFormatada(String dataFormatada){	
		this.dataFormatada = dataFormatada;	
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	

}

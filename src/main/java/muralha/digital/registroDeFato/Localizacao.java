package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="localizacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class Localizacao {
	
    private int cidadeId;
    private int tipoEnderecoEvento;
    private String cep;
    private String bairro;
    private String rua;
    private String numero;
    private String complemento;
    private double latitude;
    private double longitude;
    private String tipoEnderecoEventoDescricao;
    
	public int getCidadeId() {
		return cidadeId;
	}
	
	public void setCidadeId(int cidadeId) {
		this.cidadeId = cidadeId;
	}
	
	public int getTipoEnderecoEvento() {
		return tipoEnderecoEvento;
	}
	
	public void setTipoEnderecoEvento(int tipoEnderecoEvento) {
		this.tipoEnderecoEvento = tipoEnderecoEvento;
	}
	
	public String getCep() {
		return cep;
	}
	
	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public String getBairro() {
		return bairro;
	}
	
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	
	public String getRua() {
		return rua;
	}
	
	public void setRua(String rua) {
		this.rua = rua;
	}
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public String getComplemento() {
		return complemento;
	}
	
	public void setComplemento(String complemento) {
		this.complemento = complemento;
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

	public String getTipoEnderecoEventoDescricao() {
		return tipoEnderecoEventoDescricao;
	}

	public void setTipoEnderecoEventoDescricao(String tipoEnderecoEventoDescricao) {
		this.tipoEnderecoEventoDescricao = tipoEnderecoEventoDescricao;
	}        	
}

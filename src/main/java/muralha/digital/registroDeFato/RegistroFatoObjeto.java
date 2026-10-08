package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="registroFato")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroFatoObjeto {
	private int id;
	private int id_registro_fato;
	private String tipo;
	private String descricao;
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_registro_fato() {
		return id_registro_fato;
	}
	
	public void setId_registro_fato(int id_registro_fato) {
		this.id_registro_fato = id_registro_fato;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}		
}

package muralha.digital.boletim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.*;
@XmlRootElement(name = "IndividuoTipo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimIndividuoTipo {

    private Integer id;

    private String descricao;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}

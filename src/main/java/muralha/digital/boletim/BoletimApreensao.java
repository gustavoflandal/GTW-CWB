package muralha.digital.boletim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BoletimApreensao")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimApreensao {
	private Integer id;
	private Integer idBoletim;
	private String tipo;
	private String descricao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdBoletim() {
		return idBoletim;
	}

	public void setIdBoletim(Integer idBoletim) {
		this.idBoletim = idBoletim;
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

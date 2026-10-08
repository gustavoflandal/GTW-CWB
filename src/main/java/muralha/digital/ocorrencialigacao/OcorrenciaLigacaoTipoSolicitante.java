package muralha.digital.ocorrencialigacao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OcorrenciaLigacaoTipoSolicitante")
@XmlAccessorType (XmlAccessType.FIELD)
public class OcorrenciaLigacaoTipoSolicitante {
	
	private int id;
	private String descricao;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}		
}

package muralha.digital.ocorrencia;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "StatusOcorrencia")
@XmlAccessorType (XmlAccessType.FIELD)
public class StatusOcorrencia
{
	
	private UUID id;
	private String descricao;
	
	public StatusOcorrencia() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}

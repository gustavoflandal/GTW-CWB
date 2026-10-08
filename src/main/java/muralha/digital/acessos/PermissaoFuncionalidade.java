package muralha.digital.acessos;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PermissaoFuncionalidade")
@XmlAccessorType (XmlAccessType.FIELD)
public class PermissaoFuncionalidade 
{
	private UUID idPermFunc;
	private String descricao;
	
	public UUID getIdPermFunc() {
		return idPermFunc;
	}
	public void setIdPermFunc(UUID idPermFunc) {
		this.idPermFunc = idPermFunc;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	
}

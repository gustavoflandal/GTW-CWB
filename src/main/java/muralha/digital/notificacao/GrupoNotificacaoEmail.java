package muralha.digital.notificacao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "GrupoNotificacaoEmail")
@XmlAccessorType (XmlAccessType.FIELD)
public class GrupoNotificacaoEmail
{
	private Integer idGrupo;
	private Integer idUsuario;
	private String email;
	
	public GrupoNotificacaoEmail() {}

	
	public Integer getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}

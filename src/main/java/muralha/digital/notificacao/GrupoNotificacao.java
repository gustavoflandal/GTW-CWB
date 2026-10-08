package muralha.digital.notificacao;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "GrupoNotificacao")
@XmlAccessorType (XmlAccessType.FIELD)
public class GrupoNotificacao
{
	private UUID id;
	private UUID idTipoRegistro;
	private String tipoRegistro;
	private UUID idTipoAlertaOcorrencia;
	private String tipoAlertaOcorrencia;
	private Integer idGrupo;
	private String grupo;
	private UUID idTipoNotificacao;
	private String tipoNotificacao;
	private Integer idUsuario;
	private Integer ativo;
	
	public GrupoNotificacao() {}

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getIdTipoRegistro() {
		return idTipoRegistro;
	}
	public void setIdTipoRegistro(UUID idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}
	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public UUID getIdTipoAlertaOcorrencia() {
		return idTipoAlertaOcorrencia;
	}
	public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) {
		this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia;
	}

	public String getTipoAlertaOcorrencia() {
		return tipoAlertaOcorrencia;
	}
	public void setTipoAlertaOcorrencia(String tipoAlertaOcorrencia) {
		this.tipoAlertaOcorrencia = tipoAlertaOcorrencia;
	}

	public Integer getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public UUID getIdTipoNotificacao() {
		return idTipoNotificacao;
	}
	public void setIdTipoNotificacao(UUID idTipoNotificacao) {
		this.idTipoNotificacao = idTipoNotificacao;
	}

	public String getTipoNotificacao() {
		return tipoNotificacao;
	}
	public void setTipoNotificacao(String tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getAtivo() {
		return ativo;
	}

	public void setAtivo(Integer ativo) {
		this.ativo = ativo;
	}
}

package muralha.digital.monitoramento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Camera")
@XmlAccessorType (XmlAccessType.FIELD)
public class Camera
{
    private String descricao, ip, ipLocal, qualidade, framerate, resolution, tipoCam, pista, urlStream;
    private Integer idGrupoExibicao;
	
	public Camera() {}

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public String getIp() { return ip; }
	public void setIp(String ip) { this.ip = ip; }

	public String getIpLocal() { return ipLocal; }
	public void setIpLocal(String ipLocal) { this.ipLocal = ipLocal; }

	public String getQualidade() { return qualidade; }
	public void setQualidade(String qualidade) { this.qualidade = qualidade; }

	public String getFramerate() { return framerate; }
	public void setFramerate(String framerate) { this.framerate = framerate; }

	public String getResolution() { return resolution; }
	public void setResolution(String resolution) { this.resolution = resolution; }

	public String getTipoCam() { return tipoCam; }
	public void setTipoCam(String tipoCam) { this.tipoCam = tipoCam; }

	public String getPista() { return pista; }
	public void setPista(String pista) { this.pista = pista; }
	
	public String getUrlStream() { return urlStream; }
	public void setUrlStream(String urlStream) { this.urlStream = urlStream; }

	public Integer getIdGrupoExibicao() { return idGrupoExibicao; }
	public void setIdGrupoExibicao(Integer idGrupoExibicao) { this.idGrupoExibicao = idGrupoExibicao; }
}

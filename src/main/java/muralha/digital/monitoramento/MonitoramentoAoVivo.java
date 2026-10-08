package muralha.digital.monitoramento;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "ConfigMonitoramentoAoVivo")
@XmlAccessorType (XmlAccessType.FIELD)
public class MonitoramentoAoVivo
{
    @XmlTransient
	private static final Logger logger = Logger.getLogger(MonitoramentoAoVivo.class);
	
	private UUID id;
	private Integer segundos;
	private Date dataConfiguracao;
	private boolean ativo;
	private Integer idUsuario;
	private String usuario;
	private String nomeUsuario;
	private Integer grupoCamerasEmExibicao;
	
	public MonitoramentoAoVivo() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public Integer getSegundos() { return segundos; }
	public void setSegundos(Integer segundos) { this.segundos = segundos; }

	public Date getDataConfiguracao() { return dataConfiguracao; }
	public void setDataConfiguracao(Date dataConfiguracao) { this.dataConfiguracao = dataConfiguracao; }

	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

	public Integer getIdUsuario() { return idUsuario; }
	public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

	public String getUsuario() { return usuario; }
	public void setUsuario(String usuario) { this.usuario = usuario; }

	public String getNomeUsuario() { return nomeUsuario; }
	public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

	public Integer getGrupoCamerasEmExibicao() { return grupoCamerasEmExibicao; }
	public void setGrupoCamerasEmExibicao(Integer grupoCamerasEmExibicao) { this.grupoCamerasEmExibicao = grupoCamerasEmExibicao; }
}

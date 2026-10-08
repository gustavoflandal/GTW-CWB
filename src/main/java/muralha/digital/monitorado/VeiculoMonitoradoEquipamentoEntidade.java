package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "VeiculoMonitoradoEquipamento")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoEquipamentoEntidade {

	private UUID id;
	private UUID idCadVeiculoMonitorado;
	private Integer idLocal;
	private Date dataCadastro;
	private Integer idUsuario;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getIdCadVeiculoMonitorado() {
		return idCadVeiculoMonitorado;
	}

	public void setIdCadVeiculoMonitorado(UUID idCadVeiculoMonitorado) {
		this.idCadVeiculoMonitorado = idCadVeiculoMonitorado;
	}

	public Integer getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
}

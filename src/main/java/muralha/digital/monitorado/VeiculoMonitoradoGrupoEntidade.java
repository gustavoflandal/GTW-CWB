package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "VeiculoMonitoradoGrupo")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoGrupoEntidade {

    private UUID id;
    private UUID idCadVeiculoMonitorado;
    private Integer idGrupo;
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

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
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

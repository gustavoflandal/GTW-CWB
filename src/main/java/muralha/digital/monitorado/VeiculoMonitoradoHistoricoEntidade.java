package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "VeiculoMonitoradoHistorico")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoHistoricoEntidade {

    private UUID id;
    private UUID idCadVeiculoMonitorado;
    private Integer idUsuarioResponsavel;
    private Date dataAcao;
    private String dadosJson;

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

    public Integer getIdUsuarioResponsavel() {
        return idUsuarioResponsavel;
    }

    public void setIdUsuarioResponsavel(Integer idUsuarioResponsavel) {
        this.idUsuarioResponsavel = idUsuarioResponsavel;
    }

    public Date getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(Date dataAcao) {
        this.dataAcao = dataAcao;
    }

    public String getDadosJson() {
        return dadosJson;
    }

    public void setDadosJson(String dadosJson) {
        this.dadosJson = dadosJson;
    }
}

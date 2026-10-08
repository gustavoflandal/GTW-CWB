package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class AlertaResponse {

    private String id;
    private String idVeiculoMonitorado;
    private String placa;
    private boolean supervisionado; // novo campo

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdVeiculoMonitorado() {
        return idVeiculoMonitorado;
    }

    public void setIdVeiculoMonitorado(String idVeiculoMonitorado) {
        this.idVeiculoMonitorado = idVeiculoMonitorado;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public boolean isSupervisionado() {
        return supervisionado;
    }

    public void setSupervisionado(boolean supervisionado) {
        this.supervisionado = supervisionado;
    }
}



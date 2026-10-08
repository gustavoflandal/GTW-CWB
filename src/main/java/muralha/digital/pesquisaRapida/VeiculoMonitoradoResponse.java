package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoResponse {

    private String id;
    private String placa;
    private boolean supervisionado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

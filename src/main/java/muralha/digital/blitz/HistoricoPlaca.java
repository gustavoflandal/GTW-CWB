package muralha.digital.blitz;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import muralha.digital.registroDeFato.RegistroFatoDTO;
import muralha.digital.alerta.Alerta;

@XmlRootElement(name = "HistoricoPlaca")
public class HistoricoPlaca {
    private List<RegistroFatoDTO> registrosDeFato;
    private List<Alerta> alertas;

    public HistoricoPlaca() {
        this.registrosDeFato = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }
    
    @XmlElementWrapper(name = "RegistrosDeFato")
    @XmlElement(name = "RegistroFato")
    public List<RegistroFatoDTO> getRegistrosDeFato() {
        return registrosDeFato;
    }
    
    public void setRegistrosDeFato(List<RegistroFatoDTO> registrosDeFato) {
        this.registrosDeFato = registrosDeFato;
    }
    
    @XmlElementWrapper(name = "Alertas")
    @XmlElement(name = "Alerta")
    public List<Alerta> getAlertas() {
        return alertas;
    }
    
    public void setAlertas(List<Alerta> alertas) {
        this.alertas = alertas;
    }
}
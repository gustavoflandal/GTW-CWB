package muralha.digital.blitz;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import muralha.digital.registroDeFato.RegistroFatoDTO;

@XmlRootElement(name = "HistoricoCPF")
public class HistoricoCPF {
    private List<RegistroFatoDTO> registrosDeFato;
    private List<AntecedenteCriminal> antecedentesCriminais;
    
    @XmlElementWrapper(name = "RegistrosDeFato")
    @XmlElement(name = "RegistroFato")
    public List<RegistroFatoDTO> getRegistrosDeFato() {
        return registrosDeFato;
    }
    
    public void setRegistrosDeFato(List<RegistroFatoDTO> registrosDeFato) {
        this.registrosDeFato = registrosDeFato;
    }
    
    @XmlElementWrapper(name = "AntecedentesCriminais")
    @XmlElement(name = "AntecedenteCriminal")
    public List<AntecedenteCriminal> getAntecedentesCriminais() {
        return antecedentesCriminais;
    }
    
    public void setAntecedentesCriminais(List<AntecedenteCriminal> antecedentesCriminais) {
        this.antecedentesCriminais = antecedentesCriminais;
    }
}
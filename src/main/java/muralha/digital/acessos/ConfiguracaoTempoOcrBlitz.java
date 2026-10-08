package muralha.digital.acessos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ConfiguracaoTempoOcrBlitz")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguracaoTempoOcrBlitz {
    
    @XmlElement(name = "tempo_ocr_blitz")
    private Double tempoOcrBlitz;

    public Double getTempoOcrBlitz() {
        return tempoOcrBlitz;
    }

    public void setTempoOcrBlitz(Double tempoOcrBlitz) {
        this.tempoOcrBlitz = tempoOcrBlitz;
    }
}
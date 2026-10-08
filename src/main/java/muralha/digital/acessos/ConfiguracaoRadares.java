package muralha.digital.acessos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ConfiguracaoRadares")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguracaoRadares {
    
    @XmlElement(name = "raio_radares_mapa")
    private Integer raioRadaresMapa;

    public Integer getRaioRadaresMapa() {
        return raioRadaresMapa;
    }

    public void setRaioRadaresMapa(Integer raioRadaresMapa) {
        this.raioRadaresMapa = raioRadaresMapa;
    }
}
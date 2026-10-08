package muralha.digital.blitz;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Locais")
public class Locais {
    private List<Local> locais;
    
    @XmlElementWrapper(name = "ListaLocais")
    @XmlElement(name = "Local")
    public List<Local> getLocais() { return locais; }
    public void setLocais(List<Local> locais) { this.locais = locais; }
}
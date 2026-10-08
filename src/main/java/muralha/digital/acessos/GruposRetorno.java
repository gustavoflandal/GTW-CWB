package muralha.digital.acessos;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "grupos")
public class GruposRetorno {

    private List<Grupo> grupos;

    public GruposRetorno() {}

    public GruposRetorno(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    @XmlElement(name = "grupo")
    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }
}

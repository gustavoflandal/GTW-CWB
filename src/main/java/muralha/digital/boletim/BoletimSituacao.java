package muralha.digital.boletim;

import java.util.UUID;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "BoletimSituacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimSituacao {

    private Integer id;
    private String descricao;

    public BoletimSituacao() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

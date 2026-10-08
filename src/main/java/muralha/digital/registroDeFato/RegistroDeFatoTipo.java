package muralha.digital.registroDeFato;

import java.util.UUID;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "RegistroDeFatoTipo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoTipo {

    private Integer id;
    private String descricao;

    public RegistroDeFatoTipo() {}

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

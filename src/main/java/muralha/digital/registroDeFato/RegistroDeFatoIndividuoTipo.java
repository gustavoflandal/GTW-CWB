package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "RegistroDeFatoIndividuoTipo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoIndividuoTipo {

    private Integer id;
    private String descricao;

    public RegistroDeFatoIndividuoTipo() {}

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

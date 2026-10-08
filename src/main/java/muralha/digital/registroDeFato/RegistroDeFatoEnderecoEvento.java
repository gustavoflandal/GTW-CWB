package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "RegistroDeFatoEnderecoEvento")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoEnderecoEvento {

    private Integer id;
    private String descricao;

    public RegistroDeFatoEnderecoEvento() {}

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

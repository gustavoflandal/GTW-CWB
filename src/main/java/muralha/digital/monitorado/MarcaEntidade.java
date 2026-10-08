package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Marca")
@XmlAccessorType(XmlAccessType.FIELD)
public class MarcaEntidade {

    private Integer id;
    private String descricao;

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
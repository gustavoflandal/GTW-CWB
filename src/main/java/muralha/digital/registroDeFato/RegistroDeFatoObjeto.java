package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "objeto")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoObjeto {

    private Integer id;

    @XmlElement(name = "idRegistroFato")
    private Long idRegistroFato;

    private String tipo;

    private String descricao;

    // Getters e setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(Long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public boolean equalsConteudo(RegistroDeFatoObjeto outro) {
        if (outro == null) return false;
        return equalsNullable(tipo, outro.tipo)
            && equalsNullable(descricao, outro.descricao);
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}

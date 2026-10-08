package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RegistroDeFatoNaturezaDelituosa")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoNaturezaDelituosa {
    
    @XmlElement(name = "id")
    private Integer id;
    
    @XmlElement(name = "natureza_delituosa_desc")
    private String descricao;
    
    public RegistroDeFatoNaturezaDelituosa() {
        super();
    }
    
    public RegistroDeFatoNaturezaDelituosa(Integer id, String descricao, Boolean ativo) {
        this.id = id;
        this.descricao = descricao;
    }
    
    // Getters e Setters
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
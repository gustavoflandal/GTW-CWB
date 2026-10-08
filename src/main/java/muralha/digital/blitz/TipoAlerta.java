package muralha.digital.blitz;

import javax.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TipoAlerta")
public class TipoAlerta {
    private String id;
    private String tipo;
    private String descricao;
    
    // getters e setters
    @XmlElement
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    @XmlElement
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    @XmlElement
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

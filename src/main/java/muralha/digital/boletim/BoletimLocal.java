package muralha.digital.boletim;
import javax.xml.bind.annotation.*;
@XmlRootElement(name = "BoletimLocal")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimLocal {
    private Integer id;
    private String rua;
    private Integer numero;
    private String bairro;
    private String complemento;
    private Integer id_cidade;
    @XmlTransient
    private Cidade cidade;
    
    // Campo calculado para o XML (combina rua + cidade)
    @XmlElement(name = "ruaCidade")
    public String getRuaCidade() {
        if (rua == null && cidade == null) return null;
        if (cidade == null) return rua;
        if (rua == null) return cidade.getNome();
        return String.format("%s - %s", rua.trim(), cidade.getNome().trim());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
    public void setIdCidade(Integer id_cidade) {
        this.id_cidade = id_cidade;
    }

    public Integer getIdCidade() {
        return id_cidade;
    }
    
    @XmlTransient
    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
}

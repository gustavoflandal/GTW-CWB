package muralha.digital.boletim;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "BoletimDocumento")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimDocumento {
    private Integer id;
    private Integer idBoletim;
    private String tipo;
    private String dirArquivo;
    private String detalhamento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdBoletim() {
        return idBoletim;
    }

    public void setIdBoletim(Integer idBoletim) {
        this.idBoletim = idBoletim;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDirArquivo() {
        return dirArquivo;
    }

    public void setDirArquivo(String dirArquivo) {
        this.dirArquivo = dirArquivo;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }
}

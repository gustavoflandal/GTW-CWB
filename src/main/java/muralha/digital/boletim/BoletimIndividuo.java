package muralha.digital.boletim;
import javax.xml.bind.annotation.*;
@XmlRootElement(name = "BoletimIndividuo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimIndividuo {

    private Integer id;

    private Integer idBoletim;

    private Integer idTipoEnvolvimento;

    private String detalheEnvolvimento;

    private String nome;

    private String cpf;

    private BoletimIndividuoTipo tipoEnvolvimento;

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

    public Integer getIdTipoEnvolvimento() {
        return idTipoEnvolvimento;
    }

    public void setIdTipoEnvolvimento(Integer idTipoEnvolvimento) {
        this.idTipoEnvolvimento = idTipoEnvolvimento;
    }

    public String getDetalheEnvolvimento() {
        return detalheEnvolvimento;
    }

    public void setDetalheEnvolvimento(String detalheEnvolvimento) {
        this.detalheEnvolvimento = detalheEnvolvimento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public BoletimIndividuoTipo getTipoEnvolvimento() {
        return tipoEnvolvimento;
    }

    public void setTipoEnvolvimento(BoletimIndividuoTipo tipoEnvolvimento) {
        this.tipoEnvolvimento = tipoEnvolvimento;
    }
}

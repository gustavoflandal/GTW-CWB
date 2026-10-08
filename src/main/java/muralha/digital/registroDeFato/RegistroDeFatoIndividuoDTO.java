package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "individuo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoIndividuoDTO {

    private Integer id;

    @XmlElement(name = "idRegistroFato")
    private Long idRegistroFato;

    private Integer idTipoEnvolvimento;
 
    private String descricaoTipoEnvolvimento;
 
    private String detalheEnvolvimento;
  

    private String nome;

    private String cpf;

    private Integer ddd;

    private String telefone;

    private String email;

    private String status;
 
    public RegistroDeFatoIndividuoDTO() {}

    // Getters e Setters

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

    public Integer getIdTipoEnvolvimento() {
        return idTipoEnvolvimento;
    }

    public void setIdTipoEnvolvimento(Integer idTipoEnvolvimento) {
        this.idTipoEnvolvimento = idTipoEnvolvimento;
    }
    
    public String getDescricaoTipoEnvolvimento() {
        return descricaoTipoEnvolvimento;
    }

    public void setDescricaoTipoEnvolvimento(String descricaoTipoEnvolvimento) {
        this.descricaoTipoEnvolvimento = descricaoTipoEnvolvimento;
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

    public Integer getDdd() {
        return ddd;
    }

    public void setDdd(Integer ddd) {
        this.ddd = ddd;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

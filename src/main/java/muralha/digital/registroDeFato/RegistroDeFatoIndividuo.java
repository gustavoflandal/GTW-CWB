package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "individuo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoIndividuo {

    private Integer id;

    @XmlElement(name = "idRegistroFato")
    private Long idRegistroFato;

    private Integer idTipoEnvolvimento;

    private String detalheEnvolvimento;

    private String nome;

    private String cpf;

    private Integer ddd;

    private String telefone;

    private String email;

    private RegistroDeFatoIndividuoTipo tipoEnvolvimento;

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

    public RegistroDeFatoIndividuoTipo getTipoEnvolvimento() {
        return tipoEnvolvimento;
    }

    public void setTipoEnvolvimento(RegistroDeFatoIndividuoTipo tipoEnvolvimento) {
        this.tipoEnvolvimento = tipoEnvolvimento;
    }
    
    public boolean equalsConteudo(RegistroDeFatoIndividuo outro) {
        if (outro == null) return false;
        return equalsNullable(nome, outro.nome)
            && equalsNullable(cpf, outro.cpf)
            && equalsNullable(idTipoEnvolvimento, outro.idTipoEnvolvimento)
            && equalsNullable(detalheEnvolvimento, outro.detalheEnvolvimento)
            && equalsNullable(ddd, outro.ddd)
            && equalsNullable(telefone, outro.telefone)
            && equalsNullable(email, outro.email)
            && tipoEnvolvimentoEquals(tipoEnvolvimento, outro.tipoEnvolvimento);
    }
    
    private boolean tipoEnvolvimentoEquals(RegistroDeFatoIndividuoTipo a, RegistroDeFatoIndividuoTipo b) {
        if (a == null) return b == null;
        if (b == null) return false;
        return equalsNullable(a.getId(), b.getId())
            && equalsNullable(a.getDescricao(), b.getDescricao());
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}

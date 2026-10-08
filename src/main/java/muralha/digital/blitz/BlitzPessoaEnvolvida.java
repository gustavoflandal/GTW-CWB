package muralha.digital.blitz;

import java.sql.Timestamp;
import java.util.Date;

public class BlitzPessoaEnvolvida {
    private Long id;
    private Long id_abordagem;
    private String cpf;
    private String nome_completo;
    private Date data_nascimento;
    private String tipo_envolvimento;
    private String telefone;
    private Character sexo;
    private String email;
    private String observacoes;
    private Timestamp data_criacao;
    
    public BlitzPessoaEnvolvida() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId_abordagem() {
        return id_abordagem;
    }
    
    public void setId_abordagem(Long id_abordagem) {
        this.id_abordagem = id_abordagem;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getNome_completo() {
        return nome_completo;
    }
    
    public void setNome_completo(String nome_completo) {
        this.nome_completo = nome_completo;
    }
    
    public Date getData_nascimento() {
        return data_nascimento;
    }
    
    public void setData_nascimento(Date data_nascimento) {
        this.data_nascimento = data_nascimento;
    }
    
    public String getTipo_envolvimento() {
        return tipo_envolvimento;
    }
    
    public void setTipo_envolvimento(String tipo_envolvimento) {
        this.tipo_envolvimento = tipo_envolvimento;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public Character getSexo() {
        return sexo;
    }
    
    public void setSexo(Character sexo) {
        this.sexo = sexo;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public Timestamp getData_criacao() {
        return data_criacao;
    }
    
    public void setData_criacao(Timestamp data_criacao) {
        this.data_criacao = data_criacao;
    }
}
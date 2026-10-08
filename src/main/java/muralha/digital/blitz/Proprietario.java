package muralha.digital.blitz;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Proprietario")
public class Proprietario {
    private int id;
    private String nome;
    private String sobrenome;
    private String cpf;
    private Date data_nascimento;
    private String endereco;
    private String telefone;
    private String email;
    
    @XmlElement
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    @XmlElement
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    @XmlElement
    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    
    @XmlElement
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    @XmlElement
    public Date getData_nascimento() { return data_nascimento; }
    public void setData_nascimento(Date data_nascimento) { this.data_nascimento = data_nascimento; }
    
    @XmlElement
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    @XmlElement
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    @XmlElement
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
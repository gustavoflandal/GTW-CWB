package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="envolvidos")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvolvidoDTO {
	
	private int id;
    private String nome;
    private String cpf;
    private int tipoEnvolvimento;
    private String email;
    private String ddd;
    private String telefone;
    private String detalhamento;
    private String descricao;
           
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public int getTipoEnvolvimento() {
		return tipoEnvolvimento;
	}
	
	public void setTipoEnvolvimento(int tipoEnvolvimento) {
		this.tipoEnvolvimento = tipoEnvolvimento;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getDdd() {
		return ddd;
	}
	
	public void setDdd(String ddd) {
		this.ddd = ddd;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getDetalhamento() {
		return detalhamento;
	}
	
	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	    	    		
}

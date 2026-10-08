package muralha.digital.areamonitorada;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

@XmlRootElement(name = "AreaMonitorada")
@XmlAccessorType (XmlAccessType.FIELD)
public class AreaMonitorada {
	
	@XmlTransient
	private static final Logger logger = Logger.getLogger(AreaMonitorada.class);
	
	public int id;
	public String nome;
	public Date data_cadastro;
	public Date data_atualizacao;
	public String dados_json;
	public Boolean deletado;
	
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
	
	public Date getData_cadastro() {
		return data_cadastro;
	}
	
	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}
	
	public Date getData_atualizacao() {
		return data_atualizacao;
	}
	
	public void setData_atualizacao(Date data_atualizacao) {
		this.data_atualizacao = data_atualizacao;
	}
	
	public String getDados_json() {
		return dados_json;
	}
	
	public void setDados_json(String dados_json) {
		this.dados_json = dados_json;
	}
	
	public Boolean getDeletado() {
		return deletado;
	}
	
	public void setDeletado(Boolean deletado) {
		this.deletado = deletado;
	}		
}

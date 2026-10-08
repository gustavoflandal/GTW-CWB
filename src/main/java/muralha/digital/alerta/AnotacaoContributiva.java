package muralha.digital.alerta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class AnotacaoContributiva 
{
	private UUID 			id;
	private UUID			idAlerta;
	private String			descricao;
	private	Date			dataCadastro;
	private Integer			idUsuario;
	private String			usuario;
	private String			nomeUsuario;
	
	public String 			dataCadastroFormatada = "";
	public String 			horaCadastroFormatada = "";
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getIdAlerta() {
		return idAlerta;
	}
	public void setIdAlerta(UUID idAlerta) {
		this.idAlerta = idAlerta;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	
	
	public String getDataCadastroFormatada() { return new SimpleDateFormat("dd/MM/yyyy").format(dataCadastro); }
	public void setDataCadastroFormatada(String dataCadastroFormatada) { this.dataCadastroFormatada = dataCadastroFormatada; }
	
	public String getHoraCadastroFormatada() { return new SimpleDateFormat("HH:mm:ss").format(dataCadastro); }
	public void setHoraCadastroFormatada(String horaCadastroFormatada) { this.horaCadastroFormatada = horaCadastroFormatada; }

}

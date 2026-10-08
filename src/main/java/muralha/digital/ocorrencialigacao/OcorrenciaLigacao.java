package muralha.digital.ocorrencialigacao;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OcorrenciaLigacao")
@XmlAccessorType (XmlAccessType.FIELD)
public class OcorrenciaLigacao {
	
	private int id;
	
	private Date dataHoraEvento;
	
	private int idTipoSolicitante;
	
	private String nomeSolicitante;
	
	private String cpfSolicitante;
	
	private int idTipoOcorrencia;
	
	private int idCidade;
	
	private String bairro;
	
	private String rua;
	
	private int numero;
	
	private String complemento;
	
	private String nomeVitima;
	
	private String detalhamento;
	
	private int existeArmaEnvolvida;
	
	public Date data; 
	
	private int idUsuario;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}
	
	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}
	
	public int getIdTipoSolicitante() {
		return idTipoSolicitante;
	}
	
	public void setIdTipoSolicitante(int idTipoSolicitante) {
		this.idTipoSolicitante = idTipoSolicitante;
	}
	
	public String getNomeSolicitante() {
		return nomeSolicitante;
	}
	
	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}
	
	public String getCpfSolicitante() {
		return cpfSolicitante;
	}
	
	public void setCpfSolicitante(String cpfSolicitante) {
		this.cpfSolicitante = cpfSolicitante;
	}
	
	public int getIdTipoOcorrencia() {
		return idTipoOcorrencia;
	}
	
	public void setIdTipoOcorrencia(int idTipoOcorrencia) {
		this.idTipoOcorrencia = idTipoOcorrencia;
	}
	
	public int getIdCidade() {
		return idCidade;
	}
	
	public void setIdCidade(int idCidade) {
		this.idCidade = idCidade;
	}
	
	public String getBairro() {
		return bairro;
	}
	
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	
	public String getRua() {
		return rua;
	}
	public void setRua(String rua) {
		this.rua = rua;
	}
	
	public int getNumero() {
		return numero;
	}
	
	public void setNumero(int numero) {
		this.numero = numero;
	}
	
	public String getComplemento() {
		return complemento;
	}
	
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	public String getNomeVitima() {
		return nomeVitima;
	}
	
	public void setNomeVitima(String nomeVitima) {
		this.nomeVitima = nomeVitima;
	}
	
	public String getDetalhamento() {
		return detalhamento;
	}
	
	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}
	
	public int getExisteArmaEnvolvida() {
		return existeArmaEnvolvida;
	}
	
	public void setExisteArmaEnvolvida(int existeArmaEnvolvida) {
		this.existeArmaEnvolvida = existeArmaEnvolvida;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}	
}

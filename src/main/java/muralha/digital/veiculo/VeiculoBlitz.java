package muralha.digital.veiculo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import muralha.digital.veiculo.imagem.VeiculoImagem;


@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class VeiculoBlitz 
{
	private UUID 			id;	
	private String			placa;
	private Date			dataVeic;
	private String			dataVeicFormatada;
	private int				idLocal;
	private Integer			serieEquipamento;
	private String			descLocal;
	private int				idPista;
	private int				faixa;
	private double			latitude;
	private double			longitude;
	private Integer			velocidade;
	private String			classificacao;
	private boolean			enviadoCliente;
	private boolean			comImagem;
	private boolean			possuiCoordenadas;
	private String			marca;
	private String 			modelo;
	
	private String			tiposAlertas;
	
	@XmlElementWrapper		(name="listaImagens")
	@XmlElement				(name="imagem")
	private List<VeiculoImagem> listaImagens;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public int getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}
	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}
	public void setSerieEquipamento(Integer serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}
	public String getDescLocal() {
		return descLocal;
	}
	public void setDescLocal(String descLocal) {
		this.descLocal = descLocal;
	}
	public int getIdPista() {
		return idPista;
	}
	public void setIdPista(int idPista) {
		this.idPista = idPista;
	}
	public int getFaixa() {
		return faixa;
	}
	public void setFaixa(int faixa) {
		this.faixa = faixa;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Integer getVelocidade() {
		return velocidade;
	}
	public void setVelocidade(Integer velocidade) {
		this.velocidade = velocidade;
	}
	public String getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}
	
	//Setando a data do veiculo
	//Formatando a data para usar como String
	public Date getDataVeic() {
		return dataVeic;
	}
	public void setDataVeic(Date dataVeic) {
		this.dataVeic = dataVeic;
		setDataVeicFormatada();
	}	
	public String getDataVeicFormatada() {
		return dataVeicFormatada;
	}
	public void setDataVeicFormatada() {
		 this.dataVeicFormatada = 
				 				new SimpleDateFormat("dd/MM/yyyy").format(dataVeic) +
				 				" " +
				 				new SimpleDateFormat("HH:mm:ss").format(dataVeic);
	}
	public boolean isEnviadoCliente() {
		return enviadoCliente;
	}
	public void setEnviadoCliente(boolean enviadoCliente) {
		this.enviadoCliente = enviadoCliente;
	}
	public boolean isComImagem() {
		return comImagem;
	}
	public void setComImagem(boolean comImagem) {
		this.comImagem = comImagem;
	}
	public boolean possuiCoordenadas() {
		return possuiCoordenadas;
	}
	public void setPossuiCoordenadas(boolean possuiCoordenadas) {
		this.possuiCoordenadas = possuiCoordenadas;
	}
	public List<VeiculoImagem> getListaImagens() {
		return listaImagens;
	}
	public void setListaImagens(List<VeiculoImagem> listaImagens) {
		this.listaImagens = listaImagens;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getTiposAlertas() {
		return tiposAlertas;
	}
	public void setTiposAlertas(String tiposAlertas) {
		this.tiposAlertas = tiposAlertas;
	}
}

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
public class Veiculo 
{
	private UUID 			id;	
	private String			placa;
	private Date			dataVeic;
	private String			dataVeicFormatada;
	private int				idLocal;
	private Integer			serieEquipamento;
	private String			codigoEquipamento;
	private String			descLocal;
	private int				idPista;
	private int				faixa;
	private double			latitude;
	private double			longitude;
	private Integer			velocidade;
	private String			classificacao;
	private String			tipoVeiculo;
	private boolean			enviadoCliente;
	private boolean			comImagem;
	private boolean			possuiCoordenadas;
	private String			marca;
	private String 			modelo;
	private Date			dataImportado;
	private String			dataImportadoFormatada;
	private UUID 			idVeiculo;
	private boolean			possuiAlerta;
	private UUID 			idVeiculoAnterior;
	private UUID 			idVeiculoProximo;
	private Integer         rodagemDupla;
	private Integer         numeroEixos;
	private Integer         categoria;
	private boolean 		placaMercosul;
	private String 			corPlaca;
	
	private String 			perfil_1;
	private String			perfil_2;
	private String			placa_frontal;
	private String			info_adicional;
	
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
	public String getCodigoEquipamento() {
		return codigoEquipamento;
	}
	public void setCodigoEquipamento(String codigoEquipamento) {
		this.codigoEquipamento = codigoEquipamento;
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
	
	public String getPerfil_1() {
		return perfil_1;
	}
	public void setPerfil_1(String perfil_1) {
		this.perfil_1 = perfil_1;
	}
	public String getPerfil_2() {
		return perfil_2;
	}
	public void setPerfil_2(String perfil_2) {
		this.perfil_2 = perfil_2;
	}
	public String getPlaca_frontal() {
		return placa_frontal;
	}
	public void setPlaca_frontal(String placa_frontal) {
		this.placa_frontal = placa_frontal;
	}
	
	public String getInfo_adicional() {
		return info_adicional;
	}
	public void setInfo_adicional(String info_adicional) {
		this.info_adicional = info_adicional;
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
	public Date getDataImportado() {
		return dataImportado;
	}
	public void setDataImportado(Date dataImportado) {
		this.dataImportado = dataImportado;
		setDataImportadoFormatada();
	}
	public String getDataImportadoFormatada() {
		return dataImportadoFormatada;
	}
	public void setDataImportadoFormatada() {
		this.dataImportadoFormatada = (this.dataImportado != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.dataImportado) : null);
	}
	public UUID getIdVeiculo() {
		return idVeiculo;
	}
	public void setIdVeiculo(UUID idVeiculo) {
		this.idVeiculo = idVeiculo;
	}
	public boolean possuiAlerta() {
		return possuiAlerta;
	}
	public void setPossuiAlerta(boolean possuiAlerta) {
		this.possuiAlerta = possuiAlerta;
	}
	public UUID getIdVeiculoAnterior() {
		return idVeiculoAnterior;
	}
	public void setIdVeiculoAnterior(UUID idVeiculoAnterior) {
		this.idVeiculoAnterior = idVeiculoAnterior;
	}
	public UUID getIdVeiculoProximo() {
		return idVeiculoProximo;
	}
	public void setIdVeiculoProximo(UUID idVeiculoProximo) {
		this.idVeiculoProximo = idVeiculoProximo;
	}
	public Integer getRodagemDupla() {
		return rodagemDupla;
	}
	public void setRodagemDupla(Integer rodagemDupla) {
		this.rodagemDupla = rodagemDupla;
	}
	public Integer getNumeroEixos() {
		return numeroEixos;
	}
	public void setNumeroEixos(Integer numeroEixos) {
		this.numeroEixos = numeroEixos;
	}
	public Integer getCategoria() {
		return categoria;
	}
	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}
	public String getTipoVeiculo() {
		return tipoVeiculo;
	}
	public void setTipoVeiculo(String tipoVeiculo) {
		this.tipoVeiculo = tipoVeiculo;
	}
	public boolean isPlacaMercosul() {
    	return placaMercosul;
	}
	public void setPlacaMercosul(boolean placaMercosul) {
		this.placaMercosul = placaMercosul;
	}
	public String getCorPlaca() {
		return corPlaca;
	}
	public void setCorPlaca(String corPlaca) {
		this.corPlaca = corPlaca;
	}
}

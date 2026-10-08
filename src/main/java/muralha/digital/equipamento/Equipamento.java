package muralha.digital.equipamento;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "Equipamento")
@XmlAccessorType (XmlAccessType.FIELD)
public class Equipamento
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(Equipamento.class);
    
    private Integer idLocal;
	private Integer sequenciaLocal;
	private Integer idConfiguracaEquipamento;
	private Integer serieEquipamento;
	private String nome;
	private Boolean emOperacao;
	private Double latitude;
	private Double longitude;
	private Date dataInicioOperacao;
	private String codigoEquipamento;
	
	private Integer idMunicipio;
	private String municipio;
	private String ufMunicipio;
	
	private Integer idRegiao;
	private String regiao;
	
	public Equipamento() {}

	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public Integer getSequenciaLocal() {
		return sequenciaLocal;
	}
	public void setSequenciaLocal(Integer sequenciaLocal) {
		this.sequenciaLocal = sequenciaLocal;
	}

	public Integer getIdConfiguracaEquipamento() {
		return idConfiguracaEquipamento;
	}
	public void setIdConfiguracaEquipamento(Integer idConfiguracaEquipamento) {
		this.idConfiguracaEquipamento = idConfiguracaEquipamento;
	}

	public Integer getSerieEquipamento() {
		return serieEquipamento;
	}
	public void setSerieEquipamento(Integer serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getEmOperacao() {
		return emOperacao;
	}
	public void setEmOperacao(Boolean emOperacao) {
		this.emOperacao = emOperacao;
	}

	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Date getDataInicioOperacao() {
		return dataInicioOperacao;
	}
	public void setDataInicioOperacao(Date dataInicioOperacao) {
		this.dataInicioOperacao = dataInicioOperacao;
	}
	
	public String getCodigoEquipamento() {
		return codigoEquipamento;
	}
	public void setCodigoEquipamento(String codigoEquipamento) {
		this.codigoEquipamento = codigoEquipamento;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}
	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getUfMunicipio() {
		return ufMunicipio;
	}
	public void setUfMunicipio(String ufMunicipio) {
		this.ufMunicipio = ufMunicipio;
	}

	public Integer getIdRegiao() {
		return idRegiao;
	}
	public void setIdRegiao(Integer idRegiao) {
		this.idRegiao = idRegiao;
	}

	public String getRegiao() {
		return regiao;
	}
	public void setRegiao(String regiao) {
		this.regiao = regiao;
	}
}

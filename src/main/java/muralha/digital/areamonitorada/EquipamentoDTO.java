package muralha.digital.areamonitorada;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import muralha.digital.areamonitorada.EquipamentoDTO;

@XmlRootElement(name = "Equipamentos")
@XmlAccessorType (XmlAccessType.FIELD)
public class EquipamentoDTO {
	
	@XmlTransient
	private static final Logger logger = Logger.getLogger(EquipamentoDTO.class);
	
	public int id_local;
	public int sequencia_local;
	public String nome;
	public int id_localidade;
	public BigDecimal posicao_lat;
	public BigDecimal posicao_lon;	
	
	public EquipamentoDTO() {}

	public int getId_local() {
		return id_local;
	}

	public void setId_local(int id_local) {
		this.id_local = id_local;
	}

	public int getSequencia_local() {
		return sequencia_local;
	}

	public void setSequencia_local(int sequencia_local) {
		this.sequencia_local = sequencia_local;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getId_localidade() {
		return id_localidade;
	}

	public void setId_localidade(int id_localidade) {
		this.id_localidade = id_localidade;
	}

	public BigDecimal getPosicao_lat() {
		return posicao_lat;
	}

	public void setPosicao_lat(BigDecimal posicao_lat) {
		this.posicao_lat = posicao_lat;
	}

	public BigDecimal getPosicao_lon() {
		return posicao_lon;
	}

	public void setPosicao_lon(BigDecimal posical_lon) {
		this.posicao_lon = posical_lon;
	}	
}

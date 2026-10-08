package muralha.digital.registroDeFato;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="veiculos")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoDTO {
	
	private Integer id;
    private String placa;
    private String cor;
    private String marca;
    private String modelo;
    private Boolean cadastrarMonitorado;
    private UUID idTipoAlertaOcorrencia; 
    private String descricao;
    private String dataInicio;
    private Boolean privado;  
    private Boolean monitorado;  
           
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlaca() {
		return placa;
	}
	
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	
	public String getCor() {
		return cor;
	}
	
	public void setCor(String cor) {
		this.cor = cor;
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

	public Boolean getCadastrarMonitorado() {
		return cadastrarMonitorado;
	}

	public void setCadastrarMonitorado(Boolean cadastrarMonitorado) {
		this.cadastrarMonitorado = cadastrarMonitorado;
	}

	public UUID getIdTipoAlertaOcorrencia() {
		return idTipoAlertaOcorrencia;
	}

	public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) {
		this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Boolean getPrivado() {
		return privado;
	}

	public void setPrivado(Boolean privado) {
		this.privado = privado;
	}

	public Boolean getMonitorado() {
		return monitorado;
	}

	public void setMonitorado(Boolean monitorado) {
		this.monitorado = monitorado;
	}	
}

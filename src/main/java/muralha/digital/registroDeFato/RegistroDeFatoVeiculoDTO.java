package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="veiculos")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoVeiculoDTO {
	
	private Long id;
	
    // Campos básicos do veículo
    private String placa;
    private String cor;
    private String marca;
    private String modelo;
    
    // Campos do monitoramento
    private boolean monitorado;
    private String IdTipoAlerta;
    private String descricaoTipoAlerta;
    private String dataFim;    
    private String dataInicio; 
    private String descricao;
    private String nome;   
    
    private String status;

    // Getters e Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public boolean isMonitorado() {
        return monitorado;
    }

    public void setMonitorado(boolean monitorado) {
        this.monitorado = monitorado;
    }

    // Getter e Setter ajustados para a nova variável IdTipoAlerta
    public String getIdTipoAlerta() {
        return IdTipoAlerta;
    }

    public void setIdTipoAlerta(String IdTipoAlerta) {
        this.IdTipoAlerta = IdTipoAlerta;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }
    
    public String getDataInicio() {
        return dataFim;
    }

    public void setDataInicio(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricaoTipoAlerta() {
        return descricaoTipoAlerta;
    }

    public void setDescricaoTipoAlerta(String descricaoTipoAlerta) {
        this.descricaoTipoAlerta = descricaoTipoAlerta;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
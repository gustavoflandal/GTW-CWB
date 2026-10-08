package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "PesquisaRapidaResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PesquisaRapidaResponse {

    private String nome;
    private String cpf;
    private Integer totalRegistros;

    @XmlElementWrapper(name = "registrosFato")
    @XmlElement(name = "registroFato")
    private List<RegistroFatoResponse> registrosFato;

    @XmlElementWrapper(name = "alertas")
    @XmlElement(name = "alerta")
    private List<AlertaResponse> alertas;

    @XmlElementWrapper(name = "veiculosMonitorados")
    @XmlElement(name = "veiculo")
    private List<VeiculoMonitoradoResponse> veiculosMonitorados;

    // Getters e Setters

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

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public List<RegistroFatoResponse> getRegistrosFato() {
        return registrosFato;
    }

    public void setRegistrosFato(List<RegistroFatoResponse> registrosFato) {
        this.registrosFato = registrosFato;
    }

    public List<AlertaResponse> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<AlertaResponse> alertas) {
        this.alertas = alertas;
    }

    public List<VeiculoMonitoradoResponse> getVeiculosMonitorados() {
        return veiculosMonitorados;
    }

    public void setVeiculosMonitorados(List<VeiculoMonitoradoResponse> veiculosMonitorados) {
        this.veiculosMonitorados = veiculosMonitorados;
    }
}

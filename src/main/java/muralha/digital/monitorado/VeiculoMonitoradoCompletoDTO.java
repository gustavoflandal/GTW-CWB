package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@XmlRootElement(name = "VeiculoMonitoradoCompleto")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoCompletoDTO {

	@JsonProperty("veiculoMonitorado")
    private VeiculoMonitoradoEntidade veiculo;
	@JsonProperty("gruposMonitorados")
    private List<VeiculoMonitoradoGrupoEntidade> grupos;
	 @JsonProperty("equipamentosMonitorados")
    private List<VeiculoMonitoradoEquipamentoEntidade> equipamentos;
	 @JsonProperty("periodosMonitorados")
    private List<VeiculoMonitoradoPeriodoEntidade> periodos;

    public VeiculoMonitoradoEntidade getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoMonitoradoEntidade veiculo) {
        this.veiculo = veiculo;
    }

    public List<VeiculoMonitoradoGrupoEntidade> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<VeiculoMonitoradoGrupoEntidade> grupos) {
        this.grupos = grupos;
    }

    public List<VeiculoMonitoradoEquipamentoEntidade> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(List<VeiculoMonitoradoEquipamentoEntidade> equipamentos) {
        this.equipamentos = equipamentos;
    }

    public List<VeiculoMonitoradoPeriodoEntidade> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<VeiculoMonitoradoPeriodoEntidade> periodos) {
        this.periodos = periodos;
    }
}

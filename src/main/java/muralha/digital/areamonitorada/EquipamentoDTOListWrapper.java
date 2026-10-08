package muralha.digital.areamonitorada;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "equipamentos")
public class EquipamentoDTOListWrapper {

    private List<EquipamentoDTO> equipamentos;

    public EquipamentoDTOListWrapper() {
    }

    public EquipamentoDTOListWrapper(List<EquipamentoDTO> equipamentos) {
        this.equipamentos = equipamentos;
    }

    @XmlElement(name = "equipamento")
    public List<EquipamentoDTO> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(List<EquipamentoDTO> equipamentos) {
        this.equipamentos = equipamentos;
    }
}

package muralha.digital.painelInformacoes;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "TotalInformacaoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TotalInformacaoResponse {

    private List<IndicadorDTO> indicadores;
    private boolean sucesso;

    public List<IndicadorDTO> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(List<IndicadorDTO> indicadores) {
        this.indicadores = indicadores;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }
}

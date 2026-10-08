package muralha.digital.painelInformacoes;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "RecursoServidorResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecursoServidorResponse {

    private List<RecursoServidorDTO> indicadores;
    private boolean sucesso;

    public List<RecursoServidorDTO> getIndicadores() { return indicadores; }
    public void setIndicadores(List<RecursoServidorDTO> indicadores) { this.indicadores = indicadores; }

    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
}

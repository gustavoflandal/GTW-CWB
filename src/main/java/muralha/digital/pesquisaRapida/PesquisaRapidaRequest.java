package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "PesquisaRapidaRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class PesquisaRapidaRequest {
    private String valor;
    private TipoConsulta tipoConsulta; // enum diretamente

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }
}

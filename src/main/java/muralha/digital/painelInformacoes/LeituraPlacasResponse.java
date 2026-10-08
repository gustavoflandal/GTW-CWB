package muralha.digital.painelInformacoes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Leituras")
@XmlAccessorType(XmlAccessType.FIELD)
public class LeituraPlacasResponse {

    @XmlElementWrapper(name = "LeiturasPlaca")
    @XmlElement(name = "Leitura")
    private List<LeituraPlaca> leituras = new ArrayList<>();

    @XmlTransient
    private boolean sucesso;

    public List<LeituraPlaca> getLeituras() {
        return leituras;
    }

    public void setLeituras(List<LeituraPlaca> leituras) {
        this.leituras = leituras;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class LeituraPlaca {
    private int idLocal;
    private int total;        // total de leituras
    private double percentual; // percentual calculado
    // getters e setters
    public int getIdLocal() { return idLocal; }
    public void setIdLocal(int idLocal) { this.idLocal = idLocal; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public double getPercentual() { return percentual; }
    public void setPercentual(double percentual) { this.percentual = percentual; }
}

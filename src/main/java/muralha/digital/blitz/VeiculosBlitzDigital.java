package muralha.digital.blitz;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Veiculos")
public class VeiculosBlitzDigital {
    private List<VeiculoBlitzDigital> listaVeiculos;
    
    @XmlElementWrapper(name = "ListaVeiculos")
    @XmlElement(name = "Veiculo")
    public List<VeiculoBlitzDigital> getListaVeiculos() {
        return listaVeiculos;
    }
    
    public void setListaVeiculos(List<VeiculoBlitzDigital> listaVeiculos) {
        this.listaVeiculos = listaVeiculos;
    }
}
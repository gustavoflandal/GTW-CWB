package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimResponse {

    private Integer id;

    @XmlElementWrapper(name = "veiculos")
    @XmlElement(name = "veiculo")
    private List<Veiculo> veiculos;

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Veiculo {
        private Integer id;
        private String placa;

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
    }
}

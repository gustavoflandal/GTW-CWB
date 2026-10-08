package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroFatoResponse {

    private Long id;
    private Boolean temBoletim;
    private Integer totalDeAbordagens;
    
    @XmlElementWrapper(name = "veiculos")
    @XmlElement(name = "veiculo")
    private List<Veiculo> veiculos;

    @XmlElementWrapper(name = "individuos")
    @XmlElement(name = "individuo")
    private List<Individuo> individuos;

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getTemBoletim() {
        return temBoletim;
    }
    public void setTemBoletim(Boolean temBoletim) {
        this.temBoletim = temBoletim;
    }
    
    public Integer getTotalDeAbordagens() {
        return totalDeAbordagens;
    }
    public void setTotalDeAbordagens(Integer totalDeAbordagens) {
        this.totalDeAbordagens = totalDeAbordagens;
    }

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }
    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public List<Individuo> getIndividuos() {
        return individuos;
    }
    public void setIndividuos(List<Individuo> individuos) {
        this.individuos = individuos;
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

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Individuo {
        private String nome;
        private String cpf;

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
    }
}

package muralha.digital.boletim;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "BoletimVeiculo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimVeiculo {
    private Integer id;
    private Integer idBoletim;
    private String placa;
    private String cor;
    private String marca;
    private String modelo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdBoletim() {
        return idBoletim;
    }

    public void setIdBoletim(Integer idBoletim) {
        this.idBoletim = idBoletim;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}

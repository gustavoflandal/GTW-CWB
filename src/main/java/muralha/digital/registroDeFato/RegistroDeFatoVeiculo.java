package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "RegistroDeFatoVeiculo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoVeiculo {

    private Integer id;

    private Long idRegistroFato;

    private String placa;

    private String cor;

    private String marca;

    private String modelo;

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(Long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
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
    
    public boolean equalsConteudo(RegistroDeFatoVeiculo outro) {
        if (outro == null) return false;
        return equalsNullable(placa, outro.placa)
            && equalsNullable(marca, outro.marca)
            && equalsNullable(cor, outro.cor)
            && equalsNullable(modelo, outro.modelo);
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}

package muralha.digital.boletim;

import javax.xml.bind.annotation.*;
public class Cidade {
    private Integer id;
    private Integer idEstado;
    private String nome;

    // Construtor vazio
    public Cidade() {
    }

    // Construtor com parâmetros
    public Cidade(Integer id, Integer idEstado, String nome) {
        this.id = id;
        this.idEstado = idEstado;
        this.nome = nome;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Método toString para facilitar a visualização
    @Override
    public String toString() {
        return "Cidade{" +
                "id=" + id +
                ", idEstado=" + idEstado +
                ", nome='" + nome + '\'' +
                '}';
    }
}
package muralha.digital.painelInformacoes;

public class IndicadorDTO {
    private String titulo;
    private Integer valor;
    private String modulo;

    // construtores
    public IndicadorDTO() {}
    public IndicadorDTO(String titulo, Integer valor, String modulo) {
        this.titulo = titulo;
        this.valor = valor;
        this.modulo = modulo;
    }

    // getters e setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
}

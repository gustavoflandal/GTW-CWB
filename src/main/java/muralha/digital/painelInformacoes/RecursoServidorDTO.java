package muralha.digital.painelInformacoes;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class RecursoServidorDTO {

    private String nome;
    private Long valor;
    private String unidade; // ex: "registros", "dias", "GB"
    private Double percentual; // opcional, null se não houver percentual

    public RecursoServidorDTO() {}

    public RecursoServidorDTO(String nome, Long valor, String unidade, Double percentual) {
        this.nome = nome;
        this.valor = valor;
        this.unidade = unidade;
        this.percentual = percentual;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getValor() { return valor; }
    public void setValor(Long valor) { this.valor = valor; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public Double getPercentual() { return percentual; }
    public void setPercentual(Double percentual) { this.percentual = percentual; }
}

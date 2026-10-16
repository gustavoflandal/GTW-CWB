package muralha.digital.ocr;

/**
 * Uma placa lida numa passagem (TR 5.7.1.4: N placas por passagem). O score é o metadado
 * de confiabilidade do OCR (0-100), mantido de forma permanente (5.7.4.4); nulo = não informado.
 */
public final class LeituraPlaca {

    public enum Tipo { TRACAO, FRONTAL, REBOQUE, SEMIRREBOQUE }

    /** EMBARCADA = OCR do equipamento; CENTRAL = reprocessamento central; MANUAL = digitada na revisão. */
    public enum Origem { EMBARCADA, CENTRAL, MANUAL }

    private final int ordem;
    private final Tipo tipo;
    private final String placa;
    private final Double score;
    private final Origem origem;

    public LeituraPlaca(int ordem, Tipo tipo, String placa, Double score, Origem origem) {
        this.ordem = ordem;
        this.tipo = tipo;
        this.placa = placa == null || placa.trim().isEmpty() ? null : placa.trim().toUpperCase();
        this.score = score;
        this.origem = origem;
    }

    public int getOrdem() { return ordem; }

    public Tipo getTipo() { return tipo; }

    /** Placa em maiúsculas, ou nulo se não houve leitura. */
    public String getPlaca() { return placa; }

    public Double getScore() { return score; }

    public Origem getOrigem() { return origem; }

    public LeituraPlaca comScore(Double novoScore) {
        return new LeituraPlaca(ordem, tipo, placa, novoScore, origem);
    }
}

package muralha.digital.logtecnico;

/** Um parâmetro alterado: nome, valor anterior e valor novo (nulo = ausente). */
public final class AlteracaoParametro {

    private final String parametro;
    private final String valorAnterior;
    private final String valorNovo;

    public AlteracaoParametro(String parametro, String valorAnterior, String valorNovo) {
        this.parametro = parametro;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
    }

    public String getParametro() { return parametro; }

    public String getValorAnterior() { return valorAnterior; }

    public String getValorNovo() { return valorNovo; }

    @Override
    public String toString() { return parametro + ": " + valorAnterior + " -> " + valorNovo; }
}

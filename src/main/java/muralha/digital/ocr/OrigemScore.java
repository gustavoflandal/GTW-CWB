package muralha.digital.ocr;

import java.util.regex.Pattern;

/**
 * De onde vem o score de OCR de um equipamento (parâmetro OCR_SCORE_ORIGEM):
 * {@code TABELA_LEITURA} (gravado pela recepção em muralha.vtr_placa_leitura),
 * {@code DADO_BRUTO:<campo>} (JSON de ia.veiculo_caracteristica.dado_bruto) ou {@code AUSENTE}.
 */
public final class OrigemScore {

    public enum Tipo { TABELA_LEITURA, DADO_BRUTO, AUSENTE }

    private static final Pattern CAMPO = Pattern.compile("[A-Za-z0-9_]{1,40}(\\.[A-Za-z0-9_]{1,40}){0,4}");

    public static final OrigemScore AUSENTE = new OrigemScore(Tipo.AUSENTE, null);

    private final Tipo tipo;
    private final String campo;

    private OrigemScore(Tipo tipo, String campo) {
        this.tipo = tipo;
        this.campo = campo;
    }

    public static OrigemScore de(Tipo tipo, String campo) {
        if (tipo == null) throw new IllegalArgumentException("Origem do score não informada.");
        if (tipo == Tipo.DADO_BRUTO) {
            if (campo == null || campo.trim().length() > 100 || !CAMPO.matcher(campo.trim()).matches()) {
                throw new IllegalArgumentException(
                    "Campo do dado bruto inválido: use nomes com letras, números e _ separados por ponto (ex.: ocr.score).");
            }
            return new OrigemScore(tipo, campo.trim());
        }
        return tipo == Tipo.AUSENTE ? AUSENTE : new OrigemScore(tipo, null);
    }

    /** Lê "TABELA_LEITURA", "DADO_BRUTO:campo" ou "AUSENTE" (vazio = AUSENTE). */
    public static OrigemScore interpretar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return AUSENTE;
        String t = texto.trim();
        int sep = t.indexOf(':');
        return de(tipoDe(sep < 0 ? t : t.substring(0, sep)), sep < 0 ? null : t.substring(sep + 1));
    }

    public static Tipo tipoDe(String nome) {
        if (nome != null) {
            for (Tipo tp : Tipo.values()) {
                if (tp.name().equalsIgnoreCase(nome.trim())) return tp;
            }
        }
        throw new IllegalArgumentException("Origem do score inválida: use TABELA_LEITURA, DADO_BRUTO ou AUSENTE.");
    }

    public Tipo getTipo() { return tipo; }

    public String getCampo() { return campo; }

    public String formatar() { return tipo == Tipo.DADO_BRUTO ? tipo.name() + ":" + campo : tipo.name(); }

    @Override
    public String toString() { return formatar(); }
}

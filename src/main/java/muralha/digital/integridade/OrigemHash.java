package muralha.digital.integridade;

import java.util.regex.Pattern;

/**
 * De onde vem o hash de origem de um equipamento (parâmetro INTEGRIDADE_ORIGEM_HASH).
 * <ul>
 * <li>{@code PAYLOAD_SOAP}: hash enviado pelo Captura junto com a imagem e gravado pela
 * recepção em muralha.vtr_imagem_hash_origem;</li>
 * <li>{@code DADO_BRUTO:<campo>}: campo do JSON em ia.veiculo_caracteristica.dado_bruto;</li>
 * <li>{@code AUSENTE}: o equipamento não envia hash.</li>
 * </ul>
 */
public final class OrigemHash {

    public enum Tipo { PAYLOAD_SOAP, DADO_BRUTO, AUSENTE }

    /** Caminho no JSON: nomes separados por ponto (ex.: {@code integridade.sha256}). */
    private static final Pattern CAMPO = Pattern.compile("[A-Za-z0-9_]{1,40}(\\.[A-Za-z0-9_]{1,40}){0,4}");

    public static final OrigemHash AUSENTE = new OrigemHash(Tipo.AUSENTE, null);

    private final Tipo tipo;
    private final String campo;

    private OrigemHash(Tipo tipo, String campo) {
        this.tipo = tipo;
        this.campo = campo;
    }

    /**
     * Monta a origem validando o campo.
     * @throws IllegalArgumentException se o tipo for DADO_BRUTO sem campo válido
     */
    public static OrigemHash de(Tipo tipo, String campo) {
        if (tipo == null) throw new IllegalArgumentException("Origem do hash não informada.");
        if (tipo == Tipo.DADO_BRUTO) {
            if (campo == null || campo.trim().length() > 100 || !CAMPO.matcher(campo.trim()).matches()) {
                throw new IllegalArgumentException(
                    "Campo do dado bruto inválido: use nomes com letras, números e _ separados por ponto (ex.: integridade.sha256).");
            }
            return new OrigemHash(tipo, campo.trim());
        }
        return tipo == Tipo.AUSENTE ? AUSENTE : new OrigemHash(tipo, null);
    }

    /**
     * Lê o formato do parâmetro global: {@code PAYLOAD_SOAP}, {@code DADO_BRUTO:<campo>} ou {@code AUSENTE}.
     * @throws IllegalArgumentException se o texto for inválido
     */
    public static OrigemHash interpretar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return AUSENTE;
        String t = texto.trim();
        int sep = t.indexOf(':');
        String nomeTipo = sep < 0 ? t : t.substring(0, sep);
        String campo = sep < 0 ? null : t.substring(sep + 1);
        return de(tipoDe(nomeTipo), campo);
    }

    public static Tipo tipoDe(String nome) {
        if (nome != null) {
            for (Tipo tp : Tipo.values()) {
                if (tp.name().equalsIgnoreCase(nome.trim())) return tp;
            }
        }
        throw new IllegalArgumentException("Origem do hash inválida: use PAYLOAD_SOAP, DADO_BRUTO ou AUSENTE.");
    }

    public Tipo getTipo() { return tipo; }

    public String getCampo() { return campo; }

    /** Formato do parâmetro global. */
    public String formatar() {
        return tipo == Tipo.DADO_BRUTO ? tipo.name() + ":" + campo : tipo.name();
    }

    @Override
    public String toString() { return formatar(); }
}

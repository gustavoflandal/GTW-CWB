package muralha.digital.ocr;

import com.consilux.infra.ExpValida;

/** Validação da correção manual de placa com justificativa obrigatória (TR 5.7.5.2). */
public final class ValidacaoCorrecao {

    public static final int JUSTIFICATIVA_MIN = 10;
    public static final int JUSTIFICATIVA_MAX = 500;

    private ValidacaoCorrecao() {}

    /** Placa no formato antigo (AAA9999) ou Mercosul (AAA9A99), em maiúsculas. */
    public static String placa(String placa) {
        String p = placa == null ? "" : placa.trim().toUpperCase();
        if (!ExpValida.PLACA.validar(p) && !ExpValida.PLACA_MERCOSUL.validar(p)) {
            throw new IllegalArgumentException("Placa inválida: " + (p.isEmpty() ? "(vazia)" : p)
                + ". Use o formato AAA9999 ou AAA9A99.");
        }
        return p;
    }

    /**
     * Motivo obrigatório; texto obrigatório (mínimo {@value #JUSTIFICATIVA_MIN} caracteres) quando o
     * motivo exige descrição. Devolve o texto aparado (ou nulo).
     */
    public static String justificativa(Integer idMotivo, boolean motivoExigeTexto, String texto) {
        if (idMotivo == null) throw new IllegalArgumentException("Selecione o motivo da correção.");
        String t = texto == null ? "" : texto.trim();
        if (t.length() > JUSTIFICATIVA_MAX) {
            throw new IllegalArgumentException("A justificativa tem no máximo " + JUSTIFICATIVA_MAX + " caracteres.");
        }
        if (motivoExigeTexto && t.length() < JUSTIFICATIVA_MIN) {
            throw new IllegalArgumentException("Descreva a justificativa (mínimo " + JUSTIFICATIVA_MIN + " caracteres).");
        }
        return t.isEmpty() ? null : t;
    }
}

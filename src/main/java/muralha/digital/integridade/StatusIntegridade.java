package muralha.digital.integridade;

/** Resultado da conferência de integridade de uma imagem recebida (TR 5.2.1.2). */
public enum StatusIntegridade {
    /** SHA-256 calculado igual ao hash enviado pela origem. */
    INTEGRO,
    /** SHA-256 calculado diferente do hash da origem, ou hash da origem malformado. */
    DIVERGENTE,
    /** Não há hash de origem para comparar (equipamento sem origem configurada ou hash não recebido). */
    SEM_CONFERENCIA,
    /** Imagem vazia ou ilegível no banco. */
    ERRO_LEITURA;

    public static StatusIntegridade deTexto(String texto) {
        if (texto == null) return null;
        for (StatusIntegridade s : values()) {
            if (s.name().equalsIgnoreCase(texto.trim())) return s;
        }
        return null;
    }
}

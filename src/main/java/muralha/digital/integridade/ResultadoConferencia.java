package muralha.digital.integridade;

/** Resultado da conferência de uma imagem: status, motivo e hashes envolvidos. */
public final class ResultadoConferencia {

    private final StatusIntegridade status;
    private final String motivo;
    private final String sha256Calculado;
    private final String hashOrigem;

    public ResultadoConferencia(StatusIntegridade status, String motivo, String sha256Calculado, String hashOrigem) {
        this.status = status;
        this.motivo = motivo;
        this.sha256Calculado = sha256Calculado;
        this.hashOrigem = hashOrigem;
    }

    public StatusIntegridade getStatus() { return status; }

    public String getMotivo() { return motivo; }

    /** SHA-256 da imagem gravada; nulo quando a imagem não pôde ser lida. */
    public String getSha256Calculado() { return sha256Calculado; }

    /** Hash informado pela origem (normalizado em minúsculas); nulo se não houver. */
    public String getHashOrigem() { return hashOrigem; }
}

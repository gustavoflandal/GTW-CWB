package muralha.digital.chamado;

/** Anexo ja gravado em disco, pronto para ser registrado em muralha.chamado_anexo. */
public final class AnexoGravado {
    public final String nomeOriginal;
    public final String caminhoRelativo;
    public final String tipoMime;
    public final long tamanho;
    public final String sha256;

    public AnexoGravado(String nomeOriginal, String caminhoRelativo, String tipoMime, long tamanho, String sha256) {
        this.nomeOriginal = nomeOriginal;
        this.caminhoRelativo = caminhoRelativo;
        this.tipoMime = tipoMime;
        this.tamanho = tamanho;
        this.sha256 = sha256;
    }
}

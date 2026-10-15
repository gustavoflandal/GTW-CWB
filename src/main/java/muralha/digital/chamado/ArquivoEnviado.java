package muralha.digital.chamado;

/** Arquivo recebido em um upload multipart. */
public final class ArquivoEnviado {
    public final String nome;
    public final byte[] conteudo;

    public ArquivoEnviado(String nome, byte[] conteudo) {
        this.nome = nome;
        this.conteudo = conteudo;
    }
}

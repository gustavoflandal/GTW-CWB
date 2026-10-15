package muralha.digital.chamado;

/** Chamado inexistente ou fora da visibilidade do usuario (respondido como 404). */
public class ChamadoNaoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ChamadoNaoEncontradoException(String protocolo) {
        super("Chamado não encontrado: " + protocolo);
    }
}

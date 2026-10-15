package muralha.digital.chamado;

/** Violacao de regra de negocio; a mensagem e exibida ao usuario. */
public class RegraChamadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RegraChamadoException(String mensagem) {
        super(mensagem);
    }
}

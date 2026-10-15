package muralha.digital.dashboards.consulta;

/**
 * Erro de validacao da consulta: a mensagem e segura para exibir ao usuario.
 */
public class ErroConsulta extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroConsulta(String mensagem) {
        super(mensagem);
    }
}

package muralha.digital.chamado;

public enum StatusChamado {
    ABERTO, EM_ATENDIMENTO, AGUARDANDO_SOLICITANTE, RESOLVIDO, FECHADO, CANCELADO;

    public boolean isTerminal() {
        return this == FECHADO || this == CANCELADO;
    }
}

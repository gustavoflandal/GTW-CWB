package muralha.digital.chamado;

public enum AcaoChamado {
    ASSUMIR("assumir"), INICIAR("iniciar"), AGUARDAR("aguardar"), RESPONDER("responder"),
    RESOLVER("resolver"), AVALIAR("avaliar"), REABRIR("reabrir"), CANCELAR("cancelar"),
    RECLASSIFICAR("reclassificar");

    private final String codigo;

    AcaoChamado(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /** Observacao obrigatoria na transicao (spec §1.2). */
    public boolean exigeTexto() {
        return this != ASSUMIR && this != AVALIAR;
    }

    public static AcaoChamado deCodigo(String codigo) {
        for (AcaoChamado a : values()) {
            if (a.codigo.equals(codigo)) return a;
        }
        return null;
    }
}

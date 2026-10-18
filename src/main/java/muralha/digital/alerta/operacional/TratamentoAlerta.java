package muralha.digital.alerta.operacional;

public enum TratamentoAlerta {
    CAMPO("Encaminhamento para fiscalização em campo", false),
    ANALISE_OPERACIONAL("Encaminhamento para análise operacional", false),
    REGISTRO_ESTATISTICO("Registro estatístico", true),
    INTEGRACAO_CORPORATIVA("Integração com sistemas corporativos", false),
    GERAR_OCORRENCIA("Geração de ocorrência operacional", true),
    PRIORIZAR_AUDITORIA("Priorização para auditoria", true),
    OUTRO("Tratamento definido pela contratante", false);

    private final String descricao;
    private final boolean executavel;

    TratamentoAlerta(String descricao, boolean executavel) {
        this.descricao = descricao;
        this.executavel = executavel;
    }

    public String getDescricao() { return descricao; }

    /** Se o sistema executa o tratamento sozinho quando ele está marcado como automático. */
    public boolean isExecutavel() { return executavel; }

    public static TratamentoAlerta interpretar(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        String v = valor.trim().toUpperCase();
        for (TratamentoAlerta t : values()) {
            if (t.name().equals(v)) return t;
        }
        throw new IllegalArgumentException("Tratamento desconhecido: " + valor);
    }
}

package muralha.digital.alerta.operacional;

public enum TratamentoAlerta {
    CAMPO("Encaminhamento para fiscalização em campo"),
    ANALISE_OPERACIONAL("Encaminhamento para análise operacional"),
    REGISTRO_ESTATISTICO("Registro estatístico"),
    INTEGRACAO_CORPORATIVA("Integração com sistemas corporativos"),
    GERAR_OCORRENCIA("Geração de ocorrência operacional"),
    PRIORIZAR_AUDITORIA("Priorização para auditoria"),
    OUTRO("Tratamento definido pela contratante");

    private final String descricao;

    TratamentoAlerta(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }

    public static TratamentoAlerta interpretar(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        String v = valor.trim().toUpperCase();
        for (TratamentoAlerta t : values()) {
            if (t.name().equals(v)) return t;
        }
        throw new IllegalArgumentException("Tratamento desconhecido: " + valor);
    }
}

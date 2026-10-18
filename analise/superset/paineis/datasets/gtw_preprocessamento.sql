-- Pré-processamento das infrações por dia, equipamento e operador
-- Fonte: analise.v_preprocessamento
SELECT
    CAST(dia AS datetime) AS dia,
    local_nome AS equipamento,
    operador,
    recebidos,
    disponibilizados,
    descartados,
    pendentes,
    backlog_24h,
    tempo_medio_min,
    pct_sla_72h
FROM analise.v_preprocessamento

-- SLA de latência por dia (janelas de 5 minutos avaliadas pelo job do Plano 07)
-- Fonte: analise.v_sla_latencia_dia
SELECT
    CAST(dia AS datetime) AS dia,
    janelas_avaliadas,
    janelas_ok,
    janelas_violacao,
    media_p95_ms,
    threshold_ms,
    total_passagens
FROM analise.v_sla_latencia_dia

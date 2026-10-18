-- Disponibilidade diária por equipamento (horas com captura ÷ 24, incluindo dias sem captura)
-- Fonte: analise.v_disponibilidade_dia
SELECT
    CAST(dia AS datetime) AS dia,
    local_nome AS equipamento,
    verificacoes AS horas_monitoradas,
    verificacoes_ok AS horas_com_captura,
    max_minutos_offline AS minutos_sem_captura
FROM analise.v_disponibilidade_dia

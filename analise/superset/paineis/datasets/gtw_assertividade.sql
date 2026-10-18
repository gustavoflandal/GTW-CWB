-- Resultado da dupla análise por dia
-- Fonte: analise.v_assertividade_dia (muralha.infracao_analise)
SELECT
    CAST(dia AS datetime) AS dia,
    total_analisadas,
    pre_aprovadas,
    reprovadas,
    desempate
FROM analise.v_assertividade_dia

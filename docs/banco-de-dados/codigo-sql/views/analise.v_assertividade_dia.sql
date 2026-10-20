
CREATE VIEW analise.v_assertividade_dia AS
SELECT
    CAST(ia.dt_analise AS DATE)       AS dia,
    COUNT(DISTINCT ia.id_infracao)    AS total_analisadas,
    SUM(CASE WHEN vsa.status_analise = 'PRE_APROVADA' THEN 1 ELSE 0 END)
                                      AS pre_aprovadas,
    SUM(CASE WHEN vsa.status_analise = 'REPROVADA' THEN 1 ELSE 0 END)
                                      AS reprovadas,
    SUM(CASE WHEN vsa.status_analise = 'DESEMPATE' THEN 1 ELSE 0 END)
                                      AS desempate,
    CAST(
        CASE WHEN COUNT(DISTINCT ia.id_infracao) > 0
             THEN 100.0 * SUM(CASE WHEN vsa.status_analise IN ('PRE_APROVADA','REPROVADA') THEN 1 ELSE 0 END)
                  / COUNT(DISTINCT ia.id_infracao)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_assertividade
FROM muralha.infracao_analise ia
JOIN muralha.vtr_status_analise vsa
    ON vsa.id_veiculo_tempo_real = ia.id_infracao
WHERE ia.sequencia = 1
GROUP BY CAST(ia.dt_analise AS DATE);


CREATE VIEW analise.v_sla_latencia_dia AS
SELECT
    CAST(s.dt_alerta AS DATE)         AS dia,
    COUNT(*)                          AS janelas_avaliadas,
    SUM(CASE WHEN s.violacao = 0 THEN 1 ELSE 0 END) AS janelas_ok,
    SUM(CASE WHEN s.violacao = 1 THEN 1 ELSE 0 END) AS janelas_violacao,
    CAST(
        CASE WHEN COUNT(*) > 0
             THEN 100.0 * SUM(CASE WHEN s.violacao = 0 THEN 1 ELSE 0 END) / COUNT(*)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_cumprimento,
    AVG(s.percentil95_ms)             AS media_p95_ms,
    MAX(s.threshold_ms)               AS threshold_ms,
    SUM(s.total_passagens)            AS total_passagens
FROM muralha.alerta_sla s
GROUP BY CAST(s.dt_alerta AS DATE);

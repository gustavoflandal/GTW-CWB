
CREATE VIEW analise.v_sla_preproc_dia AS
SELECT
    CAST(sp.dt_alerta AS DATE)        AS dia,
    COUNT(*)                          AS snapshots,
    AVG(sp.total_pendentes)           AS media_pendentes,
    MAX(sp.acima_72h)                 AS max_acima_72h,
    MAX(sp.acima_48h)                 AS max_acima_48h,
    MAX(sp.acima_24h)                 AS max_acima_24h,
    CAST(
        CASE WHEN AVG(sp.total_pendentes) > 0
             THEN 100.0 * (1.0 - CAST(AVG(sp.acima_72h) AS FLOAT) / AVG(sp.total_pendentes))
             ELSE 100.0 END
        AS DECIMAL(5,2))              AS pct_dentro_sla
FROM muralha.alerta_sla_preproc sp
GROUP BY CAST(sp.dt_alerta AS DATE);

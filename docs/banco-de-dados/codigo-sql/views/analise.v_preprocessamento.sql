
-- 4. Pré-processamento com a versão da própria infração -----------------------------
CREATE   VIEW analise.v_preprocessamento AS
SELECT
    CAST(i.data_entrada AS DATE)                                       AS dia,
    i.id_local,
    ISNULL(lv.nome, 'Equipamento ' + CAST(i.id_local AS varchar(10)))  AS local_nome,
    i.id_usuario_final                                                 AS id_operador,
    ISNULL(RTRIM(u.usuario), 'Processamento automático')               AS operador,
    COUNT(*)                                                           AS recebidos,
    SUM(CASE WHEN i.id_processo_concluido IS NOT NULL THEN 1 ELSE 0 END) AS preprocessados,
    SUM(CASE WHEN i.id_processo_concluido IS NOT NULL AND ISNULL(i.id_inconsistencia, 0) = 0
             THEN 1 ELSE 0 END)                                        AS disponibilizados,
    SUM(CASE WHEN ISNULL(i.id_inconsistencia, 0) <> 0 THEN 1 ELSE 0 END) AS descartados,
    CAST(100.0 * SUM(CASE WHEN i.id_processo_concluido IS NOT NULL AND ISNULL(i.id_inconsistencia, 0) = 0
                          THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) AS DECIMAL(5,2)) AS pct_aproveitamento,
    AVG(CASE WHEN i.data_entrada >= i.data THEN DATEDIFF(MINUTE, i.data, i.data_entrada) END) AS tempo_medio_min,
    COUNT(*)                                                           AS produtividade,
    CAST(NULL AS INT)                                                  AS divergencias,
    CAST(NULL AS INT)                                                  AS retrabalho,
    SUM(CASE WHEN i.id_processo_concluido IS NULL THEN 1 ELSE 0 END)   AS pendentes,
    SUM(CASE WHEN i.id_processo_concluido IS NULL AND i.data < DATEADD(HOUR, -24, SYSDATETIME())
             THEN 1 ELSE 0 END)                                        AS backlog_24h,
    CAST(100.0 * SUM(CASE WHEN i.data_entrada >= i.data AND DATEDIFF(HOUR, i.data, i.data_entrada) <= 72
                          THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) AS DECIMAL(5,2)) AS pct_sla_72h
FROM dbo.infracao i
LEFT JOIN analise.v_local_versao lv ON lv.id_local = i.id_local AND lv.sequencia_local = i.sequencia_local
LEFT JOIN dbo.sis_usuario u ON u.id_usuario = i.id_usuario_final
WHERE i.id_enquadramento <> 1
GROUP BY CAST(i.data_entrada AS DATE), i.id_local, lv.nome, i.id_usuario_final, u.usuario;

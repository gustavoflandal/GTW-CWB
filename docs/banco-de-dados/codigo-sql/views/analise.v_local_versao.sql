
-- 1. Versões do equipamento com vigência ------------------------------------------
CREATE   VIEW analise.v_local_versao AS
SELECT
    l.id_local,
    l.sequencia_local,
    CASE WHEN l.sequencia_local = p.seq_min THEN CAST('19000101' AS DATETIME)
         ELSE ISNULL(l.data_atualizacao, CAST('19000101' AS DATETIME)) END             AS vigencia_inicio,
    ISNULL(LEAD(l.data_atualizacao) OVER (PARTITION BY l.id_local ORDER BY l.sequencia_local),
           CAST('99991231' AS DATETIME))                                              AS vigencia_fim,
    RTRIM(l.nome)                                                                     AS nome,
    l.tipo,
    RTRIM(l.localidade_desc)                                                          AS localidade,
    ISNULL(l.posicao_lat, c.lat)                                                      AS lat,
    ISNULL(l.posicao_lon, c.lon)                                                      AS lon
FROM dbo.local l
CROSS APPLY (SELECT MIN(m.sequencia_local) AS seq_min FROM dbo.local m WHERE m.id_local = l.id_local) p
OUTER APPLY (
    SELECT TOP 1 x.posicao_lat AS lat, x.posicao_lon AS lon
    FROM dbo.local x
    WHERE x.id_local = l.id_local AND x.sequencia_local < l.sequencia_local
      AND x.posicao_lat IS NOT NULL AND x.posicao_lon IS NOT NULL
      AND RTRIM(x.nome) = RTRIM(l.nome)
    ORDER BY x.sequencia_local DESC
) c;

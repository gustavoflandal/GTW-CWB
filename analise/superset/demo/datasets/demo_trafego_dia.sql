-- Tráfego diário por equipamento e classe (série histórica)
-- Fonte: dbo.veiculo_sumarizado (versões de equipamento de teste excluídas)
SELECT
    a.dia,
    LEFT(LTRIM(ISNULL(RTRIM(n.nome), 'Equipamento ' + CAST(a.id_local AS varchar(10)))), 70) AS equipamento,
    CASE WHEN cv.descricao IS NULL OR RTRIM(cv.descricao) = 'N/A' THEN N'Não classificado'
         ELSE RTRIM(cv.descricao) END AS classe,
    a.trafego,
    a.soma_velocidade,
    a.placas_lidas,
    c.lat,
    c.lon
FROM (
    SELECT
        s.data AS dia,
        s.id_local,
        s.sequencia_local,
        s.id_classe,
        SUM(s.trafego) AS trafego,
        SUM(CAST(s.media_velocidade AS float) * s.trafego) AS soma_velocidade,
        SUM(s.placa_lida) AS placas_lidas
    FROM dbo.veiculo_sumarizado s
    GROUP BY s.data, s.id_local, s.sequencia_local, s.id_classe
) a
LEFT JOIN dbo.local v ON v.id_local = a.id_local AND v.sequencia_local = a.sequencia_local
OUTER APPLY (
    SELECT TOP 1 x.nome FROM dbo.local x
    WHERE x.id_local = a.id_local AND x.nome NOT LIKE 'Teste%'
    ORDER BY x.sequencia_local DESC
) n
OUTER APPLY (
    SELECT TOP 1 CAST(x.posicao_lat AS float) AS lat, CAST(x.posicao_lon AS float) AS lon
    FROM dbo.local x
    WHERE x.id_local = a.id_local AND x.posicao_lat IS NOT NULL AND x.nome NOT LIKE 'Teste%'
    ORDER BY ABS(CAST(x.sequencia_local AS int) - CAST(a.sequencia_local AS int)), x.sequencia_local
) c
LEFT JOIN dbo.classe_veiculo cv ON cv.id_classe = a.id_classe
WHERE ISNULL(v.nome, '') NOT LIKE 'Teste%'

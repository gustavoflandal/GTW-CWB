
-- 1. Equipamento vigente -----------------------------------------------------
CREATE   VIEW analise.v_local_vigente AS
SELECT
    u.id_local,
    RTRIM(u.nome)            AS nome,
    u.tipo,
    RTRIM(u.localidade_desc) AS localidade,
    c.lat,
    c.lon
FROM (
    SELECT l.id_local, l.nome, l.tipo, l.localidade_desc,
           ROW_NUMBER() OVER (PARTITION BY l.id_local ORDER BY l.sequencia_local DESC) AS rn
    FROM dbo.local l
) u
OUTER APPLY (
    SELECT TOP 1 x.posicao_lat AS lat, x.posicao_lon AS lon
    FROM dbo.local x
    WHERE x.id_local = u.id_local AND x.posicao_lat IS NOT NULL AND x.posicao_lon IS NOT NULL
    ORDER BY x.sequencia_local DESC
) c
WHERE u.rn = 1;

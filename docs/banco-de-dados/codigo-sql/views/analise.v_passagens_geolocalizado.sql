
CREATE   VIEW analise.v_passagens_geolocalizado AS
SELECT
    p.hora,
    MAX(p.local_nome)     AS local_nome,
    CAST(p.lat AS FLOAT)  AS lat,
    CAST(p.lon AS FLOAT)  AS lon,
    SUM(p.passagens)      AS total_passagens,
    CAST(SUM(p.soma_velocidade) * 1.0 / NULLIF(SUM(p.passagens), 0) AS DECIMAL(6,2)) AS velocidade_media
FROM analise.mat_passagens_hora p
WHERE p.lat IS NOT NULL AND p.lon IS NOT NULL
GROUP BY p.hora, p.id_local, p.lat, p.lon;


-- 7. Views geolocalizadas (substituem 20261017_analise_mapas_geo.sql) --------------
CREATE   VIEW analise.v_infracoes_geolocalizado AS
SELECT
    i.dia,
    i.local_nome,
    CAST(i.lat AS FLOAT)  AS lat,
    CAST(i.lon AS FLOAT)  AS lon,
    i.enquadramento,
    i.total               AS total_infracoes,
    i.excesso_medio
FROM analise.mat_infracoes_dia i
WHERE i.lat IS NOT NULL AND i.lon IS NOT NULL;

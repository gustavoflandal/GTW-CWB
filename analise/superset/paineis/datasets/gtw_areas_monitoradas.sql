-- Áreas monitoradas como polígono [[lng, lat], ...]
-- Fonte: analise.v_areas_monitoradas_geojson (carga analise.spu_carga_areas_monitoradas)
SELECT
    id,
    nome AS area,
    cor,
    poligono
FROM analise.v_areas_monitoradas_geojson
WHERE poligono IS NOT NULL

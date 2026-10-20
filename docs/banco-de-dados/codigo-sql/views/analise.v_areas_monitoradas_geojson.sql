
CREATE   VIEW analise.v_areas_monitoradas_geojson AS
SELECT
    am.id,
    am.nome,
    am.dados_json,
    m.cor,
    m.poligono
FROM muralha.area_monitorada am
LEFT JOIN analise.mat_areas_monitoradas m ON m.id = am.id
WHERE am.deletado = 0;

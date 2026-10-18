-- Infrações por dia e equipamento, com as coordenadas da versão vigente
-- Fonte: analise.v_infracoes_geolocalizado (sem imagens de teste)
SELECT
    CAST(g.dia AS datetime) AS dia,
    g.local_nome AS equipamento,
    g.lat,
    g.lon,
    g.enquadramento,
    g.total_infracoes
FROM analise.v_infracoes_geolocalizado g
WHERE g.enquadramento <> 'Imagem teste'

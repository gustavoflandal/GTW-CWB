-- Passagens por hora e equipamento, com as coordenadas da versão vigente
-- Fonte: analise.v_passagens_geolocalizado
SELECT
    hora,
    local_nome AS equipamento,
    lat,
    lon,
    total_passagens
FROM analise.v_passagens_geolocalizado

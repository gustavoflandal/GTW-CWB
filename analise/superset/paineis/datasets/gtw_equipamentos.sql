-- Equipamentos cadastrados com coordenadas (versão atual)
-- Fonte: analise.v_equipamentos
SELECT
    id_local,
    nome AS equipamento,
    CAST(lat AS float) AS lat,
    CAST(lon AS float) AS lon,
    ISNULL(area_monitorada, N'Sem área monitorada') AS area_monitorada
FROM analise.v_equipamentos
WHERE lat IS NOT NULL AND lon IS NOT NULL

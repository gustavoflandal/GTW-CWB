-- Alertas de mobilidade urbana reportados no Waze (nível de registro)
-- Fonte: mobilidade.alertas
SELECT
    a.publish_datetime_brasilia AS data_hora,
    RIGHT('0' + CAST(DATEPART(HOUR, a.publish_datetime_brasilia) AS varchar(2)), 2) + 'h' AS hora_dia,
    CASE DATEDIFF(DAY, '19000107', CAST(a.publish_datetime_brasilia AS date)) % 7
        WHEN 0 THEN N'1-Dom' WHEN 1 THEN N'2-Seg' WHEN 2 THEN N'3-Ter' WHEN 3 THEN N'4-Qua'
        WHEN 4 THEN N'5-Qui' WHEN 5 THEN N'6-Sex' ELSE N'7-Sáb' END AS dia_semana,
    ISNULL(NULLIF(LTRIM(RTRIM(a.city)), ''), N'Não informada') AS cidade,
    ISNULL(NULLIF(LTRIM(RTRIM(a.street)), ''), N'Via não informada') AS via,
    CASE a.type
        WHEN 'ACCIDENT' THEN N'Acidente'
        WHEN 'JAM' THEN N'Congestionamento'
        WHEN 'HAZARD' THEN N'Perigo na via'
        WHEN 'ROAD_CLOSED' THEN N'Via interditada'
        WHEN 'POLICE' THEN N'Presença policial'
        ELSE ISNULL(a.type, N'Outro') END AS tipo,
    CASE a.subtype
        WHEN 'HAZARD_ON_ROAD_POT_HOLE' THEN N'Buraco na via'
        WHEN 'JAM_STAND_STILL_TRAFFIC' THEN N'Trânsito parado'
        WHEN 'JAM_HEAVY_TRAFFIC' THEN N'Trânsito intenso'
        WHEN 'JAM_MODERATE_TRAFFIC' THEN N'Trânsito moderado'
        WHEN 'HAZARD_ON_ROAD_LANE_CLOSED' THEN N'Faixa interditada'
        WHEN 'HAZARD_ON_SHOULDER_CAR_STOPPED' THEN N'Veículo parado no acostamento'
        WHEN 'HAZARD_ON_ROAD_CAR_STOPPED' THEN N'Veículo parado na pista'
        WHEN 'HAZARD_ON_ROAD_CONSTRUCTION' THEN N'Obra na via'
        WHEN 'HAZARD_ON_ROAD' THEN N'Perigo na pista'
        WHEN 'HAZARD_ON_ROAD_TRAFFIC_LIGHT_FAULT' THEN N'Semáforo com defeito'
        WHEN 'HAZARD_ON_SHOULDER_ANIMALS' THEN N'Animais no acostamento'
        WHEN 'HAZARD_WEATHER_FLOOD' THEN N'Alagamento'
        WHEN 'HAZARD_ON_ROAD_OBJECT' THEN N'Objeto na pista'
        WHEN 'ACCIDENT_MINOR' THEN N'Acidente leve'
        WHEN 'ACCIDENT_MAJOR' THEN N'Acidente grave'
        WHEN 'ROAD_CLOSED_EVENT' THEN N'Interdição por evento'
        WHEN 'ROAD_CLOSED_CONSTRUCTION' THEN N'Interdição por obra'
        ELSE CASE WHEN a.subtype IS NULL OR a.subtype = '' THEN N'Sem subtipo' ELSE a.subtype END
    END AS subtipo,
    a.alert_reliability AS confiabilidade,
    a.num_thumbs_up AS confirmacoes,
    a.latitude AS lat,
    a.longitude AS lon
FROM mobilidade.alertas a
WHERE a.publish_datetime_brasilia IS NOT NULL

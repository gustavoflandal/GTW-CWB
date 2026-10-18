-- Infrações registradas (nível de registro, para análises por hora e faixa de excesso)
-- Fonte: dbo.infracao + dbo.enquadramento; equipamento pela versão da própria infração
-- (analise.v_local_versao). Excluídas as imagens de teste (enquadramento 1).
SELECT
    i.data AS data_infracao,
    RIGHT('0' + CAST(DATEPART(HOUR, i.data) AS varchar(2)), 2) + 'h' AS hora_dia,
    CASE DATEDIFF(DAY, '19000107', CAST(i.data AS date)) % 7
        WHEN 0 THEN N'1-Dom' WHEN 1 THEN N'2-Seg' WHEN 2 THEN N'3-Ter' WHEN 3 THEN N'4-Qua'
        WHEN 4 THEN N'5-Qui' WHEN 5 THEN N'6-Sex' ELSE N'7-Sáb' END AS dia_semana,
    LTRIM(ISNULL(lv.nome, 'Equipamento ' + CAST(i.id_local AS varchar(10)))) AS equipamento,
    N'Faixa ' + CAST(i.pista AS nvarchar(3)) AS faixa,
    CASE i.id_enquadramento
        WHEN 74550 THEN N'Velocidade até 20% acima'
        WHEN 74630 THEN N'Velocidade 20% a 50% acima'
        WHEN 74710 THEN N'Velocidade mais de 50% acima'
        WHEN 57461 THEN N'Local/horário não permitido'
        WHEN 57463 THEN N'Local/horário não permitido (caminhão)'
        WHEN 60412 THEN N'Conversão proibida'
        WHEN 60503 THEN N'Avanço de sinal vermelho'
        WHEN 57030 THEN N'Fora da faixa destinada'
        WHEN 56732 THEN N'Parada sobre faixa de pedestres'
        ELSE N'Outras' END AS tipo_infracao,
    CASE WHEN i.id_enquadramento IN (74550, 74630, 74710) THEN N'Excesso de velocidade'
         ELSE N'Circulação e sinalização' END AS categoria,
    RTRIM(e.descricao) AS enquadramento,
    i.velocidade_limite,
    i.velocidade_considerada,
    CASE WHEN i.id_enquadramento IN (74550, 74630, 74710)
         THEN i.velocidade_considerada - i.velocidade_limite END AS excesso_kmh,
    CASE WHEN i.id_enquadramento NOT IN (74550, 74630, 74710) THEN N'Não se aplica'
         WHEN i.velocidade_considerada - i.velocidade_limite <= 10 THEN N'01-10 km/h'
         WHEN i.velocidade_considerada - i.velocidade_limite <= 20 THEN N'11-20 km/h'
         WHEN i.velocidade_considerada - i.velocidade_limite <= 30 THEN N'21-30 km/h'
         WHEN i.velocidade_considerada - i.velocidade_limite <= 40 THEN N'31-40 km/h'
         ELSE N'Acima de 40 km/h' END AS faixa_excesso
FROM dbo.infracao i
LEFT JOIN analise.v_local_versao lv ON lv.id_local = i.id_local AND lv.sequencia_local = i.sequencia_local
LEFT JOIN dbo.enquadramento e ON e.id_enquadramento = i.id_enquadramento
WHERE i.id_enquadramento <> 1

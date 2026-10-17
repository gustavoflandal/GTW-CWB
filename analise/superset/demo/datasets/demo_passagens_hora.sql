-- Passagens veiculares agregadas por hora — últimos 30 dias com dados
-- Fonte: muralha.veiculo_tempo_real (todas as capturas; nome = versão mais recente do equipamento)
SELECT
    a.data_hora,
    RIGHT('0' + CAST(DATEPART(HOUR, a.data_hora) AS varchar(2)), 2) + 'h' AS hora_dia,
    CASE DATEDIFF(DAY, '19000107', CAST(a.data_hora AS date)) % 7
        WHEN 0 THEN N'1-Dom' WHEN 1 THEN N'2-Seg' WHEN 2 THEN N'3-Ter' WHEN 3 THEN N'4-Qua'
        WHEN 4 THEN N'5-Qui' WHEN 5 THEN N'6-Sex' ELSE N'7-Sáb' END AS dia_semana,
    ISNULL(RTRIM(l.nome), 'Equipamento ' + CAST(a.id_local AS varchar(10))) AS equipamento,
    CASE WHEN cv.descricao IS NULL OR RTRIM(cv.descricao) = 'N/A' THEN N'Não classificado'
         ELSE RTRIM(cv.descricao) END AS classificacao,
    CASE WHEN a.placa_mercosul = 1 THEN N'Mercosul' ELSE N'Modelo antigo' END AS tipo_placa,
    a.passagens,
    a.soma_velocidade,
    a.acima_60kmh
FROM (
    SELECT
        DATEADD(HOUR, DATEDIFF(HOUR, 0, v.data), 0) AS data_hora,
        v.id_local,
        v.classificacao,
        v.placa_mercosul,
        COUNT(*) AS passagens,
        SUM(CAST(v.velocidade AS bigint)) AS soma_velocidade,
        SUM(CASE WHEN v.velocidade > 60 THEN 1 ELSE 0 END) AS acima_60kmh
    FROM muralha.veiculo_tempo_real v
    WHERE v.data >= DATEADD(DAY, -30, (SELECT MAX(m.data) FROM muralha.veiculo_tempo_real m WHERE m.data <= GETDATE()))
      AND v.data <= GETDATE()
    GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, v.data), 0), v.id_local, v.classificacao, v.placa_mercosul
) a
LEFT JOIN (
    SELECT z.id_local, z.nome
    FROM (SELECT id_local, nome, ROW_NUMBER() OVER (PARTITION BY id_local ORDER BY sequencia_local DESC) AS rn
          FROM dbo.local) z
    WHERE z.rn = 1
) l ON l.id_local = a.id_local
LEFT JOIN dbo.classe_veiculo cv ON cv.id_classe = a.classificacao

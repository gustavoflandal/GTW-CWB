-- Passagens por hora, equipamento, faixa, classificação e tipo de placa
-- Fonte: analise.mat_passagens_hora (carga analise.spu_carga_passagens_hora; nome do
-- equipamento pela versão vigente na hora da passagem)
SELECT
    p.hora AS data_hora,
    RIGHT('0' + CAST(DATEPART(HOUR, p.hora) AS varchar(2)), 2) + 'h' AS hora_dia,
    CASE DATEDIFF(DAY, '19000107', CAST(p.hora AS date)) % 7
        WHEN 0 THEN N'1-Dom' WHEN 1 THEN N'2-Seg' WHEN 2 THEN N'3-Ter' WHEN 3 THEN N'4-Qua'
        WHEN 4 THEN N'5-Qui' WHEN 5 THEN N'6-Sex' ELSE N'7-Sáb' END AS dia_semana,
    p.local_nome AS equipamento,
    N'Faixa ' + CAST(p.faixa AS nvarchar(3)) AS faixa,
    CASE WHEN cv.descricao IS NULL OR RTRIM(cv.descricao) = 'N/A' THEN N'Não classificado'
         ELSE RTRIM(cv.descricao) END AS classificacao,
    CASE WHEN p.placa_mercosul = 1 THEN N'Mercosul' ELSE N'Modelo antigo' END AS tipo_placa,
    p.passagens,
    p.soma_velocidade
FROM analise.mat_passagens_hora p
LEFT JOIN dbo.classe_veiculo cv ON cv.id_classe = p.classificacao

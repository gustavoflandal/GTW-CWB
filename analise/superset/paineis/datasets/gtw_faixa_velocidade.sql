-- Tráfego mensal por faixa de velocidade e classe
-- Fonte: dbo.veiculo_sumarizado
SELECT
    DATEFROMPARTS(YEAR(s.data), MONTH(s.data), 1) AS mes,
    CASE WHEN s.id_faixa_velocidade >= 10 THEN N'Acima de 100 km/h'
         ELSE RIGHT('0' + CAST(s.id_faixa_velocidade * 10 AS varchar(3)), 2) + N' a '
            + RIGHT('0' + CAST(s.id_faixa_velocidade * 10 + 9 AS varchar(3)), 2) + N' km/h' END AS faixa_velocidade,
    CASE WHEN cv.descricao IS NULL OR RTRIM(cv.descricao) = 'N/A' THEN N'Não classificado'
         ELSE RTRIM(cv.descricao) END AS classe,
    SUM(s.trafego) AS trafego
FROM dbo.veiculo_sumarizado s
LEFT JOIN dbo.classe_veiculo cv ON cv.id_classe = s.id_classe
GROUP BY DATEFROMPARTS(YEAR(s.data), MONTH(s.data), 1),
         CASE WHEN s.id_faixa_velocidade >= 10 THEN N'Acima de 100 km/h'
              ELSE RIGHT('0' + CAST(s.id_faixa_velocidade * 10 AS varchar(3)), 2) + N' a '
                 + RIGHT('0' + CAST(s.id_faixa_velocidade * 10 + 9 AS varchar(3)), 2) + N' km/h' END,
         cv.descricao

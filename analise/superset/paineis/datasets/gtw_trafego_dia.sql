-- Tráfego diário por equipamento e classe (série histórica consolidada)
-- Fonte: dbo.veiculo_sumarizado; equipamento pela versão do próprio registro (analise.v_local_versao)
SELECT
    a.dia,
    LEFT(LTRIM(ISNULL(lv.nome, 'Equipamento ' + CAST(a.id_local AS varchar(10)))), 70) AS equipamento,
    CASE WHEN cv.descricao IS NULL OR RTRIM(cv.descricao) = 'N/A' THEN N'Não classificado'
         ELSE RTRIM(cv.descricao) END AS classe,
    a.trafego,
    a.soma_velocidade,
    a.placas_lidas
FROM (
    SELECT
        s.data AS dia,
        s.id_local,
        s.sequencia_local,
        s.id_classe,
        SUM(s.trafego) AS trafego,
        SUM(CAST(s.media_velocidade AS float) * s.trafego) AS soma_velocidade,
        SUM(s.placa_lida) AS placas_lidas
    FROM dbo.veiculo_sumarizado s
    GROUP BY s.data, s.id_local, s.sequencia_local, s.id_classe
) a
LEFT JOIN analise.v_local_versao lv ON lv.id_local = a.id_local AND lv.sequencia_local = a.sequencia_local
LEFT JOIN dbo.classe_veiculo cv ON cv.id_classe = a.id_classe

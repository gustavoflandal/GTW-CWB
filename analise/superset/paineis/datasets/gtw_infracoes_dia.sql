-- Infrações por dia, equipamento, faixa e enquadramento
-- Fonte: analise.mat_infracoes_dia (carga analise.spu_carga_infracoes_dia). Excluídas as
-- imagens de teste (enquadramento 1), como em gtw_infracoes.
SELECT
    CAST(m.dia AS datetime) AS dia,
    m.local_nome AS equipamento,
    N'Faixa ' + CAST(m.faixa AS nvarchar(3)) AS faixa,
    m.enquadramento,
    CASE WHEN m.id_enquadramento IN (74550, 74630, 74710) THEN N'Excesso de velocidade'
         ELSE N'Circulação e sinalização' END AS categoria,
    m.total,
    m.excesso_medio,
    CASE WHEN m.excesso_medio IS NOT NULL THEN m.total END AS total_com_excesso
FROM analise.mat_infracoes_dia m
WHERE m.id_enquadramento <> 1

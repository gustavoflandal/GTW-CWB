-- Infrações descartadas no processamento, com o motivo (inconsistência)
-- Fonte: dbo.infracao + dbo.inconsistencia; equipamento pela versão da infração
SELECT
    i.data_entrada AS dt_entrada,
    LTRIM(ISNULL(lv.nome, 'Equipamento ' + CAST(i.id_local AS varchar(10)))) AS equipamento,
    ISNULL(RTRIM(x.descricao), N'Inconsistência ' + CAST(i.id_inconsistencia AS nvarchar(10))) AS motivo
FROM dbo.infracao i
LEFT JOIN analise.v_local_versao lv ON lv.id_local = i.id_local AND lv.sequencia_local = i.sequencia_local
LEFT JOIN dbo.inconsistencia x ON x.id_inconsistencia = i.id_inconsistencia
WHERE i.id_enquadramento <> 1
  AND ISNULL(i.id_inconsistencia, 0) <> 0

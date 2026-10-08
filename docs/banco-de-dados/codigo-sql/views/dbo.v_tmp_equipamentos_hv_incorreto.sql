
CREATE VIEW [dbo].[v_tmp_equipamentos_hv_incorreto] AS

SELECT lv.id_local FROM eventos_csx ev (NOLOCK)
JOIN eventos_csx_desc_proprietario evp (NOLOCK) ON ev.id_proprietario = evp.id_proprietario
JOIN local_vigente lv (NOLOCK) ON CAST(lv.serie_equipamento AS CHAR(7)) = evp.proprietario
WHERE 
ev.data_hora >= '2018-10-13 23:00:00' AND
ev.id_evento = 45
GROUP BY lv.id_local


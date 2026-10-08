
CREATE VIEW [dbo].[local_regra_infracao_vigente]
AS
SELECT
	lri.id_configuracao_equipamento,
	lri.id_local,
	lri.id_pista,
	lri.tipo,
	lri.data_modificacao,
	lri.data_inicio,
	lri.data_fim,
	lri.serie_equipamento,
	lri.usar_panoramica,
	lri.cod_pista
FROM local_vigente lv (nolock)
	INNER JOIN local_regra_infracao lri (nolock)
		ON lv.id_configuracao_equipamento = lri.id_configuracao_equipamento



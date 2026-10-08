
CREATE VIEW [dbo].[local_parametro_adicional_data_search] AS
SELECT 
	lv.serie_equipamento,
	lv.id_local,
	CASE WHEN pa.parametros_adicionais like '%DATABASE_SEARCH=1%' THEN 1 ELSE 0 END AS database_search_ativo
FROM local_vigente lv (nolock)
	JOIN configuracao_equipamento_parametros_adicionais pa (nolock)
		ON pa.id_configuracao_equipamento = lv.id_configuracao_equipamento



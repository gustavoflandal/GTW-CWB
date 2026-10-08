CREATE VIEW [muralha].[v_status_alerta_vinculado]
AS
	SELECT id,
		   descricao,
		   descricao_detalhada,
		   CASE WHEN id = 'CA5E4AE0-501E-48E2-9652-A1CF7AA340BB' THEN 1 ELSE 0 END AS status_padrao
	FROM   muralha.status_alerta
	WHERE  id IN ('298F5A62-C799-4CA9-8220-6B08F8664534','15EBBA5F-C805-449E-83CC-227ED3B3AD3C','CA5E4AE0-501E-48E2-9652-A1CF7AA340BB')

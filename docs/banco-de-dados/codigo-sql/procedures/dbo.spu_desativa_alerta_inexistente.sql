CREATE PROCEDURE [dbo].[spu_desativa_alerta_inexistente] (@info_adic VARCHAR(100))
AS
BEGIN
	UPDATE painel_contrato_alerta
	SET ativo = 0, data_atualizacao = GETDATE(), informacao_adicional=@info_adic
	FROM 
		painel_contrato_alerta pca
		LEFT JOIN dbo.fcn_getRelatorioPrioridadeManutencao(NULL, NULL) alerta
			ON	alerta.numeroSerie = pca.serie_equipamento AND pca.id_pista = alerta.idPista
				
	WHERE
		alerta.numeroSerie IS NULL AND
		pca.informacao_adicional IS NULL
		AND pca.ativo = 1
		
	RETURN @@ROWCOUNT
END




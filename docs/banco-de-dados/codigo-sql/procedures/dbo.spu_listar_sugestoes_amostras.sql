CREATE PROCEDURE [dbo].[spu_listar_sugestoes_amostras]
	@id_local INT,
	@id_pista TINYINT,
	@dia DATE,
	@metrologica BIT,
	@page_offset INT = NULL,
	@page_limit INT = NULL
AS

	DECLARE @id_veiculo_ja_selecionado INT = NULL
	DECLARE @metrologica_negado BIT = NULL

	SET @metrologica_negado =	CASE 
									WHEN @metrologica = 1 
										THEN 0 
									ELSE 
										1 
								END

	--Se vamos sujerir uma imagem, esta imagem não pode ter sido utilizada para comprovar outra amostra...
	SELECT 
		@id_veiculo_ja_selecionado = id_veiculo 
	FROM 
		fcn_lista_amostras_periodo_local_pista (@dia, @dia, @id_local, @id_pista, @metrologica_negado)

	SELECT 
		* 
	FROM 
		dbo.fcn_listar_sugestoes_amostras(@id_local, @id_pista, @dia, @metrologica, @page_offset, @page_limit)
	WHERE	(@id_veiculo_ja_selecionado IS NULL 
		OR	id_veiculo <> @id_veiculo_ja_selecionado)
	ORDER BY 
		score_total DESC





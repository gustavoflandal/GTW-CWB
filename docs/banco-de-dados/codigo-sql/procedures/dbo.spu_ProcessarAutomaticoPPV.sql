CREATE PROCEDURE [dbo].[spu_ProcessarAutomaticoPPV] @id_processo INT AS 

DECLARE @id_infracao INT
DECLARE @id_usuario INT = 1
DECLARE @id_inconsistencia INT
DECLARE @id_imagem INT

DECLARE @continuar INT = 1
DECLARE @contador INT = 1
DECLARE @total_infracoes INT 

--DECLARE @id_processo INT = 1
SELECT 
	--COUNT(id_infracao) 
	@total_infracoes = COUNT(id_infracao) 
FROM 
	infracao i (nolock) 
	INNER JOIN veiculo v (NOLOCK) ON v.id_veiculo = i.id_veiculo
WHERE	
	    id_processo = @id_processo 
	AND CAST(i.data AS DATE) = '2018-04-09'
	AND v.id_classe IN ('C', 'O')
	AND i.id_local IN (1002, 1004)

WHILE @continuar > 0 AND @contador <= @total_infracoes 

	BEGIN

	SELECT TOP 1 
		@id_infracao = i.id_infracao, 
		@id_inconsistencia = 0, --i.id_inconsistencia, 
		@id_imagem = ii.id_imagem_obj 
	FROM infracao i (nolock) 
		INNER JOIN infracao_imagem ii (nolock) ON i.id_infracao = ii.id_infracao
		INNER JOIN veiculo v (NOLOCK) ON v.id_veiculo = i.id_veiculo
	WHERE	 
		    id_processo = @id_processo 
	AND CAST(i.data AS DATE) = '2018-04-09'
	AND v.id_classe IN ('C', 'O')
	AND i.id_local IN (1002, 1004)

	SET @continuar = @@ROWCOUNT
	SET @contador = @contador + 1

	IF @continuar > 0

		BEGIN

			EXEC spu_processa_infracao 
				@id_infracao, 
				@id_usuario, 
				@id_processo, 
				1, 
				@id_imagem, 
				0, 
				0,
				0, 
				0, 
				0, 
				0, 
				0, 
				NULL, 
				0, 
				0 

			EXEC spu_status_infracao 
				@id_infracao 

		END 

END

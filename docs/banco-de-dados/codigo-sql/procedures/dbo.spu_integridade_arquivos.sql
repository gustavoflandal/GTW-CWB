
CREATE PROCEDURE [dbo].[spu_integridade_arquivos]
AS

DECLARE @currLocal INT
DECLARE @currArquivo INT
DECLARE @currData DATETIME

DECLARE @id_local INT
DECLARE @numero_arquivo INT
DECLARE @data DATETIME

SET @currLocal = 0
SET @currArquivo = 0
SET @currData = DATEADD(dd, -20, GetDate())

DELETE 
FROM falha_arquivos_importados with (rowlock)
WHERE CAST(data_arquivo_antes AS DATE) >= CAST(@currData AS DATE)

DECLARE dmy_cursor CURSOR LOCAL FAST_FORWARD 
FOR (
	SELECT 
		id_local, 
		numero_arquivo, 
		data_arquivo
	FROM arquivos_importados (nolock)
	WHERE	numero_arquivo IS NOT NULL
		AND id_local > 0
		AND data_arquivo >= CAST(@currData AS DATE)
	) 
	ORDER BY 
		id_local, 
		data_arquivo, 
		numero_arquivo

OPEN dmy_cursor

FETCH NEXT FROM dmy_cursor 
INTO 
	@id_local, 
	@numero_arquivo, 
	@data

    -- Laço
WHILE @@FETCH_STATUS = 0

	BEGIN

		IF (@currLocal != @id_local)

			BEGIN

				-- Trocou o local
				SET @currLocal = @id_local
				SET @currArquivo = @numero_arquivo
				SET @currData = @data

				FETCH NEXT FROM dmy_cursor 
				INTO 
					@id_local, 
					@numero_arquivo, 
					@data

				CONTINUE

			END
	
		IF (MONTH(@currData) != MONTH(@data))	-- Trocou o mês

			BEGIN

				SET @currLocal = @id_local
				SET @currArquivo = @numero_arquivo
				SET @currData = @data

				FETCH NEXT FROM dmy_cursor 
				INTO 
					@id_local, 
					@numero_arquivo, 
					@data

				CONTINUE

			END   	
	
		IF (@currArquivo + 1 != @numero_arquivo)

			BEGIN

				INSERT INTO falha_arquivos_importados with (rowlock) (
					id_local, 
					numero_arquivo_antes, 
					numero_arquivo_depois,
					data_arquivo_antes, 
					data_arquivo_depois) 
				VALUES (
					@currLocal, 
					@currArquivo,
					@numero_arquivo, 
					@currData, 
					@data)

			END
	
		SET @currLocal = @id_local
		SET @currArquivo = @numero_arquivo
		SET @currData = @data

		FETCH NEXT FROM dmy_cursor 
		INTO 
			@id_local, 
			@numero_arquivo, 
			@data

	END
  
CLOSE dmy_cursor
DEALLOCATE dmy_cursor




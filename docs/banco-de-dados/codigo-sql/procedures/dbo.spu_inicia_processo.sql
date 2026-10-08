CREATE PROCEDURE [dbo].[spu_inicia_processo] AS
	
	DECLARE @id_infracao INT
	declare @id_enquadramento INT
	DECLARE @contador INT = 0
	
	--	PRINT 'Iniciando a [spu_inicia_processo] ... ' + convert(char(23), getdate(), 121)
	DECLARE cursor_infracoes CURSOR LOCAL 
	FOR 
	SELECT TOP 1500 
		id_infracao, 
		id_enquadramento 
	FROM 
		infracao (rowlock) 
	WHERE 
		id_processo IS NULL 
	FOR READ ONLY

	--	PRINT 'Iterando Cursor ... ' + convert(char(23), getdate(), 121)
	OPEN cursor_infracoes

	FETCH NEXT FROM cursor_infracoes
	INTO 
		@id_infracao, 
		@id_enquadramento

	WHILE @@FETCH_STATUS = 0

		BEGIN 

			SET @contador = @contador + 1

			IF (@id_enquadramento = 1)

				BEGIN
					--	PRINT 'Call spu_ajusta_infracao TESTE ['+STR(@id_infracao)+'] ... ' + convert(char(23), getdate(), 121)
					EXEC spu_ajusta_infracao @id_infracao, null
				END

			--	PRINT 'Call spu_status_infracao ['+STR(@id_infracao)+'] ... ' + convert(char(23), getdate(), 121)
			EXEC spu_status_infracao @id_infracao
		
			--	PRINT 'NEXT FROM ... ' + convert(char(23), getdate(), 121)
			FETCH NEXT FROM cursor_infracoes
			INTO 
				@id_infracao, 
				@id_enquadramento

		END

	--	PRINT 'DEALLOCATE ... ' + convert(char(23), getdate(), 121)
	DEALLOCATE cursor_infracoes
	




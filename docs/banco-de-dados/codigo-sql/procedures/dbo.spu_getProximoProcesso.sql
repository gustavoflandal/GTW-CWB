
CREATE PROCEDURE [dbo].[spu_getProximoProcesso]( @id_infracao INT , @id_processo_atual INT , @id_inconsistencia INT)
AS
BEGIN

	DECLARE @id_processo_proximo INT
	DECLARE @sql_criterio_entrada varchar(500)
	
	DECLARE @aceita_consistente INT 
	DECLARE @aceita_inconsistente INT
	
	DECLARE @SQLString nvarchar(MAX)
	DECLARE @msg varchar(150)
	
	DECLARE @processo_ok INT
	DECLARE @infracao_consistente BIT
	
	BEGIN TRY
		
		BEGIN TRANSACTION
		
 	    
			IF @id_inconsistencia IS NULL
		
				BEGIN

					RAISERROR ('ERRO: @id_inconsistencia está NULL', 16, 1)	

				END

			IF @id_inconsistencia = 0 
				SET @infracao_consistente = 1
			ELSE
				SET @infracao_consistente = 0
		
			DECLARE @result INT

			SET @result = NULL
		
			DECLARE cursor_processos_candidatos CURSOR 
			FOR 
			--DECLARE @id_processo_atual INT = 11
			--DECLARE @infracao_consistente INT = 0
			SELECT
				pl.id_processo_destino, p.sql_criterio_entrada ,
				p.aceita_consistente, p.aceita_inconsistente
			FROM processo_ligacao pl (nolock)
				INNER JOIN processo p (nolock)
					on p.id_processo = pl.id_processo_destino
			WHERE ((pl.id_processo_origem = @id_processo_atual) 
				OR ((pl.id_processo_origem IS NULL) 
					AND (@id_processo_atual IS NULL))) 
				AND ((p.aceita_consistente = 1 
					AND @infracao_consistente = 1) 
					OR	(p.aceita_inconsistente = 1 AND @infracao_consistente = 0))			
			ORDER BY 
				CASE 
					WHEN p.sql_criterio_entrada IS NOT NULL 
						THEN 0 
					ELSE 
						1 
				END, 				
				CASE 
					WHEN p.aceita_consistente = 1 and p.aceita_inconsistente = 0 
						THEN 0 					
					WHEN p.aceita_consistente = 0 and p.aceita_inconsistente = 1 
						THEN 1 				
					ELSE 
						2 
				END,
				pl.prioridade,
				pl.id_processo_destino 

			OPEN cursor_processos_candidatos

			FETCH NEXT FROM cursor_processos_candidatos
			INTO @id_processo_proximo, 
				 @sql_criterio_entrada,
				 @aceita_consistente, @aceita_inconsistente

			WHILE @@FETCH_STATUS = 0
			BEGIN 
			
				IF (@sql_criterio_entrada IS NOT NULL) AND ( @sql_criterio_entrada <> '')

					BEGIN

						SET @SQLString =' DECLARE @id_infracao INT = '+ CAST( @id_infracao AS VARCHAR(20) ) + 
										' SELECT @countOUT = count(id_infracao) ' +
										' FROM ' + 
										'	infracao i (NOLOCK)' +
										' WHERE '+
										'	i.id_infracao = @id_infracao ' + 
										' AND (' + @sql_criterio_entrada + ')' 

						SET @SQLString = @SQLString + ' -- testando infração no processo ' + CAST( @id_processo_proximo AS VARCHAR(20) )
				
						EXECUTE sp_executesql
							@SQLString,
							N'@countOUT int OUTPUT', 
							@countOUT=@processo_ok OUTPUT
			
					END

				ELSE

					BEGIN
						SET @processo_ok = 1 			END

					IF @processo_ok > 0

						BEGIN

							SET @result = @id_processo_proximo

							BREAK

						END
			
				FETCH NEXT FROM cursor_processos_candidatos
				INTO @id_processo_proximo, 
					 @sql_criterio_entrada,
					 @aceita_consistente, @aceita_inconsistente
			END
		
			CLOSE cursor_processos_candidatos
			DEALLOCATE cursor_processos_candidatos

			IF (@result IS NULL)

				BEGIN

					SET @msg = 'ERRO: Não foi encontrado o próximo processo para esta infração: ' + STR(@id_infracao) + ', processo_atual: '+ STR(@id_processo_atual)

					RAISERROR (@msg, 16, 1)

				END	

		COMMIT

		PRINT @result 

		RETURN @result
		
	END TRY

	BEGIN CATCH
		If Cursor_Status('local','cursor_processos_candidatos') > 0 

			BEGIN

				CLOSE cursor_processos_candidatos
				DEALLOCATE cursor_processos_candidatos

			END

		IF (@@TRANCOUNT > 0)
			ROLLBACK

		EXEC spu_replica_erro
		
		RETURN 0
		
	END CATCH

END



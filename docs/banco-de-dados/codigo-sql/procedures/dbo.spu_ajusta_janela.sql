
CREATE PROCEDURE [dbo].[spu_ajusta_janela]
	@id_usuario int,
	@id_processo int = NULL,
	@tamanho_janela int = 0,
	@apenas_processadas BIT = 0
AS

	SET NOCOUNT ON

	DECLARE @id_infracao_libera INT = null
		
	BEGIN TRY

		BEGIN TRANSACTION

		IF @tamanho_janela IS NULL 

			BEGIN
				SELECT @tamanho_janela = janela 
					FROM processo (nolock)  
					WHERE id_processo = @id_processo
			END

		DECLARE infracoes_janela 
		CURSOR LOCAL READ_ONLY 
		FOR SELECT id_infracao 
				FROM (	SELECT 
							ROW_NUMBER() OVER (ORDER BY data_infracao_janela DESC, id_janela_seq DESC) AS ROWID, 
							id_infracao 
						FROM 
							infracao_janela (nolock)
						WHERE 	id_usuario = @id_usuario
							AND (processado = 1 OR @apenas_processadas = 0)
							AND (id_processo = @id_processo OR @id_processo IS NULL)
					) as sub 
				WHERE sub.ROWID > @tamanho_janela

		OPEN infracoes_janela
		FETCH NEXT FROM infracoes_janela
		INTO 
			@id_infracao_libera
		
		WHILE @@FETCH_STATUS = 0

			BEGIN

				-- Reposicionando a infração, se ela estiver presa.
				EXEC spu_status_infracao @id_infracao_libera
			
				FETCH NEXT FROM infracoes_janela
				INTO 
					@id_infracao_libera
			
			END

		CLOSE infracoes_janela
		DEALLOCATE infracoes_janela
				
		COMMIT
	
	END TRY

	BEGIN CATCH
		
		If Cursor_Status('local','infracoes_janela') > 0 

			BEGIN
				CLOSE infracoes_janela
				DEALLOCATE infracoes_janela
			END

		IF (@@TRANCOUNT > 0)

				ROLLBACK

		EXEC spu_replica_erro
		
	END CATCH



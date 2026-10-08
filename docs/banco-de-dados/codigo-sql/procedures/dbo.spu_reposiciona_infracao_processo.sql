CREATE PROCEDURE [dbo].[spu_reposiciona_infracao_processo]
	@id_infracao int,
	@novo_id_processo int
AS

BEGIN

	DECLARE @ret int = 1 --1 = OK
	DECLARE @id_infracao_processo_base int = NULL
	DECLARE @msg_error varchar(max)	= NULL
	
	IF (NOT EXISTS (SELECT 
						id_infracao 
					FROM 
						infracao_processo (nolock)
				    WHERE	id_infracao = @id_infracao 
						AND id_processo = @novo_id_processo)
	   )

		BEGIN
		
			DECLARE @nome_processo varchar(max)
		
			SELECT 
				@nome_processo = nome 
			FROM 
				processo (nolock)
			WHERE 
				id_processo = @novo_id_processo
		
			SET @msg_error = 'Não é permitido reposicionar a infração [' + RTRIM(STR(@id_infracao)) + '] para o processo [' + RTRIM(@nome_processo) + '] pois a infração não passou pelo processo.'

			RAISERROR(@msg_error , 16, 1)

			RETURN 0

		END

	IF EXISTS (SELECT id_infracao FROM descarga_infracao WHERE id_infracao = @id_infracao)

		BEGIN

			SET @msg_error = 'A infração [' + RTRIM(STR(@id_infracao)) + '] não pode ser reposicionada, pois já esta em uma DESCARGA.'

			RAISERROR(@msg_error , 16, 1)

			RETURN 0

		END

	BEGIN TRY 
		 
		BEGIN TRANSACTION 
	
			SELECT 
				@id_infracao_processo_base = min(id_infracao_processo) 
			FROM 
				infracao_processo (nolock)
			WHERE	id_infracao = @id_infracao 
				AND (@novo_id_processo IS NULL 
					OR id_processo = @novo_id_processo) 
				AND status_processo = 0 --status_processo = 0, porque deve ser válida.
		
			UPDATE infracao_processo with (rowlock)
			SET status_processo = 2 --Cancelando todos os registros após o registro base, inclusive.
			WHERE	id_infracao = @id_infracao 
				AND id_infracao_processo >= @id_infracao_processo_base
				AND status_processo = 0
		
			--Limpa as concluídas porque a status vai cuidar dela novamente...
			DELETE 
			FROM infracao_processo_concluido with (rowlock)
			WHERE id_infracao = @id_infracao

			--Limpa as obliterações ao reposicionar...
			DELETE FROM infracao_obliteracao WHERE id_infracao = @id_infracao
		
			-- retira infração da espera
			UPDATE infracao with (rowlock)
			SET espera = NULL, 
				placa = NULL, 
				id_inconsistencia = 0, 
				id_usuario_final = NULL
			WHERE id_infracao = @id_infracao 		 

			EXEC spu_status_infracao @id_infracao
		
		COMMIT 

		RETURN @ret
		
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
 
		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH		
		
END

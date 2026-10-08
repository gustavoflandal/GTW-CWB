
CREATE PROCEDURE [dbo].[spu_busca_multiplas_infracao_contestacao] 
	@id_usuario int, 
	@id_processo int,
	@id_remessa int,
	@id_enquadramento int, 
	@consistencia bit, 
	@espera bit, 
	@periodo_ini datetime, 
	@periodo_fim datetime,
	@qtde_infracao INT = 0,
	@amostra BIT = 0
AS 

	SET NOCOUNT ON 
	 
	IF @qtde_infracao = 0
		SET @qtde_infracao = 30
	 
	DECLARE @passeiaqui NVARCHAR(255) = 'inicio'
	 
	DECLARE @infracoes_escolhida TABLE (id_infracao INT)
	DECLARE @strLock NVARCHAR(255) = '[spu_busca_infracao_processamento]' + '[' + RTRIM(LTRIM(STR(@id_processo))) + ']' -- lock isolado por processo
	 
	BEGIN TRY 

		BEGIN TRANSACTION 
		 
			EXEC sp_getapplock @Resource = @strLock, @LockMode = 'Exclusive'	 
		
			SET @passeiaqui = 'SELECT 1'
			SELECT
				@qtde_infracao = @qtde_infracao - COUNT(id_infracao) 
			FROM 
				infracao_janela (nolock) 
			WHERE	processado = 0
				AND id_processo = @id_processo AND id_usuario = @id_usuario
			

			IF @qtde_infracao > 0

				BEGIN
		
					SET @passeiaqui = 'INSERT 1'

					IF @id_processo = 90
					BEGIN

					INSERT INTO @infracoes_escolhida
						SELECT id_infracao 
						FROM (	SELECT TOP 30 
									ROW_NUMBER() OVER ( ORDER BY data ) as ROWID,
									id_infracao
								FROM 
									fcn_InfracaoDisponivelContestacao(1) 
								WHERE (@periodo_ini IS NULL 
										OR @periodo_fim IS NULL 
										OR data between @periodo_ini and @periodo_fim) 
							) AS sub 
						WHERE ROWID <= @qtde_infracao
						ORDER BY ROWID

						END


					IF @id_processo = 91
					BEGIN

					INSERT INTO @infracoes_escolhida
						SELECT id_infracao 
						FROM (	SELECT TOP 30 
									ROW_NUMBER() OVER ( ORDER BY data ) as ROWID,
									id_infracao
								FROM 
									fcn_InfracaoDisponivelContestacao(2) 
								WHERE (@periodo_ini IS NULL 
										OR @periodo_fim IS NULL 
										OR data between @periodo_ini and @periodo_fim) 
							) AS sub 
						WHERE ROWID <= @qtde_infracao
						ORDER BY ROWID

						END			

					SET @passeiaqui = 'INSERT 2'
					INSERT INTO infracao_janela with (rowlock)
						(id_usuario, id_processo, id_infracao) 
					SELECT 
						@id_usuario, 
						@id_processo, 
						ie.id_infracao		 
					FROM @infracoes_escolhida ie
						LEFT JOIN infracao_janela ij (nolock) 
							ON ij.id_infracao = ie.id_infracao
					WHERE 
						ij.id_infracao IS NULL

						-- Coloca a infração para o usuário 
					UPDATE infracao with (rowlock)
						SET id_usuario_atual=@id_usuario  
						WHERE id_infracao in (	SELECT id_infracao 
													FROM @infracoes_escolhida)
		
				END
		 
		COMMIT 
 
        -- Ajusta a janela
		EXEC spu_ajusta_janela @id_usuario, @id_processo, NULL, 1 

		SET @passeiaqui = 'SELECT 2'
		-- return dataset
		SELECT 
			id_infracao 
		FROM 
			infracao_janela (nolock)
		WHERE 	id_processo = @id_processo
			AND id_usuario = @id_usuario
		ORDER BY 
			id_janela_seq			
				 
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
			 
		--EXEC spu_replica_erro 
		------- spu_replica_erro ---------------------------------------------
			DECLARE @ErrorMessage NVARCHAR(4000)
			DECLARE @ErrorSeverity INT
			DECLARE @ErrorState INT

			SELECT 
				@ErrorMessage = ERROR_MESSAGE() + 
					'id_processo=['+ STR(@id_processo) +']' +
					'id_usuario=['+ STR(@id_usuario) +']' +
					'local=['+ @passeiaqui +']',
				@ErrorSeverity = ERROR_SEVERITY(),
				@ErrorState = ERROR_STATE()

			RAISERROR (@ErrorMessage, -- Message text.
					   @ErrorSeverity, -- Severity.
					   @ErrorState -- State.
					   )		
		------- spu_replica_erro ---------------------------------------------
			 
		RETURN 0 
		 
	END CATCH



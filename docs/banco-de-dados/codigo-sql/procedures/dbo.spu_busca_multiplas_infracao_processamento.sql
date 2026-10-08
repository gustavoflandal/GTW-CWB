CREATE PROCEDURE [dbo].[spu_busca_multiplas_infracao_processamento]
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
					INSERT INTO @infracoes_escolhida
						SELECT id_infracao 
						FROM (	SELECT TOP 30 
									ROW_NUMBER() OVER ( ORDER BY data ) as ROWID,
									id_infracao
								FROM 
									fcn_InfracaoDisponivelUsuario(@id_processo, @id_usuario) 
								WHERE id_usuario_atual IS NULL 
									AND (@id_remessa IS NULL 
										OR id_remessa = @id_remessa) 
									AND (@amostra = 0 
										OR id_infracao_amostra IS NOT NULL) 
									AND (@id_enquadramento IS NULL 
										OR id_enquadramento = @id_enquadramento) 
									AND (@consistencia IS NULL 
										OR (@consistencia = 1 AND id_inconsistencia = 0) 
										OR (@consistencia = 0 AND id_inconsistencia > 0)) 
									AND (espera = @espera 
										OR (@espera IS NULL OR @espera = 0) AND (espera IS NULL OR espera = 0))
									AND (@periodo_ini IS NULL 
										OR @periodo_fim IS NULL 
										OR data between @periodo_ini and @periodo_fim) 
							) AS sub 
						WHERE ROWID <= @qtde_infracao
						ORDER BY ROWID

					--DECLARE @id_infracao_PK INT = NULL
					--DECLARE @id_usuario_PK INT = NULL
					--DECLARE @id_processo_PK INT = NULL
			
					--SELECT TOP 1
					--	@id_infracao_PK = ij.id_infracao,
					--	@id_usuario_PK = ij.id_usuario,
					--	@id_processo_PK = ij.id_processo
					--FROM 
					--	@infracoes_escolhida ie
					--	JOIN infracao_janela ij ON ij.id_infracao = ie.id_infracao
					--IF (@id_infracao_PK IS NOT NULL)
					--BEGIN	
				
					--	DECLARE @msg_error VARCHAR(4000) = 'ERRO de PK na infracao_janela ' +
					--		'id_infracaoPK=[' + RTRIM(STR(@id_infracao_PK)) + '] ' + 
					--		'id_usuarioPK=[' + RTRIM(STR(@id_usuario_PK)) + '] ' + 
					--		'id_processoPK=[' + RTRIM(STR(@id_processo_PK)) + '] ' +
					--		'id_usuario=[' + RTRIM(STR(@id_usuario)) + '] ' + 
					--		'id_processo=[' + RTRIM(STR(@id_processo)) + '] '

					--	RAISERROR(@msg_error , 16, 1)
				
					--END				

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






CREATE PROCEDURE [dbo].[spu_agendar_processamento]
	@id_usuario INT,
	@id_processo INT,
	@id_remessa INT,
	@id_enquadramento INT,
	@consistencia BIT,
	@espera BIT,
	@periodo_ini DATETIME,
	@periodo_fim DATETIME,
	@id_inconsistencia INT = NULL

AS 

DECLARE @infracoes_agendar TABLE
(
	id_infracao INT,
	id_inconsistencia INT
)

SET NOCOUNT ON 

DECLARE @data_solicitacao DATETIME = GETDATE()
DECLARE @id_infracao_set INT = NULL
DECLARE @id_inconsistencia_set INT = NULL
DECLARE @qtdAgendada INT = NULL
DECLARE @qtdTotalAgendada INT = 0
DECLARE @msg VARCHAR(1000) = NULL
	
BEGIN TRY 

	-- Caso especial, pois agora o parametro vem do Java como 'trulean'
	if (@espera IS NOT NULL AND @espera = 0)
		SET @espera = NULL
 
	WHILE (@qtdAgendada > 0 OR @qtdAgendada IS NULL)

		BEGIN

			SET @msg = 'Qtd. Agendada: ' + STR(@qtdTotalAgendada) -- + ' - ' + convert(char(23), getdate(), 121)
			RAISERROR(@msg, 1, 1) --Esta fazendo na interface.
	/*
			BEGIN TRANSACTION 

			DECLARE @strLock NVARCHAR(255) = '[spu_busca_infracao_processamento]' + '[' + RTRIM(LTRIM(STR(@id_processo))) + ']' -- lock isolado por processo
			EXEC sp_getapplock @Resource = @strLock, @LockMode = 'Exclusive'	 
	*/		
			--Limpando a tabela virtual antes...
			DELETE FROM @infracoes_agendar
		
			-- Coloca as infrações disponíveis em uma tabela temporária
			INSERT INTO @infracoes_agendar
			SELECT 	TOP(100)
				idu.id_infracao, COALESCE(@id_inconsistencia, idu.id_inconsistencia)
			FROM fcn_InfracaoDisponivelUsuario(@id_processo, @id_usuario) idu
				LEFT JOIN agendamento_processamento ap (NOLOCK) 
					ON ap.id_infracao = idu.id_infracao
			WHERE   (id_usuario_atual IS NULL OR id_usuario_atual = @id_usuario)
				AND (@id_remessa IS NULL OR id_remessa = @id_remessa) 
				AND (@id_enquadramento IS NULL OR id_enquadramento = @id_enquadramento) 
				AND (@consistencia IS NULL 
					OR (@consistencia = 1 AND idu.id_inconsistencia = 0) 
					OR (@consistencia = 0 AND idu.id_inconsistencia > 0)) 
				AND (espera = @espera OR (@espera IS NULL AND espera IS NULL)) 
				AND (@periodo_ini IS NULL 
					OR @periodo_fim IS NULL 
					OR data between @periodo_ini and @periodo_fim)
				AND ap.id_infracao IS NULL

			SET @qtdAgendada = (SELECT COUNT(*) 
									FROM @infracoes_agendar)

			DECLARE cur_infracoes_agendar 
			CURSOR LOCAL READ_ONLY 
			FOR SELECT 
					id_infracao, 
					id_inconsistencia 
				FROM @infracoes_agendar
			
			OPEN cur_infracoes_agendar
			FETCH NEXT FROM cur_infracoes_agendar
			INTO 
				@id_infracao_set, 
				@id_inconsistencia_set
		
			WHILE @@FETCH_STATUS = 0
		
				BEGIN

		--			BEGIN TRANSACTION 

						INSERT INTO agendamento_processamento with (rowlock) 
							(id_infracao, id_processo, data_requisicao, id_usuario,	status_agendamento, id_inconsistencia)
						VALUES (@id_infracao_set, @id_processo, @data_solicitacao, @id_usuario, 0, @id_inconsistencia_set)

		--			COMMIT
			
					FETCH NEXT FROM cur_infracoes_agendar
						INTO 
							@id_infracao_set, 
							@id_inconsistencia_set 

				END
		
			CLOSE cur_infracoes_agendar
			DEALLOCATE cur_infracoes_agendar
		
			-- Coloca as infrações no agendamento
	--		COMMIT

			SET @qtdTotalAgendada = @qtdTotalAgendada + @qtdAgendada
	--		WAITFOR DELAY '00:00:00.100'

		END	

	RETURN @qtdTotalAgendada
	
END TRY 

BEGIN CATCH 

	IF (@@TRANCOUNT > 0) 
		ROLLBACK 
		 
	EXEC spu_replica_erro 
	 
	RETURN @qtdTotalAgendada
	 
END CATCH




CREATE PROCEDURE [dbo].[spu_valida_infracao_processo] @id_infracao_processo INT 
AS

BEGIN
--  DECLARE @id_infracao_processo INT
	DECLARE @ret INT = 1
	DECLARE @id_infracao int = NULL
	DECLARE @id_processo int = NULL
	DECLARE @id_inconsistencia int = NULL
	DECLARE @id_inconsistencia_inf int = NULL

	DECLARE @retorno TABLE 
	( 
		mensagem varchar(1000) 
	)

	SET NOCOUNT ON

	BEGIN TRY 
		 
		SELECT 
			@id_infracao=ip.id_infracao, 
			@id_processo=ip.id_processo,
			@id_inconsistencia=ip.id_inconsistencia, 
			@id_inconsistencia_inf = i.id_inconsistencia
		FROM infracao_processo ip (nolock)
			INNER JOIN infracao i (nolock)
				ON i.id_infracao = ip.id_infracao
		where 
			ip.id_infracao_processo=@id_infracao_processo

		--Cria tabela temporaria de infracao_completa...
		SELECT 
			* 
		INTO 
			#infracao_completa_temp 
		FROM 
			infracao_completa (nolock)
		WHERE 
			id_infracao = @id_infracao

		DECLARE @id_alerta int
		DECLARE @nome_alerta nchar(50)
		DECLARE @sql_criterio varchar(1000)
		DECLARE @bloqueante bit
		DECLARE @consistente bit = 0
		DECLARE @inconsistente bit = 0
		DECLARE @mensagem varchar(150)

		DECLARE @SQLString nvarchar(MAX)
		
		DECLARE @alerta_aplicavel INT = NULL
		DECLARE @existe_bloqueante BIT = 0 -- inicia como não bloqueante
		
		DECLARE @id_filtro INT = NULL
		DECLARE @espera BIT

		-- Buscando os dados do processo para tentar ajustar a infração.
		DECLARE cursor_alertas CURSOR LOCAL 
		FOR 
		-- DECLARE @id_inconsistencia INT = 0, @id_processo INT = 91
		SELECT 
			id_alerta,
			nome_alerta,
			sql_criterio,
			bloqueante,
			mensagem,
			consistente,
			inconsistente
		FROM 
			alerta (nolock)
		WHERE @id_processo != 90 AND @id_processo != 91 AND (	(consistente = 1 AND @id_inconsistencia = 0) 
			OR	(inconsistente = 1 AND @id_inconsistencia > 0) 
			OR	@id_inconsistencia is NULL )
		ORDER BY id_alerta 
		FOR READ ONLY
			
		OPEN cursor_alertas

		
		FETCH NEXT FROM cursor_alertas
		INTO   
			@id_alerta,
			@nome_alerta,
			@sql_criterio,
			@bloqueante,
			@mensagem,
			@consistente,
			@inconsistente
			
		WHILE @@FETCH_STATUS = 0

			BEGIN 
			
			
				SET @SQLString = 	N' DECLARE @id_infracao INT = ' + STR(@id_infracao) + ' ' +
									N' DECLARE @id_infracao_processo INT = ' + STR(@id_infracao_processo) + ' ' +
									N' SELECT @countOUT = count(id_infracao) ' + 
									N' FROM #infracao_completa_temp i WHERE id_infracao = @id_infracao ' +
									N' AND ( ' + @sql_criterio + ' )' --Critério configurado.		
			
				EXECUTE sp_executesql
					@SQLString,
					N'@countOUT int OUTPUT', 
					@countOUT=@alerta_aplicavel OUTPUT
			
				IF @alerta_aplicavel > 0

					BEGIN

						INSERT INTO @retorno 
						VALUES ( @mensagem )
						
						IF( @bloqueante = 1)
							SET @existe_bloqueante = 1

					END
			
				FETCH NEXT FROM cursor_alertas
				INTO   
					@id_alerta,
					@nome_alerta,
					@sql_criterio,
					@bloqueante,
					@mensagem,
					@consistente,
					@inconsistente
				  
			END
		
		CLOSE cursor_alertas
		DEALLOCATE cursor_alertas
		
		IF (@id_inconsistencia_inf > 0 AND @id_inconsistencia = 0) --Teste de filtros somente para troca de inconsistente para consistente.
			EXEC spu_verificar_filtros 
					@id_infracao , 
					NULL, 
					@id_filtro OUTPUT, 
					@id_inconsistencia OUTPUT , 
					@espera OUTPUT
		
		--TESTE DO AUTO-FILTRO:
		IF (@id_filtro IS NOT NULL)

			BEGIN

				SET @mensagem = 'AUTO-FILTRO'+CHAR(10)+'Esta infração se encaixa com o seguinte filtro: '	
				SET @mensagem = @mensagem + CHAR(10) + RTRIM((	SELECT 
																	nome_filtro 
																FROM 
																	filtro (nolock)
																WHERE 
																	id_filtro = @id_filtro))

				IF (@id_inconsistencia IS NOT NULL) 

					BEGIN
						
						SET @mensagem = @mensagem + CHAR(10) + 'Recomenda-se a seguinte inconsistência: '
						SET @mensagem = @mensagem + CHAR(10) + +RTRIM(@id_inconsistencia) + ' - ' + 
										RTRIM((	SELECT 
													descricao 
												FROM 
													inconsistencia (nolock)
												WHERE 
													id_inconsistencia = @id_inconsistencia))

					END

				INSERT INTO @retorno 
				VALUES ( @mensagem )
				
				SET @existe_bloqueante = 1

			END
		
		SELECT 
			mensagem 
		FROM 
			@retorno --Retorna o result set para o java.
		
		DROP TABLE #infracao_completa_temp		

		IF @existe_bloqueante = 1
			RETURN 0
		ELSE
			RETURN 1
		 
	END TRY 

	BEGIN CATCH 

		DROP TABLE #infracao_completa_temp		

		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH	

END

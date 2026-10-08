CREATE PROCEDURE [dbo].[spu_status_infracao]  
	@id_infracao INT  
AS 
	 
	SET NOCOUNT ON 
 
	DECLARE @ultimo_processo_concluido INT 
	SET @ultimo_processo_concluido = NULL 
 
	DECLARE @id_processo_atual INT 
	SET @id_processo_atual = NULL 
	 
	DECLARE @processo_atual_ok INT 
	SET @processo_atual_ok = 1 	 

	DECLARE @placa CHAR(7) = null 
 
	DECLARE @placa_escolhida CHAR(7) = null 
	 
	DECLARE @id_inconsistencia INT 
	SET @id_inconsistencia = 0 	 

	DECLARE @id_infracao_processo INT = 0 
	DECLARE @id_imagem INT = 0 
	 
	DECLARE @id_inconsistencia_final INT 
	SET @id_inconsistencia_final = 0  

	BEGIN TRY 
		 
		BEGIN TRANSACTION 
		
			DECLARE @lock_name VARCHAR(1000) 
		 
			SET @lock_name = '[spu_status_infracao'+STR(@id_infracao)+']' 
			EXEC sp_getapplock @Resource = @lock_name, @LockMode = 'Exclusive'	 
 
 			SELECT TOP 1  
				@id_processo_atual = id_processo, 
				@id_inconsistencia_final = id_inconsistencia 
			FROM 
				infracao_processo_concluido (nolock)
			WHERE 
				id_infracao = @id_infracao  
			ORDER BY 
				id_infracao_processo_concluido DESC 
		 
		 	WHILE (@processo_atual_ok = 1) 
		
				BEGIN 

					SET @processo_atual_ok = NULL 
					SET @ultimo_processo_concluido = @id_processo_atual 
 
					EXEC @id_processo_atual = dbo.spu_getProximoProcesso 
													@id_infracao , 
													@ultimo_processo_concluido, 
													@id_inconsistencia_final 

					/************************************************************************************************ 
					-- verificar se a infração esta concluída para o processo atual  
					*************************************************************************************************/ 
					SELECT TOP 1 
						@processo_atual_ok = 
						CASE  
							WHEN sub.numero_iteracoes >= p.numero_iteracoes_consistentes AND sub.id_inconsistencia = 0 
								THEN 1 
							WHEN sub.numero_iteracoes >= p.numero_iteracoes_inconsistentes AND sub.id_inconsistencia > 0 
								THEN 1 
							ELSE 
								0 
						END, 
						@id_infracao_processo = sub.id_infracao_processo, 
						@id_inconsistencia = sub.id_inconsistencia, 
						@placa = sub.placa 
					FROM (	SELECT  
								COUNT(*) numero_iteracoes, 
								MAX(tp.id_infracao_processo) id_infracao_processo, 
								tp.id_inconsistencia, 
								tp.placa
							FROM (	SELECT 
										ipd.placa, 
										ip.id_inconsistencia, 
										ip.id_infracao_processo 
									FROM infracao_processo ip (nolock)
										LEFT JOIN infracao_processo_digitacao ipd (nolock) 
											ON ipd.id_infracao_processo = ip.id_infracao_processo 
										INNER JOIN infracao_processo_usuario ipu (nolock) 
											ON	ipu.id_infracao_processo = ip.id_infracao_processo 
											AND ipu.id_processo = @id_processo_atual 
											AND ipu.id_infracao = @id_infracao 
											AND ipu.id_usuario = ip.id_usuario 
									WHERE	ip.id_infracao = @id_infracao
										AND ip.id_processo = @id_processo_atual  
										AND ip.status_processo = 0 						
										AND ip.id_inconsistencia is not null 					
									UNION
									SELECT 
										vei.placa, 
										0 as id_inconsistencia, 
										0 as id_infracao_processo 
									FROM veiculo vei (nolock)
										INNER JOIN infracao i (nolock) 
											ON i.id_veiculo = vei.id_veiculo
									WHERE EXISTS (	SELECT 
														id_processo 
													FROM 
														processo p (nolock)
													WHERE	p.id_processo = @id_processo_atual 
														AND p.numero_iteracoes_consistentes > 1)
										AND i.id_infracao = @id_infracao
										AND vei.placa IS NOT NULL
								) as tp
							GROUP BY  
								tp.id_inconsistencia, 
								tp.placa
						) AS sub 
						INNER JOIN processo p (NOLOCK) 
							ON p.id_processo = @id_processo_atual 
					WHERE  
						p.id_processo = @id_processo_atual 	 
					ORDER BY 
						1 DESC, 
						sub.numero_iteracoes DESC 
					/************************************************************************************************/ 
 
					IF @processo_atual_ok = 1 

						BEGIN 
	 
							SET @placa_escolhida = COALESCE(@placa, @placa_escolhida) 				
							SET @id_inconsistencia_final = @id_inconsistencia 
										 
							EXEC @id_imagem = spu_ajusta_infracao @id_infracao, @id_infracao_processo 
							
							IF (NOT @id_imagem > 0) 
								SET @id_imagem = NULL 
				 
							IF EXISTS(	SELECT 
											id_infracao_processo_concluido
										FROM 
											infracao_processo_concluido (nolock) 
										WHERE	id_infracao = @id_infracao 
											AND id_processo = @id_processo_atual) 

								BEGIN 

									DECLARE @mens varchar(1000) = 'Já tem essa infração na processo concluído!!!' 

									SET @mens = @mens + STR(@id_infracao) 
									SET @mens = @mens + ', id_processo: '+STR(@id_processo_atual) 

									RAISERROR(@mens, 16, 1) 
								END 

							INSERT INTO infracao_processo_concluido with (rowlock) (
								id_infracao,
								id_processo,
								data_conclusao,
								id_inconsistencia,
								id_imagem) 
							VALUES (
								@id_infracao, 
								@id_processo_atual, 
								GETDATE(),
								@id_inconsistencia, 
								@id_imagem)
								 
						END 
 
				END 
 
	--		PRINT STR(@id_infracao)+', '+STR(@id_processo_atual)+', '+STR(@ultimo_processo_concluido) 		 

			DELETE 
			FROM infracao_janela with (rowlock)
			WHERE id_infracao = @id_infracao 

			UPDATE infracao with (rowlock)
			SET	id_processo = @id_processo_atual,  
				id_processo_concluido = @ultimo_processo_concluido, 
				placa = COALESCE(@placa_escolhida, placa), 
				id_inconsistencia = @id_inconsistencia_final, 
				id_usuario_atual = NULL 
			WHERE id_infracao = @id_infracao		 
		 
		COMMIT 
		 
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
 
		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH

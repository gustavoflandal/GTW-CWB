CREATE PROCEDURE [dbo].[spu_ajusta_infracao] 
( 
	@id_infracao INT, 
	@id_infracao_processo INT
)
AS

	DECLARE @id_imagem_obj INT = NULL
	DECLARE @id_imagem_pan INT = NULL
	DECLARE @id_imagem_pan2 INT = NULL
	DECLARE @id_usuario INT = NULL

 	BEGIN TRY
		
		BEGIN TRANSACTION

			SELECT
				@id_imagem_obj = id_imagem,
				@id_usuario = id_usuario
			FROM infracao_processo (nolock) 
			WHERE	id_infracao_processo = @id_infracao_processo
				AND id_imagem IS NOT NULL

			IF ( @id_imagem_obj IS NULL AND NOT EXISTS (SELECT id_infracao 
															FROM infracao_imagem (nolock) 
															where id_infracao = @id_infracao))

				BEGIN

					-- Busca a imagem OBJETIVA
					SELECT TOP 1
						@id_imagem_obj = vi.id_imagem
					FROM veiculo_imagem vi (nolock) 
						INNER JOIN imagem_info img (nolock)  
							ON img.id_imagem = vi.id_imagem
						INNER JOIN tipo_imagem ti (nolock)  
							ON ti.id_tipo_imagem = img.id_tipo_imagem
						INNER JOIN infracao i (nolock)  
							ON i.id_veiculo = vi.id_veiculo
					WHERE	ti.nome = 'OBJ'
						AND i.id_infracao = @id_infracao
					ORDER BY
						ti.numero ASC

				END
		
			-- Tenta buscar a primeira panorâmica (baseado no enquadramento)
			SELECT TOP 1
				@id_imagem_pan = ipan.id_imagem
			FROM infracao i (nolock) 
				INNER JOIN infracao_imagem ii (nolock)  
					ON ii.id_infracao = i.id_infracao
				INNER JOIN enquadramento e (nolock) 
					ON e.id_enquadramento = i.id_enquadramento
				LEFT JOIN tipo_imagem ti (nolock) 
					ON ti.id_tipo_imagem = e.id_tipo_imagem_pan
				LEFT JOIN imagem_info ipan (nolock) 
					ON ipan.id_tipo_imagem = ti.id_tipo_imagem
				AND ipan.id_imagem in (	SELECT 
											id_imagem 
										FROM 
											veiculo_imagem (nolock)  
										WHERE 
											id_veiculo = i.id_veiculo)
			WHERE i.id_infracao = @id_infracao
					
			-- Se não deu, pega a primeira panorâmica baseado na ordem (primeira panorâmica disponível)
			IF (@id_imagem_pan IS NULL)

			BEGIN

				SELECT TOP 1
					@id_imagem_pan = vi.id_imagem
				FROM veiculo_imagem vi (nolock) 
					INNER JOIN imagem_info img (nolock) 
						ON img.id_imagem = vi.id_imagem
					INNER JOIN tipo_imagem ti (nolock) 
						ON ti.id_tipo_imagem = img.id_tipo_imagem 
					INNER JOIN infracao i (nolock)  
						ON i.id_veiculo = vi.id_veiculo
				WHERE	ti.nome = 'PAN' 
					AND i.id_infracao = @id_infracao 
				ORDER BY 
					ti.numero ASC

			END					

			-- Se temos a primeira panorâmica...
			IF (@id_imagem_pan IS NOT NULL)

				BEGIN

					-- Tenta pegar a segunda panorâmica (baseado no enquadramento)
					SELECT TOP 1
						@id_imagem_pan2 = ipan.id_imagem
					FROM infracao i (nolock) 
						JOIN infracao_imagem ii (nolock)  
							ON ii.id_infracao = i.id_infracao
						JOIN enquadramento e (nolock)  
							ON e.id_enquadramento = i.id_enquadramento
						LEFT JOIN tipo_imagem ti (nolock)  
							ON ti.id_tipo_imagem = e.id_tipo_imagem_pan2
						LEFT JOIN imagem_info ipan (nolock)  
							ON ipan.id_tipo_imagem = ti.id_tipo_imagem
						AND ipan.id_imagem in (	SELECT 
													id_imagem 
												FROM 
													veiculo_imagem (nolock)  
												WHERE 
													id_veiculo = i.id_veiculo)
					WHERE i.id_infracao = @id_infracao			
			
					IF (@id_imagem_pan2 IS NULL)

						BEGIN

							-- Senão, tenta pegar a segunda panorâmica baseado na ordem (última panorâmica disponível)
							SELECT TOP 1
								@id_imagem_pan2 = vi.id_imagem
							FROM veiculo_imagem vi (nolock) 
								INNER JOIN imagem_info img (nolock)  
									ON img.id_imagem = vi.id_imagem
								INNER JOIN tipo_imagem ti (nolock)  
									ON ti.id_tipo_imagem = img.id_tipo_imagem
								INNER JOIN infracao i (nolock)  
									ON i.id_veiculo = vi.id_veiculo
							WHERE	ti.nome = 'PAN' 
								AND i.id_infracao = @id_infracao 
								AND vi.id_imagem != @id_imagem_pan
							ORDER BY
								ti.numero DESC

						END		
					
				END
		
			-- Se temos a imagem objetiva, então...
			IF (@id_imagem_obj IS NOT NULL)

				BEGIN
			
					-- Tenta fazer um UPDATE
					UPDATE infracao_imagem with (rowlock)
					SET	id_imagem_obj = @id_imagem_obj,
						id_imagem_pan = @id_imagem_pan,
						id_imagem_pan2 = @id_imagem_pan2
					WHERE id_infracao = @id_infracao
		
					-- Se não fez o UPDATE, então precisa fazer um INSERT
					IF @@ROWCOUNT = 0

						BEGIN
							INSERT INTO infracao_imagem with (rowlock)
								(id_infracao, id_imagem_obj, id_imagem_pan, id_imagem_pan2)
							VALUES(@id_infracao, @id_imagem_obj, @id_imagem_pan, @id_imagem_pan2)
						END

				END

			-- Se temos o id do usuário, então ...
			IF (@id_usuario IS NOT NULL)
			
				BEGIN

					DELETE 
						FROM infracao_obliteracao with (rowlock)
						WHERE id_infracao = @id_infracao
	
					IF (EXISTS (SELECT id_infracao_processo 
									FROM infracao_processo_obliteracao (nolock) 
									WHERE id_infracao_processo = @id_infracao_processo))

						BEGIN

							INSERT INTO infracao_obliteracao with (rowlock)
								(id_infracao, id_imagem, sequencia_obliteracao, x, y, largura, altura)
								SELECT 
									@id_infracao, 
									id_imagem, 
									sequencia_obliteracao, 
									x, 
									y, 
									largura, 
									altura 
								FROM 
									infracao_processo_obliteracao (nolock) 
								WHERE 
									id_infracao_processo = @id_infracao_processo

						END

				
					UPDATE infracao with (rowlock)
						SET id_usuario_final = @id_usuario 
						WHERE id_infracao = @id_infracao

				END
			
			-- Realiza o commit
			COMMIT

			IF (@id_imagem_obj IS NULL)
				RETURN 0
			ELSE
				RETURN @id_imagem_obj

		
	END TRY
	BEGIN CATCH

		IF (@@TRANCOUNT > 0)
			ROLLBACK

		EXEC spu_replica_erro
		
	END CATCH





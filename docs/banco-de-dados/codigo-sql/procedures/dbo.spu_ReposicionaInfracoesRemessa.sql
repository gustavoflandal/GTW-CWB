


CREATE PROCEDURE [dbo].[spu_ReposicionaInfracoesRemessa]
	@id_processo_ant INT, 
	@id_processo INT,
	@id_remessa INT  
AS
BEGIN

	SET NOCOUNT ON

	BEGIN TRY

		BEGIN TRAN

			INSERT INTO infracao_processo with (rowlock)
			SELECT 
				GETDATE() AS data, 
				0 AS tempo, 
				infracao.id_infracao, 
				infracao.id_processo, 
				infracao.id_inconsistencia, 
				infracao_imagem.id_imagem_obj AS id_imagem, 
				remessa.id_usuario, 
				0 AS status_processo, 
				0 AS tempo_cliente,
				null  
			FROM infracao (nolock) 
				INNER JOIN infracao_imagem	(nolock) 
					ON infracao.id_infracao = infracao_imagem.id_infracao 
				INNER JOIN infracao_remessa	(nolock) 
					ON infracao.id_infracao = infracao_remessa.id_infracao 
				INNER JOIN remessa (nolock) 
					ON remessa.id_remessa = infracao_remessa.id_remessa 
			WHERE	(@id_remessa = 0 OR remessa.id_remessa = @id_remessa) 
				AND infracao.id_processo = @id_processo_ant 

			PRINT @@ROWCOUNT 

			UPDATE infracao 
			SET infracao.id_processo = @id_processo, 
				infracao.id_processo_concluido = @id_processo_ant, 
				infracao.id_usuario_final = remessa.id_usuario  
			FROM infracao with (rowlock) 
				INNER JOIN infracao_remessa (nolock) 
					ON infracao.id_infracao = infracao_remessa.id_infracao 
				INNER JOIN remessa (nolock) 
					ON remessa.id_remessa = infracao_remessa.id_remessa 
			WHERE (@id_remessa = 0 OR remessa.id_remessa = @id_remessa) 
				AND infracao.id_processo = @id_processo_ant 

			PRINT @@ROWCOUNT 

		COMMIT

	END TRY

	BEGIN CATCH
	
		IF @@TRANCOUNT > 0 
			ROLLBACK 

	END CATCH

END


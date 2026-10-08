CREATE PROCEDURE [dbo].[spu_reposiciona_lote_reprovado] (@id_remessa INT)
AS

	DECLARE @retorno INT = 0, @id_infracao INT = NULL, @id_processo INT = 1 --Triagem

 	BEGIN TRY
		
		DECLARE infracoes_cursor CURSOR FOR 
		SELECT i.id_infracao
		FROM   infracao i (NOLOCK)
			   INNER JOIN infracao_remessa ir (NOLOCK)
					ON  ir.id_infracao = i.id_infracao
			   INNER JOIN remessa r (NOLOCK)
					ON  r.id_remessa = ir.id_remessa
			   INNER JOIN lote_reprovado lr (NOLOCK)
					ON  lr.id_remessa = r.id_remessa
		WHERE  r.id_remessa = @id_remessa
			   AND lr.ativo = 1
		GROUP BY
			   i.id_infracao
		ORDER BY
			   i.id_infracao;

		OPEN infracoes_cursor

		FETCH NEXT FROM infracoes_cursor 
		INTO @id_infracao

		WHILE @@FETCH_STATUS = 0
		BEGIN

			EXEC spu_reposiciona_infracao_processo @id_infracao, @id_processo

			SET @retorno = @retorno + 1
	    
			FETCH NEXT FROM infracoes_cursor 
			INTO @id_infracao

		END 
		CLOSE infracoes_cursor;
		DEALLOCATE infracoes_cursor;


		--BEGIN TRAN

		--DELETE FROM infracao_obliteracao WHERE id_infracao IN (SELECT ir.id_infracao FROM infracao_remessa ir (NOLOCK) WHERE ir.id_remessa = @id_remessa)
		
		--DELETE FROM infracao_processo_obliteracao WHERE id_infracao_processo IN (
		--				SELECT ip.id_infracao_processo
		--				FROM   infracao_processo ip (NOLOCK)
		--				WHERE  ip.id_infracao IN (
		--								SELECT ir.id_infracao
		--								FROM   infracao_remessa ir (NOLOCK)
		--								WHERE  ir.id_remessa = @id_remessa
		--				)
		--)

		--COMMIT;

	END TRY
	BEGIN CATCH

		--IF (@@TRANCOUNT > 0)
		--BEGIN
		--	ROLLBACK;
		--END

		EXEC spu_replica_erro
		
	END CATCH

	RETURN @retorno

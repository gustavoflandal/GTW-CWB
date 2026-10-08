CREATE PROCEDURE [dbo].[spu_remover_imagens_pan_velocidade]
AS
	IF OBJECT_ID('tempdb..#temp_imagem_pan_remover') IS NOT NULL
	BEGIN
		DROP TABLE #temp_imagem_pan_remover
	END

	SELECT inf.data, inf.id_enquadramento, inf.id_infracao, vi.id_veiculo, i.id_imagem, i.indice_imagem, tp.*
	INTO   #temp_imagem_pan_remover
	FROM   imagem i
		   JOIN veiculo_imagem vi ON vi.id_imagem = i.id_imagem
		   JOIN imagem_info ii ON ii.id_imagem = i.id_imagem
		   JOIN tipo_imagem tp ON tp.id_tipo_imagem = ii.id_tipo_imagem
		   JOIN infracao inf ON inf.id_veiculo = vi.id_veiculo
	WHERE  inf.data >= '2025-10-13 13:00:00' AND inf.id_local IN (201,202) AND inf.id_enquadramento IN (74550,74630,74710)
		   AND tp.nome = 'PAN'

	--SELECT * FROM #temp_imagem_pan_remover

	BEGIN TRY 
		BEGIN TRAN
	
		INSERT INTO bkp_ipatinga_infracao_imagem
		SELECT i.*
		FROM   infracao_imagem i
			   LEFT JOIN bkp_ipatinga_infracao_imagem b ON b.id_infracao = i.id_infracao
		WHERE  i.id_infracao IN (SELECT id_infracao FROM #temp_imagem_pan_remover)
			   AND b.id_infracao IS NULL


		INSERT INTO bkp_ipatinga_infracao_obliteracao
		SELECT i.*
		FROM   infracao_obliteracao i
			   LEFT JOIN bkp_ipatinga_infracao_obliteracao b ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)
			   AND b.id_imagem IS NULL


		INSERT INTO bkp_ipatinga_infracao_processo_concluido
		SELECT i.*
		FROM   infracao_processo_concluido i
			   LEFT JOIN bkp_ipatinga_infracao_processo_concluido b ON b.id_infracao_processo_concluido = i.id_infracao_processo_concluido
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)
			   AND b.id_imagem IS NULL


		INSERT INTO bkp_ipatinga_veiculo_imagem
		SELECT i.*
		FROM   veiculo_imagem i
			   LEFT JOIN bkp_ipatinga_veiculo_imagem b ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)
			   AND b.id_imagem IS NULL


		INSERT INTO bkp_ipatinga_imagem
		SELECT i.*
		FROM   imagem i
			   LEFT JOIN bkp_ipatinga_imagem b ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)
			   AND b.id_imagem IS NULL


		INSERT INTO bkp_ipatinga_imagem_info
		SELECT i.*
		FROM   imagem_info i
			   LEFT JOIN bkp_ipatinga_imagem_info b ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover) 
			   AND b.id_imagem IS NULL


		---------------------------------------------------------------------------


		DELETE i
		FROM   infracao_imagem i
			   JOIN bkp_ipatinga_infracao_imagem b (NOLOCK) ON b.id_infracao = i.id_infracao
		WHERE  i.id_infracao IN (SELECT id_infracao FROM #temp_imagem_pan_remover)


		DELETE i
		FROM   infracao_obliteracao i
			   JOIN bkp_ipatinga_infracao_obliteracao b (NOLOCK) ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)


		DELETE i
		FROM   infracao_processo_concluido i
			   JOIN bkp_ipatinga_infracao_processo_concluido b (NOLOCK) ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)


		DELETE i
		FROM   veiculo_imagem i
			   JOIN bkp_ipatinga_veiculo_imagem b (NOLOCK) ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)


		DELETE i
		FROM   imagem i
			   JOIN bkp_ipatinga_imagem b (NOLOCK) ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)


		DELETE i
		FROM   imagem_info i
			   JOIN bkp_ipatinga_imagem_info b (NOLOCK) ON b.id_imagem = i.id_imagem
		WHERE  i.id_imagem IN (SELECT id_imagem FROM #temp_imagem_pan_remover)

		COMMIT
		 
	END TRY

	BEGIN CATCH

		IF (@@TRANCOUNT > 0)
			ROLLBACK
			 
		EXEC spu_replica_erro

	END CATCH

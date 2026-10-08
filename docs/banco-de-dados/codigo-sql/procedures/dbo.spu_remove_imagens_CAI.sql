CREATE PROCEDURE [dbo].[spu_remove_imagens_CAI]
AS

SET NOCOUNT ON

DECLARE	@Amostra INT = 20,
		@Tempo_Processamento VARCHAR(8)	= '00:10:00',
		@Data_Fim DATETIME,
		@Data_Removida DATETIME,
		@Imagem_Proc INT = 0,
		@Imagem_Proc_Atualiza INT = 0,
		@Id INT = 0,
		@Total INT = 0

SET @Data_Fim = GETDATE() + @Tempo_Processamento

DECLARE @tmp_imagem_remover AS TABLE (id_imagem INT)
INSERT INTO @tmp_imagem_remover (id_imagem)
SELECT TOP(200000) vi.id_imagem
	   FROM  veiculo_imagem vi (NOLOCK)
			 INNER JOIN infracao i (NOLOCK)
				  ON  i.id_veiculo = vi.id_veiculo
			 INNER JOIN infracao_remessa ir (NOLOCK)
				  ON  ir.id_infracao = i.id_infracao
			 INNER JOIN remessa r (NOLOCK)
				  ON  r.id_remessa = ir.id_remessa
			 INNER JOIN imagem img (NOLOCK)
				  ON  img.id_imagem = vi.id_imagem
			 LEFT JOIN infracao_contestacao ic (NOLOCK)
				  ON  ic.id_infracao = i.id_infracao
	   WHERE CAST(i.data AS DATE) < CAST(DATEADD(DAY, -90, GETDATE()) AS DATE)
			 AND r.data_confirmacao IS NOT NULL
			 AND img.imagem_removida = 0
			 AND ic.id_infracao IS NULL

SET @Total = (
		SELECT COUNT(vi.id_imagem) AS qtde
		FROM   veiculo_imagem vi (NOLOCK)
			   INNER JOIN infracao i (NOLOCK)
					ON  i.id_veiculo = vi.id_veiculo
			   INNER JOIN infracao_remessa ir (NOLOCK)
					ON  ir.id_infracao = i.id_infracao
			   INNER JOIN remessa r (NOLOCK)
					ON  r.id_remessa = ir.id_remessa
			   INNER JOIN imagem img (NOLOCK)
					ON  img.id_imagem = vi.id_imagem
			   LEFT JOIN infracao_contestacao ic (NOLOCK)
					ON  ic.id_infracao = i.id_infracao
		WHERE  CAST(i.data AS DATE) < CAST(DATEADD(DAY, -90, GETDATE()) AS DATE)
			   AND r.data_confirmacao IS NOT NULL
			   AND img.imagem_removida = 0
			   AND ic.id_infracao IS NULL
)

EXEC @Id = spu_log_inicia_processo 'remove_imagens_CAI', @Total

WHILE GETDATE() < @Data_Fim
BEGIN

	BEGIN TRY

		--DECLARE @Amostra INTEGER = 20
		DECLARE @tmp_imagem_removida AS TABLE (id_imagem INT PRIMARY KEY NOT NULL)
		INSERT INTO @tmp_imagem_removida (id_imagem)
		--DECLARE @Amostra INTEGER = 20
		SELECT TOP (@Amostra) img.id_imagem--, i.data, r.tipo, r.codigo_externo
		FROM   @tmp_imagem_remover img


		IF NOT EXISTS (SELECT 1 FROM @tmp_imagem_removida)
		BEGIN
			BREAK
		END

		SET @Data_Removida = GETDATE()

		IF EXISTS (SELECT 1 FROM @tmp_imagem_removida)
		BEGIN

			BEGIN TRAN					
		
			UPDATE imagem
			SET    imagem = NULL, imagem_removida = 1
			WHERE  id_imagem IN (SELECT tir.id_imagem FROM @tmp_imagem_removida tir)

			SET @Imagem_Proc_Atualiza = @@ROWCOUNT
			SET @Imagem_Proc = @Imagem_Proc + @Imagem_Proc_Atualiza

			EXEC spu_log_atualiza_processo @id, @Imagem_Proc_Atualiza

			DELETE FROM infracao_obliteracao WHERE id_imagem IN (SELECT tir.id_imagem FROM @tmp_imagem_removida tir )
			DELETE FROM infracao_processo_obliteracao WHERE id_imagem IN (SELECT tir.id_imagem FROM @tmp_imagem_removida tir )

			INSERT INTO imagem_removida WITH(ROWLOCK) (id_imagem, data_removida)
			SELECT tir.id_imagem
				  ,@Data_Removida
			FROM   @tmp_imagem_removida tir

			DELETE FROM @tmp_imagem_remover WHERE id_imagem IN (SELECT tir.id_imagem FROM @tmp_imagem_removida tir)

			COMMIT;
		
		END

	END TRY

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
		ROLLBACK;
		
		EXEC spu_replica_erro

	END CATCH

	DELETE FROM @tmp_imagem_removida

END

PRINT ' - Imagens Removidas.....: ' + dbo.fcn_FormataNumero(@Imagem_Proc)
EXEC spu_log_finaliza_processo @id

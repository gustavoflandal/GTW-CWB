CREATE PROCEDURE [dbo].[spu_finaliza_importacao_sequencia_local_novo]
AS

	DECLARE @processo INT

	EXEC @processo = spu_log_inicia_processo @nome = 'finaliza_importacao_sequencia_local_novo'

    BEGIN TRY

		EXEC spu_atualiza_configuracao_equipamento_data_modificacao 

	    DECLARE @regs_total INT = 0
		DECLARE @regs_at INT = 1
		DECLARE @Tempo_Processamento VARCHAR(8)	= '00:10:00'
		DECLARE @Data_Fim DATETIME

		DECLARE @tmp_veiculos_sequencia AS TABLE (id_veiculo_unic BIGINT, sequencia_local TINYINT)

		SET @Data_Fim = GETDATE() + @Tempo_Processamento

		DECLARE @regs_pend INT
		SELECT @regs_pend = COUNT(*) FROM configuracao_equipamento_pendente_importacao_novo (NOLOCK)

		EXEC spu_log_atualiza_processo_total @id = @processo, @registros = 0, @iteracoes = 0, @total = @regs_pend

		WHILE @regs_pend > 0 AND @regs_at > 0 AND GETDATE() < @Data_Fim
		BEGIN

		DELETE @tmp_veiculos_sequencia

		INSERT INTO @tmp_veiculos_sequencia
		SELECT TOP(100000) id_veiculo_unic, sequencia_local FROM configuracao_equipamento_pendente_importacao_novo (NOLOCK)

        --BEGIN TRANSACTION

		UPDATE veiculo_importacao WITH(ROWLOCK)
		SET    sequencia_local = sub1.sequencia_local
		FROM   @tmp_veiculos_sequencia AS sub1
		WHERE  veiculo_importacao.id_veiculo_unic = sub1.id_veiculo_unic 

		SET @regs_at = @@rowcount

		EXEC spu_log_atualiza_processo @id = @processo, @registros = @regs_at

        --COMMIT;

		IF NOT (@regs_at > 0)
		BEGIN
			BREAK;
		END

		END

    END TRY
    BEGIN CATCH

        IF (@@TRANCOUNT > 0)
            ROLLBACK

        EXEC spu_replica_erro

    END CATCH;

	EXEC spu_log_finaliza_processo @id = @processo

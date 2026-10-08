CREATE PROCEDURE [dbo].[spu_remove_config_importacao_antigo]
AS
	SET NOCOUNT ON

	DECLARE	@Registros_Proc INT = 0, @Id INT = 0, @Total INT = 0

	TRUNCATE TABLE tmp_config_equip_importacao_remover

	INSERT INTO tmp_config_equip_importacao_remover
	SELECT TOP 100000 cei.id_arquivo--, cei.data_configuracao
	FROM   configuracao_equipamento_importacao cei (NOLOCK)
		   INNER JOIN arquivos_importados ai (NOLOCK)
				ON  ai.id_arquivo = cei.id_arquivo
		   LEFT JOIN veiculo_importacao vi (NOLOCK)
				ON  ai.nome_arquivo = vi.nome_arquivo
					AND vi.importar = 1
	WHERE  CAST(cei.data_configuracao AS DATE) < CAST(DATEADD(DAY, -30, GETDATE()) AS DATE)
		   AND vi.id_veiculo IS NULL 

	SET @Total = (SELECT COUNT(*) AS qtde FROM configuracao_equipamento_importacao (NOLOCK) WHERE id_arquivo IN (SELECT id_arquivo FROM tmp_config_equip_importacao_remover))

	EXEC @Id = spu_log_inicia_processo 'remove_config_importacao_antigo', @Total

	BEGIN TRY
		BEGIN TRAN
		
		DELETE cei FROM configuracao_equipamento_importacao cei WITH (ROWLOCK)
		INNER JOIN tmp_config_equip_importacao_remover tmp ON tmp.id_arquivo = cei.id_arquivo
		
		SET @Registros_Proc = @Registros_Proc + @@ROWCOUNT

		EXEC spu_log_atualiza_processo @id, @Registros_Proc

		COMMIT;
	END TRY

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
		ROLLBACK;
		EXEC spu_replica_erro

	END CATCH

	PRINT ' - Registros Removidos.....: ' + dbo.fcn_FormataNumero(@Registros_Proc)
	EXEC spu_log_finaliza_processo @id

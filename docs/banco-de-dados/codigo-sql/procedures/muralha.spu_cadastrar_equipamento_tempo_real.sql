CREATE PROCEDURE [muralha].[spu_cadastrar_equipamento_tempo_real]
AS
BEGIN

	BEGIN TRY

		BEGIN TRAN					
		
		INSERT INTO muralha.config_envio_tempo_real_equipamento (id_local)
		SELECT lv.id_local
		FROM   local_vigente lv
			   LEFT JOIN muralha.config_envio_tempo_real_equipamento etr
					ON  etr.id_local = lv.id_local
		WHERE  lv.desativado = 0
			   AND etr.id_local IS NULL

		COMMIT;
		
	END TRY

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
		ROLLBACK;
		
		EXEC spu_replica_erro

	END CATCH

END

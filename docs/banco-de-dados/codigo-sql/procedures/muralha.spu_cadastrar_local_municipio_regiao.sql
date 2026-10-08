CREATE PROCEDURE [muralha].[spu_cadastrar_local_municipio_regiao]
AS
BEGIN

	BEGIN TRY

		BEGIN TRAN					
		
		INSERT INTO local_municipio_regiao (id_local, id_localidade, id_regiao)
		SELECT lv.id_local,
			   (SELECT id_localidade FROM muralha.fcn_LocalidadeContrato()) AS id_localidade,
			   5 AS id_regiao
		FROM   local_vigente lv
			   LEFT JOIN local_municipio_regiao lmr
					ON  lmr.id_local = lv.id_local
		WHERE  lmr.id_local IS NULL

		COMMIT;
		
	END TRY

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
		ROLLBACK;
		
		EXEC spu_replica_erro

	END CATCH

END

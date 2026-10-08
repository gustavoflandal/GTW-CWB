CREATE PROCEDURE [dbo].[spu_finaliza_trata_data_futura]
AS

SET NOCOUNT ON
 
DECLARE	@Amostra				int			= 5000,
		@Data_Fim				datetime,
		@Registros_Proc			int			= 0,
		@Registros_Proc_Total	int			= 0

BEGIN

	BEGIN TRY

		DECLARE @tmp_veiculos AS TABLE (id_veiculo_unic BIGINT)
		INSERT INTO @tmp_veiculos
		--DECLARE @Amostra INT = 5000
		SELECT TOP (@Amostra) id_veiculo_unic
		FROM   veiculo_importacao (NOLOCK)
		WHERE  data > DATEADD(HOUR, 24, GETDATE()) AND importar = 1
		--WHERE  data > DATEADD(DAY, 1, GETDATE()) AND importar = 1
		--WHERE  data > DATEADD(MINUTE, 10, GETDATE()) AND importar = 1
		GROUP BY
				id_veiculo_unic
			
		BEGIN TRAN
		UPDATE veiculo_importacao
		SET    importar = 0
		WHERE  id_veiculo_unic IN (SELECT id_veiculo_unic FROM @tmp_veiculos)
				--AND data > DATEADD(HOUR, 24, GETDATE())
				--AND data > DATEADD(DAY, 1, GETDATE())

		SET @Registros_Proc	= @@ROWCOUNT
		SET @Registros_Proc_Total= @Registros_Proc_Total + @Registros_Proc

		COMMIT;
					
	END TRY 

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'

		IF @@TRANCOUNT > 0 
			ROLLBACK;

	END CATCH

END

PRINT ' - [Data Futura] - registros tratados....: ' + dbo.fcn_FormataNumero(@Registros_Proc_Total)	+ ' - '

CREATE PROCEDURE [dbo].[spu_finaliza_trata_velocidade_media]
AS

BEGIN TRY 
BEGIN TRAN

UPDATE veiculo_importacao WITH (ROWLOCK) SET importar = 0 WHERE velocidade_media >= 140 

COMMIT;

END TRY 
BEGIN CATCH

PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
IF @@TRANCOUNT > 0 
	ROLLBACK;

END CATCH

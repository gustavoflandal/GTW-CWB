CREATE PROCEDURE [dbo].[spu_finaliza_trata_panoramica_excesso]
AS

SET NOCOUNT ON
 
DECLARE	@Amostra				int			= 5000,
		@Data_Fim				datetime,
		@Registros_Proc			int			= 0,
		@Registros_Proc_Total	int			= 0

BEGIN

	BEGIN TRY

		--DECLARE @Amostra INT = 5000
		DECLARE @tmp_imagens AS TABLE (id_imagem INT)
		INSERT INTO @tmp_imagens
		--DECLARE @Amostra INT = 5000
		SELECT TOP (@Amostra) iim.id_imagem
		FROM   imagem_importacao iim (NOLOCK)
			   LEFT JOIN tipo_imagem tim (NOLOCK) 					
					ON tim.nome = iim.nome AND tim.numero = iim.numero
			   LEFT JOIN imagem_info img (ROWLOCK)
					ON iim.id_imagem = img.id_imagem
		WHERE  tim.id_tipo_imagem IS NULL
		
		--SELECT id_imagem FROM @tmp_imagens
			
		BEGIN TRAN
		DELETE FROM imagem_importacao WHERE id_imagem IN (SELECT id_imagem FROM @tmp_imagens)

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

PRINT ' - [Excesso panorâmicas] - registros tratados....: ' + dbo.fcn_FormataNumero(@Registros_Proc_Total)	+ ' - '


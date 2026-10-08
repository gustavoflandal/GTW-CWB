CREATE PROCEDURE [dbo].[spu_remove_cad_isento_antigo]
AS

SET NOCOUNT ON

DECLARE	@id_arquivo INT,
		@Registros_Proc INT = 0,
		@data_removido DATE

SET @id_arquivo = (
	SELECT MIN(cai.id) AS id_arquivo
	FROM   cad_arquivos_importados cai (NOLOCK)
	WHERE  CAST(cai.data_hora AS DATE) < CAST(DATEADD(DAY, -90, GETDATE()) AS DATE)
		   AND ((cai.nome_arquivo LIKE '%CAD-VEIC-ISEN-ROD%')OR(cai.nome_arquivo LIKE '%PFRETADO_VALIDO%')OR(cai.nome_arquivo LIKE '%PCAMIN_VALIDO%'))
		   AND (cai.arq_isento_removido = 0 OR cai.arq_isento_removido IS NULL)
)


BEGIN TRY

	IF (@id_arquivo IS NULL)
	BEGIN
		SET @id_arquivo = 0
	END

	SET @data_removido = CAST(GETDATE() AS DATE)

	IF @id_arquivo > 0
	BEGIN

		BEGIN TRAN

		DELETE FROM cad_isento WHERE id_arquivo = @id_arquivo
		SET @Registros_Proc = @Registros_Proc + @@ROWCOUNT

		UPDATE cad_arquivos_importados SET arq_isento_removido = 1, data_removido = @data_removido WHERE id = @id_arquivo
		
		COMMIT

	END

END TRY

BEGIN CATCH

	PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
	IF @@TRANCOUNT > 0
		ROLLBACK
		
	EXEC spu_replica_erro

END CATCH

IF @id_arquivo > 0
BEGIN
	PRINT ' - Registros Removidos.....: ' + dbo.fcn_FormataNumero(@Registros_Proc)
END
ELSE
BEGIN
	PRINT ' - Não há arquivos a serem removidos - '
END


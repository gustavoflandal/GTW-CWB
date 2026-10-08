
CREATE PROCEDURE [dbo].[spu_IncrementaRevisaoRemessa] 
	@id_remessa INTEGER 
AS
BEGIN

	DECLARE @revisao INTEGER 
	DECLARE @data_exportacao DATETIME 

	SELECT 
		@revisao = remessa.revisao, 
		@data_exportacao = remessa.data_exportacao 
	FROM 
		remessa (nolock) 
	WHERE 
		id_remessa = @id_remessa  

	--IF @data_exportacao IS NOT NULL 
	SET @revisao = @revisao + 1 

	UPDATE remessa with(rowlock) 
	SET revisao = @revisao, 
		data_exportacao = GETDATE() 
	WHERE id_remessa = @id_remessa 

END




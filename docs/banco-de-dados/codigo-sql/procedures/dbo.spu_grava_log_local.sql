

CREATE  PROCEDURE [dbo].[spu_grava_log_local] 
	@id_local int,
	@tipo char(3),
	@usuario CHAR(20),
	@detalhe varchar(1000),
	@data datetime = null
AS

	IF @data IS NULL
		SET @data = getdate()

	DECLARE @resumo varchar(150)
	DECLARE @id_usuario int
	
	IF @id_local > 0
		SET @resumo = '['+LTRIM(@id_local)+'] <LOG EQUIPAMENTO>'
	ELSE
		SET @resumo = '<LOG GERAL>'

	SELECT 
		@id_usuario = id_usuario 
	FROM 
		sis_usuario (nolock)
	WHERE 
		usuario = @usuario

	EXEC spu_grava_log @resumo, @tipo, @id_usuario, @detalhe, @data





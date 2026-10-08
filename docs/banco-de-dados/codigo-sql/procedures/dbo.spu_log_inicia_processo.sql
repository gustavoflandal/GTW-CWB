
CREATE PROCEDURE [dbo].[spu_log_inicia_processo]
	@nome VARCHAR(60),
	@total INT = NULL
AS
BEGIN
	DECLARE @Result int

	IF @total IS NULL
	INSERT INTO log_processos (nome) VALUES (@nome)
	ELSE
	INSERT INTO log_processos (nome, total) VALUES (@nome, @total)
	
	SET @Result = SCOPE_IDENTITY()

	RETURN @Result
END

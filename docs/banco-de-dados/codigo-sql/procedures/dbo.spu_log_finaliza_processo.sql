
CREATE PROCEDURE [dbo].[spu_log_finaliza_processo]
	@id INT
AS
BEGIN
	DECLARE @Result int

	UPDATE log_processos SET data_atualizado = GETDATE(), data_finalizado = GETDATE()
	WHERE id = @id

	SET @Result = @@rowcount

	RETURN @Result
END

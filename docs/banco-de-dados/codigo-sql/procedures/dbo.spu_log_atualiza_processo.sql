
CREATE PROCEDURE [dbo].[spu_log_atualiza_processo]
	@id INT,
	@registros INT
AS
BEGIN
	DECLARE @Result int

	UPDATE log_processos SET registros = registros + @registros, iteracoes = iteracoes + 1, data_atualizado = GETDATE()
	WHERE id = @id
	
	SET @Result = @@rowcount

	RETURN @Result
END

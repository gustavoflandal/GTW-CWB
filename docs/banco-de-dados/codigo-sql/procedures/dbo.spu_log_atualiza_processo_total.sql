
CREATE PROCEDURE [dbo].[spu_log_atualiza_processo_total]
	@id INT,
	@registros INT,
	@iteracoes INT,
	@total INT
AS
BEGIN
	DECLARE @Result int

	UPDATE log_processos SET registros = @registros, iteracoes = @iteracoes, total = @total, data_atualizado = GETDATE()
	WHERE id = @id
	
	SET @Result = @@rowcount

	RETURN @Result
END

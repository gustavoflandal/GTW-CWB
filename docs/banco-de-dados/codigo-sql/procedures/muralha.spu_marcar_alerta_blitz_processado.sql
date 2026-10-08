CREATE PROCEDURE muralha.spu_marcar_alerta_blitz_processado
    @id_alerta UNIQUEIDENTIFIER,
    @enviado BIT,  -- 0 = Não é blitz, 1 = Enviado com sucesso
    @data_processamento DATETIME = NULL
AS
BEGIN
    UPDATE muralha.alerta 
    SET enviado_blitz_mobile = @enviado,
        data_blitz_mobile_processado = ISNULL(@data_processamento, GETDATE())
    WHERE id = @id_alerta;
END
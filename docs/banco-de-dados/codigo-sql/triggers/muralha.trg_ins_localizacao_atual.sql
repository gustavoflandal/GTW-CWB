CREATE TRIGGER trg_ins_localizacao_atual
ON muralha.agente_localizacao_atual
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO muralha.agente_localizacao_hist (
        id_usuario,
        latitude,
        longitude,
        data_registro
    )
    SELECT
        i.id_usuario,
        i.latitude,
        i.longitude,
        GETDATE()
    FROM inserted i;
END;
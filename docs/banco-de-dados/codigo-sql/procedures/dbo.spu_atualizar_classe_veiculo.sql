
CREATE   PROCEDURE dbo.spu_atualizar_classe_veiculo
    @idCaptura VARCHAR(36),
    @idClasse VARCHAR(1)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY

        BEGIN TRANSACTION;

		IF @idCaptura IS NULL OR LTRIM(RTRIM(@idCaptura)) = '' THROW 50001, 'O identificador da captura não foi informado.', 1;

        UPDATE dbo.veiculo
        SET id_classe = @idClasse
        WHERE id_captura = @idCaptura;

        UPDATE muralha.veiculo_tempo_real SET classificacao = @idClasse WHERE id_captura = @idCaptura;

        UPDATE ia.veiculo_caracteristica SET id_classificacao = @idClasse WHERE id_captura = @idCaptura;

        UPDATE dbo.veiculo_estatistica SET id_classe = @idClasse WHERE id_captura = @idCaptura;

        COMMIT TRANSACTION;

        SELECT CAST(1 AS BIT) AS sucesso;

    END TRY

    BEGIN CATCH

        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;

        SELECT CAST(0 AS BIT) AS sucesso

    END CATCH
END;

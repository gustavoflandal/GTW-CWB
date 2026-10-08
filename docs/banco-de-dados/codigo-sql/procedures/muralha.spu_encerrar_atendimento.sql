CREATE PROCEDURE muralha.spu_encerrar_atendimento    
    @idAtendimento INT,    
    @idGuarnicao INT    
AS    
BEGIN    
    SET NOCOUNT ON;    
    
    BEGIN TRY    
        BEGIN TRANSACTION;    
    
        -- Atualiza a tabela de atendimento    
        UPDATE muralha.atendimento    
        SET id_situacao = 4,    
            data_encerramento = GETDATE()    
        WHERE id = @idAtendimento;    
    
        -- Atualiza a tabela de atendimento_guarnicao    
        UPDATE muralha.atendimento_guarnicao    
        SET id_situacao = 5    
        WHERE id_atendimento = @idAtendimento AND id_situacao = 1;    
    
        -- Atualiza a guarnição para disponível    
        UPDATE muralha.guarnicao    
        SET disponivel = 1    
        WHERE id = @idGuarnicao;    
    
        COMMIT TRANSACTION;    
    END TRY    
    BEGIN CATCH    
        ROLLBACK TRANSACTION;    
    
        -- Opcional: Retorna o erro    
        DECLARE @ErrorMessage NVARCHAR(4000), @ErrorSeverity INT, @ErrorState INT;    
        SELECT @ErrorMessage = ERROR_MESSAGE(),    
               @ErrorSeverity = ERROR_SEVERITY(),    
               @ErrorState = ERROR_STATE();    
        RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);    
    END CATCH    
END;
CREATE   PROCEDURE muralha.gerenciar_passagem_correlacionamento    
(    
 @idCorrelacionamento int,    
 @idPassagemPlacaAlvo UNIQUEIDENTIFIER,    
 @idPassagemPlacaCorrelacionada UNIQUEIDENTIFIER,    
 @idMotivoInvalido int,    
 @idUsuario int    
)    
AS    
 BEGIN    
    
 SET NOCOUNT ON;    
    
  BEGIN TRY    
       
   BEGIN TRAN    
    
    -- VARIÁVEL DE CONTROLE PARA DEFINIR SE PRECISA OU NÃO EXCLUIR O CORRELACIONAMENTO E SUAS PASSAGENS    
    DECLARE @totalPassagensRestantes int;    
    
    -- REGISTRANDO A PASSAGEM INVÁLIDA    
    INSERT INTO muralha.correlacionamento_automatico_placa_invalida(    
      id_passagem_placa_alvo,    
      id_passagem_placa_correlacionada,    
      id_usuario,    
      motivo,    
      data_registro,
      id_correlacionamento
     )    
    VALUES(    
     @idPassagemPlacaAlvo,    
     @idPassagemPlacaCorrelacionada,    
     @idUsuario,    
     @idMotivoInvalido,    
     GETDATE(),
     @idCorrelacionamento
    );    
    
    -- EXCLUINDO A PASSAGEM INVÁLIDA    
    DELETE FROM muralha.correlacionamento_automatico_placa    
    WHERE     
     id_correlacionamento = @idCorrelacionamento    
     AND id_passagem_placa_alvo = @idPassagemPlacaAlvo     
     AND id_passagem_placa_correlacionada = @idPassagemPlacaCorrelacionada    
    
    -- SALVA A QUANTIDADE DE PASSAGENS RESTANTES    
    SELECT     
     @totalPassagensRestantes = COUNT(*)    
    FROM muralha.correlacionamento_automatico_placa cap    
    WHERE cap.id_correlacionamento = @idCorrelacionamento    
    
    if(@totalPassagensRestantes < 3)    
     BEGIN    
    
      -- REMOVENDO AS ***PASSAGENS DO CORRELACIONAMENTO*** CASO NÃO ATENDAM MAIS AO MÍNIMO DE 3 PASSAGENS CORRELACIONADAS    
      DELETE FROM muralha.correlacionamento_automatico_placa    
      WHERE id_correlacionamento = @idCorrelacionamento    
    
      -- REMOVENDO O ***CORRELACIONAMENTO*** CASO ELE NÃO ATENDA MAIS AO MÍNIMO DE 3 PASSAGENS CORRELACIONADAS    
      DELETE FROM muralha.correlacionamento_automatico    
      WHERE id = @idCorrelacionamento   
      
      -- SETANDO NULL EM TODAS AS COLUNAS QUE TINHAM AQUELE CORRELACIONAMENTO POIS ELE NÃO EXISTE MAIS   
      UPDATE muralha.correlacionamento_automatico_placa_invalida
        SET id_correlacionamento = null
      WHERE id_correlacionamento = @idCorrelacionamento
    
     END    
    
   COMMIT TRAN    
    
   SELECT 1 as sucesso;    
    
   END TRY    
    
   BEGIN CATCH    
    
   IF @@TRANCOUNT > 0    
   ROLLBACK TRAN    
    
   THROW;    
    
  END CATCH    
END    
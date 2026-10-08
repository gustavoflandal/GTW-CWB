CREATE PROCEDURE [dbo].[spu_busca_infracao_processamento]   
 @id_usuario int,   
 @id_processo int,   
 @id_enquadramento int,   
 @consistencia bit,   
 @espera bit,   
 @periodo_ini datetime,   
 @periodo_fim datetime,   
 @id_infracao_atual int = 0,   
 @acao tinyint = 0   
AS   
  
-- ALVO:  
-- ANTERIOR = 0,  
-- PROXIMO = 1,  
-- ATUAL = 2,  
-- PROXIMO_NOVO = 3  
  
 SET NOCOUNT ON   
    
 DECLARE @id_infracao_escolhida INT = null   
 DECLARE @strLock NVARCHAR(255) = '[spu_busca_infracao_processamento]' + '[' + RTRIM(LTRIM(STR(@id_processo))) + ']' -- lock isolado por processo  
    
 BEGIN TRY   
    
  BEGIN TRANSACTION   
     
  EXEC sp_getapplock @Resource = @strLock, @LockMode = 'Exclusive'    
   
  if (@espera IS NOT NULL AND @espera = 0)  
   SET @espera = NULL  
   
  IF @id_infracao_atual > 0     
  
   BEGIN   
  
     -- O usuario jah tinha uma infração, então...   
     IF  @acao = 0      
  
      BEGIN   
  
         -- Mandou buscar a anterior  
        SELECT top 1   
        @id_infracao_escolhida = id_infracao    
       FROM   
        infracao_janela (nolock)   
       WHERE id_usuario = @id_usuario    
        AND id_processo = @id_processo   
        AND data_infracao_janela < (SELECT   
                data_infracao_janela   
               FROM   
                infracao_janela (nolock)   
               WHERE   
                id_infracao = @id_infracao_atual)   
       ORDER BY data_infracao_janela DESC   
       
      END   
  
    IF  @acao IN (1, 3)      
     
     BEGIN   
  
        -- Mandou buscar o proximo ou o proximo_novo  
       SELECT top 1   
       @id_infracao_escolhida = id_infracao    
      FROM   
       infracao_janela (nolock)    
      WHERE id_usuario = @id_usuario    
       AND id_processo = @id_processo   
       AND data_infracao_janela > (SELECT   
               data_infracao_janela   
              FROM   
               infracao_janela (nolock)   
              WHERE   
               id_infracao = @id_infracao_atual)   
      ORDER BY data_infracao_janela ASC   
       
     END   
  
    ELSE IF @acao = 2      
     
     BEGIN   
  
       -- Mandou pegar a atual  
      SELECT TOP 1  
       @id_infracao_escolhida = id_infracao    
      FROM   
       fcn_InfracaoDisponivelUsuario(@id_processo, @id_usuario)   
      WHERE   
       id_infracao = @id_infracao_atual   
       
     END  
  
   END   
    
  -- Se não tinha atual ou se era um proximo_novo e não conseguiu pegar ninguém  
  IF ((@id_infracao_atual = 0) OR (@acao = 3 AND @id_infracao_escolhida IS NULL))   
  
   BEGIN   
      
    SET @id_infracao_escolhida = 0       
  
    SELECT TOP 1   
     @id_infracao_escolhida = id_infracao    
    FROM   
     fcn_InfracaoDisponivelUsuario(@id_processo, @id_usuario)   
    WHERE id_usuario_atual IS NULL   
     AND (@id_enquadramento IS NULL   
      OR id_enquadramento = @id_enquadramento)   
     AND (@consistencia IS NULL   
      OR (@consistencia = 1 AND id_inconsistencia = 0)   
      OR (@consistencia = 0 AND id_inconsistencia > 0))   
     AND (espera = @espera   
      OR (@espera IS NULL AND espera IS NULL))   
     AND (@periodo_ini IS NULL   
      OR @periodo_fim IS NULL   
      OR data between @periodo_ini and @periodo_fim)   
    ORDER BY CAST(data AS date), id_local, pista, id_enquadramento, data  
   
   END   
   
  -- Se pegou alguém...  
  IF (@id_infracao_escolhida > 0)   
  
  BEGIN   
  
   -- Coloca a infração para o usuário   
   UPDATE infracao with (rowlock)   
   SET   
    id_usuario_atual=@id_usuario    
   WHERE   
    id_infracao = @id_infracao_escolhida   
  
   -- Se não estiver na janela, então coloque ela na janela.  
   IF (NOT EXISTS  (SELECT data_infracao_janela   
        FROM infracao_janela (nolock)    
          WHERE id_usuario = @id_usuario   
         AND id_processo = @id_processo    
           AND id_infracao = @id_infracao_escolhida   
          )   
    )   
  
   BEGIN   
  
    INSERT into infracao_janela with (rowlock)  
     (id_usuario, id_processo, id_infracao)   
    VALUES   
     (@id_usuario, @id_processo, @id_infracao_escolhida)     
     
   END   
     
  END   
     
  COMMIT  
   
        -- Ajusta a janela  
  EXEC spu_ajusta_janela @id_usuario, @id_processo, null   
   
  -- Se no fim das contas não pegou ninguém, retorna ZERO  
  IF (@id_infracao_escolhida IS NULL)   
   SET @id_infracao_escolhida = 0  
      
  RETURN @id_infracao_escolhida   
     
 END TRY   
  
 BEGIN CATCH   
   
  IF (@@TRANCOUNT > 0)   
   ROLLBACK   
      
  EXEC spu_replica_erro   
     
  RETURN 0   
     
 END CATCH  

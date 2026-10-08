
CREATE PROCEDURE [dbo].[spu_log_detalhe]
 @id INT,  
 @data DATETIME,
 @adicionados INT,
 @arquivos INT,
 @tempo_exec TIME  
AS  
BEGIN  
 DECLARE @Result int  
  
 INSERT INTO  log_processos_detalhe VALUES (@id, @data, @adicionados, @arquivos, @tempo_exec)
   
 SET @Result = @@rowcount  
  
 RETURN @Result  
END  

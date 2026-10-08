CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado_dia_atual]  
AS  
  
 DECLARE @data DATE = CAST(GETDATE() AS DATE)
   
 SET NOCOUNT ON   
    
 BEGIN TRY   
    
  BEGIN TRANSACTION   
   
  DELETE   
   FROM veiculo_sumarizado with (rowlock)    
   WHERE data >= @data  
    
  EXEC spu_sumariza_veiculos @data, @data  
  
  COMMIT   
     
 END TRY   
  
 BEGIN CATCH   
   
  IF (@@TRANCOUNT > 0)   
   
   ROLLBACK   
      
  EXEC spu_replica_erro   
     
 END CATCH  

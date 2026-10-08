
CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado_relatorio_dia_atual]  
AS  
  
 DECLARE @data DATE = CAST(GETDATE() AS DATE)
  
 SET NOCOUNT ON  
    
 BEGIN TRY  
    
  BEGIN TRAN  
   
  DELETE FROM veiculo_sumarizado_relatorio WHERE dia >= @data  
    
  EXEC spu_sumariza_veiculos_relatorio @data, @data  
  
  COMMIT;  
     
 END TRY  
  
 BEGIN CATCH  
   
  IF (@@TRANCOUNT > 0)  
  BEGIN  
   ROLLBACK;  
  END  
      
  EXEC spu_replica_erro  
     
 END CATCH  

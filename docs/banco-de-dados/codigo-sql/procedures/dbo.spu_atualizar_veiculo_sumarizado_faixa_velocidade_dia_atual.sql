
CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado_faixa_velocidade_dia_atual]  
AS  
  
 DECLARE @data DATE = CAST(GETDATE() AS DATE)
  
 SET NOCOUNT ON  
    
 BEGIN TRY  
    
  BEGIN TRAN  
   
  DELETE FROM veiculo_sumarizado_faixa_velocidade WHERE dia >= @data  
    
  EXEC spu_sumariza_veiculos_faixa_velocidade @data, @data  
  
  COMMIT;  
     
 END TRY  
  
 BEGIN CATCH  
   
  IF (@@TRANCOUNT > 0)  
  BEGIN  
   ROLLBACK;  
  END  
      
  EXEC spu_replica_erro  
     
 END CATCH  

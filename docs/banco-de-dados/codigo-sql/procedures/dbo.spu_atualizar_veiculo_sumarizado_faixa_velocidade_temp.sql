CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado_faixa_velocidade_temp]  
AS  
  
 DECLARE @data_inicio DATE = '2025-07-01' --(SELECT dbo.fcn_getDataInicioSumariza())  
 DECLARE @data_fim DATE = '2025-10-12' --CAST(GETDATE() AS DATE) -- hoje  
  
 SET NOCOUNT ON  
    
 BEGIN TRY  
    
  BEGIN TRAN  
   
  DELETE FROM veiculo_sumarizado_faixa_velocidade WHERE dia BETWEEN @data_inicio AND @data_fim
    
  EXEC spu_sumariza_veiculos_faixa_velocidade_temp @data_inicio, @data_fim  
  
  COMMIT;  
     
 END TRY  
  
 BEGIN CATCH  
   
  IF (@@TRANCOUNT > 0)  
  BEGIN  
   ROLLBACK;  
  END  
      
  EXEC spu_replica_erro  
     
 END CATCH  
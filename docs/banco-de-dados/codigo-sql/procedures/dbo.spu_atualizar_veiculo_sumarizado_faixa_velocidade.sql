CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado_faixa_velocidade]  
AS  
  
 DECLARE @data_inicio DATE = (SELECT dbo.fcn_getDataInicioSumariza())  
 DECLARE @data_fim DATE = CAST(GETDATE() AS DATE) -- hoje  
  
 SET NOCOUNT ON  
    
 BEGIN TRY  
    
  BEGIN TRAN  
   
  DELETE FROM veiculo_sumarizado_faixa_velocidade WHERE dia >= @data_inicio  
    
  EXEC spu_sumariza_veiculos_faixa_velocidade @data_inicio, @data_fim  
  
  COMMIT;  
     
 END TRY  
  
 BEGIN CATCH  
   
  IF (@@TRANCOUNT > 0)  
  BEGIN  
   ROLLBACK;  
  END  
      
  EXEC spu_replica_erro  
     
 END CATCH  

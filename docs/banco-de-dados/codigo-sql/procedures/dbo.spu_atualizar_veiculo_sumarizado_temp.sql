CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado_temp]  
AS  
  
 DECLARE @data_inicio DATE = '2025-07-01' --(SELECT dbo.fcn_getDataInicioSumariza())  
 DECLARE @data_fim DATE = '2025-10-12' --CAST(GETDATE() AS DATE) -- hoje  
   
 SET NOCOUNT ON   
    
 BEGIN TRY   
    
  BEGIN TRANSACTION   
   
  DELETE FROM veiculo_sumarizado with (rowlock) WHERE data BETWEEN @data_inicio AND @data_fim
    
  EXEC spu_sumariza_veiculos_temp @data_inicio, @data_fim  
  
  COMMIT   
     
 END TRY   
  
 BEGIN CATCH   
   
  IF (@@TRANCOUNT > 0)   
   
   ROLLBACK   
      
  EXEC spu_replica_erro   
     
 END CATCH 
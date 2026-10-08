CREATE PROCEDURE [dbo].[spu_atualizar_veiculo_sumarizado]  
AS  
  
 DECLARE @data_inicio DATE = (SELECT dbo.fcn_getDataInicioSumariza())  
 DECLARE @data_fim DATE = CAST(GETDATE() AS DATE) -- hoje  
   
 SET NOCOUNT ON   
    
 BEGIN TRY   
    
  BEGIN TRANSACTION   
   
  DELETE   
   FROM veiculo_sumarizado with (rowlock)    
   WHERE data >= @data_inicio  
    
  EXEC spu_sumariza_veiculos @data_inicio, @data_fim  
  
  COMMIT   
     
 END TRY   
  
 BEGIN CATCH   
   
  IF (@@TRANCOUNT > 0)   
   
   ROLLBACK   
      
  EXEC spu_replica_erro   
     
 END CATCH  

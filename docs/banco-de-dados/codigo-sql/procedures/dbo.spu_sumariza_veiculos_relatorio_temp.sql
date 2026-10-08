CREATE PROCEDURE [dbo].[spu_sumariza_veiculos_relatorio_temp] @data_inicio DATE, @data_fim DATE  
AS  
  
 SET NOCOUNT ON  
    
 BEGIN TRY  
  
  BEGIN TRAN  
  --DECLARE @data_inicio DATE = '2018-03-01', @data_fim DATE = '2018-03-08'  
  INSERT INTO veiculo_sumarizado_relatorio  
  --DECLARE @data_inicio DATE = '2018-03-01', @data_fim DATE = '2018-03-08'  
  SELECT *  
  FROM   v_veiculo_sumarizado_relatorio_temp (NOLOCK)  
  WHERE  dia BETWEEN @data_inicio AND @data_fim  
  
  COMMIT;  
     
 END TRY  
  
 BEGIN CATCH  
   
  IF (@@TRANCOUNT > 0)  
  BEGIN  
   ROLLBACK;  
  END  
      
  EXEC spu_replica_erro  
     
 END CATCH  
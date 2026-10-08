
CREATE PROCEDURE [dbo].[spu_sumariza_veiculos_faixa_velocidade] @data_inicio DATE, @data_fim DATE  
AS  
  
 SET NOCOUNT ON  
    
 BEGIN TRY  
  
  BEGIN TRAN  
  --DECLARE @data_inicio DATE = '2018-03-01', @data_fim DATE = '2018-03-08'  
  INSERT INTO veiculo_sumarizado_faixa_velocidade (dia, hora, id_local, id_pista, id_faixa_velocidade, veiculos_detectados)  
  --DECLARE @data_inicio DATE = '2018-03-01', @data_fim DATE = '2018-03-08'  
  SELECT dia,  
      hora,  
      id_local,  
      id_pista,  
      faixa_velocidade,  
      veiculos_detectados  
  FROM   v_veiculo_sumarizado_faixa_velocidade (NOLOCK)  
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
  

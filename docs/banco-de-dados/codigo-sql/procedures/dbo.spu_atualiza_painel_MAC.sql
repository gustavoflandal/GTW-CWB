CREATE PROCEDURE [dbo].[spu_atualiza_painel_MAC]  
AS  
  
 SET NOCOUNT ON  
  
 DECLARE @ultimo_evento AS TABLE (id INT, id_proprietario INT)  
 DECLARE @ultimo_mac AS TABLE (proprietario NVARCHAR(50), data_hora DATETIME, MAC VARCHAR(20), mensagem VARCHAR(512), id INT, id_proprietario INT)  
  
 INSERT INTO @ultimo_evento  
 SELECT MAX(id) AS id,  
     id_proprietario  
 FROM   eventos_csx (NOLOCK)  
 WHERE  id_evento = 52 --Inventário do equipamento.  
     AND mensagem LIKE 'Endereço MAC:%'  
     AND mensagem <> 'Endereço MAC: 00:53:45:00:00:00' -- MAC inválido  
     AND mensagem <> 'Endereço MAC: EC:9D:E9:F7:F7:F7' -- MAC inválido  
     AND mensagem <> 'Endereço MAC: 00:00:00:00:00:00' -- MAC inválido  
     --AND id_proprietario = e1.id_proprietario  
 GROUP BY  
     id_proprietario  
  
  
 INSERT INTO @ultimo_mac  
 SELECT ep1.proprietario,  
     e1.data_hora,  
     SUBSTRING(e1.mensagem,15,17) AS [MAC],  
     e1.mensagem,  
     e1.id,  
     e1.id_proprietario  
 FROM   eventos_csx e1 (nolock)  
     INNER JOIN eventos_csx_desc_proprietario ep1 (nolock)  
    ON  ep1.id_proprietario = e1.id_proprietario  
     INNER JOIN @ultimo_evento AS ult_evt  
    ON  ult_evt.id_proprietario = e1.id_proprietario  
     AND ult_evt.id = e1.id  
 WHERE  e1.id_evento = 52 --Inventário do equipamento.  
  
 --SELECT * FROM @ultimo_mac  
  
 SET NOCOUNT OFF  
  
 BEGIN TRY  
  
  UPDATE painel_contrato WITH (ROWLOCK)  
  SET    dataMAC = NULL,  
      mac = NULL  
  
  UPDATE painel_contrato WITH (ROWLOCK)  
  SET    dataMAC = ultMac.data_hora,  
      mac = ultMac.MAC  
  FROM   @ultimo_mac ultMac  
  WHERE  CAST(painel_contrato.numeroSerie AS CHAR(10)) = ultMac.proprietario  
  
 END TRY   
  
 BEGIN CATCH  
  
  PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'  
  
  IF @@TRANCOUNT > 0   
   ROLLBACK  
  
 END CATCH  


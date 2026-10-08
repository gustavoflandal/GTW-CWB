
CREATE FUNCTION [dbo].[fcn_getRelatorioLeituraCorretaOCREstatistica]  
  ( @dataInicio date,  
    @dataFim date )  
RETURNS TABLE  
AS  
  
RETURN  
(   
 --DECLARE @dataInicio DATE = '2016-07-01', @dataFim DATE = '2016-07-15'  
  
 SELECT RESULT.veiculo_sem_placa AS [Veículo sem placa]  
    ,RESULT.veiculo_com_placa AS [Veículo com placa]  
    ,RESULT.Total  
    ,CAST((CAST(RESULT.veiculo_sem_placa AS DECIMAL(10,2)) / CAST(RESULT.total AS DECIMAL(10,2))) * 100 AS DECIMAL(10,2)) AS [% Veículo sem placa]  
    ,CAST((CAST(RESULT.veiculo_com_placa AS DECIMAL(10,2)) / CAST(RESULT.total AS DECIMAL(10,2))) * 100 AS DECIMAL(10,2)) AS [% Veículo com placa]  
 FROM   (  
    SELECT SUM(CASE WHEN v.placa IS NULL THEN 1 ELSE 0 END) AS veiculo_sem_placa  
       ,SUM(CASE WHEN v.placa IS NOT NULL THEN 1 ELSE 0 END) AS veiculo_com_placa  
       ,COUNT(*) AS total  
    FROM   veiculo_pesquisa v (NOLOCK)  
        INNER JOIN local_vigente lv (NOLOCK)  
       ON  lv.id_local = v.id_local  
    WHERE  CAST(v.data AS DATE) BETWEEN @dataInicio AND @dataFim  
 ) RESULT  
  
)  

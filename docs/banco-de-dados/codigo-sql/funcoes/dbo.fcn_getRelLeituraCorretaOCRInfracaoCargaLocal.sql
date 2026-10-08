

CREATE FUNCTION [dbo].[fcn_getRelLeituraCorretaOCRInfracaoCargaLocal]  
  ( @dataInicio date,  
    @dataFim date )  
RETURNS TABLE  
AS  
  
RETURN  
(  
 --DECLARE @dataInicio DATE = '2019-09-01', @dataFim DATE = '2019-09-30'  
 SELECT RESULT.id_local AS [Id. local]  
    ,RESULT.desc_local AS [Desc. local]  
    ,RESULT.veiculo_sem_placa AS [Veículo sem placa]  
    ,RESULT.veiculo_com_placa AS [Veículo com placa]  
    ,RESULT.placa_lida_digitada AS [Placa lida e digitada]  
    ,RESULT.leitura_correta_OCR AS [Leitura correta OCR]  
    ,RESULT.Total  
    ,CASE WHEN RESULT.Total > 0 THEN ROUND(CAST((CAST(RESULT.veiculo_sem_placa AS FLOAT) / CAST(RESULT.Total AS FLOAT)) * 100 AS FLOAT), 2) ELSE 0 END AS [% Veículo sem placa]  
    ,CASE WHEN RESULT.Total > 0 THEN ROUND(CAST((CAST(RESULT.veiculo_com_placa AS FLOAT) / CAST(RESULT.Total AS FLOAT)) * 100 AS FLOAT), 2) ELSE 0 END AS [% Veículo com placa]  
    ,CASE WHEN RESULT.placa_lida_digitada > 0 THEN ROUND(CAST((CAST(RESULT.leitura_correta_OCR AS FLOAT) / CAST(RESULT.placa_lida_digitada AS FLOAT)) * 100 AS FLOAT), 2) ELSE 0 END AS [% Leitura correta OCR]  
    ,CASE WHEN RESULT.placa_lida_digitada > 0 THEN ROUND(CAST((CAST(RESULT.leitura_incorreta_OCR AS FLOAT) / CAST(RESULT.placa_lida_digitada AS FLOAT)) * 100 AS FLOAT), 2) ELSE 0 END AS [% Leitura incorreta OCR]  
       
    ,RESULT.total_mercosul AS [Total Mercosul]  
    ,RESULT.placa_lida_digitada_mercosul AS [Placa lida e digitada Mercosul]  
    ,CASE WHEN RESULT.placa_lida_digitada_mercosul > 0 THEN ROUND(CAST((CAST(RESULT.leitura_correta_OCR_mercosul AS FLOAT) / CAST(RESULT.placa_lida_digitada_mercosul AS FLOAT)) * 100 AS FLOAT), 2) ELSE 0 END AS [% Leitura correta OCR Mercosul]  
    ,CASE WHEN RESULT.placa_lida_digitada_mercosul > 0 THEN ROUND(CAST((CAST(RESULT.leitura_incorreta_OCR_mercosul AS FLOAT) / CAST(RESULT.placa_lida_digitada_mercosul AS FLOAT)) * 100 AS FLOAT), 2) ELSE 0 END AS [% Leitura incorreta OCR Mercosul]  
  
 FROM   (  
   SELECT lv.id_local  
      ,lv.nome AS desc_local  
      ,SUM(CASE WHEN v.placa IS NULL THEN 1 ELSE 0 END) AS veiculo_sem_placa  
      ,SUM(CASE WHEN v.placa IS NOT NULL THEN 1 ELSE 0 END) AS veiculo_com_placa  
      ,SUM(CASE WHEN v.placa IS NOT NULL AND i.placa IS NOT NULL THEN 1 ELSE 0 END) AS placa_lida_digitada  
      ,SUM(CASE WHEN v.placa IS NOT NULL AND i.placa = v.placa THEN 1 ELSE 0 END) AS leitura_correta_OCR  
      ,SUM(CASE WHEN v.placa IS NOT NULL AND i.placa <> v.placa THEN 1 ELSE 0 END) AS leitura_incorreta_OCR  
      ,COUNT(i.id_infracao) AS [Total]  
      ,SUM(CASE WHEN i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]' THEN 1 ELSE 0 END) AS total_mercosul  
      ,SUM(CASE WHEN i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]' AND v.placa IS NOT NULL AND i.placa IS NOT NULL THEN 1 ELSE 0 END) AS placa_lida_digitada_mercosul  
      ,SUM(CASE WHEN i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]' AND v.placa IS NOT NULL AND i.placa = v.placa THEN 1 ELSE 0 END) AS leitura_correta_OCR_mercosul  
      ,SUM(CASE WHEN i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]' AND v.placa IS NOT NULL AND i.placa <> v.placa THEN 1 ELSE 0 END) AS leitura_incorreta_OCR_mercosul  
   FROM   infracao i (NOLOCK)  
     INNER JOIN veiculo v (NOLOCK)  
      ON  v.id_veiculo = i.id_veiculo  
     INNER JOIN inconsistencia inc (NOLOCK)  
      ON  inc.id_inconsistencia = i.id_inconsistencia  
     INNER JOIN local_vigente lv (NOLOCK)  
      ON  lv.id_local = i.id_local  
   WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim  
       AND lv.desativado = 0  
	   AND v.id_classe IN ('C','O','Q')
   GROUP BY  
       lv.id_local  
      ,lv.nome  
 ) RESULT  
  
)  

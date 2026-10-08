CREATE FUNCTION [dbo].[fcn_getRelatorioLeituraCorretaOCRMercosulFaixa] ( @dataInicio date, @dataFim date )  
RETURNS TABLE  
AS  
RETURN  
(  
   
 --DECLARE @dataInicio DATE = '2019-07-01', @dataFim DATE = '2019-07-31'  
 SELECT ROW_NUMBER() OVER(ORDER BY result.id_local, result.pista) AS [Ordem],  
     result.id_local AS [Id. local],  
     result.pista AS [Id. Pista],  
     result.desc_local AS [Desc. local],  
     result.veiculo_sem_placa AS [Veículo sem placa],  
     result.veiculo_com_placa AS [Veículo com placa],  
     result.placa_lida_mercosul AS [Placa lida Mercosul],  
     result.placa_lida_digitada AS [Placa lida e digitada],  
     result.leitura_correta_OCR AS [Leitura correta OCR],  
     result.Total,  
     CASE WHEN result.Total > 0  
       THEN CAST(result.veiculo_sem_placa AS FLOAT) / CAST(result.Total AS FLOAT)  
    ELSE 0 END AS [% Veículo sem placa],  
     CASE WHEN result.Total > 0  
    THEN CAST(result.veiculo_com_placa AS FLOAT) / CAST(result.Total AS FLOAT)  
    ELSE 0 END AS [% Veículo com placa],  
     CASE WHEN result.placa_lida_digitada > 0  
    THEN CAST(result.leitura_correta_OCR AS FLOAT) / CAST(result.placa_lida_digitada AS FLOAT)  
    ELSE 0 END AS [% Leitura correta OCR],  
     CASE WHEN result.placa_lida_digitada > 0  
    THEN CAST(result.leitura_incorreta_OCR AS FLOAT) / CAST(result.placa_lida_digitada AS FLOAT)  
    ELSE 0 END AS [% Leitura incorreta OCR]  
  
 FROM   (  
    --DECLARE @dataInicio DATE = '2019-07-01', @dataFim DATE = '2019-07-31'  
    SELECT lv.id_local,  
        i.pista,  
        RTRIM(lv.nome) AS desc_local,  
        SUM(CASE WHEN v.placa IS NULL THEN 1 ELSE 0 END) AS veiculo_sem_placa,  
        SUM(CASE WHEN v.placa IS NOT NULL THEN 1 ELSE 0 END) AS veiculo_com_placa,  
        SUM(CASE WHEN v.placa IS NOT NULL AND i.placa IS NOT NULL THEN 1 ELSE 0 END) AS placa_lida_digitada,  
        SUM(CASE WHEN v.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]' THEN 1 ELSE 0 END) AS placa_lida_mercosul,  
        SUM(CASE WHEN v.placa IS NOT NULL AND i.placa = v.placa THEN 1 ELSE 0 END) AS leitura_correta_OCR,  
        SUM(CASE WHEN v.placa IS NOT NULL AND i.placa <> v.placa THEN 1 ELSE 0 END) AS leitura_incorreta_OCR,  
        COUNT(i.id_infracao) AS [Total]  
    FROM   infracao i (NOLOCK)  
        INNER JOIN veiculo v (NOLOCK)  
       ON  v.id_veiculo = i.id_veiculo  
        INNER JOIN inconsistencia inc (NOLOCK)  
       ON  inc.id_inconsistencia = i.id_inconsistencia  
        INNER JOIN local_vigente lv (NOLOCK)  
       ON  lv.id_local = i.id_local  
    WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim  
        AND i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]'  
    GROUP BY  
        lv.id_local,  
        i.pista,  
        lv.nome  
 ) result  
  
)  

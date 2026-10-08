
CREATE FUNCTION [dbo].[fcn_getRelAfericaoInicioOperacao](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(  
   
 SELECT ROW_NUMBER() OVER(ORDER BY lv.id_local) AS [Ordem],  
     lv.id_local AS [Id. Local],  
     lv.serie_equipamento AS [Nº Série],  
     CASE WHEN CAST(lv.data_afericao AS DATE) < (SELECT TRY_CAST(RTRIM(valor) + '-01-01' AS DATE) AS data FROM chave_valor WHERE chave = 'ano_contrato') THEN 'N/D'  
    ELSE CONVERT(VARCHAR(10), lv.data_afericao, 103)  
     END AS [Data Aferição],  
     CASE WHEN CAST(lv.data_inicio AS DATE) > CAST(GETDATE() AS DATE) THEN 'N/D'  
    ELSE CONVERT(VARCHAR(10), lv.data_inicio, 103)  
     END AS [Início Operação]  
 FROM   local_vigente lv (NOLOCK)  
 WHERE  lv.desativado = 0  
  
)  

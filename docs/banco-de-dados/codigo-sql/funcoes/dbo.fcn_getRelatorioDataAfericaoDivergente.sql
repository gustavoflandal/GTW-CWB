
CREATE FUNCTION [dbo].[fcn_getRelatorioDataAfericaoDivergente](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
 RETURN   
 (  
  --DECLARE @dataInicio DATE = '2019-09-01', @dataFim DATE = '2019-09-30'  
  SELECT ROW_NUMBER() OVER(ORDER BY i.data, l.id_local, cep.id_pista) AS [Ordem],  
      i.id_infracao AS [Id. Infração],  
      CONVERT(VARCHAR(10), i.data, 103) + ' ' + CONVERT(VARCHAR(10), i.data, 108) AS [Data Infraçao],  
      RTRIM(p.nome) AS [Processo],  
      ce.serie_equipamento AS [Nº Série],  
      RTRIM(l.nome) AS [Equipamento],  
      cep.id_pista AS [Id. Pista],  
      CONVERT(VARCHAR(10), i.data_afericao, 103) AS [Data Aferição Tarja],  
      CONVERT(VARCHAR(10), cea.data, 103) AS [Data Aferição Servidor]  
  FROM   infracao i (NOLOCK)  
      INNER JOIN local l (NOLOCK)  
     ON  l.id_local = i.id_local  
      AND l.sequencia_local = i.sequencia_local  
      INNER JOIN configuracao_equipamento ce (NOLOCK)  
     ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento  
      INNER JOIN configuracao_equipamento_pista cep (NOLOCK)  
     ON  cep.id_configuracao_equipamento = ce.id_configuracao_equipamento  
      AND cep.id_pista = i.pista  
      INNER JOIN configuracao_equipamento_afericao cea (NOLOCK)  
     ON  cea.id_configuracao_equipamento = cep.id_configuracao_equipamento  
      AND cea.id_pista = cep.id_pista  
      INNER JOIN enquadramento e (NOLOCK)  
     ON  e.id_enquadramento = i.id_enquadramento  
      INNER JOIN processo p (NOLOCK)  
     ON  p.id_processo = i.id_processo  
  WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim  
      AND e.infracao_metrologia = 1  
      AND CAST(i.data_afericao AS DATE) != CAST(cea.data AS DATE)  
  
 )  

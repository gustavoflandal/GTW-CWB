
CREATE FUNCTION [dbo].[fcn_getRelatorioProcessamentoVelsis](@dataInicio DATE, @dataFim DATE)  
RETURNS @result TABLE (dia VARCHAR(10), serie_equipamento INT, equipamento CHAR(100), /*id_inconsistencia INT,*/ inconsistencia CHAR(70), quantidade INT)  
AS  
BEGIN  
  
 --DECLARE @result TABLE (dia VARCHAR(10), serie_equipamento INT, equipamento CHAR(100), /*id_inconsistencia INT,*/ inconsistencia CHAR(70), quantidade INT)  
 --DECLARE @dataInicio DATE = '2017-01-01', @dataFim DATE = '2017-01-31'  
  
 INSERT INTO @result  
 --DECLARE @dataInicio DATE = '2017-01-01', @dataFim DATE = '2017-01-31'  
 SELECT CONVERT(VARCHAR(10), CAST(i.data AS DATE), 103) AS dia  
   ,COALESCE(estatico.serie_equipamento, ce.serie_equipamento) AS serie_equipamento  
   ,lv.nome AS equipamento  
   --,incons.id_inconsistencia  
   ,incons.descricao AS inconsistência  
   ,COUNT(i.id_infracao) AS quantidade  
 FROM   infracao i (NOLOCK)  
   INNER JOIN veiculo v (NOLOCK) ON i.id_veiculo = v.id_veiculo  
   INNER JOIN local lv (NOLOCK) ON i.id_local = lv.id_local AND i.sequencia_local = lv.sequencia_local  
   INNER JOIN configuracao_equipamento ce (NOLOCK) ON lv.id_configuracao_equipamento = ce.id_configuracao_equipamento  
   INNER JOIN inconsistencia incons (NOLOCK) ON  incons.id_inconsistencia = i.id_inconsistencia  
   LEFT JOIN equipamento_estatico estatico (NOLOCK) ON v.codigo_prodam = estatico.codigo_prodam  
 WHERE 
 CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim  
 GROUP BY  
   CAST(i.data AS DATE)  
   ,COALESCE(estatico.serie_equipamento, ce.serie_equipamento)  
   ,lv.nome  
   --,incons.id_inconsistencia  
   ,incons.descricao  
 ORDER BY  
   CAST(i.data AS DATE)  
   ,serie_equipamento  
   --,incons.id_inconsistencia  
  
 RETURN  
  
END  

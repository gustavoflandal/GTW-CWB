CREATE FUNCTION [dbo].[fcn_getRelatorioVolume](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(-- Cálculo Baseado na tabela 'veiculo sumarizado'  
  
 --DECLARE @dataInicio DATE = '2015-03-01', @dataFim DATE = '2015-03-14'  
 SELECT  
  lv.serie_equipamento [N/S],  
  cep.cod_pista_alternativo AS [FX],  
  cep.nome_pista [NOME],  
  sub.DIA AS [DIA],  
  sub.[HORA],  
  sub.VOLUME  
 FROM  
  (SELECT  
   vs.id_local,  
   vs.pista,  
   vs.data AS [DIA],  
   vs.hora AS [HORA],  
   SUM (vs.trafego) AS [VOLUME]     
  FROM  
   veiculo_sumarizado vs (nolock)  
  WHERE  
   CAST(vs.data AS DATE) BETWEEN @dataInicio AND @dataFim  
     
   --Alterado O.S 101 - Auditoria CET  
   --Luiz Amaral 22/07/2015  
   --and cast(vs.data as date) > cast(dateadd(day, -45, getdate()) as date)  
  
  GROUP BY  
   vs.id_local,   
   vs.pista,   
   vs.data,   
   vs.hora  
  ) AS sub  
  INNER JOIN local_vigente lv (nolock)  
   ON lv.id_local = sub.id_local  
  INNER JOIN configuracao_equipamento_pista cep (nolock)  
   ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento  
   AND cep.id_pista = sub.pista   
  --WHERE CAST(lv.data_inicio AS DATE) < @dataInicio  
  -- AND (lv.data_fim IS NULL OR CAST(lv.data_fim AS DATE) >= @dataFim)  
  
) 

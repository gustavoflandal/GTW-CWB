
CREATE FUNCTION [dbo].[fcn_getRelatorioRegrasInfracaoEquipFaixa](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
 (  
  SELECT ROW_NUMBER() OVER(ORDER BY lv.id_local, ceri.tipo, ceri.id_pista) AS [Ordem],  
      lv.id_local AS [Id. Local],  
      lv.serie_equipamento AS [Nº Série],  
      LTRIM(RTRIM(lv.nome)) AS [Nome],  
      --cep.id_pista AS [Id. Pista],  
      --cep.cod_pista_alternativo AS [Pista Alternativo],  
      --cep.cod_pista_tarja AS [Pista Tarja],  
      ceri.tipo AS [Tipo],  
      CASE WHEN ceri.id_pista IS NULL THEN 'TODAS' ELSE CAST(ceri.id_pista AS CHAR(2)) END AS [Pista - Regra],  
      LEFT(CAST(ceri.hora_ini AS TIME), 8) AS [Hora Ini],  
      LEFT(CAST(ceri.hora_fim AS TIME), 8) AS [Hora Fim],  
      ceri.dia_ini AS [Dia Ini],  
      ceri.dia_fim AS [Dia Fim],  
      ceri.tolerancia AS [Tolerancia],  
      ceri.velocidade_limite AS [Vel. Limite],  
      CASE WHEN ceri.ativo = 1 THEN 'ATIVO' ELSE 'INATIVO' END AS [Status]  
  FROM   local_vigente lv (NOLOCK)  
      INNER JOIN configuracao_equipamento ce (NOLOCK)  
     ON  ce.id_configuracao_equipamento = lv.id_configuracao_equipamento  
     -- INNER JOIN configuracao_equipamento_pista cep (NOLOCK)  
     --ON  cep.id_configuracao_equipamento = ce.id_configuracao_equipamento  
      INNER JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK)  
     ON  ceri.id_configuracao_equipamento = lv.id_configuracao_equipamento  
      --AND (ceri.id_pista = cep.id_pista OR ceri.id_pista IS NULL)  
      INNER JOIN enquadramento_regra_infracao eri (NOLOCK)  
     ON  eri.tipo = ceri.tipo  
  WHERE  
	 ceri.ativo = 1  
      AND eri.id_enquadramento > 1  
      AND lv.desativado = 0  
 )  

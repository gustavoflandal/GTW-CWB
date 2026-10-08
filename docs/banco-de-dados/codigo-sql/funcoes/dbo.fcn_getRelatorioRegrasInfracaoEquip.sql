
CREATE FUNCTION [dbo].[fcn_getRelatorioRegrasInfracaoEquip](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(  
 SELECT ROW_NUMBER() OVER(ORDER BY lv.id_local, LTRIM(RTRIM(eri.tipo))) AS ordem,  
     lv.id_local,  
     lv.serie_equipamento,  
     LTRIM(RTRIM(lv.nome)) AS nome_local,  
     LTRIM(RTRIM(eri.tipo)) AS tipo_regra,  
     LTRIM(RTRIM(eri.descricao_apait)) AS descricao_regra,  
     CASE WHEN ceri.ativo = 1 THEN 'ATIVA' ELSE 'INATIVA' END AS status_regra  
 FROM   local_vigente lv (NOLOCK)  
     INNER JOIN configuracao_equipamento ce (NOLOCK)  
    ON  ce.id_configuracao_equipamento = lv.id_configuracao_equipamento  
     INNER JOIN configuracao_equipamento_pista cep (NOLOCK)  
    ON  cep.id_configuracao_equipamento = ce.id_configuracao_equipamento  
     INNER JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK)  
    ON  ceri.id_configuracao_equipamento = cep.id_configuracao_equipamento  
     AND (ceri.id_pista = cep.id_pista OR ceri.id_pista IS NULL)  
     --AND ceri.ativo = 1  
   INNER JOIN enquadramento_regra_infracao eri (NOLOCK)  
    ON  eri.tipo = ceri.tipo  
 WHERE  eri.id_enquadramento > 1  
     AND lv.desativado = 0  
 GROUP BY  
     lv.id_local,  
     lv.serie_equipamento,  
     LTRIM(RTRIM(lv.nome)),  
     LTRIM(RTRIM(eri.tipo)),  
     LTRIM(RTRIM(eri.descricao_apait)),  
     ceri.ativo  
)  

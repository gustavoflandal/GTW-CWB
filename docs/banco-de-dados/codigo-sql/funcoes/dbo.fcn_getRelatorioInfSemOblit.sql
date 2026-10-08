
CREATE FUNCTION [dbo].[fcn_getRelatorioInfSemOblit] (@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
  
 --DECLARE @dataInicio DATE = '2015-11-19', @dataFim DATE = '2015-11-19'  
 SELECT lv.serie_equipamento,  
     inf.id_infracao,  
     ii.id_imagem_obj,  
     inf.data,  
     inf.id_enquadramento,  
     inf.placa,  
     v.id_classe,  
     pr.nome as processo,  
     lv.nome  
 FROM   dbo.infracao inf (NOLOCK)  
     INNER JOIN dbo.veiculo v (NOLOCK)  
    ON  inf.id_veiculo = v.id_veiculo  
     INNER JOIN dbo.processo pr (NOLOCK)  
    ON  inf.id_processo = pr.id_processo  
     INNER JOIN dbo.local_vigente lv (NOLOCK)   
    ON  inf.id_local = lv.id_local  
     AND inf.sequencia_local = lv.sequencia_local  
     INNER JOIN dbo.configuracao_equipamento_pista cep (NOLOCK)   
    ON  cep.id_configuracao_equipamento = lv.id_configuracao_equipamento  
     AND inf.pista = cep.id_pista  
     INNER JOIN dbo.infracao_imagem ii (NOLOCK)  
    ON  inf.id_infracao = ii.id_infracao  
     LEFT JOIN dbo.infracao_obliteracao ob (NOLOCK)  
    ON  inf.id_infracao = ob.id_infracao  
 WHERE  inf.id_processo_concluido IS NOT NULL  
     AND (cep.captura_obj_frente = 1) 
     AND inf.id_inconsistencia = 0  
     AND inf.id_enquadramento <> 1  
     AND (inf.id_processo > 1 AND inf.id_processo NOT IN (20, 21, 22))  
     AND CAST(inf.data AS DATE) BETWEEN @dataInicio AND @dataFim  
     AND ob.id_infracao IS NULL  

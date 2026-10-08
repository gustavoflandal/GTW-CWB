CREATE FUNCTION [dbo].[fcn_InfracaoDisponivelContestacao] 
  ( @id_processo_contestacao INT ) 
RETURNS TABLE 
AS 
RETURN 
(
--DECLARE @id_processo_contestacao INT = 1
SELECT TOP 100 PERCENT 
	i.id_infracao,  
	i.data,  
	i.id_enquadramento,  
	i.id_local,  
	i.pista, 
	RTRIM(COALESCE(cp.nome_pista, l.nome)) AS nome_pista,  
	espera, 
	i.id_inconsistencia, 
	i.id_usuario_atual, 
	ir.id_remessa,
	null AS id_infracao_amostra   
FROM infracao i (nolock)
	JOIN infracao_remessa ir (NOLOCK) 
		ON i.id_infracao = ir.id_infracao
	LEFT JOIN local l (nolock) 
		ON i.id_local = l.id_local AND i.sequencia_local = l.sequencia_local  
	LEFT JOIN configuracao_equipamento_pista cp (nolock) 
		ON cp.id_configuracao_equipamento = l.id_configuracao_equipamento AND cp.id_pista = i.pista
	JOIN infracao_contestacao ic (NOLOCK)
		ON i.id_infracao = ic.id_infracao
	LEFT JOIN infracao_janela ij (NOLOCK)
		ON i.id_infracao = ij.id_infracao
WHERE	ic.id_processo_contestacao = @id_processo_contestacao AND ij.id_infracao IS NULL
ORDER BY i.data
)


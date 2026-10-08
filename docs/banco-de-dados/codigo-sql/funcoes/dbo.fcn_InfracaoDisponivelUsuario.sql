CREATE FUNCTION [dbo].[fcn_InfracaoDisponivelUsuario] 
  ( @id_processo int, 
    @id_usuario int ) 
RETURNS TABLE 
AS 
RETURN 
(
--DECLARE @id_processo INT = 1, @id_usuario INT = 5
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
	LEFT JOIN local l (nolock) 
		ON i.id_local = l.id_local AND i.sequencia_local = l.sequencia_local  
	LEFT JOIN configuracao_equipamento_pista cp (nolock) 
		ON cp.id_configuracao_equipamento = l.id_configuracao_equipamento AND cp.id_pista = i.pista
	JOIN processo p (nolock) 
		ON p.id_processo = i.id_processo
	LEFT JOIN infracao_remessa ir (nolock) 
		ON ir.id_infracao = i.id_infracao 
	LEFT JOIN remessa r (NOLOCK) 
		ON ir.id_remessa = r.id_remessa 
	LEFT JOIN agendamento_processamento ap (nolock) 
		ON ap.id_infracao = i.id_infracao 
WHERE	ap.id_infracao IS NULL		 -- que não estão agendadas
	AND r.data_validacao IS NULL     -- que não foram validadas ainda
	AND i.id_processo = @id_processo -- que sejam do processo desejado
	AND (i.id_usuario_atual = @id_usuario 
		OR (i.id_usuario_atual IS NULL 
		AND ((p.numero_iteracoes_consistentes <= 1 AND p.numero_iteracoes_inconsistentes <= 1) 
		OR	(i.espera = 1 OR NOT @id_usuario IN ( 	SELECT top 2 
														ip.id_usuario 
													FROM 
														infracao_processo ip (nolock)
													WHERE	ip.id_infracao = i.id_infracao 
														AND ip.id_processo = i.id_processo 
														AND ip.status_processo = 0 
													ORDER BY id_infracao_processo DESC)
		OR @id_usuario IS NULL)))
		)
ORDER BY i.data
)

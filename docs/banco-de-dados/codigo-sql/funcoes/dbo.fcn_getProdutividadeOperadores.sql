CREATE FUNCTION [dbo].[fcn_getProdutividadeOperadores]()
RETURNS TABLE
AS
RETURN
(
	SELECT 
		CAST( data as DATE) as data,
		ip.id_usuario,
		su.nome as [Usuario],
		ip.id_processo,
		p.nome as [Processo],
		COUNT(*) as [Total]
	FROM infracao_processo ip (nolock)
		INNER JOIN infracao_processo_usuario ipu (nolock) -- usar apenas o último processamento do usuário da infração
			ON	ipu.id_infracao = ip.id_infracao 
			AND	ipu.id_infracao_processo = ip.id_infracao_processo 
			AND ipu.id_usuario = ip.id_usuario
		INNER JOIN sis_usuario su (nolock) 
			ON su.id_usuario = ip.id_usuario
		INNER JOIN processo p (nolock) 
			ON p.id_processo = ip.id_processo
	GROUP BY
		CAST( data as DATE),
		ip.id_usuario,
		ip.id_processo,
		su.nome,
		p.nome
)





--CREATE 
CREATE 
FUNCTION [dbo].[fcn_getRelatorioValidInvalid](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		SUM(CASE WHEN ipv.id_inconsistencia = ipc_l.id_inconsistencia THEN 1 ELSE 0 END) AS [Válidas],
		SUM(CASE WHEN ipv.id_inconsistencia = ipc_l.id_inconsistencia THEN 0 ELSE 1 END) AS [Inválidas],
		i.id_enquadramento AS [Enquadramento],
		suv.nome AS [Auditor],
		CASE 
			WHEN ipc_l.id_inconsistencia <> ipv.id_inconsistencia 
				THEN incv.descricao 
			ELSE 
				'Válida' 
		END AS [Motivo da Invalidade],
		CONVERT(INT,r.codigo_externo) AS [Movimento de Lote] 
	FROM infracao i (nolock) 
		INNER JOIN infracao_remessa ir (nolock) 
			ON i.id_infracao = ir.id_infracao 
		INNER JOIN remessa r (nolock) 
			ON r.id_remessa = ir.id_remessa 
		INNER JOIN infracao_processo_concluido ipc_l (nolock) 
			ON i.id_infracao = ipc_l.id_infracao AND ipc_l.id_processo = 11 
		INNER JOIN (SELECT MAX(id_infracao_processo) AS id_infracao_processo, id_infracao 
						FROM infracao_processo (nolock)
						WHERE id_processo = 3 
						GROUP BY id_infracao) AS sub1 
			ON sub1.id_infracao = i.id_infracao
		INNER JOIN infracao_processo ipv (nolock) 
			ON sub1.id_infracao_processo = ipv.id_infracao_processo 
		INNER JOIN inconsistencia incv (nolock) 
			ON incv.id_inconsistencia = ipv.id_inconsistencia 
		INNER JOIN sis_usuario suv (nolock) 
			ON suv.id_usuario = ipv.id_usuario 
	WHERE 
		CONVERT(DATE, i.data) BETWEEN @dataInicio AND @dataFim 
	GROUP BY 
		i.id_enquadramento, 
		suv.nome , 
		CASE 
			WHEN ipc_l.id_inconsistencia <> ipv.id_inconsistencia 
				THEN incv.descricao 
			ELSE 
				'Válida' 
		END, 
		r.codigo_externo
)






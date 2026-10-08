
--CREATE  
CREATE 
FUNCTION [dbo].[fcn_getRelatorioConsIncons](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		SUM(CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS [Consistentes], 
		SUM(CASE WHEN i.id_inconsistencia = 0 THEN 0 ELSE 1 END) AS [Inconsistentes],
		i.id_enquadramento AS										[Enquadramento],
		su.nome AS													[Operador],
		inc.descricao AS											[Motivo da Inconsistência], 
		CASE 
			WHEN incv.descricao IS NULL OR inc.descricao <> incv.descricao 
				THEN COALESCE(incv.descricao, 'N/A') 
			ELSE 
				'Válida' 
		END AS [Invalidação da Imagem],	 
		CONVERT(INT,r.codigo_externo) AS [Movimento de Lote]   
	FROM infracao i (nolock) 
		INNER JOIN infracao_remessa ir (nolock) 
			ON i.id_infracao = ir.id_infracao 
		INNER JOIN remessa r (nolock) 
			ON r.id_remessa = ir.id_remessa 
		INNER JOIN (SELECT MAX(id_infracao_processo) AS id_infracao_processo, id_infracao 
						FROM infracao_processo (nolock)
						WHERE	id_processo = 1 
							OR	id_processo = 2 
						GROUP BY id_infracao) AS sub1 
			ON sub1.id_infracao = i.id_infracao
		LEFT JOIN (	SELECT MAX(id_infracao_processo) AS id_infracao_processo, id_infracao 
						FROM infracao_processo (nolock)
						WHERE id_processo = 3 
						GROUP BY id_infracao) AS sub2 
			ON sub2.id_infracao = i.id_infracao
		INNER JOIN infracao_processo ip (nolock) 
			ON sub1.id_infracao_processo = ip.id_infracao_processo 
		LEFT JOIN infracao_processo ipv (nolock) 
			ON sub2.id_infracao_processo = ipv.id_infracao_processo 
		INNER JOIN sis_usuario su (nolock) 
			ON su.id_usuario = ip.id_usuario
		INNER JOIN inconsistencia inc (nolock) 
			ON inc.id_inconsistencia = ip.id_inconsistencia
		LEFT JOIN inconsistencia incv (nolock) 
			ON incv.id_inconsistencia = ipv.id_inconsistencia 
	WHERE CONVERT(DATE,i.data) BETWEEN @dataInicio AND @dataFim 
	GROUP BY 
		i.id_enquadramento, 
		su.nome, 
		inc.descricao,
		incv.descricao, 
		r.codigo_externo  
)






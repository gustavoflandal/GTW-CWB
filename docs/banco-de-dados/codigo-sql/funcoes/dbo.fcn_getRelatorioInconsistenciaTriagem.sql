CREATE FUNCTION [dbo].[fcn_getRelatorioInconsistenciaTriagem](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		lv.serie_equipamento AS [NS]
		,cep.nome_pista AS [LOCAL]
		,cep.cod_pista_alternativo AS [FX]
		,i.id_enquadramento AS [ENQ]
		,inc.descricao AS [MOTIVO]
		,(CASE WHEN inc.razao_tecnica = 2 THEN 'PT' ELSE 'PNT' END) AS [PT/PNT]
		,CAST(i.data AS DATE) AS DATA
		,DATEPART(hh,i.data)AS HORA
		,COUNT(*) AS IMAGENS
	FROM 
		infracao_processo_concluido ipc
		JOIN infracao i
			ON i.id_infracao = ipc.id_infracao
		JOIN local_vigente lv
			ON lv.id_local = i.id_local
		JOIN configuracao_equipamento_pista cep
			ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento AND cep.id_pista = i.pista
		JOIN inconsistencia inc
			ON inc.id_inconsistencia = ipc.id_inconsistencia
		LEFT JOIN infracao_remessa ir (NOLOCK)
			ON ir.id_infracao = i.id_infracao
		LEFT JOIN remessa r (NOLOCK)
			ON r.id_remessa = ir.id_remessa
	WHERE
		ipc.id_processo = 1
		AND ipc.id_inconsistencia > 0
		AND CAST(ipc.data_conclusao AS DATE) >= @dataInicio
		AND CAST(ipc.data_conclusao AS DATE) <= @dataFim


		-- Alterado O.S. 101 - Auditoria CET
		-- Thiago Surgik - 22/07/2015
		AND CAST(i.data AS DATE) >= CAST(DATEADD(DAY, -45, GETDATE()) AS DATE)
		AND r.data_validacao IS NULL
		
	GROUP BY
		lv.serie_equipamento
		,cep.nome_pista 
		,cep.cod_pista_alternativo
		,i.id_enquadramento
		,inc.descricao
		,(CASE WHEN inc.razao_tecnica = 2 THEN 'PT' ELSE 'PNT' END)
		,CAST(i.data AS DATE)
		,DATEPART(hh,i.data)

);

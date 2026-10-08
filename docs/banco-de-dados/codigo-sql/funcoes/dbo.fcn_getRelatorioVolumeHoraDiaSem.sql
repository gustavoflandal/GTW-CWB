CREATE FUNCTION [dbo].[fcn_getRelatorioVolumeHoraDiaSem]
  ( @dataInicio date,
    @dataFim date )
RETURNS TABLE
AS

RETURN
(
	SELECT
		lv.serie_equipamento AS [NS],
		RTRIM(cep.nome_pista) AS [LOCAL],
		(CASE 
			WHEN DATEPART(DW,vs.data) = 1 
				THEN 'DOM'
			WHEN DATEPART(DW,vs.data) = 2 
				THEN 'SEG'
			WHEN DATEPART(DW,vs.data) = 3
				THEN 'TER'
			WHEN DATEPART(DW,vs.data) = 4 
				THEN 'QUA'
			WHEN DATEPART(DW,vs.data) = 5 
				THEN 'QUI'
			WHEN DATEPART(DW,vs.data) = 6 
				THEN 'SEX'
			WHEN DATEPART(DW,vs.data) = 7 
				THEN 'SAB' 
		END) AS [DDS],
		vs.hora AS [HORA],
		SUM(vs.trafego) as [VOLUME]
	FROM veiculo_sumarizado vs (nolock)
		INNER JOIN local_vigente lv (nolock)
			ON lv.id_local = vs.id_local
		INNER JOIN configuracao_equipamento ce (nolock)
			ON ce.id_configuracao_equipamento = lv.id_configuracao_equipamento
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON	lv.id_configuracao_equipamento = cep.id_configuracao_equipamento 
			AND	cep.id_pista = vs.pista
	WHERE	cast(vs.data as date) >= @dataInicio
		AND cast(vs.data as date) <= @dataFim
	GROUP BY
		lv.serie_equipamento,
		cep.nome_pista,
		vs.hora,
		DATEPART(DW,vs.data)
)





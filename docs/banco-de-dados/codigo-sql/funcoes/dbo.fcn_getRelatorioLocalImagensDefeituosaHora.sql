


CREATE FUNCTION [dbo].[fcn_getRelatorioLocalImagensDefeituosaHora](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT
		id_local,
		pista as id_pista,
		CAST(data as DATE) as data,
		DATEPART(HOUR, data) AS hora,
		COUNT(*) as trafego,
		SUM(case when (flag & 4194304) <> 0 then 1 else 0 end) as trafegoImagemCapturada,
		SUM(case when (flag & 8388608) <> 0 then 1 else 0 end) as trafegoOCRprocessado,
		SUM(case when (flag & 4194304) <> 0  and (flag & 2097152) <> 0 then 1 else 0 end) as trafegoImagemDef,
		SUM(case when (flag & 8388608) <> 0  and (placa is null) then 1 else 0 end) as sem_placa_reconhecida
	FROM
		(
			SELECT id_local, pista, flag, data, placa FROM veiculo_pesquisa with(nolock)
		UNION
			SELECT id_local, pista, flag, data, placa FROM veiculo_importacao with(nolock)
		) AS v
		
	WHERE
		CAST(v.data AS DATE) >= @dataInicio AND CAST(v.data AS DATE) <= @dataFim
	GROUP BY 
		id_local, pista, CAST(data as DATE), DATEPART(HOUR, data)
)






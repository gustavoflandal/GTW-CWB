
CREATE FUNCTION [dbo].[fcn_LocalImagensDefeituosa] (@DataInicial DATETIME, @DataFinal DATETIME, @HoraInicial TIME, @HoraFinal TIME)
RETURNS TABLE
AS
RETURN
(
-- DECLARE @DataInicial DATETIME, @DataFinal DATETIME, @HoraInicial TIME, @HoraFinal TIME
	SELECT
		id_local,
		pista as id_pista,
		COUNT(*) as trafego,
		SUM(case when (flag & 4194304) <> 0 then 1 else 0 end) as trafegoImagemCapturada,
		SUM(case when (flag & 8388608) <> 0 then 1 else 0 end) as trafegoOCRprocessado,
		SUM(case when (flag & 4194304) <> 0  and (flag & 2097152) <> 0 then 1 else 0 end) as trafegoImagemDef,
		SUM(case when (flag & 8388608) <> 0  and (placa is null) then 1 else 0 end) as sem_placa_reconhecida
	FROM
		--(
		--	SELECT id_local, pista, flag, data, placa FROM veiculo_pesquisa (nolock)
		--UNION
		--	SELECT id_local, pista, flag, data, placa FROM veiculo_importacao (nolock)
		--) AS v
		veiculo_pesquisa (nolock)
		
	WHERE
		data >= @DataInicial and data <= @DataFinal
		AND 
		(
			(
				@HoraInicial <= @HoraFinal
				AND 
				convert(char(5), data, 108) >= cast(@HoraInicial as char(5)) 
				and convert(char(5), data, 108) < cast(@Horafinal as char(5)) 
			) 
			OR
			(
				@HoraInicial > @HoraFinal
				AND NOT (
					convert(char(5), data, 108) >= cast(@Horafinal as char(5)) 
					and convert(char(5), data, 108) < cast(@HoraInicial as char(5)) 
					)
			)
		)
		
	GROUP BY 
		id_local, pista
)

CREATE FUNCTION [dbo].[fcn_getRelatorioProcProdutividadeSumarizado](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	--Processamento: Produtividade
	--DECLARE @dataInicio DATE = '2017-10-23', @dataFim DATE = '2017-10-27'
	SELECT ROW_NUMBER() OVER(ORDER BY Data, Usuario) AS [Ordem],
		   [Data],
		   [Usuario],
		   [Digitação],
		   [Digitação Supervisor],
		   [Triagem],
		   ROUND(CAST([Triagem] AS FLOAT) / 3, 2) AS [Triagem / 3]
	FROM   (
				SELECT CAST(CAST(ip.data AS DATE) AS DATETIME) AS [Data],
					   RTRIM(p.nome) AS [Processo],
					   RTRIM(u.nome) AS [Usuario],
					   COUNT(ip.id_infracao_processo) AS Total
				FROM   infracao_processo ip (NOLOCK)
					   INNER JOIN infracao_processo_usuario ipu (NOLOCK)
							ON	ipu.id_infracao_processo = ip.id_infracao_processo
								AND ipu.id_infracao = ip.id_infracao
								AND ipu.id_processo = ip.id_processo
								AND ipu.id_usuario = ip.id_usuario
					   INNER JOIN infracao i (NOLOCK)
							ON  i.id_infracao = ip.id_infracao
					   INNER JOIN processo p (NOLOCK)
							ON  p.id_processo = ip.id_processo
					   INNER JOIN sis_usuario u (NOLOCK)
							ON  u.id_usuario = ip.id_usuario
					   INNER JOIN inconsistencia inc (NOLOCK)
							ON  i.id_inconsistencia = inc.id_inconsistencia
				WHERE  CAST(ip.data AS DATE) BETWEEN @dataInicio AND @dataFim
					   AND ip.tempo > 0 
				GROUP BY
					   CAST(ip.data AS DATE),
					   RTRIM(p.nome),
					   RTRIM(u.nome)
	) AS tempo
	PIVOT (
				SUM(tempo.Total)
				FOR tempo.Processo IN ([Digitação],[Digitação Supervisor],[Triagem])
		  ) AS cont_tempo

)

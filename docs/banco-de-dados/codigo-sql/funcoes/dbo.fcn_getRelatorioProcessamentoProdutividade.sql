CREATE FUNCTION [dbo].[fcn_getRelatorioProcessamentoProdutividade](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	--Processamento: Produtividade
	--DECLARE @dataInicio DATE = '2017-10-27', @dataFim DATE = '2017-10-27'
	SELECT CAST(CAST(ip.data AS DATE) AS DATETIME) AS [DATA],
		   DATEPART(HOUR,ip.data) AS [HORA],
		   DATEPART(MINUTE,ip.data) AS [MINUTO],
		   i.id_enquadramento AS [ENQ],
		   RTRIM(p.nome) AS [PROCESSO],
		   RTRIM(u.nome) AS [USUARIO],
		   RTRIM(inc.descricao) AS [MOTIVO],
		   COUNT(ip.id_infracao_processo) AS TOTAL,
		   ROUND(CAST((CAST(COUNT(ip.id_infracao_processo) AS FLOAT) / 3) AS FLOAT), 2) AS TOTAL_3,
		   AVG(tempo-tempo_cliente) AS MEDIA_TEMPO_SISTEMA,
		   AVG(tempo_cliente) AS MEDIA_TEMPO_USUARIO
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
		   DATEPART(HOUR,ip.data),
		   DATEPART(MINUTE,ip.data),
		   i.id_enquadramento,
		   RTRIM(p.nome),
		   RTRIM(u.nome),
		   RTRIM(inc.descricao)

)

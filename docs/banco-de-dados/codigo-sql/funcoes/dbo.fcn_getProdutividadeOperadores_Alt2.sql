CREATE FUNCTION [dbo].[fcn_getProdutividadeOperadores_Alt2](@id_usuario INTEGER, @data_ini DATETIME, @data_fim DATETIME)
RETURNS TABLE 
AS
RETURN 
(
	--DECLARE @data_ini DATETIME = '2016-08-04 00:00:00', @data_fim DATETIME = '2016-08-04 23:59:59'
	SELECT CAST(ip.data AS DATE) AS data,
		   ip.id_usuario,
		   su.nome AS [Usuario],
		   ip.id_processo,
		   p.nome AS [Processo],
		   COUNT(*) AS [Total]
	FROM   infracao_processo ip (NOLOCK)
           INNER JOIN infracao_processo_usuario ipu (NOLOCK)
				ON	ipu.id_infracao_processo = ip.id_infracao_processo
					AND ipu.id_infracao = ip.id_infracao
					AND ipu.id_processo = ip.id_processo
					AND ipu.id_usuario = ip.id_usuario
		   INNER JOIN sis_usuario su (NOLOCK) 
				ON  su.id_usuario  = ip.id_usuario
		   INNER JOIN processo p (NOLOCK) 
				ON  p.id_processo = ip.id_processo
	WHERE  ip.id_usuario = @id_usuario 
		   AND ip.data >= @data_ini 
		   AND ip.data <= @data_fim
		   AND ip.tempo > 0
	GROUP BY
		   CAST(ip.data AS DATE),
		   ip.id_usuario,
		   ip.id_processo,
		   su.nome,
		   p.nome

)

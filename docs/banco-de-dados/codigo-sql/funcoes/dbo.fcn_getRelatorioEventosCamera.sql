CREATE FUNCTION [dbo].[fcn_getRelatorioEventosCamera](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(  

	--DECLARE @dataInicio DATE = '2021-06-01', @dataFim DATE = '2021-06-09'
	SELECT CAST(ev.data_hora AS DATE) AS [Dia],
		   evp.proprietario AS [Equipamento],
		   CASE WHEN CHARINDEX('IP:', ev.mensagem) = 0 THEN NULL ELSE LTRIM(RTRIM(SUBSTRING(ev.mensagem, CHARINDEX('IP:', ev.mensagem) + LEN('IP:'), LEN(ev.mensagem) - CHARINDEX('IP:', ev.mensagem)))) END AS [IP Câmera],
		   COUNT(*) AS [Eventos Erro Comunicacao]
	--SELECT *,
	--	   CASE WHEN CHARINDEX('IP:', ev.mensagem) = 0 THEN NULL ELSE LTRIM(RTRIM(SUBSTRING(ev.mensagem, CHARINDEX('IP:', ev.mensagem) + LEN('IP:'), LEN(ev.mensagem) - CHARINDEX('IP:', ev.mensagem)))) END AS [IP Câmera]
	FROM   eventos_csx ev (NOLOCK)
		   JOIN eventos_csx_desc_proprietario evp (NOLOCK) 
				ON  ev.id_proprietario = evp.id_proprietario
		   JOIN eventos_csx_desc_evento ecde (NOLOCK)
				ON  ecde.id_evento = ev.id_evento
	WHERE  CAST(ev.data_hora AS DATE) BETWEEN @dataInicio and @dataFim
		   AND ev.id_evento = 66
		   AND ISNUMERIC(evp.proprietario) = 1
		   AND (
					ev.mensagem LIKE 'Perda%de%conex%o.%A%ITSCAM%parou%de%responder%completamente%'
					OR
					ev.mensagem LIKE 'IP:%'
					OR
					ev.mensagem = ''
			   )
		   --AND evp.proprietario = '2300001'
	GROUP BY
		   CAST(ev.data_hora AS DATE),
		   evp.proprietario,
		   CASE WHEN CHARINDEX('IP:', ev.mensagem) = 0 THEN NULL ELSE LTRIM(RTRIM(SUBSTRING(ev.mensagem, CHARINDEX('IP:', ev.mensagem) + LEN('IP:'), LEN(ev.mensagem) - CHARINDEX('IP:', ev.mensagem)))) END
	--ORDER BY
	--	   ev.data_hora DESC

) 

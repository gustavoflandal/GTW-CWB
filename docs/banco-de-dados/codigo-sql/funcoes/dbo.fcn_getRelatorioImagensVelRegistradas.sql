CREATE FUNCTION [dbo].[fcn_getRelatorioImagensVelRegistradas]
(
	@dataInicio     DATE
   ,@dataFim        DATE
)
RETURNS TABLE
AS
	RETURN 
	(
		--DECLARE @dataInicio DATE = '2019-05-01', @dataFim DATE = '2019-06-30'
		SELECT ROW_NUMBER() OVER(ORDER BY a.imagens_registradas) AS [Ordem],
			   a.serie_equipamento AS [Nº Série],
			   a.endereco AS [Endereço],
			   a.imagens_registradas AS [Imagens registradas]
		FROM   (
					--DECLARE @dataInicio DATE = '2019-05-01', @dataFim DATE = '2019-06-30'
					SELECT ce.serie_equipamento,
						   LTRIM(RTRIM(l.nome)) AS endereco,
						   --RIGHT('00'+CAST(MONTH(i.data) AS VARCHAR(2)), 2) + '/' + CAST(YEAR(i.data) AS VARCHAR(4)) AS mes_ano,
						   COUNT(*) AS imagens_registradas
						   --SUM(CASE WHEN i.id_inconsistencia = 0 AND r.id_remessa IS NOT NULL THEN 1 ELSE 0 END) AS consistentes_cai,
						   --SUM(CASE WHEN mi.validacao = 1 THEN 0 ELSE 1 END) AS consistentes_cav
					FROM   infracao i (NOLOCK)
						   INNER JOIN local l (NOLOCK)
								ON  l.id_local = i.id_local
									AND l.sequencia_local = i.sequencia_local
						   INNER JOIN configuracao_equipamento ce (NOLOCK)
								ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento
						   INNER JOIN enquadramento e (NOLOCK)
								ON  e.id_enquadramento = i.id_enquadramento
						  -- LEFT JOIN infracao_remessa ir (NOLOCK)
								--ON  ir.id_infracao = i.id_infracao
						  -- LEFT JOIN remessa r (NOLOCK)
								--ON  r.id_remessa = ir.id_remessa
						  -- LEFT JOIN movimento_importacao mi (NOLOCK)
								--ON  mi.data_movimento = r.data
								--	AND mi.id_movimento = r.codigo_externo
								--	AND mi.sequencia = ir.sequencia
								--	AND mi.id_enquadramento = i.id_enquadramento
					WHERE  i.id_enquadramento IN (74550,74630,74710)
						   AND CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
					GROUP BY
						   ce.serie_equipamento,
						   LTRIM(RTRIM(l.nome))
						   --MONTH(i.data),
						   --YEAR(i.data)
		) a

	)

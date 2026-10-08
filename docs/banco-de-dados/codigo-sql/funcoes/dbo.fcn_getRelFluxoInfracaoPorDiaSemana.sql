CREATE FUNCTION [dbo].[fcn_getRelFluxoInfracaoPorDiaSemana](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE    
AS    
RETURN    
(    
  
	--DECLARE @dataInicio DATE = '2019-01-01', @dataFim DATE = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE)

	WITH dados_relatorio AS
	(
		--DECLARE @dataInicio DATE = '2019-01-01', @dataFim DATE = CAST(DATEADD(DAY, -2, GETDATE()) AS DATE)
		SELECT DATEPART(WEEKDAY, vsr.dia) AS dia_semana,
			   DATENAME(WEEKDAY, vsr.dia) AS dia_semana_desc,
			   (SELECT value FROM master.dbo.fn_split_string(dbo.fcn_InitCap(FORMAT(vsr.dia, 'dddd', 'pt-BR')) , '-') WHERE id = 1) AS dia_semana_abreviado,
			   DATEPART(WEEK, vsr.dia) AS semana_ano,
			   DATEPART(YEAR, vsr.dia) AS ano,
			   vsr.id_local,
			   vsr.veiculos_detectados AS fluxo,
			   vsr.infracoes_registradas AS infracao,
			   vsr.imagens_teste_registradas AS imagem_teste
		FROM   veiculo_sumarizado_relatorio vsr (NOLOCK)
		WHERE  vsr.dia BETWEEN @dataInicio AND @dataFim
	),
	dados_fluxo_veicular AS
	(
		SELECT semana_ano,
			   ano,
			   id_local,
			   [Fluxo - Domingo],[Fluxo - Segunda],[Fluxo - Terça],[Fluxo - Quarta],[Fluxo - Quinta],[Fluxo - Sexta],[Fluxo - Sábado]
		FROM   (
					SELECT 'Fluxo - ' + d.dia_semana_abreviado AS item,
						   d.semana_ano,
						   d.ano,
						   d.id_local,
						   d.fluxo AS fluxo
					FROM   dados_relatorio d
				) r
		PIVOT  (
					SUM (r.fluxo)
					FOR item IN ([Fluxo - Domingo],[Fluxo - Segunda],[Fluxo - Terça],[Fluxo - Quarta],[Fluxo - Quinta],[Fluxo - Sexta],[Fluxo - Sábado])
				) cont_r
	),
	dados_infracao AS
	(
		SELECT semana_ano,
			   ano,
			   id_local,
			   [Infração - Domingo],[Infração - Segunda],[Infração - Terça],[Infração - Quarta],[Infração - Quinta],[Infração - Sexta],[Infração - Sábado]
		FROM   (
					SELECT 'Infração - ' + d.dia_semana_abreviado AS item,
						   d.semana_ano,
						   d.ano,
						   d.id_local,
						   d.infracao AS infracao
					FROM   dados_relatorio d
				) r
		PIVOT  (
					SUM (r.infracao)
					FOR item IN ([Infração - Domingo],[Infração - Segunda],[Infração - Terça],[Infração - Quarta],[Infração - Quinta],[Infração - Sexta],[Infração - Sábado])
				) cont_r
	),
	dados_imagem_teste AS
	(
		SELECT semana_ano,
			   ano,
			   id_local,
			   [Img. Teste - Domingo],[Img. Teste - Segunda],[Img. Teste - Terça],[Img. Teste - Quarta],[Img. Teste - Quinta],[Img. Teste - Sexta],[Img. Teste - Sábado]
		FROM   (
					SELECT 'Img. Teste - ' + d.dia_semana_abreviado AS item,
						   d.semana_ano,
						   d.ano,
						   d.id_local,
						   d.imagem_teste AS imagem_teste
					FROM   dados_relatorio d
				) r
		PIVOT  (
					SUM (r.imagem_teste)
					FOR item IN ([Img. Teste - Domingo],[Img. Teste - Segunda],[Img. Teste - Terça],[Img. Teste - Quarta],[Img. Teste - Quinta],[Img. Teste - Sexta],[Img. Teste - Sábado])
				) cont_r
	)

	--SELECT * FROM dados_imagem_teste

	SELECT ROW_NUMBER() OVER(ORDER BY serie_equipamento, ano, semana_ano) AS [Item],
		   semana_ano_desc AS [Semana - Ano],
		   serie_equipamento AS [Nº Série],
		   [Fluxo - Domingo],[Infração - Domingo],[Img. Teste - Domingo],
		   [Fluxo - Segunda],[Infração - Segunda],[Img. Teste - Segunda],
		   [Fluxo - Terça],[Infração - Terça],[Img. Teste - Terça],
		   [Fluxo - Quarta],[Infração - Quarta],[Img. Teste - Quarta],
		   [Fluxo - Quinta],[Infração - Quinta],[Img. Teste - Quinta],
		   [Fluxo - Sexta],[Infração - Sexta],[Img. Teste - Sexta],
		   [Fluxo - Sábado],[Infração - Sábado],[Img. Teste - Sábado],
		   [Total Fluxo],
		   [Total Infração],
		   [Total Img. Teste]
	FROM   (
				SELECT p.semana_ano,
					   p.ano,
					   p.semana_ano_desc,
					   p.serie_equipamento,
					   [Fluxo - Domingo],[Fluxo - Segunda],[Fluxo - Terça],[Fluxo - Quarta],[Fluxo - Quinta],[Fluxo - Sexta],[Fluxo - Sábado],
					   [Infração - Domingo],[Infração - Segunda],[Infração - Terça],[Infração - Quarta],[Infração - Quinta],[Infração - Sexta],[Infração - Sábado],
					   [Img. Teste - Domingo],[Img. Teste - Segunda],[Img. Teste - Terça],[Img. Teste - Quarta],[Img. Teste - Quinta],[Img. Teste - Sexta],[Img. Teste - Sábado],
					   (SELECT SUM(c) FROM (VALUES([Fluxo - Domingo]),([Fluxo - Segunda]),([Fluxo - Terça]),([Fluxo - Quarta]),([Fluxo - Quinta]),([Fluxo - Sexta]),([Fluxo - Sábado])) T (c)) AS [Total Fluxo],
					   (SELECT SUM(c) FROM (VALUES([Infração - Domingo]),([Infração - Segunda]),([Infração - Terça]),([Infração - Quarta]),([Infração - Quinta]),([Infração - Sexta]),([Infração - Sábado])) T (c)) AS [Total Infração],
					   (SELECT SUM(c) FROM (VALUES([Img. Teste - Domingo]),([Img. Teste - Segunda]),([Img. Teste - Terça]),([Img. Teste - Quarta]),([Img. Teste - Quinta]),([Img. Teste - Sexta]),([Img. Teste - Sábado])) T (c)) AS [Total Img. Teste]
				FROM   (
							--DECLARE @dataInicio DATE = '2021-07-01', @dataFim DATE = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)
							SELECT DATEPART(WEEK, Data) AS semana_ano,
								   DATEPART(YEAR, Data) AS ano,
								   MIN(d.Data) AS menor_data_semana,
								   MAX(d.Data) AS maior_data_semana,
								   CAST(DATEPART(YEAR, Data) AS VARCHAR(4)) + ' - ' + RIGHT('00' + CAST(DATEPART(WEEK, Data) AS VARCHAR(2)), 2)  +
												' (' + LEFT(CONVERT(VARCHAR(10), MIN(d.Data), 103), 5) + ' à ' + LEFT(CONVERT(VARCHAR(10), MAX(d.Data), 103), 5) + ')' AS semana_ano_desc,
								   lv.id_local,
								   lv.serie_equipamento
							FROM   dbo.fcn_ObterDatasPeriodo(@dataInicio, @dataFim) d
								   CROSS JOIN local_vigente lv
							WHERE  lv.desativado = 0
								   AND lv.serie_equipamento BETWEEN 2100000 AND 2199999
							GROUP BY
								   DATEPART(WEEK, Data),
								   DATEPART(YEAR, Data),
								   lv.id_local,
								   lv.serie_equipamento
					   ) p
					   LEFT JOIN dados_fluxo_veicular fluxo
							ON  fluxo.ano = p.ano
								AND fluxo.semana_ano = p.semana_ano
								AND fluxo.id_local = p.id_local
					   LEFT JOIN dados_infracao infracao
							ON  infracao.ano = p.ano
								AND infracao.semana_ano = p.semana_ano
								AND infracao.id_local = p.id_local
					   LEFT JOIN dados_imagem_teste img_teste
							ON  img_teste.ano = p.ano
								AND img_teste.semana_ano = p.semana_ano
								AND img_teste.id_local = p.id_local

				--UNION

				--SELECT p.semana_ano,
				--	   p.ano,
				--	   p.semana_ano_desc,
				--	   p.serie_equipamento,
				--	   [Fluxo - Domingo],[Fluxo - Segunda],[Fluxo - Terça],[Fluxo - Quarta],[Fluxo - Quinta],[Fluxo - Sexta],[Fluxo - Sábado],
				--	   [Infração - Domingo],[Infração - Segunda],[Infração - Terça],[Infração - Quarta],[Infração - Quinta],[Infração - Sexta],[Infração - Sábado],
				--	   (SELECT SUM(c) FROM (VALUES([Fluxo - Domingo]),([Fluxo - Segunda]),([Fluxo - Terça]),([Fluxo - Quarta]),([Fluxo - Quinta]),([Fluxo - Sexta]),([Fluxo - Sábado])) T (c)) AS [Total Fluxo],
				--	   (SELECT SUM(c) FROM (VALUES([Infração - Domingo]),([Infração - Segunda]),([Infração - Terça]),([Infração - Quarta]),([Infração - Quinta]),([Infração - Sexta]),([Infração - Sábado])) T (c)) AS [Total Infração]
				--FROM   (
				--			SELECT 99 AS semana_ano, 2999 AS ano, 'RESUMO' AS semana_ano_desc, lv.id_local, lv.serie_equipamento FROM local_vigente lv WHERE lv.desativado = 0 AND lv.serie_equipamento BETWEEN 2100000 AND 2199999
				--	   ) p
				--	   LEFT JOIN (
				--			SELECT 99 AS semana_ano,
				--				   2999 AS ano,
				--				   id_local,
				--				   SUM([Fluxo - Domingo]) AS [Fluxo - Domingo],
				--				   SUM([Fluxo - Segunda]) AS [Fluxo - Segunda],
				--				   SUM([Fluxo - Terça]) AS [Fluxo - Terça],
				--				   SUM([Fluxo - Quarta]) AS [Fluxo - Quarta],
				--				   SUM([Fluxo - Quinta]) AS [Fluxo - Quinta],
				--				   SUM([Fluxo - Sexta]) AS [Fluxo - Sexta],
				--				   SUM([Fluxo - Sábado]) AS [Fluxo - Sábado]
				--			FROM   dados_fluxo_veicular
				--			GROUP BY
				--				   id_local
				--	   ) fluxo
				--			ON  fluxo.ano = p.ano
				--				AND fluxo.semana_ano = p.semana_ano
				--				AND fluxo.id_local = p.id_local
				--	   LEFT JOIN (
				--			SELECT 99 AS semana_ano,
				--				   2999 AS ano,
				--				   id_local,
				--				   SUM([Infração - Domingo]) AS [Infração - Domingo],
				--				   SUM([Infração - Segunda]) AS [Infração - Segunda],
				--				   SUM([Infração - Terça]) AS [Infração - Terça],
				--				   SUM([Infração - Quarta]) AS [Infração - Quarta],
				--				   SUM([Infração - Quinta]) AS [Infração - Quinta],
				--				   SUM([Infração - Sexta]) AS [Infração - Sexta],
				--				   SUM([Infração - Sábado]) AS [Infração - Sábado]
				--			FROM   dados_infracao
				--			GROUP BY
				--				   id_local
				--	   ) infracao
				--			ON  infracao.ano = p.ano
				--				AND infracao.semana_ano = p.semana_ano
				--				AND infracao.id_local = p.id_local
		   ) r

)  

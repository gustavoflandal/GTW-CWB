CREATE PROCEDURE [dbo].[spu_getRelatorioFluxo15MinPorClassificacao]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME
AS
	--DECLARE @Data_Ini DATETIME = '2022-03-01 00:00:00',	@Data_Fim DATETIME = '2022-03-31 23:59:59'
	SELECT endereco AS [Endereco],
		   faixa AS [Faixa],
		   CONVERT(VARCHAR(10), data, 103) AS [Data],
		   LEFT(hora_ini, 8) AS [Horario Inicial],
		   LEFT(hora_fim, 8) AS [Horario Final],
		   [Moto],[Passeio],[Medio],[Grande],[Outros],
		   (SELECT SUM(c) FROM (VALUES([Moto]),([Passeio]),([Medio]),([Grande]),([Outros])) T (c)) AS [Total]
	FROM   (
				--DECLARE @Data_Ini DATETIME = '2022-03-01 00:00:00',	@Data_Fim DATETIME = '2022-03-31 23:59:59'
				SELECT v.id_local,
					   v.pista,
					   RTRIM(lpv.nome) AS endereco,
					   lpv.cod_pista_alternativo AS faixa,
					   CASE WHEN v.classe = 'A' THEN 'Moto'
							WHEN v.classe = 'B' THEN 'Passeio'
							WHEN v.classe = 'C' THEN 'Medio'
							WHEN v.classe = 'D' THEN 'Grande'
							WHEN v.classe = 'O' THEN 'Outros'
					   END AS classe,
					   CAST(v.data AS DATE) AS data,
					   m.hora_ini,
					   m.hora_fim,
					   COUNT(*) AS fluxo
				FROM   veiculo_pesquisa_classe v
					   JOIN local_pista_vigente lpv
							ON  lpv.id_local = v.id_local
								AND lpv.id_pista = v.pista
					   JOIN data_hora_15min m
							ON  CAST(v.data AS TIME) BETWEEN m.hora_ini AND m.hora_fim
				WHERE  v.data BETWEEN @Data_Ini AND @Data_Fim
				GROUP BY
					   v.id_local,
					   v.pista,
					   CAST(v.data AS DATE),
					   m.hora_ini,
					   m.hora_fim,
					   RTRIM(lpv.nome),
					   lpv.cod_pista_alternativo,
					   v.classe
		   ) r
	PIVOT  (
				SUM(fluxo)
				FOR classe IN ([Moto],[Passeio],[Medio],[Grande],[Outros])
		   ) cont_r
	ORDER BY
		   id_local,
		   pista,
		   cont_r.data,
		   cont_r.hora_ini

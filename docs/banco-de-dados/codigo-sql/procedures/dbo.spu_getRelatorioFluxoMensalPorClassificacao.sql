CREATE PROCEDURE [dbo].[spu_getRelatorioFluxoMensalPorClassificacao]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME
AS
	--DECLARE @Data_Ini DATETIME = '2023-01-01 00:00:00',	@Data_Fim DATETIME = '2023-01-10 23:59:59'
	SELECT id_local,
		   pista,
		   endereco,
		   grin,
		   CAST(CAST(ano AS VARCHAR(4)) + '-' + RIGHT('00' + RTRIM(CAST(mes AS VARCHAR(2))), 2) + '-01' AS DATE) AS data,
		   [Moto],[Passeio],[Medio],[Grande],[Outros],
		   (SELECT SUM(c) FROM (VALUES([Moto]),([Passeio]),([Medio]),([Grande]),([Outros])) T (c)) AS total
	FROM   (
				--DECLARE @Data_Ini DATETIME = '2022-03-01 00:00:00',	@Data_Fim DATETIME = '2022-03-31 23:59:59'
				SELECT v.id_local,
					   v.pista,
					   RTRIM(lpv.nome) AS endereco,
					   lpv.cod_pista_alternativo AS grin,
					   CASE WHEN v.classe = 'A' THEN 'Moto'
							WHEN v.classe = 'B' THEN 'Passeio'
							WHEN v.classe = 'C' THEN 'Medio'
							WHEN v.classe = 'D' THEN 'Grande'
							WHEN v.classe = 'O' THEN 'Outros'
					   END AS classe,
					   MONTH(v.data) AS mes,
					   YEAR(v.data) AS ano,
					   COUNT(*) AS fluxo
				FROM   veiculo_pesquisa_classe v
					   JOIN local_pista_vigente lpv
							ON  lpv.id_local = v.id_local
								AND lpv.id_pista = v.pista
					  -- JOIN v_grin_atual g
							--ON  g.id_local = v.id_local
							--	AND g.id_pista = v.pista
				WHERE  v.data BETWEEN @Data_Ini AND @Data_Fim
				GROUP BY
					   v.id_local,
					   v.pista,
					   MONTH(v.data),
					   YEAR(v.data),
					   RTRIM(lpv.nome),
					   --g.grin,
					   lpv.cod_pista_alternativo,
					   v.classe
		   ) r
	PIVOT  (
				SUM(fluxo)
				FOR classe IN ([Moto],[Passeio],[Medio],[Grande],[Outros])
		   ) cont_r
	ORDER BY
		   ano,
		   mes,
		   id_local,
		   pista

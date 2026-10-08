
CREATE VIEW [dbo].[local_mediavelocidade_totaltrafego]
AS
SELECT
    todos_os_locais.Local,
	CAST(COALESCE(valores_reais.Media, 0) as INT) as Media,
	COALESCE(valores_reais.Total, 0) as Total
FROM (	SELECT
			lv.id_local as Local
		FROM local_vigente lv (nolock)
	) as todos_os_locais
	LEFT JOIN (	SELECT
					top(100)
					v.id_local as Local,
					COUNT(*) as Total,
					AVG(V.velocidade) as Media
				FROM veiculo v (nolock)
				WHERE	convert(smalldatetime, v.data) between DATEADD(MINUTE, -60, GETDATE()) --'2009-12-02 07:50:00.000'
					and convert(smalldatetime, getdate()) --'2009-12-02 07:59:59.997'
				GROUP BY v.id_local
			) as valores_reais
		ON todos_os_locais.Local = valores_reais.Local



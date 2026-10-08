

CREATE   VIEW [dbo].[RelatorioVolumeHora]
AS 
SELECT 	[ID_Gerado], L.[id_local], RTRIM(L.Nome) As Nome,
	SUM(h0) AS h0, 
	SUM(h1) AS h1,
	SUM(h2) AS h2, 
	SUM(h3) AS h3, 
	SUM(h4) AS h4,
	SUM(h5) AS h5,
	SUM(h6) AS h6,
	SUM(h7) AS h7,
	SUM(h8) AS h8,
	SUM(h9) AS h9,
	SUM(h10) AS h10,
	SUM(h11) AS h11,
	SUM(h12) AS h12,
	SUM(h13) AS h13,
	SUM(h14) AS h14,
	SUM(h15) AS h15,
	SUM(h16) AS h16,
	SUM(h17) AS h17,
	SUM(h18) AS h18,
	SUM(h19) AS h19,
	SUM(h20) AS h20,
	SUM(h21) AS h21,
	SUM(h22) AS h22,
	SUM(h23) AS h23,
	SUM(total) AS total
FROM Relatorios_Volume_Hora_PMG R (nolock)
	INNER JOIN [local_vigente] L (nolock) 
		ON L.id_local = R.Local
GROUP BY ID_Gerado, L.id_local, L.Nome



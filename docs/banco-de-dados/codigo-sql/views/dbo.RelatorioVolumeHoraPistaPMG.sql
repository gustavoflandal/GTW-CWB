
CREATE   VIEW [dbo].[RelatorioVolumeHoraPistaPMG]
AS 
SELECT 	[ID_Gerado], L.[id_local], RTRIM(L.Nome) AS Nome, [pista], [ClassePorTamanho], 
	ISNULL([h0], 0) as h0, 
	ISNULL([h1], 0) as h1,
	ISNULL([h2], 0) as h2,
	ISNULL([h3], 0) as h3,
	ISNULL([h4], 0) as h4,
	ISNULL([h5], 0) as h5,
	ISNULL([h6], 0) as h6,
	ISNULL([h7], 0) as h7,
	ISNULL([h8], 0) as h8,
	ISNULL([h9], 0) as h9,
	ISNULL([h10], 0) as h10,
	ISNULL([h11], 0) as h11,
	ISNULL([h12], 0) as h12,
	ISNULL([h13], 0) as h13,
	ISNULL([h14], 0) as h14,
	ISNULL([h15], 0) as h15,
	ISNULL([h16], 0) as h16,
	ISNULL([h17], 0) as h17,
	ISNULL([h18], 0) as h18,
	ISNULL([h19], 0) as h19,
	ISNULL([h20], 0) as h20,
	ISNULL([h21], 0) as h21,
	ISNULL([h22], 0) as h22,
	ISNULL([h23], 0) as h23,
	ISNULL([total], 0) as total
FROM Relatorios_Volume_Hora_PMG R (nolock) 
	LEFT JOIN [local_vigente] L (nolock) 
		ON L.id_local = R.Local



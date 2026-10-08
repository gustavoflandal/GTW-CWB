
CREATE VIEW [dbo].[RelatorioMediaVelocidadeHora]
AS 
SELECT 	[ID_Gerado], L.[id_local] as Local, RTRIM(L.Nome) As Nome,
	[h0], [h1], [h2], [h3], [h4], [h5], [h6], [h7], [h8], [h9], 
	[h10], [h11], [h12], [h13], [h14], [h15], [h16], [h17], [h18], [h19], 
	[h20], [h21], [h22], [h23], [Total] 
FROM Relatorios_MediaVelocidade_Hora R (nolock)
	INNER JOIN local_vigente L (nolock) 
		ON L.id_local = R.Local



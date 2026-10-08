
CREATE  VIEW [dbo].[RelatoriosPTPNT]
AS 
SELECT 	R.[ID_Gerado], L.[id_local] as Local, L.[Nome], R.[Tipo], R.[Categoria], 
	R.[CodInconsistencia], R.[DescrInconsistencia], 
	R.[Total] 
FROM Relatorios_PT_PNT R (nolock)
	INNER JOIN [LOCAL] L (nolock) 
		ON L.id_local = R.Local



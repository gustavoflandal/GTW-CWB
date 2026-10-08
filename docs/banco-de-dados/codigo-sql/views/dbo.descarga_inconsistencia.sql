

CREATE VIEW [dbo].[descarga_inconsistencia] AS
SELECT DISTINCT
	inc.id_inconsistencia,
	RTRIM(inc.descricao) AS descricao
FROM veiculo_descarga d (nolock)
	INNER JOIN infracao inf (nolock)
		ON inf.id_veiculo = d.id_veiculo
	INNER JOIN inconsistencia inc (nolock)
		ON inc.id_inconsistencia = inf.id_inconsistencia



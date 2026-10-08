

CREATE VIEW [dbo].[descarga_classe_veiculo] AS
SELECT DISTINCT
	CASE c.id_classe 
		WHEN  '' 
			THEN ' ' 
		ELSE c.id_classe 
	END AS id_classe,
	c.descricao
FROM veiculo_descarga d (nolock)
	INNER JOIN veiculo v (nolock)
		ON v.id_veiculo = d.id_veiculo
	INNER JOIN classe_veiculo c (nolock)
		ON c.id_classe = v.id_classe



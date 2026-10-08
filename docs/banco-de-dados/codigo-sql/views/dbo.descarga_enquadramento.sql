

CREATE VIEW [dbo].[descarga_enquadramento] AS
SELECT DISTINCT
	e.id_enquadramento,
	RTRIM(e.descricao) AS descricao
FROM veiculo_descarga d (nolock)
	INNER JOIN infracao i (nolock)
		ON i.id_veiculo = d.id_veiculo
	INNER JOIN enquadramento e (nolock)
		ON e.id_enquadramento = i.id_enquadramento



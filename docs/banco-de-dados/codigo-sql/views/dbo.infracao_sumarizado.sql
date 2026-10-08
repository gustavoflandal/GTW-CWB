
CREATE VIEW [dbo].[infracao_sumarizado] AS
SELECT
	CAST(inf.data AS DATE) AS data,
	DATEPART(hh, inf.data) AS hora,
	inf.id_local,
	inf.sequencia_local,
	inf.pista,
	vei.id_classe,
	inf.id_enquadramento,
	inf.id_inconsistencia
FROM infracao inf (nolock)
	JOIN veiculo vei (nolock)
		ON vei.id_veiculo = inf.id_veiculo



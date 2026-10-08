

CREATE VIEW [dbo].[descarga_usuario] AS
SELECT DISTINCT
	u.id_usuario,
	u.cod_agente,
	RTRIM(u.usuario) AS usuario,
	RTRIM(u.nome) AS nome
FROM
	veiculo_descarga d (nolock)
	INNER JOIN infracao inf (nolock)
		ON inf.id_veiculo = d.id_veiculo	
	INNER JOIN sis_usuario u (nolock)
		ON u.id_usuario = inf.id_usuario_final



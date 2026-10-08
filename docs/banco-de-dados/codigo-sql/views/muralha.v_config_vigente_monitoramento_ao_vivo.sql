CREATE VIEW [muralha].[v_config_vigente_monitoramento_ao_vivo]
AS
	SELECT TOP 1
		   cmv.id,
		   cmv.segundos,
		   cmv.data_configuracao,
		   cmv.ativo,
		   su.id_usuario,
		   RTRIM(su.usuario) AS usuario,
		   RTRIM(su.nome) AS nome_usuario,
	   g.id_grupo_exibicao
	FROM   muralha.config_monitoramento_ao_vivo cmv
		   JOIN sis_usuario su
				ON  su.id_usuario = cmv.id_usuario
		   CROSS JOIN muralha.monitoramento_ao_vivo_grupo_exibicao g
	WHERE  cmv.ativo = 1
	ORDER BY
		   cmv.data_configuracao DESC

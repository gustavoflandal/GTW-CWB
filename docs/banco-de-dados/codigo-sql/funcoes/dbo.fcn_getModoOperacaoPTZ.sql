CREATE FUNCTION [dbo].[fcn_getModoOperacaoPTZ]()
RETURNS TABLE
AS
RETURN
(
	SELECT ptz_config.id_ptz_configuracao_operacao,
		   ptz_mo.id_ptz_modo_operacao,
		   ptz_mo.descricao AS desc_modo_operacao,
		   ptz_config.data_inicio,
		   ptz_config.data_fim,
		   su.id_usuario,
		   LTRIM(RTRIM(su.usuario)) AS usuario,
		   LTRIM(RTRIM(su.nome)) AS nome_usuario,
		   CASE WHEN ptz_mo.id_ptz_modo_operacao = 1 THEN 1 ELSE 0 END AS bloquear_uso_ptz
	FROM   ptz_configuracao_operacao ptz_config (NOLOCK)
		   INNER JOIN ptz_modo_operacao ptz_mo (NOLOCK)
				ON  ptz_mo.id_ptz_modo_operacao = ptz_config.id_ptz_modo_operacao
		   INNER JOIN sis_usuario su (NOLOCK)
				ON  su.id_usuario = ptz_config.id_usuario
	WHERE  ptz_config.id_ptz_configuracao_operacao = (
							SELECT MAX(conf_atual.id_ptz_configuracao_operacao) AS id_ptz_configuracao_operacao
							FROM   ptz_configuracao_operacao conf_atual (NOLOCK)
							WHERE conf_atual.data_fim IS NULL
		   )
)

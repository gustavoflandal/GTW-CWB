CREATE PROCEDURE [muralha].[spu_ObterNotificacoesPendentes] @idTipoRegistro UNIQUEIDENTIFIER, @idStatusNotificacao UNIQUEIDENTIFIER, @idTipoNotificacao UNIQUEIDENTIFIER
AS
	
	--DECLARE @idTipoRegistro UNIQUEIDENTIFIER = 'E7D115B9-E6B3-4E86-9083-F347A1917045' /*ALERTA*/, @idStatusNotificacao UNIQUEIDENTIFIER = '99AF55C6-2446-4B98-BBCD-83663C504C79' /*PENDENTE*/, @idTipoNotificacao UNIQUEIDENTIFIER = '3A3F1F17-6EC3-4FCA-9195-5B160A091779' /*EMAIL*/
	--DECLARE @idTipoRegistro UNIQUEIDENTIFIER = '5511CEF5-C1A0-450B-99B3-6FCA8668D243' /*IRREGULARIDADE*/, @idStatusNotificacao UNIQUEIDENTIFIER = '99AF55C6-2446-4B98-BBCD-83663C504C79' /*PENDENTE*/, @idTipoNotificacao UNIQUEIDENTIFIER = '3A3F1F17-6EC3-4FCA-9195-5B160A091779' /*EMAIL*/

	DECLARE @sql AS VARCHAR(MAX) = '',  @sql_1 AS VARCHAR(MAX) = '', @sql_2 AS VARCHAR(MAX) = '', @sql_3 AS VARCHAR(MAX) = '', @sql_4 AS VARCHAR(MAX) = ''

	SET @sql_1 = '
			DECLARE @idTipoRegistro UNIQUEIDENTIFIER = ''' + RTRIM(CAST(@idTipoRegistro AS VARCHAR(100))) + ''',
					@idStatusNotificacao UNIQUEIDENTIFIER = ''' + RTRIM(CAST(@idStatusNotificacao AS VARCHAR(100))) + ''',
					@idTipoNotificacao UNIQUEIDENTIFIER = ''' + RTRIM(CAST(@idTipoNotificacao AS VARCHAR(100))) + '''

			SET NOCOUNT ON;
		'

		
		IF ( @idTipoRegistro = (SELECT id FROM muralha.tipo_registro WHERE descricao = 'ALERTA') )
		BEGIN

			SET @sql_2 = '
			DECLARE @alerta_notificacao AS TABLE (id UNIQUEIDENTIFIER, id_alerta UNIQUEIDENTIFIER, id_grupo INT, id_tipo_notificacao UNIQUEIDENTIFIER, id_status_notificacao UNIQUEIDENTIFIER, id_usuario INT, data_cadastro DATETIME, data_processado DATETIME)

			INSERT INTO @alerta_notificacao
			SELECT id, id_alerta, id_grupo, id_tipo_notificacao, id_status_notificacao, id_usuario, data_cadastro, data_processado
			FROM   muralha.alerta_notificacao
			WHERE  id_status_notificacao = @idStatusNotificacao
				   AND id_tipo_notificacao = @idTipoNotificacao
			'

			SET @sql_4 = '
			   INNER JOIN @alerta_notificacao notific
					ON  notific.id_alerta = a.id
			   INNER JOIN sis_grupo sg
					ON  sg.id_grupo = notific.id_grupo
			   LEFT JOIN sis_usuario su
					ON  su.id_usuario = a.id_usuario
			   INNER JOIN muralha.status_notificacao sn
					ON  sn.id = notific.id_status_notificacao
			   INNER JOIN muralha.tipo_notificacao tn
					ON  tn.id = notific.id_tipo_notificacao
			   LEFT JOIN muralha.ocorrencia o
					ON  o.id_alerta = a.id
			ORDER BY a.id
			'

		END
		ELSE IF ( @idTipoRegistro = (SELECT id FROM muralha.tipo_registro WHERE descricao = 'OCORRÊNCIA') )
		BEGIN

			SET @sql_2 = '
				DECLARE @ocorrencia_notificacao AS TABLE (id UNIQUEIDENTIFIER, id_ocorrencia UNIQUEIDENTIFIER, id_grupo INT, id_tipo_notificacao UNIQUEIDENTIFIER, id_status_notificacao UNIQUEIDENTIFIER, id_usuario INT, data_cadastro DATETIME, data_processado DATETIME
)

				INSERT INTO @ocorrencia_notificacao
				SELECT id, id_ocorrencia, id_grupo, id_tipo_notificacao, id_status_notificacao, id_usuario, data_cadastro, data_processado
				FROM   muralha.ocorrencia_notificacao
				WHERE  id_status_notificacao = @idStatusNotificacao
					   AND id_tipo_notificacao = @idTipoNotificacao
				'

			SET @sql_4 = '
				   INNER JOIN muralha.ocorrencia o
						ON  o.id_alerta = a.id
				   INNER JOIN @ocorrencia_notificacao notific
						ON  notific.id_ocorrencia = o.id
				   INNER JOIN sis_grupo sg
						ON  sg.id_grupo = notific.id_grupo
				   LEFT JOIN sis_usuario su
						ON  su.id_usuario = o.id_usuario
				   INNER JOIN muralha.status_notificacao sn
						ON  sn.id = notific.id_status_notificacao
				   INNER JOIN muralha.tipo_notificacao tn
						ON  tn.id = notific.id_tipo_notificacao
			ORDER BY o.id
			'

		END

		SET @sql_3 = '
			SELECT notific.id,
				   notific.data_cadastro,
				   o.id AS id_ocorrencia,
				   a.id AS id_alerta,
				   RTRIM(lv.nome) AS equipamento,
				   RTRIM(SUBSTRING(lv.nome, 1, 25)) AS equipamento_sms,
				   vtr.data AS data_veiculo,
				   a.data AS data_alerta,
				   o.data AS data_ocorrencia,
				   RTRIM(su.nome) AS nome_usuario_ocorrencia,
				   cvm.placa AS placa_monitorada,
				   vtr.placa AS placa_lida,
				   sg.id_grupo,
				   RTRIM(sg.descricao) AS grupo,
				   a.id_tipo_alerta_ocorrencia,
				   tao.tipo AS tipo_alerta_ocorrencia,
				   tao.descricao_sms AS tipo_alerta_ocorrencia_sms,
				   notific.id_tipo_notificacao,
				   tn.descricao AS tipo_notificacao,
				   tr.id AS id_tipo_registro,
				   tr.descricao AS tipo_registro,
				   notific.id_status_notificacao,
				   sn.descricao AS status_notificacao,
				   muralha.fcn_ObterRemetenteEmailmuralha() AS remetente
			FROM   muralha.alerta a
				   INNER JOIN muralha.tipo_alerta_ocorrencia tao
						ON  tao.id = a.id_tipo_alerta_ocorrencia
				   INNER JOIN muralha.alerta_veiculo av
						ON  av.id_alerta = a.id
		  					AND av.id_veiculo_tempo_real = (
		  							SELECT TOP 1 av2.id_veiculo_tempo_real
		  							FROM   muralha.alerta_veiculo av2
		  							WHERE  av2.id_alerta = a.id
		  					) 
				   INNER JOIN muralha.veiculo_tempo_real vtr
						ON  vtr.id = av.id_veiculo_tempo_real
				   INNER JOIN muralha.cad_veiculo_monitorado cvm
						ON  cvm.id = a.id_cad_veiculo_monitorado
				   INNER JOIN local_vigente lv
						ON  lv.id_local = vtr.id_local
				   INNER JOIN muralha.tipo_registro tr
						ON  tr.id = @idTipoRegistro
			'

	SET @sql = (@sql_1 + @sql_2 + @sql_3 + @sql_4)
	--SELECT @sql AS SQL_GERADA;
	
	EXEC (@sql)



CREATE PROCEDURE [muralha].[spu_ObterUsuariosNotificacaoMobilePorTipoAlerta]
    @idTipoAlertaOcorrencia UNIQUEIDENTIFIER  
AS  
BEGIN 
    DECLARE @usuario_token AS TABLE (id INT INDEX IX1, id_usuario INT)

    INSERT INTO @usuario_token
    SELECT MAX(f.id) AS id,
           f.id_usuario
    FROM   dbo.sis_fcm_token f
    GROUP BY f.id_usuario

    SELECT 
        su.id_usuario,
        RTRIM(su.nome) AS nome,
        app.fcm_token AS fcm_token,
        tao.descricao_sms AS descricao_sms,
        cs.comportamento
    FROM dbo.sis_fcm_token app
    INNER JOIN @usuario_token fcm ON fcm.id = app.id
    INNER JOIN dbo.sis_usuario su ON su.id_usuario = app.id_usuario
    INNER JOIN dbo.sis_usuario_grupo sug ON sug.id_usuario = su.id_usuario
    INNER JOIN dbo.sis_grupo sg ON sg.id_grupo = sug.id_grupo
    INNER JOIN muralha.config_grupo_permissao cgp ON cgp.id_grupo = sg.id_grupo
    INNER JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = cgp.id_tipo_alerta_ocorrencia
    OUTER APPLY (
        SELECT TOP 1 comportamento
        FROM muralha.config_mobile_silencio cs
        WHERE 
            cs.id_usuario = su.id_usuario
            AND cs.ativo = 1
            AND GETDATE() BETWEEN cs.data_inicio AND cs.data_fim
        ORDER BY cs.data_criacao DESC
    ) cs 

    WHERE cgp.id_tipo_alerta_ocorrencia = @idTipoAlertaOcorrencia
      AND cgp.id_tipo_registro = 'E7D115B9-E6B3-4E86-9083-F347A1917045'
      AND cgp.id_tipo_notificacao = '778F443E-9514-44E0-BCD1-F953D91042BF'
      AND cgp.ativo = 1
      AND sg.config_muralha = 1
      AND app.fcm_token IS NOT NULL

    GROUP BY 
        su.id_usuario,
        su.nome,
        app.fcm_token,
        tao.descricao_sms,
        cs.comportamento
END;
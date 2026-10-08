
CREATE PROCEDURE [muralha].[spu_ObterUsuariosNotificacaoMobile]  
    @idTipoAlertaOcorrencia UNIQUEIDENTIFIER  
AS BEGIN  
    SELECT   
        su.id_usuario,  
        su.nome,  
        app.fcm_token,  
        sg.id_grupo,  
        sg.descricao,  
        cgp.id_tipo_alerta_ocorrencia,  
        tao.descricao_sms  
 FROM     
        sis_fcm_token app  
        INNER JOIN sis_usuario su  
            ON  su.id_usuario = app.id_usuario  
        INNER JOIN sis_usuario_grupo sug  
            ON  sug.id_usuario = su.id_usuario  
        INNER JOIN sis_grupo sg  
            ON  sg.id_grupo = sug.id_grupo  
        INNER JOIN muralha.config_grupo_permissao cgp  
            ON  cgp.id_grupo = sg.id_grupo  
        INNER JOIN muralha.tipo_alerta_ocorrencia tao  
            ON  tao.id = cgp.id_tipo_alerta_ocorrencia  
 WHERE    
        cgp.id_tipo_registro = 'E7D115B9-E6B3-4E86-9083-F347A1917045' --> FILTRO FIXO - ALERTA  
        AND cgp.id_tipo_notificacao = '778F443E-9514-44E0-BCD1-F953D91042BF' --> FILTRO FIXO - POPUP  
        AND cgp.id_tipo_alerta_ocorrencia = @idTipoAlertaOcorrencia  
        AND sug.id_grupo = 43 --> Filtro fixo para usuários do grupo 43 (Agente Central Admininstrativa) somente 
		AND app.fcm_token IS NOT NULL
END  
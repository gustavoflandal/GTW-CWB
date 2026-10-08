CREATE PROCEDURE muralha.spu_obter_usuarios_blitz
    @id_blitz_digital INT
AS
BEGIN
    DECLARE @notificar_proximos BIT;
    DECLARE @raio_km DECIMAL(8,2);
    
    SELECT 
        @notificar_proximos = notificar_agentes_proximos,
        @raio_km = raio_notificacao_km
    FROM muralha.blitz_digital 
    WHERE id = @id_blitz_digital;
    
    IF @notificar_proximos = 0
    BEGIN
        SELECT DISTINCT
            u.id_usuario,
            u.nome,
            fcm.fcm_token
        FROM dbo.sis_usuario u
        INNER JOIN muralha.blitz_usuario bu ON u.id_usuario = bu.id_usuario
        INNER JOIN dbo.sis_fcm_token fcm ON u.id_usuario = fcm.id_usuario
        WHERE bu.id_blitz_digital = @id_blitz_digital
          AND bu.ativo = 1
          AND u.ativo = 1
          AND fcm.fcm_token IS NOT NULL
        
        UNION
        
        SELECT DISTINCT
            u.id_usuario,
            u.nome,
            fcm.fcm_token
        FROM dbo.sis_usuario u
        INNER JOIN muralha.guarnicao_integrante gi ON u.id_usuario = gi.id_usuario
        INNER JOIN muralha.blitz_guarnicao bg ON gi.id_guarnicao = bg.id_guarnicao
        INNER JOIN dbo.sis_fcm_token fcm ON u.id_usuario = fcm.id_usuario
        WHERE bg.id_blitz_digital = @id_blitz_digital
          AND bg.ativo = 1
          AND u.ativo = 1
          AND fcm.fcm_token IS NOT NULL;
    END
    ELSE
    BEGIN
        WITH LocaisBlitz AS (
            SELECT DISTINCT l.id_local, l.posicao_lat, l.posicao_lon
            FROM muralha.blitz_local bl
            INNER JOIN dbo.[local] l ON bl.id_local = l.id_local
            WHERE bl.id_blitz_digital = @id_blitz_digital
        )
        SELECT DISTINCT
            u.id_usuario,
            u.nome,
            fcm.fcm_token
        FROM dbo.sis_usuario u
        INNER JOIN muralha.blitz_usuario bu ON u.id_usuario = bu.id_usuario
        INNER JOIN dbo.sis_fcm_token fcm ON u.id_usuario = fcm.id_usuario
        INNER JOIN muralha.agente_localizacao_atual ala ON u.id_usuario = ala.id_usuario
        CROSS JOIN LocaisBlitz lb
        WHERE bu.id_blitz_digital = @id_blitz_digital
          AND bu.ativo = 1
          AND u.ativo = 1
          AND fcm.fcm_token IS NOT NULL
          AND muralha.calcular_distancia_km(lb.posicao_lat, lb.posicao_lon, ala.latitude, ala.longitude) <= @raio_km
        
        UNION
        
        SELECT DISTINCT
            u.id_usuario,
            u.nome,
            fcm.fcm_token
        FROM dbo.sis_usuario u
        INNER JOIN muralha.guarnicao_integrante gi ON u.id_usuario = gi.id_usuario
        INNER JOIN muralha.blitz_guarnicao bg ON gi.id_guarnicao = bg.id_guarnicao
        INNER JOIN dbo.sis_fcm_token fcm ON u.id_usuario = fcm.id_usuario
        INNER JOIN muralha.agente_localizacao_atual ala ON u.id_usuario = ala.id_usuario
        CROSS JOIN LocaisBlitz lb
        WHERE bg.id_blitz_digital = @id_blitz_digital
          AND bg.ativo = 1
          AND u.ativo = 1
          AND fcm.fcm_token IS NOT NULL
          AND muralha.calcular_distancia_km(lb.posicao_lat, lb.posicao_lon, ala.latitude, ala.longitude) <= @raio_km;
    END
END
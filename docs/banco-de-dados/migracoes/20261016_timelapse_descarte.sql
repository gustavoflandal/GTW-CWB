SET QUOTED_IDENTIFIER ON;
GO

-- ═══════════════════════════════════════════════════════════════════════════
-- Plano 33 — Descarte automático de time-lapse (§5.7.7.5-6)
-- ROLLBACK: DROP TABLE muralha.timelapse_cache;
--           DELETE FROM muralha.config_chave_valor WHERE chave IN ('TIMELAPSE_RETENCAO_HORAS','TIMELAPSE_DESCARTE_HABILITADO');
-- ═══════════════════════════════════════════════════════════════════════════

-- 1. Cache de sessões de timelapse geradas --------------------------------
IF OBJECT_ID('muralha.timelapse_cache', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.timelapse_cache (
        id              BIGINT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_timelapse_cache PRIMARY KEY,
        id_local        INT          NOT NULL,
        dt_inicio       DATETIME2    NOT NULL,
        dt_fim          DATETIME2    NOT NULL,
        total_frames    INT          NOT NULL,
        dt_geracao      DATETIME2(3) NOT NULL CONSTRAINT DF_tc_dt DEFAULT SYSDATETIME(),
        id_usuario      INT          NULL
    );

    CREATE INDEX IX_timelapse_cache_dt
        ON muralha.timelapse_cache (dt_geracao);
END
GO

-- 2. Parâmetros de configuração -------------------------------------------
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'TIMELAPSE_RETENCAO_HORAS')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('TIMELAPSE_RETENCAO_HORAS', '72');

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'TIMELAPSE_DESCARTE_HABILITADO')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('TIMELAPSE_DESCARTE_HABILITADO', 'true');
GO

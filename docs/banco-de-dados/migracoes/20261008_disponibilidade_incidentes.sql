-- Migração: Planos 18+19 (Disponibilidade + Incidentes Externos)
-- Data: 2026-10-08
-- ROLLBACK:
--   DROP TABLE muralha.equipamento_disponibilidade;
--   DROP TABLE muralha.incidente_externo;
--   DELETE FROM muralha.config_chave_valor WHERE chave IN (
--     'disponibilidade_threshold_min',
--     'waze_api_url',
--     'waze_area_bbox'
--   );

-- =============================================
-- Plano 18: Auditoria de Disponibilidade
-- =============================================

IF NOT EXISTS (SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON t.schema_id = s.schema_id
    WHERE s.name = 'muralha' AND t.name = 'equipamento_disponibilidade')
BEGIN
    CREATE TABLE muralha.equipamento_disponibilidade (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        dt_verificacao  DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
        id_local        INT          NOT NULL,
        disponivel      BIT          NOT NULL,
        ultima_passagem DATETIME2    NULL,
        minutos_offline INT          NULL
    );

    CREATE INDEX ix_eq_disp_local ON muralha.equipamento_disponibilidade
        (id_local, dt_verificacao DESC);
END;

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'disponibilidade_threshold_min')
    INSERT INTO muralha.config_chave_valor (chave, valor)
    VALUES ('disponibilidade_threshold_min', '15');

-- =============================================
-- Plano 19: Incidentes Externos (Waze/SAMU/CSV)
-- =============================================

IF NOT EXISTS (SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON t.schema_id = s.schema_id
    WHERE s.name = 'muralha' AND t.name = 'incidente_externo')
BEGIN
    CREATE TABLE muralha.incidente_externo (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        fonte           VARCHAR(20)   NOT NULL,
        id_externo      VARCHAR(100)  NULL,
        tipo            VARCHAR(50)   NOT NULL,
        descricao       NVARCHAR(500) NULL,
        latitude        DECIMAL(10,7) NOT NULL,
        longitude       DECIMAL(10,7) NOT NULL,
        severidade      INT           NULL,
        dt_ocorrencia   DATETIME2     NOT NULL,
        dt_importacao   DATETIME2(3)  NOT NULL DEFAULT SYSDATETIME(),
        ativo           BIT           NOT NULL DEFAULT 1,
        CONSTRAINT uq_incidente UNIQUE (fonte, id_externo, dt_ocorrencia)
    );

    CREATE INDEX ix_incidente_dt ON muralha.incidente_externo
        (dt_ocorrencia, ativo);
END;

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'waze_api_url')
    INSERT INTO muralha.config_chave_valor (chave, valor)
    VALUES ('waze_api_url', 'https://www.waze.com/row-partnerhub-api/partners/{partner_id}/waze-feeds/v3');

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'waze_area_bbox')
    INSERT INTO muralha.config_chave_valor (chave, valor)
    VALUES ('waze_area_bbox', '-12.80,-38.60,-13.10,-38.30');

SET QUOTED_IDENTIFIER ON;
GO

-- ═══════════════════════════════════════════════════════════════════════════
-- Plano 32 — Retry de notificações em falha de comunicação (§5.3.5.3)
-- ROLLBACK: DROP TABLE muralha.notificacao_retry;
--           DELETE FROM muralha.config_chave_valor WHERE chave IN ('RETRY_NOTIFICACAO_MAX','RETRY_NOTIFICACAO_INTERVALO_MIN','RETRY_NOTIFICACAO_HABILITADO');
-- ═══════════════════════════════════════════════════════════════════════════

-- 1. Tabela de rastreamento de tentativas de reenvio ----------------------
IF OBJECT_ID('muralha.notificacao_retry', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.notificacao_retry (
        id              BIGINT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_notificacao_retry PRIMARY KEY,
        id_notificacao  UNIQUEIDENTIFIER NOT NULL,
        tipo_registro   VARCHAR(20)      NOT NULL,  -- 'ALERTA' ou 'OCORRENCIA'
        tentativa       INT              NOT NULL,
        dt_tentativa    DATETIME2(3)     NOT NULL CONSTRAINT DF_nr_dt DEFAULT SYSDATETIME(),
        erro            VARCHAR(500)     NULL,
        sucesso         BIT              NOT NULL CONSTRAINT DF_nr_suc DEFAULT 0
    );

    CREATE INDEX IX_notificacao_retry_notif
        ON muralha.notificacao_retry (id_notificacao, tipo_registro);
END
GO

-- 2. Parâmetros de configuração -------------------------------------------
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'RETRY_NOTIFICACAO_MAX')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('RETRY_NOTIFICACAO_MAX', '3');

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'RETRY_NOTIFICACAO_INTERVALO_MIN')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('RETRY_NOTIFICACAO_INTERVALO_MIN', '5');

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'RETRY_NOTIFICACAO_HABILITADO')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('RETRY_NOTIFICACAO_HABILITADO', 'true');
GO

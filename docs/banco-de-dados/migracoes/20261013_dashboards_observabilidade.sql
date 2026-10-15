-- =============================================================================
-- DASH-6: Observabilidade do construtor de dashboards
-- Schema: dash
-- Banco: GTW_MURALHA_DEV (SQL Server 2016+)
-- Data: 2026-10-13
-- =============================================================================
-- ROLLBACK:
--   DROP TABLE dash.consulta_log;
--   DROP TABLE dash.dataset_estatistica;
-- =============================================================================

SET QUOTED_IDENTIFIER ON;
GO

-- -----------------------------------------------
-- 1. consulta_log — registro de cada consulta via proxy
-- -----------------------------------------------
CREATE TABLE dash.consulta_log (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_id          INT             NOT NULL,
    dataset_id          INT             NOT NULL,
    widget_id           INT             NULL,
    painel_id           INT             NULL,
    consulta_json       NVARCHAR(MAX)   NULL,
    tempo_ms            INT             NOT NULL,
    linhas_retornadas   INT             NOT NULL DEFAULT 0,
    cache_hit           BIT             NOT NULL DEFAULT 0,
    lenta               BIT             NOT NULL DEFAULT 0,
    erro                VARCHAR(500)    NULL,
    criado_em           DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT chk_consulta_json_log CHECK (consulta_json IS NULL OR ISJSON(consulta_json) = 1)
);

CREATE INDEX ix_consulta_log_criado ON dash.consulta_log (criado_em);
CREATE INDEX ix_consulta_log_dataset ON dash.consulta_log (dataset_id, criado_em);
CREATE INDEX ix_consulta_log_lenta ON dash.consulta_log (lenta, criado_em) WHERE lenta = 1;

-- -----------------------------------------------
-- 2. dataset_estatistica — contadores agregados por dataset
-- -----------------------------------------------
CREATE TABLE dash.dataset_estatistica (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    dataset_id          INT             NOT NULL UNIQUE,
    total_consultas     BIGINT          NOT NULL DEFAULT 0,
    tempo_medio_ms      INT             NOT NULL DEFAULT 0,
    cache_hit_count     BIGINT          NOT NULL DEFAULT 0,
    consultas_lentas    BIGINT          NOT NULL DEFAULT 0,
    ultima_consulta     DATETIME2(3)    NULL,
    atualizado_em       DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_estatistica_dataset FOREIGN KEY (dataset_id)
        REFERENCES dash.dataset(id)
);

CREATE INDEX ix_estatistica_dataset ON dash.dataset_estatistica (dataset_id);

PRINT 'DASH-6: Tabelas de observabilidade criadas com sucesso.';
GO

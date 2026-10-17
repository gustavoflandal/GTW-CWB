SET QUOTED_IDENTIFIER ON;
GO

-- =============================================================================
-- SUP-1 / Plano 23 -- Fundacao: schema analise, configuracao e menu
-- ROLLBACK: DROP TABLE analise.carga_log; DROP SCHEMA analise;
--           DELETE FROM muralha.config_chave_valor WHERE chave = 'SUPERSET_URL';
--           DELETE FROM dbo.sis_menu_infos WHERE id_infos IN (56, 57);
--           DELETE FROM dbo.sis_menu WHERE id_menu = 395;
-- =============================================================================

-- 1. Schema para objetos analiticos ------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'analise')
    EXEC('CREATE SCHEMA analise');
GO

-- 2. Tabela de log de carga dos materializados --------------------------------
IF OBJECT_ID('analise.carga_log', 'U') IS NULL
BEGIN
    CREATE TABLE analise.carga_log (
        id          BIGINT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_carga_log PRIMARY KEY,
        processo    VARCHAR(100)  NOT NULL,
        dt_inicio   DATETIME2(3)  NOT NULL,
        dt_fim      DATETIME2(3)  NULL,
        linhas      INT           NULL,
        erro        VARCHAR(2000) NULL,
        sucesso     BIT           NOT NULL CONSTRAINT DF_cl_suc DEFAULT 0
    );

    CREATE INDEX IX_carga_log_dt ON analise.carga_log (dt_inicio);
END
GO

-- 3. Configuracao: URL do Superset -------------------------------------------
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'SUPERSET_URL')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('SUPERSET_URL', 'http://localhost:8088');
GO

-- 4. Menu "Analises" em sis_menu (item de navegacao) -------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE id_menu = 395)
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (395, 'Analises', '/MuralhaDigital/SsoAnalise?acao=abrir', 'URL', 1, 222, 'Analises', 1, 'MuralhaDigital');
GO

-- 5. Card "Analises" em sis_menu_infos (card na tela) ------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE id_infos = 56)
    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    VALUES (56, 395, 'bi-graph-up', 'Analises',
            '/MuralhaDigital/SsoAnalise?acao=abrir',
            'Plataforma de analises e dashboards interativos', 13, 15);
GO

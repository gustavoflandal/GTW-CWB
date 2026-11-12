-- ============================================================
-- Migração: Motor de Captura de Logs de Operação
-- Data: 2026-11-05
-- Autor: gustavoflandal
-- Origem: docs/captura-logs/ (análise do módulo atual) +
--         docs/captura-logs/plano-implementacao-motor.md
--
-- Evolui dbo.sis_log_auditoria para suportar captura automática
-- (filtro HTTP), regras configuráveis, cadeia de integridade
-- (hash encadeado + selo diário) e retenção/expurgo configuráveis.
--
-- Idempotente: pode ser executado várias vezes sem efeito colateral.
--
-- Rollback (manual):
--   DROP TABLE dbo.sis_log_expurgo;
--   DROP TABLE dbo.sis_log_retencao;
--   DROP TABLE dbo.sis_log_selo;
--   DROP TABLE dbo.sis_log_regra;
--   -- colunas novas em sis_log_auditoria permanecem (sem impacto)
-- ============================================================

SET NOCOUNT ON;

-- ------------------------------------------------------------
-- 1) Evolução da tabela central dbo.sis_log_auditoria
-- ------------------------------------------------------------
IF COL_LENGTH('dbo.sis_log_auditoria', 'metodo') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD metodo VARCHAR(10) NULL;

IF COL_LENGTH('dbo.sis_log_auditoria', 'rota') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD rota VARCHAR(400) NULL;

IF COL_LENGTH('dbo.sis_log_auditoria', 'status_http') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD status_http INT NULL;

IF COL_LENGTH('dbo.sis_log_auditoria', 'duracao_ms') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD duracao_ms INT NULL;

-- AUTO (filtro), MANUAL (instrumentação no código) ou SISTEMA (job)
IF COL_LENGTH('dbo.sis_log_auditoria', 'tipo_captura') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD tipo_captura VARCHAR(20) NULL;

-- LEITURA | ALTERACAO | ACESSO | ERRO | EXPORTACAO
IF COL_LENGTH('dbo.sis_log_auditoria', 'nivel') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD nivel VARCHAR(20) NULL;

IF COL_LENGTH('dbo.sis_log_auditoria', 'id_regra') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD id_regra INT NULL;

-- JSON de parâmetros da requisição, já mascarado
IF COL_LENGTH('dbo.sis_log_auditoria', 'dados_extra') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD dados_extra NVARCHAR(4000) NULL;

-- Cadeia de integridade (SHA-256 hex, 64 chars)
IF COL_LENGTH('dbo.sis_log_auditoria', 'hash_evento') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD hash_evento CHAR(64) NULL;

IF COL_LENGTH('dbo.sis_log_auditoria', 'hash_anterior') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD hash_anterior CHAR(64) NULL;

-- Marca se o evento já foi coberto por um selo diário
IF COL_LENGTH('dbo.sis_log_auditoria', 'selado') IS NULL
    ALTER TABLE dbo.sis_log_auditoria ADD selado BIT NOT NULL
        CONSTRAINT DF_sla_selado DEFAULT 0;
GO

-- Índices de apoio
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_sla_nivel' AND object_id = OBJECT_ID('dbo.sis_log_auditoria'))
    CREATE INDEX IX_sla_nivel ON dbo.sis_log_auditoria (nivel);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_sla_tipo' AND object_id = OBJECT_ID('dbo.sis_log_auditoria'))
    CREATE INDEX IX_sla_tipo ON dbo.sis_log_auditoria (tipo_captura);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_sla_selado' AND object_id = OBJECT_ID('dbo.sis_log_auditoria'))
    CREATE INDEX IX_sla_selado ON dbo.sis_log_auditoria (selado, dt_operacao);
GO

PRINT 'OK: dbo.sis_log_auditoria evoluida';
GO

-- ------------------------------------------------------------
-- 2) Regras de captura configuráveis
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'dbo' AND t.name = 'sis_log_regra')
BEGIN
    CREATE TABLE dbo.sis_log_regra (
        id                   INT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_sis_log_regra PRIMARY KEY,
        ordem                INT          NOT NULL CONSTRAINT DF_slr_ordem DEFAULT 100,
        descricao            VARCHAR(200) NOT NULL,
        -- Padrão da rota (sem o context-path). Aceita % como curinga. Ex.: '/MuralhaDigital/%'
        padrao_rota          VARCHAR(300) NOT NULL,
        -- GET/POST/PUT/DELETE. NULL = qualquer método.
        metodo               VARCHAR(10)  NULL,
        -- 1 = captura; 0 = ignora (exclusão de ruído).
        capturar             BIT          NOT NULL CONSTRAINT DF_slr_capturar DEFAULT 1,
        -- AUTO (deriva do método) | LEITURA | ALTERACAO | ACESSO | ERRO | EXPORTACAO
        nivel                VARCHAR(20)  NOT NULL CONSTRAINT DF_slr_nivel DEFAULT 'AUTO',
        -- Nome amigável da funcionalidade (sobrescreve a derivação pela rota).
        funcionalidade       VARCHAR(100) NULL,
        -- 1 = grava o JSON de parâmetros (mascarado) em dados_extra.
        registrar_parametros BIT          NOT NULL CONSTRAINT DF_slr_regparam DEFAULT 0,
        -- Nomes de parâmetros a mascarar, separados por vírgula (além dos padrões CPF/placa/senha).
        mascarar_campos      VARCHAR(400) NULL,
        -- 1 = captura garantida (grava de forma síncrona antes de responder).
        garantido            BIT          NOT NULL CONSTRAINT DF_slr_garantido DEFAULT 0,
        ativo                BIT          NOT NULL CONSTRAINT DF_slr_ativo DEFAULT 1,
        dt_criacao           DATETIME2(3) NOT NULL CONSTRAINT DF_slr_dt DEFAULT SYSDATETIME(),
        login_criacao        VARCHAR(50)  NULL
    );
    CREATE INDEX IX_slr_ordem ON dbo.sis_log_regra (ativo, ordem);
    PRINT 'Criada: dbo.sis_log_regra';
END
ELSE
    PRINT 'Ja existe: dbo.sis_log_regra';
GO

-- Seed das regras padrão (somente se a tabela estiver vazia).
-- Postura de fábrica escolhida: capturar TUDO (inclusive leituras) em todas as
-- telas, excluindo apenas ruído que não é operação (estáticos, polling, websocket).
IF NOT EXISTS (SELECT 1 FROM dbo.sis_log_regra)
BEGIN
    INSERT INTO dbo.sis_log_regra (ordem, descricao, padrao_rota, metodo, capturar, nivel, funcionalidade, registrar_parametros, mascarar_campos, garantido)
    VALUES
    -- ---- Exclusões de ruído (não são operação de negócio) ----
    (10, 'Ignorar CSS',            '%.css',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (11, 'Ignorar JS',             '%.js',         NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (12, 'Ignorar mapas de source','%.map',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (13, 'Ignorar PNG',            '%.png',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (14, 'Ignorar JPG',            '%.jpg',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (15, 'Ignorar JPEG',           '%.jpeg',       NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (16, 'Ignorar GIF',            '%.gif',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (17, 'Ignorar SVG',            '%.svg',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (18, 'Ignorar ICO',            '%.ico',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (19, 'Ignorar fontes woff',    '%.woff%',      NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (20, 'Ignorar fontes ttf',     '%.ttf',        NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (21, 'Ignorar favicon',        '%favicon%',    NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (22, 'Ignorar WebSocket',      '%ClientesWebSocket%', NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (23, 'Ignorar polling status', '%AguardaStatusEquipamento%', NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (24, 'Ignorar acesso remoto polling', '%AcessoRemotoPolling%', NULL, 0, 'AUTO', NULL, 0, NULL, 0),
    (25, 'Ignorar imagens de veiculo (thumb)', '%/ajax/Img%', 'GET', 0, 'AUTO', NULL, 0, NULL, 0),
    -- ---- Operações sensíveis: captura garantida (síncrona) ----
    (50, 'Autenticacao (login/logout)', '%/login/%', NULL, 1, 'ACESSO', 'Autenticacao', 1, 'senha,password,j_password,pwd', 1),
    (51, 'Exclusoes (DELETE)',      '%',            'DELETE', 1, 'ALTERACAO', NULL, 1, NULL, 1),
    (52, 'Gestao de usuarios',      '%/MuralhaDigital/Usuario%', NULL, 1, 'ALTERACAO', 'Gestao de Usuarios', 1, 'senha,password,novaSenha,confirmaSenha', 1),
    (53, 'Controle de acesso/menus','%/MuralhaDigital/%Acesso%', NULL, 1, 'ALTERACAO', 'Controle de Acesso', 1, NULL, 1),
    -- ---- Regra geral: captura tudo ----
    (100, 'Alteracoes Muralha Digital (POST)', '%/MuralhaDigital/%', 'POST', 1, 'ALTERACAO', NULL, 1, NULL, 0),
    (101, 'Leituras Muralha Digital (GET)',    '%/MuralhaDigital/%', 'GET',  1, 'LEITURA',   NULL, 0, NULL, 0),
    (102, 'API de dashboards',                 '%/api/dash/%',       NULL,   1, 'AUTO',      'Dashboards', 1, NULL, 0),
    (103, 'REST (Jersey)',                     '%/rest/%',           NULL,   1, 'AUTO',      'REST', 0, NULL, 0),
    -- ---- Catch-all: qualquer outra operação (inclui legado GTW classico) ----
    (900, 'Catch-all (legado e demais telas)', '%',                 NULL,   1, 'AUTO',      NULL, 0, NULL, 0);

    PRINT 'Seed: regras padrao inseridas';
END
ELSE
    PRINT 'Seed ignorado: dbo.sis_log_regra ja possui regras';
GO

-- ------------------------------------------------------------
-- 3) Selo diário de integridade
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'dbo' AND t.name = 'sis_log_selo')
BEGIN
    CREATE TABLE dbo.sis_log_selo (
        id             INT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_sis_log_selo PRIMARY KEY,
        dia            DATE         NOT NULL
            CONSTRAINT UQ_sls_dia UNIQUE,
        id_evento_ini  BIGINT       NULL,
        id_evento_fim  BIGINT       NULL,
        qtd_eventos    INT          NOT NULL CONSTRAINT DF_sls_qtd DEFAULT 0,
        -- hash do último evento do dia (cabeça da cadeia no fechamento)
        hash_final     CHAR(64)     NULL,
        -- selo = SHA-256( selo_anterior + resumo do dia )
        selo           CHAR(64)     NOT NULL,
        selo_anterior  CHAR(64)     NULL,
        dt_geracao     DATETIME2(3) NOT NULL CONSTRAINT DF_sls_dt DEFAULT SYSDATETIME()
    );
    PRINT 'Criada: dbo.sis_log_selo';
END
ELSE
    PRINT 'Ja existe: dbo.sis_log_selo';
GO

-- ------------------------------------------------------------
-- 4) Políticas de retenção configuráveis
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'dbo' AND t.name = 'sis_log_retencao')
BEGIN
    CREATE TABLE dbo.sis_log_retencao (
        id             INT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_sis_log_retencao PRIMARY KEY,
        -- NULL = aplica a todos os níveis; senão LEITURA/ALTERACAO/ACESSO/ERRO/EXPORTACAO
        nivel          VARCHAR(20)  NULL,
        dias_retencao  INT          NOT NULL,
        -- 1 = exporta (arquivo selado) antes de apagar
        arquivar_antes BIT          NOT NULL CONSTRAINT DF_slret_arq DEFAULT 1,
        -- 1 = retenção legal: bloqueia o expurgo (hold)
        hold_legal     BIT          NOT NULL CONSTRAINT DF_slret_hold DEFAULT 0,
        ativo          BIT          NOT NULL CONSTRAINT DF_slret_ativo DEFAULT 1,
        descricao      VARCHAR(200) NULL,
        dt_criacao     DATETIME2(3) NOT NULL CONSTRAINT DF_slret_dt DEFAULT SYSDATETIME(),
        login_criacao  VARCHAR(50)  NULL
    );
    PRINT 'Criada: dbo.sis_log_retencao';
END
ELSE
    PRINT 'Ja existe: dbo.sis_log_retencao';
GO

IF NOT EXISTS (SELECT 1 FROM dbo.sis_log_retencao)
BEGIN
    INSERT INTO dbo.sis_log_retencao (nivel, dias_retencao, arquivar_antes, hold_legal, descricao)
    VALUES
    ('LEITURA',    90,   0, 0, 'Consultas/aberturas de tela: 90 dias'),
    ('ACESSO',     365,  1, 0, 'Autenticacao e acessos negados: 1 ano'),
    ('ERRO',       365,  1, 0, 'Erros: 1 ano'),
    ('EXPORTACAO', 1825, 1, 0, 'Exportacoes de dados: 5 anos'),
    ('ALTERACAO',  1825, 1, 0, 'Alteracoes de dados: 5 anos'),
    (NULL,         730,  1, 0, 'Padrao (demais niveis): 2 anos');
    PRINT 'Seed: politicas de retencao padrao inseridas';
END
ELSE
    PRINT 'Seed ignorado: dbo.sis_log_retencao ja possui politicas';
GO

-- ------------------------------------------------------------
-- 5) Histórico de expurgos (auditoria do próprio expurgo)
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'dbo' AND t.name = 'sis_log_expurgo')
BEGIN
    CREATE TABLE dbo.sis_log_expurgo (
        id             INT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_sis_log_expurgo PRIMARY KEY,
        dt_execucao    DATETIME2(3) NOT NULL CONSTRAINT DF_sle_dt DEFAULT SYSDATETIME(),
        id_politica    INT          NULL,
        nivel          VARCHAR(20)  NULL,
        intervalo_ini  DATETIME2(3) NULL,
        intervalo_fim  DATETIME2(3) NULL,
        qtd_removida   INT          NOT NULL CONSTRAINT DF_sle_qtd DEFAULT 0,
        arquivo_gerado VARCHAR(400) NULL,
        login          VARCHAR(50)  NULL,
        observacao     VARCHAR(500) NULL
    );
    PRINT 'Criada: dbo.sis_log_expurgo';
END
ELSE
    PRINT 'Ja existe: dbo.sis_log_expurgo';
GO

PRINT '============================================================';
PRINT 'Motor de Captura de Logs: migracao concluida';
PRINT '============================================================';
GO

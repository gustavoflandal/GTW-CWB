-- ============================================================
-- Plano 14: Gestão de Lotes de Infrações
-- Plano 15: KPIs Configuráveis
-- ============================================================
-- ROLLBACK:
--   DROP TABLE muralha.lote_infracao_item;
--   DROP TABLE muralha.lote_infracao;
--   DROP TABLE muralha.kpi_config;
-- ============================================================

-- ── Plano 14: Gestão de lotes ──────────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'lote_infracao'
)
BEGIN
    CREATE TABLE muralha.lote_infracao (
        id               BIGINT IDENTITY(1,1) PRIMARY KEY,
        codigo           VARCHAR(30)  NOT NULL UNIQUE,
        descricao        VARCHAR(200) NULL,
        status           VARCHAR(20)  NOT NULL DEFAULT 'RASCUNHO',
        dt_criacao       DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
        dt_envio         DATETIME2    NULL,
        dt_confirmacao   DATETIME2    NULL,
        id_usuario_criou INT          NOT NULL,
        id_usuario_enviou INT         NULL,
        observacao       VARCHAR(500) NULL
    );
    PRINT 'Criada: muralha.lote_infracao';
END
ELSE
    PRINT 'Ja existe: muralha.lote_infracao';

IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'lote_infracao_item'
)
BEGIN
    CREATE TABLE muralha.lote_infracao_item (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        id_lote         BIGINT           NOT NULL REFERENCES muralha.lote_infracao(id),
        id_infracao     UNIQUEIDENTIFIER NOT NULL,
        dt_inclusao     DATETIME2(3)     NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT uq_lote_item UNIQUE (id_infracao)
    );
    CREATE INDEX ix_lote_item_lote ON muralha.lote_infracao_item (id_lote);
    CREATE INDEX ix_lote_status ON muralha.lote_infracao (status);
    PRINT 'Criada: muralha.lote_infracao_item';
END
ELSE
    PRINT 'Ja existe: muralha.lote_infracao_item';

-- ── Plano 15: KPIs configuráveis ───────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'kpi_config'
)
BEGIN
    CREATE TABLE muralha.kpi_config (
        id              INT IDENTITY(1,1) PRIMARY KEY,
        nome            VARCHAR(80)  NOT NULL,
        descricao       VARCHAR(200) NULL,
        query_sql       VARCHAR(MAX) NOT NULL,
        unidade         VARCHAR(20)  NULL,
        threshold_ok    FLOAT        NULL,
        threshold_warn  FLOAT        NULL,
        ordem           INT          NOT NULL DEFAULT 0,
        ativo           BIT          NOT NULL DEFAULT 1
    );
    PRINT 'Criada: muralha.kpi_config';

    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem) VALUES
    ('Passagens Hoje',
     'Total de passagens capturadas hoje',
     'SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real WITH (NOLOCK) WHERE CAST(data AS DATE) = CAST(SYSDATETIME() AS DATE)',
     'registros', 1000, 100, 1),

    ('Pendentes de Análise',
     'Passagens sem análise (status AGUARDANDO_ANALISE ou sem status)',
     'SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK) LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id WHERE vsa.status_analise IS NULL OR vsa.status_analise = ''AGUARDANDO_ANALISE''',
     'registros', 500, 2000, 2),

    ('Pré-aprovadas (7d)',
     'Passagens pré-aprovadas nos últimos 7 dias',
     'SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.vtr_status_analise WHERE status_analise = ''PRE_APROVADA'' AND dt_status >= DATEADD(DAY, -7, SYSDATETIME())',
     'registros', 0, 0, 3),

    ('Equipamentos Ativos (24h)',
     'Locais que registraram passagens nas últimas 24 horas',
     'SELECT CAST(COUNT(DISTINCT id_local) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real WITH (NOLOCK) WHERE data >= DATEADD(HOUR, -24, SYSDATETIME())',
     'locais', 5, 2, 4);
END
ELSE
    PRINT 'Ja existe: muralha.kpi_config';

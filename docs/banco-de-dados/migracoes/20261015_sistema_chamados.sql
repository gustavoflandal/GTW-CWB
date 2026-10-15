-- =============================================================================
-- Migracao: Sistema de Gerenciamento de Chamados (Plano 22 — itens 7.2.3 e 7.2.4)
-- Data: 2026-10-15
-- Idempotente; nao altera tabelas existentes. Executar somente com autorizacao.
-- ROLLBACK (nesta ordem):
--   DROP TABLE muralha.chamado_notificacao;
--   DROP TABLE muralha.chamado_anexo;
--   DROP TABLE muralha.chamado_historico;
--   DROP TABLE muralha.chamado;
--   DROP TABLE muralha.chamado_categoria;
--   DROP TABLE muralha.chamado_prioridade;
--   DROP TABLE muralha.chamado_protocolo;
-- =============================================================================

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_prioridade')
BEGIN
    CREATE TABLE muralha.chamado_prioridade (
        id                  INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_prioridade PRIMARY KEY,
        codigo              VARCHAR(20)  NOT NULL CONSTRAINT uq_chamado_prioridade_codigo UNIQUE,
        nome                NVARCHAR(50) NOT NULL,
        ordem               TINYINT      NOT NULL,
        minutos_atendimento INT          NOT NULL,
        minutos_resolucao   INT          NOT NULL,
        cor                 VARCHAR(7)   NOT NULL CONSTRAINT df_chamado_prioridade_cor DEFAULT '#6c757d',
        ativo               BIT          NOT NULL CONSTRAINT df_chamado_prioridade_ativo DEFAULT 1,
        CONSTRAINT chk_chamado_prioridade_minutos
            CHECK (minutos_atendimento > 0 AND minutos_resolucao >= minutos_atendimento)
    );
    PRINT 'Criada muralha.chamado_prioridade';
END
ELSE PRINT 'Ja existe muralha.chamado_prioridade';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_categoria')
BEGIN
    CREATE TABLE muralha.chamado_categoria (
        id                INT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_categoria PRIMARY KEY,
        nome              NVARCHAR(100) NOT NULL,
        id_pai            INT NULL CONSTRAINT fk_chamado_categoria_pai REFERENCES muralha.chamado_categoria(id),
        exige_equipamento BIT NOT NULL CONSTRAINT df_chamado_categoria_exige DEFAULT 0,
        ativo             BIT NOT NULL CONSTRAINT df_chamado_categoria_ativo DEFAULT 1
    );
    PRINT 'Criada muralha.chamado_categoria';
END
ELSE PRINT 'Ja existe muralha.chamado_categoria';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_protocolo')
BEGIN
    CREATE TABLE muralha.chamado_protocolo (
        ano    SMALLINT NOT NULL CONSTRAINT pk_chamado_protocolo PRIMARY KEY,
        ultimo INT      NOT NULL
    );
    PRINT 'Criada muralha.chamado_protocolo';
END
ELSE PRINT 'Ja existe muralha.chamado_protocolo';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado')
BEGIN
    CREATE TABLE muralha.chamado (
        id                       BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado PRIMARY KEY,
        protocolo                VARCHAR(20)    NOT NULL CONSTRAINT uq_chamado_protocolo UNIQUE,
        titulo                   NVARCHAR(150)  NOT NULL,
        descricao                NVARCHAR(MAX)  NOT NULL,
        id_categoria             INT NOT NULL CONSTRAINT fk_chamado_categoria REFERENCES muralha.chamado_categoria(id),
        id_prioridade            INT NOT NULL CONSTRAINT fk_chamado_prioridade REFERENCES muralha.chamado_prioridade(id),
        id_prioridade_solicitada INT NOT NULL CONSTRAINT fk_chamado_prioridade_sol REFERENCES muralha.chamado_prioridade(id),
        status                   VARCHAR(30)    NOT NULL CONSTRAINT df_chamado_status DEFAULT 'ABERTO',
        origem                   VARCHAR(20)    NOT NULL CONSTRAINT df_chamado_origem DEFAULT 'PORTAL',
        id_local                 INT NULL,
        id_usuario_solicitante   INT NOT NULL,
        id_usuario_responsavel   INT NULL,
        dt_abertura              DATETIME2(3) NOT NULL CONSTRAINT df_chamado_dt_abertura DEFAULT SYSDATETIME(),
        dt_prazo_atendimento     DATETIME2(3) NOT NULL,
        dt_prazo_resolucao       DATETIME2(3) NOT NULL,
        dt_primeiro_atendimento  DATETIME2(3) NULL,
        dt_resolucao             DATETIME2(3) NULL,
        dt_fechamento            DATETIME2(3) NULL,
        minutos_pausados         INT NOT NULL CONSTRAINT df_chamado_min_pausados DEFAULT 0,
        dt_inicio_pausa          DATETIME2(3) NULL,
        sla_atendimento          VARCHAR(10) NOT NULL CONSTRAINT df_chamado_sla_at DEFAULT 'NO_PRAZO',
        sla_resolucao            VARCHAR(10) NOT NULL CONSTRAINT df_chamado_sla_res DEFAULT 'NO_PRAZO',
        avaliacao_nota           TINYINT NULL,
        avaliacao_comentario     NVARCHAR(1000) NULL,
        dt_avaliacao             DATETIME2(3) NULL,
        fechado_automaticamente  BIT NOT NULL CONSTRAINT df_chamado_fech_auto DEFAULT 0,
        dt_atualizacao           DATETIME2(3) NOT NULL CONSTRAINT df_chamado_dt_atualizacao DEFAULT SYSDATETIME(),
        CONSTRAINT chk_chamado_status CHECK (status IN
            ('ABERTO','EM_ATENDIMENTO','AGUARDANDO_SOLICITANTE','RESOLVIDO','FECHADO','CANCELADO')),
        CONSTRAINT chk_chamado_sla_at  CHECK (sla_atendimento IN ('NO_PRAZO','EM_RISCO','VENCIDO','CUMPRIDO','VIOLADO')),
        CONSTRAINT chk_chamado_sla_res CHECK (sla_resolucao   IN ('NO_PRAZO','EM_RISCO','VENCIDO','CUMPRIDO','VIOLADO')),
        CONSTRAINT chk_chamado_nota    CHECK (avaliacao_nota IS NULL OR avaliacao_nota BETWEEN 1 AND 5)
    );
    PRINT 'Criada muralha.chamado';
END
ELSE PRINT 'Ja existe muralha.chamado';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_historico')
BEGIN
    CREATE TABLE muralha.chamado_historico (
        id              BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_historico PRIMARY KEY,
        id_chamado      BIGINT NOT NULL CONSTRAINT fk_chamado_historico_chamado REFERENCES muralha.chamado(id),
        dt_evento       DATETIME2(3) NOT NULL CONSTRAINT df_chamado_historico_dt DEFAULT SYSDATETIME(),
        id_usuario      INT NULL,
        tipo            VARCHAR(20) NOT NULL,
        status_anterior VARCHAR(30) NULL,
        status_novo     VARCHAR(30) NULL,
        texto           NVARCHAR(2000) NULL,
        CONSTRAINT chk_chamado_historico_tipo CHECK (tipo IN
            ('ABERTURA','TRANSICAO','RESPOSTA','RECLASSIFICACAO','ATRIBUICAO','AVALIACAO','SLA'))
    );
    PRINT 'Criada muralha.chamado_historico';
END
ELSE PRINT 'Ja existe muralha.chamado_historico';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_anexo')
BEGIN
    CREATE TABLE muralha.chamado_anexo (
        id            BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_anexo PRIMARY KEY,
        id_chamado    BIGINT NOT NULL CONSTRAINT fk_chamado_anexo_chamado REFERENCES muralha.chamado(id),
        id_historico  BIGINT NULL CONSTRAINT fk_chamado_anexo_historico REFERENCES muralha.chamado_historico(id),
        nome_original NVARCHAR(255) NOT NULL,
        caminho       NVARCHAR(400) NOT NULL,
        tipo_mime     VARCHAR(100)  NOT NULL,
        tamanho_bytes BIGINT        NOT NULL,
        sha256        CHAR(64)      NOT NULL,
        dt_envio      DATETIME2(3)  NOT NULL CONSTRAINT df_chamado_anexo_dt DEFAULT SYSDATETIME(),
        id_usuario    INT NOT NULL
    );
    PRINT 'Criada muralha.chamado_anexo';
END
ELSE PRINT 'Ja existe muralha.chamado_anexo';
GO

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'muralha' AND t.name = 'chamado_notificacao')
BEGIN
    CREATE TABLE muralha.chamado_notificacao (
        id           BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_chamado_notificacao PRIMARY KEY,
        id_chamado   BIGINT NOT NULL CONSTRAINT fk_chamado_notificacao_chamado REFERENCES muralha.chamado(id),
        tipo         VARCHAR(30)  NOT NULL,
        destinatario VARCHAR(200) NULL,
        status       VARCHAR(10)  NOT NULL CONSTRAINT df_chamado_notificacao_status DEFAULT 'PENDENTE',
        tentativas   TINYINT      NOT NULL CONSTRAINT df_chamado_notificacao_tent DEFAULT 0,
        dt_criacao   DATETIME2(3) NOT NULL CONSTRAINT df_chamado_notificacao_dt DEFAULT SYSDATETIME(),
        dt_envio     DATETIME2(3) NULL,
        erro         NVARCHAR(500) NULL,
        CONSTRAINT chk_chamado_notificacao_tipo   CHECK (tipo IN ('CONFIRMACAO_ABERTURA','RECLASSIFICACAO')),
        CONSTRAINT chk_chamado_notificacao_status CHECK (status IN ('PENDENTE','ENVIADO','ERRO'))
    );
    PRINT 'Criada muralha.chamado_notificacao';
END
ELSE PRINT 'Ja existe muralha.chamado_notificacao';
GO

-- Indices (cada um idempotente)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_status_abertura' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_status_abertura ON muralha.chamado (status, dt_abertura);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_solicitante' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_solicitante ON muralha.chamado (id_usuario_solicitante, dt_abertura);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_categoria' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_categoria ON muralha.chamado (id_categoria);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_local' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_local ON muralha.chamado (id_local);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_atualizacao' AND object_id = OBJECT_ID('muralha.chamado'))
    CREATE INDEX ix_chamado_atualizacao ON muralha.chamado (dt_atualizacao);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_historico_chamado' AND object_id = OBJECT_ID('muralha.chamado_historico'))
    CREATE INDEX ix_chamado_historico_chamado ON muralha.chamado_historico (id_chamado, dt_evento);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_anexo_chamado' AND object_id = OBJECT_ID('muralha.chamado_anexo'))
    CREATE INDEX ix_chamado_anexo_chamado ON muralha.chamado_anexo (id_chamado);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_chamado_notificacao_status' AND object_id = OBJECT_ID('muralha.chamado_notificacao'))
    CREATE INDEX ix_chamado_notificacao_status ON muralha.chamado_notificacao (status, dt_criacao);
PRINT 'Indices verificados';
GO

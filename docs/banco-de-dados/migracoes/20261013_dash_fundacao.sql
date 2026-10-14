-- =============================================================================
-- DASH-1: Fundacao do construtor de dashboards
-- Schema: dash
-- Banco: GTW_MURALHA_DEV (SQL Server 2016+)
-- Data: 2026-10-13
-- =============================================================================
-- ROLLBACK:
--   DROP TABLE dash.historico_versao;
--   DROP TABLE dash.compartilhamento;
--   DROP TABLE dash.widget;
--   DROP TABLE dash.dashboard;
--   DROP TABLE dash.dataset_campo;
--   DROP TABLE dash.dataset;
--   DROP TABLE dash.catalogo_relacao;
--   DROP TABLE dash.catalogo_coluna;
--   DROP TABLE dash.catalogo_tabela;
--   DROP SCHEMA dash;
-- =============================================================================

-- -----------------------------------------------
-- Schema
-- -----------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'dash')
    EXEC('CREATE SCHEMA dash');
GO

-- -----------------------------------------------
-- 1. catalogo_tabela
-- -----------------------------------------------
CREATE TABLE dash.catalogo_tabela (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    nome_fisico     VARCHAR(128)    NOT NULL,
    schema_fisico   VARCHAR(128)    NOT NULL DEFAULT 'dbo',
    rotulo          NVARCHAR(120)   NOT NULL,
    descricao       NVARCHAR(500)   NULL,
    chave_primaria  VARCHAR(128)    NOT NULL,
    sensivel        BIT             NOT NULL DEFAULT 0,
    ativo           BIT             NOT NULL DEFAULT 1,
    criado_em       DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    atualizado_em   DATETIME2(3)    NULL,
    CONSTRAINT uq_catalogo_tabela UNIQUE (schema_fisico, nome_fisico)
);

-- -----------------------------------------------
-- 2. catalogo_coluna
-- -----------------------------------------------
CREATE TABLE dash.catalogo_coluna (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    id_tabela       INT             NOT NULL,
    nome_fisico     VARCHAR(128)    NOT NULL,
    rotulo          NVARCHAR(120)   NOT NULL,
    tipo            VARCHAR(20)     NOT NULL,
    sensivel        BIT             NOT NULL DEFAULT 0,
    papel_sugerido  VARCHAR(20)     NULL,
    ativo           BIT             NOT NULL DEFAULT 1,
    CONSTRAINT fk_coluna_tabela FOREIGN KEY (id_tabela)
        REFERENCES dash.catalogo_tabela(id),
    CONSTRAINT chk_coluna_tipo CHECK (tipo IN (
        'texto','numerico','inteiro','data','data_hora','booleano','geo_lat','geo_lng'
    )),
    CONSTRAINT chk_coluna_papel CHECK (papel_sugerido IS NULL OR papel_sugerido IN (
        'dimensao','medida','tempo','geo'
    )),
    CONSTRAINT uq_coluna UNIQUE (id_tabela, nome_fisico)
);

-- -----------------------------------------------
-- 3. catalogo_relacao
-- -----------------------------------------------
CREATE TABLE dash.catalogo_relacao (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    id_tabela_origem    INT             NOT NULL,
    id_tabela_destino   INT             NOT NULL,
    cardinalidade       VARCHAR(20)     NOT NULL,
    coluna_origem       VARCHAR(128)    NOT NULL,
    coluna_destino      VARCHAR(128)    NOT NULL,
    ativo               BIT             NOT NULL DEFAULT 1,
    CONSTRAINT fk_relacao_origem FOREIGN KEY (id_tabela_origem)
        REFERENCES dash.catalogo_tabela(id),
    CONSTRAINT fk_relacao_destino FOREIGN KEY (id_tabela_destino)
        REFERENCES dash.catalogo_tabela(id),
    CONSTRAINT chk_cardinalidade CHECK (cardinalidade IN (
        'many_to_one','one_to_many','one_to_one'
    ))
);

-- -----------------------------------------------
-- 4. dataset
-- -----------------------------------------------
CREATE TABLE dash.dataset (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    nome            NVARCHAR(120)   NOT NULL,
    descricao       NVARCHAR(500)   NULL,
    id_tabela_base  INT             NOT NULL,
    id_usuario      INT             NOT NULL,
    tenant_id       INT             NOT NULL DEFAULT 1,
    visibilidade    VARCHAR(10)     NOT NULL DEFAULT 'privado',
    status          VARCHAR(15)     NOT NULL DEFAULT 'rascunho',
    versao          INT             NOT NULL DEFAULT 1,
    criado_em       DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    atualizado_em   DATETIME2(3)    NULL,
    CONSTRAINT fk_dataset_tabela FOREIGN KEY (id_tabela_base)
        REFERENCES dash.catalogo_tabela(id),
    CONSTRAINT fk_dataset_usuario FOREIGN KEY (id_usuario)
        REFERENCES dbo.sis_usuario(id_usuario),
    CONSTRAINT chk_visibilidade CHECK (visibilidade IN ('privado','publico')),
    CONSTRAINT chk_status CHECK (status IN ('rascunho','publicado','arquivado'))
);

-- -----------------------------------------------
-- 5. dataset_campo
-- -----------------------------------------------
CREATE TABLE dash.dataset_campo (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    id_dataset      INT             NOT NULL,
    id_coluna       INT             NULL,
    nome_alias      VARCHAR(80)     NOT NULL,
    rotulo          NVARCHAR(120)   NOT NULL,
    expressao       NVARCHAR(500)   NULL,
    agregacao       VARCHAR(20)     NOT NULL DEFAULT 'NONE',
    formato         VARCHAR(30)     NULL,
    tipo_campo      VARCHAR(15)     NOT NULL DEFAULT 'dimensao',
    ordem           INT             NOT NULL DEFAULT 0,
    CONSTRAINT fk_campo_dataset FOREIGN KEY (id_dataset)
        REFERENCES dash.dataset(id),
    CONSTRAINT fk_campo_coluna FOREIGN KEY (id_coluna)
        REFERENCES dash.catalogo_coluna(id),
    CONSTRAINT chk_agregacao CHECK (agregacao IN (
        'NONE','SUM','AVG','COUNT','COUNT_DISTINCT','MIN','MAX'
    )),
    CONSTRAINT chk_tipo_campo CHECK (tipo_campo IN ('dimensao','medida','tempo'))
);

-- -----------------------------------------------
-- 6. dashboard (painel)
-- -----------------------------------------------
CREATE TABLE dash.dashboard (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    nome            NVARCHAR(120)   NOT NULL,
    descricao       NVARCHAR(500)   NULL,
    id_usuario      INT             NOT NULL,
    tenant_id       INT             NOT NULL DEFAULT 1,
    layout_json     NVARCHAR(MAX)   NULL,
    filtros_json    NVARCHAR(MAX)   NULL,
    refresh_seg     INT             NOT NULL DEFAULT 0,
    versao          INT             NOT NULL DEFAULT 1,
    ativo           BIT             NOT NULL DEFAULT 1,
    criado_em       DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    atualizado_em   DATETIME2(3)    NULL,
    CONSTRAINT fk_dashboard_usuario FOREIGN KEY (id_usuario)
        REFERENCES dbo.sis_usuario(id_usuario),
    CONSTRAINT chk_layout_json CHECK (layout_json IS NULL OR ISJSON(layout_json) = 1),
    CONSTRAINT chk_filtros_json CHECK (filtros_json IS NULL OR ISJSON(filtros_json) = 1)
);

-- -----------------------------------------------
-- 7. widget
-- -----------------------------------------------
CREATE TABLE dash.widget (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    id_dashboard    INT             NOT NULL,
    id_dataset      INT             NOT NULL,
    titulo          NVARCHAR(120)   NOT NULL,
    tipo            VARCHAR(20)     NOT NULL,
    consulta_json   NVARCHAR(MAX)   NOT NULL,
    visual_json     NVARCHAR(MAX)   NULL,
    grid_x          INT             NOT NULL DEFAULT 0,
    grid_y          INT             NOT NULL DEFAULT 0,
    grid_w          INT             NOT NULL DEFAULT 4,
    grid_h          INT             NOT NULL DEFAULT 3,
    ordem           INT             NOT NULL DEFAULT 0,
    CONSTRAINT fk_widget_dashboard FOREIGN KEY (id_dashboard)
        REFERENCES dash.dashboard(id),
    CONSTRAINT fk_widget_dataset FOREIGN KEY (id_dataset)
        REFERENCES dash.dataset(id),
    CONSTRAINT chk_widget_tipo CHECK (tipo IN (
        'barras','linhas','area','pizza','rosca','dispersao',
        'combinado','funil','gauge','heatmap','kpi','tabela','pivot','texto'
    )),
    CONSTRAINT chk_consulta_json CHECK (ISJSON(consulta_json) = 1),
    CONSTRAINT chk_visual_json CHECK (visual_json IS NULL OR ISJSON(visual_json) = 1)
);

-- -----------------------------------------------
-- 8. compartilhamento
-- -----------------------------------------------
CREATE TABLE dash.compartilhamento (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    tipo_recurso    VARCHAR(15)     NOT NULL,
    id_recurso      INT             NOT NULL,
    id_usuario      INT             NULL,
    id_perfil       INT             NULL,
    nivel           VARCHAR(10)     NOT NULL DEFAULT 'ver',
    criado_em       DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT chk_tipo_recurso CHECK (tipo_recurso IN ('dashboard','dataset')),
    CONSTRAINT chk_nivel CHECK (nivel IN ('ver','editar')),
    CONSTRAINT chk_destino CHECK (id_usuario IS NOT NULL OR id_perfil IS NOT NULL)
);

-- -----------------------------------------------
-- 9. historico_versao
-- -----------------------------------------------
CREATE TABLE dash.historico_versao (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    tipo_recurso    VARCHAR(15)     NOT NULL,
    id_recurso      INT             NOT NULL,
    versao          INT             NOT NULL,
    snapshot_json   NVARCHAR(MAX)   NOT NULL,
    id_autor        INT             NOT NULL,
    criado_em       DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT chk_hist_tipo CHECK (tipo_recurso IN ('dashboard','dataset')),
    CONSTRAINT chk_hist_json CHECK (ISJSON(snapshot_json) = 1)
);

-- -----------------------------------------------
-- Indices
-- -----------------------------------------------
CREATE INDEX ix_dataset_usuario ON dash.dataset (id_usuario);
CREATE INDEX ix_dataset_tenant ON dash.dataset (tenant_id, status);
CREATE INDEX ix_dashboard_usuario ON dash.dashboard (id_usuario);
CREATE INDEX ix_dashboard_tenant ON dash.dashboard (tenant_id, ativo);
CREATE INDEX ix_widget_dashboard ON dash.widget (id_dashboard);
CREATE INDEX ix_compartilhamento ON dash.compartilhamento (tipo_recurso, id_recurso);
CREATE INDEX ix_historico ON dash.historico_versao (tipo_recurso, id_recurso, versao);

-- -----------------------------------------------
-- Login de leitura para o Cube (somente SELECT)
-- -----------------------------------------------
-- NOTA: executar com permissao de sysadmin
-- IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'cube_leitura')
--     CREATE LOGIN cube_leitura WITH PASSWORD = '<DEFINIR_SENHA_SEGURA>';
-- GO
-- USE GTW_MURALHA_DEV;
-- IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'cube_leitura')
--     CREATE USER cube_leitura FOR LOGIN cube_leitura;
-- GRANT SELECT ON SCHEMA::dbo TO cube_leitura;
-- GRANT SELECT ON SCHEMA::muralha TO cube_leitura;
-- DENY SELECT ON SCHEMA::dash TO cube_leitura;
-- GO

-- -----------------------------------------------
-- Seed: catalogo com tabelas existentes
-- -----------------------------------------------
INSERT INTO dash.catalogo_tabela (nome_fisico, schema_fisico, rotulo, chave_primaria)
VALUES
    ('veiculo_tempo_real', 'muralha', 'Passagens (Tempo Real)', 'id'),
    ('local_vigente',      'dbo',     'Equipamentos',           'id_local');

DECLARE @id_passagens INT = (SELECT id FROM dash.catalogo_tabela WHERE nome_fisico = 'veiculo_tempo_real');
DECLARE @id_equipamentos INT = (SELECT id FROM dash.catalogo_tabela WHERE nome_fisico = 'local_vigente');

INSERT INTO dash.catalogo_coluna (id_tabela, nome_fisico, rotulo, tipo, papel_sugerido)
VALUES
    (@id_passagens, 'id',        'ID',             'inteiro',   NULL),
    (@id_passagens, 'data',      'Data',           'data_hora', 'tempo'),
    (@id_passagens, 'id_local',  'ID Equipamento', 'inteiro',   'dimensao'),
    (@id_passagens, 'placa',     'Placa',          'texto',     'dimensao'),
    (@id_passagens, 'velocidade','Velocidade',      'numerico',  'medida'),
    (@id_equipamentos, 'id_local','ID',            'inteiro',   NULL),
    (@id_equipamentos, 'nome',   'Nome',           'texto',     'dimensao'),
    (@id_equipamentos, 'posicao_lat','Latitude',    'geo_lat',   'geo'),
    (@id_equipamentos, 'posicao_lon','Longitude',   'geo_lng',   'geo');

INSERT INTO dash.catalogo_relacao (id_tabela_origem, id_tabela_destino, cardinalidade, coluna_origem, coluna_destino)
VALUES
    (@id_passagens, @id_equipamentos, 'many_to_one', 'id_local', 'id_local');

-- Seed: dataset publicado
INSERT INTO dash.dataset (nome, id_tabela_base, id_usuario, tenant_id, visibilidade, status, versao)
VALUES ('Passagens por Equipamento', @id_passagens, 1, 1, 'publico', 'publicado', 1);

DECLARE @id_dataset INT = SCOPE_IDENTITY();

INSERT INTO dash.dataset_campo (id_dataset, id_coluna, nome_alias, rotulo, agregacao, tipo_campo, ordem)
VALUES
    (@id_dataset,
     (SELECT id FROM dash.catalogo_coluna WHERE id_tabela = @id_passagens AND nome_fisico = 'data'),
     'data', 'Data', 'NONE', 'tempo', 1),
    (@id_dataset,
     (SELECT id FROM dash.catalogo_coluna WHERE id_tabela = @id_equipamentos AND nome_fisico = 'nome'),
     'equipamento', 'Equipamento', 'NONE', 'dimensao', 2),
    (@id_dataset,
     (SELECT id FROM dash.catalogo_coluna WHERE id_tabela = @id_passagens AND nome_fisico = 'id'),
     'total_passagens', 'Total de Passagens', 'COUNT', 'medida', 3),
    (@id_dataset,
     (SELECT id FROM dash.catalogo_coluna WHERE id_tabela = @id_passagens AND nome_fisico = 'velocidade'),
     'velocidade_media', 'Velocidade Media', 'AVG', 'medida', 4);

-- Seed: dashboard com 1 widget de barras
INSERT INTO dash.dashboard (nome, descricao, id_usuario, tenant_id, refresh_seg)
VALUES ('Painel Operacional', 'Passagens por equipamento', 1, 1, 60);

DECLARE @id_painel INT = SCOPE_IDENTITY();

INSERT INTO dash.widget (id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h)
VALUES (
    @id_painel, @id_dataset,
    'Passagens por Equipamento (Hoje)',
    'barras',
    '{"measures":["total_passagens"],"dimensions":["equipamento"],"timeDimensions":[{"dimension":"data","granularity":"day","dateRange":"today"}],"filters":[],"order":{"total_passagens":"desc"},"limit":20}',
    '{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}',
    0, 0, 8, 4
);

PRINT 'DASH-1: Schema dash criado com sucesso. Seed inserido.';
GO

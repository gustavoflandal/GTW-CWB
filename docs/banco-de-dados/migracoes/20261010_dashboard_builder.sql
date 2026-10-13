-- ============================================================
-- Migracao: Dashboard Builder Self-Service
-- Data: 2026-10-10
-- Autor: gustavoflandal
-- Rollback:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu IN (376, 377);
--   DELETE FROM dbo.sis_menu_infos WHERE id_infos IN (43, 44);
--   DELETE FROM dbo.sis_menu WHERE id_menu IN (376, 377);
--   DROP VIEW  IF EXISTS muralha.v_dashboard_passagens_diarias;
--   DROP VIEW  IF EXISTS muralha.v_dashboard_alertas_resumo;
--   DROP TABLE IF EXISTS muralha.dashboard_widget;
--   DROP TABLE IF EXISTS muralha.dashboard_painel;
--   DROP TABLE IF EXISTS muralha.dashboard_dataset;
-- ============================================================

-- ============================================================
-- 0. Limpeza de estado parcial (execucao anterior com erros)
-- ============================================================
IF EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
           WHERE s.name = 'muralha' AND t.name = 'dashboard_widget')
    DROP TABLE muralha.dashboard_widget;

IF EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
           WHERE s.name = 'muralha' AND t.name = 'dashboard_painel')
    DROP TABLE muralha.dashboard_painel;

IF EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
           WHERE s.name = 'muralha' AND t.name = 'dashboard_dataset')
    DROP TABLE muralha.dashboard_dataset;

IF EXISTS (SELECT 1 FROM sys.views v JOIN sys.schemas s ON s.schema_id = v.schema_id
           WHERE s.name = 'muralha' AND v.name = 'v_passagens_diarias')
    DROP VIEW muralha.v_passagens_diarias;

IF EXISTS (SELECT 1 FROM sys.views v JOIN sys.schemas s ON s.schema_id = v.schema_id
           WHERE s.name = 'muralha' AND v.name = 'v_alertas_resumo')
    DROP VIEW muralha.v_alertas_resumo;

IF EXISTS (SELECT 1 FROM sys.views v JOIN sys.schemas s ON s.schema_id = v.schema_id
           WHERE s.name = 'muralha' AND v.name = 'v_disponibilidade_equipamentos')
    DROP VIEW muralha.v_disponibilidade_equipamentos;

PRINT 'Limpeza de estado parcial concluida';
GO

-- ============================================================
-- 1. Tabela de datasets (catalogo de views SQL)
-- ============================================================
CREATE TABLE muralha.dashboard_dataset (
    id            INT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_dashboard_dataset PRIMARY KEY,
    nome          VARCHAR(80)   NOT NULL,
    descricao     VARCHAR(200)  NULL,
    nome_view     VARCHAR(128)  NOT NULL,
    colunas_json  VARCHAR(MAX)  NOT NULL,
    ativo         BIT           NOT NULL
        CONSTRAINT DF_dd_ativo DEFAULT 1
);
PRINT 'Criada: muralha.dashboard_dataset';
GO

-- ============================================================
-- 2. Tabela de paineis
-- ============================================================
CREATE TABLE muralha.dashboard_painel (
    id               INT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_dashboard_painel PRIMARY KEY,
    nome             VARCHAR(80)   NOT NULL,
    descricao        VARCHAR(200)  NULL,
    id_usuario       INT           NOT NULL,
    refresh_segundos INT           NOT NULL
        CONSTRAINT DF_dp_refresh DEFAULT 60,
    publico          BIT           NOT NULL
        CONSTRAINT DF_dp_publico DEFAULT 0,
    criado_em        DATETIME      NOT NULL
        CONSTRAINT DF_dp_criado DEFAULT GETDATE(),
    atualizado_em    DATETIME      NULL,
    CONSTRAINT FK_dp_usuario FOREIGN KEY (id_usuario)
        REFERENCES dbo.sis_usuario(id_usuario)
);

CREATE INDEX IX_dp_usuario ON muralha.dashboard_painel (id_usuario);
PRINT 'Criada: muralha.dashboard_painel';
GO

-- ============================================================
-- 3. Tabela de widgets
-- ============================================================
CREATE TABLE muralha.dashboard_widget (
    id             INT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_dashboard_widget PRIMARY KEY,
    id_painel      INT           NOT NULL,
    tipo           VARCHAR(10)   NOT NULL,
    titulo         VARCHAR(80)   NOT NULL,
    id_dataset     INT           NOT NULL,
    coluna_valor   VARCHAR(60)   NULL,
    coluna_label   VARCHAR(60)   NULL,
    coluna_lat     VARCHAR(60)   NULL,
    coluna_lng     VARCHAR(60)   NULL,
    agregacao      VARCHAR(10)   NOT NULL
        CONSTRAINT DF_dw_agreg DEFAULT 'NONE',
    filtros_json   VARCHAR(MAX)  NULL,
    tipo_grafico   VARCHAR(15)   NULL,
    colunas_tabela VARCHAR(MAX)  NULL,
    threshold_ok   FLOAT         NULL,
    threshold_warn FLOAT         NULL,
    grid_col       INT           NOT NULL CONSTRAINT DF_dw_col    DEFAULT 1,
    grid_row       INT           NOT NULL CONSTRAINT DF_dw_row    DEFAULT 1,
    grid_width     INT           NOT NULL CONSTRAINT DF_dw_width  DEFAULT 4,
    grid_height    INT           NOT NULL CONSTRAINT DF_dw_height DEFAULT 2,
    ordem          INT           NOT NULL CONSTRAINT DF_dw_ordem  DEFAULT 0,
    CONSTRAINT FK_dw_painel  FOREIGN KEY (id_painel)
        REFERENCES muralha.dashboard_painel(id) ON DELETE CASCADE,
    CONSTRAINT FK_dw_dataset FOREIGN KEY (id_dataset)
        REFERENCES muralha.dashboard_dataset(id),
    CONSTRAINT CK_dw_tipo CHECK (tipo IN ('CARD','CHART','TABLE','MAP')),
    CONSTRAINT CK_dw_agreg CHECK (agregacao IN ('SUM','AVG','COUNT','MAX','MIN','NONE')),
    CONSTRAINT CK_dw_grafico CHECK (tipo_grafico IS NULL
        OR tipo_grafico IN ('bar','line','pie','doughnut'))
);

CREATE INDEX IX_dw_painel ON muralha.dashboard_widget (id_painel);
PRINT 'Criada: muralha.dashboard_widget';
GO

-- ============================================================
-- 4. Views de dados para dashboards
-- ============================================================

-- View: passagens diarias por equipamento
-- Baseada em muralha.veiculo_tempo_real (coluna data, tipo datetime)
-- JOIN com local_pista_vigente para obter nome do equipamento
EXEC('
CREATE VIEW muralha.v_dashboard_passagens_diarias AS
SELECT
    CAST(vtr.data AS DATE)   AS data,
    vtr.id_local,
    RTRIM(lpv.nome)          AS equipamento,
    lpv.posicao_lat          AS latitude,
    lpv.posicao_lon          AS longitude,
    COUNT(*)                 AS total_passagens
FROM muralha.veiculo_tempo_real vtr (NOLOCK)
INNER JOIN (
    SELECT id_local, MIN(id_pista) AS id_pista
    FROM local_pista_vigente (NOLOCK)
    GROUP BY id_local
) pista_ref ON pista_ref.id_local = vtr.id_local
INNER JOIN local_pista_vigente lpv (NOLOCK)
    ON lpv.id_local = pista_ref.id_local
    AND lpv.id_pista = pista_ref.id_pista
GROUP BY CAST(vtr.data AS DATE), vtr.id_local, RTRIM(lpv.nome), lpv.posicao_lat, lpv.posicao_lon
');
PRINT 'Criada: muralha.v_dashboard_passagens_diarias';
GO

-- View: alertas agrupados por tipo e status
-- Baseada em muralha.alerta (coluna data, tipo datetime)
-- JOIN com tipo_alerta_ocorrencia e status_alerta para descricoes
EXEC('
CREATE VIEW muralha.v_dashboard_alertas_resumo AS
SELECT
    CAST(a.data AS DATE)      AS data,
    tao.tipo                  AS tipo_alerta,
    sa.descricao              AS status_alerta,
    COUNT(*)                  AS total_alertas
FROM muralha.alerta a (NOLOCK)
INNER JOIN muralha.tipo_alerta_ocorrencia tao (NOLOCK)
    ON tao.id = a.id_tipo_alerta_ocorrencia
INNER JOIN muralha.status_alerta sa (NOLOCK)
    ON sa.id = a.id_status_alerta
GROUP BY CAST(a.data AS DATE), tao.tipo, sa.descricao
');
PRINT 'Criada: muralha.v_dashboard_alertas_resumo';
GO

-- ============================================================
-- 5. Datasets iniciais
-- ============================================================
INSERT INTO muralha.dashboard_dataset (nome, descricao, nome_view, colunas_json) VALUES (
    'Passagens Diarias',
    'Total de passagens por dia e equipamento',
    'muralha.v_dashboard_passagens_diarias',
    '[{"nome":"data","tipo":"data","label":"Data"},{"nome":"id_local","tipo":"numerico","label":"ID Local"},{"nome":"equipamento","tipo":"texto","label":"Equipamento"},{"nome":"latitude","tipo":"geo","label":"Latitude"},{"nome":"longitude","tipo":"geo","label":"Longitude"},{"nome":"total_passagens","tipo":"numerico","label":"Total Passagens"}]'
);
PRINT 'Inserido dataset: Passagens Diarias';

INSERT INTO muralha.dashboard_dataset (nome, descricao, nome_view, colunas_json) VALUES (
    'Alertas Resumo',
    'Alertas agrupados por tipo, status e dia',
    'muralha.v_dashboard_alertas_resumo',
    '[{"nome":"data","tipo":"data","label":"Data"},{"nome":"tipo_alerta","tipo":"texto","label":"Tipo Alerta"},{"nome":"status_alerta","tipo":"texto","label":"Status"},{"nome":"total_alertas","tipo":"numerico","label":"Total Alertas"}]'
);
PRINT 'Inserido dataset: Alertas Resumo';

INSERT INTO muralha.dashboard_dataset (nome, descricao, nome_view, colunas_json) VALUES (
    'Veiculos Tempo Real',
    'Veiculos capturados em tempo real com localizacao',
    'muralha.v_veiculo_tempo_real',
    '[{"nome":"placa","tipo":"texto","label":"Placa"},{"nome":"data","tipo":"data","label":"Data"},{"nome":"nome","tipo":"texto","label":"Equipamento"},{"nome":"latitude","tipo":"geo","label":"Latitude"},{"nome":"longitude","tipo":"geo","label":"Longitude"},{"nome":"velocidade","tipo":"numerico","label":"Velocidade"},{"nome":"classificacao","tipo":"texto","label":"Classificacao"}]'
);
PRINT 'Inserido dataset: Veiculos Tempo Real';
GO

-- ============================================================
-- 6. Menus: Dashboard Builder + Admin Datasets
--    Seguindo padrao da migracao 20261009:
--    id_pai_menu = 222 (raiz Muralha-Digital)
--    id_menu sequencial a partir de 376
--    id_infos sequencial a partir de 43
-- ============================================================

-- sis_menu
INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema) VALUES
(376, 'Dashboard Builder',   '/muralha-digital/pages/dashboard/builder/index.jsp',  'U', 1, 222, 'Dashboard Builder',  1, 'Muralha-Digital'),
(377, 'Admin Datasets',      '/muralha-digital/pages/admin/datasets/index.jsp',     'U', 1, 222, 'Admin Datasets',     1, 'Muralha-Digital');
PRINT 'Inseridos menus: Dashboard Builder (376), Admin Datasets (377)';

-- sis_menu_infos
-- Dashboard Builder em Dashboards (menu_pai = 1)
-- Admin Datasets em Cadastro/Configuracao (menu_pai = 16)
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai) VALUES
(43, 376, '/muralha-digital/assets/images/dashboard/dahsboard.jpg',             'Dashboard Builder',  '/muralha-digital/pages/dashboard/builder/index.jsp',  'Dashboards dinamicos com widgets configuraveis',  3, 1),
(44, 377, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg', 'Admin Datasets',     '/muralha-digital/pages/admin/datasets/index.jsp',     'Cadastro de datasets para Dashboard Builder',      9, 16);
PRINT 'Inseridos menu_infos: 43, 44';

-- sis_menu_direitos (id_menu_direito e IDENTITY)
-- Grupo 11 = Administradores, 41 = Anel de Seguranca, 42 = Supervisores Anel
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES
(41, NULL, 376), (42, NULL, 376), (11, NULL, 376),
(41, NULL, 377), (42, NULL, 377), (11, NULL, 377);
PRINT 'Inseridos direitos de menu';
GO

-- ============================================================
-- Verificacao
-- ============================================================
SELECT 'dashboard_dataset' AS objeto, COUNT(*) AS registros FROM muralha.dashboard_dataset
UNION ALL
SELECT 'dashboard_painel', COUNT(*) FROM muralha.dashboard_painel
UNION ALL
SELECT 'dashboard_widget', COUNT(*) FROM muralha.dashboard_widget
UNION ALL
SELECT 'menus', COUNT(*) FROM dbo.sis_menu WHERE id_menu IN (376, 377)
UNION ALL
SELECT 'menu_infos', COUNT(*) FROM dbo.sis_menu_infos WHERE id_infos IN (43, 44)
UNION ALL
SELECT 'menu_direitos', COUNT(*) FROM dbo.sis_menu_direitos WHERE id_menu IN (376, 377);

SELECT v.name AS view_name FROM sys.views v
JOIN sys.schemas s ON s.schema_id = v.schema_id
WHERE s.name = 'muralha' AND v.name LIKE 'v_dashboard_%';

PRINT '=== Migracao Dashboard Builder concluida ===';

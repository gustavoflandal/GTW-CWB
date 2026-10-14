/*
 * Dados de demonstracao — Dashboard Builder (Muralha Digital)
 *
 * Popula APENAS tabelas do schema dash.* (criadas pela implementacao).
 * NAO insere nem altera dados em tabelas existentes (dbo, muralha, ia).
 *
 * Pre-requisitos:
 *   - Tabelas dash.* criadas (migracoes 20261013_dash_*.sql)
 *   - Tabelas dbo.sis_usuario, dbo.local, muralha.veiculo_tempo_real,
 *     muralha.alerta, dbo.infracao ja existem com dados
 *
 * Revisao 2026-10-14 (Plano 21, sem Cube):
 *   - nome_alias e o nome simples do campo no dataset (sem "tabela.");
 *   - contagens apontam para a chave primaria cadastrada no catalogo;
 *   - catalogo reaproveita tabelas/colunas ja cadastradas (ex.: veiculo_tempo_real) e so
 *     insere o que falta;
 *   - limpeza idempotente apenas pelos ids fixos da demonstracao
 *     (datasets 101-103, paineis 101-102, widgets 1001-1016).
 *
 * Rollback: executar apenas o bloco "0. Limpeza" abaixo. As tabelas/colunas/relacoes de catalogo
 * inseridas por este script (dbo.local, muralha.alerta, dbo.infracao e colunas extras de
 * veiculo_tempo_real) podem ser mantidas; nao afetam datasets existentes.
 *
 * Execucao:
 *   sqlcmd -S <servidor> -d GTW_MURALHA_DEV -U <usuario> -P <senha> -b -I -f 65001 -i 20261014_dados_demonstracao_dashboards.sql
 */

SET NOCOUNT ON;
SET XACT_ABORT ON;
BEGIN TRANSACTION;
BEGIN TRY

-- =====================================================================
-- 0. Usuario administrador e limpeza da demonstracao anterior
-- =====================================================================
DECLARE @id_usuario INT;
SELECT TOP 1 @id_usuario = id_usuario
  FROM dbo.sis_usuario
 WHERE usuario LIKE '%administrador%' AND ativo = 1;

IF @id_usuario IS NULL
    SELECT TOP 1 @id_usuario = id_usuario FROM dbo.sis_usuario WHERE ativo = 1;

IF @id_usuario IS NULL
    THROW 50001, 'Nenhum usuario ativo encontrado em dbo.sis_usuario', 1;

PRINT 'Usuario: ' + CAST(@id_usuario AS VARCHAR(10));

DELETE FROM dash.widget              WHERE id_dashboard IN (101, 102) OR id BETWEEN 1001 AND 1016;
DELETE FROM dash.compartilhamento    WHERE (tipo_recurso = 'dashboard' AND id_recurso IN (101, 102))
                                        OR (tipo_recurso = 'dataset'   AND id_recurso IN (101, 102, 103));
DELETE FROM dash.historico_versao    WHERE (tipo_recurso = 'dashboard' AND id_recurso IN (101, 102))
                                        OR (tipo_recurso = 'dataset'   AND id_recurso IN (101, 102, 103));
DELETE FROM dash.dashboard           WHERE id IN (101, 102);
DELETE FROM dash.consulta_log        WHERE dataset_id IN (101, 102, 103);
DELETE FROM dash.dataset_estatistica WHERE dataset_id IN (101, 102, 103);
DELETE FROM dash.dataset_campo       WHERE id_dataset IN (101, 102, 103);
DELETE FROM dash.dataset_tabela      WHERE id_dataset IN (101, 102, 103);
DELETE FROM dash.dataset_relacao     WHERE id_dataset IN (101, 102, 103);
DELETE FROM dash.dataset_filtro_fixo WHERE id_dataset IN (101, 102, 103);
DELETE FROM dash.dataset_modelador   WHERE id_dataset IN (101, 102, 103);
DELETE FROM dash.dataset             WHERE id IN (101, 102, 103);

-- =====================================================================
-- 1. CATALOGO DE TABELAS (insere so as que faltam)
-- =====================================================================
INSERT INTO dash.catalogo_tabela (nome_fisico, schema_fisico, rotulo, descricao, chave_primaria, sensivel)
SELECT v.nome_fisico, v.schema_fisico, v.rotulo, v.descricao, v.chave_primaria, 0
FROM (VALUES
    ('veiculo_tempo_real', 'muralha', N'Passagens de Veiculos',
     N'Registros de passagem captados pelos equipamentos em tempo real. 8+ milhoes de registros.', 'id'),
    ('local', 'dbo', N'Equipamentos / Locais',
     N'Cadastro de equipamentos de monitoramento com posicao GPS. ~470 registros.', 'id_local'),
    ('alerta', 'muralha', N'Alertas',
     N'Alertas gerados pelo sistema de monitoramento de veiculos. ~11 mil registros.', 'id'),
    ('infracao', 'dbo', N'Infracoes',
     N'Registros de infracoes de transito detectadas automaticamente. ~73 mil registros.', 'id_infracao')
) AS v (nome_fisico, schema_fisico, rotulo, descricao, chave_primaria)
WHERE NOT EXISTS (SELECT 1 FROM dash.catalogo_tabela ct
                  WHERE ct.schema_fisico = v.schema_fisico AND ct.nome_fisico = v.nome_fisico);

DECLARE @t_vtr INT    = (SELECT id FROM dash.catalogo_tabela WHERE schema_fisico = 'muralha' AND nome_fisico = 'veiculo_tempo_real');
DECLARE @t_local INT  = (SELECT id FROM dash.catalogo_tabela WHERE schema_fisico = 'dbo'     AND nome_fisico = 'local');
DECLARE @t_alerta INT = (SELECT id FROM dash.catalogo_tabela WHERE schema_fisico = 'muralha' AND nome_fisico = 'alerta');
DECLARE @t_infr INT   = (SELECT id FROM dash.catalogo_tabela WHERE schema_fisico = 'dbo'     AND nome_fisico = 'infracao');

UPDATE dash.catalogo_tabela SET ativo = 1 WHERE id IN (@t_vtr, @t_local, @t_alerta, @t_infr) AND ativo = 0;

PRINT 'Catalogo de tabelas: vtr=' + CAST(@t_vtr AS VARCHAR) + ' local=' + CAST(@t_local AS VARCHAR)
    + ' alerta=' + CAST(@t_alerta AS VARCHAR) + ' infracao=' + CAST(@t_infr AS VARCHAR);

-- =====================================================================
-- 2. CATALOGO DE COLUNAS (insere so as que faltam)
-- =====================================================================
INSERT INTO dash.catalogo_coluna (id_tabela, nome_fisico, rotulo, tipo, sensivel, papel_sugerido)
SELECT v.id_tabela, v.nome_fisico, v.rotulo, v.tipo, v.sensivel, v.papel
FROM (VALUES
    -- veiculo_tempo_real
    (@t_vtr,    'id',                        N'ID',                 'texto',     0, NULL),
    (@t_vtr,    'placa',                     N'Placa',              'texto',     1, 'dimensao'),
    (@t_vtr,    'data',                      N'Data/Hora',          'data_hora', 0, 'tempo'),
    (@t_vtr,    'id_local',                  N'ID Local',           'inteiro',   0, 'dimensao'),
    (@t_vtr,    'velocidade',                N'Velocidade',         'inteiro',   0, 'medida'),
    (@t_vtr,    'classificacao',             N'Classificacao',      'texto',     0, 'dimensao'),
    (@t_vtr,    'id_pista',                  N'Faixa',              'inteiro',   0, 'dimensao'),
    (@t_vtr,    'estado_veiculo',            N'Estado',             'inteiro',   0, 'dimensao'),
    (@t_vtr,    'categoria',                 N'Categoria',          'inteiro',   0, 'dimensao'),
    (@t_vtr,    'placa_mercosul',            N'Placa Mercosul',     'booleano',  0, 'dimensao'),
    -- local
    (@t_local,  'id_local',                  N'ID Local',           'inteiro',   0, 'dimensao'),
    (@t_local,  'nome',                      N'Nome do Local',      'texto',     0, 'dimensao'),
    (@t_local,  'posicao_lat',               N'Latitude',           'numerico',  0, 'geo'),
    (@t_local,  'posicao_lon',               N'Longitude',          'numerico',  0, 'geo'),
    -- alerta
    (@t_alerta, 'id',                        N'ID',                 'texto',     0, NULL),
    (@t_alerta, 'data',                      N'Data do Alerta',     'data_hora', 0, 'tempo'),
    (@t_alerta, 'id_tipo_alerta_ocorrencia', N'Tipo de Alerta',     'texto',     0, 'dimensao'),
    (@t_alerta, 'enviado_cliente',           N'Enviado ao Cliente', 'booleano',  0, 'dimensao'),
    (@t_alerta, 'assinado',                  N'Assinado',           'booleano',  0, 'dimensao'),
    -- infracao
    (@t_infr,   'id_infracao',               N'ID',                 'inteiro',   0, NULL),
    (@t_infr,   'placa',                     N'Placa',              'texto',     1, 'dimensao'),
    (@t_infr,   'data',                      N'Data da Infracao',   'data_hora', 0, 'tempo'),
    (@t_infr,   'id_local',                  N'ID Local',           'inteiro',   0, 'dimensao'),
    (@t_infr,   'velocidade_considerada',    N'Vel. Considerada',   'numerico',  0, 'medida'),
    (@t_infr,   'velocidade_limite',         N'Vel. Limite',        'numerico',  0, 'medida'),
    (@t_infr,   'id_enquadramento',          N'Enquadramento',      'inteiro',   0, 'dimensao')
) AS v (id_tabela, nome_fisico, rotulo, tipo, sensivel, papel)
WHERE NOT EXISTS (SELECT 1 FROM dash.catalogo_coluna cc
                  WHERE cc.id_tabela = v.id_tabela AND cc.nome_fisico = v.nome_fisico);

PRINT 'Catalogo de colunas: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' inseridas';

-- =====================================================================
-- 3. RELACOES ENTRE TABELAS (insere so as que faltam)
-- =====================================================================
INSERT INTO dash.catalogo_relacao (id_tabela_origem, id_tabela_destino, cardinalidade, coluna_origem, coluna_destino)
SELECT v.o, v.d, 'many_to_one', 'id_local', 'id_local'
FROM (VALUES (@t_vtr, @t_local), (@t_infr, @t_local)) AS v (o, d)
WHERE NOT EXISTS (SELECT 1 FROM dash.catalogo_relacao cr
                  WHERE cr.id_tabela_origem = v.o AND cr.id_tabela_destino = v.d
                    AND cr.coluna_origem = 'id_local' AND cr.coluna_destino = 'id_local');

PRINT 'Relacoes de catalogo: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' inseridas';

-- =====================================================================
-- 4. DATASETS
-- =====================================================================
SET IDENTITY_INSERT dash.dataset ON;

INSERT INTO dash.dataset (id, nome, descricao, id_tabela_base, id_usuario, tenant_id, visibilidade, status, versao)
VALUES
    (101, N'Operacao Veicular', N'Passagens de veiculos com dados de local/equipamento', @t_vtr, @id_usuario, 1, 'publico', 'publicado', 1),
    (102, N'Monitoramento de Alertas', N'Alertas gerados pelo sistema de monitoramento', @t_alerta, @id_usuario, 1, 'publico', 'publicado', 1),
    (103, N'Controle de Infracoes', N'Infracoes detectadas com dados do local', @t_infr, @id_usuario, 1, 'publico', 'publicado', 1);

SET IDENTITY_INSERT dash.dataset OFF;
PRINT 'Datasets: 3 inseridos';

-- =====================================================================
-- 5. DATASET TABELAS E RELACOES
-- =====================================================================
INSERT INTO dash.dataset_tabela (id_dataset, id_tabela, alias, tipo, ordem)
VALUES
    (101, @t_vtr,    'veiculo_tempo_real', 'base', 0),
    (101, @t_local,  'local',              'join', 1),
    (102, @t_alerta, 'alerta',             'base', 0),
    (103, @t_infr,   'infracao',           'base', 0),
    (103, @t_local,  'local',              'join', 1);

INSERT INTO dash.dataset_relacao (id_dataset, id_tabela_origem, id_tabela_destino, coluna_origem, coluna_destino, cardinalidade, origem)
VALUES
    (101, @t_vtr,  @t_local, 'id_local', 'id_local', 'many_to_one', 'catalogo'),
    (103, @t_infr, @t_local, 'id_local', 'id_local', 'many_to_one', 'catalogo');

PRINT 'Dataset tabelas: 5, relacoes: 2';

-- =====================================================================
-- 6. DATASET CAMPOS
--    nome_alias = nome do campo no dataset (unico, sem "tabela."), usado nas consultas dos widgets
-- =====================================================================
INSERT INTO dash.dataset_campo (id_dataset, id_coluna, nome_alias, rotulo, agregacao, tipo_campo, ordem)
SELECT v.id_dataset, cc.id, v.alias, v.rotulo, v.agregacao, v.tipo_campo, v.ordem
FROM (VALUES
    -- DS 101: Operacao Veicular
    (101, @t_vtr,    'classificacao',             'classificacao',           N'Classificacao',      'NONE',  'dimensao', 0),
    (101, @t_vtr,    'id_local',                  'id_local',                N'ID Equipamento',     'NONE',  'dimensao', 1),
    (101, @t_vtr,    'id_pista',                  'faixa',                   N'Faixa',              'NONE',  'dimensao', 2),
    (101, @t_local,  'nome',                      'equipamento',             N'Equipamento',        'NONE',  'dimensao', 3),
    (101, @t_vtr,    'categoria',                 'categoria',               N'Categoria',          'NONE',  'dimensao', 4),
    (101, @t_vtr,    'data',                      'data',                    N'Data/Hora',          'NONE',  'tempo',    5),
    (101, @t_vtr,    'id',                        'total_passagens',         N'Total Passagens',    'COUNT', 'medida',   6),
    (101, @t_vtr,    'velocidade',                'velocidade_media',        N'Velocidade Media',   'AVG',   'medida',   7),
    (101, @t_vtr,    'velocidade',                'velocidade_total',        N'Velocidade Total',   'SUM',   'medida',   8),
    -- DS 102: Monitoramento de Alertas
    (102, @t_alerta, 'id_tipo_alerta_ocorrencia', 'tipo_alerta',             N'Tipo de Alerta',     'NONE',  'dimensao', 0),
    (102, @t_alerta, 'enviado_cliente',           'enviado_cliente',         N'Enviado ao Cliente', 'NONE',  'dimensao', 1),
    (102, @t_alerta, 'assinado',                  'assinado',                N'Assinado',           'NONE',  'dimensao', 2),
    (102, @t_alerta, 'data',                      'data',                    N'Data do Alerta',     'NONE',  'tempo',    3),
    (102, @t_alerta, 'id',                        'total_alertas',           N'Total de Alertas',   'COUNT', 'medida',   4),
    -- DS 103: Controle de Infracoes
    (103, @t_infr,   'id_enquadramento',          'enquadramento',           N'Enquadramento',      'NONE',  'dimensao', 0),
    (103, @t_infr,   'id_local',                  'id_local',                N'ID Local',           'NONE',  'dimensao', 1),
    (103, @t_local,  'nome',                      'equipamento',             N'Equipamento',        'NONE',  'dimensao', 2),
    (103, @t_infr,   'data',                      'data',                    N'Data da Infracao',   'NONE',  'tempo',    3),
    (103, @t_infr,   'id_infracao',               'total_infracoes',         N'Total Infracoes',    'COUNT', 'medida',   4),
    (103, @t_infr,   'velocidade_considerada',    'velocidade_media',        N'Vel. Media',         'AVG',   'medida',   5),
    (103, @t_infr,   'velocidade_limite',         'velocidade_limite_media', N'Vel. Limite Media',  'AVG',   'medida',   6)
) AS v (id_dataset, id_tabela, coluna, alias, rotulo, agregacao, tipo_campo, ordem)
JOIN dash.catalogo_coluna cc ON cc.id_tabela = v.id_tabela AND cc.nome_fisico = v.coluna;

IF @@ROWCOUNT <> 21
    THROW 50002, 'Esperados 21 campos de dataset; alguma coluna nao foi encontrada no catalogo', 1;

PRINT 'Dataset campos: 21 inseridos';

-- =====================================================================
-- 7. DASHBOARDS
-- =====================================================================
SET IDENTITY_INSERT dash.dashboard ON;

INSERT INTO dash.dashboard (id, nome, descricao, id_usuario, tenant_id, refresh_seg, versao)
VALUES
    (101, N'Painel Operacional — Muralha Digital',
     'Visao geral das passagens de veiculos, velocidades e classificacoes por equipamento.',
     @id_usuario, 1, 60, 1),
    (102, 'Painel de Seguranca',
     'Monitoramento de alertas e infracoes com indicadores de conformidade.',
     @id_usuario, 1, 60, 1);

SET IDENTITY_INSERT dash.dashboard OFF;
PRINT 'Dashboards: 2 inseridos';

-- =====================================================================
-- 8. WIDGETS — Dashboard 101 (Painel Operacional)
--
-- Layout 12 colunas:
--   Row 0: [KPI 3c][KPI 3c][KPI 3c][KPI 3c]       (altura 2)
--   Row 2: [Barras 6c]      [Pizza 6c]             (altura 4)
--   Row 6: [Linhas 12c]                            (altura 4)
--   Row 10: [Tabela 12c]                           (altura 5)
-- =====================================================================
SET IDENTITY_INSERT dash.widget ON;

-- KPI 1: Total de Passagens
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1001, 101, 101, 'Total de Passagens', 'kpi',
    '{"measures":["total_passagens"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"inteiro","subtitulo":"Passagens registradas","icone":"bi-car-front","thresholds":[{"valor":0,"cor":"#dc3545","rotulo":"Baixo"},{"valor":1000,"cor":"#ffc107","rotulo":"Normal"},{"valor":10000,"cor":"#28a745","rotulo":"Alto"}]}',
    0, 0, 3, 2, 0);

-- KPI 2: Velocidade Media
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1002, 101, 101, 'Velocidade Media', 'kpi',
    '{"measures":["velocidade_media"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"decimal","subtitulo":"km/h media geral","icone":"bi-speedometer2","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"Baixa"},{"valor":40,"cor":"#ffc107","rotulo":"Moderada"},{"valor":80,"cor":"#dc3545","rotulo":"Alta"}]}',
    3, 0, 3, 2, 1);

-- KPI 3: Total de Alertas
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1003, 101, 102, 'Total de Alertas', 'kpi',
    '{"measures":["total_alertas"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"inteiro","subtitulo":"Alertas no periodo","icone":"bi-bell","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":100,"cor":"#ffc107","rotulo":"Atencao"},{"valor":500,"cor":"#dc3545","rotulo":"Critico"}]}',
    6, 0, 3, 2, 2);

-- KPI 4: Total de Infracoes
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1004, 101, 103, 'Total de Infracoes', 'kpi',
    '{"measures":["total_infracoes"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"inteiro","subtitulo":"Infracoes detectadas","icone":"bi-exclamation-triangle","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":100,"cor":"#ffc107","rotulo":"Moderado"},{"valor":1000,"cor":"#dc3545","rotulo":"Alto"}]}',
    9, 0, 3, 2, 3);

-- Grafico de Barras: Passagens por Equipamento
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1005, 101, 101, 'Passagens por Equipamento', 'barras',
    '{"measures":["total_passagens"],"dimensions":["equipamento"],"filters":[],"order":{},"limit":15}',
    '{"empilhado":false,"rotulos":true,"paleta":"azul","orientacao":"horizontal"}',
    0, 2, 6, 4, 4);

-- Grafico Pizza: Distribuicao por Classificacao
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1006, 101, 101, 'Distribuicao por Classificacao', 'pizza',
    '{"measures":["total_passagens"],"dimensions":["classificacao"],"filters":[],"order":{},"limit":10}',
    '{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}',
    6, 2, 6, 4, 5);

-- Grafico de Linhas: Passagens por Periodo
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1007, 101, 101, 'Passagens por Periodo', 'linhas',
    '{"measures":["total_passagens"],"dimensions":[],"timeDimensions":[{"dimension":"data","granularity":"month","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":100}',
    '{"empilhado":false,"rotulos":false,"paleta":"azul","orientacao":"vertical"}',
    0, 6, 12, 4, 6);

-- Tabela: Detalhe por Equipamento
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1008, 101, 101, 'Detalhe por Equipamento', 'tabela',
    '{"measures":["total_passagens","velocidade_media"],"dimensions":["equipamento","classificacao"],"filters":[],"order":{},"limit":50}',
    '{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}',
    0, 10, 12, 5, 7);

-- =====================================================================
-- 9. WIDGETS — Dashboard 102 (Painel de Seguranca)
--
-- Layout 12 colunas:
--   Row 0: [KPI 4c][KPI 4c][KPI 4c]                (altura 2)
--   Row 2: [Barras 6c]      [Rosca 6c]             (altura 4)
--   Row 6: [Linhas 12c]                            (altura 4)
--   Row 10: [Area 6c]       [Tabela 6c]            (altura 5)
-- =====================================================================

-- KPI 1: Total de Infracoes
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1009, 102, 103, 'Total de Infracoes', 'kpi',
    '{"measures":["total_infracoes"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"inteiro","subtitulo":"Infracoes no periodo","icone":"bi-shield-exclamation","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":500,"cor":"#ffc107","rotulo":"Atencao"},{"valor":2000,"cor":"#dc3545","rotulo":"Critico"}]}',
    0, 0, 4, 2, 0);

-- KPI 2: Velocidade Media de Infracoes
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1010, 102, 103, 'Vel. Media Infracoes', 'kpi',
    '{"measures":["velocidade_media"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"decimal","subtitulo":"km/h acima do limite","icone":"bi-speedometer","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"Baixa"},{"valor":30,"cor":"#ffc107","rotulo":"Moderada"},{"valor":60,"cor":"#dc3545","rotulo":"Grave"}]}',
    4, 0, 4, 2, 1);

-- KPI 3: Total de Alertas
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1011, 102, 102, 'Alertas Ativos', 'kpi',
    '{"measures":["total_alertas"],"dimensions":[],"filters":[],"order":{},"limit":1}',
    '{"formato":"inteiro","subtitulo":"Alertas no periodo","icone":"bi-bell-fill","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":50,"cor":"#ffc107","rotulo":"Atencao"},{"valor":200,"cor":"#dc3545","rotulo":"Critico"}]}',
    8, 0, 4, 2, 2);

-- Grafico de Barras: Infracoes por Equipamento
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1012, 102, 103, 'Infracoes por Equipamento', 'barras',
    '{"measures":["total_infracoes"],"dimensions":["equipamento"],"filters":[],"order":{},"limit":15}',
    '{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"horizontal"}',
    0, 2, 6, 4, 3);

-- Grafico Rosca: Alertas por Status
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1013, 102, 102, 'Alertas por Tipo', 'rosca',
    '{"measures":["total_alertas"],"dimensions":["tipo_alerta"],"filters":[],"order":{},"limit":10}',
    '{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}',
    6, 2, 6, 4, 4);

-- Grafico de Linhas: Infracoes por Periodo
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1014, 102, 103, 'Infracoes por Periodo', 'linhas',
    '{"measures":["total_infracoes"],"dimensions":[],"timeDimensions":[{"dimension":"data","granularity":"month","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":100}',
    '{"empilhado":false,"rotulos":false,"paleta":"verde","orientacao":"vertical"}',
    0, 6, 12, 4, 5);

-- Grafico de Area: Alertas por Periodo
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1015, 102, 102, 'Alertas por Periodo', 'area',
    '{"measures":["total_alertas"],"dimensions":[],"timeDimensions":[{"dimension":"data","granularity":"month","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":100}',
    '{"empilhado":false,"rotulos":false,"paleta":"azul","orientacao":"vertical"}',
    0, 10, 6, 5, 6);

-- Tabela: Detalhe de Infracoes por Local
INSERT INTO dash.widget (id, id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, grid_x, grid_y, grid_w, grid_h, ordem)
VALUES (1016, 102, 103, 'Detalhe de Infracoes por Local', 'tabela',
    '{"measures":["total_infracoes","velocidade_media","velocidade_limite_media"],"dimensions":["equipamento"],"filters":[],"order":{},"limit":50}',
    '{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}',
    6, 10, 6, 5, 7);

SET IDENTITY_INSERT dash.widget OFF;
PRINT 'Widgets: 16 inseridos (8 + 8)';

-- =====================================================================
-- 10. ESTATISTICAS (simular uso dos datasets)
-- =====================================================================
INSERT INTO dash.dataset_estatistica (dataset_id, total_consultas, tempo_medio_ms, cache_hit_count, consultas_lentas, ultima_consulta)
VALUES
    (101, 247, 1850, 180, 12, DATEADD(MINUTE, -5, SYSDATETIME())),
    (102, 89,  920,  72,  3,  DATEADD(MINUTE, -8, SYSDATETIME())),
    (103, 156, 1420, 110, 8,  DATEADD(MINUTE, -3, SYSDATETIME()));

PRINT 'Estatisticas: 3 inseridas';

-- =====================================================================
-- 11. LOGS DE CONSULTA (simular historico de uso)
-- =====================================================================
INSERT INTO dash.consulta_log (usuario_id, dataset_id, widget_id, painel_id, tempo_ms, linhas_retornadas, cache_hit, lenta, criado_em)
VALUES
    (@id_usuario, 101, 1001, 101, 120,  1,    1, 0, DATEADD(HOUR, -2, SYSDATETIME())),
    (@id_usuario, 101, 1005, 101, 2340, 15,   0, 1, DATEADD(HOUR, -2, SYSDATETIME())),
    (@id_usuario, 101, 1006, 101, 890,  6,    1, 0, DATEADD(HOUR, -2, SYSDATETIME())),
    (@id_usuario, 101, 1007, 101, 3100, 12,   0, 1, DATEADD(HOUR, -1, SYSDATETIME())),
    (@id_usuario, 101, 1008, 101, 1560, 50,   0, 0, DATEADD(HOUR, -1, SYSDATETIME())),
    (@id_usuario, 102, 1003, 101, 450,  1,    1, 0, DATEADD(MINUTE, -30, SYSDATETIME())),
    (@id_usuario, 103, 1004, 101, 680,  1,    1, 0, DATEADD(MINUTE, -30, SYSDATETIME())),
    (@id_usuario, 103, 1009, 102, 520,  1,    1, 0, DATEADD(MINUTE, -15, SYSDATETIME())),
    (@id_usuario, 103, 1012, 102, 2780, 15,   0, 1, DATEADD(MINUTE, -15, SYSDATETIME())),
    (@id_usuario, 102, 1013, 102, 1100, 10,   0, 0, DATEADD(MINUTE, -10, SYSDATETIME())),
    (@id_usuario, 103, 1014, 102, 1890, 12,   0, 0, DATEADD(MINUTE, -10, SYSDATETIME())),
    (@id_usuario, 103, 1016, 102, 1340, 30,   0, 0, DATEADD(MINUTE, -5,  SYSDATETIME()));

PRINT 'Logs de consulta: 12 inseridos';

-- =====================================================================
-- 12. HISTORICO DE VERSAO (snapshot inicial de cada dashboard)
-- =====================================================================
INSERT INTO dash.historico_versao (tipo_recurso, id_recurso, versao, snapshot_json, id_autor)
VALUES
    ('dashboard', 101, 1,
     N'{"nome":"Painel Operacional — Muralha Digital","widgets":8,"refresh_seg":60}',
     @id_usuario),
    ('dashboard', 102, 1,
     '{"nome":"Painel de Seguranca","widgets":8,"refresh_seg":60}',
     @id_usuario);

PRINT 'Historico de versao: 2 inseridos';

-- =====================================================================
COMMIT TRANSACTION;
PRINT '';
PRINT '============================================';
PRINT ' Dados de demonstracao inseridos com sucesso';
PRINT '============================================';
PRINT ' Catalogo: 4 tabelas (reaproveitadas quando ja existiam), relacoes N:1 com dbo.local';
PRINT ' Datasets: 3 (publicados, publicos)';
PRINT ' Dashboards: 2';
PRINT '   101 - Painel Operacional (8 widgets)';
PRINT '   102 - Painel de Seguranca (8 widgets)';
PRINT ' Logs e estatisticas simulados';
PRINT '';
PRINT ' Acesse: http://localhost:8080/';
PRINT '   Menu > Paineis de Analise';
PRINT '============================================';

END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    PRINT 'ERRO: ' + ERROR_MESSAGE();
    THROW;
END CATCH

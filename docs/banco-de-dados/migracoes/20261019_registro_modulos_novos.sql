-- =============================================================================
-- Migracao: registro e atribuicao dos modulos novos (SEG-02, SEG-07, SEG-11)
-- Data: 2026-10-19 — idempotente. Executar depois de
-- 20261019_analise_permissao_abrir_superset.sql.
--
-- Origem: docs/referencia/guia-correcoes-analise-kpis-lotes.md (§7).
--
-- Contexto: dbo.fcn_VerificaAcesso e fail-open — URL sem linha em
-- sis_menu/sis_menu_direitos fica aberta a qualquer usuario logado.
-- Este script:
--   1) registra (sem card) as telas e os servlets novos que estavam sem
--      registro, com os grupos decididos em 2026-10-19:
--        - telas administrativas (Retencao, CPF/Matricula, Builder e
--          Datasets da versao anterior): grupo 11;
--        - Exportacao de Passagens e Consolidacao Multifonte: 11 e 42;
--        - demais telas operacionais (Dupla Analise, Passagens Anonimizadas,
--          editores dos Paineis de Analise, servlets das telas de Analise de
--          Dados, Lotes e KPIs): 11, 41 e 42;
--        - JSPs abertas pelos servlets 392-394: mesmos grupos do servlet.
--   2) concede o grupo 11 (Administradores) as telas novas 366-375 e 384-395;
--   3) retira o grupo 41 da Configuracao de KPIs (372);
--   4) corrige a acao do menu 395 para a URI sem query
--      (/MuralhaDigital/SsoAnalise); o card (sis_menu_infos.href) continua
--      com ?acao=abrir.
-- Fora do escopo: /MuralhaDigital/MapaConfig (usado por varias telas de
-- mapa legadas), MfaConfig e /api/dash/health (todos os usuarios),
-- servlets do Sistema de Chamados (controle proprio por perfil).
-- A permissao por ACAO (ex.: so 372 grava KPI) exige codigo (SEG-03).
--
-- ROLLBACK:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu BETWEEN 397 AND 438;
--   DELETE FROM dbo.sis_menu WHERE id_menu BETWEEN 397 AND 438;
--   DELETE FROM dbo.sis_menu_direitos WHERE id_grupo = 11 AND id_usuario IS NULL
--     AND id_menu IN (366,367,368,369,370,371,372,373,374,375,384,385,386,387,
--                     388,389,390,391,392,393,394);
--   INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES (41, NULL, 372);
--   UPDATE dbo.sis_menu SET acao = '/MuralhaDigital/SsoAnalise?acao=abrir' WHERE id_menu = 395;
--   (o direito do grupo 11 no 395 vem do script 20261019_analise_permissao_abrir_superset.sql)
-- =============================================================================

SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;   -- exigido pelos metodos XML usados para separar os grupos
SET XACT_ABORT ON;          -- qualquer erro desfaz a transacao inteira

DECLARE @menus TABLE (id_menu INT PRIMARY KEY, descricao VARCHAR(100), acao VARCHAR(250), menu VARCHAR(100), grupos VARCHAR(50));
INSERT INTO @menus (id_menu, descricao, acao, menu, grupos) VALUES
 -- JSPs encaminhadas pelos servlets 392-394
 (397, 'Tipos de Alerta - tela',              '/muralha-digital/pages/tipos-alerta/index.jsp',                   'Tipos de Alerta',           '11,42'),
 (398, 'Relatorios Gerenciais - tela',        '/muralha-digital/pages/relatorios/gerencial/index.jsp',           'Relatorios Gerenciais',     '11,42'),
 (399, 'Pre-Processamento - tela',            '/muralha-digital/pages/monitoramento/pre-processamento/index.jsp','Pre-Processamento',         '11,42'),
 -- Telas administrativas
 (400, 'Retencao de Dados',                   '/muralha-digital/pages/admin/retencao/index.jsp',                 'Retencao de Dados',         '11'),
 (401, 'Retencao de Dados - servico',         '/MuralhaDigital/Retencao',                                        'Retencao - servico',        '11'),
 (402, 'CPF e Matricula',                     '/muralha-digital/pages/admin/cpf-matricula/index.jsp',            'CPF e Matricula',           '11'),
 (403, 'CPF e Matricula - servico',           '/MuralhaDigital/UsuarioCpfMatricula',                             'CPF e Matricula - servico', '11'),
 (404, 'Cadastro de Datasets (anterior)',     '/muralha-digital/pages/admin/datasets/index.jsp',                 'Datasets (anterior)',       '11'),
 (405, 'Dashboard Builder (anterior)',        '/muralha-digital/pages/dashboard/builder/index.jsp',              'Dashboard Builder',         '11'),
 (406, 'Dashboard Builder - servico',         '/MuralhaDigital/DashboardBuilder',                                'Builder - servico',         '11'),
 (407, 'Datasets (anterior) - servico',       '/MuralhaDigital/Dataset',                                         'Datasets - servico',        '11'),
 -- Dados em massa
 (408, 'Exportacao de Passagens',             '/muralha-digital/pages/relatorios/exportacao-passagens/index.jsp','Exportacao de Passagens',   '11,42'),
 (409, 'Exportacao de Passagens - servico',   '/MuralhaDigital/Exportacao',                                      'Exportacao - servico',      '11,42'),
 (410, 'Consolidacao Multifonte',             '/muralha-digital/pages/inteligencia/consolidacao/index.jsp',      'Consolidacao Multifonte',   '11,42'),
 (411, 'Consolidacao Multifonte - servico',   '/MuralhaDigital/Consolidacao',                                    'Consolidacao - servico',    '11,42'),
 -- Telas operacionais
 (412, 'Passagens Anonimizadas',              '/muralha-digital/pages/consulta/passagens-anonimizadas/index.jsp','Passagens Anonimizadas',    '11,41,42'),
 (413, 'Passagens Anonimizadas - servico',    '/MuralhaDigital/ConsultaAnonimizada',                             'Anonimizadas - servico',    '11,41,42'),
 (414, 'Dupla Analise - fila',                '/muralha-digital/pages/processamento/dupla-analise/fila.jsp',     'Dupla Analise',             '11,41,42'),
 (415, 'Dupla Analise - analisar',            '/muralha-digital/pages/processamento/dupla-analise/analisar.jsp', 'Dupla Analise - analisar',  '11,41,42'),
 (416, 'Dupla Analise - servico',             '/MuralhaDigital/InfracaoAnalise',                                 'Dupla Analise - servico',   '11,41,42'),
 (417, 'Obliteracao de imagens - servico',    '/MuralhaDigital/Obliteracao',                                     'Obliteracao - servico',     '11,41,42'),
 -- Paineis de Analise (subtelas e API)
 (418, 'Paineis de Analise - painel',         '/muralha-digital/pages/analises/paineis/index.jsp',               'Painel de Analise',         '11,41,42'),
 (419, 'Paineis de Analise - editor de widget','/muralha-digital/pages/analises/widgets/editor.jsp',             'Editor de Widget',          '11,41,42'),
 (420, 'Catalogo de Datasets - editor',       '/muralha-digital/pages/analises/datasets/editor.jsp',             'Editor de Dataset',         '11,41,42'),
 (421, 'Catalogo de Datasets - modelador',    '/muralha-digital/pages/analises/datasets/modelador.jsp',          'Modelador de Dataset',      '11,41,42'),
 (422, 'Paineis de Analise - API catalogo',   '/api/dash/catalogo',                                              'API dash catalogo',         '11,41,42'),
 (423, 'Paineis de Analise - API consultas',  '/api/dash/consultas',                                             'API dash consultas',        '11,41,42'),
 (424, 'Paineis de Analise - API paineis',    '/api/dash/paineis',                                               'API dash paineis',          '11,41,42'),
 (425, 'Paineis de Analise - API datasets',   '/api/dash/datasets',                                              'API dash datasets',         '11,41,42'),
 (426, 'Paineis de Analise - API widgets',    '/api/dash/widgets',                                               'API dash widgets',          '11,41,42'),
 (427, 'Paineis de Analise - API exportar',   '/api/dash/exportar',                                              'API dash exportar',         '11,41,42'),
 -- Servlets das telas de Analise de Dados, Lotes e KPIs
 (428, 'Gestao de Lotes - servico',           '/MuralhaDigital/Lote',                                            'Lotes - servico',           '11,41,42'),
 (429, 'KPIs - servico',                      '/MuralhaDigital/Kpi',                                             'KPIs - servico',            '11,41,42'),
 (430, 'Assertividade - servico',             '/MuralhaDigital/Assertividade',                                   'Assertividade - servico',   '11,41,42'),
 (431, 'Log de Auditoria - servico',          '/MuralhaDigital/Auditoria',                                       'Auditoria - servico',       '11,41,42'),
 (432, 'Disponibilidade - servico',           '/MuralhaDigital/Disponibilidade',                                 'Disponibilidade - servico', '11,41,42'),
 (433, 'SLA de Latencia - servico',           '/MuralhaDigital/SlaLatencia',                                     'SLA Latencia - servico',    '11,41,42'),
 (434, 'SLA de Pre-processamento - servico',  '/MuralhaDigital/SlaPreprocessamento',                             'SLA Pre-proc - servico',    '11,41,42'),
 (435, 'Time-lapse - servico',                '/MuralhaDigital/Timelapse',                                       'Time-lapse - servico',      '11,41,42'),
 (436, 'Mapa de Incidentes - servico GIS',    '/MuralhaDigital/Gis',                                             'GIS - servico',             '11,41,42'),
 (437, 'Mapa de Incidentes - servico',        '/MuralhaDigital/Incidente',                                       'Incidentes - servico',      '11,41,42'),
 (438, 'Analises - guest token',              '/MuralhaDigital/AnaliseGuestToken',                               'Guest token - servico',     '11,41,42');

-- 0) Conflitos: id ja usado por outra acao, ou acao ja registrada em outro id
IF EXISTS (SELECT 1 FROM dbo.sis_menu s JOIN @menus m ON m.id_menu = s.id_menu WHERE RTRIM(s.acao) <> m.acao)
   OR EXISTS (SELECT 1 FROM dbo.sis_menu s JOIN @menus m ON RTRIM(s.acao) = m.acao WHERE s.id_menu <> m.id_menu)
BEGIN
    SELECT s.id_menu, RTRIM(s.acao) AS acao_atual, m.id_menu AS id_previsto, m.acao AS acao_prevista
    FROM dbo.sis_menu s JOIN @menus m ON m.id_menu = s.id_menu OR RTRIM(s.acao) = m.acao
    WHERE RTRIM(s.acao) <> m.acao OR s.id_menu <> m.id_menu;
    RAISERROR('Conflito de id_menu/acao com registros existentes. Nada foi alterado.', 16, 1);
    RETURN;
END

BEGIN TRANSACTION;

-- 1) Funcionalidades sem card
INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
SELECT m.id_menu, m.descricao, m.acao, 'U', 1, 222, m.menu, 1, 'Muralha-Digital'
FROM @menus m
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu s WHERE s.id_menu = m.id_menu);
PRINT CONCAT('Funcionalidades incluidas: ', @@ROWCOUNT);

DECLARE @direitos TABLE (id_menu INT, id_grupo INT);
INSERT INTO @direitos (id_menu, id_grupo)
SELECT m.id_menu, CAST(g.value AS INT)
FROM @menus m
CROSS APPLY (
    SELECT LTRIM(RTRIM(x.i.value('.', 'varchar(10)'))) AS value
    FROM (SELECT CAST('<g>' + REPLACE(m.grupos, ',', '</g><g>') + '</g>' AS XML) AS xm) t
    CROSS APPLY t.xm.nodes('/g') AS x(i)
) g;

-- 2) Grupo 11 nas telas novas ja registradas
INSERT INTO @direitos (id_menu, id_grupo)
SELECT v.id_menu, 11
FROM (VALUES (366),(367),(368),(369),(370),(371),(372),(373),(374),(375),
             (384),(385),(386),(387),(388),(389),(390),(391),(392),(393),(394),(395)) v(id_menu);

INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT d.id_grupo, NULL, d.id_menu
FROM @direitos d
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos x
                  WHERE x.id_menu = d.id_menu AND x.id_grupo = d.id_grupo AND x.id_usuario IS NULL);
PRINT CONCAT('Direitos incluidos: ', @@ROWCOUNT);

-- 3) Configuracao de KPIs (SQL livre) sem o grupo 41
DELETE FROM dbo.sis_menu_direitos WHERE id_menu = 372 AND id_grupo = 41 AND id_usuario IS NULL;
PRINT CONCAT('Direito do grupo 41 retirado do 372: ', @@ROWCOUNT);

-- 4) Menu 395 com a URI sem query (o card continua com ?acao=abrir)
UPDATE dbo.sis_menu SET acao = '/MuralhaDigital/SsoAnalise'
WHERE id_menu = 395 AND RTRIM(acao) = '/MuralhaDigital/SsoAnalise?acao=abrir';
PRINT CONCAT('Acao do 395 corrigida: ', @@ROWCOUNT);

COMMIT TRANSACTION;

-- Conferencia
SELECT m.id_menu, RTRIM(m.descricao) AS descricao, RTRIM(m.acao) AS acao,
       STUFF((SELECT ',' + CAST(d.id_grupo AS VARCHAR) FROM dbo.sis_menu_direitos d
              WHERE d.id_menu = m.id_menu ORDER BY d.id_grupo FOR XML PATH('')), 1, 1, '') AS grupos
FROM dbo.sis_menu m
WHERE m.id_menu BETWEEN 366 AND 438
ORDER BY m.id_menu;

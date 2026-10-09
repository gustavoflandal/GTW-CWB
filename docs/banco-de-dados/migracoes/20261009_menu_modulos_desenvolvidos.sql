-- =============================================================================
-- Migracao: Inserir entradas de menu para modulos desenvolvidos (Planos 01-19)
-- Data: 2026-10-09
-- Descricao: Adiciona 10 modulos que possuem tela JSP mas nao tinham entrada
--            em sis_menu / sis_menu_infos / sis_menu_direitos.
-- =============================================================================

BEGIN TRANSACTION;

-- ---------------------------------------------------------------------------
-- 1) sis_menu  (id_pai_menu = 222 = raiz Muralha-Digital)
-- ---------------------------------------------------------------------------
INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema) VALUES
(366, 'Log de Auditoria',                '/muralha-digital/pages/auditoria/consulta-log.jsp',                      'U', 1, 222, 'Log de Auditoria',       1, 'Muralha-Digital'),
(367, 'SLA de Latencia',                 '/muralha-digital/pages/monitoramento/sla-latencia/index.jsp',             'U', 1, 222, 'SLA de Latencia',        1, 'Muralha-Digital'),
(368, 'Painel de Assertividade',         '/muralha-digital/pages/monitoramento/assertividade/index.jsp',            'U', 1, 222, 'Painel Assertividade',   1, 'Muralha-Digital'),
(369, 'SLA de Pre-processamento 72h',    '/muralha-digital/pages/monitoramento/sla-preproc/index.jsp',              'U', 1, 222, 'SLA Pre-processamento',  1, 'Muralha-Digital'),
(370, 'Gestao de Lotes',                 '/muralha-digital/pages/processamento/lotes/index.jsp',                    'U', 1, 222, 'Gestao de Lotes',        1, 'Muralha-Digital'),
(371, 'Dashboard KPIs',                  '/muralha-digital/pages/dashboard/kpis/index.jsp',                         'U', 1, 222, 'Dashboard KPIs',         1, 'Muralha-Digital'),
(372, 'Configuracao de KPIs',            '/muralha-digital/pages/admin/kpis/index.jsp',                             'U', 1, 222, 'Configuracao KPIs',      1, 'Muralha-Digital'),
(373, 'Time-lapse de Passagens',         '/muralha-digital/pages/monitoramento/timelapse/index.jsp',                'U', 1, 222, 'Time-lapse',             1, 'Muralha-Digital'),
(374, 'Disponibilidade dos Equipamentos','/muralha-digital/pages/monitoramento/disponibilidade/index.jsp',          'U', 1, 222, 'Disponibilidade',        1, 'Muralha-Digital'),
(375, 'Mapa de Incidentes Externos',     '/muralha-digital/pages/monitoramento/mapa/index.jsp',                     'U', 1, 222, 'Mapa de Incidentes',     1, 'Muralha-Digital');

-- ---------------------------------------------------------------------------
-- 2) sis_menu_infos  (menu_pai referencia id_infos do pai no card-grid)
--
--    Agrupamento:
--      menu_pai = 15  -> Analise de Dados   (SLA, Assertividade, Disponibilidade, Timelapse, Auditoria)
--      menu_pai =  7  -> Mapas              (Mapa de Incidentes)
--      menu_pai =  1  -> Dashboards         (KPIs Dashboard)
--      menu_pai = 16  -> Cadastro e Config. (KPIs Config, Gestao de Lotes)
-- ---------------------------------------------------------------------------
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai) VALUES
-- Analise de Dados (menu_pai = 15)
(33, 367, '/muralha-digital/assets/images/analise/analise.jpg',                      'SLA de Latencia',        '/muralha-digital/pages/monitoramento/sla-latencia/index.jsp',        'Monitoramento SLA de Latencia',              1, 15),
(34, 368, '/muralha-digital/assets/images/analise/analise.jpg',                      'Assertividade',          '/muralha-digital/pages/monitoramento/assertividade/index.jsp',       'Painel de Assertividade',                    2, 15),
(35, 369, '/muralha-digital/assets/images/analise/analise.jpg',                      'SLA Pre-processamento',  '/muralha-digital/pages/monitoramento/sla-preproc/index.jsp',         'SLA de Pre-processamento 72h',               3, 15),
(36, 373, '/muralha-digital/assets/images/tempo_real/real_time_menu.jpg',            'Time-lapse',             '/muralha-digital/pages/monitoramento/timelapse/index.jsp',           'Time-lapse de Passagens',                    4, 15),
(37, 374, '/muralha-digital/assets/images/mapa-equipamento/mapa-equipamento.jpg',   'Disponibilidade',        '/muralha-digital/pages/monitoramento/disponibilidade/index.jsp',     'Disponibilidade dos Equipamentos',           5, 15),
(38, 366, '/muralha-digital/assets/images/analise/analise.jpg',                      'Log de Auditoria',       '/muralha-digital/pages/auditoria/consulta-log.jsp',                  'Log de Auditoria Centralizado',              6, 15),
-- Mapas (menu_pai = 7)
(39, 375, '/muralha-digital/assets/images/mapa-calor/mapa-calor.jpg',               'Mapa de Incidentes',     '/muralha-digital/pages/monitoramento/mapa/index.jsp',               'Mapa de Incidentes Externos',                5, 7),
-- Dashboards (menu_pai = 1)
(40, 371, '/muralha-digital/assets/images/dashboard/dahsboard.jpg',                  'KPIs',                   '/muralha-digital/pages/dashboard/kpis/index.jsp',                   'Indicadores de Performance (KPIs)',           2, 1),
-- Cadastro e Configuracao (menu_pai = 16)
(41, 372, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg',     'Configuracao KPIs',      '/muralha-digital/pages/admin/kpis/index.jsp',                       'Configuracao de KPIs',                        7, 16),
(42, 370, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg',     'Gestao de Lotes',        '/muralha-digital/pages/processamento/lotes/index.jsp',              'Gestao de Lotes de Processamento',            8, 16);

-- ---------------------------------------------------------------------------
-- 3) sis_menu_direitos  (permissoes para grupos Muralha-Digital)
--    id_menu_direito e IDENTITY — basta informar id_grupo + id_menu
--    Grupos: 41 = Anel de Seguranca, 42 = Supervisores Anel
-- ---------------------------------------------------------------------------
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES
-- Grupo 41 (Anel de Seguranca)
(41, NULL, 366), (41, NULL, 367), (41, NULL, 368), (41, NULL, 369), (41, NULL, 370),
(41, NULL, 371), (41, NULL, 372), (41, NULL, 373), (41, NULL, 374), (41, NULL, 375),
-- Grupo 42 (Supervisores Anel de Seguranca)
(42, NULL, 366), (42, NULL, 367), (42, NULL, 368), (42, NULL, 369), (42, NULL, 370),
(42, NULL, 371), (42, NULL, 372), (42, NULL, 373), (42, NULL, 374), (42, NULL, 375);

COMMIT;

-- Verificacao
SELECT 'sis_menu' AS tabela, COUNT(*) AS inseridos FROM dbo.sis_menu WHERE id_menu BETWEEN 366 AND 375
UNION ALL
SELECT 'sis_menu_infos', COUNT(*) FROM dbo.sis_menu_infos WHERE id_infos BETWEEN 33 AND 42
UNION ALL
SELECT 'sis_menu_direitos', COUNT(*) FROM dbo.sis_menu_direitos WHERE id_menu BETWEEN 366 AND 375;

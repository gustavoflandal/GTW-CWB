-- ============================================================
-- Migracao: Atualizar menus do Dashboard Builder para telas novas
-- Data: 2026-10-13
-- Autor: gustavoflandal
-- Descricao: As telas do Dashboard Builder evoluiram de
--   dashboard/builder/ e admin/datasets/ (DASH-1) para
--   analises/paineis/ e analises/datasets/ (DASH-2 a DASH-6).
--   Esta migracao atualiza os URLs dos menus 376 e 377 e
--   respectivos sis_menu_infos 43 e 44.
-- Rollback:
--   UPDATE dbo.sis_menu SET acao = '/muralha-digital/pages/dashboard/builder/index.jsp' WHERE id_menu = 376;
--   UPDATE dbo.sis_menu SET acao = '/muralha-digital/pages/admin/datasets/index.jsp'    WHERE id_menu = 377;
--   UPDATE dbo.sis_menu_infos SET href = '/muralha-digital/pages/dashboard/builder/index.jsp' WHERE id_infos = 43;
--   UPDATE dbo.sis_menu_infos SET href = '/muralha-digital/pages/admin/datasets/index.jsp'    WHERE id_infos = 44;
-- ============================================================

BEGIN TRANSACTION;

-- 1) Atualizar sis_menu — acao (URL do menu)
UPDATE dbo.sis_menu
SET acao = '/muralha-digital/pages/analises/paineis/lista.jsp',
    descricao = 'Paineis de Analise'
WHERE id_menu = 376;
PRINT 'Menu 376 atualizado -> analises/paineis/lista.jsp';

UPDATE dbo.sis_menu
SET acao = '/muralha-digital/pages/analises/datasets/index.jsp',
    descricao = 'Catalogo de Datasets'
WHERE id_menu = 377;
PRINT 'Menu 377 atualizado -> analises/datasets/index.jsp';

-- 2) Atualizar sis_menu_infos — href e descricao
UPDATE dbo.sis_menu_infos
SET href = '/muralha-digital/pages/analises/paineis/lista.jsp',
    descricao = 'Paineis de Analise',
    descricao_detalhada = 'Paineis interativos com widgets, filtros e exportacao',
    menu_pai = NULL,
    ordenacao = 8
WHERE id_infos = 43;
PRINT 'Menu info 43 atualizado -> analises/paineis/lista.jsp (nivel raiz)';

UPDATE dbo.sis_menu_infos
SET href = '/muralha-digital/pages/analises/datasets/index.jsp',
    descricao = 'Catalogo de Datasets',
    descricao_detalhada = 'Catalogo de tabelas e campos para dashboards (Cube)'
WHERE id_infos = 44;
PRINT 'Menu info 44 atualizado -> analises/datasets/index.jsp';

COMMIT;

-- Verificacao
SELECT 'sis_menu' AS tabela, id_menu, descricao, acao
FROM dbo.sis_menu
WHERE id_menu IN (376, 377);

SELECT 'sis_menu_infos' AS tabela, id_infos, descricao, href
FROM dbo.sis_menu_infos
WHERE id_infos IN (43, 44);

PRINT '=== Menus do Dashboard Builder atualizados ===';

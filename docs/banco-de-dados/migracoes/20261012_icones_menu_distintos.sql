-- =============================================================================
-- Migracao: Icones de menu distintos para modulos com imagem duplicada
-- Data: 2026-10-12
-- Descricao: Atualiza sis_menu_infos.src para que cada modulo tenha um icone
--            SVG unico e representativo da sua funcao, substituindo imagens
--            genericas compartilhadas (cadastro-basico2.jpg, analise.jpg,
--            dahsboard.jpg).
-- Rollback:
--   UPDATE dbo.sis_menu_infos SET src='/muralha-digital/assets/images/analise/analise.jpg'                  WHERE id_infos IN (33,34,35,38);
--   UPDATE dbo.sis_menu_infos SET src='/muralha-digital/assets/images/dashboard/dahsboard.jpg'               WHERE id_infos IN (40,43);
--   UPDATE dbo.sis_menu_infos SET src='/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg'  WHERE id_infos IN (41,42,44);
-- =============================================================================

BEGIN TRANSACTION;

-- ---------------------------------------------------------------------------
-- Analise de Dados (menu_pai = 15) — 4 itens compartilhavam analise.jpg
-- ---------------------------------------------------------------------------
UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/sla-latencia.svg'
 WHERE id_infos = 33;  -- SLA de Latencia

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/assertividade.svg'
 WHERE id_infos = 34;  -- Assertividade

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/sla-preproc.svg'
 WHERE id_infos = 35;  -- SLA Pre-processamento 72h

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/auditoria.svg'
 WHERE id_infos = 38;  -- Log de Auditoria

-- ---------------------------------------------------------------------------
-- Dashboards (menu_pai = 1) — 2 itens compartilhavam dahsboard.jpg
-- ---------------------------------------------------------------------------
UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/dashboard-kpis.svg'
 WHERE id_infos = 40;  -- Dashboard KPIs

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/dashboard-builder.svg'
 WHERE id_infos = 43;  -- Dashboard Builder

-- ---------------------------------------------------------------------------
-- Cadastro e Configuracao (menu_pai = 16) — 3 itens compartilhavam cadastro-basico2.jpg
-- ---------------------------------------------------------------------------
UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/config-kpis.svg'
 WHERE id_infos = 41;  -- Configuracao de KPIs

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/gestao-lotes.svg'
 WHERE id_infos = 42;  -- Gestao de Lotes

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/admin-datasets.svg'
 WHERE id_infos = 44;  -- Admin Datasets

COMMIT;

-- Verificacao
SELECT id_infos, descricao, src
  FROM dbo.sis_menu_infos
 WHERE id_infos IN (33,34,35,38,40,41,42,43,44)
 ORDER BY id_infos;

-- =============================================================================
-- Migracao: menus/cards/direitos das telas administrativas do Motor de Logs
-- Data: 2026-11-05 — idempotente. Executar depois de
-- 20261105_motor_captura_logs.sql.
--
-- Registra (verificacao ESTRITA via fcn_VerificaAcesso):
--   441  tela   Captura de Logs - Regras      (/muralha-digital/pages/admin/logs-regras/index.jsp)
--   442  servlet Regras de Captura - servico  (/MuralhaDigital/RegraLog)
--   443  tela   Retencao e Expurgo de Logs    (/muralha-digital/pages/admin/logs-retencao/index.jsp)
--   444  servlet Retencao/Expurgo - servico   (/MuralhaDigital/RetencaoLog)
-- Cards (sis_menu_infos 58,59) sob menu_pai 16 (Cadastro e Configuracao).
-- Direitos: grupo 11 (Administradores) em todas as quatro entradas.
--
-- ROLLBACK:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu BETWEEN 441 AND 444;
--   DELETE FROM dbo.sis_menu_infos    WHERE id_infos IN (58,59);
--   DELETE FROM dbo.sis_menu          WHERE id_menu BETWEEN 441 AND 444;
-- =============================================================================

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

-- 1) Funcionalidades (sis_menu)
INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
SELECT v.id_menu, v.descricao, v.acao, 'U', 1, 222, v.menu, 1, 'Muralha-Digital'
FROM (VALUES
  (441, 'Captura de Logs - Regras',     '/muralha-digital/pages/admin/logs-regras/index.jsp',   'Captura de Logs - Regras'),
  (442, 'Regras de Captura - servico',  '/MuralhaDigital/RegraLog',                             'Regras de Captura - servico'),
  (443, 'Retencao e Expurgo de Logs',   '/muralha-digital/pages/admin/logs-retencao/index.jsp', 'Retencao e Expurgo de Logs'),
  (444, 'Retencao/Expurgo - servico',   '/MuralhaDigital/RetencaoLog',                          'Retencao/Expurgo - servico')
) v(id_menu, descricao, acao, menu)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu s WHERE s.id_menu = v.id_menu);
PRINT CONCAT('sis_menu incluidos: ', @@ROWCOUNT);

-- 2) Cards (sis_menu_infos) — menu_pai 16 = Cadastro e Configuracao
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
SELECT v.id_infos, v.id_menu, v.src, v.descricao, v.href, v.detalhe, v.ordem, 16
FROM (VALUES
  (58, 441, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg',
       'Captura de Logs', '/muralha-digital/pages/admin/logs-regras/index.jsp',
       'Regras de captura de log de operacao', 9),
  (59, 443, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg',
       'Retencao de Logs', '/muralha-digital/pages/admin/logs-retencao/index.jsp',
       'Politicas de retencao e expurgo de logs', 10)
) v(id_infos, id_menu, src, descricao, href, detalhe, ordem)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos i WHERE i.id_infos = v.id_infos);
PRINT CONCAT('sis_menu_infos incluidos: ', @@ROWCOUNT);

-- 3) Direitos — grupo 11 (Administradores)
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT 11, NULL, v.id_menu
FROM (VALUES (441),(442),(443),(444)) v(id_menu)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d
                  WHERE d.id_menu = v.id_menu AND d.id_grupo = 11 AND d.id_usuario IS NULL);
PRINT CONCAT('sis_menu_direitos incluidos: ', @@ROWCOUNT);

COMMIT TRANSACTION;

-- Conferencia
SELECT m.id_menu, RTRIM(m.descricao) AS descricao, RTRIM(m.acao) AS acao,
       STUFF((SELECT ',' + CAST(d.id_grupo AS VARCHAR) FROM dbo.sis_menu_direitos d
              WHERE d.id_menu = m.id_menu ORDER BY d.id_grupo FOR XML PATH('')), 1, 1, '') AS grupos
FROM dbo.sis_menu m
WHERE m.id_menu BETWEEN 441 AND 444
ORDER BY m.id_menu;

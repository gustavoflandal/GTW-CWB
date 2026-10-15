-- ============================================================
-- Migracao: Adicionar menu "Catalogo de Dados" (gerenciamento)
-- Data: 2026-10-13
-- Autor: gustavoflandal
-- Descricao: Novo item de menu para a tela de gerenciamento do
--   catalogo de tabelas, colunas e relacoes do Dashboard Builder.
--   Menu 378 / info 45, sob "Cadastro e Configuracao" (menu_pai=16).
-- Rollback:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu = 378;
--   DELETE FROM dbo.sis_menu_infos WHERE id_infos = 45;
--   DELETE FROM dbo.sis_menu WHERE id_menu = 378;
-- ============================================================

BEGIN TRANSACTION;

-- 1) sis_menu
INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
VALUES (378, 'Catalogo de Dados', '/muralha-digital/pages/analises/catalogo/index.jsp', 'U', 1, 222, 'Catalogo de Dados', 1, 'Muralha-Digital');
PRINT 'Inserido menu 378: Catalogo de Dados';

-- 2) sis_menu_infos (card na home)
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
VALUES (45, 378, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg',
        'Catalogo de Dados',
        '/muralha-digital/pages/analises/catalogo/index.jsp',
        'Gerenciamento de tabelas, colunas e relacoes para dashboards',
        10, 16);
PRINT 'Inserido menu info 45: Catalogo de Dados (sob Cadastro e Configuracao)';

-- 3) sis_menu_direitos (grupos administradores: 41, 42, 11)
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
VALUES
    (41, NULL, 378),
    (42, NULL, 378),
    (11, NULL, 378);
PRINT 'Inseridos direitos para grupos 41, 42, 11';

COMMIT;

-- Verificacao
SELECT 'sis_menu' AS tabela, id_menu, descricao, acao
FROM dbo.sis_menu WHERE id_menu = 378;

SELECT 'sis_menu_infos' AS tabela, id_infos, descricao, href, menu_pai
FROM dbo.sis_menu_infos WHERE id_infos = 45;

SELECT 'sis_menu_direitos' AS tabela, COUNT(*) AS total
FROM dbo.sis_menu_direitos WHERE id_menu = 378;

PRINT '=== Menu Catalogo de Dados criado com sucesso ===';

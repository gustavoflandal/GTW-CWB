-- ============================================================
-- Migração: corrigir ícone do card Superset (Análises)
-- Data: 2026-10-17
-- Descrição: O card do Superset foi criado com src='bi-graph-up'
--            (classe Bootstrap Icon), mas o JSP renderiza <img src="...">
--            e precisa de um caminho de imagem real.
--            Atualiza para o SVG dedicado.
--
-- Executar com sqlcmd -b -f 65001 e somente com autorização humana.
--
-- Rollback:
--   UPDATE dbo.sis_menu_infos SET src = 'bi-graph-up' WHERE id_infos = 56;
-- ============================================================

SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Corrigir icone do card
UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/analises.svg'
 WHERE id_infos = 56
   AND id_menu = 395;

IF @@ROWCOUNT > 0
    PRINT 'Icone do Superset atualizado para analises.svg';
ELSE
    PRINT 'AVISO: registro id_infos=56 / id_menu=395 nao encontrado — verificar';
GO

-- 2. Inserir permissoes (faltavam na migracao original)
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos WHERE id_menu = 395 AND id_grupo = 41)
    INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (395, 41);
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos WHERE id_menu = 395 AND id_grupo = 42)
    INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (395, 42);
PRINT 'Permissoes do Superset conferidas (grupos 41 e 42)';
GO

-- 3. URL base do Superset (lida pelo SsoAnaliseServlet)
MERGE muralha.config_chave_valor AS t
USING (SELECT 'SUPERSET_BASE_URL' AS chave, 'http://localhost:8088' AS valor) AS s
    ON t.chave = s.chave
WHEN NOT MATCHED THEN INSERT (chave, valor) VALUES (s.chave, s.valor);
PRINT 'Chave SUPERSET_BASE_URL conferida';
GO

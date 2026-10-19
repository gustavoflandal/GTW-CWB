-- =============================================================================
-- Migracao: permissao "Analises - Abrir no Superset"
-- Data: 2026-10-19 — idempotente. Executar depois de 20261016_analise_fundacao.sql.
--
-- O botao "Abrir no Superset" da tela de Analises (SsoAnaliseServlet ->
-- pages/analise/embedding.jsp) so aparece para quem tem direito a esta
-- funcionalidade. O servlet NEGA quando a funcionalidade nao esta cadastrada
-- (diferente do padrao fail-open de fcn_VerificaAcesso para telas).
-- Para conceder o poder a outros grupos ou usuarios, inclua linhas em
-- dbo.sis_menu_direitos para o id_menu 396 (por id_grupo ou por id_usuario).
--
-- Tambem concede ao grupo 11 (Administradores) o menu 395 (tela de Analises),
-- que so tinha direito para os grupos 41 e 42.
--
-- ROLLBACK:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu = 396;
--   DELETE FROM dbo.sis_menu WHERE id_menu = 396;
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu = 395 AND id_grupo = 11 AND id_usuario IS NULL;
-- =============================================================================

IF EXISTS (SELECT 1 FROM dbo.sis_menu WHERE id_menu = 396 AND acao <> '/MuralhaDigital/SsoAnalise/abrir-superset')
BEGIN
    RAISERROR('id_menu 396 ja usado por outra funcionalidade. Ajuste este script e SsoAnaliseServlet.ACAO_ABRIR_SUPERSET.', 16, 1);
    SET NOEXEC ON;
END
GO

-- 1) Funcionalidade (sem card: nao entra em sis_menu_infos)
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE id_menu = 396)
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (396, 'Analises - Abrir no Superset', '/MuralhaDigital/SsoAnalise/abrir-superset', 'U', 1, 222,
            'Abrir no Superset', 1, 'Muralha-Digital');
PRINT 'Funcionalidade 396 verificada';

-- 2) Direito: grupo 11 (Administradores)
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos WHERE id_menu = 396 AND id_grupo = 11 AND id_usuario IS NULL)
    INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES (11, NULL, 396);

-- 3) Tela de Analises (395) tambem para Administradores
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos WHERE id_menu = 395 AND id_grupo = 11 AND id_usuario IS NULL)
    INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu) VALUES (11, NULL, 395);
PRINT 'Direitos verificados';
GO

SET NOEXEC OFF;

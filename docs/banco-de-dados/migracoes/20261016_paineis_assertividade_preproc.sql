-- ============================================================
-- Migração: menu do painel de pré-processamento (Fase 8)
-- Data: 2026-10-16 (Plano 30, Fase 8 da correção da auditoria)
-- TR: 5.2.2.3, 5.2.2.6, 5.5.3.1
--
-- O que este script faz (idempotente):
--   1. Cria item de menu para Painel de Pré-Processamento
--
-- Não cria tabelas — usa dados das tabelas existentes.
-- ============================================================

SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- ============================================================
-- 1. Menu: Painel de Pré-Processamento (grupo Análise de Dados)
-- ============================================================

DECLARE @maxMenu INT = (SELECT ISNULL(MAX(id_menu), 0) FROM dbo.sis_menu);
DECLARE @maxInfos INT = (SELECT ISNULL(MAX(id_infos), 0) FROM dbo.sis_menu_infos);

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/PreProcessamento')
BEGIN
    SET @maxMenu = @maxMenu + 1;
    SET @maxInfos = @maxInfos + 1;

    INSERT INTO dbo.sis_menu (id_menu, Descricao, acao, Tipo, nivel, menu, Ativo)
    VALUES (@maxMenu, 'Painel de Pre-Processamento', '/MuralhaDigital/PreProcessamento', 'U', 1, 'Pre-Processamento', 1);

    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    VALUES (@maxInfos, @maxMenu,
            '/muralha-digital/assets/images/menu-icons/pre-processamento.svg',
            'Pre-Processamento',
            '/MuralhaDigital/PreProcessamento',
            'Painel operacional com volumes, SLA, produtividade e classificacao',
            10, 15);

    INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (@maxMenu, 42);

    PRINT 'Menu Pre-Processamento criado: menu=' + CAST(@maxMenu AS VARCHAR) + ' infos=' + CAST(@maxInfos AS VARCHAR);
END
ELSE
    PRINT 'Menu Pre-Processamento ja existe';

PRINT 'Migracao de paineis Fase 8 concluida.';
GO

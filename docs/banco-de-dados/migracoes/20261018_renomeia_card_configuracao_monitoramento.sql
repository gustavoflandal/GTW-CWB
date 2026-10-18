-- =============================================================================
-- Migração: 20261018_renomeia_card_configuracao_monitoramento.sql
-- Objetivo: completar o rótulo do card "Configuração" da categoria
--           Cadastro e Configuração (menu_pai 16) para "Configuração de Monitoramento",
--           deixando claro que a tela trata da configuração do monitoramento.
-- Escopo:   somente dbo.sis_menu_infos (id_infos 9 / id_menu 235). sis_menu e permissões
--           não são alterados.
-- Rollback:
--   UPDATE dbo.sis_menu_infos
--      SET descricao = N'Configuração', descricao_detalhada = N'Configuração'
--    WHERE href = N'/muralha-digital/pages/configuracao-monitoramento/configuracao.jsp'
--      AND menu_pai = 16;
-- =============================================================================
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
SET NOCOUNT ON;

UPDATE dbo.sis_menu_infos
   SET descricao = N'Configuração de Monitoramento',
       descricao_detalhada = N'Configuração de Monitoramento'
 WHERE href = N'/muralha-digital/pages/configuracao-monitoramento/configuracao.jsp'
   AND menu_pai = 16;

PRINT 'Cards renomeados: ' + CAST(@@ROWCOUNT AS varchar(10));

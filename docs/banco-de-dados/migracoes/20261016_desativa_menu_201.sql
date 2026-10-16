-- ============================================================
-- Migração: desativa o menu 201 "Relatório Alt. ConfigEquip" (GTW clássico)
-- Data: 2026-10-16 (decisão do dono do projeto; registrada no Plano 26, Fase 4)
--
-- Por quê: o menu (acao /relatorio/frm_alteracoes_configequip.jsp) não tem
-- nenhum grupo em dbo.sis_menu_direitos e o relatório nunca funcionou
-- (doGet vazio). O histórico de parâmetros com valor anterior e novo passa a
-- ser muralha.parametro_historico (Plano 26), consultado na tela Logs
-- Técnicos. O servlet RelatorioAlteracaoConfigEquip agora só avisa que o
-- relatório foi desativado.
--
-- Idempotente: só altera se o menu ainda estiver ativo e com a mesma ação.
-- Executar somente com autorização humana.
--
-- Rollback:
--   UPDATE dbo.sis_menu SET Ativo = 1
--   WHERE id_menu = 201 AND acao = '/relatorio/frm_alteracoes_configequip.jsp';
-- ============================================================

UPDATE dbo.sis_menu
SET Ativo = 0
WHERE id_menu = 201
  AND acao = '/relatorio/frm_alteracoes_configequip.jsp'
  AND Ativo = 1;

PRINT CASE WHEN @@ROWCOUNT = 1 THEN 'Menu 201 desativado' ELSE 'Menu 201 ja inativo ou diferente do esperado (nada alterado)' END;
GO

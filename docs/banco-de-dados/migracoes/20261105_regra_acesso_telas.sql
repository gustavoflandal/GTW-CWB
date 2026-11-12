-- =============================================================================
-- Migracao: regra de captura "Acesso a tela" (abertura de pagina .jsp)
-- Data: 2026-11-05 — idempotente. Depois de 20261105_motor_captura_logs.sql.
--
-- Requisito: registrar o ACESSO a qualquer tela, mesmo que nada seja feito nela.
-- A abertura de uma tela e um GET da pagina (.jsp). Esta regra classifica esse
-- GET como nivel ACESSO (em vez de LEITURA), destacando o acesso. O filtro ja
-- forca o no-store nas navegacoes HTML para que a reabertura sempre chegue ao
-- servidor (nao fique em cache do navegador).
--
-- Ordem 95: apos as regras de exclusao (10-25) e as sensiveis (login=50 etc.),
-- antes do catch-all (900). Nao conflita com os servlets (/MuralhaDigital/...),
-- que nao terminam em .jsp.
--
-- ROLLBACK: DELETE FROM dbo.sis_log_regra WHERE descricao = 'Acesso a tela (abertura de pagina)';
-- =============================================================================

SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM dbo.sis_log_regra WHERE descricao = 'Acesso a tela (abertura de pagina)')
BEGIN
    INSERT INTO dbo.sis_log_regra
        (ordem, descricao, padrao_rota, metodo, capturar, nivel, funcionalidade, registrar_parametros, mascarar_campos, garantido, ativo)
    VALUES
        (95, 'Acesso a tela (abertura de pagina)', '%.jsp', 'GET', 1, 'ACESSO', NULL, 0, NULL, 0, 1);
    PRINT 'Regra de acesso a telas inserida (ordem 95).';
END
ELSE
    PRINT 'Regra de acesso a telas ja existe.';

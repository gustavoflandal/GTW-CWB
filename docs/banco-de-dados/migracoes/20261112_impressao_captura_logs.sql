-- =============================================================================
-- Migracao: captura de impressao no Motor de Captura de Logs
-- Data: 2026-11-12 — idempotente. Depois de 20261105_tipos_evento_log.sql.
--
-- Cria o tipo de evento IMPRESSAO em dbo.sis_log_tipo_evento e a regra de
-- captura do endpoint /MuralhaDigital/Impressao (POST) em dbo.sis_log_regra.
--
-- ROLLBACK:
--   DELETE FROM dbo.sis_log_tipo_evento WHERE codigo = 'IMPRESSAO';
--   DELETE FROM dbo.sis_log_regra WHERE padrao_rota = '/MuralhaDigital/Impressao';
-- =============================================================================

SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM dbo.sis_log_tipo_evento WHERE codigo = 'IMPRESSAO')
BEGIN
    INSERT INTO dbo.sis_log_tipo_evento (codigo, descricao, capturar, ordem)
    VALUES ('IMPRESSAO', 'Impressao de documento ou de tela', 1, 65);
    PRINT 'Seed: tipo de evento IMPRESSAO inserido';
END
ELSE
    PRINT 'Seed ignorado: tipo de evento IMPRESSAO ja existe';
GO

IF NOT EXISTS (SELECT 1 FROM dbo.sis_log_regra WHERE padrao_rota = '/MuralhaDigital/Impressao')
BEGIN
    INSERT INTO dbo.sis_log_regra (ordem, descricao, padrao_rota, metodo, capturar, nivel, ativo)
    VALUES (50, 'Impressao de tela (navegador)', '/MuralhaDigital/Impressao', 'POST', 1, 'IMPRESSAO', 1);
    PRINT 'Seed: regra de captura de /MuralhaDigital/Impressao inserida';
END
ELSE
    PRINT 'Seed ignorado: regra de /MuralhaDigital/Impressao ja existe';
GO

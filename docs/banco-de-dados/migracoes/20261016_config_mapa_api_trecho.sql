-- ============================================================
-- Migração: chave mapa_api_monitoramento_trecho em muralha.config_chave_valor
-- Data: 2026-10-16 (Plano 24, Fase 2 da correção da auditoria)
--
-- Por quê: o endereço da API de situação do trânsito por trecho estava fixo
-- em src/main/webapp/muralha-digital/utils/maps-config.js. Agora a tela
-- "Mapa de situação do trânsito" lê a chave pelo servlet
-- /MuralhaDigital/MapaConfig?acao=obterApiMonitoramentoTrecho.
--
-- Valor: host[:porta], sem http:// nem caminho. O valor inicial é o que
-- estava no JavaScript; ajuste por ambiente. Não é segredo.
-- Idempotente: só insere se a chave não existir (não sobrescreve ajuste).
-- Executar somente com autorização humana.
--
-- Rollback:
--   DELETE FROM muralha.config_chave_valor WHERE chave = 'mapa_api_monitoramento_trecho';
-- ============================================================

IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'mapa_api_monitoramento_trecho')
BEGIN
    INSERT INTO muralha.config_chave_valor (chave, valor)
    VALUES ('mapa_api_monitoramento_trecho', '189.112.204.89:8003');
    PRINT 'Inserida: mapa_api_monitoramento_trecho';
END
ELSE
    PRINT 'Ja existe: mapa_api_monitoramento_trecho (valor mantido)';
GO

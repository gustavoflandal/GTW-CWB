-- =============================================================================
-- Complemento: Sistema de Gerenciamento de Chamados (Plano 22, Tarefa 19) — SMTP dos chamados
-- Data: 2026-10-15 — idempotente. Executar depois de 20261015_sistema_chamados_seed.sql.
--
-- Chaves inseridas VAZIAS: com chamado_smtp_host vazio os e-mails dos chamados continuam usando o
-- primeiro servidor SMTP do confGTW.xml. O administrador preenche pela tela
-- Chamados > Configuração > "E-mail (SMTP)".
-- chamado_smtp_senha guarda somente o texto cifrado ("v1:" + Base64, AES-GCM) com a chave da
-- variavel de ambiente GTW_CHAMADO_CHAVE_SMTP do servidor (docs/requisitos/07-configuracao-e-segredos.md).
-- chamado_smtp_starttls: '1' liga STARTTLS, '0' desliga (vazio = ligado).
--
-- ROLLBACK:
--   DELETE FROM muralha.config_chave_valor WHERE chave IN ('chamado_smtp_host','chamado_smtp_porta',
--     'chamado_smtp_usuario','chamado_smtp_senha','chamado_smtp_remetente','chamado_smtp_starttls');
-- =============================================================================

INSERT INTO muralha.config_chave_valor (chave, valor)
SELECT v.chave, '' FROM (VALUES ('chamado_smtp_host'), ('chamado_smtp_porta'), ('chamado_smtp_usuario'),
                                ('chamado_smtp_senha'), ('chamado_smtp_remetente'), ('chamado_smtp_starttls')) v(chave)
WHERE NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor c WHERE c.chave = v.chave);
PRINT 'Chaves do SMTP dos chamados verificadas';
GO

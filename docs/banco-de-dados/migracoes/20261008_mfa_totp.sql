-- *** OBSOLETO — substituído por 20261008_tabelas_complementares.sql ***
-- O ALTER TABLE abaixo viola a regra de não alterar tabelas legadas.
-- As colunas totp_secret e totp_habilitado ficam em dbo.sis_usuario_complemento.
-- A config mfa_obrigatorio em sis_senha_config permanece válida.
-- NÃO EXECUTAR o ALTER TABLE deste script.
-- ================================================================
-- Plano 04: MFA/TOTP (ORIGINAL)
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;

-- ALTER TABLE dbo.sis_usuario ADD ... — PROIBIDO, usar sis_usuario_complemento

INSERT INTO dbo.sis_senha_config (chave, valor, descricao)
VALUES ('mfa_obrigatorio', '0', '1=MFA obrigatório para todos os usuários');

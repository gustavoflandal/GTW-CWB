-- Plano 04: MFA/TOTP
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
-- ROLLBACK:
-- ALTER TABLE dbo.sis_usuario DROP COLUMN totp_secret, totp_habilitado;
-- DELETE FROM dbo.sis_senha_config WHERE chave = 'mfa_obrigatorio';

ALTER TABLE dbo.sis_usuario ADD
    totp_secret     VARCHAR(200) NULL,
    totp_habilitado BIT          NOT NULL DEFAULT 0;

INSERT INTO dbo.sis_senha_config (chave, valor, descricao)
VALUES ('mfa_obrigatorio', '0', '1=MFA obrigatório para todos os usuários');

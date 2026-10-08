-- Plano 03: Política de Senhas
-- ROLLBACK:
-- DROP TABLE dbo.sis_senha_config;
-- ALTER TABLE dbo.sis_usuario DROP COLUMN senha_hash, senha_historico, tentativas_invalidas,
--   bloqueado_ate, dt_ultima_troca_senha, fl_troca_obrigatoria;

CREATE TABLE dbo.sis_senha_config (
    chave     VARCHAR(50)  NOT NULL PRIMARY KEY,
    valor     VARCHAR(200) NOT NULL,
    descricao VARCHAR(300)
);

INSERT INTO dbo.sis_senha_config (chave, valor, descricao) VALUES
('min_tamanho',      '8',  'Tamanho mínimo da senha'),
('requer_maiuscula', '1',  '1=sim, 0=não'),
('requer_numero',    '1',  '1=sim, 0=não'),
('requer_especial',  '1',  '1=sim, 0=não — caracteres: !@#$%^&*()_+-='),
('validade_dias',    '90', 'Dias até expiração; 0=nunca expira'),
('historico_qtde',   '5',  'Quantas senhas anteriores não podem ser reutilizadas'),
('max_tentativas',   '5',  'Tentativas inválidas antes do bloqueio'),
('bloqueio_minutos', '30', 'Minutos de bloqueio; 0=bloqueio permanente (só admin desbloqueia)');

ALTER TABLE dbo.sis_usuario ADD
    senha_hash           VARCHAR(64)  NULL,
    senha_historico      VARCHAR(MAX) NULL,
    tentativas_invalidas TINYINT      NOT NULL DEFAULT 0,
    bloqueado_ate        DATETIME2    NULL,
    dt_ultima_troca_senha DATE        NULL,
    fl_troca_obrigatoria  BIT         NOT NULL DEFAULT 0;

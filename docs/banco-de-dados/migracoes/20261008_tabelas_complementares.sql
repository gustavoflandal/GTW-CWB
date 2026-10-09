-- *** OBSOLETO — substituído por 20261008_complementar.sql ***
-- As tabelas aqui (vtr_complemento, vtr_imagem_complemento) foram
-- redesenhadas como tabelas focadas (vtr_status_analise, vtr_imagem_obliterada).
-- Se já executou este script, rode:
--   DROP TABLE IF EXISTS muralha.vtr_complemento;
--   DROP TABLE IF EXISTS muralha.vtr_imagem_complemento;
-- ============================================================
-- Migração ORIGINAL: Tabelas complementares para o PoC TRANSALVADOR
-- Data: 2026-10-08
-- ============================================================

-- ── 1. Complemento de veiculo_tempo_real ────────────────────
-- Nota: muralha.veiculo_tempo_real.id é uniqueidentifier (não bigint)
IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'vtr_complemento'
)
BEGIN
    CREATE TABLE muralha.vtr_complemento (
        id_vtr         UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_vtr_complemento PRIMARY KEY,
        status_analise VARCHAR(20)      NULL,   -- plano 05: PENDENTE/PRE_APROVADA/DESEMPATE/REPROVADA
        latencia_ms    INT              NULL,   -- plano 07: ms entre captura e recepcao
        dt_preclass    DATETIME2        NULL    -- plano 10: quando saiu de PENDENTE
    );
    PRINT 'Criada: muralha.vtr_complemento';
END
ELSE
    PRINT 'Ja existe: muralha.vtr_complemento';

-- ── 2. Complemento de veiculo_tempo_real_imagem ─────────────
-- Nota: muralha.veiculo_tempo_real_imagem.id é uniqueidentifier (não bigint)
IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'vtr_imagem_complemento'
)
BEGIN
    CREATE TABLE muralha.vtr_imagem_complemento (
        id_imagem          UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_vtr_imagem_complemento PRIMARY KEY,
        sha256             CHAR(64)         NULL,           -- plano 02
        status_integridade VARCHAR(20)      NOT NULL
            CONSTRAINT DF_vtric_status DEFAULT 'OK',       -- plano 02
        obliterada         BIT              NOT NULL
            CONSTRAINT DF_vtric_obliterada DEFAULT 0,      -- plano 06
        id_original        UNIQUEIDENTIFIER NULL            -- plano 06: id da imagem original
    );
    PRINT 'Criada: muralha.vtr_imagem_complemento';
END
ELSE
    PRINT 'Ja existe: muralha.vtr_imagem_complemento';

-- ── 3. Complemento de sis_usuario ───────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'dbo' AND t.name = 'sis_usuario_complemento'
)
BEGIN
    CREATE TABLE dbo.sis_usuario_complemento (
        id_usuario          INT          NOT NULL
            CONSTRAINT PK_sis_usuario_complemento PRIMARY KEY,
        senha_hash          VARCHAR(64)  NULL,   -- plano 03: SHA-256 hex
        senha_historico     VARCHAR(MAX) NULL,   -- plano 03: JSON array dos ultimos N hashes
        tentativas_invalidas INT         NOT NULL
            CONSTRAINT DF_suc_tentativas DEFAULT 0,   -- plano 03
        bloqueado_ate       DATETIME2    NULL,         -- plano 03
        senha_trocada_em    DATETIME2    NULL,         -- plano 03
        totp_secret         VARCHAR(200) NULL,         -- plano 04
        totp_habilitado     BIT          NOT NULL
            CONSTRAINT DF_suc_totp DEFAULT 0,         -- plano 04
        cpf                 CHAR(11)     NULL,         -- plano 09
        matricula           VARCHAR(20)  NULL          -- plano 09
    );

    CREATE UNIQUE INDEX UQ_suc_cpf
        ON dbo.sis_usuario_complemento(cpf)
        WHERE cpf IS NOT NULL;

    CREATE UNIQUE INDEX UQ_suc_matricula
        ON dbo.sis_usuario_complemento(matricula)
        WHERE matricula IS NOT NULL;

    PRINT 'Criada: dbo.sis_usuario_complemento';
END
ELSE
    PRINT 'Ja existe: dbo.sis_usuario_complemento';

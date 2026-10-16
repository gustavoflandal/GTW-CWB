-- ============================================================
-- Migração: muralha.vtr_imagem_complemento (Plano 02 — hash SHA-256)
-- Data: 2026-10-16 (Plano 24, Fase 2 da correção da auditoria)
--
-- Por quê: o único DDL desta tabela estava em
-- 20261008_tabelas_complementares.sql, marcado OBSOLETO, cujo cabeçalho
-- mandava descartá-la. A tabela é usada por
-- muralha.digital.integridade.JobSha256Imagem e existe no GTW_MURALHA_DEV
-- (≈ 142 mil linhas em 2026-10-15). Este script recria a mesma estrutura
-- de forma idempotente para ambientes novos; no DEV não faz nada.
--
-- Estrutura conferida contra docs/banco-de-dados/tabelas/muralha/vtr.md.
-- Executar somente com autorização humana.
--
-- Rollback (só se a tabela foi criada por este script e não tem dados
-- que precisem ser mantidos):
--   DROP TABLE muralha.vtr_imagem_complemento;
-- ============================================================

IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'vtr_imagem_complemento'
)
BEGIN
    CREATE TABLE muralha.vtr_imagem_complemento (
        id_imagem          UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_vtr_imagem_complemento PRIMARY KEY CLUSTERED,
        sha256             CHAR(64)         NULL,
        status_integridade VARCHAR(20)      NOT NULL
            CONSTRAINT DF_vtric_status DEFAULT 'OK',
        obliterada         BIT              NOT NULL
            CONSTRAINT DF_vtric_obliterada DEFAULT 0,
        id_original        UNIQUEIDENTIFIER NULL
    );
    PRINT 'Criada: muralha.vtr_imagem_complemento';
END
ELSE
    PRINT 'Ja existe: muralha.vtr_imagem_complemento (nada alterado)';
GO

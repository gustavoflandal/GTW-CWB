-- ============================================================
-- Migração: Tabela de log de auditoria centralizado
-- Data: 2026-10-08
-- Autor: gustavoflandal
-- Rollback: DROP TABLE dbo.sis_log_auditoria;
-- ============================================================

IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'dbo' AND t.name = 'sis_log_auditoria'
)
BEGIN
    CREATE TABLE dbo.sis_log_auditoria (
        id             BIGINT IDENTITY(1,1) NOT NULL
            CONSTRAINT PK_sis_log_auditoria PRIMARY KEY,
        id_usuario     INT              NULL,
        login          VARCHAR(50)      NULL,
        dt_operacao    DATETIME2(3)     NOT NULL
            CONSTRAINT DF_sla_dt DEFAULT SYSDATETIME(),
        ip_terminal    VARCHAR(45)      NULL,
        funcionalidade VARCHAR(100)     NOT NULL,
        operacao       VARCHAR(50)      NOT NULL,
        id_registro    VARCHAR(100)     NULL,
        descricao      VARCHAR(500)     NULL
    );

    CREATE INDEX IX_sla_dt        ON dbo.sis_log_auditoria (dt_operacao);
    CREATE INDEX IX_sla_usuario   ON dbo.sis_log_auditoria (id_usuario);
    CREATE INDEX IX_sla_func      ON dbo.sis_log_auditoria (funcionalidade);

    PRINT 'Criada: dbo.sis_log_auditoria';
END
ELSE
    PRINT 'Ja existe: dbo.sis_log_auditoria';

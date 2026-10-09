-- ============================================================
-- Plano 10: SLA de Pré-processamento 72h
-- ============================================================
-- Tabela de snapshots periódicos do aging de registros pendentes.
-- Alimentada por SlaPreprocessamentoJob (Quartz, a cada hora).
--
-- ROLLBACK:
--   DROP TABLE muralha.alerta_sla_preproc;
-- ============================================================

IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'alerta_sla_preproc'
)
BEGIN
    CREATE TABLE muralha.alerta_sla_preproc (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        dt_alerta       DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
        total_pendentes INT          NOT NULL,
        acima_72h       INT          NOT NULL,
        acima_48h       INT          NOT NULL,
        acima_24h       INT          NOT NULL
    );
    PRINT 'Criada: muralha.alerta_sla_preproc';
END
ELSE
    PRINT 'Ja existe: muralha.alerta_sla_preproc';

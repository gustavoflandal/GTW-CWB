-- ============================================================
-- Plano 12: Política de Retenção de Dados
-- Plano 13: Anonimização de Placas (LGPD)
-- ============================================================
-- ROLLBACK:
--   DROP TABLE muralha.expurgo_log;
--   DROP VIEW muralha.vw_passagem_anonimizada;
--   DELETE FROM muralha.config_chave_valor WHERE chave IN ('retencao_anos','anonimizar_placa_padrao');
-- ============================================================

-- ── Plano 12: Log de expurgo ──────────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM sys.tables t
    JOIN sys.schemas s ON s.schema_id = t.schema_id
    WHERE s.name = 'muralha' AND t.name = 'expurgo_log'
)
BEGIN
    CREATE TABLE muralha.expurgo_log (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        dt_execucao     DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
        tabela          VARCHAR(100) NOT NULL,
        registros_movidos INT        NOT NULL,
        retencao_anos   INT          NOT NULL,
        dt_corte        DATE         NOT NULL,
        status          VARCHAR(20)  NOT NULL,
        mensagem        VARCHAR(500) NULL
    );
    PRINT 'Criada: muralha.expurgo_log';
END
ELSE
    PRINT 'Ja existe: muralha.expurgo_log';

-- Configuração de retenção
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'retencao_anos')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('retencao_anos', '5');

-- ── Plano 13: View de passagem anonimizada ────────────────
IF NOT EXISTS (
    SELECT 1 FROM sys.views v
    JOIN sys.schemas s ON s.schema_id = v.schema_id
    WHERE s.name = 'muralha' AND v.name = 'vw_passagem_anonimizada'
)
    EXEC('
    CREATE VIEW muralha.vw_passagem_anonimizada AS
    SELECT
        vtr.id,
        CASE
            WHEN vtr.placa IS NULL THEN NULL
            WHEN LEN(vtr.placa) >= 4
                THEN LEFT(vtr.placa, LEN(vtr.placa) - 3) + ''***''
            ELSE ''***''
        END AS placa,
        vtr.data,
        vtr.data_importado,
        vtr.id_local,
        vtr.id_pista,
        COALESCE(vsa.status_analise, ''AGUARDANDO_ANALISE'') AS status_analise
    FROM muralha.veiculo_tempo_real vtr
    LEFT JOIN muralha.vtr_status_analise vsa
        ON vsa.id_veiculo_tempo_real = vtr.id
    ');
ELSE
    PRINT 'Ja existe: muralha.vw_passagem_anonimizada';

-- Configuração padrão de anonimização
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'anonimizar_placa_padrao')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('anonimizar_placa_padrao', '1');

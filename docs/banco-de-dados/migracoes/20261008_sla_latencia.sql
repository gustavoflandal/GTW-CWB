-- Plano 07: SLA de Latência
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
-- ROLLBACK:
--   ALTER TABLE muralha.veiculo_tempo_real DROP COLUMN latencia_ms;
--   DROP TABLE muralha.alerta_sla;
--   DELETE FROM muralha.config_chave_valor WHERE chave='sla_latencia_threshold_ms';

-- 1. Coluna de latência na tabela principal de passagens
ALTER TABLE muralha.veiculo_tempo_real
    ADD latencia_ms INT NULL;   -- milissegundos; NULL se data_importado ausente

-- 2. Tabela de alertas de SLA
CREATE TABLE muralha.alerta_sla (
    id               BIGINT       IDENTITY(1,1) PRIMARY KEY,
    dt_alerta        DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    janela_inicio    DATETIME2    NOT NULL,
    janela_fim       DATETIME2    NOT NULL,
    total_passagens  INT          NOT NULL,
    percentil95_ms   INT          NOT NULL,
    threshold_ms     INT          NOT NULL,
    violacao         BIT          NOT NULL,
    locais_top       VARCHAR(500) NULL   -- JSON: top 3 locais com maior latência média
);

-- 3. Índice para consultas de painel
CREATE INDEX ix_vtr_latencia_ms ON muralha.veiculo_tempo_real (latencia_ms)
    WHERE latencia_ms IS NOT NULL;

-- 4. Configuração de threshold
INSERT INTO muralha.config_chave_valor (chave, valor)
VALUES ('sla_latencia_threshold_ms', '4000');

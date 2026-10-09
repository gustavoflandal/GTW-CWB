-- Plano 05: Dupla Análise Independente de Infrações
-- ROLLBACK:
-- DROP TABLE muralha.infracao_analise;
-- ALTER TABLE muralha.veiculo_tempo_real DROP COLUMN status_analise;

CREATE TABLE muralha.infracao_analise (
    id              BIGINT         IDENTITY(1,1) PRIMARY KEY,
    id_infracao     UNIQUEIDENTIFIER NOT NULL,  -- FK para muralha.veiculo_tempo_real.id
    id_usuario      INT             NOT NULL,
    sequencia       TINYINT         NOT NULL,   -- 1=primeira, 2=segunda, 3=desempate
    classificacao   VARCHAR(20)     NOT NULL,   -- 'VALIDA', 'INVALIDA', 'DUVIDA'
    justificativa   VARCHAR(500)    NULL,
    dt_analise      DATETIME2(3)    NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT uq_infracao_usuario UNIQUE (id_infracao, id_usuario)
);

CREATE INDEX ix_ia_infracao ON muralha.infracao_analise (id_infracao);
CREATE INDEX ix_ia_usuario  ON muralha.infracao_analise (id_usuario);

ALTER TABLE muralha.veiculo_tempo_real ADD
    status_analise VARCHAR(25) NOT NULL DEFAULT 'AGUARDANDO_ANALISE';

CREATE INDEX ix_vtr_status_analise ON muralha.veiculo_tempo_real (status_analise);

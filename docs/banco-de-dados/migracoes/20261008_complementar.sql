-- Migração complementar: cria tabelas auxiliares sem ALTER TABLE
-- Substitui ALTER TABLEs dos planos 05, 06 e 07
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- ═══ Plano 05: Status de análise (complemento a veiculo_tempo_real) ═══
-- ROLLBACK: DROP TABLE muralha.vtr_status_analise;
CREATE TABLE muralha.vtr_status_analise (
    id_veiculo_tempo_real UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    status_analise        VARCHAR(25)      NOT NULL DEFAULT 'AGUARDANDO_ANALISE'
);
GO

CREATE INDEX ix_vsa_status ON muralha.vtr_status_analise (status_analise);
GO

-- ═══ Plano 06: Imagens obliteradas (complemento a veiculo_tempo_real_imagem) ═══
-- ROLLBACK: DROP TABLE muralha.vtr_imagem_obliterada;
--           DROP TABLE muralha.infracao_imagem_obliteracao;
CREATE TABLE muralha.vtr_imagem_obliterada (
    id                    UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    id_imagem_original    UNIQUEIDENTIFIER NOT NULL,
    id_veiculo_tempo_real UNIQUEIDENTIFIER NOT NULL,
    imagem                IMAGE            NOT NULL,
    dt_criacao            DATETIME2(3)     NOT NULL DEFAULT SYSDATETIME()
);
GO

CREATE INDEX ix_vio_original ON muralha.vtr_imagem_obliterada (id_imagem_original);
GO

CREATE TABLE muralha.infracao_imagem_obliteracao (
    id                     BIGINT           IDENTITY(1,1) PRIMARY KEY,
    id_imagem_original     UNIQUEIDENTIFIER NOT NULL,
    id_imagem_obliterada   UNIQUEIDENTIFIER NULL,
    tipo                   CHAR(1)          NOT NULL,
    coordenadas_json       VARCHAR(MAX)     NOT NULL,
    dt_aplicacao           DATETIME2(3)     NOT NULL DEFAULT SYSDATETIME(),
    id_usuario_aplicou     INT              NOT NULL,
    revertida              BIT              NOT NULL DEFAULT 0,
    dt_reversao            DATETIME2        NULL,
    id_usuario_reverteu    INT              NULL,
    justificativa_reversao VARCHAR(500)     NULL
);
GO

-- ═══ Plano 07: Alertas de SLA (latência calculada via DATEDIFF em runtime) ═══
-- ROLLBACK: DROP TABLE muralha.alerta_sla;
--           DELETE FROM muralha.config_chave_valor WHERE chave='sla_latencia_threshold_ms';
CREATE TABLE muralha.alerta_sla (
    id               BIGINT       IDENTITY(1,1) PRIMARY KEY,
    dt_alerta        DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    janela_inicio    DATETIME2    NOT NULL,
    janela_fim       DATETIME2    NOT NULL,
    total_passagens  INT          NOT NULL,
    percentil95_ms   INT          NOT NULL,
    threshold_ms     INT          NOT NULL,
    violacao         BIT          NOT NULL,
    locais_top       VARCHAR(500) NULL
);
GO

INSERT INTO muralha.config_chave_valor (chave, valor)
VALUES ('sla_latencia_threshold_ms', '4000');
GO

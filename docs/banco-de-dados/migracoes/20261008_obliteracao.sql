-- Plano 06: Obliteração de Imagens (LGPD)
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
-- ROLLBACK: DROP TABLE muralha.infracao_imagem_obliteracao;
--           ALTER TABLE muralha.veiculo_tempo_real_imagem DROP COLUMN obliterada, id_original;

ALTER TABLE muralha.veiculo_tempo_real_imagem ADD
    obliterada  BIT   NOT NULL DEFAULT 0,
    id_original UNIQUEIDENTIFIER NULL;   -- NULL para imagens originais; preenchido para cópias obliteradas

CREATE TABLE muralha.infracao_imagem_obliteracao (
    id                     BIGINT           IDENTITY(1,1) PRIMARY KEY,
    id_imagem_original     UNIQUEIDENTIFIER NOT NULL,
    id_imagem_obliterada   UNIQUEIDENTIFIER NULL,   -- preenchido após gerar a cópia
    tipo                   CHAR(1)      NOT NULL,  -- 'M'=manual, 'A'=automática
    coordenadas_json       VARCHAR(MAX) NOT NULL,  -- [{"x":10,"y":20,"w":50,"h":30},...]
    dt_aplicacao           DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    id_usuario_aplicou     INT          NOT NULL,
    revertida              BIT          NOT NULL DEFAULT 0,
    dt_reversao            DATETIME2    NULL,
    id_usuario_reverteu    INT          NULL,
    justificativa_reversao VARCHAR(500) NULL
);

-- Fix: executa apenas o ALTER TABLE que falhou no script principal
-- (CREATE TABLE infracao_analise e seus índices já foram criados)
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;

ALTER TABLE muralha.veiculo_tempo_real ADD
    status_analise VARCHAR(25) NOT NULL DEFAULT 'AGUARDANDO_ANALISE';

CREATE INDEX ix_vtr_status_analise ON muralha.veiculo_tempo_real (status_analise);

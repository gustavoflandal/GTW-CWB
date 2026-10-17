-- ============================================================
-- Migração: ajuste TEMPORÁRIO de apresentação dos tipos de alerta
-- Data: 2026-10-17 (Plano 28 — Alertas Operacionais; gap registrado em
--       docs/entregas/gap-tipos-alerta-ajuste-demo.md)
--
-- Por quê: a listagem de tipos de alerta (/MuralhaDigital/TipoAlertaConfig,
-- usada também pelo popup de notificação) mostra inconsistências:
--   - "Integridade de imagem divergente" e "Outros" sem cor e sem ícone;
--   - "Veículo Suspeito de Roubo à Banco" e "Veículo Suspeito de Sequestro
--     Relâmpago" são críticos, mas aparecem com a cor verde (success);
--   - "Falha de Equipamento" e "Roubo à Banco" com a mesma prioridade (5);
--   - "Outros" sem descrição.
-- Por decisão de 2026-10-17, NENHUMA tabela existente é alterada
-- (muralha.tipo_alerta_ocorrencia e muralha.tipo_alerta_config ficam como
-- estão). Esta tabela nova sobrepõe os valores só na listagem, para a
-- demonstração. A correção definitiva fica registrada como gap.
--
-- O que este script faz (idempotente):
--   1. Cria muralha.tipo_alerta_ajuste_demo (sem FK, sem tocar no legado)
--   2. Grava 5 ajustes (MERGE pelo id do tipo)
--
-- Precedência na listagem: ajuste ativo > tipo_alerta_config > tipo_alerta_ocorrencia.
-- Enquanto um ajuste estiver ativo, ele prevalece sobre o que for salvo pela tela.
--
-- Rollback (desfaz por completo; nenhum dado legado foi tocado):
--   DROP TABLE muralha.tipo_alerta_ajuste_demo;
-- Para desligar sem apagar: UPDATE muralha.tipo_alerta_ajuste_demo SET ativo = 0;
--
-- Executar com sqlcmd -b -f 65001 e somente com autorização humana.
-- ============================================================
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

IF OBJECT_ID('muralha.tipo_alerta_ajuste_demo', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.tipo_alerta_ajuste_demo (
        id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_tipo_alerta_ajuste_demo PRIMARY KEY,
        critico      BIT           NULL,
        cor          VARCHAR(30)   NULL,
        icone        VARCHAR(50)   NULL,
        prioridade   INT           NULL,
        descricao    VARCHAR(500)  NULL,
        motivo       VARCHAR(300)  NOT NULL,
        ativo        BIT           NOT NULL CONSTRAINT DF_taad_ativo DEFAULT 1,
        dt_cadastro  DATETIME      NOT NULL CONSTRAINT DF_taad_dt DEFAULT GETDATE()
    );
    PRINT 'Criada: muralha.tipo_alerta_ajuste_demo';
END
ELSE PRINT 'Ja existe: muralha.tipo_alerta_ajuste_demo';
GO

DECLARE @ajustes TABLE (
    id UNIQUEIDENTIFIER, critico BIT, cor VARCHAR(30), icone VARCHAR(50),
    prioridade INT, descricao VARCHAR(500), motivo VARCHAR(300));

INSERT INTO @ajustes VALUES
 ('634C485A-42A6-480D-8BEB-8F63BF9CBFD7', NULL, 'warning', 'bi-shield-exclamation', NULL, NULL,
  'Tipo cadastrado (Plano 25) sem cor e sem ícone'),
 ('0AB34BEA-2F45-4D83-B7CB-53A3440DA8A6', NULL, 'info', 'bi-question-circle', NULL,
  N'Alerta que não se enquadra nos demais tipos cadastrados',
  'Tipo sem cor, sem ícone e sem descrição'),
 ('FEEF9500-83C0-4942-A8AA-AED77E20BA5B', NULL, 'error', NULL, NULL, NULL,
  'Tipo crítico exibido com cor verde (success)'),
 ('CB8D5C4B-1822-4868-A2F9-0153B50212DA', NULL, 'error', NULL, NULL, NULL,
  'Tipo crítico exibido com cor verde (success)'),
 ('E2A31F01-0001-4A00-B001-000000000004', NULL, NULL, NULL, 4, NULL,
  'Prioridade 5 repetida com "Veículo Suspeito de Roubo à Banco"');

MERGE muralha.tipo_alerta_ajuste_demo AS t
USING (SELECT a.* FROM @ajustes a
       WHERE EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia o WHERE o.id = a.id)) AS s
   ON t.id_tipo_alerta_ocorrencia = s.id
WHEN MATCHED THEN UPDATE SET critico = s.critico, cor = s.cor, icone = s.icone,
     prioridade = s.prioridade, descricao = s.descricao, motivo = s.motivo, ativo = 1
WHEN NOT MATCHED THEN INSERT (id_tipo_alerta_ocorrencia, critico, cor, icone, prioridade, descricao, motivo)
     VALUES (s.id, s.critico, s.cor, s.icone, s.prioridade, s.descricao, s.motivo);

DECLARE @ativos INT = (SELECT COUNT(*) FROM muralha.tipo_alerta_ajuste_demo WHERE ativo = 1);
PRINT CONCAT('Ajustes ativos: ', @ativos);
GO

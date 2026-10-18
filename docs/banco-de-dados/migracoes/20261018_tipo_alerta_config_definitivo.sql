-- ============================================================
-- Migração: configuração definitiva dos tipos de alerta (Plano 28)
-- Data: 2026-10-18
-- Gap resolvido: docs/entregas/gap-tipos-alerta-ajuste-demo.md
--
-- Por quê: a correção de 2026-10-17 (20261017_tipo_alerta_ajuste_demo.sql) era temporária —
-- uma tabela que sobrepunha cor/ícone na listagem. Agora existe a tela de configuração
-- (Tipos de Alerta) e os valores vão para o lugar definitivo, muralha.tipo_alerta_config.
-- Os tratamentos automáticos passam a ser executados pelo JobTratamentoAutomatico.
--
-- O que este script faz (idempotente; nenhuma tabela legada é alterada):
--   1. Grava em muralha.tipo_alerta_config as cores e ícones corrigidos, com registro em
--      muralha.parametro_historico (valor anterior e novo)
--   2. Desmarca "automático" dos tratamentos que dependem do operador
--      (CAMPO, ANALISE_OPERACIONAL, INTEGRACAO_CORPORATIVA, OUTRO)
--   3. Cria muralha.alerta_tratamento_execucao (cada alerta × tratamento executado uma vez)
--   4. Parâmetro TRATAMENTO_AUTO_JANELA_MIN (minutos de alertas recentes considerados pelo job)
--   5. Remove a tabela temporária muralha.tipo_alerta_ajuste_demo
--
-- Fora do escopo, por decisão de não alterar tabelas legadas: prioridade e descrição dos tipos
-- ficam em muralha.tipo_alerta_ocorrencia e são exibidas sem edição na tela.
--
-- Executar com: sqlcmd -b -I -f 65001 -i <arquivo>   (somente com autorização humana)
-- Rollback:
--   DROP TABLE muralha.alerta_tratamento_execucao;
--   DELETE FROM muralha.config_chave_valor WHERE chave = 'TRATAMENTO_AUTO_JANELA_MIN';
--   (cores/ícones: reverter pelos valores anteriores registrados em muralha.parametro_historico,
--    origem 'migracao 20261018_tipo_alerta_config_definitivo')
-- ============================================================
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Cores e ícones ----------------------------------------------------------------
DECLARE @origem VARCHAR(60) = 'migracao 20261018_tipo_alerta_config_definitivo';
DECLARE @novos TABLE (id UNIQUEIDENTIFIER PRIMARY KEY, cor VARCHAR(30) NULL, icone VARCHAR(50) NULL);
INSERT INTO @novos VALUES
    ('634C485A-42A6-480D-8BEB-8F63BF9CBFD7', 'warning', 'bi-shield-exclamation'),  -- Integridade de imagem divergente
    ('0AB34BEA-2F45-4D83-B7CB-53A3440DA8A6', 'info',    'bi-question-circle'),     -- Outros
    ('FEEF9500-83C0-4942-A8AA-AED77E20BA5B', 'error',   NULL),                     -- Roubo à Banco (crítico, estava verde)
    ('CB8D5C4B-1822-4868-A2F9-0153B50212DA', 'error',   NULL);                     -- Sequestro Relâmpago (crítico, estava verde)

DELETE n FROM @novos n WHERE NOT EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia o WHERE o.id = n.id);

INSERT INTO muralha.parametro_historico (tipo_recurso, id_recurso, parametro, valor_anterior, valor_novo, login, origem)
SELECT 'TIPO_ALERTA', CAST(n.id AS VARCHAR(36)), x.parametro, x.anterior, x.novo, 'migracao', @origem
FROM @novos n
LEFT JOIN muralha.tipo_alerta_config c ON c.id_tipo_alerta_ocorrencia = n.id
CROSS APPLY (VALUES ('cor', c.cor, n.cor), ('icone', c.icone, ISNULL(n.icone, c.icone))) x(parametro, anterior, novo)
WHERE ISNULL(x.anterior, '') <> ISNULL(x.novo, '');

MERGE muralha.tipo_alerta_config AS t
USING @novos AS s ON t.id_tipo_alerta_ocorrencia = s.id
WHEN MATCHED THEN UPDATE SET cor = s.cor, icone = ISNULL(s.icone, t.icone), dt_atualizacao = GETDATE()
WHEN NOT MATCHED THEN INSERT (id_tipo_alerta_ocorrencia, critico, cor, icone, dt_atualizacao)
     VALUES (s.id, 0, s.cor, s.icone, GETDATE());
PRINT 'Cores e icones gravados em muralha.tipo_alerta_config';
GO

-- 2. Tratamentos que dependem do operador não podem ser automáticos ----------------
UPDATE muralha.tipo_alerta_tratamento
   SET automatico = 0
 WHERE automatico = 1
   AND tratamento IN ('CAMPO', 'ANALISE_OPERACIONAL', 'INTEGRACAO_CORPORATIVA', 'OUTRO');
PRINT CONCAT('Tratamentos manuais desmarcados como automaticos: ', @@ROWCOUNT);
GO

-- 3. Execuções dos tratamentos automáticos ------------------------------------------
IF OBJECT_ID('muralha.alerta_tratamento_execucao', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.alerta_tratamento_execucao (
        id_alerta    UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT FK_ate_alerta REFERENCES muralha.alerta(id),
        tratamento   VARCHAR(40)      NOT NULL,
        dt_execucao  DATETIME         NOT NULL CONSTRAINT DF_ate_dt DEFAULT GETDATE(),
        resultado    VARCHAR(200)     NULL,
        CONSTRAINT PK_alerta_tratamento_execucao PRIMARY KEY (id_alerta, tratamento)
    );
    CREATE INDEX IX_ate_dt_execucao ON muralha.alerta_tratamento_execucao (dt_execucao);
    PRINT 'Criada: muralha.alerta_tratamento_execucao';
END
ELSE PRINT 'Ja existe: muralha.alerta_tratamento_execucao';
GO

-- 4. Janela do job ------------------------------------------------------------------
MERGE muralha.config_chave_valor AS t
USING (VALUES ('TRATAMENTO_AUTO_JANELA_MIN', '60')) AS s(chave, valor) ON t.chave = s.chave
WHEN NOT MATCHED THEN INSERT (chave, valor) VALUES (s.chave, s.valor);
PRINT 'Parametro TRATAMENTO_AUTO_JANELA_MIN conferido';
GO

-- 5. Remove a tabela temporária ----------------------------------------------------
IF OBJECT_ID('muralha.tipo_alerta_ajuste_demo', 'U') IS NOT NULL
BEGIN
    DROP TABLE muralha.tipo_alerta_ajuste_demo;
    PRINT 'Removida: muralha.tipo_alerta_ajuste_demo';
END
GO

SELECT RTRIM(o.tipo) AS tipo, c.critico, c.cor, c.icone
FROM muralha.tipo_alerta_ocorrencia o
LEFT JOIN muralha.tipo_alerta_config c ON c.id_tipo_alerta_ocorrencia = o.id
ORDER BY o.prioridade, o.tipo;
GO

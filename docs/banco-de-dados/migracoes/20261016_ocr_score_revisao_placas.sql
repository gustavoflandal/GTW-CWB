-- ============================================================
-- Migração: score de OCR, revisão automática e múltiplas placas
-- Data: 2026-10-16 (Plano 27, Fase 5 da correção da auditoria)
-- TR: 5.7.1.4 (N placas por passagem), 5.7.4.1-4 (score, limiar,
--     encaminhamento, metadado permanente), 5.2.2.5, 5.7.5.2 (correção
--     com justificativa) e 5.7.5.4 (filtros da fila de correção)
--
-- Por quê: nenhuma tabela guarda score de OCR (dbo.spu_veiculo_tempo_real
-- não recebe score) e a passagem só comporta placa e placa_frontal.
--
-- O que este script faz (idempotente; nenhuma tabela existente muda):
--   1. muralha.vtr_placa_leitura          N placas por passagem, com score e origem
--   2. muralha.equipamento_ocr_config     origem do score e limiar por equipamento
--   3. muralha.vtr_ocr_avaliacao          passagens já avaliadas pela regra
--   4. muralha.vtr_revisao_placa          fila de revisão e histórico das intervenções
--   5. muralha.motivo_correcao_placa      motivos de correção (parametrizáveis)
--   6. muralha.vtr_correcao_justificativa justificativa de cada linha de veiculo_tempo_real_correcao
--   7. Chaves OCR_* em muralha.config_chave_valor
--   8. Cópia de placa/placa_frontal dos últimos @dias dias para vtr_placa_leitura
--      (as colunas originais continuam lá; para copiar tudo, troque @dias)
--   9. Menus
--
-- Executar com sqlcmd -b -f 65001 e somente com autorização humana.
-- O passo 8 insere ~1 milhão de linhas no DEV (30 dias), um dia por vez.
--
-- Rollback (na ordem inversa):
--   DELETE d FROM dbo.sis_menu_direitos d JOIN dbo.sis_menu m ON m.id_menu = d.id_menu
--     WHERE m.acao IN ('/muralha-digital/pages/processamento/revisao-placas/index.jsp',
--                      '/MuralhaDigital/RevisaoPlaca', '/MuralhaDigital/OcrConfig');
--   DELETE FROM dbo.sis_menu_infos WHERE href = '/muralha-digital/pages/processamento/revisao-placas/index.jsp';
--   DELETE FROM dbo.sis_menu WHERE acao IN ('/muralha-digital/pages/processamento/revisao-placas/index.jsp',
--     '/MuralhaDigital/RevisaoPlaca', '/MuralhaDigital/OcrConfig');
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'OCR[_]%';
--   DROP TABLE muralha.vtr_correcao_justificativa; DROP TABLE muralha.motivo_correcao_placa;
--   DROP TABLE muralha.vtr_revisao_placa; DROP TABLE muralha.vtr_ocr_avaliacao;
--   DROP TABLE muralha.equipamento_ocr_config; DROP TABLE muralha.vtr_placa_leitura;
-- ============================================================

SET NOCOUNT ON;
-- sqlcmd usa QUOTED_IDENTIFIER OFF por padrão; tabelas com índice filtrado exigem ON.
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Leituras de placa ----------------------------------------------------
IF OBJECT_ID('muralha.vtr_placa_leitura', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.vtr_placa_leitura (
        id                    BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_vtr_placa_leitura PRIMARY KEY,
        id_veiculo_tempo_real UNIQUEIDENTIFIER NOT NULL,
        ordem                 TINYINT          NOT NULL,   -- 1 = tração, 2 = frontal, 3+ = reboques
        tipo                  VARCHAR(15)      NOT NULL
            CONSTRAINT CK_vpl_tipo CHECK (tipo IN ('TRACAO', 'FRONTAL', 'REBOQUE', 'SEMIRREBOQUE')),
        placa                 VARCHAR(10)      NULL,       -- nulo = sem leitura
        score                 DECIMAL(5,2)     NULL
            CONSTRAINT CK_vpl_score CHECK (score IS NULL OR (score >= 0 AND score <= 100)),
        origem                VARCHAR(10)      NOT NULL
            CONSTRAINT CK_vpl_origem CHECK (origem IN ('EMBARCADA', 'CENTRAL', 'MANUAL')),
        dt                    DATETIME2        NOT NULL CONSTRAINT DF_vpl_dt DEFAULT SYSDATETIME(),
        id_usuario            INT              NULL,
        CONSTRAINT UQ_vpl_passagem UNIQUE (id_veiculo_tempo_real, origem, ordem)
    );
    PRINT 'Criada: muralha.vtr_placa_leitura';
END
ELSE PRINT 'Ja existe: muralha.vtr_placa_leitura';
GO

-- 2. Configuração por equipamento -----------------------------------------
IF OBJECT_ID('muralha.equipamento_ocr_config', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.equipamento_ocr_config (
        id_local         INT          NOT NULL CONSTRAINT PK_equipamento_ocr_config PRIMARY KEY,
        origem_score     VARCHAR(20)  NULL   -- nulo = usa OCR_SCORE_ORIGEM
            CONSTRAINT CK_eoc_origem CHECK (origem_score IS NULL OR origem_score IN ('TABELA_LEITURA', 'DADO_BRUTO', 'AUSENTE')),
        campo            VARCHAR(100) NULL,
        limiar           DECIMAL(5,2) NULL   -- nulo = usa OCR_SCORE_LIMIAR
            CONSTRAINT CK_eoc_limiar CHECK (limiar IS NULL OR (limiar >= 0 AND limiar <= 100)),
        ativo            BIT          NOT NULL CONSTRAINT DF_eoc_ativo DEFAULT 1,
        id_usuario       INT          NULL,
        data_modificacao DATETIME     NOT NULL CONSTRAINT DF_eoc_data DEFAULT GETDATE(),
        CONSTRAINT CK_eoc_campo CHECK (origem_score IS NULL OR origem_score <> 'DADO_BRUTO' OR campo IS NOT NULL)
    );
    PRINT 'Criada: muralha.equipamento_ocr_config';
END
ELSE PRINT 'Ja existe: muralha.equipamento_ocr_config';
GO

-- 3. Passagens avaliadas ------------------------------------------------------
IF OBJECT_ID('muralha.vtr_ocr_avaliacao', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.vtr_ocr_avaliacao (
        id_veiculo_tempo_real UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_vtr_ocr_avaliacao PRIMARY KEY,
        id_local              INT          NOT NULL,
        dt_passagem           DATETIME     NOT NULL,
        dt_avaliacao          DATETIME2    NOT NULL CONSTRAINT DF_voa_dt DEFAULT SYSDATETIME(),
        score_minimo          DECIMAL(5,2) NULL,
        limiar                DECIMAL(5,2) NOT NULL,
        motivo                VARCHAR(30)  NULL   -- nulo = não foi para revisão
    );
    CREATE INDEX IX_voa_local_passagem ON muralha.vtr_ocr_avaliacao (id_local, dt_passagem) INCLUDE (score_minimo, motivo);
    PRINT 'Criada: muralha.vtr_ocr_avaliacao';
END
ELSE PRINT 'Ja existe: muralha.vtr_ocr_avaliacao';
GO

-- 4. Fila de revisão e histórico das intervenções ----------------------------
--   motivo MANUAL = intervenção feita fora da fila (consulta de veículos ou lote)
--   origem_intervencao: FILA | LOTE | CONSULTA
IF OBJECT_ID('muralha.vtr_revisao_placa', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.vtr_revisao_placa (
        id                    BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_vtr_revisao_placa PRIMARY KEY,
        id_veiculo_tempo_real UNIQUEIDENTIFIER NOT NULL CONSTRAINT UQ_vrp_passagem UNIQUE,
        id_local              INT          NOT NULL,
        dt_passagem           DATETIME     NOT NULL,
        motivo                VARCHAR(30)  NOT NULL
            CONSTRAINT CK_vrp_motivo CHECK (motivo IN ('SEM_LEITURA', 'DIVERGENCIA_CAMPO_CENTRAL', 'SCORE_BAIXO', 'SCORE_AUSENTE', 'MANUAL')),
        score_minimo          DECIMAL(5,2) NULL,
        limiar                DECIMAL(5,2) NULL,
        detalhe               VARCHAR(200) NULL,
        situacao              VARCHAR(15)  NOT NULL CONSTRAINT DF_vrp_situacao DEFAULT 'PENDENTE'
            CONSTRAINT CK_vrp_situacao CHECK (situacao IN ('PENDENTE', 'CORRIGIDA', 'CONFIRMADA')),
        dt_entrada            DATETIME2    NOT NULL CONSTRAINT DF_vrp_entrada DEFAULT SYSDATETIME(),
        dt_tratamento         DATETIME2    NULL,
        id_usuario_tratamento INT          NULL,
        origem_intervencao    VARCHAR(15)  NULL
            CONSTRAINT CK_vrp_origem CHECK (origem_intervencao IS NULL OR origem_intervencao IN ('FILA', 'LOTE', 'CONSULTA')),
        id_correcao           INT          NULL   -- muralha.veiculo_tempo_real_correcao.id
    );
    CREATE INDEX IX_vrp_situacao_passagem ON muralha.vtr_revisao_placa (situacao, dt_passagem) INCLUDE (id_local, motivo, score_minimo);
    CREATE INDEX IX_vrp_local_passagem ON muralha.vtr_revisao_placa (id_local, dt_passagem);
    PRINT 'Criada: muralha.vtr_revisao_placa';
END
ELSE PRINT 'Ja existe: muralha.vtr_revisao_placa';
GO

-- 5. Motivos de correção ------------------------------------------------------
IF OBJECT_ID('muralha.motivo_correcao_placa', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.motivo_correcao_placa (
        id          INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_motivo_correcao_placa PRIMARY KEY,
        descricao   VARCHAR(100) NOT NULL,
        exige_texto BIT          NOT NULL CONSTRAINT DF_mcp_exige DEFAULT 0,
        confirmacao BIT          NOT NULL CONSTRAINT DF_mcp_confirmacao DEFAULT 0,  -- motivo usado quando a placa é conferida sem mudança
        ativo       BIT          NOT NULL CONSTRAINT DF_mcp_ativo DEFAULT 1,
        ordem       INT          NOT NULL CONSTRAINT DF_mcp_ordem DEFAULT 0
    );
    INSERT INTO muralha.motivo_correcao_placa (descricao, exige_texto, confirmacao, ordem) VALUES
        (N'Erro de leitura do OCR (caractere trocado)', 0, 0, 1),
        (N'Placa parcialmente ilegível na imagem', 0, 0, 2),
        (N'Placa não lida pelo equipamento', 0, 0, 3),
        (N'Placa frontal diferente da traseira', 1, 0, 4),
        (N'Inclusão de placa de reboque ou semirreboque', 0, 0, 5),
        (N'Conferida: leitura correta', 0, 1, 6),
        (N'Outro (descrever)', 1, 0, 99);
    PRINT 'Criada: muralha.motivo_correcao_placa (7 motivos)';
END
ELSE PRINT 'Ja existe: muralha.motivo_correcao_placa';
GO

-- 6. Justificativa das correções ----------------------------------------------
IF OBJECT_ID('muralha.vtr_correcao_justificativa', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.vtr_correcao_justificativa (
        id_correcao        INT           NOT NULL CONSTRAINT PK_vtr_correcao_justificativa PRIMARY KEY,
        id_motivo          INT           NOT NULL,
        justificativa      NVARCHAR(500) NULL,
        origem_intervencao VARCHAR(15)   NOT NULL,
        id_revisao         BIGINT        NULL,
        ip                 VARCHAR(45)   NULL,
        dt                 DATETIME2     NOT NULL CONSTRAINT DF_vcj_dt DEFAULT SYSDATETIME()
    );
    PRINT 'Criada: muralha.vtr_correcao_justificativa';
END
ELSE PRINT 'Ja existe: muralha.vtr_correcao_justificativa';
GO

-- 7. Parâmetros (não sobrescreve valor já ajustado) --------------------------
--   OCR_SCORE_ORIGEM        TABELA_LEITURA | DADO_BRUTO:<campo> | AUSENTE
--   OCR_SCORE_LIMIAR        limiar global 0-100 (cada equipamento pode ter o seu)
--   OCR_SCORE_ESCALA        100 = score em 0-100; 1 = score em 0-1
--   OCR_REVISAR_SEM_SCORE   1 = passagem sem score também vai para revisão
--   OCR_REVISAR_SEM_LEITURA 1 = passagem sem placa lida vai para revisão (0: no DEV seriam 23% das passagens)
--   OCR_JANELA_HORAS        só avalia passagens das últimas N horas
--   OCR_LOTE                passagens por execução do job (5 em 5 min)
--   OCR_ESPERA_SCORE_MIN    minutos esperando o score antes de avaliar sem ele
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_SCORE_ORIGEM')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_SCORE_ORIGEM', 'AUSENTE');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_SCORE_LIMIAR')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_SCORE_LIMIAR', '85');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_SCORE_ESCALA')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_SCORE_ESCALA', '100');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_REVISAR_SEM_SCORE')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_REVISAR_SEM_SCORE', '0');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_REVISAR_SEM_LEITURA')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_REVISAR_SEM_LEITURA', '0');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_JANELA_HORAS')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_JANELA_HORAS', '24');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_LOTE')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_LOTE', '500');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'OCR_ESPERA_SCORE_MIN')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('OCR_ESPERA_SCORE_MIN', '10');
PRINT 'Parametros OCR_* conferidos';
GO

-- 8. Cópia das placas existentes (sem apagar as colunas originais) -----------
DECLARE @dias INT = 30;
DECLARE @dia DATE = CAST(DATEADD(DAY, -@dias, GETDATE()) AS DATE);
DECLARE @fim DATE = CAST(DATEADD(DAY, 1, GETDATE()) AS DATE);
DECLARE @n INT = 0;
WHILE @dia < @fim
BEGIN
    INSERT INTO muralha.vtr_placa_leitura (id_veiculo_tempo_real, ordem, tipo, placa, score, origem)
    SELECT v.id, 1, 'TRACAO', NULLIF(LTRIM(RTRIM(v.placa)), ''), NULL, 'EMBARCADA'
    FROM muralha.veiculo_tempo_real v
    WHERE v.data >= @dia AND v.data < DATEADD(DAY, 1, @dia)
      AND NOT EXISTS (SELECT 1 FROM muralha.vtr_placa_leitura l
                      WHERE l.id_veiculo_tempo_real = v.id AND l.origem = 'EMBARCADA' AND l.ordem = 1);
    SET @n = @n + @@ROWCOUNT;

    INSERT INTO muralha.vtr_placa_leitura (id_veiculo_tempo_real, ordem, tipo, placa, score, origem)
    SELECT v.id, 2, 'FRONTAL', LTRIM(RTRIM(v.placa_frontal)), NULL, 'EMBARCADA'
    FROM muralha.veiculo_tempo_real v
    WHERE v.data >= @dia AND v.data < DATEADD(DAY, 1, @dia)
      AND NULLIF(LTRIM(RTRIM(v.placa_frontal)), '') IS NOT NULL
      AND NOT EXISTS (SELECT 1 FROM muralha.vtr_placa_leitura l
                      WHERE l.id_veiculo_tempo_real = v.id AND l.origem = 'EMBARCADA' AND l.ordem = 2);
    SET @n = @n + @@ROWCOUNT;
    SET @dia = DATEADD(DAY, 1, @dia);
END
PRINT 'Placas copiadas para vtr_placa_leitura: ' + CAST(@n AS VARCHAR(20));
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_vpl_passagem' AND object_id = OBJECT_ID('muralha.vtr_placa_leitura'))
    CREATE INDEX IX_vpl_passagem ON muralha.vtr_placa_leitura (id_veiculo_tempo_real) INCLUDE (ordem, tipo, placa, score, origem);
GO

-- 9. Menus ----------------------------------------------------------------
--   Fila: grupos 41 e 42; configuração do score/limiar: só o 42.
DECLARE @id INT;
DECLARE @tela VARCHAR(200) = '/muralha-digital/pages/processamento/revisao-placas/index.jsp';

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = @tela)
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Revisao de Placas', @tela, 'U', 1, 222, 'Revisao de Placas', 1, 'Muralha-Digital');
END
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/RevisaoPlaca')
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Revisao de Placas - fila', '/MuralhaDigital/RevisaoPlaca', 'U', 1, 222, 'Revisao de Placas fila', 1, 'Muralha-Digital');
END
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/OcrConfig')
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Score de OCR - configuracao', '/MuralhaDigital/OcrConfig', 'U', 1, 222, 'Score OCR config', 1, 'Muralha-Digital');
END

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE href = @tela)
BEGIN
    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    SELECT (SELECT MAX(id_infos) + 1 FROM dbo.sis_menu_infos), m.id_menu,
           '/muralha-digital/assets/images/analise/analise.jpg', 'Revisao de Placas', @tela,
           'Fila de revisao de placas com score de OCR baixo e correcao com justificativa', 3, 6
    FROM dbo.sis_menu m WHERE m.acao = @tela;
END

INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT g.id_grupo, NULL, m.id_menu
FROM dbo.sis_menu m
CROSS JOIN (SELECT 41 AS id_grupo UNION ALL SELECT 42) g
WHERE m.acao IN (@tela, '/MuralhaDigital/RevisaoPlaca')
  AND NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_menu = m.id_menu AND d.id_grupo = g.id_grupo);

INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT 42, NULL, m.id_menu
FROM dbo.sis_menu m
WHERE m.acao = '/MuralhaDigital/OcrConfig'
  AND NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_menu = m.id_menu AND d.id_grupo = 42);

PRINT 'Menus de revisao de placas conferidos';
GO

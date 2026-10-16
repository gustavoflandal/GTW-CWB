-- ============================================================
-- Migração: alertas operacionais — tipos, tratamentos, criticidade e retenção
-- Data: 2026-10-16 (Plano 28, Fase 6 da correção da auditoria)
-- TR: 5.3.1.3 (tratamentos), 5.3.2.1 (tipos), 5.3.3.3-4 (criticidade),
--     5.3.4.2-3 (espacial), 5.3.6 (histórico), 5.3.7.4 d-e (retenção),
--     5.3.7.6 (parametrização via interface), 5.3.7.8 (priorização)
--
-- O que este script faz (idempotente; nenhuma tabela existente muda):
--   1. muralha.tipo_alerta_config      criticidade, cor e ícone por tipo
--   2. muralha.tipo_alerta_tratamento  tratamentos parametrizáveis por tipo
--   3. muralha.alerta_retencao         detalhe da detecção de retenção/congestionamento
--   4. Novos tipos em muralha.tipo_alerta_ocorrencia (operacionais)
--   5. Seed de cores para tipos existentes em tipo_alerta_config
--   6. Seed de tratamentos padrão
--   7. Parâmetros RETENCAO_* em config_chave_valor
--   8. Menus
--
-- Executar com sqlcmd -b -f 65001 e somente com autorização humana.
--
-- Rollback (na ordem inversa):
--   DELETE d FROM dbo.sis_menu_direitos d JOIN dbo.sis_menu m ON m.id_menu = d.id_menu
--     WHERE m.acao IN ('/MuralhaDigital/TipoAlertaConfig');
--   DELETE FROM dbo.sis_menu_infos WHERE href = '/MuralhaDigital/TipoAlertaConfig';
--   DELETE FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/TipoAlertaConfig';
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'RETENCAO[_]%';
--   DROP TABLE muralha.alerta_retencao;
--   DROP TABLE muralha.tipo_alerta_tratamento;
--   DROP TABLE muralha.tipo_alerta_config;
--   DELETE FROM muralha.tipo_alerta_ocorrencia WHERE nomeinterno IN
--     ('ESTAC_IRREGULAR','CONGESTIONAMENTO','BLOQUEIO_VIA','FALHA_EQUIP');
-- ============================================================

SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Configuração complementar do tipo de alerta ----------------------------
IF OBJECT_ID('muralha.tipo_alerta_config', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.tipo_alerta_config (
        id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_tipo_alerta_config PRIMARY KEY
            CONSTRAINT FK_tac_tipo REFERENCES muralha.tipo_alerta_ocorrencia(id),
        critico       BIT NOT NULL CONSTRAINT DF_tac_critico DEFAULT 0,
        cor           VARCHAR(30)  NULL,
        icone         VARCHAR(50)  NULL,
        dt_atualizacao DATETIME    NOT NULL CONSTRAINT DF_tac_dt DEFAULT GETDATE(),
        id_usuario    INT          NULL
    );
    PRINT 'Criada: muralha.tipo_alerta_config';
END
ELSE PRINT 'Ja existe: muralha.tipo_alerta_config';
GO

-- 2. Tratamentos por tipo ---------------------------------------------------
IF OBJECT_ID('muralha.tipo_alerta_tratamento', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.tipo_alerta_tratamento (
        id            BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_tipo_alerta_tratamento PRIMARY KEY,
        id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT FK_tat_tipo REFERENCES muralha.tipo_alerta_ocorrencia(id),
        tratamento    VARCHAR(40)  NOT NULL,
        automatico    BIT          NOT NULL CONSTRAINT DF_tat_auto DEFAULT 0,
        parametros_json VARCHAR(MAX) NULL,
        ativo         BIT          NOT NULL CONSTRAINT DF_tat_ativo DEFAULT 1,
        dt_cadastro   DATETIME     NOT NULL CONSTRAINT DF_tat_dt DEFAULT GETDATE(),
        CONSTRAINT UQ_tat_tipo_trat UNIQUE (id_tipo_alerta_ocorrencia, tratamento)
    );
    PRINT 'Criada: muralha.tipo_alerta_tratamento';
END
ELSE PRINT 'Ja existe: muralha.tipo_alerta_tratamento';
GO

-- 3. Detalhes da detecção de retenção ---------------------------------------
IF OBJECT_ID('muralha.alerta_retencao', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.alerta_retencao (
        id_alerta           UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_alerta_retencao PRIMARY KEY
            CONSTRAINT FK_ar_alerta REFERENCES muralha.alerta(id),
        id_local            INT          NOT NULL,
        horas_consecutivas  INT          NOT NULL,
        velocidade_media    DECIMAL(6,1) NOT NULL,
        ocupacao_media      INT          NOT NULL,
        severidade          VARCHAR(10)  NOT NULL
    );
    PRINT 'Criada: muralha.alerta_retencao';
END
ELSE PRINT 'Ja existe: muralha.alerta_retencao';
GO

-- 4. Novos tipos de alerta operacionais -------------------------------------
IF NOT EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE nomeinterno = 'ESTAC_IRREGULAR')
BEGIN
    INSERT INTO muralha.tipo_alerta_ocorrencia (id, tipo, descricao, descricao_sms, permite_monitorado_sem_placa, tarefa_ativa, nomeinterno, prioridade)
    VALUES ('E2A31F01-0001-4A00-B001-000000000001', 'Estacionamento Irregular', 'Irregularidade de estacionamento ou parada', 'ESTAC IRREG', 1, 1, 'ESTAC_IRREGULAR', 20);
    PRINT 'Inserido: Estacionamento Irregular';
END

IF NOT EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE nomeinterno = 'CONGESTIONAMENTO')
BEGIN
    INSERT INTO muralha.tipo_alerta_ocorrencia (id, tipo, descricao, descricao_sms, permite_monitorado_sem_placa, tarefa_ativa, nomeinterno, prioridade)
    VALUES ('E2A31F01-0001-4A00-B001-AE7E4CA00001', 'Congestionamento/Retenção', 'Retenção ou congestionamento detectado por regra de tráfego', 'RETENCAO', 1, 1, 'CONGESTIONAMENTO', 15);
    PRINT 'Inserido: Congestionamento/Retencao';
END

IF NOT EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE nomeinterno = 'BLOQUEIO_VIA')
BEGIN
    INSERT INTO muralha.tipo_alerta_ocorrencia (id, tipo, descricao, descricao_sms, permite_monitorado_sem_placa, tarefa_ativa, nomeinterno, prioridade)
    VALUES ('E2A31F01-0001-4A00-B001-000000000003', 'Bloqueio de Via', 'Bloqueio ou interferência na circulação viária', 'BLOQUEIO', 1, 1, 'BLOQUEIO_VIA', 10);
    PRINT 'Inserido: Bloqueio de Via';
END

IF NOT EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE nomeinterno = 'FALHA_EQUIP')
BEGIN
    INSERT INTO muralha.tipo_alerta_ocorrencia (id, tipo, descricao, descricao_sms, permite_monitorado_sem_placa, tarefa_ativa, nomeinterno, prioridade)
    VALUES ('E2A31F01-0001-4A00-B001-000000000004', 'Falha de Equipamento', 'Falha operacional relevante de equipamento', 'FALHA EQUIP', 1, 1, 'FALHA_EQUIP', 5);
    PRINT 'Inserido: Falha de Equipamento';
END
GO

-- 5. Seed de cores para tipos existentes e novos ----------------------------
DECLARE @seed TABLE (id UNIQUEIDENTIFIER, cor VARCHAR(30), icone VARCHAR(50), critico BIT);
INSERT INTO @seed VALUES
    ('95631582-96B2-4220-9612-12131BE4923C', 'orange',  'bi-exclamation-triangle-fill', 1), -- Roubado
    ('0349F722-DFDE-4080-9E3B-D65F1C058EDC', 'orange',  'bi-exclamation-triangle',      1), -- Furtado
    ('AE93F81A-DF6D-41B5-AFC6-99B3438C291D', 'info',    'bi-truck',                     0), -- Clandestino
    ('FEEF9500-83C0-4942-A8AA-AED77E20BA5B', 'success', 'bi-bank',                      1), -- Roubo banco
    ('CB8D5C4B-1822-4868-A2F9-0153B50212DA', 'success', 'bi-person-dash',               1), -- Sequestro
    ('CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3', 'warning', 'bi-files',                     0), -- Clonado
    ('6631DC43-779F-4BFF-A329-B9653D056708', 'info',    'bi-calendar-x',                0), -- Licenciamento
    ('9D31A265-A663-4836-BF00-2309FC0D5E33', 'success', 'bi-eye',                       0), -- Monitorado
    ('19B86A23-2CD6-43ED-A596-62935EA3980A', 'info',    'bi-people',                    0), -- Comboio
    ('E2A31F01-0001-4A00-B001-000000000001', 'purple',  'bi-sign-no-parking',           0), -- Estacionamento
    ('E2A31F01-0001-4A00-B001-AE7E4CA00001', 'brown',  'bi-stoplights',                0), -- Congestionamento
    ('E2A31F01-0001-4A00-B001-000000000003', 'error',   'bi-cone-striped',              1), -- Bloqueio
    ('E2A31F01-0001-4A00-B001-000000000004', 'pink',    'bi-tools',                     0); -- Falha equip

MERGE muralha.tipo_alerta_config AS t
USING @seed AS s ON t.id_tipo_alerta_ocorrencia = s.id
WHEN NOT MATCHED THEN INSERT (id_tipo_alerta_ocorrencia, critico, cor, icone, dt_atualizacao)
    VALUES (s.id, s.critico, s.cor, s.icone, GETDATE());
PRINT 'Cores e criticidade seed conferidas';
GO

-- 6. Seed de tratamentos padrão para tipos novos ----------------------------
DECLARE @trat TABLE (id UNIQUEIDENTIFIER, tratamento VARCHAR(40), automatico BIT);
INSERT INTO @trat VALUES
    ('E2A31F01-0001-4A00-B001-AE7E4CA00001', 'REGISTRO_ESTATISTICO', 1),
    ('E2A31F01-0001-4A00-B001-AE7E4CA00001', 'ANALISE_OPERACIONAL', 0),
    ('E2A31F01-0001-4A00-B001-000000000003', 'CAMPO', 0),
    ('E2A31F01-0001-4A00-B001-000000000003', 'GERAR_OCORRENCIA', 1),
    ('E2A31F01-0001-4A00-B001-000000000004', 'CAMPO', 1),
    ('E2A31F01-0001-4A00-B001-000000000004', 'REGISTRO_ESTATISTICO', 1);

MERGE muralha.tipo_alerta_tratamento AS t
USING @trat AS s ON t.id_tipo_alerta_ocorrencia = s.id AND t.tratamento = s.tratamento
WHEN NOT MATCHED THEN INSERT (id_tipo_alerta_ocorrencia, tratamento, automatico, ativo, dt_cadastro)
    VALUES (s.id, s.tratamento, s.automatico, 1, GETDATE());
PRINT 'Tratamentos seed conferidos';
GO

-- 7. Parâmetros de retenção -------------------------------------------------
MERGE muralha.config_chave_valor AS t
USING (VALUES
    ('RETENCAO_OCUPACAO_MINIMA',         '500'),
    ('RETENCAO_VELOCIDADE_MAXIMA',       '15'),
    ('RETENCAO_DURACAO_HORAS',           '1'),
    ('RETENCAO_JANELA_HORAS',            '3'),
    ('RETENCAO_LOTE',                    '100'),
    ('RETENCAO_ESPERA_REINCIDENCIA_MIN', '60')
) AS s(chave, valor) ON t.chave = s.chave
WHEN NOT MATCHED THEN INSERT (chave, valor) VALUES (s.chave, s.valor);
PRINT 'Parametros RETENCAO_* conferidos';
GO

-- 8. Menus ------------------------------------------------------------------
DECLARE @maxMenu INT = (SELECT ISNULL(MAX(id_menu), 0) FROM dbo.sis_menu);
DECLARE @maxInfos INT = (SELECT ISNULL(MAX(id_infos), 0) FROM dbo.sis_menu_infos);

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/TipoAlertaConfig')
BEGIN
    SET @maxMenu = @maxMenu + 1;
    SET @maxInfos = @maxInfos + 1;

    INSERT INTO dbo.sis_menu (id_menu, Descricao, acao, Tipo, nivel, menu, Ativo)
    VALUES (@maxMenu, 'Configuracao de Tipos de Alerta', '/MuralhaDigital/TipoAlertaConfig', 'U', 1, 'Tipos de Alerta', 1);

    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    VALUES (@maxInfos, @maxMenu, '/muralha-digital/assets/images/menu-icons/alertas.svg', 'Tipos de Alerta', '/MuralhaDigital/TipoAlertaConfig', 'Configuracao de tipos, criticidade, cores e tratamentos', 9, 15);

    INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (@maxMenu, 42);
    PRINT 'Menu Tipos de Alerta criado: menu=' + CAST(@maxMenu AS VARCHAR) + ' infos=' + CAST(@maxInfos AS VARCHAR);
END
ELSE PRINT 'Menu Tipos de Alerta ja existe';
GO

PRINT 'Migracao de alertas operacionais concluida.';
GO

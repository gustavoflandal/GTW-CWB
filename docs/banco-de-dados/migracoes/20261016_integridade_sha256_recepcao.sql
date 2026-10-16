-- ============================================================
-- Migração: conferência de integridade SHA-256 na recepção de imagens
-- Data: 2026-10-16 (Plano 25, Fase 3 da correção da auditoria)
-- TR: 5.2.1.2 (verificação de integridade com SHA-256) e 5.2.1.3 a-f
--
-- Por quê: muralha.digital.integridade.JobSha256Imagem calculava o hash
-- depois da gravação e marcava 'OK' em todas as imagens, sem comparar com
-- nada. Agora o job compara o SHA-256 calculado com o hash enviado pela
-- origem (configurável por equipamento) e grava INTEGRO, DIVERGENTE,
-- SEM_CONFERENCIA ou ERRO_LEITURA, com motivo e data da verificação.
--
-- O que este script faz (tudo idempotente; nenhuma tabela existente muda de estrutura):
--   1. muralha.equipamento_integridade_config  origem do hash por equipamento
--   2. muralha.vtr_imagem_hash_origem          hash enviado pelo Captura (preenchida pela recepção, Fase 11)
--   3. muralha.vtr_imagem_integridade          resultado da conferência de cada imagem
--   4. Chaves INTEGRIDADE_* em muralha.config_chave_valor
--   5. Tipo de alerta INTEGRIDADE em muralha.tipo_alerta_ocorrencia
--   6. Legado: imagens já marcadas 'OK' passam a SEM_CONFERENCIA
--      (copiadas para vtr_imagem_integridade com motivo)
--   7. View muralha.v_integridade_recepcao (contadores por equipamento e dia)
--   8. Menu da tela e dos servlets (sis_menu, sis_menu_infos, sis_menu_direitos)
--
-- Observação: a coluna muralha.vtr_imagem_complemento.status_integridade
-- continua com DEFAULT 'OK' (mudar exigiria ALTER TABLE). Nenhum código grava
-- nessa tabela sem informar o status; o job sempre grava o status explícito.
--
-- Executar com sqlcmd -f 65001 e somente com autorização humana.
-- O passo 6 percorre ~200 mil linhas em lotes de 20 mil (alguns minutos).
--
-- Rollback (na ordem inversa; o passo 6 não volta para 'OK' de propósito,
-- porque 'OK' sem comparação era o defeito):
--   DELETE d FROM dbo.sis_menu_direitos d JOIN dbo.sis_menu m ON m.id_menu = d.id_menu
--     WHERE m.acao IN ('/muralha-digital/pages/monitoramento/integridade/index.jsp',
--                      '/MuralhaDigital/IntegridadeRecepcao', '/MuralhaDigital/IntegridadeConfig');
--   DELETE FROM dbo.sis_menu_infos WHERE href = '/muralha-digital/pages/monitoramento/integridade/index.jsp';
--   DELETE FROM dbo.sis_menu WHERE acao IN ('/muralha-digital/pages/monitoramento/integridade/index.jsp',
--     '/MuralhaDigital/IntegridadeRecepcao', '/MuralhaDigital/IntegridadeConfig');
--   DROP VIEW muralha.v_integridade_recepcao;
--   DELETE FROM muralha.tipo_alerta_ocorrencia WHERE RTRIM(nomeinterno) = 'INTEGRIDADE'
--     AND NOT EXISTS (SELECT 1 FROM muralha.alerta a WHERE a.id_tipo_alerta_ocorrencia = tipo_alerta_ocorrencia.id);
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'INTEGRIDADE[_]%';
--   DROP TABLE muralha.vtr_imagem_integridade;
--   DROP TABLE muralha.vtr_imagem_hash_origem;
--   DROP TABLE muralha.equipamento_integridade_config;
-- ============================================================

SET NOCOUNT ON;
-- sqlcmd usa QUOTED_IDENTIFIER OFF por padrão; tabelas com índice filtrado exigem ON.
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Origem do hash por equipamento -------------------------------------
IF OBJECT_ID('muralha.equipamento_integridade_config', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.equipamento_integridade_config (
        id_local         INT          NOT NULL
            CONSTRAINT PK_equipamento_integridade_config PRIMARY KEY,
        origem_hash      VARCHAR(20)  NOT NULL
            CONSTRAINT CK_eic_origem CHECK (origem_hash IN ('PAYLOAD_SOAP', 'DADO_BRUTO', 'AUSENTE')),
        campo            VARCHAR(100) NULL,   -- caminho no JSON de ia.veiculo_caracteristica.dado_bruto (origem DADO_BRUTO)
        ativo            BIT          NOT NULL CONSTRAINT DF_eic_ativo DEFAULT 1,
        id_usuario       INT          NULL,
        data_modificacao DATETIME     NOT NULL CONSTRAINT DF_eic_data DEFAULT GETDATE(),
        CONSTRAINT CK_eic_campo CHECK (origem_hash <> 'DADO_BRUTO' OR campo IS NOT NULL)
    );
    PRINT 'Criada: muralha.equipamento_integridade_config';
END
ELSE
    PRINT 'Ja existe: muralha.equipamento_integridade_config';
GO

-- 2. Hash enviado pelo Captura ------------------------------------------
IF OBJECT_ID('muralha.vtr_imagem_hash_origem', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.vtr_imagem_hash_origem (
        id_imagem     UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_vtr_imagem_hash_origem PRIMARY KEY,
        sha256_origem VARCHAR(128)     NOT NULL,  -- SHA-256 (64), SHA-384 (96) ou SHA-512 (128) em hexadecimal
        dt_recepcao   DATETIME2        NOT NULL CONSTRAINT DF_viho_dt DEFAULT SYSDATETIME()
    );
    PRINT 'Criada: muralha.vtr_imagem_hash_origem';
END
ELSE
    PRINT 'Ja existe: muralha.vtr_imagem_hash_origem';
GO

-- 3. Resultado da conferência -------------------------------------------
IF OBJECT_ID('muralha.vtr_imagem_integridade', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.vtr_imagem_integridade (
        id_imagem             UNIQUEIDENTIFIER NOT NULL
            CONSTRAINT PK_vtr_imagem_integridade PRIMARY KEY,
        id_veiculo_tempo_real UNIQUEIDENTIFIER NOT NULL,
        indice_imagem         TINYINT          NULL,
        id_local              INT              NOT NULL,
        dt_captura            DATETIME         NOT NULL,  -- veiculo_tempo_real.data
        dt_recepcao           DATETIME         NULL,      -- veiculo_tempo_real.data_importado
        dt_verificacao        DATETIME2        NOT NULL CONSTRAINT DF_vii_dt DEFAULT SYSDATETIME(),
        status_integridade    VARCHAR(20)      NOT NULL
            CONSTRAINT CK_vii_status CHECK (status_integridade IN ('INTEGRO', 'DIVERGENTE', 'SEM_CONFERENCIA', 'ERRO_LEITURA')),
        origem_hash           VARCHAR(120)     NOT NULL,  -- PAYLOAD_SOAP | DADO_BRUTO:<campo> | AUSENTE
        sha256_calculado      CHAR(64)         NULL,
        hash_origem           VARCHAR(128)     NULL,
        motivo                VARCHAR(200)     NULL
    );
    CREATE INDEX IX_vii_local_captura ON muralha.vtr_imagem_integridade (id_local, dt_captura)
        INCLUDE (status_integridade, dt_verificacao);
    CREATE INDEX IX_vii_captura ON muralha.vtr_imagem_integridade (dt_captura)
        INCLUDE (id_local, status_integridade);
    CREATE INDEX IX_vii_passagem ON muralha.vtr_imagem_integridade (id_veiculo_tempo_real);
    PRINT 'Criada: muralha.vtr_imagem_integridade';
END
ELSE
    PRINT 'Ja existe: muralha.vtr_imagem_integridade';
GO

-- 4. Parâmetros (não sobrescreve valor já ajustado) ----------------------
--   INTEGRIDADE_ORIGEM_HASH       PAYLOAD_SOAP | DADO_BRUTO:<campo> | AUSENTE (padrão de quem não tem config própria)
--   INTEGRIDADE_LOTE              imagens por execução do job (5 em 5 min)
--   INTEGRIDADE_JANELA_HORAS      só confere passagens capturadas nas últimas N horas
--   INTEGRIDADE_ESPERA_HASH_MIN   minutos esperando o hash de origem antes de concluir "não recebido"
--   INTEGRIDADE_ALERTA_HABILITADO 1 = divergência gera alerta em muralha.alerta
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'INTEGRIDADE_ORIGEM_HASH')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('INTEGRIDADE_ORIGEM_HASH', 'AUSENTE');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'INTEGRIDADE_LOTE')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('INTEGRIDADE_LOTE', '200');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'INTEGRIDADE_JANELA_HORAS')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('INTEGRIDADE_JANELA_HORAS', '24');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'INTEGRIDADE_ESPERA_HASH_MIN')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('INTEGRIDADE_ESPERA_HASH_MIN', '10');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'INTEGRIDADE_ALERTA_HABILITADO')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('INTEGRIDADE_ALERTA_HABILITADO', '1');
PRINT 'Parametros INTEGRIDADE_* conferidos';
GO

-- 5. Tipo de alerta (prioridade 2 estava livre em 2026-10-16) -----------
IF NOT EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE RTRIM(nomeinterno) = 'INTEGRIDADE')
BEGIN
    INSERT INTO muralha.tipo_alerta_ocorrencia
        (tipo, descricao, descricao_sms, permite_monitorado_sem_placa, tarefa_ativa,
         data_alteracao, id_usuario, nomeinterno, prioridade, cor)
    VALUES
        ('Integridade de imagem divergente',
         'SHA-256 da imagem recebida difere do hash enviado pelo equipamento (TR 5.2.1.2)',
         'Integridade imagem', 1, 1, GETDATE(), NULL, 'INTEGRIDADE',
         CASE WHEN EXISTS (SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE prioridade = 2) THEN NULL ELSE 2 END,
         '#DC3545');
    PRINT 'Inserido: tipo de alerta INTEGRIDADE';
END
ELSE
    PRINT 'Ja existe: tipo de alerta INTEGRIDADE';
GO

-- 6. Legado 'OK' -> SEM_CONFERENCIA --------------------------------------
DECLARE @n INT = 1;
WHILE @n > 0
BEGIN
    INSERT INTO muralha.vtr_imagem_integridade
        (id_imagem, id_veiculo_tempo_real, indice_imagem, id_local, dt_captura, dt_recepcao,
         dt_verificacao, status_integridade, origem_hash, sha256_calculado, hash_origem, motivo)
    SELECT TOP (20000)
        c.id_imagem, i.id_veiculo_tempo_real, i.indice_imagem, v.id_local, v.data, v.data_importado,
        SYSDATETIME(), 'SEM_CONFERENCIA', 'AUSENTE', c.sha256, NULL,
        'Hash calculado antes de 2026-10-16 sem hash de origem para comparar (antigo status OK).'
    FROM muralha.vtr_imagem_complemento c
    JOIN muralha.veiculo_tempo_real_imagem i ON i.id = c.id_imagem
    JOIN muralha.veiculo_tempo_real v ON v.id = i.id_veiculo_tempo_real
    WHERE c.status_integridade = 'OK'
      AND NOT EXISTS (SELECT 1 FROM muralha.vtr_imagem_integridade g WHERE g.id_imagem = c.id_imagem);
    SET @n = @@ROWCOUNT;
END

SET @n = 1;
WHILE @n > 0
BEGIN
    UPDATE TOP (20000) muralha.vtr_imagem_complemento
    SET status_integridade = 'SEM_CONFERENCIA'
    WHERE status_integridade = 'OK';
    SET @n = @@ROWCOUNT;
END
PRINT 'Legado OK convertido para SEM_CONFERENCIA';
GO

-- 7. View de contadores (TR 5.2.1.3 d-f) ---------------------------------
--   recebidos  = imagens conferidas no dia de captura
--   rejeitados = DIVERGENTE + ERRO_LEITURA (o motivo está em vtr_imagem_integridade.motivo)
IF OBJECT_ID('muralha.v_integridade_recepcao', 'V') IS NULL
    EXEC('CREATE VIEW muralha.v_integridade_recepcao AS SELECT 1 AS x');
GO
ALTER VIEW muralha.v_integridade_recepcao
AS
SELECT g.id_local,
       CAST(g.dt_captura AS DATE)                                                AS dia,
       COUNT(*)                                                                  AS recebidos,
       SUM(CASE WHEN g.status_integridade = 'INTEGRO'         THEN 1 ELSE 0 END) AS integros,
       SUM(CASE WHEN g.status_integridade = 'DIVERGENTE'      THEN 1 ELSE 0 END) AS divergentes,
       SUM(CASE WHEN g.status_integridade = 'SEM_CONFERENCIA' THEN 1 ELSE 0 END) AS sem_conferencia,
       SUM(CASE WHEN g.status_integridade = 'ERRO_LEITURA'    THEN 1 ELSE 0 END) AS erro_leitura,
       SUM(CASE WHEN g.status_integridade IN ('DIVERGENTE', 'ERRO_LEITURA') THEN 1 ELSE 0 END) AS rejeitados,
       MIN(g.dt_captura)                                                         AS primeira_captura,
       MAX(g.dt_captura)                                                         AS ultima_captura,
       MAX(g.dt_verificacao)                                                     AS ultima_verificacao
FROM muralha.vtr_imagem_integridade g
GROUP BY g.id_local, CAST(g.dt_captura AS DATE);
GO
PRINT 'View muralha.v_integridade_recepcao criada/atualizada';
GO

-- 8. Menus ----------------------------------------------------------------
--   Tela e consulta: grupos 41 (Anel de Seguranca) e 42 (Supervisores), como os demais itens de Analise de Dados.
--   Configuracao da origem do hash: so o grupo 42, como a configuracao de monitoramento (menu 235).
DECLARE @id INT;
DECLARE @tela VARCHAR(200) = '/muralha-digital/pages/monitoramento/integridade/index.jsp';

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = @tela)
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Integridade das Imagens', @tela, 'U', 1, 222, 'Integridade Imagens', 1, 'Muralha-Digital');
END
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/IntegridadeRecepcao')
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Integridade - consulta', '/MuralhaDigital/IntegridadeRecepcao', 'U', 1, 222, 'Integridade consulta', 1, 'Muralha-Digital');
END
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/IntegridadeConfig')
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Integridade - configuracao', '/MuralhaDigital/IntegridadeConfig', 'U', 1, 222, 'Integridade config', 1, 'Muralha-Digital');
END

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE href = @tela)
BEGIN
    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    SELECT (SELECT MAX(id_infos) + 1 FROM dbo.sis_menu_infos), m.id_menu,
           '/muralha-digital/assets/images/analise/analise.jpg', 'Integridade das Imagens', @tela,
           'Conferencia SHA-256 das imagens recebidas', 7, 15
    FROM dbo.sis_menu m WHERE m.acao = @tela;
END

INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT g.id_grupo, NULL, m.id_menu
FROM dbo.sis_menu m
CROSS JOIN (SELECT 41 AS id_grupo UNION ALL SELECT 42) g
WHERE m.acao IN (@tela, '/MuralhaDigital/IntegridadeRecepcao')
  AND NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_menu = m.id_menu AND d.id_grupo = g.id_grupo);

INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT 42, NULL, m.id_menu
FROM dbo.sis_menu m
WHERE m.acao = '/MuralhaDigital/IntegridadeConfig'
  AND NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_menu = m.id_menu AND d.id_grupo = 42);

PRINT 'Menus de integridade conferidos';
GO

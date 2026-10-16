-- ============================================================
-- Migração: repositório de logs técnicos e histórico de parâmetros
-- Data: 2026-10-16 (Plano 26, Fase 4 da correção da auditoria)
-- TR: 5.4.7.1-6 (logs técnicos, consulta, histórico de parâmetros) e 5.2.7.2
--
-- Por quê: os logs técnicos já existem no legado (dbo.eventos_csx,
-- dbo.status_conexao, dbo.status_energia, dbo.manutencao, dbo.log_alerta,
-- dbo.log_processos...) e são lidos e normalizados pelo serviço Java
-- muralha.digital.logtecnico.ServicoLogTecnico, sem view nova. Falta onde
-- guardar o valor anterior e o novo de cada parâmetro alterado (5.4.7.6).
--
-- O que este script faz (idempotente; nenhuma tabela existente muda):
--   1. muralha.parametro_historico
--   2. Chaves LOG_TECNICO_* e PARAMETRO_HIST_IGNORAR_CONFIGEQUIP em muralha.config_chave_valor
--   3. Menu da tela Logs Técnicos e do servlet (sis_menu, sis_menu_infos, sis_menu_direitos)
--
-- Executar com sqlcmd -b -f 65001 e somente com autorização humana.
--
-- Rollback:
--   DELETE d FROM dbo.sis_menu_direitos d JOIN dbo.sis_menu m ON m.id_menu = d.id_menu
--     WHERE m.acao IN ('/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp', '/MuralhaDigital/LogTecnico');
--   DELETE FROM dbo.sis_menu_infos WHERE href = '/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp';
--   DELETE FROM dbo.sis_menu WHERE acao IN ('/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp', '/MuralhaDigital/LogTecnico');
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'LOG[_]TECNICO[_]%' OR chave = 'PARAMETRO_HIST_IGNORAR_CONFIGEQUIP';
--   DROP TABLE muralha.parametro_historico;
-- ============================================================

SET NOCOUNT ON;
-- sqlcmd usa QUOTED_IDENTIFIER OFF por padrão; tabelas com índice filtrado exigem ON.
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Histórico de parâmetros ---------------------------------------------
--   tipo_recurso: EQUIPAMENTO (id_recurso = id_local), CONFIG_CHAVE_VALOR (id_recurso = chave), KPI (id_recurso = id)
IF OBJECT_ID('muralha.parametro_historico', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.parametro_historico (
        id             BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_parametro_historico PRIMARY KEY,
        dt             DATETIME2      NOT NULL CONSTRAINT DF_ph_dt DEFAULT SYSDATETIME(),
        tipo_recurso   VARCHAR(40)    NOT NULL,
        id_recurso     VARCHAR(100)   NOT NULL,
        parametro      VARCHAR(200)   NOT NULL,
        valor_anterior NVARCHAR(1000) NULL,   -- nulo = parâmetro não existia
        valor_novo     NVARCHAR(1000) NULL,   -- nulo = parâmetro removido
        id_usuario     INT            NULL,
        login          VARCHAR(50)    NULL,
        ip             VARCHAR(45)    NULL,
        origem         VARCHAR(60)    NULL    -- funcionalidade que alterou (ex.: ConfigEquip, Configuracao de KPIs)
    );
    CREATE INDEX IX_ph_dt ON muralha.parametro_historico (dt);
    CREATE INDEX IX_ph_recurso ON muralha.parametro_historico (tipo_recurso, id_recurso, dt);
    PRINT 'Criada: muralha.parametro_historico';
END
ELSE
    PRINT 'Ja existe: muralha.parametro_historico';
GO

-- 2. Parâmetros (não sobrescreve valor já ajustado) ----------------------
--   LOG_TECNICO_PERIODO_MAX_DIAS        período máximo das consultas (dias)
--   LOG_TECNICO_PROCESSOS               NENHUM | NAO_FINALIZADOS | TODOS (dbo.log_processos tem uma linha por minuto)
--   LOG_TECNICO_PROCESSO_ATRASO_MIN     minutos sem atualização para um processo contar como não finalizado
--   LOG_TECNICO_EXPORTACAO_MAX          linhas máximas do CSV
--   LOG_TECNICO_JANELA_CORRELACAO_MIN   janela (± minutos) da correlação temporal
--   PARAMETRO_HIST_IGNORAR_CONFIGEQUIP  campos do ConfigEquip fora do histórico (separados por vírgula)
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'LOG_TECNICO_PERIODO_MAX_DIAS')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('LOG_TECNICO_PERIODO_MAX_DIAS', '31');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'LOG_TECNICO_PROCESSOS')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('LOG_TECNICO_PROCESSOS', 'NAO_FINALIZADOS');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'LOG_TECNICO_PROCESSO_ATRASO_MIN')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('LOG_TECNICO_PROCESSO_ATRASO_MIN', '30');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'LOG_TECNICO_EXPORTACAO_MAX')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('LOG_TECNICO_EXPORTACAO_MAX', '50000');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'LOG_TECNICO_JANELA_CORRELACAO_MIN')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('LOG_TECNICO_JANELA_CORRELACAO_MIN', '10');
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'PARAMETRO_HIST_IGNORAR_CONFIGEQUIP')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('PARAMETRO_HIST_IGNORAR_CONFIGEQUIP', 'dataHoraConfiguracao');
PRINT 'Parametros LOG_TECNICO_* conferidos';
GO

-- 3. Menus (grupos 41 e 42, como os demais itens de Analise de Dados) ----
DECLARE @id INT;
DECLARE @tela VARCHAR(200) = '/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp';

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = @tela)
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Logs Tecnicos', @tela, 'U', 1, 222, 'Logs Tecnicos', 1, 'Muralha-Digital');
END
IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/LogTecnico')
BEGIN
    SELECT @id = MAX(id_menu) + 1 FROM dbo.sis_menu;
    INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
    VALUES (@id, 'Logs Tecnicos - consulta', '/MuralhaDigital/LogTecnico', 'U', 1, 222, 'Logs Tecnicos consulta', 1, 'Muralha-Digital');
END

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE href = @tela)
BEGIN
    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    SELECT (SELECT MAX(id_infos) + 1 FROM dbo.sis_menu_infos), m.id_menu,
           '/muralha-digital/assets/images/analise/analise.jpg', 'Logs Tecnicos', @tela,
           'Logs tecnicos dos equipamentos, linha do tempo e historico de parametros', 8, 15
    FROM dbo.sis_menu m WHERE m.acao = @tela;
END

INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT g.id_grupo, NULL, m.id_menu
FROM dbo.sis_menu m
CROSS JOIN (SELECT 41 AS id_grupo UNION ALL SELECT 42) g
WHERE m.acao IN (@tela, '/MuralhaDigital/LogTecnico')
  AND NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_menu = m.id_menu AND d.id_grupo = g.id_grupo);

PRINT 'Menus de logs tecnicos conferidos';
GO

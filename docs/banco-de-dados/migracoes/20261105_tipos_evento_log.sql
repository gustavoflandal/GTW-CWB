-- =============================================================================
-- Migracao: tipos de evento configuraveis do Motor de Captura de Logs
-- Data: 2026-11-05 — idempotente. Depois de 20261105_motor_captura_logs.sql.
--
-- Cria dbo.sis_log_tipo_evento com os tipos relevantes (ACESSO, VISUALIZACAO,
-- CADASTRO, ALTERACAO, EXCLUSAO, EXPORTACAO, ERRO), que podem ser ligados/
-- desligados globalmente (valem para TODAS as telas) pela tela de regras.
--
-- Tambem ajusta as regras gerais para nivel 'AUTO', de modo que o motor derive
-- o tipo do evento (cadastro/alteracao/exclusao/visualizacao) a partir da acao,
-- em vez do rotulo generico LEITURA/ALTERACAO.
--
-- ROLLBACK:
--   DROP TABLE dbo.sis_log_tipo_evento;
--   (os niveis das regras podem ser reajustados manualmente se necessario)
-- =============================================================================

SET NOCOUNT ON;

IF NOT EXISTS (SELECT 1 FROM sys.tables t JOIN sys.schemas s ON s.schema_id = t.schema_id
               WHERE s.name = 'dbo' AND t.name = 'sis_log_tipo_evento')
BEGIN
    CREATE TABLE dbo.sis_log_tipo_evento (
        codigo     VARCHAR(20)  NOT NULL CONSTRAINT PK_sis_log_tipo_evento PRIMARY KEY,
        descricao  VARCHAR(100) NOT NULL,
        capturar   BIT          NOT NULL CONSTRAINT DF_slte_capturar DEFAULT 1,
        ordem      INT          NOT NULL CONSTRAINT DF_slte_ordem DEFAULT 100
    );
    PRINT 'Criada: dbo.sis_log_tipo_evento';
END
ELSE
    PRINT 'Ja existe: dbo.sis_log_tipo_evento';
GO

IF NOT EXISTS (SELECT 1 FROM dbo.sis_log_tipo_evento)
BEGIN
    INSERT INTO dbo.sis_log_tipo_evento (codigo, descricao, capturar, ordem) VALUES
        ('ACESSO',       'Acesso a tela (abertura de pagina)',          1, 10),
        ('VISUALIZACAO', 'Visualizacao / consulta de dados',            1, 20),
        ('CADASTRO',     'Cadastro (inclusao de registro)',             1, 30),
        ('ALTERACAO',    'Alteracao (edicao de registro)',              1, 40),
        ('EXCLUSAO',     'Exclusao / inativacao de registro',           1, 50),
        ('EXPORTACAO',   'Exportacao / download de dados',              1, 60),
        ('ERRO',         'Erro de processamento (HTTP 5xx)',            1, 70);
    PRINT 'Seed: tipos de evento inseridos';
END
ELSE
    PRINT 'Seed ignorado: dbo.sis_log_tipo_evento ja possui registros';
GO

-- Regras gerais passam a 'AUTO' para o motor derivar o tipo semantico pela acao.
-- (Nao mexe nas regras de ACESSO: login e abertura de pagina .jsp.)
UPDATE dbo.sis_log_regra
   SET nivel = 'AUTO'
 WHERE capturar = 1
   AND nivel IN ('ALTERACAO', 'LEITURA');
PRINT CONCAT('Regras ajustadas para AUTO: ', @@ROWCOUNT);
GO

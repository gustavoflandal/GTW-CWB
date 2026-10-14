-- =============================================================================
-- DASH-2: Tabelas complementares para editor de dataset
-- Schema: dash
-- Banco: GTW_MURALHA_DEV (SQL Server 2016+)
-- Data: 2026-10-13
-- =============================================================================
-- ROLLBACK:
--   DROP TABLE dash.dataset_filtro_fixo;
--   DROP TABLE dash.dataset_tabela;
-- =============================================================================

-- -----------------------------------------------
-- 1. dataset_tabela — tabelas relacionadas incluidas no dataset
-- -----------------------------------------------
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dash' AND TABLE_NAME = 'dataset_tabela')
CREATE TABLE dash.dataset_tabela (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    id_dataset      INT             NOT NULL,
    id_tabela       INT             NOT NULL,
    alias           VARCHAR(80)     NOT NULL,
    tipo            VARCHAR(10)     NOT NULL DEFAULT 'join',
    ordem           INT             NOT NULL DEFAULT 0,
    CONSTRAINT fk_ds_tabela_dataset FOREIGN KEY (id_dataset)
        REFERENCES dash.dataset(id),
    CONSTRAINT fk_ds_tabela_catalogo FOREIGN KEY (id_tabela)
        REFERENCES dash.catalogo_tabela(id),
    CONSTRAINT chk_ds_tabela_tipo CHECK (tipo IN ('base','join')),
    CONSTRAINT uq_ds_tabela UNIQUE (id_dataset, id_tabela)
);
GO

-- -----------------------------------------------
-- 2. dataset_filtro_fixo — condicoes aplicadas sempre ao dataset
-- -----------------------------------------------
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dash' AND TABLE_NAME = 'dataset_filtro_fixo')
CREATE TABLE dash.dataset_filtro_fixo (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    id_dataset      INT             NOT NULL,
    coluna          VARCHAR(128)    NOT NULL,
    operador        VARCHAR(20)     NOT NULL,
    valor           NVARCHAR(500)   NOT NULL,
    ordem           INT             NOT NULL DEFAULT 0,
    CONSTRAINT fk_filtro_dataset FOREIGN KEY (id_dataset)
        REFERENCES dash.dataset(id),
    CONSTRAINT chk_filtro_operador CHECK (operador IN (
        '=','!=','>','<','>=','<=','IN','NOT IN','BETWEEN','LIKE','IS NULL','IS NOT NULL'
    ))
);
GO

-- Indices
CREATE INDEX ix_ds_tabela_dataset ON dash.dataset_tabela (id_dataset);
CREATE INDEX ix_filtro_dataset ON dash.dataset_filtro_fixo (id_dataset);
GO

-- Seed: vincular tabela base ao dataset existente (Passagens por Equipamento)
DECLARE @id_ds INT = (SELECT TOP 1 id FROM dash.dataset WHERE nome = 'Passagens por Equipamento');
DECLARE @id_tab_pass INT = (SELECT id FROM dash.catalogo_tabela WHERE nome_fisico = 'veiculo_tempo_real');
DECLARE @id_tab_equip INT = (SELECT id FROM dash.catalogo_tabela WHERE nome_fisico = 'local_vigente');

IF @id_ds IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dash.dataset_tabela WHERE id_dataset = @id_ds)
BEGIN
    INSERT INTO dash.dataset_tabela (id_dataset, id_tabela, alias, tipo, ordem)
    VALUES
        (@id_ds, @id_tab_pass, 'veiculo_tempo_real', 'base', 0),
        (@id_ds, @id_tab_equip, 'local_vigente', 'join', 1);
END
GO

PRINT 'DASH-2: Tabelas dataset_tabela e dataset_filtro_fixo criadas.';
GO

-- ============================================================
-- Migracao: Modelador visual de datasets (DASH-2)
-- Data: 2026-10-13
-- Autor: gustavoflandal
-- Descricao: Tabelas complementares para o modelador visual
--   de relacionamentos do Dataset Editor. Permite ao usuario
--   desenhar relacoes customizadas (alem das do catalogo) e
--   salvar o layout do canvas.
-- Rollback:
--   DROP TABLE IF EXISTS dash.dataset_relacao;
--   DROP TABLE IF EXISTS dash.dataset_modelador;
-- ============================================================

BEGIN TRANSACTION;

-- 1) Relacoes customizadas desenhadas pelo usuario no modelador
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dash' AND TABLE_NAME = 'dataset_relacao')
BEGIN
    CREATE TABLE dash.dataset_relacao (
        id              INT IDENTITY(1,1) PRIMARY KEY,
        id_dataset      INT NOT NULL,
        id_tabela_origem  INT NOT NULL,
        id_tabela_destino INT NOT NULL,
        coluna_origem   NVARCHAR(200) NOT NULL,
        coluna_destino  NVARCHAR(200) NOT NULL,
        cardinalidade   VARCHAR(20) NOT NULL DEFAULT 'many_to_one'
            CHECK (cardinalidade IN ('many_to_one','one_to_many','one_to_one')),
        origem          VARCHAR(20) NOT NULL DEFAULT 'usuario'
            CHECK (origem IN ('banco','catalogo','usuario')),
        ativo           BIT NOT NULL DEFAULT 1,
        data_criacao    DATETIME NOT NULL DEFAULT GETDATE(),
        CONSTRAINT FK_dataset_relacao_dataset FOREIGN KEY (id_dataset) REFERENCES dash.dataset(id),
        CONSTRAINT FK_dataset_relacao_tab_orig FOREIGN KEY (id_tabela_origem) REFERENCES dash.catalogo_tabela(id),
        CONSTRAINT FK_dataset_relacao_tab_dest FOREIGN KEY (id_tabela_destino) REFERENCES dash.catalogo_tabela(id)
    );
    PRINT 'Criada tabela dash.dataset_relacao';
END;

-- 2) Layout do canvas do modelador (complementar a dataset, sem ALTER TABLE)
IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dash' AND TABLE_NAME = 'dataset_modelador')
BEGIN
    CREATE TABLE dash.dataset_modelador (
        id_dataset      INT NOT NULL PRIMARY KEY,
        layout_json     NVARCHAR(MAX) NOT NULL DEFAULT '{}' CHECK (ISJSON(layout_json) = 1),
        data_criacao    DATETIME NOT NULL DEFAULT GETDATE(),
        data_modificacao DATETIME NULL,
        CONSTRAINT FK_dataset_modelador_dataset FOREIGN KEY (id_dataset) REFERENCES dash.dataset(id)
    );
    PRINT 'Criada tabela dash.dataset_modelador';
END;

COMMIT;

-- Verificacao
SELECT 'dataset_relacao' AS tabela, COUNT(*) AS colunas
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'dash' AND TABLE_NAME = 'dataset_relacao';

SELECT 'dataset_modelador' AS tabela, COUNT(*) AS colunas
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'dash' AND TABLE_NAME = 'dataset_modelador';

PRINT '=== Modelador visual: tabelas criadas com sucesso ===';

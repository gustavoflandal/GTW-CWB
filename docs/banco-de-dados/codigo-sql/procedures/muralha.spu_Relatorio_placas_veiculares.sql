
/*
===============================================================================
Nome da Procedure: [muralha].[spu_relatorio_placas_veiculares]

Descrição: Consulta placas veiculares com leituras incorretas e corrigidas pelos 
    operadores, exibindo identificação do operador, placa anterior (original), 
    nova placa (digitada), data e hora da correção. Considera tanto correções
    registradas em `muralha.veiculo_tempo_real_correcao` quanto alterações
    manuais em `muralha.veiculo_tempo_real`.

Parâmetros:
    @placas dbo.udtt_ListaPlacas READONLY - Tabela com placa(s) veicular(es) (original ou digitada)
                        Utilize o tipo table-valued `dbo.udtt_ListaPlacas` (char(7) ou similar)
                        Hífens e espaços serão desconsiderados na comparação
                        Se NULL ou vazio, retorna TODAS as placas do período
    @dataInicio DATETIME - Data de início do período de consulta
    @dataFim DATETIME - Data de fim do período de consulta

Autor: Thiago Guilsotti
Data de Criação: 28/09/2025

Dependências:

IF TYPE_ID(N'dbo.udtt_ListaPlacas') IS NULL
    CREATE TYPE dbo.udtt_ListaPlacas AS TABLE (placa NVARCHAR(7) NULL);

Exemplo de uso:
    -- Com placas específicas
    DECLARE @placas dbo.udtt_ListaPlacas;
    INSERT INTO @placas (placa)
    VALUES ('OMC0990'), ('TML6H55'), ('HAI5476');

    EXEC muralha.spu_relatorio_placas_veiculares 
        @placas,
        '2025-01-01',
        '2025-12-31';

    -- Para TODAS as placas do período (placas vazias)
    DECLARE @placasVazias dbo.udtt_ListaPlacas;
    
    EXEC muralha.spu_relatorio_placas_veiculares 
        @placasVazias,
        '2025-01-01',
        '2025-12-31';
===============================================================================
*/
CREATE     PROCEDURE [muralha].[spu_Relatorio_placas_veiculares]
(
    @placas dbo.udtt_ListaPlacas READONLY,
    @dataInicio DATETIME,
    @dataFim DATETIME
)
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

    -- Copiar parâmetros para variáveis locais
    DECLARE @dataInicioLocal DATETIME = @dataInicio;
    DECLARE @dataFimLocal DATETIME = @dataFim;

    -- Declarar variável para contagem de placas de entrada
    DECLARE @qtdPlacas INT;
    SELECT @qtdPlacas = COUNT(DISTINCT placa) FROM @placas WHERE placa IS NOT NULL;

    -- Criar tabela temporária para placas (mesmo se vazia)
    CREATE TABLE #PlacasLocal (
        placa NVARCHAR(7) NOT NULL,
        PRIMARY KEY (placa)
    );

    -- Inserir placas apenas se houver placas válidas
    IF @qtdPlacas > 0
    BEGIN
        INSERT INTO #PlacasLocal (placa)
        SELECT DISTINCT UPPER(REPLACE(placa, '-', '')) 
        FROM @placas
        WHERE placa IS NOT NULL 
            AND LEN(LTRIM(RTRIM(placa))) > 0;
    END;

    ;WITH PlacasEntrada AS (
        SELECT placa FROM #PlacasLocal
    ),
    Correcoes AS (
        SELECT 
            MVTR.id_usuario,
            MVTR.placa_original,
            MVTR.placa_digitada,
            MVTR.data
        FROM muralha.veiculo_tempo_real_correcao MVTR WITH (NOLOCK)
        WHERE MVTR.data BETWEEN @dataInicioLocal AND @dataFimLocal
			AND (@qtdPlacas = 0 OR 
				(EXISTS (SELECT 1 FROM PlacasEntrada P WHERE P.placa = MVTR.placa_original)
				OR EXISTS (SELECT 1 FROM PlacasEntrada P WHERE P.placa = MVTR.placa_digitada)))
        UNION ALL
        SELECT
            MVTR.id_usuario_alt AS id_usuario,
            MVTR.dados_alt_orig AS placa_original,
            MVTR.placa AS placa_digitada,
            MVTR.data_alt AS data
        FROM muralha.veiculo_tempo_real MVTR WITH (NOLOCK)
        WHERE MVTR.id_usuario_alt IS NOT NULL
            AND MVTR.dados_alt_orig IS NOT NULL
            AND MVTR.data_alt BETWEEN @dataInicioLocal AND @dataFimLocal
			AND (@qtdPlacas = 0 OR 
				(EXISTS (SELECT 1 FROM PlacasEntrada P WHERE P.placa = MVTR.placa)
				OR EXISTS (SELECT 1 FROM PlacasEntrada P WHERE P.placa = MVTR.dados_alt_orig)))
    )
    SELECT 
        LTRIM(RTRIM(U.nome)) AS usuario,
        C.placa_original,
        C.placa_digitada AS placa_nova,
        C.data
    FROM Correcoes C
    INNER JOIN dbo.sis_usuario U 
        ON C.id_usuario = U.id_usuario
    ORDER BY C.placa_original, 
        C.data DESC
    OPTION (OPTIMIZE FOR UNKNOWN);

    -- Limpeza da tabela temporária
    DROP TABLE #PlacasLocal;
END;

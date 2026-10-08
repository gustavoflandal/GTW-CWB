CREATE   PROCEDURE [muralha].[spu_PassagensSequenciais]
    @data_ini DATE,
    @data_fim DATE,
    @lista    VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;

    -- 1. Tabela temporária para armazenar os IDs da sequência e nomes
    CREATE TABLE #sequencia_locais (
        sequencia INT IDENTITY(1,1),
        id_local INT,
        nome_local VARCHAR(100) -- Ajuste conforme o nome da coluna na sua tabela
    );

    -- 2. Popular a tabela com os IDs na ordem fornecida
    INSERT INTO #sequencia_locais (id_local)
    SELECT CAST(LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) AS INT)
    FROM (
        SELECT CAST('<M>' + REPLACE(@lista, ',', '</M><M>') + '</M>' AS XML) AS Data
    ) AS A
    CROSS APPLY Data.nodes('/M') AS Split(a)
    WHERE LTRIM(RTRIM(Split.a.value('.', 'VARCHAR(100)'))) <> '';

    -- Atualizar com os nomes (AJUSTE A TABELA 'muralha.local' SE NECESSÁRIO)
    UPDATE sl
    SET sl.nome_local = l.nome -- Verifique se a coluna de nome é 'nome' ou 'descricao'
    FROM #sequencia_locais sl
    INNER JOIN dbo.local l ON sl.id_local = l.id_local;

    -- 3. Verificar quantidade de locais
    DECLARE @qtd_locais INT;
    SELECT @qtd_locais = COUNT(*) FROM #sequencia_locais;

    IF @qtd_locais < 2 -- Mínimo para considerar sequência
    BEGIN
        SELECT 'Itinerário incompleto' AS nome_itinerario, 0 AS total_passagens, 0 AS total_placas_distintas;
        RETURN;
    END

    -- 4. Buscar passagens
    CREATE TABLE #passagens (
        placa VARCHAR(7),
        data_passagem DATE,
        hora_passagem DATETIME,
        id_local INT,
        sequencia INT
    );

    INSERT INTO #passagens (placa, data_passagem, hora_passagem, id_local, sequencia)
    SELECT vtr.placa, CAST(vtr.data AS DATE), vtr.data, vtr.id_local, sl.sequencia
    FROM muralha.veiculo_tempo_real vtr (NOLOCK)
    INNER JOIN #sequencia_locais sl ON sl.id_local = vtr.id_local
    WHERE vtr.data >= @data_ini AND vtr.data < DATEADD(DAY, 1, @data_fim);

    -- 5. Validar Sequência e Itinerário Completo
    -- Filtramos placas que passaram em todos os locais da lista na ordem correta
    WITH primeira_passagem_por_local AS (
        SELECT placa, data_passagem, id_local, sequencia, hora_passagem,
               ROW_NUMBER() OVER (PARTITION BY placa, data_passagem, id_local ORDER BY hora_passagem ASC) AS rn
        FROM #passagens
    ),
    passagens_ordenadas AS (
        SELECT placa, data_passagem, sequencia,
               ROW_NUMBER() OVER (PARTITION BY placa, data_passagem ORDER BY sequencia ASC, hora_passagem ASC) AS ordem_real
        FROM primeira_passagem_por_local
        WHERE rn = 1
    ),
    itinerarios_validos AS (
        SELECT placa, data_passagem
        FROM passagens_ordenadas
        GROUP BY placa, data_passagem
        HAVING COUNT(DISTINCT sequencia) = @qtd_locais -- GARANTE QUE PASSOU EM TODOS
           AND SUM(CASE WHEN ordem_real = sequencia THEN 1 ELSE 0 END) = @qtd_locais -- GARANTE A ORDEM
    )
    SELECT * INTO #placas_validas FROM itinerarios_validos;

    -- 6. Retorno final
    DECLARE @nomes_locais VARCHAR(MAX);
    SELECT @nomes_locais = STUFF((
        SELECT ' -> ' + rtrim(nome_local)
        FROM #sequencia_locais
        ORDER BY sequencia
        FOR XML PATH(''), TYPE).value('.', 'VARCHAR(MAX)'), 1, 4, '');

    SELECT 
        @nomes_locais AS rota,
        COUNT(*) AS total_passagens,
        COUNT(DISTINCT placa) AS placas_distintas
    FROM #placas_validas;

    -- Limpeza
    DROP TABLE #sequencia_locais;
    DROP TABLE #passagens;
    DROP TABLE #placas_validas;
END

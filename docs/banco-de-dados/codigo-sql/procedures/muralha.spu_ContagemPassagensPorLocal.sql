CREATE   PROCEDURE [muralha].[spu_ContagemPassagensPorLocal]
    @data_ini  DATETIME2(0),
    @data_fim  DATETIME2(0),
    @lista     NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;

    -- ── Validações básicas ────────────────────────────────────────────────────
    IF @data_ini IS NULL OR @data_fim IS NULL
    BEGIN
        RAISERROR('@data_ini e @data_fim são obrigatórios.', 16, 1);
        RETURN;
    END;

    IF @data_ini > @data_fim
    BEGIN
        RAISERROR('@data_ini não pode ser posterior a @data_fim.', 16, 1);
        RETURN;
    END;

    IF @lista IS NULL OR LTRIM(RTRIM(@lista)) = ''
    BEGIN
        RAISERROR('@lista não pode ser nulo ou vazio.', 16, 1);
        RETURN;
    END;

    -- ── Carrega a lista de id_local em tabela temporária ─────────────────────
    CREATE TABLE #locais (id_local INT NOT NULL PRIMARY KEY);

    INSERT INTO #locais (id_local)
    SELECT DISTINCT CAST(LTRIM(RTRIM(n.v.value('.', 'NVARCHAR(20)'))) AS INT)
    FROM (
        SELECT CAST('<v>' + REPLACE(@lista, ',', '</v><v>') + '</v>' AS XML) AS x
    ) AS d
    CROSS APPLY d.x.nodes('/v') AS n(v)
    WHERE ISNUMERIC(LTRIM(RTRIM(n.v.value('.', 'NVARCHAR(20)')))) = 1;

    IF NOT EXISTS (SELECT 1 FROM #locais)
    BEGIN
        RAISERROR('@lista não contém nenhum id_local numérico válido.', 16, 1);
        RETURN;
    END;

    -- ── Resultset 1: Contagem de passagens por id_local ───────────────────────
    SELECT
        vtr.id_local,
        COUNT(*) AS total_passagens
        --COUNT(DISTINCT NULLIF(LTRIM(RTRIM(vtr.placa)), '')) AS total_placas_distintas
    FROM  muralha.veiculo_tempo_real vtr WITH (NOLOCK)
    INNER JOIN #locais l ON l.id_local = vtr.id_local
    WHERE vtr.data BETWEEN @data_ini AND @data_fim
    GROUP BY vtr.id_local
    ORDER BY vtr.id_local;

    -- ── Resultset 2: Placas em comum e volume de passagens ────────────────────
    -- CTE: Conta quantas vezes cada placa passou em cada local
    ;WITH PassagensPorPlaca AS (
        SELECT 
            id_local, 
            LTRIM(RTRIM(placa)) AS placa, 
            COUNT(*) AS qtd_passagens
        FROM muralha.veiculo_tempo_real WITH (NOLOCK)
        WHERE data BETWEEN @data_ini AND @data_fim
          AND id_local IN (SELECT id_local FROM #locais)
          AND LTRIM(RTRIM(placa)) <> ''
        GROUP BY id_local, LTRIM(RTRIM(placa))
    )
    SELECT
        p1.id_local AS id_local_a,
        p2.id_local AS id_local_b,
        SUM(p1.qtd_passagens + p2.qtd_passagens) AS total_passagens,
        COUNT(p1.placa) AS total_placas_distintas
    FROM PassagensPorPlaca p1
    INNER JOIN PassagensPorPlaca p2 ON p1.placa = p2.placa 
                                   AND p2.id_local > p1.id_local
    GROUP BY p1.id_local, p2.id_local
    ORDER BY total_passagens DESC, p1.id_local, p2.id_local;

    DROP TABLE #locais;
END;

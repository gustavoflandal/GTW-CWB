CREATE   PROCEDURE [muralha].[spu_getEvolucaoTemporalInfracoesAlertas]
    @Data_Ini      DATETIME,
    @Data_Fim      DATETIME,
    @Id_Local      INT,
    @Id_Municipio  INT,
    @Id_Regiao     TINYINT
AS
BEGIN
    SET NOCOUNT ON;

    ------------------------------------------------------------
    -- 1. VALIDAÇÃO DO PERÍODO
    ------------------------------------------------------------
    IF @Data_Ini IS NULL OR @Data_Fim IS NULL
    BEGIN
        THROW 50001, 'Informe as datas inicial e final.', 1;
    END;

    IF CAST(@Data_Ini AS DATE) > CAST(@Data_Fim AS DATE)
    BEGIN
        THROW 50002, 'A data inicial não pode ser maior que a final.', 1;
    END;

    ------------------------------------------------------------
    -- 2. EQUIPAMENTOS
    -- Mesma regra da spu_getComparativoFluxoInfracao
    ------------------------------------------------------------
    DECLARE @temp_equipamentos TABLE
    (
        id_local INT PRIMARY KEY
    );

    INSERT INTO @temp_equipamentos (id_local)
    SELECT DISTINCT
        lv.id_local
    FROM local_vigente lv
    WHERE lv.desativado = 0
      AND lv.id_localidade = ISNULL(@Id_Municipio, lv.id_localidade)
      AND lv.id_regiao = ISNULL(@Id_Regiao, lv.id_regiao)
      AND lv.id_local = ISNULL(@Id_Local, lv.id_local);

    ------------------------------------------------------------
    -- 3. DATAS DO PERÍODO
    ------------------------------------------------------------
    DECLARE @temp_datas TABLE
    (
        data DATE PRIMARY KEY
    );

    INSERT INTO @temp_datas (data)
    SELECT DISTINCT
        CAST(d.Data AS DATE)
    FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d;

    ------------------------------------------------------------
    -- 4. DADOS DIÁRIOS
    ------------------------------------------------------------
    DECLARE @temp_dados TABLE
    (
        id_label   TINYINT,
        data       DATE,
        quantidade BIGINT
    );

    ------------------------------------------------------------
    -- 4.1 INFRAÇÕES
    -- Mesmo relacionamento da procedure original
    ------------------------------------------------------------
    INSERT INTO @temp_dados
    (
        id_label,
        data,
        quantidade
    )
    SELECT
        1 AS id_label,
        CAST(i.data AS DATE) AS data,
        COUNT_BIG(*) AS quantidade
    FROM infracao i
    INNER JOIN veiculo v
        ON v.id_veiculo = i.id_veiculo
    INNER JOIN @temp_equipamentos lv
        ON lv.id_local = i.id_local
    WHERE CAST(i.data AS DATE)
          BETWEEN CAST(@Data_Ini AS DATE)
              AND CAST(@Data_Fim AS DATE)
      AND i.id_processo NOT IN (99, 98)
    GROUP BY
        CAST(i.data AS DATE);

    ------------------------------------------------------------
    -- 4.2 ALERTAS
    --
    -- Sem filtros geográficos:
    --     Conta todos os alertas.
    --
    -- Com filtros geográficos:
    --     Conta alertas vinculados aos equipamentos.
    --
    -- EXISTS evita duplicação de alertas.
    ------------------------------------------------------------
    INSERT INTO @temp_dados
    (
        id_label,
        data,
        quantidade
    )
    SELECT
        2 AS id_label,
        CAST(a.data AS DATE) AS data,
        COUNT_BIG(*) AS quantidade
    FROM muralha.alerta a
    WHERE CAST(a.data AS DATE)
          BETWEEN CAST(@Data_Ini AS DATE)
              AND CAST(@Data_Fim AS DATE)
      AND
      (
          (
              @Id_Local IS NULL
              AND @Id_Municipio IS NULL
              AND @Id_Regiao IS NULL
          )
          OR EXISTS
          (
              SELECT 1
              FROM muralha.alerta_veiculo av
              INNER JOIN muralha.veiculo_tempo_real vtr
                  ON vtr.id = av.id_veiculo_tempo_real
              INNER JOIN @temp_equipamentos lv
                  ON lv.id_local = vtr.id_local
              WHERE av.id_alerta = a.id
          )
      )
    GROUP BY
        CAST(a.data AS DATE);

    ------------------------------------------------------------
    -- 5. CONSOLIDAÇÃO
    -- Garante zero nos dias sem registros.
    ------------------------------------------------------------
    SELECT
        s.label_dataset,
        d.data,
        ISNULL(td.quantidade, 0) AS quantidade
    INTO #DadosConsolidados
    FROM
    (
        SELECT
            1 AS id_label,
            'Infrações' AS label_dataset

        UNION ALL

        SELECT
            2 AS id_label,
            'Alertas' AS label_dataset
    ) s
    CROSS JOIN @temp_datas d
    LEFT JOIN @temp_dados td
        ON td.id_label = s.id_label
       AND td.data = d.data;

    ------------------------------------------------------------
    -- 6. COLUNAS DINÂMICAS
    ------------------------------------------------------------
    DECLARE @Colunas NVARCHAR(MAX);
    DECLARE @ColunasFormatadas NVARCHAR(MAX);
    DECLARE @SQL NVARCHAR(MAX);

    SELECT
        @Colunas = STUFF
        (
            (
                SELECT
                    ',' + QUOTENAME(CONVERT(VARCHAR(8), d.data, 3))
                FROM @temp_datas d
                ORDER BY d.data
                FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)'),
            1,
            1,
            ''
        );

    SELECT
        @ColunasFormatadas = STUFF
        (
            (
                SELECT
                    ',ISNULL('
                    + QUOTENAME(CONVERT(VARCHAR(8), d.data, 3))
                    + ', 0) AS '
                    + QUOTENAME(CONVERT(VARCHAR(8), d.data, 3))
                FROM @temp_datas d
                ORDER BY d.data
                FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)'),
            1,
            1,
            ''
        );

    ------------------------------------------------------------
    -- 7. RETORNO PARA O GRAFICO JAVA
    ------------------------------------------------------------
    IF @Colunas IS NULL OR @Colunas = ''
    BEGIN
        SELECT
            CAST(NULL AS VARCHAR(20)) AS label_dataset
        WHERE 1 = 0;

        RETURN;
    END;

    SET @SQL = N'
        SELECT
            label_dataset,
            ' + @ColunasFormatadas + N'
        FROM
        (
            SELECT
                label_dataset,
                CONVERT(VARCHAR(8), data, 3) AS data_label,
                quantidade
            FROM #DadosConsolidados
        ) AS origem
        PIVOT
        (
            SUM(quantidade)
            FOR data_label IN (' + @Colunas + N')
        ) AS resultado
        ORDER BY
            CASE label_dataset
                WHEN ''Infrações'' THEN 1
                WHEN ''Alertas'' THEN 2
                ELSE 3
            END;
    ';

    EXEC sys.sp_executesql @SQL;

    DROP TABLE #DadosConsolidados;
END;
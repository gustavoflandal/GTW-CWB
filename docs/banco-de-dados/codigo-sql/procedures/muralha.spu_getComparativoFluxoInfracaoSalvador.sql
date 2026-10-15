CREATE   PROCEDURE [muralha].[spu_getComparativoFluxoInfracaoSalvador]
    @Data_Ini DATETIME,
    @Data_Fim DATETIME,
    @Id_Local INT = NULL,
    @Id_Municipio INT = NULL,
    @Id_Regiao TINYINT = NULL,
    @Hora_Ini INT = 0,
    @Hora_Fim INT = 23,
    @Id_Classe CHAR(1) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Normaliza os filtros opcionais.
    SET @Id_Local = NULLIF(@Id_Local, 0);
    SET @Id_Municipio = NULLIF(@Id_Municipio, 0);
    SET @Id_Regiao = NULLIF(@Id_Regiao, 0);

    SET @Id_Classe = NULLIF(
        UPPER(LTRIM(RTRIM(@Id_Classe))),
        ''
    );

    -- Valida o periodo e o intervalo horario.
    IF @Data_Ini IS NULL
       OR @Data_Fim IS NULL
       OR @Data_Ini > @Data_Fim
       OR @Hora_Ini IS NULL
       OR @Hora_Fim IS NULL
       OR @Hora_Ini < 0
       OR @Hora_Ini > 23
       OR @Hora_Fim < 0
       OR @Hora_Fim > 23
       OR @Hora_Ini > @Hora_Fim
    BEGIN
        THROW 50001,
              'Parametros de periodo ou horario invalidos.',
              1;
    END;

    -- Define o periodo de consulta por dias completos.
    DECLARE @DiaInicial DATE =
        CAST(@Data_Ini AS DATE);

    DECLARE @DiaFinal DATE =
        CAST(@Data_Fim AS DATE);

    DECLARE @FimExclusivo DATETIME =
        DATEADD(
            DAY,
            1,
            CAST(@DiaFinal AS DATETIME)
        );

    -- Seleciona os equipamentos ativos.
    CREATE TABLE #equipamentos (
        id_local INT NOT NULL PRIMARY KEY
    );

    INSERT INTO #equipamentos (
        id_local
    )
    SELECT DISTINCT
        lv.id_local
    FROM dbo.local_vigente lv
    WHERE lv.desativado = 0
      AND (
          @Id_Local IS NULL
          OR lv.id_local = @Id_Local
      )
      AND (
          @Id_Municipio IS NULL
          OR lv.id_localidade = @Id_Municipio
      )
      AND (
          @Id_Regiao IS NULL
          OR lv.id_regiao = @Id_Regiao
      );

    -- Monta o calendario e os totais diarios.
    ;WITH Dias AS (
        SELECT
            @DiaInicial AS data

        UNION ALL

        SELECT
            DATEADD(DAY, 1, data)
        FROM Dias
        WHERE data < @DiaFinal
    ),

    Fluxo AS (
        SELECT
            vp.data,
            SUM(
                CAST(vp.trafego AS BIGINT)
            ) AS fluxo

        FROM dbo.veiculo_sumarizado vp

        INNER JOIN #equipamentos eq
            ON eq.id_local = vp.id_local

        WHERE vp.data >= @DiaInicial
          AND vp.data <= @DiaFinal

          -- Filtra a hora de cada dia.
          AND vp.hora BETWEEN @Hora_Ini AND @Hora_Fim

          -- Filtra a classificacao veicular.
          AND (
              @Id_Classe IS NULL
              OR vp.id_classe = @Id_Classe
          )

        GROUP BY
            vp.data
    ),

    Infracoes AS (
        SELECT
            CAST(i.data AS DATE) AS data,
            COUNT_BIG(*) AS infracoes

        FROM dbo.infracao i

        INNER JOIN dbo.veiculo v
            ON v.id_veiculo = i.id_veiculo

        INNER JOIN #equipamentos eq
            ON eq.id_local = i.id_local

        WHERE i.data >= @DiaInicial
          AND i.data < @FimExclusivo

          -- Filtra a hora de cada dia.
          AND DATEPART(HOUR, i.data)
              BETWEEN @Hora_Ini AND @Hora_Fim

          -- Exclui os processos desconsiderados.
          AND i.id_processo NOT IN (98, 99)

          -- Filtra a classificacao veicular.
          AND (
              @Id_Classe IS NULL
              OR v.id_classe = @Id_Classe
          )

        GROUP BY
            CAST(i.data AS DATE)
    )

    -- Retorna os indicadores diarios.
    SELECT
        CONVERT(
            VARCHAR(8),
            d.data,
            3
        ) AS data,

        ISNULL(
            f.fluxo,
            0
        ) AS fluxo,

        ISNULL(
            i.infracoes,
            0
        ) AS infracoes

    FROM Dias d

    LEFT JOIN Fluxo f
        ON f.data = d.data

    LEFT JOIN Infracoes i
        ON i.data = d.data

    ORDER BY
        d.data

    OPTION (MAXRECURSION 0);

END;

CREATE   PROCEDURE [muralha].[spu_getDistribuicaoInfracoesPorEnquadramento]
    @Data_Ini DATETIME,
    @Data_Fim DATETIME,
    @Id_Local INT = NULL,
    @Id_Municipio INT = NULL,
    @Id_Regiao TINYINT = NULL,
    @Hora_Ini INT = 0,
    @Hora_Fim INT = 23,
    @Id_Classe CHAR(1) = NULL,
    @Id_Enquadramento INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SET @Id_Local = NULLIF(@Id_Local, 0);
    SET @Id_Municipio = NULLIF(@Id_Municipio, 0);
    SET @Id_Regiao = NULLIF(@Id_Regiao, 0);
    SET @Id_Enquadramento = NULLIF(@Id_Enquadramento, 0);

    SET @Id_Classe = NULLIF(
        UPPER(LTRIM(RTRIM(@Id_Classe))),
        ''
    );

    IF @Data_Ini IS NULL
       OR @Data_Fim IS NULL
       OR CAST(@Data_Ini AS DATE) > CAST(@Data_Fim AS DATE)
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

    DECLARE @DiaInicial DATE = CAST(@Data_Ini AS DATE);

    DECLARE @FimExclusivo DATETIME =
        DATEADD(
            DAY,
            1,
            CAST(CAST(@Data_Fim AS DATE) AS DATETIME)
        );

    -- Equipamentos ativos que atendem aos filtros.
    CREATE TABLE #equipamentos
    (
        id_local INT NOT NULL PRIMARY KEY
    );

    INSERT INTO #equipamentos (id_local)
    SELECT DISTINCT
        lv.id_local
    FROM dbo.local_vigente lv
    WHERE lv.desativado = 0
      AND (@Id_Local IS NULL OR lv.id_local = @Id_Local)
      AND (@Id_Municipio IS NULL OR lv.id_localidade = @Id_Municipio)
      AND (@Id_Regiao IS NULL OR lv.id_regiao = @Id_Regiao);

    -- Quantidade de infrações por enquadramento.
    SELECT
        i.id_enquadramento,
        COALESCE(
            NULLIF(LTRIM(RTRIM(e.descricao)), ''),
            'Enquadramento sem descricao'
        ) AS descricao,
        COUNT_BIG(*) AS quantidade

    FROM dbo.infracao i

    INNER JOIN dbo.veiculo v
        ON v.id_veiculo = i.id_veiculo

    INNER JOIN #equipamentos eq
        ON eq.id_local = i.id_local

    LEFT JOIN dbo.enquadramento e
        ON e.id_enquadramento = i.id_enquadramento

    WHERE i.data >= @DiaInicial
      AND i.data < @FimExclusivo

      AND DATEPART(HOUR, i.data)
          BETWEEN @Hora_Ini AND @Hora_Fim

      AND i.id_processo NOT IN (98, 99)

      AND (
          @Id_Classe IS NULL
          OR v.id_classe = @Id_Classe
      )

      AND (
          @Id_Enquadramento IS NULL
          OR i.id_enquadramento = @Id_Enquadramento
      )

    GROUP BY
        i.id_enquadramento,
        e.descricao

    ORDER BY
        quantidade DESC,
        i.id_enquadramento ASC;
END;

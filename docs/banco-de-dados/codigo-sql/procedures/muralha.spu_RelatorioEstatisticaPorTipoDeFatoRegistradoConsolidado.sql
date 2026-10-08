
CREATE     PROCEDURE [muralha].[spu_RelatorioEstatisticaPorTipoDeFatoRegistradoConsolidado]

    @DataInicial DATETIME,
    @DataFinal DATETIME
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;
    SET LANGUAGE Portuguese;

    SET @DataFinal = DATEADD(dd, 1, DATEDIFF(dd, 0, @DataFinal));

    SELECT
        tao.tipo AS TipoFato,
        CAST(a.data AS DATE) AS DataFato,
        DATENAME(weekday, a.data) AS DiaDaSemana,
        COUNT(*) AS Quantidade,
        CAST(MIN(a.data) AS TIME) AS HoraDaPrimeiraOcorrencia,
        CAST(MAX(a.data) AS TIME) AS HoraDaUltimaOcorrencia,
        CONVERT(varchar(8), DATEADD(second, DATEDIFF(second, MIN(a.data), MAX(a.data)), 0), 108) AS PeriodoDeTempo
    FROM
        muralha.alerta a
    INNER JOIN
        muralha.tipo_alerta_ocorrencia tao ON a.id_tipo_alerta_ocorrencia = tao.id
    WHERE
        a.data BETWEEN @DataInicial AND @DataFinal
    GROUP BY
        tao.tipo,
        CAST(a.data AS DATE),
        DATENAME(weekday, a.data)
    ORDER BY
		TipoFato,
        DataFato;
END

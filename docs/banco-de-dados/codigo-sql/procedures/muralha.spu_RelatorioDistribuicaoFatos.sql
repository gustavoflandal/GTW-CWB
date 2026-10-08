
-- =============================================
-- Autor: Gustavo Landal
-- Data de Criação: 2024-08-25
-- Descrição: Fornece a contagem de fatos (alertas) registrados,
--            agrupados por tipo e filtrados por um período.
--            Inclui a data da primeira/última ocorrência e o
--            percentual de cada tipo em relação ao total.
--
-- Alteração: Permite que os parâmetros de data sejam NULL ou ''.
-- =============================================
CREATE     PROCEDURE [muralha].[spu_RelatorioDistribuicaoFatos]
    @DataInicial DATE = NULL,
    @DataFinal DATE = NULL
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

    WITH ContagemPorTipo AS (
        SELECT
            TAO.tipo AS TipoDeFato,
            COUNT(A.id) AS TotalRegistros,
            MIN(A.data) AS DataPrimeiraOcorrencia,
            MAX(A.data) AS DataUltimaOcorrencia,
            CAST(COUNT(A.id) * 100.0 / SUM(COUNT(A.id)) OVER() AS DECIMAL(5, 2)) AS PercentualSobreTotal
        FROM
            muralha.alerta AS A
        INNER JOIN
            muralha.tipo_alerta_ocorrencia AS TAO ON A.id_tipo_alerta_ocorrencia = TAO.id
        WHERE
            (@DataInicial IS NULL OR CAST(A.data AS DATE) >= @DataInicial)
            AND (@DataFinal IS NULL OR CAST(A.data AS DATE) <= @DataFinal)
        GROUP BY
            TAO.tipo
    )
    SELECT
        (
            SELECT
                TipoDeFato,
                TotalRegistros,
                DataPrimeiraOcorrencia,
                DataUltimaOcorrencia,
                PercentualSobreTotal
            FROM
                ContagemPorTipo
            ORDER BY
                TotalRegistros DESC
            FOR JSON PATH
        ) AS JsonData;
END


-- ==================================================================================================
-- Procedure: Relatório de Evolução Semanal nos Registros de Fato
-- Descrição: Mostra a Evolução Semanal do Registro de Ocorrencias de Fato
-- Autor: Gustavo F. Landal
-- Data: 2025-09-06
-- Otimização V1: Desenvolvimento Inicial - Testado
-- ===================================================================================================

CREATE        PROCEDURE [muralha].[spu_RelatorioDeEvolucaoSemanalDeFatos]
    @param1 VARCHAR(4) = NULL,
    @param2 VARCHAR(4) = NULL
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ano INT;
    DECLARE @semana INT = NULL;

    IF LEN(@param1) = 4 AND ISNUMERIC(@param1) = 1
    BEGIN
        SET @ano = CAST(@param1 AS INT);
    END
    ELSE IF LEN(@param1) = 2 AND ISNUMERIC(@param1) = 1
    BEGIN
        SET @semana = CAST(@param1 AS INT);
    END

    IF LEN(@param2) = 4 AND ISNUMERIC(@param2) = 1
    BEGIN
        SET @ano = CAST(@param2 AS INT);
    END
    ELSE IF LEN(@param2) = 2 AND ISNUMERIC(@param2) = 1
    BEGIN
        SET @semana = CAST(@param2 AS INT);
    END
    ;WITH FatosPorSemana AS (
        SELECT
            DATEPART(year, rf.data_criacao) AS Ano,
            DATEPART(week, rf.data_criacao) AS Semana,
            rft.tipo_desc AS TipoFato,
            COUNT(rf.id) AS Quantidade,
            SUM(COUNT(rf.id)) OVER(PARTITION BY DATEPART(year, rf.data_criacao), DATEPART(week, rf.data_criacao)) AS TotalSemanal
        FROM
            muralha.registro_fato AS rf
        JOIN
            muralha.registro_fato_tipo AS rft ON rf.id_tipo = rft.id
        GROUP BY
            DATEPART(year, rf.data_criacao),
            DATEPART(week, rf.data_criacao),
            rft.tipo_desc
    )
    SELECT
        Ano,
        Semana,
        TipoFato,
        Quantidade,
        CAST(ROUND((CAST(Quantidade AS FLOAT) * 100.0 / TotalSemanal), 2) AS numeric(10,2)) AS PercentualParticipacao
    FROM
        FatosPorSemana
    WHERE  
        (@ano IS NULL OR Ano = @ano) AND (@semana IS NULL OR Semana = @semana)
    ORDER BY
        Ano,
        Semana,
        Quantidade DESC;
END

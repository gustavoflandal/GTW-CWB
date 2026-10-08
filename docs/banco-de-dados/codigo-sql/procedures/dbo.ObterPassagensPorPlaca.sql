CREATE   PROCEDURE ObterPassagensPorPlaca
    @Placa VARCHAR(255),
    @DataInicio DATETIME,
    @DataFim DATETIME,
    @IdLocal INT
AS
BEGIN
    CREATE TABLE #Resultado (
        Placa VARCHAR(255),
        id_local INT,
        Nome_Local VARCHAR(255),
        QuantidadePassagens INT,
        PeriodoPassagens VARCHAR(MAX),
        IntervaloHoras DECIMAL(10, 2)
    );

    INSERT INTO #Resultado (Placa, id_local, Nome_Local, QuantidadePassagens, PeriodoPassagens, IntervaloHoras)
    SELECT
        v.placa,
        v.id_local,
        v.nome,
        COUNT(*),
        CONCAT(FORMAT(MIN(v.data), 'dd/MM/yyyy HH:mm:ss'), ' a ', FORMAT(MAX(v.data), 'dd/MM/yyyy HH:mm:ss')),
        DATEDIFF(hour, MIN(v.data), MAX(v.data)) 
    FROM
        (SELECT DISTINCT placa, data, vtr.id_local, lc.nome FROM muralha.veiculo_tempo_real vtr
        join dbo.local lc on vtr.id_local = lc.id_local) AS v
    WHERE
        v.placa = @Placa AND v.data BETWEEN @DataInicio AND @DataFim AND (@IdLocal IS NULL OR @IdLocal = v.id_local)
    GROUP BY
        v.placa, v.id_local, v.nome;

    SELECT
        Placa,
        id_local,
        Nome_Local,
        QuantidadePassagens,
        PeriodoPassagens,
        IntervaloHoras
    FROM
        #Resultado;

    DROP TABLE #Resultado;
END;
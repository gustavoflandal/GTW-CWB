CREATE   PROCEDURE muralha.sp_RelatorioFluxoVeicularDinamico
    @PontoColetaOrigem VARCHAR(50) = NULL,
    @PontoColetaDestino VARCHAR(50) = NULL,
    @DataInicio DATETIME = NULL,
    @DataFim DATETIME = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Tabela temporária para armazenar a primeira e a última passagem de cada veículo
    CREATE TABLE #PassagensVeiculares (
        placa_lida_orig VARCHAR(50),
        placa_lida_destino VARCHAR(50),
        primeira_passagem DATETIME,
        ultima_passagem DATETIME
    );

    -- Insere a primeira passagem de cada veículo no ponto de origem (se fornecido)
    INSERT INTO #PassagensVeiculares (placa_lida_orig, primeira_passagem)
    SELECT
        placa_lida_orig,
        MIN(data)
    FROM muralha.veiculo_tempo_real
    WHERE
        (placa_lida_orig = @PontoColetaOrigem OR @PontoColetaOrigem IS NULL)
        AND (@DataInicio IS NULL OR data >= @DataInicio)
        AND (@DataFim IS NULL OR data <= @DataFim)
    GROUP BY
        placa_lida_orig;

    -- Atualiza a última passagem de cada veículo no ponto de destino (se fornecido)
    UPDATE T1
    SET T1.placa_lida_destino = T2.placa_lida_orig,
        T1.ultima_passagem = T2.data
    FROM #PassagensVeiculares T1
    INNER JOIN muralha.veiculo_tempo_real T2 ON
        T1.placa_lida_orig = T2.placa_lida_orig
    WHERE
        T1.placa_lida_destino IS NULL -- Para evitar atualizações repetidas
        AND (T2.placa_lida_orig = @PontoColetaDestino OR @PontoColetaDestino IS NULL)
        AND (T2.data > T1.primeira_passagem)
        AND (@DataFim IS NULL OR T2.data <= @DataFim);


    -- Seleciona os dados para o relatório, com base nos pontos de origem e destino
    SELECT
        placa_lida_orig AS PontoColetaOrigem,
        placa_lida_destino AS PontoColetaDestino,
        COUNT(DISTINCT placa_lida_orig) AS TotalVeiculos,
        AVG(DATEDIFF(minute, primeira_passagem, ultima_passagem)) AS TempoMedioTransitoMinutos
    FROM #PassagensVeiculares
    WHERE
        ultima_passagem IS NOT NULL
    GROUP BY
        placa_lida_orig, placa_lida_destino;

    -- Limpeza da tabela temporária
    DROP TABLE #PassagensVeiculares;
END;



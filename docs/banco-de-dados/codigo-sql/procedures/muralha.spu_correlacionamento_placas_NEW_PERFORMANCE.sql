create   PROCEDURE [muralha].[spu_correlacionamento_placas_NEW_PERFORMANCE]
    @placa_informada VARCHAR(10),
    @data_pesquisa VARCHAR(10), -- Data informada como string no formato dd/MM/aaaa
    @considerar_antes_depois BIT -- 1 para considerar +/- 3 minutos, 0 para considerar + 3 minutos
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        -- Converter a string da data para o tipo DATE
        DECLARE @data_pesquisa_date DATE;
        SET @data_pesquisa_date = CONVERT(DATE, @data_pesquisa, 103);

        -- Tabela temporária para armazenar as passagens da(s) placa(s) informada(s) na data especificada
        DROP TABLE IF EXISTS #PassagensPlacaInformada;
        CREATE TABLE #PassagensPlacaInformada (
            placa VARCHAR(10),
            data DATETIME,
            id_local INT
        );

        IF @placa_informada IS NOT NULL AND @placa_informada <> ''
        BEGIN
            -- Lógica para uma placa específica
            INSERT INTO #PassagensPlacaInformada (placa, data, id_local)
            SELECT placa, data, id_local
            FROM muralha.veiculo_tempo_real
            WHERE placa = @placa_informada
              AND data >= @data_pesquisa_date
              AND data < DATEADD(day, 1, @data_pesquisa_date);
        END
        ELSE
        BEGIN
            -- Lógica para todas as placas na data informada
            INSERT INTO #PassagensPlacaInformada (placa, data, id_local)
            SELECT vtr.placa, vtr.data, vtr.id_local
            FROM muralha.veiculo_tempo_real vtr
            WHERE vtr.data >= @data_pesquisa_date
              AND vtr.data < DATEADD(day, 1, @data_pesquisa_date);
        END

        -- Tabela temporária para armazenar os correlacionamentos
        DROP TABLE IF EXISTS #Correlacionamentos;
        CREATE TABLE #Correlacionamentos (
            placa_informada VARCHAR(10),
            placa_correlacionada VARCHAR(10),
            id_local INT,
            data_informada DATETIME,
            data_correlacionada DATETIME
        );

        -- Encontrar todas as placas que transitaram pelo mesmo ponto no intervalo de tempo definido
        INSERT INTO #Correlacionamentos (placa_informada, placa_correlacionada, id_local, data_informada, data_correlacionada)
        SELECT
            ppi.placa,
            vtr.placa,
            ppi.id_local,
            ppi.data,
            vtr.data
        FROM #PassagensPlacaInformada ppi
        INNER JOIN muralha.veiculo_tempo_real vtr ON ppi.id_local = vtr.id_local
        WHERE vtr.placa <> ppi.placa
          AND vtr.data >= @data_pesquisa_date
          AND vtr.data < DATEADD(day, 1, @data_pesquisa_date)
          AND (
              (@considerar_antes_depois = 1 AND ABS(DATEDIFF(minute, ppi.data, vtr.data)) <= 3) OR
              (@considerar_antes_depois = 0 AND DATEDIFF(minute, ppi.data, vtr.data) >= 0 AND DATEDIFF(minute, ppi.data, vtr.data) <= 3)
          );

        -- Criar tabela temporária para armazenar os resultados da listagem
        DROP TABLE IF EXISTS #ResultadosCorrelacionamento;
        CREATE TABLE #ResultadosCorrelacionamento (
            placa_informada VARCHAR(10) NOT NULL,
            id_local INT NOT NULL,
            data_passagem_informada DATETIME NOT NULL,
            placa_correlacionada VARCHAR(10) NOT NULL,
            data_passagem_correlacionada DATETIME NOT NULL
        );

        -- Salvar os dados da listagem na tabela temporária
        INSERT INTO #ResultadosCorrelacionamento (
            placa_informada,
            id_local,
            data_passagem_informada,
            placa_correlacionada,
            data_passagem_correlacionada
        )
        SELECT
            ppi.placa AS placa_informada,
            ppi.id_local,
            ppi.data AS data_passagem_informada,
            co.placa_correlacionada,
            co.data_correlacionada AS data_passagem_correlacionada
        FROM #PassagensPlacaInformada ppi
        INNER JOIN #Correlacionamentos co ON ppi.placa = co.placa_informada
                                          AND ppi.id_local = co.id_local
                                          AND (
                                              (@considerar_antes_depois = 1 AND ABS(DATEDIFF(minute, ppi.data, co.data_correlacionada)) <= 3) OR
                                              (@considerar_antes_depois = 0 AND DATEDIFF(minute, ppi.data, co.data_correlacionada) >= 0 AND DATEDIFF(minute, ppi.data, co.data_correlacionada) <= 3)
                                          );

        -- Listar os dados agregados
        SELECT
            placa_informada,
            id_local,
            placa_correlacionada,
            COUNT(*) - 1 AS passagens,
            CASE
                WHEN COUNT(*) - 1 = 2 THEN 'F'
                WHEN COUNT(*) - 1 = 3 THEN 'M'
                WHEN COUNT(*) - 1 >= 4 THEN 'A'
                ELSE ''
            END AS incidencia
        FROM #ResultadosCorrelacionamento
        GROUP BY placa_informada, id_local, placa_correlacionada
        ORDER BY placa_informada, id_local, placa_correlacionada;

        -- Limpar tabelas temporárias
        DROP TABLE IF EXISTS #PassagensPlacaInformada;
        DROP TABLE IF EXISTS #Correlacionamentos;
        DROP TABLE IF EXISTS #ResultadosCorrelacionamento;

    END TRY
    BEGIN CATCH
        -- Tratar erros
        DECLARE @ErrorMessage NVARCHAR(MAX) = ERROR_MESSAGE();
        DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
        DECLARE @ErrorState INT = ERROR_STATE();

        RAISERROR (@ErrorMessage, @ErrorSeverity, @ErrorState);
    END CATCH;
END

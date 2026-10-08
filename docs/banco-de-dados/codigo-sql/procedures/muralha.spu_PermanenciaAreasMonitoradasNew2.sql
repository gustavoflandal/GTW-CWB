CREATE   PROCEDURE [muralha].[spu_PermanenciaAreasMonitoradasNew2]
    @area INT = NULL,
    @placa VARCHAR(8) = NULL,
    @data_inicio DATE,
    @data_final DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Converte strings vazias para NULL
    IF @area = 0 OR @area IS NULL SET @area = NULL;
    IF @placa = '' OR @placa IS NULL SET @placa = NULL;
    
    -- Valida parâmetros de data
    IF @data_inicio IS NULL OR @data_final IS NULL
    BEGIN
        RAISERROR('As datas de início e fim são obrigatórias', 16, 1);
        RETURN;
    END;
    
    -- Ajusta data_final para incluir o dia completo
    DECLARE @data_final_ajustada DATETIME = DATEADD(DAY, 1, @data_final);
    
    -- CTE para obter locais das áreas monitoradas ativas
    WITH LocaisAreasMonitoradas AS (
        SELECT DISTINCT
            eam.id_equipamento AS id_local,
            am.id AS id_area_monitorada,
            am.nome AS nome_area
        FROM muralha.area_monitorada am
        INNER JOIN muralha.equipamentos_area_monitorada eam ON am.id = eam.id_area_monitorada
        WHERE am.deletado = 0
            AND (@area IS NULL OR am.id = @area)
    ),
    -- CTE para passagens em áreas monitoradas
    PassagensAreas AS (
        SELECT 
            vtr.placa,
            vtr.data,
            vtr.id_local,
            lam.id_area_monitorada,
            lam.nome_area,
            ROW_NUMBER() OVER (PARTITION BY vtr.placa ORDER BY vtr.data) AS seq
        FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
        INNER JOIN LocaisAreasMonitoradas lam ON vtr.id_local = lam.id_local
        WHERE vtr.data >= @data_inicio 
            AND vtr.data < @data_final_ajustada
            AND (@placa IS NULL OR vtr.placa = @placa)
    ),
    -- CTE para identificar entradas e saídas
    EntradasSaidas AS (
        SELECT 
            pa.placa,
            pa.id_area_monitorada,
            pa.nome_area,
            pa.data AS data_entrada,
            pa.id_local AS id_local_entrada,
            -- Busca próxima passagem fora da área ou próxima entrada
            (
                SELECT MIN(vtr2.data)
                FROM muralha.veiculo_tempo_real vtr2 WITH (NOLOCK)
                WHERE vtr2.placa = pa.placa
                    AND vtr2.data > pa.data
                    AND vtr2.data < @data_final_ajustada
                    AND NOT EXISTS (
                        SELECT 1 
                        FROM LocaisAreasMonitoradas lam2 
                        WHERE lam2.id_local = vtr2.id_local 
                            AND lam2.id_area_monitorada = pa.id_area_monitorada
                    )
            ) AS data_saida
        FROM PassagensAreas pa
    ),
    -- CTE para calcular permanências válidas
    Permanencias AS (
        SELECT 
            placa,
            id_area_monitorada,
            nome_area,
            data_entrada,
            data_saida,
            CASE 
                WHEN data_saida IS NOT NULL 
                THEN DATEDIFF(SECOND, data_entrada, data_saida)
                ELSE NULL
            END AS permanencia_segundos
        FROM EntradasSaidas
        WHERE data_saida IS NOT NULL
    ),
    -- CTE para contagem de passagens
    ContagemPassagens AS (
        SELECT 
            id_area_monitorada,
            nome_area,
            placa,
            COUNT(*) AS total_passagens
        FROM PassagensAreas
        GROUP BY id_area_monitorada, nome_area, placa
    )
    -- Resultado final consolidado
    SELECT 
        cp.id_area_monitorada,
        cp.nome_area,
        COUNT(DISTINCT cp.placa) AS total_veiculos,
        SUM(cp.total_passagens) AS total_passagens,
        COUNT(p.permanencia_segundos) AS total_permanencias,
        -- Tempo médio de permanência formatado
        CASE 
            WHEN AVG(CAST(p.permanencia_segundos AS BIGINT)) IS NOT NULL THEN
                CAST(AVG(CAST(p.permanencia_segundos AS BIGINT)) / 86400 AS VARCHAR(10)) + 'd ' +
                RIGHT('0' + CAST((AVG(CAST(p.permanencia_segundos AS BIGINT)) % 86400) / 3600 AS VARCHAR(2)), 2) + 'h ' +
                RIGHT('0' + CAST((AVG(CAST(p.permanencia_segundos AS BIGINT)) % 3600) / 60 AS VARCHAR(2)), 2) + 'm ' +
                RIGHT('0' + CAST(AVG(CAST(p.permanencia_segundos AS BIGINT)) % 60 AS VARCHAR(2)), 2) + 's'
            ELSE '00d 00h 00m 00s'
        END AS tempo_medio_permanencia,
        -- Tempo mínimo de permanência
        CASE 
            WHEN MIN(p.permanencia_segundos) IS NOT NULL THEN
                CAST(MIN(p.permanencia_segundos) / 86400 AS VARCHAR(10)) + 'd ' +
                RIGHT('0' + CAST((MIN(p.permanencia_segundos) % 86400) / 3600 AS VARCHAR(2)), 2) + 'h ' +
                RIGHT('0' + CAST((MIN(p.permanencia_segundos) % 3600) / 60 AS VARCHAR(2)), 2) + 'm ' +
                RIGHT('0' + CAST(MIN(p.permanencia_segundos) % 60 AS VARCHAR(2)), 2) + 's'
            ELSE NULL
        END AS tempo_minimo_permanencia,
        -- Tempo máximo de permanência
        CASE 
            WHEN MAX(p.permanencia_segundos) IS NOT NULL THEN
                CAST(MAX(p.permanencia_segundos) / 86400 AS VARCHAR(10)) + 'd ' +
                RIGHT('0' + CAST((MAX(p.permanencia_segundos) % 86400) / 3600 AS VARCHAR(2)), 2) + 'h ' +
                RIGHT('0' + CAST((MAX(p.permanencia_segundos) % 3600) / 60 AS VARCHAR(2)), 2) + 'm ' +
                RIGHT('0' + CAST(MAX(p.permanencia_segundos) % 60 AS VARCHAR(2)), 2) + 's'
            ELSE NULL
        END AS tempo_maximo_permanencia
    FROM ContagemPassagens cp
    LEFT JOIN Permanencias p ON cp.id_area_monitorada = p.id_area_monitorada 
        AND cp.placa = p.placa
    GROUP BY 
        cp.id_area_monitorada,
        cp.nome_area
    ORDER BY nome_area;
    
    -- Detalhamento por veículo (opcional, comentado por performance)
    
    SELECT 
        p.placa,
        p.id_area_monitorada,
        p.nome_area,
        COUNT(*) AS total_entradas,
        CASE 
            WHEN AVG(CAST(p.permanencia_segundos AS BIGINT)) IS NOT NULL THEN
                CAST(AVG(CAST(p.permanencia_segundos AS BIGINT)) / 86400 AS VARCHAR(10)) + 'd ' +
                RIGHT('0' + CAST((AVG(CAST(p.permanencia_segundos AS BIGINT)) % 86400) / 3600 AS VARCHAR(2)), 2) + 'h ' +
                RIGHT('0' + CAST((AVG(CAST(p.permanencia_segundos AS BIGINT)) % 3600) / 60 AS VARCHAR(2)), 2) + 'm ' +
                RIGHT('0' + CAST(AVG(CAST(p.permanencia_segundos AS BIGINT)) % 60 AS VARCHAR(2)), 2) + 's'
            ELSE '00d 00h 00m 00s'
        END AS tempo_medio_permanencia
    FROM Permanencias p
    GROUP BY p.placa, p.id_area_monitorada, p.nome_area
    ORDER BY p.nome_area, p.placa;
    
END;

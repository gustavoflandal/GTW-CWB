CREATE   PROCEDURE spu_RelatorioObterResumoDiario
    @data_ini DATETIME,
    @data_fim DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    -- Índice para muralha.veiculo_tempo_real
    -- Chaves: placa (para PARTITION BY), data (para WHERE e ORDER BY)
    -- Include: id_local (usado na CTE 1)
    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_VTR_RelatorioDiario' AND object_id = OBJECT_ID('muralha.veiculo_tempo_real'))
    BEGIN
        CREATE NONCLUSTERED INDEX IX_VTR_RelatorioDiario
        ON muralha.veiculo_tempo_real (placa, data)
        INCLUDE (id_local)
        WITH (DROP_EXISTING = OFF);
    END;

    -- Índice para muralha.equipamentos_area_monitorada
    -- Usado na JOIN e na subconsulta NOT IN.
    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_EAM_id_equipamento' AND object_id = OBJECT_ID('muralha.equipamentos_area_monitorada'))
    BEGIN
        CREATE NONCLUSTERED INDEX IX_EAM_id_equipamento
        ON muralha.equipamentos_area_monitorada (id_equipamento)
        WITH (DROP_EXISTING = OFF);
    END;

    -- 1. CTE para unir, filtrar todos os eventos (Entrada e Saída)
    WITH todos_eventos AS (
        SELECT
            data,
            placa,
            'Entrada' AS status,
            1 AS ordem_status 
        FROM
            muralha.veiculo_tempo_real AS a
        INNER JOIN
            muralha.equipamentos_area_monitorada b
            ON a.id_local = b.id_equipamento
        WHERE
            a.data >= @data_ini
            AND a.data <= @data_fim

        UNION ALL

        SELECT
            data,
            placa,
            'Saida' AS status,
            2 AS ordem_status 
        FROM
            muralha.veiculo_tempo_real
        WHERE
            data >= @data_ini
            AND data <= @data_fim
            AND id_local NOT IN (
                SELECT id_equipamento
                FROM muralha.equipamentos_area_monitorada
            )
    ),
    
    -- 2. CTE para calcular o status do evento anterior
    lag_calculado AS (
        SELECT
            data,
            placa,
            status,
            ordem_status,
            LAG(status) OVER (PARTITION BY placa ORDER BY data, ordem_status) AS status_anterior
        FROM
            todos_eventos
    ),
    
    -- 3. CTE para buscar a próxima Saída e criar o ciclo completo
    ciclos_permanencia AS (
        SELECT
            data AS data_entrada,
            placa,
            LEAD(data) OVER (PARTITION BY placa ORDER BY data, ordem_status) AS data_saida,
            LEAD(status) OVER (PARTITION BY placa ORDER BY data, ordem_status) AS status_saida
        FROM
            lag_calculado
        WHERE
            status = 'Entrada'
            AND (status_anterior = 'Saida' OR status_anterior IS NULL)
    ),

    -- 4. CTE para calcular a permanência em SEGUNDOS
    permanencia_calculada AS (
        SELECT
            placa,
            data_entrada,
            CASE WHEN status_saida = 'Saida' THEN data_saida ELSE NULL END AS data_saida,
            -- Data final de cálculo (Saída ou Momento Atual)
            CASE WHEN status_saida = 'Saida' THEN data_saida ELSE GETDATE() END AS data_final_calculo,
            
            -- CORREÇÃO APLICADA AQUI: CAST(DATEDIFF(...) AS BIGINT)
            CAST(DATEDIFF(SECOND, data_entrada, CASE WHEN status_saida = 'Saida' THEN data_saida ELSE GETDATE() END) AS BIGINT) AS duracao_segundos
        FROM
            ciclos_permanencia
        WHERE
            data_entrada IS NOT NULL
    )
    
    -- 5. Seleção final com a AGGREGAÇÃO DIÁRIA
    SELECT
        CAST(a.data AS DATE) AS Data,
        COUNT(*) AS TotalPassagens,
        SUM(CASE WHEN a.status = 'Entrada' THEN 1 ELSE 0 END) AS TotalEntradas,
        SUM(CASE WHEN a.status = 'Saida' THEN 1 ELSE 0 END) AS TotalSaidas,
        COUNT(DISTINCT CASE WHEN a.status = 'Entrada' THEN a.placa ELSE NULL END) AS VeiculosDistintosEntraram,
        COUNT(DISTINCT CASE WHEN a.status = 'Saida' THEN a.placa ELSE NULL END) AS VeiculosDistintosSairam,
        
        -- CORREÇÃO APLICADA AQUI: AVG agora opera sobre o BIGINT 'duracao_segundos'
        CONVERT(
            VARCHAR,
            DATEADD(
                SECOND,
                ISNULL(AVG(p.duracao_segundos), 0), -- Usa o campo BIGINT
                0
            ),
            108 -- 108 retorna no formato hh:mm:ss
        ) AS TempoMedioPermanencia
        
    FROM
        todos_eventos a
    LEFT JOIN
        permanencia_calculada p
        ON a.placa = p.placa 
        AND a.data = p.data_entrada
    
    GROUP BY
        CAST(a.data AS DATE)
        
    ORDER BY
        Data;
END

CREATE   PROCEDURE spu_RelatorioObterEventosCiclom
    @placa VARCHAR(20) = NULL,
    @data_ini DATETIME,
    @data_fim DATETIME
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED; -- Melhora performance em relatórios

    -- =================================================================
    -- I. MATERIALIZAÇÃO DOS DADOS BASE
    -- =================================================================
    
    -- Tabela temporária com índices para melhor performance
    IF OBJECT_ID('tempdb..#EventosBase') IS NOT NULL DROP TABLE #EventosBase;
    
    SELECT 
        a.placa,
        a.data,
        a.id_local AS id_equipamento,
        CASE 
            WHEN b.id_equipamento IS NOT NULL THEN 'Entrada'
            ELSE 'Saida'
        END AS status_evento,
        CASE 
            WHEN b.id_equipamento IS NOT NULL THEN 1
            ELSE 2
        END AS ordem_status
    INTO #EventosBase
    FROM muralha.veiculo_tempo_real a
    LEFT JOIN muralha.equipamentos_area_monitorada b ON a.id_local = b.id_equipamento
    WHERE (@placa IS NULL OR a.placa = @placa)
        AND a.data >= @data_ini
        AND a.data <= @data_fim;
    
    -- Índices para otimizar as consultas subsequentes
    CREATE CLUSTERED INDEX IX_EventosBase ON #EventosBase (placa, data, ordem_status);

    -- =================================================================
    -- II. CÁLCULO DOS CICLOS DE PERMANÊNCIA
    -- =================================================================
    
    IF OBJECT_ID('tempdb..#PermanenciaBruta') IS NOT NULL DROP TABLE #PermanenciaBruta;
    
    ;WITH eventos_ordenados AS (
        SELECT 
            placa,
            data,
            id_equipamento,
            status_evento,
            ordem_status,
            ROW_NUMBER() OVER (PARTITION BY placa ORDER BY data, ordem_status) AS rn
        FROM #EventosBase
    ),
    eventos_com_anterior AS (
        SELECT 
            e1.placa,
            e1.data,
            e1.id_equipamento,
            e1.status_evento,
            e1.rn,
            e2.status_evento AS status_anterior
        FROM eventos_ordenados e1
        LEFT JOIN eventos_ordenados e2 
            ON e1.placa = e2.placa 
            AND e1.rn = e2.rn + 1
    ),
    apenas_entradas_validas AS (
        -- Somente entradas que são início de ciclo
        SELECT 
            placa,
            data AS data_entrada,
            id_equipamento AS id_equipamento_entrada,
            status_evento AS status_entrada,
            rn
        FROM eventos_com_anterior
        WHERE status_evento = 'Entrada' 
            AND (status_anterior = 'Saida' OR status_anterior IS NULL)
    ),
    entradas_com_proxima_saida AS (
        -- Associa cada entrada com sua próxima saída
        SELECT 
            ent.placa,
            ent.data_entrada,
            ent.id_equipamento_entrada,
            ent.status_entrada,
            ent.rn AS rn_entrada,
            (
                SELECT MIN(ev.data)
                FROM eventos_ordenados ev
                WHERE ev.placa = ent.placa
                    AND ev.status_evento = 'Saida'
                    AND ev.rn > ent.rn
            ) AS data_saida,
            (
                SELECT TOP 1 ev.id_equipamento
                FROM eventos_ordenados ev
                WHERE ev.placa = ent.placa
                    AND ev.status_evento = 'Saida'
                    AND ev.rn > ent.rn
                ORDER BY ev.rn
            ) AS id_equipamento_saida
        FROM apenas_entradas_validas ent
    )
    SELECT
        placa,
        data_entrada,
        id_equipamento_entrada,
        status_entrada,
        data_saida,
        id_equipamento_saida,
        CASE WHEN data_saida IS NOT NULL THEN 'Saida' ELSE NULL END AS status_saida,
        COALESCE(data_saida, GETDATE()) AS data_final_calculo,
        DATEDIFF(SECOND, data_entrada, COALESCE(data_saida, GETDATE())) AS TotalSegundos
    INTO #PermanenciaBruta
    FROM entradas_com_proxima_saida;
    
    -- Índice para otimizar as consultas finais
    CREATE CLUSTERED INDEX IX_Permanencia ON #PermanenciaBruta (placa, data_entrada);

    -- =================================================================
    -- RECORDSET 1: RESUMO ESTATÍSTICO
    -- =================================================================
    
    DECLARE @TotalPassagens INT;
    DECLARE @VeiculosEntraram INT;
    DECLARE @VeiculosSairam INT;
    DECLARE @MediaSegundos INT;
    
    SELECT @TotalPassagens = COUNT(*) FROM #EventosBase;
    SELECT @VeiculosEntraram = COUNT(DISTINCT placa) FROM #PermanenciaBruta;
    SELECT @VeiculosSairam = COUNT(DISTINCT placa) FROM #PermanenciaBruta WHERE data_saida IS NOT NULL;
    SELECT @MediaSegundos = AVG(TotalSegundos) FROM #PermanenciaBruta WHERE data_saida IS NOT NULL;
    
    SELECT
        @TotalPassagens AS TotalDePassagens,
        @VeiculosEntraram AS VeiculosQueEntraram,
        @VeiculosSairam AS VeiculosQueSairam,
        CASE 
            WHEN @MediaSegundos IS NULL THEN 'N/A'
            ELSE CONCAT(
                RIGHT('00' + CAST(@MediaSegundos / 86400 AS VARCHAR(20)), 2), 'd ',
                RIGHT('00' + CAST((@MediaSegundos % 86400) / 3600 AS VARCHAR(2)), 2), 'h ',
                RIGHT('00' + CAST(((@MediaSegundos % 86400) % 3600) / 60 AS VARCHAR(2)), 2), 'm ',
                RIGHT('00' + CAST(((@MediaSegundos % 86400) % 3600) % 60 AS VARCHAR(2)), 2), 's'
            )
        END AS TempoMedioPermanencia;

    -- =================================================================
    -- RECORDSET 2: RELATÓRIO DETALHADO DE CICLOS
    -- =================================================================
    
    SELECT
        placa,
        data_entrada,
        id_equipamento_entrada,
        status_entrada,
        data_saida,
        id_equipamento_saida,
        status_saida,
        CONCAT(
            RIGHT('00' + CAST(TotalSegundos / 86400 AS VARCHAR(20)), 2), 'd ',
            RIGHT('00' + CAST((TotalSegundos % 86400) / 3600 AS VARCHAR(2)), 2), 'h ',
            RIGHT('00' + CAST(((TotalSegundos % 86400) % 3600) / 60 AS VARCHAR(2)), 2), 'm ',
            RIGHT('00' + CAST(((TotalSegundos % 86400) % 3600) % 60 AS VARCHAR(2)), 2), 's'
        ) AS permanencia
    FROM #PermanenciaBruta
    ORDER BY placa, data_entrada;

    -- =================================================================
    -- RECORDSET 3: VEÍCULOS ATUALMENTE NA ÁREA
    -- =================================================================
    
    SELECT
        placa,
        data_entrada,
        id_equipamento_entrada,
        'Permanência Atual' AS status_permanencia,
        CONCAT(
            RIGHT('00' + CAST(TotalSegundos / 86400 AS VARCHAR(20)), 2), 'd ',
            RIGHT('00' + CAST((TotalSegundos % 86400) / 3600 AS VARCHAR(2)), 2), 'h ',
            RIGHT('00' + CAST(((TotalSegundos % 86400) % 3600) / 60 AS VARCHAR(2)), 2), 'm ',
            RIGHT('00' + CAST(((TotalSegundos % 86400) % 3600) % 60 AS VARCHAR(2)), 2), 's'
        ) AS permanencia_ate_agora
    FROM #PermanenciaBruta
    WHERE data_saida IS NULL
    ORDER BY placa, data_entrada;
        
    -- Limpeza
    DROP TABLE IF EXISTS #EventosBase;
    DROP TABLE IF EXISTS #PermanenciaBruta;

END

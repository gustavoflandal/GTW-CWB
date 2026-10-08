-- =============================================
-- Procedure: sp_MonitoramentoAreaMonitorada
-- Versão: 2.0 - Lógica de Entrada e Saída
-- =============================================

CREATE   PROCEDURE muralha.sp_MonitoramentoAreaMonitorada_V2
    @area_monitorada VARCHAR(6) = NULL,
    @placa VARCHAR(8) = NULL,
    @data_ini DATE,
    @data_fim DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Validação de parâmetros
    IF @data_ini IS NULL OR @data_fim IS NULL
    BEGIN
        RAISERROR('Os parâmetros @data_ini e @data_fim são obrigatórios', 16, 1);
        RETURN;
    END
    
    IF @data_fim < @data_ini
    BEGIN
        RAISERROR('A data final não pode ser menor que a data inicial', 16, 1);
        RETURN;
    END
    
    -- Tabela temporária para armazenar todos os registros relevantes
    IF OBJECT_ID('tempdb..#RegistrosVeiculos') IS NOT NULL
        DROP TABLE #RegistrosVeiculos;
    
    CREATE TABLE #RegistrosVeiculos (
        placa CHAR(7),
        data_passagem DATETIME,
        id_local INT,
        id_area_monitorada INT NULL,
        seq_registro INT,
        PRIMARY KEY (placa, seq_registro)
    );
    
    -- Buscar todos os registros de veículos no período
    INSERT INTO #RegistrosVeiculos (placa, data_passagem, id_local, id_area_monitorada, seq_registro)
    SELECT 
        vtr.placa,
        vtr.data AS data_passagem,
        vtr.id_local,
        eam.id_area_monitorada,
        ROW_NUMBER() OVER (PARTITION BY vtr.placa ORDER BY vtr.data) AS seq_registro
    FROM muralha.veiculo_tempo_real vtr
    LEFT JOIN muralha.equipamentos_area_monitorada eam 
        ON vtr.id_local = eam.id_equipamento
    LEFT JOIN muralha.area_monitorada am 
        ON eam.id_area_monitorada = am.id
        AND am.deletado = 0
    WHERE vtr.data >= @data_ini 
        AND vtr.data < DATEADD(DAY, 1, @data_fim)
        AND (@placa IS NULL OR vtr.placa = @placa)
    ORDER BY vtr.placa, vtr.data;
    
    -- Tabela para armazenar entradas e saídas identificadas
    IF OBJECT_ID('tempdb..#EntradasSaidas') IS NOT NULL
        DROP TABLE #EntradasSaidas;
    
    CREATE TABLE #EntradasSaidas (
        placa CHAR(7),
        ocorrencia VARCHAR(10),
        data_entrada DATETIME NULL,
        id_area_monitorada_entrada INT NULL,
        id_equipamento_entrada INT NULL,
        data_saida DATETIME NULL,
        id_equipamento_saida INT NULL,
        seq_entrada INT
    );
    
    -- Identificar entradas e suas respectivas saídas
    -- Uma entrada ocorre quando:
    -- 1. Há registro em equipamento de área monitorada
    -- 2. O registro anterior NÃO é da mesma área (ou não existe)
    INSERT INTO #EntradasSaidas (
        placa, 
        ocorrencia,
        data_entrada, 
        id_area_monitorada_entrada, 
        id_equipamento_entrada,
        data_saida,
        id_equipamento_saida,
        seq_entrada
    )
    SELECT 
        r1.placa,
        'Entrada' AS ocorrencia,
        r1.data_passagem AS data_entrada,
        r1.id_area_monitorada AS id_area_monitorada_entrada,
        r1.id_local AS id_equipamento_entrada,
        r_saida.data_passagem AS data_saida,
        r_saida.id_local AS id_equipamento_saida,
        ROW_NUMBER() OVER (PARTITION BY r1.placa ORDER BY r1.data_passagem) AS seq_entrada
    FROM #RegistrosVeiculos r1
    -- Encontrar a próxima passagem que seja saída válida
    OUTER APPLY (
        SELECT TOP 1
            r2.data_passagem,
            r2.id_local,
            r2.id_area_monitorada
        FROM #RegistrosVeiculos r2
        WHERE r2.placa = r1.placa
            AND r2.seq_registro > r1.seq_registro
            -- Saída válida: equipamento de área diferente ou sem área
            AND (r2.id_area_monitorada IS NULL 
                 OR r2.id_area_monitorada <> r1.id_area_monitorada)
        ORDER BY r2.seq_registro
    ) r_saida
    WHERE r1.id_area_monitorada IS NOT NULL  -- Apenas registros em áreas monitoradas
        -- Entrada válida: área anterior é diferente ou não existe
        AND (
            -- Primeiro registro do veículo
            NOT EXISTS (
                SELECT 1 FROM #RegistrosVeiculos r_ant
                WHERE r_ant.placa = r1.placa
                AND r_ant.seq_registro < r1.seq_registro
            )
            OR
            -- Registro anterior é de área diferente
            (
                SELECT TOP 1 r_ant.id_area_monitorada
                FROM #RegistrosVeiculos r_ant
                WHERE r_ant.placa = r1.placa
                AND r_ant.seq_registro < r1.seq_registro
                ORDER BY r_ant.seq_registro DESC
            ) IS NULL
            OR
            (
                SELECT TOP 1 r_ant.id_area_monitorada
                FROM #RegistrosVeiculos r_ant
                WHERE r_ant.placa = r1.placa
                AND r_ant.seq_registro < r1.seq_registro
                ORDER BY r_ant.seq_registro DESC
            ) <> r1.id_area_monitorada
        )
        AND (@area_monitorada IS NULL OR r1.id_area_monitorada = CAST(@area_monitorada AS INT));
    
    -- Retornar resultado
    SELECT 
        placa,
        'Entrada' AS ocorrencia,
        data_entrada,
        id_area_monitorada_entrada AS id_area_monitorada,
        id_equipamento_entrada,
        CASE 
            WHEN data_saida IS NULL THEN 'Saída Pendente'
            ELSE 'Saída'
        END AS ocorrencia_saida,
        data_saida,
        id_equipamento_saida,
        CASE 
            WHEN data_saida IS NULL THEN 
                DATEDIFF(SECOND, data_entrada, GETDATE())
            ELSE 
                DATEDIFF(SECOND, data_entrada, data_saida)
        END AS segundos_permanencia
    FROM #EntradasSaidas
    ORDER BY placa, data_entrada;
    
    -- Limpeza
    DROP TABLE #RegistrosVeiculos;
    DROP TABLE #EntradasSaidas;
    
END;

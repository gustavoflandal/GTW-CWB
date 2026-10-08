CREATE   PROCEDURE spu_RelatorioObterEventosCiclo
    @placa VARCHAR(20) = NULL, -- Placa opcional
    @data_ini DATETIME,
    @data_fim DATETIME
AS
BEGIN
    SET NOCOUNT ON;
	IF @placa = ''  
	   SET @placa = null;

    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_VTR_RelatorioDiario' AND object_id = OBJECT_ID('muralha.veiculo_tempo_real'))
    BEGIN
        CREATE NONCLUSTERED INDEX IX_VTR_RelatorioDiario
        ON muralha.veiculo_tempo_real (placa, data)
        INCLUDE (id_local)
        -- Opção ONLINE = ON removida para compatibilidade com versões não-Enterprise
        WITH (DROP_EXISTING = OFF); 
    END;

    -- Índice para muralha.equipamentos_area_monitorada
    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_EAM_id_equipamento' AND object_id = OBJECT_ID('muralha.equipamentos_area_monitorada'))
    BEGIN
        CREATE NONCLUSTERED INDEX IX_EAM_id_equipamento
        ON muralha.equipamentos_area_monitorada (id_equipamento)
        -- Opção ONLINE = ON removida
        WITH (DROP_EXISTING = OFF);
    END;	

    -- 1. CTE para unir, filtrar e ordenar todos os eventos (Entrada e Saída)
    WITH todos_eventos AS (
        SELECT
            data,
            placa,
            id_equipamento,
            'Entrada' AS status,
            1 AS ordem_status -- Entrada primeiro em caso de desempate
        FROM
            muralha.veiculo_tempo_real AS a
        INNER JOIN
            muralha.equipamentos_area_monitorada b
            ON a.id_local = b.id_equipamento
        WHERE
            (@placa IS NULL OR a.placa = @placa)
            AND a.data >= @data_ini
            AND a.data <= @data_fim

        UNION ALL

        SELECT
            data,
            placa,
            id_local AS id_equipamento,
            'Saida' AS status,
            2 AS ordem_status -- Saida depois em caso de desempate
        FROM
            muralha.veiculo_tempo_real
        WHERE
            (@placa IS NULL OR placa = @placa)
            AND data >= @data_ini
            AND data <= @data_fim
            AND id_local NOT IN (
                SELECT id_equipamento
                FROM muralha.equipamentos_area_monitorada
            )
    ),
    
    -- 2. CTE para calcular o status do evento anterior (necessário para identificar a PRIMEIRA Entrada de um ciclo)
    lag_calculado AS (
        SELECT
            data,
            placa,
            id_equipamento,
            status,
            -- Status do evento imediatamente anterior
            LAG(status) OVER (PARTITION BY placa ORDER BY data, ordem_status) AS status_anterior
        FROM
            todos_eventos
    ),
    
    -- 3. CTE para isolar apenas as PRIMEIAS Entradas de cada ciclo de presença
    entradas_finais AS (
        SELECT
            data AS data_entrada,
            placa,
            id_equipamento AS id_equipamento_entrada,
            status AS status_entrada
        FROM
            lag_calculado
        WHERE
            status = 'Entrada'
            AND (status_anterior = 'Saida' OR status_anterior IS NULL)
    ),

    -- 4. CTE para buscar a próxima Saída e criar o ciclo completo
    ciclos_permanencia AS (
        SELECT
            data_entrada,
            placa,
            id_equipamento_entrada,
            status_entrada,
            
            -- Busca a data da próxima Saída (Status = 'Saida')
            LEAD(data_entrada) OVER (PARTITION BY placa ORDER BY data_entrada) AS data_saida,
            
            -- Busca o Status da próxima Saída ('Saida' se não for o último evento)
            LEAD(status_entrada) OVER (PARTITION BY placa ORDER BY data_entrada) AS status_saida,
            
            -- Busca o ID do equipamento da próxima Saída
            LEAD(id_equipamento_entrada) OVER (PARTITION BY placa ORDER BY data_entrada) AS id_equipamento_saida
            
        FROM
            entradas_finais
    ),

    -- 5. CTE para calcular o DATEDIFF em SEGUNDOS e o valor final de Saída/Atual
    permanencia_bruta AS (
        SELECT
            placa,
            data_entrada,
            id_equipamento_entrada,
            status_entrada,
            
            CASE WHEN status_saida = 'Saida' THEN data_saida ELSE NULL END AS data_saida,
            CASE WHEN status_saida = 'Saida' THEN id_equipamento_saida ELSE NULL END AS id_equipamento_saida,
            CASE WHEN status_saida = 'Saida' THEN status_saida ELSE NULL END AS status_saida,
            
            -- Pega a hora final para o cálculo (Saída ou Momento Atual)
            CASE WHEN status_saida = 'Saida' THEN data_saida ELSE GETDATE() END AS data_final_calculo
        FROM
            ciclos_permanencia
        WHERE
            status_entrada = 'Entrada'
    )
    
    -- 6. Seleção final com a conversão do DATEDIFF para o formato "00d 00h 00m 00s"
    SELECT
        placa,
        data_entrada,
        id_equipamento_entrada,
        status_entrada,
        data_saida,
        id_equipamento_saida,
        status_saida,
        
       
        -- Aplica a conversão e formatação: "00d 00h 00m 00s"
        CONCAT(
            -- Dias
            RIGHT('0' + CAST(DATEDIFF(SECOND, data_entrada, data_final_calculo) / 86400 AS VARCHAR(20)), 2), 'd ',
            -- Horas
            RIGHT('0' + CAST((DATEDIFF(SECOND, data_entrada, data_final_calculo) % 86400) / 3600 AS VARCHAR(2)), 2), 'h ',
            -- Minutos
            RIGHT('0' + CAST(((DATEDIFF(SECOND, data_entrada, data_final_calculo) % 86400) % 3600) / 60 AS VARCHAR(2)), 2), 'm ',
            -- Segundos
            RIGHT('0' + CAST(((DATEDIFF(SECOND, data_entrada, data_final_calculo) % 86400) % 3600) % 60 AS VARCHAR(2)), 2), 's'
        ) AS permanencia
        
    FROM
        permanencia_bruta
        
    ORDER BY
        placa,
        data_entrada;
END


-- =============================================
-- Procedure: Relatório de Permanência de Veículos em Área Monitorada (Detalhado)
-- Descrição: Controla tempo de permanência em áreas monitoradas
--            Garante que cada entrada tenha apenas uma saída única
-- Autor: Gustavo F. Landal
-- Data: 2025-10-06
-- =============================================
CREATE     PROCEDURE [spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhadoTESTE]
   
    @area_monitorada VARCHAR(6) = NULL,
    @placa VARCHAR(8) = NULL,
    @data_ini DATE,
    @data_fim DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Converter strings vazias em NULL
    IF @area_monitorada = '' SET @area_monitorada = NULL;
    IF @placa = '' SET @placa = NULL;
    
    -- Validação de parâmetros
    IF @data_ini IS NULL OR @data_fim IS NULL
    BEGIN
        RAISERROR('Os parâmetros @data_ini e @data_fim são obrigatórios', 16, 1);
        RETURN;
    END
    
    ;WITH todas_passagens AS (
        -- Todas as passagens ordenadas cronologicamente
        SELECT 
            vtr.placa,
            vtr.data,
            vtr.id_local,
            eam.id_area_monitorada,
            ROW_NUMBER() OVER (PARTITION BY vtr.placa ORDER BY vtr.data) AS seq_passagem
        FROM muralha.veiculo_tempo_real vtr
        LEFT JOIN muralha.equipamentos_area_monitorada eam 
            ON vtr.id_local = eam.id_equipamento
        LEFT JOIN muralha.area_monitorada am 
            ON am.id = eam.id_area_monitorada
            AND am.deletado = 0
        WHERE vtr.placa IS NOT NULL
            AND (@placa IS NULL OR vtr.placa = @placa)
            AND vtr.data BETWEEN @data_ini AND @data_fim
    ),
    passagens_com_lag AS (
        -- Adicionar área anterior usando LAG
        SELECT 
            placa,
            data,
            id_local,
            id_area_monitorada,
            seq_passagem,
            -- Área anterior
            LAG(id_area_monitorada) OVER (PARTITION BY placa ORDER BY seq_passagem) AS area_anterior
        FROM todas_passagens
    ),
    mudancas_de_area AS (
        -- Filtrar apenas MUDANÇAS de área (ignora passagens intermediárias)
        SELECT 
            placa,
            data,
            id_local,
            id_area_monitorada,
            seq_passagem,
            area_anterior
        FROM passagens_com_lag
        WHERE 
            -- Apenas passagens que representam MUDANÇA de área
            id_area_monitorada <> area_anterior
            OR area_anterior IS NULL
    ),
    entradas_saidas AS (
        -- Para cada ENTRADA, encontrar primeira SAÍDA válida
        SELECT 
            placa,
            id_area_monitorada,
            data AS data_entrada,
            id_local AS equipamento_entrada,
            -- Buscar próxima mudança de área (SAÍDA)
            (
                SELECT MIN(m.data)
                FROM mudancas_de_area m
                WHERE m.placa = e.placa
                    AND m.data > e.data
                    AND (m.id_area_monitorada IS NULL OR m.id_area_monitorada <> e.id_area_monitorada)
            ) AS data_saida,
            (
                SELECT TOP 1 m.id_local
                FROM mudancas_de_area m
                WHERE m.placa = e.placa
                    AND m.data > e.data
                    AND (m.id_area_monitorada IS NULL OR m.id_area_monitorada <> e.id_area_monitorada)
                ORDER BY m.data
            ) AS equipamento_saida
        FROM mudancas_de_area e
        WHERE e.id_area_monitorada IS NOT NULL  -- Apenas ENTRADAS em áreas
            AND (@area_monitorada IS NULL OR e.id_area_monitorada = CAST(@area_monitorada AS INT))
    )
    -- Resultado final
    SELECT 
        placa,
        'Entrada' AS ocorrencia_entrada,
        data_entrada AS data_entrada_area_monitorada,
        id_area_monitorada,
        equipamento_entrada AS id_equipamento_entrada,
        'Saída' AS ocorrencia_saida,
        data_saida AS data_saida_area_monitorada,
        equipamento_saida AS id_equipamento_saida,
        -- Tempo de permanência
        RIGHT('0' + CAST(DATEDIFF(DAY, data_entrada, ISNULL(data_saida, GETDATE())) AS VARCHAR), 2) + 'd ' +
        RIGHT('0' + CAST(DATEDIFF(HOUR, data_entrada, ISNULL(data_saida, GETDATE())) % 24 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST(DATEDIFF(MINUTE, data_entrada, ISNULL(data_saida, GETDATE())) % 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(DATEDIFF(SECOND, data_entrada, ISNULL(data_saida, GETDATE())) % 60 AS VARCHAR), 2) + 's'
        AS tempo_permanencia
    FROM entradas_saidas
    ORDER BY id_area_monitorada, placa, data_entrada_area_monitorada;
    
END

 --exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','DMK6G86','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','SEU7J11','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','PZJ3H82','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','SXF7H59','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','AAY4A05','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '65','','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','','2025-07-10','2025-10-05'


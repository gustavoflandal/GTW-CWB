CREATE   PROCEDURE muralha.spu_PermanenciaAreasMonitoradasNew
    @area INT = NULL,
    @placa VARCHAR(8) = NULL,
    @data_inicio DATE,
    @data_fim DATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Tratar parâmetros vazios como NULL
    IF @area = 0 OR @area = -1 SET @area = NULL;
    IF @placa = '' SET @placa = NULL;

    -- ============================================================================
    -- Tabela temporária para armazenar permanências calculadas
    -- ============================================================================
    DECLARE @permanencias TABLE (
        placa VARCHAR(8),
        area_id INT,
        area_nome VARCHAR(255),
        data_entrada DATETIME,
        data_saida DATETIME,
        id_local_entrada INT,
        id_local_saida INT,
        duracao_minutos INT,
        duracao_segundos INT
    );

    -- ============================================================================
    -- CTE: Equipamentos vinculados a áreas monitoradas
    -- ============================================================================
    WITH
        EquipamentosAreas
        AS
        (
            SELECT DISTINCT
                eam.id_equipamento,
                am.id as area_id,
                am.nome as area_nome
            FROM muralha.equipamentos_area_monitorada eam
                INNER JOIN muralha.area_monitorada am
                ON eam.id_area_monitorada = am.id
            WHERE am.deletado = 0
                AND (@area IS NULL OR am.id = @area)
        ),

        -- ============================================================================
        -- CTE: Todas as passagens do período com flag de área monitorada
        -- ============================================================================
        PassagensComClassificacao
        AS
        (
            SELECT
                vtr.placa,
                vtr.id_local,
                vtr.data,
                ea.area_id,
                ea.area_nome,
                CASE 
                WHEN ea.area_id IS NOT NULL THEN 1 
                ELSE 0 
            END AS em_area_monitorada,
                ROW_NUMBER() OVER (
                PARTITION BY vtr.placa 
                ORDER BY vtr.data
            ) AS seq_numero
            FROM muralha.veiculo_tempo_real vtr
                LEFT JOIN EquipamentosAreas ea
                ON vtr.id_local = ea.id_equipamento
            WHERE vtr.data >= @data_inicio
                AND vtr.data < DATEADD(DAY, 1, @data_fim)
                AND (@placa IS NULL OR vtr.placa = @placa)
        ),

        -- ============================================================================
        -- CTE: Identificação de eventos (ENTRADA/SAIDA/PASSAGEM)
        -- ============================================================================
        EventosClassificados
        AS
        (
            SELECT
                p.placa,
                p.id_local,
                p.data,
                p.area_id,
                p.area_nome,
                p.em_area_monitorada,
                p.seq_numero,
                LAG(p.em_area_monitorada) OVER (
                PARTITION BY p.placa 
                ORDER BY p.seq_numero
            ) AS estava_em_area,
                CASE 
                -- ENTRADA: não estava em área e agora está
                WHEN p.em_area_monitorada = 1
                    AND ISNULL(LAG(p.em_area_monitorada) OVER (
                         PARTITION BY p.placa ORDER BY p.seq_numero), 0) = 0
                THEN 'ENTRADA'
                -- SAIDA: estava em área e agora não está
                WHEN p.em_area_monitorada = 0
                    AND ISNULL(LAG(p.em_area_monitorada) OVER (
                         PARTITION BY p.placa ORDER BY p.seq_numero), 0) = 1
                THEN 'SAIDA'
                -- PASSAGEM: continua na área
                WHEN p.em_area_monitorada = 1
                THEN 'PASSAGEM'
                ELSE 'EXTERNA'
            END AS tipo_evento
            FROM PassagensComClassificacao p
        ),

        -- ============================================================================
        -- CTE: Numerar entradas e saídas separadamente
        -- ============================================================================
        EntradasNumeradas
        AS
        (
            SELECT
                placa,
                data,
                id_local,
                area_id,
                area_nome,
                ROW_NUMBER() OVER (PARTITION BY placa ORDER BY seq_numero) AS num_entrada
            FROM EventosClassificados
            WHERE tipo_evento = 'ENTRADA'
        ),

        SaidasNumeradas
        AS
        (
            SELECT
                placa,
                data,
                id_local,
                ROW_NUMBER() OVER (PARTITION BY placa ORDER BY seq_numero) AS num_saida
            FROM EventosClassificados
            WHERE tipo_evento = 'SAIDA'
        ),

        -- ============================================================================
        -- CTE: Cálculo de permanências (cada ENTRADA até próxima SAIDA)
        -- ============================================================================
        PermanenciasCalculadas
        AS
        (
            SELECT
                e.placa,
                e.area_id,
                e.area_nome,
                e.data AS data_entrada,
                e.id_local AS id_local_entrada,
                s.data AS data_saida,
                s.id_local AS id_local_saida
            FROM EntradasNumeradas e
                INNER JOIN SaidasNumeradas s
                ON e.placa = s.placa
                    AND s.num_saida = e.num_entrada
                    AND s.data > e.data
        )

    -- ============================================================================
    -- Inserir dados na tabela temporária
    -- ============================================================================
    INSERT INTO @permanencias
    SELECT
        p.placa,
        p.area_id,
        p.area_nome,
        p.data_entrada,
        p.data_saida,
        p.id_local_entrada,
        p.id_local_saida,
        DATEDIFF(MINUTE, p.data_entrada, p.data_saida) AS duracao_minutos,
        DATEDIFF(SECOND, p.data_entrada, p.data_saida) AS duracao_segundos
    FROM PermanenciasCalculadas p
    WHERE p.data_saida IS NOT NULL
        AND DATEDIFF(SECOND, p.data_entrada, p.data_saida) > 0;

    -- ============================================================================
    -- RECORDSET 1: Resumo Geral
    -- ============================================================================
    SELECT
        COUNT(*) as total_permanencias,
        COUNT(DISTINCT placa) as total_veiculos,
        COUNT(DISTINCT area_id) as total_areas,
        SUM(CASE WHEN area_id IS NOT NULL THEN 1 ELSE 0 END) as total_passagens_areas,
        -- Tempo médio formatado (dias horas minutos segundos)
        CAST(AVG(duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((AVG(duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((AVG(duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(AVG(duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_medio_permanencia,
        -- Tempo mínimo formatado
        CAST(MIN(duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((MIN(duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((MIN(duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(MIN(duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_minimo_permanencia,
        -- Tempo máximo formatado
        CAST(MAX(duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((MAX(duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((MAX(duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(MAX(duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_maximo_permanencia,
        MIN(data_entrada) as primeira_entrada,
        MAX(data_saida) as ultima_saida,
        ISNULL(@placa, 'TODAS') as placa_filtrada,
        ISNULL(CAST(@area AS VARCHAR), 'TODAS') as area_filtrada,
        @data_inicio as data_inicio_periodo,
        @data_fim as data_fim_periodo
    FROM @permanencias;

    -- ============================================================================
    -- RECORDSET 2: Permanência por Área
    -- ============================================================================
    SELECT
        p.area_id,
        p.area_nome,
        COUNT(*) as total_permanencias,
        COUNT(DISTINCT p.placa) as total_veiculos,
        -- Tempo médio formatado
        CAST(AVG(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((AVG(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((AVG(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(AVG(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_medio_permanencia,
        -- Tempo mínimo formatado
        CAST(MIN(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((MIN(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((MIN(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(MIN(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_minimo_permanencia,
        -- Tempo máximo formatado
        CAST(MAX(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((MAX(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((MAX(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(MAX(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_maximo_permanencia,
        -- Tempo total formatado
        CAST(SUM(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((SUM(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((SUM(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(SUM(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_total_permanencia,
        ROUND((CAST(COUNT(*) AS FLOAT) / NULLIF((SELECT COUNT(*) FROM @permanencias), 0)) * 100, 1) as percentual_permanencias,
        MIN(p.data_entrada) as primeira_entrada,
        MAX(p.data_saida) as ultima_saida
    FROM @permanencias p
    GROUP BY p.area_id, p.area_nome
    ORDER BY COUNT(*) DESC;

    -- ============================================================================
    -- RECORDSET 3: Permanência por Veículo (Top 50)
    -- ============================================================================
    SELECT TOP 50
        p.placa,
        COUNT(*) as total_permanencias,
        COUNT(DISTINCT p.area_id) as areas_visitadas,
        -- Tempo médio formatado
        CAST(AVG(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((AVG(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((AVG(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(AVG(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_medio_permanencia,
        -- Tempo mínimo formatado
        CAST(MIN(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((MIN(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((MIN(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(MIN(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_minimo_permanencia,
        -- Tempo máximo formatado
        CAST(MAX(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((MAX(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((MAX(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(MAX(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_maximo_permanencia,
        -- Tempo total formatado
        CAST(SUM(p.duracao_segundos) / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((SUM(p.duracao_segundos) % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((SUM(p.duracao_segundos) % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(SUM(p.duracao_segundos) % 60 AS VARCHAR), 2) + 's' as tempo_total_permanencia,
        MIN(p.data_entrada) as primeira_entrada,
        MAX(p.data_saida) as ultima_saida
    FROM @permanencias p
    GROUP BY p.placa
    ORDER BY SUM(p.duracao_segundos) DESC;

    -- ============================================================================
    -- RECORDSET 4: Detalhes das Permanências (Top 100)
    -- ============================================================================
    SELECT TOP 100
        p.placa,
        p.area_nome,
        p.id_local_entrada,
        p.id_local_saida,
        p.data_entrada,
        p.data_saida,
        -- Duração formatada
        CAST(p.duracao_segundos / 86400 AS VARCHAR) + 'd ' +
        RIGHT('0' + CAST((p.duracao_segundos % 86400) / 3600 AS VARCHAR), 2) + 'h ' +
        RIGHT('0' + CAST((p.duracao_segundos % 3600) / 60 AS VARCHAR), 2) + 'm ' +
        RIGHT('0' + CAST(p.duracao_segundos % 60 AS VARCHAR), 2) + 's' as duracao,

        CASE 
            WHEN DATEPART(HOUR, p.data_entrada) BETWEEN 6 AND 11 THEN 'Manhã'
            WHEN DATEPART(HOUR, p.data_entrada) BETWEEN 12 AND 17 THEN 'Tarde'
            WHEN DATEPART(HOUR, p.data_entrada) BETWEEN 18 AND 23 THEN 'Noite'
            ELSE 'Madrugada'
        END as periodo_entrada
    FROM @permanencias p
    ORDER BY p.data_entrada DESC;

END

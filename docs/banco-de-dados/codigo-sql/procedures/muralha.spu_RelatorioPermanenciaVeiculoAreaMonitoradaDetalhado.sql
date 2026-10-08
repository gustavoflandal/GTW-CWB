
-- ==================================================================================================
-- Procedure: Relatório de Permanência de Veículos em Área Monitorada 
-- Descrição: Controla tempo de permanência apenas em áreas monitoradas ATIVAS (deletado = 0)
-- Autor: Gustavo F. Landal
-- Data: 2025-10-06
-- Otimização V5.0: Filtro fixo de exclusão lógica (apenas am.deletado = 0)
-- ===================================================================================================

CREATE       PROCEDURE [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado]
    @area_monitorada VARCHAR(6) = NULL,
    @placa VARCHAR(8) = NULL,
    @data_ini DATE,
    @data_fim DATE
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Validação dos parâmetros obrigatórios
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

    -- Ajusta valores vazios para NULL
    IF LTRIM(RTRIM(ISNULL(@area_monitorada, ''))) = '' SET @area_monitorada = NULL;
    IF LTRIM(RTRIM(ISNULL(@placa, ''))) = '' SET @placa = NULL;

    -- Variáveis para controle
    DECLARE @area_monitorada_int INT = TRY_CAST(@area_monitorada AS INT);
    DECLARE @data_fim_ajustada DATETIME = DATEADD(DAY, 1, @data_fim);

    -- =============================================
    -- ETAPA 1: Coleta de Dados Base
    -- =============================================
    IF OBJECT_ID('tempdb..#DadosBase') IS NOT NULL DROP TABLE #DadosBase;
    
    CREATE TABLE #DadosBase (
        placa CHAR(7) NOT NULL,
        data_passagem DATETIME NOT NULL,
        id_local INT NOT NULL,
        id_area_monitorada INT NULL,
        nome VARCHAR(255) NULL,   
        INDEX IX_Placa_Data CLUSTERED (placa, data_passagem),
        INDEX IX_Area NONCLUSTERED (id_area_monitorada)
    );

    WITH vtr_filtrado AS (
        SELECT vtr.placa, vtr.data, vtr.id_local
        FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK, FORCESEEK)
        WHERE vtr.data >= @data_ini 
          AND vtr.data < @data_fim_ajustada
          AND vtr.placa IS NOT NULL
          AND (@placa IS NULL OR vtr.placa = @placa)
    ),
    PassagensPriorizadas AS (
        SELECT 
            vtr.placa,
            vtr.data AS data_passagem,
            vtr.id_local,
            eam.id_area_monitorada,
            am.nome,
            -- Priorização para evitar duplicidade na mesma passagem/local
            ROW_NUMBER() OVER (
                PARTITION BY vtr.placa, vtr.data, vtr.id_local 
                ORDER BY am.nome DESC, eam.id_area_monitorada ASC
            ) as rn
        FROM vtr_filtrado vtr
        LEFT JOIN muralha.equipamentos_area_monitorada eam WITH (NOLOCK)
            ON vtr.id_local = eam.id_equipamento
        LEFT JOIN muralha.area_monitorada am WITH (NOLOCK) 
            ON eam.id_area_monitorada = am.id
            AND am.deletado = 0 -- FILTRO FIXO: Apenas áreas ativas
        WHERE 1=1
            AND (@area_monitorada_int IS NULL OR eam.id_area_monitorada = @area_monitorada_int)
    )
    INSERT INTO #DadosBase WITH (TABLOCK)
    SELECT 
        placa,
        data_passagem,
        id_local,
        id_area_monitorada,
        nome
    FROM PassagensPriorizadas
    WHERE rn = 1; 

    UPDATE STATISTICS #DadosBase WITH FULLSCAN;

    -- =============================================
    -- ETAPA 2: Identificação de Entradas
    -- =============================================
    IF OBJECT_ID('tempdb..#Entradas') IS NOT NULL DROP TABLE #Entradas;
    
    CREATE TABLE #Entradas (
        id_entrada INT IDENTITY(1,1) NOT NULL,
        placa CHAR(7) NOT NULL,
        data_entrada DATETIME NOT NULL,
        id_area_monitorada INT NULL,
        id_equipamento_entrada INT NOT NULL,
        nome VARCHAR(150),
        PRIMARY KEY CLUSTERED (placa, data_entrada)
    );

    WITH PassagensSequenciais AS (
        SELECT 
            placa, data_passagem, id_local, id_area_monitorada, nome,
            COALESCE(LAG(id_area_monitorada, 1, -1) OVER (PARTITION BY placa ORDER BY data_passagem), -2) AS area_anterior_ajustada,
            COALESCE(id_area_monitorada, -2) AS area_atual_ajustada
        FROM #DadosBase
    )
    INSERT INTO #Entradas (placa, data_entrada, id_area_monitorada, id_equipamento_entrada, nome)
    SELECT placa, data_passagem, id_area_monitorada, id_local, nome
    FROM PassagensSequenciais
    WHERE area_anterior_ajustada <> area_atual_ajustada 
      AND id_area_monitorada IS NOT NULL; -- Apenas se a área atual for monitorada e ativa

    -- =============================================
    -- ETAPA 3: Identificação de Saídas
    -- =============================================
    IF OBJECT_ID('tempdb..#Saidas') IS NOT NULL DROP TABLE #Saidas;
    
    CREATE TABLE #Saidas (
        placa CHAR(7) NOT NULL,
        data_entrada_ref DATETIME NOT NULL,
        data_saida DATETIME NOT NULL,
        id_equipamento_saida INT NOT NULL,
        id_area_saida INT NULL,
        nome VARCHAR(150),
        PRIMARY KEY CLUSTERED (placa, data_entrada_ref)
    );

    WITH ProximasPassagens AS (
        SELECT
            e.placa, e.data_entrada, db.data_passagem AS proxima_saida,
            db.id_local AS id_equipamento_saida, db.id_area_monitorada AS id_area_saida, db.nome,
            ROW_NUMBER() OVER (PARTITION BY e.placa, e.data_entrada ORDER BY db.data_passagem) AS rn
        FROM #Entradas e
        JOIN #DadosBase db ON db.placa = e.placa AND db.data_passagem > e.data_entrada
        AND (db.id_area_monitorada <> e.id_area_monitorada OR db.id_area_monitorada IS NULL)
    )
    INSERT INTO #Saidas
    SELECT placa, data_entrada, proxima_saida, id_equipamento_saida, id_area_saida, nome
    FROM ProximasPassagens WHERE rn = 1;

    -- =============================================
    -- RECORDSET 1: DETALHAMENTO DAS PERMANÊNCIAS
    -- =============================================
    SELECT 
        e.placa,
        'Entrada' AS ocorrencia,
        e.data_entrada,
        e.id_area_monitorada,
        e.nome,
        e.id_equipamento_entrada,
        CASE WHEN s.data_saida IS NULL THEN 'Sem Saída' ELSE 'Saída' END AS ocorrencia_saida,
        s.data_saida,
        s.id_equipamento_saida,
        CASE 
            WHEN s.data_saida IS NULL THEN 
                FORMAT(DATEDIFF(SECOND, e.data_entrada, GETDATE()) / 86400, '00') + 'd ' +
                FORMAT((DATEDIFF(SECOND, e.data_entrada, GETDATE()) % 86400) / 3600, '00') + 'h ' +
                FORMAT((DATEDIFF(SECOND, e.data_entrada, GETDATE()) % 3600) / 60, '00') + 'm ' +
                FORMAT(DATEDIFF(SECOND, e.data_entrada, GETDATE()) % 60, '00') + 's'
            ELSE 
                FORMAT(DATEDIFF(SECOND, e.data_entrada, s.data_saida) / 86400, '00') + 'd ' +
                FORMAT((DATEDIFF(SECOND, e.data_entrada, s.data_saida) % 86400) / 3600, '00') + 'h ' +
                FORMAT((DATEDIFF(SECOND, e.data_entrada, s.data_saida) % 3600) / 60, '00') + 'm ' +
                FORMAT(DATEDIFF(SECOND, e.data_entrada, s.data_saida) % 60, '00') + 's'
        END AS tempo_permanencia
    FROM #Entradas e
    LEFT JOIN #Saidas s ON e.placa = s.placa AND e.data_entrada = s.data_entrada_ref
    ORDER BY e.placa, e.data_entrada
    OPTION (MAXDOP 4);

    -- =============================================
    -- RECORDSET 2: ESTATÍSTICAS DE PERMANÊNCIA
    -- =============================================
    WITH PermanenciasCalculadas AS (
        SELECT 
            e.id_area_monitorada, e.placa, e.data_entrada, s.data_saida,
            CASE WHEN s.data_saida IS NULL THEN CAST(DATEDIFF(SECOND, e.data_entrada, GETDATE()) AS BIGINT)
                 ELSE CAST(DATEDIFF(SECOND, e.data_entrada, s.data_saida) AS BIGINT) END AS tempo_segundos,
            CASE WHEN s.data_saida IS NULL THEN 1 ELSE 0 END AS sem_saida
        FROM #Entradas e
        LEFT JOIN #Saidas s ON e.placa = s.placa AND e.data_entrada = s.data_entrada_ref
    ),
    MedianaCalculada AS (
        SELECT DISTINCT id_area_monitorada,
            CAST(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY tempo_segundos) OVER (PARTITION BY id_area_monitorada) AS BIGINT) AS mediana_segundos
        FROM PermanenciasCalculadas
    )
    SELECT 
        pc.id_area_monitorada,
        am.nome AS nome_area_monitorada,
        COUNT(DISTINCT pc.placa) AS total_veiculos_distintos,
        COUNT(*) AS total_permanencias,
        SUM(pc.sem_saida) AS permanencias_sem_saida,
        COUNT(*) - SUM(pc.sem_saida) AS permanencias_finalizadas,
        
        FORMAT(AVG(pc.tempo_segundos) / 86400, '00') + 'd ' +
        FORMAT((AVG(pc.tempo_segundos) % 86400) / 3600, '00') + 'h ' +
        FORMAT((AVG(pc.tempo_segundos) % 3600) / 60, '00') + 'm ' +
        FORMAT(AVG(pc.tempo_segundos) % 60, '00') + 's' AS tempo_medio_permanencia,
        
        FORMAT(SUM(pc.tempo_segundos) / 86400, '00') + 'd ' +
        FORMAT((SUM(pc.tempo_segundos) % 86400) / 3600, '00') + 'h ' +
        FORMAT((SUM(pc.tempo_segundos) % 3600) / 60, '00') + 'm ' +
        FORMAT(SUM(pc.tempo_segundos) % 60, '00') + 's' AS tempo_total_acumulado,

        FORMAT(MAX(mc.mediana_segundos) / 86400, '00') + 'd ' +
        FORMAT((MAX(mc.mediana_segundos) % 86400) / 3600, '00') + 'h ' +
        FORMAT((MAX(mc.mediana_segundos) % 3600) / 60, '00') + 'm ' +
        FORMAT(MAX(mc.mediana_segundos) % 60, '00') + 's' AS tempo_mediano_permanencia,
        
        MIN(pc.data_entrada) AS primeira_entrada_periodo,
        MAX(ISNULL(pc.data_saida, GETDATE())) AS ultima_saida_periodo
    FROM PermanenciasCalculadas pc
    INNER JOIN muralha.area_monitorada am WITH (NOLOCK) ON pc.id_area_monitorada = am.id
    LEFT JOIN MedianaCalculada mc ON pc.id_area_monitorada = mc.id_area_monitorada
    WHERE am.deletado = 0 -- GARANTIA FINAL: Apenas estatísticas de áreas ativas
    GROUP BY pc.id_area_monitorada, am.nome
    ORDER BY total_permanencias DESC
    OPTION (MAXDOP 4);

    DROP TABLE IF EXISTS #DadosBase;
    DROP TABLE IF EXISTS #Entradas;
    DROP TABLE IF EXISTS #Saidas;
END;

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','SEU7J11','2025-02-26','2026-02-23'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','SEU7J11','2025-07-10','2026-02-23'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','PZJ3H82','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','SXF7H59','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','AAY4A05','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '65','','2025-07-10','2025-10-05'

--exec [muralha].[spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado] '','','2025-07-10','2025-10-05'

-- select * from muralha.area_monitorada where id in(70,69,66)


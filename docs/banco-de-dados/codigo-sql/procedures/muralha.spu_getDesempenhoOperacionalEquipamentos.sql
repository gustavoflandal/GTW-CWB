CREATE   PROCEDURE muralha.spu_getDesempenhoOperacionalEquipamentos
    @Data_Ini DATETIME,
    @Data_Fim DATETIME,
    @Id_Local INT = NULL,
    @Id_Municipio INT = NULL,
    @Id_Regiao TINYINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    IF @Data_Ini IS NULL OR @Data_Fim IS NULL
       OR CAST(@Data_Ini AS DATE) > CAST(@Data_Fim AS DATE)
    BEGIN
        THROW 50001, 'Periodo de consulta invalido.', 1;
    END;

    DECLARE @Inicio DATETIME2(3) = CAST(CAST(@Data_Ini AS DATE) AS DATETIME2(3));
    DECLARE @FimExclusivo DATETIME2(3) = DATEADD(DAY, 1, CAST(CAST(@Data_Fim AS DATE) AS DATETIME2(3)));
    DECLARE @Agora DATETIME2(3) = SYSDATETIME();
    DECLARE @Fim DATETIME2(3) = CASE WHEN @FimExclusivo > @Agora THEN @Agora ELSE @FimExclusivo END;

    IF @Inicio >= @Fim
    BEGIN
        THROW 50002, 'Periodo ainda nao iniciado.', 1;
    END;

    DECLARE @MsPeriodo BIGINT = DATEDIFF_BIG(MILLISECOND, @Inicio, @Fim);

    -- 1. Equipamentos filtrados
    SELECT l.id_local, RTRIM(l.nome) AS equipamento, l.serie_equipamento
    INTO #Equipamentos
    FROM dbo.local_vigente l
    WHERE l.desativado = 0
      AND (@Id_Local IS NULL OR l.id_local = @Id_Local)
      AND (
            (@Id_Municipio IS NULL AND @Id_Regiao IS NULL)
            OR EXISTS (
                SELECT 1
                FROM dbo.local_pista_vigente lp
                WHERE lp.id_local = l.id_local
                  AND lp.desativado = 0
                  AND (@Id_Municipio IS NULL OR lp.id_localidade = @Id_Municipio)
                  AND (@Id_Regiao IS NULL OR lp.id_regiao = @Id_Regiao)
            )
      );

    CREATE UNIQUE CLUSTERED INDEX IX_Equipamentos ON #Equipamentos(id_local);

    -- 2. Eventos dentro do periodo + ultimo evento anterior
    SELECT e.id_local, la.id, CAST(la.data AS DATETIME2(3)) AS data, la.id_tipo
    INTO #Eventos
    FROM #Equipamentos e
    INNER JOIN dbo.log_alerta la
      ON la.serial = CONVERT(VARCHAR(15), e.serie_equipamento)
    WHERE la.id_tipo IN (112, 113)
      AND la.data >= @Inicio
      AND la.data < @Fim

    UNION ALL

    SELECT e.id_local, anterior.id, CAST(anterior.data AS DATETIME2(3)), anterior.id_tipo
    FROM #Equipamentos e
    CROSS APPLY (
        SELECT TOP (1) la.id, la.data, la.id_tipo
        FROM dbo.log_alerta la
        WHERE la.serial = CONVERT(VARCHAR(15), e.serie_equipamento)
          AND la.id_tipo IN (112, 113)
          AND la.data < @Inicio
        ORDER BY la.data DESC, la.id DESC
    ) anterior;

    CREATE CLUSTERED INDEX IX_Eventos ON #Eventos(id_local, data, id);

    -- 3. Intervalos. Herdado = evento anterior ao inicio da janela.
    ;WITH EventosOrdenados AS (
        SELECT id_local, id_tipo, data,
               LEAD(data) OVER (PARTITION BY id_local ORDER BY data, id) AS proxima_data,
               LEAD(id_tipo) OVER (PARTITION BY id_local ORDER BY data, id) AS proximo_tipo,
               LAG(id_tipo) OVER (PARTITION BY id_local ORDER BY data, id) AS tipo_anterior
        FROM #Eventos
    ),
    Intervalos AS (
        SELECT id_local, id_tipo, tipo_anterior, proximo_tipo,
               data AS data_evento,
               CASE WHEN data < @Inicio THEN @Inicio ELSE data END AS inicio_intervalo,
               CASE WHEN proxima_data IS NULL OR proxima_data > @Fim
                    THEN @Fim ELSE proxima_data END AS fim_intervalo,
               CASE WHEN data < @Inicio THEN 1 ELSE 0 END AS estado_herdado
        FROM EventosOrdenados
    )
    SELECT id_local, id_tipo, tipo_anterior, proximo_tipo, data_evento,
           inicio_intervalo, fim_intervalo, estado_herdado,
           DATEDIFF_BIG(MILLISECOND, inicio_intervalo, fim_intervalo) AS milissegundos
    INTO #Intervalos
    FROM Intervalos
    WHERE inicio_intervalo < fim_intervalo;

    -- 4. Consolidacao
    SELECT e.id_local, e.equipamento, e.serie_equipamento,
           COALESCE(SUM(CASE WHEN i.id_tipo = 112 THEN i.milissegundos ELSE 0 END), 0) AS ms_conectado,
           COALESCE(SUM(CASE WHEN i.id_tipo = 113 THEN i.milissegundos ELSE 0 END), 0) AS ms_desconectado,
           COALESCE(SUM(CASE WHEN i.estado_herdado = 1 THEN i.milissegundos ELSE 0 END), 0) AS ms_estado_herdado,
           COALESCE(SUM(CASE WHEN i.estado_herdado = 0 THEN i.milissegundos ELSE 0 END), 0) AS ms_estado_iniciado_periodo,
           MAX(CASE WHEN i.estado_herdado = 1 THEN i.data_evento END) AS data_evento_anterior,
           COALESCE(SUM(CASE
               WHEN i.id_tipo = 113 AND i.tipo_anterior = 112 AND i.data_evento >= @Inicio
               THEN 1 ELSE 0 END), 0) AS interrupcoes,
           COALESCE(SUM(CASE
               WHEN i.id_tipo = 113 AND i.proximo_tipo = 112 AND i.data_evento >= @Inicio
               THEN 1 ELSE 0 END), 0) AS interrupcoes_concluidas,
           COALESCE(SUM(CASE
               WHEN i.id_tipo = 113 AND i.proximo_tipo = 112 AND i.data_evento >= @Inicio
               THEN i.milissegundos ELSE 0 END), 0) AS ms_interrupcoes_concluidas,
           COALESCE(MAX(CASE WHEN i.id_tipo = 113 THEN i.milissegundos END), 0) AS maior_interrupcao_ms
    INTO #DesempenhoBase
    FROM #Equipamentos e
    LEFT JOIN #Intervalos i ON i.id_local = e.id_local
    GROUP BY e.id_local, e.equipamento, e.serie_equipamento;

    -- 5. Indicadores por equipamento
    SELECT b.id_local, b.equipamento, b.serie_equipamento,
           CAST(b.ms_conectado / 3600000.0 AS DECIMAL(18,2)) AS horas_conectado,
           CAST(b.ms_desconectado / 3600000.0 AS DECIMAL(18,2)) AS horas_desconectado,
           CAST((@MsPeriodo - b.ms_conectado - b.ms_desconectado) / 3600000.0 AS DECIMAL(18,2)) AS horas_sem_informacao,
           CAST(100.0 * b.ms_conectado / NULLIF(b.ms_conectado + b.ms_desconectado, 0) AS DECIMAL(6,2)) AS percentual_conectado,
           CAST(100.0 * (b.ms_conectado + b.ms_desconectado) / NULLIF(@MsPeriodo, 0) AS DECIMAL(6,2)) AS cobertura_historico,
           b.interrupcoes, b.interrupcoes_concluidas,
           CAST(b.maior_interrupcao_ms / 60000.0 AS DECIMAL(18,2)) AS maior_interrupcao_minutos,
           CAST(b.ms_interrupcoes_concluidas / NULLIF(b.interrupcoes_concluidas, 0) / 60000.0 AS DECIMAL(18,2)) AS tempo_medio_interrupcao_minutos,
           -- Novos indicadores; nao substituem os campos existentes.
           CAST(b.ms_estado_herdado / 3600000.0 AS DECIMAL(18,2)) AS horas_estado_inferido,
           CAST(b.ms_estado_iniciado_periodo / 3600000.0 AS DECIMAL(18,2)) AS horas_estado_observado,
           CAST(100.0 * b.ms_estado_herdado / NULLIF(@MsPeriodo, 0) AS DECIMAL(6,2)) AS cobertura_inferida,
           CAST(100.0 * b.ms_estado_iniciado_periodo / NULLIF(@MsPeriodo, 0) AS DECIMAL(6,2)) AS cobertura_observada,
           b.data_evento_anterior AS data_ultimo_evento_anterior,
           CAST(CASE WHEN b.ms_estado_herdado > 0 THEN 1 ELSE 0 END AS BIT) AS possui_estado_inferido
    INTO #Desempenho
    FROM #DesempenhoBase b;

    -- RESULTSET 1: mesmas colunas do contrato atual
    SELECT COUNT(*) AS total_equipamentos,
           COALESCE(SUM(CASE WHEN percentual_conectado IS NOT NULL THEN 1 ELSE 0 END), 0) AS equipamentos_com_historico,
           COALESCE(SUM(CASE WHEN percentual_conectado IS NULL THEN 1 ELSE 0 END), 0) AS equipamentos_sem_historico,
           COALESCE(SUM(interrupcoes), 0) AS total_interrupcoes,
           COALESCE(SUM(interrupcoes_concluidas), 0) AS total_interrupcoes_concluidas,
           CAST(COALESCE(SUM(horas_conectado), 0) AS DECIMAL(18,2)) AS total_horas_conectado,
           CAST(COALESCE(SUM(horas_desconectado), 0) AS DECIMAL(18,2)) AS total_horas_desconectado
    FROM #Desempenho;

    -- RESULTSET 2: campos antigos + novos ao final
    SELECT id_local, equipamento, serie_equipamento,
           horas_conectado, horas_desconectado, horas_sem_informacao,
           percentual_conectado, cobertura_historico,
           interrupcoes, interrupcoes_concluidas,
           maior_interrupcao_minutos, tempo_medio_interrupcao_minutos,
           horas_estado_inferido, horas_estado_observado,
           cobertura_inferida, cobertura_observada,
           data_ultimo_evento_anterior, possui_estado_inferido
    FROM #Desempenho
    ORDER BY percentual_conectado DESC, equipamento;
END;
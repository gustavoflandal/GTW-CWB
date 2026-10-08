/*
===============================================================================
Nome da Rotina: [muralha].[spu_correlacionamento_placas_base]

Descrição:
    Correlação de placas por co-ocorrência no mesmo ponto (PCL) dentro de uma 
    janela temporal. Estrutura base para análise de correlações entre veículos.

Formato de Saída:
    - placa:        VARCHAR(10)  -- Placa informada ou correlacionada
    - passagens:    BIGINT       -- Número de correlações (0 = placa informada)
    - incidencia:   CHAR(1)      -- 'F'/'M'/'A' ou NULL (placa informada)
    - monitorado:   BIT          -- Está no cadastro de veículos monitorados
    - alerta:       BIT          -- Possui alertas ativos
    - boletim:      BIT          -- Possui registros de fato (BO)
    - antecedentes: BIT          -- Proprietário com antecedentes criminais

Parâmetros:
    @placa_informada            VARCHAR(10)
    @data_inicio                DATETIME
    @data_final                 DATETIME
    @tempo_passagem_minutos     INT      (default = 3)
    @considerar_antes_depois    BIT      (1: +/- janela; 0: [t, t+janela])
    @num_min_passagens_correlacionadas INT      (default = 3) -- mínimo de passagens correlacionadas

Autor: Thiago Guilsotti
Data de Criação: 04/09/2025
Versão: 2.0 - Formato Unificado

Exemplo:
    EXEC muralha.spu_correlacionamento_placas_base
        @placa_informada='SEU7J11',
        @data_inicio='2025-01-01 00:00:00',
        @data_final='2025-12-31 23:59:59',
        @tempo_passagem_minutos=3,
        @considerar_antes_depois=1,
        @num_min_passagens_correlacionadas=3;
===============================================================================
*/
CREATE   PROCEDURE [muralha].[spu_correlacionamento_placas_base]
    @placa_informada           VARCHAR(10),
    @data_inicio               DATETIME    = NULL,
    @data_final                DATETIME    = NULL,
    @tempo_passagem_minutos    INT         = 3,
    @considerar_antes_depois   BIT         = 1,
    @num_min_passagens_correlacionadas INT     = 3
AS
BEGIN
    SET NOCOUNT ON;

    -- Faixas de passagens para classificação de incidência.
    -- Carregadas da tabela muralha.config_chave_valor quando disponíveis.
    -- Na ausência da tabela ou das chaves, permanecem os valores padrão.
    -- Regras:
    --   * Sem correlação: passagens entre 1 e (@passagens_correlacao_baixa - 1).
    --     Se @passagens_correlacao_baixa = 1, a faixa "sem correlação" é desconsiderada
    --     (BETWEEN 1 AND 0 nunca é verdadeiro).
    --   * Baixa (F): passagens = @passagens_correlacao_baixa
    --   * Média (M): passagens = @passagens_correlacao_media
    --   * Alta  (A): passagens >= @passagens_correlacao_alta_min
    DECLARE @passagens_correlacao_baixa INT = 3;
    DECLARE @passagens_correlacao_media INT = 4;
    DECLARE @passagens_correlacao_alta_min INT = 5;

    IF OBJECT_ID('muralha.config_chave_valor', 'U') IS NOT NULL
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM muralha.config_chave_valor
            WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA'
        )
        BEGIN
            INSERT INTO muralha.config_chave_valor (chave, valor)
            VALUES ('VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA', CAST(@passagens_correlacao_baixa AS VARCHAR(10)));
        END;

        IF NOT EXISTS (
            SELECT 1
            FROM muralha.config_chave_valor
            WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA'
        )
        BEGIN
            INSERT INTO muralha.config_chave_valor (chave, valor)
            VALUES ('VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA', CAST(@passagens_correlacao_media AS VARCHAR(10)));
        END;

        IF NOT EXISTS (
            SELECT 1
            FROM muralha.config_chave_valor
            WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA'
        )
        BEGIN
            INSERT INTO muralha.config_chave_valor (chave, valor)
            VALUES ('VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA', CAST(@passagens_correlacao_alta_min AS VARCHAR(10)));
        END;

        SELECT @passagens_correlacao_baixa = COALESCE(
            (SELECT TOP(1) TRY_CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA'),
            @passagens_correlacao_baixa
        );

        SELECT @passagens_correlacao_media = COALESCE(
            (SELECT TOP(1) TRY_CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA'),
            @passagens_correlacao_media
        );

        SELECT @passagens_correlacao_alta_min = COALESCE(
            (SELECT TOP(1) TRY_CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA'),
            @passagens_correlacao_alta_min
        );
    END;
    
    -- Anti-Parameter Sniffing: Cópia local dos parâmetros
    DECLARE @placa_informada_local VARCHAR(10) = @placa_informada;
    DECLARE @data_inicio_local DATETIME = @data_inicio;
    DECLARE @data_final_local DATETIME = @data_final;
    DECLARE @tempo_passagem_local INT = @tempo_passagem_minutos;
    DECLARE @num_min_passagens_correlacionadas_local INT = CASE
        WHEN @num_min_passagens_correlacionadas IS NULL OR @num_min_passagens_correlacionadas < 1 THEN 1
        ELSE @num_min_passagens_correlacionadas
    END;
    DECLARE @considerar_antes_depois_local BIT = @considerar_antes_depois;

    /*=======================================================================
    [ETAPA 1] PASSAGENS DA PLACA INFORMADA + JANELAS TEMPORAIS
    =======================================================================*/
    DROP TABLE IF EXISTS #PassagensBase;
    CREATE TABLE #PassagensBase (
        id        UNIQUEIDENTIFIER NOT NULL,
        placa     VARCHAR(10)      NOT NULL,
        data      DATETIME         NOT NULL,
        id_local  INT              NOT NULL,
        win_start DATETIME         NOT NULL,
        win_end   DATETIME         NOT NULL
    );

    -- Cálculo das janelas temporais
    INSERT INTO #PassagensBase (id, placa, data, id_local, win_start, win_end)
    SELECT 
        vtr.id, 
        vtr.placa, 
        vtr.data, 
        vtr.id_local,
        CASE WHEN @considerar_antes_depois_local = 1
            THEN DATEADD(minute, -@tempo_passagem_local, vtr.data)
            ELSE vtr.data
        END AS win_start,
        DATEADD(minute, @tempo_passagem_local, vtr.data) AS win_end
    FROM muralha.veiculo_tempo_real AS vtr
    WHERE vtr.placa = @placa_informada_local
        AND (@data_inicio_local IS NULL OR vtr.data >= @data_inicio_local)
        AND (@data_final_local IS NULL OR vtr.data <= @data_final_local)
    OPTION (RECOMPILE);

    -- Índice para lookup por local e janela temporal
    CREATE CLUSTERED INDEX CX_Base ON #PassagensBase (id_local, win_start, win_end);

    /*=======================================================================
    [ETAPA 2] CORRELAÇÕES + AGREGAÇÃO
    =======================================================================*/
    DROP TABLE IF EXISTS #Correlacoes;
    CREATE TABLE #Correlacoes (
        placa_correlacionada VARCHAR(10) NULL,
        passagens           BIGINT       NOT NULL
    );

    INSERT INTO #Correlacoes (placa_correlacionada, passagens)
    SELECT 
        vtr.placa AS placa_correlacionada,
        COUNT_BIG(*) AS passagens
    FROM #PassagensBase AS pb
    INNER JOIN muralha.veiculo_tempo_real AS vtr
        ON vtr.id_local = pb.id_local
        AND vtr.data BETWEEN pb.win_start AND pb.win_end
        AND (vtr.placa <> pb.placa OR vtr.placa IS NULL)
        AND (@data_inicio_local IS NULL OR vtr.data >= @data_inicio_local)
        AND (@data_final_local IS NULL OR vtr.data <= @data_final_local)
    GROUP BY vtr.placa
    OPTION (RECOMPILE);

    -- Índice para lookup rápido por placa
    CREATE UNIQUE CLUSTERED INDEX CX_Corr ON #Correlacoes (placa_correlacionada);

    /*=======================================================================
    [ETAPA 3] FLAGS DE SEGURANÇA
    =======================================================================*/
    ;WITH PlacasEnvolvidas AS (
        -- Placa informada
        SELECT @placa_informada_local AS placa
        UNION ALL
        -- Placas correlacionadas
        SELECT placa_correlacionada FROM #Correlacoes
    ),
    FlagsSeguranca AS (
        SELECT 
            pe.placa,
            -- Flags de segurança em uma única passada
            CASE WHEN EXISTS (
                SELECT 1 FROM muralha.cad_veiculo_monitorado vm WHERE vm.placa = pe.placa
            ) THEN 1 ELSE 0 END AS monitorado,
            
            CASE WHEN EXISTS (
                SELECT 1 FROM muralha.cad_veiculo_monitorado vm 
                INNER JOIN muralha.alerta al ON al.id_cad_veiculo_monitorado = vm.id
                WHERE vm.placa = pe.placa
            ) THEN 1 ELSE 0 END AS alerta,
            
            CASE WHEN EXISTS (
                SELECT 1 FROM muralha.registro_fato_veiculo bl WHERE bl.placa = pe.placa
            ) THEN 1 ELSE 0 END AS boletim,
            
            CASE WHEN EXISTS (
                SELECT 1 FROM dbo.cadastro_veiculo v 
                INNER JOIN muralha.proprietario_veiculo pv ON pv.placa = v.placa
                INNER JOIN muralha.antecedentes_criminais ac ON ac.id_proprietario = pv.id_proprietario
                WHERE v.placa = pe.placa
            ) THEN 1 ELSE 0 END AS antecedentes
        FROM PlacasEnvolvidas pe
    ),
    FlagsConsolidados AS (
        SELECT 
            placa,
            monitorado,
            alerta,
            boletim,
            antecedentes
        FROM FlagsSeguranca
    )
    /*=======================================================================
    [ETAPA 4] RESULTADO FINAL
    =======================================================================*/
    SELECT 
        placa,
        passagens,
        incidencia,
        monitorado,
        alerta,
        boletim,
        antecedentes
    FROM (
        -- 1) Placa informada (passagens = 0, incidencia = NULL)
        SELECT 
            @placa_informada_local AS placa,
            CAST(0 AS BIGINT) AS passagens,
            CAST(NULL AS CHAR(1)) AS incidencia,
            ISNULL(fc.monitorado, 0) AS monitorado,
            ISNULL(fc.alerta, 0) AS alerta,
            ISNULL(fc.boletim, 0) AS boletim,
            ISNULL(fc.antecedentes, 0) AS antecedentes,
            0 AS ordem  -- Para garantir que fique no topo
        FROM FlagsConsolidados fc
        WHERE fc.placa = @placa_informada_local
        
        UNION ALL
        
        -- 2) Placas correlacionadas (com incidência calculada)
        SELECT 
            c.placa_correlacionada AS placa,
            c.passagens,
            CASE
                WHEN c.passagens BETWEEN 1 AND (@passagens_correlacao_baixa - 1) THEN NULL
                WHEN c.passagens = @passagens_correlacao_baixa THEN 'F'
                WHEN c.passagens = @passagens_correlacao_media THEN 'M'
                WHEN c.passagens >= @passagens_correlacao_alta_min THEN 'A'
                ELSE NULL
            END AS incidencia,
            ISNULL(fc.monitorado, 0) AS monitorado,
            ISNULL(fc.alerta, 0) AS alerta,
            ISNULL(fc.boletim, 0) AS boletim,
            ISNULL(fc.antecedentes, 0) AS antecedentes,
            1 AS ordem  -- Placas correlacionadas vêm depois
        FROM #Correlacoes c
        LEFT JOIN FlagsConsolidados fc ON fc.placa = c.placa_correlacionada
    ) ResultadoFinal
    WHERE passagens = 0
        OR (
            incidencia IS NOT NULL
            AND passagens >= @num_min_passagens_correlacionadas_local
        )
    ORDER BY ordem, passagens DESC
    OPTION (RECOMPILE);

    /*=======================================================================
    [LIMPEZA]
    =======================================================================*/
    DROP TABLE IF EXISTS #PassagensBase;
    DROP TABLE IF EXISTS #Correlacoes;
END

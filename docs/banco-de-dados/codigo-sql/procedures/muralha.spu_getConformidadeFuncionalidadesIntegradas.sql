
CREATE   PROCEDURE muralha.spu_getConformidadeFuncionalidadesIntegradas
    @dataInicio DATETIME,
    @dataFim DATETIME,
    @idLocal INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @fimExclusivo DATETIME =
        DATEADD(DAY, 1, CONVERT(DATE, @dataFim));

    DECLARE @resultado TABLE
    (
        indicador VARCHAR(60),
        total BIGINT,
        integros BIGINT,
        divergentes BIGINT,
        sem_conferencia BIGINT,
        erro_leitura BIGINT,
        enviados BIGINT,
        nao_enviados BIGINT,
        percentual DECIMAL(9,2)
    );

    -- 1. Integridade das imagens
    INSERT INTO @resultado
    SELECT
        'INTEGRIDADE_IMAGENS',
        COUNT_BIG(*),

        COALESCE(SUM(CASE
            WHEN status_integridade = 'INTEGRO'
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        COALESCE(SUM(CASE
            WHEN status_integridade = 'DIVERGENTE'
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        COALESCE(SUM(CASE
            WHEN status_integridade = 'SEM_CONFERENCIA'
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        COALESCE(SUM(CASE
            WHEN status_integridade = 'ERRO_LEITURA'
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        NULL,
        NULL,

        CAST(
            100.0 *
            SUM(CASE WHEN status_integridade = 'INTEGRO'
                THEN 1 ELSE 0 END)
            /
            NULLIF(
                SUM(CASE WHEN status_integridade IN
                    ('INTEGRO', 'DIVERGENTE')
                    THEN 1 ELSE 0 END),
                0
            )
            AS DECIMAL(9,2)
        )
    FROM muralha.vtr_imagem_integridade
    WHERE dt_captura >= @dataInicio
      AND dt_captura < @fimExclusivo
      AND (@idLocal IS NULL OR id_local = @idLocal);

    -- 2. Envio de alertas
    INSERT INTO @resultado
    SELECT
        'ENVIO_ALERTAS',
        COUNT_BIG(*),
        NULL, NULL, NULL, NULL,

        COALESCE(SUM(CASE WHEN enviado_cliente = 1
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        COALESCE(SUM(CASE WHEN enviado_cliente = 0
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        CAST(
            100.0 *
            SUM(CASE WHEN enviado_cliente = 1
                THEN 1 ELSE 0 END)
            / NULLIF(COUNT_BIG(*), 0)
            AS DECIMAL(9,2)
        )
    FROM muralha.alerta
    WHERE data >= @dataInicio
      AND data < @fimExclusivo;

    -- 3. Envio de veículos em tempo real
    INSERT INTO @resultado
    SELECT
        'ENVIO_VEICULOS',
        COUNT_BIG(*),
        NULL, NULL, NULL, NULL,

        COALESCE(SUM(CASE WHEN enviado_cliente = 1
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        COALESCE(SUM(CASE WHEN enviado_cliente = 0
            THEN CAST(1 AS BIGINT) ELSE 0 END), 0),

        CAST(
            100.0 *
            SUM(CASE WHEN enviado_cliente = 1
                THEN 1 ELSE 0 END)
            / NULLIF(COUNT_BIG(*), 0)
            AS DECIMAL(9,2)
        )
    FROM muralha.veiculo_tempo_real
    WHERE data >= @dataInicio
      AND data < @fimExclusivo
      AND (@idLocal IS NULL OR id_local = @idLocal);

    SELECT
        indicador,
        total,
        integros,
        divergentes,
        sem_conferencia,
        erro_leitura,
        enviados,
        nao_enviados,
        percentual
    FROM @resultado;
END;

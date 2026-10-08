CREATE   PROCEDURE [muralha].[spu_correlacionamento_placas_eventos]
(
    @data_inicio DATETIME = NULL,
    @data_final DATETIME = NULL,
    @tempo_passagem_minutos INT = 3,
    @considerar_antes_depois BIT = 1
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @data_inicio_local DATETIME = @data_inicio;
    DECLARE @data_final_local DATETIME = @data_final;
    DECLARE @tempo_passagem_local INT = @tempo_passagem_minutos;
    DECLARE @considerar_antes_depois_local BIT = @considerar_antes_depois;

    /*=========================================================
    ETAPA 1 – PLACAS BASE (REGISTRO DE FATO)
    =========================================================*/

    DROP TABLE IF EXISTS #PlacasBase;

    SELECT
        rfv.placa
    INTO #PlacasBase
    FROM muralha.registro_fato_veiculo rfv
    GROUP BY rfv.placa;

    /*=========================================================
    ETAPA 2 – PASSAGENS DAS PLACAS BASE
    =========================================================*/

    DROP TABLE IF EXISTS #PassagensBase;

    CREATE TABLE #PassagensBase
    (
        placa_base VARCHAR(10),
        data_base DATETIME,
        id_local INT,
        win_start DATETIME,
        win_end DATETIME
    );

    INSERT INTO #PassagensBase
    SELECT
        vtr.placa AS placa_base,
        vtr.data,
        vtr.id_local,

        CASE
            WHEN @considerar_antes_depois_local = 1
            THEN DATEADD(MINUTE,-@tempo_passagem_local,vtr.data)
            ELSE vtr.data
        END,

        DATEADD(MINUTE,@tempo_passagem_local,vtr.data)

    FROM muralha.veiculo_tempo_real vtr
    INNER JOIN #PlacasBase pb
        ON pb.placa = vtr.placa
    WHERE
        (@data_inicio_local IS NULL OR vtr.data >= @data_inicio_local)
    AND (@data_final_local IS NULL OR vtr.data <= @data_final_local);

    CREATE CLUSTERED INDEX IX_PassagensBase
        ON #PassagensBase (id_local, win_start, win_end);

    /*=========================================================
    ETAPA 3 – CORRELAÇÕES EVENTO A EVENTO
    =========================================================*/

    DROP TABLE IF EXISTS #Correlacoes;

    SELECT
        pb.placa_base,
        vtr.placa AS placa_correlacionada,
        pb.id_local,
        pb.data_base,
        vtr.data AS data_correlacionada
    INTO #Correlacoes
    FROM #PassagensBase pb
    INNER JOIN muralha.veiculo_tempo_real vtr
        ON vtr.id_local = pb.id_local
        AND vtr.data BETWEEN pb.win_start AND pb.win_end
        AND vtr.placa <> pb.placa_base
    WHERE
        (@data_inicio_local IS NULL OR vtr.data >= @data_inicio_local)
    AND (@data_final_local IS NULL OR vtr.data <= @data_final_local);

    CREATE INDEX IX_Correlacoes_Placa
        ON #Correlacoes (placa_correlacionada);

    /*=========================================================
    ETAPA 4 – FLAGS DE SEGURANÇA
    =========================================================*/

    ;WITH PlacasEnvolvidas AS
    (
        SELECT placa_base AS placa FROM #Correlacoes
        UNION
        SELECT placa_correlacionada FROM #Correlacoes
    ),

    FlagsSeguranca AS
    (
        SELECT
            pe.placa,

            CASE WHEN EXISTS
            (
                SELECT 1
                FROM muralha.cad_veiculo_monitorado vm
                WHERE vm.placa = pe.placa
            )
            THEN 1 ELSE 0 END AS monitorado,

            CASE WHEN EXISTS
            (
                SELECT 1
                FROM muralha.cad_veiculo_monitorado vm
                INNER JOIN muralha.alerta al
                    ON al.id_cad_veiculo_monitorado = vm.id
                WHERE vm.placa = pe.placa
            )
            THEN 1 ELSE 0 END AS alerta,

            CASE WHEN EXISTS
            (
                SELECT 1
                FROM muralha.registro_fato_veiculo bl
                WHERE bl.placa = pe.placa
            )
            THEN 1 ELSE 0 END AS boletim,

            CASE WHEN EXISTS
            (
                SELECT 1
                FROM dbo.cadastro_veiculo v
                INNER JOIN muralha.proprietario_veiculo pv
                    ON pv.placa = v.placa
                INNER JOIN muralha.antecedentes_criminais ac
                    ON ac.id_proprietario = pv.id_proprietario
                WHERE v.placa = pe.placa
            )
            THEN 1 ELSE 0 END AS antecedentes

        FROM PlacasEnvolvidas pe
    )

    /*=========================================================
    RESULTADO FINAL
    =========================================================*/

    SELECT
        c.placa_base,
        c.placa_correlacionada,
        c.id_local,
        c.data_base,
        c.data_correlacionada,

        ISNULL(f.monitorado,0) AS monitorado,
        ISNULL(f.alerta,0) AS alerta,
        ISNULL(f.boletim,0) AS boletim,
        ISNULL(f.antecedentes,0) AS antecedentes

    FROM #Correlacoes c
    LEFT JOIN FlagsSeguranca f
        ON f.placa = c.placa_correlacionada

    ORDER BY
        c.data_base DESC,
        c.placa_base;

    /*=========================================================
    LIMPEZA
    =========================================================*/

    DROP TABLE IF EXISTS #PlacasBase;
    DROP TABLE IF EXISTS #PassagensBase;
    DROP TABLE IF EXISTS #Correlacoes;

END
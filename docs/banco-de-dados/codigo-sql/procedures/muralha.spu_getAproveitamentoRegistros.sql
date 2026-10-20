
CREATE   PROCEDURE muralha.spu_getAproveitamentoRegistros
    @Data_Ini      DATETIME,
    @Data_Fim      DATETIME,
    @Id_Local      INT = NULL,
    @Id_Municipio  INT = NULL,
    @Id_Regiao     TINYINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    /*
     * EDITAL 5.4.2.3 - ITEM F
     * Indicadores de Aproveitamento dos Registros
     *
     * 0 ou NULL = Todos
     *
     * Aproveitamento:
     * Percentual de registros com placa preenchida.
     */

    -- Normalização dos filtros
    SET @Id_Local = NULLIF(@Id_Local, 0);
    SET @Id_Municipio = NULLIF(@Id_Municipio, 0);
    SET @Id_Regiao = NULLIF(@Id_Regiao, 0);

    SELECT
        COUNT_BIG(*) AS total_registros,

        COALESCE(
            SUM(
                CASE
                    WHEN NULLIF(LTRIM(RTRIM(vtr.placa)), '') IS NOT NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ),
            0
        ) AS registros_com_placa,

        COALESCE(
            SUM(
                CASE
                    WHEN NULLIF(LTRIM(RTRIM(vtr.placa)), '') IS NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ),
            0
        ) AS registros_sem_placa,

        CAST(
            COALESCE(
                100.0 *
                SUM(
                    CASE
                        WHEN NULLIF(LTRIM(RTRIM(vtr.placa)), '') IS NOT NULL
                        THEN CAST(1 AS BIGINT)
                        ELSE CAST(0 AS BIGINT)
                    END
                )
                / NULLIF(COUNT_BIG(*), 0),
                0
            )
            AS DECIMAL(10, 2)
        ) AS percentual_aproveitamento

    FROM muralha.veiculo_tempo_real vtr

    WHERE
        -- Inclui todos os horários da data final
        vtr.data >= CAST(@Data_Ini AS DATE)
        AND vtr.data < DATEADD(
            DAY,
            1,
            CAST(@Data_Fim AS DATE)
        )

        -- Filtros de equipamento, município e região
        AND EXISTS
        (
            SELECT 1
            FROM local_vigente lv
            WHERE lv.id_local = vtr.id_local
              AND lv.desativado = 0

              AND (
                  @Id_Local IS NULL
                  OR lv.id_local = @Id_Local
              )

              AND (
                  @Id_Municipio IS NULL
                  OR lv.id_localidade = @Id_Municipio
              )

              AND (
                  @Id_Regiao IS NULL
                  OR lv.id_regiao = @Id_Regiao
              )
        );

END;
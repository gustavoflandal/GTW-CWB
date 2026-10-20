
CREATE   PROCEDURE muralha.spu_getIndicesClassificacaoVeicular
    @Data_Ini      DATETIME,
    @Data_Fim      DATETIME,
    @Id_Local      INT = NULL,
    @Id_Municipio  INT = NULL,
    @Id_Regiao     TINYINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    /*
     * EDITAL 5.4.2.3 - ITEM H
     *
     * H: Leitura de placas - 90%
     * F: Classificacao veicular - 90%
     * K: Excluir motos e periodo noturno de marca/modelo
     * M: Marca/modelo - 80%
     * P: Classificacao de cor
     *
     * Percentuais operacionais de identificacao.
     * Nao representam validacao independente de acerto.
     *
     * 0 ou NULL nos filtros = Todos.
     */

    SET @Id_Local = NULLIF(@Id_Local, 0);
    SET @Id_Municipio = NULLIF(@Id_Municipio, 0);
    SET @Id_Regiao = NULLIF(@Id_Regiao, 0);

    IF @Data_Ini IS NULL
       OR @Data_Fim IS NULL
       OR CAST(@Data_Ini AS DATE) > CAST(@Data_Fim AS DATE)
    BEGIN
        THROW 50001, 'Periodo informado invalido.', 1;
    END;

    DECLARE @Inicio DATETIME =
        CAST(CAST(@Data_Ini AS DATE) AS DATETIME);

    DECLARE @FimExclusivo DATETIME =
        DATEADD(DAY, 1, CAST(@Data_Fim AS DATE));

    DECLARE
        @TotalPassagens BIGINT = 0,
        @PlacasIdentificadas BIGINT = 0,
        @ClassificacoesIdentificadas BIGINT = 0,
        @CoresIdentificadas BIGINT = 0,
        @TotalElegivelMarcaModelo BIGINT = 0,
        @MarcaModeloIdentificados BIGINT = 0,
        @ClassificacaoDesconhecida BIGINT = 0,
        @SemCaracteristicas BIGINT = 0;

    /*
     * Uma unica agregacao para os quatro indicadores.
     *
     * O filtro de data permanece sobre vtr.data,
     * permitindo aproveitamento dos indices existentes.
     *
     * O relacionamento por id_captura utiliza a
     * chave UNIQUE de ia.veiculo_caracteristica.
     */

    SELECT
        @TotalPassagens = COUNT_BIG(*),

        @PlacasIdentificadas =
            COALESCE(SUM(
                CASE
                    WHEN NULLIF(
                        LTRIM(RTRIM(vtr.placa)), ''
                    ) IS NOT NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0),

        @ClassificacoesIdentificadas =
            COALESCE(SUM(
                CASE
                    WHEN vc.id_classificacao IS NOT NULL
                     AND LTRIM(RTRIM(vc.id_classificacao)) <> ''
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0),

        @CoresIdentificadas =
            COALESCE(SUM(
                CASE
                    WHEN vc.id_cor IS NOT NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0),

        @TotalElegivelMarcaModelo =
            COALESCE(SUM(
                CASE
                    WHEN vtr.classificacao IN ('P', 'C', 'O')
                     AND CAST(vtr.data AS TIME) >= '06:00:00'
                     AND CAST(vtr.data AS TIME) < '18:00:00'
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0),

        @MarcaModeloIdentificados =
            COALESCE(SUM(
                CASE
                    WHEN vtr.classificacao IN ('P', 'C', 'O')
                     AND CAST(vtr.data AS TIME) >= '06:00:00'
                     AND CAST(vtr.data AS TIME) < '18:00:00'
                     AND vc.id_marca IS NOT NULL
                     AND vc.id_modelo IS NOT NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0),

        @ClassificacaoDesconhecida =
            COALESCE(SUM(
                CASE
                    WHEN NULLIF(
                        LTRIM(RTRIM(vtr.classificacao)), ''
                    ) IS NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0),

        @SemCaracteristicas =
            COALESCE(SUM(
                CASE
                    WHEN vc.id_captura IS NULL
                    THEN CAST(1 AS BIGINT)
                    ELSE CAST(0 AS BIGINT)
                END
            ), 0)

    FROM muralha.veiculo_tempo_real vtr

    LEFT JOIN ia.veiculo_caracteristica vc
        ON vc.id_captura = vtr.id_captura

    WHERE
        vtr.data >= @Inicio
        AND vtr.data < @FimExclusivo

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

    /*
     * Retorno padronizado para DAO / Servlet / XML.
     *
     * Percentual NULL quando o denominador e zero
     * ou quando a fonte da IA ainda nao possui dados
     * para o indicador.
     */

    SELECT
        indicador,
        requisito,
        meta_percentual,
        total_avaliado,
        total_identificados,

        CAST(
            CASE
                WHEN total_avaliado = 0
                  OR (
                      depende_ia = 1
                      AND @TotalPassagens = @SemCaracteristicas
                  )
                THEN NULL
                ELSE 100.0 * total_identificados
                     / NULLIF(total_avaliado, 0)
            END
            AS DECIMAL(10,2)
        ) AS percentual,

        CASE
            WHEN total_avaliado = 0
              OR (
                  depende_ia = 1
                  AND @TotalPassagens = @SemCaracteristicas
              )
            THEN 'SEM_DADOS'

            ELSE 'DISPONIVEL'
        END AS situacao,

        @ClassificacaoDesconhecida
            AS classificacao_desconhecida,

        @SemCaracteristicas
            AS passagens_sem_caracteristicas

    FROM
    (
        VALUES
        (
            1,
            'LEITURA_PLACA',
            'H',
            CAST(90.00 AS DECIMAL(10,2)),
            @TotalPassagens,
            @PlacasIdentificadas,
            0
        ),
        (
            2,
            'CLASSIFICACAO_VEICULAR',
            'F',
            CAST(90.00 AS DECIMAL(10,2)),
            @TotalPassagens,
            @ClassificacoesIdentificadas,
            1
        ),
        (
            3,
            'MARCA_MODELO',
            'K/M',
            CAST(80.00 AS DECIMAL(10,2)),
            @TotalElegivelMarcaModelo,
            @MarcaModeloIdentificados,
            1
        ),
        (
            4,
            'CLASSIFICACAO_COR',
            'P',
            CAST(NULL AS DECIMAL(10,2)),
            @TotalPassagens,
            @CoresIdentificadas,
            1
        )
    ) AS indicadores
    (
        ordem,
        indicador,
        requisito,
        meta_percentual,
        total_avaliado,
        total_identificados,
        depende_ia
    )
    ORDER BY ordem;
END;

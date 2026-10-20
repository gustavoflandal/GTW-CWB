CREATE   PROCEDURE [muralha].[spu_getComportamentoFluxoViario]
    @Data_Ini      DATETIME,
    @Data_Fim      DATETIME,
    @Id_Local      INT = NULL,
    @Id_Municipio  INT = NULL,
    @Id_Regiao     TINYINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Consolida os dados uma única vez para gráfico e tabela.
    SELECT
        vp.hora,
        vp.id_local,
        vp.pista,
        SUM(CAST(vp.trafego AS BIGINT)) AS quantidade,
        CAST(
            SUM(
                CAST(vp.media_velocidade AS DECIMAL(28, 6))
                * CAST(vp.trafego AS DECIMAL(28, 6))
            )
            / NULLIF(SUM(CAST(vp.trafego AS DECIMAL(28, 6))), 0)
            AS DECIMAL(18, 2)
        ) AS velocidade_media
    INTO #Fluxo
    FROM veiculo_sumarizado vp
    WHERE
        vp.data BETWEEN CAST(@Data_Ini AS DATE)
                    AND CAST(@Data_Fim AS DATE)
        AND vp.id_local = ISNULL(@Id_Local, vp.id_local)
        AND (
            (vp.data > CAST(@Data_Ini AS DATE))
            OR (
                vp.data = CAST(@Data_Ini AS DATE)
                AND vp.hora >= DATEPART(HOUR, @Data_Ini)
            )
        )
        AND (
            (vp.data < CAST(@Data_Fim AS DATE))
            OR (
                vp.data = CAST(@Data_Fim AS DATE)
                AND vp.hora <= DATEPART(HOUR, @Data_Fim)
            )
        )
        AND EXISTS (
            SELECT 1
            FROM local_pista_vigente lpv
            WHERE lpv.id_local = vp.id_local
              AND lpv.id_pista = vp.pista
              AND lpv.desativado = 0
              AND (
                  @Id_Municipio IS NULL
                  OR lpv.id_localidade = @Id_Municipio
              )
              AND (
                  @Id_Regiao IS NULL
                  OR lpv.id_regiao = @Id_Regiao
              )
        )
    GROUP BY
        vp.hora,
        vp.id_local,
        vp.pista;

    -- Resultado 1: gráfico de fluxo total por hora.
    SELECT
        hora,
        SUM(quantidade) AS quantidade
    FROM #Fluxo
    GROUP BY hora
    ORDER BY hora;

    -- Resultado 2: tabela por equipamento e faixa.
    SELECT
        f.id_local,
        RTRIM(lpv.nome) AS equipamento,
        f.pista,
        lpv.cod_pista_alternativo AS faixa,
        SUM(f.quantidade) AS quantidade,
        CAST(
            SUM(
                CAST(f.velocidade_media AS DECIMAL(28, 6))
                * CAST(f.quantidade AS DECIMAL(28, 6))
            )
            / NULLIF(SUM(CAST(f.quantidade AS DECIMAL(28, 6))), 0)
            AS DECIMAL(18, 2)
        ) AS velocidade_media
    FROM #Fluxo f
    JOIN local_pista_vigente lpv
      ON lpv.id_local = f.id_local
     AND lpv.id_pista = f.pista
     AND lpv.desativado = 0
    GROUP BY
        f.id_local,
        lpv.nome,
        f.pista,
        lpv.cod_pista_alternativo
    ORDER BY
        f.id_local,
        lpv.cod_pista_alternativo;

    DROP TABLE #Fluxo;
END;
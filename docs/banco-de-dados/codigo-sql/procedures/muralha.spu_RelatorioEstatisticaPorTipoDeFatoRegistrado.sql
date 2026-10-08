
CREATE     PROCEDURE [muralha].[spu_RelatorioEstatisticaPorTipoDeFatoRegistrado]
    @DataInicio DATETIME = NULL,
    @DataFinal DATETIME = NULL
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

    IF @DataInicio IS NULL
    BEGIN
        SET @DataInicio = DATEADD(year, -1, GETDATE());
    END

    IF @DataFinal IS NULL
    BEGIN
        SET @DataFinal = GETDATE();
    END
    ELSE
    BEGIN
        SET @DataFinal = DATEADD(day, 1, @DataFinal);
    END

    ;WITH FatosUnicos AS (
        SELECT
            ta.tipo AS TipoFato,
            CAST(a.data AS DATE) AS DataFato,
            CASE DATENAME(dw, a.data)
                WHEN 'Monday'    THEN 'Segunda-feira'
                WHEN 'Tuesday'   THEN 'Terça-feira'
                WHEN 'Wednesday' THEN 'Quarta-feira'
                WHEN 'Thursday'  THEN 'Quinta-feira'
                WHEN 'Friday'    THEN 'Sexta-feira'
                WHEN 'Saturday'  THEN 'Sábado'
                WHEN 'Sunday'    THEN 'Domingo'
                ELSE DATENAME(dw, a.data)
            END AS DiaDaSemana,
            vtr.placa,
            l.posicao_lat AS Latitude,
            l.posicao_lon AS Longitude,
            a.data AS DataHoraFato,
            ROW_NUMBER() OVER(PARTITION BY a.id ORDER BY a.data) AS rn
        FROM
            muralha.alerta a
        INNER JOIN
            muralha.alerta_veiculo av ON a.id = av.id_alerta
        INNER JOIN
            muralha.veiculo_tempo_real vtr ON av.id_veiculo_tempo_real = vtr.id
        INNER JOIN
            dbo.local l ON vtr.id_local = l.id_local
        INNER JOIN
            muralha.tipo_alerta_ocorrencia ta ON a.id_tipo_alerta_ocorrencia = ta.id
        WHERE
            a.data >= @DataInicio
            AND a.data < @DataFinal
            AND l.posicao_lat IS NOT NULL
            AND l.posicao_lon IS NOT NULL
    )
    SELECT
        TipoFato,
        DataFato,
        DiaDaSemana,
        placa,
        Latitude,
        Longitude,
        DataHoraFato
    FROM
        FatosUnicos
    WHERE
        rn = 1
    ORDER BY
        TipoFato,
        DataHoraFato;
END

CREATE   PROCEDURE [dbo].[spu_monitoramento_mapa]
    @equipamentos ListaEquipamentos READONLY
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @possui_filtro BIT = (
        SELECT CASE
            WHEN COUNT(*) > 0 THEN 1
            ELSE 0
        END
        FROM @equipamentos
    );

    SELECT
        cem.posicao_lat,
        cem.posicao_lon,
        cem.localidade_desc + ' - ' + cem.nome AS descricao,

        -- Locais
        STUFF((
            SELECT ', ' + CAST(sub.id_local AS VARCHAR)
            FROM local_vigente AS sub
            WHERE sub.posicao_lat = cem.posicao_lat
              AND sub.posicao_lon = cem.posicao_lon
              AND sub.id_local = cem.id_local
            GROUP BY sub.id_local
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS id_locais,

        -- Tipo do alerta
        ISNULL(
            STUFF((
                SELECT ', ' + CAST(ISNULL(la_sub.id_tipo, 0) AS VARCHAR)
                FROM local_vigente AS lv_sub

                LEFT JOIN log_alerta AS la_sub
                    ON la_sub.serial = lv_sub.serie_equipamento

                INNER JOIN (
                    SELECT
                        serial,
                        MAX(data) AS ultima_data
                    FROM log_alerta
                    GROUP BY serial
                ) AS ult_evento
                    ON la_sub.serial = ult_evento.serial
                   AND la_sub.data = ult_evento.ultima_data

                WHERE lv_sub.posicao_lat = cem.posicao_lat
                  AND lv_sub.posicao_lon = cem.posicao_lon
                  AND lv_sub.id_local = cem.id_local

                GROUP BY la_sub.id_tipo

                FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)'), 1, 2, ''),
            '0'
        ) AS id_tipo

    FROM local_vigente AS cem

    WHERE cem.posicao_lat IS NOT NULL
      AND cem.posicao_lon IS NOT NULL
      AND cem.data_inicio IS NOT NULL
      AND (
            (@possui_filtro = 1
                AND cem.id_local IN (
                    SELECT id_local
                    FROM @equipamentos
                )
            )
            OR
            (@possui_filtro = 0)
          )

    GROUP BY
        cem.posicao_lat,
        cem.posicao_lon,
        cem.id_local,
        cem.localidade_desc,
        cem.nome

    ORDER BY id_locais;
END;
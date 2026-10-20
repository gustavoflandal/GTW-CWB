
-- 2a. Carga de passagens por hora ---------------------------------------------------
CREATE   PROCEDURE analise.spu_carga_passagens_hora
    @horas     INT          = 25,
    @dt_inicio DATETIME2(0) = NULL,
    @dt_fim    DATETIME2(0) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dt_exec DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        IF @dt_inicio IS NULL
            SET @dt_inicio = DATEADD(HOUR, -@horas,
                DATEADD(HOUR, DATEDIFF(HOUR, 0, SYSDATETIME()), CAST('00:00' AS DATETIME2(0))));
        IF @dt_fim IS NULL
            SET @dt_fim = DATEADD(HOUR, 1, CAST(SYSDATETIME() AS DATETIME2(0)));

        DELETE FROM analise.mat_passagens_hora
        WHERE hora >= @dt_inicio AND hora < @dt_fim;

        INSERT INTO analise.mat_passagens_hora
            (hora, id_local, local_nome, faixa, classificacao, placa_mercosul,
             passagens, soma_velocidade, lat, lon)
        SELECT
            a.hora, a.id_local,
            ISNULL(lv.nome, 'Equipamento ' + CAST(a.id_local AS varchar(10))),
            a.faixa, a.classificacao, a.placa_mercosul,
            a.passagens, a.soma_velocidade, lv.lat, lv.lon
        FROM (
            SELECT
                CAST(DATEADD(HOUR, DATEDIFF(HOUR, 0, vtr.data), 0) AS DATETIME2(0)) AS hora,
                vtr.id_local,
                ISNULL(vtr.id_pista, 0)         AS faixa,
                ISNULL(vtr.classificacao, ' ')  AS classificacao,
                ISNULL(vtr.placa_mercosul, 0)   AS placa_mercosul,
                COUNT(*)                        AS passagens,
                SUM(CAST(ISNULL(vtr.velocidade, 0) AS BIGINT)) AS soma_velocidade
            FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
            WHERE vtr.data >= @dt_inicio AND vtr.data < @dt_fim
            GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, vtr.data), 0), vtr.id_local,
                     ISNULL(vtr.id_pista, 0), ISNULL(vtr.classificacao, ' '), ISNULL(vtr.placa_mercosul, 0)
        ) a
        LEFT JOIN analise.v_local_versao lv
            ON lv.id_local = a.id_local
           AND a.hora >= lv.vigencia_inicio AND a.hora < lv.vigencia_fim;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_passagens_hora', @dt_exec, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;

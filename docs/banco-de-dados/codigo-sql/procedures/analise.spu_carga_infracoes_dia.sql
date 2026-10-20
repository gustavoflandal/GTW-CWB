
-- 2b. Carga de infrações por dia -----------------------------------------------------
CREATE   PROCEDURE analise.spu_carga_infracoes_dia
    @dias      INT  = 7,
    @dt_inicio DATE = NULL,
    @dt_fim    DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dt_exec DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        IF @dt_inicio IS NULL SET @dt_inicio = DATEADD(DAY, -@dias, CAST(SYSDATETIME() AS DATE));
        IF @dt_fim IS NULL SET @dt_fim = DATEADD(DAY, 1, CAST(SYSDATETIME() AS DATE));

        DELETE FROM analise.mat_infracoes_dia
        WHERE dia >= @dt_inicio AND dia < @dt_fim;

        INSERT INTO analise.mat_infracoes_dia
            (dia, id_local, local_nome, faixa, id_enquadramento, enquadramento,
             limite, total, excesso_medio, lat, lon)
        SELECT
            a.dia, a.id_local,
            ISNULL(lv.nome, 'Equipamento ' + CAST(a.id_local AS varchar(10))),
            a.faixa, a.id_enquadramento, ISNULL(RTRIM(e.descricao), 'N/D'),
            a.limite, a.total, a.excesso_medio, lv.lat, lv.lon
        FROM (
            SELECT
                CAST(i.data AS DATE)     AS dia,
                i.id_local,
                ISNULL(i.pista, 0)       AS faixa,
                i.id_enquadramento,
                MAX(i.velocidade_limite) AS limite,
                COUNT(*)                 AS total,
                CAST(AVG(CASE WHEN i.velocidade_considerada > i.velocidade_limite
                              THEN i.velocidade_considerada - i.velocidade_limite END) AS DECIMAL(6,2)) AS excesso_medio
            FROM dbo.infracao i
            WHERE i.data >= @dt_inicio AND i.data < @dt_fim
            GROUP BY CAST(i.data AS DATE), i.id_local, ISNULL(i.pista, 0), i.id_enquadramento
        ) a
        LEFT JOIN analise.v_local_versao lv
            ON lv.id_local = a.id_local
           AND CAST(a.dia AS DATETIME) >= lv.vigencia_inicio AND CAST(a.dia AS DATETIME) < lv.vigencia_fim
        LEFT JOIN dbo.enquadramento e ON e.id_enquadramento = a.id_enquadramento;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_infracoes_dia', @dt_exec, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;

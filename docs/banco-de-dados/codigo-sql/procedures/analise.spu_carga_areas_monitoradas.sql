
-- Converte {"dadosMancha":{"cor":"#...","coordenadas":[{"lat":y,"lng":x},...]}} em [[x,y],...].
-- O banco está no nível de compatibilidade 110 (sem OPENJSON): a lista é reescrita como XML.
CREATE   PROCEDURE analise.spu_carga_areas_monitoradas
AS
BEGIN
    SET NOCOUNT ON;
    SET ANSI_WARNINGS ON;
    SET ANSI_PADDING ON;
    SET ARITHABORT ON;
    SET CONCAT_NULL_YIELDS_NULL ON;
    SET NUMERIC_ROUNDABORT OFF;

    DECLARE @dt_exec DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        DELETE FROM analise.mat_areas_monitoradas;

        INSERT INTO analise.mat_areas_monitoradas (id, nome, cor, poligono)
        SELECT
            am.id,
            LEFT(am.nome, 200),
            CASE WHEN CHARINDEX('"cor":"', am.dados_json) > 0
                 THEN SUBSTRING(am.dados_json, CHARINDEX('"cor":"', am.dados_json) + 7, 7) END,
            '[' + STUFF((
                SELECT ',[' + n.value('@o', 'varchar(40)') + ',' + n.value('@a', 'varchar(40)') + ']'
                FROM x.doc.nodes('/r/p') t(n)
                FOR XML PATH(''), TYPE).value('.', 'nvarchar(max)'), 1, 1, '') + ']'
        FROM muralha.area_monitorada am
        CROSS APPLY (SELECT CHARINDEX('"coordenadas":[', am.dados_json) AS ini) a
        CROSS APPLY (
            SELECT CASE WHEN a.ini > 0 AND CHARINDEX(']', am.dados_json, a.ini) > a.ini + 15
                        THEN SUBSTRING(am.dados_json, a.ini + 15, CHARINDEX(']', am.dados_json, a.ini) - a.ini - 15)
                   END AS seg
        ) s
        CROSS APPLY (
            SELECT TRY_CAST(N'<r>' + REPLACE(REPLACE(REPLACE(s.seg, N'{"lat":', N'<p a="'), N',"lng":', N'" o="'),
                                             N'}', N'"/>') + N'</r>' AS XML) AS doc
        ) x
        WHERE am.deletado = 0;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_areas_monitoradas', @dt_exec, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;

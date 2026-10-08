

-- =============================================
-- Author:      Gustavo Landal
-- Create date: 13 de setembro de 2025
-- Description: Procedure alternativa para buscar fatos pendentes com filtros opcionais
-- =============================================

CREATE     PROCEDURE [muralha].[sp_RelatorioPendenciasRegistroFato]
    @data_inicio DATE = NULL,
    @data_fim DATE = NULL,
    @tipo_falta VARCHAR(50) = NULL,
    -- 'TIPO', 'STATUS', 'USUARIO', 'DATA', 'ENDERECO', 'NATUREZA'
    @somente_privados BIT = NULL
-- NULL = todos, 0 = nÃ£o privados, 1 = apenas privados
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @where_clause NVARCHAR(MAX) = '';

    -- Construir clÃ¡usula WHERE dinamicamente
    SET @where_clause = '
    WHERE 1=1 ';

    -- Filtro de dados faltantes
    IF @tipo_falta IS NULL
    BEGIN
        SET @where_clause = @where_clause + '
        AND (rf.id_tipo IS NULL OR
             rf.id_status IS NULL OR
             rf.id_usuario IS NULL OR
             rf.data_criacao IS NULL) ';
    END
    ELSE
    BEGIN
        -- Filtro especÃ­fico por tipo de falta
        IF @tipo_falta = 'TIPO'
            SET @where_clause = @where_clause + ' AND rf.id_tipo IS NULL ';
        ELSE IF @tipo_falta = 'STATUS'
            SET @where_clause = @where_clause + ' AND rf.id_status IS NULL ';
        ELSE IF @tipo_falta = 'USUARIO'
            SET @where_clause = @where_clause + ' AND rf.id_usuario IS NULL ';
        ELSE IF @tipo_falta = 'DATA'
            SET @where_clause = @where_clause + ' AND rf.data_criacao IS NULL ';
    END

    -- Filtro de data (simplificado para trabalhar apenas com datas)
    IF @data_inicio IS NOT NULL AND @data_fim IS NOT NULL
    BEGIN
        SET @where_clause = @where_clause + ' AND CAST(rf.data_criacao AS DATE) BETWEEN ''' + CONVERT(VARCHAR(10), @data_inicio, 120) + ''' AND ''' + CONVERT(VARCHAR(10), @data_fim, 120) + ''' ';
    END

    -- Filtro de privacidade
    IF @somente_privados IS NOT NULL
    BEGIN
        SET @where_clause = @where_clause + ' AND rf.privado = ' + CAST(@somente_privados AS VARCHAR(1)) + ' ';
    END

    -- Query dinÃ¢mica
    DECLARE @sql NVARCHAR(MAX) = '
    SELECT
        rf.id,
        rft.tipo_desc AS tipo,
        rfs.descricao AS status,
        su.nome AS usuario,
        rf.data_criacao,
        rf.data_encerramento,
        rf.privado,
        '''' AS endereco,
        '''' AS Faltas
    FROM muralha.registro_fato rf
    LEFT JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    LEFT JOIN muralha.registro_fato_status rfs ON rf.id_status = rfs.id
    LEFT JOIN dbo.sis_usuario su ON rf.id_usuario = su.id_usuario
    ' + @where_clause + '
    ORDER BY rf.data_criacao DESC;';

    -- Executar query dinÃ¢mica
    EXEC sp_executesql @sql;

END



-- ==================================================================================================
-- Procedure: Relatório de Pendencia nos registros de Fato 
-- Descrição: Verifica as pendencias nos registros de fato
-- Autor: Gustavo F. Landal
-- Data: 13/09/2025
-- Otimização V2: Melhoria na parformance 
-- ===================================================================================================

CREATE       PROCEDURE [muralha].[spu_RelatorioDePendenciasNosRegistrosDeFato]
    @data_inicio date = NULL,
    @data_fim date = NULL,
    @tipo_falta VARCHAR(50) = NULL,
    @somente_privados BIT = NULL
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

	IF @data_inicio = ''  
	    SET @data_inicio = NULL;
    IF @data_fim = ''
		SET @data_fim = NULL;
    IF @tipo_falta = ''
		SET @tipo_falta = NULL;
    IF @somente_privados = ''
		SET @somente_privados = NULL;

    DECLARE @data_inicio_convertida DATE;
    DECLARE @data_fim_convertida DATE;
    DECLARE @somente_privados_convertido BIT;

    SET @data_inicio_convertida = TRY_CONVERT(DATE, @data_inicio);
    SET @data_fim_convertida = TRY_CONVERT(DATE, @data_fim);
    SET @somente_privados_convertido = TRY_CONVERT(BIT, @somente_privados);

    DECLARE @sql NVARCHAR(MAX) = N'';
    DECLARE @params NVARCHAR(MAX) = N'@data_inicio_p DATE, @data_fim_p DATE, @somente_privados_p BIT';

    SET @sql = N'
    SELECT
        rf.id,
        rft.tipo_desc AS tipo,
        rfs.descricao AS status,
        su.nome AS usuario,
        rf.data_criacao,
        rf.data_encerramento,
        rf.privado,
        RTRIM(
            CASE WHEN rf.id_tipo IS NULL THEN ''Falta Tipo; '' ELSE '''' END +
            CASE WHEN rf.id_status IS NULL THEN ''Falta Status; '' ELSE '''' END +
            CASE WHEN rf.id_usuario IS NULL THEN ''Falta Usuário; '' ELSE '''' END +
            CASE WHEN rf.data_criacao IS NULL THEN ''Falta Data Criação; '' ELSE '''' END +
            CASE WHEN rfe.id IS NULL THEN ''Falta Endereço; '' ELSE '''' END +
            CASE WHEN rfn.id IS NULL THEN ''Falta Natureza; '' ELSE '''' END
        ) AS Faltas
    FROM muralha.registro_fato rf
    LEFT JOIN muralha.registro_fato_endereco rfe ON rf.id = rfe.id_registro_fato
    LEFT JOIN muralha.registro_fato_natureza rfn ON rf.id_tipo = rfn.id_registro_tipo
    LEFT JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    LEFT JOIN muralha.registro_fato_status rfs ON rf.id_status = rfs.id
    LEFT JOIN dbo.sis_usuario su ON rf.id_usuario = su.id_usuario
    WHERE 1=1';

    IF @data_inicio_convertida IS NOT NULL
        SET @sql = @sql + ' AND rf.data_criacao >= @data_inicio_p';
    
    IF @data_fim_convertida IS NOT NULL
        SET @sql = @sql + ' AND rf.data_criacao < DATEADD(DAY, 1, @data_fim_p)';

    IF @tipo_falta IS NOT NULL AND @tipo_falta <> ''
    BEGIN
        IF @tipo_falta = 'Falta Tipo'
            SET @sql = @sql + ' AND rf.id_tipo IS NULL ';
        ELSE IF @tipo_falta = 'Falta Status'
            SET @sql = @sql + ' AND rf.id_status IS NULL ';
        ELSE IF @tipo_falta = 'Falta Usuário'
            SET @sql = @sql + ' AND rf.id_usuario IS NULL ';
        ELSE IF @tipo_falta = 'Falta Data Criação'
            SET @sql = @sql + ' AND rf.data_criacao IS NULL ';
        ELSE IF @tipo_falta = 'Falta Endereço'
            SET @sql = @sql + ' AND rfe.id IS NULL ';
        ELSE IF @tipo_falta = 'Falta Natureza'
            SET @sql = @sql + ' AND rfn.id IS NULL ';
    END
    ELSE
    BEGIN
        SET @sql = @sql + '
        AND (rf.id_tipo IS NULL OR
             rf.id_status IS NULL OR
             rf.id_usuario IS NULL OR
             rf.data_criacao IS NULL OR
             rfe.id IS NULL OR
             rfn.id IS NULL)';
    END
    IF @somente_privados_convertido IS NOT NULL
        SET @sql = @sql + ' AND rf.privado = @somente_privados_p';
    SET @sql = @sql + ' ORDER BY rf.data_criacao DESC;';
    EXEC sp_executesql @sql, 
                       @params, 
                       @data_inicio_p = @data_inicio_convertida, 
                       @data_fim_p = @data_fim_convertida, 
                       @somente_privados_p = @somente_privados_convertido;
END

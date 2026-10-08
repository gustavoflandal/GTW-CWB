

CREATE   PROCEDURE dbo.spu_obter_imagens_exportacao
(
    @data_ini                  DATE,
    @data_fim                  DATE,
    @apenas_img_obj             BIT = 1,
    @apenas_img_diurna          BIT = 0,
    @id_classe                  CHAR(1) = NULL,
    @marca                      VARCHAR(50) = NULL,
    @modelo                     VARCHAR(60) = NULL,
    @ano_modelo                 INT = NULL,
    @cor                        VARCHAR(30) = NULL,
    @serie_equipamento          VARCHAR(60) = NULL,
    @ID_modelos                 VARCHAR(2000) = NULL,
    @tipo_hatch_sedan           VARCHAR(100) = NULL,
    @IDs_serie_equipamentos     VARCHAR(500) = NULL
)
AS
BEGIN

    SET NOCOUNT ON;

    --------------------------------------------------------
    -- Validações obrigatórias
    --------------------------------------------------------

    IF @data_ini IS NULL OR @data_fim IS NULL
    BEGIN
        THROW 50001, 'Os parâmetros @data_ini e @data_fim são obrigatórios.', 1;
    END;

    IF @data_ini > @data_fim
    BEGIN
        THROW 50002, '@data_ini não pode ser maior que @data_fim.', 1;
    END;

    IF @id_classe IS NULL
       AND NULLIF(LTRIM(RTRIM(@modelo)), '') IS NULL
       AND NULLIF(LTRIM(RTRIM(@ID_modelos)), '') IS NULL
    BEGIN
        THROW 50003, 'É obrigatório informar @id_classe, @modelo ou @ID_modelos.', 1;
    END;

    SET @marca = NULLIF(LTRIM(RTRIM(@marca)), '');
    SET @modelo = NULLIF(LTRIM(RTRIM(@modelo)), '');
    SET @serie_equipamento = NULLIF(LTRIM(RTRIM(@serie_equipamento)), '');
    SET @ID_modelos = NULLIF(LTRIM(RTRIM(@ID_modelos)), '');
    SET @tipo_hatch_sedan = NULLIF(LTRIM(RTRIM(@tipo_hatch_sedan)), '');
    SET @IDs_serie_equipamentos = NULLIF(LTRIM(RTRIM(@IDs_serie_equipamentos)), '');

    DECLARE @sql NVARCHAR(MAX);

    SET @sql = N'
        TRUNCATE TABLE dbo.temp_imagens_exportar_dados;

        INSERT INTO dbo.temp_imagens_exportar_dados WITH (TABLOCK)
        (
            nome_arquivo,
            id_imagem
        )
        SELECT
            @tipo_hatch_sedan
            + ''''
            + REPLACE(
                CAST(
                    LTRIM(RTRIM(cv.marca_cet))
                    + ''''
                    + COALESCE(@modelo, '''')
                    + ''''
                    + LTRIM(RTRIM(i.placa))
                    + ''''
                    + cem.serie_equipamento
                    AS VARCHAR(50)
                ),
                ''/'',
                ''''
            )
            + ''_''
            + REPLACE(
                REPLACE(
                    REPLACE(
                        CONVERT(VARCHAR(20), i.data, 120),
                        ''-'',
                        ''''
                    ),
                    '':'',
                    ''''
                ),
                '' '',
                ''''
            )
            + ''''
            + CASE
                WHEN img.indice_imagem = 0 THEN
                    ''OBJ''
                ELSE
                    ''PAN'' + CAST(img.indice_imagem AS VARCHAR(10))
              END
            + ''_''
            + CAST(NEWID() AS VARCHAR(40)),

            img.id_imagem

        FROM infracao i WITH (NOLOCK)

        INNER JOIN veiculo v WITH (NOLOCK)
            ON v.id_veiculo = i.id_veiculo

        INNER JOIN configuracao_equipamento_medicao cem WITH (NOLOCK)
            ON cem.cod_pista_prodam = v.codigo_prodam

        INNER JOIN veiculo_imagem vi WITH (NOLOCK)
            ON vi.id_veiculo = v.id_veiculo

        INNER JOIN imagem img WITH (NOLOCK)
            ON img.id_imagem = vi.id_imagem

        LEFT JOIN cadastro_veiculo cv WITH (NOLOCK)
            ON cv.placa = i.placa

        WHERE i.data >= @data_ini
          AND i.data < DATEADD(DAY, 1, @data_fim)
    ';

    IF @apenas_img_obj = 1
    BEGIN
        SET @sql += N'
            AND img.indice_imagem = 0
        ';
    END;

    IF @apenas_img_diurna = 1
    BEGIN
        SET @sql += N'
            AND CAST(i.data AS TIME) >= ''07:00:00''
            AND CAST(i.data AS TIME) <= ''17:20:00''
        ';
    END;

    IF @id_classe IS NOT NULL
    BEGIN
        SET @sql += N'
            AND v.id_classe = @id_classe
        ';
    END;

    IF @ano_modelo IS NOT NULL
    BEGIN
        SET @sql += N'
            AND cv.ano = @ano_modelo
        ';
    END;

    IF @cor IS NOT NULL
    BEGIN
        SET @sql += N'
            AND cv.cor = @cor
        ';
    END;

    IF @serie_equipamento IS NOT NULL
    BEGIN
        SET @sql += N'
            AND cem.serie_equipamento = @serie_equipamento
        ';
    END;

    IF @IDs_serie_equipamentos IS NOT NULL
    BEGIN
        SET @sql += N'
            AND cem.serie_equipamento IN ('''
            + REPLACE(
                REPLACE(@IDs_serie_equipamentos, ' ', ''),
                ',',
                N''','''
            )
            + N''')
        ';
    END;

    IF @marca IS NOT NULL
    BEGIN
        SET @sql += N'
            AND EXISTS
            (
                SELECT 1
                FROM cad_marca_cet mc
                WHERE mc.id_marca_cet = cv.id_marca_cet
                  AND mc.descricao LIKE ''%'' + @marca + ''%''
            )
        ';
    END;

    IF @modelo IS NOT NULL
    BEGIN
        SET @sql += N'
            AND EXISTS
            (
                SELECT 1
                FROM cad_marca m
                WHERE m.id_marca = cv.id_marca
                  AND m.descricao LIKE ''%'' + @modelo + ''%''
            )
        ';
    END;

    IF @ID_modelos IS NOT NULL
    BEGIN
        SET @sql += N'
            AND cv.id_marca IN (' + @ID_modelos + ')
        ';
    END;

    SET @sql += N'
        OPTION (RECOMPILE);

        SELECT @@ROWCOUNT AS total_imagens;
    ';

    -- SELECT @sql;

    EXEC sp_executesql
        @sql,
        N'
            @data_ini DATE,
            @data_fim DATE,
            @id_classe CHAR(1),
            @marca VARCHAR(50),
            @modelo VARCHAR(60),
            @cor VARCHAR(30),
            @ano_modelo INT,
            @serie_equipamento VARCHAR(60),
            @tipo_hatch_sedan VARCHAR(100),
            @IDs_serie_equipamentos VARCHAR(500)
        ',
        @data_ini = @data_ini,
        @data_fim = @data_fim,
        @id_classe = @id_classe,
        @marca = @marca,
        @modelo = @modelo,
        @cor = @cor,
        @ano_modelo = @ano_modelo,
        @serie_equipamento = @serie_equipamento,
        @tipo_hatch_sedan = @tipo_hatch_sedan,
        @IDs_serie_equipamentos = @IDs_serie_equipamentos;

END;
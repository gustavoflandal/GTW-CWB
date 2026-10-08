
CREATE PROCEDURE [muralha].[spu_obterVeiculosPorFiltros_exemplo]
    @placa VARCHAR(7) = NULL,
    @dataIni DATETIME = NULL,
    @dataFim DATETIME = NULL,
    @equipamentos VARCHAR(1600) = NULL,
    @faixa VARCHAR(100) = NULL,
    @classificacao VARCHAR(50) = NULL,
    @marca VARCHAR(100) = NULL,
    @modelo VARCHAR(100) = NULL,
    @cor VARCHAR(50) = NULL,
    @anoFabricacao INT = NULL,
    @anoModelo INT = NULL,
    @renavam VARCHAR(20) = NULL,
    @chassi VARCHAR(50) = NULL,
    @tipoVeiculo VARCHAR(50) = NULL,
    @municipio VARCHAR(100) = NULL,
    @estado VARCHAR(50) = NULL,
    @restricao BIT = NULL,
    @buscarApenasVeiculoComImagem BIT = 1,
    @consultaMapa BIT = 0,
    @exportarConsulta BIT = 0,
    @offset INT = 0,
    @itensPorPagina INT = 8
AS
BEGIN
    DECLARE @sql_1 VARCHAR(MAX) = '', @sql_2 VARCHAR(MAX) = '', @sql_3 VARCHAR(MAX) = '';

	SET @equipamentos = REPLACE(@equipamentos, ' ', '');
	SET @classificacao = REPLACE(@classificacao, ' ', '');


    SET @sql_1 = '
    SELECT vtr.id, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, vtr.codigo_equipamento, vtr.nome, 
           vtr.id_pista, vtr.faixa, vtr.latitude, vtr.longitude, vtr.velocidade, vtr.id_classe, vtr.classificacao, 
           vtr.enviado_cliente, vtr.com_imagem, vtr.possui_coordenadas, vtr.possui_alerta, vtr.marca, vtr.modelo, 
           vtr.cor, vtr.ano_fabricacao, vtr.ano_modelo, vtr.renavam, vtr.chassi, vtr.tipo_veiculo, vtr.municipio, 
           vtr.estado, vtr.restricao,
           LAG(vtr.id) OVER (ORDER BY vtr.data DESC, vtr.id_local, vtr.id_pista) AS veic_anterior,
           LEAD(vtr.id) OVER (ORDER BY vtr.data DESC, vtr.id_local, vtr.id_pista) AS veic_proximo
    ';

    IF (@consultaMapa = 0 AND @exportarConsulta = 0)
    BEGIN
        SET @sql_1 = @sql_1 + ' ,COUNT(*) OVER() AS total_registros ';
    END

    SET @sql_1 = @sql_1 + ' FROM muralha.v_veiculo_tempo_real vtr (NOLOCK) ';

    IF (@placa IS NULL AND (@dataIni IS NULL OR @dataFim IS NULL))
    BEGIN
        SET @sql_2 = ' WHERE 1 = 2 ';
    END
    ELSE
    BEGIN
        SET @sql_2 = ' WHERE 1 = 1 ';
    END

    IF (@placa IS NOT NULL)
        SET @sql_2 = @sql_2 + ' AND vtr.placa LIKE ''' + @placa + ''' ';

    IF (@dataIni IS NOT NULL AND @dataFim IS NOT NULL)
        SET @sql_2 = @sql_2 + ' AND vtr.data BETWEEN ''' + CONVERT(VARCHAR(20), @dataIni, 120) + ''' AND ''' + CONVERT(VARCHAR(20), @dataFim, 120) + ''' ';

    IF (@equipamentos IS NOT NULL)
        SET @sql_2 = @sql_2 + ' AND vtr.id_local IN (' + RTRIM(@equipamentos) + ') ';

	IF (@faixa IS NOT NULL)
		SET @sql_2 = @sql_2 + ' AND vtr.faixa = ''' + @faixa + ''' ';


    IF (@classificacao IS NOT NULL)
        SET @sql_2 = @sql_2 + ' AND vtr.id_classe IN (' + RTRIM(@classificacao) + ') ';

    IF (@marca IS NOT NULL)
        SET @sql_2 = @sql_2 + ' AND vtr.marca = ''' + @marca + ''' ';

    IF (@modelo IS NOT NULL)
        SET @sql_2 = @sql_2 + ' AND vtr.modelo = ''' + @modelo + ''' ';

    -- Faça o mesmo para cor, anoFabricacao, anoModelo, renavam, chassi, tipoVeiculo, municipio, estado, restricao

    IF (@buscarApenasVeiculoComImagem = 1)
        SET @sql_2 = @sql_2 + ' AND vtr.com_imagem = ' + CAST(@buscarApenasVeiculoComImagem AS CHAR(1)) + ' ';

    IF (@consultaMapa = 1)
        SET @sql_2 = @sql_2 + ' AND vtr.latitude IS NOT NULL AND vtr.longitude IS NOT NULL ';

    SET @sql_3 = ' ORDER BY ';

    IF (@consultaMapa = 1)
        SET @sql_3 = @sql_3 + ' vtr.data, ';
    ELSE
        SET @sql_3 = @sql_3 + ' vtr.data DESC, ';

    SET @sql_3 = @sql_3 + ' vtr.id_local, vtr.id_pista ';

    IF (@consultaMapa = 0 AND @exportarConsulta = 0)
        SET @sql_3 = @sql_3 + ' OFFSET ' + CAST(@offset AS VARCHAR(5)) + ' ROWS FETCH NEXT ' + CAST(@itensPorPagina AS VARCHAR(5)) + ' ROWS ONLY';

    EXEC (@sql_1 + @sql_2 + @sql_3);
END;

CREATE PROCEDURE dbo.spu_obterVeiculosGtwPorFiltros
	@placa VARCHAR(7) = NULL,
	@dataIni DATETIME = NULL,
	@dataFim DATETIME = NULL,
	@equipamentos VARCHAR(1600) = NULL,
	@faixa VARCHAR(100) = NULL,
	@classificacao VARCHAR(50) = NULL,
	@buscarApenasVeiculoComImagem BIT = 1,
	@consultaMapa BIT = 0,
	@exportarConsulta BIT = 0,
	@offset INT = 0,
	@itensPorPagina INT = 8,
	@marca VARCHAR(35) = NULL,
	@modelo VARCHAR(35) = NULL,
	@idCor INT = NULL,
	@anoFabricacao INT = NULL,
	@anoModelo INT = NULL,
	@renavam VARCHAR(11) = NULL,
	@chassi VARCHAR(17) = NULL,
	@idLocalidade INT = NULL,
	@restricao VARCHAR(100) = NULL,
	@filtroPlaca INT = 0,
    @tipoPlaca VARCHAR(10) = NULL,
	@tipoVeiculo VARCHAR(100) = NULL,
	@deveAplicarFiltroDeImagens BIT = 1,
	@somenteUltimaPassagem BIT = 0,
	@com_pesagem BIT = 1,
	@apenas_veiculos_carga BIT = 1
AS    
BEGIN
	/*
	DECLARE @placa CHAR(7) = NULL,
			@dataIni DATETIME = '2025-10-12 11:30:00',
			@dataFim DATETIME  = '2025-10-12 11:50:59',
			@equipamentos VARCHAR(1600) = '201,202',
			@faixa VARCHAR(100) = NULL,
			@classificacao VARCHAR(50) = NULL,
			@buscarApenasVeiculoComImagem BIT = 0,
			@consultaMapa BIT = 0,
			@exportarConsulta BIT = 0,
			@offset INT = 0,
			@itensPorPagina INT = 20,
			@marca VARCHAR(35) = NULL,
			@modelo VARCHAR(35) = NULL,
			@idCor INT = NULL,
			@anoFabricacao INT = NULL,
			@anoModelo INT = NULL,
			@renavam VARCHAR(11) = NULL,
			@chassi VARCHAR(17) = NULL,
			@idLocalidade INT = NULL,
			@ufLocalidade CHAR(2) = NULL,
			@restricao VARCHAR(100) = NULL,
			@filtroPlaca INT = 0,
			@tipoPlaca VARCHAR(10) = NULL,
			@tipoVeiculo VARCHAR(100) = NULL,
			@deveAplicarFiltroDeImagens BIT = 1,
			@somenteUltimaPassagem BIT = 1,
			@com_pesagem BIT = 1,
			@apenas_veiculos_carga BIT = 1
	*/

	DECLARE @sql_1 VARCHAR(MAX) = '', @sql_2 VARCHAR(MAX) = '', @sql_3 VARCHAR(MAX) = ''

	-- MODIFICAÇÃO: Quando for última passagem, forçar filtro de placa mas manter paginação
	IF (@somenteUltimaPassagem = 1)
	BEGIN
		-- Forçar filtro para trazer apenas veículos com placa
		SET @filtroPlaca = 1
	END

	IF (@buscarApenasVeiculoComImagem IS NULL OR @buscarApenasVeiculoComImagem = 0)
	BEGIN
		SET @buscarApenasVeiculoComImagem = 1
	END

	IF (@com_pesagem IS NULL OR @com_pesagem = 0)
	BEGIN
		SET @com_pesagem = 1
	END

	IF (@apenas_veiculos_carga IS NULL OR @apenas_veiculos_carga = 0)
	BEGIN
		SET @apenas_veiculos_carga = 1
	END

	-- MODIFICAÇÃO: Lógica para última passagem por placa
	IF (@somenteUltimaPassagem = 1)
	BEGIN
		SET @sql_1 = '
			SET NOCOUNT ON;
			WITH UltimasPassagens AS (
			    SELECT 
			        vtr.id, vtr.id_veiculo, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, vtr.codigo_equipamento, vtr.nome, vtr.id_pista, vtr.faixa, vtr.latitude, vtr.longitude, vtr.comprimento, vtr.velocidade, vtr.id_classe, vtr.classificacao, vtr.enviado_cliente,
			        vtr.com_imagem, vtr.possui_coordenadas, vtr.possui_alerta, vtr.marca, vtr.modelo, vtr.cor, vtr.ano_modelo, vtr.tipo, vtr.localidade, vtr.uf,
			        vtr.ano_fabricacao, vtr.renavam, vtr.chassi, vtr.restricao,
			        vtr.pbt, vtr.pbtc, vtr.numero_eixos, vtr.rodagem_dupla, vtr.categoria, vtr.classificacao_art96,
					vtr.E1, vtr.E2, vtr.E3, vtr.E4, vtr.E5, vtr.E6, vtr.E7, vtr.E8, vtr.E9,
					vtr.distancia_E1E2, vtr.distancia_E2E3, vtr.distancia_E3E4, vtr.distancia_E4E5, vtr.distancia_E5E6, vtr.distancia_E6E7, vtr.distancia_E7E8, vtr.distancia_E8E9,
			        ''1900-01-01 00:00:00'' AS data_importado,
			        NULL AS veic_anterior,
			        NULL AS veic_proximo,
			        vtr.placa_mercosul,
					CASE 
						WHEN vtr.classificacao IN (''Caminhão       '', ''Onibus         '', ''Caminhão tanque'') THEN ''Vermelho''
						ELSE ''Branco''
					END AS cor_placa,
			        ROW_NUMBER() OVER (PARTITION BY vtr.placa ORDER BY vtr.data DESC) as rn_ultima_passagem '
		
		IF (@consultaMapa = 0 AND @exportarConsulta = 0)
		BEGIN
			SET @sql_1 = @sql_1 + ' ,COUNT(*) OVER() AS total_registros '
		END
		
		SET @sql_1 = @sql_1 + ' FROM dbo.v_veiculo_completo_gtw vtr (NOLOCK) '
	END
	ELSE
	BEGIN
		SET @sql_1 = '
		SET NOCOUNT ON;
		SELECT vtr.id, vtr.id_veiculo, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, vtr.codigo_equipamento, vtr.nome, vtr.id_pista, vtr.faixa, vtr.latitude, vtr.longitude, vtr.comprimento, vtr.velocidade, vtr.id_classe, vtr.classificacao, vtr.enviado_cliente,
		        vtr.com_imagem, vtr.possui_coordenadas, vtr.possui_alerta, vtr.marca, vtr.modelo, vtr.cor, vtr.ano_modelo, vtr.tipo, vtr.localidade, vtr.uf,
		        vtr.ano_fabricacao, vtr.renavam, vtr.chassi, vtr.restricao,
		        vtr.pbt, vtr.pbtc, vtr.numero_eixos, vtr.rodagem_dupla, vtr.categoria, vtr.classificacao_art96,
				vtr.E1, vtr.E2, vtr.E3, vtr.E4, vtr.E5, vtr.E6, vtr.E7, vtr.E8, vtr.E9,
				vtr.distancia_E1E2, vtr.distancia_E2E3, vtr.distancia_E3E4, vtr.distancia_E4E5, vtr.distancia_E5E6, vtr.distancia_E6E7, vtr.distancia_E7E8, vtr.distancia_E8E9,
		        ''1900-01-01 00:00:00'' AS data_importado,
		        vtr.placa_mercosul,
					CASE 
						WHEN vtr.classificacao IN (''Caminhão       '', ''Onibus         '', ''Caminhão tanque'') THEN ''Vermelho''
						ELSE ''Branco''
					END AS cor_placa,
		        LAG(vtr.id) OVER (ORDER BY vtr.data DESC, vtr.id_local, vtr.id_pista) AS veic_anterior,
		        LEAD(vtr.id) OVER (ORDER BY vtr.data DESC, vtr.id_local, vtr.id_pista) AS veic_proximo '

		IF (@consultaMapa = 0 AND @exportarConsulta = 0)
		BEGIN
			SET @sql_1 = @sql_1 + ' ,COUNT(*) OVER() AS total_registros '
		END
			
		SET @sql_1 = @sql_1 + ' FROM dbo.v_veiculo_completo_gtw vtr (NOLOCK) '
	END
	
	--> PESQUISA MUITO ABRANGENTE. INFORMAR AO MENOS A PLACA, OU O PERÍODO PARA CONSULTA
	IF (@placa IS NULL AND (@dataIni IS NULL OR @dataFim IS NULL))
	BEGIN
		SET @sql_2 = @sql_2 + ' WHERE  1 = 2 '
	END
	ELSE
	BEGIN
		SET @sql_2 = @sql_2 + ' WHERE  1 = 1 '
	END
			
			
	IF (@placa IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.placa LIKE '''+ @placa +''''
	END
			
	IF (@dataIni IS NOT NULL AND @dataFim IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.data BETWEEN ''' + CONVERT(VARCHAR(20), @dataIni, 120) + ''' AND ''' + CONVERT(VARCHAR(20), @dataFim, 120) + ''''
	END

	IF (@equipamentos IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.id_local IN (' + RTRIM(@equipamentos) + ') '
	END
			
	IF (@faixa IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.faixa IN (' + RTRIM(@faixa) + ') '
	END
			
	IF (@classificacao IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.id_classe IN (' + RTRIM(@classificacao)  + ') '
	END
			
	IF (@deveAplicarFiltroDeImagens = 1)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.com_imagem = ' + CAST(@buscarApenasVeiculoComImagem AS CHAR(1))
	END
			
	IF (@com_pesagem = 1)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.com_pesagem = ' + CAST(@com_pesagem AS CHAR(1))
	END
			
	IF (@apenas_veiculos_carga = 1)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.id_classe IN (''C'', ''O'', ''Q'')'
	END
			
	IF (@consultaMapa = 1)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.latitude IS NOT NULL AND vtr.longitude IS NOT NULL '
	END

	
	IF (@idCor IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.id_cor = ' + CAST(@idCor AS VARCHAR(5))
	END

	IF (@anoFabricacao IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.ano_fabricacao = ' + CAST(@anoFabricacao AS VARCHAR(4))
	END

	IF (@anoModelo IS NOT NULL)
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.ano_modelo = ' + CAST(@anoModelo AS VARCHAR(4))
	END

	IF (@renavam IS NOT NULL AND RTRIM(@renavam) != '')
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.renavam LIKE ''%' + RTRIM(@renavam)  + '%'' '
	END

	IF (@chassi IS NOT NULL AND RTRIM(@chassi) != '')
	BEGIN
		SET @sql_2 = @sql_2 + ' AND vtr.chassi LIKE ''%' + RTRIM(@chassi)  + '%'' '
	END

	-- FILTRO DE LOCALIDADE
    IF (@idLocalidade IS NOT NULL AND @idLocalidade > -1)
    BEGIN
        SET @sql_2 = @sql_2 + ' AND vtr.id_localidade = ' + CAST(@idLocalidade AS VARCHAR(5))
    END

	IF (@restricao IS NOT NULL AND RTRIM(@restricao) != '')
	BEGIN
		IF (@restricao = '1')
		BEGIN
			SET @sql_2 = @sql_2 + ' AND (vtr.restricao IS NOT NULL AND vtr.restricao != '''') '
		END
		ELSE IF (@restricao = '0')
		BEGIN
			SET @sql_2 = @sql_2 + ' AND (vtr.restricao IS NULL OR vtr.restricao = '''') '
		END
	END
	
	IF (@marca IS NOT NULL AND RTRIM(@marca) != '')
	BEGIN
	    SET @sql_2 = @sql_2 + ' AND vtr.id_marca = ' + RTRIM(@marca) + ' '
	END
	
	IF (@modelo IS NOT NULL AND RTRIM(@modelo) != '')
	BEGIN
	    SET @sql_2 = @sql_2 + ' AND vtr.id_modelo = ' + RTRIM(@modelo) + ' '
	END
	
	IF (@tipoVeiculo IS NOT NULL AND RTRIM(@tipoVeiculo) != '')
	BEGIN
	    SET @sql_2 = @sql_2 + ' AND vtr.id_tipo IN (' + RTRIM(@tipoVeiculo) + ') '
	END
	
	
	-- FILTRO: VEÍCULOS COM/SEM PLACA (0 = Todos, 1 = Com placa, 2 = Sem placa)
	IF (@filtroPlaca = 1) -- Somente com leitura
	BEGIN
		SET @sql_2 = @sql_2 + ' AND (vtr.placa IS NOT NULL AND vtr.placa != '''') '
	END
	ELSE IF (@filtroPlaca = 2) -- Somente sem leitura
	BEGIN
		SET @sql_2 = @sql_2 + ' AND (vtr.placa IS NULL OR vtr.placa = '''') '
	END
	
	IF (@tipoPlaca IS NOT NULL AND @tipoPlaca != 'Todos')
	BEGIN
	    IF (@tipoPlaca = 'Mercosul')
	    BEGIN
	        SET @sql_2 = @sql_2 + ' AND vtr.placa_mercosul = 1 '
	    END
	    ELSE IF (@tipoPlaca = 'Padrao')
	    BEGIN
	        SET @sql_2 = @sql_2 + ' AND (vtr.placa_mercosul = 0 OR vtr.placa_mercosul IS NULL) '
	    END
	END

	-- MODIFICAÇÃO: Finalização da query para última passagem COM PAGINAÇÃO
	IF (@somenteUltimaPassagem = 1)
	BEGIN
		SET @sql_3 = ' ) SELECT * FROM UltimasPassagens WHERE rn_ultima_passagem = 1 ORDER BY data DESC '
		
		-- APLICAR PAGINAÇÃO MESMO PARA ÚLTIMAS PASSAGENS
		IF (@consultaMapa = 0 AND @exportarConsulta = 0)
		BEGIN
			SET @sql_3 = @sql_3 + ' OFFSET ' + CAST(@offset AS VARCHAR(5)) + ' ROWS FETCH NEXT ' + CAST(@itensPorPagina AS VARCHAR(5)) + ' ROWS ONLY'
		END
	END
	ELSE
	BEGIN
		SET @sql_3 = @sql_3 + ' ORDER BY '

		IF (@consultaMapa = 1)
		BEGIN
			SET @sql_3 = @sql_3 + ' vtr.data, '
		END
		ELSE
		BEGIN
			SET @sql_3 = @sql_3 + ' vtr.data DESC, '
		END

		SET @sql_3 = @sql_3 + ' vtr.id_local, vtr.id_pista'
			
		IF (@consultaMapa = 0 AND @exportarConsulta = 0)
		BEGIN
			SET @sql_3 = @sql_3 + '  OFFSET ' + CAST(@offset AS VARCHAR(5)) + ' ROWS FETCH NEXT ' + CAST(@itensPorPagina AS VARCHAR(5)) + ' ROWS ONLY'
		END
	END

	--PRINT (@sql_2)
	--PRINT (@sql_1 + @sql_2 + @sql_3)
	EXEC (@sql_1 + @sql_2 + @sql_3)
END;


CREATE PROCEDURE [dbo].[spu_criar_amostras_imagens] 
AS 

	SET NOCOUNT ON 
	
	DECLARE @data_inicio	date = GETDATE()-30
	DECLARE @data_fim		date = GETDATE()

	DECLARE @curr_dia date 
	DECLARE @curr_local int 
	DECLARE @curr_pista tinyint 
	DECLARE @curr_veiculo int 
	DECLARE @curr_infracao int 
	DECLARE @curr_score int 
	DECLARE @amostra_metro BIGINT 
	DECLARE @amostra_nao_metro BIGINT 
	DECLARE @Registros INT

	PRINT 'Buscando imagens ...' + CONVERT(CHAR(23), GETDATE(), 120 )
	SELECT 
	   ia.id_veiculo,
	   ia.serie_equipamento,
	   ia.id_configuracao_equipamento,
	   ia.id_local,
	   ia.id_pista,
	   ia.cod_pista,
	   ia.data,
	   ia.id_imagem,
	   ia.id_enquadramento,
	   dbo.fcn_pontua_infracao(ia.id_infracao) as score_total,
	   CAST(ia.data AS DATE) AS dia,
	   ia.tipo,
	   COALESCE(ia.placa_ocr, ia.placa_digitada) AS placa
	INTO
		#Dummy
	FROM
		infracao_amostra ia (NOLOCK)
	WHERE 
		ia.data >= CAST(@data_inicio AS DATETIME) AND
		ia.data < CAST( DATEADD(Day,1, @data_fim) AS DATETIME)
		
		--CAST (ia.data AS DATE) BETWEEN @data_inicio AND @data_fim 
	PRINT 'Busca de imagens concluida ...' + CONVERT(CHAR(23), GETDATE(), 120 )
	PRINT '' 
 
	PRINT 'Apagando as amostras ...' + CONVERT(CHAR(23), GETDATE(), 120 )
	DELETE FROM amostra_imagem WITH (ROWLOCK)
	WHERE CAST(data AS DATE) BETWEEN @data_inicio AND @data_fim 
	PRINT 'Amostras apagadas ...' + CONVERT(CHAR(23), GETDATE(), 120 )
	PRINT '' 

	SET @curr_dia = @data_inicio 
	 
	WHILE (@curr_dia <= @data_fim) 
	BEGIN 
	
		--PRINT 'Declare dummy_cursor ...' + CONVERT(CHAR(23), GETDATE(), 120 ) + ' ' + CONVERT(CHAR(23), @curr_dia, 120 )
		DECLARE dummy_cursor CURSOR LOCAL 
			FOR SELECT DISTINCT id_local, id_pista 
			FROM #Dummy WHERE dia = @curr_dia ORDER BY id_local, id_pista FOR READ ONLY 
		
		--PRINT 'Open dummy_cursor ...' + CONVERT(CHAR(23), GETDATE(), 120 )
		OPEN dummy_cursor 
		FETCH NEXT FROM dummy_cursor 
			INTO @curr_local, @curr_pista 
		--PRINT 'Open dummy_cursor Terminou...' + CONVERT(CHAR(23), GETDATE(), 120 )

		PRINT '' 
		PRINT REPLICATE('*',24)
		PRINT '*** DATA: ' + CAST(@curr_dia AS VARCHAR) + ' ***'
		PRINT REPLICATE('*',24)
		PRINT '' 
		 
		WHILE @@FETCH_STATUS = 0 
		BEGIN 

			PRINT ''
			PRINT 'DATA: ' + CAST(@curr_dia AS VARCHAR) + ' - LOCAL: ' + CAST(@curr_local AS VARCHAR) + ' - PISTA: ' + CAST(@curr_pista AS VARCHAR)
			PRINT ''

--			PRINT 'SET @amostra_metrológica ...' + CONVERT(CHAR(23), GETDATE(), 120 ) + ' ' + CONVERT(CHAR(23), @curr_dia, 120 ) + ' ' + STR(@curr_local) + ' ' + STR(@curr_pista)
			SET @amostra_metro = ( 
				SELECT TOP 1 _dmy.id_veiculo 
				FROM #Dummy _dmy 
				JOIN local_regra_infracao lri (NOLOCK) 
				  ON lri.id_configuracao_equipamento = _dmy.id_configuracao_equipamento 
				  AND lri.id_pista = _dmy.id_pista 
				WHERE _dmy.dia = @curr_dia 
				AND lri.id_local = @curr_local 
				AND lri.id_pista = @curr_pista 
				AND lri.tipo = 'VL'
				AND _dmy.tipo IN ('VL', 'TS')
				ORDER BY score_total DESC 
			) 
	     
			--PRINT 'INSERT amostra_imagem @amostra_metrológica ...' + CONVERT(CHAR(23), GETDATE(), 120 ) + ' ' + CONVERT(CHAR(23), @curr_dia, 120 ) + ' ' + STR(@curr_local) + ' ' + STR(@curr_pista)
			INSERT INTO amostra_imagem WITH (ROWLOCK)
				SELECT dmy.id_veiculo, 
					dmy.data, 
					dmy.serie_equipamento, 
					dmy.cod_pista, 
					dmy.id_enquadramento, 
					dmy.id_imagem, 
					dmy.score_total, 
					dmy.tipo, 
					CAST(1 AS BIT)
				FROM 
		     		#Dummy AS dmy 
				 WHERE
					dmy.id_veiculo = @amostra_metro 

			SET @Registros = @@ROWCOUNT
			PRINT 'AMOSTRAS METROLOGICAS.....: ' + CAST(ISNULL(@Registros,0) AS VARCHAR)		

			--PRINT 'SET @amostra_nao_metrológica ...' + CONVERT(CHAR(23), GETDATE(), 120 ) + ' ' + CONVERT(CHAR(23), @curr_dia, 120 ) + ' ' + STR(@curr_local) + ' ' + STR(@curr_pista)
			SET @amostra_nao_metro = ( 
					SELECT TOP 1 _dmy.id_veiculo 
					FROM #Dummy AS _dmy 
					JOIN local_regra_infracao lri (NOLOCK) 
					  ON lri.id_configuracao_equipamento = _dmy.id_configuracao_equipamento 
					  AND lri.id_pista = _dmy.id_pista 
					WHERE _dmy.dia = @curr_dia 
					AND lri.id_local = @curr_local 
					AND lri.id_pista = @curr_pista 
					AND lri.tipo NOT IN ('VL','TS','MT')
					AND (
						_dmy.tipo NOT IN ('VL','TS')
						OR (
							_dmy.tipo = 'TS'
							AND (
								@amostra_metro IS NULL OR _dmy.id_veiculo != @amostra_metro
							)
						)
					)	 
					ORDER BY score_total DESC 
	    		)
	     
			--PRINT 'INSERT amostra_imagem @amostra_nao_metrológica ...' + CONVERT(CHAR(23), GETDATE(), 120 ) + ' ' + CONVERT(CHAR(23), @curr_dia, 120 ) + ' ' + STR(@curr_local) + ' ' + STR(@curr_pista)
			INSERT INTO amostra_imagem WITH (ROWLOCK)
				SELECT dmy.id_veiculo, 
					dmy.data, 
					dmy.serie_equipamento, 
					dmy.cod_pista, 
					dmy.id_enquadramento, 
					dmy.id_imagem, 
					dmy.score_total, 
					dmy.tipo, 
					CAST(0 AS BIT) 
				FROM 
					#Dummy AS dmy 
				WHERE
					dmy.id_veiculo = @amostra_nao_metro 

			SET @Registros = @@ROWCOUNT
			PRINT 'AMOSTRAS NÃO METROLOGICAS.: ' + CAST(ISNULL(@Registros,0) AS VARCHAR)		
					
			FETCH NEXT FROM dummy_cursor 
			INTO @curr_local, @curr_pista
		
		END    
		  
		CLOSE dummy_cursor 
  		DEALLOCATE dummy_cursor
  		 
		SET @curr_dia = DATEADD(DAY, 1, @curr_dia) 
		
	END


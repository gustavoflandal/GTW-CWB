CREATE PROCEDURE [muralha].[spu_obterVeiculosTempoReal_20240926]
AS
	SET NOCOUNT ON;

	DECLARE @id_local INT, @nome VARCHAR(100), @data_exibicao_inicial_usuarios DATETIME

	CREATE TABLE #locais (id_local INT INDEX IX1, data_exibicao_inicial_usuarios DATETIME)
	CREATE TABLE #temp_local_pista (id_local INT INDEX IX1, id_pista TINYINT INDEX IX2, faixa TINYINT, nome VARCHAR(100))
	CREATE TABLE #temp_veiculos (id UNIQUEIDENTIFIER, placa VARCHAR(7), data DATETIME, id_local INT, nome VARCHAR(100), id_pista TINYINT, faixa TINYINT, velocidade SMALLINT, enviado_cliente BIT, classificacao CHAR(1))

	INSERT INTO #locais
	SELECT cfg.id_local,
		   cfg.data_exibicao_inicial_usuarios
	FROM   muralha.config_envio_tempo_real_equipamento cfg (NOLOCK)
	WHERE  cfg.enviar = 1

	INSERT INTO #temp_local_pista
	SELECT lpv.id_local, lpv.id_pista, lpv.cod_pista_alternativo AS faixa, RTRIM(lpv.nome) AS nome
	FROM   local_pista_vigente lpv
		   INNER JOIN #locais l
				ON  l.id_local = lpv.id_local

	DECLARE equip_tempo_real_cursor CURSOR FOR 
	SELECT id_local, data_exibicao_inicial_usuarios FROM #locais

	OPEN equip_tempo_real_cursor

	FETCH NEXT FROM equip_tempo_real_cursor 
	INTO @id_local, @data_exibicao_inicial_usuarios

	WHILE @@FETCH_STATUS = 0
	BEGIN

		INSERT INTO #temp_veiculos
		SELECT vtr.id,
			   vtr.placa,
			   vtr.data,
			   vtr.id_local,
			   lpv.nome,
			   vtr.id_pista,
			   lpv.faixa,
			   vtr.velocidade,
			   vtr.enviado_cliente,
			   CASE WHEN vtr.classificacao LIKE '[a-zA-Z]' THEN vtr.classificacao ELSE NULL END AS classificacao
		FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
			   INNER JOIN #temp_local_pista lpv
					ON  lpv.id_local = vtr.id_local
						AND lpv.id_pista = vtr.id_pista
		WHERE  vtr.id_local = @id_local
			   AND vtr.data >= @data_exibicao_inicial_usuarios
			   AND vtr.enviado_cliente = 0
			   AND vtr.estado_veiculo IN (2,3)
		ORDER BY
			   vtr.data DESC
    
		FETCH NEXT FROM equip_tempo_real_cursor 
		INTO @id_local, @data_exibicao_inicial_usuarios

	END 
	CLOSE equip_tempo_real_cursor;
	DEALLOCATE equip_tempo_real_cursor;

	UPDATE muralha.veiculo_tempo_real
	SET	   enviado_cliente = 1, data_enviado = GETDATE()
	WHERE  id IN (SELECT id FROM #temp_veiculos)

	SET NOCOUNT OFF;  

	SELECT id,
		   placa,
		   data,
		   id_local,
		   nome,
		   id_pista,
		   faixa,
		   velocidade,
		   enviado_cliente,
		   classificacao
	FROM   #temp_veiculos
	ORDER BY
		   data

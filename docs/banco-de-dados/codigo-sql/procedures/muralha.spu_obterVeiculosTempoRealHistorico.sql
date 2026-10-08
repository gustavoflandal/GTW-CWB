CREATE PROCEDURE [muralha].[spu_obterVeiculosTempoRealHistorico]
AS
	SET NOCOUNT ON;

	DECLARE @id_local INT, @nome VARCHAR(100)

	IF OBJECT_ID('tempdb..#locais') IS NOT NULL
	BEGIN
		DROP TABLE #locais
	END
	IF OBJECT_ID('tempdb..#temp_local_pista') IS NOT NULL
	BEGIN
		DROP TABLE #temp_local_pista
	END
	IF OBJECT_ID('tempdb..#temp_veiculos') IS NOT NULL
	BEGIN
		DROP TABLE #temp_veiculos
	END

	CREATE TABLE #locais (id_local INT INDEX IX1)
	CREATE TABLE #temp_local_pista (id_local INT INDEX IX1, id_pista TINYINT INDEX IX2, faixa TINYINT, nome VARCHAR(100), serie_equipamento INT, codigo_equipamento VARCHAR(10))
	CREATE TABLE #temp_veiculos (id UNIQUEIDENTIFIER, placa VARCHAR(7), data DATETIME, id_local INT, serie_equipamento INT, codigo_equipamento VARCHAR(10), nome VARCHAR(100), id_pista TINYINT, faixa TINYINT, velocidade SMALLINT, enviado_cliente BIT, classificacao CHAR(1), perfil_1 varchar(8000), perfil_2 varchar(8000), placa_frontal varchar(7), info_adicional varchar(1000))

	INSERT INTO #locais
	SELECT cfg.id_local
	FROM   muralha.config_envio_tempo_real_equipamento cfg (NOLOCK)
	WHERE  cfg.enviar = 1

	INSERT INTO #temp_local_pista
	SELECT lpv.id_local, lpv.id_pista, lpv.cod_pista_alternativo AS faixa, RTRIM(lpv.nome) AS nome, lpv.serie_equipamento, lpv.codigo_equipamento
	FROM   local_pista_vigente lpv
		   INNER JOIN #locais l
				ON  l.id_local = lpv.id_local

	DECLARE equip_tempo_real_cursor CURSOR FOR 
	SELECT id_local
	FROM   #locais

	OPEN equip_tempo_real_cursor

	FETCH NEXT FROM equip_tempo_real_cursor 
	INTO @id_local

	WHILE @@FETCH_STATUS = 0
	BEGIN

		INSERT INTO #temp_veiculos
		SELECT TOP 4
			   vtr.id,
			   vtr.placa,
			   vtr.data,
			   vtr.id_local,
			   lpv.serie_equipamento,
			   lpv.codigo_equipamento,
			   lpv.nome,
			   lpv.id_pista,
			   lpv.faixa,
			   vtr.velocidade,
			   vtr.enviado_cliente,
			   CASE WHEN vtr.classificacao LIKE '[a-zA-Z]' THEN vtr.classificacao ELSE NULL END AS classificacao,
			   vtr.perfil_1, 
			   vtr.perfil_2, 
			   vtr.placa_frontal,
			   vtr.info_adicional
		FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
			   INNER JOIN #temp_local_pista lpv
					ON  lpv.id_local = vtr.id_local
						AND lpv.id_pista = vtr.id_pista
		WHERE  vtr.id_local = @id_local
			   AND vtr.estado_veiculo IN (2, 3)
		ORDER BY
			   vtr.data DESC
    
		FETCH NEXT FROM equip_tempo_real_cursor 
		INTO @id_local

	END 
	CLOSE equip_tempo_real_cursor;
	DEALLOCATE equip_tempo_real_cursor;

	SET NOCOUNT OFF;

	SELECT id,
		   placa,
		   data,
		   id_local,
		   serie_equipamento,
		   codigo_equipamento,
		   nome,
		   id_pista,
		   faixa,
		   velocidade,
		   enviado_cliente,
		   classificacao,
		   perfil_1, 
		   perfil_2, 
		   placa_frontal,
		   info_adicional
	FROM   #temp_veiculos
	ORDER BY
		   id_local,
		   data

CREATE PROCEDURE [muralha].[spuObterDadosVeiculoTempoReal] @id_veiculo_tempo_real UNIQUEIDENTIFIER
AS
	--DECLARE @id_veiculo_tempo_real UNIQUEIDENTIFIER = '526AF634-1F1A-45A9-91B6-845149938F0E' --COM IMAGEM
	--DECLARE @id_veiculo_tempo_real UNIQUEIDENTIFIER = 'C9514D4E-1BA7-44D6-B842-B94BBC025A48' --SEM IMAGEM
	DECLARE @veiculo AS TABLE (id UNIQUEIDENTIFIER, placa VARCHAR(7), data DATETIME, id_local INT, id_pista TINYINT, velocidade INT, classificacao VARCHAR(15), enviado_cliente BIT, marca VARCHAR(35), modelo VARCHAR(35))
	DECLARE @local_pista_vigente AS TABLE (serie_equipamento INT, id_local INT, codigo_equipamento VARCHAR(11), id_pista TINYINT, faixa TINYINT, nome VARCHAR(100), latitude DECIMAL(19,17), longitude DECIMAL(19,17), possui_coordenadas BIT)
	DECLARE @com_imagem BIT

	SET NOCOUNT ON;

	INSERT INTO @veiculo
	SELECT vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   vtr.id_pista,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END AS velocidade,
		   --COALESCE(vtr.classificacao,'') AS classificacao,
		   CASE WHEN cv.id_classe LIKE '[a-zA-Z]' THEN COALESCE(RTRIM(cv.descricao),'') ELSE 'Veíc. Passeio' END AS classificacao,
		   vtr.enviado_cliente,
		   NULL AS marca,
		   NULL AS modelo
	FROM   muralha.veiculo_tempo_real vtr
		   LEFT JOIN classe_veiculo cv
				ON  cv.id_classe = vtr.classificacao
	WHERE  vtr.id = @id_veiculo_tempo_real

	SET @com_imagem = (SELECT COUNT(*) FROM muralha.veiculo_tempo_real_imagem WHERE id_veiculo_tempo_real = @id_veiculo_tempo_real AND indice_imagem = 0)
	IF (@com_imagem > 1)
	BEGIN
		SET @com_imagem = 1
	END

	INSERT INTO @local_pista_vigente
	SELECT l.serie_equipamento,
		   l.id_local,
		   l.codigo_equipamento,
		   l.id_pista,
		   l.cod_pista_alternativo AS faixa,
		   RTRIM(l.nome) AS nome,
		   l.posicao_lat AS latitude,
		   l.posicao_lon AS longitude,
		   CASE WHEN l.posicao_lat IS NOT NULL AND l.posicao_lon IS NOT NULL THEN 1 ELSE 0 END AS possui_coordenadas 
	FROM   local_pista_vigente l
		   JOIN @veiculo v
				ON  v.id_local = l.id_local
					AND v.id_pista = l.id_pista

	SELECT vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   lv.nome,
		   lv.id_pista,
		   lv.faixa,
		   lv.latitude,
		   lv.longitude,
		   vtr.velocidade,
		   vtr.classificacao,
		   vtr.enviado_cliente,
		   @com_imagem AS com_imagem,
		   lv.possui_coordenadas,
		   vtr.marca,
		   vtr.modelo
	FROM   @veiculo vtr
		   INNER JOIN @local_pista_vigente lv
				ON  lv.id_local = vtr.id_local
					AND lv.id_pista = vtr.id_pista
	GROUP BY
		   vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   lv.nome,
		   lv.id_pista,
		   lv.faixa,
		   lv.latitude,
		   lv.longitude,
		   vtr.velocidade,
		   vtr.classificacao,
		   vtr.enviado_cliente,
		   lv.possui_coordenadas,
		   vtr.marca,
		   vtr.modelo

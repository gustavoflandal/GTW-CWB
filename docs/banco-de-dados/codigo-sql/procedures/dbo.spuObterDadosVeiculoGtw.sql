CREATE PROCEDURE [dbo].[spuObterDadosVeiculoGtw] @id_veiculo_unic BIGINT
AS
	--DECLARE @id_veiculo_unic BIGINT = -4555066552659285325 --COM IMAGEM
	--DECLARE @id_veiculo_unic BIGINT = 1910549890724139381 --SEM IMAGEM
	--DECLARE @id_veiculo_unic BIGINT = 3900388313005688542 --COM PESAGEM
	DECLARE @veiculo AS TABLE (id BIGINT, id_veiculo BIGINT, seq_passagem INT, placa VARCHAR(7), data DATETIME, id_local INT, id_pista TINYINT, comprimento DECIMAL(6,1), velocidade INT, classificacao VARCHAR(15),
								enviado_cliente BIT, marca VARCHAR(35), modelo VARCHAR(35), com_pesagem BIT, classificacao_art96 VARCHAR(100))
	DECLARE @veiculo_pesagem AS TABLE (id BIGINT, pbt FLOAT, pbtc FLOAT, numero_eixos INT, [E1] FLOAT, [E2] FLOAT, [E3] FLOAT, [E4] FLOAT, [E5] FLOAT, [E6] FLOAT, [E7] FLOAT, [E8] FLOAT, [E9] FLOAT,
										[distancia_E1E2] FLOAT, [distancia_E2E3] FLOAT, [distancia_E3E4] FLOAT, [distancia_E4E5] FLOAT, [distancia_E5E6] FLOAT, [distancia_E6E7] FLOAT, [distancia_E7E8] FLOAT, [distancia_E8E9] FLOAT)
	DECLARE @local_pista_vigente AS TABLE (serie_equipamento INT, id_local INT, codigo_equipamento VARCHAR(11), id_pista TINYINT, faixa TINYINT, nome VARCHAR(100), sentido VARCHAR(30), latitude DECIMAL(19,17), longitude DECIMAL(19,17), possui_coordenadas BIT)
	DECLARE @com_imagem BIT, @com_pesagem BIT
	SET NOCOUNT ON;

	INSERT INTO @veiculo
	SELECT vtr.id_veiculo_unic AS id,
		   vtr.id_veiculo,
		   vtr.id_veiculo_local AS seq_passagem,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   vtr.pista AS id_pista,
		   vtr.comprimento,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE CAST(vtr.velocidade AS INT) END AS velocidade,
		   --COALESCE(vtr.classificacao,'') AS classificacao,
		   CASE WHEN cv.id_classe LIKE '[a-zA-Z]' THEN COALESCE(RTRIM(cv.descricao),'') ELSE 'Veíc. Passeio' END AS classificacao,
		   NULL AS enviado_cliente,
		   NULL AS marca,
		   NULL AS modelo,
		   vtr.com_pesagem,
		   vtr.classificacao AS classificacao_art96
	FROM   dbo.veiculo_pesquisa vtr
		   LEFT JOIN classe_veiculo cv
				ON  cv.id_classe = vtr.id_classe
	WHERE  vtr.id_veiculo_unic = @id_veiculo_unic

	SET @com_imagem = (SELECT COUNT(id_imagem) FROM dbo.imagem WHERE id_imagem IN (SELECT id_imagem FROM veiculo_imagem WHERE id_veiculo IN (SELECT id_veiculo FROM veiculo WHERE id_veiculo_unic = @id_veiculo_unic)) AND indice_imagem = 0)
	IF (@com_imagem > 1)
	BEGIN
		SET @com_imagem = 1
	END

	INSERT INTO @veiculo_pesagem
	SELECT vp.id_veiculo_unic AS id,
		   vp.pbt,
		   vpe.pbtc,
		   vpe.qtde_eixos AS numero_eixos, vpe.[E1], vpe.[E2], vpe.[E3], vpe.[E4], vpe.[E5], vpe.[E6], vpe.[E7], vpe.[E8], vpe.[E9],
		   vpde.distancia_E1E2, vpde.distancia_E2E3, vpde.distancia_E3E4, vpde.distancia_E4E5, vpde.distancia_E5E6, vpde.distancia_E6E7, vpde.distancia_E7E8, vpde.distancia_E8E9
	FROM   dbo.v_veiculo_pesagem vp
		   JOIN v_veiculo_pesagem_eixo vpe
				ON  vpe.id_veiculo_unic = vp.id_veiculo_unic
		   JOIN v_veiculo_pesagem_distancia_eixos vpde
				ON  vpde.id_veiculo_unic = vp.id_veiculo_unic
	WHERE  vp.id_veiculo_unic = @id_veiculo_unic

	INSERT INTO @local_pista_vigente
	SELECT l.serie_equipamento,
		   l.id_local,
		   l.codigo_equipamento,
		   l.id_pista,
		   l.cod_pista_alternativo AS faixa,
		   RTRIM(l.nome) AS nome,
		   RTRIM(l.sentido) AS sentido,
		   l.posicao_lat AS latitude,
		   l.posicao_lon AS longitude,
		   CASE WHEN l.posicao_lat IS NOT NULL AND l.posicao_lon IS NOT NULL THEN 1 ELSE 0 END AS possui_coordenadas 
	FROM   local_pista_vigente l
		   JOIN @veiculo v
				ON  v.id_local = l.id_local
					AND v.id_pista = l.id_pista

	SELECT vtr.id,
		   vtr.id_veiculo,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   lv.nome,
		   lv.sentido,
		   lv.id_pista,
		   lv.faixa,
		   lv.latitude,
		   lv.longitude,
		   vtr.comprimento,
		   vtr.velocidade,
		   vtr.classificacao,
		   vtr.enviado_cliente,
		   @com_imagem AS com_imagem,
		   vtr.com_pesagem,
		   lv.possui_coordenadas,
		   vtr.marca,
		   vtr.modelo,
		   vp.pbt,
		   vp.pbtc,
		   vp.numero_eixos, vp.[E1], vp.[E2], vp.[E3], vp.[E4], vp.[E5], vp.[E6], vp.[E7], vp.[E8], vp.[E9],
		   vp.distancia_E1E2, vp.distancia_E2E3, vp.distancia_E3E4, vp.distancia_E4E5, vp.distancia_E5E6, vp.distancia_E6E7, vp.distancia_E7E8, vp.distancia_E8E9,
		   vtr.classificacao_art96
	FROM   @veiculo vtr
		   INNER JOIN @local_pista_vigente lv
				ON  lv.id_local = vtr.id_local
					AND lv.id_pista = vtr.id_pista
		   LEFT JOIN @veiculo_pesagem vp
				ON  vp.id = vtr.id
	GROUP BY
		   vtr.id,
		   vtr.id_veiculo,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   lv.nome,
		   lv.sentido,
		   lv.id_pista,
		   lv.faixa,
		   lv.latitude,
		   lv.longitude,
		   vtr.comprimento,
		   vtr.velocidade,
		   vtr.classificacao,
		   vtr.enviado_cliente,
		   vtr.com_pesagem,
		   lv.possui_coordenadas,
		   vtr.marca,
		   vtr.modelo,
		   vp.pbt,
		   vp.pbtc,
		   vp.numero_eixos, vp.[E1], vp.[E2], vp.[E3], vp.[E4], vp.[E5], vp.[E6], vp.[E7], vp.[E8], vp.[E9],
		   vp.distancia_E1E2, vp.distancia_E2E3, vp.distancia_E3E4, vp.distancia_E4E5, vp.distancia_E5E6, vp.distancia_E6E7, vp.distancia_E7E8, vp.distancia_E8E9,
		   vtr.classificacao_art96

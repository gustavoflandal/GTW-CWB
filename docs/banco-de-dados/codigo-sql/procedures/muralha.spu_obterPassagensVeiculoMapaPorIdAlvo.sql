CREATE PROCEDURE [muralha].[spu_obterPassagensVeiculoMapaPorIdAlvo]
	@id_veiculo UNIQUEIDENTIFIER
AS
	--DECLARE @id_veiculo UNIQUEIDENTIFIER = 'ebe2c35b-4551-4416-8897-78f4103e0413'
	DECLARE @periodo_horas INT = 4

	SET NOCOUNT ON

	DECLARE @temp_passagens_veiculo AS TABLE
	(
		id UNIQUEIDENTIFIER,
		placa CHAR(7),
		data DATETIME,
		id_local INT,
		serie_equipamento INT,
		nome VARCHAR(100),
		id_pista TINYINT,
		latitude DECIMAL(19,17),
		longitude DECIMAL(19,17),
		velocidade SMALLINT,
		classificacao VARCHAR(15),
		enviado_cliente BIT,
		com_imagem BIT,
		possui_coordenadas BIT
	)

	INSERT INTO @temp_passagens_veiculo
	SELECT id, placa, data, id_local, serie_equipamento, nome, id_pista, latitude, longitude, velocidade, classificacao, enviado_cliente, com_imagem, possui_coordenadas
	FROM   muralha.v_veiculo_tempo_real
	WHERE  id = @id_veiculo
		   AND possui_coordenadas = 1

	--SELECT * FROM @temp_passagens_veiculo

	INSERT INTO @temp_passagens_veiculo
	SELECT vtr.id, vtr.placa, vtr.data, vtr.id_local, vtr.serie_equipamento, vtr.nome, vtr.id_pista, vtr.latitude, vtr.longitude, vtr.velocidade, vtr.classificacao, vtr.enviado_cliente, vtr.com_imagem, vtr.possui_coordenadas
	FROM   muralha.v_veiculo_tempo_real vtr
		   INNER JOIN @temp_passagens_veiculo a
				ON  a.id != vtr.id
					AND a.placa = vtr.placa
					AND vtr.data BETWEEN DATEADD(HOUR, (@periodo_horas*(-1)), a.data) AND DATEADD(HOUR, @periodo_horas, a.data)
	WHERE  vtr.possui_coordenadas = 1

	SELECT * FROM @temp_passagens_veiculo ORDER BY data

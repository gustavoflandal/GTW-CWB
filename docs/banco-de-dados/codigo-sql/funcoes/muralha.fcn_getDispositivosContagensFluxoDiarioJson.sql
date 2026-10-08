CREATE FUNCTION [muralha].[fcn_getDispositivosContagensFluxoDiarioJson] (@id_local INT)
RETURNS NVARCHAR(MAX)
AS
BEGIN

	--DECLARE @id_local INT = 1
	DECLARE @Resultado NVARCHAR(MAX)

	DECLARE @contagem_veiculos AS TABLE (id_local INT, data DATE, hora TINYINT, veiculos_detectados INT, velocidade_media INT)
	DECLARE @data_ini DATETIME = CAST(CAST(GETDATE() AS DATE) AS DATETIME), @data_fim DATETIME = GETDATE()

	INSERT INTO @contagem_veiculos
	SELECT id_local,
		   CAST(data AS DATE) AS data,
		   DATEPART(HOUR, vtr.data) AS hora,
		   COUNT(*) AS veiculos_detectados,
		   AVG(CASE WHEN vtr.velocidade BETWEEN 5 AND 200 THEN vtr.velocidade ELSE NULL END) AS velocidade_media
	FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
	WHERE  vtr.data BETWEEN @data_ini AND @data_fim
	GROUP BY
		   id_local,
		   CAST(data AS DATE),
		   DATEPART(HOUR, vtr.data)

	--SELECT * FROM @contagem_veiculos
	SET @Resultado = (
	SELECT l.hora,
		   l.hora_desc,
		   ISNULL(ct.veiculos_detectados, 0) AS fluxo,
		   ISNULL(ct.velocidade_media, 0) AS vel_media
	--SELECT *
	FROM   (
				SELECT lv.id_local, lv.serie_equipamento, RTRIM(lv.nome) AS nome, lv.posicao_lat, lv.posicao_lon, h.*
				FROM   local_vigente lv (NOLOCK)
					   CROSS JOIN hora h
				WHERE  lv.desativado = 0
					   AND h.hora <= DATEPART(HOUR, @data_fim)
				--ORDER BY lv.id_local, h.hora
		   ) l
		   LEFT JOIN @contagem_veiculos ct
				ON  ct.id_local = l.id_local
					AND ct.hora = l.hora
	WHERE  l.id_local = @id_local
	ORDER BY
		   l.hora
	FOR JSON AUTO
	)

	--SELECT @Resultado
		
	RETURN @Resultado

END

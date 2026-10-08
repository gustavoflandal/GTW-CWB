CREATE PROCEDURE [muralha].[spu_ObterDadosLinhaTempo] @idAlerta UNIQUEIDENTIFIER, @idVeiculo UNIQUEIDENTIFIER
AS
	--DECLARE @idAlerta UNIQUEIDENTIFIER = '66e0d913-8e26-4026-a4e0-6e824c1ada56', @idVeiculo UNIQUEIDENTIFIER = NULL--'6157e6b4-e4db-459f-bbd9-6dbf972f1a24'
	DECLARE @data_veiculo DATETIME, @data_importado DATETIME, @data_alerta DATETIME, @data_enviado DATETIME, @data_processado DATETIME
	DECLARE @resultado AS TABLE (passo INT, nome_passo VARCHAR(30), data DATETIME, tempo VARCHAR(100))

	SET NOCOUNT ON
	
	IF (@idAlerta IS NOT NULL)
	BEGIN
		SELECT @data_alerta = a.data, @data_enviado = a.data_enviado FROM muralha.alerta a WHERE a.id = @idAlerta

		SELECT @data_veiculo = data, @data_importado = data_importado
		FROM   muralha.veiculo_tempo_real
		WHERE  data IN (SELECT MIN(data) FROM muralha.veiculo_tempo_real WHERE id IN (SELECT id_veiculo_tempo_real FROM muralha.alerta_veiculo WHERE id_alerta = @idAlerta))
			   AND id IN (SELECT id_veiculo_tempo_real FROM muralha.alerta_veiculo WHERE id_alerta = @idAlerta)

		/*
		SELECT 'ALERTA' AS item,
			   @data_veiculo AS data_veiculo, @data_importado AS data_importado, @data_alerta AS data_alerta, @data_enviado AS data_enviado,
			   DATEDIFF(SECOND, @data_veiculo, @data_importado) AS tempo_recebimento_imagem,
			   DATEDIFF(SECOND, @data_importado, @data_alerta) AS tempo_processamento,
			   DATEDIFF(SECOND, @data_alerta, @data_enviado) AS tempo_alarme
		*/

		INSERT INTO @resultado
		SELECT t.passo,
			   t.nome_passo,
			   t.data,
			   CASE WHEN t.tempo IS NULL THEN NULL
					WHEN t.tempo = 1 THEN t.origem + CAST(t.tempo AS VARCHAR) + ' segundo'
					ELSE t.origem + CAST(t.tempo AS VARCHAR) + ' segundos'
				END AS tempo
		FROM   (
					SELECT 1 AS passo, 'Data de Captura da Imagem' AS nome_passo, @data_veiculo AS data, NULL AS origem, NULL AS tempo UNION
					SELECT 2 AS passo, 'Data de Recebimento CAM' AS nome_passo, @data_importado AS data, 'Tempo desde captura: ' AS origem, DATEDIFF(SECOND, @data_veiculo, @data_importado) AS tempo UNION
					SELECT 3 AS passo, 'Data de Processamento' AS nome_passo, @data_alerta AS data, 'Tempo desde importação: ' AS origem, DATEDIFF(SECOND, @data_importado, @data_alerta) AS tempo UNION
					SELECT 4 AS passo, 'Data de Disparo do Alarme' AS nome_passo, @data_enviado AS data, 'Tempo desde processamento: ' AS origem, DATEDIFF(SECOND, @data_alerta, @data_enviado) AS tempo
			   ) t
	END
	ELSE IF (@idVeiculo IS NOT NULL)
	BEGIN
		--DECLARE @idVeiculo UNIQUEIDENTIFIER = '6157e6b4-e4db-459f-bbd9-6dbf972f1a24'
		SELECT @data_veiculo = data, @data_importado = data_importado, @data_processado = data_processado_tarefas_alerta
		FROM   muralha.veiculo_tempo_real
		WHERE  id = @idVeiculo

		/*
		SELECT 'VEICULO' AS item,
			   @data_veiculo AS data_veiculo, @data_importado AS data_importado, @data_processado AS data_processado,
			   DATEDIFF(SECOND, @data_veiculo, @data_importado) AS tempo_recebimento_imagem,
			   DATEDIFF(SECOND, @data_importado, @data_processado) AS tempo_processamento
		*/

		INSERT INTO @resultado
		SELECT t.passo,
			   t.nome_passo,
			   t.data,
			   CASE WHEN t.tempo IS NULL THEN NULL
					WHEN t.tempo = 1 THEN t.origem + CAST(t.tempo AS VARCHAR) + ' segundo'
					ELSE t.origem + CAST(t.tempo AS VARCHAR) + ' segundos'
				END AS tempo
		FROM   (
					SELECT 1 AS passo, 'Data de Captura da Imagem' AS nome_passo, @data_veiculo AS data, NULL AS origem, NULL AS tempo UNION
					SELECT 2 AS passo, 'Data de Recebimento CAM' AS nome_passo, @data_importado AS data, 'Tempo desde captura: ' AS origem, DATEDIFF(SECOND, @data_veiculo, @data_importado) AS tempo UNION
					SELECT 3 AS passo, 'Data de Processamento' AS nome_passo, @data_processado AS data, 'Tempo desde importação: ' AS origem, DATEDIFF(SECOND, @data_importado, @data_processado) AS tempo
			   ) t
	END

	SELECT * FROM @resultado ORDER BY passo
CREATE PROCEDURE [dbo].[spu_sumariza_veiculos]
	@data_inicio DATE,
	@data_fim DATE
AS

INSERT INTO veiculo_sumarizado with (rowlock) (
	data,
	hora,
	id_local,
	sequencia_local,
	pista,
	id_classe,
	id_faixa_velocidade,
	media_velocidade,
	min_velocidade,
	max_velocidade,
	trafego,
	ocupacao,
	placa_lida)
SELECT
	CAST(data AS date) AS data,
	DATEPART(HH, data) AS hora,
	id_local,
	sequencia_local,
	pista,
	id_classe,
	(CASE 
		WHEN id_faixa_velocidade < 15 
			THEN id_faixa_velocidade 
		ELSE 
			16 
		END) AS id_faixa_velocidade, 
	AVG(velocidade) AS media_velocidade,
	MIN(velocidade) AS min_velocidade,
	MAX(velocidade) AS max_velocidade,
	COUNT(*) AS trafego,
	SUM(ocupacao) AS ocupacao,
	SUM(CASE WHEN v.placa IS NOT NULL THEN 1 ELSE 0 END) AS placa_lida
FROM
	veiculo_pesquisa v (nolock)
WHERE	CAST(data AS DATE) >= @data_inicio
	AND CAST(data AS DATE) <= @data_fim
GROUP BY
	CAST(data AS DATE),
	DATEPART(HH, data),
	id_local,
	sequencia_local,
	pista,
	id_classe,
	(CASE 
		WHEN id_faixa_velocidade < 15 
			THEN id_faixa_velocidade 
		ELSE 
			16 
	END)

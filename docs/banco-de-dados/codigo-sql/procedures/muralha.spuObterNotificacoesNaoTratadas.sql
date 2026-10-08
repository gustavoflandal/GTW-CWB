CREATE PROCEDURE [muralha].[spuObterNotificacoesNaoTratadas]
AS
	SET NOCOUNT ON

	CREATE TABLE #alerta (ordem INT INDEX IX1, id UNIQUEIDENTIFIER INDEX IX2, id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER INDEX IX3, data DATETIME, lembrete_visualizado INT, id_cad_veiculo_monitorado UNIQUEIDENTIFIER INDEX IX4)
	CREATE TABLE #alerta_veiculo (id_alerta UNIQUEIDENTIFIER INDEX IX1, id_veiculo_tempo_real UNIQUEIDENTIFIER INDEX IX2)

	INSERT INTO #alerta
	SELECT ROW_NUMBER() OVER(ORDER BY lembrete_visualizado, data DESC) AS ordem, id, id_tipo_alerta_ocorrencia, data, lembrete_visualizado, id_cad_veiculo_monitorado FROM muralha.alerta WHERE lembrete_visualizado < 2

	INSERT INTO #alerta_veiculo
	SELECT tmp.id_alerta,
		   MAX(tmp.id_veiculo_tempo_real) AS id_veiculo_tempo_real
	FROM   muralha.alerta_veiculo tmp
		   JOIN #alerta a
				ON  a.id = tmp.id_alerta
	GROUP BY
		   tmp.id_alerta

	SELECT a.id,
		   tao.tipo,
		   a.id_tipo_alerta_ocorrencia,
		   COALESCE(cvm.placa, vtr.placa, 'ERRO') as placa,
		   CONVERT(varchar, a.data, 103) + ' ' + CONVERT(varchar, a.data, 108) as data,
		   a.lembrete_visualizado,
		   COUNT(*) OVER() AS total_registros
	FROM   #alerta a
		   INNER JOIN muralha.tipo_alerta_ocorrencia tao
				ON  tao.id = a.id_tipo_alerta_ocorrencia
		   INNER JOIN muralha.cad_veiculo_monitorado cvm
				ON  cvm.id = a.id_cad_veiculo_monitorado
		   INNER JOIN #alerta_veiculo AS av
				ON  av.id_alerta = a.id
		   INNER JOIN muralha.veiculo_tempo_real vtr
				ON  vtr.id = av.id_veiculo_tempo_real
	--WHERE  a.lembrete_visualizado < 2
	ORDER BY  
		   a.ordem
	OFFSET 0 ROWS FETCH NEXT 25 ROWS ONLY

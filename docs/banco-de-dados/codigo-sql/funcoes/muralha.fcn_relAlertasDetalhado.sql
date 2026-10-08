CREATE FUNCTION [muralha].[fcn_relAlertasDetalhado](@dataInicio DATETIME, @dataFim DATETIME)
RETURNS TABLE
AS
	RETURN
	(
		--DECLARE @dataInicio DATETIME = '2021-10-01 00:00:00', @dataFim DATETIME = '2021-12-31 23:59:59'
		SELECT a.data AS data_alerta,
			   a.tipo_alerta_ocorrencia AS tipo_alerta,
			   RTRIM(CAST(lv.serie_equipamento AS VARCHAR(20))) + ' - ' + RTRIM(lv.nome) AS equipamento,
			   a.placa_lida AS placa_lida,
			   cvm.placa AS placa_monitorada,
			   cvm.data_cadastro AS data_cadastro_monitoramento,
			   cvm.data_inicio AS data_inicio_monitoramento,
			   cvm.data_fim AS data_fim_monitoramento,
			   CASE WHEN cvm.data_fim IS NULL OR cvm.data_fim >= CAST(GETDATE() AS DATE) THEN 'VIGENTE' ELSE 'ENCERRADO' END AS situacao_cadastro,
			   a.usuario AS usuario_alerta
		FROM   muralha.fcn_ObterAlertasAlt() a
			   JOIN local_vigente lv
					ON  lv.id_local = a.id_local
			   JOIN muralha.cad_veiculo_monitorado cvm
					ON  cvm.id = a.id_cad_veiculo_monitorado
		WHERE  a.data BETWEEN @dataInicio AND @dataFim
	)

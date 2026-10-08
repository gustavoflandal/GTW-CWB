CREATE FUNCTION [muralha].[fcn_relOcorrenciasDetalhado](@dataInicio DATETIME, @dataFim DATETIME)
RETURNS TABLE
AS
	RETURN
	(
		--DECLARE @dataInicio DATETIME = '2021-10-01 00:00:00', @dataFim DATETIME = '2021-12-31 23:59:59'
		SELECT o.data AS data_ocorrencia,
			   o.tipo_alerta_ocorrencia AS tipo_ocorrencia,
			   RTRIM(CAST(lv.serie_equipamento AS VARCHAR(20))) + ' - ' + RTRIM(lv.nome) AS equipamento,
			   o.placa_lida AS placa_lida,
			   cvm.placa AS placa_monitorada,
			   cvm.data_cadastro AS data_cadastro_monitoramento,
			   cvm.data_inicio AS data_inicio_monitoramento,
			   cvm.data_fim AS data_fim_monitoramento,
			   CASE WHEN cvm.data_fim IS NULL OR cvm.data_fim >= CAST(GETDATE() AS DATE) THEN 'VIGENTE' ELSE 'ENCERRADO' END AS situacao_cadastro,
			   o.usuario AS usuario_ocorrencia
		FROM   muralha.fcn_ObterOcorrenciasAlt() o
			   JOIN local_vigente lv
					ON  lv.id_local = o.id_local
			   JOIN muralha.cad_veiculo_monitorado cvm
					ON  cvm.id = o.id_cad_veiculo_monitorado
		WHERE  o.data BETWEEN @dataInicio AND @dataFim
	)

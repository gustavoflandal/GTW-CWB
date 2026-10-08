
CREATE VIEW [muralha].[v_config_status_tipo_alerta]
AS
	SELECT *
	FROM   (
				SELECT 'sequestro_relampago' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = 'CB8D5C4B-1822-4868-A2F9-0153B50212DA'UNION ALL
				SELECT 'roubado' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = '95631582-96B2-4220-9612-12131BE4923C'UNION ALL
				SELECT 'comboio' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = '19B86A23-2CD6-43ED-A596-62935EA3980A'UNION ALL
				SELECT 'clandestino' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = 'AE93F81A-DF6D-41B5-AFC6-99B3438C291D'UNION ALL
				SELECT 'roubo_banco' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = 'FEEF9500-83C0-4942-A8AA-AED77E20BA5B'UNION ALL
				SELECT 'furtado' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = '0349F722-DFDE-4080-9E3B-D65F1C058EDC'UNION ALL
				SELECT 'clonado' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = 'CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3'UNION ALL
				SELECT 'licenciamento' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = '6631DC43-779F-4BFF-A329-B9653D056708'UNION ALL
				SELECT 'monitorado' AS tipo, CAST(tarefa_ativa AS TINYINT) AS tarefa_ativa FROM muralha.tipo_alerta_ocorrencia WHERE id = '9D31A265-A663-4836-BF00-2309FC0D5E33'
	) r
	PIVOT (
			MAX(tarefa_ativa)
			FOR tipo IN ([sequestro_relampago],[roubado],[comboio],[clandestino],[roubo_banco],[furtado],[clonado],[licenciamento],[monitorado])
		  ) cont

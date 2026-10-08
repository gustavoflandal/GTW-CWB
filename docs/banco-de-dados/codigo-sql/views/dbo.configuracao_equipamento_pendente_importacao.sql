
CREATE VIEW [dbo].[configuracao_equipamento_pendente_importacao]
AS
	SELECT
		ai.id_arquivo,
		ai.id_local,
		ce.sequencia_local,
		cei.data_configuracao,
		ai.nome_arquivo,
		cei.xml_configuracao
	FROM configuracao_equipamento_importacao cei
		INNER JOIN arquivos_importados ai (nolock) 
			ON ai.id_arquivo = cei.id_arquivo
		INNER JOIN local_vigente lv (nolock)
			ON lv.id_local = ai.id_local			
		LEFT JOIN (	SELECT 
						l.id_local, 
						l.sequencia_local, 
						ce.data_modificacao
					FROM configuracao_equipamento ce (nolock)
						INNER JOIN [local] l (nolock)
							ON l.id_configuracao_equipamento = ce.id_configuracao_equipamento
					) AS ce
			ON CONVERT(CHAR(19),ce.data_modificacao,120) = CONVERT(CHAR(19),cei.data_configuracao,120) and ai.id_local = ce.id_local



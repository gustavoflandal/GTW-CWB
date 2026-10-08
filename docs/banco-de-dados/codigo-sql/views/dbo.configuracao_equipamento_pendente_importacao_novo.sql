CREATE VIEW [dbo].[configuracao_equipamento_pendente_importacao_novo]
AS
    SELECT 
		vi.data,
		vi.id_veiculo_unic,
        ce.sequencia_local
    FROM
        configuracao_equipamento_importacao cei (NOLOCK)
        JOIN arquivos_importados ai (NOLOCK)
            ON ai.id_arquivo = cei.id_arquivo
		JOIN veiculo_importacao vi (NOLOCK) 
			ON ai.nome_arquivo = vi.nome_arquivo
        JOIN configuracao_equipamento_data_modificacao (NOLOCK) AS ce
            ON ai.id_local = ce.id_local 
			AND ce.data_modificacao = cei.data_configuracao 
	WHERE vi.sequencia_local IS NULL


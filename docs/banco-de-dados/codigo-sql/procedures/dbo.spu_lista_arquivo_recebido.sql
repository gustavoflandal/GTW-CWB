
CREATE PROCEDURE [dbo].[spu_lista_arquivo_recebido]
	@nome_arquivo VARCHAR(50)
AS

	SELECT 
		a.nome_arquivo AS nome_arquivo
	FROM 
		arquivos a (nolock)
	WHERE 
		a.nome_arquivo = @nome_arquivo





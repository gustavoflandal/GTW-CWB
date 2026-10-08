CREATE PROCEDURE [dbo].[spu_lista_arquivos_recebidos]
	@serie_equipamento INT,
	@dias_atras INT = 30 
AS

	DECLARE	@data_inicio 	DATETIME
	DECLARE	@data_fim	DATETIME
	DECLARE	@id_configuracao_equipamento	INT
	
	SET @data_inicio = GETDATE() - @dias_atras
	SET @data_fim	 = GETDATE()
	
	SELECT
		a.nome_arquivo AS nome_arquivo
	FROM 
		arquivos a (nolock)
	ORDER BY 
		SUBSTRING(a.nome_arquivo,6,14) DESC






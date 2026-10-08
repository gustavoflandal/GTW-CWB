CREATE PROCEDURE [dbo].[spu_cria_exportacao_trafego]
	@data_trafego DATE
AS 

	DECLARE @id_exportacao_trafego INT
	DECLARE @total_trafego INT = 0
	DECLARE @id_primeiro_arquivo INT = 0
	DECLARE @id_ultimo_arquivo INT = 0
	DECLARE @arquivos TABLE
	(
		id_arquivo INT
	)

	SET NOCOUNT ON
	
	INSERT INTO @arquivos
		SELECT DISTINCT 
			id_arquivo 
		FROM 
			veiculo_estatistica ve (nolock)
		WHERE	CAST(data as DATE) = @data_trafego 
			AND	id_arquivo NOT IN (	SELECT id_arquivo 
										FROM exportacao_trafego_arquivo (nolock))

	SELECT 
		@total_trafego = COUNT(*)
	FROM 
		veiculo_estatistica ve (nolock)
	WHERE id_arquivo IN (SELECT id_arquivo 
							FROM @arquivos)
	
	IF (@total_trafego = 0)
		RAISERROR('Não existe trafego a ser exportado.', 16, 1) 
	
	INSERT INTO exportacao_trafego with (rowlock) 
		(data_criacao, data_trafego, total_trafego)
	VALUES 
		(GETDATE(), @data_trafego, @total_trafego)
		 
	SET @id_exportacao_trafego = @@IDENTITY

	INSERT INTO exportacao_trafego_arquivo with (rowlock)
		(id_exportacao_trafego, id_arquivo)
	SELECT 
		@id_exportacao_trafego, 
		id_arquivo 
	FROM @arquivos
	
	RETURN @id_exportacao_trafego





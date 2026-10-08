
CREATE PROCEDURE [dbo].[spu_cria_exportacao_imagem]
	@id_remessa INT
AS 

	DECLARE @id_exportacao_imagem INT;
	DECLARE @total_imagens INT = 0;

	SET NOCOUNT ON
	
	SELECT @total_imagens = COUNT(*) 
	FROM infracao_imagem ii (nolock)
		INNER JOIN veiculo_imagem vi (nolock)
			ON vi.id_imagem IN (ii.id_imagem_obj,ii.id_imagem_pan,ii.id_imagem_pan2)
		INNER JOIN infracao_remessa ir (nolock)
			ON ir.id_infracao = ii.id_infracao
	WHERE 
		ir.id_remessa = @id_remessa
	
	IF (@total_imagens = 0)
		RAISERROR('Não existem imagens a serem exportadas.', 16, 1); 
	
	INSERT INTO exportacao_imagem with (rowlock)
		(data_criacao, total_imagens)
	VALUES 
		(GETDATE(), @total_imagens)
		 
	SET @id_exportacao_imagem = @@IDENTITY
	
	INSERT INTO exportacao_imagem_imagem with (rowlock) 
		(id_exportacao_imagem, id_imagem)
	SELECT 
		@id_exportacao_imagem, 
		id_imagem 
	FROM infracao_imagem ii (nolock)
		INNER JOIN veiculo_imagem vi (nolock)
			ON vi.id_imagem IN (ii.id_imagem_obj,ii.id_imagem_pan,ii.id_imagem_pan2)
		INNER JOIN infracao_remessa ir (nolock)
			ON ir.id_infracao = ii.id_infracao
	WHERE 
		ir.id_remessa = @id_remessa

	RETURN @id_exportacao_imagem




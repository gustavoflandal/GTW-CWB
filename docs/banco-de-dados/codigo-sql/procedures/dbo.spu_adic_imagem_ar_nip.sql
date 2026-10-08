
CREATE PROCEDURE [dbo].[spu_adic_imagem_ar_nip]
	@id_infracao INT,
	@id_usuario INT,
	@tipo_ar CHAR(3),
	@imagem IMAGE,
	@id_status INT,
	@data_retorno DATE,
	@observacao VARCHAR(300)
AS 
	SET NOCOUNT ON
	
	DECLARE @tipo_ar_nip CHAR(3)

	INSERT INTO imagem_ar with (rowlock) 
			   (id_infracao,
				id_usuario,
				tipo_ar,
				imagem,
				data)
		 VALUES
			   (@id_infracao,
				@id_usuario,
				@tipo_ar,
				@imagem,
				GETDATE())
								 	
	UPDATE controle_ar with (rowlock) 
		SET id_status = @id_status,
			data_retorno = @data_retorno,
			observacao = @observacao
		WHERE	id_infracao = @id_infracao 
			AND tipo_ar = @tipo_ar
				 



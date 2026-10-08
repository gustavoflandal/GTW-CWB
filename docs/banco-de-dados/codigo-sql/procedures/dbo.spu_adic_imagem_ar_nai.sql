
CREATE PROCEDURE [dbo].[spu_adic_imagem_ar_nai]
	@id_infracao INT,
	@id_usuario INT,
	@tipo_ar CHAR(3),
	@imagem IMAGE,
	@id_status INT,
	@data_retorno DATE,
	@observacao VARCHAR(300)
AS 
	SET NOCOUNT ON 
	
	DECLARE @id_status_nip INT
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
	
	INSERT INTO controle_ar with (rowlock) 
				(id_infracao,
				 id_status,
				 data_retorno,
				 observacao,
				 tipo_ar)
		  VALUES
				(@id_infracao,
				 @id_status,
				 @data_retorno,
				 @observacao,
				 @tipo_ar
				 )
				 
	SET @id_status_nip = 0
	SET @tipo_ar_nip = 'NIP'
	
	INSERT INTO controle_ar with (rowlock) 
				(id_infracao,
				 id_status,
				 tipo_ar)
		  VALUES
				(@id_infracao,
				 @id_status_nip,
				 @tipo_ar_nip
				 )



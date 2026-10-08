CREATE PROCEDURE [dbo].[spu_grava_log] 
	@resumo varchar(150),
	@tipo char(3),
	@id_usuario int,
	@detalhe varchar(1000),
	@data datetime = getdate
AS
	INSERT INTO sis_log with (rowlock)
		([descricao], [data], [tipo], [id_usuario])
	VALUES
		(@resumo, @data, @tipo, @id_usuario)
	
	INSERT INTO sis_log_detalhe with (rowlock)
		([id_log], [detalhe])
	VALUES
		(@@identity, @detalhe)




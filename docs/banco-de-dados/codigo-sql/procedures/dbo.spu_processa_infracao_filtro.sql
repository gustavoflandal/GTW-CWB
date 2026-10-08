
CREATE PROCEDURE [dbo].[spu_processa_infracao_filtro] 
	@id_infracao INT, 
	@id_usuario INT, 
	@id_processo INT, 
	@id_inconsistencia INT, 
	@espera BIT,
	@id_filtro INT 
	 
AS 
 
	DECLARE @id_infracao_processo INT 
	 
	EXEC @id_infracao_processo = spu_processa_infracao 
		@id_infracao, 
		@id_usuario, 
		@id_processo, 
		@id_inconsistencia, 
		NULL, 
		NULL, 
		NULL, 
		NULL, 
		NULL, 
		0, 
		0, 
		NULL, 
		NULL, 
		0, 
		0 

	IF (@espera = 1)
		exec spu_espera_infracao 
			@id_infracao, 
			@id_usuario, 
			@id_processo
 
	IF @id_filtro IS NOT NULL 
		INSERT INTO infracao_processo_filtro with (rowlock) (
			id_infracao_processo, 
			id_filtro) 
		VALUES ( 
			@id_infracao_processo, 
			@id_filtro ) 
	 
	RETURN @id_infracao_processo




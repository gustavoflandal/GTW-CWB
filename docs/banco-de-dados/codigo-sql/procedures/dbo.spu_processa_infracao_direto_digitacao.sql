
------------------------------------------------------------------------------------------------------------------------------------

CREATE PROCEDURE [dbo].[spu_processa_infracao_direto_digitacao] 
	@id_infracao int, 
	@id_usuario int, 
	@id_processo int,
	@id_inconsistencia int	= NULL,
	@placa char(7)			= NULL,
	@id_marca_processo int	= NULL,
	@id_especie_processo int	= NULL,
	@uf_processo int		= NULL
AS 

    DECLARE @x INT 
    DECLARE @y INT 
    DECLARE @largura INT 
    DECLARE @altura INT 
 
	BEGIN TRY 
 
		BEGIN TRANSACTION 

			EXEC sp_getapplock @Resource = '[spu_processa_infracao_direto_digitacao]', @LockMode = 'Exclusive'	 
		 
			SET @x = NULL 
			SET @y = NULL 
			SET @largura = NULL 
			SET @altura = NULL 
		 
			DECLARE @id_infracao_processo int 
			DECLARE @id_imagem int 

			IF (@id_inconsistencia IS NULL)

				BEGIN

					SELECT
						@id_inconsistencia = i.id_inconsistencia
					FROM  
						infracao i (nolock)
					WHERE 
						i.id_infracao = @id_infracao 

				END

			IF (@id_inconsistencia IS NULL) 
				RAISERROR('INCONSISTÊNCIA INCOMPLETA! CONTATE O ADMINISTRADOR.', 1, 1)  
 
			SELECT  
				@id_imagem = ii.id_imagem_obj, 
				@x = [io].x, 
				@y = [io].y, 
				@largura = [io].largura, 
				@altura = [io].altura 
			FROM infracao_imagem ii (nolock)
				LEFT JOIN infracao_obliteracao [io] (nolock)
					ON [io].id_infracao = ii.id_infracao 
			WHERE 
				ii.id_infracao = @id_infracao 
		 
			EXEC @id_infracao_processo = spu_processa_infracao_com_digitacao		
				@id_infracao, 
				@id_usuario, 
				@id_processo, 
				@id_inconsistencia, 
				@id_imagem,  
				@x, 
				@y, 
				@largura, 
				@altura, 
				@placa, 
				@id_marca_processo, 
				@id_especie_processo,
				@uf_processo, 
				0, 
				0, 
				0, 
				NULL, 
				NULL,
				0  
  
			EXEC spu_conclui_infracao_processo 
				@id_infracao_processo
		 
		COMMIT 
		 
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
 			 
		EXEC spu_replica_erro 
		 
	END CATCH 
 
	RETURN @id_infracao_processo


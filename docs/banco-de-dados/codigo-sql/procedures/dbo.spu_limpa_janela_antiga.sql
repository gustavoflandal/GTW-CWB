
CREATE PROCEDURE [dbo].[spu_limpa_janela_antiga]
	@dia_inativo int = 1
AS

SET NOCOUNT ON

--DECLARE @dia_inativo INT = 1

DECLARE cur_usuarios CURSOR 
FOR
SELECT DISTINCT 
	id_usuario_atual, 
	id_processo 
FROM 
	infracao i (nolock)
WHERE 
	NOT id_usuario_atual IS NULL

DECLARE @id_usuario int
DECLARE @id_processo int
DECLARE @ultima_data_processsada datetime

BEGIN

	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	OPEN cur_usuarios
	
	FETCH FROM cur_usuarios 
	INTO 
		@id_usuario, 
		@id_processo
	
	WHILE @@FETCH_STATUS = 0
	
		BEGIN

			SELECT 
				@ultima_data_processsada = max(data) 
			FROM 
				infracao_processo (nolock)
			WHERE	id_usuario = @id_usuario 
				AND id_processo = @id_processo
		
			IF (@ultima_data_processsada < (getDate() - @dia_inativo))

				BEGIN
					exec spu_ajusta_janela @id_usuario, @id_processo
				END
	
			FETCH NEXT FROM cur_usuarios 
			INTO 
				@id_usuario, 
				@id_processo

		END

	CLOSE cur_usuarios
	DEALLOCATE cur_usuarios

END

CREATE PROCEDURE [dbo].[spu_ppv_sis_usuario_token](@id_usuario INT, @endereco_ip VARCHAR(17), @ret UNIQUEIDENTIFIER OUTPUT) AS

BEGIN TRY
BEGIN TRAN 

-- DECLARE @id_usuario INT 

--IF EXISTS(SELECT 1 FROM sis_usuario_token (NOLOCK) WHERE id_usuario = @id_usuario AND data_saida IS NULL)
--UPDATE sis_usuario_token WITH(ROWLOCK) SET data_saida = GETDATE() WHERE id_usuario = @id_usuario AND data_saida IS NULL

INSERT INTO sis_usuario_token (id_usuario,endereco_ip) VALUES (@id_usuario,@endereco_ip)

SELECT @ret = token FROM sis_usuario_token (NOLOCK) WHERE id_token = SCOPE_IDENTITY()

COMMIT

END TRY
BEGIN CATCH

IF @@trancount > 0
	ROLLBACK

SET @ret = NULL

END CATCH

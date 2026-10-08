CREATE PROCEDURE [dbo].[spu_ppv_sis_usuario_token_valida](@token UNIQUEIDENTIFIER, @endereco_ip VARCHAR(17)) AS

DECLARE @ret INTEGER = 0

UPDATE sis_usuario_token WITH(ROWLOCK) SET data_verificado = GETDATE(), endereco_ip = @endereco_ip WHERE token = @token 

SELECT @ret = id_usuario FROM sis_usuario_token (NOLOCK) WHERE token = @token AND data_saida IS NULL

RETURN @ret

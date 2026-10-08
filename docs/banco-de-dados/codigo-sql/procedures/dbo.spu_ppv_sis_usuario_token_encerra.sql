CREATE PROCEDURE [dbo].[spu_ppv_sis_usuario_token_encerra](@token UNIQUEIDENTIFIER) AS

UPDATE sis_usuario_token WITH(ROWLOCK) SET data_verificado = GETDATE(), data_saida = GETDATE() WHERE token = @token AND data_saida IS NULL

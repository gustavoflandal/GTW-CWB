CREATE FUNCTION [dbo].[fcn_getNomeContrato] ()
RETURNS nvarchar(6)
AS
BEGIN

	DECLARE @nomeContrato NVARCHAR(30)

	SET @nomeContrato = (SELECT RTRIM(valor) 
							FROM chave_valor (nolock) 
							WHERE chave = 'nome_contrato')

	RETURN @nomeContrato

END




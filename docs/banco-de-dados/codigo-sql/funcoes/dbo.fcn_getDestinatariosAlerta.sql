CREATE FUNCTION [dbo].[fcn_getDestinatariosAlerta]()
RETURNS NVARCHAR(MAX)
AS
BEGIN

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = 'manutencao-sp@consilux.com.br sistemas@consilux.com.br'
	
	RETURN @emails

END




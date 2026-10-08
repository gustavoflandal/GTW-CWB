CREATE FUNCTION [muralha].[fcn_ObterRemetenteEmailmuralha]()
RETURNS NVARCHAR(50)
AS
BEGIN

	DECLARE @remetente NVARCHAR(50) = 'sistemas@consilux.com.br'
	RETURN RTRIM(@remetente)

END

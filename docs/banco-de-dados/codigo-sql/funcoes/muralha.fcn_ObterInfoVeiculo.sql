
CREATE   FUNCTION [muralha].[fcn_ObterInfoVeiculo](@placa VARCHAR(7))
RETURNS TABLE
AS
RETURN
(
	--DECLARE @placa VARCHAR(7) = 'HAR5B39'
	SELECT TOP 1
                placa,
                LTRIM(RTRIM(marca_cet)) AS marca,
                LTRIM(RTRIM(marca)) AS modelo,
                LTRIM(RTRIM(cor)) AS cor,
                ano AS anoFabricacao
	FROM dbo.cadastro_veiculo
	WHERE  placa = @placa
)
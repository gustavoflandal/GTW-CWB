CREATE FUNCTION [muralha].[fcn_LocalidadeContrato]()
RETURNS TABLE
AS
	RETURN
	(
		SELECT * FROM cad_localidade WHERE nome LIKE 'IPATINGA' AND uf = 'MG'
	)

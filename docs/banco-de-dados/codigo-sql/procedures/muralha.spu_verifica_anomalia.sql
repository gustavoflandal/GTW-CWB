CREATE PROCEDURE [muralha].[spu_verifica_anomalia]
AS
BEGIN
	
	EXEC muralha.spu_verifica_anomalia_fluxo
	EXEC muralha.spu_verifica_anomalia_infracao
	EXEC muralha.spu_verifica_anomalia_irregularidades

END

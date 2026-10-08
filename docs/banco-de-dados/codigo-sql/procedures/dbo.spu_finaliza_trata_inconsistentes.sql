CREATE PROCEDURE [dbo].[spu_finaliza_trata_inconsistentes] 
AS

	EXEC spu_finaliza_trata_data_futura
	EXEC spu_finaliza_trata_registro_duplicado
	EXEC spu_finaliza_trata_velocidade_media



CREATE PROCEDURE [dbo].[spu_finaliza_cav] 
AS
EXEC spu_finaliza_remessas_validadas
EXEC spu_finaliza_importacao_movimento_lote
EXEC spu_finaliza_movimento_rejeitado 

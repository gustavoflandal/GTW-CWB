
--sp_helptext spu_finaliza_importacao_sequencia_local_novo 

CREATE PROCEDURE [dbo].[spu_finaliza]  
AS  
BEGIN

EXEC spu_finaliza_importacao_sequencia_local_novo
--GO
EXEC spu_finaliza_importacao_sequencia_local
--GO
EXEC spu_finaliza_importacao_imagens
--GO

UPDATE infracao SET id_processo = 1 WHERE id_processo IS NULL 
--GO

UPDATE infracao SET id_inconsistencia = 0 WHERE id_inconsistencia IS NULL
--GO
EXEC spu_finaliza_importacao_estatisticas
--GO
EXEC spu_atualizar_veiculo_sumarizado_dia_atual
--GO

EXEC spu_atualizar_veiculo_sumarizado_relatorio_dia_atual
--GO

EXEC spu_atualizar_veiculo_sumarizado_faixa_velocidade_dia_atual
--GO

END

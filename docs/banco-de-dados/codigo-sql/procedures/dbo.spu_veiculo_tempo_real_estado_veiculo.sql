CREATE PROCEDURE [dbo].[spu_veiculo_tempo_real_estado_veiculo]     
@estado_veiculo tinyint,@id uniqueidentifier    
AS     
UPDATE       muralha.veiculo_tempo_real  WITH(ROWLOCK)    
SET          estado_veiculo = @estado_veiculo  WHERE        (id = @id)  

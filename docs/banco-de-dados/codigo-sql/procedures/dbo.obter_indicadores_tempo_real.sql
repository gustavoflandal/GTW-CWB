



CREATE PROCEDURE [dbo].[obter_indicadores_tempo_real] @id BIGINT OUT    
AS    
    
SELECT @id = id FROM indicadores_estatisticas_tempo_real    
    

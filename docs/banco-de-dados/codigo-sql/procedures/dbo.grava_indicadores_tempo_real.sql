  
  
    
CREATE PROCEDURE [dbo].[grava_indicadores_tempo_real] @recebido INT, @enviado INT, @id INT    
AS     
    
UPDATE indicadores_estatisticas_tempo_real SET recebido = @recebido, enviado = @enviado, id = @id    
    


CREATE PROCEDURE [dbo].[spu_estatistica_tempo_real]       
-- DECLARE     
@id_local SMALLINT,      
@id_pista TINYINT,      
@placa CHAR(7),      
@data DATETIME,      
@velocidade SMALLINT  
,@classificacao CHAR(1)      
AS       
      
--INSERT INTO estatistica_tempo_real WITH(ROWLOCK) VALUES       
--(      
--@id_local ,       
--@id_pista,   
--@placa,  
--@data,       
--@velocidade        
----,@classificacao    
--); 

CREATE FUNCTION [muralha].[fn_RegistroFatoRequerEPossuiBO]     
(    
    @IdRegistroFato INT  
)    

RETURNS @RETORNO TABLE     
(  
    requer_e_possui VARCHAR(10)
)    
AS    
BEGIN  
   
	insert Into @RETORNO
	  SELECT TOP 1
		 CASE
            WHEN rfn.requer_bo = 1 AND rf.tem_boletim = 0 THEN 'NAO'
            ELSE 'SIM'
        END AS requer_e_possui
		FROM muralha.registro_fato rf
		JOIN muralha.registro_fato_tipo rft on rf.id_tipo = rft.id 
		JOIN muralha.registro_fato_natureza rfn on rfn.id_registro_tipo = rft.id
	WHERE rf.id = @IdRegistroFato
RETURN
END 
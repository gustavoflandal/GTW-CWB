CREATE FUNCTION [muralha].[fn_ObterSom]     
(    
    @IdAlerta UNIQUEIDENTIFIER  
)    
RETURNS @RETORNO TABLE     
(  
    som VARCHAR(130)  
)    
AS    
BEGIN   
    DECLARE @semelhanca INT,
            @supervisionado INT,
            @idTipoAlerta UNIQUEIDENTIFIER

    -- Obter os valores iniciais
    SELECT TOP 1
        @semelhanca = a.com_semelhanca, 
        @supervisionado = cadv.supervisionado, 
        @idTipoAlerta = a.id_tipo_alerta_ocorrencia
    FROM muralha.alerta a
    JOIN muralha.cad_veiculo_monitorado cadv
        ON a.id_cad_veiculo_monitorado = cadv.id
    WHERE a.id = @IdAlerta
insert Into @RETORNO
  SELECT TOP 1 RESULT.SOM
  FROM
  (
	   SELECT		
			cat2.prioridade,
			CASE 
				WHEN @supervisionado = 1 and cat2.tipo = 'MONITORAMENTO SUPERVISIONADO' THEN ca2.som 
				WHEN @supervisionado = 0 and cat2.tipo = 'MONITORAMENTO SIMPLES' THEN ca2.som 
				WHEN @semelhanca = 0 and cat2.tipo = 'EXATIDÃO PLACA' THEN ca2.som 
				WHEN @semelhanca = 1 and cat2.tipo = 'SEMELHANÇA PLACA' THEN ca2.som 
				WHEN cat2.id_tipo_alerta IS NOT NULL THEN ca2.som 
			END SOM

	   FROM muralha.config_alarme ca2 
	   JOIN muralha.config_alarme_tipo cat2
			ON ca2.id_tipo = cat2.id
	  WHERE
		cat2.habilitado = 1
		AND (cat2.id_tipo_alerta = @idTipoAlerta OR cat2.id_tipo_alerta IS NULL)

 ) AS RESULT
 WHERE RESULT.SOM IS NOT NULL
 ORDER BY RESULT.prioridade 
    RETURN   
END  
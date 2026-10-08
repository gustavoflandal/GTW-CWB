CREATE VIEW [dbo].[pmesp_movimento] AS                
                        
SELECT id_movimento ,id_evento_conexao ,id_equipamento ,placa ,data_movimento ,data_recebido ,data_registrado ,data_transmitido                
FROM pmesp_movimento_2017 (NOLOCK) 

UNION ALL 

SELECT id_movimento ,id_evento_conexao ,id_equipamento ,placa ,data_movimento ,data_recebido ,data_registrado ,data_transmitido                
FROM pmesp_movimento_2016 (NOLOCK) 


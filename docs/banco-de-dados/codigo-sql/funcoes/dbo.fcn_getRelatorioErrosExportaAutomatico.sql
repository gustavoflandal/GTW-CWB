CREATE FUNCTION [dbo].[fcn_getRelatorioErrosExportaAutomatico](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 
SELECT CAST(gra.data_solicitacao AS DATE) data_solicitacao, r.id_remessa, r.tipo, r.codigo_externo, gral.mensagem FROM gera_remessa_automatico gra (NOLOCK) 
JOIN gera_remessa_automatico_log gral (NOLOCK) ON gra.id_remessa_automatico = gral.id_remessa_automatico
LEFT JOIN remessa r (NOLOCK) ON gral.id_remessa = r.id_remessa
WHERE 
(gra.flag_geracao > 1 OR gra.flag_exportacao > 1) AND gral.id_status > 1
AND   r.data_exportacao IS NULL 
)


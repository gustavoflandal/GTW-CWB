
CREATE VIEW [dbo].[painel_ultimo_arquivo] AS  

SELECT id_local, data_arquivo AS [data_ultimo_arquivo] FROM arquivos_importados (NOLOCK)
WHERE id_arquivo IN (
SELECT   
 MAX(ai.id_arquivo)  
FROM arquivos_importados ai (nolock)  
WHERE ai.data_arquivo < GETDATE()   
GROUP BY ai.id_local
)

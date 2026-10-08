
CREATE PROCEDURE [dbo].[spu_status_importacao] AS 
SELECT
COUNT(*) AS veiculos,
SUM(CASE WHEN ii.id_veiculo_unic IS NOT NULL THEN 1 ELSE 0 END) AS imagens,
MIN(ai.data_arquivo) data_arquivo
FROM veiculo_importacao vi (NOLOCK)
JOIN arquivos_importados ai (NOLOCK) ON vi.nome_arquivo = ai.nome_arquivo
LEFT JOIN imagem_importacao ii (NOLOCK) ON vi.id_veiculo_unic = ii.id_veiculo_unic AND ii.indice_imagem = 0
WHERE vi.importar = 1

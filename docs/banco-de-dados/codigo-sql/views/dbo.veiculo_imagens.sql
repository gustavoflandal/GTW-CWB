
CREATE VIEW [dbo].[veiculo_imagens]
AS
SELECT     
	v.id_veiculo, i.id_infracao, ifo.id_imagem, ifo.id_tipo_imagem
FROM veiculo AS v (nolock) 
	LEFT JOIN dbo.infracao AS i (nolock) 
		ON i.id_veiculo = v.id_veiculo 
	INNER JOIN dbo.veiculo_imagem AS vi (nolock) 
		ON vi.id_veiculo = v.id_veiculo
	INNER JOIN dbo.imagem_info AS ifo (nolock) 
		ON ifo.id_imagem = vi.id_imagem



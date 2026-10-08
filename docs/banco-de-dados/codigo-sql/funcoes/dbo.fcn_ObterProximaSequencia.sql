
CREATE FUNCTION [dbo].[fcn_ObterProximaSequencia] 
(	
	@id_veiculo INT
)
RETURNS TABLE 
AS
RETURN 
(
	SELECT id_veiculo, MAX(indice_imagem) + 1 AS indice_imagem 
		FROM veiculo_imagem vi (nolock) 
			JOIN imagem i (nolock) 
				ON vi.id_imagem = i.id_imagem
	WHERE id_veiculo = @id_veiculo
	GROUP BY id_veiculo 
)



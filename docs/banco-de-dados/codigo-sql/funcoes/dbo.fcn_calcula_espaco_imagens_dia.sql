CREATE FUNCTION [dbo].[fcn_calcula_espaco_imagens_dia] 
(
	@dia DATE
)
RETURNS BIGINT
AS

BEGIN

	DECLARE @espacoImagens BIGINT
	
	SET @espacoImagens = (	SELECT 100000
							--	SUM(CAST(DATALENGTH(i.imagem) AS BIGINT))
							--FROM veiculo v (nolock)
							--	INNER JOIN local_vigente lvg (nolock)
							--		ON lvg.id_local = v.id_local
							--	INNER JOIN veiculo_imagem vi (nolock)
							--		ON vi.id_veiculo = v.id_veiculo
							--	INNER JOIN imagem i (nolock)
							--		ON i.id_imagem = vi.id_imagem
							--WHERE	CAST(v.data AS DATE) = @dia
							--	AND v.data >= lvg.data_inicio
						)
				
	RETURN @espacoImagens

END

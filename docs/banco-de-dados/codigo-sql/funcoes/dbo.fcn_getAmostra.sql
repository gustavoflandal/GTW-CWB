
CREATE FUNCTION [dbo].[fcn_getAmostra] 
(	
	@nivel VARCHAR(3), 
	@nqa FLOAT, 
	@tamanho_lote INT 
)
RETURNS TABLE 
AS
RETURN 
(-- DECLARE @tamanho_lote INT = 200
	SELECT 
		tamanho_amostra, 
		Ac, 
		Re  
	FROM amostragem am (NOLOCK) 
		JOIN amostragem_nivel an (nolock)
			ON am.id_nivel = an.id_nivel 
		JOIN amostragem_tamanho at (nolock)
			ON at.codigo = am.codigo 
	WHERE	an.nivel = (select valor from chave_valor where chave = 'nivel_inspecao') 
		AND at.nqa = (select valor from chave_valor where chave = 'nqa')
		AND @tamanho_lote BETWEEN am.tamanho_inicial AND am.tamanho_final 
)




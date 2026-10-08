
CREATE FUNCTION [dbo].[fcn_Locais_Vigentes_Na_Data]
  ( @data datetime )
RETURNS TABLE
AS
RETURN
(
	SELECT
		lcl.id_local,
		MAX(lcl.id_configuracao_equipamento) AS id_configuracao_equipamento,
		cecv.id_pista AS pista,
		cecv.num_lacos
		FROM [local] lcl (nolock)
			INNER JOIN configuracao_equipamento ce (nolock)
				ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
			INNER JOIN configuracao_equipamento_captura_veiculo cecv (nolock)
				ON cecv.id_configuracao_equipamento = lcl.id_configuracao_equipamento
		WHERE	ce.ativo = 1
			AND @data >= ce.data_inicio
			AND (ce.data_fim IS NULL OR @data <= ce.data_fim)
		GROUP BY 
			lcl.id_local,
			cecv.id_pista,
			cecv.num_lacos
)





CREATE FUNCTION [dbo].[fcn_getRelatorioMediaMaxCoeficInf](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		SELECT TOP 100 PERCENT
			   pc.numeroSerie AS [Número Série]
			  ,pc.idPista AS [Id. Pista]
			  ,pc.codigoFaixa AS [Código Faixa]
			  ,pc.nomeFaixa AS [Nome Faixa]
			  ,pc.infracoesMedia AS [Média Infrações]
			  ,pc.infracoesMaximo AS [Máximo Infrações]
			  ,pc.infracoesCoeficiente AS [Coeficiente Infrações]
		FROM   painel_contrato pc (NOLOCK)
		WHERE  CAST(pc.dataGeracao AS DATE) BETWEEN CAST(@dataInicio AS DATE) AND CAST(@dataFim AS DATE)
		ORDER BY
			   pc.numeroSerie
			  ,pc.idPista
	)

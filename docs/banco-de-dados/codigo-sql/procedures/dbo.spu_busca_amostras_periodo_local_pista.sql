CREATE PROCEDURE [dbo].[spu_busca_amostras_periodo_local_pista]
	@dataInicio DATE,
	@dataFinal DATE,
	@idLocal INT,
	@idPista INT,
	@metrologica BIT
AS
	SELECT 
		* 
	FROM 
		fcn_lista_amostras_periodo_local_pista(@dataInicio, @dataFinal, @idLocal, @idPista, @metrologica)
	ORDER BY 
		data




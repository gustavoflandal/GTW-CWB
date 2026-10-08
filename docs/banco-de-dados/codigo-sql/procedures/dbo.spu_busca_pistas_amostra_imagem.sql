
CREATE PROCEDURE [dbo].[spu_busca_pistas_amostra_imagem]
	@dataInicio DATE,
	@dataFim DATE
AS

BEGIN

	SELECT 
		id_local, 
		cod_pista, 
		cod_pista_alternativo, 
		RTRIM(nome_pista) AS nome_pista, 
		id_pista, 
		serie_equipamento, 
		cod_pista_prodam, 
		metrologica, 
		data_inicio 
	 FROM 
		fcn_ListaPistasAmostra (@dataInicio, @dataFim)
	 ORDER BY 
		id_local ASC, id_pista ASC
	
END




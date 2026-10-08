CREATE VIEW [muralha].[v_enquadramentos_dashboard]
AS
	SELECT eri.id_enquadramento,
		   eri.descricao_apait AS descricao
	--SELECT *
	FROM   dbo.enquadramento_regra_infracao eri
	WHERE  eri.id_enquadramento IN (60503, 56732, 74550, 74630, 74710)

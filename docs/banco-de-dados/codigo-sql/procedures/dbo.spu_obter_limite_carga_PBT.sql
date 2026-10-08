CREATE PROCEDURE [dbo].[spu_obter_limite_carga_PBT] @id_classificacao CHAR(3), @comprimento INT
AS

	--DECLARE @id_classificacao CHAR(3) = '3D4'--, @comprimento FLOAT = 17.50
	
	SELECT TOP 1
		   qfv.id_classificacao,
		   qfv.codigo,
		   qfv.numero_grupos,
		   qfv.numero_eixos,
		   qfv.pbt * 1000 AS pbt,
		   qfv.pbt_tolerancia * 1000 AS pbt_tolerancia,
		   qfv.comprimento_ini,
		   qfv.comprimento_fim,
		   qfv.descricao
	FROM   v_ppv_qfv qfv
	WHERE  qfv.id_classificacao = @id_classificacao
		   AND (
					(qfv.comprimento_ini IS NULL AND qfv.comprimento_fim IS NULL)
					OR
					(qfv.comprimento_fim = (SELECT MAX(aux.comprimento_fim) FROM v_ppv_qfv aux WHERE aux.id_classificacao = @id_classificacao))
		   )
		   --AND (
					--(qfv.comprimento_ini IS NULL AND qfv.comprimento_fim IS NULL)
					--OR
					--(@comprimento BETWEEN qfv.comprimento_ini AND qfv.comprimento_fim)
		   --)
	ORDER BY
		   qfv.pbt

/*
	SELECT *
	FROM   v_ppv_qfv
	WHERE  id_classificacao = '3D4'
	ORDER BY
		   comprimento DESC

	SELECT *
	FROM   ppv_qfv_distancias
*/

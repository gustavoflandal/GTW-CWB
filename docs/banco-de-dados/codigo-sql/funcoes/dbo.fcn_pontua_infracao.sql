

CREATE FUNCTION [dbo].[fcn_pontua_infracao]
(
		@id_infracao INT
)
RETURNS SMALLINT
AS
BEGIN

	DECLARE @ponto INT = null

	SELECT @ponto = (	CASE 
							WHEN (ia.id_processo IN (SELECT id_processo 
														FROM processo (nolock) 
														WHERE	id_processo_proximo IS NULL 
															AND ativo = 1 
															AND recebe_consistentes_inconsistentes = 1)
								)
								THEN 300 
							ELSE 
								0 
						END +
		
						CASE 
							WHEN (EXISTS (	SELECT id_infracao 
												FROM infracao_processo_concluido (nolock)
												WHERE	id_inconsistencia = 0 
													AND id_processo = 22 
													AND id_infracao = ia.id_infracao)
								)
								THEN 10 ELSE 0 END +
		
						CASE 
							WHEN (EXISTS (	SELECT id_infracao 
												FROM infracao_processo_concluido (nolock) 
												WHERE	id_inconsistencia = 0 
													AND id_processo = 3 
													AND id_infracao = ia.id_infracao)
								)
								THEN 200 
							ELSE 
								0 
						END +
		
						CASE 
							WHEN (EXISTS (	SELECT id_infracao 
												FROM infracao_processo_concluido (nolock) 
												WHERE	id_inconsistencia = 0 
													AND id_processo = 21 
													AND id_infracao = ia.id_infracao)
								)
								THEN 10 
							ELSE 
								0 
						END +
		
						CASE 
							WHEN (EXISTS (	SELECT id_infracao 
												FROM infracao_processo_concluido (nolock) 
												WHERE	id_inconsistencia = 0 
													AND id_processo = 2 
													AND id_infracao = ia.id_infracao)
								)
								THEN 150 
							ELSE 
								0 
						END +
		
						CASE 
							WHEN (EXISTS (	SELECT id_infracao 
												FROM infracao_processo_concluido (nolock) 
												WHERE	id_inconsistencia = 0 
													AND id_processo = 1 
													AND id_infracao = ia.id_infracao)
								)
								THEN 100 
							ELSE 
								0 
						END +
		
						CASE 
							WHEN (EXISTS (	SELECT id_infracao 
												FROM infracao_processo_concluido (nolock) 
												WHERE	id_inconsistencia = 0 
													AND id_processo = 20 
													AND id_infracao = ia.id_infracao)
								)
								THEN 50 
							ELSE 
								0 
						END +

	/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------*/		

						CASE 
							WHEN ia.id_inconsistencia IN (4,9,13,14,3) 
								THEN 5 
							ELSE 
								0 
						END +

						CASE 
							WHEN (ia.id_inconsistencia = 0) 
								THEN 10 
							ELSE 
								0 
						END +
		
						CASE 
							WHEN (ia.placa_digitada IS NOT NULL) 
								THEN 50
							ELSE 
								0 
						END +
	   
						CASE 
							WHEN (ia.placa_ocr = ia.placa_digitada) 
								THEN 100
							ELSE 
								0 
						END +	     

						CASE 
							WHEN (DATEPART(hh, ia.data) BETWEEN 8 AND 16) 
								THEN 40
							ELSE 
								0 
						END + 	     
     
						CASE 
							WHEN (ia.id_classe = 'P') 
								THEN 30
							ELSE 
								0 
						END +

						CASE 
							WHEN (ia.id_enquadramento > 1) 
								THEN 10
							ELSE 
								0 
						END +
	   		   
						CASE 
							WHEN (ia.id_enquadramento = 1) 
								THEN 1
							ELSE 
								0 
						END +
	   
						CASE 
							WHEN (ia.id_classe = ' ') 
								THEN -20
							ELSE 
								0 
						END + 
	   
						CASE 
							WHEN (ia.id_classe = 'M') 
								THEN -60
							ELSE 
								0 
						END +
	   		   
						CASE 
							WHEN (ia.id_enquadramento = 57461) 
								THEN -400
							ELSE 
								0 
						END +
	   
						CASE 
							WHEN (ia.imagem_captura_ruim = 1) 
								THEN -500
							ELSE 
								0 
						END
   					)
	FROM
		infracao_amostra ia (nolock)
	WHERE 
		id_infracao = @id_infracao

	RETURN @ponto

END




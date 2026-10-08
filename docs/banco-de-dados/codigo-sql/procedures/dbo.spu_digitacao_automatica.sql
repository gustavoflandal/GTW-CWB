CREATE PROCEDURE [dbo].[spu_digitacao_automatica] 
	@data_ini datetime, 
	@data_fim datetime
AS 

    DECLARE @id_infracao INT = NULL
    DECLARE @id_usuario INT = NULL
    DECLARE @id_processo INT = 2
    DECLARE @id_inconsistencia INT = 0
    DECLARE @placa CHAR(7) = NULL
    DECLARE @id_marca_processo INT = NULL
    DECLARE @id_especie_processo INT = NULL

    DECLARE cur_infracoes CURSOR LOCAL 
	READ_ONLY 
	FOR 
    SELECT top 1000 
		i.id_infracao, 
		(SELECT TOP 1 
			id_usuario 
		FROM 
			infracao_processo 
		WHERE	id_infracao = i.id_infracao 
			AND id_processo=1),
		coalesce(cv.placa, cep.placa), cmp.id_marca_cet, cep.id_especie
	FROM infracao i (nolock)
		INNER JOIN veiculo v (nolock)
			ON v.id_veiculo = i.id_veiculo
		LEFT JOIN cad_veiculo cv (nolock) 
			ON cv.placa = v.placa
		LEFT JOIN cad_marca_cet_processo cmp (nolock)
			ON cmp.placa = v.placa
		LEFT JOIN cad_especie_processo cep (nolock)
			ON cmp.placa = cep.placa
	WHERE	id_processo = 2
		AND coalesce(cv.placa, cep.placa) IS NOT NULL
		AND i.id_local NOT IN (4007, 4012,4024,4009,4011,4088,4008)
		AND NOT (i.id_local = 4087 AND i.pista = 2)
		AND NOT (i.id_local = 4015 AND i.pista = 1)
		AND NOT (i.id_local = 4076 AND i.pista IN (2,3) AND i.id_enquadramento = 57462)
		AND datepart(hour, i.data) BETWEEN 7 AND 19
		AND i.id_usuario_atual IS NULL
		AND i.data BETWEEN @data_ini AND @data_fim
		AND i.id_enquadramento not in (74550,75630,74710)
	ORDER BY 
		i.data

	OPEN cur_infracoes

	FETCH NEXT FROM cur_infracoes
	INTO 
		@id_infracao, 
		@id_usuario, 
		@placa, 
		@id_marca_processo, 
		@id_especie_processo
	
	WHILE @@FETCH_STATUS = 0

		BEGIN

			EXEC [spu_processa_infracao_direto_digitacao] @id_infracao, @id_usuario, @id_processo, @id_inconsistencia, @placa,	@id_marca_processo,	@id_especie_processo, null
			EXEC spu_status_infracao @id_infracao
				
			FETCH NEXT FROM cur_infracoes
			INTO 
				@id_infracao, 
				@id_usuario, 
				@placa, 
				@id_marca_processo, 
				@id_especie_processo

		END

	CLOSE cur_infracoes
	DEALLOCATE cur_infracoes




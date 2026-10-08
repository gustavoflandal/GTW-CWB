





CREATE    PROCEDURE [dbo].[spu_valida_processo_digitacao]
	@id_infracao_processo int
AS

	DECLARE @placa CHAR(7) = NULL
	DECLARE @placa_isento CHAR(7) = NULL
	DECLARE @id_infracao int = NULL
	DECLARE @id_processo int = NULL
	DECLARE @id_inconsistencia int = NULL
	DECLARE @id_marca_cet int = NULL
	DECLARE @id_especie int = NULL
	DECLARE @placa_cadastro CHAR(7) = NULL
	DECLARE @id_especie_cadastro int = NULL
	DECLARE @id_enquadramento int = NULL
	DECLARE @data DATETIME = NULL
	DECLARE @data_ini DATETIME = NULL
	DECLARE @data_fim DATETIME = NULL
	DECLARE @id_modalidade CHAR(2) = NULL
	DECLARE @descricao CHAR(80) = ''
	DECLARE @msg CHAR(200) = NULL

	SELECT @placa = id.placa,
		   @id_infracao = ip.id_infracao,
		   @id_processo = ip.id_processo,
		   @id_inconsistencia = ip.id_inconsistencia,
		   @id_marca_cet = id.id_marca_cet,
		   @id_especie = id.id_especie
	FROM infracao_processo ip 
		LEFT JOIN infracao_processo_digitacao id (nolock)
			ON id.id_infracao_processo = ip.id_infracao_processo (nolock)
	WHERE 
		ip.id_infracao_processo = @id_infracao_processo
	
	-- RAISERROR (<mens>, <severidade>, 1)...onde a severidade até 10 nao vai parar a query, de 11 a 16 vai parar a query.
	-- Primeiro, testando se a imagem é consistente ou inconsistente...

	IF (@id_inconsistencia IS NULL OR @id_inconsistencia = 0)

		BEGIN

			-- Verificando se foi feita a digitação:
			IF (NOT @placa IS NULL)

				BEGIN

					IF (EXISTS (SELECT 
									i.id_infracao 
								FROM infracao i (nolock)
									JOIN local l (nolock) 
										ON	l.id_local = i.id_local 
										AND l.sequencia_local = i.sequencia_local
									JOIN veiculo v (nolock) 
										ON v.id_veiculo = i.id_veiculo
									JOIN configuracao_equipamento_afericao a (nolock) 
										ON	a.id_configuracao_equipamento = l.id_configuracao_equipamento 
										AND a.id_pista = v.pista
								WHERE	i.data > a.data_validade 
									AND	i.id_infracao = @id_infracao 
									AND i.id_enquadramento in (74550, 74630, 74710))
						)

						BEGIN

							RAISERROR('DATA DE AFERIÇÃO VENCIDA!', 11, 1)

						END
		
	
					SELECT @placa_cadastro = cv.placa,
						   @id_especie_cadastro = id_especie 
					FROM 
						cad_veiculo cv (nolock) 
					WHERE
						cv.placa = @placa
			
					IF ((@placa_cadastro IS NULL AND NOT @id_marca_cet > 0) 
						OR (@id_especie_cadastro IS NULL AND NOT @id_especie > 0))

						BEGIN

							RAISERROR('CADASTRO INCOMPLETO!', 11, 1)

						END
			
					/*
					IF (EXISTS (SELECT distinct 
									p.id_processo 
								FROM processo p (nolock) --VERIFICANDO SE ESTÁ NO ULTIMO PROCESSO ANTES DA REMESSA VALIDAS...(VALIDAÇÃO).
									JOIN processo p_prox (nolock) 
										ON	(p.id_processo = p_prox.id_processo_anterior OR p.id_processo_proximo = p_prox.id_processo)
										AND p_prox.id_processo_proximo IS NULL AND p_prox.recebe_consistentes_inconsistentes = 1
										AND p.id_processo = @id_processo)
						)
					BEGIN
					*/

					SELECT 
						@id_enquadramento = id_enquadramento, 
						@data = data 
					FROM 
						infracao (nolock) --PEGANDO DADOS NECESSÁRIO PARA FILTRAR O CADASTRO DE ISENTOS.
					WHERE 
						id_infracao = @id_infracao
			
					SELECT 
						@placa_isento = placa, 
						@id_modalidade = modalidade, 
						@data_ini = data_inicio, 
						@data_fim = data_fim 
					FROM 
						cad_isento ci (nolock)    --Verificando se é isento...
					WHERE	id_enquadramento = @id_enquadramento 
						AND	placa = @placa 
						AND @data BETWEEN data_inicio AND data_fim 
						/*
						AND	((CAST(@data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) OR 
							(ci.horario_inicio > ci.horario_fim AND (CAST(@data AS time) > ci.horario_inicio OR CAST(@data AS time) < ci.horario_fim))) -- Caso o período extrapole às 00:00
						*/
					   
					IF (NOT @placa_isento IS NULL) --ISENTO?

						BEGIN

							SELECT 
								@descricao = RTRIM(descricao) --TENTANDO PEGAR O MOTIVO DA ISENCAO.
							FROM 
								cad_modalidade_isento (nolock)
							WHERE 
								id_modalidade = @id_modalidade
					
							IF (@descricao <> '') --TEM UM MOTIVO?

								BEGIN
							
									SET @descricao = 'MOTIVO: '+RTRIM(@descricao)+CHAR(10)
							
								END

							SET @msg =	'VEÍCULO ISENTO!'+CHAR(10)+RTRIM(@descricao)+
										'PERIODO: '+CONVERT(CHAR(10), @data_ini, 103)+' ATÉ '+ 
										CONVERT(CHAR(10), @data_fim, 103)+CHAR(10)
							   
							RAISERROR(@msg, 11, 1)

						END

					/*
					END
					*/
				END

	END

	


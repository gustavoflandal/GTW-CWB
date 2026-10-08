
/****** Object:  StoredProcedure [dbo].[spu_gera_remessa_urbs]    Script Date: 02/08/2012 10:24:19 ******/
CREATE PROCEDURE [dbo].[spu_gera_remessa_urbs]
	@id_processo_remessa int,
	@codigo_externo int,
	@data_inicio datetime,
	@data_fim datetime,
	@tipo VARCHAR(4),
	@data_remessa date,
	@residual bit
AS

DECLARE @id_infracao INT
DECLARE @id_remessa INT
DECLARE @serie CHAR(2)
DECLARE @auto INT = NULL
DECLARE @total_infracao INT
DECLARE @auto_inicial INT
DECLARE @serie_inicial CHAR(2)
DECLARE @auto_final INT
DECLARE @serie_final CHAR(2)
DECLARE @sigla_infracao_cliente CHAR(5) 

SET @total_infracao = 0           
SET @auto_inicial = NULL           
SET @auto_final = NULL    

SET @total_infracao = (	SELECT 
							COUNT(id_infracao) 
						FROM 
							infracao i (nolock)
						WHERE i.id_processo = @id_processo_remessa
							AND data BETWEEN @data_inicio AND @data_fim
							AND NOT EXISTS (SELECT 
												id_infracao 
											FROM 
												infracao_remessa (nolock)
											WHERE 
												id_infracao = i.id_infracao)
					   )

IF (@total_infracao = 0)

	BEGIN

		RAISERROR(N'Não existem infrações para gerar uma remessa deste tipo.',16, 1)
		RETURN

	END

SET NOCOUNT ON

BEGIN TRANSACTION

	INSERT INTO remessa (
		codigo_externo, 
		data, 
		data_confirmacao,
		data_inicial, 
		data_final, 
		id_processo,
		total_infracao, 
		auto_inicial, 
		serie_inicial,
		auto_final, 
		serie_final, 
		tipo)
	VALUES (
		@codigo_externo, 
		@data_remessa, 
		NULL,
		@data_inicio, 
		@data_fim, 
		@id_processo_remessa,
		NULL, 
		NULL, 
		NULL,
		NULL, 
		NULL, 
		@tipo)
           
	SET @id_remessa = @@IDENTITY           

	 -- Velocidade Barreira
	IF @tipo = 'CS5'

		BEGIN

			SET @serie = 'Y'
			SET @sigla_infracao_cliente = 'BEL'

		END

	-- Velocidade Radar
	IF	@tipo = 'CR1'

		BEGIN

			SET @serie = 'W'
			SET @sigla_infracao_cliente = 'RAD'

		END

	-- Avanco Sinal
	IF @tipo = 'CS1'

		BEGIN

			SET @serie = 'S'
			SET @sigla_infracao_cliente = 'FEL'	

		END

	-- Conversao Proibida
	IF @tipo = 'CS4'

		BEGIN

			SET @serie = 'K'
			SET @sigla_infracao_cliente = 'FEL'	

		END

	-- Para sobre faixa
	IF @tipo = 'CS2'

		BEGIN

			SET @serie = 'P'
			SET @sigla_infracao_cliente = 'FEL'	

		END

	-- Retorno Proibido
	IF @tipo = 'CS3'

		BEGIN

			SET @serie = 'R'  
			SET @sigla_infracao_cliente = 'FEL'	

		END

	-- ADICIONADO 2013-08-28
	IF @tipo = 'CS6'

		BEGIN

			SET @serie = 'Z'  
			SET @sigla_infracao_cliente = 'FEL'	

		END

	IF @tipo = 'CS7'

		BEGIN

			SET @serie = 'C'  
			SET @sigla_infracao_cliente = 'FEL'	

		END

	IF @tipo = 'CS8'

		BEGIN

			SET @serie = 'F'  
			SET @sigla_infracao_cliente = 'FEL'	

		END

	IF @tipo = 'CS9'

		BEGIN

			SET @serie = 'E'  
			SET @sigla_infracao_cliente = 'FEL'	

		END

	IF @tipo = 'CS0'

		BEGIN

			SET @serie = 'X'  
			SET @sigla_infracao_cliente = 'FEL'	

		END

	SELECT 
		@auto = MAX(ir.auto) 
	FROM infracao_remessa ir (nolock)
		JOIN remessa r (nolock)
			ON r.id_remessa = ir.id_remessa
	WHERE	ir.serie = @serie 
		AND r.tipo = @tipo

	IF (@auto IS NULL)

		BEGIN

			IF (@serie IS NOT NULL)

				BEGIN

					SET @auto = (	SELECT 
										valor 
									FROM 
										chave_valor (nolock)
									WHERE 
										chave = 'auto_inicial_serie_' + @serie)

				END

			ELSE

				BEGIN

					ROLLBACK
					RAISERROR('SERIE INVALIDA!',16, 1)
					RETURN

				END

		END

	SET @total_infracao = 0 -- Zera a variável

	DECLARE cur_infracoes CURSOR LOCAL FAST_FORWARD 
	FOR
	SELECT 
		id_infracao 
	FROM 
		infracao i (nolock)
	WHERE	i.id_processo = @id_processo_remessa 
		AND	data BETWEEN @data_inicio AND @data_fim
		AND (NOT @residual = 1 
			OR NOT EXISTS (	SELECT 
								id_infracao 
							FROM 
								infracao_remessa (nolock)
							WHERE 
								id_infracao = i.id_infracao)
			)
	ORDER BY data

	OPEN cur_infracoes

	FETCH FROM cur_infracoes 
	INTO 
		@id_infracao

	WHILE @@FETCH_STATUS = 0
	
		BEGIN

			--Verificando se a infração já está em uma remessa:
			IF EXISTS (	SELECT 
							id_infracao 
						FROM 
							infracao_remessa (nolock)
						WHERE 
							id_infracao = @id_infracao)

				BEGIN

					ROLLBACK

					CLOSE cur_infracoes
					DEALLOCATE cur_infracoes

					RAISERROR('ESTA REMESSA CONTÉM PELO MENOS UMA INFRAÇÃO QUE JÁ FAZ PARTE DE OUTRA REMESSA!', 11, 1)
					RETURN

				END

			SET @auto = @auto + 1
	
			IF (@auto_inicial IS NULL)

				BEGIN

					SET @auto_inicial = @auto
					SET @serie_inicial = @serie

				END
	
			INSERT INTO infracao_remessa  with (rowlock) (
				id_infracao, 
				id_remessa,
				auto,
				serie, 
				uf,
				sigla_infracao_cliente )
			VALUES (
				@id_infracao, 
				@id_remessa,
				@auto, 
				@serie, 
				'',		-- Isso aqui está certo, não passa a UF?
				@sigla_infracao_cliente)

			SET @total_infracao = @total_infracao + 1

			FETCH NEXT FROM cur_infracoes 
			INTO 
				@id_infracao

		END -- Fim do cursor

	CLOSE cur_infracoes
	DEALLOCATE cur_infracoes

	SET @auto_final = @auto
	SET @serie_final = @serie

	UPDATE remessa with (rowlock)
	SET	total_infracao = @total_infracao, 
		auto_inicial = @auto_inicial,
		serie_inicial = @serie_inicial, 
		auto_final = @auto_final,
		serie_final = @serie_inicial
	WHERE id_remessa = @id_remessa

COMMIT

RETURN @id_remessa





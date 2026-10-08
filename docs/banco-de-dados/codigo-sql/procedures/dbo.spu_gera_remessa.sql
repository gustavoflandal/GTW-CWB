CREATE PROCEDURE [dbo].[spu_gera_remessa]
	@id_enquadramento INT,
	@id_processo_remessa INT,
	@codigo_externo INT,
	@data_inicio DATETIME,
	@data_fim DATETIME,
	@tipo CHAR(2),
	@data_remessa DATE,
	@residual BIT,
	@infracoes INT,
	@id_usuario INT,
	@id_inconsistencia INT,
	@id_remessa_automatico INT
AS

DECLARE @id_infracao INT
DECLARE @id_remessa INT
DECLARE @serie CHAR(2)
DECLARE @auto INT
DECLARE @total_infracao INT
DECLARE @auto_inicial INT
DECLARE @serie_inicial CHAR(2)
DECLARE @auto_final INT
DECLARE @serie_final CHAR(2)
DECLARE @sequencia INT = 1

IF @id_inconsistencia IS NOT NULL
BEGIN
	IF @id_inconsistencia = 999999
	BEGIN
		SET @id_inconsistencia = NULL
	END
END

IF @id_remessa_automatico IS NOT NULL
BEGIN
	IF @id_remessa_automatico = 0
	BEGIN
		SET @id_remessa_automatico = NULL
	END
END

IF @infracoes > 0 

	BEGIN

		DECLARE cur_infracoes CURSOR 
		FOR
		SELECT TOP (@infracoes) 
			i.id_infracao 
		FROM 
			infracao i (nolock)
		WHERE	i.id_enquadramento = @id_enquadramento 
			AND i.id_processo = @id_processo_remessa 
			AND data BETWEEN @data_inicio AND @data_fim 
			AND (NOT @residual = 1 OR NOT EXISTS (	SELECT 
														id_infracao 
													FROM 
														infracao_remessa (nolock)
													WHERE 
														id_infracao = i.id_infracao))
			AND i.id_inconsistencia = CASE WHEN @id_inconsistencia IS NULL THEN i.id_inconsistencia ELSE @id_inconsistencia END
		ORDER BY data
	END

ELSE
	
	BEGIN

		DECLARE cur_infracoes CURSOR 
		FOR
		SELECT 
			i.id_infracao 
		FROM 
			infracao i (nolock)
		WHERE	i.id_enquadramento = @id_enquadramento 
			AND i.id_processo = @id_processo_remessa 
			AND data BETWEEN @data_inicio AND @data_fim 
			AND (NOT @residual = 1 OR NOT EXISTS (	SELECT 
														id_infracao 
													FROM 
														infracao_remessa (nolock)
													WHERE 
														id_infracao = i.id_infracao))
			AND i.id_inconsistencia = CASE WHEN @id_inconsistencia IS NULL THEN i.id_inconsistencia ELSE @id_inconsistencia END
		ORDER BY data
	END

SET @total_infracao = 0           
SET @auto_inicial = NULL           
SET @auto_final = NULL    

SET NOCOUNT ON

INSERT INTO remessa with (rowlock)
			(codigo_externo,
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
			tipo,
			id_usuario,
			id_enquadramento,
			id_inconsistencia,
			id_remessa_automatico
			)
		VALUES
			(@codigo_externo,
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
			@tipo,
			@id_usuario,
			@id_enquadramento,
			@id_inconsistencia,
			@id_remessa_automatico
			)
           
SET @id_remessa = @@IDENTITY           

SELECT 
	@serie = MAX(ir.serie) 
FROM infracao_remessa ir (nolock)
	INNER JOIN remessa r (nolock)
		ON r.id_remessa = ir.id_remessa
WHERE tipo = @tipo

SELECT 
	@auto = MAX(ir.auto) 
FROM infracao_remessa ir (nolock)
	INNER JOIN remessa r (nolock)
		ON r.id_remessa = ir.id_remessa
WHERE	ir.serie = @serie 
	AND r.tipo = @tipo

IF (@serie IS NULL)
	SET @serie = 'A1'

IF (@auto IS NULL)
	SET @auto = 0

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

			RAISERROR('ESTA REMESSA CONTÉM PELO MENOS UMA INFRAÇÃO QUE JÁ FAZ PARTE DE OUTRA REMESSA!', 11, 1)

		SET @auto = @auto + 1
	
		IF (@auto > 999999)

			BEGIN

				IF (SUBSTRING(@serie,2,1) = '9')
					SET @serie = CHAR(ASCII(SUBSTRING(@serie,1,1))+1)+'0'
				ELSE
					SET @serie = SUBSTRING(@serie,1,1) + CHAR(ASCII(SUBSTRING(@serie,2,1))+1)
				SET @auto = 1

			END
	
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
			sequencia) 
		VALUES (
			@id_infracao,
			@id_remessa,
			@auto,
			@serie,
			'',
			@sequencia)

		SET @sequencia = @sequencia + 1 

		SET @total_infracao = @total_infracao + 1

		FETCH NEXT FROM cur_infracoes 
		INTO 
			@id_infracao

	END

CLOSE cur_infracoes
DEALLOCATE cur_infracoes

SET @auto_final = @auto
SET @serie_final = @serie

UPDATE remessa with (rowlock)
SET total_infracao = @total_infracao,
	auto_inicial = @auto_inicial,
	serie_inicial = @serie_inicial,
	auto_final = @auto_final,
	serie_final = @serie_final
WHERE id_remessa = @id_remessa

RETURN @id_remessa

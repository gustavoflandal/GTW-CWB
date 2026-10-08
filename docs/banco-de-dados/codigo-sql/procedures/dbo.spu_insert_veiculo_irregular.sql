

CREATE PROCEDURE [dbo].[spu_insert_veiculo_irregular]
	@id_local int,
	@data datetime,
	@velocidade numeric(6,2),
	@comprimento numeric(6,2),
	@pista int,
	@placa char(10),
	@formato char(5),
	@id_veiculo_local int,
	@flag int,
	@id_veiculo_unic bigint,
	@id_classe char(1),
	@imagem image,
	@lote int = NULL,
	@data_afericao DateTime = NULL,
	@id_imagem_local INT = NULL,
	@id_imagem_importacao INT = NULL
AS

DECLARE @id_veiculo_monitorado BIGINT
DECLARE @id_imagem_monitorado INT

DECLARE @sequencia_local INT

SELECT 
	@sequencia_local = sequencia_local 
FROM 
	local_vigente (nolock)
WHERE 
	id_local = @id_local

BEGIN TRANSACTION

	EXEC sp_getapplock @Resource = '[spu_finaliza_importacao]', @LockMode = 'Exclusive'; -- não gerar conflito com a importação.	

	-- ****************************************************************************
	-- Importa as imagens (OS BLOBs)
	-- ****************************************************************************	
	INSERT INTO imagem_monitorado( id_tipo_imagem, formato, imagem )
	SELECT 
		id_tipo_imagem, @formato, 
		CASE 
			WHEN @imagem IS NOT NULL 
				THEN @imagem
			WHEN @id_imagem_importacao IS NOT NULL 
				THEN (	SELECT 
							imagem 
						FROM 
							imagem_importacao (nolock)
						WHERE 
							id_imagem = @id_imagem_importacao)
			ELSE
				NULL
		END
	FROM 
		tipo_imagem tim (nolock)
	WHERE 
		tim.nome = 'OBJ'AND tim.numero = 1

	SET @id_imagem_monitorado = @@identity

	INSERT INTO veiculo_monitorado with (rowlock) ( 
		id_veiculo_local, 
		data, 
		placa, 
		velocidade, 
		comprimento, 
		pista, 
		flag, 
		id_veiculo_unic, 
		id_classe, 
		id_local, 
		sequencia_local)
	VALUES ( 
		@id_veiculo_local, 
		@data, 
		@placa, 
		@velocidade, 
		@comprimento, 
		@pista,
		@flag, 
		@id_veiculo_unic, 
		@id_classe, 
		@id_local, 
		@sequencia_local)

	SET @id_veiculo_monitorado = @@identity

	INSERT INTO veiculo_monitorado_imagem with (rowlock)(
		id_imagem, 
		id_veiculo_monitorado, 
		id_imagem_local)
	VALUES( 
		@id_imagem_monitorado , 
		@id_veiculo_monitorado, 
		@id_imagem_local)

	IF (@@ERROR <> 0)

		BEGIN

			PRINT 'OCORREU UM ERRO: ' + LTRIM(STR(@@ERROR))

			ROLLBACK

			RETURN (@@ERROR)	

		END	

	IF (@@ERROR = 0)

		BEGIN

			-- Tudo OK: go,go,go...
			COMMIT

		END



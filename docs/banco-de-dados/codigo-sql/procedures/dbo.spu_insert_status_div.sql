
CREATE PROCEDURE [dbo].[spu_insert_status_div]
	@serie_equipamento INT,
	@status INT,
	@codigo_DIV INT,
	@comunicacao_ok bit ,
	@grupo_centena_ok bit ,
	@grupo_decena_ok bit ,
	@grupo_unidade_ok bit ,
	@grupo_vermelho_ok bit ,
	@grupo_amarelo_ok bit ,
	@grupo_verde_ok bit,
	@endereco_DIV INT
AS

DECLARE @sequencia_local INT
DECLARE @id_local INT

SET @sequencia_local = NULL

/* busca o id_local e o sequencia_local atual do equipamento */
SELECT 
	@sequencia_local = sequencia_local,
	@id_local = id_local
FROM 
	local_vigente (nolock)
WHERE 
	serie_equipamento = @serie_equipamento

IF ( @sequencia_local is not null )

	BEGIN

		INSERT INTO status_DIV (
			id_local, 
			sequencia_local, 
			data_atualizacao, 
			status, 
			codigo_DIV,
			comunicacao_ok,
			grupo_centena_ok, 
			grupo_decena_ok, 
			grupo_unidade_ok,
			grupo_vermelho_ok, 
			grupo_amarelo_ok, 
			grupo_verde_ok, 
			endereco_DIV)
		VALUES( 
			@id_local, 
			@sequencia_local, 
			GETDATE(), 
			@status, 
			@codigo_DIV,
			@comunicacao_ok, 
			@grupo_centena_ok, 
			@grupo_decena_ok, 
			@grupo_unidade_ok,
			@grupo_vermelho_ok, 
			@grupo_amarelo_ok, 
			@grupo_verde_ok,
			@endereco_DIV)

	END

ELSE

	RAISERROR  ('status_DIV Local não existente!', 16 , 1)



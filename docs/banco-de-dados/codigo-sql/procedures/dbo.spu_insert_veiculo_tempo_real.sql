
CREATE PROCEDURE [dbo].[spu_insert_veiculo_tempo_real]
	@id_veiculo_local int,
    @data datetime,
    @placa char(7),
    @velocidade decimal(6,1),
    @comprimento decimal(6,1),
    @pista tinyint,
    @flag int,
    @segundos decimal(6,3),
    @id_veiculo_unic bigint,
    @id_classe char(1),
    @id_local int,
    @ocupacao int
AS

	DECLARE	@sequencia_local tinyint

	SELECT 
		@sequencia_local = sequencia_local 
	FROM 
		local_vigente (nolock)
	WHERE 
		id_local = @id_local

	INSERT INTO veiculo_tempo_real with (rowlock) (
		id_veiculo_local,
		data,
		placa,
		velocidade,
		comprimento,
		pista,
		flag,
		segundos,
		id_veiculo_unic,
		id_classe,
		id_local,
		sequencia_local,
		ocupacao)
	 VALUES (
		@id_veiculo_local,
		@data,
		@placa,
		@velocidade,
		@comprimento,
		@pista,
		@flag,
		@segundos,
		@id_veiculo_unic,
		@id_classe,
		@id_local,
		@sequencia_local,
		@ocupacao)




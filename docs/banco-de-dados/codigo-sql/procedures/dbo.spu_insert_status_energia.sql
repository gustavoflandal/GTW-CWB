
CREATE  PROCEDURE [dbo].[spu_insert_status_energia]
	@serie_equipamento INT,
	@status INT
AS

DECLARE @sequencia_local INT
DECLARE @id_status_energia INT
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

SELECT 
	@id_status_energia = ISNULL( MAX( id_status_energia ) , 0) + 1 
FROM 
	status_energia (nolock)
WHERE id_local = @id_local

IF ( @sequencia_local is not null )

	BEGIN

		INSERT INTO status_energia with (rowlock)
			([id_local], [sequencia_local], [id_status_energia], [data_atualizacao], [status])
		VALUES
			( @id_local , @sequencia_local , @id_status_energia, GETDATE() , @status )

	END

ELSE

	RAISERROR  ('[status_energia] Local não existente!', 16 , 1)







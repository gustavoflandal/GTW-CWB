CREATE PROCEDURE [dbo].[spu_insert_status_conexao]
	@serie_equipamento INT,
	@status INT,
	@ip nchar(15)
	
AS

	DECLARE @sequencia_local INT
	DECLARE @id_local INT
	DECLARE @lastStatus INT

	SET @sequencia_local = NULL


	/**** busca o id_local e o sequencia_local atual do equipamento ****/

	SELECT 
		@sequencia_local = sequencia_local,
		@id_local = id_local
	FROM 
		local_vigente (nolock)
	WHERE 
		serie_equipamento = @serie_equipamento

	/*******************************************************************/

	SELECT TOP 1 
		@lastStatus = status 
	FROM 
		status_conexao (nolock)
	WHERE 
		id_local = @id_local 
	ORDER BY data_atualizacao DESC

	IF (@lastStatus = 1) OR ( @status <> 2 )

		BEGIN

			IF ( @sequencia_local is not null )

				BEGIN

					INSERT INTO status_conexao with (rowlock)
						(id_local, sequencia_local, data_atualizacao, status, ip)
					VALUES
						(@id_local , @sequencia_local , GETDATE() , @status , @ip )

				END

			ELSE

				RAISERROR  ('[status_conexao] Local não existente!' , 16 , 1)
	
		END



CREATE PROCEDURE [dbo].[spu_excluir_remessa]
	@id_remesssa int
AS

BEGIN TRY
		
	BEGIN TRANSACTION
	
		INSERT INTO remessa_excluida with (rowlock)
			(id_remessa,
			codigo_externo,
			data,
			data_confirmacao,
			id_processo,
			data_inicial,
			data_final,
			total_infracao,
			auto_inicial,
			serie_inicial,
			auto_final,
			serie_final,
			tipo)
		SELECT 
			id_remessa,
			codigo_externo,
			data,
			data_confirmacao,
			id_processo,
			data_inicial,
			data_final,
			total_infracao,
			auto_inicial,
			serie_inicial,
			auto_final,
			serie_final,
			tipo
		FROM 
			remessa (nolock)
		WHERE 
			id_remessa = @id_remesssa

		INSERT INTO infracao_remessa_excluida with (rowlock)
			(id_infracao,
			id_remessa,
			auto,
			serie,
			uf,
			data_confirmacao,
			erro1,
			erro2,
			erro3,
			sigla_infracao_cliente)
		SELECT 
			id_infracao,
			id_remessa,
			auto,
			serie,
			uf,
			data_confirmacao,
			erro1,
			erro2,
			erro3,
			sigla_infracao_cliente
		FROM infracao_remessa (nolock)
		WHERE id_remessa = @id_remesssa
		
	DELETE 
	FROM remessa with (rowlock)
	WHERE id_remessa=@id_remesssa
		
	COMMIT
		
END TRY

BEGIN CATCH

	IF (@@TRANCOUNT > 0)
		ROLLBACK

	EXEC spu_replica_erro
		
	RETURN 0
		
END CATCH




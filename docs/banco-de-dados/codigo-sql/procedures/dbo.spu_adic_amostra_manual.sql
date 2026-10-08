CREATE PROCEDURE [dbo].[spu_adic_amostra_manual] 
	@data DATE, 
	@id_local INT, 
	@id_pista INT, 
	@metrologica BIT, 
	@id_veiculo BIGINT,
	@aplicavel BIT,
	@id_usuario INT
AS 
	DECLARE @id_infracao INT = NULL
	
	SET NOCOUNT ON 
	
	--Pegando o id_infracao para pontuar...
	SELECT @id_infracao=id_infracao 
		FROM infracao (nolock)
		WHERE id_veiculo = @id_veiculo
	
	BEGIN TRY 
		 
		BEGIN TRANSACTION 
	
			DELETE 
				FROM amostra_imagem_manual with (rowlock)
				WHERE	data = @data AND id_local = @id_local
					AND id_pista = @id_pista AND metrologica = @metrologica

			DELETE 
				FROM infracao_rejeita_amostra with (rowlock)
				WHERE id_infracao IN	(SELECT id_infracao 
												FROM infracao inf (nolock)
	 												JOIN enquadramento enq(nolock)
	 													ON enq.id_enquadramento = inf.id_enquadramento
	 										WHERE	CAST(inf.data AS DATE) = @data
	 											AND inf.id_local = @id_local
	 											AND inf.pista = @id_pista
	 											AND enq.infracao_metrologia = @metrologica)


			--Se não é aplicável, colocamos na tabela de rejeitos.
			IF @aplicavel = 0
			BEGIN
				 INSERT INTO infracao_rejeita_amostra with (rowlock) (id_infracao)
					SELECT id_infracao 
						FROM infracao inf (nolock)
						 JOIN enquadramento enq (nolock)
							ON enq.id_enquadramento = inf.id_enquadramento
					WHERE	CAST(inf.data AS DATE) = @data
						AND inf.id_enquadramento > 1 --Não marca a imagem teste como ruim porquê a imagem teste é a única que pode ser utilizada para outra comprovação.
						AND inf.id_local = @id_local
						AND inf.pista = @id_pista
						AND enq.infracao_metrologia = @metrologica
			END
						
			INSERT INTO amostra_imagem_manual with (rowlock) 
				(data, id_local, id_pista, metrologica, id_veiculo, aplicavel, id_usuario, score_total)
				VALUES (@data,@id_local,@id_pista,@metrologica,@id_veiculo,@aplicavel,@id_usuario,dbo.fcn_pontua_infracao(@id_infracao))
		
		COMMIT 
		 
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
 
		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH




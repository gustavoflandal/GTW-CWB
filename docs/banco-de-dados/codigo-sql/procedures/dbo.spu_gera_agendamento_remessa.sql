CREATE PROCEDURE [dbo].[spu_gera_agendamento_remessa]
	@data_remessa DATETIME,
	@data_inicial DATETIME,
	@data_final DATETIME,
	@infracoes_por_lote INT,
	@id_processo_remessa INT,
	@id_usuario INT 
AS

	DECLARE @total_infracoes INT, @qtde_aprox_lotes INT, @id_remessa_automatico INT

	BEGIN TRY

		SET @total_infracoes = (
					SELECT COUNT(*) AS qtde
					FROM   infracao inf (NOLOCK)
						   LEFT JOIN infracao_remessa ir (NOLOCK)
								ON  ir.id_infracao = inf.id_infracao
					WHERE  inf.id_processo = @id_processo_remessa
						   AND CAST(inf.data AS DATE) BETWEEN CAST(@data_inicial AS DATE) AND CAST(@data_final AS DATE)
						   AND ir.id_infracao IS NULL
			)
	
		EXEC @qtde_aprox_lotes = spu_qtde_aproximada_lotes @infracoes_por_lote, @id_processo_remessa, @data_inicial, @data_final

		BEGIN TRAN

		INSERT INTO gera_remessa_automatico
			(
				data_solicitacao,
				data_remessa,
				data_inicial,
				data_final,
				infracoes_por_lote,
				total_infracoes,
				qtde_aprox_lotes,
				id_usuario,
				flag_geracao,
				data_geracao,
				flag_exportacao,
				data_exportacao
			)
		VALUES
			(
				GETDATE(),
				@data_remessa,
				@data_inicial,
				@data_final,
				@infracoes_por_lote,
				@total_infracoes,
				@qtde_aprox_lotes,
				@id_usuario,
				0,
				NULL,
				0,
				NULL
			)

		SET @id_remessa_automatico = @@IDENTITY

		COMMIT;

		RETURN @id_remessa_automatico

	END TRY
	BEGIN CATCH
	
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK;
		END

		EXEC spu_replica_erro

	END CATCH

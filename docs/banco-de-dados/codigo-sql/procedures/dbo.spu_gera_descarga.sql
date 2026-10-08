CREATE PROCEDURE [dbo].[spu_gera_descarga]
	@diaInicio DATE,
	@diaFim DATE,
	@idUsuario INT
AS

DECLARE @idDescarga INT

IF EXISTS (	SELECT id_descarga 
			FROM descarga d (nolock)
			WHERE	@diaInicio BETWEEN d.dia_inicio AND d.dia_fim
				OR	@diaFim BETWEEN d.dia_inicio AND d.dia_fim)

	RAISERROR('JÁ EXISTE UMA DESCARGA CONTENDO TOTAL OU PARCIALMENTE O PERÍODO DESEJADO!', 11, 1)	

BEGIN TRY

	BEGIN TRANSACTION

		DECLARE @qtdVeiculo INT
		DECLARE @qtdInfracao INT
		DECLARE @qtdImagem INT

		INSERT INTO descarga with (rowlock)
			(dia_inicio, dia_fim, data_criacao, total_veiculos, total_imagens, total_infracoes, id_usuario)
		VALUES 
			(@diaInicio, @diaFim, GETDATE(), 0,	0, 0, @idUsuario)

		SET @idDescarga = SCOPE_IDENTITY()

		INSERT INTO veiculo_descarga with (rowlock)
			(id_descarga, id_veiculo, id_pasta)
		SELECT 
			@idDescarga, 
			v.id_veiculo, 
			ROW_NUMBER() OVER (ORDER BY v.id_veiculo) / 1000 AS id_pasta
		FROM veiculo v (nolock) 
			INNER JOIN local_vigente lvg (nolock)
				ON lvg.id_local = v.id_local
		WHERE	CAST(v.data AS DATE) BETWEEN @diaInicio AND @diaFim 
			AND v.data >= lvg.data_inicio
			AND v.id_veiculo IN (	SELECT 
										id_veiculo 
									FROM 
										veiculo_imagem (nolock))

		SET @qtdVeiculo =  (SELECT 
								COUNT (*) 
							FROM 
								veiculo_descarga vd (nolock)
							WHERE 
								vd.id_descarga = @idDescarga)

		SET @qtdInfracao = (SELECT 
								COUNT (*) 
							FROM veiculo_descarga vd (nolock)
								INNER JOIN infracao i (nolock) 
									ON vd.id_veiculo = i.id_veiculo 
							WHERE 
								vd.id_descarga = @idDescarga)
					  
		SET @qtdImagem =   (SELECT 
								COUNT (*) 
							FROM veiculo_descarga vd (nolock)
								INNER JOIN veiculo_imagem vi (nolock) 
									ON vd.id_veiculo = vi.id_veiculo 
							WHERE 
								vd.id_descarga = @idDescarga)
		
		UPDATE descarga with (rowlock)
		SET total_veiculos	= @qtdVeiculo, 
			total_imagens	= @qtdImagem, 
			total_infracoes	= @qtdInfracao
		WHERE id_descarga = @idDescarga

	COMMIT
	
	RETURN @idDescarga
	
END TRY

BEGIN CATCH

	IF (@@TRANCOUNT > 0)
		ROLLBACK

	EXEC spu_replica_erro
	
END CATCH	






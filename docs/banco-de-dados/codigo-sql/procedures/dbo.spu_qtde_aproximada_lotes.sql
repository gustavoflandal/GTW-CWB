CREATE PROCEDURE [dbo].[spu_qtde_aproximada_lotes]
	@tamanho_lotes INT,
	@id_processo_remessa INT,
	@data_inicial DATETIME,
	@data_final DATETIME
AS

	--DECLARE @tamanho_lotes INT = 280, @id_processo_remessa INT = 4, @data_inicial DATETIME = '2019-02-20 00:00:00', @data_final DATETIME = '2019-02-20 23:59:59'
	DECLARE @qtde_aprox_lotes INT,
			@valor DECIMAL(10,2),
			@total_infracoes INT,
			@enquadramento INT

	SET @qtde_aprox_lotes = 0
	SET @total_infracoes = 0


	DECLARE enquadramentos_cursor CURSOR FOR 
	SELECT enq.id_enquadramento
	FROM   infracao inf (NOLOCK)
		   INNER JOIN enquadramento enq (NOLOCK)
				ON  enq.id_enquadramento = inf.id_enquadramento
		   LEFT JOIN infracao_remessa ir (NOLOCK)
				ON  ir.id_infracao = inf.id_infracao
	WHERE  inf.id_processo = @id_processo_remessa
		   AND CAST(inf.data AS DATE) BETWEEN CAST(@data_inicial AS DATE) AND CAST(@data_final AS DATE)
		   AND ir.id_infracao IS NULL
	GROUP BY
		   enq.id_enquadramento
	ORDER BY
		   enq.id_enquadramento;

	OPEN enquadramentos_cursor

	FETCH NEXT FROM enquadramentos_cursor 
	INTO @enquadramento

	WHILE @@FETCH_STATUS = 0
	BEGIN

		SET @total_infracoes = (
					SELECT COUNT(*) AS qtde
					FROM   infracao inf (NOLOCK)
						   LEFT JOIN infracao_remessa ir (NOLOCK)
								ON  ir.id_infracao = inf.id_infracao
					WHERE  inf.id_processo = @id_processo_remessa
						   AND inf.id_enquadramento = @enquadramento
						   AND CAST(inf.data AS DATE) BETWEEN CAST(@data_inicial AS DATE) AND CAST(@data_final AS DATE)
						   AND ir.id_infracao IS NULL
		)

		SET @valor = CAST(@total_infracoes AS FLOAT) / CAST(@tamanho_lotes AS FLOAT)
		SET @qtde_aprox_lotes = @qtde_aprox_lotes + CAST(CEILING(@valor) AS INT)

		FETCH NEXT FROM enquadramentos_cursor 
		INTO @enquadramento

	END 
	
	CLOSE enquadramentos_cursor;
	DEALLOCATE enquadramentos_cursor;

	RETURN @qtde_aprox_lotes

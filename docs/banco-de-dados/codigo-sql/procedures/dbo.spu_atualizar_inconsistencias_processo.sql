CREATE PROCEDURE [dbo].[spu_atualizar_inconsistencias_processo]
AS

	DECLARE @id INT
	EXEC @id = spu_log_inicia_processo 'atualizar_inconsistencias_processo', 0

	DECLARE @inconsistencia_processo_enquadramento AS TABLE (id_processo INT NOT NULL, id_enquadramento INT NOT NULL, id_inconsistencia INT NOT NULL, quantidade INT NULL)
	DECLARE @id_processo INT, @id_enquadramento INT

	DECLARE processo_enquadramento_cursor CURSOR FOR 
	SELECT p.id_processo, e.id_enquadramento
	FROM   processo p, enquadramento e
		  -- INNER JOIN (
				--			SELECT id_enquadramento
				--			FROM   infracao (NOLOCK)
				--			GROUP BY
				--				   id_enquadramento
		  -- ) enq
				--ON  enq.id_enquadramento = e.id_enquadramento
		   INNER JOIN (
							SELECT eri.id_enquadramento
							FROM   local_vigente lv (NOLOCK)
								   INNER JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK)
										ON  ceri.id_configuracao_equipamento = lv.id_configuracao_equipamento
								   INNER JOIN enquadramento_regra_infracao eri (NOLOCK)
										ON  eri.tipo = ceri.tipo
							WHERE  eri.id_enquadramento > 1
							GROUP BY
								   eri.id_enquadramento
		   ) enq
				ON  enq.id_enquadramento = e.id_enquadramento
	WHERE  e.id_enquadramento > 1
		   AND e.id_enquadramento < 99999
		   AND p.id_processo IN (1, 2, 24)
	--ORDER BY
	--	   p.id_processo,
	--	   e.id_enquadramento

	OPEN processo_enquadramento_cursor

	FETCH NEXT FROM processo_enquadramento_cursor 
	INTO @id_processo, @id_enquadramento

	WHILE @@FETCH_STATUS = 0
	BEGIN

		--PRINT (@id_processo)
		--PRINT (@id_enquadramento)
		INSERT INTO @inconsistencia_processo_enquadramento (id_processo, id_enquadramento, id_inconsistencia, quantidade)
		--DECLARE @id_processo INT = 1, @id_enquadramento INT = 60412
		SELECT @id_processo AS id_processo,
			   @id_enquadramento AS id_enquadramento,
			   inc.id_inconsistencia,
			   COUNT(sub1.id_inconsistencia) quantidade
		FROM   inconsistencia inc (NOLOCK)     
			   INNER JOIN processo_inconsistencia pi (NOLOCK)     
					ON  inc.id_inconsistencia = pi.id_inconsistencia
			   INNER JOIN enquadramento_inconsistencia ei (NOLOCK)     
					ON  inc.id_inconsistencia = ei.id_inconsistencia
			   LEFT JOIN (    
							SELECT TOP(100)
								   ip.id_inconsistencia
							FROM   infracao_processo ip (NOLOCK)    
								   INNER JOIN infracao i (NOLOCK)
										ON  ip.id_infracao = i.id_infracao    
							WHERE  ip.id_inconsistencia > 0
								   AND ip.id_processo = @id_processo
								   AND i.id_enquadramento = @id_enquadramento
							ORDER BY
								   ip.id_infracao_processo DESC
			   ) AS sub1
					ON  sub1.id_inconsistencia = inc.id_inconsistencia
		WHERE  inc.id_inconsistencia > 0
			   AND pi.id_processo = @id_processo
			   AND ei.id_enquadramento = @id_enquadramento
		GROUP BY
			   pi.id_processo,
			   ei.id_enquadramento,
			   inc.id_inconsistencia
		ORDER BY
			   quantidade DESC
    
		FETCH NEXT FROM processo_enquadramento_cursor 
		INTO @id_processo, @id_enquadramento

	END 
	CLOSE processo_enquadramento_cursor;
	DEALLOCATE processo_enquadramento_cursor;

	TRUNCATE TABLE inconsistencia_processo_enquadramento
	INSERT INTO inconsistencia_processo_enquadramento
	SELECT id_processo, id_enquadramento, id_inconsistencia, ISNULL(quantidade, 0) AS quantidade FROM @inconsistencia_processo_enquadramento

	EXEC spu_log_finaliza_processo @id


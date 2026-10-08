CREATE FUNCTION [dbo].[fcn_getAproveitamentoImagensAgrupadoPeriodo]
(
	@dataInicio date,
    @dataFim date 
)
RETURNS nvarchar(max)
AS

BEGIN

	DECLARE @resultado NVARCHAR(MAX)

	SET @resultado = N''
	
	DECLARE @tempLocal TABLE
	(
		id_local int,
		turno CHAR
	)

	DECLARE @tempInfo TABLE
	(
	  id_local INT,
	  id_inconsistencia INT,
	  turno CHAR
	)

	DECLARE @tempTotal TABLE
	(
		id_local INT,
		turno CHAR,
		total INT
	)

	DECLARE @tempValidas TABLE
	(
		id_local INT,
		turno CHAR,
		validas INT
	)

	DECLARE @tempPTL TABLE
	(
		id_local INT,
		turno CHAR,
		ptl INT 
	)

	DECLARE @tempPTG TABLE
	(
		id_local INT,
		turno CHAR,
		ptg INT
	)
	
	DECLARE @tempPNT TABLE
	(
		id_local INT,
		turno CHAR,
		pnt INT
	)	
	
	DECLARE @tempBruto TABLE
	(
		id_local INT,
		turno CHAR,
		bruto FLOAT
	)		

	DECLARE @tempLiquido TABLE
	(
		id_local INT,
		turno CHAR,
		liquido FLOAT
	)	
	
	-- Cabeçalho
    DECLARE @cabecalho NVARCHAR(MAX)

    SET @cabecalho = N'<table border="1">'
					+ N'<th class="head_tabela">Local</th>'
					+ N'<th class="head_tabela">Total</th>'
					+ N'<th class="head_tabela">Válidas</th>'
					+ N'<th class="head_tabela">PTL</th>'
					+ N'<th class="head_tabela">PTG</th>'
					+ N'<th class="head_tabela">PNT</th>'
					+ N'<th class="head_tabela">Ap.Bruto(%)</th>'
					+ N'<th class="head_tabela">Ap.Liq.(%)</th>'	
	
	-- **************************************************************
	--Insere em tabela temporária o produto cart. ([id_local] x ['D','N', '']
	-- **************************************************************
	INSERT INTO @tempLocal (id_local, turno)
	SELECT
		lvg.id_local,
		sub.turno
	FROM
		local_vigente lvg (nolock),
		(SELECT 'D' AS turno UNION 
			SELECT 'N' AS turno UNION
				SELECT '' AS turno) AS sub
	WHERE
		em_operacao = 1
		
	-- **************************************************************
	-- Insere em tabela temporária as infrações do período
	-- **************************************************************
	INSERT INTO @tempInfo (id_local,id_inconsistencia,turno)
	SELECT
		inf.id_local,
		inf.id_inconsistencia,
		CASE
			WHEN DATEPART(hh, inf.data) BETWEEN 6 AND 18 THEN 'D'
			ELSE  'N'
		END
	FROM
		infracao inf (nolock)
	WHERE	CAST(inf.data AS DATE) BETWEEN @dataInicio AND @dataFim
		AND inf.id_processo	IN (SELECT id_processo 
									FROM processo (nolock) 
									WHERE ativo = 1
									AND id_processo_proximo IS NULL)

	-- **************************************************************
	-- Agrupa Infrações "Totais"
	-- **************************************************************
	INSERT INTO @tempTotal(id_local,turno,total)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.qtd, 0)
		FROM @tempLocal tl 
			LEFT JOIN (	SELECT 
							id_local, turno, 
							COUNT(*) AS qtd
						FROM @tempInfo t
						GROUP BY
							t.id_local, t.turno
					) AS sub
				ON	tl.id_local = sub.id_local
				AND tl.turno = sub.turno
		WHERE tl.turno <> ''
			
	-- **************************************************************
	-- Agrupa Infrações "Válidas"
	-- **************************************************************
	INSERT INTO @tempValidas (id_local,turno,validas)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.qtd, 0)
		FROM @tempLocal tl 
			LEFT JOIN (	SELECT 
							id_local, 
							turno, 
							COUNT(*) AS qtd
						FROM @tempInfo t
						WHERE t.id_inconsistencia = 0 -- Consistentes
							OR t.id_inconsistencia IS NULL -- Desconhecidas (presumir como válidas)
						GROUP BY
							t.id_local, t.turno
					) AS sub
			ON	tl.id_local = sub.id_local
			AND tl.turno = sub.turno
		WHERE tl.turno <> ''
				
	-- **************************************************************
	-- Agrupa "Inconsistentes por PTL"
	-- **************************************************************
	INSERT INTO @tempPTL (id_local,turno,ptl)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.qtd, 0)
		FROM @tempLocal tl 
		LEFT JOIN (	SELECT 
						id_local, 
						turno, 
						COUNT(*) AS qtd
					FROM @tempInfo t
						INNER JOIN inconsistencia inc (nolock) 
							ON inc.id_inconsistencia = t.id_inconsistencia
					WHERE	t.id_inconsistencia > 0 -- Inconsistentes
						AND inc.razao_tecnica = 1 -- Razão Técnica Leve
					GROUP BY
						t.id_local, t.turno) AS sub
			ON	tl.id_local = sub.id_local
			AND tl.turno = sub.turno
		
	-- **************************************************************
	-- Agrupa "Inconsistentes por PTG"
	-- **************************************************************
	INSERT INTO @tempPTG (id_local,turno,ptg)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.qtd, 0)
		FROM @tempLocal tl 
			LEFT JOIN (	SELECT 
							id_local, 
							turno, 
							COUNT(*) AS qtd
						FROM @tempInfo t
							INNER JOIN inconsistencia inc (nolock) 
								ON inc.id_inconsistencia = t.id_inconsistencia
						WHERE	t.id_inconsistencia > 0 -- Inconsistentes
							AND inc.razao_tecnica = 2 -- Razão Técnica Grave
						GROUP BY
							t.id_local, t.turno
					) AS sub
				ON	tl.id_local = sub.id_local
				AND tl.turno = sub.turno
		WHERE tl.turno <> ''
			
	-- **************************************************************
	-- Agrupa "Inconsistentes por PNT"
	-- **************************************************************
	INSERT INTO @tempPNT (id_local,turno,pnt)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.qtd, 0)
		FROM @tempLocal tl 
			LEFT JOIN (	SELECT 
							id_local, 
							turno, 
							COUNT(*) AS qtd
						FROM @tempInfo t
							INNER JOIN inconsistencia inc (nolock) 
								ON inc.id_inconsistencia = t.id_inconsistencia
						WHERE t.id_inconsistencia > 0 -- Inconsistentes
							AND inc.razao_tecnica = 0 -- Não Razão Técnica
						GROUP BY
							t.id_local, t.turno
					) AS sub
				ON	tl.id_local = sub.id_local
				AND tl.turno = sub.turno
		WHERE tl.turno <> ''
				
	-- **************************************************************
	-- Agrupa "Aproveitamento Bruto"
	-- **************************************************************
	INSERT INTO @tempBruto (id_local,turno,bruto)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.bruto, CAST(0 AS FLOAT))
		FROM @tempLocal tl 
			LEFT JOIN (	SELECT 
							tv.id_local, 
							tv.turno,
							(CAST (100 AS FLOAT) * CAST(tv.validas AS FLOAT) / CAST(tt.total AS FLOAT)) AS bruto
						FROM @tempValidas tv
							INNER JOIN @tempTotal tt 
								ON	tt.id_local = tv.id_local
								AND tt.turno = tv.turno
						WHERE tt.total > 0
					) AS sub
				ON	tl.id_local = sub.id_local
				AND tl.turno = sub.turno
		WHERE tl.turno <> ''

	-- **************************************************************
	-- Agrupa "Aproveitamento Bruto", por dia
	-- **************************************************************
	INSERT INTO @tempBruto (id_local,turno,bruto)
		SELECT
			tl.id_local,
			'',
			COALESCE(sub2.bruto, CAST(0 AS FLOAT))
		FROM (	SELECT 
					sub.id_local, 
					sub.turno 
				FROM 
					@tempLocal AS sub
				WHERE 
					sub.turno = ''
			) AS tl
			LEFT JOIN (	SELECT 
							tv.id_local, 
							tv.turno,
							(CAST (100 AS FLOAT) * CAST(tv.validas AS FLOAT) / CAST(tt.total AS FLOAT)) AS bruto
						FROM (	SELECT 
									sub.id_local, 
									'' AS turno,
									SUM(sub.validas) AS validas
								FROM @tempValidas sub
								GROUP BY sub.id_local
							) AS tv
							INNER JOIN (SELECT 
											sub.id_local, 
											'' AS turno,
											SUM(sub.total) AS total
										FROM @tempTotal sub
										GROUP BY sub.id_local
									) AS tt
								ON	tt.id_local = tv.id_local
								AND tt.turno = ''					  
						WHERE tt.total > 0
				) AS sub2
				ON tl.id_local = sub2.id_local
			WHERE tl.turno = ''

	-- **************************************************************
	-- Agrupa "Aproveitamento Líquido"
	-- **************************************************************
	INSERT INTO @tempLiquido(id_local,turno,liquido)
		SELECT
			tl.id_local,
			tl.turno,
			COALESCE(sub.liquido, CAST(0 AS FLOAT))
		FROM @tempLocal tl 
			LEFT JOIN (	SELECT 
							tv.id_local, 
							tv.turno,
							(CAST (100 AS FLOAT) * CAST(tv.validas AS FLOAT) / (CAST(tv.validas AS FLOAT) + CAST(tg.ptg AS FLOAT))) AS liquido
						FROM @tempValidas tv
							INNER JOIN @tempPTG tg 
								ON	tg.id_local = tv.id_local
								AND tg.turno = tv.turno
						WHERE tv.validas > 0 OR tg.ptg > 0
					) AS sub
				ON	tl.id_local = sub.id_local
				AND tl.turno = sub.turno
		WHERE tl.turno <> ''

	-- **************************************************************
	-- Agrupa "Aproveitamento Líquido", por dia
	-- **************************************************************
	INSERT INTO @tempLiquido(id_local,turno,liquido)
		SELECT
			tl.id_local,
			'',
			COALESCE(sub2.liquido, CAST(0 AS FLOAT))
		FROM (	SELECT 
					sub.id_local, 
					sub.turno 
				FROM @tempLocal AS sub
				WHERE sub.turno = ''
			) AS tl
			LEFT JOIN (	SELECT 
							tv.id_local, 
							tv.turno,
							(CAST (100 AS FLOAT) * CAST(tv.validas AS FLOAT) / (CAST(tv.validas AS FLOAT) + CAST(tg.ptg AS FLOAT))) AS liquido
						FROM (	SELECT 
									sub.id_local, 
									'' AS turno,
									SUM(sub.validas) AS validas
								FROM @tempValidas sub
								GROUP BY sub.id_local
							) AS tv
							INNER JOIN (SELECT 
											sub.id_local, 
											'' AS turno,
											SUM(sub.ptg) AS ptg
										FROM @tempPTG sub
										GROUP BY sub.id_local
									) AS tg
								ON	tg.id_local = tv.id_local
								AND tg.turno = ''
						WHERE tv.validas > 0 OR tg.ptg > 0
						) AS sub2
				ON	tl.id_local = sub2.id_local
				WHERE tl.turno = ''


	DECLARE @Local INT
	DECLARE @Total INT
	DECLARE @Validas INT
	DECLARE @PTL INT
	DECLARE @PTG INT
	DECLARE @PNT INT
	DECLARE @ApBruto FLOAT
	DECLARE @ApLiquido FLOAT
			
	-- *****************************************************************
	-- Cursor "Diurno"
	-- *****************************************************************

    DECLARE cursorDiurno CURSOR LOCAL FAST_FORWARD
    FOR (	SELECT
				tt.id_local, tt.total, tv.validas, ptl.ptl,
				ptg.ptg, pnt.pnt,
				ROUND(tb.bruto, 2,1),
				ROUND(tl.liquido,2,1)
			FROM @tempTotal tt
				INNER JOIN @tempValidas tv
					ON	tv.id_local = tt.id_local
					AND tv.turno = tt.turno
				INNER JOIN @tempPTL ptl
					ON	ptl.id_local = tt.id_local
					AND ptl.turno = tt.turno
				INNER JOIN @tempPTG ptg
					ON	ptg.id_local = tt.id_local
					AND ptg.turno = tt.turno
				INNER JOIN @tempPNT pnt
					ON	pnt.id_local = tt.id_local
					AND pnt.turno = tt.turno			
				INNER JOIN @tempBruto tb
					ON	tb.id_local = tt.id_local
					AND tb.turno = tt.turno
				INNER JOIN @tempLiquido tl
					ON	tl.id_local = tt.id_local
					AND tl.turno = tt.turno
				WHERE
					tt.turno = 'D'
			) ORDER BY 8

		SET @resultado = @resultado + N'<br><b>Diurno (06:00-18:00)</b>'

		OPEN cursorDiurno 
		FETCH NEXT FROM cursorDiurno 
		INTO 
			@Local, 
			@Total, 
			@Validas,
			@PTL, 
			@PTG, 
			@PNT, 
			@ApBruto, 
			@ApLiquido
     
		IF (@@FETCH_STATUS = 0)

			BEGIN

      				-- Adiciona cabeçalho
				SET @resultado = @resultado + @cabecalho
		
				-- Primeira linha				
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTG)				
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApBruto)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApLiquido)
								+ N'</td></tr>'
				
				FETCH NEXT FROM cursorDiurno 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT, 
					@ApBruto, 
					@ApLiquido
			
			END -- END IF
		
		-- Outras linhas
		WHILE @@FETCH_STATUS = 0

			BEGIN
      
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTG)				
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApBruto)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApLiquido)
								+ '</td></tr>'

				FETCH NEXT FROM cursorDiurno 
				INTO 
					@Local, 
					@Total,
					@Validas,
					@PTL, 
					@PTG, 
					@PNT, 
					@ApBruto, 
					@ApLiquido
			
			END -- END WHILE FETCH

		-- Totaliza os valores diurnos
		SET @Total = (SELECT SUM(tt.total) 
						FROM @tempTotal tt
						WHERE tt.turno = 'D')
		SET @Validas = (	SELECT SUM(tv.validas) 
							FROM @tempValidas tv
							WHERE tv.turno = 'D')
		SET @PTL = (	SELECT SUM(ptl.ptl) 
						FROM @tempPTL ptl
						WHERE ptl.turno = 'D')
		SET @PTG = (	SELECT SUM(ptg.ptg) 
						FROM @tempPTG ptg
						WHERE ptg.turno = 'D')
		SET @PNT = (	SELECT SUM(pnt.pnt) 
						FROM @tempPNT pnt
						WHERE pnt.turno = 'D')
		SET @ApBruto = ROUND((SELECT AVG(brt.bruto) 
								FROM @tempBruto brt
								WHERE brt.turno = 'D'), 2, 1)
		SET @ApLiquido = ROUND((	SELECT AVG(liq.liquido) 
									FROM @tempLiquido liq
									WHERE liq.turno = 'D'), 2, 1)

		SET @resultado = @resultado + N'<tr>'
					+ N'<td align="center">Total:'
					+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Total)
					+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Validas)
					+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTL)
					+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTG)				
					+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PNT)
					+ N'</td><td align="right"></td><td align="right">'
					+ '</td></tr>'				   	
				   		
		SET @resultado = @resultado + N'<tr>'
					+ N'<td align="center">Média:'
					+ N'</td><td align="center"></td><td align="center">'
					+ N'</td><td align="center"></td><td align="center">'
					+ N'</td><td align="center"></td><td align="right">'
					+ CONVERT(NVARCHAR(5), @ApBruto) + N'</td><td align="right">'
					+ CONVERT(NVARCHAR(5), @ApLiquido)
					+ '</td></tr>'				   		
				   		
		-- Fecha a tabela					
		SET @resultado = @resultado + N'</table>'

		CLOSE cursorDiurno
		DEALLOCATE cursorDiurno
      
		-- *****************************************************************
		-- Cursor "Noturno"
		-- *****************************************************************

		DECLARE cursorNoturno CURSOR LOCAL FAST_FORWARD
		FOR (
			SELECT
				tt.id_local, tt.total, tv.validas, ptl.ptl,
				ptg.ptg, pnt.pnt,
				ROUND(tb.bruto, 2,1),
				ROUND(tl.liquido,2,1)
			FROM @tempTotal tt
				INNER JOIN @tempValidas tv
					ON	tv.id_local = tt.id_local
					AND tv.turno = tt.turno
				INNER JOIN @tempPTL ptl
					ON	ptl.id_local = tt.id_local
					AND ptl.turno = tt.turno
				INNER JOIN @tempPTG ptg
					ON	ptg.id_local = tt.id_local
					AND ptg.turno = tt.turno
				INNER JOIN @tempPNT pnt
					ON	pnt.id_local = tt.id_local
					AND pnt.turno = tt.turno			
				INNER JOIN @tempBruto tb
					ON	tb.id_local = tt.id_local
					AND tb.turno = tt.turno
				INNER JOIN @tempLiquido tl
					ON	tl.id_local = tt.id_local
					AND tl.turno = tt.turno
				WHERE
					tt.turno = 'N'
			) ORDER BY 8

		SET @resultado = @resultado + N'<br><br><b>Noturno (00:00-06:00 e 18:00-00:00)</b>'

		OPEN cursorNoturno 
		FETCH NEXT FROM cursorNoturno 
		INTO 
			@Local, 
			@Total, 
			@Validas,
			@PTL, 
			@PTG, 
			@PNT, 
			@ApBruto, 
			@ApLiquido
     
		IF (@@FETCH_STATUS = 0)

			BEGIN
      
				-- Adiciona cabeçalho
				SET @resultado = @resultado + @cabecalho
		
				-- Primeira linha				
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTG)				
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApBruto)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApLiquido)
								+ N'</td></tr>'
				
				FETCH NEXT FROM cursorNoturno 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT, 
					@ApBruto, 
					@ApLiquido
			
			END -- END IF
		
		-- Outras linhas
		WHILE @@FETCH_STATUS = 0

			BEGIN
      
				SET @resultado = @resultado + N'<tr>'
					+ N'<td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(4), @Local)
					+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Total)
					+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Validas)
					+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTL)
					+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTG)				
					+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PNT)
					+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApBruto)
					+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApLiquido)
					+ '</td></tr>'

				FETCH NEXT FROM cursorNoturno 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT, 
					@ApBruto, 
					@ApLiquido
			
			END -- END WHILE FETCH

		-- Totaliza os valores noturnos
		SET @Total = (	SELECT SUM(tt.total) 
							FROM @tempTotal tt
							WHERE tt.turno = 'N')
		SET @Validas = (SELECT SUM(tv.validas) 
							FROM @tempValidas tv
							WHERE tv.turno = 'N')
		SET @PTL = (SELECT SUM(ptl.ptl) 
						FROM @tempPTL ptl
						WHERE ptl.turno = 'N')
		SET @PTG = (SELECT SUM(ptg.ptg) 
						FROM @tempPTG ptg
						WHERE ptg.turno = 'N')
		SET @PNT = (SELECT SUM(pnt.pnt) 
						FROM @tempPNT pnt
						WHERE pnt.turno = 'N')
		SET @ApBruto = ROUND((	SELECT AVG(brt.bruto) 
									FROM @tempBruto brt
									WHERE brt.turno = 'N'), 2, 1)
		SET @ApLiquido = ROUND((SELECT AVG(liq.liquido) 
									FROM @tempLiquido liq
									WHERE liq.turno = 'N'), 2, 1)

		SET @resultado = @resultado + N'<tr>'
						+ N'<td align="center">Total:'
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Total)
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Validas)
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTL)
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTG)				
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PNT)
						+ N'</td><td align="right"></td><td align="right">'
						+ '</td></tr>'				   	
				   		
		SET @resultado = @resultado + N'<tr>'
						+ N'<td align="center">Média:'
						+ N'</td><td align="center"></td><td align="center">'
						+ N'</td><td align="center"></td><td align="center">'
						+ N'</td><td align="center"></td><td align="right">'
						+ CONVERT(NVARCHAR(5), @ApBruto) + N'</td><td align="right">'
						+ CONVERT(NVARCHAR(5), @ApLiquido)
						+ '</td></tr>'		
		
		-- Fecha a tabela					
		SET @resultado = @resultado + N'</table>'

		CLOSE cursorNoturno
		DEALLOCATE cursorNoturno      

		-- *****************************************************************
		-- Cursor "Geral"
		-- *****************************************************************

		DECLARE cursorGeral CURSOR LOCAL FAST_FORWARD
		FOR (
			SELECT
				tt.id_local, 
				SUM(tt.total), 
				SUM(tv.validas), 
				SUM(ptl.ptl),
				SUM(ptg.ptg), 
				SUM(pnt.pnt),
				ROUND(AVG(tb.bruto), 2, 1),
				ROUND(AVG(tl.liquido), 2, 1)
			FROM @tempTotal tt
				INNER JOIN @tempValidas tv
					ON	tv.id_local = tt.id_local
					AND tv.turno = tt.turno
				INNER JOIN @tempPTL ptl
					ON	ptl.id_local = tt.id_local
					AND ptl.turno = tt.turno
				INNER JOIN @tempPTG ptg
					ON	ptg.id_local = tt.id_local
					AND ptg.turno = tt.turno
				INNER JOIN @tempPNT pnt
					ON	pnt.id_local = tt.id_local
					AND pnt.turno = tt.turno
				INNER JOIN @tempBruto tb
					ON	tb.id_local = tt.id_local
					AND tb.turno = ''
				INNER JOIN @tempLiquido tl
					ON	tl.id_local = tt.id_local
					AND tl.turno = ''
				GROUP BY
					tt.id_local
			) ORDER BY 8, 7, 1

		SET @resultado = @resultado + N'<br><br><b>Geral</b>'

		OPEN cursorGeral 
		FETCH NEXT FROM cursorGeral 
		INTO 
			@Local, 
			@Total, 
			@Validas,
			@PTL, 
			@PTG, 
			@PNT, 
			@ApBruto, 
			@ApLiquido
     
		IF (@@FETCH_STATUS = 0)

			BEGIN
      
				-- Adiciona cabeçalho
				SET @resultado = @resultado + @cabecalho
		
				-- Primeira linha				
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTG)				
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApBruto)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApLiquido)
								+ N'</td></tr>'
				
				FETCH NEXT FROM cursorGeral 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT, 
					@ApBruto, 
					@ApLiquido
			
			END -- END IF
		
		-- Outras linhas
		WHILE @@FETCH_STATUS = 0

			BEGIN
      
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PTG)				
								+ N'</td><td align="center" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApBruto)
								+ N'</td><td align="right" class="dado_lista_tabela_grid">' + CONVERT(NVARCHAR(5), @ApLiquido)
								+ '</td></tr>'

				FETCH NEXT FROM cursorGeral 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT, 
					@ApBruto, 
					@ApLiquido
			
			END -- END WHILE FETCH
		
		-- Totaliza os valores gerais
		SET @Total = (	SELECT SUM(tt.total) 
							FROM @tempTotal tt)
		SET @Validas = (SELECT SUM(tv.validas) 
							FROM @tempValidas tv)
		SET @PTL = (SELECT SUM(ptl.ptl) 
						FROM @tempPTL ptl)
		SET @PTG = (SELECT SUM(ptg.ptg) 
						FROM @tempPTG ptg)
		SET @PNT = (SELECT SUM(pnt.pnt) 
						FROM @tempPNT pnt)
		SET @ApBruto = ROUND((	SELECT AVG(brt.bruto) 
									FROM @tempBruto brt
									WHERE brt.turno = ''), 2, 1)
		SET @ApLiquido = ROUND((SELECT AVG(liq.liquido) 
									FROM @tempLiquido liq
									WHERE liq.turno = ''), 2, 1)
		
		SET @resultado = @resultado + N'<tr>'
						+ N'<td align="center">Total:'
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Total)
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Validas)
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTL)
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTG)				
						+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PNT)
						+ N'</td><td align="right"></td><td align="right">'
						+ '</td></tr>'				   	
				   		
		SET @resultado = @resultado + N'<tr>'
						+ N'<td align="center">Média:'
						+ N'</td><td align="center"></td><td align="center">'
						+ N'</td><td align="center"></td><td align="center">'
						+ N'</td><td align="center"></td><td align="right">'
						+ CONVERT(NVARCHAR(5), @ApBruto) + N'</td><td align="right">'
						+ CONVERT(NVARCHAR(5), @ApLiquido)
						+ '</td></tr>'			
		
		-- Fecha a tabela					
		SET @resultado = @resultado + N'</table>'

		CLOSE cursorGeral
		DEALLOCATE cursorGeral     

	RETURN @resultado

END --End function




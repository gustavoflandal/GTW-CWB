CREATE FUNCTION [dbo].[fcn_getAproveitamentoImagens]
( 
	@data date 
)
RETURNS nvarchar(max)
AS

BEGIN

	DECLARE @resultado NVARCHAR(MAX)
	DECLARE @hoje DATE
	DECLARE @currDia DATE
	
	DECLARE @Total INT
	DECLARE @Local INT
	DECLARE @Validas INT
	
	DECLARE @PTL INT
	DECLARE @PTG INT
	DECLARE @PNT INT
	
	SET @currDia = CAST(GetDate() AS DATE)
	
	SET @resultado = N''
	

	WHILE (@currDia >= @data)

	BEGIN

		IF ((	SELECT 
					COUNT(*) 
				FROM 
					infracao (nolock)
				WHERE CAST(data AS DATE) = @currDia) <>	(	SELECT COUNT(*) 
															FROM infracao (nolock)
															WHERE	CAST(data AS DATE) = @currDia 
																AND id_processo IN (SELECT id_processo 
																					FROM processo (nolock) 
																					WHERE ativo = 1
																					AND id_processo_proximo IS NULL)
														)
			)

			BEGIN

				SET @currDia = DATEADD(dd, -1, @currDia)
				CONTINUE

			END	
	

		DECLARE cursorEquipamento CURSOR LOCAL
		FOR 
		SELECT 
			sub.[Local], 
			sub.Total, 
			sub.Validas,
			sub.PTL, 
			sub.PTG, 
			sub.PNT 
		FROM (	SELECT 
					inf.id_local AS [Local],
					(SELECT COUNT(*) 
						FROM infracao (nolock)
						WHERE	CAST(data AS DATE) = @currDia
							AND id_local = inf.id_local AND infracao.id_processo IN (SELECT id_processo 
																						FROM processo (nolock) 
																						WHERE ativo = 1
																						AND id_processo_proximo IS NULL)
					) AS Total,  
					-- ****************
					(SELECT COUNT(*) 
						FROM infracao (nolock)
						WHERE	CAST(data AS DATE) = @currDia
							AND infracao.id_inconsistencia = 0
							AND id_local = inf.id_local AND infracao.id_processo IN (SELECT id_processo 
																						FROM processo pro (nolock)
																						WHERE	pro.id_processo_proximo IS NULL
																							AND pro.ativo = 1)
					) AS Validas,
					-- ****************      
					(SELECT COUNT (*) 
						FROM infracao (nolock)
							INNER JOIN inconsistencia (nolock) 
								ON infracao.id_inconsistencia = inconsistencia.id_inconsistencia
						WHERE	CAST(data AS DATE) = @currDia
							AND id_local = inf.id_local AND infracao.id_processo IN (SELECT id_processo 
																						FROM processo pro (nolock)
																						WHERE	inconsistencia.razao_tecnica = 1
																							AND pro.id_processo_proximo IS NULL
																							AND pro.ativo = 1)
					) AS PTL,
					-- ****************      
					(SELECT COUNT (*) 
						FROM infracao (nolock)
							INNER JOIN inconsistencia (nolock) 
								ON infracao.id_inconsistencia = inconsistencia.id_inconsistencia
						WHERE	CAST(data AS DATE) = @currDia
							AND id_local = inf.id_local AND infracao.id_processo IN (SELECT id_processo 
																						FROM processo pro (nolock)
																						WHERE	inconsistencia.razao_tecnica = 2
																							AND pro.id_processo_proximo IS NULL
																							AND pro.ativo = 1)
					) AS PTG,
					-- ****************      
					(SELECT COUNT (*) 
						FROM infracao (nolock)
							INNER JOIN inconsistencia (nolock) 
								ON infracao.id_inconsistencia = inconsistencia.id_inconsistencia
					WHERE	CAST(data AS DATE) = @currDia
						AND id_local = inf.id_local AND infracao.id_processo IN (SELECT id_processo 
																					FROM processo pro (nolock)
																					WHERE	inconsistencia.razao_tecnica = 0 
																						AND inconsistencia.id_inconsistencia > 0
																						AND pro.id_processo_proximo IS NULL
																						AND pro.ativo = 1)
					) AS PNT
				FROM
					infracao inf (nolock)
				WHERE
					CAST(inf.data AS DATE) = @currDia
					GROUP BY inf.id_local
				) AS sub 
		ORDER BY 
			CAST(100.0 - (CAST((PTG / CAST(Total AS DECIMAL(9,3))) AS DECIMAL(4,3)) * 100) AS DECIMAL(4,1)),
			CAST(CAST((@Validas / CAST(@Total AS DECIMAL(9,3))) AS DECIMAL(4,3)) * 100 AS DECIMAL(4,1)),
			sub.Total 
		FOR READ ONLY
	
		OPEN cursorEquipamento 
		FETCH NEXT FROM cursorEquipamento 
		INTO 
			@Local, 
			@Total, 
			@Validas,
			@PTL, 
			@PTG, 
			@PNT
     
		IF (@@FETCH_STATUS = 0)

			BEGIN
      
				-- Título do bloco						
				SET @resultado = @resultado + N'<br><h1>Dia: ' + CONVERT(NVARCHAR(20), @currDia, 103) + '</h1>'

				-- Cabeçalho
				SET @resultado = @resultado + N'<table border="1">'
								+ N'<th>Local</th><th>Total</th><th>Validas</th><th>PTL</th>'
								+ N'<th>PTG</th><th>PNT</th><th>ApBruto%</th><th>ApSomentePTG%</th>'
		
				-- Primeira linha				
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTG)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right">' + STR(CAST(CAST((@Validas / CAST(@Total AS DECIMAL(9,3))) AS DECIMAL(4,3)) * 100 AS DECIMAL(4,1)))
								+ N'</td><td align="right">' + STR(CAST(100.0 - (CAST((@PTG / CAST(@Total AS DECIMAL(9,3))) AS DECIMAL(4,3)) * 100) AS DECIMAL(4,1)))
								+ N'</td></tr>'
				
				FETCH NEXT FROM cursorEquipamento 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT		
				
			END -- END IF
		
		-- Outras linhas
		WHILE @@FETCH_STATUS = 0

			BEGIN
      
				SET @resultado = @resultado + N'<tr>'
								+ N'<td align="center">' + CONVERT(NVARCHAR(4), @Local)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Total)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @Validas)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTL)
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PTG)				
								+ N'</td><td align="center">' + CONVERT(NVARCHAR(10), @PNT)
								+ N'</td><td align="right">' + STR(CAST(CAST((@Validas / CAST(@Total AS DECIMAL(9,3))) AS DECIMAL(4,3)) * 100 AS DECIMAL(4,1)))
								+ N'</td><td align="right">' + STR(CAST(100.0 - (CAST((@PTG / CAST(@Total AS DECIMAL(9,3))) AS DECIMAL(4,3)) * 100) AS DECIMAL(4,1)))
								+ '</td></tr>'

				FETCH NEXT FROM cursorEquipamento 
				INTO 
					@Local, 
					@Total, 
					@Validas,
					@PTL, 
					@PTG, 
					@PNT

			END -- END WHILE FETCH
		
		-- Fecha a tabela					
		SET @resultado = @resultado + N'</table>'

		CLOSE cursorEquipamento
		DEALLOCATE cursorEquipamento
      
		SET @currDia = DATEADD(dd, -1, @currDia)

	END

    RETURN @resultado
    
END




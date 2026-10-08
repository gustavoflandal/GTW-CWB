CREATE FUNCTION [dbo].[fcn_getProdutividadeOperacoes] ()
RETURNS nvarchar(max)
AS
BEGIN

	DECLARE @resultado NVARCHAR(MAX)
	
	SET @resultado =
		N'<h1>Produtividade Operações</h1><br>'

	DECLARE @data DATE		
	DECLARE @tipo NVARCHAR(50)
	DECLARE @qtd INT

	SET @data = CAST(DATEADD(DAY, -12, GetDate()) AS DATE)

	DECLARE cursorDia CURSOR LOCAL
    FOR SELECT 
			sub.data, 
			sub.nome, 
			sub.qtd 
		FROM ( (SELECT 
					CAST(ifp.data AS DATE) AS data, 
					pro.nome, 
					COUNT(*) AS qtd,
					ifp.id_processo AS ord
				FROM processo  pro WITH (NOLOCK)
					LEFT JOIN infracao_processo ifp WITH (NOLOCK)  
						ON pro.id_processo = ifp.id_processo
				WHERE	CAST(ifp.data AS DATE) >= @data
					AND (pro.nome IN ('Triagem', 'Digitação') 
						OR	(pro.nome = 'Validação' AND ifp.tempo > 0))
				GROUP BY CAST(ifp.data AS DATE), pro.nome, ifp.id_processo
				)
				UNION 
				(SELECT 
					CAST(ifp.data AS DATE) AS data, 
					'Total', 
					COUNT(*) AS qtd,
					4 AS ord
				FROM processo pro WITH (NOLOCK)
					LEFT JOIN infracao_processo ifp WITH (NOLOCK)  
						ON pro.id_processo = ifp.id_processo
				WHERE	CAST(ifp.data AS DATE) >= @data
					AND (pro.nome IN ('Triagem', 'Digitação') 
						OR (pro.nome = 'Validação' AND ifp.tempo > 0))
				GROUP BY CAST(ifp.data AS DATE)) 
			) AS sub
		ORDER BY sub.data DESC, sub.ord

	DECLARE @currDia DATE

	SET @currDia = CAST(DATEADD(DAY, -100, GetDate()) AS DATE)

	DECLARE @abriuTable BIT

	SET @abriuTable = 0
   	  
	OPEN cursorDia 
	FETCH NEXT FROM cursorDia 
	INTO 
		@data, 
		@tipo, 
		@qtd

	WHILE (@@FETCH_STATUS = 0)

		BEGIN
      
			IF (@currDia != @data)

				BEGIN

					IF(@abriuTable = 1)

						BEGIN
	   						SET @resultado = @resultado + '</table><br>'
	   						SET @abriuTable = 0
						END
		   
					IF(@abriuTable = 0)

						BEGIN
							SET @resultado = @resultado + N'<table border="1">'
								+ N'<tr style="background-color:#DADADA">'
								+ N'<th colspan="3" align="left">Dia: '
								+ CONVERT(NVARCHAR(10), @data, 103) + N'</th>'
							SET @abriuTable = 1
						END
		   
				END -- END IF 

			SET @currDia = @data
			
			SET @resultado = @resultado + N'<tr style="color: #6B81A6">'
							+ '<td>' + @tipo + N'</td><td>' + CAST(@qtd AS NVARCHAR(10))
							+ N'</td></tr>'	

			FETCH NEXT FROM cursorDia 
			INTO 
				@data, 
				@tipo, 
				@qtd

		END -- END WHILE
      
	CLOSE cursorDia
	DEALLOCATE cursorDia
      
	IF(@abriuTable = 1)

		BEGIN

			SET @resultado = @resultado + '</table>'

			SET @abriuTable = 0

		END      
      
	RETURN @resultado

END




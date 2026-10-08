CREATE PROCEDURE [dbo].[spu_verificar_dados_importacao]
AS

	-- desconsiderar o dia atual, pois pode não ter sumarizado ainda.
	DECLARE @mensagem VARCHAR(255) = ''
	
    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ ' - Verificação dos dados de importação ...'

	DECLARE @ontem DATE = CAST(GETDATE()-1 AS DATE)
	DECLARE @hoje DATE = CAST(GETDATE() AS DATE)
	DECLARE @dias_para_verificar INT = 15
	DECLARE @dias_sumarizados INT = 0

	DECLARE @ultima_data_estatistica DATETIME
	DECLARE @ultima_data_veiculo DATETIME

	DECLARE @min_data_veiculo_imp DATETIME
	DECLARE @max_data_veiculo_imp DATETIME
	DECLARE @count_veiculo_imp INT
	
	DECLARE @min_data_veiculo_est DATETIME
	DECLARE @max_data_veiculo_est DATETIME
	DECLARE @count_veiculo_est INT


	DECLARE @emails NVARCHAR(MAX)
	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

	/********* busca dos dados ****************************/
	SELECT 
		@dias_sumarizados = COUNT(*)
	FROM (	SELECT
				data
			FROM 
				veiculo_sumarizado (nolock)
			WHERE
				data >= DATEADD(DAY, -@dias_para_verificar, @ontem)	
			GROUP BY 
				data
		) AS T

	-- verifica a tabela de veiculo_estatistica
	SELECT
		@ultima_data_estatistica = MAX(data)
	FROM
		veiculo_estatistica (nolock)
	WHERE
		data < @hoje -- desconsiderar o dia atual

	-- verifica a tabela de veiculo (imagem)
	SELECT
		@ultima_data_veiculo = MAX(data)
	FROM
		veiculo (nolock)
	WHERE
		data < @hoje -- desconsiderar o dia atual

	-- verifica a tabela de importacao
	SELECT 
		@min_data_veiculo_imp = MIN(Data), 
		@max_data_veiculo_imp = MAX(DATA), 
		@count_veiculo_imp = COUNT(*)
	FROM 
		veiculo_importacao vi (nolock) 
	WHERE
		data < @hoje -- desconsiderar o dia atual
		
	SELECT
		@min_data_veiculo_est = MIN(data), 
		@max_data_veiculo_est = MAX(data), 
		@count_veiculo_est = COUNT(*)
	FROM 
		veiculo_estatistica (nolock)
	WHERE 
		data < DATEADD(DAY, -181, @hoje) -- 6 meses + 1 dia
				

	/**** verificações **************************************/
	IF @dias_para_verificar > @dias_sumarizados

		BEGIN
			SET @mensagem = 'Sumarização dos veículos esta com problemas... Dias Sumarizados: ' + 
							LTRIM(STR(@dias_sumarizados)) + ' de ' + LTRIM(STR(@dias_para_verificar))
			PRINT @mensagem
			
			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @mensagem,
				@body_format = 'TEXT' 		
		END		

	ELSE

		BEGIN

			PRINT 'veiculo_sumarizado OK'

		END		
		
	IF DATEDIFF(DAY, @ultima_data_veiculo, @hoje) > 1

		BEGIN

			SET @mensagem = 'Imagens com atraso na importação... Último dia: ' + 
							CONVERT(CHAR(19), @ultima_data_veiculo, 120) 
			PRINT @mensagem
			
			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @mensagem,
				@body_format = 'TEXT' 		
		END		

	ELSE

		PRINT 'Veículo OK'

	IF DATEDIFF(DAY, @ultima_data_estatistica, @hoje) > 1

		BEGIN

			SET @mensagem = 'Veículos para estatistica com atraso na importação... Último dia: ' + 
							CONVERT(CHAR(19), @ultima_data_estatistica, 120) 
			PRINT @mensagem
			
			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @mensagem,
				@body_format = 'TEXT' 		

		END	

	ELSE

		PRINT 'Veículo estatistica OK'

	IF DATEDIFF(DAY, @min_data_veiculo_imp, @hoje) > 1

		BEGIN

			SET @mensagem = 'Dados com atraso na finalização da importação...' + 
							' Primeiro dia: ' + CONVERT(CHAR(19), @min_data_veiculo_imp, 120) + 
							' Último dia: ' + CONVERT(CHAR(19), @max_data_veiculo_imp, 120) + 
							' Quantidade de veículos: ' + LTRIM(STR(@count_veiculo_imp))
			PRINT @mensagem
			
			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @mensagem,
				@body_format = 'TEXT' 		

		END	

	ELSE

		PRINT 'Veículo Importacao OK'

	IF @min_data_veiculo_est IS NOT NULL

		BEGIN

			SET @mensagem = 'Dados de veículos para estatistica superiores ao limite...' + 
				' Primeiro dia: ' + CONVERT(CHAR(19), @min_data_veiculo_est, 120) + 
				' Último dia: ' + CONVERT(CHAR(19), @max_data_veiculo_est, 120) + 
				' Quantidade de veículos: ' + LTRIM(STR(@count_veiculo_est))
			PRINT @mensagem
			
			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @mensagem,
				@body_format = 'TEXT' 		

		END	

	ELSE

		PRINT 'Veículo estatistica OK'



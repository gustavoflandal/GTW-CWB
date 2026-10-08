--sp_helptext spu_VeiculoEstatistica_ApagarSumarizados
CREATE PROCEDURE [dbo].[spu_VeiculoEstatistica_ApagarSumarizados]
AS
/*
Criado em 03/07/2013
Apaga os dados da tabela veiculo_estatistica superiores a 90 dias e que já foram sumarizados
Obs: Os que ainda não foram sumarizados são mantidos na tabela
*/

SET NOCOUNT ON

DECLARE @Database		VARCHAR(50),
		@Dias_Manter	INT,
		@Data_Atual		DATE,
		@Data_Apagar	DATE,
		@Data_Maior		DATE,
		@Data_Limite	DATE,
		@NaoSumarizados	INT,
		@Count			INT,
		@Inicio			DATETIME,
		@Termino		DATETIME,
		@Registros		INT

-- Informar quantidade de dias a ser mantido no banco de dados
SELECT @Database	= 'CSX02_22_EUNAPOLIS_BA_HOM'
SELECT @Dias_Manter = 100

-- Verifica Data mais antiga a ser apagada
SELECT @Data_Atual  = CAST(GETDATE() AS DATE)
SELECT @Data_Limite = CAST(GETDATE()-@Dias_Manter AS DATE)
SELECT @Data_Apagar = CAST(MIN(data) AS DATE) FROM veiculo_estatistica (NOLOCK)
SELECT @Data_Maior = CAST(MAX(data) AS DATE) FROM veiculo_estatistica (NOLOCK)

SELECT @Count = DATEDIFF(dd,@Data_Apagar,@Data_Limite)

DECLARE @Relatorio VARCHAR(MAX)

-- Busca Informações de quantas datas estão pendentes de serem apagados
SET @Relatorio =  
'------------------------------------------------------- '									+ CHAR(10) +	
'Banco de Dados... : ' + ISNULL(@Database,'NULL')											+ CHAR(10) +
'Dias Armazenados. : ' + ISNULL(CAST(DATEDIFF(dd,@Data_Apagar,@Data_Atual) as VARCHAR),0)	+ ' dia(s)' + CHAR(10) +
'Dias a Manter.... : ' + ISNULL(CAST(@Dias_Manter	AS VARCHAR),0)							+ ' dia(s)' + CHAR(10) +
'Dias a Apagar.... : ' + ISNULL(CAST(@Count		AS VARCHAR),0)								+ ' dia(s)' + CHAR(10) +
'Data Atual....... : ' + ISNULL(CAST(@Data_Atual	AS VARCHAR),'NULL')						+ CHAR(10) +
'Data Limite ..... : ' + ISNULL(CAST(@Data_Limite	AS VARCHAR),'NULL')						+ CHAR(10) +
'Data mais Antiga. : ' + ISNULL(CAST(@Data_Apagar	AS VARCHAR),'NULL')						+ CHAR(10) +
'Data mais Recente : ' + ISNULL(CAST(@Data_maior	AS VARCHAR),'NULL')						+ CHAR(10)

-- Executa exclusão de dados enquanto a data estiver fora do prazo de armazenamento
-- e não for horário de trabalho para não impactar no sistema
WHILE @Count > 0 and @Data_Apagar < @Data_Limite

	BEGIN
	
		IF @Data_Apagar >= @Data_Limite

			BEGIN
			
				SET @Relatorio = @Relatorio + 
				'------------------------------------------------------- '						+ CHAR(10) +	
				'Não há mais registros a apagar... '											+ CHAR(10)
				BREAK
			
			END

		IF CAST(GETDATE() AS TIME) BETWEEN '06:00:00' AND '19:00:00'
		
			BEGIN
			
				SET @Relatorio = @Relatorio + 
				'------------------------------------------------------- '						 + CHAR(10) +	
				'Processamento interrompido devido ao horário !!! ' + CAST(GETDATE() AS VARCHAR) + CHAR(10)
				BREAK
				
			END
		

		IF EXISTS (SELECT 1 FROM veiculo_sumarizado (NOLOCK) WHERE data = @Data_Apagar)
		
			BEGIN 

				SET @Inicio = GETDATE()
				
				-- SELECT @Data_Apagar AS Data, COUNT(id_veiculo_unic) AS Quantidade
				DELETE
					FROM veiculo_estatistica WITH (ROWLOCK) 
					WHERE CAST(data AS DATE) = @Data_Apagar
					
				SET @Registros	= @@ROWCOUNT
				SET @Termino	= GETDATE()
				SET @Relatorio	= @Relatorio +
				'------------------------------------------------------- '	+ CHAR(10) +	
				'Dia................ : ' + CAST(@Data_Apagar AS VARCHAR)	+ CHAR(10) +
				'Inicio............. : ' + CAST(@Inicio  AS VARCHAR)		+ CHAR(10) +
				'Termino............ : ' + CAST(@Termino AS VARCHAR)		+ CHAR(10) +
				'Registros Apagados. : ' + CAST(@Registros AS VARCHAR)		+ CHAR(10)

				SET @Count			= @Count-1
				SET @Data_Apagar	= DATEADD(dd, 1, @Data_Apagar)

				WAITFOR DELAY '00:10:00'
				
			END
			
		ELSE 

			BEGIN
			
				SET @Relatorio = @Relatorio +
				'--------------------------------------------- '							+ CHAR(10) +	
				'Dia ' + CAST(@Data_Apagar AS VARCHAR) + ' ainda não foi sumarizado !!! '	+ CHAR(10) +
				'Sumarizando veículos do dia ' + CAST(@Data_Apagar AS VARCHAR)				+ CHAR(10)
				
				EXEC spu_sumariza_veiculos @Data_Apagar, @Data_Apagar
				
				SET @Count			= @Count-1
				SET @Data_Apagar	= DATEADD(dd, 1, @Data_Apagar)
				
			END				

	END

	IF @Data_Apagar >= @Data_Limite

		BEGIN
		
			SET @Relatorio = @Relatorio + 
			'------------------------------------------------------- '						+ CHAR(10) +	
			'Não há mais registros a apagar... '											+ CHAR(10)
	
		END
				
SET @Relatorio = @Relatorio +
'------------------------------------------------------- '	+ CHAR(10)	

PRINT @Relatorio

--EXEC msdb.dbo.sp_send_dbmail 
--	@recipients = 'thiago.surgik@consilux.com.br',
--	@subject = '[C008] GTW - Monitoramento Veiculo Estatistica',
--	@body = @Relatorio,
--	@body_format = 'TEXT'

CREATE PROCEDURE [dbo].[spu_EnviaEmailAlertaArquivosCadastrosSemNovos]
AS

    --body DO HTML DO EMAIL
    DECLARE @html nvarchar(max)

    SET @html = '<table border =1><tr><td>Último Arquivo</td><td>Última Data de Importação</td><td>Info</td></tr>'

    --COUNT DE QUANTOS ARQUIVOS EXISTEM PARA CONTROLAR O WHILE
    DECLARE @QuantidadeArquivos as Int

    SELECT 
		@QuantidadeArquivos = COUNT(*) 
	FROM 
		configuracao_alerta_cad_arquivos_importados (nolock)

    WHILE (@QuantidadeArquivos > 0)

		BEGIN

			--Nome do arquivo com mascara
			DECLARE @NomeArquivo as varchar(100)

			/*Determina o formato da data no nome do arquivo
			** 0 = 311210 = (31/12/2010)
			** 1 = arquivo sem data no nome
			** 2 = 31122010 = (31/12/2010)
			*/

			--agrupa a forma como a data vem no nome do arquivo
			DECLARE @DataTipo as int

			--maximo de dias que o arquivo pode ficar sem atualização
			DECLARE @MaxDiasSemArq as int

			--Nome compreto do ultimo arquivo importado
			DECLARE @UltimoImportado as varchar(100)

			--data de importação do ultimo arquivo importado
			DECLARE @UltimaDataImportacao as Datetime

			--reseta para caso do arquivo procurado não existir e não ficar com o nome do arquivo da ultima iteração
			SET @NomeArquivo = ''
			SET @UltimoImportado = ''

			--busca o primeiro arquivo da tabela que contem a mascara para todos os arquivos
			SELECT TOP 1 
				@NomeArquivo = arquivo, 
				@DataTipo = tipoData,
				@MaxDiasSemArq = max_dias_sem_arqui
			FROM 
				configuracao_alerta_cad_arquivos_importados (nolock)
			WHERE
				verificado = 0

			--marcar como utilizado--
			UPDATE configuracao_alerta_cad_arquivos_importados with (rowlock)
			SET verificado = 1
			WHERE arquivo = @NomeArquivo

			--busca o registro do ultimo arquivo importado
			SELECT TOP 1 
				@UltimoImportado = nome_arquivo,
				@UltimaDataImportacao = data_hora
			FROM 
				cad_arquivos_importados (nolock)
			WHERE 
				nome_arquivo like @NomeArquivo
			ORDER BY 
				data_hora desc

			DECLARE @Dia as VARCHAR(2)
			DECLARE @Mes as VARCHAR(2)
			DECLARE @Ano as VARCHAR(2)
			DECLARE @Ano4 as VARCHAR(4)

			--TRATA ARQUIVOS DO TIPO 0 MASCARADOS COMO 311210 = (31/12/2010)
			IF(@DataTipo = 0)

				BEGIN

					SELECT TOP 1
						@Dia = substring(nome_arquivo, 1, 2),
						@Mes = substring(nome_arquivo, 3, 2),
						@Ano = substring(nome_arquivo, 5, 2)
					FROM 
						cad_arquivos_importados (nolock)
					WHERE 
						nome_arquivo = @UltimoImportado
					ORDER BY 
						1 DESC

					DECLARE @UltimoImportadoDate as DateTime
					DECLARE @Formatada as VARCHAR(11)

					SET @Formatada = '20' + @Ano + '/' + @Mes + '/' + @Dia
					SET @UltimoImportadoDate = cast(@Formatada as Datetime)

					--DETERMINA QUANTOS DIAS O ARQUIVO PODE ESTAR DESATUALIZADO
					IF(@UltimoImportadoDate < GETDATE() - @MaxDiasSemArq)

						BEGIN

							IF(@UltimoImportado != '')
								BEGIN    

									SET @html = @html + '<tr><td>'+rtrim(ltrim(@UltimoImportado))+'</td><td>'+
												rtrim(ltrim(CONVERT(VARCHAR(10), @UltimaDataImportacao, 103)))+ '</td><td>'+
												cast((SELECT DATEDIFF(DD,@UltimoImportadoDate, GETDATE())) as varchar(2)) +'
												DIAS SEM ARQUIVO NOVO'+'</td></tr>'

								END

							IF(@UltimoImportado = '')

								BEGIN   
								 
									SET @html = @html + '<tr><td>'+rtrim(ltrim(@NomeArquivo))+'</td><td>'+ '-'+
												'</td><td>'+'</td><td>ARQUIVO NUNCA FOI IMPORTADO'+'</td></tr>'
								
								END

						END
			END


			--TRATA ARQUIVOS DO TIPO 2 MASCARADOS COMO 31122010 = (31/12/2010)
			IF(@DataTipo = 2)

			BEGIN

				SELECT TOP 1
					@Dia = substring(nome_arquivo, 1, 2),
					@Mes = substring(nome_arquivo, 3, 2),
					@Ano4 = substring(nome_arquivo, 5, 4)
				FROM 
					cad_arquivos_importados (nolock)
				WHERE 
					nome_arquivo like @UltimoImportado
				ORDER BY 
					1 DESC

				SET @Formatada = @Ano4 + '/' + @Mes + '/' + @Dia
				SET @UltimoImportadoDate = cast(@Formatada as Datetime)

				--DETERMINA QUANTOS DIAS O ARQUIVO PODE ESTAR DESATUALIZADO
				IF(@UltimoImportadoDate < GETDATE() - @MaxDiasSemArq)

					BEGIN

						IF(@UltimoImportado != '')

						BEGIN    

							SET @html = @html + '<tr><td>'+rtrim(ltrim(@UltimoImportado))+'</td><td>'+
										rtrim(ltrim(CONVERT(VARCHAR(10), @UltimaDataImportacao, 103)))+'</td><td>'+
										cast((SELECT DATEDIFF(DD,@UltimoImportadoDate, GETDATE())) as varchar(2)) +'
										DIAS SEM ARQUIVO NOVO'+'</td></tr>'

						END

						IF(@UltimoImportado = '')

							BEGIN

								SET @html = @html + '<tr><td>'+rtrim(ltrim(@NomeArquivo))+'</td><td>'+ '-'+
											'</td><td>ARQUIVO NUNCA FOI IMPORTADO'+'</td></tr>'

							END

					END        

			END

			--TRATA ARQUIVOS DO TIPO 1 SEM DATA NO NOME
			IF(@DataTipo = 1)

				BEGIN

					IF(@UltimaDataImportacao < GETDATE() - @MaxDiasSemArq)

						BEGIN

							IF(@UltimoImportado != '')

								BEGIN

									SET @html = @html + '<tr><td>'+rtrim(ltrim(@UltimoImportado))+'</td><td>'+
												rtrim(ltrim(CONVERT(VARCHAR(10), @UltimaDataImportacao, 103)))+'</td><td>'+
												cast((SELECT DATEDIFF(DD,@UltimaDataImportacao, GETDATE())) as varchar(2))+'
												DIAS SEM ARQUIVO NOVO'+'</td></tr>'

								END

							IF(@UltimoImportado = '')

								BEGIN

									SET @html = @html + '<tr><td>'+rtrim(ltrim(@NomeArquivo))+'</td><td>'+ '-' +'
												</td><td>'+'ARQUIVO	NUNCA FOI IMPORTADO'+'</td></tr>'

								END

						END

				END

			SET @QuantidadeArquivos = @QuantidadeArquivos -1

    END

    --ZERA AS CONSULTAS PARA A PROXIMA EXECUÇÃO
    UPDATE configuracao_alerta_cad_arquivos_importados with (rowlock)
	SET verificado = 0

    SET @html = @html + '</table>'

    DECLARE @emails NVARCHAR(MAX)
        SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ImpTXT] Alerta Cad Arquivos Desatualizados'

        EXEC msdb.dbo.sp_send_dbmail
            @recipients = @emails,
            @subject = @assuntoEmail,
            @body = @html,
            @body_format = 'HTML'


/****** Object:  StoredProcedure [dbo].[spu_EnviaEmailAlertaCapturaTravado]    Script Date: 03/26/2010 09:12:39 ******/
SET ANSI_NULLS ON




CREATE PROCEDURE [dbo].[spu_finaliza_importacao_estatisticas_temp]
AS

SET NOCOUNT ON

DECLARE	@Amostra					int			= 100000,
		@Tempo_Processamento		varchar(8)	= '00:10:00',
		@Data_Fim					datetime,
		@Estatisticas_Proc			int			= 0,
		@Estatisticas_Proc_Total	int			= 0,
		@id							int			= 0,
		@Total						int			= 0

SET @Data_Fim = GETDATE() + @Tempo_Processamento

SELECT @Total = COUNT(*) FROM veiculo_importacao vi (NOLOCK)
LEFT JOIN imagem_importacao ii (NOLOCK) ON vi.id_veiculo_unic = ii.id_veiculo_unic
WHERE ii.id_veiculo_unic IS NULL AND vi.importar = 1 AND vi.data BETWEEN '2018-10-19 00:00:00' AND '2018-10-19 23:59:59'

EXEC @id = spu_log_inicia_processo 'finaliza_importacao_estatisticas_temp', @Total


DECLARE @tmp_arquivos_importados AS TABLE (id_arquivo INT PRIMARY KEY, nome_arquivo VARCHAR(200), UNIQUE (nome_arquivo))

--INSERT INTO @tmp_arquivos_importados (id_arquivo, nome_arquivo)
--SELECT ai.id_arquivo,
--	   ai.nome_arquivo
--FROM   arquivos_importados ai (NOLOCK)
--WHERE  ai.data_importacao >= DATEADD(DAY, -3, GETDATE())

--SELECT * FROM @tmp_arquivos_importados

WHILE GETDATE() < @Data_Fim

	BEGIN

		BEGIN TRY
			
			-- DECLARE @AMOSTRA INT = 1000
			DECLARE @tmp_veiculo_importacao_estatisticas AS TABLE
				(
					data DATETIME,
					id_veiculo_local INT, 
					placa CHAR(7),
					velocidade DECIMAL(6,1),
					comprimento DECIMAL(6,1),
					pista TINYINT,
					flag INT,
					segundos DECIMAL(8,3),
					id_veiculo_unic BIGINT,
					id_classe CHAR(1),
					id_local INT,
					sequencia_local TINYINT,
					ocupacao INT,
					id_arquivo INT,
					entre_faixa INT
				)

			--TRUNCATE TABLE dbo.tmp_veiculo_importacao_estatisticas

			-- DECLARE @AMOSTRA INT = 1000000
			INSERT INTO @tmp_veiculo_importacao_estatisticas
				(
					data,
					id_veiculo_local, 
					placa,
					velocidade,
					comprimento,
					pista,
					flag,
					segundos,
					id_veiculo_unic,
					id_classe,
					id_local,
					sequencia_local,
					ocupacao,
					id_arquivo,
					entre_faixa)
				-- DECLARE @AMOSTRA INT = 10000
				SELECT TOP (@Amostra) --vim.id_veiculo_unic--, vim.data
						vim.data, 
						vim.id_veiculo_local, 
						vim.placa,
						vim.velocidade,
						vim.comprimento, 
						vim.pista, 
						vim.flag, 
						vim.segundos, 
						vim.id_veiculo_unic,
						vim.id_classe, 
						vim.id_local, 
						vim.sequencia_local, 
						vim.ocupacao, 
						aim.id_arquivo,
						vim.entre_faixa
				FROM veiculo_importacao vim (nolock)
					LEFT JOIN imagem_importacao ii (nolock) 
						ON vim.id_veiculo_unic = ii.id_veiculo_unic
					--INNER JOIN @tmp_arquivos_importados aim
					INNER JOIN arquivos_importados aim
						ON aim.nome_arquivo = vim.nome_arquivo 
				WHERE	ii.id_veiculo_unic IS NULL 
					AND	(vim.tipo_registro IS NULL OR vim.tipo_registro = 0) 
					AND vim.sequencia_local IS NOT NULL
					AND vim.importar = 1
					AND vim.data BETWEEN '2018-10-19 00:00:00' AND '2018-10-19 23:59:59'
				--ORDER BY
				--	 vim.data ASC
			
			--SELECT * FROM @tmp_veiculo_importacao_estatisticas

			IF NOT EXISTS (SELECT TOP 1 1 FROM @tmp_veiculo_importacao_estatisticas)
				BEGIN
					BREAK
				END

			INSERT INTO veiculo_estatistica with(rowlock) (
				data, 
				id_veiculo_local, 
				placa, 
				velocidade,
				comprimento, 
				pista, 
				flag, 
				segundos, 
				id_veiculo_unic,
				id_classe, 
				id_local, 
				sequencia_local, 
				ocupacao, 
				id_arquivo,
				entre_faixa)
			SELECT 
				vpi.data, 
				vpi.id_veiculo_local, 
				vpi.placa,
				vpi.velocidade,
				vpi.comprimento, 
				vpi.pista, 
				vpi.flag, 
				vpi.segundos, 
				vpi.id_veiculo_unic,
				vpi.id_classe, 
				vpi.id_local, 
				vpi.sequencia_local, 
				vpi.ocupacao, 
				vpi.id_arquivo,
				vpi.entre_faixa
			FROM @tmp_veiculo_importacao_estatisticas vpi
				--INNER JOIN veiculo_importacao vim (nolock) 
				--	ON vpi.id_veiculo_unic = vim.id_veiculo_unic 
				--LEFT JOIN veiculo_estatistica ve (nolock) 
				--	ON vpi.id_veiculo_unic = ve.id_veiculo_unic 
				--INNER JOIN arquivos_importados aim (nolock) 
				--	ON aim.nome_arquivo = vim.nome_arquivo 
			--WHERE ve.id_veiculo_unic IS NULL

			SET @Estatisticas_Proc		= @@ROWCOUNT
			SET @Estatisticas_Proc_Total= @Estatisticas_Proc_Total + @Estatisticas_Proc


			DELETE pim 
			FROM perfil_importacao pim with(rowlock) 
				INNER JOIN @tmp_veiculo_importacao_estatisticas vpi
					ON vpi.id_veiculo_unic = pim.id_veiculo_unic 

			DELETE vim 
			FROM video_importacao vim with(rowlock) 
				INNER JOIN @tmp_veiculo_importacao_estatisticas vpi
					ON vpi.id_veiculo_unic = vim.id_veiculo_unic 

			DELETE vi
			FROM veiculo_importacao vi with(rowlock)
				INNER JOIN @tmp_veiculo_importacao_estatisticas vpi
					ON vpi.id_veiculo_unic = vi.id_veiculo_unic 

			EXEC spu_log_atualiza_processo @id, @Estatisticas_Proc

		END TRY 

		BEGIN CATCH

			PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'

			IF @@TRANCOUNT > 0 
				ROLLBACK

			--UPDATE veiculo_importacao SET importar = 0 FROM veiculo_estatistica ve (NOLOCK)
			--WHERE veiculo_importacao.id_veiculo_unic = ve.id_veiculo_unic
			--AND veiculo_importacao.id_veiculo_unic NOT IN (SELECT id_veiculo_unic FROM imagem_importacao (NOLOCK))

			BREAK

		END CATCH

		DELETE FROM @tmp_veiculo_importacao_estatisticas

	END

--TRUNCATE TABLE tmp_veiculo_importacao_estatisticas

PRINT ' - Estatisticas Processadas....: ' + dbo.fcn_FormataNumero(@Estatisticas_Proc_Total)	+ ' - '
EXEC spu_log_finaliza_processo @id

CREATE PROCEDURE [dbo].[spu_finaliza_importacao_estatisticas_ant]
AS

SET NOCOUNT ON

DECLARE	@Amostra					int			= 1000,
		@Tempo_Processamento		varchar(8)	= '00:10:00',
		@Data_Fim					datetime,
		@Estatisticas_Proc			int			= 0,
		@Estatisticas_Proc_Total	int			= 0,
		@id							int			= 0,
		@Total						int			= 0

SET @Data_Fim = GETDATE() + @Tempo_Processamento

SELECT @Total = COUNT(*) FROM veiculo_importacao_ant vi (NOLOCK) WHERE vi.importar = 1

EXEC @id = spu_log_inicia_processo 'finaliza_importacao_estatisticas_ant', @Total

WHILE GETDATE() < @Data_Fim

	BEGIN

		BEGIN TRY

			--DECLARE @AMOSTRA INT = 1000
			DECLARE @tmp_veiculo_importacao_estatisticas_ant AS TABLE
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

			-- DECLARE @AMOSTRA INT = 1000
			INSERT INTO @tmp_veiculo_importacao_estatisticas_ant
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
				-- DECLARE @AMOSTRA INT = 1000
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
				FROM veiculo_importacao_ant vim (nolock)
					INNER JOIN arquivos_importados aim (nolock) 
						ON aim.nome_arquivo = vim.nome_arquivo 
				WHERE vim.importar = 1
				ORDER BY
					 vim.data ASC

			--SELECT * FROM @@tmp_veiculo_importacao_estatisticas_ant

			IF NOT EXISTS (SELECT 1 FROM @tmp_veiculo_importacao_estatisticas_ant)
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
			FROM @tmp_veiculo_importacao_estatisticas_ant vpi

			SET @Estatisticas_Proc		= @@ROWCOUNT
			SET @Estatisticas_Proc_Total= @Estatisticas_Proc_Total + @Estatisticas_Proc

			DELETE vi
			FROM veiculo_importacao_ant vi with(rowlock)
				INNER JOIN @tmp_veiculo_importacao_estatisticas_ant vpi
					ON vpi.id_veiculo_unic = vi.id_veiculo_unic 
					
			EXEC spu_log_atualiza_processo @id, @Estatisticas_Proc

		END TRY 

		BEGIN CATCH

			PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'

			IF @@TRANCOUNT > 0 
				ROLLBACK

			BREAK

		END CATCH

		DELETE FROM @tmp_veiculo_importacao_estatisticas_ant

	END

PRINT ' - Estatisticas Antigas Processadas....: ' + dbo.fcn_FormataNumero(@Estatisticas_Proc_Total)	+ ' - '
EXEC spu_log_finaliza_processo @id

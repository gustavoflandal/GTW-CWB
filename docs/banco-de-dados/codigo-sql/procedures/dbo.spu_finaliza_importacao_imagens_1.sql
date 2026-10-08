



CREATE PROCEDURE [dbo].[spu_finaliza_importacao_imagens_1]
AS

SET NOCOUNT ON

DECLARE	@Amostra				int			= 20,
		@Tempo_Processamento	varchar(8)	= '00:10:00',
		@Data_Fim				datetime,
		@Veiculos_Proc_At		int	= 0,
		@Veiculos_Proc			int	= 0,
		@Infracao_Proc			int	= 0,
		@Imagem_Proc			int	= 0,
		@Perfil_Proc			int	= 0,
		@Video_Proc				int	= 0,
		@Registros_Proc_Total	int	= 0,
		@Total					int = 0,
		@id						int = 0

SET @Data_Fim = GETDATE() + @Tempo_Processamento

SELECT @Total = COUNT(ii.id_imagem) 
FROM   veiculo_importacao vi (NOLOCK)
	   INNER JOIN imagem_importacao ii (NOLOCK)
			ON  ii.id_veiculo_unic = vi.id_veiculo_unic
				AND ii.indice_imagem = 0
WHERE  vi.importar = 1
	   --AND vi.data BETWEEN '2018-06-25 00:00:00' AND '2018-06-28 23:59:59'

EXEC @id = spu_log_inicia_processo 'finaliza_importacao_imagens', @Total

WHILE GETDATE() < @Data_Fim

	BEGIN

		BEGIN TRY

			--DELETE FROM tmp_veiculo_importacao_imagens

			--DECLARE @Amostra INTEGER = 20
			DECLARE @tmp_veiculo_importacao_imagens AS TABLE (id_veiculo_unic BIGINT)

			INSERT INTO @tmp_veiculo_importacao_imagens (id_veiculo_unic)
			--DECLARE @Amostra INTEGER = 20
			SELECT TOP (@Amostra) ii.id_veiculo_unic--, vi.data
			FROM   imagem_importacao ii (NOLOCK)
				   JOIN veiculo_importacao vi (NOLOCK)
						ON  ii.id_veiculo_unic = vi.id_veiculo_unic
				   LEFT JOIN veiculo v (NOLOCK)
						ON  ii.id_veiculo_unic = v.id_veiculo_unic
			WHERE  --vi.sequencia_local IS NOT NULL
				   --AND 
				   (vi.tipo_registro IS NULL OR vi.tipo_registro = 1)
				   AND ii.indice_imagem = 0
				   AND v.id_veiculo IS NULL 
				   --AND vi.data < @MINDIA
				   AND vi.importar = 1
				   --AND vi.data BETWEEN '2018-06-25 00:00:00' AND '2018-06-28 23:59:59'
			--ORDER BY
			--	   vi.data ASC


			IF NOT EXISTS (SELECT 1 FROM @tmp_veiculo_importacao_imagens)
				BEGIN
					BREAK
				END
																
			INSERT INTO veiculo WITH(ROWLOCK) 
				(
					id_veiculo_local, 
					data, placa, velocidade,
					comprimento, pista, flag, 
					segundos, id_veiculo_unic,
					id_classe, id_local, 
					sequencia_local, ocupacao, id_arquivo, 
					velocidade_media,serie_equipamento_Montante,
					id_veiculo_Local_Montante,porteVeiculo,cadastro,
					serie_equipamento,
					codigo_prodam,
					entre_faixa   
				)
				SELECT vim.id_veiculo_local,
					   vim.data,
					   vim.placa,
					   vim.velocidade,
					   vim.comprimento,
					   vim.pista,
					   vim.flag,
					   vim.segundos,
					   vim.id_veiculo_unic,
					   vim.id_classe,
					   vim.id_local,
					   lv.sequencia_local, --vim.sequencia_local,
					   vim.ocupacao,
					   aim.id_arquivo,
					   velocidade_media,
					   serie_equipamento_Montante,
					   id_veiculo_Local_Montante,
					   porteVeiculo,cadastro,
					   vie.serie_equipamento,
					   ee.codigo_prodam,
					   vim.entre_faixa
				FROM   veiculo_importacao vim (NOLOCK)
					   JOIN arquivos_importados aim (NOLOCK) 
							ON  aim.nome_arquivo = vim.nome_arquivo
					   JOIN @tmp_veiculo_importacao_imagens vpi
							ON  vpi.id_veiculo_unic = vim.id_veiculo_unic 
					   JOIN local_vigente lv (NOLOCK)
							ON  vim.id_local = lv.id_local
					   LEFT JOIN veiculo_importacao_estatico vie (NOLOCK)
							ON  vie.id_veiculo_unic = vim.id_veiculo_unic 
					   LEFT JOIN equipamento_estatico ee (NOLOCK)
							ON  ee.serie_equipamento = vie.serie_equipamento

			SET @Veiculos_Proc_At = @@ROWCOUNT
			SET @Veiculos_Proc = @Veiculos_Proc + @Veiculos_Proc_At

			INSERT INTO infracao WITH(ROWLOCK)
				(
					id_imagem_local,
					id_enquadramento,
					id_veiculo,
					id_local,
					sequencia_local,
					pista,
					data,
					velocidade_limite,
					velocidade_considerada,
					segundos_tolerancia,
					tempo_vermelho_detec,
					data_afericao
				)
			SELECT --iim.id_imagem_local,
					vim.id_veiculo_local AS id_imagem_local,
					iim.id_enquadramento,
					vei.id_veiculo,
					vim.id_local,
					lv.sequencia_local,  --vim.sequencia_local,
					vim.pista,
					vim.data,
					iim.velocidade_limite,
					iim.velocidade_considerada,
					iim.segundos_tolerancia,
					iim.tempo_vermelho_detec,
					iim.data_afericao
			FROM   infracao_importacao iim (NOLOCK)
				   JOIN veiculo_importacao vim (NOLOCK)
						ON  vim.id_veiculo_unic = iim.id_veiculo_unic
				   JOIN veiculo vei (NOLOCK)
						ON  vei.id_veiculo_unic = iim.id_veiculo_unic	
				   JOIN local_vigente lv (NOLOCK)
						ON  lv.id_local = vim.id_local
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON  vpi.id_veiculo_unic = vim.id_veiculo_unic 			
			WHERE  vim.data >= lv.data_inicio

			SET @Infracao_Proc = @Infracao_Proc + @@ROWCOUNT
					
			INSERT INTO imagem_info WITH(ROWLOCK)
				(
					id_imagem,
					id_tipo_imagem,
					formato
				)
			SELECT iim.id_imagem,
					tim.id_tipo_imagem,
					iim.formato
			FROM   imagem_importacao iim (NOLOCK)
				   LEFT JOIN tipo_imagem tim (NOLOCK) 					
						ON tim.nome = iim.nome AND tim.numero = iim.numero
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON vpi.id_veiculo_unic = iim.id_veiculo_unic 
										
			INSERT INTO imagem WITH(ROWLOCK)
				(
					id_imagem,
					imagem,
					indice_imagem,
					ds_caminho
				)
			SELECT iim.id_imagem,
				   iim.imagem,
				   iim.indice_imagem,
				   iim.ds_caminho 
			FROM   imagem_importacao iim (NOLOCK)
				   --LEFT JOIN tipo_imagem tim (NOLOCK) 					
						--ON  tim.nome = iim.nome AND tim.numero = iim.numero
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON  vpi.id_veiculo_unic = iim.id_veiculo_unic 

			SET @Imagem_Proc = @Imagem_Proc + @@ROWCOUNT
							
			INSERT INTO veiculo_imagem WITH(ROWLOCK)
				(
					id_veiculo,
					id_imagem,
					id_imagem_local
				)
			SELECT vei.id_veiculo,
				   iim.id_imagem,
				   vei.id_veiculo_local AS id_imagem_local
				   --iim.id_imagem_local		
			FROM   imagem_importacao iim (NOLOCK)
				   JOIN veiculo vei (NOLOCK)
						ON  vei.id_veiculo_unic = iim.id_veiculo_unic
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON  vpi.id_veiculo_unic = iim.id_veiculo_unic
	
			INSERT INTO perfil WITH(ROWLOCK)
				(
					id_veiculo_unic,
					pista,
					sensor,
					quantidade_amostras,
					tamanho_amostra,
					inicio_disparo,
					final_disparo,
					perfil
				)
			SELECT pm.id_veiculo_unic,
				   pm.pista,
				   pm.sensor,
				   pm.quantidade_amostras,
				   pm.tamanho_amostra,
				   pm.inicio_disparo,
				   pm.final_disparo,
				   pm.perfil
			FROM   perfil_importacao pm (NOLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON  vpi.id_veiculo_unic = pm.id_veiculo_unic
				   --LEFT JOIN perfil p
						--ON  pm.id_veiculo_unic = p.id_veiculo_unic
			--WHERE  p.id_veiculo_unic IS NULL

			SET @Perfil_Proc = @Perfil_Proc + @@ROWCOUNT
					
			INSERT INTO video_info WITH(ROWLOCK)
				(
					id_video,
					id_tipo_video,
					formato
				)
			SELECT vdim.id_video,
				   tvd.id_tipo_video,
				   vdim.formato
			FROM   video_importacao vdim WITH(NOLOCK)
				   LEFT JOIN tipo_video tvd WITH(NOLOCK) 					
						ON  tvd.nome = vdim.nome
							AND tvd.numero = vdim.numero
				   JOIN @tmp_veiculo_importacao_imagens vpi 
						ON  vpi.id_veiculo_unic = vdim.id_veiculo_unic 
										
			INSERT INTO video WITH(ROWLOCK)
				(
					id_video,
					video
				)
			SELECT vdim.id_video,
				   vdim.video
			FROM   video_importacao vdim WITH(NOLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON  vpi.id_veiculo_unic = vdim.id_veiculo_unic 

			SET @Video_Proc = @Video_Proc + @@ROWCOUNT
							
			INSERT INTO veiculo_video WITH(ROWLOCK)
				(
					id_veiculo,
					id_video
				)
			SELECT vei.id_veiculo,
				   vdim.id_video		
			FROM   video_importacao vdim WITH(NOLOCK)
				   JOIN veiculo vei WITH(NOLOCK)
						ON  vei.id_veiculo_unic = vdim.id_veiculo_unic
				   JOIN @tmp_veiculo_importacao_imagens vpi
						ON  vpi.id_veiculo_unic = vdim.id_veiculo_unic

			DELETE video_importacao WITH(ROWLOCK)
			FROM   video_importacao p WITH(ROWLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi ON vpi.id_veiculo_unic = p.id_veiculo_unic 	

			DELETE perfil_importacao WITH(ROWLOCK)
			FROM   perfil_importacao p WITH(ROWLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi ON vpi.id_veiculo_unic = p.id_veiculo_unic 		

			DELETE imagem_importacao WITH(ROWLOCK)
			FROM   imagem_importacao ii WITH(ROWLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi ON vpi.id_veiculo_unic = ii.id_veiculo_unic 				

			DELETE infracao_importacao WITH(ROWLOCK)
			FROM   infracao_importacao ii WITH(ROWLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi ON vpi.id_veiculo_unic = ii.id_veiculo_unic 				
		
			DELETE veiculo_importacao_estatico WITH(ROWLOCK)
			FROM   veiculo_importacao_estatico vie WITH(ROWLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi ON vpi.id_veiculo_unic = vie.id_veiculo_unic 				

			DELETE veiculo_importacao WITH(ROWLOCK)
			FROM   veiculo_importacao vi WITH(ROWLOCK)
				   JOIN @tmp_veiculo_importacao_imagens vpi ON vpi.id_veiculo_unic = vi.id_veiculo_unic 		

			EXEC spu_log_atualiza_processo @id, @Veiculos_Proc_At

		END TRY

		BEGIN CATCH
				
			PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
			IF @@TRANCOUNT > 0
				ROLLBACK
		
			EXEC spu_replica_erro

		END CATCH

		DELETE FROM @tmp_veiculo_importacao_imagens

	END

PRINT ' - Veiculos Processados....: ' + dbo.fcn_FormataNumero(@Veiculos_Proc)	+ ' - '
		PRINT ' - Infrações Processadas...: ' + dbo.fcn_FormataNumero(@Infracao_Proc)	+ ' - '
		PRINT ' - Imagens Processadas.....: ' + dbo.fcn_FormataNumero(@Imagem_Proc)	+ ' - '
		PRINT ' - Perfis Processados......: ' + dbo.fcn_FormataNumero(@Perfil_Proc)	+ ' - '
		PRINT ' - Videos Processados......: ' + dbo.fcn_FormataNumero(@Video_Proc)	+ ' - '

EXEC spu_log_finaliza_processo @id



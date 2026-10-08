
CREATE PROCEDURE [dbo].[spu_popula_painel_contrato]
AS

	DECLARE @dataGeracao DATETIME = GETDATE()

 	BEGIN TRY

		BEGIN TRANSACTION

			INSERT INTO painel_contrato with (rowlock) (
				dataGeracao,
				numeroSerie, 
				idPista,
				codigoFaixa, 
				nomeFaixa,
				dataUltimaInfracao,
				capturaUptime, 
				capturaVersao,
				statusCopia, 
				ultimaDeteccao,
				capturaTravado, 
				excessoEventos,
				falhaComunicacaoCamera, 
				conexaoInstavel,
				equipamentoOffline, 
				naoEnviaEventosDias,
				semVeiculosValidos, 
				erroAtualizacaoSoftware,
				erroDIV, 
				erroLeituraLote,
				erroPainelControlador, 
				erroRespostaPolling,
				capturaIniciado, 
				desconexoesDiv)
			SELECT
				@dataGeracao,
				lv.serie_equipamento AS numeroSerie,
				cep.id_pista AS idPista,
				CASE 
					WHEN (cep.cod_pista_alternativo = 0)
						THEN cep.id_pista
					ELSE 
						cep.cod_pista_alternativo 
				END AS codigoFaixa,
				RTRIM(cep.nome_pista) AS nomeFaixa,
				ui.data_ultima_infracao AS dataUltimaInfracao,
				str.tempo_executando AS capturaUptime,
				RTRIM(str.versao) AS capturaVersao,
				RTRIM(str.status_copia) AS statusCopia,
				p13.ultima_deteccao AS ultimaDeteccao,
				p1.qtd_eventos AS capturaTravado,
				COALESCE(p2.qtd_eventos, 0) AS excessoEventos,
				p3.qtd_eventos AS falhaComunicacaoCamera,
				p4.qtd_eventos AS conexaoInstavel,
				p5.tempo_offline AS equipamentoOffline,
				p6.dias AS naoEnviaEventosDias,
				p7.qtd_eventos AS semVeiculosValidos,
				p8.qtd_eventos AS erroAtualizacaoSoftware,
				p9.qtd_eventos AS erroDIV,
				p10.qtd_eventos AS erroLeituraLote,
				p11.qtd_eventos AS erroPainelControlador,
				p12.qtd_eventos AS erroRespostaPolling,
				p14.qtd_eventos AS capturaIniciado,
				p15.desconexoes AS desconexoesDiv
			FROM local_vigente lv (nolock)
				INNER JOIN configuracao_equipamento_pista cep (nolock)
					ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
				LEFT JOIN status_tempo_real str (nolock)
					ON str.serie_equipamento = lv.serie_equipamento
				-- Esta informacao eh por pista, respeitar o LEFT JOIN		
				-- com a configuracao_equipamento_pista
				LEFT JOIN painel_ultima_infracao ui (nolock)
					ON ui.serie_equipamento = lv.serie_equipamento
					AND ui.id_pista = cep.id_pista
				LEFT JOIN painel_captura_travado p1 (nolock)
					ON p1.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_captura_excesso_eventos p2 (nolock)
					ON p2.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_falha_comunicacao_camera p3 (nolock)
					ON p3.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_conexao_instavel p4 (nolock)
					ON p4.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_equipamento_offline p5 (nolock)
					ON p5.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_nao_envia_eventos p6 (nolock)
					ON p6.serie_equipamento = lv.serie_equipamento
				-- Esta informacao eh por pista, respeitar o LEFT JOIN		
				-- com a configuracao_equipamento_pista
				LEFT JOIN painel_sem_veiculos_validos p7 (nolock)
					ON p7.serie_equipamento = lv.serie_equipamento
					AND p7.id_pista = cep.id_pista
				LEFT JOIN painel_erro_atualizacao_software p8 (nolock)
					ON p8.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_erro_no_div p9 (nolock)
					ON p9.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_erro_leitura_arquivo_lote p10 (nolock)
					ON p10.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_erro_funcionamento_painel_controlador p11 (nolock)
					ON p11.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_erro_resposta_polling p12 (nolock)
					ON p12.serie_equipamento = lv.serie_equipamento
				LEFT JOIN painel_ultima_deteccao p13 (nolock)
					ON p13.serie_equipamento = lv.serie_equipamento
					AND p13.id_pista = cep.id_pista
				LEFT JOIN painel_captura_iniciado p14 (nolock)
					ON p14.serie_equipamento = lv.serie_equipamento
				-- Esta informacao eh por pista, respeitar o LEFT JOIN		
				-- com a configuracao_equipamento_pista
				LEFT JOIN painel_div_desconexao p15 (nolock)
					ON p15.serie_equipamento = lv.serie_equipamento
					AND p15.id_pista = cep.id_pista
			WHERE	lv.data_inicio <= GETDATE() -- Equipamentos ativos 
				AND lv.desativado = 0

		COMMIT

	END TRY

	BEGIN CATCH

		IF (@@TRANCOUNT > 0)
			ROLLBACK

		EXEC spu_replica_erro
		
	END CATCH





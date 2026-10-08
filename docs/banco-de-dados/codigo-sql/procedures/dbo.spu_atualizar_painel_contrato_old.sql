



CREATE PROCEDURE [dbo].[spu_atualizar_painel_contrato_old]
AS

	-- insere os locais que não existem na tabela
	INSERT INTO painel_contrato 
	(
		dataGeracao,
		numeroSerie,
		idLocal,
		idPista,
		codigoFaixa,
		nomeFaixa
	)
		SELECT
			GETDATE(),
			lv.serie_equipamento AS [numeroSerie],
			lv.id_local,
			cep.id_pista AS [idPista],
			CASE WHEN (cep.cod_pista_alternativo = 0) THEN cep.id_pista ELSE cep.cod_pista_alternativo END AS [codigoFaixa],
			RTRIM(cep.nome_pista) AS [nomeFaixa]
		FROM local_vigente lv (nolock)
			JOIN configuracao_equipamento_pista cep (nolock)
				ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
			LEFT JOIN painel_contrato pc (nolock)
				ON	pc.numeroSerie = lv.serie_equipamento 
				AND pc.idPista = cep.id_pista
		WHERE	pc.numeroSerie IS NULL
			AND lv.data_inicio < GETDATE()
			AND lv.desativado = 0
			
	--GO		

	WAITFOR DELAY '00:00:01'

	/* atualizar ultima infracao ***************************************************************/
		DECLARE @ultimaInfracao TABLE 
		(
			serieEquipamento INT, 
			idPista INT, 
			ultima_data DATETIME
		)
	
		-- copia locais e pistas da tabela painel_contrato 
		INSERT INTO @ultimaInfracao 
			(serieEquipamento, idPista, ultima_data)
		SELECT 
			pc.numeroSerie, 
			idPista, 
			NULL
		FROM painel_contrato pc (nolock)
		
		-- atualiza os dados da última infração na tabela temporária
		UPDATE @ultimaInfracao 
			SET ultima_data = ui.data_ultima_infracao
		FROM painel_ultima_infracao ui
		WHERE	serieEquipamento = ui.serie_equipamento
			AND idPista = ui.id_pista 

		-- atualiza tabela painel_contrato		
		UPDATE painel_contrato with (rowlock) 
		SET dataUltimaInfracao = ui.ultima_data
			FROM @ultimaInfracao ui
		WHERE	painel_contrato.numeroSerie = ui.serieEquipamento
			AND painel_contrato.idPista = ui.idPista 
	/*********************************************************************************************/
		
	WAITFOR DELAY '00:00:01'

	-- atualiza ult. desconexao
	UPDATE painel_contrato with (rowlock) 
		SET dataUltimaDesconexao = NULL

	UPDATE painel_contrato with (rowlock) 
		SET dataUltimaDesconexao = ld.data_ultima_desconexao
	FROM 
		painel_local_desconectado ld (nolock)
	WHERE 
		painel_contrato.idLocal = ld.id_local
	--GO

	WAITFOR DELAY '00:00:01'

	-- atualizar ult. arquivo
	UPDATE painel_contrato with (rowlock) 
		SET dataUltimoArquivo = NULL

	UPDATE painel_contrato with (rowlock) 
		SET dataUltimoArquivo = ultArq.data_ultimo_arquivo
	FROM 
		painel_ultimo_arquivo ultArq (nolock)
	WHERE 
		painel_contrato.idLocal = ultArq.id_local
	--GO

	WAITFOR DELAY '00:00:01'
/* atualizar regra infracao desligada ***************************************************************/

	DECLARE @regraInfracaoDesabilitada TABLE 
		(
		serie_equipamento INT, 
		regraInfracaoDesabilitada INT
		)

	-- copia locais e pistas da tabela painel_contrato 
	INSERT INTO @regraInfracaoDesabilitada 
		(serie_equipamento, regraInfracaoDesabilitada)
	SELECT 
		pc.numeroSerie, 
		0
	FROM painel_contrato pc (nolock)
		
	-- atualiza os dados da última infração na tabela temporária
	UPDATE @regraInfracaoDesabilitada 
		SET regraInfracaoDesabilitada = 1
	WHERE serie_equipamento IN (SELECT DISTINCT
									lv.serie_equipamento
								FROM local_vigente lv (nolock)
									INNER JOIN configuracao_equipamento_regra_infracao ceri (nolock)
										ON ceri.id_configuracao_equipamento = lv.id_configuracao_equipamento
								WHERE
									ceri.ativo = 0
								)

	-- atualiza tabela painel_contrato		
	UPDATE painel_contrato with (rowlock) 
		SET regraInfracaoDesabilitada = rid.regraInfracaoDesabilitada
	FROM 
		@regraInfracaoDesabilitada rid
	WHERE 
		painel_contrato.numeroSerie = rid.serie_equipamento
/*********************************************************************************************/

	WAITFOR DELAY '00:00:01'

	-- atualizar MAC
	UPDATE painel_contrato with (rowlock) 
		SET dataMAC = NULL, 
			mac = NULL
	
	UPDATE painel_contrato with (rowlock) 
		SET dataMAC = ultMac.data_hora, 
			mac = ultMac.MAC
	FROM 
		ultimo_mac ultMac (nolock)
	WHERE 
		CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultMac.proprietario

	WAITFOR DELAY '00:00:01'

	-- atualizar ICCID
	UPDATE painel_contrato with (rowlock) 
		SET dataICCID = NULL, 
			iccid  = NULL

	UPDATE painel_contrato 
		SET dataICCID = ultIccid.data_hora, 
			iccid  = ultIccid.ICCID
	FROM
		ultimo_iccid ultIccid (nolock)
	WHERE 
		CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultIccid.proprietario

	WAITFOR DELAY '00:00:01'

	-- atualizar Status DIV
	UPDATE painel_contrato with (rowlock) 
		SET statusDiv = NULL

	UPDATE painel_contrato with (rowlock) 
		SET statusDiv = statusDiv.[status]
	FROM 
		[local_status_div] statusDiv WITH(NOLOCK)
	WHERE 
		painel_contrato.idLocal = statusDiv.id_local

	WAITFOR DELAY '00:00:01'

	-- atualizar imagens defeituosas
	UPDATE painel_contrato with (rowlock) 
		SET imagDefDia = NULL, 
			semRecPlacaDia = NULL

	UPDATE painel_contrato with (rowlock) 
	SET imagDefDia = 	CAST((	CASE WHEN imgDefDia.trafegoImagemCapturada > 0 
									THEN 
										(imgDefDia.trafegoImagemDef * 100.0) / (imgDefDia.trafegoImagemCapturada) 
									ELSE 
										0 
								END) AS INT),
		semRecPlacaDia = CAST((	CASE WHEN imgDefDia.trafegoOCRprocessado > 0 
									THEN 
										(imgDefDia.sem_placa_reconhecida * 100.0) / (imgDefDia.trafegoOCRprocessado) 
									ELSE 
										0 
								END) AS INT)
	FROM 
		fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '07:10', '17:20') imgDefDia 
	WHERE	painel_contrato.idLocal = imgDefDia.id_local 
		AND painel_contrato.idPista = imgDefDia.id_pista


	WAITFOR DELAY '00:00:01'

	UPDATE painel_contrato with (rowlock) 
		SET imagDefNoite = NULL, 
			semRecPlacaNoite = NULL

	UPDATE painel_contrato with (rowlock) 
	SET imagDefNoite = 	CAST((	CASE WHEN imgDefNoite.trafegoImagemCapturada > 0 
									THEN 
										(imgDefNoite.trafegoImagemDef * 100.0) / (imgDefNoite.trafegoImagemCapturada) 
									ELSE 
										0 
								END) AS INT),
		semRecPlacaNoite = 	CAST((	CASE WHEN imgDefNoite.trafegoOCRprocessado > 0 
										THEN 
											(imgDefNoite.sem_placa_reconhecida * 100.0) / (imgDefNoite.trafegoOCRprocessado) 
										ELSE 
											0 
									END) AS INT)
	FROM 
		fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '20:10', '05:30') imgDefNoite
	WHERE	painel_contrato.idLocal = imgDefNoite.id_local 
		AND painel_contrato.idPista = imgDefNoite.id_pista
	
	WAITFOR DELAY '00:00:01'
	
	/** Transição Agendas *********************************************************************************/

	DECLARE @dadosImgTrans TABLE 
	(
		id_local INT, 
		id_pista INT, 
		trafegoImagemCapturada INT,
		trafegoOCRProcessado INT,
		trafegoImagemDef INT,
		semPlacaReconhecida INT
	)

	INSERT INTO @dadosImgTrans 
		(id_local, id_pista, trafegoImagemCapturada, trafegoOCRprocessado, trafegoImagemDef, semPlacaReconhecida)
	SELECT
		id_local, id_pista, trafegoImagemCapturada, trafegoOCRprocessado, trafegoImagemDef, sem_placa_reconhecida
	FROM 
		fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '05:30', '07:10') imgDefNoite

	INSERT INTO @dadosImgTrans 
		(id_local, id_pista, trafegoImagemCapturada, trafegoOCRprocessado, trafegoImagemDef, semPlacaReconhecida)
	SELECT
		id_local, id_pista, trafegoImagemCapturada, trafegoOCRprocessado, trafegoImagemDef, sem_placa_reconhecida
	FROM 
		fcn_LocalImagensDefeituosa( GETDATE() - 1, GETDATE() , '17:20', '20:10') imgDefNoite
		
	UPDATE painel_contrato with (rowlock) 
		SET imagDefTrans = NULL, 
			semRecPlacaTrans = NULL

	UPDATE painel_contrato with (rowlock) 
		SET imagDefTrans		= ImgsTrans.imagDefTrans,
			semRecPlacaTrans	= ImgsTrans.semRecPlacaTrans 
	FROM (	SELECT
				id_local, id_pista,
				imagDefTrans = 	CAST((	CASE WHEN SUM(trafegoImagemCapturada) > 0 
											THEN 
												(SUM(trafegoImagemDef) * 100.0) / SUM(trafegoImagemCapturada) 
											ELSE 
												0 
										END) AS INT),
				semRecPlacaTrans = CAST((	CASE WHEN SUM(trafegoOCRprocessado) > 0 
												THEN 
													(SUM(semPlacaReconhecida) * 100.0) / SUM(trafegoOCRprocessado) 
												ELSE 
													0 
											END) AS INT)
			FROM 
				@dadosImgTrans
			GROUP BY 
				id_local, id_pista
		) AS ImgsTrans
	WHERE	painel_contrato.idLocal = ImgsTrans.id_local 
		AND painel_contrato.idPista = ImgsTrans.id_pista

	/***********************************************************************************/
	

	WAITFOR DELAY '00:00:01'

	UPDATE painel_contrato with (rowlock) 
		SET percOffline = NULL

	UPDATE painel_contrato with (rowlock) 
	SET 
		percOffline = (tempoDesconexao.tempoSegundos * 100) / DATEDIFF(SECOND, GETDATE() -7, GETDATE()) -- número de dias para divisão, deve ser igual ao consultado
	FROM (	SELECT 
				id_local, SUM(tempo_evento_segundos) as tempoSegundos
			FROM 
				[fcn_LocalEventosConexao]( GETDATE() - 7, GETDATE() ) 
			WHERE 
				[status] = 2
			GROUP BY 
				id_local
		) as tempoDesconexao
	WHERE 
		painel_contrato.idLocal = tempoDesconexao.id_local 

	UPDATE painel_contrato with (rowlock) 
		SET dataUltimaSemaforoOK = NULL

	UPDATE painel_contrato with (rowlock) 
		SET dataUltimaSemaforoOK = ultSequenciaSemaforo.ultima_data
	FROM 
		fcn_maxDataHoraEventoPorProprietario(44) AS ultSequenciaSemaforo
	WHERE 
		CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultSequenciaSemaforo.proprietario

	WAITFOR DELAY '00:00:01'

	-- atualizar ultima agenda aplicada
	BEGIN TRANSACTION

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimaAgenda = NULL

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimaAgenda = ultAgenda.data_ultima_agenda
		FROM 
			painel_ultima_camera_carregada AS ultAgenda (nolock)
		WHERE 
			CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultAgenda.proprietario
	
	COMMIT
	
	WAITFOR DELAY '00:00:01'

	-- atualizar ultimo sincronismo
	BEGIN TRANSACTION

		DECLARE @sincHorario TABLE 
		(
			proprietario CHAR(7), 
			ultima_data DATETIME
		)
	
		INSERT INTO @sincHorario 
			(proprietario, ultima_data)
		SELECT 
			ultSincHorario.proprietario, 
			ultima_data
		FROM 
			fcn_maxDataHoraEventoPorProprietario(38) AS ultSincHorario

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimoSincHorario = ultSincHorario.ultima_data
		FROM 
			@sincHorario ultSincHorario
		WHERE 
			CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultSincHorario.proprietario

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimoSincHorario = NULL
		WHERE CAST(painel_contrato.numeroSerie AS CHAR(7)) NOT IN (SELECT proprietario 
																		FROM @sincHorario)

	COMMIT

	WAITFOR DELAY '00:00:01'
	
	-- atualizar numero Vezes Captura Iniciado
	UPDATE painel_contrato with (rowlock) 
		SET	numeroVezesCapturaIniciado = NULL

	UPDATE painel_contrato with (rowlock) 
		SET	numeroVezesCapturaIniciado = capturaIniciado.Total
	FROM 
		fcn_getAlertaEvento(2, GETDATE() - 1, 0) AS capturaIniciado
	WHERE 
		CAST(painel_contrato.numeroSerie AS CHAR(7)) = capturaIniciado.proprietario

	WAITFOR DELAY '00:00:01'


	-- ult. atualização do banco de dados
	BEGIN TRANSACTION

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimaAtualizacaoBD = NULL

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimaAtualizacaoBD = ultUpdateDB.ultima_data
		FROM 
			fcn_maxDataHoraEventoPorProprietario(54) AS ultUpdateDB
		WHERE	CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultUpdateDB.proprietario
			AND ultUpdateDB.proprietario NOT IN ( -- somente os equip. que fazem busca no banco de dados 
													SELECT 
														CAST(serie_equipamento AS CHAR(7)) 
													FROM 
														local_parametro_adicional_data_search (nolock) 
													WHERE 
														database_search_ativo = 0
												)
	COMMIT

	WAITFOR DELAY '00:00:01'

	-- data da ult. manutenção no equipamento.
	UPDATE painel_contrato with (rowlock) 
		SET dataUltimaManutencao = NULL

	UPDATE painel_contrato with (rowlock) 
		SET dataUltimaManutencao = ultManutencao.ultima_data
	FROM 
		fcn_maxDataHoraEventoPorProprietario(15) AS ultManutencao
	WHERE 
		CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultManutencao.proprietario

	WAITFOR DELAY '00:00:01'

	-- data da ult. manutenção no equipamento.
	BEGIN TRANSACTION

		UPDATE painel_contrato with (rowlock) 
			SET dataUltimaDifRelogioServidor = NULL

		UPDATE painel_contrato 
			SET dataUltimaDifRelogioServidor = ultDifRelogio.ultima_data
		FROM 
			fcn_maxDataHoraEventoPorProprietario(51) AS ultDifRelogio
		WHERE 
			CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultDifRelogio.proprietario

	COMMIT
	
	WAITFOR DELAY '00:00:01'
		
	-- STATUS ConfigEquip
	BEGIN TRANSACTION

		UPDATE painel_contrato with (rowlock) 
			SET statusConfigEquip = NULL

		UPDATE painel_contrato with (rowlock) 
			SET statusConfigEquip = statusConfigEquip.statusConfigEquip
		FROM(	SELECT
					lv.serie_equipamento,
					(CASE 
						WHEN	MAX(configEquipOK.ultima_data) IS NOT NULL 
							AND MAX(configEquipOK.ultima_data) > COALESCE( MAX(configEquipCaptura.ultima_data), 0)
							AND MAX(configEquipOK.ultima_data) > COALESCE(MAX(configEquipGTW.ultima_data) , 0) 
							THEN 
								1
						WHEN	MAX(configEquipOK.ultima_data) IS NULL 
							AND MAX(configEquipCaptura.ultima_data) IS NULL
							AND MAX(configEquipGTW.ultima_data) IS NULL
							THEN 
								NULL
						ELSE 
							0 
					END
					) as statusConfigEquip
			FROM
				local_vigente lv (nolock)	
					-- CSX_EVENT_CONFIGEQUIP_NEWER_IN_CAPTURA
					LEFT JOIN fcn_maxDataHoraEventoPorProprietario(5001) configEquipCaptura 
						ON CAST(lv.serie_equipamento AS CHAR(7)) = configEquipCaptura.proprietario
					-- CSX_EVENT_CONFIGEQUIP_NEWER_IN_GTW
					LEFT JOIN fcn_maxDataHoraEventoPorProprietario(5002) configEquipGTW 
						ON CAST(lv.serie_equipamento AS CHAR(7)) = configEquipGTW.proprietario
					-- CSX_EVENT_CONFIGEQUIP_OK
					LEFT JOIN fcn_maxDataHoraEventoPorProprietario(5003) configEquipOK 
						ON CAST(lv.serie_equipamento AS CHAR(7)) = configEquipOK.proprietario
			WHERE	lv.data_inicio < GETDATE()
				AND lv.desativado = 0
			GROUP BY
				lv.serie_equipamento	
			) AS statusConfigEquip 
		WHERE 
			painel_contrato.numeroSerie = statusConfigEquip.serie_equipamento
	
	COMMIT

	WAITFOR DELAY '00:00:01'

	-- ultimo evento geral
	UPDATE painel_contrato with (rowlock) 
		SET dataUltimoEvento = NULL

	UPDATE painel_contrato with (rowlock)  
		SET dataUltimoEvento = ultEvento.ultima_data
	FROM (	SELECT
				ep.proprietario
				,MAX(e.data_hora) as [ultima_data]
			FROM eventos_csx e (nolock)
				INNER JOIN eventos_csx_desc_proprietario ep (nolock)
					ON ep.id_proprietario = e.id_proprietario
			WHERE
				data_hora < GETDATE()					
			GROUP BY
				ep.proprietario		
			) AS ultEvento 
	WHERE 
		CAST(painel_contrato.numeroSerie AS CHAR(7)) = ultEvento.proprietario

	UPDATE painel_contrato with (rowlock)  
		SET dataGeracao = GETDATE()
		
	/****************************************************************************************
	 * desativa os alertas marcados como falso positivo
	 ****************************************************************************************/
	UPDATE painel_contrato_alerta
		SET ativo = 0, 
		data_atualizacao = GETDATE()
	FROM 
		painel_contrato_alerta pca with (rowlock) 
	WHERE	pca.ativo = 1
		AND pca.motivo_falso_positivo = 1
		AND DATEPART(DAY, pca.data_inclusao) <> DATEPART(DAY, GETDATE())
		
	/****************************************************************************************
	 * criar alertas do painel_contrato que ainda não existem
	 ****************************************************************************************/
	INSERT INTO painel_contrato_alerta with (rowlock) 
		(serie_equipamento, id_pista) 
	SELECT 
		alerta.numeroSerie , alerta.idPista 
	FROM dbo.fcn_getRelatorioPrioridadeManutencao(NULL, NULL) alerta
		LEFT JOIN painel_contrato_alerta pca (nolock)
			ON	pca.serie_equipamento = alerta.numeroSerie 
			AND pca.id_pista = alerta.idPista
			AND pca.ativo = 1
	WHERE
		pca.id_painel_contrato_alerta IS NULL
		


	/****************************************************************************************
	 * desativa os alertas que não existem mais
	 ****************************************************************************************/
	--UPDATE painel_contrato_alerta with (rowlock) 
	--SET	ativo = 0, 
	--		data_atualizacao = GETDATE()
	--FROM painel_contrato_alerta pca (nolock)
	--	LEFT JOIN dbo.fcn_getRelatorioPrioridadeManutencao(NULL, NULL) alerta
	--		ON	alerta.numeroSerie = pca.serie_equipamento 
	--		AND pca.id_pista = alerta.idPista
	--WHERE	alerta.numeroSerie IS NULL
	--	AND pca.ativo = 1











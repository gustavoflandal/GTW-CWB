
CREATE   PROCEDURE [muralha].[analise_automatica_correlacionamento]
AS
BEGIN

	SET NOCOUNT ON;

	--//////////////////////////////////////////////////////--
	--//			SEÇÃO DE "LIMPEZA"					  //--
	--//////////////////////////////////////////////////////--

	-- DROP DAS TABELAS TEMPORÁRIAS
	DROP TABLE IF EXISTS #passagens_alvo;
	DROP TABLE IF EXISTS #passagens_correlacionadas;

	-- EXCLUIR PASSAGENS ANTIGAS ANTES DE FAZER NOVAS INSERÇÕES
	DELETE FROM muralha.correlacionamento_automatico_processamento
	WHERE data_registro < CAST(DATEADD(day, -60, GETDATE()) AS DATE)

	--//////////////////////////////////////////////////////--
	--//	DECLARAÇÃO DAS VARIÁVEIS					  //--
	--//////////////////////////////////////////////////////--

	DECLARE @data_inicio DATETIME = CAST(DATEADD(day, -1, GETDATE()) AS DATE);
	DECLARE @data_fim DATETIME = CAST(GETDATE() AS DATE);

	DECLARE @corr_fraco INT;
	DECLARE @corr_medio INT;
	DECLARE @corr_forte INT;

	SELECT @corr_fraco = CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_BAIXA';
	SELECT @corr_medio = CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_MEDIA';
	SELECT @corr_forte  = CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'VEICULO_CORRELACIONADO_QTD_PASSAGENS_ALTA';


	--//////////////////////////////////////////////////////--
	--// SEÇÃO PARA BUSCAR AS PASSAGENS DAS PLACAS ALVO   //--
	--//////////////////////////////////////////////////////--

	SELECT
		vtr.id, 
		vtr.placa, 
		DATEADD(minute, -3, vtr.data) as data_anterior, 
		vtr.data, 
		DATEADD(minute, 3, vtr.data) as data_posterior,
		vtr.id_local
	INTO #passagens_alvo
	FROM muralha.veiculo_tempo_real vtr
	JOIN muralha.registro_fato_veiculo rfv
		ON rfv.placa = vtr.placa
	JOIN muralha.registro_fato rf
		ON rf.id = rfv.id_registro_fato
	WHERE rf.id_status = 1 --> REGISTROS DE FATO AINDA ATIVOS
	GROUP BY vtr.id, vtr.placa, vtr.data, vtr.id_local

	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--
	--// SEÇÃO PARA BUSCAR PASSAGENS DE VEÍCULOS QUE TENHAM PASSADO NO MESMO LOCAL DAS PLACAS ALVO				      //--
    --// ENQUANTO CONSIDERANDO UM ESPAÇO DE TEMPO DE 3 MINUTOS PRA MAIS E PRA MENOS DA DATA DE PASSAGEM DA PLACA ALVO //--
	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--

	SELECT -- TOP 10
		pa.id as id_passagem_alvo,
		pa.placa as placa_alvo,
		vtr.id as id_passagem, 
		vtr.placa as placa_correlacionada,
		pa.data_anterior as data_inicio_alvo,
		pa.data_posterior as data_fim_alvo,
		vtr.data as data_correlacionada,
		pa.id_local as local_alvo,
		vtr.id_local as local_correlacionada
	INTO #passagens_correlacionadas
	FROM muralha.veiculo_tempo_real vtr
	INNER JOIN #passagens_alvo pa
		ON (vtr.id_local = pa.id_local) 
			AND vtr.placa <> pa.placa 
			AND vtr.data > pa.data_anterior 
			AND vtr.data < pa.data_posterior 
	WHERE 
		vtr.data >= @data_inicio 
		AND vtr.data <= @data_fim

	-- Um correlacionamento entre placas nunca deve se repetir. 
	-- Ex: Uma mesma dupla de placas nunca poderá repetir as passagens correlacionadas.
	-- A data de passagem da placa correlacionada deve estar dentro do período de data da placa alvo

	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--
	--// SEÇÃO PARA INSERIR AS PASSAGENS ENCONTRADAS EM UMA TABELA CICLADA (REGISTROS DELETADOS A CADA 60 DIAS)       //--
	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--

	INSERT INTO muralha.correlacionamento_automatico_processamento(
		placa_alvo,
		id_passagem_placa_alvo,
		placa_correlacionada,
		id_passagem_placa_correlacionada,
		data_registro
	)
	SELECT
		sub.placa_alvo, -- Placa Alvo -> Buscada a partir do registro de fato
		sub.id_passagem_alvo, -- Id da passagem da placa alvo
		sub.placa_correlacionada, -- Placa Correlacionada -> Que foi encontrada trafegando "junto" a placa alvo
		sub.id_passagem, -- Id da passagem da placa correlacionada
		GETDATE()
	FROM ( 
		SELECT 
			pc.*,
			COUNT(*) OVER(PARTITION BY pc.placa_alvo, pc.placa_correlacionada) as total_encontros
		FROM #passagens_correlacionadas pc
		) sub
	WHERE NOT EXISTS (
		-- VALIDAÇÃO PARA NÃO REPETIR A MESMA PASSAGEM
		SELECT 1 
		FROM muralha.correlacionamento_automatico_processamento cap
		WHERE 
			cap.id_passagem_placa_alvo = sub.id_passagem_alvo
			AND cap.id_passagem_placa_correlacionada = sub.id_passagem
	)

	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--
	--// SEÇÃO PARA CRIAR O CORRELACIONAMENTO DE PLACAS QUE TENHAM 3 (OU MAIS) PASSSAGENS "ANDANDO JUNTAS"			  //--
	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--

	INSERT INTO muralha.correlacionamento_automatico (placa_alvo, placa_correlacionada, data_cadastro)
	SELECT DISTINCT 
		pc.placa_alvo, 
		pc.placa_correlacionada, 
		GETDATE()
	FROM #passagens_correlacionadas pc
	LEFT JOIN muralha.correlacionamento_automatico ca
		ON ca.placa_alvo = pc.placa_alvo AND ca.placa_correlacionada = pc.placa_correlacionada
	LEFT JOIN muralha.correlacionamento_automatico_placa cap
		ON cap.id_correlacionamento = ca.id
	GROUP BY pc.placa_alvo, pc.placa_correlacionada
	HAVING COUNT(*) >= 3
	AND NOT EXISTS (
		-- EVITA CRIAR NOVAMENTE O MESMO CORRELACIONAMENTO
		SELECT 1 FROM muralha.correlacionamento_automatico ca
		WHERE ca.placa_alvo = pc.placa_alvo AND ca.placa_correlacionada = pc.placa_correlacionada
	)
	AND pc.placa_alvo != pc.placa_correlacionada;

	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--
	--// SEÇÃO PARA REGISTRAR DE MANEIRA INCREMENTAL AS PASSAGENS DE PLACAS QUE TENHAM UM CORRELACIONAMENTO  		  //--
	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--

	INSERT INTO muralha.correlacionamento_automatico_placa (
		id_correlacionamento, 
		id_passagem_placa_alvo, 
		id_passagem_placa_correlacionada
	)
	SELECT DISTINCT
		ca.id,
		cap.id_passagem_placa_alvo,
		cap.id_passagem_placa_correlacionada
	FROM muralha.correlacionamento_automatico_processamento cap
	INNER JOIN muralha.correlacionamento_automatico ca
		ON ca.placa_alvo = cap.placa_alvo 
		AND ca.placa_correlacionada = cap.placa_correlacionada
	WHERE NOT EXISTS (
		SELECT 1 
		FROM muralha.correlacionamento_automatico_placa capp
		WHERE capp.id_correlacionamento = ca.id
		  AND capp.id_passagem_placa_alvo = cap.id_passagem_placa_alvo
		  AND capp.id_passagem_placa_correlacionada = cap.id_passagem_placa_correlacionada
	)
	AND NOT EXISTS (
		SELECT 1
		FROM muralha.correlacionamento_automatico_placa_invalida capi
		WHERE capi.id_passagem_placa_alvo = cap.id_passagem_placa_alvo 
		  AND capi.id_passagem_placa_correlacionada = cap.id_passagem_placa_correlacionada
	)
	AND ca.status != 3;

	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--
	--// SEÇÃO PARA DEFINIR O GRAU DO CORRELACIONAMENTO																  //--
	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--

	WITH totalPassagens AS (
		SELECT ca.id as id_correlacionamento, count(ca.id) as total_passagens
		FROM muralha.correlacionamento_automatico ca
		JOIN muralha.correlacionamento_automatico_placa cap
			ON cap.id_correlacionamento = ca.id
		GROUP BY ca.id, ca.placa_alvo, ca.placa_correlacionada
	) UPDATE ca
		set ca.nivel_correlacao =
			CASE 
				WHEN total_passagens >= @corr_forte THEN 'FORTE'
				WHEN total_passagens < @corr_forte AND total_passagens >= @corr_medio THEN 'MEDIO'
				ELSE 'BAIXA'
			END
		FROM muralha.correlacionamento_automatico ca
		INNER JOIN totalPassagens tp
			ON ca.id = tp.id_correlacionamento	

	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--
	--// REMOVENDO AS TABELAS TEMPORÁRIAS																			  //--
	--//////////////////////////////////////////////////////////////////////////////////////////////////////////////////--

	DROP TABLE IF EXISTS #passagens_alvo;
	DROP TABLE IF EXISTS #passagens_correlacionadas;

END

-- exec muralha.analise_automatica_correlacionamento


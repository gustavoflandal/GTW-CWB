CREATE PROCEDURE [dbo].[spu_getModoOperacaoPPV]
AS


	/*  CODIGOS DE OCORRENCIAS
		1	Modo de operação coercitiva
		2	Modo de operação educativa
		3	Posto de Pesagem Fechado
		4	Liberação de veículos na Balança Precisão
		5	Liberação de veículos na Balança Seletiva
		6	Informação operacional de controle	  
	*/

	DECLARE 
			@COERCITIVA INT, 
			@COERCITIVA_FILA INT,
			@EDUCATIVA INT,
			@FECHADO INT,
			@LIBERACAO_PRECISAO INT;
		

	SET @COERCITIVA				= 1;
	SET @EDUCATIVA				= 2;
	SET @FECHADO				= 3;
	SET @LIBERACAO_PRECISAO		= 4;
	SET @COERCITIVA_FILA		= 5;


	SELECT 

		Resultado.tp_equipamento,
		Resultado.tp_operacao_equipamento,
		Resultado.id_ocorrencia,
		Resultado.id_SAI,
		Resultado.id_tpocorrencia,
		Resultado.tipo_ocorrencia,
		Resultado.data,
		Resultado.observacoes,
		Resultado.id_veiculo_interno	 

	FROM 
	(

		SELECT 
				'GERACAO_INFRACAO' as tp_equipamento,
				CASE 
					WHEN pto.id_tpocorrencia in (@COERCITIVA )					THEN 'BALANCA_ABERTA'
					WHEN pto.id_tpocorrencia in (@COERCITIVA_FILA )				THEN 'BALANCA_ABERTA_COM_FILA'
					WHEN pto.id_tpocorrencia in (@FECHADO, @EDUCATIVA)			THEN 'BALANCA_FECHADA'
				END as tp_operacao_equipamento,
				po.id_ocorrencia,
				po.id_SAI,
				pto.id_tpocorrencia,
				pto.descricao AS tipo_ocorrencia,
				po.data,
				po.observacoes,
				po.id_veiculo_interno
		FROM   ppv_ocorrencias po (NOLOCK)
				INNER JOIN ppv_tp_ocorrencia pto (NOLOCK)
					ON  pto.id_tpocorrencia = po.id_tpocorrencia
				INNER JOIN (
							SELECT MAX(po_aux.id_ocorrencia) AS id_ocorrencia
							FROM   ppv_ocorrencias po_aux (NOLOCK)
							WHERE  po_aux.id_tpocorrencia IN ( @COERCITIVA, @EDUCATIVA, @FECHADO, @COERCITIVA_FILA )
				) modo_geral
					ON  modo_geral.id_ocorrencia = po.id_ocorrencia
		WHERE  po.id_tpocorrencia IN ( @COERCITIVA, @EDUCATIVA, @FECHADO, @COERCITIVA_FILA  )

		UNION ALL


		SELECT 
				'MENSAGEM_PMV' as tp_equipamento,

				CASE 
					WHEN pto.id_tpocorrencia in ( @COERCITIVA, @EDUCATIVA )	THEN 'BALANCA_ABERTA'
					WHEN pto.id_tpocorrencia in ( @COERCITIVA_FILA )		THEN 'BALANCA_ABERTA_COM_FILA'
					WHEN pto.id_tpocorrencia in ( @FECHADO)					THEN 'BALANCA_FECHADA'					
				END as tp_operacao_equipamento,

				po.id_ocorrencia,
				po.id_SAI,
				pto.id_tpocorrencia,

				pto.descricao AS tipo_ocorrencia,
				po.data,
				po.observacoes,
				po.id_veiculo_interno
		FROM   ppv_ocorrencias po (NOLOCK)
				INNER JOIN ppv_tp_ocorrencia pto (NOLOCK)
					ON  pto.id_tpocorrencia = po.id_tpocorrencia
				INNER JOIN (
							SELECT MAX(po_aux.id_ocorrencia) AS id_ocorrencia
							FROM   ppv_ocorrencias po_aux (NOLOCK)
							WHERE  po_aux.id_tpocorrencia IN ( @COERCITIVA, @EDUCATIVA, @FECHADO, @COERCITIVA_FILA )
				) modo_geral
					ON  modo_geral.id_ocorrencia = po.id_ocorrencia
		WHERE  po.id_tpocorrencia IN ( @COERCITIVA, @EDUCATIVA, @FECHADO, @COERCITIVA_FILA )

		UNION ALL

		SELECT 
				'BALANCA_PRECISAO' as tp_equipamento,
				CASE 
					WHEN pto.id_tpocorrencia in ( @COERCITIVA, @COERCITIVA_FILA, @EDUCATIVA )			THEN 'BALANCA_ABERTA'
					WHEN pto.id_tpocorrencia in ( @FECHADO, @LIBERACAO_PRECISAO )						THEN 'BALANCA_LIBERACAO_VEICULOS'
				END as tp_operacao_equipamento,
				po.id_ocorrencia,
				po.id_SAI,
				pto.id_tpocorrencia,
				pto.descricao AS tipo_ocorrencia,
				po.data,
				po.observacoes,
				po.id_veiculo_interno
		FROM   ppv_ocorrencias po (NOLOCK)
				INNER JOIN ppv_tp_ocorrencia pto (NOLOCK)
					ON  pto.id_tpocorrencia = po.id_tpocorrencia
				INNER JOIN (
							SELECT MAX(po_aux.id_ocorrencia) AS id_ocorrencia
							FROM   ppv_ocorrencias po_aux (NOLOCK)
							WHERE  po_aux.id_tpocorrencia IN ( @COERCITIVA, @COERCITIVA_FILA, @EDUCATIVA, @FECHADO, @LIBERACAO_PRECISAO )
				) modo_geral
					ON  modo_geral.id_ocorrencia = po.id_ocorrencia
		WHERE  po.id_tpocorrencia IN ( @COERCITIVA,@COERCITIVA_FILA,  @EDUCATIVA, @FECHADO, @LIBERACAO_PRECISAO )



	) AS Resultado

CREATE   PROCEDURE [muralha].[spu_obtem_ocorrencias] 

AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#ocorrencias') IS NOT NULL
        DROP TABLE #ocorrencias;

    CREATE TABLE #ocorrencias (
        id VARCHAR(36),
        id_registro_fato INT NULL,
        id_alerta UNIQUEIDENTIFIER NULL,
        protocolo VARCHAR(50) NULL,
        id_origem_registro INT,
        prioridade INT,
        tipo VARCHAR(255),
        status VARCHAR(255),
        data DATETIME,
        data_criacao DATETIME,
        descricao VARCHAR(MAX),
        placa VARCHAR(50),
        observacao VARCHAR(MAX),
        data_encerramento DATETIME,
        id_situacao_envio INT NULL,
        id_atendimento INT NULL,
        id_guarnicao INT NULL,
        tem_boletim INT NULL,
		id_local INT NULL,
		endereco_alerta VARCHAR(MAX),
		endereco_local_evento VARCHAR(MAX)
    );

    -- Inserção do primeiro SELECT (Ocorrências de alerta)
    INSERT INTO #ocorrencias
    SELECT DISTINCT
        CONVERT(VARCHAR(36), o.id) AS id,
        cvm.id_registro_fato AS id_registro_fato,
        o.id_alerta,
        aten.protocolo,
        1 AS id_origem_registro,
        3 AS prioridade,
        tao.tipo,
        COALESCE(atenS.descricao, '-') AS status,
        o.data,
        aten.data_criacao,
        cvm.descricao,
        COALESCE(cvm.placa, '-') AS placa,
        a.observacao,
        aten.data_encerramento,
        aei.id_situacao,
        aten.id,
        aei.id_guarnicao,
        rf.tem_boletim,
		lv.id_local,
		lv.nome AS endereco_alerta,
		CONCAT(            
            'CEP: ', COALESCE(rfe.cep, ''),
            ', Bairro: ', COALESCE(rfe.bairro, ''),
            ', Rua: ', COALESCE(rfe.rua, ''),
            ', Nº: ', COALESCE(rfe.numero, ''),
            CASE WHEN rfe.complemento IS NOT NULL THEN CONCAT(', ', rfe.complemento) ELSE '' END
        ) AS endereco_local_evento
    FROM muralha.ocorrencia o
    INNER JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = o.id_tipo_alerta_ocorrencia
    LEFT JOIN muralha.alerta a ON a.id = o.id_alerta
	LEFT JOIN muralha.alerta_veiculo av on av.id_alerta = a.id
	LEFT JOIN muralha.veiculo_tempo_real vtr on vtr.id = av.id_veiculo_tempo_real
	LEFT JOIN local_vigente lv on lv.id_local = vtr.id_local
    LEFT JOIN muralha.cad_veiculo_monitorado cvm ON cvm.id = a.id_cad_veiculo_monitorado
    JOIN muralha.registro_fato rf ON cvm.id_registro_fato = rf.id   
    LEFT JOIN muralha.atendimento aten ON aten.id_ocorrencia = o.id
    LEFT JOIN muralha.atendimento_situacao atenS ON atenS.id = aten.id_situacao	
		

    OUTER APPLY (
        SELECT TOP 1 * 
        FROM muralha.atendimento_guarnicao ag
        WHERE ag.id_atendimento = aten.id
        ORDER BY ag.id DESC
    ) aei
	
	OUTER APPLY(
		SELECT TOP 1 * FROM muralha.registro_fato_endereco rfe		
		WHERE rfe.id_registro_fato = cvm.id_registro_fato
		ORDER BY rfe.id ASC
	)rfe

    WHERE o.permite_atendimento = 1 AND cvm.id_registro_fato IS NOT NULL;

    -- Segundo SELECT (Boletins)
    INSERT INTO #ocorrencias
    SELECT
        CONVERT(VARCHAR(36), b.id) AS id,
        rf.id AS id_registro_fato,
        NULL AS id_alerta,
        aten.protocolo,
        2 AS id_origem_registro,
        1 AS prioridade,
        rft.tipo_desc AS tipo,
        COALESCE(atenS.descricao, '-') AS status,
        b.data_criacao,
        aten.data_criacao,
        b.detalhamento,
        '-' AS placa,
        '' AS observacao,
        aten.data_encerramento,
        aei.id_situacao,
        aten.id,
        aei.id_guarnicao,
        rf.tem_boletim,
		NULL as id_local,
		NULL AS endereco_alerta,
		CONCAT(            
            'CEP: ', COALESCE(rfe.cep, ''),
            ', Bairro: ', COALESCE(rfe.bairro, ''),
            ', Rua: ', COALESCE(rfe.rua, ''),
            ', Nº: ', COALESCE(rfe.numero, ''),
            CASE WHEN rfe.complemento IS NOT NULL THEN CONCAT(', ', rfe.complemento) ELSE '' END
        ) AS endereco_local_evento
    FROM muralha.registro_fato rf
    JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo
    JOIN muralha.boletim b ON b.id_registro_fato = rf.id
    LEFT JOIN muralha.atendimento aten ON aten.id_registro_fato = rf.id
    LEFT JOIN muralha.atendimento_situacao atenS ON atenS.id = aten.id_situacao
    OUTER APPLY (
        SELECT TOP 1 * 
        FROM muralha.atendimento_guarnicao ag
        WHERE ag.id_atendimento = aten.id
        ORDER BY ag.id DESC
    ) aei

	OUTER APPLY (
		SELECT TOP 1 rfe.*
		FROM muralha.registro_fato_endereco rfe
		WHERE rfe.id_registro_fato = rf.id
		ORDER BY rfe.id ASC
	) rfe

    WHERE b.permite_atendimento = 1;

    -- Terceiro SELECT (Ocorrência Ligação)
    INSERT INTO #ocorrencias
    SELECT
        CONVERT(VARCHAR(36), f.id) AS id,
        rf.id AS id_registro_fato,
        NULL AS id_alerta,
        aten.protocolo,
        3 AS id_origem_registro,
        2 AS prioridade,
        rft.tipo_desc AS tipo,
        COALESCE(atenS.descricao, '-') AS status,
        f.data_hora_evento,
        aten.data_criacao,
        f.detalhamento,
        '-' AS placa,
        '' AS observacao,
        aten.data_encerramento,
        aei.id_situacao,
        aten.id,
        aei.id_guarnicao,
		NULL as id_local,
        rf.tem_boletim,
		NULL AS endereco_alerta,
		CONCAT(            
            'CEP: ', COALESCE(rfe.cep, ''),
            ', Bairro: ', COALESCE(rfe.bairro, ''),
            ', Rua: ', COALESCE(rfe.rua, ''),
            ', Nº: ', COALESCE(rfe.numero, ''),
            CASE WHEN rfe.complemento IS NOT NULL THEN CONCAT(', ', rfe.complemento) ELSE '' END
        ) AS endereco_local_evento
    FROM muralha.registro_fato rf
    JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo
    JOIN muralha.fato f ON f.id_registro_fato = rf.id
    LEFT JOIN muralha.atendimento aten ON aten.id_registro_fato = rf.id
    LEFT JOIN muralha.atendimento_situacao atenS ON atenS.id = aten.id_situacao
    OUTER APPLY (
        SELECT TOP 1 * 
        FROM muralha.atendimento_guarnicao ag
        WHERE ag.id_atendimento = aten.id
        ORDER BY ag.id DESC
    ) aei

	OUTER APPLY (
		SELECT TOP 1 rfe.*
		FROM muralha.registro_fato_endereco rfe
		WHERE rfe.id_registro_fato = rf.id
		ORDER BY rfe.id ASC
	) rfe
    WHERE f.permite_atendimento = 1;    

    SELECT *
    FROM #ocorrencias
    ORDER BY status, prioridade, data, data_criacao DESC;

END;



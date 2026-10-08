/*
===============================================================================
Nome da Rotina: [muralha].[spu_correlacionamento_placas]

Descrição:
    Correlação de placas por co-ocorrência no mesmo ponto (PCL) dentro de uma 
    janela temporal com enriquecimento de dados complementares.
    
    Esta rotina utiliza spu_correlacionamento_placas_base como foundation e 
    adiciona informações enriquecidas como tipo de veículo, tipos de alerta,
    tipos de registro de fato e análise de manchas comportamentais.

Formato de Saída:
    - placa:                VARCHAR(10)   -- Placa informada (passagens=0) + correlacionadas
    - passagens:            BIGINT        -- 0 para informada, N para correlacionadas
    - incidencia:           CHAR(1)       -- NULL para informada, F/M/A para correlacionadas
    - monitorado:           BIT           -- Está no cadastro de veículos monitorados
    - alerta:               BIT           -- Possui alertas ativos
    - boletim:              BIT           -- Possui registros de fato (BO)
    - antecedentes:         BIT           -- Proprietário com antecedentes criminais
    - supervisionado:       BIT           -- Está sob supervisão
    - tipo_veiculo:         VARCHAR(30)   -- Tipo do veículo (carro, moto, etc.)
    - periodo_predominante: VARCHAR(20)   -- Período de maior circulação (Diurno: 6h-17h / Noturno: 18h-5h)
    - tipos_alerta:         NVARCHAR(MAX) -- Tipos de alerta (apenas com flag alerta)
    - tipos_registro_fato:  NVARCHAR(MAX) -- Tipos de registro (apenas com flag boletim)
    - manchas:              NVARCHAR(MAX) -- Análise de manchas comportamentais

Parâmetros:
    @placa_informada            VARCHAR(10)
    @data_inicio                DATETIME
    @data_final                 DATETIME
    @tempo_passagem_minutos     INT      (default = 3)
    @considerar_antes_depois    BIT      (1: +/- janela; 0: [t, t+janela])
    @num_min_passagens_correlacionadas INT      (default = 3) -- mínimo de passagens correlacionadas

Autor: Thiago Guilsotti
Data de Criação: 04/09/2025
Versão: 2.0 - Formato Unificado + Enriquecimento

Exemplo:
    EXEC muralha.spu_correlacionamento_placas
        @placa_informada='SEU7J11',
        @data_inicio='2025-01-01 00:00:00',
        @data_final='2025-12-31 23:59:59',
        @tempo_passagem_minutos=3,
        @considerar_antes_depois=1,
        @num_min_passagens_correlacionadas=3;
===============================================================================
*/
CREATE   PROCEDURE [muralha].[spu_correlacionamento_placas]
    @placa_informada           VARCHAR(10),
    @data_inicio               DATETIME    = NULL,
    @data_final                DATETIME    = NULL,
    @tempo_passagem_minutos    INT         = 3,
    @considerar_antes_depois   BIT         = 1,
    @num_min_passagens_correlacionadas INT     = 3
AS
BEGIN
    SET NOCOUNT ON;

    -- Anti-Parameter Sniffing: Cópia local dos parâmetros
    DECLARE @placa_informada_local VARCHAR(10) = @placa_informada;
    DECLARE @data_inicio_local DATETIME = @data_inicio;
    DECLARE @data_final_local DATETIME = @data_final;
    DECLARE @tempo_passagem_minutos_local INT = @tempo_passagem_minutos;
    DECLARE @considerar_antes_depois_local BIT = @considerar_antes_depois;
    DECLARE @num_min_passagens_correlacionadas_local INT = CASE
        WHEN @num_min_passagens_correlacionadas IS NULL OR @num_min_passagens_correlacionadas < 1 THEN 1
        ELSE @num_min_passagens_correlacionadas
    END;
    
    /*=======================================================================
    [ETAPA 1] EXECUÇÃO DA BASE + CAPTURA DO RESULTADO
    Executa a stored procedure spu_correlacionamento_placas_base, que
    calcula as co-ocorrências de placas no mesmo PCL dentro da janela
    temporal configurada. O resultado (placa, passagens, incidência e
    flags de monitoramento) é capturado em #ResultadoBase para servir
    de entrada às etapas de enriquecimento seguintes.
    =======================================================================*/
    -- Tabela temporária para capturar resultado da base
    CREATE TABLE #ResultadoBase (
        placa        VARCHAR(10) NULL,
        passagens    BIGINT      NOT NULL,
        incidencia   CHAR(1)     NULL,
        monitorado   BIT         NOT NULL,
        alerta       BIT         NOT NULL,
        boletim      BIT         NOT NULL,
        antecedentes BIT         NOT NULL
    );

    -- Inserir resultado da SP base
    INSERT INTO #ResultadoBase (placa, passagens, incidencia, monitorado, alerta, boletim, antecedentes)
    EXEC [muralha].[spu_correlacionamento_placas_base]
        @placa_informada = @placa_informada_local,
        @data_inicio = @data_inicio_local,
        @data_final = @data_final_local,
        @tempo_passagem_minutos = @tempo_passagem_minutos_local,
        @considerar_antes_depois = @considerar_antes_depois_local,
        @num_min_passagens_correlacionadas = @num_min_passagens_correlacionadas_local;

    -- Índice para enriquecimentos
    CREATE UNIQUE CLUSTERED INDEX CX_Base ON #ResultadoBase (placa);

    /*=======================================================================
    [ETAPA 2] TABELA FINAL COM CAMPOS DE ENRIQUECIMENTO
    Cria a tabela #ResultadoFinal com todos os campos de saída,
    incluindo os de enriquecimento (tipo_veiculo, periodo_predominante,
    tipos_alerta, tipos_registro_fato, manchas, sem_ocr_registro_fato).
    Carrega os dados de #ResultadoBase e já resolve o tipo_veiculo via
    OUTER APPLY na última classificação registrada em veiculo_tempo_real
    com JOIN em classe_veiculo.
    =======================================================================*/
    CREATE TABLE #ResultadoFinal (
        placa               VARCHAR(10)     NULL,
        passagens           BIGINT          NOT NULL,
        incidencia          CHAR(1)         NULL,
        monitorado          BIT             NOT NULL,
        alerta              BIT             NOT NULL,
        boletim             BIT             NOT NULL,
        antecedentes        BIT             NOT NULL,
        supervisionado      BIT             NULL,
        tipo_veiculo        VARCHAR(30)     NULL,
        periodo_predominante VARCHAR(20)    NULL,
        tipos_alerta        NVARCHAR(MAX)   NULL,
        tipos_registro_fato NVARCHAR(MAX)   NULL,
        manchas             NVARCHAR(MAX)   NULL,
        sem_ocr_registro_fato BIT           NOT NULL DEFAULT 0
    );

    -- Carregar dados base + tipo_veiculo + flags individuais
    INSERT INTO #ResultadoFinal (placa, passagens, incidencia, monitorado, alerta, boletim, antecedentes, supervisionado, tipo_veiculo, periodo_predominante, tipos_alerta, tipos_registro_fato, manchas)
    SELECT 
        rb.placa,
        rb.passagens,
        rb.incidencia,
        rb.monitorado,
        rb.alerta,
        rb.boletim,
        rb.antecedentes,
        NULL AS supervisionado,
        cv.descricao AS tipo_veiculo,
        NULL AS periodo_predominante,
        NULL AS tipos_alerta,
        NULL AS tipos_registro_fato,
        '[]' AS manchas
    FROM #ResultadoBase rb
    OUTER APPLY (
		SELECT TOP 1 vtr_sub.classificacao
		FROM muralha.veiculo_tempo_real vtr_sub WITH (NOLOCK)
		WHERE vtr_sub.placa = rb.placa
		  AND rb.placa IS NOT NULL
		  AND vtr_sub.classificacao IS NOT NULL
		  AND LTRIM(RTRIM(vtr_sub.classificacao)) <> ''
		ORDER BY vtr_sub.data DESC
	) vtr
	LEFT JOIN classe_veiculo cv ON cv.id_classe = vtr.classificacao;

    /*=======================================================================
    [ETAPA 3] ENRIQUECIMENTO - TIPOS DE ALERTA E SUPERVISIONADO
    Para placas que possuem flag alerta=1, consulta cad_veiculo_monitorado
    e busca os tipos de alerta distintos (tipo_alerta_ocorrencia.tipo)
    concatenados em string separada por vírgula. Também atualiza o flag
    supervisionado a partir do cadastro do veículo monitorado.
    Tabelas: cad_veiculo_monitorado, alerta, tipo_alerta_ocorrencia.
    =======================================================================*/
    UPDATE rf SET 
        tipos_alerta = ta.tipos_json,
        supervisionado = ta.supervisionado
    FROM #ResultadoFinal rf
    INNER JOIN (
        SELECT vm.placa,
            vm.supervisionado,
            STUFF(
                (SELECT DISTINCT ', ' + LTRIM(RTRIM(tao.tipo))
                FROM muralha.alerta al 
                INNER JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = al.id_tipo_alerta_ocorrencia
                WHERE al.id_cad_veiculo_monitorado = vm.id
                FOR XML PATH(''))
            , 1, 2, '') AS tipos_json
        FROM muralha.cad_veiculo_monitorado vm
        WHERE EXISTS (
            SELECT 1 FROM #ResultadoBase rb 
            WHERE rb.placa = vm.placa 
            AND rb.placa IS NOT NULL
            AND rb.alerta = 1 
        )
    ) ta ON ta.placa = rf.placa
    WHERE rf.alerta = 1 AND rf.placa IS NOT NULL;

    /*=======================================================================
    [ETAPA 4] ENRIQUECIMENTO - PERÍODO PREDOMINANTE DE CIRCULAÇÃO
    Calcula o período do dia em que a placa mais circulou dentro do
    intervalo consultado. Classifica cada passagem como "Diurno" (6h-17h)
    ou "Noturno" (18h-5h), agrupa por placa e seleciona o período com
    maior contagem via ROW_NUMBER.
    Tabela: veiculo_tempo_real.
    =======================================================================*/
    UPDATE rf SET periodo_predominante = pp.periodo
    FROM #ResultadoFinal rf
    INNER JOIN (
        SELECT 
            placa,
            periodo,
            ROW_NUMBER() OVER (PARTITION BY placa ORDER BY total DESC) AS rn
        FROM (
            SELECT 
                vtr.placa,
                CASE 
                    WHEN DATEPART(HOUR, vtr.data) BETWEEN 6 AND 17 THEN 'Diurno'
                    ELSE 'Noturno'
                END AS periodo,
                COUNT(*) AS total
            FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
            WHERE EXISTS (
                SELECT 1 FROM #ResultadoBase rb 
                WHERE rb.placa = vtr.placa
                AND rb.placa IS NOT NULL
            )
                AND vtr.data >= @data_inicio_local
                AND vtr.data <= @data_final_local
            GROUP BY vtr.placa,
                CASE 
                    WHEN DATEPART(HOUR, vtr.data) BETWEEN 6 AND 17 THEN 'Diurno'
                    ELSE 'Noturno'
                END
        ) AS periodos
    ) pp ON pp.placa = rf.placa AND pp.rn = 1;

    /*=======================================================================
    [ETAPA 5] ENRIQUECIMENTO - TIPOS DE REGISTRO DE FATO
    Para placas com boletim=1 ou antecedentes=1, identifica os tipos de
    registro de fato vinculados (registro_fato_tipo.tipo_desc) e também
    verifica a existência de Produto/Objeto (registro_fato_objeto) e
    Recuperação (registro_fato_endereco com id_tipo_evento IN 3,4).
    Todos os tipos encontrados são concatenados em string única via
    STUFF + FOR XML PATH e gravados em tipos_registro_fato.
    Tabelas: registro_fato_veiculo, registro_fato, registro_fato_tipo,
             registro_fato_objeto, registro_fato_endereco.
    =======================================================================*/
    -- muralha.registro_fato_objeto
    ;WITH PlacasAlvo AS (
        SELECT DISTINCT rf.placa
        FROM #ResultadoFinal rf
        WHERE rf.placa IS NOT NULL
        AND (rf.boletim = 1 OR rf.antecedentes = 1)
    ),
    TiposBase AS (
        SELECT DISTINCT
            rfv.placa,
            LTRIM(RTRIM(trf2.tipo_desc)) AS tipo_desc
        FROM muralha.registro_fato_veiculo rfv
        INNER JOIN PlacasAlvo pa ON pa.placa = rfv.placa
        INNER JOIN muralha.registro_fato rf2 ON rf2.id = rfv.id_registro_fato
        INNER JOIN muralha.registro_fato_tipo trf2 ON trf2.id = rf2.id_tipo
    ),
    TiposProdutoObjeto AS (
        SELECT DISTINCT
            rfv_po.placa,
            'Produto/Objeto' AS tipo_desc
        FROM muralha.registro_fato_veiculo rfv_po
        INNER JOIN PlacasAlvo pa ON pa.placa = rfv_po.placa
        INNER JOIN muralha.registro_fato_objeto rfo ON rfo.id_registro_fato = rfv_po.id_registro_fato
    ),
    TiposRecuperacao AS (
        SELECT DISTINCT
            rfv_rec.placa,
            'Recuperação' AS tipo_desc
        FROM muralha.registro_fato_veiculo rfv_rec
        INNER JOIN PlacasAlvo pa ON pa.placa = rfv_rec.placa
        INNER JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rfv_rec.id_registro_fato
        WHERE rfe.id_tipo_evento IN (3, 4)
    ),
    TodosTipos AS (
        SELECT placa, tipo_desc FROM TiposBase
        UNION ALL
        SELECT placa, tipo_desc FROM TiposProdutoObjeto
        UNION ALL
        SELECT placa, tipo_desc FROM TiposRecuperacao
    ),
    TiposAgregados AS (
        SELECT
            tt.placa,
            STUFF(
                (
                    SELECT DISTINCT ', ' + tt2.tipo_desc
                    FROM TodosTipos tt2
                    WHERE tt2.placa = tt.placa
                    FOR XML PATH(''), TYPE
                ).value('.', 'NVARCHAR(MAX)')
            , 1, 2, '') AS tipos_json
        FROM TodosTipos tt
        GROUP BY tt.placa
    )
    UPDATE rf SET tipos_registro_fato = ta.tipos_json
    FROM #ResultadoFinal rf
    INNER JOIN TiposAgregados ta ON ta.placa = rf.placa
    WHERE rf.placa IS NOT NULL
    AND (rf.boletim = 1 OR rf.antecedentes = 1);

    /*=======================================================================
    [ETAPA 6] ENRIQUECIMENTO - REGISTRO DE FATO SEM FOTO
    Identifica placas que possuem pelo menos um Registro de Fato cujo
    registro NÃO possui documento de imagem associado na tabela
    registro_fato_documento (tipo = 'imagem'). Filtra pelo período
    consultado via registro_fato.data_criacao.
    Quando detectado, seta sem_ocr_registro_fato = 1 na placa.
    Tabelas: registro_fato_veiculo, registro_fato, registro_fato_documento.
    =======================================================================*/
    UPDATE res SET sem_ocr_registro_fato = 1
    FROM #ResultadoFinal res
    WHERE res.placa IS NOT NULL
    AND EXISTS (
        SELECT 1
        FROM muralha.registro_fato_veiculo rfv
        INNER JOIN muralha.registro_fato rf ON rf.id = rfv.id_registro_fato
        WHERE rfv.placa = res.placa
            AND rf.data_criacao >= @data_inicio_local
            AND rf.data_criacao <= @data_final_local
            AND NOT EXISTS (
                SELECT 1
                FROM muralha.registro_fato_documento rfd
                WHERE rfd.id_registro_fato = rfv.id_registro_fato
                    AND rfd.tipo = 'imagem'
            )
    );

    /*=======================================================================
    [ETAPA 7] ENRIQUECIMENTO - ANÁLISE DE MANCHAS
    Para cada placa com valor (não NULL), calcula o perfil comportamental
    de manchas usando as funções fcn_perfil_comportamental_estadia_por_manchas
    e fcn_perfil_comportamental_base_locais_com_mancha. Gera um JSON com
    as áreas monitoradas (manchas), suas transições, tempo de estadia e
    os locais visitados dentro de cada mancha com total de passagens.
    Utiliza cursor para processar placa a placa.
    Tabelas/Funções: veiculo_tempo_real, fcn_perfil_comportamental_*.
    =======================================================================*/
    -- Atualizar manchas para cada placa individualmente
    DECLARE @placa_atual VARCHAR(10);
    DECLARE @manchas_json NVARCHAR(MAX);
    
    DECLARE cursor_manchas CURSOR FOR
    SELECT DISTINCT placa 
    FROM #ResultadoFinal
    WHERE placa IS NOT NULL;
    
    OPEN cursor_manchas;
    FETCH NEXT FROM cursor_manchas INTO @placa_atual;
    
    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Gerar JSON das manchas para a placa atual
        WITH EstadiasPorManchas AS (
            SELECT 
                epm.id_area_monitorada_entrada,
                epm.nome_area_monitorada_entrada,
                epm.qtd_transicoes_entre_manchas,
                epm.tempo_total_estadia_min
            FROM muralha.fcn_perfil_comportamental_estadia_por_manchas(
                @placa_atual, 
                @data_inicio_local, 
                @data_final_local
            ) AS epm
            WHERE epm.id_area_monitorada_entrada IS NOT NULL
        ),
        LocaisPorMancha AS (
            SELECT 
                lcm.id_area_monitorada,
                lcm.nome_local,
                COUNT(vtr.id_local) AS total_passagens
            FROM muralha.veiculo_tempo_real AS vtr
            INNER JOIN muralha.fcn_perfil_comportamental_base_locais_com_mancha() AS lcm
                ON lcm.id_local = vtr.id_local
            WHERE vtr.placa = @placa_atual
                AND vtr.data >= @data_inicio_local
                AND vtr.data <= @data_final_local
                AND lcm.id_area_monitorada IS NOT NULL
            GROUP BY lcm.id_area_monitorada, lcm.nome_local
        ),
        JsonLocaisPorMancha AS (
            SELECT 
                lpm.id_area_monitorada,
                (
                    SELECT 
                        lpm2.nome_local,
                        lpm2.total_passagens
                    FROM LocaisPorMancha AS lpm2
                    WHERE lpm2.id_area_monitorada = lpm.id_area_monitorada
                    ORDER BY lpm2.total_passagens DESC, lpm2.nome_local
                    FOR JSON PATH
                ) AS locais_json
            FROM (SELECT DISTINCT id_area_monitorada FROM LocaisPorMancha) AS lpm
        )
        SELECT @manchas_json = ISNULL(
            (
                SELECT 
                    epm.nome_area_monitorada_entrada AS nome_area,
                    epm.qtd_transicoes_entre_manchas AS qtd_transicoes,
                    epm.tempo_total_estadia_min AS tempo_estadia_minutos,
                    JSON_QUERY(ISNULL(jlpm.locais_json, '[]')) AS locais
                FROM EstadiasPorManchas AS epm
                LEFT JOIN JsonLocaisPorMancha AS jlpm ON jlpm.id_area_monitorada = epm.id_area_monitorada_entrada
                ORDER BY epm.tempo_total_estadia_min DESC, epm.nome_area_monitorada_entrada
                FOR JSON PATH
            ), '[]'
        );
        
        -- Atualizar a placa atual
        UPDATE #ResultadoFinal 
        SET manchas = @manchas_json
        WHERE placa = @placa_atual;
        
        FETCH NEXT FROM cursor_manchas INTO @placa_atual;
    END;
    
    CLOSE cursor_manchas;
    DEALLOCATE cursor_manchas;

    /*=======================================================================
    [RESULTADO FINAL] SAÍDA ENRIQUECIDA
    Retorna todas as colunas de #ResultadoFinal ordenadas com a placa
    informada primeiro (passagens=0) seguida das correlacionadas em
    ordem decrescente de passagens.
    =======================================================================*/
    SELECT 
        rf.placa,
        rf.passagens,
        rf.incidencia,
        rf.monitorado,
        rf.alerta,
        rf.boletim,
        rf.antecedentes,
        rf.supervisionado,
        rf.tipo_veiculo,
        rf.periodo_predominante,
        ISNULL(rf.tipos_alerta, NULL) AS tipos_alerta,
        ISNULL(rf.tipos_registro_fato, NULL) AS tipos_registro_fato,
        ISNULL(rf.manchas, '[]') AS manchas,
        rf.sem_ocr_registro_fato
    FROM #ResultadoFinal rf
    ORDER BY 
        CASE WHEN rf.passagens = 0 THEN 0 ELSE 1 END,
        rf.passagens DESC;

    /*=======================================================================
    [LIMPEZA]
    =======================================================================*/
    DROP TABLE IF EXISTS #ResultadoBase;
    DROP TABLE IF EXISTS #ResultadoFinal;
END

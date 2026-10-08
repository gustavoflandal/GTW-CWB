CREATE   PROCEDURE [muralha].[spu_correlacionamento_placas_registro_fato]          
    @data_inicio               DATETIME    = NULL,          
    @data_final                DATETIME    = NULL,          
    @tempo_passagem_minutos    INT         = 3,          
    @considerar_antes_depois   BIT         = 1,          
    @num_min_correlacoes       INT         = 3,          
    @incluir_abaixo_minimo     BIT         = 0    
   
AS          
BEGIN          
    SET NOCOUNT ON;          
        
 SET @data_inicio = '20250101' -- CAST(DATEADD(DAY, -1, GETDATE()) AS DATE);        
 SET @data_final =  '20251231' --CAST(GETDATE() AS DATE);      
           
    -- Anti-Parameter Sniffing: Cópia local dos parâmetros          
    DECLARE @tempo_passagem_local INT = @tempo_passagem_minutos;          
    DECLARE @num_min_correlacoes_local INT = @num_min_correlacoes;          
    DECLARE @considerar_antes_depois_local BIT = @considerar_antes_depois;          
    DECLARE @incluir_abaixo_minimo_local BIT = @incluir_abaixo_minimo        
          
    /*=======================================================================          
    [ETAPA 1] PASSAGENS DAS PLACAS EXISTENTES EM REGISTRO_FATO_VEICULO + JANELAS TEMPORAIS          
    =======================================================================*/          
        
  SELECT DISTINCT rfv.placa    
  INTO #PlacasBase    
  FROM muralha.registro_fato_veiculo rfv    
  JOIN muralha.registro_fato rf ON rf.id = rfv.id_registro_fato    
  WHERE rf.id_status = 1; --> Registros de Fato ainda ativos        
        
  DROP TABLE IF EXISTS #PassagensBase;          
  CREATE TABLE #PassagensBase (          
   id    UNIQUEIDENTIFIER NOT NULL,          
   placa_base      VARCHAR(10)      NOT NULL,          
   data   DATETIME         NOT NULL,          
   id_local  INT              NOT NULL,          
   win_start  DATETIME         NOT NULL,          
   win_end   DATETIME         NOT NULL          
  );          
          
  -- Cálculo das janelas temporais          
  INSERT INTO #PassagensBase (id, placa_base, data, id_local, win_start, win_end)          
  SELECT           
   vtr.id,           
   pb.placa,           
   vtr.data,           
   vtr.id_local,          
   CASE WHEN @considerar_antes_depois_local = 1          
    THEN DATEADD(minute, -@tempo_passagem_local, vtr.data)          
    ELSE vtr.data          
   END AS win_start,          
   DATEADD(minute, @tempo_passagem_local, vtr.data) AS win_end          
   FROM #PlacasBase pb        
   JOIN muralha.veiculo_tempo_real vtr        
  ON vtr.placa = pb.placa        
   AND vtr.data >= @data_inicio        
   AND vtr.data < @data_final        
   OPTION (RECOMPILE);        
          
    -- Índice para lookup por local e janela temporal          
    CREATE CLUSTERED INDEX CX_Base ON #PassagensBase (id_local, win_start, win_end);          
          
    /*=======================================================================          
    [ETAPA 2] CORRELAÇÕES + AGREGAÇÃO + MAPEAMENTO DE IMAGENS           
    =======================================================================*/    
   
    DROP TABLE IF EXISTS #CorrelacoesDetalhe;  
  
    CREATE TABLE #CorrelacoesDetalhe (  
        id_passagem_base UNIQUEIDENTIFIER,
        id_passagem_correlacionada UNIQUEIDENTIFIER,
        placa_base VARCHAR(10),  
        placa_correlacionada VARCHAR(10)  
    );  

    INSERT INTO #CorrelacoesDetalhe  
    SELECT DISTINCT
        pb.id AS placa_base,  
        vtr.id AS placa_corr,  
        pb.placa_base,  
        vtr.placa  
    FROM #PassagensBase AS pb          
    INNER JOIN muralha.veiculo_tempo_real AS vtr          
        ON vtr.id_local = pb.id_local          
        AND vtr.data BETWEEN pb.win_start AND pb.win_end          
        AND vtr.placa IS NOT NULL          
        AND (vtr.placa > pb.placa_base);  

    DROP TABLE IF EXISTS #Correlacoes;  
    SELECT   
        placa_base,   
        placa_correlacionada,   
        COUNT_BIG(*) AS passagens  
    INTO #Correlacoes  
    FROM #CorrelacoesDetalhe  
    GROUP BY placa_base, placa_correlacionada  
    OPTION (RECOMPILE);      
          
    -- Índice para lookup rápido por placa          
 CREATE CLUSTERED INDEX CX_Corr ON #Correlacoes (placa_base, placa_correlacionada);        
          
    /*=======================================================================          
    [ETAPA 3] FLAGS DE SEGURANÇA          
    =======================================================================*/          
    ;WITH PlacasEnvolvidas AS  (   SELECT placa_base AS placa   FROM #PassagensBase    UNION    SELECT placa_correlacionada   FROM #Correlacoes   WHERE placa_correlacionada IS NOT NULL  ),          
    FlagsSeguranca AS (          
        SELECT           
            pe.placa,          
            -- Flags de segurança em uma única passada          
            CASE WHEN EXISTS (          
                SELECT 1 FROM muralha.cad_veiculo_monitorado vm WHERE vm.placa = pe.placa          
            ) THEN 1 ELSE 0 END AS monitorado,          
                      
            CASE WHEN EXISTS (          
                SELECT 1 FROM muralha.cad_veiculo_monitorado vm           
                INNER JOIN muralha.alerta al ON al.id_cad_veiculo_monitorado = vm.id          
                WHERE vm.placa = pe.placa          
            ) THEN 1 ELSE 0 END AS alerta,          
                      
            CASE WHEN EXISTS (          
                SELECT 1 FROM muralha.registro_fato_veiculo bl WHERE bl.placa = pe.placa          
            ) THEN 1 ELSE 0 END AS boletim,          
                      
            CASE WHEN EXISTS (          
                SELECT 1 FROM dbo.cadastro_veiculo v           
                INNER JOIN muralha.proprietario_veiculo pv ON pv.placa = v.placa          
                INNER JOIN muralha.antecedentes_criminais ac ON ac.id_proprietario = pv.id_proprietario          
                WHERE v.placa = pe.placa          
            ) THEN 1 ELSE 0 END AS antecedentes          
        FROM PlacasEnvolvidas pe          
    ),          
    FlagsConsolidados AS (          
        SELECT           
            placa,          
            monitorado,          
            alerta,          
            boletim,          
            antecedentes          
        FROM FlagsSeguranca          
    )          
    /*=======================================================================          
    [ETAPA 4] RESULTADO FINAL          
    =======================================================================*/         
       
  SELECT *  
  INTO #ResultadoFinal  
  FROM (  
   SELECT        
    c.placa_base,      
    c.placa_correlacionada,      
    c.passagens,   
    CASE      
     WHEN c.passagens < @num_min_correlacoes_local THEN NULL      
     WHEN c.passagens = @num_min_correlacoes_local THEN 'F'      
     WHEN c.passagens = (@num_min_correlacoes_local + 1) THEN 'M'      
     ELSE 'A'      
    END AS incidencia,      
    monitorado,      
    f.alerta,      
    f.boletim,      
    f.antecedentes,      
    @data_inicio AS data_inicio_analise,      
    @data_final AS data_fim_analise      
   FROM #Correlacoes c      
   LEFT JOIN FlagsConsolidados f ON f.placa = c.placa_correlacionada  
  ) r  
  WHERE      
   r.incidencia IS NOT NULL      
   OR (r.passagens < @num_min_correlacoes_local AND (r.monitorado = 1 OR r.alerta = 1 OR r.boletim = 1 OR r.antecedentes = 1))  
   OR (@incluir_abaixo_minimo_local = 1 AND r.passagens < @num_min_correlacoes_local);  
  
    /*=======================================================================          
    [ETAPA 5] GARANTIR RELACIONAMENTOS          
    =======================================================================*/          
    MERGE muralha.correlacionamento_placas AS target  
    USING (  
        SELECT DISTINCT placa_base, placa_correlacionada  
        FROM #ResultadoFinal  
    ) AS source  
    ON target.placa_base = source.placa_base  
    AND target.placa_correlacionada = source.placa_correlacionada  
    WHEN NOT MATCHED THEN  
        INSERT (id, placa_base, placa_correlacionada)  
        VALUES (NEWID(), source.placa_base, source.placa_correlacionada);  
  
    /*=======================================================================          
    [ETAPA 6] INSERIR HISTÓRICO          
    =======================================================================*/   
      
	INSERT INTO muralha.analise_correlacionamento_placas_registro_fato
	(
		id_veiculo_tempo_real,
		id_correlacionamento,
		data_inicio_analise,
		data_fim_analise
	)
	SELECT DISTINCT
		ids.id_passagem,
		cp.id,
		@data_inicio,
		@data_final
	FROM #CorrelacoesDetalhe det
	JOIN muralha.correlacionamento_placas cp
		ON cp.placa_base = det.placa_base
		AND cp.placa_correlacionada = det.placa_correlacionada
	CROSS APPLY (
		SELECT det.id_passagem_base
		UNION
		SELECT det.id_passagem_correlacionada
	) ids(id_passagem)
	WHERE NOT EXISTS (
		SELECT 1
		FROM muralha.analise_correlacionamento_placas_registro_fato f
		WHERE f.id_veiculo_tempo_real = ids.id_passagem
		  AND f.id_correlacionamento = cp.id
	);
          
    /*=======================================================================          
    [LIMPEZA]          
    =======================================================================*/          
    DROP TABLE IF EXISTS #PlacasBase;  
    DROP TABLE IF EXISTS #PassagensBase;  
    DROP TABLE IF EXISTS #Correlacoes;  
    DROP TABLE IF EXISTS #ResultadoFinal;  
 DROP TABLE IF EXISTS #CorrelacoesDetalhe;  
  
END
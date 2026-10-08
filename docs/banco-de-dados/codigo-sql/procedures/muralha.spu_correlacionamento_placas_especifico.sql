CREATE    PROCEDURE [muralha].[spu_correlacionamento_placas_especifico]    
    @placa_base                VARCHAR(10),  
    @placa_correlacionada      VARCHAR(10),
    @data_inicio               DATETIME    = NULL,  
    @data_final                DATETIME    = NULL,  
    @tempo_passagem_minutos    INT         = 3,  
    @considerar_antes_depois   BIT         = 1,
	@offset					   INT =		0,
	@itens_por_pagina		   INT = 0
AS  
BEGIN  
    SET NOCOUNT ON;  
      
    DECLARE @p_base VARCHAR(10) = @placa_base;
    DECLARE @p_corr VARCHAR(10) = @placa_correlacionada;
    DECLARE @ini    DATETIME    = @data_inicio;
    DECLARE @fim    DATETIME    = @data_final;
    DECLARE @tempo  INT         = @tempo_passagem_minutos;
	DECLARE @offset_local INT	= @offset;
	DECLARE @itens_por_pagina_local INT = @itens_por_pagina;

    /*=======================================================================  
    [ETAPA 1] PASSAGENS DA PLACA BASE
    =======================================================================*/  
    DROP TABLE IF EXISTS #PassagensBase;  
    SELECT 
        id, 
        id_local, 
        data,
        CASE WHEN @considerar_antes_depois = 1  
            THEN DATEADD(minute, -@tempo, data)  
            ELSE data  
        END AS win_start,  
        DATEADD(minute, @tempo, data) AS win_end
    INTO #PassagensBase
    FROM muralha.veiculo_tempo_real
    WHERE placa = @p_base
      AND data BETWEEN @ini AND @fim;

    CREATE CLUSTERED INDEX CX_Base ON #PassagensBase (id_local, win_start, win_end);

    /*=======================================================================  
    [ETAPA 2] ENCONTROS INDIVIDUAIS (SEM AGRUPAMENTO)
    =======================================================================*/  

    SELECT 
		pb.id AS id_captura_placa_base,
		@placa_base as placa_base,
        pb.data AS data_passagem_placa_registro_fato,
		vtr.id AS id_captura_placa_correlacionada,  
		@placa_correlacionada as placa_Correlacionada,
        vtr.data AS data_passagem_placa_correlacionada,
        ABS(DATEDIFF(SECOND, pb.data, vtr.data)) AS diferenca_segundos,
        vtr.id_local,
		lpv.nome as nome_local,
		COUNT(*) OVER() AS total_registros
    FROM #PassagensBase AS pb  
    INNER JOIN muralha.veiculo_tempo_real AS vtr  
        ON vtr.id_local = pb.id_local  
        AND vtr.data BETWEEN pb.win_start AND pb.win_end  
	INNER JOIN dbo.local_pista_vigente lpv
		ON lpv.id_local = vtr.id_local
    WHERE vtr.placa = @p_corr
      AND vtr.data BETWEEN @ini AND @fim
    ORDER BY pb.data DESC
	OFFSET @offset_local ROWS FETCH NEXT @itens_por_pagina_local ROWS ONLY
    OPTION (RECOMPILE);

    DROP TABLE IF EXISTS #PassagensBase;  
END
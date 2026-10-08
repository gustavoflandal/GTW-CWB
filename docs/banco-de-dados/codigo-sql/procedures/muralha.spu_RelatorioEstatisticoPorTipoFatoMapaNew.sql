

-- ==================================================================================================
-- Procedure: Relatório Estatístico de Fatos Registrados por Tipo
-- Descrição: Cria um relatório com dados estatisticos dos alarmes de ocorrencia de fato por tipo e
-- RETORNA OS DADOS EM RESULT SETS TABULARES (NOVO FORMATO)
-- Autor: Gustavo F. Landal
-- Data: 2025-10-14
-- Otimização V2: Conversão para formato tabular (múltiplos result sets)
-- ===================================================================================================

CREATE     PROCEDURE [muralha].[spu_RelatorioEstatisticoPorTipoFatoMapaNew]
    @dataInicio VARCHAR(10),
    @dataFim VARCHAR(10)
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Criar tabela temporária com dados filtrados por data
    CREATE TABLE #TempFatos (
        id_fato INT,
        id_tipo INT,
        data_cadastro DATETIME,
        latitude DECIMAL(18, 10),
        longitude DECIMAL(18, 10),
		rua VARCHAR(150),
		numero INT,
		bairro VARCHAR(100)
    );
    
    -- Inserir apenas dados dentro do período especificado
    -- Usar ROW_NUMBER para pegar apenas o primeiro endereço de cada fato com geolocalização válida
    INSERT INTO #TempFatos (id_fato, id_tipo, data_cadastro, latitude, longitude,rua,numero,bairro)
    SELECT 
        id_fato,
        id_tipo,
        data_criacao,
        latitude,
        longitude,
		rua,
		numero,
		bairro
    FROM (
        SELECT 
            rf.id as id_fato,
            rf.id_tipo,
            rf.data_criacao,
            e.latitude,
            e.longitude,
			e.rua,
			e.numero,
			e.bairro,
            ROW_NUMBER() OVER (PARTITION BY rf.id ORDER BY e.id) as rn
        FROM muralha.registro_fato rf
        INNER JOIN muralha.registro_fato_endereco e ON e.id_registro_fato = rf.id
        WHERE CAST(rf.data_criacao AS DATE) BETWEEN @dataInicio AND @dataFim
            AND ISNULL(e.latitude,0) <> 0
            AND isnull(e.longitude,0) <> 0
    ) AS dados_com_rn
    WHERE rn = 1; -- Apenas o primeiro endereço válido de cada fato
    
	
    
    ----------------------------------------------------------------------
    -- 1. OCORRÊNCIAS POR TIPO COM GEOLOCALIZAÇÃO ÚNICA (Result Set 1)
    ----------------------------------------------------------------------
    -- Retorna o total de ocorrências por tipo e as coordenadas (latitude, longitude)
    -- de CADA fato para plotagem no mapa.
    
    SELECT
        t.id_tipo,
        TIPO_EVENTO.tipo_desc AS nome_tipo_evento, -- Adicionado para melhor identificação
        COUNT(DISTINCT t.id_fato) AS total_ocorrencias,
        t.latitude,
        t.longitude,
		t.rua,
		t.numero,
		t.bairro
    FROM #TempFatos t
    INNER JOIN muralha.registro_fato_tipo TIPO_EVENTO ON TIPO_EVENTO.id = t.id_tipo
    GROUP BY t.id_tipo, TIPO_EVENTO.tipo_desc, t.latitude, t.longitude,rua,numero,bairro
    ORDER BY t.id_tipo, total_ocorrencias DESC;
    
    
    ----------------------------------------------------------------------
    -- 2. HISTOGRAMA SEMANAL (Result Set 2)
    ----------------------------------------------------------------------
    -- Agrupamento por semana
    SELECT 
        DATEPART(WEEK, data_cadastro) AS semana,
        COUNT(DISTINCT id_fato) AS total_ocorrencias
    FROM #TempFatos
    GROUP BY DATEPART(WEEK, data_cadastro)
    ORDER BY semana;
    
    
    ----------------------------------------------------------------------
    -- 3. HISTOGRAMA DIÁRIO (dia da semana) (Result Set 3)
    ----------------------------------------------------------------------
    -- Agrupamento por dia da semana (1=Domingo, 7=Sábado no SQL Server por padrão)
    SELECT 
        DATEPART(WEEKDAY, data_cadastro) AS dia_semana_sql,
        -- Sugestão: calcular o dia da semana no formato de 0 a 6 (0=Dom, 6=Sáb) se necessário
        (DATEPART(WEEKDAY, data_cadastro) - 1) AS dia_semana_0a6, 
        COUNT(DISTINCT id_fato) AS total_ocorrencias
    FROM #TempFatos
    GROUP BY DATEPART(WEEKDAY, data_cadastro)
    ORDER BY dia_semana_sql;
    
    
    ----------------------------------------------------------------------
    -- 4. HISTOGRAMA POR HORA (Result Set 4)
    ----------------------------------------------------------------------
    -- Agrupamento por hora do dia
    SELECT 
        DATEPART(HOUR, data_cadastro) AS hora,
        COUNT(DISTINCT id_fato) AS total_ocorrencias
    FROM #TempFatos
    GROUP BY DATEPART(HOUR, data_cadastro)
    ORDER BY hora;
    
    
    ----------------------------------------------------------------------
    -- 5. TIPOS DE EVENTOS (para mapeamento/legenda) (Result Set 5)
    ----------------------------------------------------------------------
    -- Retorna os tipos de eventos que TIVERAM ocorrências no período
    SELECT DISTINCT
        te.id,
        te.tipo_desc AS tipo_evento
    FROM muralha.registro_fato_tipo te
    INNER JOIN #TempFatos t ON t.id_tipo = te.id
    ORDER BY te.id;
    
    
    -- Limpar tabela temporária
    DROP TABLE #TempFatos;
    
END;

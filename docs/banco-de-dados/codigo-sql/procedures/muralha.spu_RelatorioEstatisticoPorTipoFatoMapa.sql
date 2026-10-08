
-- ==================================================================================================
-- Procedure: Relatório Estatístico de Fatos Registrados por Tipo
-- Descrição: Cria um relatório com dados estatisticos dos alarmes de ocorrencia de fato por tipo e
-- retorna no formato json com cordenada geograficas para plotagem dos pontos no mapa
-- Autor: Gustavo F. Landal
-- Data: 2025-09-05
-- Otimização V1: 
-- ===================================================================================================

CREATE       PROCEDURE [muralha].[spu_RelatorioEstatisticoPorTipoFatoMapa]
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
        longitude DECIMAL(18, 10)
    );
    
    -- Inserir apenas dados dentro do período especificado
    -- CORREÇÃO CRÍTICA: Usar ROW_NUMBER para pegar apenas o primeiro endereço de cada fato
    -- Isso garante que cada fato seja contado apenas UMA vez
    INSERT INTO #TempFatos (id_fato, id_tipo, data_cadastro, latitude, longitude)
    SELECT 
        id_fato,
        id_tipo,
        data_criacao,
        latitude,
        longitude
    FROM (
        SELECT 
            rf.id as id_fato,
            rf.id_tipo,
            rf.data_criacao,
            e.latitude,
            e.longitude,
            ROW_NUMBER() OVER (PARTITION BY rf.id ORDER BY e.id) as rn
        FROM muralha.registro_fato rf
        INNER JOIN muralha.registro_fato_endereco e ON e.id_registro_fato = rf.id
        WHERE CAST(rf.data_criacao AS DATE) BETWEEN @dataInicio AND @dataFim
            AND e.latitude IS NOT NULL 
            AND e.longitude IS NOT NULL
            AND e.latitude <> 0 
            AND e.longitude <> 0
    ) AS dados_com_rn
    WHERE rn = 1;  -- Apenas o primeiro endereço válido de cada fato
    
    DECLARE @json NVARCHAR(MAX);
    
    -- Construir JSON com todas as estatísticas
    SET @json = '{';
    
    -- 1. OCORRÊNCIAS POR TIPO COM GEOLOCALIZAÇÃO ÚNICA
    SET @json = @json + '"ocorrencias_por_tipo": [';
    
    SELECT @json = @json + 
        STUFF((
            SELECT 
                ',{' +
                '"id_tipo_evento":' + CAST(id_tipo AS VARCHAR(10)) + ',' +
                '"total_ocorrencias":' + CAST(total_fatos AS VARCHAR(10)) + ',' +
                '"geolocalizacao":[' +
                STUFF((
                    SELECT TOP 100
                        ',{"latitude":' + CAST(latitude AS VARCHAR(20)) + 
                        ',"longitude":' + CAST(longitude AS VARCHAR(20)) + '}'
                    FROM (
                        -- Coordenadas únicas por tipo
                        SELECT DISTINCT t2.latitude, t2.longitude
                        FROM #TempFatos t2
                        WHERE t2.id_tipo = tipos_agrupados.id_tipo
                    ) AS coord
                    FOR XML PATH(''), TYPE
                ).value('.', 'NVARCHAR(MAX)'), 1, 1, '') +
                ']' +
                '}'
            FROM (
                SELECT 
                    id_tipo,
                    COUNT(DISTINCT id_fato) as total_fatos
                FROM #TempFatos
                GROUP BY id_tipo
                HAVING COUNT(DISTINCT id_fato) > 0
            ) AS tipos_agrupados
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 1, '');
    
    SET @json = @json + '],';
    
    -- 2. HISTOGRAMA SEMANAL
    SET @json = @json + '"histograma_semanal": [';
    
    SELECT @json = @json + 
        STUFF((
            SELECT 
                ',{' +
                '"semana":' + CAST(DATEPART(WEEK, data_cadastro) AS VARCHAR(10)) + ',' +
                '"total_ocorrencias":' + CAST(COUNT(DISTINCT id_fato) AS VARCHAR(10)) +
                '}'
            FROM #TempFatos
            GROUP BY DATEPART(WEEK, data_cadastro)
            ORDER BY DATEPART(WEEK, data_cadastro)
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 1, '');
    
    SET @json = @json + '],';
    
    -- 3. HISTOGRAMA DIÁRIO (dia da semana)
    SET @json = @json + '"histograma_diario": [';
    
    SELECT @json = @json + 
        STUFF((
            SELECT 
                ',{' +
                '"dia_semana":' + CAST(DATEPART(WEEKDAY, data_cadastro) - 1 AS VARCHAR(10)) + ',' +
                '"total_ocorrencias":' + CAST(COUNT(DISTINCT id_fato) AS VARCHAR(10)) +
                '}'
            FROM #TempFatos
            GROUP BY DATEPART(WEEKDAY, data_cadastro)
            ORDER BY DATEPART(WEEKDAY, data_cadastro)
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 1, '');
    
    SET @json = @json + '],';
    
    -- 4. HISTOGRAMA POR HORA
    SET @json = @json + '"histograma_por_hora": [';
    
    SELECT @json = @json + 
        STUFF((
            SELECT 
                ',{' +
                '"hora":' + CAST(DATEPART(HOUR, data_cadastro) AS VARCHAR(10)) + ',' +
                '"total_ocorrencias":' + CAST(COUNT(DISTINCT id_fato) AS VARCHAR(10)) +
                '}'
            FROM #TempFatos
            GROUP BY DATEPART(HOUR, data_cadastro)
            ORDER BY DATEPART(HOUR, data_cadastro)
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 1, '');
    
    SET @json = @json + '],';
    
    -- 5. TIPOS DE EVENTOS (para mapeamento)
    SET @json = @json + '"tipo_eventos": [';
    
    SELECT @json = @json + 
        STUFF((
            SELECT 
                ',{' +
                '"id":' + CAST(te.id AS VARCHAR(10)) + ',' +
                '"tipo_evento":"' + REPLACE(te.tipo_desc, '"', '\"') + '"' +
                '}'
            FROM (
                SELECT DISTINCT te.id, te.tipo_desc
                FROM muralha.registro_fato_tipo te
                INNER JOIN #TempFatos t ON t.id_tipo = te.id
            ) AS te
            ORDER BY te.id
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 1, '');
    
    SET @json = @json + ']';
    
    -- Fechar JSON
    SET @json = @json + '}';
    
    -- Limpar tabela temporária
    DROP TABLE #TempFatos;
    
    -- Retornar JSON como resultado
    SELECT @json AS JsonResult;
END;

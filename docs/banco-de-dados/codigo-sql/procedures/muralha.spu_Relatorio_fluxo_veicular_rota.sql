
CREATE   PROCEDURE [muralha].[spu_Relatorio_fluxo_veicular_rota]
    @data_referencia DATE,
    @id_local_origem INT,
    @id_local_destino INT
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
    
    -- Tabela variável (mais eficiente que temp table para volumes médios)
    DECLARE @passagens_rota TABLE (
        data DATE,
        placa VARCHAR(10),
        id_local_origem INT,
        nome_local_origem VARCHAR(200),
        data_hora_origem DATETIME,
        id_local_destino INT,
        nome_local_destino VARCHAR(200),
        data_hora_destino DATETIME,
        tempo_transito_minutos INT,
        hora INT,
        periodo_dia VARCHAR(20),
        INDEX IX_periodo (periodo_dia),
        INDEX IX_hora (hora),
        INDEX IX_placa (placa),
        INDEX IX_tempo (tempo_transito_minutos)
    );
    
    -- Inserir as passagens correlacionadas (otimizado com ROW_NUMBER para evitar duplicatas)
    INSERT INTO @passagens_rota
    SELECT 
        data,
        placa,
        id_local_origem,
        nome_local_origem,
        data_hora_origem,
        id_local_destino,
        nome_local_destino,
        data_hora_destino,
        tempo_transito_minutos,
        hora,
        periodo_dia
    FROM (
        SELECT 
            CAST(v1.data AS DATE) AS data,
            v1.placa,
            v1.id_local AS id_local_origem,
            l1.nome AS nome_local_origem,
            v1.data AS data_hora_origem,
            v2.id_local AS id_local_destino,
            l2.nome AS nome_local_destino,
            v2.data AS data_hora_destino,
            DATEDIFF(MINUTE, v1.data, v2.data) AS tempo_transito_minutos,
            DATEPART(HOUR, v1.data) AS hora,
            CASE 
                WHEN DATEPART(HOUR, v1.data) BETWEEN 6 AND 11 THEN 'Manhã'
                WHEN DATEPART(HOUR, v1.data) BETWEEN 12 AND 17 THEN 'Tarde'
                WHEN DATEPART(HOUR, v1.data) BETWEEN 18 AND 23 THEN 'Noite'
                ELSE 'Madrugada'
            END AS periodo_dia,
            -- ROW_NUMBER para pegar apenas a primeira passagem no destino após cada origem
            ROW_NUMBER() OVER (
                PARTITION BY v1.placa, v1.data 
                ORDER BY v2.data ASC
            ) AS rn
        FROM muralha.veiculo_tempo_real v1
        INNER JOIN muralha.veiculo_tempo_real v2
            ON v1.placa = v2.placa
            AND v2.data > v1.data
            AND v2.data <= DATEADD(MINUTE, 120, v1.data)
            AND v2.data >= DATEADD(MINUTE, 1, v1.data)
            AND v2.id_local = @id_local_destino
        INNER JOIN dbo.local_vigente l1
            ON v1.id_local = l1.id_local
        INNER JOIN dbo.local_vigente l2
            ON v2.id_local = l2.id_local
        WHERE 
            v1.data >= @data_referencia
            AND v1.data < DATEADD(DAY, 1, @data_referencia)
            AND v1.id_local = @id_local_origem
            AND v1.placa IS NOT NULL
            AND v2.placa IS NOT NULL
    ) AS correlacoes
    WHERE rn = 1;  -- Apenas a primeira correspondência de destino para cada origem
    
    -- Variável para total de passagens
    DECLARE @total_passagens INT = (SELECT COUNT(*) FROM @passagens_rota);
    
    -- RESULT SET 1: Resumo Geral
    SELECT 
        @total_passagens AS total_passagens,
        COUNT(DISTINCT placa) AS total_veiculos_unicos,
        AVG(CAST(tempo_transito_minutos AS FLOAT)) AS tempo_medio_minutos,
        MIN(tempo_transito_minutos) AS tempo_minimo_minutos,
        MAX(tempo_transito_minutos) AS tempo_maximo_minutos,
        STDEV(tempo_transito_minutos) AS desvio_padrao,
        @id_local_origem AS id_local_origem,
        @id_local_destino AS id_local_destino,
        @data_referencia AS data_referencia
    FROM @passagens_rota;
    
    -- RESULT SET 2: Dados por Período do Dia
    SELECT 
        periodo_dia,
        CASE periodo_dia
            WHEN 'Madrugada' THEN 1
  WHEN 'Manhã' THEN 2
            WHEN 'Tarde' THEN 3
            WHEN 'Noite' THEN 4
        END AS ordem_periodo,
        COUNT(*) AS total_passagens,
        COUNT(DISTINCT placa) AS veiculos_unicos,
        AVG(CAST(tempo_transito_minutos AS FLOAT)) AS tempo_medio_minutos,
        MIN(tempo_transito_minutos) AS tempo_minimo_minutos,
        MAX(tempo_transito_minutos) AS tempo_maximo_minutos,
        CAST(COUNT(*) * 100.0 / NULLIF(@total_passagens, 0) AS DECIMAL(5,2)) AS percentual_do_total
    FROM @passagens_rota
    GROUP BY periodo_dia
    ORDER BY ordem_periodo;
    
    -- RESULT SET 3: Dados Horários
    SELECT 
        hora,
        COUNT(*) AS total_passagens,
        AVG(CAST(tempo_transito_minutos AS FLOAT)) AS tempo_medio_minutos,
        MAX(periodo_dia) AS periodo_dia
    FROM @passagens_rota
    GROUP BY hora
    ORDER BY hora;
    
    -- RESULT SET 4: Top 10 Placas
    SELECT TOP 10
        placa,
        COUNT(*) AS total_passagens,
        AVG(CAST(tempo_transito_minutos AS FLOAT)) AS tempo_medio_minutos,
        MIN(tempo_transito_minutos) AS tempo_minimo_minutos,
        MAX(tempo_transito_minutos) AS tempo_maximo_minutos,
        CONVERT(VARCHAR(20), MIN(data_hora_origem), 120) AS primeira_passagem,
        CONVERT(VARCHAR(20), MAX(data_hora_origem), 120) AS ultima_passagem
    FROM @passagens_rota
    GROUP BY placa
    ORDER BY total_passagens DESC, tempo_medio_minutos ASC;
    
    -- RESULT SET 5: Distribuição de Tempo de Trânsito
    SELECT 
        faixa_tempo,
        ordem_faixa,
        COUNT(*) AS total_passagens,
        CAST(COUNT(*) * 100.0 / NULLIF(@total_passagens, 0) AS DECIMAL(5,2)) AS percentual,
        AVG(CAST(tempo_transito_minutos AS FLOAT)) AS tempo_medio_faixa
    FROM (
        SELECT 
            tempo_transito_minutos,
            CASE 
                WHEN tempo_transito_minutos <= 5 THEN '0-5 min'
                WHEN tempo_transito_minutos <= 10 THEN '6-10 min'
                WHEN tempo_transito_minutos <= 15 THEN '11-15 min'
                WHEN tempo_transito_minutos <= 20 THEN '16-20 min'
                WHEN tempo_transito_minutos <= 30 THEN '21-30 min'
                WHEN tempo_transito_minutos <= 45 THEN '31-45 min'
                WHEN tempo_transito_minutos <= 60 THEN '46-60 min'
                ELSE '> 60 min'
            END AS faixa_tempo,
            CASE 
                WHEN tempo_transito_minutos <= 5 THEN 1
                WHEN tempo_transito_minutos <= 10 THEN 2
                WHEN tempo_transito_minutos <= 15 THEN 3
                WHEN tempo_transito_minutos <= 20 THEN 4
                WHEN tempo_transito_minutos <= 30 THEN 5
                WHEN tempo_transito_minutos <= 45 THEN 6
                WHEN tempo_transito_minutos <= 60 THEN 7
                ELSE 8
            END AS ordem_faixa
        FROM @passagens_rota
    ) AS distribuicao
    GROUP BY faixa_tempo, ordem_faixa
    ORDER BY ordem_faixa;
    
    -- RESULT SET 6: Listagem Detalhada de Passagens
    SELECT TOP 500
        data,
        placa,
        id_local_origem,
        id_local_destino,
        CONVERT(VARCHAR(20), data_hora_origem, 120) AS origem,
        CONVERT(VARCHAR(20), data_hora_destino, 120) AS destino,
        tempo_transito_minutos AS tempo_tansito_minutos,  -- Mantém o typo
        periodo_dia,
        hora
    FROM @passagens_rota
    ORDER BY data_hora_origem DESC;
    
    -- RESULT SET 7: Comparativo de Períodos
    SELECT 
        'TOTAIS' AS tipo_resultado,
        SUM(CASE WHEN periodo_dia = 'Manhã' THEN 1 ELSE 0 END) AS passagens_manha,
        SUM(CASE WHEN periodo_dia = 'Tarde' THEN 1 ELSE 0 END) AS passagens_tarde,
        SUM(CASE WHEN periodo_dia = 'Noite' THEN 1 ELSE 0 END) AS passagens_noite,
        SUM(CASE WHEN periodo_dia = 'Madrugada' THEN 1 ELSE 0 END) AS passagens_madrugada,
        AVG(CASE WHEN periodo_dia = 'Manhã' THEN CAST(tempo_transito_minutos AS FLOAT) ELSE NULL END) AS tempo_medio_manha,
        AVG(CASE WHEN periodo_dia = 'Tarde' THEN CAST(tempo_transito_minutos AS FLOAT) ELSE NULL END) AS tempo_medio_tarde,
        AVG(CASE WHEN periodo_dia = 'Noite' THEN CAST(tempo_transito_minutos AS FLOAT) ELSE NULL END) AS tempo_medio_noite,
        AVG(CASE WHEN periodo_dia = 'Madrugada' THEN CAST(tempo_transito_minutos AS FLOAT) ELSE NULL END) AS tempo_medio_madrugada
    FROM @passagens_rota;
    
END



-- ==================================================================================================
-- Procedure: Relatório Estatístico de Alarmes
-- Descrição: Cria um relatório com dados estatisticos dos alarmes de ocorrencia de fato
-- Autor: Gustavo F. Landal
-- Data: 2025-09-01
-- Otimização V2: Ajustado o nome dos dias da semana para portugues do brasil.
-- ===================================================================================================

CREATE     PROCEDURE [muralha].[spu_RelatorioEstatisticoAlarmesJson]
(
    @DataInicio DATE = NULL,
    @DataFim DATE = NULL,
    @IdLocal INT = NULL,
    @IdTipoAlerta UNIQUEIDENTIFIER = NULL,
    @TipoRelatorio VARCHAR(20) = 'COMPLETO'
)
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;
    
    IF @DataInicio IS NULL SET @DataInicio = DATEADD(DAY, -30, GETDATE());
    IF @DataFim IS NULL SET @DataFim = GETDATE();
    
    CREATE TABLE #DadosAlarmes (
        id_alerta UNIQUEIDENTIFIER,
        data_alerta DATETIME,
        id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER,
        id_cad_veiculo_monitorado UNIQUEIDENTIFIER,
        id_status_alerta UNIQUEIDENTIFIER,
        enviado_cliente BIT,
        data_enviado DATETIME,
        origem VARCHAR(MAX),
        placa_monitorada CHAR(10),
        descricao_veiculo VARCHAR(MAX),
        nome_veiculo VARCHAR(MAX),
        placa_tempo_real CHAR(10),
        id_local INT,
        id_pista TINYINT,
        velocidade SMALLINT,
        classificacao CHAR(10),
        data_veiculo_tempo_real DATETIME,
        nome_local CHAR(255),
        codigo_local_cliente CHAR(255),
        localidade_desc VARCHAR(255),
        tipo_alerta VARCHAR(255),
        descricao_tipo_alerta VARCHAR(255),
        data_alarme DATE,
        hora_alarme INT,
        dia_semana_num INT,
        dia_semana_nome VARCHAR(20),
        dia_mes INT,
        mes INT,
        ano INT,
        periodo_dia VARCHAR(20),
        faixa_horaria VARCHAR(10)
    );
    
    INSERT INTO #DadosAlarmes
    SELECT 
        a.id AS id_alerta,
        a.data AS data_alerta,
        a.id_tipo_alerta_ocorrencia,
        a.id_cad_veiculo_monitorado,
        a.id_status_alerta,
        a.enviado_cliente,
        a.data_enviado,
        a.origem,
        
        vm.placa AS placa_monitorada,
        vm.descricao AS descricao_veiculo,
        vm.nome AS nome_veiculo,
        
        vtr.placa AS placa_tempo_real,
        vtr.id_local,
        vtr.id_pista,
        vtr.velocidade,
        vtr.classificacao,
        vtr.data AS data_veiculo_tempo_real,
        
        l.nome AS nome_local,
        l.serie_equipamento AS codigo_local_cliente,
        l.localidade_desc,
        
        tao.tipo AS tipo_alerta,
        tao.descricao AS descricao_tipo_alerta,
        
        CAST(a.data AS DATE) AS data_alarme,
        DATEPART(HOUR, a.data) AS hora_alarme,
        DATEPART(WEEKDAY, a.data) AS dia_semana_num,
        DATENAME(WEEKDAY, a.data) AS dia_semana_nome,
        DATEPART(DAY, a.data) AS dia_mes,
        DATEPART(MONTH, a.data) AS mes,
        DATEPART(YEAR, a.data) AS ano,
        
        CASE 
            WHEN DATEPART(HOUR, a.data) BETWEEN 6 AND 11 THEN 'Manhã'
            WHEN DATEPART(HOUR, a.data) BETWEEN 12 AND 17 THEN 'Tarde'
            WHEN DATEPART(HOUR, a.data) BETWEEN 18 AND 23 THEN 'Noite'
            ELSE 'Madrugada'
        END AS periodo_dia,
        
        CASE 
            WHEN DATEPART(HOUR, a.data) BETWEEN 0 AND 5 THEN '00-05h'
            WHEN DATEPART(HOUR, a.data) BETWEEN 6 AND 11 THEN '06-11h'
            WHEN DATEPART(HOUR, a.data) BETWEEN 12 AND 17 THEN '12-17h'
            WHEN DATEPART(HOUR, a.data) BETWEEN 18 AND 23 THEN '18-23h'
        END AS faixa_horaria
        
    FROM muralha.alerta a
    
    -- JOIN com veículo monitorado (opcional)
    LEFT JOIN muralha.cad_veiculo_monitorado vm 
        ON a.id_cad_veiculo_monitorado = vm.id

    -- JOIN com alerta_veiculo para obter relação com veículo tempo real
    LEFT JOIN muralha.alerta_veiculo av 
        ON a.id = av.id_alerta
        
    -- JOIN com veículo tempo real
    LEFT JOIN muralha.veiculo_tempo_real vtr 
        ON av.id_veiculo_tempo_real = vtr.id
        
    -- JOIN com local
    LEFT JOIN dbo.local_vigente l 
        ON vtr.id_local = l.id_local
        
    -- JOIN com tipo de alerta
    LEFT JOIN muralha.tipo_alerta_ocorrencia tao 
        ON a.id_tipo_alerta_ocorrencia = tao.id
        
    WHERE 
        CAST(a.data AS DATE) BETWEEN @DataInicio AND @DataFim
        AND (@IdLocal IS NULL OR vtr.id_local = @IdLocal)
        AND (@IdTipoAlerta IS NULL OR a.id_tipo_alerta_ocorrencia = @IdTipoAlerta);
    
    -- Relatório baseado no tipo solicitado
    DECLARE @ResultadoJSON NVARCHAR(MAX);
    DECLARE @EstatisticasGerais NVARCHAR(MAX);
    DECLARE @DistribuicaoPorData NVARCHAR(MAX);
    DECLARE @DistribuicaoPorPeriodo NVARCHAR(MAX);
    DECLARE @DistribuicaoPorDiaSemana NVARCHAR(MAX);
    DECLARE @DistribuicaoPorHora NVARCHAR(MAX);
    DECLARE @Top10Locais NVARCHAR(MAX);
    DECLARE @TiposAlarmes NVARCHAR(MAX);
    
    -- Construir componentes JSON
    SET @EstatisticasGerais = (
        SELECT 
            COUNT(*) AS totalAlarmes,
            COUNT(DISTINCT data_alarme) AS diasComAlarmes,
            COUNT(DISTINCT id_local) AS locaisComAlarmes,
            COUNT(DISTINCT id_tipo_alerta_ocorrencia) AS tiposAlarmesDistintos,
            CAST(COUNT(*) * 1.0 / NULLIF(COUNT(DISTINCT data_alarme), 0) AS DECIMAL(10,2)) AS mediaAlarmesPorDia,
            (
                SELECT TOP 1 data_alarme 
                FROM #DadosAlarmes 
                GROUP BY data_alarme 
                ORDER BY COUNT(*) DESC
            ) AS diaMaisAlarmes,
            (
                SELECT TOP 1 COUNT(*) 
                FROM #DadosAlarmes 
                GROUP BY data_alarme 
                ORDER BY COUNT(*) DESC
            ) AS quantidadeDiaMaisAlarmes,
            (
                SELECT TOP 1 periodo_dia 
                FROM #DadosAlarmes 
                GROUP BY periodo_dia 
                ORDER BY COUNT(*) DESC
            ) AS periodoMaisFrequente,
            (
                SELECT TOP 1 hora_alarme 
                FROM #DadosAlarmes 
                GROUP BY hora_alarme 
                ORDER BY COUNT(*) DESC
            ) AS horaMaisFrequente,
            (
                -- Usando a lógica de tradução interna para garantir que o nome em português seja retornado
                SELECT TOP 1 
                    CASE 
                        WHEN dia_semana_nome = 'Sunday' THEN 'Domingo'
                        WHEN dia_semana_nome = 'Monday' THEN 'Segunda-feira'
                        WHEN dia_semana_nome = 'Tuesday' THEN 'Terça-feira'
                        WHEN dia_semana_nome = 'Wednesday' THEN 'Quarta-feira'
                        WHEN dia_semana_nome = 'Thursday' THEN 'Quinta-feira'
                        WHEN dia_semana_nome = 'Friday' THEN 'Sexta-feira'
                        WHEN dia_semana_nome = 'Saturday' THEN 'Sábado'
                        -- Adicione outras traduções se o idioma do servidor for diferente (ex: Português, Espanhol)
                        WHEN dia_semana_nome = 'domingo' THEN 'Domingo'
                        WHEN dia_semana_nome = 'segunda-feira' THEN 'Segunda-feira'
                        WHEN dia_semana_nome = 'terça-feira' THEN 'Terça-feira'
                        WHEN dia_semana_nome = 'quarta-feira' THEN 'Quarta-feira'
                        WHEN dia_semana_nome = 'quinta-feira' THEN 'Quinta-feira'
                        WHEN dia_semana_nome = 'sexta-feira' THEN 'Sexta-feira'
                        WHEN dia_semana_nome = 'sábado' THEN 'Sábado'
                        ELSE dia_semana_nome -- Retorna o original se não encontrar
                    END
                FROM #DadosAlarmes 
                GROUP BY dia_semana_nome, dia_semana_num 
                ORDER BY COUNT(*) DESC
            ) AS diaSemanaComMaisAlarmes,
            MIN(data_alerta) AS primeiroAlarmeRegistrado,
            MAX(data_alerta) AS ultimoAlarmeRegistrado,
            DATEDIFF(DAY, MIN(data_alerta), MAX(data_alerta)) + 1 AS totalDiasPeriodo,
            (
                SELECT COUNT(DISTINCT placa_tempo_real) 
                FROM #DadosAlarmes 
                WHERE placa_tempo_real IS NOT NULL
            ) AS placasEnvolvidasAlarmes,
            CAST(
                CASE 
                    WHEN COUNT(*) > 0 THEN
                        (SELECT COUNT(*) FROM #DadosAlarmes WHERE enviado_cliente = 1) * 100.0 / COUNT(*)
                    ELSE 0 
                END AS DECIMAL(5,2)
            ) AS percentualAlarmesEnviadosCliente
        FROM #DadosAlarmes
        FOR JSON PATH, WITHOUT_ARRAY_WRAPPER
    );
    
    SET @DistribuicaoPorPeriodo = (
        SELECT 
            periodo_dia AS periodo,
            COUNT(*) AS quantidade,
            CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
        FROM #DadosAlarmes
        GROUP BY periodo_dia
        ORDER BY quantidade DESC
        FOR JSON PATH
    );
    
    --------------------------------------------------------------------------------------
    -- BLOCO MODIFICADO: @DistribuicaoPorDiaSemana
    --------------------------------------------------------------------------------------
    SET @DistribuicaoPorDiaSemana = (
        SELECT 
            -- Mapeamento para Português do Brasil
            CASE 
                WHEN dia_semana_nome = 'Sunday' THEN 'Domingo'
                WHEN dia_semana_nome = 'Monday' THEN 'Segunda-feira'
                WHEN dia_semana_nome = 'Tuesday' THEN 'Terça-feira'
                WHEN dia_semana_nome = 'Wednesday' THEN 'Quarta-feira'
                WHEN dia_semana_nome = 'Thursday' THEN 'Quinta-feira'
                WHEN dia_semana_nome = 'Friday' THEN 'Sexta-feira'
                WHEN dia_semana_nome = 'Saturday' THEN 'Sábado'
                -- Se o idioma do servidor for Português (Brasil) ou outro idioma que use o formato por extenso,
                -- é bom incluir as variações também.
                WHEN dia_semana_nome = 'domingo' THEN 'Domingo'
                WHEN dia_semana_nome = 'segunda-feira' THEN 'Segunda-feira'
                WHEN dia_semana_nome = 'terça-feira' THEN 'Terça-feira'
                WHEN dia_semana_nome = 'quarta-feira' THEN 'Quarta-feira'
                WHEN dia_semana_nome = 'quinta-feira' THEN 'Quinta-feira'
                WHEN dia_semana_nome = 'sexta-feira' THEN 'Sexta-feira'
                WHEN dia_semana_nome = 'sábado' THEN 'Sábado'
                ELSE dia_semana_nome -- Valor de fallback
            END AS diaSemana,
            COUNT(*) AS quantidade,
            CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
        FROM #DadosAlarmes
        GROUP BY dia_semana_num, dia_semana_nome
        ORDER BY dia_semana_num
        FOR JSON PATH
    );
    --------------------------------------------------------------------------------------
    -- FIM BLOCO MODIFICADO
    --------------------------------------------------------------------------------------
    
    SET @DistribuicaoPorHora = (
        SELECT 
            hora_alarme AS hora,
            COUNT(*) AS quantidade,
            CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
        FROM #DadosAlarmes
        GROUP BY hora_alarme
        ORDER BY hora_alarme
        FOR JSON PATH
    );
    
    SET @Top10Locais = (
        SELECT TOP 10
            ISNULL(nome_local, 'Local não identificado') AS nomeLocal,
            id_local AS idLocal,
            COUNT(*) AS totalAlarmes
        FROM #DadosAlarmes
        GROUP BY id_local, nome_local
        ORDER BY totalAlarmes DESC
        FOR JSON PATH
    );
    
    SET @TiposAlarmes = (
        SELECT 
            ISNULL(tipo_alerta, 'Tipo não identificado') AS tipo,
            ISNULL(descricao_tipo_alerta, 'Sem descrição') AS descricao,
            COUNT(*) AS quantidade,
            CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
        FROM #DadosAlarmes
        GROUP BY tipo_alerta, descricao_tipo_alerta
        ORDER BY quantidade DESC
        FOR JSON PATH
    );
    
    IF @TipoRelatorio = 'COMPLETO'
    BEGIN
        SET @DistribuicaoPorData = (
            SELECT 
                data_alarme AS data,
                COUNT(*) AS quantidade
            FROM #DadosAlarmes
            GROUP BY data_alarme
            ORDER BY data_alarme
            FOR JSON PATH
        );
        
        -- Construir JSON completo
        SET @ResultadoJSON = N'{' +
            N'"tipoRelatorio": "COMPLETO",' +
            N'"dataInicio": "' + CAST(@DataInicio AS NVARCHAR) + N'",' +
            N'"dataFim": "' + CAST(@DataFim AS NVARCHAR) + N'",' +
            N'"filtroLocal": ' + ISNULL(CAST(@IdLocal AS NVARCHAR), N'null') + N',' +
            N'"dataGeracao": "' + CONVERT(NVARCHAR, GETDATE(), 127) + N'",' +
            N'"estatisticasGerais": ' + @EstatisticasGerais + N',' +
            N'"distribuicaoPorData": ' + ISNULL(@DistribuicaoPorData, N'[]') + N',' +
            N'"distribuicaoPorPeriodo": ' + ISNULL(@DistribuicaoPorPeriodo, N'[]') + N',' +
            N'"distribuicaoPorDiaSemana": ' + ISNULL(@DistribuicaoPorDiaSemana, N'[]') + N',' +
            N'"distribuicaoPorHora": ' + ISNULL(@DistribuicaoPorHora, N'[]') + N',' +
            N'"top10Locais": ' + ISNULL(@Top10Locais, N'[]') + N',' +
            N'"tiposAlarmes": ' + ISNULL(@TiposAlarmes, N'[]') +
            N'}';
    END
    ELSE IF @TipoRelatorio = 'RESUMO'
    BEGIN
        -- Construir JSON resumo
        SET @ResultadoJSON = N'{' +
            N'"tipoRelatorio": "RESUMO",' +
            N'"dataInicio": "' + CAST(@DataInicio AS NVARCHAR) + N'",' +
            N'"dataFim": "' + CAST(@DataFim AS NVARCHAR) + N'",' +
            N'"filtroLocal": ' + ISNULL(CAST(@IdLocal AS NVARCHAR), N'null') + N',' +
            N'"dataGeracao": "' + CONVERT(NVARCHAR, GETDATE(), 127) + N'",' +
            N'"resumoExecutivo": ' + @EstatisticasGerais +
            N'}';
    END
    ELSE IF @TipoRelatorio = 'LOCALIZAÇÃO'
    BEGIN
        DECLARE @EstatisticasPorLocal NVARCHAR(MAX) = (
            SELECT 
                ISNULL(nome_local, 'Local não identificado') AS nomeLocal,
                id_local AS idLocal,
                codigo_local_cliente AS codigoLocal,
                localidade_desc AS descricaoLocalidade,
                COUNT(*) AS totalAlarmes,
                COUNT(DISTINCT data_alarme) AS diasComAlarmes,
                CAST(COUNT(*) * 1.0 / NULLIF(COUNT(DISTINCT data_alarme), 0) AS DECIMAL(10,2)) AS mediaPorDia,
                MIN(data_alerta) AS primeiroAlarme,
                MAX(data_alerta) AS ultimoAlarme
            FROM #DadosAlarmes
            GROUP BY id_local, nome_local, codigo_local_cliente, localidade_desc
            ORDER BY totalAlarmes DESC
            FOR JSON PATH
        );
        
        -- Construir JSON localização
        SET @ResultadoJSON = N'{' +
            N'"tipoRelatorio": "LOCALIZAÇÃO",' +
            N'"dataInicio": "' + CAST(@DataInicio AS NVARCHAR) + N'",' +
            N'"dataFim": "' + CAST(@DataFim AS NVARCHAR) + N'",' +
            N'"filtroLocal": ' + ISNULL(CAST(@IdLocal AS NVARCHAR), N'null') + N',' +
            N'"dataGeracao": "' + CONVERT(NVARCHAR, GETDATE(), 127) + N'",' +
            N'"estatisticasPorLocal": ' + ISNULL(@EstatisticasPorLocal, N'[]') + N',' +
            N'"distribuicaoPorPeriodo": ' + ISNULL(@DistribuicaoPorPeriodo, N'[]') + N',' +
            N'"distribuicaoPorDiaSemana": ' + ISNULL(@DistribuicaoPorDiaSemana, N'[]') + N',' +
            N'"top10Locais": ' + ISNULL(@Top10Locais, N'[]') +
            N'}';
    END
    ELSE IF @TipoRelatorio = 'TEMPORAL'
    BEGIN
        DECLARE @AnaliseTemporalDetalhada NVARCHAR(MAX) = (
            SELECT 
                periodo_dia AS periodo,
                -- Usando a lógica de tradução interna para garantir que o nome em português seja retornado
                CASE 
                    WHEN dia_semana_nome = 'Sunday' THEN 'Domingo'
                    WHEN dia_semana_nome = 'Monday' THEN 'Segunda'
                    WHEN dia_semana_nome = 'Tuesday' THEN 'Terça'
                    WHEN dia_semana_nome = 'Wednesday' THEN 'Quarta'
                    WHEN dia_semana_nome = 'Thursday' THEN 'Quinta'
                    WHEN dia_semana_nome = 'Friday' THEN 'Sexta'
                    WHEN dia_semana_nome = 'Saturday' THEN 'Sábado'
                    WHEN dia_semana_nome = 'domingo' THEN 'Domingo'
                    WHEN dia_semana_nome = 'segunda-feira' THEN 'Segunda'
                    WHEN dia_semana_nome = 'terça-feira' THEN 'Terça'
                    WHEN dia_semana_nome = 'quarta-feira' THEN 'Quarta'
                    WHEN dia_semana_nome = 'quinta-feira' THEN 'Quinta'
                    WHEN dia_semana_nome = 'sexta-feira' THEN 'Sexta'
                    WHEN dia_semana_nome = 'sábado' THEN 'Sábado'
                    ELSE dia_semana_nome 
                END AS diaSemana,
                faixa_horaria AS faixaHoraria,
                COUNT(*) AS quantidadeAlarmes,
                CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
            FROM #DadosAlarmes
            GROUP BY periodo_dia, dia_semana_nome, faixa_horaria
            ORDER BY quantidadeAlarmes DESC
            FOR JSON PATH
        );
        
        -- Construir JSON temporal
        SET @ResultadoJSON = N'{' +
            N'"tipoRelatorio": "TEMPORAL",' +
            N'"dataInicio": "' + CAST(@DataInicio AS NVARCHAR) + N'",' +
            N'"dataFim": "' + CAST(@DataFim AS NVARCHAR) + N'",' +
            N'"filtroLocal": ' + ISNULL(CAST(@IdLocal AS NVARCHAR), N'null') + N',' +
            N'"dataGeracao": "' + CONVERT(NVARCHAR, GETDATE(), 127) + N'",' +
            N'"analiseTemporalDetalhada": ' + ISNULL(@AnaliseTemporalDetalhada, N'[]') + N',' +
            N'"distribuicaoPorPeriodo": ' + ISNULL(@DistribuicaoPorPeriodo, N'[]') + N',' +
            N'"distribuicaoPorDiaSemana": ' + ISNULL(@DistribuicaoPorDiaSemana, N'[]') +
            N'}';
    END
    
    -- Retornar resultado JSON como texto puro
    SELECT @ResultadoJSON AS RelatorioJSON;
    
    -- Limpar tabela temporária
    DROP TABLE #DadosAlarmes;
    
END;

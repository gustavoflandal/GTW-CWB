/*
===========================================================================
PROCEDURE: SP_RelatorioEstatisticoAlarmes
DESCRIÇÃO: Gera relatório estatístico de alarmes com distribuição por:
           - Data, período do dia, dia da semana, dia do mês
           - Horário do alarme e pontos de coleta (id_local)
AUTOR: Sistema Muralha
DATA: 18/09/2025
===========================================================================
*/

CREATE   PROCEDURE [muralha].[SP_RelatorioEstatisticoAlarmes]
(
    @DataInicio DATE = NULL,
    @DataFim DATE = NULL,
    @IdLocal INT = NULL,
    @IdTipoAlerta UNIQUEIDENTIFIER = NULL,
    @TipoRelatorio VARCHAR(20) = 'COMPLETO' -- COMPLETO, RESUMO, LOCALIZAÇÃO, TEMPORAL
)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Definir período padrão se não fornecido (últimos 30 dias)
    IF @DataInicio IS NULL SET @DataInicio = DATEADD(DAY, -30, GETDATE());
    IF @DataFim IS NULL SET @DataFim = GETDATE();
    
    -- Criar tabela temporária com dados consolidados
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
    
    -- Inserir dados na tabela temporária
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
        
        -- Dados do veículo monitorado
        vm.placa AS placa_monitorada,
        vm.descricao AS descricao_veiculo,
        vm.nome AS nome_veiculo,
        
        -- Dados do veículo tempo real (através da tabela de relacionamento)
        vtr.placa AS placa_tempo_real,
        vtr.id_local,
        vtr.id_pista,
        vtr.velocidade,
        vtr.classificacao,
        vtr.data AS data_veiculo_tempo_real,
        
        -- Dados do local
        l.nome AS nome_local,
        l.codigo_local_cliente,
        l.localidade_desc,
        
        -- Dados do tipo de alerta
        tao.tipo AS tipo_alerta,
        tao.descricao AS descricao_tipo_alerta,
        
        -- Campos calculados para estatísticas
        CAST(a.data AS DATE) AS data_alarme,
        DATEPART(HOUR, a.data) AS hora_alarme,
        DATEPART(WEEKDAY, a.data) AS dia_semana_num,
        DATENAME(WEEKDAY, a.data) AS dia_semana_nome,
        DATEPART(DAY, a.data) AS dia_mes,
        DATEPART(MONTH, a.data) AS mes,
        DATEPART(YEAR, a.data) AS ano,
        
        -- Período do dia
        CASE 
            WHEN DATEPART(HOUR, a.data) BETWEEN 6 AND 11 THEN 'Manhã'
            WHEN DATEPART(HOUR, a.data) BETWEEN 12 AND 17 THEN 'Tarde'
            WHEN DATEPART(HOUR, a.data) BETWEEN 18 AND 23 THEN 'Noite'
            ELSE 'Madrugada'
        END AS periodo_dia,
        
        -- Faixa horária
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
    LEFT JOIN dbo.local l 
        ON vtr.id_local = l.id_local
        
    -- JOIN com tipo de alerta
    LEFT JOIN muralha.tipo_alerta_ocorrencia tao 
        ON a.id_tipo_alerta_ocorrencia = tao.id
        
    WHERE 
        CAST(a.data AS DATE) BETWEEN @DataInicio AND @DataFim
        AND (@IdLocal IS NULL OR vtr.id_local = @IdLocal)
        AND (@IdTipoAlerta IS NULL OR a.id_tipo_alerta_ocorrencia = @IdTipoAlerta);
    
    -- Relatório baseado no tipo solicitado
    IF @TipoRelatorio = 'COMPLETO'
    BEGIN
        -- Relatório completo com todas as estatísticas
        SELECT 
            'ESTATÍSTICAS GERAIS' AS secao,
            CAST(COUNT(*) AS VARCHAR) AS total_alarmes,
            CAST(COUNT(DISTINCT data_alarme) AS VARCHAR) AS dias_com_alarmes,
            CAST(COUNT(DISTINCT id_local) AS VARCHAR) AS locais_com_alarmes,
            CAST(COUNT(DISTINCT id_tipo_alerta_ocorrencia) AS VARCHAR) AS tipos_alarmes_distintos,
            CAST(CAST(COUNT(*) * 1.0 / NULLIF(COUNT(DISTINCT data_alarme), 0) AS DECIMAL(10,2)) AS VARCHAR) AS media_alarmes_por_dia
        FROM #DadosAlarmes;
        
        -- Distribuição por data
        SELECT 
            'DISTRIBUIÇÃO POR DATA' AS secao,
            CAST(COUNT(*) AS VARCHAR) + ' alarmes em ' + CAST(data_alarme AS VARCHAR) AS descricao,
            '' AS campo3, '' AS campo4, '' AS campo5
        FROM #DadosAlarmes
        GROUP BY data_alarme
        ORDER BY data_alarme;
        
    END
    ELSE IF @TipoRelatorio = 'RESUMO'
    BEGIN
        -- Resumo executivo
        SELECT 
            'RESUMO EXECUTIVO' AS titulo,
            COUNT(*) AS total_alarmes,
            MIN(data_alarme) AS primeira_ocorrencia,
            MAX(data_alarme) AS ultima_ocorrencia,
            COUNT(DISTINCT id_local) AS locais_distintos,
            COUNT(DISTINCT tipo_alerta) AS tipos_alarmes
        FROM #DadosAlarmes;
        
    END
    ELSE IF @TipoRelatorio = 'LOCALIZAÇÃO'
    BEGIN
        -- Estatísticas por localização
        SELECT 
            'Estatísticas por Local' AS categoria,
            ISNULL(nome_local, 'Local não identificado') AS local,
            id_local,
            COUNT(*) AS total_alarmes,
            COUNT(DISTINCT data_alarme) AS dias_com_alarmes,
            CAST(COUNT(*) * 1.0 / NULLIF(COUNT(DISTINCT data_alarme), 0) AS DECIMAL(10,2)) AS media_por_dia
        FROM #DadosAlarmes
        GROUP BY id_local, nome_local
        ORDER BY total_alarmes DESC;
        
    END
    ELSE IF @TipoRelatorio = 'TEMPORAL'
    BEGIN
        -- Análise temporal detalhada
        SELECT 
            'DISTRIBUIÇÃO TEMPORAL' AS analise,
            periodo_dia,
            dia_semana_nome,
            faixa_horaria,
            COUNT(*) AS quantidade_alarmes,
            CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
        FROM #DadosAlarmes
        GROUP BY periodo_dia, dia_semana_nome, faixa_horaria
        ORDER BY quantidade_alarmes DESC;
    END
    
    -- Sempre retornar estatísticas básicas complementares
    SELECT 
        'DISTRIBUIÇÃO POR PERÍODO DO DIA' AS categoria,
        periodo_dia,
        COUNT(*) AS quantidade,
        CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
    FROM #DadosAlarmes
    GROUP BY periodo_dia
    ORDER BY quantidade DESC;
    
    SELECT 
        'DISTRIBUIÇÃO POR DIA DA SEMANA' AS categoria,
        dia_semana_nome,
        COUNT(*) AS quantidade,
        CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
    FROM #DadosAlarmes
    GROUP BY dia_semana_num, dia_semana_nome
    ORDER BY dia_semana_num;
    
    SELECT 
        'DISTRIBUIÇÃO POR HORA' AS categoria,
        hora_alarme,
        COUNT(*) AS quantidade,
        CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
    FROM #DadosAlarmes
    GROUP BY hora_alarme
    ORDER BY hora_alarme;
    
    SELECT 
        'TOP 10 LOCAIS COM MAIS ALARMES' AS categoria,
        ISNULL(nome_local, 'Local não identificado') AS local,
        id_local,
        COUNT(*) AS total_alarmes
    FROM #DadosAlarmes
    GROUP BY id_local, nome_local
    ORDER BY total_alarmes DESC
    OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
    
    SELECT 
        'TIPOS DE ALARMES' AS categoria,
        ISNULL(tipo_alerta, 'Tipo não identificado') AS tipo,
        ISNULL(descricao_tipo_alerta, 'Sem descrição') AS descricao,
        COUNT(*) AS quantidade,
        CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() AS DECIMAL(5,2)) AS percentual
    FROM #DadosAlarmes
    GROUP BY tipo_alerta, descricao_tipo_alerta
    ORDER BY quantidade DESC;
    
    -- Limpar tabela temporária
    DROP TABLE #DadosAlarmes;
    
END;


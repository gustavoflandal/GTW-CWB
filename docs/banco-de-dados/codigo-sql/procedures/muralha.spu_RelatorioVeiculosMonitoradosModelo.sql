
-- ==================================================================================================
-- Procedure: Relatório de Veiculos Monitoradas por Modelo
-- Descrição: Cria um relatório com dados veiculos monitorados agrupados por modelos
-- Autor: Gustavo F. Landal
-- Data: 2025-09-27
-- Otimização V1: 
-- ===================================================================================================

CREATE     PROCEDURE [muralha].[spu_RelatorioVeiculosMonitoradosModelo]
    @data_inicio VARCHAR(10),
    @data_final VARCHAR(10)
WITH RECOMPILE 
AS
BEGIN
	--DECLARE @data_inicio VARCHAR(10) = '2025-09-02', @data_final VARCHAR(10) = '2025-10-02'
    SET NOCOUNT ON;
    
    -- Validação dos parâmetros
    IF @data_inicio IS NULL OR @data_final IS NULL OR @data_inicio = '' OR @data_final = ''
    BEGIN
        RAISERROR('Os parâmetros data_inicio e data_final são obrigatórios', 16, 1)
        RETURN
    END
    
    DECLARE @data_ini DATETIME, @data_fim DATETIME
    
    -- Converter strings para datetime
    SET @data_ini = CAST(@data_inicio + ' 00:00:00' AS DATETIME)
    SET @data_fim = CAST(@data_final + ' 23:59:59' AS DATETIME)
    
    IF @data_ini > @data_fim
    BEGIN
        RAISERROR('A data_inicio não pode ser maior que data_final', 16, 1)
        RETURN
    END

    IF OBJECT_ID('tempdb..#FatosFiltrados') IS NOT NULL DROP TABLE #FatosFiltrados;
    
    SELECT
        rf.id AS IdRegistroFato,
        rf.data_criacao,
        rf.data_encerramento,
        rf.id_tipo,
        rft.tipo_desc AS TipoFatoDesc,
		rfv.placa,
        rfv.cor,
        rfv.marca,
        rfv.modelo,
        cvm.descricao AS DescricaoMonitoramento,
        cvm.data_inicio AS DataInicioMonitoramento,
        cvm.data_fim AS DataFimMonitoramento
    INTO #FatosFiltrados -- Cria e insere na tabela temporária
    FROM muralha.registro_fato AS rf
    INNER JOIN muralha.registro_fato_tipo AS rft ON rf.id_tipo = rft.id
    INNER JOIN muralha.registro_fato_veiculo AS rfv ON rf.id = rfv.id_registro_fato
    INNER JOIN muralha.cad_veiculo_monitorado AS cvm ON rfv.id_registro_fato = cvm.id_registro_fato AND cvm.placa = rfv.placa
    WHERE rf.data_criacao >= @data_ini
      AND rf.data_criacao <= @data_fim
      AND cvm.data_exclusao IS NULL;

    DECLARE @TotalFatos INT;
    SELECT @TotalFatos = COUNT(IdRegistroFato) FROM #FatosFiltrados;

    --------------------------------------------------------------------------------
    -- RECORDSET 1: VeiculosMonitorados - Relatório principal
    --------------------------------------------------------------------------------
    SELECT 
        ff.IdRegistroFato,
        CONVERT(VARCHAR(19), ff.data_criacao, 120) AS DataCriacao,
        ff.TipoFatoDesc AS TipoFato,
        ff.placa AS Placa,
        ISNULL(ff.cor, '') AS Cor,
        ISNULL(ff.marca, '') AS Marca,
        ISNULL(ff.modelo, '') AS Modelo,
        ISNULL(ff.DescricaoMonitoramento, '') AS DescricaoMonitoramento,
        ISNULL(CONVERT(VARCHAR(10), ff.DataInicioMonitoramento, 120), '') AS DataInicioMonitoramento,
        ISNULL(CONVERT(VARCHAR(10), ff.DataFimMonitoramento, 120), '') AS DataFimMonitoramento,
        CASE 
            WHEN ff.data_encerramento IS NULL THEN 'ABERTO'
            ELSE 'ENCERRADO'
        END AS StatusFato,
        DATEDIFF(DAY, ff.data_criacao, ISNULL(ff.data_encerramento, GETDATE())) AS DiasEmAndamento
    FROM #FatosFiltrados AS ff -- Usando a tabela temporária
    ORDER BY ff.data_criacao DESC, ff.placa;

    --------------------------------------------------------------------------------
    -- RECORDSET 2: HistogramaTiposFatos - Distribuição dos tipos de fatos
    --------------------------------------------------------------------------------
    SELECT 
        ff.TipoFatoDesc AS TipoFato,
        COUNT(*) AS QuantidadeFatos,
        -- Usando a variável @TotalFatos
        CAST(COUNT(*) * 100.0 / NULLIF(@TotalFatos, 0) AS DECIMAL(5,2)) AS PercentualFatos
    FROM #FatosFiltrados AS ff -- Usando a tabela temporária
    GROUP BY ff.id_tipo, ff.TipoFatoDesc
    ORDER BY QuantidadeFatos DESC;

    --------------------------------------------------------------------------------
    -- RECORDSET 3: HistogramaModelosVeiculos - Modelos mais monitorados
    --------------------------------------------------------------------------------
    SELECT 
        ISNULL(ff.marca, 'NAO INFORMADO') AS Marca,
        ISNULL(ff.modelo, 'NAO INFORMADO') AS Modelo,
        COUNT(DISTINCT ff.placa) AS QuantidadeVeiculosUnicos,
        COUNT(*) AS QuantidadeFatos,
        -- Usando a variável @TotalFatos
        CAST(COUNT(*) * 100.0 / NULLIF(@TotalFatos, 0) AS DECIMAL(5,2)) AS PercentualFatos
    FROM #FatosFiltrados AS ff -- Usando a tabela temporária
    GROUP BY ff.marca, ff.modelo
    ORDER BY QuantidadeFatos DESC;

    --------------------------------------------------------------------------------
    -- RECORDSET 4: ResumoEstatistico - Estatísticas gerais
    --------------------------------------------------------------------------------
    SELECT 
        COUNT(DISTINCT ff.placa) AS TotalVeiculosMonitorados,
        COUNT(*) AS TotalFatosRegistrados,
        COUNT(DISTINCT ff.id_tipo) AS TiposFatosDistintos,
        COUNT(DISTINCT CONCAT(ISNULL(ff.marca,''), ' ', ISNULL(ff.modelo,''))) AS ModelosVeiculosDistintos,
        ISNULL(CONVERT(VARCHAR(19), MIN(ff.data_criacao), 120), '') AS PrimeiroFatoRegistrado,
        ISNULL(CONVERT(VARCHAR(19), MAX(ff.data_criacao), 120), '') AS UltimoFatoRegistrado,
        ISNULL(CAST(AVG(CAST(DATEDIFF(DAY, ff.data_criacao, ISNULL(ff.data_encerramento, GETDATE())) AS FLOAT)) AS DECIMAL(10,2)), 0) AS MediaDiasAndamento
    FROM #FatosFiltrados AS ff; -- Usando a tabela temporária

    --------------------------------------------------------------------------------
    -- RECORDSET 5: TopPlacasOcorrencias - Top 10 placas com mais ocorrências
    --------------------------------------------------------------------------------
    SELECT TOP 10
        ff.placa AS Placa,
        ISNULL(ff.marca, '') AS Marca,
        ISNULL(ff.modelo, '') AS Modelo,
        COUNT(*) AS QuantidadeOcorrencias,
        -- Subconsulta ajustada para usar a #FatosFiltrados
        STUFF((
            SELECT DISTINCT ', ' + ff_sub.TipoFatoDesc
            FROM #FatosFiltrados ff_sub
            WHERE ff_sub.placa = ff.placa
            FOR XML PATH(''), TYPE
        ).query('.').value('.', 'VARCHAR(MAX)'), 1, 2, '') AS TiposFatosEnvolvidos,
        CONVERT(VARCHAR(19), MIN(ff.data_criacao), 120) AS PrimeiraOcorrencia,
        CONVERT(VARCHAR(19), MAX(ff.data_criacao), 120) AS UltimaOcorrencia
    FROM #FatosFiltrados AS ff -- Usando a tabela temporária
    GROUP BY ff.placa, ff.marca, ff.modelo
    ORDER BY QuantidadeOcorrencias DESC;
END

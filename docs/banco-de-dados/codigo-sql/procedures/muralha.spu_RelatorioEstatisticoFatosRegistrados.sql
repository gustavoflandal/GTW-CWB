
CREATE   PROCEDURE [muralha].[spu_RelatorioEstatisticoFatosRegistrados]
    @DataInicio DATE = NULL,
    @DataFim DATE = NULL
WITH RECOMPILE 
AS
BEGIN
    SET NOCOUNT ON;

    -- Definir datas padrão se não informadas
    IF @DataInicio IS NULL
        SET @DataInicio = DATEADD(DAY, -30, GETDATE());
    
    IF @DataFim IS NULL
        SET @DataFim = GETDATE();

    -- Dados estatísticos por tipo de fato registrado
    SELECT 
        rft.tipo_desc AS TipoFato,
        COUNT(rf.id) AS TotalRegistros,
        COUNT(DISTINCT rf.id_usuario) AS UsuariosEnvolvidos,
        AVG(DATEDIFF(HOUR, rf.data_criacao, COALESCE(rf.data_encerramento, GETDATE()))) AS TempoMedioHoras,
        SUM(CASE WHEN rf.tem_boletim = 1 THEN 1 ELSE 0 END) AS ComBoletim,
        SUM(CASE WHEN rf.privado = 1 THEN 1 ELSE 0 END) AS RegistrosPrivados
    FROM muralha.registro_fato rf
    INNER JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    WHERE CAST(rf.data_criacao AS DATE) BETWEEN @DataInicio AND @DataFim
    GROUP BY rft.tipo_desc
    ORDER BY TotalRegistros DESC;

    -- Dados de georreferenciamento com veículos
    SELECT 
        rft.tipo_desc AS TipoFato,
        c.nome AS Cidade,
        e.nome AS Estado,
        COUNT(rf.id) AS TotalRegistros,
        COUNT(DISTINCT vtr.placa) AS VeiculosUnicos,
        AVG(vtr.velocidade) AS VelocidadeMedia,
        COUNT(DISTINCT rfe.id) AS LocalizacoesGeoreferenciadas
    FROM muralha.registro_fato rf
    INNER JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    LEFT JOIN muralha.registro_fato_endereco rfe ON rf.id = rfe.id_registro_fato
    LEFT JOIN muralha.cidade c ON rfe.id_cidade = c.id
    LEFT JOIN muralha.estado e ON c.id_estado = e.id
    LEFT JOIN muralha.registro_fato_passagem_veic rfpv ON rf.id = rfpv.id_registro_fato
    LEFT JOIN muralha.veiculo_tempo_real vtr ON rfpv.id_veiculo = vtr.id
    WHERE CAST(rf.data_criacao AS DATE) BETWEEN @DataInicio AND @DataFim
    GROUP BY rft.tipo_desc, c.nome, e.nome
    ORDER BY TotalRegistros DESC;

    -- Histograma semanal por dia da semana
    SELECT 
        rft.tipo_desc AS TipoFato,
        DATEPART(WEEKDAY, rf.data_criacao) AS DiaSemana,
        CASE DATEPART(WEEKDAY, rf.data_criacao)
            WHEN 1 THEN 'Domingo'
            WHEN 2 THEN 'Segunda-feira'
            WHEN 3 THEN 'Terça-feira'
            WHEN 4 THEN 'Quarta-feira'
            WHEN 5 THEN 'Quinta-feira'
            WHEN 6 THEN 'Sexta-feira'
            WHEN 7 THEN 'Sábado'
        END AS NomeDiaSemana,
        COUNT(rf.id) AS TotalRegistros
    FROM muralha.registro_fato rf
    INNER JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    WHERE CAST(rf.data_criacao AS DATE) BETWEEN @DataInicio AND @DataFim
    GROUP BY rft.tipo_desc, DATEPART(WEEKDAY, rf.data_criacao)
    ORDER BY rft.tipo_desc, DiaSemana;

    -- Histograma por intervalos de hora
    SELECT 
        rft.tipo_desc AS TipoFato,
        DATEPART(HOUR, rf.data_criacao) AS Hora,
        CASE 
            WHEN DATEPART(HOUR, rf.data_criacao) BETWEEN 0 AND 5 THEN 'Madrugada (0h-6h)'
            WHEN DATEPART(HOUR, rf.data_criacao) BETWEEN 6 AND 11 THEN 'Manhã (6h-12h)'
            WHEN DATEPART(HOUR, rf.data_criacao) BETWEEN 12 AND 17 THEN 'Tarde (12h-18h)'
            WHEN DATEPART(HOUR, rf.data_criacao) BETWEEN 18 AND 23 THEN 'Noite (18h-24h)'
        END AS Periodo,
        COUNT(rf.id) AS TotalRegistros
    FROM muralha.registro_fato rf
    INNER JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    WHERE CAST(rf.data_criacao AS DATE) BETWEEN @DataInicio AND @DataFim
    GROUP BY rft.tipo_desc, DATEPART(HOUR, rf.data_criacao)
    ORDER BY rft.tipo_desc, Hora;

    -- Dados detalhados para análise cruzada
    SELECT 
        rft.tipo_desc AS TipoFato,
        DATEPART(WEEKDAY, rf.data_criacao) AS DiaSemana,
        DATEPART(HOUR, rf.data_criacao) AS Hora,
        c.nome AS Cidade,
        COUNT(rf.id) AS TotalRegistros,
        COUNT(DISTINCT vtr.placa) AS VeiculosEnvolvidos,
        AVG(vtr.velocidade) AS VelocidadeMedia
    FROM muralha.registro_fato rf
    INNER JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    LEFT JOIN muralha.registro_fato_endereco rfe ON rf.id = rfe.id_registro_fato
    LEFT JOIN muralha.cidade c ON rfe.id_cidade = c.id
    LEFT JOIN muralha.registro_fato_passagem_veic rfpv ON rf.id = rfpv.id_registro_fato
    LEFT JOIN muralha.veiculo_tempo_real vtr ON rfpv.id_veiculo = vtr.id
    WHERE CAST(rf.data_criacao AS DATE) BETWEEN @DataInicio AND @DataFim
    GROUP BY rft.tipo_desc, DATEPART(WEEKDAY, rf.data_criacao), DATEPART(HOUR, rf.data_criacao), c.nome
    ORDER BY TotalRegistros DESC;
END;

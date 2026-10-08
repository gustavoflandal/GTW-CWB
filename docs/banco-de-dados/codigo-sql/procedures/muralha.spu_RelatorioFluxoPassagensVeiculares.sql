
-- ==================================================================================================
-- Procedure: Relatório de 
-- Descrição: Cria um relatório com dados dos fluxos de passagem de veículos pelos 
-- pontos de coleta 
-- Autor: Gustavo F. Landal
-- Data: 2025-09-12
-- Otimização V1: 
-- ===================================================================================================

CREATE     PROCEDURE [muralha].[spu_RelatorioFluxoPassagensVeiculares]
     @id_local INT = NULL,
     @data_inicio DATE = NULL,
     @data_fim DATE = NULL
WITH RECOMPILE 
 AS
 BEGIN
     SET NOCOUNT ON;
 
     IF @data_inicio IS NULL
         SET @data_inicio = DATEADD(DAY, -30, GETDATE());
     
     IF @data_fim IS NULL
         SET @data_fim = GETDATE();
 
     -- Primeiro recordset: Fluxo por hora
     SELECT
         CAST(v.data AS DATE) AS data,
         cep.id_local,
         cep.nome AS nome_local,
         cep.sentido,
         DATEPART(HOUR, v.data) AS hora,
         cv.descricao AS classificacao_veiculo,
         COUNT(*) AS total_passagens
     FROM muralha.veiculo_tempo_real v
     INNER JOIN dbo.v_local_pista_vigente cep 
		ON v.id_local = cep.id_local AND v.id_pista = cep.id_pista
     INNER JOIN dbo.classe_veiculo cv 
		ON v.classificacao = cv.id_classe
     WHERE (@id_local IS NULL OR v.id_local = @id_local)
         AND CAST(v.data AS DATE) BETWEEN @data_inicio AND @data_fim
     GROUP BY
         CAST(v.data AS DATE),
         cep.id_local,
         cep.nome,
         cep.nome,
         cep.sentido,
         DATEPART(HOUR, v.data),
         cv.descricao
     ORDER BY CAST(v.data AS DATE), DATEPART(HOUR, v.data)
 
     -- Segundo recordset: Resumo por classificação de veículos
     SELECT
         l.id_local,
         RTRIM(l.nome) AS nome_local,
         cv.descricao AS classificacao_veiculo,
         COUNT(*) AS total_passagens,
         CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (PARTITION BY l.id_local) AS DECIMAL(5,2)) AS percentual
     FROM muralha.veiculo_tempo_real v
     INNER JOIN dbo.v_local_pista_vigente l ON v.id_local = l.id_local AND l.id_pista = v.id_pista
     INNER JOIN dbo.classe_veiculo cv ON v.classificacao = cv.id_classe
     WHERE (@id_local IS NULL OR v.id_local = @id_local)
         AND CAST(v.data AS DATE) BETWEEN @data_inicio AND @data_fim
     GROUP BY
         l.id_local,
         l.nome,
         cv.descricao
     ORDER BY l.nome, cv.descricao
 
     ---- Terceiro recordset: Distribuição horária
     SELECT
         CAST(v.data AS DATE) as data,
         l.id_local,
         RTRIM(l.nome) AS nome_local,
         DATEPART(HOUR, v.data) AS hora,
         COUNT(*) AS total_passagens
     FROM muralha.veiculo_tempo_real v
     INNER JOIN dbo.v_local_pista_vigente l ON v.id_local = l.id_local AND l.id_pista = v.id_pista
     WHERE (@id_local IS NULL OR v.id_local = @id_local)
         AND CAST(v.data AS DATE) BETWEEN @data_inicio AND @data_fim
     GROUP BY
         CAST(v.data AS DATE),
         l.id_local,
         l.nome,
         DATEPART(HOUR, v.data)
     ORDER BY l.id_local, CAST(v.data AS DATE), DATEPART(HOUR, v.data)
 
     ---- Quarto recordset: Distribuição por sentido
     SELECT
         l.id_local,
         l.nome AS nome_local,
         cep.sentido,
         COUNT(*) AS total_passagens,
         CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (PARTITION BY l.id_local) AS DECIMAL(5,2)) AS percentual
     FROM muralha.veiculo_tempo_real v
     INNER JOIN dbo.local l ON v.id_local = l.id_local
     INNER JOIN dbo.v_local_pista_vigente cep ON v.id_local = cep.id_local AND v.id_pista = cep.id_pista
     WHERE (@id_local IS NULL OR v.id_local = @id_local)
         AND CAST(v.data AS DATE) BETWEEN @data_inicio AND @data_fim
     GROUP BY
         l.id_local,
         l.nome,
         cep.sentido
     ORDER BY l.nome, cep.sentido
 END;

--exec muralha.spu_RelatorioFluxoPassagensVeiculares NULL,'2025-09-20','2025-09-20'

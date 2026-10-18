-- ============================================================
-- Migração: nome e coordenadas do equipamento pela versão vigente na data do registro
-- Data: 2026-10-17 (Plano 23 — complementa 20261017_analise_correcao_camada.sql)
--
-- Por quê: dbo.local é versionada (sequencia_local, data_atualizacao) e o mesmo id_local
-- é reaproveitado em outro local ao longo do tempo. Exemplos no DEV:
--   - id_local 1: Av. Antônio Olinto (Sete Lagoas) até 2026-08-11; depois Av. Ernesto Viléla (PR)
--   - id_local 38: Av. Norte Sul (Sete Lagoas) até 2026-07-28; depois "Teste RJ - Av Americas"
-- Rotular o histórico com a versão mais recente atribuía passagens de Sete Lagoas ao RJ.
--
-- O que este script faz (idempotente; só objetos do schema analise):
--   1. View analise.v_local_versao: cada versão com vigência [inicio, fim), nome e coordenadas
--      (sem coordenada na versão, usa a da versão anterior com o mesmo nome)
--   2. Cargas spu_carga_passagens_hora e spu_carga_infracoes_dia passam a usar a versão
--      vigente na data do registro
--   3. v_disponibilidade_dia agrupa por equipamento e local (versão), com o calendário de cada
--      local do primeiro ao último dia com dados
--   4. v_preprocessamento usa a versão da própria infração (sequencia_local)
--   5. Recarga completa de passagens (desde 2025-06-01) e infrações
--
-- v_local_vigente (versão atual) continua usada por v_equipamentos (situação atual).
--
-- Executar com: sqlcmd -b -I -f 65001 -i <arquivo>   (somente com autorização humana)
-- Rollback: reexecutar as definições de 20261017_analise_correcao_camada.sql (seções 2, 4 e 5),
--           recarregar (seção 8 daquele script) e DROP VIEW analise.v_local_versao.
-- ============================================================
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Versões do equipamento com vigência ------------------------------------------
CREATE OR ALTER VIEW analise.v_local_versao AS
SELECT
    l.id_local,
    l.sequencia_local,
    CASE WHEN l.sequencia_local = p.seq_min THEN CAST('19000101' AS DATETIME)
         ELSE ISNULL(l.data_atualizacao, CAST('19000101' AS DATETIME)) END             AS vigencia_inicio,
    ISNULL(LEAD(l.data_atualizacao) OVER (PARTITION BY l.id_local ORDER BY l.sequencia_local),
           CAST('99991231' AS DATETIME))                                              AS vigencia_fim,
    RTRIM(l.nome)                                                                     AS nome,
    l.tipo,
    RTRIM(l.localidade_desc)                                                          AS localidade,
    ISNULL(l.posicao_lat, c.lat)                                                      AS lat,
    ISNULL(l.posicao_lon, c.lon)                                                      AS lon
FROM dbo.local l
CROSS APPLY (SELECT MIN(m.sequencia_local) AS seq_min FROM dbo.local m WHERE m.id_local = l.id_local) p
OUTER APPLY (
    SELECT TOP 1 x.posicao_lat AS lat, x.posicao_lon AS lon
    FROM dbo.local x
    WHERE x.id_local = l.id_local AND x.sequencia_local < l.sequencia_local
      AND x.posicao_lat IS NOT NULL AND x.posicao_lon IS NOT NULL
      AND RTRIM(x.nome) = RTRIM(l.nome)
    ORDER BY x.sequencia_local DESC
) c;
GO
PRINT 'View analise.v_local_versao criada/atualizada';
GO

-- 2a. Carga de passagens por hora ---------------------------------------------------
CREATE OR ALTER PROCEDURE analise.spu_carga_passagens_hora
    @horas     INT          = 25,
    @dt_inicio DATETIME2(0) = NULL,
    @dt_fim    DATETIME2(0) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dt_exec DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        IF @dt_inicio IS NULL
            SET @dt_inicio = DATEADD(HOUR, -@horas,
                DATEADD(HOUR, DATEDIFF(HOUR, 0, SYSDATETIME()), CAST('00:00' AS DATETIME2(0))));
        IF @dt_fim IS NULL
            SET @dt_fim = DATEADD(HOUR, 1, CAST(SYSDATETIME() AS DATETIME2(0)));

        DELETE FROM analise.mat_passagens_hora
        WHERE hora >= @dt_inicio AND hora < @dt_fim;

        INSERT INTO analise.mat_passagens_hora
            (hora, id_local, local_nome, faixa, classificacao, placa_mercosul,
             passagens, soma_velocidade, lat, lon)
        SELECT
            a.hora, a.id_local,
            ISNULL(lv.nome, 'Equipamento ' + CAST(a.id_local AS varchar(10))),
            a.faixa, a.classificacao, a.placa_mercosul,
            a.passagens, a.soma_velocidade, lv.lat, lv.lon
        FROM (
            SELECT
                CAST(DATEADD(HOUR, DATEDIFF(HOUR, 0, vtr.data), 0) AS DATETIME2(0)) AS hora,
                vtr.id_local,
                ISNULL(vtr.id_pista, 0)         AS faixa,
                ISNULL(vtr.classificacao, ' ')  AS classificacao,
                ISNULL(vtr.placa_mercosul, 0)   AS placa_mercosul,
                COUNT(*)                        AS passagens,
                SUM(CAST(ISNULL(vtr.velocidade, 0) AS BIGINT)) AS soma_velocidade
            FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
            WHERE vtr.data >= @dt_inicio AND vtr.data < @dt_fim
            GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, vtr.data), 0), vtr.id_local,
                     ISNULL(vtr.id_pista, 0), ISNULL(vtr.classificacao, ' '), ISNULL(vtr.placa_mercosul, 0)
        ) a
        LEFT JOIN analise.v_local_versao lv
            ON lv.id_local = a.id_local
           AND a.hora >= lv.vigencia_inicio AND a.hora < lv.vigencia_fim;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_passagens_hora', @dt_exec, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;
GO

-- 2b. Carga de infrações por dia -----------------------------------------------------
CREATE OR ALTER PROCEDURE analise.spu_carga_infracoes_dia
    @dias      INT  = 7,
    @dt_inicio DATE = NULL,
    @dt_fim    DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dt_exec DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        IF @dt_inicio IS NULL SET @dt_inicio = DATEADD(DAY, -@dias, CAST(SYSDATETIME() AS DATE));
        IF @dt_fim IS NULL SET @dt_fim = DATEADD(DAY, 1, CAST(SYSDATETIME() AS DATE));

        DELETE FROM analise.mat_infracoes_dia
        WHERE dia >= @dt_inicio AND dia < @dt_fim;

        INSERT INTO analise.mat_infracoes_dia
            (dia, id_local, local_nome, faixa, id_enquadramento, enquadramento,
             limite, total, excesso_medio, lat, lon)
        SELECT
            a.dia, a.id_local,
            ISNULL(lv.nome, 'Equipamento ' + CAST(a.id_local AS varchar(10))),
            a.faixa, a.id_enquadramento, ISNULL(RTRIM(e.descricao), 'N/D'),
            a.limite, a.total, a.excesso_medio, lv.lat, lv.lon
        FROM (
            SELECT
                CAST(i.data AS DATE)     AS dia,
                i.id_local,
                ISNULL(i.pista, 0)       AS faixa,
                i.id_enquadramento,
                MAX(i.velocidade_limite) AS limite,
                COUNT(*)                 AS total,
                CAST(AVG(CASE WHEN i.velocidade_considerada > i.velocidade_limite
                              THEN i.velocidade_considerada - i.velocidade_limite END) AS DECIMAL(6,2)) AS excesso_medio
            FROM dbo.infracao i
            WHERE i.data >= @dt_inicio AND i.data < @dt_fim
            GROUP BY CAST(i.data AS DATE), i.id_local, ISNULL(i.pista, 0), i.id_enquadramento
        ) a
        LEFT JOIN analise.v_local_versao lv
            ON lv.id_local = a.id_local
           AND CAST(a.dia AS DATETIME) >= lv.vigencia_inicio AND CAST(a.dia AS DATETIME) < lv.vigencia_fim
        LEFT JOIN dbo.enquadramento e ON e.id_enquadramento = a.id_enquadramento;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_infracoes_dia', @dt_exec, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;
GO
PRINT 'Procedures de carga atualizadas (versão vigente na data)';
GO

-- 3. Disponibilidade diária por equipamento e local --------------------------------
-- Agrega primeiro por dia/equipamento/versão (4 milhões de linhas) e só depois resolve o nome;
-- num dia com duas versões do mesmo local, vale o maior número de horas.
CREATE OR ALTER VIEW analise.v_disponibilidade_dia AS
WITH horas_versao AS (
    SELECT s.data AS dia, s.id_local, s.sequencia_local, COUNT(DISTINCT s.hora) AS horas_captura
    FROM dbo.veiculo_sumarizado s
    GROUP BY s.data, s.id_local, s.sequencia_local
),
horas AS (
    SELECT
        hv.dia,
        hv.id_local,
        ISNULL(lv.nome, 'Equipamento ' + CAST(hv.id_local AS varchar(10))) AS local_nome,
        MAX(hv.horas_captura) AS horas_captura
    FROM horas_versao hv
    LEFT JOIN analise.v_local_versao lv
        ON lv.id_local = hv.id_local AND lv.sequencia_local = hv.sequencia_local
    GROUP BY hv.dia, hv.id_local, ISNULL(lv.nome, 'Equipamento ' + CAST(hv.id_local AS varchar(10)))
),
dias_sistema AS (
    SELECT DISTINCT dia FROM horas
),
faixa AS (
    SELECT id_local, local_nome, MIN(dia) AS d0, MAX(dia) AS d1
    FROM horas GROUP BY id_local, local_nome
)
SELECT
    d.dia,
    f.id_local,
    f.local_nome,
    24                                                            AS verificacoes,
    ISNULL(h.horas_captura, 0)                                    AS verificacoes_ok,
    CAST(100.0 * ISNULL(h.horas_captura, 0) / 24 AS DECIMAL(5,2)) AS pct_disponibilidade,
    (24 - ISNULL(h.horas_captura, 0)) * 60                        AS max_minutos_offline
FROM faixa f
JOIN dias_sistema d ON d.dia BETWEEN f.d0 AND f.d1
LEFT JOIN horas h ON h.id_local = f.id_local AND h.local_nome = f.local_nome AND h.dia = d.dia;
GO
PRINT 'View analise.v_disponibilidade_dia atualizada';
GO

-- 4. Pré-processamento com a versão da própria infração -----------------------------
CREATE OR ALTER VIEW analise.v_preprocessamento AS
SELECT
    CAST(i.data_entrada AS DATE)                                       AS dia,
    i.id_local,
    ISNULL(lv.nome, 'Equipamento ' + CAST(i.id_local AS varchar(10)))  AS local_nome,
    i.id_usuario_final                                                 AS id_operador,
    ISNULL(RTRIM(u.usuario), 'Processamento automático')               AS operador,
    COUNT(*)                                                           AS recebidos,
    SUM(CASE WHEN i.id_processo_concluido IS NOT NULL THEN 1 ELSE 0 END) AS preprocessados,
    SUM(CASE WHEN i.id_processo_concluido IS NOT NULL AND ISNULL(i.id_inconsistencia, 0) = 0
             THEN 1 ELSE 0 END)                                        AS disponibilizados,
    SUM(CASE WHEN ISNULL(i.id_inconsistencia, 0) <> 0 THEN 1 ELSE 0 END) AS descartados,
    CAST(100.0 * SUM(CASE WHEN i.id_processo_concluido IS NOT NULL AND ISNULL(i.id_inconsistencia, 0) = 0
                          THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) AS DECIMAL(5,2)) AS pct_aproveitamento,
    AVG(CASE WHEN i.data_entrada >= i.data THEN DATEDIFF(MINUTE, i.data, i.data_entrada) END) AS tempo_medio_min,
    COUNT(*)                                                           AS produtividade,
    CAST(NULL AS INT)                                                  AS divergencias,
    CAST(NULL AS INT)                                                  AS retrabalho,
    SUM(CASE WHEN i.id_processo_concluido IS NULL THEN 1 ELSE 0 END)   AS pendentes,
    SUM(CASE WHEN i.id_processo_concluido IS NULL AND i.data < DATEADD(HOUR, -24, SYSDATETIME())
             THEN 1 ELSE 0 END)                                        AS backlog_24h,
    CAST(100.0 * SUM(CASE WHEN i.data_entrada >= i.data AND DATEDIFF(HOUR, i.data, i.data_entrada) <= 72
                          THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) AS DECIMAL(5,2)) AS pct_sla_72h
FROM dbo.infracao i
LEFT JOIN analise.v_local_versao lv ON lv.id_local = i.id_local AND lv.sequencia_local = i.sequencia_local
LEFT JOIN dbo.sis_usuario u ON u.id_usuario = i.id_usuario_final
WHERE i.id_enquadramento <> 1
GROUP BY CAST(i.data_entrada AS DATE), i.id_local, lv.nome, i.id_usuario_final, u.usuario;
GO
PRINT 'View analise.v_preprocessamento atualizada';
GO

-- 5. Recarga completa ---------------------------------------------------------------
DECLARE @ini DATETIME2(0) = '2025-06-01';
DECLARE @fim DATETIME2(0);
DECLARE @limite DATETIME2(0) = DATEADD(HOUR, 1, CAST(SYSDATETIME() AS DATETIME2(0)));
WHILE @ini < @limite
BEGIN
    SET @fim = DATEADD(MONTH, 1, @ini);
    EXEC analise.spu_carga_passagens_hora @dt_inicio = @ini, @dt_fim = @fim;
    SET @ini = @fim;
END
EXEC analise.spu_carga_infracoes_dia @dt_inicio = '2025-01-01';
GO

SELECT local_nome, MIN(hora) AS de, MAX(hora) AS ate, SUM(passagens) AS passagens
FROM analise.mat_passagens_hora
WHERE id_local IN (1, 38)
GROUP BY local_nome
ORDER BY MIN(hora);
GO

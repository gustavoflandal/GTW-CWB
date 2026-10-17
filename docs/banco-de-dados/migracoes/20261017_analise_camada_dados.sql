SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

-- =============================================================================
-- SUP-2 / Plano 23 — Camada de dados analitica: views, materializacoes e carga
-- Todas as views e tabelas ficam no schema analise (criado na SUP-1).
-- Nenhum DDL/DML em tabelas existentes. Banco do GTW somente leitura.
--
-- ROLLBACK (ordem inversa):
--   DROP PROCEDURE analise.spu_carga_infracoes_dia;
--   DROP PROCEDURE analise.spu_carga_passagens_hora;
--   DROP VIEW analise.v_preprocessamento;
--   DROP VIEW analise.v_assertividade_dia;
--   DROP VIEW analise.v_sla_preproc_dia;
--   DROP VIEW analise.v_sla_latencia_dia;
--   DROP VIEW analise.v_disponibilidade_dia;
--   DROP VIEW analise.v_areas_monitoradas_geojson;
--   DROP VIEW analise.v_equipamentos;
--   DROP VIEW analise.v_alertas;
--   DROP TABLE analise.mat_infracoes_dia;
--   DROP TABLE analise.mat_passagens_hora;
-- =============================================================================

-- ═══════════════════════════════════════════════════════════════════════════════
-- 1. analise.mat_passagens_hora — tabela materializada de passagens por hora
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.mat_passagens_hora', 'U') IS NULL
BEGIN
    CREATE TABLE analise.mat_passagens_hora (
        hora                DATETIME2(0)  NOT NULL,
        id_local            INT           NOT NULL,
        local_nome          VARCHAR(100)  NOT NULL,
        faixa               TINYINT       NOT NULL,
        classificacao       CHAR(1)       NOT NULL,
        placa_mercosul      BIT           NOT NULL,
        passagens           INT           NOT NULL,
        soma_velocidade     BIGINT        NOT NULL,
        velocidade_media    AS CASE WHEN passagens > 0
                               THEN CAST(soma_velocidade AS DECIMAL(10,2)) / passagens
                               ELSE 0 END PERSISTED,
        lat                 DECIMAL(10,7) NULL,
        lon                 DECIMAL(10,7) NULL,
        CONSTRAINT PK_mat_passagens_hora
            PRIMARY KEY CLUSTERED (hora, id_local, faixa, classificacao, placa_mercosul)
    );

    CREATE NONCLUSTERED INDEX IX_mph_local ON analise.mat_passagens_hora (id_local, hora);
    PRINT 'Criada: analise.mat_passagens_hora';
END
ELSE PRINT 'Ja existe: analise.mat_passagens_hora';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 2. analise.mat_infracoes_dia — tabela materializada de infracoes por dia
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.mat_infracoes_dia', 'U') IS NULL
BEGIN
    CREATE TABLE analise.mat_infracoes_dia (
        dia                 DATE          NOT NULL,
        id_local            INT           NOT NULL,
        local_nome          VARCHAR(100)  NOT NULL,
        faixa               TINYINT       NOT NULL,
        id_enquadramento    INT           NOT NULL,
        enquadramento       VARCHAR(200)  NOT NULL,
        limite              INT           NULL,
        total               INT           NOT NULL,
        excesso_medio       DECIMAL(6,2)  NULL,
        lat                 DECIMAL(10,7) NULL,
        lon                 DECIMAL(10,7) NULL,
        CONSTRAINT PK_mat_infracoes_dia
            PRIMARY KEY CLUSTERED (dia, id_local, faixa, id_enquadramento)
    );

    CREATE NONCLUSTERED INDEX IX_mid_local ON analise.mat_infracoes_dia (id_local, dia);
    PRINT 'Criada: analise.mat_infracoes_dia';
END
ELSE PRINT 'Ja existe: analise.mat_infracoes_dia';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 3. analise.v_alertas — sem dados pessoais (LGPD: sem placa)
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_alertas', 'V') IS NOT NULL
    DROP VIEW analise.v_alertas;
GO

CREATE VIEW analise.v_alertas AS
SELECT
    a.data                          AS dt_alerta,
    tao.tipo                        AS tipo,
    tao.descricao                   AS tipo_descricao,
    ISNULL(tac.critico, 0)          AS critico,
    COALESCE(tao.prioridade, 50)    AS prioridade,
    sa.descricao                    AS status,
    a.origem,
    CASE WHEN a.id_motivo_descarte IS NOT NULL THEN 1 ELSE 0 END AS descartado,
    md.descricao                    AS motivo_descarte,
    a.data_descarte,
    CASE WHEN a.id_usuario IS NOT NULL THEN 1 ELSE 0 END AS tratado,
    u.usuario                       AS usuario_responsavel,
    a.observacao
FROM muralha.alerta a
JOIN muralha.tipo_alerta_ocorrencia tao
    ON tao.id = a.id_tipo_alerta_ocorrencia
LEFT JOIN muralha.status_alerta sa
    ON sa.id = a.id_status_alerta
LEFT JOIN muralha.motivo_descarte md
    ON md.id = a.id_motivo_descarte
LEFT JOIN muralha.tipo_alerta_config tac
    ON tac.id_tipo_alerta_ocorrencia = tao.id
LEFT JOIN dbo.sis_usuario u
    ON u.id_usuario = a.id_usuario;
GO

PRINT 'Criada: analise.v_alertas';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 4. analise.v_equipamentos — cadastro de locais com coordenadas e area
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_equipamentos', 'V') IS NOT NULL
    DROP VIEW analise.v_equipamentos;
GO

CREATE VIEW analise.v_equipamentos AS
SELECT
    l.id_local,
    RTRIM(l.nome)                   AS nome,
    l.posicao_lat                   AS lat,
    l.posicao_lon                   AS lon,
    l.tipo,
    CASE WHEN EXISTS (
        SELECT 1 FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
        WHERE vtr.id_local = l.id_local AND vtr.data >= DATEADD(HOUR, -24, SYSDATETIME())
    ) THEN 1 ELSE 0 END            AS ativo_24h,
    am.nome                         AS area_monitorada
FROM dbo.local l
LEFT JOIN muralha.equipamentos_area_monitorada eam
    ON eam.id_equipamento = l.id_local
LEFT JOIN muralha.area_monitorada am
    ON am.id = eam.id_area_monitorada AND am.deletado = 0
WHERE l.sequencia_local = 1;
GO

PRINT 'Criada: analise.v_equipamentos';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 5. analise.v_areas_monitoradas_geojson — poligonos para deck.gl
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_areas_monitoradas_geojson', 'V') IS NOT NULL
    DROP VIEW analise.v_areas_monitoradas_geojson;
GO

CREATE VIEW analise.v_areas_monitoradas_geojson AS
SELECT
    am.id,
    am.nome,
    am.dados_json
FROM muralha.area_monitorada am
WHERE am.deletado = 0;
GO

PRINT 'Criada: analise.v_areas_monitoradas_geojson';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 6. analise.v_disponibilidade_dia — disponibilidade diaria por equipamento
--    Fonte: muralha.equipamento_disponibilidade (Plano 18)
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_disponibilidade_dia', 'V') IS NOT NULL
    DROP VIEW analise.v_disponibilidade_dia;
GO

CREATE VIEW analise.v_disponibilidade_dia AS
SELECT
    CAST(ed.dt_verificacao AS DATE)   AS dia,
    ed.id_local,
    RTRIM(l.nome)                     AS local_nome,
    COUNT(*)                          AS verificacoes,
    SUM(CAST(ed.disponivel AS INT))   AS verificacoes_ok,
    CAST(
        CASE WHEN COUNT(*) > 0
             THEN 100.0 * SUM(CAST(ed.disponivel AS INT)) / COUNT(*)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_disponibilidade,
    MAX(ed.minutos_offline)           AS max_minutos_offline
FROM muralha.equipamento_disponibilidade ed
JOIN dbo.local l ON l.id_local = ed.id_local AND l.sequencia_local = 1
GROUP BY CAST(ed.dt_verificacao AS DATE), ed.id_local, l.nome;
GO

PRINT 'Criada: analise.v_disponibilidade_dia';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 7. analise.v_sla_latencia_dia — SLA de latencia diario
--    Fonte: muralha.alerta_sla (Plano 07)
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_sla_latencia_dia', 'V') IS NOT NULL
    DROP VIEW analise.v_sla_latencia_dia;
GO

CREATE VIEW analise.v_sla_latencia_dia AS
SELECT
    CAST(s.dt_alerta AS DATE)         AS dia,
    COUNT(*)                          AS janelas_avaliadas,
    SUM(CASE WHEN s.violacao = 0 THEN 1 ELSE 0 END) AS janelas_ok,
    SUM(CASE WHEN s.violacao = 1 THEN 1 ELSE 0 END) AS janelas_violacao,
    CAST(
        CASE WHEN COUNT(*) > 0
             THEN 100.0 * SUM(CASE WHEN s.violacao = 0 THEN 1 ELSE 0 END) / COUNT(*)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_cumprimento,
    AVG(s.percentil95_ms)             AS media_p95_ms,
    MAX(s.threshold_ms)               AS threshold_ms,
    SUM(s.total_passagens)            AS total_passagens
FROM muralha.alerta_sla s
GROUP BY CAST(s.dt_alerta AS DATE);
GO

PRINT 'Criada: analise.v_sla_latencia_dia';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 8. analise.v_sla_preproc_dia — SLA de pre-processamento 72h
--    Fonte: muralha.alerta_sla_preproc (Plano 10)
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_sla_preproc_dia', 'V') IS NOT NULL
    DROP VIEW analise.v_sla_preproc_dia;
GO

CREATE VIEW analise.v_sla_preproc_dia AS
SELECT
    CAST(sp.dt_alerta AS DATE)        AS dia,
    COUNT(*)                          AS snapshots,
    AVG(sp.total_pendentes)           AS media_pendentes,
    MAX(sp.acima_72h)                 AS max_acima_72h,
    MAX(sp.acima_48h)                 AS max_acima_48h,
    MAX(sp.acima_24h)                 AS max_acima_24h,
    CAST(
        CASE WHEN AVG(sp.total_pendentes) > 0
             THEN 100.0 * (1.0 - CAST(AVG(sp.acima_72h) AS FLOAT) / AVG(sp.total_pendentes))
             ELSE 100.0 END
        AS DECIMAL(5,2))              AS pct_dentro_sla
FROM muralha.alerta_sla_preproc sp
GROUP BY CAST(sp.dt_alerta AS DATE);
GO

PRINT 'Criada: analise.v_sla_preproc_dia';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 9. analise.v_assertividade_dia — taxa de concordancia na dupla analise
--    Fontes: muralha.vtr_status_analise, muralha.infracao_analise (Plano 05)
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_assertividade_dia', 'V') IS NOT NULL
    DROP VIEW analise.v_assertividade_dia;
GO

CREATE VIEW analise.v_assertividade_dia AS
SELECT
    CAST(ia.dt_analise AS DATE)       AS dia,
    COUNT(DISTINCT ia.id_infracao)    AS total_analisadas,
    SUM(CASE WHEN vsa.status_analise = 'PRE_APROVADA' THEN 1 ELSE 0 END)
                                      AS pre_aprovadas,
    SUM(CASE WHEN vsa.status_analise = 'REPROVADA' THEN 1 ELSE 0 END)
                                      AS reprovadas,
    SUM(CASE WHEN vsa.status_analise = 'DESEMPATE' THEN 1 ELSE 0 END)
                                      AS desempate,
    CAST(
        CASE WHEN COUNT(DISTINCT ia.id_infracao) > 0
             THEN 100.0 * SUM(CASE WHEN vsa.status_analise IN ('PRE_APROVADA','REPROVADA') THEN 1 ELSE 0 END)
                  / COUNT(DISTINCT ia.id_infracao)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_assertividade
FROM muralha.infracao_analise ia
JOIN muralha.vtr_status_analise vsa
    ON vsa.id_veiculo_tempo_real = ia.id_infracao
WHERE ia.sequencia = 1
GROUP BY CAST(ia.dt_analise AS DATE);
GO

PRINT 'Criada: analise.v_assertividade_dia';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 10. analise.v_preprocessamento — 14 alineas do TR 5.5.3.1
--     por periodo/equipamento/operador
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.v_preprocessamento', 'V') IS NOT NULL
    DROP VIEW analise.v_preprocessamento;
GO

CREATE VIEW analise.v_preprocessamento AS
SELECT
    CAST(ia.dt_analise AS DATE)       AS dia,
    vtr.id_local,
    RTRIM(l.nome)                     AS local_nome,
    ia.id_usuario                     AS id_operador,
    u.usuario                         AS operador,

    -- 1. Recebidos: total de registros que entraram para analise no dia/local
    COUNT(DISTINCT ia.id_infracao)    AS recebidos,

    -- 2. Pre-processados: registros com pelo menos uma analise concluida
    COUNT(DISTINCT CASE WHEN ia.sequencia >= 1 THEN ia.id_infracao END)
                                      AS preprocessados,

    -- 3. Disponibilizados: registros com status final definido
    COUNT(DISTINCT CASE WHEN vsa.status_analise IS NOT NULL
                          AND vsa.status_analise <> 'AGUARDANDO_ANALISE'
                         THEN ia.id_infracao END)
                                      AS disponibilizados,

    -- 4. Descartados: reprovados
    COUNT(DISTINCT CASE WHEN vsa.status_analise = 'REPROVADA'
                         THEN ia.id_infracao END)
                                      AS descartados,

    -- 5. Aproveitamento (%)
    CAST(
        CASE WHEN COUNT(DISTINCT ia.id_infracao) > 0
             THEN 100.0 * COUNT(DISTINCT CASE WHEN vsa.status_analise = 'PRE_APROVADA'
                                               THEN ia.id_infracao END)
                  / COUNT(DISTINCT ia.id_infracao)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_aproveitamento,

    -- 6. Tempo medio de analise (minutos) — dt_analise - data da passagem
    AVG(DATEDIFF(MINUTE, vtr.data, ia.dt_analise))
                                      AS tempo_medio_min,

    -- 7. Produtividade por operador (registros analisados)
    COUNT(*)                          AS produtividade,

    -- 8. Divergencia: desempates
    COUNT(DISTINCT CASE WHEN vsa.status_analise = 'DESEMPATE'
                         THEN ia.id_infracao END)
                                      AS divergencias,

    -- 9. Retrabalho: analises de sequencia > 2 (desempate)
    COUNT(CASE WHEN ia.sequencia > 2 THEN 1 END)
                                      AS retrabalho,

    -- 10. Pendentes: aguardando analise
    COUNT(DISTINCT CASE WHEN vsa.status_analise = 'AGUARDANDO_ANALISE'
                          OR vsa.status_analise IS NULL
                         THEN ia.id_infracao END)
                                      AS pendentes,

    -- 11. Backlog: pendentes com mais de 24h
    COUNT(DISTINCT CASE WHEN (vsa.status_analise = 'AGUARDANDO_ANALISE'
                              OR vsa.status_analise IS NULL)
                          AND vtr.data < DATEADD(HOUR, -24, SYSDATETIME())
                         THEN ia.id_infracao END)
                                      AS backlog_24h,

    -- 12. SLA: % processados em ate 72h
    CAST(
        CASE WHEN COUNT(DISTINCT ia.id_infracao) > 0
             THEN 100.0 * COUNT(DISTINCT CASE
                    WHEN DATEDIFF(HOUR, vtr.data, ia.dt_analise) <= 72
                    THEN ia.id_infracao END)
                  / COUNT(DISTINCT ia.id_infracao)
             ELSE 0 END
        AS DECIMAL(5,2))              AS pct_sla_72h

    -- 13. Motivos de descarte: via v_alertas ou relatorio separado
    -- 14. Historico: esta view ja serve como fonte historica por periodo

FROM muralha.infracao_analise ia
JOIN muralha.veiculo_tempo_real vtr ON vtr.id = ia.id_infracao
JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1
LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = ia.id_infracao
LEFT JOIN dbo.sis_usuario u ON u.id_usuario = ia.id_usuario
GROUP BY
    CAST(ia.dt_analise AS DATE),
    vtr.id_local,
    l.nome,
    ia.id_usuario,
    u.usuario;
GO

PRINT 'Criada: analise.v_preprocessamento';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 11. Procedure: analise.spu_carga_passagens_hora
--     Carga incremental: ultima hora fechada + reprocesso das ultimas 24h
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.spu_carga_passagens_hora', 'P') IS NOT NULL
    DROP PROCEDURE analise.spu_carga_passagens_hora;
GO

CREATE PROCEDURE analise.spu_carga_passagens_hora
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dt_inicio DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        -- Janela: ultimas 25 horas (24h reprocesso + 1h corrente)
        DECLARE @hora_corte DATETIME2(0) = DATEADD(HOUR, -25,
            DATEADD(HOUR, DATEDIFF(HOUR, 0, SYSDATETIME()), CAST('00:00' AS DATETIME2(0))));

        -- Remove e reinsere dados da janela (merge incremental)
        DELETE FROM analise.mat_passagens_hora
        WHERE hora >= @hora_corte;

        INSERT INTO analise.mat_passagens_hora
            (hora, id_local, local_nome, faixa, classificacao, placa_mercosul,
             passagens, soma_velocidade, lat, lon)
        SELECT
            DATEADD(HOUR, DATEDIFF(HOUR, 0, vtr.data), CAST('00:00' AS DATETIME2(0))) AS hora,
            vtr.id_local,
            RTRIM(l.nome),
            ISNULL(vtr.id_pista, 0),
            ISNULL(vtr.classificacao, ' '),
            ISNULL(vtr.placa_mercosul, 0),
            COUNT(*),
            SUM(CAST(ISNULL(vtr.velocidade, 0) AS BIGINT)),
            MAX(l.posicao_lat),
            MAX(l.posicao_lon)
        FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
        JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1
        WHERE vtr.data >= @hora_corte
        GROUP BY
            DATEADD(HOUR, DATEDIFF(HOUR, 0, vtr.data), CAST('00:00' AS DATETIME2(0))),
            vtr.id_local, l.nome,
            ISNULL(vtr.id_pista, 0),
            ISNULL(vtr.classificacao, ' '),
            ISNULL(vtr.placa_mercosul, 0);

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_passagens_hora', @dt_inicio, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;
GO

PRINT 'Criada: analise.spu_carga_passagens_hora';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- 12. Procedure: analise.spu_carga_infracoes_dia
--     Carga incremental: ultimos 7 dias
-- ═══════════════════════════════════════════════════════════════════════════════
IF OBJECT_ID('analise.spu_carga_infracoes_dia', 'P') IS NOT NULL
    DROP PROCEDURE analise.spu_carga_infracoes_dia;
GO

CREATE PROCEDURE analise.spu_carga_infracoes_dia
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @dt_inicio DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        DECLARE @dia_corte DATE = DATEADD(DAY, -7, CAST(SYSDATETIME() AS DATE));

        DELETE FROM analise.mat_infracoes_dia
        WHERE dia >= @dia_corte;

        INSERT INTO analise.mat_infracoes_dia
            (dia, id_local, local_nome, faixa, id_enquadramento, enquadramento,
             limite, total, excesso_medio, lat, lon)
        SELECT
            CAST(i.data AS DATE)        AS dia,
            i.id_local,
            RTRIM(l.nome),
            ISNULL(i.pista, 0),
            i.id_enquadramento,
            ISNULL(e.descricao, 'N/D'),
            MAX(i.velocidade_limite),
            COUNT(*),
            CAST(AVG(
                CASE WHEN i.velocidade_considerada > i.velocidade_limite
                     THEN i.velocidade_considerada - i.velocidade_limite
                     ELSE NULL END
            ) AS DECIMAL(6,2)),
            MAX(l.posicao_lat),
            MAX(l.posicao_lon)
        FROM dbo.infracao i
        JOIN dbo.local l ON l.id_local = i.id_local AND l.sequencia_local = 1
        LEFT JOIN dbo.enquadramento e ON e.id_enquadramento = i.id_enquadramento
        WHERE CAST(i.data AS DATE) >= @dia_corte
        GROUP BY
            CAST(i.data AS DATE),
            i.id_local, l.nome,
            ISNULL(i.pista, 0),
            i.id_enquadramento, e.descricao;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_infracoes_dia', @dt_inicio, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;
GO

PRINT 'Criada: analise.spu_carga_infracoes_dia';
GO

-- ═══════════════════════════════════════════════════════════════════════════════
-- TESTE (comentado) — verificar dados apos execucao
-- ═══════════════════════════════════════════════════════════════════════════════
-- EXEC analise.spu_carga_passagens_hora;
-- SELECT TOP 5 * FROM analise.mat_passagens_hora ORDER BY hora DESC;
-- SELECT * FROM analise.carga_log WHERE processo = 'spu_carga_passagens_hora' ORDER BY dt_inicio DESC;
--
-- EXEC analise.spu_carga_infracoes_dia;
-- SELECT TOP 5 * FROM analise.mat_infracoes_dia ORDER BY dia DESC;
-- SELECT * FROM analise.carga_log WHERE processo = 'spu_carga_infracoes_dia' ORDER BY dt_inicio DESC;
--
-- SELECT COUNT(*) FROM analise.v_alertas;
-- SELECT COUNT(*) FROM analise.v_equipamentos;
-- SELECT COUNT(*) FROM analise.v_areas_monitoradas_geojson;
-- SELECT COUNT(*) FROM analise.v_disponibilidade_dia;
-- SELECT COUNT(*) FROM analise.v_sla_latencia_dia;
-- SELECT COUNT(*) FROM analise.v_sla_preproc_dia;
-- SELECT COUNT(*) FROM analise.v_assertividade_dia;
-- SELECT * FROM analise.v_preprocessamento;
-- =============================================================================

PRINT '>>> Migracao SUP-2 concluida com sucesso.';
GO

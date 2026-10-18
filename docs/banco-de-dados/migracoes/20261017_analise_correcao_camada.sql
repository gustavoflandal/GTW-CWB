-- ============================================================
-- Migração: correção da camada analise (Plano 23 — SUP-2 e SUP-8)
-- Data: 2026-10-17
-- Gap: docs/entregas/gap-camada-analise-superset.md (itens 1 a 5)
--
-- Por quê: os painéis operacionais do Superset ficavam sem dados porque
--   - as cargas analise.spu_carga_* só olhavam as últimas 25 h / 7 dias;
--   - as coordenadas vinham da versão 1 do equipamento (quase sempre sem lat/lon);
--   - v_disponibilidade_dia dependia de muralha.equipamento_disponibilidade (vazia);
--   - v_preprocessamento dependia de muralha.infracao_analise (só registros de teste);
--   - as áreas monitoradas não estavam em formato utilizável no mapa;
--   - a migração 20261017_analise_mapas_geo.sql (views geolocalizadas) nunca rodou
--     porque usava colunas inexistentes.
--
-- O que este script faz (idempotente; nenhuma tabela legada é alterada; só objetos do schema analise):
--   1. View analise.v_local_vigente: nome da versão mais recente do equipamento e
--      coordenadas da versão mais recente que tem lat/lon
--   2. spu_carga_passagens_hora e spu_carga_infracoes_dia: janela parametrizável
--      (padrão igual ao anterior) e coordenadas de v_local_vigente
--   3. v_equipamentos: coordenadas de v_local_vigente
--   4. v_disponibilidade_dia: horas com captura (dbo.veiculo_sumarizado) ÷ 24,
--      incluindo os dias sem captura de cada equipamento (mesmas colunas)
--   5. v_preprocessamento: fluxo de processamento de dbo.infracao (mesmas colunas)
--   6. Tabela analise.mat_areas_monitoradas + spu_carga_areas_monitoradas
--      (polígono [[lng,lat],...]) e v_areas_monitoradas_geojson com cor e poligono
--   7. Views v_infracoes_geolocalizado e v_passagens_geolocalizado (substituem a
--      migração 20261017_analise_mapas_geo.sql, que fica obsoleta)
--   8. Carga inicial: passagens desde 2025-06-01 (mês a mês), infrações completas e áreas
--
-- Executar com: sqlcmd -b -I -f 65001 -i <arquivo>   (somente com autorização humana)
-- Duração esperada no DEV: alguns minutos (carga de ~4,2 milhões de passagens).
--
-- Rollback: reexecutar as definições originais de 20261017_analise_camada_dados.sql
-- (views e procedures) e:
--   DROP PROCEDURE analise.spu_carga_areas_monitoradas;
--   DROP TABLE analise.mat_areas_monitoradas;
--   DROP VIEW analise.v_infracoes_geolocalizado;
--   DROP VIEW analise.v_passagens_geolocalizado;
--   DROP VIEW analise.v_local_vigente;   -- depois de restaurar as views que a usam
-- ============================================================
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Equipamento vigente -----------------------------------------------------
CREATE OR ALTER VIEW analise.v_local_vigente AS
SELECT
    u.id_local,
    RTRIM(u.nome)            AS nome,
    u.tipo,
    RTRIM(u.localidade_desc) AS localidade,
    c.lat,
    c.lon
FROM (
    SELECT l.id_local, l.nome, l.tipo, l.localidade_desc,
           ROW_NUMBER() OVER (PARTITION BY l.id_local ORDER BY l.sequencia_local DESC) AS rn
    FROM dbo.local l
) u
OUTER APPLY (
    SELECT TOP 1 x.posicao_lat AS lat, x.posicao_lon AS lon
    FROM dbo.local x
    WHERE x.id_local = u.id_local AND x.posicao_lat IS NOT NULL AND x.posicao_lon IS NOT NULL
    ORDER BY x.sequencia_local DESC
) c
WHERE u.rn = 1;
GO
PRINT 'View analise.v_local_vigente criada/atualizada';
GO

-- 2a. Carga de passagens por hora ----------------------------------------------
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
        LEFT JOIN analise.v_local_vigente lv ON lv.id_local = a.id_local;

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

-- 2b. Carga de infrações por dia -----------------------------------------------
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
                CAST(i.data AS DATE)    AS dia,
                i.id_local,
                ISNULL(i.pista, 0)      AS faixa,
                i.id_enquadramento,
                MAX(i.velocidade_limite) AS limite,
                COUNT(*)                AS total,
                CAST(AVG(CASE WHEN i.velocidade_considerada > i.velocidade_limite
                              THEN i.velocidade_considerada - i.velocidade_limite END) AS DECIMAL(6,2)) AS excesso_medio
            FROM dbo.infracao i
            WHERE i.data >= @dt_inicio AND i.data < @dt_fim
            GROUP BY CAST(i.data AS DATE), i.id_local, ISNULL(i.pista, 0), i.id_enquadramento
        ) a
        LEFT JOIN analise.v_local_vigente lv ON lv.id_local = a.id_local
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
PRINT 'Procedures de carga atualizadas';
GO

-- 3. Equipamentos ----------------------------------------------------------------
CREATE OR ALTER VIEW analise.v_equipamentos AS
SELECT
    lv.id_local,
    lv.nome,
    lv.lat,
    lv.lon,
    lv.tipo,
    CASE WHEN EXISTS (
        SELECT 1 FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK)
        WHERE vtr.id_local = lv.id_local AND vtr.data >= DATEADD(HOUR, -24, SYSDATETIME())
    ) THEN 1 ELSE 0 END             AS ativo_24h,
    am.nome                         AS area_monitorada
FROM analise.v_local_vigente lv
LEFT JOIN muralha.equipamentos_area_monitorada eam
    ON eam.id_equipamento = lv.id_local
LEFT JOIN muralha.area_monitorada am
    ON am.id = eam.id_area_monitorada AND am.deletado = 0;
GO
PRINT 'View analise.v_equipamentos atualizada';
GO

-- 4. Disponibilidade diária -------------------------------------------------------
-- Horas do dia com captura ÷ 24. O calendário de cada equipamento vai do primeiro ao
-- último dia com dados, considerando só os dias em que o sistema teve algum dado
-- (lacunas gerais da sumarização não contam como indisponibilidade).
CREATE OR ALTER VIEW analise.v_disponibilidade_dia AS
WITH horas AS (
    SELECT s.data AS dia, s.id_local, COUNT(DISTINCT s.hora) AS horas_captura
    FROM dbo.veiculo_sumarizado s
    GROUP BY s.data, s.id_local
),
dias_sistema AS (
    SELECT DISTINCT dia FROM horas
),
faixa AS (
    SELECT id_local, MIN(dia) AS d0, MAX(dia) AS d1 FROM horas GROUP BY id_local
)
SELECT
    d.dia,
    f.id_local,
    ISNULL(lv.nome, 'Equipamento ' + CAST(f.id_local AS varchar(10))) AS local_nome,
    24                                                                AS verificacoes,
    ISNULL(h.horas_captura, 0)                                        AS verificacoes_ok,
    CAST(100.0 * ISNULL(h.horas_captura, 0) / 24 AS DECIMAL(5,2))     AS pct_disponibilidade,
    (24 - ISNULL(h.horas_captura, 0)) * 60                            AS max_minutos_offline
FROM faixa f
JOIN dias_sistema d ON d.dia BETWEEN f.d0 AND f.d1
LEFT JOIN horas h ON h.id_local = f.id_local AND h.dia = d.dia
LEFT JOIN analise.v_local_vigente lv ON lv.id_local = f.id_local;
GO
PRINT 'View analise.v_disponibilidade_dia atualizada';
GO

-- 5. Pré-processamento ------------------------------------------------------------
-- Fluxo de processamento de dbo.infracao. Divergências e retrabalho não existem nesse
-- fluxo e ficam nulos.
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
LEFT JOIN analise.v_local_vigente lv ON lv.id_local = i.id_local
LEFT JOIN dbo.sis_usuario u ON u.id_usuario = i.id_usuario_final
WHERE i.id_enquadramento <> 1
GROUP BY CAST(i.data_entrada AS DATE), i.id_local, lv.nome, i.id_usuario_final, u.usuario;
GO
PRINT 'View analise.v_preprocessamento atualizada';
GO

-- 6. Áreas monitoradas como polígono ---------------------------------------------
IF OBJECT_ID('analise.mat_areas_monitoradas', 'U') IS NULL
BEGIN
    CREATE TABLE analise.mat_areas_monitoradas (
        id        INT           NOT NULL CONSTRAINT PK_mat_areas_monitoradas PRIMARY KEY,
        nome      NVARCHAR(200) NOT NULL,
        cor       VARCHAR(20)   NULL,
        poligono  NVARCHAR(MAX) NULL,
        dt_carga  DATETIME2(0)  NOT NULL CONSTRAINT DF_mat_areas_dt DEFAULT SYSDATETIME()
    );
    PRINT 'Criada: analise.mat_areas_monitoradas';
END
ELSE PRINT 'Ja existe: analise.mat_areas_monitoradas';
GO

-- Converte {"dadosMancha":{"cor":"#...","coordenadas":[{"lat":y,"lng":x},...]}} em [[x,y],...].
-- O banco está no nível de compatibilidade 110 (sem OPENJSON): a lista é reescrita como XML.
CREATE OR ALTER PROCEDURE analise.spu_carga_areas_monitoradas
AS
BEGIN
    SET NOCOUNT ON;
    SET ANSI_WARNINGS ON;
    SET ANSI_PADDING ON;
    SET ARITHABORT ON;
    SET CONCAT_NULL_YIELDS_NULL ON;
    SET NUMERIC_ROUNDABORT OFF;

    DECLARE @dt_exec DATETIME2(3) = SYSDATETIME();
    DECLARE @linhas INT = 0;
    DECLARE @erro VARCHAR(2000) = NULL;

    BEGIN TRY
        DELETE FROM analise.mat_areas_monitoradas;

        INSERT INTO analise.mat_areas_monitoradas (id, nome, cor, poligono)
        SELECT
            am.id,
            LEFT(am.nome, 200),
            CASE WHEN CHARINDEX('"cor":"', am.dados_json) > 0
                 THEN SUBSTRING(am.dados_json, CHARINDEX('"cor":"', am.dados_json) + 7, 7) END,
            '[' + STUFF((
                SELECT ',[' + n.value('@o', 'varchar(40)') + ',' + n.value('@a', 'varchar(40)') + ']'
                FROM x.doc.nodes('/r/p') t(n)
                FOR XML PATH(''), TYPE).value('.', 'nvarchar(max)'), 1, 1, '') + ']'
        FROM muralha.area_monitorada am
        CROSS APPLY (SELECT CHARINDEX('"coordenadas":[', am.dados_json) AS ini) a
        CROSS APPLY (
            SELECT CASE WHEN a.ini > 0 AND CHARINDEX(']', am.dados_json, a.ini) > a.ini + 15
                        THEN SUBSTRING(am.dados_json, a.ini + 15, CHARINDEX(']', am.dados_json, a.ini) - a.ini - 15)
                   END AS seg
        ) s
        CROSS APPLY (
            SELECT TRY_CAST(N'<r>' + REPLACE(REPLACE(REPLACE(s.seg, N'{"lat":', N'<p a="'), N',"lng":', N'" o="'),
                                             N'}', N'"/>') + N'</r>' AS XML) AS doc
        ) x
        WHERE am.deletado = 0;

        SET @linhas = @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        SET @erro = ERROR_MESSAGE();
    END CATCH

    INSERT INTO analise.carga_log (processo, dt_inicio, dt_fim, linhas, erro, sucesso)
    VALUES ('spu_carga_areas_monitoradas', @dt_exec, SYSDATETIME(), @linhas, @erro,
            CASE WHEN @erro IS NULL THEN 1 ELSE 0 END);
END;
GO

CREATE OR ALTER VIEW analise.v_areas_monitoradas_geojson AS
SELECT
    am.id,
    am.nome,
    am.dados_json,
    m.cor,
    m.poligono
FROM muralha.area_monitorada am
LEFT JOIN analise.mat_areas_monitoradas m ON m.id = am.id
WHERE am.deletado = 0;
GO
PRINT 'Areas monitoradas: tabela, procedure e view atualizadas';
GO

-- 7. Views geolocalizadas (substituem 20261017_analise_mapas_geo.sql) --------------
CREATE OR ALTER VIEW analise.v_infracoes_geolocalizado AS
SELECT
    i.dia,
    i.local_nome,
    CAST(i.lat AS FLOAT)  AS lat,
    CAST(i.lon AS FLOAT)  AS lon,
    i.enquadramento,
    i.total               AS total_infracoes,
    i.excesso_medio
FROM analise.mat_infracoes_dia i
WHERE i.lat IS NOT NULL AND i.lon IS NOT NULL;
GO

CREATE OR ALTER VIEW analise.v_passagens_geolocalizado AS
SELECT
    p.hora,
    MAX(p.local_nome)     AS local_nome,
    CAST(p.lat AS FLOAT)  AS lat,
    CAST(p.lon AS FLOAT)  AS lon,
    SUM(p.passagens)      AS total_passagens,
    CAST(SUM(p.soma_velocidade) * 1.0 / NULLIF(SUM(p.passagens), 0) AS DECIMAL(6,2)) AS velocidade_media
FROM analise.mat_passagens_hora p
WHERE p.lat IS NOT NULL AND p.lon IS NOT NULL
GROUP BY p.hora, p.id_local, p.lat, p.lon;
GO
PRINT 'Views geolocalizadas criadas/atualizadas';
GO

-- 8. Carga inicial ------------------------------------------------------------------
DECLARE @ini DATETIME2(0) = '2025-06-01';
DECLARE @fim DATETIME2(0);
DECLARE @limite DATETIME2(0) = DATEADD(HOUR, 1, CAST(SYSDATETIME() AS DATETIME2(0)));
WHILE @ini < @limite
BEGIN
    SET @fim = DATEADD(MONTH, 1, @ini);
    EXEC analise.spu_carga_passagens_hora @dt_inicio = @ini, @dt_fim = @fim;
    SET @ini = @fim;
END
PRINT 'Carga inicial de passagens concluida';
GO

EXEC analise.spu_carga_infracoes_dia @dt_inicio = '2025-01-01';
EXEC analise.spu_carga_areas_monitoradas;
GO

SELECT processo, COUNT(*) AS execucoes, SUM(linhas) AS linhas,
       SUM(CASE WHEN sucesso = 0 THEN 1 ELSE 0 END) AS falhas
FROM analise.carga_log
WHERE dt_inicio >= DATEADD(MINUTE, -30, SYSDATETIME())
GROUP BY processo;
GO

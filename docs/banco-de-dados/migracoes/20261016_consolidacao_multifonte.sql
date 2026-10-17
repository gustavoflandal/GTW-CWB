SET QUOTED_IDENTIFIER ON;
GO

-- ═══════════════════════════════════════════════════════════════════════════
-- Plano 34 — Consolidação multifonte (§5.4.1.1)
-- ROLLBACK: DROP VIEW muralha.v_dados_consolidados;
--           DELETE FROM muralha.config_chave_valor WHERE chave = 'CONSOLIDACAO_FONTES_HABILITADO';
-- ═══════════════════════════════════════════════════════════════════════════

-- 1. View consolidada de múltiplas fontes ---------------------------------
IF OBJECT_ID('muralha.v_dados_consolidados', 'V') IS NOT NULL
    DROP VIEW muralha.v_dados_consolidados;
GO

CREATE VIEW muralha.v_dados_consolidados AS
SELECT
    'PASSAGEM'           AS fonte,
    CAST(vtr.id AS VARCHAR(36)) AS id_registro,
    vtr.id_local,
    l.nome               AS equipamento,
    vtr.data             AS dt_evento,
    vtr.placa,
    CAST(NULL AS VARCHAR(50)) AS tipo_evento,
    CAST(NULL AS DECIMAL(10,6)) AS latitude,
    CAST(NULL AS DECIMAL(10,6)) AS longitude,
    CAST(NULL AS VARCHAR(200)) AS descricao
FROM muralha.veiculo_tempo_real vtr
JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1

UNION ALL

SELECT
    'ALERTA'             AS fonte,
    CAST(a.id AS VARCHAR(36)) AS id_registro,
    CAST(NULL AS INT)    AS id_local,
    CAST(NULL AS VARCHAR(100)) AS equipamento,
    a.data               AS dt_evento,
    CAST(NULL AS VARCHAR(15)) AS placa,
    tao.tipo             AS tipo_evento,
    CAST(NULL AS DECIMAL(10,6)) AS latitude,
    CAST(NULL AS DECIMAL(10,6)) AS longitude,
    tao.descricao        AS descricao
FROM muralha.alerta a
JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia

UNION ALL

SELECT
    'INCIDENTE_WAZE'     AS fonte,
    CAST(ie.id AS VARCHAR(36)) AS id_registro,
    CAST(NULL AS INT)    AS id_local,
    CAST(NULL AS VARCHAR(100)) AS equipamento,
    ie.dt_ocorrencia     AS dt_evento,
    CAST(NULL AS VARCHAR(15)) AS placa,
    ie.tipo              AS tipo_evento,
    ie.latitude,
    ie.longitude,
    CAST(ie.descricao AS VARCHAR(200)) AS descricao
FROM muralha.incidente_externo ie

UNION ALL

SELECT
    'SUMARIZADO'         AS fonte,
    CAST(vs.data AS VARCHAR(36)) + '_' + CAST(vs.hora AS VARCHAR(5)) + '_' + CAST(vs.id_local AS VARCHAR(10)) AS id_registro,
    vs.id_local,
    l2.nome              AS equipamento,
    CAST(DATEADD(HOUR, vs.hora, vs.data) AS DATETIME) AS dt_evento,
    CAST(NULL AS VARCHAR(15)) AS placa,
    'VOLUMETRIA'         AS tipo_evento,
    CAST(NULL AS DECIMAL(10,6)) AS latitude,
    CAST(NULL AS DECIMAL(10,6)) AS longitude,
    CAST(vs.trafego AS VARCHAR(200)) AS descricao
FROM dbo.veiculo_sumarizado vs
JOIN dbo.local l2 ON l2.id_local = vs.id_local AND l2.sequencia_local = 1;
GO

-- 2. Configuração ---------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'CONSOLIDACAO_FONTES_HABILITADO')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('CONSOLIDACAO_FONTES_HABILITADO', 'true');
GO

-- =============================================================================
-- Migração: Views geolocalizadas para análise espacial (SUP-8)
-- Banco: GTW_MURALHA_DEV
-- Data: 2026-10-17
-- =============================================================================
SET QUOTED_IDENTIFIER ON;
GO

-- -----------------------------------------------------------------------------
-- View: Infrações com coordenadas geográficas do equipamento
-- Usada pelo deck.gl (scatter, screengrid) no dashboard de mapa
-- -----------------------------------------------------------------------------
IF OBJECT_ID('analise.v_infracoes_geolocalizado', 'V') IS NOT NULL
    DROP VIEW analise.v_infracoes_geolocalizado;
GO

CREATE VIEW analise.v_infracoes_geolocalizado AS
SELECT
    i.dia,
    l.nome                   AS local_nome,
    l.latitude               AS lat,
    l.longitude              AS lon,
    i.enquadramento,
    i.total_infracoes,
    i.excesso_medio_ponderado AS excesso_medio
FROM analise.mat_infracoes_dia i
INNER JOIN dbo.local l
    ON  l.id_local        = i.id_local
    AND l.sequencia_local  = 1
WHERE l.latitude  IS NOT NULL
  AND l.longitude IS NOT NULL;
GO

-- -----------------------------------------------------------------------------
-- View: Passagens com coordenadas geográficas do equipamento
-- Usada pelo deck.gl (screengrid de fluxo) no dashboard de mapa
-- -----------------------------------------------------------------------------
IF OBJECT_ID('analise.v_passagens_geolocalizado', 'V') IS NOT NULL
    DROP VIEW analise.v_passagens_geolocalizado;
GO

CREATE VIEW analise.v_passagens_geolocalizado AS
SELECT
    p.hora,
    l.nome                        AS local_nome,
    l.latitude                    AS lat,
    l.longitude                   AS lon,
    p.total_passagens,
    p.velocidade_media_ponderada  AS velocidade_media
FROM analise.mat_passagens_hora p
INNER JOIN dbo.local l
    ON  l.id_local        = p.id_local
    AND l.sequencia_local  = 1
WHERE l.latitude  IS NOT NULL
  AND l.longitude IS NOT NULL;
GO

-- -----------------------------------------------------------------------------
-- View auxiliar: Vínculo usuário ↔ área monitorada (para RLS SUP-5)
-- Permite filtro dinâmico por região no Superset via Jinja
-- -----------------------------------------------------------------------------
IF OBJECT_ID('analise.v_usuario_area_monitorada', 'V') IS NOT NULL
    DROP VIEW analise.v_usuario_area_monitorada;
GO

CREATE VIEW analise.v_usuario_area_monitorada AS
SELECT
    u.usuario,
    ug.id_grupo,
    am.id             AS id_area_monitorada,
    am.nome           AS nome_area
FROM dbo.sis_usuario u
INNER JOIN dbo.sis_usuario_grupo ug
    ON ug.id_usuario = u.id_usuario
INNER JOIN dbo.sis_grupo g
    ON g.id_grupo = ug.id_grupo
INNER JOIN analise.area_monitorada_grupo amg
    ON amg.id_grupo = g.id_grupo
INNER JOIN dbo.local am
    ON  am.id_local       = amg.id_area_monitorada
    AND am.sequencia_local = 1;
GO

PRINT '>>> Views geolocalizadas criadas com sucesso.';
GO

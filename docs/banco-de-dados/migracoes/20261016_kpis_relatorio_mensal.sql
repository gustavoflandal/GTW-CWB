-- ============================================================
-- Migração: KPIs com histórico, alertas analíticos e relatório mensal
-- Data: 2026-10-16 (Plano 29, Fase 7 da correção da auditoria)
-- TR: 5.4.3.5-7 (alertas analíticos, relatório gerencial periódico),
--     5.4.5.1-2 (relatórios analíticos/gerenciais),
--     5.4.6.2-3 (indicadores obrigatórios, metas, histórico),
--     5.5.3.1-3 (relatório mensal de pré-processamento)
--
-- O que este script faz (idempotente; nenhuma tabela existente muda):
--   1. muralha.kpi_config_meta         meta/alvo por KPI (complementar a kpi_config)
--   2. muralha.kpi_historico            snapshots periódicos de valor de KPI
--   3. muralha.relatorio_periodico      relatórios gerenciais gerados automaticamente
--   4. Seed de KPIs obrigatórios (5.4.6.2 a-h)
--   5. Seed de metas para KPIs existentes
--   6. Parâmetros KPI_SNAPSHOT_* e RELATORIO_* em config_chave_valor
--   7. Menus (Relatórios Gerenciais)
--
-- Executar com sqlcmd -b -f 65001 e somente com autorização humana.
--
-- Rollback (na ordem inversa):
--   DELETE d FROM dbo.sis_menu_direitos d JOIN dbo.sis_menu m ON m.id_menu = d.id_menu
--     WHERE m.acao IN ('/MuralhaDigital/RelatorioGerencial');
--   DELETE FROM dbo.sis_menu_infos WHERE href = '/MuralhaDigital/RelatorioGerencial';
--   DELETE FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/RelatorioGerencial';
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'KPI[_]SNAPSHOT[_]%'
--     OR chave LIKE 'RELATORIO[_]%';
--   DELETE FROM muralha.kpi_config WHERE id > 4;  -- seed novos
--   DELETE FROM muralha.kpi_config_meta;
--   DROP TABLE muralha.relatorio_periodico;
--   DROP TABLE muralha.kpi_historico;
--   DROP TABLE muralha.kpi_config_meta;
-- ============================================================

SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- 1. Meta/alvo por KPI (complementar a kpi_config, sem ALTER TABLE) ----------
IF OBJECT_ID('muralha.kpi_config_meta', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.kpi_config_meta (
        id_kpi        INT          NOT NULL
            CONSTRAINT PK_kpi_config_meta PRIMARY KEY
            CONSTRAINT FK_kcm_kpi REFERENCES muralha.kpi_config(id),
        meta          FLOAT        NULL,
        meta_minima   FLOAT        NULL,
        categoria     VARCHAR(60)  NOT NULL CONSTRAINT DF_kcm_cat DEFAULT 'geral',
        gerar_alerta  BIT          NOT NULL CONSTRAINT DF_kcm_alerta DEFAULT 1,
        dt_atualizacao DATETIME    NOT NULL CONSTRAINT DF_kcm_dt DEFAULT GETDATE()
    );
    PRINT 'Criada: muralha.kpi_config_meta';
END
ELSE PRINT 'Ja existe: muralha.kpi_config_meta';
GO

-- 2. Snapshots periódicos de KPI ---------------------------------------------
IF OBJECT_ID('muralha.kpi_historico', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.kpi_historico (
        id            BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_kpi_historico PRIMARY KEY,
        id_kpi        INT          NOT NULL
            CONSTRAINT FK_kh_kpi REFERENCES muralha.kpi_config(id),
        valor         FLOAT        NOT NULL,
        status        VARCHAR(10)  NOT NULL,
        dt_coleta     DATETIME     NOT NULL CONSTRAINT DF_kh_dt DEFAULT GETDATE()
    );
    CREATE NONCLUSTERED INDEX IX_kpi_historico_kpi_dt
        ON muralha.kpi_historico (id_kpi, dt_coleta);
    PRINT 'Criada: muralha.kpi_historico (com indice)';
END
ELSE PRINT 'Ja existe: muralha.kpi_historico';
GO

-- 3. Relatórios gerenciais periódicos ----------------------------------------
IF OBJECT_ID('muralha.relatorio_periodico', 'U') IS NULL
BEGIN
    CREATE TABLE muralha.relatorio_periodico (
        id            BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_relatorio_periodico PRIMARY KEY,
        tipo          VARCHAR(40)  NOT NULL,
        titulo        VARCHAR(200) NOT NULL,
        periodo_inicio DATE        NOT NULL,
        periodo_fim    DATE        NOT NULL,
        dados_json    VARCHAR(MAX) NOT NULL,
        dt_geracao    DATETIME     NOT NULL CONSTRAINT DF_rp_dt DEFAULT GETDATE(),
        gerado_por    VARCHAR(60)  NOT NULL CONSTRAINT DF_rp_por DEFAULT 'SISTEMA'
    );
    CREATE NONCLUSTERED INDEX IX_relatorio_periodico_tipo_dt
        ON muralha.relatorio_periodico (tipo, dt_geracao DESC);
    PRINT 'Criada: muralha.relatorio_periodico (com indice)';
END
ELSE PRINT 'Ja existe: muralha.relatorio_periodico';
GO

-- 4. Seed de KPIs obrigatórios (5.4.6.2 a-h) --------------------------------
-- Apenas insere os que não existem (não altera os 4 existentes).
-- As queries usam tabelas reais do GTW/Muralha.
-- (a) Infrações por volume de tráfego
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Taxa de Infração por Tráfego')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Taxa de Infração por Tráfego',
            'Razão infrações/passagens nas últimas 24h (5.4.6.2-a)',
            'SELECT CASE WHEN ISNULL(t.total,0)=0 THEN 0 ELSE ROUND(CAST(i.total AS FLOAT)/t.total*100,2) END AS valor FROM (SELECT COUNT(*) total FROM dbo.veiculo_sumarizado WHERE data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE)) t CROSS JOIN (SELECT COUNT(*) total FROM muralha.veiculo_tempo_real WHERE data >= DATEADD(DAY,-1,GETDATE()) AND resultado_ia IS NOT NULL AND resultado_ia <> ''OK'') i',
            '%', 5.0, 10.0, 10, 1, 'card', NULL);

-- (b) Variação temporal de infrações
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Variação Semanal de Infrações')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Variação Semanal de Infrações',
            'Variação percentual da semana atual vs anterior (5.4.6.2-b)',
            'SELECT CASE WHEN ISNULL(sem_ant.total,0)=0 THEN 0 ELSE ROUND((CAST(sem_atu.total AS FLOAT)-sem_ant.total)/sem_ant.total*100,2) END AS valor FROM (SELECT COUNT(*) total FROM muralha.veiculo_tempo_real WHERE data >= DATEADD(DAY,-7,GETDATE())) sem_atu CROSS JOIN (SELECT COUNT(*) total FROM muralha.veiculo_tempo_real WHERE data >= DATEADD(DAY,-14,GETDATE()) AND data < DATEADD(DAY,-7,GETDATE())) sem_ant',
            '%', 10.0, 25.0, 11, 1, 'card', NULL);

-- (c) Disponibilidade operacional
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Disponibilidade Operacional')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Disponibilidade Operacional',
            'Percentual de equipamentos com passagem nas últimas 24h (5.4.6.2-c)',
            'SELECT CASE WHEN total.cnt=0 THEN 0 ELSE ROUND(CAST(ativos.cnt AS FLOAT)/total.cnt*100,1) END AS valor FROM (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.local_vigente WHERE ativo=1) total CROSS JOIN (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.veiculo_sumarizado WHERE data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND trafego > 0) ativos',
            '%', 90.0, 70.0, 12, 1, 'medidor', NULL);

-- (d) Aproveitamento de registros
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Aproveitamento de Registros')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Aproveitamento de Registros',
            'Taxa de registros aproveitados vs recebidos nos últimos 7 dias (5.4.6.2-d)',
            'SELECT CASE WHEN ISNULL(recebidos.total,0)=0 THEN 100 ELSE ROUND(CAST(aproveitados.total AS FLOAT)/recebidos.total*100,1) END AS valor FROM (SELECT COUNT(*) total FROM muralha.veiculo_tempo_real WHERE data >= DATEADD(DAY,-7,GETDATE())) recebidos CROSS JOIN (SELECT COUNT(*) total FROM muralha.veiculo_tempo_real WHERE data >= DATEADD(DAY,-7,GETDATE()) AND resultado_ia IS NOT NULL) aproveitados',
            '%', 85.0, 70.0, 13, 1, 'medidor', NULL);

-- (e) Efetividade da fiscalização
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Efetividade de Fiscalização')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Efetividade de Fiscalização',
            'Taxa de alertas encerrados/gerados nos últimos 30 dias (5.4.6.2-e)',
            'SELECT CASE WHEN ISNULL(gerados.total,0)=0 THEN 100 ELSE ROUND(CAST(encerrados.total AS FLOAT)/gerados.total*100,1) END AS valor FROM (SELECT COUNT(*) total FROM muralha.alerta WHERE data >= DATEADD(DAY,-30,GETDATE())) gerados CROSS JOIN (SELECT COUNT(*) total FROM muralha.alerta WHERE data >= DATEADD(DAY,-30,GETDATE()) AND id_status_alerta IN (SELECT id FROM muralha.status_alerta WHERE UPPER(status) IN (''ENCERRADO'',''DESCARTADO''))) encerrados',
            '%', 80.0, 60.0, 14, 1, 'card', NULL);

-- (f) Segurança viária (alertas de congestionamento)
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Alertas de Congestionamento (7d)')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Alertas de Congestionamento (7d)',
            'Quantidade de alertas de congestionamento nos últimos 7 dias (5.4.6.2-f)',
            'SELECT COUNT(*) AS valor FROM muralha.alerta a JOIN muralha.tipo_alerta_ocorrencia t ON a.id_tipo_alerta_ocorrencia = t.id WHERE t.nomeinterno = ''CONGESTIONAMENTO'' AND a.data >= DATEADD(DAY,-7,GETDATE())',
            'alertas', 5.0, 15.0, 15, 1, 'card', NULL);

-- (g) Conformidade operacional
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'Conformidade Operacional')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('Conformidade Operacional',
            'Percentual de equipamentos dentro do SLA 24h (5.4.6.2-g)',
            'SELECT CASE WHEN total.cnt=0 THEN 100 ELSE ROUND(CAST(ok.cnt AS FLOAT)/total.cnt*100,1) END AS valor FROM (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.local_vigente WHERE ativo=1) total CROSS JOIN (SELECT COUNT(DISTINCT vs.id_local) cnt FROM dbo.veiculo_sumarizado vs WHERE vs.data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND vs.trafego > 0 AND vs.media_velocidade > 0) ok',
            '%', 95.0, 80.0, 16, 1, 'medidor', NULL);

-- (h) Funcionalidades integradas (SLA de latência)
IF NOT EXISTS (SELECT 1 FROM muralha.kpi_config WHERE nome = 'SLA de Latência P95')
    INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada)
    VALUES ('SLA de Latência P95',
            'Percentil 95 de latência da última janela de SLA (5.4.6.2-h)',
            'SELECT TOP 1 percentil95_ms AS valor FROM muralha.alerta_sla ORDER BY janela_fim DESC',
            'ms', 4000.0, 6000.0, 17, 1, 'compacto', NULL);
GO

-- 5. Seed de metas para KPIs existentes e novos ------------------------------
DECLARE @metas TABLE (id_kpi INT, meta FLOAT, meta_minima FLOAT, categoria VARCHAR(60));
INSERT INTO @metas VALUES
    (1, 5000, 500, 'volume'),
    (2, 200, 0, 'volume'),
    (3, 1000, 0, 'volume'),
    (4, 10, 3, 'disponibilidade');

-- KPIs novos: IDs variam, buscamos pelo nome
INSERT INTO @metas
    SELECT k.id, 3.0, 0, 'infracao'
    FROM muralha.kpi_config k WHERE k.nome = 'Taxa de Infração por Tráfego'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 5.0, -10.0, 'infracao'
    FROM muralha.kpi_config k WHERE k.nome = 'Variação Semanal de Infrações'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 95.0, 80.0, 'disponibilidade'
    FROM muralha.kpi_config k WHERE k.nome = 'Disponibilidade Operacional'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 90.0, 70.0, 'aproveitamento'
    FROM muralha.kpi_config k WHERE k.nome = 'Aproveitamento de Registros'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 85.0, 60.0, 'efetividade'
    FROM muralha.kpi_config k WHERE k.nome = 'Efetividade de Fiscalização'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 3.0, 0, 'seguranca'
    FROM muralha.kpi_config k WHERE k.nome = 'Alertas de Congestionamento (7d)'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 98.0, 85.0, 'conformidade'
    FROM muralha.kpi_config k WHERE k.nome = 'Conformidade Operacional'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);
INSERT INTO @metas
    SELECT k.id, 3000.0, 0, 'integracao'
    FROM muralha.kpi_config k WHERE k.nome = 'SLA de Latência P95'
    AND NOT EXISTS (SELECT 1 FROM @metas WHERE id_kpi = k.id);

MERGE muralha.kpi_config_meta AS t
USING @metas AS s ON t.id_kpi = s.id_kpi
WHEN NOT MATCHED THEN INSERT (id_kpi, meta, meta_minima, categoria, gerar_alerta, dt_atualizacao)
    VALUES (s.id_kpi, s.meta, s.meta_minima, s.categoria, 1, GETDATE());
PRINT 'Metas seed conferidas';
GO

-- 6. Parâmetros em config_chave_valor ----------------------------------------
MERGE muralha.config_chave_valor AS t
USING (VALUES
    ('KPI_SNAPSHOT_INTERVALO_MIN', '60'),
    ('KPI_SNAPSHOT_RETENCAO_DIAS', '90'),
    ('KPI_ALERTA_COOLDOWN_MIN',   '360'),
    ('RELATORIO_GERENCIAL_DIA',   '1'),
    ('RELATORIO_PREPROC_DIA_UTIL','5')
) AS s(chave, valor) ON t.chave = s.chave
WHEN NOT MATCHED THEN INSERT (chave, valor) VALUES (s.chave, s.valor);
PRINT 'Parametros KPI_SNAPSHOT_* e RELATORIO_* conferidos';
GO

-- 7. Menus -------------------------------------------------------------------
DECLARE @maxMenu INT = (SELECT ISNULL(MAX(id_menu), 0) FROM dbo.sis_menu);
DECLARE @maxInfos INT = (SELECT ISNULL(MAX(id_infos), 0) FROM dbo.sis_menu_infos);

IF NOT EXISTS (SELECT 1 FROM dbo.sis_menu WHERE acao = '/MuralhaDigital/RelatorioGerencial')
BEGIN
    SET @maxMenu = @maxMenu + 1;
    SET @maxInfos = @maxInfos + 1;

    INSERT INTO dbo.sis_menu (id_menu, Descricao, acao, Tipo, nivel, menu, Ativo)
    VALUES (@maxMenu, 'Relatorios Gerenciais', '/MuralhaDigital/RelatorioGerencial', 'U', 1, 'Relatorios Gerenciais', 1);

    INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
    VALUES (@maxInfos, @maxMenu, '/muralha-digital/assets/images/menu-icons/relatorios.svg', 'Relatorios Gerenciais', '/MuralhaDigital/RelatorioGerencial', 'Relatorios gerenciais periodicos e de pre-processamento', 10, 15);

    INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (@maxMenu, 42);
    PRINT 'Menu Relatorios Gerenciais criado: menu=' + CAST(@maxMenu AS VARCHAR) + ' infos=' + CAST(@maxInfos AS VARCHAR);
END
ELSE PRINT 'Menu Relatorios Gerenciais ja existe';
GO

PRINT 'Migracao de KPIs e relatorio mensal concluida.';
GO

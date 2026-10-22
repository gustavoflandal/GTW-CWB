-- =============================================================================
-- 20261022_remove_tabelas_migracoes.sql
-- Remove as 17 views e as 65 tabelas criadas pelas migrações desta pasta
-- (2026-10-08 a 2026-10-19), devolvendo o banco aos objetos que existiam antes.
-- Data: 2026-10-22. Autorizado pelo responsável do projeto para GTW_MURALHA_DEV.
--
-- Escopo: SOMENTE views e tabelas. Não altera:
--   - dados inseridos em tabelas legadas (sis_menu, sis_menu_infos,
--     sis_menu_direitos, sis_grupo, config_chave_valor, tipo_alerta_ocorrencia);
--   - colunas acrescentadas em dbo.sis_usuario (senha_hash ... totp_habilitado);
--   - procedures (analise.spu_carga_*) e schemas (dash, analise). As procedures
--     que leem estas tabelas passam a falhar ao executar;
--   - objetos do período que não vêm destas migrações (imagem_pan_selecionada,
--     log_alerta_arquivo, bkp_20261020_*, procedures spu_get*). Destas,
--     muralha.spu_getConformidadeFuncionalidadesIntegradas lê tabelas daqui e
--     passa a falhar.
--
-- Antes de executar: exportar as tabelas com bcp e salvar a definição das views
-- (feito em 2026-10-22 em D:\GTW-CWB-backup\rollback-migracoes-20261022:
-- <tabela>.bcp + <tabela>.fmt, formato nativo; views\<view>.sql).
--
-- Rollback: reexecutar as migrações de criação de tabela (ver README.md),
-- recarregar os dados com
--   bcp GTW_MURALHA_DEV.<schema>.<tabela> in <arquivo>.bcp -f <arquivo>.fmt -E -S ... -U ...
-- e recriar as views com os arquivos de views\ (definição vigente em 2026-10-22).
--
-- Execução: sqlcmd -b -I -f 65001 -i 20261022_remove_tabelas_migracoes.sql
-- Idempotente: tabela que já não existe é ignorada.
-- =============================================================================
SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_NAME() <> 'GTW_MURALHA_DEV'
BEGIN
    RAISERROR('Script autorizado apenas para GTW_MURALHA_DEV.', 16, 1);
    RETURN;
END

DECLARE @alvo TABLE (nome sysname PRIMARY KEY);
INSERT INTO @alvo (nome) VALUES
-- 20261008 (Planos 01-19)
('dbo.sis_log_auditoria'),('dbo.sis_senha_config'),('dbo.sis_usuario_complemento'),
('muralha.vtr_complemento'),('muralha.vtr_imagem_complemento'),
('muralha.infracao_analise'),('muralha.vtr_status_analise'),('muralha.vtr_imagem_obliterada'),
('muralha.infracao_imagem_obliteracao'),('muralha.alerta_sla'),('muralha.alerta_sla_preproc'),
('muralha.lote_infracao'),('muralha.lote_infracao_item'),('muralha.kpi_config'),
('muralha.expurgo_log'),('muralha.equipamento_disponibilidade'),('muralha.incidente_externo'),
-- 20261010 (Plano 20, Dashboard Builder inicial)
('muralha.dashboard_dataset'),('muralha.dashboard_painel'),('muralha.dashboard_widget'),
-- 20261013 (schema dash)
('dash.catalogo_tabela'),('dash.catalogo_coluna'),('dash.catalogo_relacao'),('dash.dataset'),
('dash.dataset_campo'),('dash.dashboard'),('dash.widget'),('dash.compartilhamento'),
('dash.historico_versao'),('dash.dataset_filtro_fixo'),('dash.dataset_tabela'),
('dash.consulta_log'),('dash.dataset_estatistica'),('dash.dataset_relacao'),('dash.dataset_modelador'),
-- 20261015 (Plano 22, chamados)
('muralha.chamado'),('muralha.chamado_anexo'),('muralha.chamado_categoria'),('muralha.chamado_historico'),
('muralha.chamado_notificacao'),('muralha.chamado_prioridade'),('muralha.chamado_protocolo'),
-- 20261016
('muralha.tipo_alerta_config'),('muralha.tipo_alerta_tratamento'),('muralha.alerta_retencao'),
('analise.carga_log'),
('muralha.equipamento_integridade_config'),('muralha.vtr_imagem_hash_origem'),('muralha.vtr_imagem_integridade'),
('muralha.kpi_config_meta'),('muralha.kpi_historico'),('muralha.relatorio_periodico'),
('muralha.parametro_historico'),
('muralha.vtr_placa_leitura'),('muralha.equipamento_ocr_config'),('muralha.vtr_ocr_avaliacao'),
('muralha.vtr_revisao_placa'),('muralha.motivo_correcao_placa'),('muralha.vtr_correcao_justificativa'),
('muralha.notificacao_retry'),('muralha.timelapse_cache'),
-- 20261017 (camada analise)
('analise.mat_passagens_hora'),('analise.mat_infracoes_dia'),('analise.mat_areas_monitoradas'),
-- 20261018
('muralha.alerta_tratamento_execucao');

DECLARE @views TABLE (nome sysname PRIMARY KEY);
INSERT INTO @views (nome) VALUES
('muralha.vw_passagem_anonimizada'),                                          -- 20261008_retencao_anonimizacao
('muralha.v_dashboard_alertas_resumo'),('muralha.v_dashboard_passagens_diarias'), -- 20261010_dashboard_builder
('muralha.v_dados_consolidados'),                                             -- 20261016_consolidacao_multifonte
('muralha.v_integridade_recepcao'),                                           -- 20261016_integridade_sha256_recepcao
('analise.v_alertas'),('analise.v_areas_monitoradas_geojson'),('analise.v_assertividade_dia'),
('analise.v_disponibilidade_dia'),('analise.v_equipamentos'),('analise.v_preprocessamento'),
('analise.v_sla_latencia_dia'),('analise.v_sla_preproc_dia'),                 -- 20261017_analise_camada_dados
('analise.v_local_vigente'),('analise.v_infracoes_geolocalizado'),
('analise.v_passagens_geolocalizado'),                                        -- 20261017_analise_correcao_camada
('analise.v_local_versao');                                                   -- 20261017_analise_versao_equipamento

DECLARE @sql nvarchar(max), @n int, @nv int;

-- Nenhuma tabela fora da lista pode depender de uma tabela da lista por FK.
IF EXISTS (
    SELECT 1 FROM sys.foreign_keys fk
    JOIN @alvo a ON fk.referenced_object_id = OBJECT_ID(a.nome)
    WHERE NOT EXISTS (SELECT 1 FROM @alvo b WHERE OBJECT_ID(b.nome) = fk.parent_object_id))
BEGIN
    RAISERROR('Ha FK de tabela fora da lista apontando para uma tabela da lista. Nada foi removido.', 16, 1);
    RETURN;
END

BEGIN TRANSACTION;

-- 0. Views (antes das tabelas; nenhuma é WITH SCHEMABINDING)
SET @sql = N'';
SELECT @sql += N'DROP VIEW ' + QUOTENAME(PARSENAME(v.nome, 2)) + N'.' + QUOTENAME(PARSENAME(v.nome, 1)) + N';' + CHAR(10)
FROM @views v
WHERE OBJECT_ID(v.nome, 'V') IS NOT NULL;
SET @nv = @@ROWCOUNT;
EXEC sys.sp_executesql @sql;

-- 1. FKs das tabelas da lista (entre si e para tabelas legadas: sis_usuario,
--    alerta, tipo_alerta_ocorrencia)
SET @sql = N'';
SELECT @sql += N'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME(fk.parent_object_id)) + N'.'
             + QUOTENAME(OBJECT_NAME(fk.parent_object_id)) + N' DROP CONSTRAINT ' + QUOTENAME(fk.name) + N';' + CHAR(10)
FROM sys.foreign_keys fk
JOIN @alvo a ON fk.parent_object_id = OBJECT_ID(a.nome);
EXEC sys.sp_executesql @sql;

-- 2. Tabelas
SET @sql = N'';
SELECT @sql += N'DROP TABLE ' + QUOTENAME(PARSENAME(a.nome, 2)) + N'.' + QUOTENAME(PARSENAME(a.nome, 1)) + N';' + CHAR(10)
FROM @alvo a
WHERE OBJECT_ID(a.nome, 'U') IS NOT NULL;
SET @n = @@ROWCOUNT;
EXEC sys.sp_executesql @sql;

COMMIT TRANSACTION;

PRINT CONCAT('Views removidas: ', @nv, ' | Tabelas removidas: ', @n);
SELECT a.nome AS ainda_existe FROM @alvo a WHERE OBJECT_ID(a.nome, 'U') IS NOT NULL
UNION ALL
SELECT v.nome FROM @views v WHERE OBJECT_ID(v.nome, 'V') IS NOT NULL;

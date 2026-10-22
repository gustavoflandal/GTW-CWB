-- =============================================================================
-- 20261022_remove_schemas_dash_analise.sql
-- Remove os schemas dash (20261013_dash_fundacao.sql) e analise
-- (20261016_analise_fundacao.sql), criados nesta fase do projeto.
-- Data: 2026-10-22. Autorizado pelo responsável do projeto para GTW_MURALHA_DEV.
--
-- Pré-requisito: 20261022_remove_tabelas_migracoes.sql (tabelas e views dos
-- dois schemas já removidas). Restam em analise só as procedures de carga
-- spu_carga_passagens_hora, spu_carga_infracoes_dia e spu_carga_areas_monitoradas
-- (20261017_analise_*), removidas aqui.
--
-- Backup das procedures (definição vigente em 2026-10-22):
--   D:\GTW-CWB-backup\rollback-migracoes-20261022\procedures\
--
-- Rollback: reexecutar 20261013_dash_fundacao.sql e 20261016_analise_fundacao.sql
-- (recriam os schemas) e as demais migrações, conforme README.md.
--
-- Execução: sqlcmd -b -I -f 65001 -i 20261022_remove_schemas_dash_analise.sql
-- Idempotente. Para se o schema tiver qualquer objeto fora da lista acima.
-- =============================================================================
SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_NAME() <> 'GTW_MURALHA_DEV'
BEGIN
    RAISERROR('Script autorizado apenas para GTW_MURALHA_DEV.', 16, 1);
    RETURN;
END

IF EXISTS (
    SELECT 1 FROM sys.objects o
    WHERE o.schema_id IN (SCHEMA_ID('dash'), SCHEMA_ID('analise'))
      AND NOT (o.schema_id = SCHEMA_ID('analise') AND o.type = 'P'
               AND o.name IN ('spu_carga_passagens_hora', 'spu_carga_infracoes_dia', 'spu_carga_areas_monitoradas')))
   OR EXISTS (SELECT 1 FROM sys.types WHERE is_user_defined = 1 AND schema_id IN (SCHEMA_ID('dash'), SCHEMA_ID('analise')))
BEGIN
    RAISERROR('Os schemas dash/analise tem objetos fora do esperado. Nada foi removido.', 16, 1);
    RETURN;
END

BEGIN TRANSACTION;

IF OBJECT_ID('analise.spu_carga_passagens_hora', 'P') IS NOT NULL DROP PROCEDURE analise.spu_carga_passagens_hora;
IF OBJECT_ID('analise.spu_carga_infracoes_dia', 'P') IS NOT NULL DROP PROCEDURE analise.spu_carga_infracoes_dia;
IF OBJECT_ID('analise.spu_carga_areas_monitoradas', 'P') IS NOT NULL DROP PROCEDURE analise.spu_carga_areas_monitoradas;

IF SCHEMA_ID('analise') IS NOT NULL EXEC('DROP SCHEMA analise');
IF SCHEMA_ID('dash') IS NOT NULL EXEC('DROP SCHEMA dash');

COMMIT TRANSACTION;

SELECT name AS schema_ainda_existe FROM sys.schemas WHERE name IN ('dash', 'analise');
PRINT 'Schemas dash e analise removidos.';

-- =============================================================================
-- Migracao: corrige o SQL dos KPIs 5, 7, 8, 9 e 11 (FUN-04)
-- Data: 2026-10-19 — idempotente.
-- Origem: docs/planos-salvador/35-correcoes-seguranca-funcionais.md (Task 4).
-- As consultas usavam colunas inexistentes (veiculo_tempo_real.resultado_ia,
-- local_vigente.ativo, status_alerta.status); o KpiSnapshotJob pulava esses KPIs.
--   5  Taxa de Infracao por Trafego: infracoes (dbo.infracao, sem imagem de teste)
--      / trafego (SUM de veiculo_sumarizado.trafego) nas ultimas 24 h.
--   7  Disponibilidade Operacional e 11 Conformidade Operacional: local_vigente.desativado = 0.
--   8  Aproveitamento de Registros: disponibilizados / recebidos de dbo.infracao em 7 dias
--      (mesma regra de analise.v_preprocessamento).
--   9  Efetividade de Fiscalizacao: alertas tratados (status diferente de PENDENTE) / gerados em 30 dias.
-- ROLLBACK: os valores anteriores ficam em muralha.parametro_historico
--   (tipo_recurso = 'KPI', parametro = 'query_sql', origem = 'migracao 20261019_kpis_corrige_consultas').
-- =============================================================================
SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @novos TABLE (id INT PRIMARY KEY, marcador VARCHAR(50), query_sql NVARCHAR(MAX));
INSERT INTO @novos (id, marcador, query_sql) VALUES
(5,  'resultado_ia', N'SELECT CASE WHEN ISNULL(t.total,0)=0 THEN 0 ELSE ROUND(CAST(i.total AS FLOAT)/t.total*100,2) END AS valor FROM (SELECT SUM(CAST(trafego AS BIGINT)) total FROM dbo.veiculo_sumarizado WHERE data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE)) t CROSS JOIN (SELECT COUNT(*) total FROM dbo.infracao WHERE data >= DATEADD(DAY,-1,GETDATE()) AND id_enquadramento <> 1) i'),
(7,  'WHERE ativo=1', N'SELECT CASE WHEN total.cnt=0 THEN 0 ELSE ROUND(CAST(ativos.cnt AS FLOAT)/total.cnt*100,1) END AS valor FROM (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.local_vigente WHERE desativado=0) total CROSS JOIN (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.veiculo_sumarizado WHERE data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND trafego > 0) ativos'),
(8,  'resultado_ia', N'SELECT CASE WHEN COUNT(*)=0 THEN 100 ELSE ROUND(100.0*SUM(CASE WHEN id_processo_concluido IS NOT NULL AND ISNULL(id_inconsistencia,0)=0 THEN 1 ELSE 0 END)/COUNT(*),1) END AS valor FROM dbo.infracao WHERE data >= DATEADD(DAY,-7,GETDATE()) AND id_enquadramento <> 1'),
(9,  'UPPER(status)', N'SELECT CASE WHEN ISNULL(gerados.total,0)=0 THEN 100 ELSE ROUND(CAST(tratados.total AS FLOAT)/gerados.total*100,1) END AS valor FROM (SELECT COUNT(*) total FROM muralha.alerta WHERE data >= DATEADD(DAY,-30,GETDATE())) gerados CROSS JOIN (SELECT COUNT(*) total FROM muralha.alerta WHERE data >= DATEADD(DAY,-30,GETDATE()) AND id_status_alerta IN (SELECT id FROM muralha.status_alerta WHERE UPPER(descricao) <> ''PENDENTE'')) tratados'),
(11, 'WHERE ativo=1', N'SELECT CASE WHEN total.cnt=0 THEN 100 ELSE ROUND(CAST(ok.cnt AS FLOAT)/total.cnt*100,1) END AS valor FROM (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.local_vigente WHERE desativado=0) total CROSS JOIN (SELECT COUNT(DISTINCT vs.id_local) cnt FROM dbo.veiculo_sumarizado vs WHERE vs.data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND vs.trafego > 0 AND vs.media_velocidade > 0) ok');

BEGIN TRANSACTION;

INSERT INTO muralha.parametro_historico (tipo_recurso, id_recurso, parametro, valor_anterior, valor_novo, id_usuario, login, ip, origem)
SELECT 'KPI', CAST(k.id AS VARCHAR(20)), 'query_sql', k.query_sql, n.query_sql, NULL, 'migracao', NULL,
       'migracao 20261019_kpis_corrige_consultas'
FROM muralha.kpi_config k JOIN @novos n ON n.id = k.id
WHERE CHARINDEX(n.marcador, k.query_sql) > 0;
PRINT CONCAT('Historico gravado: ', @@ROWCOUNT);

UPDATE k SET k.query_sql = n.query_sql
FROM muralha.kpi_config k JOIN @novos n ON n.id = k.id
WHERE CHARINDEX(n.marcador, k.query_sql) > 0;
PRINT CONCAT('KPIs corrigidos: ', @@ROWCOUNT);

COMMIT TRANSACTION;

SELECT id, nome, LEFT(query_sql, 120) AS inicio_sql FROM muralha.kpi_config WHERE id IN (5,7,8,9,11) ORDER BY id;

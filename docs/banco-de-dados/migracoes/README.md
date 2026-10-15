# Migracoes de banco de dados

Scripts versionados (AAAAMMDD_descricao.sql). Regras: idempotentes, schema explicito, compativeis com SQL Server 2016 (nivel de compatibilidade 130), rollback comentado, sem dados reais, **nunca aplicados por agentes sem autorizacao humana explicita**. Ver docs/requisitos/10-guia-de-implementacao.md secao 6 e docs/agentes/AGENTS.md.

## Inventario de scripts

### Planos Salvador (2026-10-08)

| Script | Plano | Descricao | Status |
|---|---|---|---|
| `20261008_log_auditoria.sql` | 01 | Tabela de log de auditoria | Executado |
| `20261008_sha256_integridade.sql` | 02 | Coluna hash SHA-256 | Executado |
| `20261008_politica_senhas.sql` | 03 | Politica de senhas e historico | Executado |
| `20261008_mfa_totp.sql` | 04 | MFA/TOTP (chave e backup) | Executado |
| `20261008_dupla_analise.sql` | 05 | Dupla analise de infracoes | Executado |
| `20261008_sla_latencia.sql` | 07 | Metricas de SLA/latencia | Executado |
| `20261008_sla_preprocessamento.sql` | 10 | SLA preprocessamento | Executado |
| `20261008_exportacao_passagens.sql` | 11 | Exportacao padronizada | Executado |
| `20261008_retencao_dados.sql` | 12 | Retencao e LGPD | Executado |
| `20261008_disponibilidade_incidentes.sql` | 18+19 | Disponibilidade e sensores | Executado |

### Dashboard Builder (2026-10-13)

| Script | Sessao | Descricao | Status |
|---|---|---|---|
| `20261013_dash_fundacao.sql` | DASH-1 | Schema `dash`, 9 tabelas, seed | Executado |
| `20261013_dash2_dataset_editor.sql` | DASH-2 | Tabelas complementares do editor | Executado |
| `20261013_dashboards_observabilidade.sql` | DASH-6 | consulta_log, dataset_estatistica | Executado |
| `20261013_atualizar_menus_dashboard_builder.sql` | — | Menus 376/377, infos 43/44 | Executado |
| `20261013_menu_catalogo_dados.sql` | — | Menu 378, info 45 (Catalogo de Dados) | Executado |
| `20261013_dash_modelador_visual.sql` | DASH-2B | dataset_relacao, dataset_modelador | Executado |
| `20261014_dados_demonstracao_dashboards.sql` | Plano 21 | Dados de demonstracao: 3 datasets, 2 paineis, 16 widgets (idempotente; rodar com `-f 65001`) | Executado (2026-10-14) |

# Testes — Plano 29: KPIs com Histórico, Alertas Analíticos e Relatório Mensal

Roteiro para a [Entrega 29](../entregas/29-kpis-relatorio-mensal.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migração `docs/banco-de-dados/migracoes/20261016_kpis_relatorio_mensal.sql` executada com autorização (`sqlcmd -b -f 65001 -i …`).
- Usuário do grupo 42.

## T01 — Testes automatizados

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=KpiSnapshotJobTest,RelatorioPreProcMensalJobTest'
```

**Esperado:** 16 testes, 0 falhas. No Windows (2026-10-16): ✅ 16/16.

## T02 — Migração

```sql
SELECT COUNT(*) AS total_kpis FROM muralha.kpi_config;
SELECT id_kpi, meta, categoria FROM muralha.kpi_config_meta;
SELECT COUNT(*) AS colunas FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='muralha' AND TABLE_NAME='kpi_historico';
SELECT OBJECT_ID('muralha.relatorio_periodico', 'U') AS tabela_existe;
SELECT chave, valor FROM muralha.config_chave_valor WHERE chave LIKE 'KPI[_]%' OR chave LIKE 'RELATORIO[_]%';
```

**Esperado:** 12 KPIs (4 existentes + 8 novos), 12 metas em `kpi_config_meta`, tabela `kpi_historico` com 4 colunas de dados + PK, tabela `relatorio_periodico` existe, 5 parâmetros `KPI_SNAPSHOT_*`/`RELATORIO_*`. Rodar de novo só imprime "Ja existe". ⏳ Pendente.

## T03 — Dashboard de KPIs com indicadores obrigatórios

1. Navegar para Dashboard > KPIs.
2. Verificar que os 12 KPIs aparecem (4 existentes + 8 novos).
3. Verificar que cada KPI mostra valor, status e unidade.

**Esperado:** todos os KPIs renderizam corretamente; os novos (Taxa de Infração, Variação Semanal, Disponibilidade, Aproveitamento, Efetividade, Congestionamento, Conformidade, SLA P95) mostram valores calculados. ⏳ Pendente.

## T04 — Histórico de KPI

1. No dashboard de KPIs, clicar em "Histórico" de qualquer KPI.
2. O modal deve abrir com gráfico (inicialmente vazio, pois é o primeiro snapshot).
3. Alterar o período para 7/30/90 dias.
4. Verificar que a meta aparece (se configurada).

**Esperado:** modal abre sem erros; gráfico renderiza; seletor de período funciona; meta exibida como linha pontilhada (após acumular snapshots). ⏳ Pendente.

## T05 — Coleta de snapshot pelo job

1. Aguardar 60 minutos ou reiniciar o servidor.
2. Verificar:
```sql
SELECT TOP 5 id_kpi, valor, status, dt_coleta FROM muralha.kpi_historico ORDER BY dt_coleta DESC;
```

**Esperado:** pelo menos 12 linhas (uma por KPI ativo) com `dt_coleta` recente. ⏳ Pendente.

## T06 — Alerta analítico

1. Verificar no log de auditoria após o snapshot:
```sql
SELECT TOP 5 funcionalidade, operacao, descricao, data
FROM muralha.sis_log_auditoria
WHERE funcionalidade = 'KPI' AND operacao = 'ALERTA_ANALITICO'
ORDER BY data DESC;
```

**Esperado:** alertas gerados para KPIs em WARN ou CRIT (se houver); cooldown de 360 min respeita duplicatas. ⏳ Pendente.

## T07 — Página de Relatórios Gerenciais

1. Navegar para Análise de Dados > Relatórios Gerenciais (`/MuralhaDigital/RelatorioGerencial`).
2. A lista deve estar vazia inicialmente (primeiro mês não completou).
3. Filtrar por tipo "Gerencial" e "Pré-Processamento".

**Esperado:** página carrega sem erro; filtro funciona; mensagem "Nenhum relatório encontrado" quando vazio. ⏳ Pendente.

## T08 — Exportação CSV/XLSX de relatório

1. Após um relatório ser gerado (ou inserir manualmente para teste):
```sql
INSERT INTO muralha.relatorio_periodico (tipo, titulo, periodo_inicio, periodo_fim, dados_json, gerado_por)
VALUES ('GERENCIAL', 'Teste Manual', '2026-09-01', '2026-09-30', '{"volumeTrafego":{"totalPassagens":1000}}', 'TESTE');
```
2. Na página de relatórios, clicar em "Ver detalhe".
3. No modal, clicar CSV e depois XLSX.

**Esperado:** CSV com BOM UTF-8, separador `;`, dados corretos; XLSX abre no Excel sem erro. ⏳ Pendente.

## T09 — Menu

1. Verificar que "Relatórios Gerenciais" aparece no menu lateral (grupo Análise de Dados).
2. Clicar no item.

**Esperado:** navega para `/MuralhaDigital/RelatorioGerencial`; ícone e descrição corretos. ⏳ Pendente.

# Testes — Plano 30: Painéis de Assertividade e de Pré-Processamento

Roteiro para a [Entrega 30](../entregas/30-paineis-assertividade-preproc.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migração `docs/banco-de-dados/migracoes/20261016_paineis_assertividade_preproc.sql` executada.
- Usuário do grupo 42.

## T01 — Testes automatizados

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=AssertividadeServletTest'
```

**Esperado:** 8 testes, 0 falhas. No Windows (2026-10-16): ✅ 8/8.

## T02 — Painel de assertividade — cards em tempo real

1. Navegar para Monitoramento > Assertividade.
2. Verificar que os 8 cards aparecem: Taxa de Concordância, Volume Processado, Divergências, Revisão Manual, Pré-aprovadas, Desempates, Reprovadas, 1ª Análise.
3. Verificar que os valores são numéricos (mesmo que zero).

**Esperado:** todos os cards renderizam com valores coerentes. ⏳ Pendente.

## T03 — Auto-refresh

1. No painel de assertividade, verificar que o toggle "Auto 30s" está ligado.
2. Aguardar a contagem regressiva chegar a 0.
3. Verificar que os dados são recarregados automaticamente (timestamp do fetch atualiza).
4. Desligar o toggle e verificar que a contagem para.

**Esperado:** auto-refresh funciona e respeita o toggle. ⏳ Pendente.

## T04 — Alerta de aderência < 80%

1. Se a taxa de concordância estiver abaixo de 80%, verificar:
   - Card "Taxa de Concordância" com borda vermelha.
   - Banner de alerta visível no topo.
2. Se a taxa estiver ≥ 80%, verificar:
   - Card com borda verde.
   - Banner de alerta oculto.

**Esperado:** alerta visual corresponde ao limiar de 80%. ⏳ Pendente.

## T05 — Gráfico com linha de meta

1. No painel de assertividade, verificar que o gráfico de evolução exibe uma linha pontilhada vermelha em 80% com label "Meta 80%".

**Esperado:** linha de meta visível no gráfico de evolução. ⏳ Pendente.

## T06 — Painel de pré-processamento — cards e métricas

1. Navegar para Análise de Dados > Pré-Processamento (`/MuralhaDigital/PreProcessamento`).
2. Verificar os cards: Recebidos, Processados, Disponibilizados, Descartados, Aproveitamento, SLA, Pendentes, Backlog > 72h, Produtividade.
3. Alterar período (7/30/90 dias).

**Esperado:** página carrega sem erro; valores numéricos nos cards; filtro funciona. ⏳ Pendente.

## T07 — Gráfico de evolução e classificação

1. No painel de pré-processamento, verificar o gráfico de barras (recebidos × processados).
2. Verificar o doughnut de classificação.
3. Verificar a tabela de produtividade por operador.

**Esperado:** gráficos renderizam corretamente; tabela de operadores mostra dados (se houver análises no período). ⏳ Pendente.

## T08 — Auto-refresh do painel de pré-processamento

1. Verificar toggle "Auto 60s".
2. Aguardar contagem e verificar recarga.

**Esperado:** auto-refresh funciona. ⏳ Pendente.

## T09 — Menu de pré-processamento

1. Verificar que "Pré-Processamento" aparece no menu lateral (grupo Análise de Dados).
2. Clicar e navegar.

**Esperado:** navega para `/MuralhaDigital/PreProcessamento`; ícone e descrição corretos. ⏳ Pendente.

## T10 — Exportação CSV do painel de assertividade

1. No painel de assertividade, clicar em "CSV".
2. Abrir o arquivo.

**Esperado:** CSV com BOM UTF-8, separador `;`, dados do ranking de analistas. ⏳ Pendente.

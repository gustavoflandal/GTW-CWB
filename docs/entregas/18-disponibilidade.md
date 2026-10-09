# Entrega — Plano 18: Auditoria de Disponibilidade dos Equipamentos

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Monitoramento automatico da disponibilidade dos equipamentos de captura, com deteccao de inatividade, registro historico e painel de uptime com grafico.

## Origem

- Plano [`18-disponibilidade.md`](../planos-salvador/18-disponibilidade.md) — Auditoria de Disponibilidade dos Equipamentos

## Arquivos produzidos

| Arquivo | Descricao |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_disponibilidade_incidentes.sql` | Migracao combinada (Planos 18+19): CREATE TABLE `muralha.equipamento_disponibilidade` e `muralha.incidente_externo` |
| `src/main/java/muralha/digital/disponibilidade/DisponibilidadeJob.java` | Job Quartz (10 min): verifica ultima passagem por `id_local`, insere registro de disponibilidade |
| `src/main/java/muralha/digital/disponibilidade/DisponibilidadeServlet.java` | GET: status atual de cada equipamento + uptime 24h agregado |
| `src/main/webapp/muralha-digital/pages/monitoramento/disponibilidade/index.jsp` | Painel com cards (online/offline/uptime medio/total), tabela de status e grafico Chart.js de uptime |

## Arquitetura

- **Tabela `equipamento_disponibilidade`**: registra verificacao periodica por `id_local` com flag `disponivel`, timestamp da ultima passagem e minutos offline
- **Job Quartz a cada 10 minutos**: varre `id_local` distintos dos ultimos 7 dias de passagens, verifica se a ultima passagem e mais antiga que o threshold
- **Threshold configuravel**: `muralha.config_chave_valor` chave `disponibilidade_threshold_min` (padrao 15 min)
- **Join com `local_vigente`**: exibe nome do equipamento no painel
- **Uptime calculado**: percentual de verificacoes com `disponivel=1` nas ultimas 24h
- **Grafico horizontal Chart.js**: barras coloridas por faixa (verde >= 95%, amarelo >= 80%, vermelho < 80%)
- **Auto-refresh**: painel atualiza a cada 60 segundos

## Criterios de aceite

- [x] Build compila sem erros
- [ ] Job registrado no Quartz e executando a cada 10 minutos
- [ ] Cards exibem contagem online/offline
- [ ] Tabela lista equipamentos com status e ultima passagem
- [ ] Grafico de uptime 24h renderiza corretamente
- [ ] Auto-refresh funciona

## Pendencias

- Migracao `20261008_disponibilidade_incidentes.sql` pendente de execucao manual
- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/18-disponibilidade.md`)

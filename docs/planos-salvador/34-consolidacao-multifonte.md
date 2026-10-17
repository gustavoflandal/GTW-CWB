# Consolidação de Dados Multifonte — Plano de Implementação

> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) antes de usar o código deste plano.

**Goal:** Implementar pipeline de consolidação de dados provenientes de múltiplas fontes (equipamentos de campo, alertas, incidentes Waze, volumetria sumarizada) em view unificada, com servlet de consulta e tela administrativa, atendendo §5.4.1.1 do TR TRANSALVADOR.

**Architecture:** View SQL `muralha.v_dados_consolidados` unifica 4 fontes de dados com schema normalizado (fonte, dt_evento, equipamento, placa, tipo, lat/lon, descrição). `ConsolidacaoServlet` expõe ações `resumo` (totais por fonte), `consultar` (filtros dinâmicos) e `fontes` (lista). Tela JSP com cards resumo e tabela de consulta com filtros.

**Tech Stack:** Java Servlet · SQL View · Bootstrap 5.3 · JavaScript

## Referência ao Edital (TR TRANSALVADOR)

| Ref. TR | Requisito |
|---|---|
| §5.4.1.1 | Consolidação de dados de múltiplas fontes (equipamentos, sistemas) |

## Fontes consolidadas

| Fonte | Tabela de origem | Tipo de dados |
|---|---|---|
| PASSAGEM | `muralha.veiculo_tempo_real` + `dbo.local` | Passagens de veículos (8,2M registros) |
| ALERTA | `muralha.alerta` + `tipo_alerta_ocorrencia` | Alertas operacionais (placa monitorada, retenção etc.) |
| INCIDENTE_WAZE | `muralha.incidente` | Incidentes importados via Waze API (jams, alerts) |
| SUMARIZADO | `dbo.veiculo_sumarizado` + `dbo.local` | Volumetria horária por equipamento (4,15M registros) |

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/consolidacao/ConsolidacaoServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/inteligencia/consolidacao/index.jsp` |
| Criar | `docs/banco-de-dados/migracoes/20261016_consolidacao_multifonte.sql` |

## Migração

Script: `docs/banco-de-dados/migracoes/20261016_consolidacao_multifonte.sql`
- Cria view `muralha.v_dados_consolidados`
- Insere 1 parâmetro em `config_chave_valor`

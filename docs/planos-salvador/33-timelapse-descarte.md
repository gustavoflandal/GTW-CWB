# Descarte Automático de Time-lapse — Plano de Implementação

> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) antes de usar o código deste plano.

**Goal:** Implementar descarte parametrizado de dados de sessões de time-lapse, com retenção padrão de 72 horas e configuração via `config_chave_valor`, atendendo §5.7.7.5-6 do TR TRANSALVADOR.

**Architecture:** Job Quartz (`JobTimelapseDescarte`) roda a cada 60 min, consulta o parâmetro `TIMELAPSE_RETENCAO_HORAS` (padrão 72h) e remove registros mais antigos da tabela `timelapse_cache`. O `TimelapseServlet` foi alterado para registrar cada sessão consultada no cache.

**Tech Stack:** Java Servlet · Quartz · SQL Server

## Referência ao Edital (TR TRANSALVADOR)

| Ref. TR | Requisito |
|---|---|
| §5.7.7.5 | Descarte automático de vídeos operacionais time-lapse |
| §5.7.7.6 | Parametrização do período de descarte |

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/timelapse/JobTimelapseDescarte.java` |
| Criar | `docs/banco-de-dados/migracoes/20261016_timelapse_descarte.sql` |
| Modificar | `src/main/java/muralha/digital/timelapse/TimelapseServlet.java` — registro de sessão no cache |
| Modificar | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |

## Parâmetros de configuração (`config_chave_valor`)

| Chave | Padrão | Descrição |
|---|---|---|
| `TIMELAPSE_RETENCAO_HORAS` | 72 | Período de retenção em horas |
| `TIMELAPSE_DESCARTE_HABILITADO` | true | Habilita/desabilita o descarte automático |

## Migração

Script: `docs/banco-de-dados/migracoes/20261016_timelapse_descarte.sql`
- Cria tabela `muralha.timelapse_cache`
- Insere 2 parâmetros em `config_chave_valor`

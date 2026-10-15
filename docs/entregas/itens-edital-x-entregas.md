# Itens do Edital × Entregas — Capítulo 5 do TR TRANSALVADOR

Relação de todos os itens do Capítulo 5 do Termo de Referência (Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica), em ordem crescente, com o arquivo de entrega desta pasta que implementou cada item.

## Fontes

- Itens: [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md) — uma linha por item numerado sem subitens numerados. O item 5.1.5 aparece por letra (a–e) porque cada letra foi atendida por uma entrega diferente.
- Situação e entrega: [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) (fonte primária) e [`rastreabilidade-tr.md`](rastreabilidade-tr.md), usada quando a análise de aderência não avalia o item.
- Entrega 21 (Dashboard Builder) incorporada conforme [`21-dashboard-builder.md`](21-dashboard-builder.md).

## Legenda

| Valor na coluna "Entrega" | Significado |
|---|---|
| Nome do arquivo `.md` | Item implementado na entrega indicada |
| Implementação Parcial | Item atendido em parte; quando houver, segue o arquivo da entrega que tratou a parte implementada |
| Já atendido pelo GTW | Item que o sistema já atendia antes da PoC, sem entrega nova associada |
| Não implementado | Item sem desenvolvimento |
| † | Item que não constava da análise de aderência; situação atribuída nesta revisão, a partir do código e das entregas — recomenda-se conferência |
| ‡ | Obrigação contratual, operacional ou do equipamento de campo, sem funcionalidade de software a desenvolver no sistema |

## Resumo

| Situação | Itens | % |
|---|---|---|
| Implementado em entrega | 73 | 35% |
| Implementação Parcial | 54 | 26% |
| Já atendido pelo GTW | 34 | 16% |
| Não implementado | 46 | 22% |
| **Total** | **207** | 100% |

Dos 46 itens não implementados, 25 são obrigações contratuais, operacionais ou do equipamento de campo (‡). 27 dos itens parciais têm entrega associada. 89 itens estão marcados com † para conferência.

## 5.1 — Princípios Gerais de Controle de Acesso

| Item do edital | Entrega |
|---|---|
| 5.1.1 | Implementação Parcial — [05-dupla-analise.md](05-dupla-analise.md) |
| 5.1.2 | Já atendido pelo GTW † |
| 5.1.3 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.1.4 | Implementação Parcial — [02-sha256-integridade.md](02-sha256-integridade.md), [06-obliteracao-imagens.md](06-obliteracao-imagens.md), [13-anonimizacao-placas.md](13-anonimizacao-placas.md) † |
| 5.1.5.a | [09-cpf-matricula.md](09-cpf-matricula.md) |
| 5.1.5.b | Já atendido pelo GTW |
| 5.1.5.c | [03-politica-senhas.md](03-politica-senhas.md) |
| 5.1.5.d | [03-politica-senhas.md](03-politica-senhas.md) |
| 5.1.5.e | [04-mfa-totp.md](04-mfa-totp.md) |
| 5.1.6 | Já atendido pelo GTW |
| 5.1.7 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.1.8 | [01-log-auditoria.md](01-log-auditoria.md), [12-retencao-dados.md](12-retencao-dados.md) |
| 5.1.9 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.1.10 | [01-log-auditoria.md](01-log-auditoria.md), [11-exportacao-padronizada.md](11-exportacao-padronizada.md) |

## 5.2 — Funcionalidade de Gestão e Classificação de Infrações

| Item do edital | Entrega |
|---|---|
| 5.2.1.1 | Já atendido pelo GTW |
| 5.2.1.2 | [02-sha256-integridade.md](02-sha256-integridade.md) |
| 5.2.1.3 | Implementação Parcial |
| 5.2.1.4 | Já atendido pelo GTW |
| 5.2.1.5 | Implementação Parcial |
| 5.2.1.6 | Implementação Parcial — [10-sla-preprocessamento.md](10-sla-preprocessamento.md) |
| 5.2.2.1 | Implementação Parcial |
| 5.2.2.2 | Já atendido pelo GTW † |
| 5.2.2.3 | [08-painel-assertividade.md](08-painel-assertividade.md) |
| 5.2.2.4 | Já atendido pelo GTW |
| 5.2.2.5 | Implementação Parcial |
| 5.2.2.6 | [08-painel-assertividade.md](08-painel-assertividade.md) |
| 5.2.3.1 | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.2 | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.3 | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.4 | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.5 | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.4.1 | [17-timelapse.md](17-timelapse.md) |
| 5.2.4.2 | [17-timelapse.md](17-timelapse.md) |
| 5.2.4.3 | [06-obliteracao-imagens.md](06-obliteracao-imagens.md) |
| 5.2.4.4 | [06-obliteracao-imagens.md](06-obliteracao-imagens.md) |
| 5.2.4.5 | Já atendido pelo GTW |
| 5.2.5.1 | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.2 | Já atendido pelo GTW |
| 5.2.5.3 | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.4 | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.5 | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.6 | [12-retencao-dados.md](12-retencao-dados.md) † |
| 5.2.6.1 | Implementação Parcial † |
| 5.2.6.2 | Implementação Parcial |
| 5.2.6.3 | Não implementado † |
| 5.2.6.4 | Não implementado † ‡ |
| 5.2.7.1 | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.2.7.2 | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.2.7.3 | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.2.8.1 | [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.2.8.2 | [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.2.8.3 | [11-exportacao-padronizada.md](11-exportacao-padronizada.md) |
| 5.2.8.4 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |

## 5.3 — Funcionalidade de Alertas de Irregularidades

| Item do edital | Entrega |
|---|---|
| 5.3.1.1 | Já atendido pelo GTW |
| 5.3.1.2 | Já atendido pelo GTW |
| 5.3.1.3 | Implementação Parcial |
| 5.3.1.4 | Já atendido pelo GTW |
| 5.3.2.1 | Já atendido pelo GTW |
| 5.3.2.2 | Já atendido pelo GTW |
| 5.3.3.1 | Já atendido pelo GTW |
| 5.3.3.2 | Já atendido pelo GTW |
| 5.3.3.3 | Implementação Parcial † |
| 5.3.3.4 | Já atendido pelo GTW |
| 5.3.4.1 | Implementação Parcial † |
| 5.3.4.2 | Implementação Parcial † |
| 5.3.4.3 | Implementação Parcial † |
| 5.3.5.1 | Implementação Parcial † |
| 5.3.5.2 | Não implementado † ‡ |
| 5.3.5.3 | Não implementado |
| 5.3.6.1 | Já atendido pelo GTW † |
| 5.3.6.2 | Já atendido pelo GTW † |
| 5.3.6.3 | Implementação Parcial — [01-log-auditoria.md](01-log-auditoria.md) † |
| 5.3.6.4 | [11-exportacao-padronizada.md](11-exportacao-padronizada.md) |
| 5.3.7.1 | [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.2 | [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.3 | [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.4 | Não implementado |
| 5.3.7.5 | Implementação Parcial — [07-sla-latencia.md](07-sla-latencia.md) |
| 5.3.7.6 | Implementação Parcial † |
| 5.3.7.7 | [16-exportacao-gis.md](16-exportacao-gis.md), [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.8 | Já atendido pelo GTW † |

## 5.4 — Funcionalidade de Inteligência de Dados e Apoio à Decisão Operacional

| Item do edital | Entrega |
|---|---|
| 5.4.1.1 | Implementação Parcial |
| 5.4.1.2 | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.4.1.3 | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.4.1.4 | Implementação Parcial † |
| 5.4.2.1 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.2.2 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.2.3 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [18-disponibilidade.md](18-disponibilidade.md) † |
| 5.4.2.4 | [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.4.2.5 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.4.2.6 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.3.1 | Implementação Parcial — [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [21-dashboard-builder.md](21-dashboard-builder.md) † |
| 5.4.3.2 | Implementação Parcial † |
| 5.4.3.3 | Implementação Parcial — [15-kpis-configuraveis.md](15-kpis-configuraveis.md) † |
| 5.4.3.4 | Implementação Parcial † |
| 5.4.3.5 | Implementação Parcial — [15-kpis-configuraveis.md](15-kpis-configuraveis.md) † |
| 5.4.3.6 | Implementação Parcial — [15-kpis-configuraveis.md](15-kpis-configuraveis.md) † |
| 5.4.3.7 | Não implementado † |
| 5.4.4.1 | Já atendido pelo GTW |
| 5.4.4.2 | Já atendido pelo GTW |
| 5.4.4.3 | Já atendido pelo GTW |
| 5.4.4.4 | [16-exportacao-gis.md](16-exportacao-gis.md) |
| 5.4.5.1 | [21-dashboard-builder.md](21-dashboard-builder.md) † |
| 5.4.5.2 | [21-dashboard-builder.md](21-dashboard-builder.md) † |
| 5.4.6.1 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.6.2 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.6.3 | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.7.1 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.4.7.2 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.4.7.3 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.4.7.4 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.4.7.5 | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.4.7.6 | Implementação Parcial — [01-log-auditoria.md](01-log-auditoria.md) |
| 5.4.7.7 | [01-log-auditoria.md](01-log-auditoria.md) |

## 5.5 — Serviço de Pré-Processamento de Imagens

| Item do edital | Entrega |
|---|---|
| 5.5.1.1 | Não implementado † ‡ |
| 5.5.1.2 | Implementação Parcial — [02-sha256-integridade.md](02-sha256-integridade.md), [05-dupla-analise.md](05-dupla-analise.md) † |
| 5.5.1.3 | Implementação Parcial |
| 5.5.1.4 | Implementação Parcial — [05-dupla-analise.md](05-dupla-analise.md), [14-gestao-lotes.md](14-gestao-lotes.md) † |
| 5.5.2.1 | [10-sla-preprocessamento.md](10-sla-preprocessamento.md) |
| 5.5.2.2 | Não implementado † ‡ |
| 5.5.2.3 | Não implementado † ‡ |
| 5.5.2.4 | Não implementado † ‡ |
| 5.5.2.5 | Não implementado † ‡ |
| 5.5.3.1 | [10-sla-preprocessamento.md](10-sla-preprocessamento.md), [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.5.3.2 | Implementação Parcial — [10-sla-preprocessamento.md](10-sla-preprocessamento.md) † |
| 5.5.3.3 | Não implementado |
| 5.5.3.4 | [10-sla-preprocessamento.md](10-sla-preprocessamento.md), [11-exportacao-padronizada.md](11-exportacao-padronizada.md) † |
| 5.5.3.5 | Implementação Parcial — [21-dashboard-builder.md](21-dashboard-builder.md) † |
| 5.5.4.1 | Não implementado † ‡ |
| 5.5.4.2 | Já atendido pelo GTW |
| 5.5.4.3 | [17-timelapse.md](17-timelapse.md) † |
| 5.5.4.4 | Implementação Parcial — [01-log-auditoria.md](01-log-auditoria.md) † |
| 5.5.4.5 | Implementação Parcial — [01-log-auditoria.md](01-log-auditoria.md) |
| 5.5.4.6 | [12-retencao-dados.md](12-retencao-dados.md) |
| 5.5.4.7 | Implementação Parcial — [06-obliteracao-imagens.md](06-obliteracao-imagens.md), [13-anonimizacao-placas.md](13-anonimizacao-placas.md) † |
| 5.5.4.8 | [03-politica-senhas.md](03-politica-senhas.md), [04-mfa-totp.md](04-mfa-totp.md), [09-cpf-matricula.md](09-cpf-matricula.md) † |
| 5.5.4.9 | [01-log-auditoria.md](01-log-auditoria.md), [14-gestao-lotes.md](14-gestao-lotes.md) † |

## 5.6 — Infraestrutura Sistêmica de Comunicação, Processamento e Transmissão de Dados

| Item do edital | Entrega |
|---|---|
| 5.6.1.1 | Já atendido pelo GTW |
| 5.6.1.2 | Não implementado |
| 5.6.2.1 | Já atendido pelo GTW |
| 5.6.2.2 | Já atendido pelo GTW † |
| 5.6.2.3 | Implementação Parcial — [07-sla-latencia.md](07-sla-latencia.md) |
| 5.6.3 | Não implementado |
| 5.6.4.1 | Não implementado † ‡ |
| 5.6.4.2 | Não implementado † ‡ |
| 5.6.4.3 | Não implementado |
| 5.6.5 | Não implementado † ‡ |
| 5.6.6.1 | Implementação Parcial |
| 5.6.7 | Já atendido pelo GTW |

## 5.7 — Tecnologia de Leitura Automática de Placas Veiculares — LAP

| Item do edital | Entrega |
|---|---|
| 5.7.1.1 | Já atendido pelo GTW |
| 5.7.1.2 | Já atendido pelo GTW † |
| 5.7.1.3 | Implementação Parcial † |
| 5.7.1.4 | Não implementado † |
| 5.7.2.1 | Implementação Parcial |
| 5.7.2.2 | Não implementado † |
| 5.7.2.3 | Não implementado † |
| 5.7.2.4 | Não implementado † |
| 5.7.2.5 | Não implementado † ‡ |
| 5.7.3.1 | Não implementado † ‡ |
| 5.7.3.2 | Não implementado † ‡ |
| 5.7.3.3 | Já atendido pelo GTW † |
| 5.7.4.1 | Implementação Parcial |
| 5.7.4.2 | Implementação Parcial |
| 5.7.4.3 | Implementação Parcial |
| 5.7.4.4 | Já atendido pelo GTW |
| 5.7.5.1 | Já atendido pelo GTW |
| 5.7.5.2 | Implementação Parcial |
| 5.7.5.3 | [12-retencao-dados.md](12-retencao-dados.md) |
| 5.7.5.4 | Implementação Parcial † |
| 5.7.6.1 | Já atendido pelo GTW † |
| 5.7.6.2 | Implementação Parcial † |
| 5.7.7.1 | [17-timelapse.md](17-timelapse.md) † |
| 5.7.8.1 | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) † |
| 5.7.8.2 | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) † |
| 5.7.8.3 | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) † |
| 5.7.9.1 | Implementação Parcial — [07-sla-latencia.md](07-sla-latencia.md) |
| 5.7.9.2 | [07-sla-latencia.md](07-sla-latencia.md) † |
| 5.7.9.3 | [07-sla-latencia.md](07-sla-latencia.md) † |
| 5.7.10.1 | Não implementado † ‡ |
| 5.7.10.2 | Não implementado † ‡ |
| 5.7.10.3 | Não implementado † ‡ |
| 5.7.10.4 | Não implementado † ‡ |

## 5.8 — Infraestrutura de Datacenter, Disponibilidade e Continuidade Operacional

| Item do edital | Entrega |
|---|---|
| 5.8.1.1 | Não implementado † ‡ |
| 5.8.1.2 | Não implementado |
| 5.8.1.3 | Não implementado † ‡ |
| 5.8.1.4 | Não implementado † ‡ |
| 5.8.1.5 | Não implementado † ‡ |
| 5.8.2.1 | Implementação Parcial — [18-disponibilidade.md](18-disponibilidade.md) |
| 5.8.2.2 | Não implementado † ‡ |
| 5.8.2.3 | Implementação Parcial — [18-disponibilidade.md](18-disponibilidade.md) † |
| 5.8.2.4 | Não implementado † ‡ |
| 5.8.3.1 | Não implementado |
| 5.8.3.2 | Não implementado † |
| 5.8.3.3 | Não implementado |
| 5.8.4.1 | Implementação Parcial — [03-politica-senhas.md](03-politica-senhas.md), [04-mfa-totp.md](04-mfa-totp.md) † |
| 5.8.4.2 | Implementação Parcial — [04-mfa-totp.md](04-mfa-totp.md) † |
| 5.8.4.3 | Implementação Parcial — [06-obliteracao-imagens.md](06-obliteracao-imagens.md), [13-anonimizacao-placas.md](13-anonimizacao-placas.md) † |
| 5.8.4.4 | Não implementado |
| 5.8.5.1 | Não implementado |
| 5.8.5.2 | Não implementado † |
| 5.8.5.3 | Implementação Parcial — [12-retencao-dados.md](12-retencao-dados.md) † |
| 5.8.5.4 | Não implementado |
| 5.8.5.5 | Não implementado † ‡ |
| 5.8.5.6 | Não implementado † |
| 5.8.6.1 | Implementação Parcial — [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.8.6.2 | [01-log-auditoria.md](01-log-auditoria.md), [18-disponibilidade.md](18-disponibilidade.md), [21-dashboard-builder.md](21-dashboard-builder.md) † |
| 5.8.6.3 | Implementação Parcial — [18-disponibilidade.md](18-disponibilidade.md) † |

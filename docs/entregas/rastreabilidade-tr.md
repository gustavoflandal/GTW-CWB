# Matriz de Rastreabilidade — TR TRANSALVADOR x Planos x Entregas x Testes

Mapeamento cruzado entre os itens do Termo de Referência (Cap. 5 + Anexo A PoC) e os artefatos produzidos na PoC.

> **Fonte primária de rastreabilidade:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) — contém status de cada requisito e links para os artefatos de resolução. Esta matriz é derivada do documento de aderência.

**Documento de referência:** [`docs-editais/edital-salvador/TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md)

---

## 5.1 — Principios Gerais de Controle de Acesso

| Item TR | Requisito | Plano | Entrega | Teste |
|---|---|---|---|---|
| 5.1.3 | Rastreabilidade integral das acoes | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |
| 5.1.5.a | Identificacao por CPF/matricula | [09](../planos-salvador/09-cpf-matricula.md) | [09](09-cpf-matricula.md) | [09](../testes/09-cpf-matricula.md) |
| 5.1.5.c | Politica de senhas (complexidade, validade, historico, bloqueio) | [03](../planos-salvador/03-politica-senhas.md) | [03](03-politica-senhas.md) | [03](../testes/03-politica-senhas.md) |
| 5.1.5.d | Encerramento de sessao por inatividade | [03](../planos-salvador/03-politica-senhas.md) | [03](03-politica-senhas.md) | [03](../testes/03-politica-senhas.md) |
| 5.1.5.e | Autenticacao multifator (MFA) | [04](../planos-salvador/04-mfa-totp.md) | [04](04-mfa-totp.md) | [04](../testes/04-mfa-totp.md) |
| 5.1.7 | Log automatico, inviolavel, rastreavel | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |
| 5.1.8 | Retencao de logs durante vigencia | [12](../planos-salvador/12-retencao-dados.md) | [12](12-retencao-dados.md) | [12](../testes/12-retencao-dados.md) |
| 5.1.9 | Conteudo minimo do log (usuario, data, IP, funcionalidade, operacao, registro) | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |
| 5.1.10 | Consultas nos logs com filtros + exportacao PDF/CSV/XLS | [01](../planos-salvador/01-log-auditoria.md), [11](../planos-salvador/11-exportacao-padronizada.md) | [01](01-log-auditoria.md), [11](11-exportacao-padronizada.md) | [01](../testes/01-log-auditoria.md), [11](../testes/11-exportacao-padronizada.md) |

## 5.2 — Funcionalidade de Gestao e Classificacao de Infracoes

| Item TR | Requisito | Plano | Entrega | Teste |
|---|---|---|---|---|
| 5.2.1.2 | Verificacao SHA-256 de integridade | [02](../planos-salvador/02-sha256-integridade.md) | [02](02-sha256-integridade.md) | [02](../testes/02-sha256-integridade.md) |
| 5.2.2.3 | Indice minimo 80% de aderencia da pre-classificacao | [08](../planos-salvador/08-painel-assertividade.md) | [08](08-painel-assertividade.md) | [08](../testes/08-painel-assertividade.md) |
| 5.2.2.6 | Painel de controle com taxa de assertividade | [08](../planos-salvador/08-painel-assertividade.md) | [08](08-painel-assertividade.md) | [08](../testes/08-painel-assertividade.md) |
| 5.2.3.1–5 | Dupla analise independente, desempatador, log | [05](../planos-salvador/05-dupla-analise.md) | [05](05-dupla-analise.md) | [05](../testes/05-dupla-analise.md) |
| 5.2.4.1–2 | Interface de analise de imagens (alta resolucao, ampliacao) | [17](../planos-salvador/17-timelapse.md) | [17](17-timelapse.md) | [17](../testes/17-timelapse.md) |
| 5.2.4.3–4 | Obliteracao de imagens + reversao com justificativa | [06](../planos-salvador/06-obliteracao-imagens.md) | [06](06-obliteracao-imagens.md) | [06](../testes/06-obliteracao-imagens.md) |
| 5.2.5.1–5 | Gestao de lotes, validacao, impedimento de transmissao | [14](../planos-salvador/14-gestao-lotes.md) | [14](14-gestao-lotes.md) | [14](../testes/14-gestao-lotes.md) |
| 5.2.7.1–3 | Telemetria operacional: monitoramento de equipamentos | [07](../planos-salvador/07-sla-latencia.md), [18](../planos-salvador/18-disponibilidade.md) | [07](07-sla-latencia.md), [18](18-disponibilidade.md) | [07](../testes/07-sla-latencia.md), [18](../testes/18-disponibilidade.md) |
| 5.2.8.1–4 | Relatorios gerenciais com filtros e exportacao | [11](../planos-salvador/11-exportacao-padronizada.md), [15](../planos-salvador/15-kpis-configuráveis.md) | [11](11-exportacao-padronizada.md), [15](15-kpis-configuraveis.md) | [11](../testes/11-exportacao-padronizada.md), [15](../testes/15-kpis-configuraveis.md) |

## 5.3 — Funcionalidade de Alertas de Irregularidades

| Item TR | Requisito | Plano | Entrega | Teste |
|---|---|---|---|---|
| 5.3.7.1–3 | Integracao com sensores externos (Waze, Google Maps) | [19](../planos-salvador/19-sensores-externos.md) | [19](19-sensores-externos.md) | [19](../testes/19-sensores-externos.md) |
| 5.3.7.7 | Mapas, graficos, indicadores visuais | [16](../planos-salvador/16-exportacao-gis.md), [19](../planos-salvador/19-sensores-externos.md) | [16](16-exportacao-gis.md), [19](19-sensores-externos.md) | [16](../testes/16-exportacao-gis.md), [19](../testes/19-sensores-externos.md) |

## 5.4 — Funcionalidade de Inteligencia de Dados

| Item TR | Requisito | Plano | Entrega | Teste |
|---|---|---|---|---|
| 5.4.1.2.g | Anonimizacao de dados (LGPD) | [13](../planos-salvador/13-anonimizacao-placas.md) | [13](13-anonimizacao-placas.md) | [13](../testes/13-anonimizacao-placas.md) |
| 5.4.2.1–6 | Dashboards analiticos interativos | [15](../planos-salvador/15-kpis-configuráveis.md) | [15](15-kpis-configuraveis.md) | [15](../testes/15-kpis-configuraveis.md) |
| 5.4.2.3.e | Disponibilidade da solucao | [18](../planos-salvador/18-disponibilidade.md) | [18](18-disponibilidade.md) | [18](../testes/18-disponibilidade.md) |
| 5.4.4.1–4 | Analise espacial, georreferenciamento e exportacao GIS | [16](../planos-salvador/16-exportacao-gis.md) | [16](16-exportacao-gis.md) | [16](../testes/16-exportacao-gis.md) |
| 5.4.6.1–3 | Indicadores operacionais configuraveis com metas e alertas | [15](../planos-salvador/15-kpis-configuráveis.md) | [15](15-kpis-configuraveis.md) | [15](../testes/15-kpis-configuraveis.md) |
| 5.4.7.1–7 | Repositorio central de logs | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |

## 5.5 — Servico de Pre-Processamento

| Item TR | Requisito | Plano | Entrega | Teste |
|---|---|---|---|---|
| 5.5.2.1 | Prazo maximo de 72h para pre-processamento | [10](../planos-salvador/10-sla-preprocessamento.md) | [10](10-sla-preprocessamento.md) | [10](../testes/10-sla-preprocessamento.md) |
| 5.5.4.6 | Retencao de logs por 5 anos apos encerramento | [12](../planos-salvador/12-retencao-dados.md) | [12](12-retencao-dados.md) | [12](../testes/12-retencao-dados.md) |

## Anexo A — Prova de Conceito

| Item PoC | Verificacao | Planos relacionados |
|---|---|---|
| 5.2.8.2.a | Gestao e Classificacao (recepcao, dupla classificacao, lotes) | [02](../planos-salvador/02-sha256-integridade.md), [05](../planos-salvador/05-dupla-analise.md), [14](../planos-salvador/14-gestao-lotes.md) |
| 5.2.8.2.b | Alertas de Irregularidades (geracao, parametrizacao, rastreabilidade) | [19](../planos-salvador/19-sensores-externos.md) |
| 5.2.8.2.c | Inteligencia de Dados (paineis, indicadores, exportacao) | [15](../planos-salvador/15-kpis-configuráveis.md), [16](../planos-salvador/16-exportacao-gis.md), [11](../planos-salvador/11-exportacao-padronizada.md) |
| 5.2.8.2.d | Integracao equipamentos-sistema (imagens, registros, tempo real) | [07](../planos-salvador/07-sla-latencia.md), [17](../planos-salvador/17-timelapse.md) |

---

## Cobertura por Plano

| # | Plano | Itens TR atendidos |
|---|---|---|
| 01 | Log de Auditoria | 5.1.3, 5.1.7, 5.1.9, 5.1.10, 5.4.7.1–7 |
| 02 | SHA-256 Integridade | 5.2.1.2 |
| 03 | Politica de Senhas | 5.1.5.c, 5.1.5.d |
| 04 | MFA/TOTP | 5.1.5.e |
| 05 | Dupla Analise | 5.2.3.1–5 |
| 06 | Obliteracao de Imagens | 5.2.4.3–4 |
| 07 | SLA de Latencia | 5.2.7.1–3 |
| 08 | Painel de Assertividade | 5.2.2.3, 5.2.2.6 |
| 09 | CPF/Matricula | 5.1.5.a |
| 10 | SLA Pre-processamento | 5.5.2.1 |
| 11 | Exportacao Padronizada | 5.1.10, 5.2.8.3 |
| 12 | Retencao de Dados | 5.1.8, 5.5.4.6 |
| 13 | Anonimizacao de Placas | 5.1.4, 5.4.1.2.g |
| 14 | Gestao de Lotes | 5.2.5.1–5 |
| 15 | KPIs Configuraveis | 5.2.8.4, 5.4.2.1–6, 5.4.6.1–3 |
| 16 | Exportacao GIS | 5.4.4.1–4, 5.3.7.7 |
| 17 | Time-lapse | 5.2.4.1–2 |
| 18 | Disponibilidade | 5.2.7.1–3, 5.4.2.3.e |
| 19 | Sensores Externos | 5.3.7.1–3 |

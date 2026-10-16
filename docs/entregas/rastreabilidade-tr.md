# Matriz de Rastreabilidade — TR TRANSALVADOR x Planos x Entregas x Testes

Mapeamento cruzado entre os itens do Termo de Referência (Cap. 5 + Anexo A PoC) e os artefatos produzidos na PoC.

> **Fonte primária de status:** matriz canônica [`itens-edital-x-entregas.md`](itens-edital-x-entregas.md). A coluna "Situação" repete o status dela (agregado quando a linha cobre vários itens: ✅ só se todos forem ✅). Revisão de 2026-10-16: [Plano 23](../planos-salvador/23-correcao-documentacao-aderencia.md).

**Documento de referência:** [`docs-editais/edital-salvador/TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md)

---

## 5.1 — Principios Gerais de Controle de Acesso

| Item TR | Requisito | Situação | Plano | Entrega | Teste |
|---|---|---|---|---|---|
| 5.1.3 | Rastreabilidade integral das acoes | ✅ | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |
| 5.1.5.a | Identificacao por CPF/matricula | ✅ | [09](../planos-salvador/09-cpf-matricula.md) | [09](09-cpf-matricula.md) | [09](../testes/09-cpf-matricula.md) |
| 5.1.5.c | Politica de senhas (complexidade, validade, historico, bloqueio) | ✅ | [03](../planos-salvador/03-politica-senhas.md) | [03](03-politica-senhas.md) | [03](../testes/03-politica-senhas.md) |
| 5.1.5.d | Encerramento de sessao por inatividade | ✅ | [03](../planos-salvador/03-politica-senhas.md) | [03](03-politica-senhas.md) | [03](../testes/03-politica-senhas.md) |
| 5.1.5.e | Autenticacao multifator (MFA) | ✅ | [04](../planos-salvador/04-mfa-totp.md) | [04](04-mfa-totp.md) | [04](../testes/04-mfa-totp.md) |
| 5.1.7 | Log automatico, inviolavel, rastreavel | ✅ | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |
| 5.1.8 | Retencao de logs durante vigencia | ✅ | [12](../planos-salvador/12-retencao-dados.md) | [12](12-retencao-dados.md) | [12](../testes/12-retencao-dados.md) |
| 5.1.9 | Conteudo minimo do log (usuario, data, IP, funcionalidade, operacao, registro) | ✅ | [01](../planos-salvador/01-log-auditoria.md) | [01](01-log-auditoria.md) | [01](../testes/01-log-auditoria.md) |
| 5.1.10 | Consultas nos logs com filtros + exportacao PDF/CSV/XLS | ⚠️ | [01](../planos-salvador/01-log-auditoria.md), [11](../planos-salvador/11-exportacao-padronizada.md) | [01](01-log-auditoria.md), [11](11-exportacao-padronizada.md) | [01](../testes/01-log-auditoria.md), [11](../testes/11-exportacao-padronizada.md) |

## 5.2 — Funcionalidade de Gestao e Classificacao de Infracoes

| Item TR | Requisito | Situação | Plano | Entrega | Teste |
|---|---|---|---|---|---|
| 5.2.1.2 | Verificacao SHA-256 de integridade | ⚠️ | [02](../planos-salvador/02-sha256-integridade.md), [25](../planos-salvador/25-integridade-sha256-recepcao.md) | [02](02-sha256-integridade.md), [25](25-integridade-sha256-recepcao.md) | [02](../testes/02-sha256-integridade.md), [25](../testes/25-integridade-sha256-recepcao.md) |
| 5.2.1.3 | Registro de origem, captura, recepcao e quantidades (recebidos, integros, rejeitados com motivo) | ⚠️ | [25](../planos-salvador/25-integridade-sha256-recepcao.md) | [25](25-integridade-sha256-recepcao.md) | [25](../testes/25-integridade-sha256-recepcao.md) |
| 5.2.2.3 | Indice minimo 80% de aderencia da pre-classificacao | ✅ | [08](../planos-salvador/08-painel-assertividade.md) | [08](08-painel-assertividade.md) | [08](../testes/08-painel-assertividade.md) |
| 5.2.2.5 | Encaminhamento automatico de divergencias para revisao | ⚠️ | [27](../planos-salvador/27-score-ocr-revisao-placas.md) | [27](27-score-ocr-revisao-placas.md) | [27](../testes/27-score-ocr-revisao-placas.md) |
| 5.2.2.6 | Painel de controle com taxa de assertividade | ✅ | [08](../planos-salvador/08-painel-assertividade.md), [30](../planos-salvador/30-paineis-assertividade-preproc.md) | [08](08-painel-assertividade.md), [30](30-paineis-assertividade-preproc.md) | [08](../testes/08-painel-assertividade.md), [30](../testes/30-paineis-assertividade-preproc.md) |
| 5.2.3.1–5 | Dupla analise independente, desempatador, log | ✅ | [05](../planos-salvador/05-dupla-analise.md) | [05](05-dupla-analise.md) | [05](../testes/05-dupla-analise.md) |
| 5.2.4.1–2 | Interface de analise de imagens (alta resolucao, ampliacao) | ✅ | [05](../planos-salvador/05-dupla-analise.md) (brilho/contraste em `dupla-analise.js`) e legado `proc_infracao.jsp` | [05](05-dupla-analise.md) | [05](../testes/05-dupla-analise.md) |
| 5.2.4.3–4 | Obliteracao de imagens + reversao com justificativa | ✅ | [06](../planos-salvador/06-obliteracao-imagens.md) | [06](06-obliteracao-imagens.md) | [06](../testes/06-obliteracao-imagens.md) |
| 5.2.5.1–5 | Gestao de lotes, validacao, impedimento de transmissao | ✅ | [14](../planos-salvador/14-gestao-lotes.md) | [14](14-gestao-lotes.md) | [14](../testes/14-gestao-lotes.md) |
| 5.2.7.1–3 | Telemetria operacional: monitoramento de equipamentos | ✅ | [07](../planos-salvador/07-sla-latencia.md), [18](../planos-salvador/18-disponibilidade.md) | [07](07-sla-latencia.md), [18](18-disponibilidade.md) | [07](../testes/07-sla-latencia.md), [18](../testes/18-disponibilidade.md) |
| 5.2.8.1–4 | Relatorios gerenciais com filtros e exportacao | ⚠️ | [11](../planos-salvador/11-exportacao-padronizada.md), [15](../planos-salvador/15-kpis-configuráveis.md) | [11](11-exportacao-padronizada.md), [15](15-kpis-configuraveis.md) | [11](../testes/11-exportacao-padronizada.md), [15](../testes/15-kpis-configuraveis.md) |

## 5.3 — Funcionalidade de Alertas de Irregularidades

| Item TR | Requisito | Situação | Plano | Entrega | Teste |
|---|---|---|---|---|---|
| 5.3.7.1–3 | Integracao com sensores externos (Waze, Google Maps) | ✅ | [19](../planos-salvador/19-sensores-externos.md) | [19](19-sensores-externos.md) | [19](../testes/19-sensores-externos.md) |
| 5.3.7.7 | Mapas, graficos, indicadores visuais | ✅ | [16](../planos-salvador/16-exportacao-gis.md), [19](../planos-salvador/19-sensores-externos.md) | [16](16-exportacao-gis.md), [19](19-sensores-externos.md) | [16](../testes/16-exportacao-gis.md), [19](../testes/19-sensores-externos.md) |

## 5.4 — Funcionalidade de Inteligencia de Dados

| Item TR | Requisito | Situação | Plano | Entrega | Teste |
|---|---|---|---|---|---|
| 5.4.1.2.g | Anonimizacao de dados (LGPD) | ✅ | [13](../planos-salvador/13-anonimizacao-placas.md) | [13](13-anonimizacao-placas.md) | [13](../testes/13-anonimizacao-placas.md) |
| 5.4.2.1–6 | Dashboards analiticos interativos | ⚠️ | [15](../planos-salvador/15-kpis-configuráveis.md) | [15](15-kpis-configuraveis.md) | [15](../testes/15-kpis-configuraveis.md) |
| 5.4.2.3.e | Disponibilidade da solucao | ✅ | [18](../planos-salvador/18-disponibilidade.md) | [18](18-disponibilidade.md) | [18](../testes/18-disponibilidade.md) |
| 5.4.2.4a-b | Filtros dinamicos com atualizacao simultanea | ⚠️ | [20](../planos-salvador/20-dashboard-builder.md) | [21](21-dashboard-builder.md) | [20](../testes/20-dashboard-builder.md) |
| 5.4.2.5 | Dashboards customizaveis pela CONTRATANTE | ✅ | [20](../planos-salvador/20-dashboard-builder.md) | [21](21-dashboard-builder.md) | [20](../testes/20-dashboard-builder.md) |
| 5.4.4.1–4 | Analise espacial, georreferenciamento e exportacao GIS | ⚠️ | [16](../planos-salvador/16-exportacao-gis.md) | [16](16-exportacao-gis.md) | [16](../testes/16-exportacao-gis.md) |
| 5.4.6.1–3 | Indicadores operacionais configuraveis com metas e alertas | ⚠️ | [15](../planos-salvador/15-kpis-configuráveis.md) | [15](15-kpis-configuraveis.md) | [15](../testes/15-kpis-configuraveis.md) |
| 5.4.7.1–7 | Repositorio central de logs e historico de parametros | ⚠️ | [01](../planos-salvador/01-log-auditoria.md), [26](../planos-salvador/26-logs-tecnicos-historico-parametros.md) | [01](01-log-auditoria.md), [26](26-logs-tecnicos-historico-parametros.md) | [01](../testes/01-log-auditoria.md), [26](../testes/26-logs-tecnicos-historico-parametros.md) |

## 5.5 — Servico de Pre-Processamento

| Item TR | Requisito | Situação | Plano | Entrega | Teste |
|---|---|---|---|---|---|
| 5.5.2.1 | Prazo maximo de 72h para pre-processamento | ✅ | [10](../planos-salvador/10-sla-preprocessamento.md) | [10](10-sla-preprocessamento.md) | [10](../testes/10-sla-preprocessamento.md) |
| 5.5.3.1 | Painel operacional pre-processamento | ⚠️ | [20](../planos-salvador/20-dashboard-builder.md) | [21](21-dashboard-builder.md) | [20](../testes/20-dashboard-builder.md) |
| 5.5.4.6 | Retencao de logs por 5 anos apos encerramento | ✅ | [12](../planos-salvador/12-retencao-dados.md) | [12](12-retencao-dados.md) | [12](../testes/12-retencao-dados.md) |

## 5.7 — Leitura Automática de Placas (LAP)

| Item TR | Requisito | Situação | Plano | Entrega | Teste |
|---|---|---|---|---|---|
| 5.7.1.4 | Registro individual de cada placa em composicoes com multiplas placas | ⚠️ | [27](../planos-salvador/27-score-ocr-revisao-placas.md) | [27](27-score-ocr-revisao-placas.md) | [27](../testes/27-score-ocr-revisao-placas.md) |
| 5.7.4.1–4 | Score de confianca, limiar, encaminhamento e metadado permanente | ⚠️ | [27](../planos-salvador/27-score-ocr-revisao-placas.md) | [27](27-score-ocr-revisao-placas.md) | [27](../testes/27-score-ocr-revisao-placas.md) |
| 5.7.5.2 | Log automatico de toda correcao manual | ⚠️ | [27](../planos-salvador/27-score-ocr-revisao-placas.md) | [27](27-score-ocr-revisao-placas.md) | [27](../testes/27-score-ocr-revisao-placas.md) |
| 5.7.5.4 | Consulta por equipamento, periodo, score, tipo de intervencao e operador | ⚠️ | [27](../planos-salvador/27-score-ocr-revisao-placas.md) | [27](27-score-ocr-revisao-placas.md) | [27](../testes/27-score-ocr-revisao-placas.md) |

## 7 — Central de Suporte Técnico (aba "Sist. Chamado")

Itens da planilha [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx); o Capítulo 7 do TR não está no repositório.

| Item | Requisito | Plano | Entrega | Teste |
|---|---|---|---|---|
| 7.2.3 (a–f) | Sistema de chamados: abertura com prioridade, acompanhamento de status, histórico, relatórios, avaliação e exportação PDF/CSV/XLS | [22](../planos-salvador/22-sistema-chamados.md) | [22](22-sistema-chamados.md) | [22](../testes/22-sistema-chamados.md) |
| 7.2.4 | Protocolo único com confirmação automática (protocolo, prioridade, prazos) | [22](../planos-salvador/22-sistema-chamados.md) | [22](22-sistema-chamados.md) | [22](../testes/22-sistema-chamados.md) |

## Anexo A — Prova de Conceito

| Item PoC | Verificacao | Planos relacionados |
|---|---|---|
| 5.2.8.2.a | Gestao e Classificacao (recepcao, dupla classificacao, lotes) | ✅ | [02](../planos-salvador/02-sha256-integridade.md), [05](../planos-salvador/05-dupla-analise.md), [14](../planos-salvador/14-gestao-lotes.md) |
| 5.2.8.2.b | Alertas de Irregularidades (geracao, parametrizacao, rastreabilidade) | ✅ | [19](../planos-salvador/19-sensores-externos.md) |
| 5.2.8.2.c | Inteligencia de Dados (paineis, indicadores, exportacao) | ✅ | [15](../planos-salvador/15-kpis-configuráveis.md), [16](../planos-salvador/16-exportacao-gis.md), [11](../planos-salvador/11-exportacao-padronizada.md) |
| 5.2.8.2.d | Integracao equipamentos-sistema (imagens, registros, tempo real) | ✅ | [07](../planos-salvador/07-sla-latencia.md), [17](../planos-salvador/17-timelapse.md) |

---

## Cobertura por Plano

Itens do TR tratados por cada plano; "(parcial)" quando o item está ⚠️ na matriz canônica. A numeração de plano, entrega e teste não é sempre a mesma: o **Plano 20** gerou a **Entrega 21** e o **Teste 20**; o **Plano 21** não tem entrega própria (está embutido na Entrega 21); a **Entrega 20** (menus) não tem plano próprio e nasceu da pendência registrada na Entrega 17 (execução do Plano 17).

| # | Plano | Entrega | Teste | Itens TR atendidos |
|---|---|---|---|---|
| 01 | Log de Auditoria | 01 | 01 | 5.1.3, 5.1.7, 5.1.9, 5.1.10 (parcial), 5.4.7.1–5 e 5.4.7.7 (parcial: só logs de usuários) |
| 02 | SHA-256 Integridade | 02 | 02 | 5.2.1.2 (parcial) |
| 03 | Politica de Senhas | 03 | 03 | 5.1.5.c, 5.1.5.d, 5.3.7.5 (parcial) |
| 04 | MFA/TOTP | 04 | 04 | 5.1.5.e, 5.3.7.5 (parcial) |
| 05 | Dupla Analise | 05 | 05 | 5.2.3.1–5, 5.2.4.1–2 (com o legado), 5.5.4.3 (parcial) |
| 06 | Obliteracao de Imagens | 06 | 06 | 5.2.4.3–4 |
| 07 | SLA de Latencia | 07 | 07 | 5.2.7.1–3, 5.2.2.1, 5.6.2.3 e 5.7.9.1 (parcial), 5.7.9.2–3 |
| 08 | Painel de Assertividade | 08 | 08 | 5.2.2.3, 5.2.2.6 (parcial) |
| 09 | CPF/Matricula | 09 | 09 | 5.1.5.a, 5.3.7.5 (parcial) |
| 10 | SLA Pre-processamento | 10 | 10 | 5.5.2.1, 5.5.3.1 (parcial) |
| 11 | Exportacao Padronizada | 11 | 11 | 5.1.10 (parcial), 5.2.8.3 |
| 12 | Retencao de Dados | 12 | 12 | 5.1.8, 5.5.4.6, 5.7.5.3 |
| 13 | Anonimizacao de Placas | 13 | 13 | 5.4.1.2.g, 5.7.8.1–3 |
| 14 | Gestao de Lotes | 14 | 14 | 5.2.5.1–5 |
| 15 | KPIs Configuraveis | 15 | 15 | 5.2.8.4 (parcial), 5.4.2.1–2, 5.4.6.1, 5.4.6.2–3 (parcial) |
| 16 | Exportacao GIS | 16 | 16 | 5.4.4.4 (parcial), 5.3.7.7 |
| 17 | Time-lapse | 17 | 17 | 5.7.7.1–4 (parcial: player de imagens, sem vídeo) |
| 18 | Disponibilidade | 18 | 18 | 5.2.7.1–3, 5.4.2.3.e, 5.6.1.5 |
| 19 | Sensores Externos | 19 | 19 | 5.3.7.1–3 |
| 20 | Dashboard Builder | 21 | 20 | 5.4.2.4 (parcial), 5.4.2.5, 5.4.5.1–2 (parcial), 5.5.3.1 (parcial) |
| 21 | Consulta SQL própria sem Cube | 21 (embutido) | 20 | Sem itens próprios: corrige a camada de consulta do Plano 20 |
| 22 | Sistema de Chamados | 22 | 22 | 7.2.3 (a–f), 7.2.4 |
| 23 | Correção da documentação de aderência | 23 | 23 | Sem itens próprios: alinha a documentação ao código |
| 24 | Correções pontuais da auditoria | 24 | 24 | Sem itens próprios: corrige defeitos (completa a Waze da Entrega 19) |
| 25 | Integridade SHA-256 na recepção | 25 | 25 | 5.2.1.2 (parcial: conferência pronta, sem hash de origem enviado), 5.2.1.3 (parcial: aguardando migração e teste) |
| 26 | Logs técnicos e histórico de parâmetros | 26 | 26 | 5.4.7.1–7 (parcial), 5.4.7.6 (parcial: aguardando migração e teste), 5.2.7.2 (períodos início/fim) |
| 27 | Score de OCR, revisão automática e múltiplas placas | 27 | 27 | 5.7.1.4, 5.7.4.1–4, 5.2.2.5, 5.7.5.2, 5.7.5.4 (parciais: aguardando migração e teste; equipamentos não enviam score) |
| — | Registro dos módulos no menu (pendência da Entrega 17) | 20 | — | Sem itens próprios |

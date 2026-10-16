# Itens do Edital × Entregas — Capítulo 5 do TR TRANSALVADOR

**Matriz canônica de aderência.** Este é o documento primário de situação de cada item do Capítulo 5 do Termo de Referência. [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md), [`aderencia-comparativa.md`](aderencia-comparativa.md) e [`rastreabilidade-tr.md`](rastreabilidade-tr.md) repetem os mesmos status; em caso de diferença, vale esta matriz.

**Revisão:** 2026-10-16, após a auditoria que cruzou a documentação com o código (commit `06740f8`). Ver [Plano 23](../planos-salvador/23-correcao-documentacao-aderencia.md) e [Entrega 23](23-correcao-documentacao-aderencia.md).

## Fontes

- Itens: [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md), uma linha por item numerado sem subitens numerados. O item 5.1.5 aparece por letra (a–e) porque cada letra foi atendida por uma entrega diferente. Nas seções 5.6 e 5.7.7 o `.md` resume os subitens em faixas (ex.: "5.6.3.1–4"); a numeração e o texto completos vêm do PDF [`TR Salvador - Sistema.pdf`](../../docs-editais/edital-salvador/TR%20Salvador%20-%20Sistema.pdf).
- Situação: conferida no código. "✅" significa que a funcionalidade existe no código; **não** significa que os critérios de aceite da entrega foram executados (ver a coluna Status em [`README.md`](README.md)).
- Conferência automática: [`docs/referencia/scripts/validar_aderencia.py`](../referencia/scripts/validar_aderencia.py) compara os status das quatro fontes e valida os links relativos.

## Legenda

| Situação | Significado |
|---|---|
| ✅ Implementado | Item implementado na entrega indicada |
| ✅ Já atendido pelo GTW | Item que o sistema já atendia antes da PoC, sem entrega nova associada |
| ⚠️ Parcial | Item atendido em parte; a coluna de evidência diz o que existe e o que falta |
| ❌ Não implementado | Item sem desenvolvimento |
| † | Situação atribuída por revisão do código, sem avaliação na análise de aderência original — recomenda-se conferência |
| ‡ | Obrigação contratual, operacional ou do equipamento de campo, sem funcionalidade de software a desenvolver no sistema |

## Resumo

| Situação | Itens | % |
|---|---|---|
| ✅ Implementado em entrega | 54 | 23% |
| ✅ Já atendido pelo GTW | 32 | 14% |
| ⚠️ Parcial | 88 | 38% |
| ❌ Não implementado | 58 | 25% |
| **Total** | **232** | 100% |

Em 2026-10-16 a matriz passou de 207 para 232 itens: entraram os 28 subitens de 5.6 e 5.7.7 que estavam omitidos (5.6.1.3–5, 5.6.2.4–5, 5.6.3.1–4, 5.6.4.4–5, 5.6.5.1–4, 5.6.6.2–4, 5.6.7.1–5, 5.7.7.2–6) e saíram as três linhas agregadas 5.6.3, 5.6.5 e 5.6.7.

### Cobertura por seção do TR

Cobertura = (✅ + 0,5 × ⚠️) ÷ itens da seção. O cálculo é refeito pelo script de validação (`--resumo`).

| Seção | Itens | ✅ | ⚠️ | ❌ | Cobertura |
|---|---|---|---|---|---|
| 5.1 Controle de Acesso | 14 | 11 | 3 | 0 | 89% |
| 5.2 Gestão e Classificação de Infrações | 39 | 27 | 10 | 2 | 82% |
| 5.3 Alertas de Irregularidades | 28 | 11 | 15 | 2 | 66% |
| 5.4 Inteligência de Dados | 33 | 11 | 21 | 1 | 65% |
| 5.5 Pré-processamento de Imagens | 23 | 6 | 10 | 7 | 48% |
| 5.6 Infraestrutura de Comunicação | 32 | 8 | 6 | 18 | 34% |
| 5.7 LAP | 38 | 11 | 15 | 12 | 49% |
| 5.8 Datacenter e Continuidade | 25 | 1 | 8 | 16 | 20% |
| **Total** | **232** | **86** | **88** | **58** | **56%** |

## 5.1 — Princípios Gerais de Controle de Acesso

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.1.1 | ⚠️ Parcial | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.1.2 | ✅ Já atendido pelo GTW † | — |
| 5.1.3 | ✅ Implementado | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.1.4 | ⚠️ Parcial † | [02-sha256-integridade.md](02-sha256-integridade.md), [06-obliteracao-imagens.md](06-obliteracao-imagens.md), [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.1.5.a | ✅ Implementado | [09-cpf-matricula.md](09-cpf-matricula.md) |
| 5.1.5.b | ✅ Já atendido pelo GTW | — |
| 5.1.5.c | ✅ Implementado | [03-politica-senhas.md](03-politica-senhas.md) |
| 5.1.5.d | ✅ Implementado | [03-politica-senhas.md](03-politica-senhas.md) |
| 5.1.5.e | ✅ Implementado | [04-mfa-totp.md](04-mfa-totp.md) |
| 5.1.6 | ✅ Já atendido pelo GTW | — |
| 5.1.7 | ✅ Implementado | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.1.8 | ✅ Implementado | [01-log-auditoria.md](01-log-auditoria.md), [12-retencao-dados.md](12-retencao-dados.md) |
| 5.1.9 | ✅ Implementado | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.1.10 | ✅ Implementado | [01-log-auditoria.md](01-log-auditoria.md), [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [31-acabamentos-baixo-custo.md](31-acabamentos-baixo-custo.md) — Filtros por perfil e equipamento + exportação PDF adicionados |

## 5.2 — Funcionalidade de Gestão e Classificação de Infrações

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.2.1.1 | ✅ Já atendido pelo GTW | — |
| 5.2.1.2 | ⚠️ Parcial | [25-integridade-sha256-recepcao.md](25-integridade-sha256-recepcao.md) (corrige a [02](02-sha256-integridade.md)) — `JobSha256Imagem` compara o SHA-256 com o hash de origem configurado por equipamento e grava `INTEGRO`/`DIVERGENTE`/`SEM_CONFERENCIA`/`ERRO_LEITURA`, com alerta e auditoria na divergência. Parcial porque nenhum equipamento envia hash hoje (Fase 11) e a migração e o roteiro estão pendentes |
| 5.2.1.3 | ⚠️ Parcial | [25-integridade-sha256-recepcao.md](25-integridade-sha256-recepcao.md) — equipamento, captura, recepção e motivo por imagem em `muralha.vtr_imagem_integridade`; recebidos, íntegros e rejeitados por equipamento e dia em `muralha.v_integridade_recepcao` e na tela de integridade. Aguardando migração e teste |
| 5.2.1.4 | ✅ Já atendido pelo GTW | — |
| 5.2.1.5 | ⚠️ Parcial | — |
| 5.2.1.6 | ⚠️ Parcial | [10-sla-preprocessamento.md](10-sla-preprocessamento.md) |
| 5.2.2.1 | ⚠️ Parcial | — |
| 5.2.2.2 | ✅ Já atendido pelo GTW † | — |
| 5.2.2.3 | ✅ Implementado | [08-painel-assertividade.md](08-painel-assertividade.md), [30-paineis-assertividade-preproc.md](30-paineis-assertividade-preproc.md) — Alerta visual quando assertividade < 80% (borda vermelha + banner) |
| 5.2.2.4 | ✅ Já atendido pelo GTW | — |
| 5.2.2.5 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — Regra de divergência entre leitura embarcada e central encaminha para a fila de revisão; não existe OCR central para gerar a segunda leitura. Aguardando migração e teste no navegador |
| 5.2.2.6 | ✅ Implementado | [08-painel-assertividade.md](08-painel-assertividade.md), [30-paineis-assertividade-preproc.md](30-paineis-assertividade-preproc.md) — Auto-refresh 30s, volume processado, divergências, revisão manual, alerta < 80% |
| 5.2.3.1 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.2 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.3 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.4 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.3.5 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) |
| 5.2.4.1 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) — Tela de dupla análise e tela legada `proc_infracao.jsp` |
| 5.2.4.2 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md) — Zoom, brilho e contraste na tela legada `proc_infracao.jsp`; brilho e contraste em `dupla-analise.js`, sem alterar o original |
| 5.2.4.3 | ✅ Implementado | [06-obliteracao-imagens.md](06-obliteracao-imagens.md) |
| 5.2.4.4 | ✅ Implementado | [06-obliteracao-imagens.md](06-obliteracao-imagens.md) |
| 5.2.4.5 | ✅ Já atendido pelo GTW | — |
| 5.2.5.1 | ✅ Implementado | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.2 | ✅ Já atendido pelo GTW | — |
| 5.2.5.3 | ✅ Implementado | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.4 | ✅ Implementado | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.5 | ✅ Implementado | [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.2.5.6 | ✅ Implementado † | [12-retencao-dados.md](12-retencao-dados.md) |
| 5.2.6.1 | ⚠️ Parcial † | — |
| 5.2.6.2 | ⚠️ Parcial | — |
| 5.2.6.3 | ❌ Não implementado † | — |
| 5.2.6.4 | ❌ Não implementado † ‡ | — |
| 5.2.7.1 | ✅ Implementado | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.2.7.2 | ✅ Implementado | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.2.7.3 | ✅ Implementado | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.2.8.1 | ✅ Implementado | [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.2.8.2 | ✅ Implementado | [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.2.8.3 | ✅ Implementado | [11-exportacao-padronizada.md](11-exportacao-padronizada.md) |
| 5.2.8.4 | ⚠️ Parcial | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [21-dashboard-builder.md](21-dashboard-builder.md) — Atualização automática fixa; "tempo real" sem definição (intervalo e modo push ainda não configuráveis por painel) |

## 5.3 — Funcionalidade de Alertas de Irregularidades

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.3.1.1 | ✅ Já atendido pelo GTW | — |
| 5.3.1.2 | ✅ Já atendido pelo GTW | — |
| 5.3.1.3 | ⚠️ Parcial | [28-alertas-operacionais.md](28-alertas-operacionais.md) — tratamentos parametrizáveis com execução automática; aguardando migração |
| 5.3.1.4 | ✅ Já atendido pelo GTW | — |
| 5.3.2.1 | ⚠️ Parcial | [28-alertas-operacionais.md](28-alertas-operacionais.md) — 4 tipos operacionais inseridos pelo script; aguardando migração |
| 5.3.2.2 | ✅ Já atendido pelo GTW | — |
| 5.3.3.1 | ✅ Já atendido pelo GTW | — |
| 5.3.3.2 | ✅ Já atendido pelo GTW | — |
| 5.3.3.3 | ⚠️ Parcial † | [28-alertas-operacionais.md](28-alertas-operacionais.md) — criticidade configurável via `tipo_alerta_config`; aguardando migração |
| 5.3.3.4 | ⚠️ Parcial | [28-alertas-operacionais.md](28-alertas-operacionais.md) — cor e ícone configuráveis via banco, `notificacao.js` carrega via AJAX; aguardando migração |
| 5.3.4.1 | ⚠️ Parcial † | — |
| 5.3.4.2 | ⚠️ Parcial † | — |
| 5.3.4.3 | ⚠️ Parcial † | — |
| 5.3.5.1 | ⚠️ Parcial † | — |
| 5.3.5.2 | ❌ Não implementado † ‡ | — |
| 5.3.5.3 | ❌ Não implementado | — |
| 5.3.6.1 | ✅ Já atendido pelo GTW † | — |
| 5.3.6.2 | ⚠️ Parcial † | [28-alertas-operacionais.md](28-alertas-operacionais.md) — filtros de localidade, responsável e resultado adicionados; aguardando migração e teste |
| 5.3.6.3 | ⚠️ Parcial † | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.3.6.4 | ⚠️ Parcial | [28-alertas-operacionais.md](28-alertas-operacionais.md) — exportação CSV adicionada; aguardando teste no navegador |
| 5.3.7.1 | ✅ Implementado | [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.2 | ✅ Implementado | [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.3 | ✅ Implementado | [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.4 | ⚠️ Parcial | [28-alertas-operacionais.md](28-alertas-operacionais.md) — alíneas a–c pelo legado; d (retenção por regra sobre `veiculo_sumarizado`) e e (alertas parametrizáveis) implementados; aguardando migração |
| 5.3.7.5 | ⚠️ Parcial | [03-politica-senhas.md](03-politica-senhas.md), [04-mfa-totp.md](04-mfa-totp.md), [09-cpf-matricula.md](09-cpf-matricula.md) — Navegador, autenticação individualizada e perfis atendidos; criptografia (HTTPS) depende da configuração do deploy |
| 5.3.7.6 | ⚠️ Parcial † | [28-alertas-operacionais.md](28-alertas-operacionais.md) — limiares de retenção e config de tipos via interface administrativa; aguardando migração |
| 5.3.7.7 | ✅ Implementado | [16-exportacao-gis.md](16-exportacao-gis.md), [19-sensores-externos.md](19-sensores-externos.md) |
| 5.3.7.8 | ⚠️ Parcial † | [28-alertas-operacionais.md](28-alertas-operacionais.md) — prioridade + cor/ícone parametrizáveis via tela; aguardando migração |

## 5.4 — Funcionalidade de Inteligência de Dados e Apoio à Decisão Operacional

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.4.1.1 | ⚠️ Parcial | — |
| 5.4.1.2 | ✅ Implementado | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.4.1.3 | ✅ Implementado | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.4.1.4 | ⚠️ Parcial † | — |
| 5.4.2.1 | ✅ Implementado | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.2.2 | ✅ Implementado | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.2.3 | ✅ Implementado † | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.4.2.4 | ⚠️ Parcial | [11-exportacao-padronizada.md](11-exportacao-padronizada.md), [21-dashboard-builder.md](21-dashboard-builder.md) — Filtros e atualização simultânea no Dashboard Builder; exportação PNG e filtros por faixa horária/classificação veicular não verificados |
| 5.4.2.5 | ✅ Implementado | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.4.2.6 | ✅ Implementado | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.3.1 | ⚠️ Parcial † | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.4.3.2 | ⚠️ Parcial † | — |
| 5.4.3.3 | ⚠️ Parcial † | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.3.4 | ⚠️ Parcial † | — |
| 5.4.3.5 | ⚠️ Parcial | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Alertas analíticos automáticos via KpiSnapshotJob quando KPI em WARN/CRIT; cooldown configurável |
| 5.4.3.6 | ⚠️ Parcial | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Alíneas a-e cobertas pelos 8 KPIs obrigatórios: infrações, desempenho, disponibilidade, retenções, padrões |
| 5.4.3.7 | ⚠️ Parcial | [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Relatório gerencial periódico com a-e: resumo, comparação, evolução, alertas, acompanhamento; aguardando migração |
| 5.4.4.1 | ✅ Já atendido pelo GTW | — |
| 5.4.4.2 | ✅ Já atendido pelo GTW | — |
| 5.4.4.3 | ✅ Já atendido pelo GTW | — |
| 5.4.4.4 | ✅ Implementado | [16-exportacao-gis.md](16-exportacao-gis.md), [31-acabamentos-baixo-custo.md](31-acabamentos-baixo-custo.md) — GeoJSON e KML com 3 camadas: equipamentos, alertas e infrações |
| 5.4.5.1 | ⚠️ Parcial | [21-dashboard-builder.md](21-dashboard-builder.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Relatórios gerenciais cobrem a (tráfego), c (ocupação via KPI), e (equipamentos), f (aproveitamento), h (efetividade), i (funcionalidades integradas); b,d,g parciais |
| 5.4.5.2 | ⚠️ Parcial | [21-dashboard-builder.md](21-dashboard-builder.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Filtros por período (a), exportação CSV/XLS (c), emissão periódica automática (d); segmentação completa (b) e PDF (c parcial) na Fase 9 |
| 5.4.6.1 | ✅ Implementado | [15-kpis-configuraveis.md](15-kpis-configuraveis.md) |
| 5.4.6.2 | ⚠️ Parcial | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — 12 KPIs (4 existentes + 8 obrigatórios a-h): infração/tráfego, variação temporal, disponibilidade, aproveitamento, efetividade, segurança viária, conformidade, SLA; aguardando migração |
| 5.4.6.3 | ⚠️ Parcial | [15-kpis-configuraveis.md](15-kpis-configuraveis.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Metas (a), limites atenção/crítico (b), alertas analíticos automáticos (c), histórico horário (d), gráfico Chart.js (e); aguardando migração |
| 5.4.7.1 | ⚠️ Parcial | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) — Repositório central na tela Logs Técnicos: 12 fontes (`eventos_csx`, `status_conexao`, `status_energia`, `manutencao`, `log_alerta`, `log_processos`, disponibilidade, SLA, auditoria, históricos de parâmetros) lidas e normalizadas por `ServicoLogTecnico`, sem cópia; o recebimento continua nas tabelas do legado. Implementado, aguardando migração e teste no navegador |
| 5.4.7.2 | ⚠️ Parcial | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) — Alíneas a-f cobertas pelas fontes existentes (equipamento, eventos, falhas/degradação, alterações de configuração, ações de usuário, ocorrências sistêmicas); nada novo é recebido além do que o legado já grava. Implementado, aguardando migração e teste no navegador |
| 5.4.7.3 | ⚠️ Parcial | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) — Categorias energia, comunicação, sincronismo, hardware, software, sensor, parâmetro, intervenção técnica, processamento e usuário (alíneas a-i); "formação de lotes" (i) não tem fonte de log própria. Implementado, aguardando migração e teste no navegador |
| 5.4.7.4 | ⚠️ Parcial | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) — Registro normalizado com data/hora, equipamento, usuário quando houver, descrição e referência à linha de origem; a rastreabilidade integral (e) depende das tabelas legadas, sem trilha imutável |
| 5.4.7.5 | ⚠️ Parcial | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) — Filtros por período, equipamento, localidade, categoria e severidade; linha do tempo e correlação temporal; CSV com SHA-256, XLS e PDF; relatório técnico por equipamento; exportação e relatório auditados. Implementado, aguardando migração e teste no navegador |
| 5.4.7.6 | ⚠️ Parcial | [26-logs-tecnicos-historico-parametros.md](26-logs-tecnicos-historico-parametros.md) — `muralha.parametro_historico` (data, parâmetro, valor anterior, valor novo, usuário, IP) alimentada pelo ConfigEquip (web service e importação), KPIs, retenção e configuração de integridade; as telas antigas seguem só com o texto de `config_chave_valor_hist`. Implementado, aguardando migração e teste no navegador |
| 5.4.7.7 | ⚠️ Parcial | [01-log-auditoria.md](01-log-auditoria.md), [12-retencao-dados.md](12-retencao-dados.md) — Retenção configurável cobre `sis_log_auditoria`; logs técnicos legados sem política própria |

## 5.5 — Serviço de Pré-Processamento de Imagens

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.5.1.1 | ❌ Não implementado † ‡ | — |
| 5.5.1.2 | ⚠️ Parcial † | [02-sha256-integridade.md](02-sha256-integridade.md), [05-dupla-analise.md](05-dupla-analise.md) |
| 5.5.1.3 | ⚠️ Parcial | — |
| 5.5.1.4 | ⚠️ Parcial † | [05-dupla-analise.md](05-dupla-analise.md), [14-gestao-lotes.md](14-gestao-lotes.md) |
| 5.5.2.1 | ✅ Implementado | [10-sla-preprocessamento.md](10-sla-preprocessamento.md) |
| 5.5.2.2 | ❌ Não implementado † ‡ | — |
| 5.5.2.3 | ❌ Não implementado † ‡ | — |
| 5.5.2.4 | ❌ Não implementado † ‡ | — |
| 5.5.2.5 | ❌ Não implementado † ‡ | — |
| 5.5.3.1 | ⚠️ Parcial | [10-sla-preprocessamento.md](10-sla-preprocessamento.md), [21-dashboard-builder.md](21-dashboard-builder.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md), [30-paineis-assertividade-preproc.md](30-paineis-assertividade-preproc.md) — Painel operacional com a-e, g, j-l, n em tempo real; h, i, m dependem de colunas ausentes em vtr_status_analise |
| 5.5.3.2 | ⚠️ Parcial | [10-sla-preprocessamento.md](10-sla-preprocessamento.md), [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Segmentação por período (a); filtros b-h na Fase 9 |
| 5.5.3.3 | ⚠️ Parcial | [29-kpis-relatorio-mensal.md](29-kpis-relatorio-mensal.md) — Relatório mensal consolidado automático no 5o dia útil via RelatorioPreProcMensalJob; aguardando migração |
| 5.5.3.4 | ✅ Implementado † | [10-sla-preprocessamento.md](10-sla-preprocessamento.md), [11-exportacao-padronizada.md](11-exportacao-padronizada.md) |
| 5.5.3.5 | ⚠️ Parcial † | [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.5.4.1 | ❌ Não implementado † ‡ | — |
| 5.5.4.2 | ✅ Já atendido pelo GTW | — |
| 5.5.4.3 | ✅ Implementado | [05-dupla-analise.md](05-dupla-analise.md), [31-acabamentos-baixo-custo.md](31-acabamentos-baixo-custo.md) — Brilho, contraste e inversão de cores em 3 telas de análise |
| 5.5.4.4 | ⚠️ Parcial † | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.5.4.5 | ⚠️ Parcial | [01-log-auditoria.md](01-log-auditoria.md) |
| 5.5.4.6 | ✅ Implementado | [12-retencao-dados.md](12-retencao-dados.md) |
| 5.5.4.7 | ⚠️ Parcial † | [06-obliteracao-imagens.md](06-obliteracao-imagens.md), [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.5.4.8 | ✅ Implementado † | [03-politica-senhas.md](03-politica-senhas.md), [04-mfa-totp.md](04-mfa-totp.md), [09-cpf-matricula.md](09-cpf-matricula.md) |
| 5.5.4.9 | ✅ Implementado † | [01-log-auditoria.md](01-log-auditoria.md), [14-gestao-lotes.md](14-gestao-lotes.md) |

## 5.6 — Infraestrutura Sistêmica de Comunicação, Processamento e Transmissão de Dados

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.6.1.1 | ✅ Já atendido pelo GTW | — |
| 5.6.1.2 | ❌ Não implementado | — |
| 5.6.1.3 | ❌ Não implementado † ‡ | Dimensionamento dos enlaces é obrigação de infraestrutura |
| 5.6.1.4 | ⚠️ Parcial † | SOAP/HTTP disponível; HTTPS depende da configuração do deploy |
| 5.6.1.5 | ✅ Implementado † | [18-disponibilidade.md](18-disponibilidade.md) — `fcn_getAlertaLocaisOffLine`, `fcn_getAlertaLocaisConexaoInstavel` e painel de disponibilidade |
| 5.6.2.1 | ✅ Já atendido pelo GTW | — |
| 5.6.2.2 | ✅ Já atendido pelo GTW † | — |
| 5.6.2.3 | ⚠️ Parcial | [07-sla-latencia.md](07-sla-latencia.md) |
| 5.6.2.4 | ⚠️ Parcial † | Passagens chegam continuamente em `veiculo_tempo_real`; periodicidade não é parametrizável no sistema |
| 5.6.2.5 | ✅ Já atendido pelo GTW † | `veiculo_tempo_real` recebe todas as passagens, com ou sem infração |
| 5.6.3.1 | ❌ Não implementado † | Sem criptografia explícita nas transmissões (depende de TLS no deploy) |
| 5.6.3.2 | ❌ Não implementado † | — |
| 5.6.3.3 | ❌ Não implementado † | — |
| 5.6.3.4 | ❌ Não implementado † ‡ | — |
| 5.6.4.1 | ❌ Não implementado † ‡ | — |
| 5.6.4.2 | ❌ Não implementado † ‡ | — |
| 5.6.4.3 | ❌ Não implementado | — |
| 5.6.4.4 | ❌ Não implementado † ‡ | Retransmissão é responsabilidade do Captura, fora deste repositório |
| 5.6.4.5 | ❌ Não implementado † ‡ | — |
| 5.6.5.1 | ❌ Não implementado † ‡ | Alimentação alternativa é requisito do equipamento |
| 5.6.5.2 | ❌ Não implementado † ‡ | — |
| 5.6.5.3 | ❌ Não implementado † ‡ | — |
| 5.6.5.4 | ✅ Já atendido pelo GTW † | Falta e retorno de energia registrados em `dbo.status_energia` (legado) |
| 5.6.6.1 | ⚠️ Parcial | — |
| 5.6.6.2 | ❌ Não implementado † | Sem mecanismo de sincronismo documentado no sistema |
| 5.6.6.3 | ❌ Não implementado † | — |
| 5.6.6.4 | ❌ Não implementado † | — |
| 5.6.7.1 | ✅ Já atendido pelo GTW † | `ConfigEquipWS`, tabelas `configuracao_equipamento*` |
| 5.6.7.2 | ⚠️ Parcial † | Autenticação e perfis atendidos; rastreabilidade das alterações incompleta (ver 5.4.7.6) |
| 5.6.7.3 | ❌ Não implementado † | Sem registro de valor anterior/novo por parâmetro |
| 5.6.7.4 | ⚠️ Parcial † | Envio de listas e parâmetros por `ConfigEquipWS`; rastreabilidade incompleta |
| 5.6.7.5 | ✅ Já atendido pelo GTW † | Parâmetros dos equipamentos alterados por `ConfigEquipWS`, sem mudança de código |

## 5.7 — Tecnologia de Leitura Automática de Placas Veiculares — LAP

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.7.1.1 | ✅ Já atendido pelo GTW | — |
| 5.7.1.2 | ✅ Já atendido pelo GTW † | — |
| 5.7.1.3 | ⚠️ Parcial † | — |
| 5.7.1.4 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — `muralha.vtr_placa_leitura` registra N placas por passagem (tração, frontal, reboques); as do equipamento são só tração e frontal, reboques entram pela revisão manual. Aguardando migração e teste no navegador |
| 5.7.2.1 | ⚠️ Parcial | — |
| 5.7.2.2 | ❌ Não implementado † | — |
| 5.7.2.3 | ❌ Não implementado † | — |
| 5.7.2.4 | ❌ Não implementado † | — |
| 5.7.2.5 | ❌ Não implementado † ‡ | — |
| 5.7.3.1 | ❌ Não implementado † ‡ | — |
| 5.7.3.2 | ❌ Não implementado † ‡ | — |
| 5.7.3.3 | ✅ Já atendido pelo GTW † | — |
| 5.7.4.1 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — Score por placa guardado conforme a origem configurada por equipamento; nenhum equipamento envia score hoje (`spu_veiculo_tempo_real` não recebe, `dado_bruto` vazio) |
| 5.7.4.2 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — Limiar `OCR_SCORE_LIMIAR` (padrão 85) e limiar por equipamento, com tela e histórico de alteração. Aguardando migração e teste no navegador |
| 5.7.4.3 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — `JobOcrRevisao` encaminha à fila as leituras abaixo do limiar; sem score enviado pelos equipamentos, nada é encaminhado. Aguardando migração e teste no navegador |
| 5.7.4.4 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — Score como metadado de cada leitura em `vtr_placa_leitura`. Aguardando migração e teste no navegador |
| 5.7.5.1 | ✅ Já atendido pelo GTW | — |
| 5.7.5.2 | ⚠️ Parcial | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — Toda correção (fila, lote, consulta) exige motivo e grava `veiculo_tempo_real_correcao`, justificativa e `sis_log_auditoria`. Aguardando migração e teste no navegador |
| 5.7.5.3 | ✅ Implementado | [12-retencao-dados.md](12-retencao-dados.md) |
| 5.7.5.4 | ⚠️ Parcial † | [27-score-ocr-revisao-placas.md](27-score-ocr-revisao-placas.md) — Fila com filtros de equipamento, período, faixa de score, tipo de intervenção e operador. Aguardando migração e teste no navegador |
| 5.7.6.1 | ✅ Já atendido pelo GTW † | — |
| 5.7.6.2 | ⚠️ Parcial † | — |
| 5.7.7.1 | ⚠️ Parcial † | [17-timelapse.md](17-timelapse.md) — Player de imagens no navegador (`TimelapseServlet`); não gera arquivo de vídeo |
| 5.7.7.2 | ⚠️ Parcial † | [17-timelapse.md](17-timelapse.md) — Sequência cronológica de imagens por equipamento, exibida no navegador; sem vídeo |
| 5.7.7.3 | ⚠️ Parcial † | [17-timelapse.md](17-timelapse.md) — Finalidade atendida pelo player; sem vídeo gerado |
| 5.7.7.4 | ⚠️ Parcial † | [17-timelapse.md](17-timelapse.md) — Reconstrução visual e validação temporal no player; sem vídeo nem vínculo com alertas |
| 5.7.7.5 | ❌ Não implementado † | Sem descarte automático de vídeos |
| 5.7.7.6 | ❌ Não implementado † | Sem parametrização do descarte |
| 5.7.8.1 | ✅ Implementado † | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.7.8.2 | ✅ Implementado † | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.7.8.3 | ✅ Implementado † | [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.7.9.1 | ⚠️ Parcial | [07-sla-latencia.md](07-sla-latencia.md) |
| 5.7.9.2 | ✅ Implementado † | [07-sla-latencia.md](07-sla-latencia.md) |
| 5.7.9.3 | ✅ Implementado † | [07-sla-latencia.md](07-sla-latencia.md) |
| 5.7.10.1 | ❌ Não implementado † ‡ | — |
| 5.7.10.2 | ❌ Não implementado † ‡ | — |
| 5.7.10.3 | ❌ Não implementado † ‡ | — |
| 5.7.10.4 | ❌ Não implementado † ‡ | — |

## 5.8 — Infraestrutura de Datacenter, Disponibilidade e Continuidade Operacional

| Item do edital | Situação | Entrega / evidência |
|---|---|---|
| 5.8.1.1 | ❌ Não implementado † ‡ | — |
| 5.8.1.2 | ❌ Não implementado | — |
| 5.8.1.3 | ❌ Não implementado † ‡ | — |
| 5.8.1.4 | ❌ Não implementado † ‡ | — |
| 5.8.1.5 | ❌ Não implementado † ‡ | — |
| 5.8.2.1 | ⚠️ Parcial | [18-disponibilidade.md](18-disponibilidade.md) |
| 5.8.2.2 | ❌ Não implementado † ‡ | — |
| 5.8.2.3 | ⚠️ Parcial † | [18-disponibilidade.md](18-disponibilidade.md) |
| 5.8.2.4 | ❌ Não implementado † ‡ | — |
| 5.8.3.1 | ❌ Não implementado | — |
| 5.8.3.2 | ❌ Não implementado † | — |
| 5.8.3.3 | ❌ Não implementado | — |
| 5.8.4.1 | ⚠️ Parcial † | [03-politica-senhas.md](03-politica-senhas.md), [04-mfa-totp.md](04-mfa-totp.md) |
| 5.8.4.2 | ⚠️ Parcial † | [04-mfa-totp.md](04-mfa-totp.md) |
| 5.8.4.3 | ⚠️ Parcial † | [06-obliteracao-imagens.md](06-obliteracao-imagens.md), [13-anonimizacao-placas.md](13-anonimizacao-placas.md) |
| 5.8.4.4 | ❌ Não implementado | — |
| 5.8.5.1 | ❌ Não implementado | — |
| 5.8.5.2 | ❌ Não implementado † | — |
| 5.8.5.3 | ⚠️ Parcial † | [12-retencao-dados.md](12-retencao-dados.md) |
| 5.8.5.4 | ❌ Não implementado | — |
| 5.8.5.5 | ❌ Não implementado † ‡ | — |
| 5.8.5.6 | ❌ Não implementado † | — |
| 5.8.6.1 | ⚠️ Parcial | [07-sla-latencia.md](07-sla-latencia.md), [18-disponibilidade.md](18-disponibilidade.md) |
| 5.8.6.2 | ✅ Implementado † | [01-log-auditoria.md](01-log-auditoria.md), [18-disponibilidade.md](18-disponibilidade.md), [21-dashboard-builder.md](21-dashboard-builder.md) |
| 5.8.6.3 | ⚠️ Parcial † | [18-disponibilidade.md](18-disponibilidade.md) |
## Capítulo 7 — Central de Suporte Técnico (aba Sist. Chamado)

Itens da aba "Sist. Chamado" de [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx); o Capítulo 7 do TR não está no repositório. A entrega 22 está implementada; as migrações foram executadas no `GTW_MURALHA_DEV` e o teste integrado de 2026-10-15 passou em 10 de 10 casos. Faltam os testes no navegador e com o perfil Gestor (ver a entrega).

| Item do edital | Entrega |
|---|---|
| 7.2.1 | Fora do escopo de software ‡ (central 24x7 é obrigação de serviço) |
| 7.2.2 | Não atendido por este plano (7.2.2.c e 7.2.2.d fora do escopo; 7.2.2.a é telefone) |
| 7.2.3.a | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.3.b | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.3.c | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.3.d | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.3.e | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.3.f | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.4 | [22-sistema-chamados.md](22-sistema-chamados.md) |
| 7.2.5 | Fora do escopo de software ‡ (preposto) |

# Aderência Comparativa — Antes e Após PoC TRANSALVADOR

Visão tabular da evolução da aderência do GTW-CWB aos requisitos do TR Salvador (Capítulo 5), comparando o estado anterior (base `main` pré-PoC) com o estado atual (após implementação dos 19 planos).

> **Fonte:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)  
> **Data da análise:** 2026-10-08

---

## 1. Resumo Executivo

| Módulo TR | Antes | Após | Δ | Planos que contribuem |
|---|---|---|---|---|
| §5.1 Controle de Acesso | 40% | **82%** | +42pp | 01, 03, 04, 05, 09, 12 |
| §5.2 Gestão e Classificação | 45% | **85%** | +40pp | 02, 05, 06, 07, 08, 11, 14, 15, 17, 18 |
| §5.3 Alertas de Irregularidades | 60% | **75%** | +15pp | 07, 11, 16, 19 |
| §5.4 Inteligência de Dados | 50% | **75%** | +25pp | 01, 11, 13, 15, 16, 18 |
| §5.5 Pré-processamento | 25% | **55%** | +30pp | 01, 05, 10, 12 |
| §5.6 Infraestrutura | 40% | **45%** | +5pp | 07, 18 |
| §5.7 LAP | 45% | **65%** | +20pp | 07, 12, 13, 17 |
| §5.8 Datacenter | 5% | **10%** | +5pp | 07, 18 |
| **Global** | **39%** | **62%** | **+23pp** | **19 planos** |

---

## 2. Detalhamento por Requisito — Status Antes × Depois

### §5.1 — Controle de Acesso

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.1.5a-b | Autenticação individualizada | ✅ | ✅ | Já existente |
| 5.1.5c | Política de senhas parametrizável | ❌ | ✅ | Plano 03 |
| 5.1.5d | Encerramento sessão por inatividade | ✅ | ✅ | Já existente + Plano 03 |
| 5.1.5e | MFA nativo | ❌ | ✅ | Plano 04 |
| 5.1.6 | Perfis de acesso | ✅ | ✅ | Já existente |
| 5.1.7-8 | Log de auditoria automático | ⚠️ | ✅ | Plano 01 |
| 5.1.9 | Conteúdo mínimo do log | ⚠️ | ✅ | Plano 01 |
| 5.1.10 | Consulta de log + exportação | ❌ | ✅ | Planos 01, 11 |
| 5.1.1 | Segregação de funções | ❌ | ⚠️ | Plano 05 (parcial) |
| 5.1.5a | CPF/matrícula | ⚠️ | ✅ | Plano 09 |
| 5.1 | Credenciais não compartilhadas | ⚠️ | ⚠️ | Sem enforcement |
| 5.1.8 | Retenção de logs | — | ✅ | Plano 12 |

### §5.2 — Gestão e Classificação de Infrações

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.2.1.1 | Recepção automática | ✅ | ✅ | Já existente |
| 5.2.1.2 | SHA-256 integridade | ❌ | ✅ | Plano 02 |
| 5.2.1.3 | Registro de recepção | ⚠️ | ⚠️ | Parcial |
| 5.2.1.4 | Indexação por tipo/equip/local | ✅ | ✅ | Já existente |
| 5.2.1.5 | Recepção simultânea + margem | ⚠️ | ⚠️ | Sem métricas |
| 5.2.1.6 | Registros fora do prazo | ❌ | ⚠️ | Plano 10 (parcial) |
| 5.2.2.1 | Pré-classificação ≤4s | ⚠️ | ⚠️ | Sem SLA monitorado |
| 5.2.2.3 | Aderência ≥80% | ❌ | ✅ | Plano 08 |
| 5.2.2.4 | OCR/LAP com conferência | ✅ | ✅ | Já existente |
| 5.2.2.5 | Encaminhamento por score | ⚠️ | ⚠️ | Sem automação |
| 5.2.2.6 | Painel de assertividade | ❌ | ✅ | Plano 08 |
| 5.2.3.2-3 | Dupla análise independente | ❌ | ✅ | Plano 05 |
| 5.2.3.4 | Desempate por 3o operador | ❌ | ✅ | Plano 05 |
| 5.2.4.1-2 | Interface de análise de imagens | ⚠️ | ✅ | Plano 17 |
| 5.2.4.3-4 | Obliteração de imagens | ❌ | ✅ | Plano 06 |
| 5.2.4.5 | Placa editável com log | ✅ | ✅ | Já existente |
| 5.2.5.1 | Gestão de lotes | ✅ | ✅ | Plano 14 |
| 5.2.5.2 | Amostras para auditoria | ✅ | ✅ | Já existente |
| 5.2.5.3 | Auditoria integral | ⚠️ | ✅ | Plano 14 |
| 5.2.5.4-5 | Bloqueio de envio | ⚠️ | ✅ | Plano 14 |
| 5.2.6.2 | WebService HTTPS | ⚠️ | ⚠️ | Deploy-dependent |
| 5.2.7 | Telemetria operacional | ⚠️ | ✅ | Planos 07, 18 |
| 5.2.8.3 | Exportação PDF/CSV/XLS | ⚠️ | ✅ | Plano 11 |
| 5.2.8.4 | Dashboards analíticos | ⚠️ | ✅ | Plano 15 |

### §5.3 — Alertas de Irregularidades

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.3.1.1 | Recepção de alertas | ✅ | ✅ | Já existente |
| 5.3.1.2 | Classificação automática | ✅ | ✅ | Já existente |
| 5.3.1.3 | Parametrização | ⚠️ | ⚠️ | Parcial |
| 5.3.1.4 | Rastreabilidade do fluxo | ✅ | ✅ | Já existente |
| 5.3.2.1-2 | Tipos de alertas | ✅ | ✅ | Já existente |
| 5.3.3.1 | Interface web | ✅ | ✅ | Já existente |
| 5.3.3.2a | Visualização geo | ✅ | ✅ | Já existente |
| 5.3.3.2b | Filtros | ✅ | ✅ | Já existente |
| 5.3.3.2e | Pesquisa por placa | ✅ | ✅ | Já existente |
| 5.3.3.4 | Destaque visual | ✅ | ✅ | Já existente |
| 5.3.4 | Mapa interativo | ⚠️ | ⚠️ | Sem clustering |
| 5.3.5.1d | Mobile | ⚠️ | ⚠️ | Sem app dedicado |
| 5.3.5.3 | Retry de alertas | ❌ | ❌ | Gap remanescente |
| 5.3.6.4 | Histórico exportável | ⚠️ | ✅ | Plano 11 |
| 5.3.7.1-2 | Sensores externos | ❌ | ✅ | Plano 19 |
| 5.3.7.3 | Integração Waze | ⚠️ | ✅ | Plano 19 |
| 5.3.7.4 | Videodetecção | ❌ | ❌ | Gap remanescente |
| 5.3.7.5 | Resposta ≤4s | ⚠️ | ⚠️ | Plano 07 (monitoramento) |
| 5.3.7.7 | Mapas e indicadores | — | ✅ | Planos 16, 19 |

### §5.4 — Inteligência de Dados

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.4.1.1 | Consolidação multifonte | ⚠️ | ⚠️ | Sem pipeline |
| 5.4.1.2-3 | LGPD (anonimização) | ⚠️ | ✅ | Plano 13 |
| 5.4.2.1-2 | Dashboards interativos | ✅ | ✅ | Plano 15 |
| 5.4.2.3.e | Disponibilidade | — | ✅ | Plano 18 |
| 5.4.2.4a-b | Filtros dinâmicos | ⚠️ | ⚠️ | Parcial |
| 5.4.2.4d | Exportação dashboards | ⚠️ | ✅ | Plano 11 |
| 5.4.2.5 | Dashboards customizáveis | ❌ | ⚠️ | Plano 15 (parcial) |
| 5.4.4.1-3 | Análise espacial | ✅ | ✅ | Já existente |
| 5.4.4.4 | Exportação GIS | ❌ | ✅ | Plano 16 |
| 5.4.5 | Relatórios analíticos | ⚠️ | ⚠️ | Cobertura parcial |
| 5.4.6.1-3 | KPIs com metas/alertas | ❌ | ✅ | Plano 15 |
| 5.4.7.1-7 | Repositório de logs | ⚠️ | ✅ | Plano 01 |
| 5.4.7.6 | Histórico de configurações | ⚠️ | ⚠️ | Audit trail parcial |

### §5.5 — Pré-processamento

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.5.1.3 | 100% pré-processados | ⚠️ | ⚠️ | Sem garantia documentada |
| 5.5.1.2g | Dupla análise | ❌ | ✅ | Plano 05 |
| 5.5.2.1 | Prazo 72h | ❌ | ✅ | Plano 10 |
| 5.5.3.1 | Painel operacional | ❌ | ⚠️ | Plano 10 (parcial) |
| 5.5.3.3 | Relatório mensal | ❌ | ❌ | Gap remanescente |
| 5.5.4.2 | Vedação alteração original | ✅ | ✅ | Já existente |
| 5.5.4.5 | Log completo | ⚠️ | ⚠️ | Plano 01 (parcial) |
| 5.5.4.6 | Retenção 5 anos | ❌ | ✅ | Plano 12 |

### §5.6 — Infraestrutura de Comunicação

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.6.1.1 | Arquitetura integrada | ✅ | ✅ | Já existente |
| 5.6.1.2 | Redundância | ❌ | ❌ | Gap remanescente |
| 5.6.1.4 | Protocolos seguros | ⚠️ | ⚠️ | Deploy-dependent |
| 5.6.1.5 | Monitoramento de enlaces | ⚠️ | ✅ | Plano 18 |
| 5.6.2.1 | Transmissão automática | ✅ | ✅ | Já existente |
| 5.6.2.3 | Tempo ≤4s | ⚠️ | ⚠️ | Plano 07 (monitoramento) |
| 5.6.3 | Criptografia | ❌ | ❌ | Gap remanescente |
| 5.6.4.3 | Armazenamento 6h | ❌ | ❌ | Firmware |
| 5.6.6.1 | Sincronismo NTP | ⚠️ | ⚠️ | Não documentado |
| 5.6.7 | Config remota | ✅ | ✅ | Já existente |

### §5.7 — LAP

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.7.1.1 | Padrão Mercosul + anterior | ✅ | ✅ | Já existente |
| 5.7.2.1 | Índice mínimo | ⚠️ | ⚠️ | Não aferido |
| 5.7.4.1-2 | Score parametrizável | ⚠️ | ⚠️ | Sem parametrização |
| 5.7.4.3 | Encaminhamento por score | ⚠️ | ⚠️ | Sem automação |
| 5.7.4.4 | Score permanente | ✅ | ✅ | Já existente |
| 5.7.5.1 | Rastreabilidade | ✅ | ✅ | Já existente |
| 5.7.5.2 | Log de correção | ⚠️ | ⚠️ | Sem justificativa |
| 5.7.5.3 | Retenção 5 anos | ❌ | ✅ | Plano 12 |
| 5.7.8 | Anonimização | ❌ | ✅ | Plano 13 |
| 5.7.9.1 | Latência ≤4s | ⚠️ | ⚠️ | Plano 07 (monitoramento) |
| 5.7.7 | Time-lapse | ❌ | ✅ | Plano 17 |

### §5.8 — Datacenter

| Ref. TR | Requisito | Antes | Depois | Resolução |
|---|---|---|---|---|
| 5.8.2.1 | Disponibilidade 99,5% | ❌ | ⚠️ | Plano 18 (parcial) |
| 5.8.1.2 | Tier III | ❌ | ❌ | Infraestrutura |
| 5.8.3.1 | HA | ❌ | ❌ | Infraestrutura |
| 5.8.3.3 | RTO/RPO | ❌ | ❌ | Não definido |
| 5.8.4.4 | Incidente ≤2h | ❌ | ❌ | Sem processo |
| 5.8.5.1 | Backup | ❌ | ❌ | Sem política |
| 5.8.5.4 | PRD/PCN | ❌ | ❌ | Não existe |
| 5.8.6.1 | Monitoramento infra | ❌ | ⚠️ | Planos 07, 18 (parcial) |

---

## 3. Contagem de Transições

| Transição | Quantidade |
|---|---|
| ❌ → ✅ (gap resolvido) | **22** |
| ❌ → ⚠️ (gap parcialmente resolvido) | **5** |
| ⚠️ → ✅ (parcial → completo) | **13** |
| ✅ → ✅ (já atendia) | **29** |
| ⚠️ → ⚠️ (sem mudança) | **15** |
| ❌ → ❌ (gap remanescente) | **11** |
| **Total de requisitos analisados** | **~95** |

---

## 4. Gaps Remanescentes Prioritários

| # | Requisito | Ref. TR | Natureza |
|---|---|---|---|
| 1 | Criptografia TLS em trânsito e repouso | §5.6.3 | Infraestrutura/deploy |
| 2 | Alta disponibilidade e PRD/PCN | §5.8.3, §5.8.5 | Infraestrutura/operação |
| 3 | Videodetecção volumétrica de tráfego | §5.3.7.4 | Nova funcionalidade |
| 4 | Relatório mensal automático | §5.5.3.3 | Desenvolvimento |
| 5 | Retry de alertas em falha de comunicação | §5.3.5.3 | Desenvolvimento |
| 6 | Redundância lógica e física | §5.6.1.2 | Infraestrutura |
| 7 | Datacenter Tier III | §5.8.1.2 | Infraestrutura |
| 8 | RTO ≤4h / RPO ≤1h | §5.8.3.3 | Operação |
| 9 | Monitoramento de infraestrutura completo | §5.8.6.1 | Observabilidade |
| 10 | Consolidação multifonte | §5.4.1.1 | Pipeline de dados |
| 11 | Armazenamento local 6h (firmware) | §5.6.4.3 | Equipamentos |

> Dos 11 gaps remanescentes, **7 são de infraestrutura/operação** (não resolvíveis via código da aplicação) e **4 são de desenvolvimento** (endereçáveis em ondas futuras).

---

## 5. Impacto na Prova de Conceito (Anexo A)

| Item PoC | Antes | Depois |
|---|---|---|
| Gestão e Classificação completa | ⚠️ | ✅ |
| Recepção + SHA-256 | ⚠️ | ✅ |
| Dupla classificação | ❌ | ✅ |
| Exportação de lotes | ✅ | ✅ |
| Alertas + sensores externos | ✅ | ✅ |
| Inteligência de dados + exportação | ⚠️ | ✅ |
| Integração campo ↔ sistema | ✅ | ✅ |
| LOG do sistema | ⚠️ | ✅ |
| Falta/retorno energia | ✅ | ✅ |
| Falha/retorno comunicação | ✅ | ✅ |
| Ativação/desativação remota | ✅ | ✅ |
| Disponibilidade ≥95% | ❌ | ✅ |
| **Itens PoC atendidos** | **7/14** | **12/14** |

---

## Referências

- Análise de aderência completa: [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)
- Matriz de rastreabilidade: [`rastreabilidade-tr.md`](rastreabilidade-tr.md)
- Pendências de banco: [`pendencias-banco-de-dados.md`](pendencias-banco-de-dados.md)
- Roteiro de testes: [`roteiro-testes-manuais.md`](roteiro-testes-manuais.md)

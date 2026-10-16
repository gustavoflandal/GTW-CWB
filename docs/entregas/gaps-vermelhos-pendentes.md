# Gaps Vermelhos Pendentes — Itens de Desenvolvimento

**Data:** 2026-10-09 · **Revisão:** 2026-10-16 (citações do TR conferidas com o texto literal de [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md) — [Plano 23](../planos-salvador/23-correcao-documentacao-aderencia.md))  
**Status:** 🔴 Pendente de definição  
**Fonte de status:** matriz canônica [`itens-edital-x-entregas.md`](itens-edital-x-entregas.md) · [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)

---

## 1. §5.3.5.3 — Retry de alertas em falha de comunicação

> *"O sistema deverá possuir mecanismos automáticos de reenvio em caso de falha temporária de comunicação, preservando a integridade e rastreabilidade dos alertas processados."*

### Contexto atual

O sistema já possui jobs Quartz de notificação (`JobEnviaSmsAlerta`, `JobEnviaEmailAlerta`, `JobEnviaSmsOcorrencia`, `JobEnviaEmailOcorrencia`). Não há mecanismo de reenvio automático em caso de falha.

### Perguntas para definição

**a)** Quando um envio de SMS/email falha hoje, o que acontece? É perdido silenciosamente ou registrado em alguma tabela?

> **Resposta:**

**b)** Qual a política de retry desejada? (ex: 3 tentativas com backoff de 1min, 5min, 15min?)

> **Resposta:**

**c)** Precisa de uma tela administrativa para visualizar alertas pendentes/falhos, ou basta o mecanismo automático + log de auditoria?

> **Resposta:**

---

## 2. §5.3.7.4 — Videodetecção volumétrica de tráfego

> *"5.3.7.4. A funcionalidade deverá suportar recursos de análise volumétrica de tráfego baseados em videodetecção e laço virtual, permitindo: a) contagem veicular; b) análise de fluxo por faixa; c) cálculo de ocupação da via; d) identificação de retenções ou congestionamentos; e) geração de alertas operacionais parametrizáveis."*

### Contexto atual

Não há módulo de videodetecção no sistema. As alíneas a–c já têm relatórios no legado, com dados dos próprios equipamentos: `com.consilux.model.relatorio.OcupacaoTempo`, `com.consilux.model.relatorio.FluxoTempo` e `muralha.digital.relatorios.RelatorioTaxaOcupacaoVia`; por isso o item está ⚠️ na matriz canônica. As alíneas d e e não estão implementadas. O Plano 19 implementou integração com sensores externos (Waze/SAMU) usando um modelo de importação periódica (`IncidenteImportJob`) + visualização em mapa.

### Perguntas para definição

**a)** TRANSALVADOR já tem equipamentos de videodetecção que fornecem esses dados (contagem, fluxo, ocupação) via API/arquivo? Ou a expectativa é que o GTW processe vídeo diretamente?

> **Resposta:**

**b)** Se é integração com hardware externo: qual o formato dos dados recebidos (API REST, XML, CSV periódico)?

> **Resposta:**

**c)** Dado que não podemos adicionar frameworks novos, a abordagem proposta é modelar como recepção e visualização de dados volumétricos fornecidos por equipamentos externos (similar ao Plano 19 — Waze). Faz sentido essa abordagem?

> **Resposta:**

---

## 3. §5.5.3.3 — Relatório mensal automático de pré-processamento

> *"A CONTRATADA deverá apresentar, até o 5o dia útil de cada mês, relatório consolidado referente ao mês imediatamente anterior."*

### Contexto atual

O sistema possui o módulo de SLA de pré-processamento (Plano 10) que monitora prazo de 72h por lote, e o módulo de exportação padronizada (Plano 11) que suporta PDF/CSV/XLS. Não há geração automática periódica de relatórios consolidados.

O relatório deve conter no mínimo o que pede §5.5.3.1: *"a) volume de registros recebidos; b) volume de registros pré-processados; c) volume de registros disponibilizados para validação; d) volume de registros descartados; e) índice de aproveitamento dos registros; f) tempo médio de processamento por registro e por lote; g) produtividade operacional por operador; h) taxa de divergência entre operadores; i) taxa de retrabalho operacional; j) quantidade de registros pendentes de processamento; k) backlog operacional; l) cumprimento dos níveis mínimos de serviço (SLA); m) motivos de descarte ou inconsistência classificados por categoria; n) histórico de classificação, reclassificação e validação operacional."*

### Perguntas para definição

**a)** O relatório deve ser gerado automaticamente por um job Quartz e disponibilizado na interface, ou também enviado por email?

> **Resposta:**

**b)** Formato: PDF é suficiente, ou precisa dos três (PDF/CSV/XLS) como o TR pede no item 5.5.3.4?

> **Resposta:**

**c)** Os dados necessários para o relatório já existem nas tabelas atuais (ex: `veiculo_tempo_real`, lotes, log de auditoria), ou tem alguma métrica que precisaria de tabela nova?

> **Resposta:**

---

## Próximos passos

1. Preencher as respostas acima para alinhar o escopo de cada item
2. Criar planos de implementação individuais em `docs/planos-salvador/`
3. Priorizar e estimar esforço
4. Implementar, testar e documentar como entregas (os números 22 e 23 já foram usados pelo Sistema de Chamados e pela correção da documentação; no plano de correção da auditoria, §5.3.7.4 d–e está na Fase 6 e §5.5.3.3 na Fase 7; §5.3.5.3 ainda não tem fase)

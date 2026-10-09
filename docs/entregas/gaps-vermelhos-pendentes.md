# Gaps Vermelhos Pendentes — Itens de Desenvolvimento

**Data:** 2026-10-09  
**Status:** 🔴 Pendente de definição  
**Fonte:** [`aderencia-comparativa.md`](aderencia-comparativa.md) · [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)

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

> *"A funcionalidade deverá suportar recursos de análise volumétrica de tráfego baseados em videodetecção e laço virtual, permitindo: a) contagem veicular; b) análise de fluxo por faixa; c) cálculo de ocupação da via."*

### Contexto atual

Não há módulo de videodetecção no sistema. O Plano 19 implementou integração com sensores externos (Waze/SAMU) usando um modelo de importação periódica (`IncidenteImportJob`) + visualização em mapa.

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

O relatório deve conter no mínimo (§5.5.3.1): volume recebido, pré-processado, disponibilizado para validação, validado, descartado, conformidade SLA, taxas de acerto, tempos de processamento, tempo médio por analista, taxas de rejeição por tipo, motivos de descarte e histórico de classificação.

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
4. Implementar, testar e documentar como entregas (21, 22, 23)

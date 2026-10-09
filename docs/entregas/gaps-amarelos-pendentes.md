# Gaps Amarelos Pendentes — Itens de Desenvolvimento

**Data:** 2026-10-09  
**Status:** 🟡 Parcialmente atendido — pendente de definição  
**Fonte:** [`aderencia-comparativa.md`](aderencia-comparativa.md) · [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)

---

## §5.1 — Controle de Acesso

### 1. §5.1.1 — Segregação de funções

> *"Segregação de funções, impedindo que um mesmo usuário acumule permissões incompatíveis com o princípio do duplo controle."*

**Estado atual:** Dupla análise implementada para infrações (Plano 05), mas nem todas as operações têm segregação. O sistema de perfis (`sis_grupo`) existe mas não impede combinações incompatíveis.

**Perguntas:**

**a)** Quais pares de operações são considerados incompatíveis? (ex: quem analisa não pode validar, quem configura não pode operar?)

> **Resposta:**

**b)** A segregação deve ser um bloqueio sistêmico (impedir a atribuição de permissões conflitantes) ou apenas um alerta ao administrador?

> **Resposta:**

---

### 2. §5.1 — Credenciais não compartilhadas (enforcement)

> *Princípio geral de controle de acesso: credenciais individualizadas, não compartilhadas.*

**Estado atual:** Tecnicamente possível criar usuário genérico (ex: "operador1" usado por vários). Sem enforcement sistêmico que impeça isso.

**Perguntas:**

**a)** O enforcement deve ser apenas organizacional (política documentada) ou sistêmico? (ex: detectar login simultâneo do mesmo usuário em IPs diferentes e bloquear/alertar)

> **Resposta:**

**b)** Se sistêmico, qual ação tomar: bloquear a segunda sessão, alertar o administrador, ou ambos?

> **Resposta:**

---

## §5.2 — Gestão e Classificação de Infrações

### 3. §5.2.1.3 — Registro de recepção

> *"O sistema deverá registrar automaticamente: a) identificação do equipamento de origem; b) data e horário da captura; c) data e horário da recepção; d) quantidade de registros por lote."*

**Estado atual:** Dados parcialmente em `infracao`, `infracao_processo`, `eventos_csx`. Nem todos os campos exigidos estão explicitamente registrados (ex: data/hora de recepção distinta da captura).

**Perguntas:**

**a)** Os campos `data_hora` em `infracao` referem-se à captura ou à recepção? Existe distinção clara entre os dois timestamps no modelo atual?

> **Resposta:**

**b)** O campo de quantidade de registros por lote já é rastreável via `muralha.lote`, ou precisa de registro adicional?

> **Resposta:**

---

### 4. §5.2.1.5 — Recepção simultânea + margem 30%

> *"O sistema deverá suportar recepção simultânea de registros provenientes de múltiplos equipamentos, observando capacidade compatível com o parque tecnológico previsto, acrescido de margem mínima de expansão de 30%."*

**Estado atual:** Pool JDBC max 400 conexões. Sem métricas de capacidade documentadas nem teste de carga.

**Perguntas:**

**a)** Quantos equipamentos estão previstos no parque de Salvador? (para calcular o +30%)

> **Resposta:**

**b)** Este item se resolve com documentação de capacidade (benchmark do pool) + monitoramento, ou precisa de desenvolvimento de tela/dashboard de capacidade?

> **Resposta:**

---

### 5. §5.2.1.6 — Registros fora do prazo

> *"Registros recebidos fora do prazo máximo de transmissão deverão ser automaticamente sinalizados em sistema, com geração de alerta operacional e correspondente registro em log."*

**Estado atual:** SLA de pré-processamento (Plano 10) monitora prazos por lote, mas sem flag individual por registro fora do prazo de transmissão.

**Perguntas:**

**a)** Qual é o prazo máximo de transmissão considerado? (ex: 4s para blitz, ou um prazo diferente para registros normais?)

> **Resposta:**

**b)** A sinalização deve aparecer como coluna/flag na tela de consulta de infrações, ou basta gerar um alerta no módulo de alertas existente?

> **Resposta:**

---

### 6. §5.2.2.1 — Pré-classificação ≤4s

> *"Processamento automatizado de pré-classificação imediatamente após a recepção do registro, com conclusão em até 4 segundos contados do evento fiscalizado."*

**Estado atual:** Processamento automático via Quartz existe. Sem SLA de 4s monitorado para a pré-classificação individual.

**Perguntas:**

**a)** O SLA de latência (Plano 07) já monitora o tempo de resposta. Basta estender esse monitoramento para cobrir também o tempo de pré-classificação, ou precisa de um mecanismo separado?

> **Resposta:**

**b)** O processamento Quartz atual é batch (processa N registros por execução) ou individual? Se batch, é viável medir o SLA de 4s por registro?

> **Resposta:**

---

### 7. §5.2.2.5 — Encaminhamento automático por score

> *"Divergências identificadas entre leituras automáticas deverão ser encaminhadas automaticamente para revisão operacional."*

**Estado atual:** `CorrecaoPlaca` existe para tratar divergências. Sem encaminhamento automático baseado em score de confiança da leitura.

**Perguntas:**

**a)** O encaminhamento deve criar uma tarefa na fila de um operador específico, ou basta marcar o registro com status "pendente revisão" na tela de gestão de lotes?

> **Resposta:**

**b)** O limiar de score que dispara o encaminhamento deve ser configurável pela interface (admin), ou pode ser um parâmetro fixo no banco?

> **Resposta:**

---

## §5.3 — Alertas de Irregularidades

### 8. §5.3.1.3 — Parametrização de alertas

> *"O sistema deverá permitir parametrização individualizada dos tratamentos aplicáveis a cada categoria de alerta, incluindo: a) encaminhamento para fiscalização em campo; b) encaminhamento para análise operacional; c) geração automática de notificação; d) registro em base de dados."*

**Estado atual:** `config_grupo_permissao` e `ConfiguracaoSons` permitem configuração parcial. Não há tela para parametrizar o tratamento por categoria de alerta.

**Perguntas:**

**a)** Quantas categorias de alerta existem hoje no sistema? (tipos em `tipo_alerta_ocorrencia`)

> **Resposta:**

**b)** A tela de parametrização deve permitir associar cada categoria a uma ou mais ações (SMS, email, campo, log), ou basta um toggle liga/desliga por categoria?

> **Resposta:**

---

### 9. §5.3.4 — Mapa interativo com clustering

> *"Os alertas deverão ser apresentados em mapa digital interativo, permitindo: a) visualização por região; b) filtros espaciais; c) agrupamento por concentração geográfica."*

**Estado atual:** Mapa Leaflet funcional com `mapa-interativo/`, `mancha-monitorada/`, `ponto-interesse/`. Sem agrupamento por concentração (clustering).

**Perguntas:**

**a)** O clustering pode ser implementado usando a biblioteca Leaflet.markercluster (já usada em outros projetos GTW) ou isso conta como "novo framework"?

> **Resposta:**

**b)** O clustering deve se aplicar apenas a alertas, ou também aos pontos do mapa de incidentes (Plano 19)?

> **Resposta:**

---

### 10. §5.3.5.1d — App mobile dedicado

> *"O sistema deverá permitir encaminhamento dos alertas para dispositivos móveis utilizados em campo."*

**Estado atual:** SMS/e-mail via Twilio/SendGrid. Sem app móvel dedicado.

**Perguntas:**

**a)** Um PWA (Progressive Web App) responsivo que funcione no navegador do celular seria aceito como "dispositivo móvel", ou é obrigatório um app nativo (Play Store/App Store)?

> **Resposta:**

**b)** Se PWA, o escopo seria apenas visualização de alertas push + mapa, ou também ações operacionais (aceitar/rejeitar alerta, abrir ocorrência)?

> **Resposta:**

---

### 11. §5.3.7.5 / §5.6.2.3 / §5.7.9.1 — Resposta ≤4s (consolidado)

> *"Tempo máximo de resposta de 4 segundos para eventos críticos e operações de Blitz Eletrônica."*
>
> *Aparece em três seções: §5.3.7.5 (alertas), §5.6.2.3 (transmissão) e §5.7.9.1 (LAP).*

**Estado atual:** Thread de 2s para alertas já existe. SLA de latência (Plano 07) monitora o tempo. Os três itens compartilham o mesmo requisito de 4s.

**Perguntas:**

**a)** O monitoramento de SLA de latência (Plano 07) já cobre o ciclo completo captura→alerta. Para resolver estes 3 itens, basta documentar que o SLA está sendo monitorado e que as métricas atendem, ou precisa de alguma garantia adicional (ex: circuit breaker, fallback)?

> **Resposta:**

**b)** Em caso de violação do SLA de 4s, qual ação deve ser tomada? Alerta ao operador, log, ou ambos?

> **Resposta:**

---

## §5.4 — Inteligência de Dados

### 12. §5.4.1.1 — Consolidação multifonte

> *"A funcionalidade deverá consolidar automaticamente dados provenientes de: a) Equipamentos de Fiscalização; b) Gestão e Classificação; c) Alertas; d) fontes externas (Waze, sensores)."*

**Estado atual:** Dados em tabelas separadas (`veiculo_tempo_real` 8,2M registros, `muralha.alerta`, `muralha.incidente_externo`). Sem pipeline de consolidação unificada.

**Perguntas:**

**a)** A consolidação deve ser uma view materializada/tabela unificada para consulta cruzada, ou basta que os dashboards (Plano 15) já façam JOINs entre as tabelas existentes?

> **Resposta:**

**b)** A atualização deve ser em tempo real (trigger) ou periódica (job Quartz, ex: a cada 5 min)?

> **Resposta:**

---

### 13. §5.4.2.4a-b — Filtros dinâmicos com atualização simultânea

> *"Os dashboards deverão suportar: a) filtros dinâmicos por período, equipamento, localidade, faixa horária, tipo de infração e classificação veicular; b) atualização simultânea dos indicadores apresentados."*

**Estado atual:** Filtros existem nos módulos PoC. A atualização simultânea (mudar um filtro e todos os gráficos/indicadores se atualizam) não é garantida em todos os dashboards.

**Perguntas:**

**a)** Quais dashboards específicos precisam desta melhoria? Todos os módulos PoC, ou apenas o dashboard de KPIs (Plano 15)?

> **Resposta:**

**b)** A atualização deve ser via AJAX (sem reload da página), correto?

> **Resposta:**

---

### 14. §5.4.2.5 — Dashboards customizáveis

> *"A CONTRATANTE poderá configurar dashboards personalizados, selecionando indicadores, filtros, visualizações e parâmetros operacionais."*

**Estado atual:** KPIs configuráveis (Plano 15) permitem definir indicadores e metas. Sem drag-and-drop de painéis ou criação livre de dashboards.

**Perguntas:**

**a)** O TR pede que a CONTRATANTE "configure" dashboards. Uma tela onde o administrador seleciona quais KPIs/gráficos exibir (checkbox + ordenação) seria suficiente, ou precisa de um builder visual drag-and-drop?

> **Resposta:**

**b)** Os dashboards customizados devem ser salvos por usuário ou são globais (visíveis para todos do mesmo perfil)?

> **Resposta:**

---

### 15. §5.4.5 — Relatórios analíticos completos

> *"Relatórios analíticos e gerenciais: volume de tráfego, velocidade média, taxa de ocupação, índice de infrações, fluxo veicular por faixa horária, volume de alertas, distribuição geográfica, comparativos temporais, conformidade SLA."*

**Estado atual:** ~35 relatórios existentes no módulo legado. Cobertura parcial dos indicadores exigidos.

**Perguntas:**

**a)** Quais indicadores específicos faltam nos relatórios atuais? (para mapear o gap exato)

> **Resposta:**

**b)** Os relatórios novos devem seguir o padrão de exportação do Plano 11 (PDF/CSV/XLS), correto?

> **Resposta:**

---

### 16. §5.4.7.6 — Histórico de configurações

> *"Histórico das alterações nos parâmetros operacionais: a) data/horário; b) parâmetro alterado; c) valor anterior; d) valor novo; e) responsável."*

**Estado atual:** Log de auditoria (Plano 01) registra ações genéricas. Não registra especificamente o valor anterior/novo de parâmetros de configuração.

**Perguntas:**

**a)** Quais configurações devem ser rastreadas? Apenas parâmetros de equipamentos, ou também configurações do sistema (SLA, KPIs, políticas de senha, etc.)?

> **Resposta:**

**b)** O log de auditoria (Plano 01) pode ser estendido para incluir campos `valor_anterior` e `valor_novo`, ou precisa de tabela separada?

> **Resposta:**

---

## §5.5 — Pré-processamento

### 17. §5.5.1.3 — 100% pré-processados

> *"A CONTRATADA deverá realizar o pré-processamento de 100% dos registros capturados, sendo vedado o descarte automático sem análise prévia."*

**Estado atual:** Processamento via Quartz existe. Sem garantia de cobertura 100% documentada nem mecanismo de detecção de registros "perdidos".

**Perguntas:**

**a)** Este item se resolve com um relatório/monitor que compare registros recebidos vs. processados e alerte quando houver diferença, ou precisa de mudança no fluxo de processamento?

> **Resposta:**

**b)** Já existe algum mecanismo de reconciliação entre o que o equipamento enviou e o que o sistema recebeu?

> **Resposta:**

---

### 18. §5.5.3.1 — Painel operacional de pré-processamento

> *"Relatórios operacionais contemplando: volume recebido, pré-processado, validado, descartado, conformidade SLA, taxas de acerto, tempos, motivos de descarte, histórico de classificação."*

**Estado atual:** SLA de pré-processamento (Plano 10) serve como painel parcial. Faltam vários indicadores exigidos.

**Perguntas:**

**a)** Este painel deve ser uma extensão da tela de SLA pré-processamento (Plano 10), ou uma tela nova dedicada?

> **Resposta:**

**b)** Os indicadores devem ser em tempo real (atualização automática) ou sob demanda (botão atualizar)?

> **Resposta:**

---

### 19. §5.5.4.5 — Log completo de pré-processamento

> *"Registros de log contendo: a) identificação do usuário; b) data/horário; c) estação de trabalho; d) ação executada; e) resultado."*

**Estado atual:** Log de auditoria (Plano 01) cobre operador/timestamp/ação. Falta o campo "estação de trabalho" (hostname/IP da máquina do operador).

**Perguntas:**

**a)** "Estação de trabalho" refere-se ao IP do cliente (`request.getRemoteAddr()`) ou ao hostname da máquina?

> **Resposta:**

**b)** Basta adicionar esse campo ao log de auditoria existente (`muralha.log_auditoria`), ou precisa de tabela separada para logs de pré-processamento?

> **Resposta:**

---

## §5.7 — LAP (Leitura Automática de Placas)

### 20. §5.7.2.1 — Índice mínimo de acerto LAP

> *"Índices mínimos de acerto: 90% para placas padrão Mercosul; 85% para placas padrão anterior."*

**Estado atual:** LAP funciona. Índices não aferidos mensalmente com metodologia formal.

**Perguntas:**

**a)** Os dados de acerto/erro da LAP já estão no banco (ex: placa lida vs. placa corrigida manualmente)? Se sim, em quais tabelas?

> **Resposta:**

**b)** Este item se resolve com um relatório/dashboard que calcule automaticamente o índice de acerto por período, ou precisa de mudança no motor de LAP?

> **Resposta:**

---

### 21. §5.7.4.1-2 — Score parametrizável

> *"Indicador de confiança (score) atribuído automaticamente a cada leitura. Limiar parametrizável pela CONTRATANTE."*

**Estado atual:** `ia.veiculo_caracteristica` tem score de confiança. O limiar não é parametrizável pela interface.

**Perguntas:**

**a)** Onde o limiar de score está definido hoje? (hardcoded no código, tabela de config, ou parâmetro do equipamento?)

> **Resposta:**

**b)** A tela de parametrização deve permitir definir limiares diferentes por equipamento/local, ou um limiar global é suficiente?

> **Resposta:**

---

### 22. §5.7.4.3 — Encaminhamento automático por score baixo

> *"Score inferior ao limiar → encaminhamento automático para revisão operacional."*

**Estado atual:** `CorrecaoPlaca` trata divergências manualmente. Sem encaminhamento automático baseado no score.

**Perguntas:**

**a)** O encaminhamento deve criar um item na fila do operador de correção de placas, ou marcar o registro com um status específico?

> **Resposta:**

**b)** Este item depende da resolução do item 21 (score parametrizável). Devem ser implementados juntos?

> **Resposta:**

---

### 23. §5.7.5.2 — Log de correção com justificativa

> *"Log automático para toda correção manual."*

**Estado atual:** `CorrecaoPlaca` registra operador, data, placa original e corrigida. Sem campo de justificativa obrigatório.

**Perguntas:**

**a)** A justificativa deve ser texto livre ou seleção de motivos pré-definidos (ex: "erro OCR", "placa parcialmente visível", "placa danificada")?

> **Resposta:**

**b)** A justificativa deve ser obrigatória para toda correção, ou apenas quando a correção difere significativamente da leitura original?

> **Resposta:**

---

## Resumo

| Seção TR | Qtde itens ⚠️ | Natureza predominante |
|---|---|---|
| §5.1 Controle de Acesso | 2 | Regras de negócio / enforcement |
| §5.2 Gestão e Classificação | 5 | Monitoramento / automação |
| §5.3 Alertas | 4 | Parametrização / UX |
| §5.4 Inteligência de Dados | 5 | Dashboards / relatórios |
| §5.5 Pré-processamento | 3 | Completude de dados |
| §5.7 LAP | 4 | Parametrização / métricas |
| **Total** | **23** | |

## Próximos passos

1. Preencher as respostas para alinhar escopo
2. Agrupar itens relacionados em planos de implementação (ex: §5.7.4.1-3 podem ser um único plano)
3. Priorizar conforme impacto na aderência e esforço estimado
4. Implementar, testar e documentar como entregas sequenciais

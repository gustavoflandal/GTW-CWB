# Análise de Aderência — GTW-CWB × Edital Salvador (TRANSALVADOR)

> **Documento:** TR Salvador — Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica  
> **Referência edital:** Capítulo 5 (Sistema) + Anexo A (PoC)  
> **Base de análise:** GTW-CWB em estado atual (branch `main`, 2026-10-08)  
> **Legenda:** ✅ Atende · ⚠️ Atende parcialmente · ❌ Não atende / Gap

---

## 1. Controle de Acesso (§5.1)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Autenticação individualizada com login/senha | 5.1.5a-b | ✅ | `login_action.jsp`, `Usuario.comparaSenha()`, `SessaoConstantes.SESSAO_USUARIO` | — |
| Política parametrizável de senha (complexidade, validade, histórico, bloqueio) | 5.1.5c | ❌ | Validação apenas de formato `[A-Z0-9.]{2,30}`; sem complexidade, expiração ou histórico | Implementar controle de senha |
| Encerramento de sessão por inatividade configurável | 5.1.5d | ✅ | `ConfiguracaoInatividade`, `LoginTempoInatividade`, timeout padrão 240 min | — |
| Suporte nativo a MFA | 5.1.5e | ❌ | Não existe; apenas login Google OAuth como segundo fator opcional | Implementar TOTP/MFA |
| Criação/edição/desativação de perfis de acesso | 5.1.6 | ✅ | `config_grupo_permissao` (277 linhas), `sis_menu_direitos`, `PermissoesFuncionalidade` | — |
| Log de auditoria automático e inviolável | 5.1.7-8 | ⚠️ | `sis_log` existe; `LogonLogoff` registra entrada/saída; motivo obrigatório em relatórios — mas nem todas as operações são logadas | Completar cobertura de log |
| Log contendo: usuário, timestamp, IP, funcionalidade, operação, ID do registro | 5.1.9 | ⚠️ | `sis_log` tem user+timestamp; IP capturado no login, mas não em todas as ações | Adicionar IP e ID de registro ao log |
| Consulta de log com filtros e exportação PDF/CSV/XLS | 5.1.10 | ❌ | Não há tela de auditoria de log com filtros e exportação | Criar módulo de auditoria |
| Segregação de funções / duplo controle | 5.1.1 | ❌ | Não implementado; um único usuário pode executar todas as etapas | Bloquear re-validação pelo mesmo operador |
| Identificação vinculada a CPF/matrícula | 5.1.5a | ⚠️ | Modelo de usuário não inclui CPF obrigatório; campo pode existir mas não é validado | Adicionar CPF/matrícula ao cadastro |
| Credenciais não compartilhadas/genéricas | 5.1 | ⚠️ | Tecnicamente possível criar usuário genérico; sem enforcement sistêmico | Adicionar validação no cadastro |

**Cobertura §5.1: ~40%**

---

## 2. Gestão e Classificação de Infrações (§5.2)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Recepção automática de registros dos equipamentos | 5.2.1.1 | ✅ | SOAP `CSXEventsWS`, `ConfigEquipWS`, jobs Quartz de processamento | — |
| Verificação de integridade SHA-256 | 5.2.1.2 | ❌ | Não implementado; sem hash de integridade explícito | Implementar verificação SHA-256 na recepção |
| Registro de: equipamento origem, data/hora captura/recepção, qtdes | 5.2.1.3 | ⚠️ | Dados parcialmente em `infracao`, `infracao_processo`, `eventos_csx` | Consolidar em tabela de recepção de lotes |
| Indexação por tipo de infração, equipamento, localização, período, lote | 5.2.1.4 | ✅ | Tabelas `infracao`, `infracao_processo`, filtros nos servlets GTW | — |
| Recepção simultânea de múltiplos equipamentos + margem 30% | 5.2.1.5 | ⚠️ | Pool JDBC max 400; sem métricas de capacidade documentadas | Documentar capacidade e margem |
| Sinalização de registros fora do prazo | 5.2.1.6 | ❌ | Sem alerta automático para registros tardios | Implementar flag de atraso |
| Pré-classificação automatizada ≤4 segundos | 5.2.2.1 | ⚠️ | Processamento automático via Quartz existe; sem SLA de 4s monitorado | Adicionar monitoramento de latência |
| Aderência mínima de 80% da pré-classificação | 5.2.2.3 | ❌ | Sem métrica de assertividade entre pré-classificação e validação final | Implementar painel de assertividade |
| OCR/LAP automático com conferência cruzada | 5.2.2.4 | ✅ | LAP integrado; `CorrecaoPlaca`, `veiculo_tempo_real_correcao` | — |
| Encaminhamento automático de divergências de placa para revisão | 5.2.2.5 | ⚠️ | `CorrecaoPlaca` existe; sem encaminhamento automático por score | Automatizar encaminhamento |
| Painel de controle com assertividade, volume, divergências em tempo real | 5.2.2.6 | ❌ | Não existe painel de aderência da pré-classificação | Criar painel operacional |
| Dupla análise independente obrigatória (sem ver análise anterior) | 5.2.3.2-3 | ❌ | GTW não impede um mesmo operador de validar duas vezes; sem segregação | Implementar controle de dupla análise |
| Desempate por terceiro operador sem ver análises anteriores | 5.2.3.4 | ❌ | Não existe fluxo de desempate | Criar fluxo de desempate |
| Interface de análise com zoom, brilho, contraste (sem alterar original) | 5.2.4.1-3 | ⚠️ | Visualização de imagens existe; ferramentas de zoom/ajuste dependem do browser | Implementar ferramentas de edição visual client-side |
| Obliteração automática e manual de imagens (LGPD) | 5.2.4.3-4 | ❌ | Sem obliteração implementada | Implementar obliteração de imagens |
| Campo de placa editável com log | 5.2.4.5 | ✅ | `CorrecaoPlaca`, alteração grava `veiculo_tempo_real_correcao` | — |
| Gestão de lotes com parâmetros configuráveis | 5.2.5.1 | ✅ | `infracao_remessa*`, gestão de remessas | — |
| Seleção automática de amostras para auditoria | 5.2.5.2 | ✅ | `/ExportaAmostras`, `amostras/*` | — |
| Auditoria integral de qualquer lote | 5.2.5.3 | ⚠️ | Possível mas não sistematizado | Formalizar fluxo de auditoria integral |
| Bloqueio lógico de envio sem validação da CONTRATANTE | 5.2.5.4-5 | ⚠️ | Remessa exige workflow, mas bloqueio sistêmico não está documentado | Verificar e reforçar bloqueio |
| Integração via WebService HTTPS seguro | 5.2.6.2 | ⚠️ | SOAP/HTTP existe; HTTPS depende de configuração de deploy | Garantir HTTPS e criptografia TLS |
| Telemetria operacional em tempo real | 5.2.7 | ⚠️ | `monitoramento/*`, alertas de offline (`fcn_getAlertaLocaisOffLine`) | Adicionar telemetria centralizada |
| Relatórios com exportação PDF/CSV/XLS | 5.2.8.3 | ⚠️ | SheetJS e jsPDF nos relatórios existentes; cobertura parcial | Padronizar exportação para todos os relatórios |
| Dashboards analíticos em tempo real | 5.2.8.4 | ⚠️ | `dashboards/` existe; não cobrindo todos os indicadores operacionais | Expandir dashboards |

**Cobertura §5.2: ~45%**

---

## 3. Alertas de Irregularidades (§5.3)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Recepção automática de alertas dos equipamentos | 5.3.1.1 | ✅ | Thread `Alertas` (ciclo 2s), `spu_ObterNovosAlertas`, WebSocket `ALERTA-NOTIFICACAO` | — |
| Classificação, organização e priorização automática | 5.3.1.2 | ✅ | `tipo_alerta_ocorrencia`, `status_alerta`; prioridade configurável | — |
| Parametrização individualizada de tratamentos | 5.3.1.3 | ⚠️ | `config_grupo_permissao`, `ConfiguracaoSons`; parcialmente configurável | Ampliar parametrização de tratamentos |
| Rastreabilidade integral do fluxo (geração → encerramento) | 5.3.1.4 | ✅ | `muralha.alerta`, `anotacao_contributiva`, `linha-tempo/`, `LinhaTempo` API | — |
| Tipos de alertas: circulação, restrições, congestionamentos, falhas | 5.3.2.1 | ✅ | `tipo_alerta_ocorrencia` configurável | — |
| Inclusão de novos tipos durante vigência | 5.3.2.2 | ✅ | Tabela de tipos parametrizável | — |
| Interface web com alertas ativos/tratados/pendentes | 5.3.3.1 | ✅ | `consulta-alerta-ocorrencia/`, `alerta-tratativa/` | — |
| Visualização georreferenciada | 5.3.3.2a | ✅ | `mapa-alertas-ocorrencias/`, Google Maps | — |
| Filtros por período, tipo, equipamento, localidade, status | 5.3.3.2b | ✅ | `AlertaOcorrencia.consultaPorFiltrosTela` | — |
| Pesquisa por placa (inclusive parcial) | 5.3.3.2e | ✅ | Wildcards `*` no campo placa, `ExigirPlacaCompleta=false` | — |
| Destaque visual para alertas críticos | 5.3.3.4 | ✅ | `ConfiguracaoSons`, prioridade visual na tela | — |
| Mapa interativo com filtros espaciais e áreas de interesse | 5.3.4 | ⚠️ | `mapa-interativo/`, `mancha-monitorada/`, `ponto-interesse/` — funcional, sem agrupamento por concentração | Adicionar clustering e áreas de interesse parametrizáveis |
| Encaminhamento para dispositivos móveis | 5.3.5.1d | ⚠️ | SMS/e-mail via Twilio/SendGrid; sem app móvel dedicado | Avaliar integração mobile |
| Reenvio automático em caso de falha de comunicação | 5.3.5.3 | ❌ | Não implementado | Implementar retry de alertas |
| Histórico integral exportável (PDF/CSV/XLS) | 5.3.6.4 | ⚠️ | Histórico existe; exportação padronizada parcial | Completar exportação |
| Integração com sensores meteorológicos, PMV, sensores de tráfego | 5.3.7.1-2 | ❌ | Não existe integração com sensores externos | Nova integração a desenvolver |
| Integração com Google Maps/Waze para contextualização | 5.3.7.3 | ⚠️ | Google Maps para mapas; sem dados Waze | Integrar API de tráfego |
| Análise volumétrica de tráfego (videodetecção, laço virtual) | 5.3.7.4 | ❌ | Sem videodetecção de tráfego volumétrico | Nova funcionalidade a desenvolver |
| Resposta ≤4 segundos para eventos críticos/Blitz Eletrônica | 5.3.7.5 | ⚠️ | Thread 2s; sem SLA medido nem alertas de degradação de latência | Implementar monitoramento de SLA de latência |

**Cobertura §5.3: ~60%**

---

## 4. Inteligência de Dados e Apoio à Decisão (§5.4)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Consolidação de dados de múltiplas fontes (equipamentos, sistemas) | 5.4.1.1 | ⚠️ | `veiculo_tempo_real` (8,2M), `muralha.alerta`; sem pipeline de consolidação multifonte | Criar pipeline de consolidação |
| Conformidade LGPD (anonimização, finalidade, necessidade) | 5.4.1.3 | ⚠️ | Não há mecanismo formal de anonimização | Implementar anonimização parametrizável |
| Dashboards analíticos interativos via navegador | 5.4.2.1-2 | ✅ | `dashboards/`, `painel-informacoes/`, Chart.js | — |
| Filtros dinâmicos com atualização simultânea | 5.4.2.4a-b | ⚠️ | Filtros existem; atualização simultânea de indicadores não garantida | Refatorar dashboards para atualização sincronizada |
| Exportação PDF/CSV/XLS/PNG dos dashboards | 5.4.2.4d | ⚠️ | SheetJS/jsPDF disponíveis; não implementado para todos os painéis | Completar exportação nos dashboards |
| Configuração de dashboards personalizados pela CONTRATANTE | 5.4.2.5 | ❌ | Dashboards fixos, sem customização dinâmica | Implementar customização de painéis |
| Análise espacial com mapa interativo (heatmap, concentração) | 5.4.4 | ✅ | `mapa-calor/`, `mapa-interativo/`, `mapa-equipamento/` | — |
| Exportação de dados georreferenciados (GIS) | 5.4.4.4 | ❌ | Sem exportação em formatos GIS (GeoJSON, Shapefile, KML) | Implementar exportação GIS |
| Relatórios analíticos completos (tráfego, velocidade, infração) | 5.4.5 | ⚠️ | ~35 relatórios existentes; cobertura parcial dos indicadores exigidos | Ampliar cobertura de relatórios |
| Indicadores parametrizáveis com metas e alertas automáticos | 5.4.6.3 | ❌ | Indicadores fixos; sem definição de metas e alertas analíticos | Criar módulo de KPIs configuráveis |
| Repositório central de logs técnicos e operacionais | 5.4.7 | ⚠️ | `sis_log`, logs de equipamentos dispersos | Centralizar logs em repositório único com interface |
| Histórico de alterações de parâmetros de equipamentos | 5.4.7.6 | ⚠️ | Audit trail parcial | Completar rastreabilidade de configurações |

**Cobertura §5.4: ~50%**

---

## 5. Pré-processamento de Imagens (§5.5)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| 100% dos registros pré-processados antes de validação | 5.5.1.3 | ⚠️ | Processamento via Quartz; sem garantia de cobertura 100% documentada | Implementar controle de cobertura |
| Dupla análise independente por operadores distintos | 5.5.1.2g | ❌ | Não implementado sistematicamente | Implementar workflow de dupla análise |
| Prazo máximo 72h por lote | 5.5.2.1 | ❌ | Sem SLA de 72h monitorado | Implementar alertas de SLA por lote |
| Painel de controle operacional do pré-processamento | 5.5.3.1 | ❌ | Não existe painel específico de pré-processamento | Criar painel de gestão de lotes |
| Relatório mensal até 5º dia útil | 5.5.3.3 | ❌ | Sem relatório automático periódico | Automatizar relatório mensal |
| Vedação de alteração de arquivos originais | 5.5.4.2 | ✅ | Imagens originais em `veiculo_tempo_real_imagem`; ajustes apenas para visualização | — |
| Log completo de pré-processamento (operador, timestamp, workstation, ação) | 5.5.4.5 | ⚠️ | Log parcial; falta estação de trabalho e ação detalhada | Completar campos de log |
| Retenção de logs por 5 anos após encerramento contratual | 5.5.4.6 | ❌ | Sem política de retenção definida | Definir e implementar política de retenção |

**Cobertura §5.5: ~25%**

---

## 6. Infraestrutura de Comunicação (§5.6)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Arquitetura integrada campo ↔ plataforma central | 5.6.1.1 | ✅ | SOAP `CSXEventsWS`, threads de processamento, nginx proxy | — |
| Redundância lógica e física | 5.6.1.2 | ❌ | Sem redundância documentada na aplicação | Planejar redundância de infraestrutura |
| Transmissão com protocolos seguros | 5.6.1.4 | ⚠️ | HTTP/SOAP disponível; HTTPS depende de configuração de deploy | Garantir TLS end-to-end |
| Monitoramento de enlaces com alerta de degradação | 5.6.1.5 | ⚠️ | `fcn_getAlertaLocaisOffLine`, `fcn_getAlertaLocaisConexaoInstavel` | Centralizar monitoramento de links |
| Transmissão automática de imagens e registros | 5.6.2.1 | ✅ | Jobs Quartz, SOAP | — |
| Tempo máximo 4s para eventos críticos/Blitz | 5.6.2.3 | ⚠️ | Thread 2s para alertas; sem medição de latência fim-a-fim | Implementar SLA de latência |
| Criptografia de dados em trânsito e em repouso | 5.6.3 | ❌ | Sem criptografia explícita implementada | Implementar TLS e criptografia em repouso |
| Armazenamento local por 6h de autonomia | 5.6.4.3 | ❌ | Responsabilidade do firmware dos equipamentos; sem especificação | Verificar capacidade dos equipamentos |
| Sincronismo NTP/GPS | 5.6.6.1 | ⚠️ | Menciona sincronismo; sem mecanismo sist. documentado | Documentar e verificar sincronismo |
| Configuração remota de equipamentos com log | 5.6.7 | ✅ | `ConfigEquipWS`, `configuracao_equipamento*`, histórico de configurações | — |

**Cobertura §5.6: ~40%**

---

## 7. LAP — Leitura Automática de Placas (§5.7)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Padrão Mercosul e anterior simultâneo | 5.7.1.1 | ✅ | LAP existente suporta ambos os padrões | — |
| Índice mínimo: 90% Mercosul, 85% anterior | 5.7.2.1 | ⚠️ | LAP funciona; índices não aferidos mensalmente com metodologia formal | Implementar aferição mensal formal |
| Score de confiança parametrizável | 5.7.4.1-2 | ⚠️ | `ia.veiculo_caracteristica` tem score; limiar não parametrizável pela CONTRATANTE | Expor parametrização do limiar |
| Encaminhamento automático por score baixo | 5.7.4.3 | ⚠️ | `CorrecaoPlaca` trata divergências; sem encaminhamento automático por score | Automatizar encaminhamento |
| Score como metadado permanente | 5.7.4.4 | ✅ | `ia.veiculo_caracteristica` | — |
| Rastreabilidade: leitura original, processada, corrigida | 5.7.5.1 | ✅ | `veiculo_tempo_real_correcao`, log de correções | — |
| Log de correção manual (operador, data, original, corrigida, justificativa) | 5.7.5.2 | ⚠️ | `CorrecaoPlaca` registra; sem campo de justificativa obrigatório | Adicionar justificativa ao log de correção |
| Retenção do histórico por 5 anos pós-contrato | 5.7.5.3 | ❌ | Sem política de retenção definida | Definir política de retenção |
| Anonimização de placas para fins estatísticos (LGPD) | 5.7.8 | ❌ | Não implementado | Implementar anonimização parametrizável |
| Tempo máximo 4s entre leitura e alerta na plataforma | 5.7.9.1 | ⚠️ | Thread 2s; sem monitoramento contínuo do tempo de resposta | Implementar monitoramento de latência LAP |
| Vídeos operacionais time-lapse | 5.7.7 | ❌ | FFmpeg para clipes; sem time-lapse automático associado aos registros LAP | Implementar geração de time-lapse |

**Cobertura §5.7: ~45%**

---

## 8. Infraestrutura de Datacenter (§5.8)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Gap |
|---|---|---|---|---|
| Disponibilidade mínima: 99,5% (sistema) / 99% (datacenter) | 5.8.2.1 | ❌ | Sem SLA formal documentado | Definir SLA e implementar monitoramento |
| Datacenter Tier III ou equivalente | 5.8.1.2 | ❌ | Infraestrutura não documentada com certificação | Obter certificação ou hospedar em DC certificado |
| Alta disponibilidade (HA) dos servidores | 5.8.3.1 | ❌ | Sem HA documentado | Planejar arquitetura HA |
| RTO ≤4h, RPO ≤1h | 5.8.3.3 | ❌ | Sem objetivos de recuperação definidos | Definir e testar RTO/RPO |
| Comunicar incidente de segurança em ≤2h | 5.8.4.4 | ❌ | Sem processo de resposta a incidentes | Criar processo de resposta a incidentes |
| Política de backup e recuperação | 5.8.5.1 | ❌ | Sem política formal documentada | Implementar política de backup |
| PRD (Plano de Recuperação de Desastres) | 5.8.5.4 | ❌ | Não existe | Elaborar PRD e PCN |
| Monitoramento contínuo da infraestrutura | 5.8.6.1 | ❌ | Sem solução de monitoramento de infraestrutura documentada | Implementar stack de observabilidade |

**Cobertura §5.8: ~5%**

---

## 9. Prova de Conceito — PoC (Anexo A)

| Item avaliado na PoC | Status GTW-CWB |
|---|---|
| Módulo de Gestão e Classificação de Infrações (operação completa) | ⚠️ Funcional mas com gaps de dupla análise e obliteração |
| Recepção de registros e fluxo de análise | ⚠️ Funcional; SHA-256 ausente |
| Dupla classificação por operadores independentes | ❌ Não implementado |
| Exportação de lotes infracionais | ✅ Remessas existentes |
| Alertas de Irregularidades (geração, visualização, rastreabilidade) | ✅ Funcional |
| Painel de Inteligência de Dados (painéis analíticos, exportação) | ⚠️ Funcional; exportação incompleta |
| Integração campo ↔ sistema (imagens e eventos em tempo real) | ✅ Funcional |
| LOG do sistema (eventos relevantes) | ⚠️ Parcial |
| Verificação de falta/retorno de energia (alerta em ≤5 min) | ✅ `fcn_getAlertaLocaisOffLine` |
| Verificação de falha/retorno de comunicação (alerta em ≤5 min) | ✅ `fcn_getAlertaLocaisConexaoInstavel` |
| Ativação/desativação remota de fiscalização | ✅ `ConfigEquipWS` |
| LAP com índices mínimos (90%/85%) | ⚠️ Funciona; índices não aferidos |
| Classificação veicular (tipo: 95%, marca/modelo/cor: 80%) | ⚠️ `ia.veiculo_caracteristica` existe; precisão não aferida |
| Disponibilidade operacional ≥95% | ❌ Sem monitoramento formal |
| Imagens válidas ≥95% | ⚠️ Não aferido |

---

## 10. Resumo Executivo de Aderência

| Módulo | Aderência | Prioridade para PoC |
|---|---|---|
| 5.1 Controle de Acesso | **~40%** | Alta |
| 5.2 Gestão e Classificação de Infrações | **~45%** | Crítica |
| 5.3 Alertas de Irregularidades | **~60%** | Média |
| 5.4 Inteligência de Dados | **~50%** | Média |
| 5.5 Pré-processamento de Imagens | **~25%** | Alta |
| 5.6 Infraestrutura de Comunicação | **~40%** | Alta |
| 5.7 LAP | **~45%** | Crítica |
| 5.8 Datacenter / Disponibilidade | **~5%** | Alta |

**Aderência global estimada: ~39%**

### Gaps críticos que inviabilizam a PoC hoje
1. Dupla análise independente obrigatória (§5.2.3 e §5.5)
2. Obliteração de imagens — LGPD (§5.2.4.3)
3. Controle de política de senhas e MFA (§5.1.5)
4. Verificação de integridade SHA-256 (§5.2.1.2)
5. Painel de assertividade da pré-classificação (§5.2.2.6)
6. Log de auditoria completo com exportação (§5.1.9-10)
7. SLA de latência ≤4s monitorado (§5.6.2.3 / §5.7.9)
8. Métricas de disponibilidade e uptime formal (§5.8.2)

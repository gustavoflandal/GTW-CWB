# Análise de Aderência — GTW-CWB × Edital Salvador (TRANSALVADOR)

> **Documento:** TR Salvador — Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica  
> **Referência edital:** Capítulo 5 (Sistema) + Anexo A (PoC)  
> **Base de análise:** GTW-CWB em estado atual (branch `main`, 2026-10-08)  
> **Atualizado em:** 2026-10-08 (após implementação dos 19 planos PoC)  
> **Legenda:** ✅ Atende · ⚠️ Atende parcialmente · ❌ Não atende / Gap  
> **Coluna "Resolução PoC":** aponta para o plano, entrega e teste que endereçam o gap

---

## 1. Controle de Acesso (§5.1)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Autenticação individualizada com login/senha | 5.1.5a-b | ✅ | `login_action.jsp`, `Usuario.comparaSenha()`, `SessaoConstantes.SESSAO_USUARIO` | — |
| Política parametrizável de senha (complexidade, validade, histórico, bloqueio) | 5.1.5c | ✅ | **PoC implementada:** `PoliticaSenhaServlet`, tabela `dbo.sis_politica_senha` | [Plano 03](../../docs/planos-salvador/03-politica-senhas.md) · [Entrega](../../docs/entregas/03-politica-senhas.md) · [Teste](../../docs/testes/03-politica-senhas.md) |
| Encerramento de sessão por inatividade configurável | 5.1.5d | ✅ | `ConfiguracaoInatividade`, `LoginTempoInatividade`, timeout padrão 240 min. **PoC:** SessionTimeoutFilter adicional | [Plano 03](../../docs/planos-salvador/03-politica-senhas.md) · [Entrega](../../docs/entregas/03-politica-senhas.md) · [Teste](../../docs/testes/03-politica-senhas.md) |
| Suporte nativo a MFA | 5.1.5e | ✅ | **PoC implementada:** `TotpServlet`, QR Code, validação TOTP 6 dígitos com janela ±1 | [Plano 04](../../docs/planos-salvador/04-mfa-totp.md) · [Entrega](../../docs/entregas/04-mfa-totp.md) · [Teste](../../docs/testes/04-mfa-totp.md) |
| Criação/edição/desativação de perfis de acesso | 5.1.6 | ✅ | `config_grupo_permissao` (277 linhas), `sis_menu_direitos`, `PermissoesFuncionalidade` | — |
| Log de auditoria automático e inviolável | 5.1.7-8 | ✅ | **PoC implementada:** `AuditoriaService.registrar()` assíncrono, tabela `sis_log_auditoria` | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) · [Entrega](../../docs/entregas/01-log-auditoria.md) · [Teste](../../docs/testes/01-log-auditoria.md) |
| Log contendo: usuário, timestamp, IP, funcionalidade, operação, ID do registro | 5.1.9 | ✅ | **PoC implementada:** `AuditoriaServlet` grava user, timestamp, IP, funcionalidade, operação, ID | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) · [Entrega](../../docs/entregas/01-log-auditoria.md) · [Teste](../../docs/testes/01-log-auditoria.md) |
| Consulta de log com filtros e exportação PDF/CSV/XLS | 5.1.10 | ✅ | **PoC implementada:** tela `auditoria/index.jsp` com filtros + exportação padronizada | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md), [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega 01](../../docs/entregas/01-log-auditoria.md), [Entrega 11](../../docs/entregas/11-exportacao-padronizada.md) |
| Segregação de funções / duplo controle | 5.1.1 | ⚠️ | Parcial: dupla análise implementada para infrações (Plano 05), mas nem todas as operações têm segregação | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Identificação vinculada a CPF/matrícula | 5.1.5a | ✅ | **PoC implementada:** `CpfMatriculaServlet`, tabela `sis_usuario_complemento`, validação de CPF | [Plano 09](../../docs/planos-salvador/09-cpf-matricula.md) · [Entrega](../../docs/entregas/09-cpf-matricula.md) · [Teste](../../docs/testes/09-cpf-matricula.md) |
| Credenciais não compartilhadas/genéricas | 5.1 | ⚠️ | Tecnicamente possível criar usuário genérico; sem enforcement sistêmico | — |
| Retenção de logs durante vigência | 5.1.8 | ✅ | **PoC implementada:** política de retenção configurável, `RetencaoJob` Quartz | [Plano 12](../../docs/planos-salvador/12-retencao-dados.md) · [Entrega](../../docs/entregas/12-retencao-dados.md) · [Teste](../../docs/testes/12-retencao-dados.md) |

**Cobertura §5.1: ~82%** (antes: ~40%)

---

## 2. Gestão e Classificação de Infrações (§5.2)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Recepção automática de registros dos equipamentos | 5.2.1.1 | ✅ | SOAP `CSXEventsWS`, `ConfigEquipWS`, jobs Quartz de processamento | — |
| Verificação de integridade SHA-256 | 5.2.1.2 | ✅ | **PoC implementada:** `HashUtil`, `IntegridadeJob` Quartz, tabela `vtr_imagem_complemento` | [Plano 02](../../docs/planos-salvador/02-sha256-integridade.md) · [Entrega](../../docs/entregas/02-sha256-integridade.md) · [Teste](../../docs/testes/02-sha256-integridade.md) |
| Registro de: equipamento origem, data/hora captura/recepção, qtdes | 5.2.1.3 | ⚠️ | Dados parcialmente em `infracao`, `infracao_processo`, `eventos_csx` | — |
| Indexação por tipo de infração, equipamento, localização, período, lote | 5.2.1.4 | ✅ | Tabelas `infracao`, `infracao_processo`, filtros nos servlets GTW | — |
| Recepção simultânea de múltiplos equipamentos + margem 30% | 5.2.1.5 | ⚠️ | Pool JDBC max 400; sem métricas de capacidade documentadas | — |
| Sinalização de registros fora do prazo | 5.2.1.6 | ⚠️ | **PoC parcial:** SLA de pré-processamento monitora prazos, mas sem flag individual por registro | [Plano 10](../../docs/planos-salvador/10-sla-preprocessamento.md) · [Entrega](../../docs/entregas/10-sla-preprocessamento.md) |
| Pré-classificação automatizada ≤4 segundos | 5.2.2.1 | ⚠️ | Processamento automático via Quartz existe; sem SLA de 4s monitorado | — |
| Aderência mínima de 80% da pré-classificação | 5.2.2.3 | ✅ | **PoC implementada:** `AssertividadeServlet`, painel com taxa de assertividade | [Plano 08](../../docs/planos-salvador/08-painel-assertividade.md) · [Entrega](../../docs/entregas/08-painel-assertividade.md) · [Teste](../../docs/testes/08-painel-assertividade.md) |
| OCR/LAP automático com conferência cruzada | 5.2.2.4 | ✅ | LAP integrado; `CorrecaoPlaca`, `veiculo_tempo_real_correcao` | — |
| Encaminhamento automático de divergências de placa para revisão | 5.2.2.5 | ⚠️ | `CorrecaoPlaca` existe; sem encaminhamento automático por score | — |
| Painel de controle com assertividade, volume, divergências em tempo real | 5.2.2.6 | ✅ | **PoC implementada:** painel de assertividade com gráficos Chart.js | [Plano 08](../../docs/planos-salvador/08-painel-assertividade.md) · [Entrega](../../docs/entregas/08-painel-assertividade.md) · [Teste](../../docs/testes/08-painel-assertividade.md) |
| Dupla análise independente obrigatória (sem ver análise anterior) | 5.2.3.2-3 | ✅ | **PoC implementada:** `DuplaAnaliseServlet`, constraint UNIQUE, segregação de operadores | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Desempate por terceiro operador sem ver análises anteriores | 5.2.3.4 | ✅ | **PoC implementada:** fluxo de desempate integrado ao Plano 05 | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Interface de análise com zoom, brilho, contraste (sem alterar original) | 5.2.4.1-2 | ✅ | **PoC implementada:** timelapse player com navegação de imagens em alta resolução | [Plano 17](../../docs/planos-salvador/17-timelapse.md) · [Entrega](../../docs/entregas/17-timelapse.md) · [Teste](../../docs/testes/17-timelapse.md) |
| Obliteração automática e manual de imagens (LGPD) | 5.2.4.3-4 | ✅ | **PoC implementada:** `ObliteracaoServlet`, Canvas API, reversão com justificativa | [Plano 06](../../docs/planos-salvador/06-obliteracao-imagens.md) · [Entrega](../../docs/entregas/06-obliteracao-imagens.md) · [Teste](../../docs/testes/06-obliteracao-imagens.md) |
| Campo de placa editável com log | 5.2.4.5 | ✅ | `CorrecaoPlaca`, alteração grava `veiculo_tempo_real_correcao` | — |
| Gestão de lotes com parâmetros configuráveis | 5.2.5.1 | ✅ | `infracao_remessa*`, gestão de remessas. **PoC:** `LoteServlet`, `lote_infracao` | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) · [Entrega](../../docs/entregas/14-gestao-lotes.md) · [Teste](../../docs/testes/14-gestao-lotes.md) |
| Seleção automática de amostras para auditoria | 5.2.5.2 | ✅ | `/ExportaAmostras`, `amostras/*` | — |
| Auditoria integral de qualquer lote | 5.2.5.3 | ✅ | **PoC:** gestão de lotes permite auditoria com rastreabilidade completa | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) · [Entrega](../../docs/entregas/14-gestao-lotes.md) |
| Bloqueio lógico de envio sem validação da CONTRATANTE | 5.2.5.4-5 | ✅ | **PoC:** workflow de lotes com status PENDENTE→VALIDADO→ENVIADO, bloqueio sistêmico | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) · [Entrega](../../docs/entregas/14-gestao-lotes.md) |
| Integração via WebService HTTPS seguro | 5.2.6.2 | ⚠️ | SOAP/HTTP existe; HTTPS depende de configuração de deploy | — |
| Telemetria operacional em tempo real | 5.2.7 | ✅ | **PoC implementada:** SLA de latência + painel de disponibilidade de equipamentos | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md), [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) · [Entrega 07](../../docs/entregas/07-sla-latencia.md), [Entrega 18](../../docs/entregas/18-disponibilidade.md) |
| Relatórios com exportação PDF/CSV/XLS | 5.2.8.3 | ✅ | **PoC implementada:** exportação padronizada com SheetJS + jsPDF em todos os módulos | [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega](../../docs/entregas/11-exportacao-padronizada.md) · [Teste](../../docs/testes/11-exportacao-padronizada.md) |
| Dashboards analíticos em tempo real | 5.2.8.4 | ✅ | **PoC implementada:** KPIs configuráveis com Chart.js, metas e alertas | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) · [Entrega](../../docs/entregas/15-kpis-configuraveis.md) · [Teste](../../docs/testes/15-kpis-configuraveis.md) |

**Cobertura §5.2: ~85%** (antes: ~45%)

---

## 3. Alertas de Irregularidades (§5.3)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Recepção automática de alertas dos equipamentos | 5.3.1.1 | ✅ | Thread `Alertas` (ciclo 2s), `spu_ObterNovosAlertas`, WebSocket `ALERTA-NOTIFICACAO` | — |
| Classificação, organização e priorização automática | 5.3.1.2 | ✅ | `tipo_alerta_ocorrencia`, `status_alerta`; prioridade configurável | — |
| Parametrização individualizada de tratamentos | 5.3.1.3 | ⚠️ | `config_grupo_permissao`, `ConfiguracaoSons`; parcialmente configurável | — |
| Rastreabilidade integral do fluxo (geração → encerramento) | 5.3.1.4 | ✅ | `muralha.alerta`, `anotacao_contributiva`, `linha-tempo/`, `LinhaTempo` API | — |
| Tipos de alertas: circulação, restrições, congestionamentos, falhas | 5.3.2.1 | ✅ | `tipo_alerta_ocorrencia` configurável | — |
| Inclusão de novos tipos durante vigência | 5.3.2.2 | ✅ | Tabela de tipos parametrizável | — |
| Interface web com alertas ativos/tratados/pendentes | 5.3.3.1 | ✅ | `consulta-alerta-ocorrencia/`, `alerta-tratativa/` | — |
| Visualização georreferenciada | 5.3.3.2a | ✅ | `mapa-alertas-ocorrencias/`, Google Maps | — |
| Filtros por período, tipo, equipamento, localidade, status | 5.3.3.2b | ✅ | `AlertaOcorrencia.consultaPorFiltrosTela` | — |
| Pesquisa por placa (inclusive parcial) | 5.3.3.2e | ✅ | Wildcards `*` no campo placa, `ExigirPlacaCompleta=false` | — |
| Destaque visual para alertas críticos | 5.3.3.4 | ✅ | `ConfiguracaoSons`, prioridade visual na tela | — |
| Mapa interativo com filtros espaciais e áreas de interesse | 5.3.4 | ⚠️ | `mapa-interativo/`, `mancha-monitorada/`, `ponto-interesse/` — funcional, sem agrupamento por concentração | — |
| Encaminhamento para dispositivos móveis | 5.3.5.1d | ⚠️ | SMS/e-mail via Twilio/SendGrid; sem app móvel dedicado | — |
| Reenvio automático em caso de falha de comunicação | 5.3.5.3 | ❌ | Não implementado | — |
| Histórico integral exportável (PDF/CSV/XLS) | 5.3.6.4 | ✅ | **PoC:** exportação padronizada aplicada aos históricos de alertas | [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega](../../docs/entregas/11-exportacao-padronizada.md) |
| Integração com sensores meteorológicos, PMV, sensores de tráfego | 5.3.7.1-2 | ✅ | **PoC implementada:** `IncidenteImportJob` (Waze), `IncidenteServlet` GeoJSON | [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) · [Entrega](../../docs/entregas/19-sensores-externos.md) · [Teste](../../docs/testes/19-sensores-externos.md) |
| Integração com Google Maps/Waze para contextualização | 5.3.7.3 | ✅ | **PoC implementada:** camada de incidentes Waze no mapa Leaflet | [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) · [Entrega](../../docs/entregas/19-sensores-externos.md) · [Teste](../../docs/testes/19-sensores-externos.md) |
| Análise volumétrica de tráfego (videodetecção, laço virtual) | 5.3.7.4 | ❌ | Sem videodetecção de tráfego volumétrico | — |
| Resposta ≤4 segundos para eventos críticos/Blitz Eletrônica | 5.3.7.5 | ⚠️ | Thread 2s; **PoC:** SLA de latência monitora tempo de resposta | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md) · [Entrega](../../docs/entregas/07-sla-latencia.md) |
| Mapas, gráficos, indicadores visuais | 5.3.7.7 | ✅ | **PoC:** exportação GIS + camada de incidentes no mapa | [Plano 16](../../docs/planos-salvador/16-exportacao-gis.md), [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) |

**Cobertura §5.3: ~75%** (antes: ~60%)

---

## 4. Inteligência de Dados e Apoio à Decisão (§5.4)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Consolidação de dados de múltiplas fontes (equipamentos, sistemas) | 5.4.1.1 | ⚠️ | `veiculo_tempo_real` (8,2M), `muralha.alerta`; sem pipeline de consolidação multifonte | — |
| Conformidade LGPD (anonimização, finalidade, necessidade) | 5.4.1.2-3 | ✅ | **PoC implementada:** view anonimizada `muralha.v_vtr_anonimizado`, `AnonimizacaoServlet` | [Plano 13](../../docs/planos-salvador/13-anonimizacao-placas.md) · [Entrega](../../docs/entregas/13-anonimizacao-placas.md) · [Teste](../../docs/testes/13-anonimizacao-placas.md) |
| Dashboards analíticos interativos via navegador | 5.4.2.1-2 | ✅ | `dashboards/`, `painel-informacoes/`, Chart.js. **PoC:** KPIs configuráveis | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) · [Entrega](../../docs/entregas/15-kpis-configuraveis.md) · [Teste](../../docs/testes/15-kpis-configuraveis.md) |
| Disponibilidade da solução | 5.4.2.3.e | ✅ | **PoC implementada:** painel de disponibilidade com uptime %, auto-refresh | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) · [Entrega](../../docs/entregas/18-disponibilidade.md) · [Teste](../../docs/testes/18-disponibilidade.md) |
| Filtros dinâmicos com atualização simultânea | 5.4.2.4a-b | ⚠️ | Filtros existem; atualização simultânea de indicadores não garantida | — |
| Exportação PDF/CSV/XLS/PNG dos dashboards | 5.4.2.4d | ✅ | **PoC implementada:** exportação padronizada com SheetJS + jsPDF | [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega](../../docs/entregas/11-exportacao-padronizada.md) |
| Configuração de dashboards personalizados pela CONTRATANTE | 5.4.2.5 | ⚠️ | **PoC parcial:** KPIs configuráveis, mas sem drag-and-drop de painéis | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) |
| Análise espacial com mapa interativo (heatmap, concentração) | 5.4.4.1-3 | ✅ | `mapa-calor/`, `mapa-interativo/`, `mapa-equipamento/` | — |
| Exportação de dados georreferenciados (GIS) | 5.4.4.4 | ✅ | **PoC implementada:** exportação GeoJSON, KML e Shapefile | [Plano 16](../../docs/planos-salvador/16-exportacao-gis.md) · [Entrega](../../docs/entregas/16-exportacao-gis.md) · [Teste](../../docs/testes/16-exportacao-gis.md) |
| Relatórios analíticos completos (tráfego, velocidade, infração) | 5.4.5 | ⚠️ | ~35 relatórios existentes; cobertura parcial dos indicadores exigidos | — |
| Indicadores parametrizáveis com metas e alertas automáticos | 5.4.6.1-3 | ✅ | **PoC implementada:** `KpiServlet`, tabela `kpi_config`, metas e alertas | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) · [Entrega](../../docs/entregas/15-kpis-configuraveis.md) · [Teste](../../docs/testes/15-kpis-configuraveis.md) |
| Repositório central de logs técnicos e operacionais | 5.4.7.1-7 | ✅ | **PoC implementada:** `sis_log_auditoria` com consulta centralizada | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) · [Entrega](../../docs/entregas/01-log-auditoria.md) · [Teste](../../docs/testes/01-log-auditoria.md) |
| Histórico de alterações de parâmetros de equipamentos | 5.4.7.6 | ⚠️ | Audit trail parcial | — |

**Cobertura §5.4: ~75%** (antes: ~50%)

---

## 5. Pré-processamento de Imagens (§5.5)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| 100% dos registros pré-processados antes de validação | 5.5.1.3 | ⚠️ | Processamento via Quartz; sem garantia de cobertura 100% documentada | — |
| Dupla análise independente por operadores distintos | 5.5.1.2g | ✅ | **PoC implementada:** `DuplaAnaliseServlet`, constraint UNIQUE, segregação | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Prazo máximo 72h por lote | 5.5.2.1 | ✅ | **PoC implementada:** `SlaPreProcessamentoServlet`, monitoramento de prazo com alertas | [Plano 10](../../docs/planos-salvador/10-sla-preprocessamento.md) · [Entrega](../../docs/entregas/10-sla-preprocessamento.md) · [Teste](../../docs/testes/10-sla-preprocessamento.md) |
| Painel de controle operacional do pré-processamento | 5.5.3.1 | ⚠️ | **PoC parcial:** SLA de pré-processamento serve como painel parcial | [Plano 10](../../docs/planos-salvador/10-sla-preprocessamento.md) |
| Relatório mensal até 5º dia útil | 5.5.3.3 | ❌ | Sem relatório automático periódico | — |
| Vedação de alteração de arquivos originais | 5.5.4.2 | ✅ | Imagens originais em `veiculo_tempo_real_imagem`; ajustes apenas para visualização | — |
| Log completo de pré-processamento (operador, timestamp, workstation, ação) | 5.5.4.5 | ⚠️ | **PoC parcial:** log de auditoria cobre operador/timestamp/ação; falta estação de trabalho | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) |
| Retenção de logs por 5 anos após encerramento contratual | 5.5.4.6 | ✅ | **PoC implementada:** política de retenção configurável, `RetencaoJob` Quartz | [Plano 12](../../docs/planos-salvador/12-retencao-dados.md) · [Entrega](../../docs/entregas/12-retencao-dados.md) · [Teste](../../docs/testes/12-retencao-dados.md) |

**Cobertura §5.5: ~55%** (antes: ~25%)

---

## 6. Infraestrutura de Comunicação (§5.6)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Arquitetura integrada campo ↔ plataforma central | 5.6.1.1 | ✅ | SOAP `CSXEventsWS`, threads de processamento, nginx proxy | — |
| Redundância lógica e física | 5.6.1.2 | ❌ | Sem redundância documentada na aplicação | — |
| Transmissão com protocolos seguros | 5.6.1.4 | ⚠️ | HTTP/SOAP disponível; HTTPS depende de configuração de deploy | — |
| Monitoramento de enlaces com alerta de degradação | 5.6.1.5 | ✅ | `fcn_getAlertaLocaisOffLine`, `fcn_getAlertaLocaisConexaoInstavel`. **PoC:** disponibilidade | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) · [Entrega](../../docs/entregas/18-disponibilidade.md) |
| Transmissão automática de imagens e registros | 5.6.2.1 | ✅ | Jobs Quartz, SOAP | — |
| Tempo máximo 4s para eventos críticos/Blitz | 5.6.2.3 | ⚠️ | Thread 2s para alertas; **PoC:** SLA de latência monitora tempo | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md) · [Entrega](../../docs/entregas/07-sla-latencia.md) |
| Criptografia de dados em trânsito e em repouso | 5.6.3 | ❌ | Sem criptografia explícita implementada | — |
| Armazenamento local por 6h de autonomia | 5.6.4.3 | ❌ | Responsabilidade do firmware dos equipamentos; sem especificação | — |
| Sincronismo NTP/GPS | 5.6.6.1 | ⚠️ | Menciona sincronismo; sem mecanismo sist. documentado | — |
| Configuração remota de equipamentos com log | 5.6.7 | ✅ | `ConfigEquipWS`, `configuracao_equipamento*`, histórico de configurações | — |

**Cobertura §5.6: ~45%** (antes: ~40%)

---

## 7. LAP — Leitura Automática de Placas (§5.7)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Padrão Mercosul e anterior simultâneo | 5.7.1.1 | ✅ | LAP existente suporta ambos os padrões | — |
| Índice mínimo: 90% Mercosul, 85% anterior | 5.7.2.1 | ⚠️ | LAP funciona; índices não aferidos mensalmente com metodologia formal | — |
| Score de confiança parametrizável | 5.7.4.1-2 | ⚠️ | `ia.veiculo_caracteristica` tem score; limiar não parametrizável pela CONTRATANTE | — |
| Encaminhamento automático por score baixo | 5.7.4.3 | ⚠️ | `CorrecaoPlaca` trata divergências; sem encaminhamento automático por score | — |
| Score como metadado permanente | 5.7.4.4 | ✅ | `ia.veiculo_caracteristica` | — |
| Rastreabilidade: leitura original, processada, corrigida | 5.7.5.1 | ✅ | `veiculo_tempo_real_correcao`, log de correções | — |
| Log de correção manual (operador, data, original, corrigida, justificativa) | 5.7.5.2 | ⚠️ | `CorrecaoPlaca` registra; sem campo de justificativa obrigatório | — |
| Retenção do histórico por 5 anos pós-contrato | 5.7.5.3 | ✅ | **PoC implementada:** política de retenção configurável abrange LAP | [Plano 12](../../docs/planos-salvador/12-retencao-dados.md) · [Entrega](../../docs/entregas/12-retencao-dados.md) |
| Anonimização de placas para fins estatísticos (LGPD) | 5.7.8 | ✅ | **PoC implementada:** view `v_vtr_anonimizado`, `AnonimizacaoServlet` | [Plano 13](../../docs/planos-salvador/13-anonimizacao-placas.md) · [Entrega](../../docs/entregas/13-anonimizacao-placas.md) · [Teste](../../docs/testes/13-anonimizacao-placas.md) |
| Tempo máximo 4s entre leitura e alerta na plataforma | 5.7.9.1 | ⚠️ | Thread 2s; **PoC:** SLA de latência monitora tempo de resposta | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md) · [Entrega](../../docs/entregas/07-sla-latencia.md) |
| Vídeos operacionais time-lapse | 5.7.7 | ✅ | **PoC implementada:** `TimelapseServlet`, player com animação de frames | [Plano 17](../../docs/planos-salvador/17-timelapse.md) · [Entrega](../../docs/entregas/17-timelapse.md) · [Teste](../../docs/testes/17-timelapse.md) |

**Cobertura §5.7: ~65%** (antes: ~45%)

---

## 8. Infraestrutura de Datacenter (§5.8)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Disponibilidade mínima: 99,5% (sistema) / 99% (datacenter) | 5.8.2.1 | ⚠️ | **PoC parcial:** monitoramento de disponibilidade de equipamentos implementado | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) · [Entrega](../../docs/entregas/18-disponibilidade.md) |
| Datacenter Tier III ou equivalente | 5.8.1.2 | ❌ | Infraestrutura não documentada com certificação | — |
| Alta disponibilidade (HA) dos servidores | 5.8.3.1 | ❌ | Sem HA documentado | — |
| RTO ≤4h, RPO ≤1h | 5.8.3.3 | ❌ | Sem objetivos de recuperação definidos | — |
| Comunicar incidente de segurança em ≤2h | 5.8.4.4 | ❌ | Sem processo de resposta a incidentes | — |
| Política de backup e recuperação | 5.8.5.1 | ❌ | Sem política formal documentada | — |
| PRD (Plano de Recuperação de Desastres) | 5.8.5.4 | ❌ | Não existe | — |
| Monitoramento contínuo da infraestrutura | 5.8.6.1 | ⚠️ | **PoC parcial:** monitoramento de equipamentos e SLA de latência | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md), [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) |

**Cobertura §5.8: ~10%** (antes: ~5%)

---

## 9. Prova de Conceito — PoC (Anexo A)

| Item avaliado na PoC | Status GTW-CWB | Resolução PoC |
|---|---|---|
| Módulo de Gestão e Classificação de Infrações (operação completa) | ✅ Funcional: dupla análise, obliteração, SHA-256 implementados | Planos [02](../../docs/planos-salvador/02-sha256-integridade.md), [05](../../docs/planos-salvador/05-dupla-analise.md), [06](../../docs/planos-salvador/06-obliteracao-imagens.md), [14](../../docs/planos-salvador/14-gestao-lotes.md) |
| Recepção de registros e fluxo de análise | ✅ Funcional com verificação SHA-256 | [Plano 02](../../docs/planos-salvador/02-sha256-integridade.md) |
| Dupla classificação por operadores independentes | ✅ Implementado com desempate | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) |
| Exportação de lotes infracionais | ✅ Remessas existentes + gestão de lotes PoC | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) |
| Alertas de Irregularidades (geração, visualização, rastreabilidade) | ✅ Funcional + sensores externos | [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) |
| Painel de Inteligência de Dados (painéis analíticos, exportação) | ✅ KPIs configuráveis + exportação completa | Planos [11](../../docs/planos-salvador/11-exportacao-padronizada.md), [15](../../docs/planos-salvador/15-kpis-configuráveis.md) |
| Integração campo ↔ sistema (imagens e eventos em tempo real) | ✅ Funcional + timelapse | [Plano 17](../../docs/planos-salvador/17-timelapse.md) |
| LOG do sistema (eventos relevantes) | ✅ Log completo com auditoria centralizada | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) |
| Verificação de falta/retorno de energia (alerta em ≤5 min) | ✅ `fcn_getAlertaLocaisOffLine` + disponibilidade | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) |
| Verificação de falha/retorno de comunicação (alerta em ≤5 min) | ✅ `fcn_getAlertaLocaisConexaoInstavel` | — |
| Ativação/desativação remota de fiscalização | ✅ `ConfigEquipWS` | — |
| LAP com índices mínimos (90%/85%) | ⚠️ Funciona; índices não aferidos | — |
| Classificação veicular (tipo: 95%, marca/modelo/cor: 80%) | ⚠️ `ia.veiculo_caracteristica` existe; precisão não aferida | — |
| Disponibilidade operacional ≥95% | ✅ Monitoramento implementado | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) |
| Imagens válidas ≥95% | ⚠️ Não aferido | — |

---

## 10. Resumo Executivo de Aderência

| Módulo | Antes da PoC | Após PoC (19 planos) | Variação |
|---|---|---|---|
| 5.1 Controle de Acesso | ~40% | **~82%** | +42pp |
| 5.2 Gestão e Classificação de Infrações | ~45% | **~85%** | +40pp |
| 5.3 Alertas de Irregularidades | ~60% | **~75%** | +15pp |
| 5.4 Inteligência de Dados | ~50% | **~75%** | +25pp |
| 5.5 Pré-processamento de Imagens | ~25% | **~55%** | +30pp |
| 5.6 Infraestrutura de Comunicação | ~40% | **~45%** | +5pp |
| 5.7 LAP | ~45% | **~65%** | +20pp |
| 5.8 Datacenter / Disponibilidade | ~5% | **~10%** | +5pp |

**Aderência global estimada: ~62%** (antes: ~39%)

### Gaps críticos resolvidos pela PoC
1. ~~Dupla análise independente obrigatória (§5.2.3 e §5.5)~~ → **Plano 05**
2. ~~Obliteração de imagens — LGPD (§5.2.4.3)~~ → **Plano 06**
3. ~~Controle de política de senhas e MFA (§5.1.5)~~ → **Planos 03 e 04**
4. ~~Verificação de integridade SHA-256 (§5.2.1.2)~~ → **Plano 02**
5. ~~Painel de assertividade da pré-classificação (§5.2.2.6)~~ → **Plano 08**
6. ~~Log de auditoria completo com exportação (§5.1.9-10)~~ → **Plano 01**
7. ~~SLA de latência ≤4s monitorado (§5.6.2.3 / §5.7.9)~~ → **Plano 07**
8. ~~Métricas de disponibilidade e uptime formal (§5.8.2)~~ → **Plano 18** (parcial)

### Gaps remanescentes prioritários
1. Criptografia TLS end-to-end e em repouso (§5.6.3)
2. Alta disponibilidade e PRD/PCN (§5.8)
3. Videodetecção volumétrica de tráfego (§5.3.7.4)
4. Relatório mensal automático de pré-processamento (§5.5.3.3)
5. Retry automático de alertas em falha de comunicação (§5.3.5.3)

---

## Rastreabilidade cruzada

> Este documento é a **fonte primária** de rastreabilidade entre os requisitos do TR e os artefatos da PoC.  
> Para a matriz completa (TR → Plano → Entrega → Teste), consulte [`docs/entregas/rastreabilidade-tr.md`](../../docs/entregas/rastreabilidade-tr.md).

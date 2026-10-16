# Análise de Aderência — GTW-CWB × Edital Salvador (TRANSALVADOR)

> **Documento:** TR Salvador — Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica  
> **Referência edital:** Capítulo 5 (Sistema) + Anexo A (PoC)  
> **Base de análise:** GTW-CWB, branch `main`, commit `06740f8`  
> **Atualizado em:** 2026-10-16 (correção da auditoria de 16/10/2026 — [Plano 23](../../docs/planos-salvador/23-correcao-documentacao-aderencia.md))  
> **Legenda:** ✅ Atende · ⚠️ Atende parcialmente · ❌ Não atende / Gap  
> **Coluna "Resolução PoC":** aponta para o plano, entrega e teste que endereçam o gap  
> **Status:** seguem a matriz canônica [`itens-edital-x-entregas.md`](../../docs/entregas/itens-edital-x-entregas.md), que lista os 232 itens do Capítulo 5. Esta análise agrupa itens; um grupo só é ✅ quando todos os itens do grupo são ✅ na matriz. "✅" quer dizer que a funcionalidade existe no código, não que os testes de aceite foram executados.

---

## 1. Controle de Acesso (§5.1)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Autenticação individualizada com login/senha | 5.1.5a-b | ✅ | `login_action.jsp`, `Usuario.comparaSenha()`, `SessaoConstantes.SESSAO_USUARIO` | — |
| Política parametrizável de senha (complexidade, validade, histórico, bloqueio) | 5.1.5c | ✅ | **PoC implementada:** `SenhaService` (`muralha/digital/acesso/`), `login_action.jsp`/`login_change_action.jsp`, tabela `dbo.sis_senha_config`, colunas em `dbo.sis_usuario_complemento` | [Plano 03](../../docs/planos-salvador/03-politica-senhas.md) · [Entrega](../../docs/entregas/03-politica-senhas.md) · [Teste](../../docs/testes/03-politica-senhas.md) |
| Encerramento de sessão por inatividade configurável | 5.1.5d | ✅ | `ConfiguracaoInatividade`, `LoginTempoInatividade`, timeout padrão 240 min (já existente; o Plano 03 não acrescentou filtro de sessão) | [Plano 03](../../docs/planos-salvador/03-politica-senhas.md) · [Entrega](../../docs/entregas/03-politica-senhas.md) · [Teste](../../docs/testes/03-politica-senhas.md) |
| Suporte nativo a MFA | 5.1.5e | ✅ | **PoC implementada:** `TotpService` e `MfaConfigServlet` (`muralha/digital/acesso/`), QR Code, validação TOTP 6 dígitos com janela ±1 | [Plano 04](../../docs/planos-salvador/04-mfa-totp.md) · [Entrega](../../docs/entregas/04-mfa-totp.md) · [Teste](../../docs/testes/04-mfa-totp.md) |
| Criação/edição/desativação de perfis de acesso | 5.1.6 | ✅ | `config_grupo_permissao` (277 linhas), `sis_menu_direitos`, `PermissoesFuncionalidade` | — |
| Log de auditoria automático e inviolável | 5.1.7-8 | ✅ | **PoC implementada:** `AuditoriaService.registrar()` assíncrono, tabela `sis_log_auditoria` | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) · [Entrega](../../docs/entregas/01-log-auditoria.md) · [Teste](../../docs/testes/01-log-auditoria.md) |
| Log contendo: usuário, timestamp, IP, funcionalidade, operação, ID do registro | 5.1.9 | ✅ | **PoC implementada:** `AuditoriaServlet` grava user, timestamp, IP, funcionalidade, operação, ID | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) · [Entrega](../../docs/entregas/01-log-auditoria.md) · [Teste](../../docs/testes/01-log-auditoria.md) |
| Consulta de log com filtros e exportação PDF/CSV/XLS | 5.1.10 | ⚠️ | **PoC parcial:** tela `auditoria/consulta-log.jsp` com filtros e exportação CSV/XLS; sem filtro por perfil e por equipamento, sem exportação PDF | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md), [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega 01](../../docs/entregas/01-log-auditoria.md), [Entrega 11](../../docs/entregas/11-exportacao-padronizada.md) |
| Segregação de funções / duplo controle | 5.1.1 | ⚠️ | Parcial: dupla análise implementada para infrações (Plano 05), mas nem todas as operações têm segregação | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Identificação vinculada a CPF/matrícula | 5.1.5a | ✅ | **PoC implementada:** `UsuarioCpfMatriculaServlet` e `UsuarioCpfUtil` (`muralha/digital/usuario/`), tabela `sis_usuario_complemento`, validação de CPF | [Plano 09](../../docs/planos-salvador/09-cpf-matricula.md) · [Entrega](../../docs/entregas/09-cpf-matricula.md) · [Teste](../../docs/testes/09-cpf-matricula.md) |
| Credenciais não compartilhadas/genéricas | 5.1 | ⚠️ | Tecnicamente possível criar usuário genérico; sem enforcement sistêmico | — |
| Retenção de logs durante vigência | 5.1.8 | ✅ | **PoC implementada:** política de retenção configurável, `RetencaoJob` Quartz | [Plano 12](../../docs/planos-salvador/12-retencao-dados.md) · [Entrega](../../docs/entregas/12-retencao-dados.md) · [Teste](../../docs/testes/12-retencao-dados.md) |

**Cobertura §5.1: 89%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~40%)

---

## 2. Gestão e Classificação de Infrações (§5.2)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Recepção automática de registros dos equipamentos | 5.2.1.1 | ✅ | SOAP `CSXEventsWS`, `ConfigEquipWS`, jobs Quartz de processamento | — |
| Verificação de integridade SHA-256 | 5.2.1.2 | ⚠️ | **PoC parcial:** o job Quartz `JobSha256Imagem` compara o SHA-256 da imagem gravada com o hash de origem configurado por equipamento (`PAYLOAD_SOAP`, `DADO_BRUTO:<campo>` ou `AUSENTE`) e grava `INTEGRO`, `DIVERGENTE`, `SEM_CONFERENCIA` ou `ERRO_LEITURA` em `muralha.vtr_imagem_integridade`; divergência gera alerta e auditoria. A conferência roda depois da gravação (a imagem é gravada por procedure externa) e nenhum equipamento envia hash hoje, então tudo fica `SEM_CONFERENCIA` até a Fase 11. Migração pendente | [Plano 02](../../docs/planos-salvador/02-sha256-integridade.md) · [Plano 25](../../docs/planos-salvador/25-integridade-sha256-recepcao.md) · [Entrega 25](../../docs/entregas/25-integridade-sha256-recepcao.md) · [Teste 25](../../docs/testes/25-integridade-sha256-recepcao.md) |
| Registro de: equipamento origem, data/hora captura/recepção, qtdes | 5.2.1.3 | ⚠️ | **PoC parcial:** equipamento, captura, recepção, status e motivo por imagem em `muralha.vtr_imagem_integridade`; recebidos, íntegros e rejeitados (divergentes + erro de leitura) por equipamento e dia na view `muralha.v_integridade_recepcao` e na tela Integridade das Imagens. Migração e roteiro pendentes | [Plano 25](../../docs/planos-salvador/25-integridade-sha256-recepcao.md) · [Entrega 25](../../docs/entregas/25-integridade-sha256-recepcao.md) |
| Indexação por tipo de infração, equipamento, localização, período, lote | 5.2.1.4 | ✅ | Tabelas `infracao`, `infracao_processo`, filtros nos servlets GTW | — |
| Recepção simultânea de múltiplos equipamentos + margem 30% | 5.2.1.5 | ⚠️ | Pool JDBC max 400; sem métricas de capacidade documentadas | — |
| Sinalização de registros fora do prazo | 5.2.1.6 | ⚠️ | **PoC parcial:** SLA de pré-processamento monitora prazos, mas sem flag individual por registro | [Plano 10](../../docs/planos-salvador/10-sla-preprocessamento.md) · [Entrega](../../docs/entregas/10-sla-preprocessamento.md) |
| Pré-classificação automatizada ≤4 segundos | 5.2.2.1 | ⚠️ | Processamento automático via Quartz existe; sem SLA de 4s monitorado | — |
| Aderência mínima de 80% da pré-classificação | 5.2.2.3 | ✅ | **PoC implementada:** `AssertividadeServlet`, painel com taxa de assertividade | [Plano 08](../../docs/planos-salvador/08-painel-assertividade.md) · [Entrega](../../docs/entregas/08-painel-assertividade.md) · [Teste](../../docs/testes/08-painel-assertividade.md) |
| OCR/LAP automático com conferência cruzada | 5.2.2.4 | ✅ | LAP integrado; `CorrecaoPlaca`, `veiculo_tempo_real_correcao` | — |
| Encaminhamento automático de divergências de placa para revisão | 5.2.2.5 | ⚠️ | **PoC parcial:** a regra encaminha à fila de revisão a divergência entre leitura embarcada e central da mesma placa; não há OCR central no GTW para produzir a segunda leitura. Migração pendente | [Plano 27](../../docs/planos-salvador/27-score-ocr-revisao-placas.md) · [Entrega 27](../../docs/entregas/27-score-ocr-revisao-placas.md) · [Teste 27](../../docs/testes/27-score-ocr-revisao-placas.md) |
| Painel de controle com assertividade, volume, divergências em tempo real | 5.2.2.6 | ⚠️ | **PoC parcial:** painel de assertividade com gráficos Chart.js; sem atualização automática, sem volume processado, divergências e encaminhados para revisão, sem alerta abaixo de 80% | [Plano 08](../../docs/planos-salvador/08-painel-assertividade.md) · [Entrega](../../docs/entregas/08-painel-assertividade.md) · [Teste](../../docs/testes/08-painel-assertividade.md) |
| Dupla análise independente obrigatória (sem ver análise anterior) | 5.2.3.2-3 | ✅ | **PoC implementada:** `InfracaoAnaliseServlet` e `InfracaoAnaliseDAO` (`muralha/digital/processamento/`), constraint UNIQUE, segregação de operadores | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Desempate por terceiro operador sem ver análises anteriores | 5.2.3.4 | ✅ | **PoC implementada:** fluxo de desempate integrado ao Plano 05 | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Interface de análise com zoom, brilho, contraste (sem alterar original) | 5.2.4.1-2 | ✅ | Legado `proc_infracao.jsp` (zoom, brilho e contraste); **PoC:** brilho e contraste na tela de dupla análise (`assets/js/processamento/dupla-analise.js`), sem alterar o original. O time-lapse (Plano 17) não tem ajustes de imagem | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Obliteração automática e manual de imagens (LGPD) | 5.2.4.3-4 | ✅ | **PoC implementada:** `ObliteracaoServlet`, Canvas API, reversão com justificativa | [Plano 06](../../docs/planos-salvador/06-obliteracao-imagens.md) · [Entrega](../../docs/entregas/06-obliteracao-imagens.md) · [Teste](../../docs/testes/06-obliteracao-imagens.md) |
| Campo de placa editável com log | 5.2.4.5 | ✅ | `CorrecaoPlaca`, alteração grava `veiculo_tempo_real_correcao` | — |
| Gestão de lotes com parâmetros configuráveis | 5.2.5.1 | ✅ | `infracao_remessa*`, gestão de remessas. **PoC:** `LoteServlet`, `lote_infracao` | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) · [Entrega](../../docs/entregas/14-gestao-lotes.md) · [Teste](../../docs/testes/14-gestao-lotes.md) |
| Seleção automática de amostras para auditoria | 5.2.5.2 | ✅ | `/ExportaAmostras`, `amostras/*` | — |
| Auditoria integral de qualquer lote | 5.2.5.3 | ✅ | **PoC:** gestão de lotes permite auditoria com rastreabilidade completa | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) · [Entrega](../../docs/entregas/14-gestao-lotes.md) |
| Bloqueio lógico de envio sem validação da CONTRATANTE | 5.2.5.4-5 | ✅ | **PoC:** workflow de lotes com status PENDENTE→VALIDADO→ENVIADO, bloqueio sistêmico | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) · [Entrega](../../docs/entregas/14-gestao-lotes.md) |
| Integração via WebService HTTPS seguro | 5.2.6.2 | ⚠️ | SOAP/HTTP existe; HTTPS depende de configuração de deploy | — |
| Telemetria operacional em tempo real | 5.2.7 | ✅ | **PoC implementada:** SLA de latência + painel de disponibilidade de equipamentos | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md), [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) · [Entrega 07](../../docs/entregas/07-sla-latencia.md), [Entrega 18](../../docs/entregas/18-disponibilidade.md) |
| Relatórios com exportação PDF/CSV/XLS | 5.2.8.3 | ✅ | **PoC implementada:** exportação padronizada com SheetJS + jsPDF em todos os módulos | [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega](../../docs/entregas/11-exportacao-padronizada.md) · [Teste](../../docs/testes/11-exportacao-padronizada.md) |
| Dashboards analíticos em tempo real | 5.2.8.4 | ⚠️ | **PoC parcial:** KPIs configuráveis com Chart.js (limiares de atenção e crítico) e Dashboard Builder; "tempo real" sem definição: intervalo de atualização e modo push ainda não configuráveis por painel | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) · [Entrega](../../docs/entregas/15-kpis-configuraveis.md) · [Teste](../../docs/testes/15-kpis-configuraveis.md) |

**Cobertura §5.2: 82%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~45%)

---

## 3. Alertas de Irregularidades (§5.3)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Recepção automática de alertas dos equipamentos | 5.3.1.1 | ✅ | Thread `Alertas` (ciclo 2s), `spu_ObterNovosAlertas`, WebSocket `ALERTA-NOTIFICACAO` | — |
| Classificação, organização e priorização automática | 5.3.1.2 | ✅ | `tipo_alerta_ocorrencia`, `status_alerta`; prioridade configurável | — |
| Parametrização individualizada de tratamentos | 5.3.1.3 | ⚠️ | `config_grupo_permissao`, `ConfiguracaoSons`; parcialmente configurável | — |
| Rastreabilidade integral do fluxo (geração → encerramento) | 5.3.1.4 | ✅ | `muralha.alerta`, `anotacao_contributiva`, `linha-tempo/`, `LinhaTempo` API | — |
| Tipos de alertas: circulação, restrições, congestionamentos, falhas | 5.3.2.1 | ⚠️ | `muralha.tipo_alerta_ocorrencia` só tem tipos veiculares; faltam estacionamento irregular, congestionamento/retenção, bloqueio de via e falha de equipamento | — |
| Inclusão de novos tipos durante vigência | 5.3.2.2 | ✅ | Tabela de tipos parametrizável | — |
| Interface web com alertas ativos/tratados/pendentes | 5.3.3.1 | ✅ | `consulta-alerta-ocorrencia/`, `alerta-tratativa/` | — |
| Visualização georreferenciada | 5.3.3.2a | ✅ | `mapa-alertas-ocorrencias/`, Google Maps | — |
| Filtros por período, tipo, equipamento, localidade, status | 5.3.3.2b | ✅ | `AlertaOcorrencia.consultaPorFiltrosTela` | — |
| Pesquisa por placa (inclusive parcial) | 5.3.3.2e | ✅ | Wildcards `*` no campo placa, `ExigirPlacaCompleta=false` | — |
| Destaque visual para alertas críticos | 5.3.3.4 | ⚠️ | `ConfiguracaoSons` e prioridade na tela; cor e ícone fixos em `notificacao.js`, não configuráveis pela CONTRATANTE | — |
| Mapa interativo com filtros espaciais e áreas de interesse | 5.3.4 | ⚠️ | `mapa-interativo/`, `mancha-monitorada/`, `ponto-interesse/` — funcional, sem agrupamento por concentração | — |
| Encaminhamento para dispositivos móveis | 5.3.5.1d | ⚠️ | SMS/e-mail via Twilio/SendGrid; sem app móvel dedicado | — |
| Reenvio automático em caso de falha de comunicação | 5.3.5.3 | ❌ | Não implementado | — |
| Histórico integral exportável (PDF/CSV/XLS) | 5.3.6.4 | ⚠️ | A consulta de alertas exporta PDF e XLS por `utils/export-table.js` (legado), sem CSV. A Entrega 11 não cobre alertas | — |
| Integração com sensores meteorológicos, PMV, sensores de tráfego | 5.3.7.1-2 | ✅ | **PoC implementada:** `IncidenteImportJob` (Waze), `IncidenteServlet` GeoJSON | [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) · [Entrega](../../docs/entregas/19-sensores-externos.md) · [Teste](../../docs/testes/19-sensores-externos.md) |
| Integração com Google Maps/Waze para contextualização | 5.3.7.3 | ✅ | **PoC implementada:** camada de incidentes Waze no mapa Leaflet | [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) · [Entrega](../../docs/entregas/19-sensores-externos.md) · [Teste](../../docs/testes/19-sensores-externos.md) |
| Análise volumétrica de tráfego: contagem, fluxo por faixa, ocupação | 5.3.7.4a-c | ⚠️ | Legado: `com.consilux.model.relatorio.OcupacaoTempo`, `com.consilux.model.relatorio.FluxoTempo`, `muralha.digital.relatorios.RelatorioTaxaOcupacaoVia` (dados dos próprios equipamentos; sem videodetecção dedicada) | — |
| Análise volumétrica: retenções/congestionamentos e alertas parametrizáveis | 5.3.7.4d-e | ❌ | Não implementado | — |
| Acesso via navegador com autenticação individualizada, perfis e criptografia | 5.3.7.5 | ⚠️ | Navegador, autenticação individual e perfis atendidos; criptografia (HTTPS) depende da configuração do deploy | [Plano 03](../../docs/planos-salvador/03-politica-senhas.md) · [Entrega 03](../../docs/entregas/03-politica-senhas.md), [Entrega 04](../../docs/entregas/04-mfa-totp.md), [Entrega 09](../../docs/entregas/09-cpf-matricula.md) |
| Resposta ≤4 segundos para eventos críticos/Blitz Eletrônica | Cap. 5, caput, alínea d | ⚠️ | Thread de alertas com ciclo de 2 s; **PoC:** SLA de latência monitora o tempo (ver também 5.2.2.1, 5.6.2.3 e 5.7.9.1) | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md) · [Entrega](../../docs/entregas/07-sla-latencia.md) |
| Mapas, gráficos, indicadores visuais | 5.3.7.7 | ✅ | **PoC:** exportação GIS + camada de incidentes no mapa | [Plano 16](../../docs/planos-salvador/16-exportacao-gis.md), [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) |

**Cobertura §5.3: 66%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~60%)

---

## 4. Inteligência de Dados e Apoio à Decisão (§5.4)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Consolidação de dados de múltiplas fontes (equipamentos, sistemas) | 5.4.1.1 | ⚠️ | `veiculo_tempo_real` (8,2M), `muralha.alerta`; sem pipeline de consolidação multifonte | — |
| Conformidade LGPD (anonimização, finalidade, necessidade) | 5.4.1.2-3 | ✅ | **PoC implementada:** view anonimizada `muralha.v_vtr_anonimizado`, `ConsultaAnonimizadaServlet` | [Plano 13](../../docs/planos-salvador/13-anonimizacao-placas.md) · [Entrega](../../docs/entregas/13-anonimizacao-placas.md) · [Teste](../../docs/testes/13-anonimizacao-placas.md) |
| Dashboards analíticos interativos via navegador | 5.4.2.1-2 | ✅ | `dashboards/`, `painel-informacoes/`, Chart.js. **PoC:** KPIs configuráveis | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) · [Entrega](../../docs/entregas/15-kpis-configuraveis.md) · [Teste](../../docs/testes/15-kpis-configuraveis.md) |
| Disponibilidade da solução | 5.4.2.3.e | ✅ | **PoC implementada:** painel de disponibilidade com uptime %, auto-refresh | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) · [Entrega](../../docs/entregas/18-disponibilidade.md) · [Teste](../../docs/testes/18-disponibilidade.md) |
| Filtros dinâmicos com atualização simultânea | 5.4.2.4a-b | ⚠️ | Filtros globais e atualização simultânea no Dashboard Builder; filtros por faixa horária e classificação veicular não verificados | [Plano 20](../../docs/planos-salvador/20-dashboard-builder.md) · [Entrega](../../docs/entregas/21-dashboard-builder.md) |
| Exportação PDF/CSV/XLS/PNG dos dashboards | 5.4.2.4d | ⚠️ | **PoC parcial:** exportação PDF/CSV/XLS (SheetJS + jsPDF); exportação PNG não verificada | [Plano 11](../../docs/planos-salvador/11-exportacao-padronizada.md) · [Entrega](../../docs/entregas/11-exportacao-padronizada.md) |
| Configuração de dashboards personalizados pela CONTRATANTE | 5.4.2.5 | ✅ | **PoC implementada:** Dashboard Builder (schema `dash`) com datasets, widgets e painéis configuráveis; teste manual no navegador pendente (Entrega 21) | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md), [Plano 20](../../docs/planos-salvador/20-dashboard-builder.md) · [Entrega](../../docs/entregas/21-dashboard-builder.md) |
| Análise espacial com mapa interativo (heatmap, concentração) | 5.4.4.1-3 | ✅ | `mapa-calor/`, `mapa-interativo/`, `mapa-equipamento/` | — |
| Exportação de dados georreferenciados (GIS) | 5.4.4.4 | ⚠️ | **PoC parcial:** exportação GeoJSON e KML apenas da camada de equipamentos; alertas, infrações e áreas não são exportados | [Plano 16](../../docs/planos-salvador/16-exportacao-gis.md) · [Entrega](../../docs/entregas/16-exportacao-gis.md) · [Teste](../../docs/testes/16-exportacao-gis.md) |
| Relatórios analíticos completos (tráfego, velocidade, infração) | 5.4.5 | ⚠️ | ~35 relatórios legados; Dashboard Builder adiciona widgets TABLE. Cobertura das alíneas a–i de 5.4.5.1 não verificada | [Plano 20](../../docs/planos-salvador/20-dashboard-builder.md) · [Entrega](../../docs/entregas/21-dashboard-builder.md) |
| Indicadores parametrizáveis com metas e alertas automáticos | 5.4.6.1-3 | ⚠️ | **PoC parcial:** `KpiServlet` e `kpi_config` com cálculo por SQL e dois limiares (atenção e crítico); sem meta, histórico nem alerta automático; o conjunto mínimo de 5.4.6.2 não está cadastrado | [Plano 15](../../docs/planos-salvador/15-kpis-configuráveis.md) · [Entrega](../../docs/entregas/15-kpis-configuraveis.md) · [Teste](../../docs/testes/15-kpis-configuraveis.md) |
| Repositório central de logs técnicos e operacionais | 5.4.7.1-7 | ⚠️ | **PoC parcial:** tela Logs Técnicos (Muralha) reúne 12 fontes do legado e da PoC (`eventos_csx`, `status_conexao`, `status_energia`, `manutencao`, `log_alerta`, `log_processos`, disponibilidade, SLA, auditoria, históricos de parâmetros), normalizadas em categoria e severidade, com filtros, linha do tempo, correlação temporal, exportação e relatório técnico por equipamento. Sem trilha imutável nem retenção própria dos logs (5.4.7.7); migração e teste no navegador pendentes | [Plano 26](../../docs/planos-salvador/26-logs-tecnicos-historico-parametros.md) · [Entrega 26](../../docs/entregas/26-logs-tecnicos-historico-parametros.md) · [Teste 26](../../docs/testes/26-logs-tecnicos-historico-parametros.md) |
| Histórico de alterações de parâmetros de equipamentos | 5.4.7.6 | ⚠️ | **PoC parcial:** `muralha.parametro_historico` com valor anterior, valor novo, usuário, IP e origem, gravada pelo ConfigEquip (web service e importação, campo a campo), KPIs, retenção e integridade. Telas antigas ainda só gravam o texto de `config_chave_valor_hist`. O menu 201 (relatório vazio) foi desativado. Migração pendente | [Plano 26](../../docs/planos-salvador/26-logs-tecnicos-historico-parametros.md) · [Entrega 26](../../docs/entregas/26-logs-tecnicos-historico-parametros.md) |

**Cobertura §5.4: 64%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~50%)

---

## 5. Pré-processamento de Imagens (§5.5)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| 100% dos registros pré-processados antes de validação | 5.5.1.3 | ⚠️ | Processamento via Quartz; sem garantia de cobertura 100% documentada | — |
| Dupla análise independente por operadores distintos | 5.5.1.2g | ✅ | **PoC implementada:** `InfracaoAnaliseServlet`, constraint UNIQUE, segregação | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) · [Entrega](../../docs/entregas/05-dupla-analise.md) · [Teste](../../docs/testes/05-dupla-analise.md) |
| Prazo máximo 72h por lote | 5.5.2.1 | ✅ | **PoC implementada:** `SlaPreprocessamentoServlet` e `SlaPreprocessamentoJob`, monitoramento de prazo com alertas | [Plano 10](../../docs/planos-salvador/10-sla-preprocessamento.md) · [Entrega](../../docs/entregas/10-sla-preprocessamento.md) · [Teste](../../docs/testes/10-sla-preprocessamento.md) |
| Painel de controle operacional do pré-processamento | 5.5.3.1 | ⚠️ | **PoC parcial:** painel de SLA de 72 h (Plano 10) e painéis montáveis no Dashboard Builder; cobrem parte das alíneas a–n | [Plano 10](../../docs/planos-salvador/10-sla-preprocessamento.md), [Plano 20](../../docs/planos-salvador/20-dashboard-builder.md) |
| Relatório mensal até 5º dia útil | 5.5.3.3 | ❌ | Sem relatório automático periódico | — |
| Vedação de alteração de arquivos originais | 5.5.4.2 | ✅ | Imagens originais em `veiculo_tempo_real_imagem`; ajustes apenas para visualização | — |
| Log completo de pré-processamento (operador, timestamp, workstation, ação) | 5.5.4.5 | ⚠️ | **PoC parcial:** log de auditoria cobre operador/timestamp/ação; falta estação de trabalho | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) |
| Retenção de logs por 5 anos após encerramento contratual | 5.5.4.6 | ✅ | **PoC implementada:** política de retenção configurável, `RetencaoJob` Quartz | [Plano 12](../../docs/planos-salvador/12-retencao-dados.md) · [Entrega](../../docs/entregas/12-retencao-dados.md) · [Teste](../../docs/testes/12-retencao-dados.md) |

**Cobertura §5.5: 48%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~25%)

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
| Configuração remota de equipamentos com log | 5.6.7 | ⚠️ | `ConfigEquipWS` e `configuracao_equipamento*` atendem a configuração remota; não há log de valor anterior/novo por parâmetro (5.6.7.3) | — |

**Cobertura §5.6: 34%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~40%)

---

## 7. LAP — Leitura Automática de Placas (§5.7)

| Requisito | Ref. TR | Status | Evidência no GTW-CWB | Resolução PoC |
|---|---|---|---|---|
| Padrão Mercosul e anterior simultâneo | 5.7.1.1 | ✅ | LAP existente suporta ambos os padrões | — |
| Índice mínimo: 90% Mercosul, 85% anterior | 5.7.2.1 | ⚠️ | LAP funciona; índices não aferidos mensalmente com metodologia formal | — |
| Score de confiança parametrizável | 5.7.4.1-2 | ⚠️ | **PoC parcial:** score por placa em `muralha.vtr_placa_leitura`, origem configurável por equipamento (`TABELA_LEITURA`, `DADO_BRUTO:<campo>`, `AUSENTE`) e limiar global/por equipamento com tela. Nenhum equipamento envia score hoje (`spu_veiculo_tempo_real` não recebe; `dado_bruto` vazio). Migração pendente | [Plano 27](../../docs/planos-salvador/27-score-ocr-revisao-placas.md) · [Entrega 27](../../docs/entregas/27-score-ocr-revisao-placas.md) · [Teste 27](../../docs/testes/27-score-ocr-revisao-placas.md) |
| Encaminhamento automático por score baixo | 5.7.4.3 | ⚠️ | **PoC parcial:** `JobOcrRevisao` põe na fila de revisão as leituras abaixo do limiar; sem score enviado, nada é encaminhado | [Plano 27](../../docs/planos-salvador/27-score-ocr-revisao-placas.md) · [Entrega 27](../../docs/entregas/27-score-ocr-revisao-placas.md) · [Teste 27](../../docs/testes/27-score-ocr-revisao-placas.md) |
| Score como metadado permanente | 5.7.4.4 | ⚠️ | **PoC parcial:** score gravado por leitura em `vtr_placa_leitura`; migração pendente | [Plano 27](../../docs/planos-salvador/27-score-ocr-revisao-placas.md) · [Entrega 27](../../docs/entregas/27-score-ocr-revisao-placas.md) · [Teste 27](../../docs/testes/27-score-ocr-revisao-placas.md) |
| Rastreabilidade: leitura original, processada, corrigida | 5.7.5.1 | ✅ | `veiculo_tempo_real_correcao`, log de correções | — |
| Log de correção manual (operador, data, original, corrigida, justificativa) | 5.7.5.2 | ⚠️ | **PoC parcial:** os três caminhos de correção (fila, lote, consulta) usam `CorrecaoPlacaService`, com motivo obrigatório, justificativa e `sis_log_auditoria`; migração e teste pendentes | [Plano 27](../../docs/planos-salvador/27-score-ocr-revisao-placas.md) · [Entrega 27](../../docs/entregas/27-score-ocr-revisao-placas.md) · [Teste 27](../../docs/testes/27-score-ocr-revisao-placas.md) |
| Retenção do histórico por 5 anos pós-contrato | 5.7.5.3 | ✅ | **PoC implementada:** política de retenção configurável abrange LAP | [Plano 12](../../docs/planos-salvador/12-retencao-dados.md) · [Entrega](../../docs/entregas/12-retencao-dados.md) |
| Anonimização de placas para fins estatísticos (LGPD) | 5.7.8 | ✅ | **PoC implementada:** view `v_vtr_anonimizado`, `ConsultaAnonimizadaServlet` | [Plano 13](../../docs/planos-salvador/13-anonimizacao-placas.md) · [Entrega](../../docs/entregas/13-anonimizacao-placas.md) · [Teste](../../docs/testes/13-anonimizacao-placas.md) |
| Tempo máximo 4s entre leitura e alerta na plataforma | 5.7.9.1 | ⚠️ | Thread 2s; **PoC:** SLA de latência monitora tempo de resposta | [Plano 07](../../docs/planos-salvador/07-sla-latencia.md) · [Entrega](../../docs/entregas/07-sla-latencia.md) |
| Vídeos operacionais time-lapse | 5.7.7 | ⚠️ | **PoC parcial:** `TimelapseServlet` e player de imagens no navegador; não gera arquivo de vídeo nem faz o descarte parametrizado de 72 h (5.7.7.5-6) | [Plano 17](../../docs/planos-salvador/17-timelapse.md) · [Entrega](../../docs/entregas/17-timelapse.md) · [Teste](../../docs/testes/17-timelapse.md) |

**Cobertura §5.7: 45%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~45%)

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

**Cobertura §5.8: 20%** (matriz canônica, 2026-10-16; estimativa anterior à PoC: ~5%)

---

## 9. Prova de Conceito — PoC (Anexo A)

| Item avaliado na PoC | Status GTW-CWB | Resolução PoC |
|---|---|---|
| Módulo de Gestão e Classificação de Infrações (operação completa) | ⚠️ Funcional: dupla análise e obliteração implementadas; SHA-256 sem verificação na recepção | Planos [02](../../docs/planos-salvador/02-sha256-integridade.md), [05](../../docs/planos-salvador/05-dupla-analise.md), [06](../../docs/planos-salvador/06-obliteracao-imagens.md), [14](../../docs/planos-salvador/14-gestao-lotes.md) |
| Recepção de registros e fluxo de análise | ⚠️ Funcional; o SHA-256 é calculado depois da gravação, sem conferência com o hash de origem | [Plano 02](../../docs/planos-salvador/02-sha256-integridade.md) |
| Dupla classificação por operadores independentes | ✅ Implementado com desempate | [Plano 05](../../docs/planos-salvador/05-dupla-analise.md) |
| Exportação de lotes infracionais | ✅ Remessas existentes + gestão de lotes PoC | [Plano 14](../../docs/planos-salvador/14-gestao-lotes.md) |
| Alertas de Irregularidades (geração, visualização, rastreabilidade) | ✅ Funcional + sensores externos | [Plano 19](../../docs/planos-salvador/19-sensores-externos.md) |
| Painel de Inteligência de Dados (painéis analíticos, exportação) | ✅ KPIs configuráveis + exportação completa | Planos [11](../../docs/planos-salvador/11-exportacao-padronizada.md), [15](../../docs/planos-salvador/15-kpis-configuráveis.md) |
| Integração campo ↔ sistema (imagens e eventos em tempo real) | ✅ Funcional; time-lapse como player de imagens | [Plano 17](../../docs/planos-salvador/17-timelapse.md) |
| LOG do sistema (eventos relevantes) | ⚠️ Log de ações de usuários centralizado; logs técnicos só no legado | [Plano 01](../../docs/planos-salvador/01-log-auditoria.md) |
| Verificação de falta/retorno de energia (alerta em ≤5 min) | ✅ `fcn_getAlertaLocaisOffLine` + disponibilidade | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) |
| Verificação de falha/retorno de comunicação (alerta em ≤5 min) | ✅ `fcn_getAlertaLocaisConexaoInstavel` | — |
| Ativação/desativação remota de fiscalização | ✅ `ConfigEquipWS` | — |
| LAP com índices mínimos (90%/85%) | ⚠️ Funciona; índices não aferidos | — |
| Classificação veicular (tipo: 95%, marca/modelo/cor: 80%) | ⚠️ `ia.veiculo_caracteristica` existe; precisão não aferida | — |
| Disponibilidade operacional ≥95% | ✅ Monitoramento implementado | [Plano 18](../../docs/planos-salvador/18-disponibilidade.md) |
| Imagens válidas ≥95% | ⚠️ Não aferido | — |

---

## 10. Resumo Executivo de Aderência

Cobertura = (✅ + 0,5 × ⚠️) ÷ itens da seção, calculada sobre os 232 itens da [matriz canônica](../../docs/entregas/itens-edital-x-entregas.md) com `python -I docs/referencia/scripts/validar_aderencia.py --resumo`. A coluna "Antes da PoC" é a estimativa de 2026-10-08, feita por outro método (por grupo de requisitos), e serve só como referência histórica. Os percentuais "após PoC" publicados em 2026-10-08 (global ~62%) foram substituídos porque contavam como atendidos itens que o código não atende.

| Módulo | Antes da PoC (estimativa) | Atual (matriz canônica) | Itens ✅ / ⚠️ / ❌ |
|---|---|---|---|
| 5.1 Controle de Acesso | ~40% | **89%** | 11 / 3 / 0 |
| 5.2 Gestão e Classificação de Infrações | ~45% | **82%** | 27 / 10 / 2 |
| 5.3 Alertas de Irregularidades | ~60% | **66%** | 11 / 15 / 2 |
| 5.4 Inteligência de Dados | ~50% | **64%** | 11 / 20 / 2 |
| 5.5 Pré-processamento de Imagens | ~25% | **48%** | 6 / 10 / 7 |
| 5.6 Infraestrutura de Comunicação | ~40% | **34%** | 8 / 6 / 18 |
| 5.7 LAP | ~45% | **45%** | 11 / 12 / 15 |
| 5.8 Datacenter / Disponibilidade | ~5% | **20%** | 1 / 8 / 16 |

**Aderência global: 55%** (86 ✅ · 84 ⚠️ · 62 ❌ em 232 itens; estimativa anterior à PoC: ~39%)

### Gaps críticos resolvidos pela PoC
1. ~~Dupla análise independente obrigatória (§5.2.3 e §5.5)~~ → **Plano 05**
2. ~~Obliteração de imagens — LGPD (§5.2.4.3)~~ → **Plano 06**
3. ~~Controle de política de senhas e MFA (§5.1.5)~~ → **Planos 03 e 04**
4. Verificação de integridade SHA-256 (§5.2.1.2) → **Planos 02 e 25** (parcial: conferência pronta; falta o equipamento enviar o hash, Fase 11)
5. Painel de assertividade da pré-classificação (§5.2.2.6) → **Plano 08** (parcial: sem volume, divergências e alerta < 80%)
6. ~~Conteúdo mínimo do log de auditoria (§5.1.9)~~ → **Plano 01**; consulta e exportação (§5.1.10) parciais
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

> A **fonte primária de status** é a matriz canônica [`itens-edital-x-entregas.md`](../../docs/entregas/itens-edital-x-entregas.md); este documento resume os requisitos por grupo e aponta os artefatos da PoC.  
> Para a matriz completa (TR → Plano → Entrega → Teste), consulte [`docs/entregas/rastreabilidade-tr.md`](../../docs/entregas/rastreabilidade-tr.md).

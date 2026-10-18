# 04 — Catálogo Funcional dos Módulos

> Fonte: menus reais do banco (`../referencia/menus-e-permissoes.md`), servlets/ações (`../referencia/modulos-backend-muralha.md`), telas (`../referencia/telas-muralha-digital.md`) e leitura do código. Convenções: **Tela** = `/muralha-digital/pages/<dir>/…`; **API** = `/MuralhaDigital/<X>` (servlet, parâmetro `acao`); **Pacote** = `muralha.digital.<pkg>`.

## 1. Menu principal do Muralha Digital (tabela `sis_menu_infos`)

| Ordem | Item | Destino |
|---|---|---|
| 1 | Registro de Fato | `pages/registro_fato/comBoletim/consulta.jsp` |
| 2 | Monitoramento → Veículos Monitorados, Alerta e Ocorrência, Veículos em Tempo Real, Veículo de Carga em Tempo Real, Vídeo Monitoramento, Vídeo ao Vivo | submenu (`menu_pai=17`) |
| 3 | Central de Atendimento | `pages/atendimento-ocorrencias/atendimento-ocorrencias.jsp` |
| 5 | Consulta de Veículos → Consulta de Veículos, Consulta de Veículos de Carga | submenu (`menu_pai=6`) |
| 6 | Mapas → Cadastro Área Monitorada (mancha), Mapa Interativo, Mapa de Calor, Mapa de Equipamentos, Quantitativos em camadas | submenu (`menu_pai=7`) |
| 7 | Análise de Dados → Perfil Comportamental, Análise de Correlacionamentos | submenu (`menu_pai=15`) |
| 8 | Dashboards → Painel Informativo, Dashboards | submenu (`menu_pai=1`) |
| — | Painéis de Análise → Painéis, Catálogo de Datasets | submenu (itens 376/377, infos 43/44 — Plano 20) |
| 9 | Blitz Digital → Cadastro, Blitz Ostensiva, Abordagem | submenu (`menu_pai=29`) |
| — | Catálogo de Dados (menu 378, sem card desde 2026-10-18) | `analises/catalogo/index.jsp`, aberto pelo botão **Catálogo de Dados** do Catálogo de Datasets |
| 99 | Cadastro e Configuração → Gestão de Lotes, Catálogo de Datasets, Ponto de Interesse, Configuração de Monitoramento | submenu (`menu_pai=16`). Sem card desde 2026-10-18 (abertas por botão da tela do módulo): Guarnição (Central de Atendimento → Gerenciar Guarnições → Gerenciar), Configuração de KPIs (KPIs → Configurar) e Catálogo de Dados |
| — | **Relatórios** (menu separado, `sis_menu_relatorio_infos`, ~35 itens) e **Gráficos** (`sis_menu_graficos_infos`, 5 itens) | `pages/relatorios/*` |

Home: `login/abertura-sistemas.jsp` → `login/muralha_principal.jsp?menu_info_pai=<id>` (cards por menu). A navbar (`cabecalho_bootstrap_simples.jsp`) mostra Processamento, Relatórios, Gráficos, GTW, Pesquisa Rápida, sino/envelope de alertas e menu do usuário.

## 2. Módulos Muralha Digital

### 2.1 Alerta e Ocorrência (núcleo)
- **Telas:** `consulta-alerta-ocorrencia/` (listagem + modais), `alerta-tratativa/` (tratar alerta), `modal-cad-ocorrencia-ligacao/`, `linha-tempo/`, `assinatura/`.
- **API:** `/MuralhaDigital/Alerta` (`obterAlertaPorId`, `obterAlertasNaoTratadas`, `descartarAlerta`, `marcarComoEmAtendimento`, `podeAtender`, `criarAlerta`, `processarAlertasVinculados`…), `/Alerta/AnotacaoContributiva`, `/Alerta/MotivoDescarte`, `/AlertaOcorrencia` (consultas, `consultaPorFiltrosTela`, `QuantitativosAlertasOcorrencias`), `/AlertaOcorrencia/{Status,Tipo,TipoRegistro}`, `/LinhaTempo`, `/Ocorrencia` (`gerarOcorrencia`, `finalizarOcorrencia`), `/OcorrenciaLigacao`, `/AlertaQuestionario`.
- **Pacotes:** `alerta`, `consulta`, `ocorrencia`, `ocorrencialigacao`, `perguntasRespostas`, `assinatura`.
- **Tabelas:** `muralha.alerta` (11,5 mil), `alerta_veiculo`, `status_alerta`, `tipo_alerta_ocorrencia`, `motivo_descarte`, `anotacao_contributiva`, `alerta_questionario*`, `ocorrencia`, `ocorrencia_notificacao*`, `status_ocorrencia`, `tipo_ocorrencia_status`.
- **Procedures/funções:** `spu_ObterNovosAlertas`, `spu_ObterDadosAlertaOcorrencia`, `fcn_ObterDadosAlertaOcorrencia`, `fcn_ObterAlertas(Alt)`, `fcn_ObterOcorrencias(Alt)`, `spu_gerar_ocorrencia`, `spu_ObterDadosLinhaTempo`.
- **Fluxo:** passagem OCR → (DB) gera alerta se placa ∈ monitorados → thread `Alertas` (2 s) detecta novos → WebSocket `ALERTA-NOTIFICACAO` → navbar mostra contador (`qtdeAlertasNaoTratados`/`qtdeAlertasNaoAssinados`) e toca som (`ConfiguracaoSons`) → operador abre `tratar-alerta.jsp`, responde questionário obrigatório se houver, descarta (com `motivo_descarte`) ou atende/gera ocorrência → notificações por e-mail/SMS (jobs Quartz) → assinatura digital da imagem (`assinatura`).

### 2.2 Veículos monitorados
- **Telas:** `monitorado/` (cadastro/consulta). **API:** `/Monitorado` (`inserir`, `atualizar`, `encerrar`, `obterLista`, `exportarCadastrosAtivos`, `ObterPlacasComAlerta`…), `/VeiculoAuxiliar` (cores, marcas, modelos, classes).
- **Pacote:** `monitorado`. **Tabelas:** `muralha.cad_veiculo_monitorado` (487) + `_equipamento`, `_grupo`, `_historico`, `_periodo`, `_exclusao`; `cad_cor/marca/modelo/tipo`, `classe_veiculo`. Há restrição de horário (`HorarioPermitido`) e de equipamento/período.

### 2.3 Veículos em tempo real e consulta de passagens
- **Telas:** `veiculo-tempo-real/veiculos-passagem.jsp` (+ `?VeiculoCarga=1`), `consulta-veiculo/`, `consulta-veiculo-de-carga/`, `mapa-passagens/`, `video-passagem-*`, `correcao-placas-lote/`.
- **API:** `/VeiculoTempoReal`, `/BlitzEletronica/VeiculoIrregular`, `/Veiculo` (`consultaPorFiltrosTela`, `exportarConsulta`, `obterPassagensRelacionadas`, `alterarPlaca`, `registrarExportacaoImagem`…), `/Veiculo/Imagem(/Lista)`, `/VeiculoDeCarga`, `/CorrecaoPlaca`.
- **Pacotes:** `veiculo`, `temporeal`, `correlacaoplaca`. **Tabelas:** `muralha.veiculo_tempo_real` (8,2 M), `veiculo_tempo_real_imagem` (10,9 M), `veiculo_tempo_real_correcao`, `ia.veiculo_caracteristica` (características por IA: cor/marca/modelo). **Procedures:** `spu_ObterVeiculosTempoReal(Historico)`, `spu_ObterVeiculosPorFiltros`, `spu_ObterVeiculosGtwPorFiltros`, `spu_ObterVeiculosBlitzEletronica`.
- Regras vindas de `muralha-digital-config.xml`: `LimiteConsultaAtivo/EmSegundos`, `TamanhoMinimoPlaca`, `ExigirPlacaCompleta`, `QtdeMaxCaracterEspecialPlaca` (curingas `*` na placa). Consultas exigem **motivo da solicitação** (`motivo_solicitacao_relatorio`).

### 2.4 Mapas, equipamentos, câmeras e vídeo
- **Telas:** `mapa-interativo/`, `mapa-calor/`, `mapa-equipamento/` (+3D), `mapa-situacao-transito/`, `mapa-alertas-ocorrencias/`, `mancha-monitorada/`, `ponto-interesse/`, `visualizacao-cameras/`, `mosaico/`, `video-monitoramento/`, `video-ao-vivo/`, `configuracao-monitoramento/`.
- **API:** `/MapaInterativo`, `/MapaCalor`, `/MapaDispositivosEquipamentos`, `/MapaDispositivos3D`, `/AreaMonitorada`, `/PontoInteresse`, `/Equipamento`, `/DispositivoEquipamento`, `/ConfigMonAoVivo`, `/VideoMonitoramento(/Video)`.
- **Pacotes:** `mapainterativo`, `mapacalor`, `mapadispositivos*`, `areamonitorada`, `pontointeresse`, `equipamento`, `dispositivo`, `monitoramento`. **Tabelas/views:** `area_monitorada`, `equipamentos_area_monitorada`, `ponto_interesse*`, `v_equipamento_cameras(_todas)`, `local_vigente`, `agente_localizacao_atual/hist`, `config_monitoramento_ao_vivo*`.
- Mapas: Google Maps JS (chave em `maps-config.js`/`confGTW.xml`) e Leaflet 1.9 (relatório de rota). Vídeo: câmeras Pumatronix/Dahua via nginx (RTSP-over-WebSocket) e FFmpeg para clipes (`DirFFMPEG`, `FormatoVideoTemp=ogv`).

### 2.5 Registro de Fato e Boletim
- **Telas:** `registro_fato/` (68 arquivos; `comBoletim/`, `semBoletim/`), `boletim/`, `pesquisa-rapida/`.
- **API:** `/RegistroDeFato(/Tipo|Situacao|Natureza|Grupo|Historico|IndividuoTipo|Doducmento|EnderecoEvento|Boletim/Situacao|Cidade)`, `/RegistroDeFatoNaturezaDelituosa`, `/Boletim(/Tipo|Situacao|Cidade|TipoIndividuo|Doducmento)`, `/PesquisaRapida(/Autocomplete)`.
- **Pacotes:** `registroDeFato` (90 classes), `boletim` (26), `pesquisaRapida`. **Tabelas:** `registro_fato*` (indivíduo, veículo, objeto, endereço, documento, histórico, link, natureza, status, tipo, usuário_grupo, passagem_veic), `boletim*`, `antecedentes_criminais`, `proprietario*`. **View:** `vw_registro_fato_origem`.
- Um Registro de Fato **com boletim** vem de integração externa (boletim de ocorrência); **sem boletim** é cadastrado pelo operador. Visibilidade por grupo (`registro_fato_usuario_grupo`, `privado`).

### 2.6 Atendimento, guarnições e blitz
- **Telas:** `atendimento-ocorrencias/`, `guarnicao/`, `blitz/`, `blitz-ostensiva/`, `blitz-abordagem/`, `blitz-eletronica/`.
- **API:** `/Atendimento`, `/Anexo`, `/Guarnicao`, `/Blitz` (50+ ações: abordagens, pessoas, documentos, imagens, histórico por CPF/placa, geocoding, encerrar), `/MapaInterativo` (localização de agentes).
- **Pacotes:** `atendimento`, `guarnicao`, `blitz`. **Tabelas:** `atendimento*`, `guarnicao*`, `agente_localizacao_*`, `blitz_*`. **Procedures:** `spu_encerrar_atendimento`, `spu_obtem_ocorrencias`, `spu_ObterVeiculosBlitzWebSocket`.
- Blitz Digital usa WebSocket `BLITZ-DIGITAL` com timer de passagens reais enquanto houver cliente conectado; geocoding via chave `api_key_geocoding` (config).

### 2.7 Análise: correlacionamento, perfil comportamental, dashboards
- **Telas:** `veiculos-correlacionados/`, `correlacao/`, `perfil-comportamental/`, `dashboards/`, `painel-informacoes/`, `relatorios/` (61 arquivos).
- **API:** `/VeiculosCorrelacionados` (`detalhes`, `correlacionados`, `passagens`), `/PerfilComportamental` (`info`, `pordia`, `porhora`, `porpcl`, `permanencia`, `passagensindividuais`), `/Dashboard`, `/Grafico`, `/PainelInformacao/*`.
- **Funções:** `fcn_perfil_comportamental_*`, `spu_correlacionamento_placas`, `fcn_ObterInfo*`. **Tabelas:** `correlacionamento_automatico*` (10 mil+), `historico_passagens_correlacionadas`, `veiculo_sumarizado*`, `veiculo_estatistica`.

### 2.8 Relatórios e gráficos
- ~35 relatórios e 5 gráficos, cada um uma JSP em `pages/relatorios/` + servlet `/Relatorio/*`, `/relatorio/*` ou `/MuralhaDigital/Relatorios/*`, apoiados em `spu_*`/`fcn_*` (lista em `modulos-backend-muralha.md`). Exportação cliente (SheetJS, jsPDF+autotable) e Chart.js para gráficos; **todo relatório exige registrar o motivo** (`MotivoSolicitacaoRelatorio`) e grava `sis_log`/auditoria.
- Paleta própria de dashboards (azul `#1e40af`, verde `#10b981`, âmbar `#f59e0b`, vermelho `#ef4444`, roxo `#8b5cf6`) — ver `05-padrao-visual.md` §3.4.

### 2.9 Notificação, configuração e acesso
- **API:** `/Notificacao`, `/GrupoNotificacao`, `/TipoNotificacao`, `/ConfiguracaoTempo` (sons, alerta contínuo, prioridade), `/ConfiguracaoInatividade`, `/ConfiguracaoRadares`, `/ConfiguracaoTempoOcrBlitz`, `/PermissoesFuncionalidade`, `/Usuarios` (`login`, `out`, `login_google_token`, `acessoViaToken`, `obterListaUsuariosAtivos`).
- **Pacotes:** `notificacao` (23: e-mail SendGrid/SMTP, SMS Twilio/Facilita/SmsDev/Comtele, Bitly, recuperação de senha), `acessos` (18). Adicionado na PoC: `muralha.digital.acesso` com política de senhas (complexidade, expiração, histórico) e MFA/TOTP — ver §5.3.
- **Tabelas:** `muralha.config_chave_valor(_hist)`, `config_grupo_permissao` (277 linhas — permissões finas por grupo, ex.: quem vê tipos de alerta), `ocorrencia_notificacao*`, `alerta_notificacao`, `sis_usuario_recupera_senha`.

### 2.10 Utilidades
`JuncaoBase` (importa CSV de base de veículos via `spu_ImportarBase`), `Anomalia`, `cidade` (cidades de MG), `util` (`Paginacao`, `Resultado`, `EncurtadorURL`, `Constantes`).

## 3. Módulos do GTW clássico (resumo)

| Área | JSP (`webapp/…`) | Servlets/URLs típicos | Tabelas `dbo` típicas |
|---|---|---|---|
| Processamento de infrações | `processo/*` (iniciar_validacao, estado_processamento, concluir_validacao, reposicionamento, auditoria) | `/processo/*`, `/ajax/Processar`, `/ajax/Info*` | `infracao`, `infracao_processo`, `infracao_imagem`, `processo*`, `cad_inibicao_infracao` |
| Cadastros | `cadastro/*` (usuário, grupo, menu, veículos monitorados, eventos manuais, agenda) | servlets de cadastro | `sis_*`, `cad_*`, `grupo_equipamento`, `cad_isento` |
| Remessa / AIT / notificação | `remessa/*`, `controleAIT/*`, `ait/*`, `notificacao/*`, `descarga/*` | `/remessa/*`, `/ait/AITPDF`, `/notificacao/NotificacaoPDF`, Jasper `relatorios/AIT*.jrxml` | `remessa*`, `infracao_remessa*`, `infracao_notificacao` |
| Relatórios GTW | `relatorio/*` (fluxo × tempo/classe/velocidade, aproveitamento de imagens, consistências, acidentes) | `/relatorio/*` | `veiculo_*`, `veiculo_sumarizado*`, `relatorios_*` |
| Ferramentas | `ferramenta/*` (filtros, isentos, cadastro de veículo, mover infrações, pré-relatório) | `/ferramenta/*`, `/ferramentas/*` | `cad_isento`, `infracao_processo_filtro` |
| Monitoramento clássico | `monitoramento/*`, `acesso_remoto/*` | `/GtwWidgets/Monitoramento*`, `/AcessoRemotoPolling` | `configuracao_equipamento*`, `eventos_csx` |
| Manutenção / medição / amostras | `manutencao/*`, `medicao/*`, `amostras/*` | `/manutencao/*`, `/ExportaAmostras` | `manutencao*`, `processo_medicao*` |
| Equipamentos (SOAP) | — | `/services/*` (Axis) | `configuracao_equipamento*` (~40 tabelas), `eventos_csx` |

## 4. Matriz módulo × risco de alteração

| Módulo | Acoplamento | Cuidado |
|---|---|---|
| Alerta/Ocorrência/WebSocket | Alto (DB + threads + front) | Testar com 2 abas; mudar contrato XML do WebSocket quebra navbar e telas |
| Registro de Fato | Alto (90 classes, DTOs) | Preservar DTOs/JSON consumidos pelo JS |
| Veículo/Consulta | Alto volume (8 M linhas) | Sempre filtrar por data/equipamento; usar `spu_*` existentes |
| Relatórios | Médio | Seguir procedimento: motivo → servlet → procedure → JSP |
| GTW clássico | Alto e pouco testado | Só correções pontuais |

## 5. Módulos PoC TRANSALVADOR (Planos 01-16)

Módulos desenvolvidos para a PoC de Salvador, cada um em pacote próprio sob `muralha.digital.*`. Todos seguem o padrão servlet + sessão/permissão + JDBC parametrizado (§2 de `06`). Migrações SQL em `docs/banco-de-dados/migracoes/`.

### 5.1 Auditoria e rastreabilidade (Plano 01)
- **Pacote:** `auditoria` (2 classes). **API:** `/MuralhaDigital/Auditoria` (`listar`, `exportar`).
- **Telas:** `pages/auditoria/consulta-log.jsp`, `pages/relatorios/auditoria/auditoria.jsp`.
- Registra ações do usuário com IP, sessão e timestamp; consulta com filtros por período, usuário e ação.

### 5.2 Integridade SHA-256 (Plano 02)
- **Pacote:** `integridade` (1 classe: `JobSha256Imagem`). Sem servlet — job batch Quartz.
- Calcula hash SHA-256 de imagens de passagem e armazena em `veiculo_tempo_real_imagem.hash_sha256` para garantia de cadeia de custódia.

### 5.3 Política de senhas e MFA/TOTP (Planos 03-04)
- **Pacote:** `acesso` (3 classes: `SenhaService`, `TotpService`, `MfaConfigServlet`).
- **API:** `/MuralhaDigital/MfaConfig` (`gerarSegredo`, `ativar`, `desativar`, `verificar`).
- **Tela:** `pages/meu-perfil/configurar-mfa.jsp` (QR Code TOTP, ativação em dois passos).
- Política de senhas: complexidade mínima, expiração, histórico e bloqueio por tentativas (integrado ao login existente via `SenhaService`). MFA opcional por usuário via TOTP (Google Authenticator, Authy).

### 5.4 Dupla análise de infrações (Plano 05)
- **Pacote:** `processamento` (2 classes). **API:** `/MuralhaDigital/InfracaoAnalise` (`listarFila`, `obter`, `analisar`, `estatisticas`).
- **Telas:** `pages/processamento/dupla-analise/fila.jsp`, `analisar.jsp`.
- Fluxo de revisão em duas etapas para infrações: primeiro analista confirma/rejeita, segundo valida. Rastreabilidade completa com registro de decisões.

### 5.5 Obliteração de imagens (Plano 06)
- **Pacote:** `imagem` (1 classe). **API:** `/MuralhaDigital/Obliteracao` (`obliterar`, `status`).
- Sem tela — API para obliteração (borrar/mascarar) de imagens de passagem, atendendo requisitos de privacidade (LGPD).

### 5.6 SLA de latência (Plano 07)
- **Pacote:** `sla` (compartilhado com Plano 10). **API:** `/MuralhaDigital/SlaLatencia` (`resumo`, `detalhe`, `historico`).
- **Tela:** `pages/monitoramento/sla-latencia/index.jsp`.
- Painel de monitoramento de latência ponta-a-ponta (captura → processamento), com indicadores de conformidade ao SLA contratual.

### 5.7 Painel de assertividade (Plano 08)
- **Pacote:** `assertividade`. **API:** `/MuralhaDigital/Assertividade` (`resumo`, `porEquipamento`, `porPeriodo`, `exportar`).
- **Tela:** `pages/monitoramento/assertividade/index.jsp`.
- Métricas de acurácia do OCR e da detecção de infrações por equipamento e período; identifica equipamentos com desempenho abaixo do limiar.

### 5.8 CPF e matrícula de operadores (Plano 09)
- **Pacote:** `usuario`. **API:** `/MuralhaDigital/UsuarioCpfMatricula` (`listar`, `atualizar`, `validar`).
- **Tela:** `pages/admin/cpf-matricula/index.jsp`.
- Vinculação de CPF e matrícula funcional aos usuários do sistema; validação de formato e unicidade.

### 5.9 SLA de pré-processamento (Plano 10)
- **Pacote:** `sla` (compartilhado com Plano 07). **API:** `/MuralhaDigital/SlaPreprocessamento` (`resumo`, `detalhe`, `historico`).
- **Tela:** `pages/monitoramento/sla-preproc/index.jsp`.
- Indicadores de tempo entre captura e disponibilização da imagem processada; complementa o SLA de latência (Plano 07).

### 5.10 Exportação padronizada (Plano 11)
- **Pacote:** `exportacao`. **API:** `/MuralhaDigital/Exportacao` (`exportar`, `listarFormatos`, `status`).
- **Tela:** `pages/relatorios/exportacao-passagens/index.jsp`.
- Exportação de passagens e infrações em formatos padronizados (CSV, XLSX) com filtros por período, equipamento e tipo. Usa POI 4.1.1 (já presente no pom.xml).

### 5.11 Retenção de dados (Plano 12)
- **Pacote:** `retencao`. **API:** `/MuralhaDigital/Retencao` (`listarPoliticas`, `configurar`, `executar`, `historico`).
- **Tela:** `pages/admin/retencao/index.jsp`.
- Configuração de políticas de retenção e expurgo automático de dados antigos, com log de execução e prévia de volume afetado.

### 5.12 Anonimização de placas (Plano 13)
- **Pacote:** `anonimizacao`. **API:** `/MuralhaDigital/ConsultaAnonimizada` (`consultar`, `exportar`).
- **Tela:** `pages/consulta/passagens-anonimizadas/index.jsp`.
- Consulta de passagens com placas parcialmente mascaradas para perfis sem permissão de visualização completa (LGPD).

### 5.13 Gestão de lotes (Plano 14)
- **Pacote:** `lote`. **API:** `/MuralhaDigital/Lote` (`listar`, `obter`, `criar`, `fechar`, `reabrir`, `estatisticas`).
- **Tela:** `pages/processamento/lotes/index.jsp`.
- Agrupamento de infrações em lotes para processamento, conferência e envio; controle de status e rastreabilidade.

### 5.14 KPIs configuráveis (Plano 15)
- **Pacote:** `kpi`. **API:** `/MuralhaDigital/Kpi` (`listar`, `obter`, `cadastrar`, `atualizar`, `calcular`, `dashboard`).
- **Telas:** `pages/admin/kpis/index.jsp` (cadastro), `pages/dashboard/kpis/index.jsp` (visualização).
- Cadastro de indicadores de desempenho com fórmulas, metas e periodicidade configuráveis; painel de acompanhamento com gráficos ECharts.

### 5.15 Exportação GIS (Plano 16)
- **Pacote:** `gis`. **API:** `/MuralhaDigital/Gis` (`exportar`).
- Sem tela — endpoint de download de dados georreferenciados (equipamentos, passagens) em formato compatível com ferramentas GIS.

### Resumo dos Planos 01-16

| Plano | Pacote | Servlet | Tela principal |
|---|---|---|---|
| 01 — Auditoria | `auditoria` | `/MuralhaDigital/Auditoria` | `auditoria/consulta-log.jsp` |
| 02 — SHA-256 | `integridade` | — (job batch) | — |
| 03 — Política de senhas | `acesso` | — (serviço interno) | — (regras no login) |
| 04 — MFA/TOTP | `acesso` | `/MuralhaDigital/MfaConfig` | `meu-perfil/configurar-mfa.jsp` |
| 05 — Dupla análise | `processamento` | `/MuralhaDigital/InfracaoAnalise` | `processamento/dupla-analise/fila.jsp` |
| 06 — Obliteração | `imagem` | `/MuralhaDigital/Obliteracao` | — (API) |
| 07 — SLA latência | `sla` | `/MuralhaDigital/SlaLatencia` | `monitoramento/sla-latencia/index.jsp` |
| 08 — Assertividade | `assertividade` | `/MuralhaDigital/Assertividade` | `monitoramento/assertividade/index.jsp` |
| 09 — CPF/matrícula | `usuario` | `/MuralhaDigital/UsuarioCpfMatricula` | `admin/cpf-matricula/index.jsp` |
| 10 — SLA pré-proc. | `sla` | `/MuralhaDigital/SlaPreprocessamento` | `monitoramento/sla-preproc/index.jsp` |
| 11 — Exportação | `exportacao` | `/MuralhaDigital/Exportacao` | `relatorios/exportacao-passagens/index.jsp` |
| 12 — Retenção | `retencao` | `/MuralhaDigital/Retencao` | `admin/retencao/index.jsp` |
| 13 — Anonimização | `anonimizacao` | `/MuralhaDigital/ConsultaAnonimizada` | `consulta/passagens-anonimizadas/index.jsp` |
| 14 — Lotes | `lote` | `/MuralhaDigital/Lote` | `processamento/lotes/index.jsp` |
| 15 — KPIs | `kpi` | `/MuralhaDigital/Kpi` | `admin/kpis/index.jsp` |
| 16 — Exportação GIS | `gis` | `/MuralhaDigital/Gis` | — (download) |

## 6. Planos 17-19 — Timelapse, disponibilidade e sensores

- **`muralha.digital.timelapse`** (1 arquivo) — Timelapse de imagens de passagens. **API:** `/MuralhaDigital/Timelapse`. **Tela:** `pages/timelapse/index.jsp`.
- **`muralha.digital.disponibilidade`** (2 arquivos) — Monitoramento de disponibilidade de equipamentos. **API:** `/MuralhaDigital/Disponibilidade`. **Tela:** `pages/monitoramento/disponibilidade/index.jsp`.
- **`muralha.digital.sensores`** (2 arquivos) — Integração com sensores externos e incidentes. **API:** `/MuralhaDigital/SensorExterno`. **Tela:** `pages/monitoramento/sensores/index.jsp`.

## 7. Construtor de Dashboards (Plano 20)

Módulo self-service de painéis analíticos interativos. Pacote principal: `muralha.digital.dashboards` (16 arquivos Java) e subpacote `consulta` (8 arquivos), que monta e executa o SQL das consultas direto no SQL Server ([Plano 21](../planos-salvador/21-dashboards-consulta-sql-sem-cube.md)).

**Servlets REST (prefixo `/api/dash/`):**
- `DashCatalogoServlet` — CRUD do catálogo de tabelas/colunas/relações
- `DatasetServlet` — CRUD de datasets com publicação (com validação de todos os campos) e versionamento
- `DashConsultaServlet` — consultas dos widgets: valida permissão, monta SQL parametrizado a partir do catálogo, executa e audita
- `DashPainelServlet` — CRUD de painéis e widgets
- `WidgetServlet` — CRUD de widgets individuais
- `ExportacaoDashboardServlet` — exportação CSV/XLSX
- `HealthCheckServlet` — saúde do banco e estatísticas de consultas

**Services:** DatasetService, PainelService, WidgetService, ExportacaoService, CompartilhamentoService, HistoricoVersaoService, PivotService, GramaticaCampoCalculado, ConsultaLogService.

**Subpacote `consulta`:** ConsultaDashboard, ResolvedorDataset, DatasetResolvido, MontadorSql, SqlMontado, ExecutorConsulta, PeriodoRelativo, ErroConsulta.

**Frontend:** Telas em `analises/` (paineis/lista.jsp, paineis/index.jsp, datasets/index.jsp, datasets/editor.jsp, datasets/modelador.jsp, widgets/editor.jsp, catalogo/index.jsp). JavaScript em `static/js/dashboards/` (27 módulos ES, incluindo 4 do modelador visual em `modelador/`). Bibliotecas vendor: AntV X6 2.18.1 (canvas de grafos) e dagre 0.8.5 (auto-layout). CSS em `static/css/dashboards/print.css`.

**Catálogo de Dados** (`analises/catalogo/index.jsp`, menu 378 sob Cadastro e Configuração): tela de gerenciamento do catálogo de metadados usado pelo construtor de dashboards. Três abas: Tabelas (listar/editar schema, nome físico, rótulo), Colunas (listar/editar tipo, papel sugerido — dimensão/medida/tempo/geo, flag sensível) e Relações (origem→destino, colunas de junção, cardinalidade, soft delete). API via `DashCatalogoServlet` (`/api/dash/catalogo`): GET `listarTabelas`, `listarColunas`, `listarRelacoes`, `importarSugestoes`; POST `salvarTabela`, `salvarColuna`, `salvarRelacao`, `excluirRelacao`.

**Modelador Visual** (`datasets/modelador.jsp`): interface alternativa ao wizard para edição de datasets com canvas interativo (AntV X6). Arrastar tabelas do painel lateral para o canvas, criar relações por drag entre portas de colunas, sugestões automáticas (FKs, catálogo, nomes id_X), auto-layout dagre, undo/redo, seleção de campos com prévia de dados. API: GET `sugestoesRelacao`; POST `validarRelacao`, `verificarCardinalidade`, `salvarModelador`, `promoverRelacao`.

**Schema:** `dash` (15 tabelas: catalogo_tabela, catalogo_coluna, catalogo_relacao, dataset, dataset_campo, dataset_tabela, dataset_relacao, dataset_modelador, dataset_filtro_fixo, dashboard, widget, compartilhamento, historico_versao, consulta_log, dataset_estatistica).

## 8. Sistema de Chamados (Plano 22)

Central de suporte técnico integrada ao Muralha Digital, atendendo os itens 7.2.3 (a–f) e 7.2.4 do edital ([entrega 22](../entregas/22-sistema-chamados.md), [arquitetura](../chamados/sistema-chamados-arquitetura.md), [documentação em `docs/chamados/`](../chamados/), [manual](../chamados/manual-sistema-chamados.md)). Pacote `muralha.digital.chamado` (26 arquivos Java). Perfis `SOLICITANTE`, `GESTOR` e `EQUIPE` definidos por grupos de `dbo.sis_grupo` (107–109, abaixo do grupo 11), e perfil `ADMIN` mapeado do grupo 11 (Administradores, chave `chamado_grupo_admin`), único que acessa a Configuração.

| Item | Detalhe |
|---|---|
| Menus | 383 (categoria "Sistema de Chamados" na tela inicial, `sis_menu_infos` 49), com o card 379 (Chamados); 381 (Relatórios de Chamados) e 382 (Configuração de Chamados, só administrador) são abertos pelos botões Relatórios e Configuração da tela Chamados (sem card desde 2026-10-18); 380 (Detalhe do Chamado, sem card) |
| Telas (`pages/chamados/`) | `index.jsp` (lista, filtros, modal "Novo chamado", exportação), `detalhe.jsp` (histórico e ações), `relatorios.jsp` (indicadores e gráfico), `configuracao.jsp` (parâmetros de SLA, perfis de acesso, prioridades e categorias) |
| Servlets | `/MuralhaDigital/Chamado`, `/MuralhaDigital/ChamadoRelatorio`, `/MuralhaDigital/ChamadoConfig` |
| Jobs Quartz | `ChamadoSlaJob` (5 min: marcas de SLA e fechamento automático), `ChamadoEmailJob` (1 min: fila de e-mails) |
| Tabelas (schema `muralha`) | `chamado_prioridade`, `chamado_categoria`, `chamado`, `chamado_historico`, `chamado_anexo`, `chamado_notificacao`, `chamado_protocolo` |
| Parâmetros | chaves `chamado_*` em `muralha.config_chave_valor` (risco de SLA, fechamento automático, diretório e tamanho de anexos, e-mail ativo, IDs dos grupos) |

**Status:** implementado; migração, seed e complemento (`20261015_sistema_chamados.sql`, `_seed.sql` e `_admin.sql`) executados no `GTW_MURALHA_DEV` e teste integrado executado em 2026-10-15 ([roteiro](../testes/22-sistema-chamados.md)).

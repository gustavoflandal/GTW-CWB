# 04 — Catálogo Funcional dos Módulos

> Fonte: menus reais do banco (`referencia/menus-e-permissoes.md`), servlets/ações (`referencia/modulos-backend-muralha.md`), telas (`referencia/telas-muralha-digital.md`) e leitura do código. Convenções: **Tela** = `/muralha-digital/pages/<dir>/…`; **API** = `/MuralhaDigital/<X>` (servlet, parâmetro `acao`); **Pacote** = `muralha.digital.<pkg>`.

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
| 9 | Blitz Digital → Cadastro, Blitz Ostensiva, Abordagem | submenu (`menu_pai=29`) |
| 99 | Cadastro e Configuração → Guarnição, Ponto de Interesse, Configuração | submenu (`menu_pai=16`) |
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
- **Pacotes:** `notificacao` (23: e-mail SendGrid/SMTP, SMS Twilio/Facilita/SmsDev/Comtele, Bitly, recuperação de senha), `acessos` (18).
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

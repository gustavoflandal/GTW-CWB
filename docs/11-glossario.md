# 11 — Glossário

| Termo | Significado |
|---|---|
| **GTW** | Gestão de Trânsito WEB — produto/sistema legado (nome do WAR e do GTW clássico) |
| **GTW clássico** | Geração 1: processamento de infrações, remessas, cadastros, relatórios (UI JSP+GWT) |
| **Muralha Digital** / **Anel de Segurança** / **Cinturão de Segurança** | Geração 2: monitoramento, alertas, ocorrências, blitz (o `<title>` da navbar é "Cinturão de Segurança"; o nó de menu é "Anel de Segurança") |
| **Consilux** | Empresa desenvolvedora (Consilux Tecnologia) |
| **PCL** | Ponto de Captura/Leitura — equipamento (radar/câmera OCR) em um *local*; "Equipamento (PCL)" nos filtros |
| **Local / Local vigente** | Ponto físico (`dbo.local`); `local_vigente` = visão da versão em vigor do local/equipamento |
| **Faixa / Pista** | Faixa de rolamento monitorada (`configuracao_equipamento_pista`) |
| **OCR** | Leitura automática de placa (reconhecimento óptico) |
| **Passagem** | Leitura de um veículo por um PCL em um instante (linha de `veiculo_tempo_real`) |
| **Veículo monitorado** | Placa cadastrada para gerar alerta quando passar (`cad_veiculo_monitorado`) |
| **Alerta** | Evento gerado quando uma passagem corresponde a um veículo monitorado / regra (`muralha.alerta`) |
| **Alarme** | Notificação sonora/visual de alerta (config em `config_alarme*`) |
| **Ocorrência** | Desdobramento do alerta tratado: registro de ação/atendimento (`muralha.ocorrencia*`) |
| **Atendimento** | Despacho de guarnições a uma ocorrência/fato (`atendimento*`) |
| **Guarnição** | Equipe policial/de campo com integrantes e meio de deslocamento (`guarnicao*`) |
| **Agente (AG)** | Usuário de campo (app) com localização em `agente_localizacao_*` |
| **Registro de Fato (RF)** | Registro de um fato de interesse (ocorrência policial, evento) com indivíduos, veículos, objetos, endereços (`registro_fato*`) |
| **Boletim (BO)** | Boletim de ocorrência vinculado ao RF (`boletim*`); "com boletim" × "sem boletim" |
| **Blitz Digital / Ostensiva / Eletrônica** | Operação de abordagem com apoio do sistema; "Eletrônica" = detecção automática por equipamentos |
| **Abordagem** | Parada de veículo/pessoa durante a blitz (`blitz_abordagem`) |
| **Correlacionamento** | Cruzamento de passagens/placas para achar veículos relacionados (comboio, clone, roteiro) |
| **Perfil comportamental** | Análise de padrão de passagens/permanência de um veículo (`fcn_perfil_comportamental_*`) |
| **Mancha / Área monitorada** | Polígono no mapa que agrupa equipamentos (`area_monitorada`) |
| **Ponto de interesse** | Local marcado no mapa com equipamentos associados |
| **Linha do tempo** | Histórico cronológico de um alerta/ocorrência |
| **Anotação contributiva** | Observação do operador que contribui para o tratamento do alerta |
| **Questionário (alerta)** | Perguntas obrigatórias a responder antes de tratar certos tipos de alerta |
| **Motivo de solicitação (relatório)** | Justificativa obrigatória registrada ao consultar dados/relatórios |
| **Infração** | Violação detectada por equipamento (velocidade, avanço, parada…) — GTW clássico |
| **Processo / Processamento** | Fluxo de triagem → digitação → validação de infrações e imagens |
| **Obliteração** | Ocultação de parte da imagem (ex.: terceiros) |
| **Enquadramento** | Tipificação legal da infração (códigos C006…C999 nos relatórios) |
| **Remessa** | Lote de infrações validadas enviado ao órgão autuador (CET, URBS…) |
| **AIT** | Auto de Infração de Trânsito |
| **NAI / NIP** | Notificação de Autuação de Infração / Notificação de Imposição de Penalidade |
| **PREM** | Relatório/arquivo de remessa (`PREM_C0xx.jrxml`) |
| **Descarga** | Exportação de mídia/dados para encerramento de período (módulo `descarga-core`) |
| **Aferição** | Verificação metrológica do equipamento (`configuracao_equipamento_afericao`) |
| **ConfigEquip** | Aplicativo/serviço de configuração de equipamentos (Axis) |
| **CSX** | Prefixo histórico da plataforma de captura (`CSXEventsWS`, `eventos_csx`, `AlertCsx`) |
| **PM / DER / CET / URBS / DNIT / BHTrans** | Órgãos/clientes atendidos (logotipos e exportadores específicos; siglas conforme nomes de classes, relatórios e logos) |
| **spu_ / fcn_ / v_ / vw_** | Prefixos de stored procedure / função / view no SQL Server |
| **`acao`** | Parâmetro que seleciona a operação em um servlet (`?acao=listar`) |
| **GXT / GWT** | Extjs GXT 2.2.1 / Google Web Toolkit 2.1 — UI legada compilada |
| **Axis** | Apache Axis 1.4 — SOAP legado |
| **DEV** | Ambiente de desenvolvimento: SQL Server `10.0.0.200`, banco `GTW_MURALHA_DEV` |

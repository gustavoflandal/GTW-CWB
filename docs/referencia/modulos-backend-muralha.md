# Referência — Módulos back-end do Muralha Digital (pacote `muralha.digital.*`)

Gerado automaticamente por varredura do código. Para cada pacote: classes, servlets (URL), ações (parâmetro `acao`), e objetos de banco referenciados (tabelas, views, procedures e funções citadas em SQL). A lista de tabelas é heurística (regex sobre FROM/JOIN/INSERT/UPDATE) — confirme no código-fonte.

## _ini

- Arquivos Java: 1 (pacote `muralha.digital._ini`)
- Classes: Inicializacao

## acessos

- Arquivos Java: 18 (pacote `muralha.digital.acessos`)
- Classes: ConfiguracaoInatividade, ConfiguracaoInatividadeResult, ConfiguracaoInatividadeServlet, ConfiguracaoRadares, ConfiguracaoRadaresResult, ConfiguracaoRadaresServlet, ConfiguracaoTempoOcrBlitz, ConfiguracaoTempoOcrBlitzResult, ConfiguracaoTempoOcrBlitzServlet, Grupo, GruposRetorno, Menu, PermissaoFuncionalidade, PermissoesFuncionalidade, PermissoesFuncionalidadeServlet, RespostaPermissao, Usuario, UsuarioServlet
- Servlet URL(s): `/MuralhaDigital/ConfiguracaoInatividade`, `/MuralhaDigital/ConfiguracaoRadares`, `/MuralhaDigital/ConfiguracaoTempoOcrBlitz`, `/MuralhaDigital/PermissoesFuncionalidade`, `/MuralhaDigital/Usuarios`
- Ações (`acao`): buscarTempoInatividade, buscarConfigsInatividade, atualizarTempoInatividade, obterRaioRadaresMapa, configurarRaioRadaresMapa, obterTempoOCRBlitz, configurarTempoOCRBlitz, obterListaUsuariosAtivos, login, openview, out, login_google_token, acessoViaToken
- Tabelas/objetos em SQL: config_chave_valor, config_chave_valor_hist, config_grupo_permissao, fcn_verificaacesso, sis_grupo, sis_menu, sis_menu_direitos, sis_menu_graficos_infos, sis_menu_infos, sis_menu_relatorio_infos, sis_usuario, sis_usuario_grupo, tipo_alerta_ocorrencia, tipo_registro, token_acesso_mobilidade_urbana, v_grupo_supervisionado, v_sis_usuario
- Procedures/Funções/Views: fcn_verificaacesso, spu_ppv_sis_usuario_token, spu_ppv_sis_usuario_token_encerra, spu_ppv_sis_usuario_token_valida

## alerta

- Arquivos Java: 11 (pacote `muralha.digital.alerta`)
- Classes: Alerta, Alertas, AlertaServlet, AlertasVinculadosAtualizar, AlertaVinculadoAtualizar, AnotacaoContributiva, AnotacaoContributivaServlet, AnotacoesContributivas, MotivoDescarte, MotivoDescarteServlet, MotivosDescarte
- Servlet URL(s): `/MuralhaDigital/Alerta`, `/MuralhaDigital/Alerta/AnotacaoContributiva`, `/MuralhaDigital/Alerta/MotivoDescarte`
- Ações (`acao`): obterAlertaPorId, obterAlertasNaoTratadas, obterCoordAlerta, obtemAlerta, obterVeiculosMonitorados, obterAssinadoAlertaPorId, obterAlertasPendentesAssinatura, obterAberturaAtendimento, descartarAlerta, processarAlertasVinculados, marcarComoEmAtendimento, podeAtender, criarAlerta, atualizarAlertaModalAcao, obterAnotacaoContributivaPorIdAlerta, obterListaMotivosDescarte, obterMotivoDescartePorId
- Tabelas/objetos em SQL: alerta_veiculo, anotacao_contributiva, cad_veiculo_monitorado, fcn_obterdadosalertaocorrencia, motivo_descarte, sis_usuario, status_alerta, veiculo_monitorado, veiculo_tempo_real
- Procedures/Funções/Views: fcn_obterdadosalertaocorrencia, spu_obterdadosalertaocorrencia, spu_obternovosalertas

## anomalia

- Arquivos Java: 3 (pacote `muralha.digital.anomalia`)
- Classes: Anomalia, Anomalias, AnomaliaServlet
- Servlet URL(s): `/MuralhaDigital/Anomalia`

## areamonitorada

- Arquivos Java: 6 (pacote `muralha.digital.areamonitorada`)
- Classes: AreaMonitorada, AreaMonitoradaServlet, AreasMonitoradas, EquipamentoAreaMonitorada, EquipamentoDTO, EquipamentoDTOListWrapper
- Servlet URL(s): `/MuralhaDigital/AreaMonitorada`
- Ações (`acao`): buscarAreas, buscarPorNome, obterEquipamentos, cadastrar, excluir
- Tabelas/objetos em SQL: area_monitorada, equipamentos_area_monitorada, local_vigente

## assinatura

- Arquivos Java: 8 (pacote `muralha.digital.assinatura`)
- Classes: AssinaturaConfig, AssinaturaImagemService, AssinaturaImagemServlet, DadosAssinatura, KeystoreAutoGenerator, ResultadoValidacao, StatusAssinatura, TipoSolicitanteAssinatura
- Servlet URL(s): `/muralha-digital/assinatura/*`

## atendimento

- Arquivos Java: 9 (pacote `muralha.digital.atendimento`)
- Classes: Atendimento, AtendimentoAnexoServlet, Atendimentos, AtendimentoServlet, Documento, Documentos, Historico, Historicos, RegistroFato
- Servlet URL(s): `/MuralhaDigital/Anexo`, `/MuralhaDigital/Atendimento`
- Ações (`acao`): obterDocumentos, downloadDocumento, pdf, jpg, jpeg, png, obterOcorrencias, obterGuarnicoes, obterGuarnicoesStatus, obterTiposRegistroFato, obterVeiculosSinistradosRecuperados, obterHistorico, iniciarAtendimento, enviarGuarnicoes, encerrarAtendimento, anexarDocumentos, vincularBoletimAtendimento
- Tabelas/objetos em SQL: atendimento_documento, atendimento_guarnicao, atendimento_historico, atendimento_historico_tipo, registro_fato, registro_fato_endereco, registro_fato_endereco_evento, registro_fato_tipo, registro_fato_veiculo, sis_usuario
- Procedures/Funções/Views: spu_encerrar_atendimento, spu_obtem_ocorrencias

## blitz

- Arquivos Java: 20 (pacote `muralha.digital.blitz`)
- Classes: AntecedenteCriminal, BlitzAbordagem, BlitzAbordagemResultado, BlitzDigital, BlitzDocumento, BlitzDocumentoArquivo, Blitzes, BlitzImagemAbordagem, BlitzPessoaEnvolvida, BlitzServlet, HistoricoCPF, HistoricoPlaca, Locais, Local, Proprietario, TipoAlerta, TipoBlitz, VeiculoBlitzDigital, VeiculoBlitzDTO, VeiculosBlitzDigital
- Servlet URL(s): `/MuralhaDigital/Blitz`
- Ações (`acao`): listar, listarAtivas, listarBlitzAtivasAutomaticas, listarLocais, listarUsuariosAG, listarGuarnicoes, obterBlitz, listarLocaisBlitz, listarUsuariosBlitz, listarGuarnicoesBlitz, verificarTipoAssociacao, listarTiposAlerta, listarTiposAlertaBlitz, listarTiposEnvolvimento, listarSituacoesDocumento, listarTiposDocumento, obterAbordagem, listarAbordagensPorBlitz, listarPessoasAbordagem, listarDocumentosAbordagem, listarImagensAbordagem, listarAbordagensFiltro, obterAbordagemDetalhada, obterImagemDocumento, obterImagemAbordagem, downloadDocumento, listarRegistrosFato, obterDetalhesRegistroFato, listarBlitzAtivasPorLocal, obterInformacoesAlerta, verificarAbordagemPorAlerta, verificarAlertaBlitz, obterInformacoesBlitzOstensiva, buscarHistoricoPorCpf, buscarHistoricoPorPlaca, listarTiposBlitz, listarResultadosAbordagem, obterInformacoesVeiculoPorPlaca, listarIdsLocaisBlitz, geocodingMapa, obterPassagensReaisBlitz, cadastrar, atualizar, excluirBlitz, salvarAbordagem, encerrarBlitz, jpg, jpeg, png, pdf
- Tabelas/objetos em SQL: alerta_veiculo, antecedentes_criminais, blitz_abordagem, blitz_abordagem_imagem, blitz_abordagem_resultado, blitz_abordagem_status, blitz_digital, blitz_documento, blitz_documento_arquivo, blitz_guarnicao, blitz_local, blitz_pessoa_envolvida, blitz_tipo, blitz_tipo_alerta, blitz_usuario, cad_cor, cad_marca, cad_modelo, cad_tipo, cad_veiculo, cad_veiculo_monitorado, local_vigente, registro_fato, registro_fato_individuo, registro_fato_natureza, registro_fato_natureza_delituosa, registro_fato_passagem_veic, registro_fato_status, registro_fato_tipo, registro_fato_veiculo, sis_grupo, sis_usuario, sis_usuario_grupo, status_alerta, tipo_alerta_ocorrencia, veiculo_tempo_real
- Procedures/Funções/Views: spu_obterveiculosblitzwebsocket

## boletim

- Arquivos Java: 26 (pacote `muralha.digital.boletim`)
- Classes: Boletim, BoletimApreensao, BoletimApreensoes, BoletimCidades, BoletimCidadeServlet, BoletimDocumento, BoletimDocumentos, BoletimDocumentoServlet, BoletimIndividuo, BoletimIndividuos, BoletimIndividuosTipos, BoletimIndividuoTipo, BoletimIndividuoTipoServlet, BoletimLocais, BoletimLocal, BoletimServlet, BoletimSituacao, BoletimSituacaoServlet, BoletimSituacoes, BoletimTipos, BoletimTipoServlet, BoletimVeiculo, BoletimVeiculos, Boletins, Cidade, OcorrenciaTipo
- Servlet URL(s): `/MuralhaDigital/Boletim/Cidade`, `/MuralhaDigital/Boletim/Doducmento`, `/MuralhaDigital/Boletim/TipoIndividuo`, `/MuralhaDigital/Boletim`, `/MuralhaDigital/Boletim/Situacao`, `/MuralhaDigital/Boletim/Tipo`
- Ações (`acao`): obterPorId, downloadDocumento, obterLista, obterListaPorIds
- Tabelas/objetos em SQL: boletim_apreensao, boletim_documento, boletim_individuo, boletim_individuo_tipo, boletim_local, boletim_situacao, boletim_veiculo, ocorrencia_tipo, sis_usuario

## cidade

- Arquivos Java: 3 (pacote `muralha.digital.cidade`)
- Classes: Cidade, Cidades, CidadeServlet
- Servlet URL(s): `/MuralhaDigital/CidadesMinasGerais`
- Ações (`acao`): buscarCidades

## consulta

- Arquivos Java: 16 (pacote `muralha.digital.consulta`)
- Classes: AlertaOcorrencia, AlertaOcorrenciaServlet, AlertasOcorrencias, BaixaLogFtp, LinhasTempo, LinhaTempo, LinhaTempoServlet, StatusAlertaOcorrencia, StatusAlertaOcorrenciaServlet, StatusAlertasOcorrencias, TipoAlertaOcorrencia, TipoAlertaOcorrenciaServlet, TipoRegistro, TipoRegistroServlet, TiposAlertaOcorrencias, TiposRegistros
- Servlet URL(s): `/MuralhaDigital/AlertaOcorrencia`, `/MuralhaDigital/Ftp`, `/MuralhaDigital/LinhaTempo`, `/MuralhaDigital/AlertaOcorrencia/Status`, `/MuralhaDigital/AlertaOcorrencia/Tipo`, `/MuralhaDigital/AlertaOcorrencia/TipoRegistro`
- Ações (`acao`): consultaPorFiltrosTela, consultaPredefinida, QuantitativosAlertasOcorrencias, consultaAlertasPendCadMonitorado, consultaAlertasPorCadMonitorado, consultaAlertasPorVeiculo, obterLinhaTempo, obterListaStatus, obterListaStatusVinculado
- Tabelas/objetos em SQL: alerta_veiculo, cad_veiculo_monitorado, fcn_obteralertasalt, local_vigente, status_alerta, status_ocorrencia, tipo_alerta_ocorrencia, tipo_registro, v_status_alerta_vinculado, veiculo_tempo_real
- Procedures/Funções/Views: fcn_obteralertas, fcn_obteralertasalt, fcn_obterocorrencias, fcn_obterocorrenciasalt, spu_obterdadoslinhatempo

## correlacaoplaca

- Arquivos Java: 2 (pacote `muralha.digital.correlacaoplaca`)
- Classes: CorrecaoPlacaServlet, VeiculoRegistro
- Servlet URL(s): `/MuralhaDigital/CorrecaoPlaca`
- Ações (`acao`): listarVeiculos, listarIntegrantes, atualizarPlacas
- Tabelas/objetos em SQL: veiculo_tempo_real, veiculo_tempo_real_correcao, veiculo_tempo_real_imagem

## dispositivo

- Arquivos Java: 5 (pacote `muralha.digital.dispositivo`)
- Classes: Camera, CameraEquipamento, DispositivoEquipamento, DispositivoEquipamentoServlet, DispositivosEquipamentos
- Servlet URL(s): `/MuralhaDigital/DispositivoEquipamento`
- Ações (`acao`): obterListaEquipamentos, obterListaEquipamentosPorCategoria, obterListaEquipamentosSentido, ObterCamerasPorNumeroSerie
- Tabelas/objetos em SQL: configuracao_equipamento, configuracao_equipamento_pista, grupo_equipamento, local_vigente, v_equipamento_cameras, v_equipamento_cameras_todas
- Procedures/Funções/Views: spu_getfluxoequipamentoporcategoria

## equipamento

- Arquivos Java: 3 (pacote `muralha.digital.equipamento`)
- Classes: Equipamento, Equipamentos, EquipamentoServlet
- Servlet URL(s): `/MuralhaDigital/Equipamento`
- Ações (`acao`): obterListaEquipamentos, obterListaEquipamentosMunReg, obterListaMunicipiosEquipamentos, obterListaRegioesEquipamentos
- Tabelas/objetos em SQL: cad_regiao, local_vigente, v_municipios_equipamentos

## google

- Arquivos Java: 1 (pacote `muralha.digital.google`)
- Classes: LoginGoogle
- Servlet URL(s): `/callback`
- Tabelas/objetos em SQL: sis_usuario, sis_usuario_grupo

## guarnicao

- Arquivos Java: 4 (pacote `muralha.digital.guarnicao`)
- Classes: Guarnicao, GuarnicaoDiario, GuarnicaoServlet, Guarnicoes
- Servlet URL(s): `/MuralhaDigital/Guarnicao`
- Ações (`acao`): listar, listarIntegrantes, buscarPorId, buscarDetalhesPorId, listarGuarnicoesDiariasPorGuarnicao, cadastrar, cadastrarInfoDiaria, atualizarTelefone, atualizarGuarnicao, deletarGuarnicao
- Tabelas/objetos em SQL: guarnicao_diario, guarnicao_integrante, guarnicao_meio_deslocamento, sis_usuario

## juncaobase

- Arquivos Java: 2 (pacote `muralha.digital.juncaobase`)
- Classes: JuncaoBaseCSV, JuncaoBaseServlet
- Servlet URL(s): `/MuralhaDigital/JuncaoBase`
- Procedures/Funções/Views: spu_importarbase

## mapacalor

- Arquivos Java: 3 (pacote `muralha.digital.mapacalor`)
- Classes: MapaCalorPonto, MapaCalorPontos, MapaCalorServlet
- Servlet URL(s): `/MuralhaDigital/MapaCalor`
- Tabelas/objetos em SQL: alerta_veiculo, local_vigente, veiculo_sumarizado, veiculo_tempo_real

## mapadispositivos

- Arquivos Java: 1 (pacote `muralha.digital.mapadispositivos`)
- Classes: MapaDispositivoServlet
- Servlet URL(s): `/MuralhaDigital/MapaDispositivosEquipamentos`
- Ações (`acao`): obterDispositivosEquipamentos, obterDispositivosEquipamentosMisto, completo, simplificado, misto

## mapadispositivos3d

- Arquivos Java: 1 (pacote `muralha.digital.mapadispositivos3d`)
- Classes: MapaDispositivos3DServlet
- Servlet URL(s): `/MuralhaDigital/MapaDispositivos3D`
- Ações (`acao`): obterMapaDispositivos3D

## mapainterativo

- Arquivos Java: 3 (pacote `muralha.digital.mapainterativo`)
- Classes: Atendimento, GuarnicaoLocalizacao, MapaInterativoServlet
- Servlet URL(s): `/MuralhaDigital/MapaInterativo`
- Ações (`acao`): obterAtendimentos, obterGuarnicaoLocalizacao
- Tabelas/objetos em SQL: agente_localizacao_atual, guarnicao_integrante, registro_fato, registro_fato_endereco, registro_fato_status, registro_fato_tipo, sis_usuario

## monitorado

- Arquivos Java: 21 (pacote `muralha.digital.monitorado`)
- Classes: ClassesVeiculo, ClasseVeiculoEntidade, CorEntidade, Cores, HorarioPermitido, LocalTimeAdapter, MarcaEntidade, Marcas, ModeloEntidade, Modelos, VeicMonitorado, VeiculoAuxiliarServlet, VeiculoMonitorado, VeiculoMonitoradoCompletoDTO, VeiculoMonitoradoEntidade, VeiculoMonitoradoEquipamentoEntidade, VeiculoMonitoradoGrupoEntidade, VeiculoMonitoradoHistoricoEntidade, VeiculoMonitoradoPeriodoEntidade, VeiculoMonitoradoServlet, VeiculosMonitorados
- Servlet URL(s): `/MuralhaDigital/VeiculoAuxiliar`, `/MuralhaDigital/Monitorado`
- Ações (`acao`): buscarCores, buscarMarcas, buscarModelos, buscarClassesVeiculo, inserir, atualizar, encerrar, exportarCadastrosAtivos, obterLista, ObterPlacasComAlerta, obterPorId, obterIdsMonitorarSomenteEste
- Tabelas/objetos em SQL: cad_veiculo_monitorado, cad_veiculo_monitorado_equipamento, cad_veiculo_monitorado_grupo, cad_veiculo_monitorado_historico, cad_veiculo_monitorado_periodo, classe_veiculo, registro_fato_veiculo, sis_usuario, tipo_alerta_ocorrencia

## monitoramento

- Arquivos Java: 11 (pacote `muralha.digital.monitoramento`)
- Classes: Camera, CameraMonitoramentoAoVivo, CamerasMonitoramentoAoVivo, EquipamentoCameras, MonitoramentoAoVivo, MonitoramentoAoVivoServlet, MonitoramentosAoVivo, VerVideoMonitoramentoServlet, VideoMonitoramento, VideoMonitoramentoServlet, VideosMonitoramento
- Servlet URL(s): `/MuralhaDigital/ConfigMonAoVivo`, `/MuralhaDigital/VideoMonitoramento/Video`, `/MuralhaDigital/VideoMonitoramento`
- Ações (`acao`): salvar, atualizarGrupoExibicao, obterConfigVigente, obterCamerasMonAoVivo, obterCamerasPorIdLocal, verVideoPorEndereco, verVideoPorListaEndereco, verVideoPorListaEnderecoTemp, consultaVideosMonitoramento
- Tabelas/objetos em SQL: v_config_vigente_monitoramento_ao_vivo, video_monitoramento
- Procedures/Funções/Views: spu_obtercamerasmonitoramentoaovivo, spu_obterlistavideosmonitoramentoexibicao, spu_salvar_config_monitoramento_ao_vivo

## motivosolicitacaorelatorio

- Arquivos Java: 3 (pacote `muralha.digital.motivosolicitacaorelatorio`)
- Classes: MotivoSolicitacaoRelatorio, MotivoSolicitacaoRelatorioServlet, MotivosSolicitacoesRelatorios
- Servlet URL(s): `/MuralhaDigital/MotivoSolicitacaoRelatorio`
- Ações (`acao`): registrarMotivoSolicitacaoRelatorio
- Tabelas/objetos em SQL: motivo_solicitacao_relatorio

## notificacao

- Arquivos Java: 23 (pacote `muralha.digital.notificacao`)
- Classes: ConfiguracaoSom, ConfiguracaoSons, ConfiguracaoSonsServlet, GrupoNotificacao, GrupoNotificacaoEmail, GrupoNotificacaoServlet, GruposNotificacao, JobEnviaEmailAlerta, JobEnviaEmailOcorrencia, JobEnviaSmsAlerta, JobEnviaSmsOcorrencia, Notificacao, NotificacaoServlet, Notificacoes, RecuperacaoSenha, ServicoEmailMuralha, ServicoSMS, StatusNotificacao, TipoNotificacao, TipoNotificacaoServlet, TiposNotificacao, ViewGrupoAlertas, ViewGruposAlertas
- Servlet URL(s): `/MuralhaDigital/ConfiguracaoTempo`, `/MuralhaDigital/GrupoNotificacao`, `/MuralhaDigital/Notificacao`, `/MuralhaDigital/TipoNotificacao`
- Ações (`acao`): obterConfigTempos, obterSonsAtivo, obterAlertaContinuo, obterTempoMaximoEmissao, obterTiposAlertas, obterPrioridade, configurarAlertaSonoro, configurarTempoMaximoEmissao, cadastrarPrioridade, obterGruposEmail, obterGruposSMS, obterGruposPopup, obterGruposEmailPorIdOcorrencia, obterGruposSmsPorIdOcorrencia, obterGruposPopupPorIdOcorrencia, obterGruposPorIdGrupoETipoNotificacao, buscarGruposView, buscarGruposSelecionados, obterStatusAgenteGuarnicao, configAgenteGuarnicao, listar, enviarsms, enviarEmail, buscarusuario, validartoken, atualizarsenha
- Tabelas/objetos em SQL: alerta_notificacao, codigo_verificacao, config_chave_valor, config_chave_valor_hist, config_grupo_permissao, ocorrencia_notificacao, sis_grupo, sis_usuario, sis_usuario_grupo, sis_usuario_recupera_senha, tipo_alerta_ocorrencia, tipo_notificacao, tipo_registro, v_grupo_alertas
- Procedures/Funções/Views: spu_obternotificacoespendentes

## ocorrencia

- Arquivos Java: 5 (pacote `muralha.digital.ocorrencia`)
- Classes: Ocorrencias, OcorrenciaServlet, StatusOcorrencia, StatusOcorrencias, StatusOcorrenciaServlet
- Servlet URL(s): `/MuralhaDigital/Ocorrencia`, `/MuralhaDigital/Ocorrencia/Status`
- Ações (`acao`): gerarOcorrencia, salvarConfigAcaoProcedimento, finalizarOcorrencia, obterListaStatusFinalizacao
- Tabelas/objetos em SQL: ocorrencia_notificacao, ocorrencia_notificacao_historico, tipo_ocorrencia_status, v_status_ocorrencia_finalizacao
- Procedures/Funções/Views: spu_gerar_ocorrencia

## ocorrencialigacao

- Arquivos Java: 7 (pacote `muralha.digital.ocorrencialigacao`)
- Classes: OcorrenciaLigacao, OcorrenciaLigacaoServlet, OcorrenciaLigacaoTipo, OcorrenciaLigacaoTipos, OcorrenciaLigacaoTipoSolicitante, OcorrenciaLigacaoTipoSolicitantes, OcorrenciasLigacao
- Servlet URL(s): `/MuralhaDigital/OcorrenciaLigacao`
- Ações (`acao`): obterTiposSolicitante, obterTiposOcorrencias, cadastrarOcorrencia
- Tabelas/objetos em SQL: ocorrencia_ligacao, ocorrencia_ligacao_tipo_solicitante, ocorrencia_tipo

## painelInformacoes

- Arquivos Java: 13 (pacote `muralha.digital.painelInformacoes`)
- Classes: Camera, CameraResponse, Cameras, CameraServlet, DispositivoSimples, DispositivoSimplesResponse, IndicadorDTO, LeituraPlacasResponse, RecursoServidorDTO, RecursoServidorResponse, TotalInformacaoResponse, TotalInformacaoServlet, TotalInformacoes
- Servlet URL(s): `/MuralhaDigital/PainelInformacao/Camera`, `/MuralhaDigital/PainelInformacao/TotalInformacoes`
- Ações (`acao`): ObterCamerasPorDispositivoId, buscarLeituraPlacas, ObterStatusCamera, ObterRecursoServidor, ObterTotalInformacoes
- Tabelas/objetos em SQL: cad_veiculo_monitorado, eventos_csx, eventos_csx_desc_proprietario, local_vigente, registro_fato, registro_fato_documento, registro_fato_endereco, registro_fato_historico, registro_fato_individuo, registro_fato_link, registro_fato_objeto, registro_fato_veiculo, v_equipamento_cameras_todas, veiculo_tempo_real, veiculo_tempo_real_imagem

## perfilcomportamental

- Arquivos Java: 2 (pacote `muralha.digital.perfilcomportamental`)
- Classes: PerfilComportamental, PerfilComportamentalServlet
- Servlet URL(s): `/MuralhaDigital/PerfilComportamental`
- Ações (`acao`): info, pordia, porhora, porpcl, permanencia, passagensindividuais
- Tabelas/objetos em SQL: fcn_perfil_comportamental_estadia_por_manchas, fcn_perfil_comportamental_info_veiculo, fcn_perfil_comportamental_passagens_por_dia_com_mancha, fcn_perfil_comportamental_passagens_por_dia_hora_com_mancha, fcn_perfil_comportamental_passagens_por_pcl_com_mancha, local_vigente, veiculo_tempo_real
- Procedures/Funções/Views: fcn_perfil_comportamental_estadia_por_manchas, fcn_perfil_comportamental_info_veiculo, fcn_perfil_comportamental_passagens_por_dia_com_mancha, fcn_perfil_comportamental_passagens_por_dia_hora_com_mancha, fcn_perfil_comportamental_passagens_por_pcl_com_mancha

## perguntasRespostas

- Arquivos Java: 6 (pacote `muralha.digital.perguntasRespostas`)
- Classes: AlertaQuestionario, AlertaQuestionarioResposta, AlertaQuestionarioServlet, AlertasQuestionarios, AlertaUsuarioVisualiza, RespostaQuestionarioDTO
- Servlet URL(s): `/MuralhaDigital/AlertaQuestionario`
- Ações (`acao`): obterQuestionarios, verificarQuestionarioObrigatorio, obterRespostas, registrarAcessoUsuario, registrarRespostasUsuario
- Tabelas/objetos em SQL: alerta_questionario, alerta_questionario_resposta, alerta_usuario_visualiza, config_chave_valor, sis_usuario, tipo_alerta_ocorrencia

## pesquisaRapida

- Arquivos Java: 11 (pacote `muralha.digital.pesquisaRapida`)
- Classes: AlertaResponse, AutocompleteResponse, BoletimResponse, PesquisaRapidaAutocompleteServlet, PesquisaRapidaRequest, PesquisaRapidaResponse, PesquisaRapidaServlet, PesquisasRapidas, RegistroFatoResponse, TipoConsulta, VeiculoMonitoradoResponse
- Servlet URL(s): `/MuralhaDigital/PesquisaRapida/Autocomplete`, `/MuralhaDigital/PesquisaRapida`
- Ações (`acao`): nome
- Tabelas/objetos em SQL: cad_veiculo_monitorado, registro_fato, registro_fato_endereco, registro_fato_individuo, registro_fato_veiculo

## pontointeresse

- Arquivos Java: 3 (pacote `muralha.digital.pontointeresse`)
- Classes: PontoInteresse, PontoInteresseServlet, PontosInteresse
- Servlet URL(s): `/MuralhaDigital/PontoInteresse`
- Ações (`acao`): salvaPontoInteresse, obterPontosInteresse
- Tabelas/objetos em SQL: local_vigente, ponto_interesse, ponto_interesse_equipamentos, ponto_interesse_tipos

## registroDeFato

- Arquivos Java: 90 (pacote `muralha.digital.registroDeFato`)
- Classes: AtualizarRegistroFato, Boletim, BoletimApreensao, BoletimApreensaoDTO, BoletimDTO, BoletimSituacao, BoletimSituacaoServlet, BoletimSituacoes, Boletins, CadastroRegistroFatoDTO, CadastroVeiculoMonitorado, Cidade, Cidades, CidadeServlet, EnvolvidoDTO, EnvolvidosDiferencialDTO, Fato, FatoHistoricoService, FatoSemBoletimCompletoDTO, GrupoRegistroDeFato, GrupoRegistroDeFatos, GrupoRegistroDeFatoServlet, GruposDiferencialDTO, GruposUsuariosResponse, Localizacao, ObjetosDiferencialDTO, RegistroDeFato, RegistroDeFatoAnotacao, RegistroDeFatoAnotacoes, RegistroDeFatoComBoletimDTO, RegistroDeFatoDocumento, RegistroDeFatoDocumentoDTO, RegistroDeFatoDocumentos, RegistroDeFatoDocumentoServlet, RegistroDeFatoDTO, RegistroDeFatoEndereco, RegistroDeFatoEnderecoDTO, RegistroDeFatoEnderecoEvento, RegistroDeFatoEnderecoEventos, RegistroDeFatoEnderecoEventoServlet, RegistroDeFatoEnderecos, RegistroDeFatoHistorico, RegistroDeFatoHistoricoDTO, RegistroDeFatoHistoricoResponse, RegistroDeFatoHistoricos, RegistroDeFatoHistoricoServlet, RegistroDeFatoIndividuo, RegistroDeFatoIndividuoDTO, RegistroDeFatoIndividuos, RegistroDeFatoIndividuoTipo, RegistroDeFatoIndividuoTipos, RegistroDeFatoIndividuoTipoServlet, RegistroDeFatoLink, RegistroDeFatoLinkDTO, RegistroDeFatoLinks, RegistroDeFatoNatureza, RegistroDeFatoNaturezaDelituosa, RegistroDeFatoNaturezaDelituosas, RegistroDeFatoNaturezaDelituosaServlet, RegistroDeFatoNaturezas, RegistroDeFatoNaturezaServlet, RegistroDeFatoObjeto, RegistroDeFatoObjetoDTO, RegistroDeFatoObjetos, RegistroDeFatoPassagemVeiculo, RegistroDeFatos, RegistroDeFatoServlet, RegistroDeFatoSituacao, RegistroDeFatoSituacaoServlet, RegistroDeFatoSituacoes, RegistroDeFatoTipo, RegistroDeFatoTipos, RegistroDeFatoTipoServlet, RegistroDeFatoUsuarioGrupo, RegistroDeFatoUsuarioGrupoDTO, RegistroDeFatoUsuarioGrupos, RegistroDeFatoVeiculo, RegistroDeFatoVeiculoDTO, RegistroDeFatoVeiculos, RegistroFatoAnotacaoDTO, RegistroFatoDTO, RegistroFatoObjeto, RegistroFatoPassagemVeiculo, RegistrosPassagensVeiculos, UsuarioGrupo, UsuarioGrupoRegistroDeFato, UsuarioRegistroDeFato, VeiculoDTO, VeiculosDiferencialDTO, VeiculoTempoReal
- Servlet URL(s): `/MuralhaDigital/RegistroDeFato/Boletim/Situacao`, `/MuralhaDigital/RegistroDeFato/Cidade`, `/MuralhaDigital/RegistroDeFato/Grupo`, `/MuralhaDigital/RegistroDeFato/Doducmento`, `/MuralhaDigital/RegistroDeFato/EnderecoEvento`, `/MuralhaDigital/RegistroDeFato/Historico`, `/MuralhaDigital/RegistroDeFato/IndividuoTipo`, `/MuralhaDigital/RegistroDeFatoNaturezaDelituosa`, `/MuralhaDigital/RegistroDeFato/Natureza`, `/MuralhaDigital/RegistroDeFato`, `/MuralhaDigital/RegistroDeFato/Situacao`, `/MuralhaDigital/RegistroDeFato/Tipo`
- Ações (`acao`): obterTodosPorUsuarioId, obterTodos, obterPorId, downloadDocumento, obterHistoricoPorId, obterLista, tipoDescricao, statusDescricao, dataEvento, dataCriacao, dataModificacao, dataEncerramento, naturezaFato, privadoFato, nome_cpf, veiculo_placa, endereco, boletimSituacao, cadastrarSemBoletim, cadastrarComBoletim, edicaoComBoletim, atualizarSemBoletim, cadastrarPassagem, EncerrarMonitoramento, obterListaComBoletim, obterListaSemBoletim, buscarFatoSemBoletimPorId, buscarFatoComBoletimPorId, buscarFatoAmbosPorId, consultarVeiculosMonitorados
- Tabelas/objetos em SQL: boletim_apreensao, boletim_situacao, cad_veiculo_monitorado, registro_fato, registro_fato_anotacao, registro_fato_documento, registro_fato_endereco, registro_fato_endereco_evento, registro_fato_historico, registro_fato_individuo, registro_fato_individuo_tipo, registro_fato_link, registro_fato_natureza, registro_fato_natureza_delituosa, registro_fato_objeto, registro_fato_passagem_veic, registro_fato_status, registro_fato_tipo, registro_fato_usuario_grupo, registro_fato_veiculo, sis_grupo, sis_usuario, sis_usuario_grupo, veiculo_tempo_real, vw_registro_fato_origem
- Procedures/Funções/Views: vw_registro_fato_origem

## relatorios

- Arquivos Java: 52 (pacote `muralha.digital.relatorios`)
- Classes: BlitzEletronicaVeicIrregular, CalendarioIntensidade, CalendariosIntensidades, ContagemPassagensPorLocalServlet, DadosRelatorios, Dashboards, DashboardServlet, DashboardValidacao, Grafico, Graficos, GraficoServlet, GraficoValidacao, ItemRelatorios, PassagensSequenciaisServlet, pesquisaVeiculosRealizados, pesquisaVeiculosRealizadosServlet, RelatorioAcoesAlarmes, RelatorioAcoesAlarmesServlet, RelatorioAlertasDetalhado, RelatorioAuditoria, RelatorioAuditoriaServlet, RelatorioDetalhadoAlertasServlet, RelatorioDistribuicaoFaixaVel, RelatorioDistribuicaoFatos, RelatorioDistribuicaoPorteVeic, RelatorioDistribuicaoTiposFatosServlet, RelatorioEstatisticoAlarmesServlet, RelatorioEstatisticoFatoServlet, RelatorioEstatisticoTipoFatoServletMapa, RelatorioEstatisticoTipoFatosServlet, RelatorioEvolucaoSemanal, RelatorioExportarImagens, RelatorioExtratoAlertasOcorrencias, RelatorioFluxoMensalPorClassificacao, RelatorioFluxoPassagensVeicularesServlet, RelatorioFluxoVeicularRotaServlet, RelatorioOcorrenciasDetalhado, RelatorioPendenciasRegistroFato, RelatorioPermanenciaVeiculoAreaMonitoradaNew, RelatorioPlacasVeiculares, RelatorioPlacasVeicularesServlet, RelatorioQuantidadePassagens, RelatorioSessao, RelatorioSessaoUsuario, RelatorioSessaoUsuarioServlet, RelatoriosExportarImagensServlet, RelatorioTaxaOcupacaoVia, RelatorioUtils, RelatorioValidacao, RelatorioVeiculosMonitoradosModeloServlet, RelatorioVelocidadeMedia, TotalizadorCategoria
- Servlet URL(s): `/MuralhaDigital/BlitzEletronica/Imprimir`, `/relatorio/ContagemPassagensPorLocal`, `/MuralhaDigital/Dashboard`, `/MuralhaDigital/Grafico`, `/relatorio/PassagensSequenciais`, `/Relatorio/AlertasDetalhado`, `/muralha-digital/relatorio/RelatorioDetalhadoAlertas`, `/Relatorio/DistribuicaoFaixaVelocidade`, `/MuralhaDigital/RelatorioDistribuicaoFatos`, `/Relatorio/DistribuicaoPorteVeicular`, `/relatorio/RelatorioDistribuicaoTiposFatosServlet`, `/muralha-digital/relatorio/RelatorioEstatisticoAlarmes`, `/relatorio/RelatorioEstatisticoFatoServlet`, `/relatorio/RelatorioEstatisticoTipoFatoMapa`, `/relatorio/RelatorioEstatisticoTipoFatos`, `/MuralhaDigital/RelatorioEvolucaoSemanal`, `/Relatorio/ExtratoAlertaOcorrencia`, `/Relatorio/FluxoMensalPorClassificacao`, `/relatorio/RelatorioFluxoPassagensVeiculares`, `/muralha-digital/relatorio/RelatorioFluxoVeicularRota`, `/Relatorio/OcorrenciasDetalhado`, `/MuralhaDigital/RelatorioPendenciasRegistroFato`, `/relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew`, `/Relatorio/QuantidadePassagens`, `/MuralhaDigital/Relatorios/SessaoUsuario`, `/Relatorio/TaxaOcupacaoVia`, `/muralha-digital/relatorio/RelatorioVeiculosMonitoradosModelo`, `/Relatorio/VelocidadeMedia`, `/MuralhaDigital/Relatorios/AcoesAlarmes`, `/MuralhaDigital/Relatorios/Auditoria`, `/MuralhaDigital/Relatorios/ExportarImagens`, `/MuralhaDigital/Relatorios/PesquisasVeiculos`, `/MuralhaDigital/RelatorioPlacasVeiculares`
- Ações (`acao`): obterTotalizadorCategoria, obterCalendarioIntensidade, ocorrenciaPorteVeicular, veiculosPorteVeicular, veiculosPorPeriodo, velocidadeMediaPorPeriodo, ocorrenciaFaixaVel, comparativoPassagensInfracoes, comparativoAnoAnterior, comparativoMesAnterior, comparativoEvolucaoClassificacao, distribuicaoPorFaixa, comparativoPrevisaoFuturo, rankingPorFaixa, fluxoVelMediaPorHorarioMapa, fluxoVelMediaPorMinutoMapa, infracoesPorDiaMapa, buscarDados, exportarExcel, buscarTiposFato, gerarRelatorio, carregarLocais, getAreasMonitoradas, navegacao, getOperadores, getRelatorio, getUsuarios
- Tabelas/objetos em SQL: alerta_veiculo, anotacao_contributiva, area_monitorada, fcn_getrelatorio6medicaofluxoveicular, fcn_getrelatoriofluxoveicular, fcn_getreldistribuicaofaixavelocidade, fcn_getreldistribuicaoporteveicular, fcn_getrelfluxomensalporclassificacao, fcn_obterinforegistrofatoalertaporplaca, local_vigente, motivo_descarte, motivo_solicitacao_relatorio, registro_fato, registro_fato_tipo, relatorio_imagens_exportadas, sis_log, sis_usuario, veiculo_tempo_real, veiculo_tempo_real_imagem
- Procedures/Funções/Views: fcn_getrelatorio6medicaofluxoveicular, fcn_getrelatoriofluxoveicular, fcn_getreldistribuicaofaixavelocidade, fcn_getreldistribuicaoporteveicular, fcn_getrelfluxomensalporclassificacao, fcn_obterinforegistrofatoalertaporplaca, sp_relatoriodistribuicaofatos, spu_contagempassagensporlocal, spu_getcalendariointensidade, spu_getcomparativofluxoinfracao, spu_getcomparativoperiodoanoanterior, spu_getcomparativoperiodomesanterior, spu_getdistribuicaoporfaixarolagem, spu_getevolucaoporclassificacao, spu_getfluxodiahorariografico, spu_getfluxodiaminutografico, spu_getgraficoprevisaofutura, spu_getinfracoesdiagrafico, spu_getqtdefluxofaixavelocidade, spu_getqtdefluxoporporteveicular, spu_getrankingporfaixarolagem, spu_gettotalizadorporcategoria, spu_getveiculosporclassificacao, spu_getveiculosporperiodo, spu_getvelociademediaporperiodo, spu_passagenssequenciais, spu_relatorio_placas_veiculares, spu_relatoriodeevolucaosemanaldefatos, spu_relatoriodependenciasnosregistrosdefato, spu_relatoriodistribuicaofatos, spu_relatorioestatisticoalarmesjson, spu_relatorioestatisticoportipofatomapa, spu_relatorioestatisticoportipofatomapanew, spu_relatoriofluxopassagensveiculares, spu_relatoriofluxoveicularrota, spu_relatorioirregularidadesdashboard, spu_relatoriopermanenciaveiculoareamonitoradadetalhado, spu_relatorioveiculosmonitoradosmodelo, spu_sp_calculaparticipacaofatosporsemana

## temporeal

- Arquivos Java: 2 (pacote `muralha.digital.temporeal`)
- Classes: BlitzEletronica, VeiculoTempoReal
- Servlet URL(s): `/MuralhaDigital/BlitzEletronica/VeiculoIrregular`, `/MuralhaDigital/VeiculoTempoReal`
- Tabelas/objetos em SQL: config_envio_tempo_real_equipamento, veiculo_tempo_real

## util

- Arquivos Java: 6 (pacote `muralha.digital.util`)
- Classes: Constantes, EncurtadorURL, Paginacao, RespostaRequisicaoXML, Resultado, Utils

## veiculo

- Arquivos Java: 23 (pacote `muralha.digital.veiculo`)
- Classes: ClassesVeiculo, ClasseVeiculo, ClasseVeiculoServlet, ImgVeiculoTempoReal, PassagemRelacionada, PassagensRelacionadasDTO, Veiculo, VeiculoAlerta, VeiculoBlitz, VeiculoDeCarga, VeiculoDeCargaDetalhes, VeiculoDeCargaRelatorio, VeiculoDeCargaServlet, VeiculoDeCargaValidacao, VeiculoImagem, VeiculoImagemServlet, VeiculoImagens, Veiculos, VeiculosAlerta, VeiculosBlitz, VeiculosDeCarga, VeiculoServlet, VeiculoValidacao
- Servlet URL(s): `/MuralhaDigital/Veiculo/Classificacao`, `/MuralhaDigital/VeiculoDeCarga`, `/MuralhaDigital/Veiculo`, `/MuralhaDigital/Veiculo/Imagem`, `/MuralhaDigital/Veiculo/Imagem/Lista`
- Ações (`acao`): obterListaClassificacoes, consultaPorFiltrosTela, obterListaCores, obterListaUf, obterListaTiposVeiculo, obterLocalidades, consultaPorFiltrosTelaMapa, obterVeiculoPorId, exportarConsulta, obterDadosUsuario, obterListaMarcas, obterListaModelos, exportarConsultaSelecionadosManual, exportarConsultaSPU, null, obterVeiculoMapaPorIdAlvo, verificarPlaca, obterPassagensRelacionadas, alterarPlaca, registrarExportacaoImagem, obterListaImagensPorIdVeiculo
- Tabelas/objetos em SQL: alerta_veiculo, cad_cor, cad_localidade, cad_marca, cad_marca_cet, cad_tipo, classe_veiculo, fcn_obterimagensobjalertavinculado, registro_fato_veiculo, relatorio_imagens_exportadas, tipo_alerta_ocorrencia, v_veiculo_tempo_real, veiculo_tempo_real, veiculo_tempo_real_imagem
- Procedures/Funções/Views: fcn_obterimagensobjalertavinculado, spu_obterpassagensveiculomapaporidalvo, spu_obterveiculosblitzeletronica, spu_obterveiculosgtwporfiltros, spu_obterveiculosporfiltros, spu_obterveiculostemporeal, spu_obterveiculostemporealhistorico

## veiculosCorrelacionados

- Arquivos Java: 2 (pacote `muralha.digital.veiculosCorrelacionados`)
- Classes: VeiculosCorrelacionados, VeiculosCorrelacionadosServlet
- Servlet URL(s): `/MuralhaDigital/VeiculosCorrelacionados`
- Ações (`acao`): detalhes, correlacionados, passagens
- Tabelas/objetos em SQL: fcn_obterinfoalerta, fcn_obterinfoantecedentesproprietarioporplaca, fcn_obterinfoboletimocorrenciaalertaporplaca, fcn_obterinfoproprietarioveiculo, fcn_obterinforegistrofatoporplaca, fcn_obterinfoveiculo, veiculo_tempo_real, veiculo_tempo_real_imagem
- Procedures/Funções/Views: fcn_obterinfoalerta, fcn_obterinfoantecedentesproprietarioporplaca, fcn_obterinfoboletimocorrenciaalertaporplaca, fcn_obterinfoproprietarioveiculo, fcn_obterinforegistrofatoporplaca, fcn_obterinfoveiculo, spu_correlacionamento_placas

## websocket

- Arquivos Java: 3 (pacote `muralha.digital.websocket`)
- Classes: Cliente, ClienteSessoes, ControleAcessoEnvio


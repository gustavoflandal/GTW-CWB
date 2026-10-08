# Referência — Telas do Muralha Digital (front-end `/muralha-digital/pages/*`)

Gerado por varredura. Para cada diretório de página: JSPs, JS/CSS próprios e endpoints chamados pelo front-end (strings `/MuralhaDigital/...`, `/rest/...`, `/ajax/...`). Referência de URL: `/muralha-digital/pages/<diretório>/<arquivo>.jsp`.

## alerta-tratativa

- Caminho: `muralha-digital/pages/alerta-tratativa` · 17 arquivos
- JSPs: modal-alerta-acao.jsp, modal-alerta-anotacao.jsp, modal-alerta-descartar.jsp, modal-encerrar-monitorado.jsp, modal-ocorrencia-finalizar.jsp, tratar-alerta.jsp, tratar-alertas-vinculados.jsp
- Títulos (`<title>`): tratar-alerta: GTW - Alerta · tratar-alertas-vinculados: GTW - Alertas Vinculados
- JS próprios: js/modal-alerta-acao.js, js/modal-alerta-descartar.js, js/modal-anotacao-contributiva.js, js/modal-encerrar-monitorado.js, js/modal-ocorrencia-finalizar.js, js/tratar-alerta.js, js/tratar-alertas-vinculados.js
- CSS próprios: css/modal-alerta-acao.css, css/modal-alerta-anotacao.css, css/tratar-alerta.css
- Endpoints chamados: `/MuralhaDigital/Blitz`, `/MuralhaDigital/Monitorado`, `/MuralhaDigital/PermissoesFuncionalidade`

## assinatura

- Caminho: `muralha-digital/pages/assinatura` · 14 arquivos
- JSPs: assinar-content.jsp, assinar-imagem.jsp, base-template.jsp, painel-historico.jsp, painel-resultado.jsp, validacao-content.jsp, validacao-imagem.jsp
- JS próprios: js/client.js, js/controller.js, js/download.js, js/loading.js, js/resultados.js, js/utils.js
- CSS próprios: css/validacao-imagem.css
- Endpoints chamados: `/muralha-digital/assinatura/assinar`, `/muralha-digital/assinatura/verificar`

## atendimento-ocorrencias

- Caminho: `muralha-digital/pages/atendimento-ocorrencias` · 4 arquivos
- JSPs: abrir-ocorrencia.jsp, atendimento-ocorrencias.jsp
- Títulos (`<title>`): abrir-ocorrencia: Abrir Alerta · atendimento-ocorrencias: Atendimento de ocorrências
- JS próprios: assets/js/atendimento-ocorrencia.js
- CSS próprios: assets/css/atendimento-ocorrencias.css
- Endpoints chamados: `/MuralhaDigital/Alerta`, `/MuralhaDigital/Anexo`, `/MuralhaDigital/Atendimento`

## blitz

- Caminho: `muralha-digital/pages/blitz` · 7 arquivos
- JSPs: componente-mapa-radares.jsp, listagem-blitz.jsp, modal-blitz.jsp
- Títulos (`<title>`): listagem-blitz: Listagem de Blitz Digital
- JS próprios: assets/js/componente-mapa-radares.js, assets/js/listagem-blitz.js, assets/js/modal-blitz.js
- CSS próprios: assets/css/listagem-blitz.css
- Endpoints chamados: `/MuralhaDigital/Blitz`, `/MuralhaDigital/ConfiguracaoRadares`

## blitz-abordagem

- Caminho: `muralha-digital/pages/blitz-abordagem` · 15 arquivos
- JSPs: criar-abordagem.jsp, detalhes-abordagem.jsp, listagem-abordagem.jsp, modal-historico-cpf.jsp, modal-historico-placa.jsp, modal-motivo-liberacao.jsp
- Títulos (`<title>`): criar-abordagem: Nova Abordagem · detalhes-abordagem: Detalhes da Abordagem · listagem-abordagem: Histórico de Abordagens
- JS próprios: assets/js/criar-abordagem.js, assets/js/detalhes-abordagem.js, assets/js/historico-cpf.js, assets/js/historico-placa.js, assets/js/listagem-abordagem.js, assets/js/localizacao-helper.js
- CSS próprios: assets/css/criar-abordagem.css, assets/css/detalhes-abordagem.css, assets/css/listagem-abordagem.css
- Endpoints chamados: `/MuralhaDigital/Blitz`

## blitz-eletronica

- Caminho: `muralha-digital/pages/blitz-eletronica` · 5 arquivos
- JSPs: veiculos-irregulares.jsp
- Títulos (`<title>`): veiculos-irregulares: Muralha Digital
- JS próprios: assets/js/tempo-real-multi-select.js, assets/js/veiculos-irregulares.js
- CSS próprios: assets/css/veiculos-irregulares.css
- Endpoints chamados: `/MuralhaDigital/VeiculoTempoReal`

## blitz-ostensiva

- Caminho: `muralha-digital/pages/blitz-ostensiva` · 4 arquivos
- JSPs: blitz-ostensiva.jsp, modal-passagens-relacionadas.jsp
- Títulos (`<title>`): blitz-ostensiva: Muralha Digital - Blitz Ostensiva
- JS próprios: assets/js/blitz-ostensiva.js
- CSS próprios: assets/css/blitz-ostensiva.css

## boletim

- Caminho: `muralha-digital/pages/boletim` · 30 arquivos
- JSPs: botao-cadastro/botao-cadastro-boletim.jsp, botao-visualizar/botao-visualizar-boletim.jsp, consulta.jsp, consulta-filtros.jsp, consulta-tabela.jsp, modal/modal-boletim.jsp, modal/tab-apreencoes/tab-apreencoes.jsp, modal/tab-boletim/tab-boletim.jsp, modal/tab-documentos/tab-documentos.jsp, modal/tab-individuos/tab-individuos.jsp, modal/tab-veiculos/tab-veiculos.jsp
- Títulos (`<title>`): consulta: Consulta de Boletins
- JS próprios: botao-cadastro/js/botao-cadastro-boletim.js, botao-visualizar/js/botao-visualizar-boletim.js, js/consulta.js, modal/js/modal-boletim.js, modal/js/modal-cadastro-boletim.js, modal/js/modal-visualizar-boletim.js, modal/tab-apreencoes/js/tab-apreencoes.js, modal/tab-boletim/js/consulta.js, modal/tab-boletim/js/tab-boletim.js, modal/tab-documentos/js/consulta.js, modal/tab-documentos/js/tab-documentos.js, modal/tab-individuos/js/consulta.js …(+2)
- CSS próprios: botao-cadastro/css/botao-cadastro-boletim.css, botao-visualizar/css/botao-visualizar-boletim.css, css/consulta.css, modal/css/modal-cadastro-boletim.css, modal/tab-boletim/css/tab-boletim.css
- Endpoints chamados: `/MuralhaDigital/Boletim`, `/MuralhaDigital/Boletim/Cidade`, `/MuralhaDigital/Boletim/Doducmento`, `/MuralhaDigital/Boletim/Situacao`, `/MuralhaDigital/Boletim/Tipo`, `/MuralhaDigital/Boletim/TipoIndividuo`

## configuracao-monitoramento

- Caminho: `muralha-digital/pages/configuracao-monitoramento` · 3 arquivos
- JSPs: configuracao.jsp
- Títulos (`<title>`): configuracao: Configuração de Monitoramento
- JS próprios: assets/js/configuracao.js
- Endpoints chamados: `/MuralhaDigital/ConfiguracaoInatividade`, `/MuralhaDigital/ConfiguracaoRadares`, `/MuralhaDigital/ConfiguracaoTempo`, `/MuralhaDigital/ConfiguracaoTempoOcrBlitz`, `/MuralhaDigital/GrupoNotificacao`, `/MuralhaDigital/HistoricoConfiguracaoEquipamento`, `/MuralhaDigital/PermissoesFuncionalidade`

## configuracoes

- Caminho: `muralha-digital/pages/configuracoes` · 7 arquivos
- JSPs: parametros.jsp
- Títulos (`<title>`): parametros: Muralha Digital
- JS próprios: assets/js/estiloMapas.js, assets/js/parametros.js
- CSS próprios: assets/css/parametros.css
- Endpoints chamados: `/MuralhaDigital/AreaMonitorada`

## consulta

- Caminho: `muralha-digital/pages/consulta` · 4 arquivos
- JSPs: log/consulta_log.jsp
- JS próprios: log/js/consulta_log.js, log/js/moment.js
- CSS próprios: log/css/style.css
- Endpoints chamados: `/MuralhaDigital/Ftp`

## consulta-alerta-ocorrencia

- Caminho: `muralha-digital/pages/consulta-alerta-ocorrencia` · 7 arquivos
- JSPs: consulta.jsp, modal-alertas-veiculo.jsp, visualizar-alertas.jsp
- Títulos (`<title>`): consulta: GTW - Consulta de Alertas e Ocorrências · visualizar-alertas: GTW - Alertas
- JS próprios: js/consulta.js, js/modal-alertas-veiculo.js, js/visualizar-alertas.js
- CSS próprios: css/consulta.css
- Endpoints chamados: `/MuralhaDigital/PermissoesFuncionalidade`, `/MuralhaDigital/Veiculo`, `/MuralhaDigital/Veiculo/Imagem`

## consulta-veiculo

- Caminho: `muralha-digital/pages/consulta-veiculo` · 28 arquivos
- JSPs: consulta.jsp, modal-detalhe-veiculo.jsp, modal-parametros-visualizacao-grade.jsp, modal-passagem-veiculo.jsp
- Títulos (`<title>`): consulta: GTW - Consulta de Veículos · modal-parametros-visualizacao-grade: Modal para gerenciamentos dos par�metros em grade · modal-passagem-veiculo: Modal para associa��o de passagens de ve�culos a um fato
- JS próprios: freewall/js/freewall.js, freewall/js/index.js, js/consulta.js, js/modal-detalhe-veiculo.js, js/modal-parametros-visualizacao-grade.js, js/modal-passagem-veiculo.js
- CSS próprios: css/consulta.css, css/modal-detalhe-veiculo.css, css/modal-passagem-veiculo.css, freewall/css/metro-style.css, freewall/css/pinterest-style.css, freewall/css/style.css, freewall/css/style_original.css
- Endpoints chamados: `/muralha-digital/assinatura/assinar`, `/MuralhaDigital/MotivoSolicitacaoRelatorio`, `/MuralhaDigital/RegistroDeFato`, `/MuralhaDigital/RegistroDeFato/Natureza`, `/MuralhaDigital/RegistroDeFato/Tipo`, `/MuralhaDigital/RegistroDeFatoNaturezaDelituosa`, `/MuralhaDigital/Veiculo`, `/MuralhaDigital/Veiculo/Imagem`

## consulta-veiculo-de-carga

- Caminho: `muralha-digital/pages/consulta-veiculo-de-carga` · 28 arquivos
- JSPs: consulta-veiculo-de-carga.jsp, modal-detalhe-veiculo-de-carga.jsp, modal-parametros-visualizacao-grade.jsp, modal-passagem-veiculo-de-carga.jsp
- Títulos (`<title>`): consulta-veiculo-de-carga: GTW - Consulta de Veículos de Carga · modal-parametros-visualizacao-grade: Modal para gerenciamentos dos parâmetros em grade · modal-passagem-veiculo-de-carga: Modal para associação de passagens de veículos a um fato
- JS próprios: freewall/js/freewall.js, freewall/js/index.js, js/consulta.js, js/modal-detalhe-veiculo-de-carga.js, js/modal-parametros-visualizacao-grade.js, js/modal-passagem-veiculo.js
- CSS próprios: css/consulta.css, css/modal-detalhe-veiculo.css, css/modal-passagem-veiculo.css, freewall/css/metro-style.css, freewall/css/pinterest-style.css, freewall/css/style.css, freewall/css/style_original.css
- Endpoints chamados: `/ajax/ImgVeiculo`, `/ajax/InfoVeiculoImagem`, `/MuralhaDigital/MotivoSolicitacaoRelatorio`, `/MuralhaDigital/RegistroDeFato`

## correcao-placas-lote

- Caminho: `muralha-digital/pages/correcao-placas-lote` · 3 arquivos
- JSPs: correcao-placas-lote.jsp
- Títulos (`<title>`): correcao-placas-lote: Carrossel de Veículos
- JS próprios: assets/js/correcao-placas-lote.js
- CSS próprios: assets/css/correcao-placas-lote.css
- Endpoints chamados: `/MuralhaDigital/CorrecaoPlaca`, `/MuralhaDigital/Veiculo/Imagem`

## correlacao

- Caminho: `muralha-digital/pages/correlacao` · 3 arquivos
- JSPs: correlacao.jsp
- Títulos (`<title>`): correlacao: GTW - Mapa de Calor
- JS próprios: assets/js/correlacao.js
- CSS próprios: assets/css/correlacao.css

## dashboards

- Caminho: `muralha-digital/pages/dashboards` · 39 arquivos
- JSPs: calendario-intensidade.jsp, dashboard.jsp
- Títulos (`<title>`): dashboard: Dashboard
- JS próprios: assets/heatmap/gmaps-heatmap.js, assets/heatmap/heatmap.js, assets/js/alerta-anomalia.js, assets/js/calendario-intensidade.js, assets/js/dashboard-init.js, assets/js/grafico-comp-diario-ano-anterior.js, assets/js/grafico-comp-diario-fluxo-X-infracoes.js, assets/js/grafico-comp-diario-mes-anterior.js, assets/js/grafico-distribuicao-faixa.js, assets/js/grafico-evolucao-por-classificacao.js, assets/js/grafico-previsao-futuro.js, assets/js/grafico-utils.js …(+3)
- CSS próprios: assets/css/calendario-intensidade.css, assets/css/dashboard-init.css
- Endpoints chamados: `/MuralhaDigital/MapaCalor`

## guarnicao

- Caminho: `muralha-digital/pages/guarnicao` · 14 arquivos
- JSPs: listagem-guarnicoes.jsp, modal-atualiza-guarnicao.jsp, modal-edita-guarnicao.jsp, modal-visualiza-guarnicao.jsp, nova-guarnicao.jsp
- Títulos (`<title>`): listagem-guarnicoes: Listagem de Guarnições · nova-guarnicao: Cadastro de Nova Guarnição
- JS próprios: assets/js/listagem-guarnicoes.js, assets/js/modal-atualiza-guarnicao.js, assets/js/modal-edita-guarnicao.js, assets/js/modal-visualiza-guarnicao.js, assets/js/nova-guarnicao.js
- CSS próprios: assets/css/listagem-guarnicoes.css, assets/css/modal-atualiza-guarnicao.css, assets/css/modal-visualiza-guarnicao.css, assets/css/nova-guarnicao.css
- Endpoints chamados: `/MuralhaDigital/Guarnicao`

## juncao-base

- Caminho: `muralha-digital/pages/juncao-base` · 4 arquivos
- JSPs: consulta.jsp
- Títulos (`<title>`): consulta: GTW - Junção de Bases
- JS próprios: js/consulta.js
- CSS próprios: css/consulta.css

## linha-tempo

- Caminho: `muralha-digital/pages/linha-tempo` · 3 arquivos
- JSPs: modal-linha-tempo.jsp
- JS próprios: assets/js/modal-linha-tempo.js
- CSS próprios: assets/css/modal-linha-tempo.css

## mancha-monitorada

- Caminho: `muralha-digital/pages/mancha-monitorada` · 9 arquivos
- JSPs: mancha-monitorada.jsp
- Títulos (`<title>`): mancha-monitorada: Mapa de rota do ve�culo
- JS próprios: assets/js/mapa.js, assets/js/perfil-comportamental.js
- CSS próprios: assets/css/mancha-monitorada.css, assets/css/perfil-comportamental-mapa.css
- Endpoints chamados: `/MuralhaDigital/AreaMonitorada`, `/MuralhaDigital/PerfilComportamental`

## mapa-alertas-ocorrencias

- Caminho: `muralha-digital/pages/mapa-alertas-ocorrencias` · 23 arquivos
- JSPs: mapa-alertas-ocorr.jsp
- Títulos (`<title>`): mapa-alertas-ocorr: Mapa de alertas e ocorrÃªncias
- JS próprios: assets/js/mapa-alertas-ocorr.js
- CSS próprios: assets/css/mapa-alertas-ocorr.css
- Endpoints chamados: `/MuralhaDigital/MapaDispositivosEquipamentos`, `/MuralhaDigital/PermissoesFuncionalidade`

## mapa-calor

- Caminho: `muralha-digital/pages/mapa-calor` · 7 arquivos
- JSPs: mapa-calor.jsp
- Títulos (`<title>`): mapa-calor: GTW - Mapa de Calor
- JS próprios: assets/js/gmaps-heatmap.js, assets/js/heatmap.js, assets/js/mapaCalor.js
- CSS próprios: assets/css/mapaCalor.css
- Endpoints chamados: `/MuralhaDigital/MapaCalor`, `/MuralhaDigital/PermissoesFuncionalidade`

## mapa-equipamento

- Caminho: `muralha-digital/pages/mapa-equipamento` · 37 arquivos
- JSPs: mapa-dispositivos.jsp, popover-legenda-mapa.jsp
- Títulos (`<title>`): mapa-dispositivos: Mapa de Dispositivos
- JS próprios: assets/js/mapa-dispositivos.js
- CSS próprios: assets/css/mapa-dispositivos.css
- Endpoints chamados: `/MuralhaDigital/DispositivoEquipamento`, `/MuralhaDigital/MapaDispositivosEquipamentos`

## mapa-interativo

- Caminho: `muralha-digital/pages/mapa-interativo` · 16 arquivos
- JSPs: mapa-interativo.jsp
- Títulos (`<title>`): mapa-interativo: Mapa de alertas e ocorrÃªncias
- JS próprios: assets/js/mapa-interativo.js
- CSS próprios: assets/css/mapa-interativo.css
- Endpoints chamados: `/MuralhaDigital/Anexo`, `/MuralhaDigital/Atendimento`, `/MuralhaDigital/MapaDispositivosEquipamentos`, `/MuralhaDigital/MapaInterativo`

## mapa-passagens

- Caminho: `muralha-digital/pages/mapa-passagens` · 24 arquivos
- JSPs: modal-visualizar-mapa.jsp
- JS próprios: assets/js/modal-visualizar-mapa.js
- CSS próprios: assets/css/modal-visualizar-mapa.css

## mapa-situacao-transito

- Caminho: `muralha-digital/pages/mapa-situacao-transito` · 34 arquivos
- JSPs: mapa-situacao-transito.jsp
- Títulos (`<title>`): mapa-situacao-transito: Mapa de Dispositivos
- JS próprios: assets/js/mapa-situacao-transito.js
- CSS próprios: assets/css/mapa-situacao-transito.css

## modal-cad-ocorrencia-ligacao

- Caminho: `muralha-digital/pages/modal-cad-ocorrencia-ligacao` · 3 arquivos
- JSPs: modal-cadastro-ocorrencia.jsp
- Títulos (`<title>`): modal-cadastro-ocorrencia: Modal para cadastro de ocorr�ncias
- JS próprios: assets/js/modal-cadastro-ocorrencia.js
- CSS próprios: assets/css/modal-cadastro-ocorrencia.css
- Endpoints chamados: `/MuralhaDigital/CidadesMinasGerais`, `/MuralhaDigital/OcorrenciaLigacao`

## monitorado

- Caminho: `muralha-digital/pages/monitorado` · 9 arquivos
- JSPs: consulta.jsp, modal-cadastro-monitorado.jsp, modal-editar-monitorado.jsp, modal-enviar_cad_ativo_equip.jsp
- Títulos (`<title>`): consulta: GTW - Consulta de Monitorados (Registro de fatos)
- JS próprios: js/cadastro-monitorado.js, js/consulta.js, js/editar-monitorado.js, js/enviar_cad_ativo_equip.js
- CSS próprios: css/consulta.css
- Endpoints chamados: `/MuralhaDigital/AlertaOcorrencia/Tipo`, `/MuralhaDigital/Monitorado`, `/MuralhaDigital/PermissoesFuncionalidade`, `/MuralhaDigital/VeiculoAuxiliar`

## mosaico

- Caminho: `muralha-digital/pages/mosaico` · 7 arquivos
- JSPs: modal-config-mosaico.jsp, mosaico-videos.jsp
- Títulos (`<title>`): mosaico-videos: Muralha Digital
- JS próprios: assets/js/mosaico-videos.js

## painel-informacoes

- Caminho: `muralha-digital/pages/painel-informacoes` · 4 arquivos
- JSPs: painel-informacoes.jsp
- Títulos (`<title>`): painel-informacoes: Dashboard - Gestão Muralha
- JS próprios: js/consulta.js, js/painel-informacoes.js
- CSS próprios: css/painel-informacoes.css
- Endpoints chamados: `/MuralhaDigital/PainelInformacao`, `/MuralhaDigital/PainelInformacao/Camera`, `/MuralhaDigital/PainelInformacao/TotalInformacoes`

## perfil-comportamental

- Caminho: `muralha-digital/pages/perfil-comportamental` · 6 arquivos
- JSPs: perfil-comportamental.jsp
- Títulos (`<title>`): perfil-comportamental: Perfil Comportamental do Veículo
- JS próprios: assets/js/client.js, assets/js/mock.js, assets/js/script.js, assets/js/utils.js
- CSS próprios: assets/css/style.css
- Endpoints chamados: `/MuralhaDigital/PerfilComportamental`

## perguntasRespostas

- Caminho: `muralha-digital/pages/perguntasRespostas` · 6 arquivos
- JSPs: historico-perguntas-respostas/modal-historico-questionario.jsp, iframe-tela-tratativa-alerta.jsp, modal-questionario-alerta.jsp
- Títulos (`<title>`): modal-questionario-alerta: Modal para gerenciamento dos question�rios dos alertas · modal-historico-questionario: Modal de Question�rio
- JS próprios: historico-perguntas-respostas/js/modal-historico-questionario.js, js/questionario-alerta.js
- CSS próprios: css/questionario-alerta.css
- Endpoints chamados: `/MuralhaDigital/AlertaQuestionario`

## pesquisa-rapida

- Caminho: `muralha-digital/pages/pesquisa-rapida` · 7 arquivos
- JSPs: dropdown-pesquisa-rapida.jsp, modal-pesquisa-rapida.jsp
- JS próprios: js/consulta.js, js/dropdown-pesquisa-rapida.js, js/modal-pesquisa-rapida.js
- CSS próprios: css/dropdown-pesquisa-rapida.css, css/modal-pesquisa-rapida.css
- Endpoints chamados: `/muralha-digital/pages/registro_fato/comBoletim/`, `/MuralhaDigital/PesquisaRapida`, `/MuralhaDigital/PesquisaRapida/Autocomplete`, `/MuralhaDigital/Veiculo/Imagem`

## ponto-interesse

- Caminho: `muralha-digital/pages/ponto-interesse` · 11 arquivos
- JSPs: cadastro-ponto-interesse.jsp, consulta-ponto-interesse.jsp, ponto-interesse.jsp
- Títulos (`<title>`): cadastro-ponto-interesse: Cadastro de Ponto de Interesse · consulta-ponto-interesse: GTW - Consulta de Pontos de Interesse · ponto-interesse: Cadastro de Ponto de Interesse
- JS próprios: assets/js/cadastro-ponto-interesse.js, assets/js/consulta-ponto-interesse.js, assets/js/ponto-interesse.js
- CSS próprios: assets/css/cadastro-ponto-interesse.css, assets/css/consulta-ponto-interesse.css

## registro_fato

- Caminho: `muralha-digital/pages/registro_fato` · 68 arquivos
- JSPs: comBoletim/botao-cadastro/botao-cadastro-registroDeFato.jsp, comBoletim/botao-editar/botao-editar-registroDeFato.jsp, comBoletim/consulta.jsp, comBoletim/consulta-filtros.jsp, comBoletim/consulta-tabela.jsp, comBoletim/modal/modal-registroDeFato.jsp, comBoletim/modal/tab-boletim/tab-boletim.jsp, comBoletim/modal/tab-documento/tab-documento.jsp, comBoletim/modal/tab-endereco/tab-endereco.jsp, comBoletim/modal/tab-grupo/tab-grupo.jsp, comBoletim/modal/tab-individuo/tab-individuo.jsp, comBoletim/modal/tab-link/tab-link.jsp, comBoletim/modal/tab-objeto/tab-objeto.jsp, comBoletim/modal/tab-passagem/tab-passagem.jsp, comBoletim/modal/tab-registroDeFato/tab-registroDeFato.jsp, comBoletim/modal/tab-veiculo/tab-veiculo.jsp, historico/registro-fato-modal-historico.jsp, menu_registro_fato.jsp, semBoletim/consulta.jsp, semBoletim/consulta-filtros.jsp, semBoletim/consulta-tabela.jsp, semBoletim/detalhes-fato.jsp, semBoletim/modal/modal-registroDeFato.jsp
- Títulos (`<title>`): menu_registro_fato: Registro de Fato · consulta: Consulta de Registros de Fato · consulta: Consulta de Registros de Fato sem Boletim · detalhes-fato: Detalhes do Fato · modal-registroDeFato: Modal para cadastro de ocorr�ncias
- JS próprios: comBoletim/botao-cadastro/js/botao-cadastro-registroDeFato.js, comBoletim/botao-editar/js/botao-editar-registroDeFato.js, comBoletim/js/consulta.js, comBoletim/modal/js/modal-registroDeFato.js, comBoletim/modal/tab-boletim/js/consulta.js, comBoletim/modal/tab-boletim/js/tab-boletim.js, comBoletim/modal/tab-documento/js/consulta.js, comBoletim/modal/tab-documento/js/tab-documento.js, comBoletim/modal/tab-endereco/js/consulta.js, comBoletim/modal/tab-endereco/js/tab-endereco.js, comBoletim/modal/tab-grupo/js/consulta.js, comBoletim/modal/tab-grupo/js/tab-grupo.js …(+14)
- CSS próprios: comBoletim/botao-cadastro/css/botao-cadastro-registroDeFato.css, comBoletim/botao-editar/css/botao-editar-registroDeFato.css, comBoletim/css/consulta.css, comBoletim/modal/css/modal-cadastro-registroDeFato.css, comBoletim/modal/tab-boletim/css/tab-boletim.css, comBoletim/modal/tab-documento/css/tab-documento.css, comBoletim/modal/tab-endereco/css/tab-endereco.css, comBoletim/modal/tab-grupo/css/tab-grupo.css
- Endpoints chamados: `/MuralhaDigital/AlertaOcorrencia/Tipo`, `/MuralhaDigital/Boletim`, `/MuralhaDigital/PermissoesFuncionalidade`, `/MuralhaDigital/RegistroDeFato`, `/MuralhaDigital/RegistroDeFato/Boletim/Situacao`, `/MuralhaDigital/RegistroDeFato/Cidade`, `/MuralhaDigital/RegistroDeFato/Doducmento`, `/MuralhaDigital/RegistroDeFato/EnderecoEvento`, `/MuralhaDigital/RegistroDeFato/Grupo`, `/MuralhaDigital/RegistroDeFato/Historico`, `/MuralhaDigital/RegistroDeFato/IndividuoTipo`, `/MuralhaDigital/RegistroDeFato/Natureza`, `/MuralhaDigital/RegistroDeFato/Situacao`, `/MuralhaDigital/RegistroDeFato/Tipo`

## relatorios

- Caminho: `muralha-digital/pages/relatorios` · 61 arquivos
- JSPs: _filtros_graficos.jsp, acoes-alarmes/acoes-alarmes.jsp, alertas-detalhado.jsp, auditoria/auditoria.jsp, distribuicao-faixa-velocidade.jsp, distribuicao-porte-veicular.jsp, extrato-alerta-ocorrencia.jsp, fluxo-mensal-classificacao.jsp, grafico-ocorrencia-faixa-vel.jsp, grafico-ocorrencia-porte-veic.jsp, grafico-veiculos.jsp, grafico-veiculos-porte.jsp, grafico-velocidade-media.jsp, imagens-exportadas/imagens-relatorios.jsp, ocorrencias-detalhado.jsp, permanencia-veiculo.jsp, permanencia-veiculo-area-monitorada-new.jsp, pesquisa-veiculos-realizados/pesquisa-veiculos-realizados.jsp, placas-veiculares/relatorio-placas-veiculares.jsp, quantidade-passagens.jsp, relatorio-acompanhamento.jsp, relatorio-detalhado-alertas.jsp, relatorio-distribuicao-fatos.jsp, relatorio-distribuicao-tipos-fatos.jsp, relatorio-estatistico-alarmes.jsp, relatorio-estatistico-fato.jsp, relatorio-estatistico-tipo-fatos.jsp, relatorio-estatistico-tipo-fatos-mapa.jsp, relatorio-evolucao-semanal.jsp, relatorio-fluxo-passagens-veiculares.jsp, relatorio-fluxo-veicular-rota.jsp, relatorio-passagens-rota-leaflet.jsp, relatorio-pendencias-registro-fato.jsp, relatorio-veiculos-monitorados-modelo.jsp, sessao-usuario.jsp, taxa-ocupacao-via.jsp, velocidade-media.jsp
- Títulos (`<title>`): alertas-detalhado: GTW - Relatório de Alertas Detalhado · distribuicao-faixa-velocidade: GTW - Relatório de Distribuição por Faixa de Velocidade · distribuicao-porte-veicular: GTW - Relatório de Distribuição por Porte Veicular · extrato-alerta-ocorrencia: GTW - Extrato de Alertas e Irregularidades · fluxo-mensal-classificacao: GTW - Relatório de Fluxo por Classificação Veicular · grafico-ocorrencia-faixa-vel: Gr�fico de Distribui��o Ocorr�ncia por Faixa de Velocidade
- JS próprios: acoes-alarmes/acoes-alarmes.js, auditoria/auditoria.js, imagens-exportadas/imagens-relatorios.js, js/alertas-detalhado.js, js/carregar-combos-filtros-relatorio.js, js/distribuicao-faixa-velocidade.js, js/distribuicao-porte-veicular.js, js/extrato-alerta-ocorrencia.js, js/gerar-arquivo-download.js, js/grafico-ocorrencia-faixa-vel.js, js/grafico-ocorrencia-porte-veic.js, js/grafico-utilidades.js …(+10)
- CSS próprios: placas-veiculares/assets/css/style.css
- Endpoints chamados: `/muralha-digital/relatorio/RelatorioDetalhadoAlertas`, `/muralha-digital/relatorio/RelatorioEstatisticoAlarmes`, `/muralha-digital/relatorio/RelatorioFluxoVeicularRota`, `/muralha-digital/relatorio/RelatorioVeiculosMonitoradosModelo`, `/MuralhaDigital/RelatorioDistribuicaoFatos`, `/MuralhaDigital/RelatorioEvolucaoSemanal`, `/MuralhaDigital/RelatorioPendenciasRegistroFato`, `/MuralhaDigital/RelatorioPlacasVeiculares`, `/MuralhaDigital/Relatorios/SessaoUsuario`, `/relatorio/ContagemPassagensPorLocal`, `/relatorio/PassagensSequenciais`, `/relatorio/RelatorioDistribuicaoTiposFatosServlet`, `/relatorio/RelatorioEstatisticoFatoServlet`, `/relatorio/RelatorioEstatisticoTipoFatoMapa`, `/relatorio/RelatorioEstatisticoTipoFatos`, `/relatorio/RelatorioFluxoPassagensVeiculares`, `/relatorio/RelatorioPermanenciaVeiculo`, `/relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew`

## veiculos-correlacionados

- Caminho: `muralha-digital/pages/veiculos-correlacionados` · 13 arquivos
- JSPs: veiculos-correlacionados.jsp
- Títulos (`<title>`): veiculos-correlacionados: Correlação de Veículos
- JS próprios: js/client.js, js/filtros.js, js/grafo/grafo-core.js, js/grafo/grafo-fetch.js, js/grafo/grafo-filtros.js, js/grafo/grafo-modal.js, js/grafo/grafo-zoom.js, js/modal.js, js/utils.js
- CSS próprios: css/style.css
- Endpoints chamados: `/MuralhaDigital/ConfigurarEquipamento`, `/MuralhaDigital/CorrecaoPlaca`, `/MuralhaDigital/VeiculosCorrelacionados`

## veiculo-tempo-real

- Caminho: `muralha-digital/pages/veiculo-tempo-real` · 32 arquivos
- JSPs: opcoes-tempo-real-video.jsp, veiculos-passagem.jsp
- Títulos (`<title>`): opcoes-tempo-real-video: Cinturão de Segurança · veiculos-passagem: Muralha Digital
- JS próprios: assets/js/tempo-real-multi-select.js, assets/js/veiculo-tempo-real.js
- CSS próprios: assets/css/veiculo-passagem.css
- Endpoints chamados: `/MuralhaDigital/VeiculoTempoReal`

## video-ao-vivo

- Caminho: `muralha-digital/pages/video-ao-vivo` · 2 arquivos
- JSPs: video-ao-vivo.jsp
- Títulos (`<title>`): video-ao-vivo: Muralha Digital
- JS próprios: assets/js/video-ao-vivo.js

## video-monitoramento

- Caminho: `muralha-digital/pages/video-monitoramento` · 8 arquivos
- JSPs: consulta.jsp, opcoes-video-monitoramento.jsp
- Títulos (`<title>`): consulta: Muralha Digital · opcoes-video-monitoramento: Cinturão de Segurança
- JS próprios: assets/js/consulta.js

## video-passagem-semaforo

- Caminho: `muralha-digital/pages/video-passagem-semaforo` · 20 arquivos
- JSPs: video-passagem-semaforo.jsp
- Títulos (`<title>`): video-passagem-semaforo: Muralha Digital
- JS próprios: assets/js/chart_perfil.js, assets/js/video-passagem-semaforo.js
- CSS próprios: assets/css/veiculo-passagem.css
- Endpoints chamados: `/MuralhaDigital/VeiculoTempoReal`

## video-passagem-tempo-real

- Caminho: `muralha-digital/pages/video-passagem-tempo-real` · 7 arquivos
- JSPs: video-passagem.jsp
- Títulos (`<title>`): video-passagem: Muralha Digital
- JS próprios: assets/js/video-passagem-tempo-real.js
- CSS próprios: assets/css/veiculo-passagem.css
- Endpoints chamados: `/MuralhaDigital/VeiculoTempoReal`

## visualizacao-cameras

- Caminho: `muralha-digital/pages/visualizacao-cameras` · 17 arquivos
- JSPs: visualizacao-cameras.jsp
- Títulos (`<title>`): visualizacao-cameras: Muralha Digital
- JS próprios: assets/js/visualizacao-cameras.js, camera-api/controles.js, camera-api/controles-original.js, camera-api/module/audioTalkWorker.worker.js, camera-api/module/audioWorker.worker.js, camera-api/module/libDecodeSDK.js, camera-api/module/PlayerControl.js, camera-api/module/videoWorker.worker.js, camera-api/module/videoWorkerTrain.worker.js
- CSS próprios: camera-api/css/visualizacao-cameras.css, camera-api/css/visualizacao-cameras-original.css


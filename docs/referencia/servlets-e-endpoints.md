# Referência — Servlets e endpoints HTTP

Gerado por varredura de `web.xml` e das anotações `@WebServlet`. Total: 194 servlets em web.xml + 118 anotados.

## 1. Servlets declarados em web.xml (`src/main/webapp/WEB-INF/web.xml`)

| Nome | URL(s) | Classe |
|---|---|---|
| Configuracao |  | `com.consilux.conf.ConfiguracaoServlet` |
| Versao |  | `com.consilux.conf.Versao` |
| InfoProgresso | /ajax/InfoProgresso | `com.consilux.infra.InfoProgresso` |
| TempoProcessamentoServiceImpl | /GtwClientLogger/TempoProcessamentoService | `com.consilux.log.server.TempoProcessamentoServiceImpl` |
| menuServiceImpl | /GtwMenu/MenuService, /GtwWidgets/MenuService | `com.consilux.menu.server.MenuServiceImpl` |
| ArquivoLaudo | /ferramenta/ArquivoLaudo | `com.consilux.model.ArquivoLaudo` |
| ArquivoVideo | /ferramenta/ArquivoVideo | `com.consilux.model.ArquivoVideo` |
| AcidentePDF | /acidente/AcidentePDF | `com.consilux.servlet.ait.AcidentePDF` |
| AITPDF | /ait/AITPDF | `com.consilux.servlet.ait.AITPDF` |
| AITPDFDownload | /ait/AITPDFDownload | `com.consilux.servlet.ait.AITPDFDownload` |
| AcessoRemotoPolling | /AcessoRemotoPolling | `com.consilux.servlet.ajax.AcessoRemotoPolling` |
| ArquivoIntegracao | /Integracao/ArquivoIntegracao | `com.consilux.servlet.ajax.ArquivoIntegracao` |
| AtualizaInfracaoPerfilInvalido | /ajax/AtualizaInfracaoPerfilInvalido | `com.consilux.servlet.ajax.AtualizaInfracaoPerfilInvalido` |
| AtualizaProcessoMedicao | /ajax/AtualizaProcessoMedicao | `com.consilux.servlet.ajax.AtualizaProcessoMedicao` |
| AguardaStatusEquipamento | /ajax/AguardaStatusEquipamento | `com.consilux.servlet.ajax.binding.AguardaStatusEquipamento` |
| teste | /teste | `com.consilux.servlet.ajax.binding.teste` |
| ContaInfracaoDisponivelUsuario | /ajax/ContaInfracaoDisponivelUsuario | `com.consilux.servlet.ajax.ContaInfracaoDisponivelUsuario` |
| ExcluirRemessa | /ajax/remessa/ExcluirRemessa | `com.consilux.servlet.ajax.ExcluirRemessa` |
| ImagemAjusteController | /ajax/ImagemAjusteController | `com.consilux.servlet.ajax.ImagemAjusteController` |
| ImagemMiniaturaController | /ajax/ImagemMiniaturaController | `com.consilux.servlet.ajax.ImagemMiniaturaController` |
| ImgLocalPistaCroqui | /ajax/ImgLocalPistaCroqui | `com.consilux.servlet.ajax.ImgLocalPistaCroqui` |
| ImgSinalizacao | /ajax/ImgSinalizacao | `com.consilux.servlet.ajax.ImgSinalizacao` |
| ImgVeiculo | /ajax/ImgVeiculo | `com.consilux.servlet.ajax.ImgVeiculo` |
| ImgVeiculoMonitorado | /ajax/ImgVeiculoMonitorado | `com.consilux.servlet.ajax.ImgVeiculoMonitorado` |
| ImgVeiculoThumb | /ajax/ImgVeiculoThumb | `com.consilux.servlet.ajax.ImgVeiculoThumb` |
| Inconsistencias | /ajax/Inconsistencias | `com.consilux.servlet.ajax.Inconsistencias` |
| InfoCadastro | /ajax/InfoCadastro | `com.consilux.servlet.ajax.InfoCadastro` |
| InfoClasse | /ajax/InfoClasse | `com.consilux.servlet.ajax.InfoClasse` |
| InfoInconsistencia | /ajax/InfoInconsistencia | `com.consilux.servlet.ajax.InfoInconsistencia` |
| InfoInfracao | /ajax/InfoInfracao | `com.consilux.servlet.ajax.InfoInfracao` |
| InfoInfracaoCompleta | /ajax/InfoInfracaoCompleta | `com.consilux.servlet.ajax.InfoInfracaoCompleta` |
| InfoInfracaoObliteracao | /ajax/InfoInfracaoObliteracao | `com.consilux.servlet.ajax.InfoInfracaoObliteracao` |
| InfoInfracaoProcesso | /ajax/InfoInfracaoProcesso | `com.consilux.servlet.ajax.InfoInfracaoProcesso` |
| InfoIsento | /ajax/InfoIsento | `com.consilux.servlet.ajax.InfoIsento` |
| InfoTrafego | /ajax/InfoTrafego | `com.consilux.servlet.ajax.InfoTrafego` |
| InfoVeiculo | /ajax/InfoVeiculo | `com.consilux.servlet.ajax.InfoVeiculo` |
| InfoVeiculoImagem | /ajax/InfoVeiculoImagem | `com.consilux.servlet.ajax.InfoVeiculoImagem` |
| Processar | /ajax/processamento/Processar | `com.consilux.servlet.ajax.processamento.Processar` |
| ProcessarObliteracao | /ajax/processamento/ProcessarObliteracao | `com.consilux.servlet.ajax.processamento.ProcessarObliteracao` |
| corredorBRS | /corredorBRS/corredorBRS | `com.consilux.servlet.corredorBRS.corredorBRS` |
| ConfirmarDescarga | /descarga/ConfirmarDescarga | `com.consilux.servlet.descarga.ConfirmarDescarga` |
| DownloadDescarga | /descarga/DownloadDescarga | `com.consilux.servlet.descarga.DownloadDescargaServlet` |
| EtiquetaDescarga | /descarga/EtiquetaDescarga | `com.consilux.servlet.descarga.EtiquetaDescarga` |
| ExportarDescarga | /ferramenta/ExportarDescarga | `com.consilux.servlet.descarga.ExportarDescargaServlet` |
| GerarDescarga | /descarga/GerarDescarga | `com.consilux.servlet.descarga.GerarDescargaServlet` |
| AnexarDoc | /documentos/AnexarDoc | `com.consilux.servlet.documentos.AnexarDoc` |
| Agendador |  | `com.consilux.servlet.ferramentas.Agendador` |
| CadAgendaEstatico | /Ferramentas/CadAgendaEstatico | `com.consilux.servlet.ferramentas.CadAgendaEstatico` |
| CadastrarEventoManual | /ferramentas/CadastrarEventoManual | `com.consilux.servlet.ferramentas.CadastrarEventoManual` |
| CadastrarFiltroServlet | /ferramenta/CadastrarFiltroServlet | `com.consilux.servlet.ferramentas.CadastrarFiltroServlet` |
| CadastrarInibicaoServlet | /ferramenta/CadastrarInibicao | `com.consilux.servlet.ferramentas.CadastrarInibicaoServlet` |
| CriarPreRelatorioServlet | /CriarPreRelatorioServlet | `com.consilux.servlet.ferramentas.CriarPreRelatorioServlet` |
| DesabilitaFiltroServlet | /ferramenta/DesabilitaFiltroServlet | `com.consilux.servlet.ferramentas.DesabilitaFiltroServlet` |
| DesativarInibicaoServlet | /ferramenta/DesativarInibicao | `com.consilux.servlet.ferramentas.DesativarInibicaoServlet` |
| DownloadInfracao | /infracao/DownloadInfracao | `com.consilux.servlet.ferramentas.DownloadInfracao` |
| ExportarArquivoDT | /ferramentas/ExportarArquivoDT | `com.consilux.servlet.ferramentas.ExportarArquivoDT` |
| ExportarImagensServlet | /ferramenta/ExportarImagens | `com.consilux.servlet.ferramentas.ExportarImagensServlet` |
| ExportarTrafegoServlet | /ferramenta/ExportarTrafegoServlet | `com.consilux.servlet.ferramentas.ExportarTrafegoServlet` |
| EnviarArquivoImpTxt | /ferramentas/ImpTxt | `com.consilux.servlet.ferramentas.ImpTxt` |
| ListarInibicaoServlet | /ferramenta/ListarInibicao | `com.consilux.servlet.ferramentas.ListarInibicaoServlet` |
| MoverInfracoesRemoverListaServlet | /MoverInfracoesRemoverListaServlet | `com.consilux.servlet.ferramentas.MoverInfracoesRemoverListaServlet` |
| MoverInfracoesServlet | /ferramenta/MoverInfracoesServlet | `com.consilux.servlet.ferramentas.MoverInfracoesServlet` |
| PerfilVeiculo | /servlet/PerfilVeiculo | `com.consilux.servlet.ferramentas.PerfilVeiculo` |
| UpImgVeiculoServlet | /servlet/UpImgVeiculo | `com.consilux.servlet.ferramentas.UpImgVeiculoServlet` |
| VeiculoTarget | /servlet/VeiculoTarget | `com.consilux.servlet.ferramentas.VeiculoTarget` |
| VerVideo | /servlet/VerVideo | `com.consilux.servlet.ferramentas.VerVideo` |
| VisualizarInibicaoServlet | /ferramenta/VisualizarInibicao | `com.consilux.servlet.ferramentas.VisualizarInibicaoServlet` |
| LogServlet | /LogServlet | `com.consilux.servlet.log.LogServlet` |
| AjustarAlertasManutencao | /manutencao/AjustarAlertasManutencao | `com.consilux.servlet.manutencao.AjustarAlertasManutencao` |
| AlterarAlertaManutencao | /manutencao/AlterarAlertaManutencao | `com.consilux.servlet.manutencao.AlterarAlertaManutencao` |
| CadastroManutencao | /manutencao/CadastroManutencao | `com.consilux.servlet.manutencao.CadastroManutencao` |
| ExportarOcorrenciaManutencao | /manutencao/ExportarOcorrenciaManutencao | `com.consilux.servlet.manutencao.ExportarOcorrenciaManutencao` |
| CartaImagensComprovacao | /medicao/CartaImagensComprovacao | `com.consilux.servlet.medicao.CartaImagensComprovacao` |
| ExportaAmostras | /ExportaAmostras | `com.consilux.servlet.medicao.ExportaAmostrasServlet` |
| ExportaComprovacaoImagens | /medicao/ExportaComprovacaoImagens | `com.consilux.servlet.medicao.ExportaComprovacaoImagensServlet` |
| FixarAmostra | /medicao/FixarAmostra | `com.consilux.servlet.medicao.FixarAmostra` |
| ListarProcessoMedicao | /medicao/ListarProcessoMedicao | `com.consilux.servlet.medicao.ListarProcessoMedicao` |
| NotificacaoPDF | /notificacao/NotificacaoPDF | `com.consilux.servlet.notificacao.NotificacaoPDF` |
| CacheMediatorServlet |  | `com.consilux.servlet.processamento.CacheMediatorServlet` |
| FinalizarJanelaProcessamento | /FinalizarJanelaProcessamento | `com.consilux.servlet.processamento.FinalizarJanelaProcessamento` |
| processo/FinalizarProcesso | /processo/FinalizarProcesso | `com.consilux.servlet.processamento.FinalizarProcesso` |
| FinalizarReposicionamento | /processo/FinalizarReposicionamento | `com.consilux.servlet.processamento.FinalizarReposicionamento` |
| GerarSolicitacaoAuditoria | /processo/GerarSolicitacaoAuditoria | `com.consilux.servlet.processamento.GerarSolicitacaoAuditoria` |
| InconsistirImagensForaEscala | /processo/InconsistirImagensForaEscala | `com.consilux.servlet.processamento.InconsistirImagensForaEscala` |
| IniciarProcesso | /processo/IniciarProcesso | `com.consilux.servlet.processamento.IniciarProcesso` |
| IniciarReposicionamento | /processo/IniciarReposicionamento | `com.consilux.servlet.processamento.IniciarReposicionamento` |
| LiberarMovimentosLote | /processo/LiberarMovimentosLote | `com.consilux.servlet.processamento.LiberarMovimentosLote` |
| ListarSolicitacaoAuditoria | /processo/ListarSolicitacaoAuditoria | `com.consilux.servlet.processamento.ListarSolicitacaoAuditoria` |
| ProcessarDireto | /processo/ProcessarDireto | `com.consilux.servlet.processamento.ProcessarDireto` |
| ReposicionarInfracoes | /processo/ReposicionarInfracoes | `com.consilux.servlet.processamento.ReposicionarInfracoes` |
| ReposicionarLoteReprovado | /processo/ReposicionarLoteReprovado | `com.consilux.servlet.processamento.ReposicionarLoteReprovado` |
| SolicitacaoAuditoriaServlet | /processo/SolicitacaoAuditoriaServlet | `com.consilux.servlet.processamento.SolicitacaoAuditoriaServlet` |
| AproveitamentoImagens | /relatorio/AproveitamentoImagens | `com.consilux.servlet.relatorio.AproveitamentoImagens` |
| DownloadRelatorio | /ferramentas/DownloadRelatorio | `com.consilux.servlet.relatorio.DownloadRelatorio` |
| ExportarPlanilhaFluxoXTempo | /relatorio/ExportarPlanilhaFluxoXTempo | `com.consilux.servlet.relatorio.ExportarPlanilhaFluxoXTempo` |
| ExportarPlanilhaInfracoes | /relatorio/ExportarPlanilhaInfracoes | `com.consilux.servlet.relatorio.ExportarPlanilhaInfracoes` |
| ListarVeiculosQualquer | /ferramentas/ListarVeiculosQualquer | `com.consilux.servlet.relatorio.ListarVeiculosQualquer` |
| ProcessamentoAproveitamento | /relatorio/ProcessamentoAproveitamento | `com.consilux.servlet.relatorio.ProcessamentoAproveitamento` |
| ProcessamentoAproveitamentoControl | /relatorio/ProcessamentoAproveitamentoControl | `com.consilux.servlet.relatorio.ProcessamentoAproveitamentoControl` |
| ProcessamentoAproveitamentoPorFaixa | /relatorio/ProcessamentoAproveitamentoPorFaixa | `com.consilux.servlet.relatorio.ProcessamentoAproveitamentoPorFaixa` |
| ProcessamentoAproveitamentoPorLocal | /relatorio/ProcessamentoAproveitamentoPorLocal | `com.consilux.servlet.relatorio.ProcessamentoAproveitamentoPorLocal` |
| Relatorio4Minutos | /relatorio/Relatorio4Minutos | `com.consilux.servlet.relatorio.Relatorio4Minutos` |
| RelatorioAlteracaoConfigEquip | /relatorio/RelatorioAlteracaoConfigEquip | `com.consilux.servlet.relatorio.RelatorioAlteracaoConfigEquip` |
| RelatorioAproveitaveis | /relatorio/RelatorioAproveitaveis | `com.consilux.servlet.relatorio.RelatorioAproveitaveis` |
| RelatorioDivergenciaCAI_CAV | /relatorio/RelatorioDivergenciaCAI_CAV | `com.consilux.servlet.relatorio.RelatorioDivergenciaCAI_CAV` |
| RelatorioEnquadramentoHabilitadoEfetivo | /relatorio/RelatorioEnquadramentoHabilitadoEfetivo | `com.consilux.servlet.relatorio.RelatorioEnquadramentoHabilitadoEfetivo` |
| RelatorioEquipamentoInconsistencias | /relatorio/RelatorioEquipamentoInconsistencias | `com.consilux.servlet.relatorio.RelatorioEquipamentoInconsistencias` |
| RelatorioErrosValidacao | /relatorio/RelatorioErrosValidacao | `com.consilux.servlet.relatorio.RelatorioErrosValidacao` |
| RelatorioFormacaoLote | /relatorio/RelatorioFormacaoLote | `com.consilux.servlet.relatorio.RelatorioFormacaoLote` |
| RelatorioFuncionamento | /relatorio/RelatorioFuncionamento | `com.consilux.servlet.relatorio.RelatorioFuncionamento` |
| RelatorioImagensAtrasadas | /relatorio/RelatorioImagensAtrasadas | `com.consilux.servlet.relatorio.RelatorioImagensAtrasadas` |
| RelatorioImagensTeste | /relatorio/RelatorioImagensTeste | `com.consilux.servlet.relatorio.RelatorioImagensTesteServlet` |
| RelatorioInfracoesCAV | /relatorio/RelatorioInfracoesCAV | `com.consilux.servlet.relatorio.RelatorioInfracoesCAV` |
| RelatorioInfracoesConsistentes | /relatorio/RelatorioInfracoesConsistentes | `com.consilux.servlet.relatorio.RelatorioInfracoesConsistentes` |
| RelatorioJustificativaFalhas | /relatorio/RelatorioJustificativaFalhas | `com.consilux.servlet.relatorio.RelatorioJustificativaFalhas` |
| RelatorioLotesReprovados | /relatorio/RelatorioLotesReprovados | `com.consilux.servlet.relatorio.RelatorioLotesReprovados` |
| RelatorioManutencao | /RelatorioManutencao | `com.consilux.servlet.relatorio.RelatorioManutencao` |
| RelatorioOcorrencia | /relatorio/RelatorioOcorrencia | `com.consilux.servlet.relatorio.RelatorioOcorrencia` |
| RelatorioPlacas | /relatorio/RelatorioPlacas | `com.consilux.servlet.relatorio.RelatorioPlacas` |
| RelatorioProdutividadeAuditoria | /relatorio/RelatorioProdutividadeAuditoria | `com.consilux.servlet.relatorio.RelatorioProdutividadeAuditoria` |
| RelatorioSegurancaTransito | /relatorio/RelatorioSegurancaTransito | `com.consilux.servlet.relatorio.RelatorioSegurancaTransito` |
| RelatoriosMedicao_CAV_CAI | /relatorio/RelatoriosMedicao_CAV_CAI | `com.consilux.servlet.relatorio.RelatoriosMedicao_CAV_CAI` |
| RelatorioTotalInfracoes | /relatorio/RelatorioTotalInfracoes | `com.consilux.servlet.relatorio.RelatorioTotalInfracoes` |
| RelatorioValidacao | /relatorio/RelatorioValidacao | `com.consilux.servlet.relatorio.RelatorioValidacao` |
| RelatorioValidasEnquadramento | /relatorio/RelatorioValidasEnquadramento | `com.consilux.servlet.relatorio.RelatorioValidasEnquadramento` |
| RelatorioVencimentoAfericoes | /relatorio/RelatorioVencimentoAfericoes | `com.consilux.servlet.relatorio.RelatorioVencimentoAfericoes` |
| RelatorioVolumeDia | /relatorio/RelatorioVolumeDia | `com.consilux.servlet.relatorio.RelatorioVolumeDia` |
| PlanilhaAcompanhamento | /relatorio/rj/PlanilhaAcompanhamento | `com.consilux.servlet.relatorio.rj.PlanilhaAcompanhamento` |
| Relatorio10FluxoVeicular | /relatorio/rj/Relatorio10FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio10FluxoVeicular` |
| Relatorio10TempoPercurso | /relatorio/rj/Relatorio10TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio10TempoPercurso` |
| Relatorio11FluxoVeicular | /relatorio/rj/Relatorio11FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio11FluxoVeicular` |
| Relatorio12FluxoVeicular | /relatorio/rj/Relatorio12FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio12FluxoVeicular` |
| Relatorio13FluxoVeicular | /relatorio/rj/Relatorio13FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio13FluxoVeicular` |
| Relatorio14FluxoVeicular | /relatorio/rj/Relatorio14FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio14FluxoVeicular` |
| Relatorio15FluxoVeicular | /relatorio/rj/Relatorio15FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio15FluxoVeicular` |
| Relatorio16FluxoVeicular | /relatorio/rj/Relatorio16FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio16FluxoVeicular` |
| Relatorio17FluxoVeicular | /relatorio/rj/Relatorio17FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio17FluxoVeicular` |
| Relatorio18FluxoVeicular | /relatorio/rj/Relatorio18FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio18FluxoVeicular` |
| Relatorio19FluxoVeicular | /relatorio/rj/Relatorio19FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio19FluxoVeicular` |
| Relatorio1FluxoVeicular | /relatorio/rj/Relatorio1FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio1FluxoVeicular` |
| Relatorio1TempoPercurso | /relatorio/rj/Relatorio1TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio1TempoPercurso` |
| Relatorio20FluxoVeicular | /relatorio/rj/Relatorio20FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio20FluxoVeicular` |
| Relatorio21FluxoVeicular | /relatorio/rj/Relatorio21FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio21FluxoVeicular` |
| Relatorio22FluxoVeicular | /relatorio/rj/Relatorio22FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio22FluxoVeicular` |
| Relatorio24FluxoVeicular | /relatorio/rj/Relatorio24FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio24FluxoVeicular` |
| Relatorio25FluxoVeicular | /relatorio/rj/Relatorio25FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio25FluxoVeicular` |
| Relatorio26FluxoVeicular | /relatorio/rj/Relatorio26FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio26FluxoVeicular` |
| Relatorio27FluxoVeicular | /relatorio/rj/Relatorio27FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio27FluxoVeicular` |
| Relatorio28FluxoVeicular | /relatorio/rj/Relatorio28FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio28FluxoVeicular` |
| Relatorio29FluxoVeicular | /relatorio/rj/Relatorio29FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio29FluxoVeicular` |
| Relatorio2FluxoVeicular | /relatorio/rj/Relatorio2FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio2FluxoVeicular` |
| Relatorio2TempoPercurso | /relatorio/rj/Relatorio2TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio2TempoPercurso` |
| Relatorio3FluxoVeicular | /relatorio/rj/Relatorio3FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio3FluxoVeicular` |
| Relatorio3TempoPercurso | /relatorio/rj/Relatorio3TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio3TempoPercurso` |
| Relatorio4FluxoVeicular | /relatorio/rj/Relatorio4FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio4FluxoVeicular` |
| Relatorio4TempoPercurso | /relatorio/rj/Relatorio4TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio4TempoPercurso` |
| Relatorio5FluxoVeicular | /relatorio/rj/Relatorio5FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio5FluxoVeicular` |
| Relatorio5TempoPercurso | /relatorio/rj/Relatorio5TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio5TempoPercurso` |
| Relatorio6FluxoVeicular | /relatorio/rj/Relatorio6FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio6FluxoVeicular` |
| Relatorio6TempoPercurso | /relatorio/rj/Relatorio6TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio6TempoPercurso` |
| Relatorio7FluxoVeicular | /relatorio/rj/Relatorio7FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio7FluxoVeicular` |
| Relatorio7TempoPercurso | /relatorio/rj/Relatorio7TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio7TempoPercurso` |
| Relatorio8FluxoVeicular | /relatorio/rj/Relatorio8FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio8FluxoVeicular` |
| Relatorio8TempoPercurso | /relatorio/rj/Relatorio8TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio8TempoPercurso` |
| Relatorio9FluxoVeicular | /relatorio/rj/Relatorio9FluxoVeicular | `com.consilux.servlet.relatorio.rj.Relatorio9FluxoVeicular` |
| Relatorio9TempoPercurso | /relatorio/rj/Relatorio9TempoPercurso | `com.consilux.servlet.relatorio.rj.Relatorio9TempoPercurso` |
| RelatorioFluxoVeicularPorHora | /relatorio/rj/RelatorioFluxoVeicularPorHora | `com.consilux.servlet.relatorio.rj.RelatorioFluxoVeicularPorHora` |
| RelatorioFuncionamentoRJ | /relatorio/rj/RelatorioFuncionamentoRJ | `com.consilux.servlet.relatorio.rj.RelatorioFuncionamentoRJ` |
| RelatoriosFluxoVeicular | /relatorio/rj/RelatoriosFluxoVeicular | `com.consilux.servlet.relatorio.rj.RelatoriosFluxoVeicular` |
| RelatorioSinalizacao | /relatorio/rj/RelatorioSinalizacao | `com.consilux.servlet.relatorio.rj.RelatorioSinalizacao` |
| RelatoriosTempoPercurso | /relatorio/rj/RelatoriosTempoPercurso | `com.consilux.servlet.relatorio.rj.RelatoriosTempoPercurso` |
| VelocidadeTodosVeiculos | /relatorio/VelocidadeTodosVeiculos | `com.consilux.servlet.relatorio.VelocidadeTodosVeiculos` |
| EtiquetaRemessa | /remessa/EtiquetaRemessa | `com.consilux.servlet.remessa.EtiquetaRemessa` |
| ExportarRemessa | /remessa/ExportarRemessa | `com.consilux.servlet.remessa.ExportarRemessa` |
| ExportarTXTRemessa | /remessa/ExportarTXTRemessa | `com.consilux.servlet.remessa.ExportarTXTRemessa` |
| GerarRemessa | /remessa/GerarRemessa | `com.consilux.servlet.remessa.GerarRemessa` |
| PREM | /remessa/PREM | `com.consilux.servlet.remessa.PREM` |
| remessa/ReprovarRemessa | /remessa/ReprovarRemessa | `com.consilux.servlet.remessa.ReprovarRemessa` |
| AmostraImagemServiceImpl | /GtwWidgets/AmostraImagemService | `com.consilux.ui.server.AmostraImagemServiceImpl` |
| analiseAutuacoesServiceImpl | /GtwWidgets/AnaliseAutuacoesService | `com.consilux.ui.server.AnaliseAutuacoesServiceImpl` |
| EventosPesquisaService | /GtwWidgets/EventosPesquisaService | `com.consilux.ui.server.EventosPesquisaServiceImpl` |
| ImpTxtService | /GtwWidgets/ImpTxtService | `com.consilux.ui.server.ImpTxtServiceImpl` |
| ListaConfiguracoesEquipamentoService | /GtwWidgets/ListaConfiguracoesEquipamentoService | `com.consilux.ui.server.ListaConfiguracoesEquipamentoServiceImpl` |
| ManutencaoService | /GtwWidgets/ManutencaoService | `com.consilux.ui.server.ManutencaoServiceImpl` |
| monitoramentoMapServiceImpl | /GtwWidgets/MonitoramentoMapService | `com.consilux.ui.server.MonitoramentoMapServiceImpl` |
| RelatorioDinamicoServiceImpl | /GtwWidgets/RelatorioDinamicoService | `com.consilux.ui.server.RelatorioDinamicoServiceImpl` |
| UsuarioService | /GtwWidgets/UsuarioService | `com.consilux.ui.server.UsuarioServiceImpl` |
| veiculosMonitoradosServiceImpl | /GtwWidgets/VeiculosMonitoradosService | `com.consilux.ui.server.VeiculosMonitoradosServiceImpl` |
| Jersey REST Service | /rest/* | `com.sun.jersey.spi.container.servlet.ServletContainer` |
| ConfiguracaoMuralha |  | `muralha.digital._ini.Inicializacao` |
| PerfilComportamental | /perfilcomportamental/PerfilComportamentalServlet | `muralha.digital.perfilcomportamental.PerfilComportamentalServlet` |
| AdminServlet | /servlet/AdminServlet | `org.apache.axis.transport.http.AdminServlet` |
| AxisServlet | /servlet/AxisServlet, *.jws, /services/* | `org.apache.axis.transport.http.AxisServlet` |
| DisplayChart | /graficos/MostraGrafico | `org.jfree.chart.servlet.DisplayChart` |

## 2. Servlets anotados com @WebServlet

| URL | Classe |
|---|---|
| `/Abertura/SistemasConsilux` | `com.abertura.sistemas.SistemasServlet` |
| `/callback` | `muralha.digital.google.LoginGoogle` |
| `/MuralhaDigital/Alerta` | `muralha.digital.alerta.AlertaServlet` |
| `/MuralhaDigital/Alerta/AnotacaoContributiva` | `muralha.digital.alerta.AnotacaoContributivaServlet` |
| `/MuralhaDigital/Alerta/MotivoDescarte` | `muralha.digital.alerta.MotivoDescarteServlet` |
| `/MuralhaDigital/AlertaOcorrencia` | `muralha.digital.consulta.AlertaOcorrenciaServlet` |
| `/MuralhaDigital/AlertaOcorrencia/Status` | `muralha.digital.consulta.StatusAlertaOcorrenciaServlet` |
| `/MuralhaDigital/AlertaOcorrencia/Tipo` | `muralha.digital.consulta.TipoAlertaOcorrenciaServlet` |
| `/MuralhaDigital/AlertaOcorrencia/TipoRegistro` | `muralha.digital.consulta.TipoRegistroServlet` |
| `/MuralhaDigital/AlertaQuestionario` | `muralha.digital.perguntasRespostas.AlertaQuestionarioServlet` |
| `/MuralhaDigital/Anexo` | `muralha.digital.atendimento.AtendimentoAnexoServlet` |
| `/MuralhaDigital/Anomalia` | `muralha.digital.anomalia.AnomaliaServlet` |
| `/MuralhaDigital/AreaMonitorada` | `muralha.digital.areamonitorada.AreaMonitoradaServlet` |
| `/muralha-digital/assinatura/*` | `muralha.digital.assinatura.AssinaturaImagemServlet` |
| `/MuralhaDigital/Atendimento` | `muralha.digital.atendimento.AtendimentoServlet` |
| `/MuralhaDigital/Blitz` | `muralha.digital.blitz.BlitzServlet` |
| `/MuralhaDigital/BlitzEletronica/Imprimir` | `muralha.digital.relatorios.BlitzEletronicaVeicIrregular` |
| `/MuralhaDigital/BlitzEletronica/VeiculoIrregular` | `muralha.digital.temporeal.BlitzEletronica` |
| `/MuralhaDigital/Boletim` | `muralha.digital.boletim.BoletimServlet` |
| `/MuralhaDigital/Boletim/Cidade` | `muralha.digital.boletim.BoletimCidadeServlet` |
| `/MuralhaDigital/Boletim/Doducmento` | `muralha.digital.boletim.BoletimDocumentoServlet` |
| `/MuralhaDigital/Boletim/Situacao` | `muralha.digital.boletim.BoletimSituacaoServlet` |
| `/MuralhaDigital/Boletim/Tipo` | `muralha.digital.boletim.BoletimTipoServlet` |
| `/MuralhaDigital/Boletim/TipoIndividuo` | `muralha.digital.boletim.BoletimIndividuoTipoServlet` |
| `/MuralhaDigital/CidadesMinasGerais` | `muralha.digital.cidade.CidadeServlet` |
| `/MuralhaDigital/ConfigMonAoVivo` | `muralha.digital.monitoramento.MonitoramentoAoVivoServlet` |
| `/MuralhaDigital/ConfiguracaoInatividade` | `muralha.digital.acessos.ConfiguracaoInatividadeServlet` |
| `/MuralhaDigital/ConfiguracaoRadares` | `muralha.digital.acessos.ConfiguracaoRadaresServlet` |
| `/MuralhaDigital/ConfiguracaoTempo` | `muralha.digital.notificacao.ConfiguracaoSonsServlet` |
| `/MuralhaDigital/ConfiguracaoTempoOcrBlitz` | `muralha.digital.acessos.ConfiguracaoTempoOcrBlitzServlet` |
| `/MuralhaDigital/ConfigurarEquipamento` | `muralha.configuracaoequipamento.ConfiguracaoEquipamentoServlet` |
| `/MuralhaDigital/CorrecaoPlaca` | `muralha.digital.correlacaoplaca.CorrecaoPlacaServlet` |
| `/MuralhaDigital/Dashboard` | `muralha.digital.relatorios.DashboardServlet` |
| `/MuralhaDigital/DispositivoEquipamento` | `muralha.digital.dispositivo.DispositivoEquipamentoServlet` |
| `/MuralhaDigital/Equipamento` | `muralha.digital.equipamento.EquipamentoServlet` |
| `/MuralhaDigital/Ftp` | `muralha.digital.consulta.BaixaLogFtp` |
| `/MuralhaDigital/Grafico` | `muralha.digital.relatorios.GraficoServlet` |
| `/MuralhaDigital/GrupoNotificacao` | `muralha.digital.notificacao.GrupoNotificacaoServlet` |
| `/MuralhaDigital/Guarnicao` | `muralha.digital.guarnicao.GuarnicaoServlet` |
| `/MuralhaDigital/HistoricoConfiguracaoEquipamento` | `muralha.configuracaoequipamento.HistoricoConfiguracaoEquipamentoServlet` |
| `/MuralhaDigital/JuncaoBase` | `muralha.digital.juncaobase.JuncaoBaseServlet` |
| `/MuralhaDigital/LinhaTempo` | `muralha.digital.consulta.LinhaTempoServlet` |
| `/MuralhaDigital/MapaCalor` | `muralha.digital.mapacalor.MapaCalorServlet` |
| `/MuralhaDigital/MapaDispositivos3D` | `muralha.digital.mapadispositivos3d.MapaDispositivos3DServlet` |
| `/MuralhaDigital/MapaDispositivosEquipamentos` | `muralha.digital.mapadispositivos.MapaDispositivoServlet` |
| `/MuralhaDigital/MapaInterativo` | `muralha.digital.mapainterativo.MapaInterativoServlet` |
| `/MuralhaDigital/Monitorado` | `muralha.digital.monitorado.VeiculoMonitoradoServlet` |
| `/MuralhaDigital/MotivoSolicitacaoRelatorio` | `muralha.digital.motivosolicitacaorelatorio.MotivoSolicitacaoRelatorioServlet` |
| `/MuralhaDigital/Notificacao` | `muralha.digital.notificacao.NotificacaoServlet` |
| `/MuralhaDigital/Ocorrencia` | `muralha.digital.ocorrencia.OcorrenciaServlet` |
| `/MuralhaDigital/Ocorrencia/Status` | `muralha.digital.ocorrencia.StatusOcorrenciaServlet` |
| `/MuralhaDigital/OcorrenciaLigacao` | `muralha.digital.ocorrencialigacao.OcorrenciaLigacaoServlet` |
| `/MuralhaDigital/PainelInformacao/Camera` | `muralha.digital.painelInformacoes.CameraServlet` |
| `/MuralhaDigital/PainelInformacao/TotalInformacoes` | `muralha.digital.painelInformacoes.TotalInformacaoServlet` |
| `/MuralhaDigital/PerfilComportamental` | `muralha.digital.perfilcomportamental.PerfilComportamentalServlet` |
| `/MuralhaDigital/PermissoesFuncionalidade` | `muralha.digital.acessos.PermissoesFuncionalidadeServlet` |
| `/MuralhaDigital/PesquisaRapida` | `muralha.digital.pesquisaRapida.PesquisaRapidaServlet` |
| `/MuralhaDigital/PesquisaRapida/Autocomplete` | `muralha.digital.pesquisaRapida.PesquisaRapidaAutocompleteServlet` |
| `/MuralhaDigital/PontoInteresse` | `muralha.digital.pontointeresse.PontoInteresseServlet` |
| `/MuralhaDigital/RegistroDeFato` | `muralha.digital.registroDeFato.RegistroDeFatoServlet` |
| `/MuralhaDigital/RegistroDeFato/Boletim/Situacao` | `muralha.digital.registroDeFato.BoletimSituacaoServlet` |
| `/MuralhaDigital/RegistroDeFato/Cidade` | `muralha.digital.registroDeFato.CidadeServlet` |
| `/MuralhaDigital/RegistroDeFato/Doducmento` | `muralha.digital.registroDeFato.RegistroDeFatoDocumentoServlet` |
| `/MuralhaDigital/RegistroDeFato/EnderecoEvento` | `muralha.digital.registroDeFato.RegistroDeFatoEnderecoEventoServlet` |
| `/MuralhaDigital/RegistroDeFato/Grupo` | `muralha.digital.registroDeFato.GrupoRegistroDeFatoServlet` |
| `/MuralhaDigital/RegistroDeFato/Historico` | `muralha.digital.registroDeFato.RegistroDeFatoHistoricoServlet` |
| `/MuralhaDigital/RegistroDeFato/IndividuoTipo` | `muralha.digital.registroDeFato.RegistroDeFatoIndividuoTipoServlet` |
| `/MuralhaDigital/RegistroDeFato/Natureza` | `muralha.digital.registroDeFato.RegistroDeFatoNaturezaServlet` |
| `/MuralhaDigital/RegistroDeFato/Situacao` | `muralha.digital.registroDeFato.RegistroDeFatoSituacaoServlet` |
| `/MuralhaDigital/RegistroDeFato/Tipo` | `muralha.digital.registroDeFato.RegistroDeFatoTipoServlet` |
| `/MuralhaDigital/RegistroDeFatoNaturezaDelituosa` | `muralha.digital.registroDeFato.RegistroDeFatoNaturezaDelituosaServlet` |
| `/muralha-digital/relatorio/RelatorioDetalhadoAlertas` | `muralha.digital.relatorios.RelatorioDetalhadoAlertasServlet` |
| `/muralha-digital/relatorio/RelatorioEstatisticoAlarmes` | `muralha.digital.relatorios.RelatorioEstatisticoAlarmesServlet` |
| `/muralha-digital/relatorio/RelatorioFluxoVeicularRota` | `muralha.digital.relatorios.RelatorioFluxoVeicularRotaServlet` |
| `/muralha-digital/relatorio/RelatorioVeiculosMonitoradosModelo` | `muralha.digital.relatorios.RelatorioVeiculosMonitoradosModeloServlet` |
| `/MuralhaDigital/RelatorioDistribuicaoFatos` | `muralha.digital.relatorios.RelatorioDistribuicaoFatos` |
| `/MuralhaDigital/RelatorioEvolucaoSemanal` | `muralha.digital.relatorios.RelatorioEvolucaoSemanal` |
| `/MuralhaDigital/RelatorioPendenciasRegistroFato` | `muralha.digital.relatorios.RelatorioPendenciasRegistroFato` |
| `/MuralhaDigital/RelatorioPlacasVeiculares` | `muralha.digital.relatorios.placasveiculares.RelatorioPlacasVeicularesServlet` |
| `/MuralhaDigital/Relatorios/AcoesAlarmes` | `muralha.digital.relatorios.acoesAlarmes.RelatorioAcoesAlarmesServlet` |
| `/MuralhaDigital/Relatorios/Auditoria` | `muralha.digital.relatorios.auditoria.RelatorioAuditoriaServlet` |
| `/MuralhaDigital/Relatorios/ExportarImagens` | `muralha.digital.relatorios.exportarImagens.RelatoriosExportarImagensServlet` |
| `/MuralhaDigital/Relatorios/PesquisasVeiculos` | `muralha.digital.relatorios.pesquisaVeiculosRealizados.pesquisaVeiculosRealizadosServlet` |
| `/MuralhaDigital/Relatorios/SessaoUsuario` | `muralha.digital.relatorios.RelatorioSessaoUsuarioServlet` |
| `/MuralhaDigital/TipoNotificacao` | `muralha.digital.notificacao.TipoNotificacaoServlet` |
| `/MuralhaDigital/Usuarios` | `muralha.digital.acessos.UsuarioServlet` |
| `/MuralhaDigital/Veiculo` | `muralha.digital.veiculo.VeiculoServlet` |
| `/MuralhaDigital/Veiculo/Classificacao` | `muralha.digital.veiculo.ClasseVeiculoServlet` |
| `/MuralhaDigital/Veiculo/Imagem` | `muralha.digital.veiculo.imagem.ImgVeiculoTempoReal` |
| `/MuralhaDigital/Veiculo/Imagem/Lista` | `muralha.digital.veiculo.imagem.VeiculoImagemServlet` |
| `/MuralhaDigital/VeiculoAuxiliar` | `muralha.digital.monitorado.VeiculoAuxiliarServlet` |
| `/MuralhaDigital/VeiculoDeCarga` | `muralha.digital.veiculo.VeiculoDeCargaServlet` |
| `/MuralhaDigital/VeiculosCorrelacionados` | `muralha.digital.veiculosCorrelacionados.VeiculosCorrelacionadosServlet` |
| `/MuralhaDigital/VeiculoTempoReal` | `muralha.digital.temporeal.VeiculoTempoReal` |
| `/MuralhaDigital/VideoMonitoramento` | `muralha.digital.monitoramento.VideoMonitoramentoServlet` |
| `/MuralhaDigital/VideoMonitoramento/Video` | `muralha.digital.monitoramento.VerVideoMonitoramentoServlet` |
| `/Relatorio/AlertasDetalhado` | `muralha.digital.relatorios.RelatorioAlertasDetalhado` |
| `/relatorio/ContagemPassagensPorLocal` | `muralha.digital.relatorios.ContagemPassagensPorLocalServlet` |
| `/Relatorio/DistribuicaoFaixaVelocidade` | `muralha.digital.relatorios.RelatorioDistribuicaoFaixaVel` |
| `/Relatorio/DistribuicaoPorteVeicular` | `muralha.digital.relatorios.RelatorioDistribuicaoPorteVeic` |
| `/Relatorio/ExtratoAlertaOcorrencia` | `muralha.digital.relatorios.RelatorioExtratoAlertasOcorrencias` |
| `/Relatorio/FluxoMensalPorClassificacao` | `muralha.digital.relatorios.RelatorioFluxoMensalPorClassificacao` |
| `/Relatorio/OcorrenciasDetalhado` | `muralha.digital.relatorios.RelatorioOcorrenciasDetalhado` |
| `/relatorio/PassagensSequenciais` | `muralha.digital.relatorios.PassagensSequenciaisServlet` |
| `/Relatorio/QuantidadePassagens` | `muralha.digital.relatorios.RelatorioQuantidadePassagens` |
| `/relatorio/RelatorioAcompanhamento` | `com.consilux.servlet.relatorio.rj.RelatorioAcompanhamento` |
| `/relatorio/RelatorioDistribuicaoTiposFatosServlet` | `muralha.digital.relatorios.RelatorioDistribuicaoTiposFatosServlet` |
| `/relatorio/RelatorioEstatisticoFatoServlet` | `muralha.digital.relatorios.RelatorioEstatisticoFatoServlet` |
| `/relatorio/RelatorioEstatisticoTipoFatoMapa` | `muralha.digital.relatorios.RelatorioEstatisticoTipoFatoServletMapa` |
| `/relatorio/RelatorioEstatisticoTipoFatos` | `muralha.digital.relatorios.RelatorioEstatisticoTipoFatosServlet` |
| `/relatorio/RelatorioFluxoPassagensVeiculares` | `muralha.digital.relatorios.RelatorioFluxoPassagensVeicularesServlet` |
| `/relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew` | `muralha.digital.relatorios.RelatorioPermanenciaVeiculoAreaMonitoradaNew` |
| `/relatorio/rj/ExportarArquivoFluxo15MinutosDER` | `com.consilux.servlet.relatorio.rj.ExportarArquivoFluxo15MinutosDER` |
| `/relatorio/rj/ExportarArquivoFluxoHoraDER` | `com.consilux.servlet.relatorio.rj.ExportarArquivoFluxoHoraDER` |
| `/relatorio/rj/RelatorioDiarioClassificacaoDER` | `com.consilux.servlet.relatorio.rj.RelatorioDiarioPorClassificacaoDER` |
| `/relatorio/rj/RelatorioMensalClassificacaoDER` | `com.consilux.servlet.relatorio.rj.RelatorioMensalPorClassificacaoDER` |
| `/Relatorio/TaxaOcupacaoVia` | `muralha.digital.relatorios.RelatorioTaxaOcupacaoVia` |
| `/Relatorio/VelocidadeMedia` | `muralha.digital.relatorios.RelatorioVelocidadeMedia` |
